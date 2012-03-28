package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.ExperimentUpdateEvent;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.model.ClientData;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 * 
 */
public class RegistrationEvent extends AbstractEvent implements ExperimentUpdateEvent {

    private static final long serialVersionUID = 3065215773124456718L;

    private RoundConfiguration roundConfiguration;
    private final ClientData clientData;

    /**
     * Constructor used by the facilitator, which doesn't need any ClientData objects.
     * 
     * @param id
     * @param roundConfiguration
     */
    public RegistrationEvent(Identifier id, RoundConfiguration roundConfiguration) {
        this(id, roundConfiguration, null);
    }

    public RegistrationEvent(Identifier id, RoundConfiguration roundConfiguration, ClientData clientData) {
        super(id, roundConfiguration.getInstructions());
        this.clientData = clientData;
        this.roundConfiguration = roundConfiguration;
    }

    public RegistrationEvent(ClientData clientData, RoundConfiguration roundConfiguration) {
        this(clientData.getId(), roundConfiguration, clientData);
    }

    public RoundConfiguration getRoundConfiguration() {
        return roundConfiguration;
    }

    public ClientData getClientData() {
        return clientData;
    }

}
