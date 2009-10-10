package edu.asu.commons.irrigation.server;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.asu.commons.event.BeginExperimentRequest;
import edu.asu.commons.event.BeginRoundRequest;
import edu.asu.commons.event.ChatEvent;
import edu.asu.commons.event.ChatRequest;
import edu.asu.commons.event.EndRoundRequest;
import edu.asu.commons.event.EventTypeProcessor;
import edu.asu.commons.event.FacilitatorRegistrationRequest;
import edu.asu.commons.experiment.AbstractExperiment;
import edu.asu.commons.experiment.StateMachine;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.irrigation.events.BeginCommunicationRequest;
import edu.asu.commons.irrigation.events.CloseGateEvent;
import edu.asu.commons.irrigation.events.DisplaySubmitTokenRequest;
import edu.asu.commons.irrigation.events.EndRoundEvent;
import edu.asu.commons.irrigation.events.FacilitatorEndRoundEvent;
import edu.asu.commons.irrigation.events.InstructionEnableRequest;
import edu.asu.commons.irrigation.events.InvestedTokensEvent;
import edu.asu.commons.irrigation.events.OpenGateEvent;
import edu.asu.commons.irrigation.events.PauseEvent;
import edu.asu.commons.irrigation.events.QuizCompletedEvent;
import edu.asu.commons.irrigation.events.RegistrationEvent;
import edu.asu.commons.irrigation.events.RoundStartedEvent;
import edu.asu.commons.irrigation.events.SendContributionStatusEvent;
import edu.asu.commons.irrigation.events.SendFileProgressEvent;
import edu.asu.commons.net.Dispatcher;
import edu.asu.commons.net.Identifier;
import edu.asu.commons.net.event.ConnectionEvent;
import edu.asu.commons.net.event.DisconnectionEvent;
import edu.asu.commons.net.event.DisconnectionRequest;
import edu.asu.commons.util.Duration;

/**
 * $Id$
 * 
 * Main entry point for the irrigation experiment server.
 * 
 * @author Sanket Joshi, <a href='Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */
public class IrrigationServer extends AbstractExperiment<ServerConfiguration> {

    private final Map<Identifier, ClientData> clients = new LinkedHashMap<Identifier, ClientData>();

    private final static int SERVER_SLEEP_INTERVAL = 333;
    
    private final Object roundSignal = new Object();

    private Duration currentRoundDuration;

    private final ServerDataModel serverDataModel;
    
//    private final Object quizSignal = new Object();
//    private int numberOfCompletedQuizzes;

    private int submittedClients;
    
    private IrrigationPersister persister;
    
    private int numberOfCompletedQuizzes = 0;

    private IrrigationServerStateMachine stateMachine = new IrrigationServerStateMachine();

    /**
     * this is the variable that would be reset to Bt for 
     * every loop of the client traversal.
     */

    private Identifier facilitatorId;

    public IrrigationServer() {
        this(new ServerConfiguration());
    }

    // FIXME: add the ability to reconfigure an already instantiated server
    public IrrigationServer(ServerConfiguration configuration) {
        setConfiguration(configuration);
        serverDataModel = new ServerDataModel();
        serverDataModel.setEventChannel(getEventChannel());
        serverDataModel.setCurrentConfiguration(configuration.getCurrentParameters());
        persister = new IrrigationPersister(getEventChannel(), configuration);
        initializeFacilitatorHandlers();
        initializeClientHandlers();
    }

    private void initializeFacilitatorHandlers() {
        // facilitator handlers
        addEventProcessor(new EventTypeProcessor<FacilitatorRegistrationRequest>(FacilitatorRegistrationRequest.class) {
            public void handle(FacilitatorRegistrationRequest event) {
                System.err.println("facilitator registered: " + event.getId());
                // remap the facilitator ID and remove from the clients list.
                facilitatorId = event.getId();
                synchronized (clients) {
                    clients.remove(facilitatorId);
                    serverDataModel.removeClient(facilitatorId);
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<BeginExperimentRequest>(BeginExperimentRequest.class) {
            public void handle(BeginExperimentRequest event) {
                synchronized (roundSignal) {
                    roundSignal.notifyAll();
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<EndRoundRequest>(EndRoundRequest.class) {
            public void handle(EndRoundRequest request) {
                currentRoundDuration.stop();
            }

        });
        addEventProcessor(new EventTypeProcessor<BeginCommunicationRequest>(BeginCommunicationRequest.class) {
            public void handle(BeginCommunicationRequest request) {
                // pass it on to all the clients
                for (Identifier id: clients.keySet()) {
                    transmit(new BeginCommunicationRequest(id, serverDataModel.getGroupDataModel(id)));
                }
                // start a timer on the server side, give it a little extra leeway to allow for latency time.
//                chatDuration = Duration.create(getRoundConfiguration().getChatDuration() + 5);
            }
        });
        
        addEventProcessor(new EventTypeProcessor<InstructionEnableRequest>(InstructionEnableRequest.class) {
            public void handle(InstructionEnableRequest request) {
                // pass it on to all the clients
                for (Identifier id: clients.keySet()) {
                    transmit(new InstructionEnableRequest(id));
                }
                // start a timer on the server side, give it a little extra leeway to allow for latency time.
//                chatDuration = Duration.create(getRoundConfiguration().getChatDuration() + 5);
            }
        });
       
        
        addEventProcessor(new EventTypeProcessor<DisplaySubmitTokenRequest>(DisplaySubmitTokenRequest.class) {
            public void handle(DisplaySubmitTokenRequest request) {
                for (Identifier id: clients.keySet()) {
                    transmit(new DisplaySubmitTokenRequest(id));
                }
            }
        });
    }

    private void initializeClientHandlers() {
        // client handlers
        addEventProcessor(new EventTypeProcessor<ConnectionEvent>(ConnectionEvent.class) {
            @Override
            public void handle(ConnectionEvent event) {
                // handle incoming connections
                Identifier identifier = event.getId();
                ClientData clientData = new ClientData(identifier);
                synchronized (clients) {
                    clients.put(identifier, clientData);
                    serverDataModel.addClient(clientData);
                }

                transmit(new RegistrationEvent(clientData.getId(), getRoundConfiguration(), clientData));
            }
        });
        addEventProcessor(new EventTypeProcessor<DisconnectionRequest>(DisconnectionRequest.class) {
            @Override
            public void handleInExperimentThread(DisconnectionRequest request) {
                getLogger().warning("disconnecting: " + request);
                Identifier disconnectedClientId = request.getId();
                synchronized (clients) {
                    clients.remove(request.getId());
                    serverDataModel.removeClient(request.getId());
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<ChatRequest>(ChatRequest.class) {
            @Override
            public void handle(ChatRequest request) {
                Identifier source = request.getSource();
                Identifier target = request.getTarget();
                if (Identifier.ALL.equals(target)) {
                    // relay to all clients in this client's group.
                    getLogger().info(String.format("%s sending [ %s ] to all group participants", request.getSource(), request));
                    for (Identifier targetId : clients.get(source).getGroupDataModel().getClientIdentifiers()) {
                        if (targetId.equals(source)) {
                            continue;
                        }
                        ChatEvent chatEvent = new ChatEvent(targetId, request.toString(), source, true);
                        transmit(chatEvent);
                    }
                }
                else {
                    getLogger().info(String.format("%s sending [%s] to target [%s]", request.getSource(), request, request.getTarget()));
                    ChatEvent chatEvent = new ChatEvent(request.getTarget(), request.toString(), request.getSource());                  
                    transmit(chatEvent);
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<InvestedTokensEvent>(InvestedTokensEvent.class) {
            public void handle(InvestedTokensEvent event) {
                ClientData clientData = clients.get(event.getId());
                clientData.setContributedTokens(event.getInvestedTokens());
                submittedClients++;
                if (submittedClients == clients.size()) {
                    // everyone's submitted their tokens so we can calculate the available bandwidth and 
                    // notify each client
                    initializeInfrastructure();
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<QuizCompletedEvent>(QuizCompletedEvent.class) {
			public void handle(QuizCompletedEvent event) {
                System.err.println("Quiz Completed Event:"+event.getId()+" : instruction Number"+event.getInstructionNumber());
                numberOfCompletedQuizzes++;
                if(numberOfCompletedQuizzes == clients.size()*8){
                	getLogger().info("Everyone has finished reading the general instructions successfully");
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<OpenGateEvent>(OpenGateEvent.class) {
            public void handle(OpenGateEvent event) {
                ClientData clientData = clients.get(event.getId());
                clientData.openGate();
            }
        });
        addEventProcessor(new EventTypeProcessor<CloseGateEvent>(CloseGateEvent.class) {
            public void handle(CloseGateEvent event) {
                clients.get(event.getId()).closeGate();
            }
        });
        addEventProcessor(new EventTypeProcessor<PauseEvent>(PauseEvent.class) {
            public void handle(PauseEvent event) {
                clients.get(event.getId()).setPaused();
            }
        });
    }

    /**
     * Invoked after every client has submit their tokens
     *
     */
      public void initializeInfrastructure() {
        // clients are added to the ServerDataModel as they register and then reallocated if necessary in 
        // post round cleanup
        /////////////////////////////////////////////////////////////////////////////////
        /**
         * Bi is the Btmax/number of clients in a group.
         */
//        double maxClientBandwidth = (double)getRoundConfiguration().getBtmax()/2;
    	int maxClientFlowCapacity = getRoundConfiguration().getMaximumClientFlowCapacity();
        GroupDataModel.setMaximumClientFlowCapacity(maxClientFlowCapacity);

        for(GroupDataModel group : serverDataModel.getAllGroupDataModels()){
            group.calculateTotalFlowCapacity();            
            System.out.println("group Bt is "+ group.getCurrentlyAvailableFlowCapacity());

            // iterate through all groups and send back their contribution status
            for (ClientData clientData : group.getClientDataMap().values()) {
                SendContributionStatusEvent sendContributionStatusEvent = 
                    new SendContributionStatusEvent(clientData.getId(),group);
                transmit(sendContributionStatusEvent);
            }
        }
    }       

    private RoundConfiguration getRoundConfiguration() {
        return getConfiguration().getCurrentParameters();
    }


    /**
     * Processes all the clients message queues. The main loop of the game while
     * the server is running, notifies clients of the current state of affairs.
     * 
     * This method is very important as in it tries to process all the clients in one 
     * group, every second. Then checks the clientData field and transmits the event accordingly
     * At present dont know whether this would be a scalable solution, but there is a scope of 
     * improvement here
     * @param group 
     */
    private void process(GroupDataModel group) {
        // reset group's available bandwidth and re-allocate client delivery bandwidths.
        group.resetCurrentlyAvailableFlowCapacity();
        long time = currentRoundDuration.getTimeLeft() / 1000;
        //System.out.println("Current Time Left :"+time);
         // allocate bandwidth to each client
        for (ClientData clientData : group.getClientDataMap().values()) {
        	/**
        	 * undisrupted bandwith extensions
        	 */
        	if(clientData.getAvailableFlowCapacity()<=0 && getConfiguration().isUndisruptedBandwidth()){
        		clientData.init(group.getCurrentlyAvailableFlowCapacity());
        	}
        	if (clientData.isGateOpen()) {
            	//System.out.println("Downloading file"+clientData.getFileNumber()+"Current time"+System.currentTimeMillis()/1000);
                group.allocateFlowCapacity(clientData);
            }
            else if (clientData.isGateClosed()) {
            	clientData.init(group.getCurrentlyAvailableFlowCapacity());
            }
        	// right now the clients cannot be paused.
            else if (clientData.isPaused()) {

            }
        }
        // transmit new bandwidth information to each client
        for(ClientData clientData : group.getClientDataMap().values()){
            SendFileProgressEvent sendFileProgressEvent = new SendFileProgressEvent(clientData.getId(), group, time);
            transmit(sendFileProgressEvent);
        }
    }

    public static void main(String[] args) {
        IrrigationServer server = new IrrigationServer(new ServerConfiguration()); 
        server.start();
        server.repl();
    }

    enum IrrigationServerState { WAITING, ROUND_IN_PROGRESS };

    private class IrrigationServerStateMachine implements StateMachine {

        private IrrigationServerState state;

        //private final Duration secondTick = Duration.create(1000L);
        
        private long previous_time;
        
       public void initialize() {
            // FIXME: may want to change this as we add more states.
            state = IrrigationServerState.WAITING;
            addEventProcessor(new EventTypeProcessor<BeginRoundRequest>(BeginRoundRequest.class) {
                public void handle(BeginRoundRequest event) {
                    if (! event.getId().equals(facilitatorId)) {
                        System.err.println("received begin round request from non-facilitator");
                        return;
                    }
                    // ignore the request if not every group has submit their tokens.
                    if (submittedClients == clients.size()) {
                        synchronized (roundSignal) {
                            roundSignal.notifyAll();
                        }                        
                    }
                    else {
                        System.err.println("clients still haven't submitted their tokens: " + submittedClients + " - # clients: " + clients.size());
                    }
                }
            });
            persister.initialize(getRoundConfiguration());
        }

       private void initializeRound() {
           // send RoundStartedEvents to all connected clients
           for (Map.Entry<Identifier, ClientData> entry : clients.entrySet()) {
               Identifier id = entry.getKey();
               ClientData data = entry.getValue();
               transmit(new RoundStartedEvent(id, data.getGroupDataModel()));
           }
           // start timers
           currentRoundDuration = getRoundConfiguration().getRoundDuration();
           currentRoundDuration.start();
           //secondTick.start();
           previous_time = System.currentTimeMillis()/1000;
           state = IrrigationServerState.ROUND_IN_PROGRESS;
       }

        private void processRound() {
            //boolean secondHasPassed = secondTick.hasExpired();
            boolean secondHasPassed = (System.currentTimeMillis()/1000 - previous_time)>=1;
        	if (secondHasPassed) {
                for (GroupDataModel group: serverDataModel.getAllGroupDataModels()) {
                    // reset available bandwidth for this group to calculate new allocations for the group
                    // perform bandwidth calculations
                	
                	//FIXME: tried to handle the exception if something goes wrong with a particular group
                	//just remove that group from the list, wiithout diisturbing the experiment
                	try{
                		process(group); 
                	}
                	catch(RuntimeException e){
                		for(ClientData clientData : group.getClientDataMap().values()){
                			System.out.println("Removing the client id :"+clientData.getId());
                			clients.remove(clientData.getId());
                		}
                	}
                }
                //secondTick.restart();
                previous_time++;
            }
        }

        private void stopRound() {
            state = IrrigationServerState.WAITING;
            sendEndRoundEvents();
            persistRound();
            cleanupRound();
            // FIXME: have to wait for .... some reason.
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            advanceToNextRound();
        }
        
        private void sendEndRoundEvents() {
            // need to send instructions
            //Send the end round event to the facilitator
            //Send the end round event to all the clients
            for (ClientData data : clients.values()) {
                data.award();
                transmit(new EndRoundEvent(data.getId(), data.getGroupDataModel(), getConfiguration().isLastRound()));
            }
            transmit(new FacilitatorEndRoundEvent(facilitatorId, serverDataModel));

        }

        private void persistRound() {
            persister.persist(serverDataModel);
        }

        private void cleanupRound() {            
            // reset client data values
            for (ClientData clientData: clients.values()) {
                if (getConfiguration().getCurrentParameters().isPracticeRound()) {
                    clientData.resetAllTokens();
                }
                else {
                    clientData.reset();
                }
            }
            submittedClients = 0;
            persister.clear();
        }
        private void advanceToNextRound() {
            //FIXME: Could not understand how to evaluate the .isLastRound() function. Hence 
        	if (getConfiguration().isLastRound()) {
                return;
            }
        	/*if(getRoundConfiguration().getRoundNumber()== getConfiguration().getLastRoundNumber()){
        		return;
        	}*/
            RoundConfiguration nextRoundConfiguration = getConfiguration().nextRound();
            serverDataModel.setCurrentConfiguration(nextRoundConfiguration);
            // set up the next round
            if (nextRoundConfiguration.shouldRandomizeGroup()) {
                serverDataModel.clear();
                List<ClientData> clientDataList = new ArrayList<ClientData>(clients.values());
                Collections.shuffle(clientDataList);
                for (ClientData data: clientDataList) {
                    // adds the clients to the server data model and sends a registration event to them.
                    serverDataModel.addClient(data);
                }
            }      
            // send registration events to unshuffled participants
            for (ClientData data: clients.values()) {
                transmit(new RegistrationEvent(data.getId(), nextRoundConfiguration, data));
            }
            // send new round configuration to the facilitator
            transmit(new RegistrationEvent(facilitatorId, nextRoundConfiguration));
            persister.initialize(getRoundConfiguration());
        }

		public void execute(Dispatcher dispatcher) {
            switch (state) {
            case WAITING:
                getLogger().info("Waiting for facilitator signal to start next round.");
                synchronized (roundSignal) {
                    try {
                        roundSignal.wait();
                    }
                    catch (InterruptedException ignored) { }
                }  
                // initialize the start of a new round.
                initializeRound();
                break;
            case ROUND_IN_PROGRESS:
                // process incoming information

                synchronized (serverDataModel) {
                    if (currentRoundDuration.hasExpired()) {
                        // end round
                        stopRound();
                    }
                    else {
                    		processRound();
                    	}
                }
                try {
                    Thread.sleep(SERVER_SLEEP_INTERVAL);
                } catch (InterruptedException ignored) {}
                break;
            default:
                throw new RuntimeException("Should never get here.");
            }
        }
    }
    @Override
    protected StateMachine getStateMachine() {
        return stateMachine;
    }
    @Override
    public void processReplInput(String input, BufferedReader reader) {
        if ("clients".equals(input)) {
            System.err.println("clients: " + clients);
        }
    }
}
