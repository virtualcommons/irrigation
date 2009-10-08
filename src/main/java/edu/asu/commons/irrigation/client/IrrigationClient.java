/**
 *
 */
package edu.asu.commons.irrigation.client;

import java.awt.Dimension;
import java.awt.Frame;
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
import edu.asu.commons.irrigation.events.BeginCommunicationRequest;
import edu.asu.commons.irrigation.events.DisplaySubmitTokenRequest;
import edu.asu.commons.irrigation.events.EndRoundEvent;
import edu.asu.commons.irrigation.events.GateOpenedEvent;
import edu.asu.commons.irrigation.events.InstructionEnableRequest;
import edu.asu.commons.irrigation.events.InvestedTokensEvent;
import edu.asu.commons.irrigation.events.RegistrationEvent;
import edu.asu.commons.irrigation.events.RoundStartedEvent;
import edu.asu.commons.irrigation.events.SendContributionStatusEvent;
import edu.asu.commons.irrigation.events.SendFileProgressEvent;
import edu.asu.commons.irrigation.events.StartPausedEvent;
import edu.asu.commons.irrigation.events.StopDownloadEvent;
import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.net.ClientDispatcher;
import edu.asu.commons.net.DispatcherFactory;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 */
public class IrrigationClient {
    /**
     * @param args
     */
    enum ClientState {
        UNCONNECTED, CONNECTED, READY, RUNNING, DENIED
    };

    // FIXME: change to enum
    public final static int PRECONNECT = 0;

    public final static int CONNECTED = 1;

    public final static int READY = 2;

    public final static int RUNNING = 3;

    public final static int DENIED = 100;

    private int state = PRECONNECT;

    private ServerConfiguration serverConfiguration;

    private ClientDispatcher clientDispatcher;

    private Identifier id;

    // private MainIrrigationGameWindow mainIrrigationGameWindow;

    private ExperimentGameWindow experimentGameWindow;

    IrrigationGameWindow currentWindow = null;

    private IrrigationClientGameState clientGameState;

    private final EventChannel channel;

    Map<Identifier, ClientData> updatedClientDataMap = new LinkedHashMap<Identifier, ClientData>();

    private IrrigationClient(Dimension screenSize) {
        this(EventChannelFactory.create(), screenSize, new ServerConfiguration());
    }

    public IrrigationClient(EventChannel channel, Dimension screenSize,
            ServerConfiguration configuration) {
        this.channel = channel;
        this.serverConfiguration = configuration;
        // moved this line down to the connect so that we get a new instance
        // of a dispatcher every time we connect

        clientDispatcher = DispatcherFactory.getInstance().createClientDispatcher(channel);
        clientGameState = new IrrigationClientGameState(channel, this);
        experimentGameWindow = new ExperimentGameWindow(screenSize, this);
        // clientGameState.setMainIrrigationGameWindow(irrigationGameWindow1);
        initEventProcessors();
    }

    public void connect() {
        System.err.println("connecting to: " + serverConfiguration.getServerAddress()
                + " state: " + state);
        if (state != PRECONNECT)
            return;
        id = clientDispatcher.connect(serverConfiguration.getServerAddress());
        System.out.println("\nThe id is " + id);
        if (id == null) {
            throw new RuntimeException("Null ID from Dispatcher.  Server: <"
                    + serverConfiguration.getServerAddress() + "> is probably down.");
        }
        state = READY;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Runnable createGuiRunnable = new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                Dimension defaultDimension = new Dimension(500, 500);
                Frame frame = new JFrame();
                IrrigationClient client = new IrrigationClient(defaultDimension);
                client.connect();
                frame.setTitle("Client Window: " + client.id);
                frame.setSize(1130, 600);

                ((JFrame) frame).getContentPane().add(
                        client.getExperimentGameWindow());
                ((JFrame) frame).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    public void transmitTokenContributed(int tokens) {
        InvestedTokensEvent transferTokenBandwidthEvent = new InvestedTokensEvent(
                getId(), tokens);
        clientDispatcher.transmit(transferTokenBandwidthEvent);
    }

    /**
     * Transmitting the file string , that is the file chosen for the current
     * file download
     * 
     * @param fileNo
     */
    public void startDownload(String fileNo) {
        GateOpenedEvent startDownloadEvent = new GateOpenedEvent(getId());
        startDownloadEvent.setFileNumber(fileNo);
        clientDispatcher.transmit(startDownloadEvent);
    }

    public void stopDownload(String fileNo) {
        StopDownloadEvent stopDownloadEvent = new StopDownloadEvent(getId());
        stopDownloadEvent.setFileNumber(fileNo);
        clientDispatcher.transmit(stopDownloadEvent);
    }

    public void startPause(String fileNo) {
        StartPausedEvent startPausedEvent = new StartPausedEvent(getId());
        startPausedEvent.setFileNumber(fileNo);
        clientDispatcher.transmit(startPausedEvent);
    }
    
    public void transmit(Event event) {
        clientDispatcher.transmit(event);
    }

    Identifier getId() {
        return id;
    }

    private void initEventProcessors() {
        channel.add(this, new EventTypeProcessor<RegistrationEvent>(RegistrationEvent.class) {
            public void handle(RegistrationEvent event) {
                RoundConfiguration configuration = event.getRoundConfiguration();
                setRoundConfiguration(configuration);
                clientGameState.setPriority(event.getClientData().getPriority());
                // FIXME: display priority
                if (!configuration.isPracticeRound() || configuration.isSecondPracticeRound()) {
                    experimentGameWindow.updateRoundInstructions(configuration.getInstructions(),event.getClientData().getPriority());
                }
            }
        });
        channel.add(this, new EventTypeProcessor<SendContributionStatusEvent>(SendContributionStatusEvent.class) {
            public void handle(SendContributionStatusEvent event) {
                clientGameState.setGroupDataModel(event.getGroupDataModel());
                experimentGameWindow.updateGraphDisplay(event.getClientDataMap().get(event.getId()));
                experimentGameWindow.updateSendContributionStatus();
            }
        });
        channel.add(this, new EventTypeProcessor<RoundStartedEvent>(RoundStartedEvent.class) {
            public void handle(RoundStartedEvent event) {
                clientGameState.initialize(event);
                experimentGameWindow.startRound(getRoundConfiguration());
            }
        });
        channel.add(this, new EventTypeProcessor<EndRoundEvent>(EndRoundEvent.class) {
            public void handle(EndRoundEvent event) {
                experimentGameWindow.updateEndRoundEvent(event);
            }
        });
        channel.add(this, new EventTypeProcessor<SendFileProgressEvent>(SendFileProgressEvent.class) {
            public void handle(SendFileProgressEvent sendFileProgressEvent) {
                // setting the clientDataMap for this client's clientGameState
                clientGameState.setGroupDataModel(sendFileProgressEvent.getGroupDataModel());
                clientGameState.setTimeRemaining(sendFileProgressEvent.getTimeRemaining());
                experimentGameWindow.updateSendFileProgress();
            }
        });
        channel.add(this, new EventTypeProcessor<DisplaySubmitTokenRequest>(DisplaySubmitTokenRequest.class) {
            public void handle(DisplaySubmitTokenRequest request) {
                experimentGameWindow.updateSubmitTokenScreenDisplay();
            }
        });
        channel.add(this, new EventTypeProcessor<BeginCommunicationRequest>(BeginCommunicationRequest.class) {
            public void handle(BeginCommunicationRequest request) {
                clientGameState.setGroupDataModel(request.getGroupDataModel());
                experimentGameWindow.initializeChatWindow();
            }
        });
        channel.add(this, new EventTypeProcessor<InstructionEnableRequest>(InstructionEnableRequest.class) {
            public void handle(InstructionEnableRequest request) {
                experimentGameWindow.enableInstructions();
            }
        });
    }

    public IrrigationClientGameState getClientGameState() {
        return clientGameState;
    }

    public RoundConfiguration getRoundConfiguration() {
  
    	return clientGameState.getConfiguration();
    }

    public void setRoundConfiguration(RoundConfiguration roundConfiguration) {
        clientGameState.setConfiguration(roundConfiguration);
    }

    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    public void setServerConfiguration(ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
    }

}
