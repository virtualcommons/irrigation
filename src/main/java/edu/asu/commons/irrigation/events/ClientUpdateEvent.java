package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.ExperimentUpdateEvent;
import edu.asu.commons.irrigation.model.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * Updates a specific client (identified by getId()) with an up-to-date GroupDataModel and the 
 * number of seconds left in the round.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class ClientUpdateEvent extends AbstractEvent
implements ExperimentUpdateEvent {

    private static final long serialVersionUID = -128693557750400520L;
    
    private final GroupDataModel groupDataModel;
    private final int timeLeft;

    public ClientUpdateEvent(Identifier id, GroupDataModel groupDataModel, int timeLeft) {
        super(id);
        this.groupDataModel = groupDataModel;
        this.timeLeft = timeLeft;
    }

    public GroupDataModel getGroupDataModel() {
        return groupDataModel;
    }

    /**
     * Returns the time left in the round, in seconds.
     * 
     * @return the number of seconds left in the round.
     */
    public int getTimeLeft() {
        return timeLeft;
    }

}
