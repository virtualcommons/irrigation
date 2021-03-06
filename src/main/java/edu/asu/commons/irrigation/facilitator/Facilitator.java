package edu.asu.commons.irrigation.facilitator;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.asu.commons.event.BeginExperimentRequest;
import edu.asu.commons.event.BeginRoundRequest;
import edu.asu.commons.event.EndRoundRequest;
import edu.asu.commons.event.EventTypeProcessor;
import edu.asu.commons.event.FacilitatorMessageEvent;
import edu.asu.commons.facilitator.BaseFacilitator;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.irrigation.events.FacilitatorEndRoundEvent;
import edu.asu.commons.irrigation.events.RegistrationEvent;
import edu.asu.commons.irrigation.model.ServerDataModel;
import edu.asu.commons.ui.UserInterfaceUtils;

/**
 * $Id$
 * 
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>, Sanket Joshi
 * @version $Rev$
 */
public class Facilitator extends BaseFacilitator<ServerConfiguration, RoundConfiguration> {

    private ServerDataModel serverDataModel;
    private FacilitatorWindow facilitatorWindow;

    private Facilitator() {
        this(new ServerConfiguration());
    }

    public Facilitator(ServerConfiguration configuration) {
        super(configuration);
    }

    private void initializeEventProcessors() {
        addEventProcessor(new EventTypeProcessor<RegistrationEvent>(RegistrationEvent.class) {
            @Override
            public void handle(RegistrationEvent registrationEvent) {
                facilitatorWindow.addInstructions(registrationEvent.getRoundConfiguration().getInstructions());
            }
        });
        addEventProcessor(new EventTypeProcessor<FacilitatorEndRoundEvent>(FacilitatorEndRoundEvent.class) {
            @Override
            public void handle(FacilitatorEndRoundEvent event) {
                facilitatorWindow.endRound(event);
                getServerConfiguration().nextRound();
            }
        });
        addEventProcessor(new EventTypeProcessor<FacilitatorMessageEvent>(FacilitatorMessageEvent.class) {
            @Override
            public void handle(FacilitatorMessageEvent event) {
                facilitatorWindow.addMessage(event.toString());
            }
        });
    }

    void initialize() {
        facilitatorWindow = new FacilitatorWindow(this);
        facilitatorWindow.setInstructions(getServerConfiguration().getFacilitatorInstructions());
        initializeEventProcessors();
    }

    public static void main(String[] args) {
        Runnable createGuiRunnable = new Runnable() {
            public void run() {
                Facilitator facilitator = new Facilitator();
                facilitator.initialize();
                facilitator.connect();
                JFrame frame = new JFrame();
                frame.setTitle("Facilitator window: " + facilitator.getId());
                frame.add(facilitator.getFacilitatorWindow());
                UserInterfaceUtils.maximize(frame);
            }
        };
        SwingUtilities.invokeLater(createGuiRunnable);
    }

    void sendStartRoundOverride() {
        transmit(new BeginExperimentRequest(getId()));
    }

    /*
     * Send a request to start a round
     */
    public void sendBeginRoundRequest() {
        transmit(new BeginRoundRequest(getId()));
    }

    public void sendEndRoundRequest() {
        transmit(new EndRoundRequest(getId()));
    }

    public FacilitatorWindow getFacilitatorWindow() {
        return facilitatorWindow;
    }

    public ServerDataModel getServerDataModel() {
        return serverDataModel;
    }

    public RoundConfiguration getCurrentRoundConfiguration() {
        return getServerConfiguration().getCurrentParameters();
    }

    public void setServerDataModel(ServerDataModel serverGameState) {
        this.serverDataModel = serverGameState;
    }
}
