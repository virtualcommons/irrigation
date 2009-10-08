package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class RoundStartedEvent extends AbstractEvent {

    private static final long serialVersionUID = -1955741094119749317L;
    
    private final GroupDataModel groupDataModel;
    private final RoundConfiguration roundConfiguration;

    public RoundStartedEvent(Identifier id, GroupDataModel groupDataModel) { 
        super(id);
        this.groupDataModel = groupDataModel;
        this.roundConfiguration = groupDataModel.getRoundConfiguration();
    }
    
    public GroupDataModel getGroupDataModel() {
        return groupDataModel;
    }
    
    public RoundConfiguration getConfiguration() {
        return roundConfiguration;
    }

}
