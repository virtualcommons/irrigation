package edu.asu.commons.irrigation.client;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import edu.asu.commons.event.Event;
import edu.asu.commons.event.EventChannel;
import edu.asu.commons.event.EventChannelFactory;
import edu.asu.commons.event.EventTypeProcessor;
import edu.asu.commons.event.SocketIdentifierUpdateRequest;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.irrigation.events.BeginChatRoundRequest;
import edu.asu.commons.irrigation.events.ClientUpdateEvent;
import edu.asu.commons.irrigation.events.CloseGateEvent;
import edu.asu.commons.irrigation.events.EndRoundEvent;
import edu.asu.commons.irrigation.events.InfrastructureUpdateEvent;
import edu.asu.commons.irrigation.events.InvestedTokensEvent;
import edu.asu.commons.irrigation.events.OpenGateEvent;
import edu.asu.commons.irrigation.events.RegistrationEvent;
import edu.asu.commons.irrigation.events.RoundStartedEvent;
import edu.asu.commons.irrigation.events.ShowGameScreenshotRequest;
import edu.asu.commons.irrigation.events.ShowInstructionsRequest;
import edu.asu.commons.irrigation.events.ShowQuizRequest;
import edu.asu.commons.irrigation.events.ShowTokenInvestmentScreenRequest;
import edu.asu.commons.net.ClientDispatcher;
import edu.asu.commons.net.DispatcherFactory;
import edu.asu.commons.net.Identifier;
import edu.asu.commons.net.SocketIdentifier;
import edu.asu.commons.ui.UserInterfaceUtils;

/**
 * $Id$
 * 
 * Irrigation client main entry point / controller that ties together the GUI component, networking logic via the Dispatcher, 
 * and general game logic.
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>, Sanket Joshi
 * @version $Rev$
 */
public class IrrigationClient {

    enum ClientState {
        UNCONNECTED, CONNECTED, READY, RUNNING, DENIED
    };

    private ClientState state = ClientState.UNCONNECTED;

    private ServerConfiguration serverConfiguration;

    private ClientDispatcher clientDispatcher;

    private Identifier id;

    private ExperimentGameWindow experimentGameWindow;

    private ClientDataModel clientDataModel;

    private final EventChannel channel;

    private IrrigationClient() {
        this(EventChannelFactory.create(), new ServerConfiguration());
    }

    public IrrigationClient(EventChannel channel, ServerConfiguration serverConfiguration) {
        this.channel = channel;
        setServerConfiguration(serverConfiguration);
        this.clientDispatcher = DispatcherFactory.getInstance().createClientDispatcher(channel, serverConfiguration);
        initEventProcessors();
    }
    
    private void initialize() {
    	clientDataModel = new ClientDataModel(channel, this);
        experimentGameWindow = new ExperimentGameWindow(this);
        connect();
    }

    public void connect() {
        if (state != ClientState.UNCONNECTED)
            return;
        
        id = clientDispatcher.connect(serverConfiguration.getServerAddress());
        if (id == null) {
            throw new RuntimeException(
            		"Null ID from Dispatcher.  Server: <"
            		+ serverConfiguration.getServerAddress() + "> is probably down.");
        }
        // send back id
        SocketIdentifier socketId = (SocketIdentifier) id;
        transmit(new SocketIdentifierUpdateRequest(socketId, socketId.getStationNumber()));
        state = ClientState.CONNECTED;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        Runnable createGuiRunnable = new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                IrrigationClient client = new IrrigationClient();
                client.initialize();
                frame.setTitle("Client Window: " + client.getId());
                frame.add(client.getExperimentGameWindow());
                UserInterfaceUtils.maximize(frame);
            }
        };
        SwingUtilities.invokeLater(createGuiRunnable);
    }

    public ExperimentGameWindow getExperimentGameWindow() {
        return experimentGameWindow;
    }

    public EventChannel getEventChannel() {
        return channel;
    }

    public void transmitInvestedTokensEvent(int tokens) {
        InvestedTokensEvent investedTokensEvent = new InvestedTokensEvent(getId(), tokens);
        transmit(investedTokensEvent);
    }

    public void openGate() {
        OpenGateEvent openGateEvent = new OpenGateEvent(getId());
        transmit(openGateEvent);
    }

    public void closeGate() {
        CloseGateEvent closeGateEvent = new CloseGateEvent(getId());
        transmit(closeGateEvent);
    }
    
    public void transmit(Event event) {
        clientDispatcher.transmit(event);
    }

    Identifier getId() {
        return id;
    }

    private void initEventProcessors() {
        // registration events are sent before the start of each round and contain the configuration
        // for that round, including all instructions, parameters, for that round.
        channel.add(this, new EventTypeProcessor<RegistrationEvent>(RegistrationEvent.class) {
            public void handle(RegistrationEvent event) {
                RoundConfiguration configuration = event.getRoundConfiguration();
                clientDataModel.setGroupDataModel(event.getClientData().getGroupDataModel());
                clientDataModel.setRoundConfiguration(configuration);
                experimentGameWindow.updateRoundInstructions(configuration);
            }
        });
        channel.add(this, new EventTypeProcessor<InfrastructureUpdateEvent>(InfrastructureUpdateEvent.class) {
            public void handle(InfrastructureUpdateEvent event) {
                System.err.println("Received group update event: " + event);
                clientDataModel.setGroupDataModel(event.getGroupDataModel());
                experimentGameWindow.displayContributionInformation(event.getClientData());
            }
        });
        channel.add(this, new EventTypeProcessor<RoundStartedEvent>(RoundStartedEvent.class) {
            public void handle(RoundStartedEvent event) {
                clientDataModel.initialize(event);
                experimentGameWindow.startRound();
            }
        });
        channel.add(this, new EventTypeProcessor<EndRoundEvent>(EndRoundEvent.class) {
            public void handle(EndRoundEvent event) {
            	clientDataModel.setGroupDataModel(event.getGroupDataModel());
                experimentGameWindow.endRound(event);
            }
        });
        channel.add(this, new EventTypeProcessor<ClientUpdateEvent>(ClientUpdateEvent.class) {
            public void handle(ClientUpdateEvent clientUpdateEvent) {
                // update the client game state and then update the view.
                clientDataModel.update(clientUpdateEvent);
                experimentGameWindow.update();
            }
        });
        channel.add(this, new EventTypeProcessor<ShowTokenInvestmentScreenRequest>(ShowTokenInvestmentScreenRequest.class) {
            public void handle(ShowTokenInvestmentScreenRequest request) {
                experimentGameWindow.showTokenInvestmentScreen();
            }
        });
        channel.add(this, new EventTypeProcessor<BeginChatRoundRequest>(BeginChatRoundRequest.class) {
            public void handle(BeginChatRoundRequest request) {
                clientDataModel.setGroupDataModel(request.getGroupDataModel());
                experimentGameWindow.initializeChatWindow();
            }
        });
        channel.add(this, new EventTypeProcessor<ShowInstructionsRequest>(ShowInstructionsRequest.class) {
            public void handle(ShowInstructionsRequest request) {
                experimentGameWindow.showInstructions();
            }
        });
        channel.add(this, new EventTypeProcessor<ShowQuizRequest>(ShowQuizRequest.class) {
            public void handle(ShowQuizRequest request) {
                experimentGameWindow.showQuiz();
            }
        });
        channel.add(this, new EventTypeProcessor<ShowGameScreenshotRequest>(ShowGameScreenshotRequest.class) {
            public void handle(ShowGameScreenshotRequest request) {
                experimentGameWindow.showGameScreenshot();
            }
        });
    }

    public ClientDataModel getClientDataModel() {
        return clientDataModel;
    }

    public RoundConfiguration getRoundConfiguration() {
      	return clientDataModel.getRoundConfiguration();
    }

    public void setRoundConfiguration(RoundConfiguration roundConfiguration) {
        clientDataModel.setRoundConfiguration(roundConfiguration);
    }

    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }
    
    public void setServerConfiguration(ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
    }

}
