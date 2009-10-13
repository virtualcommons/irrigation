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
import edu.asu.commons.event.EventChannel;
import edu.asu.commons.event.EventChannelFactory;
import edu.asu.commons.event.EventTypeProcessor;
import edu.asu.commons.event.FacilitatorRegistrationRequest;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.irrigation.events.FacilitatorEndRoundEvent;
import edu.asu.commons.irrigation.server.ServerDataModel;
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
public class Facilitator {

    private final static Facilitator INSTANCE = new Facilitator();

    private Identifier id;  //  @jve:decl-index=0:

    private ClientDispatcher dispatcher;

    private ServerConfiguration configuration;

    private ServerDataModel serverGameState;  //  @jve:decl-index=0:

    private FacilitatorWindow irrigationFacilitatorWindow;

    private boolean experimentRunning = false;

    private final EventChannel channel = EventChannelFactory.create();

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
        channel.add(this, new EventTypeProcessor<ConfigurationEvent>(ConfigurationEvent.class) {
            public void handle(ConfigurationEvent configurationEvent) {
                setConfiguration((ServerConfiguration) configurationEvent.getConfiguration());
            }
        });
        channel.add(this, new EventTypeProcessor<FacilitatorEndRoundEvent>(FacilitatorEndRoundEvent.class) {
            public void handle(FacilitatorEndRoundEvent event) {
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
        transmit(new FacilitatorRegistrationRequest(id));
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
        Runnable createGuiRunnable = new Runnable() {
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

    /*
     * Send a request to server to start an experiment 
     */
    void sendBeginExperimentRequest(){
        transmit(new BeginExperimentRequest(id));
    }

    /*
     * Send a request to start a round
     */
    public void sendBeginRoundRequest()	{
        transmit(new BeginRoundRequest(id));
    }

    public void sendEndRoundRequest() {
        transmit(new EndRoundRequest(id));
    }

    public FacilitatorWindow getFacilitatorWindow() {
        return irrigationFacilitatorWindow;
    }

    public Identifier getId(){
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
