package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.ExperimentUpdateEvent;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class ClientUpdateEvent extends AbstractEvent
implements ExperimentUpdateEvent {

    private static final long serialVersionUID = -128693557750400520L;
    
    private final GroupDataModel groupDataModel;
    private final long timeLeft;

    public ClientUpdateEvent(Identifier clientId, GroupDataModel groupDataModel, long timeLeft) {
        this.groupDataModel = groupDataModel;
        this.timeLeft = timeLeft;
    }

    public GroupDataModel getGroupDataModel() {
        return groupDataModel;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

}
