package edu.asu.commons.irrigation.client;

import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.asu.commons.event.Event;
import edu.asu.commons.event.EventChannel;
import edu.asu.commons.event.EventChannelFactory;
import edu.asu.commons.event.EventTypeProcessor;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.irrigation.events.BeginChatRoundRequest;
import edu.asu.commons.irrigation.events.ClientUpdateEvent;
import edu.asu.commons.irrigation.events.CloseGateEvent;
import edu.asu.commons.irrigation.events.DisplaySubmitTokenRequest;
import edu.asu.commons.irrigation.events.EndRoundEvent;
import edu.asu.commons.irrigation.events.InfrastructureUpdateEvent;
import edu.asu.commons.irrigation.events.InvestedTokensEvent;
import edu.asu.commons.irrigation.events.OpenGateEvent;
import edu.asu.commons.irrigation.events.PauseRequest;
import edu.asu.commons.irrigation.events.RegistrationEvent;
import edu.asu.commons.irrigation.events.RoundStartedEvent;
import edu.asu.commons.irrigation.events.ShowInstructionsRequest;
import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.net.ClientDispatcher;
import edu.asu.commons.net.DispatcherFactory;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
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

    // private MainIrrigationGameWindow mainIrrigationGameWindow;

    private ExperimentGameWindow experimentGameWindow;

    IrrigationGameWindow currentWindow = null;

    private ClientDataModel clientDataModel;

    private final EventChannel channel;

    Map<Identifier, ClientData> updatedClientDataMap = new LinkedHashMap<Identifier, ClientData>();

    private IrrigationClient() {
        this(EventChannelFactory.create(), new ServerConfiguration());
    }

    public IrrigationClient(EventChannel channel, ServerConfiguration serverConfiguration) {
        this.channel = channel;
        setServerConfiguration(serverConfiguration);
        this.clientDispatcher = DispatcherFactory.getInstance().createClientDispatcher(channel);
        initEventProcessors();
    }
    
    private void initialize(Dimension screenSize) {
        clientDataModel = new ClientDataModel(channel, this);
        experimentGameWindow = new ExperimentGameWindow(this);
        experimentGameWindow.initialize(screenSize);
        connect();
    }

    public void connect() {
        System.err.println("connecting to: " + serverConfiguration.getServerAddress()
                + " state: " + state);
        if (state != ClientState.UNCONNECTED)
            return;
        
        id = clientDispatcher.connect(serverConfiguration.getServerAddress());
        if (id == null) {
            throw new RuntimeException("Null ID from Dispatcher.  Server: <"
                    + serverConfiguration.getServerAddress() + "> is probably down.");
        }
        state = ClientState.CONNECTED;
    }

    public static void main(String[] args) {
        Runnable createGuiRunnable = new Runnable() {
            public void run() {
                Dimension defaultDimension = new Dimension(500, 500);
                JFrame frame = new JFrame();
                IrrigationClient client = new IrrigationClient();
                client.initialize(defaultDimension);
                frame.setTitle("Virtual Commons Experiment Client: " + client.id);
                frame.setSize(1130, 600);
                frame.getContentPane().add(client.getExperimentGameWindow());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
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

    /**
     * Transmitting the file string , that is the file chosen for the current
     * file download
     * 
     * @param fileNo
     */
    public void openGate() {
        OpenGateEvent openGateEvent = new OpenGateEvent(getId());
        transmit(openGateEvent);
    }

    public void closeGate(String fileNo) {
        CloseGateEvent closeGateEvent = new CloseGateEvent(getId());
        closeGateEvent.setFileNumber(fileNo);
        transmit(closeGateEvent);
    }

    public void pause() {
        PauseRequest pauseRequest = new PauseRequest(getId());
        transmit(pauseRequest);
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
                experimentGameWindow.updateGraphDisplay(event.getClientData());
            }
        });
        channel.add(this, new EventTypeProcessor<RoundStartedEvent>(RoundStartedEvent.class) {
            public void handle(RoundStartedEvent event) {
                clientDataModel.initialize(event);
                experimentGameWindow.startRound(getRoundConfiguration());
            }
        });
        channel.add(this, new EventTypeProcessor<EndRoundEvent>(EndRoundEvent.class) {
            public void handle(EndRoundEvent event) {
                experimentGameWindow.updateEndRoundEvent(event);
            }
        });
        channel.add(this, new EventTypeProcessor<ClientUpdateEvent>(ClientUpdateEvent.class) {
            public void handle(ClientUpdateEvent clientUpdateEvent) {
                // update the client game state and then update the view.
                clientDataModel.update(clientUpdateEvent);
                experimentGameWindow.update();
            }
        });
        channel.add(this, new EventTypeProcessor<DisplaySubmitTokenRequest>(DisplaySubmitTokenRequest.class) {
            public void handle(DisplaySubmitTokenRequest request) {
                experimentGameWindow.updateSubmitTokenScreenDisplay();
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
                experimentGameWindow.enableInstructions();
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
