package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.irrigation.model.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class RoundStartedEvent extends AbstractEvent {

    private static final long serialVersionUID = -1955741094119749317L;
    
    private final GroupDataModel groupDataModel;

    public RoundStartedEvent(Identifier id, GroupDataModel groupDataModel) { 
        super(id);
        this.groupDataModel = groupDataModel;
    }
    
    public GroupDataModel getGroupDataModel() {
        return groupDataModel;
    }

}
