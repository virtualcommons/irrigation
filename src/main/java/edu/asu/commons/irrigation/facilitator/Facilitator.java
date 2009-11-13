package edu.asu.commons.irrigation.facilitator;

import java.awt.Dimension;
import java.net.InetSocketAddress;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.asu.commons.event.BeginExperimentRequest;
import edu.asu.commons.event.BeginRoundRequest;
import edu.asu.commons.event.EndRoundRequest;
import edu.asu.commons.event.Event;
import edu.asu.commons.event.EventChannel;
import edu.asu.commons.event.EventChannelFactory;
import edu.asu.commons.event.EventTypeProcessor;
import edu.asu.commons.event.FacilitatorRegistrationRequest;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.irrigation.events.FacilitatorEndRoundEvent;
import edu.asu.commons.irrigation.events.RegistrationEvent;
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

    private Identifier id; 

    private ClientDispatcher dispatcher;

    private ServerConfiguration configuration;

    private ServerDataModel serverDataModel;  //  @jve:decl-index=0:

    private FacilitatorWindow facilitatorWindow;

    private final EventChannel channel = EventChannelFactory.create();

    private Facilitator() {
        this(new ServerConfiguration());
    }

    public Facilitator(ServerConfiguration configuration) {
        dispatcher = DispatcherFactory.getInstance().createClientDispatcher(channel);
        setConfiguration(configuration);
    }
    
    private void initializeEventProcessors() {
        channel.add(this, new EventTypeProcessor<RegistrationEvent>(RegistrationEvent.class) {
            public void handle(RegistrationEvent registrationEvent) {
                facilitatorWindow.addInstructions(registrationEvent.getRoundConfiguration().getInstructions());
            }
        });
        channel.add(this, new EventTypeProcessor<FacilitatorEndRoundEvent>(FacilitatorEndRoundEvent.class) {
            public void handle(FacilitatorEndRoundEvent event) {
                facilitatorWindow.endRound(event);
                configuration.nextRound();
            }
        });
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

    void initialize() {
        facilitatorWindow = new FacilitatorWindow(this);
        facilitatorWindow.setText(configuration.getFacilitatorInstructions());
        initializeEventProcessors();
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
                Dimension dimension = new Dimension(800, 600);
                Facilitator facilitator = new Facilitator();
                facilitator.initialize();
                facilitator.connect();
                JFrame frame = new JFrame();
                frame.setTitle("Facilitator window: " + facilitator.id);
                frame.setPreferredSize(dimension);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(facilitator.getFacilitatorWindow());
                frame.pack();
                frame.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(createGuiRunnable);
    }

    void sendStartRoundOverride(){
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
        return facilitatorWindow;
    }

    public Identifier getId(){
        return id;
    }

    public ServerDataModel getServerDataModel(){
        return serverDataModel;
    }

    public ServerConfiguration getConfiguration(){
        return configuration;
    }

    public RoundConfiguration getCurrentRoundConfiguration() {
        return configuration.getCurrentParameters();
    }

    public void setServerDataModel(ServerDataModel serverGameState) {
        this.serverDataModel = serverGameState;
    }
}
