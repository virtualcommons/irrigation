package edu.asu.commons.irrigation.facilitator;

import java.awt.Dimension;
import java.net.InetSocketAddress;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.asu.commons.event.BeginExperimentRequest;
import edu.asu.commons.event.BeginRoundRequest;
import edu.asu.commons.event.ConfigurationEvent;
import edu.asu.commons.event.EndRoundRequest;
import edu.asu.commons.event.Event;
import edu.asu.commons.event.EventTypeChannel;
import edu.asu.commons.event.EventTypeProcessor;
import edu.asu.commons.event.FacilitatorRegistrationRequest;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.irrigation.events.FacilitatorEndRoundEvent;
import edu.asu.commons.irrigation.events.ServerGameStateEvent;
import edu.asu.commons.irrigation.server.ServerDataModel;
import edu.asu.commons.net.ClientDispatcher;
import edu.asu.commons.net.DispatcherFactory;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class Facilitator {

    private final static Facilitator INSTANCE = new Facilitator();

    private Identifier id;  //  @jve:decl-index=0:

    private ClientDispatcher dispatcher;

    private ServerConfiguration configuration;

    private ServerDataModel serverGameState;  //  @jve:decl-index=0:

    private FacilitatorWindow irrigationFacilitatorWindow;

    private boolean stopExperiment = false;

    private boolean experimentRunning = false;

    private final EventTypeChannel channel = new EventTypeChannel();

    private Facilitator() {
        this(new ServerConfiguration());
    }

    public Facilitator(ServerConfiguration configuration) {
        dispatcher = DispatcherFactory.getInstance().createClientDispatcher(channel);
        setConfiguration(configuration);
        initializeEventProcessors();
    }

    @SuppressWarnings("unchecked")
    private void initializeEventProcessors() {
        channel.add(new EventTypeProcessor<ConfigurationEvent>(ConfigurationEvent.class) {
            public void handle(ConfigurationEvent configurationEvent) {
                setConfiguration((ServerConfiguration) configurationEvent.getConfiguration());
            }
        });
        channel.add(new EventTypeProcessor<FacilitatorEndRoundEvent>(FacilitatorEndRoundEvent.class) {
            public void handle(FacilitatorEndRoundEvent event) {
                //                serverGameState = null;
                irrigationFacilitatorWindow.endRound(event);
                configuration.nextRound();
            }
        });
    }

    public static Facilitator getInstance(){
        return INSTANCE;
    }

    public void setConfiguration(ServerConfiguration configuration) {
        if (configuration == null) {
            System.err.println("attempt to setConfiguration with null, ignoring");
            return;
        } 
        else {
            this.configuration = configuration;
        }
    }

    /**
     * @param args
     */

    /*
     * Connects facilitator to the server and registers with the server as a facilitator.
     * 
     * If the connection was successful, configures the FacilitatorWindow to manage experiments,
     * otherwise configures the FacilitatorWindow to replay experiments and view configuration 
     * for those experiments.
     */
    public void connect() {
        connect(configuration.getServerAddress());
    }

    public void connect(InetSocketAddress address) {
        id = dispatcher.connect(address);
        if (id != null) {
            transmit(new FacilitatorRegistrationRequest(id));
        }

    }

    void createFacilitatorWindow(Dimension dimension) {
        irrigationFacilitatorWindow = new FacilitatorWindow(dimension, this);
    }

    /*
     * Sends requests to the server
     */
    public void transmit(Event event) {
        dispatcher.transmit(event);
    }

    public static void main(String[] args) {
        Runnable createGuiRunnable = new Runnable(){
            public void run() {
                Dimension dimension = new Dimension(500, 600);
                Facilitator facilitator = Facilitator.getInstance();
                facilitator.connect();
                JFrame frame = new JFrame();
                frame.setTitle("Facilitator window: " + facilitator.id);
                frame.setSize((int) dimension.getWidth(), (int) dimension.getHeight());
                facilitator.createFacilitatorWindow(dimension);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(facilitator.getFacilitatorWindow());
                frame.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(createGuiRunnable);
    }

    //	public void accept(Identifier id, Object event) {
    //		// TODO Auto-generated method stub
    //		if (event instanceof ConfigurationEvent) {
    //             ConfigurationEvent configEvent = (ConfigurationEvent) event;
    //             setConfiguration((ServerConfiguration)configEvent.getConfiguration());
    //        } 
    //		else if(event instanceof ServerGameStateEvent){
    //			ServerGameStateEvent serverGameStateEvent = (ServerGameStateEvent)event;
    //			if (!stopExperiment) {
    //               if (serverGameState == null) {
    //                    System.err.println("about to display game..");
    //                    experimentRunning = true;
    //                    // FIXME: could use configuration from this event... serverGameStateEvent.getServerGameState().getConfiguration();
    //                    serverGameState = serverGameStateEvent.getServerGameState();
    //                    /**
    //                     * Here I need to display Game..this goes to the facilitator window.
    //                     */
    //                    //facilitatorWindow.displayGame();
    //               } 
    //                else { 
    //                    // synchronous updates
    //                    serverGameState = serverGameStateEvent.getServerGameState();
    //                }
    //            }
    //			//facilitatorWindow.updateWindow(serverGameStateEvent.getTimeLeft());
    //		}
    //		else if (event instanceof FacilitatorEndRoundEvent){
    //			FacilitatorEndRoundEvent endRoundEvent = (FacilitatorEndRoundEvent)event;
    //			serverGameState = null;
    //			/**
    //			 * This method goes to facilitator widow.
    //			 */
    //			//facilitatorWindow.endRound(endRoundEvent);
    //		}
    //		
    //	}

    /*
     * Send a request to server to start an experiment 
     */
    void sendBeginExperimentRequest(){
        System.out.println("I am in sendBeginExperiment");
        transmit(new BeginExperimentRequest(id));
        //sendBeginRoundRequest();
        stopExperiment = false;
    }

    /*
     * Send a request to start a round
     */
    public void sendBeginRoundRequest()	{
        transmit(new BeginRoundRequest(id));
    }

    //    /*
    //     * Send a request to stop an experiment
    //     */
    //    
    //    public void sendStopExperimentRequest() {
    //        transmit(new EndExperimentRequest(id));
    //        endExperiment();
    //    }

    //    public void endExperiment() {
    //        configuration.resetExperimentRoundConfiguration();
    //        serverGameState = null;
    //        stopExperiment = true;
    //        experimentRunning = false;
    //        //facilitatorWindow.updateMenuItems();
    //    }
    /*
     * Send a request to stop a round
     */

    public void sendEndRoundRequest() {
        transmit(new EndRoundRequest(id));
    }
    /*
     * Send a request to set the configuration object
     */
    public void sendSetConfigRequest() {
        dispatcher.transmit(new ConfigurationEvent(id, getConfiguration()));
    }

    public FacilitatorWindow getFacilitatorWindow() {
        return irrigationFacilitatorWindow;
    }

    public Identifier getIdentifier(){
        return id;
    }

    public ServerDataModel getServerGameState(){
        return serverGameState;
    }

    public ServerConfiguration getConfiguration(){
        return configuration;
    }

    public boolean isExperimentRunning(){
        return experimentRunning;
    }

    public RoundConfiguration getCurrentRoundConfiguration() {
        return configuration.getCurrentParameters();
    }

    public void setServerGameState(ServerDataModel serverGameState) {
        this.serverGameState = serverGameState;
    }
}
