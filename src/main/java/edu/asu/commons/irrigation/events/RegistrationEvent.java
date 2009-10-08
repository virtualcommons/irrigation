package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.ExperimentUpdateEvent;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class RegistrationEvent extends AbstractEvent implements ExperimentUpdateEvent {

    private static final long serialVersionUID = 3065215773124456718L;
    
    private RoundConfiguration roundConfiguration;
    private final ClientData clientData;

    public RegistrationEvent(Identifier target, RoundConfiguration configuration) {
        this(target, configuration, null);
    }
    
    public RegistrationEvent(Identifier target, RoundConfiguration roundConfiguration, ClientData clientData) {
        super(target, roundConfiguration.getInstructions());
        this.roundConfiguration = roundConfiguration;
        this.clientData = clientData;
    }
 
    public RoundConfiguration getRoundConfiguration() {
        return roundConfiguration;
    }

    public ClientData getClientData() {
        return clientData;
    }

}
