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
import edu.asu.commons.event.FacilitatorMessageEvent;
import edu.asu.commons.event.FacilitatorRegistrationRequest;
import edu.asu.commons.event.RoundStartedMarkerEvent;
import edu.asu.commons.event.SocketIdentifierUpdateRequest;
import edu.asu.commons.experiment.AbstractExperiment;
import edu.asu.commons.experiment.StateMachine;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.irrigation.events.BeginChatRoundRequest;
import edu.asu.commons.irrigation.events.ClientUpdateEvent;
import edu.asu.commons.irrigation.events.CloseGateEvent;
import edu.asu.commons.irrigation.events.EndRoundEvent;
import edu.asu.commons.irrigation.events.FacilitatorEndRoundEvent;
import edu.asu.commons.irrigation.events.InfrastructureUpdateEvent;
import edu.asu.commons.irrigation.events.InvestedTokensEvent;
import edu.asu.commons.irrigation.events.OpenGateEvent;
import edu.asu.commons.irrigation.events.QuizResponseEvent;
import edu.asu.commons.irrigation.events.RegistrationEvent;
import edu.asu.commons.irrigation.events.RoundStartedEvent;
import edu.asu.commons.irrigation.events.ShowGameScreenshotRequest;
import edu.asu.commons.irrigation.events.ShowInstructionsRequest;
import edu.asu.commons.irrigation.events.ShowQuizRequest;
import edu.asu.commons.irrigation.events.ShowTokenInvestmentScreenRequest;
import edu.asu.commons.net.Dispatcher;
import edu.asu.commons.net.Identifier;
import edu.asu.commons.net.SocketIdentifier;
import edu.asu.commons.net.event.ConnectionEvent;
import edu.asu.commons.net.event.DisconnectionRequest;
import edu.asu.commons.util.Duration;
import edu.asu.commons.util.Utils;

/**
 * $Id$
 * 
 * Main entry point for the irrigation experiment server.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */
public class IrrigationServer extends AbstractExperiment<ServerConfiguration, RoundConfiguration> {

    private final Map<Identifier, ClientData> clients = new LinkedHashMap<Identifier, ClientData>();

    private final static int SERVER_SLEEP_INTERVAL = 100;

    private final Object roundSignal = new Object();

    private Duration currentRoundDuration;

    private final ServerDataModel serverDataModel;

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
        serverDataModel.setRoundConfiguration(configuration.getCurrentParameters());
        persister = new IrrigationPersister(getEventChannel(), configuration);
        initializeFacilitatorHandlers();
        initializeClientHandlers();
    }

    private void initializeFacilitatorHandlers() {
        addEventProcessor(new EventTypeProcessor<ShowGameScreenshotRequest>(ShowGameScreenshotRequest.class) {
            public void handle(ShowGameScreenshotRequest request) {
                // FIXME: check request id against facilitator id?
                synchronized (clients) {
                    for (Identifier id: clients.keySet()) {
                        transmit(new ShowGameScreenshotRequest(id));
                    }
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<ShowQuizRequest>(ShowQuizRequest.class) {
            public void handle(ShowQuizRequest request) {
                // FIXME: check request id against facilitator id?
                synchronized (clients) {
                    for (Identifier id: clients.keySet()) {
                        transmit(new ShowQuizRequest(id));
                    }
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<FacilitatorRegistrationRequest>(FacilitatorRegistrationRequest.class) {
            @Override
            public void handle(FacilitatorRegistrationRequest event) {
                getLogger().info("facilitator registered: " + event.getId());
                // remap the facilitator ID and remove from the clients list.
                facilitatorId = event.getId();
                synchronized (clients) {
                    clients.remove(facilitatorId);
                    serverDataModel.removeClient(facilitatorId);
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<BeginExperimentRequest>(BeginExperimentRequest.class) {
            @Override
            public void handle(BeginExperimentRequest event) {
            	// sends override and immediately starts the round.
                synchronized (roundSignal) {
                    roundSignal.notifyAll();
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<BeginRoundRequest>(BeginRoundRequest.class) {
            @Override
            public void handle(BeginRoundRequest event) {
                if (! event.getId().equals(facilitatorId)) {
                    getLogger().warning(
                            String.format("facilitator is [%s] but received begin round request from non-facilitator [%s]", facilitatorId, event.getId()));
                    return;
                }
                // ignore the request if not every group has submit their tokens.
                if (isTokenInvestmentComplete()) {
                    synchronized (roundSignal) {
                        roundSignal.notifyAll();
                    }
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<EndRoundRequest>(EndRoundRequest.class) {
            @Override
            public void handleInExperimentThread(EndRoundRequest request) {
                currentRoundDuration.stop();
            }

        });
        addEventProcessor(new EventTypeProcessor<BeginChatRoundRequest>(BeginChatRoundRequest.class) {
            @Override
            public void handle(BeginChatRoundRequest request) {
                // XXX: the participants have already been added to the data model at this point
                // so we shuffle them around right before the first practice round's chat.
            	if (getRoundConfiguration().isFirstRound()) {
            		shuffleParticipants();
            	}
            	else {
            	    persister.clearChatData();
            	}
                // pass it on to all the clients
                synchronized (clients) {
                    for (Identifier id: clients.keySet()) {
                        transmit(new BeginChatRoundRequest(id, serverDataModel.getGroupDataModel(id)));
                    }
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<ShowInstructionsRequest>(ShowInstructionsRequest.class) {
            @Override
            public void handle(ShowInstructionsRequest request) {
                synchronized (clients) {
                    for (Identifier id: clients.keySet()) {
                        transmit(new ShowInstructionsRequest(id));
                    }
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<ShowTokenInvestmentScreenRequest>(ShowTokenInvestmentScreenRequest.class) {
            @Override
            public void handle(ShowTokenInvestmentScreenRequest request) {
                synchronized (clients) {
                    for (Identifier id: clients.keySet()) {
                        transmit(new ShowTokenInvestmentScreenRequest(id));
                    }
                }
            }
        });
    }

    /**
     * Registers client handling EventTypeProcessors.  
     * Each EventTypeProcessor encapsulates the handling of a specific kind of message event.
     */
    private void initializeClientHandlers() {
        addEventProcessor(new EventTypeProcessor<SocketIdentifierUpdateRequest>(SocketIdentifierUpdateRequest.class) {
            @Override
            public void handle(SocketIdentifierUpdateRequest request) {
                SocketIdentifier socketId = request.getSocketIdentifier();
                //getLogger().info("socket id from client: " + socketId);
                //getLogger().info("station number from client: " + socketId.getStationNumber());
                //getLogger().info("station number from event: " + request.getStationNumber());
                ClientData clientData = clients.get(socketId);
                if (clientData == null) {
                    getLogger().warning("No client data available for socket: " + socketId);
                    return;
                }
                SocketIdentifier clientSocketId = (SocketIdentifier) clientData.getId();
                clientSocketId.setStationNumber(request.getStationNumber());
            }
        });
        addEventProcessor(new EventTypeProcessor<ConnectionEvent>(ConnectionEvent.class) {
            @Override
            public void handle(ConnectionEvent event) {
                getLogger().info("incoming connection: " + event);
                // handle incoming connections
                Identifier identifier = event.getId();
                ClientData clientData = new ClientData(identifier);
                synchronized (clients) {
                    clients.put(identifier, clientData);
                    serverDataModel.addClient(clientData);
                }
                transmit(new RegistrationEvent(clientData, getRoundConfiguration()));
            }
        });
        addEventProcessor(new EventTypeProcessor<DisconnectionRequest>(DisconnectionRequest.class) {
            @Override
            public void handle(DisconnectionRequest request) {
                getLogger().warning("irrigation server handling disconnection request: " + request);
                Identifier disconnectedClientId = request.getId();
                if (disconnectedClientId.equals(facilitatorId)) {
                    getLogger().warning("Disconnecting facilitator.");
                    facilitatorId = null;
                    return;
                }
                synchronized (clients) {
                    clients.remove(disconnectedClientId);
                    serverDataModel.removeClient(disconnectedClientId);
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<ChatRequest>(ChatRequest.class) {
            @Override
            public void handle(ChatRequest request) {
                Identifier source = request.getSource();
                Identifier target = request.getTarget();
                ClientData sendingClient = clients.get(source);
                if (Identifier.ALL.equals(target)) {
                    // relay to all clients in this client's group.
                    sendFacilitatorMessage(String.format("%s sending [ %s ] to all group participants", request.getSource(), request));
                    boolean restrictedVisibility = getRoundConfiguration().isRestrictedVisibility();
                    for (ClientData clientData: clients.get(source).getGroupDataModel().getClientDataMap().values()) {
                        Identifier targetId = clientData.getId();
                        if (targetId.equals(source)) {
                            continue;
                        }
                        if (restrictedVisibility && ! sendingClient.isImmediateNeighbor(clientData)) {
                            sendFacilitatorMessage(String.format("%s out of range of %s, not sending message [%s]", clientData, sendingClient, request.getMessage()));
                            continue;
                        }
                        ChatEvent chatEvent = new ChatEvent(targetId, request.getMessage(), source, true);
                        transmit(chatEvent);
                    }
                }
                else {
                    getLogger().info(String.format("%s sending [%s] to target [%s]", request.getSource(), request, request.getTarget()));
                    ChatEvent chatEvent = new ChatEvent(request.getTarget(), request.getMessage(), request.getSource());                  
                    transmit(chatEvent);
                }
                persister.store(request);
            }
        });
        addEventProcessor(new EventTypeProcessor<InvestedTokensEvent>(InvestedTokensEvent.class) {
            @Override
            public void handle(InvestedTokensEvent event) {
                if (isTokenInvestmentComplete()) {
                    getLogger().severe("Trying to invest more tokens but token investment is already complete:" + event);
                    return;
                }
                clients.get(event.getId()).setInvestedTokens(event.getInvestedTokens());
                submittedClients++;
                if (isTokenInvestmentComplete()) {
                    // everyone's submitted their tokens so we can calculate the available bandwidth and 
                    // notify each client
                    initializeInfrastructureEfficiency();
                }
            }
        });
        addEventProcessor(new EventTypeProcessor<QuizResponseEvent>(QuizResponseEvent.class) {
            @Override
            public void handle(QuizResponseEvent event) {
                getLogger().info("Completed quizzes: " + numberOfCompletedQuizzes);
                numberOfCompletedQuizzes++;
                persister.store(event);
            }
        });
        addEventProcessor(new EventTypeProcessor<OpenGateEvent>(OpenGateEvent.class) {
            public void handle(OpenGateEvent event) {
                clients.get(event.getId()).openGate();
            }
        });
        addEventProcessor(new EventTypeProcessor<CloseGateEvent>(CloseGateEvent.class) {
            public void handle(CloseGateEvent event) {
                clients.get(event.getId()).closeGate();
            }
        });
    }
    private void sendFacilitatorMessage(String message) {
        getLogger().info(message);
        transmit(new FacilitatorMessageEvent(facilitatorId, message));
    }
    private boolean isTokenInvestmentComplete() {
        return submittedClients >= clients.size();
    }

    /**
     * Invoked after every client has submit their tokens
     *
     */
    public void initializeInfrastructureEfficiency() {
        // clients are added to the ServerDataModel as they register and then reallocated if necessary in 
        // post round cleanup
        /////////////////////////////////////////////////////////////////////////////////
        for(GroupDataModel group : serverDataModel.getAllGroupDataModels()){
            group.initializeInfrastructure();            
            // iterate through all groups and send back their contribution status
            for (Identifier id : group.getClientDataMap().keySet()) {
                InfrastructureUpdateEvent infrastructureUpdateEvent = new InfrastructureUpdateEvent(id, group);
                transmit(infrastructureUpdateEvent);
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
        int timeLeft = (int) (currentRoundDuration.getTimeLeft() / 1000);
        // allocate bandwidth to each client
        for (ClientData clientData : group.getClientDataMap().values()) {
            // for undisrupted flow extensions, disabled for the time being.
//            if (clientData.getAvailableFlowCapacity() <= 0 && getConfiguration().isUndisruptedFlowRequired()){
//                clientData.init(group.getCurrentlyAvailableFlowCapacity());
//            }
            if (clientData.isGateOpen()) {
                group.allocateWater(clientData);
            }
            else {
                clientData.init(group.getAvailableClientFlowCapacity());
            }
        }
        for (Identifier id: group.getAllClientIdentifiers()) {
            ClientUpdateEvent clientUpdateEvent = new ClientUpdateEvent(id, group, timeLeft);
            transmit(clientUpdateEvent);
        }
    }

    public static void main(String[] args) {
        IrrigationServer server = new IrrigationServer(new ServerConfiguration()); 
        server.start();
        server.repl();
    }

    private void shuffleParticipants() {
        serverDataModel.clear();
        List<ClientData> clientDataList = new ArrayList<ClientData>(clients.values());
        // randomize the client data list 
        Collections.shuffle(clientDataList);
        // re-add each the clients to the server data model 
        for (ClientData data: clientDataList) {
            serverDataModel.addClient(data);
        }
    }
    

    enum IrrigationServerState { WAITING, ROUND_IN_PROGRESS };

    private class IrrigationServerStateMachine implements StateMachine {

        private IrrigationServerState state;
        
//        private long lastTime;
        
        private Duration secondTick = Duration.create(1000L);

        public void initialize() {
            // FIXME: may want to change this as we add more states.
            state = IrrigationServerState.WAITING;
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
            state = IrrigationServerState.ROUND_IN_PROGRESS;
            secondTick.start();
            persister.store(new RoundStartedMarkerEvent());
//            lastTime = System.currentTimeMillis();
        }

        private void processRound() {
//            if ((System.currentTimeMillis() - lastTime) / 1000 > 1) {
            if (secondTick.hasExpired()) {
                for (GroupDataModel group: serverDataModel.getAllGroupDataModels()) {
                    // reset available bandwidth for this group to calculate new allocations for the group
                    // perform bandwidth calculations
                    try{
                        process(group); 
                    }
                    catch (RuntimeException exception) {
                        exception.printStackTrace();
                        getLogger().throwing(IrrigationServerStateMachine.class.getName(), "processRound", exception);
                    }
                }
//                lastTime = System.currentTimeMillis();
                secondTick.restart();
            }
        }

        private void stopRound() {
            state = IrrigationServerState.WAITING;
            sendEndRoundEvents();
            persistRound();
            cleanupRound();
            // FIXME: have to wait for .... some reason?
            Utils.sleep(2000);
            advanceToNextRound();
        }

        private void sendEndRoundEvents() {
            // need to send instructions
            //Send the end round event to the facilitator
            //Send the end round event to all the clients
            synchronized (clients) {
                // add collected tokens to total if this isn't a practice round.
                if (! getRoundConfiguration().isPracticeRound()) {
                    // first add tokens
                    for (ClientData data : clients.values()) {
                        data.addTokensEarnedThisRoundToTotal();
                    }
                }
                for (ClientData data : clients.values()) {
                    transmit(new EndRoundEvent(data.getId(), 
                            data.getGroupDataModel(), 
                            getConfiguration().isLastRound()));    
                }
            }
            transmit(new FacilitatorEndRoundEvent(facilitatorId, serverDataModel));

        }

        private void persistRound() {
            persister.persist(serverDataModel);
        }

        private void cleanupRound() {            
            // reset client data values
            synchronized (clients) {
                for (ClientData clientData: clients.values()) {
                    clientData.resetEndRound();
                }
            }
            submittedClients = 0;
            persister.clear();
        }
        

        private void advanceToNextRound() {
            if (getConfiguration().isLastRound()) {
                state = IrrigationServerState.WAITING;
                return;
            }
            RoundConfiguration nextRoundConfiguration = getConfiguration().nextRound();
            serverDataModel.setRoundConfiguration(nextRoundConfiguration);
            // set up the next round
            synchronized (clients) {
                if (nextRoundConfiguration.shouldRandomizeGroup()) {
                	shuffleParticipants();

                }      
                // send registration events to all participants.
                for (ClientData data: clients.values()) {
                    transmit(new RegistrationEvent(data, nextRoundConfiguration));
                }
            }
            // send new round configuration to the facilitator
            transmit(new RegistrationEvent(facilitatorId, nextRoundConfiguration));
            persister.initialize(nextRoundConfiguration);
        }

        public void execute(Dispatcher dispatcher) {
            switch (state) {
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
                Utils.sleep(SERVER_SLEEP_INTERVAL);
                break;
            case WAITING:
                getLogger().info("Waiting for facilitator signal to start next round.");
                Utils.waitOn(roundSignal);
                initializeRound();
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
