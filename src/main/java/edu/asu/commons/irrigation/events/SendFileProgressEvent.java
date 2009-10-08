package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.ExperimentUpdateEvent;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
/**
 * This events sends the updated file progress event for each client
 */
public class SendFileProgressEvent extends AbstractEvent implements ExperimentUpdateEvent {
    
    private static final long serialVersionUID = -22173326076395287L;
    
    private GroupDataModel groupDataModel;

	private long timeRemaining;
	
	public SendFileProgressEvent(Identifier id, GroupDataModel groupDataModel, long time) {
	    super(id);
        this.groupDataModel = groupDataModel;
		this.timeRemaining = time;
	}

    public GroupDataModel getGroupDataModel() {
        return groupDataModel;
    }



    public long getTimeRemaining() {
        return timeRemaining;
    }

}
