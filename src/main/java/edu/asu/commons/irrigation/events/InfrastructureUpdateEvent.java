package edu.asu.commons.irrigation.events;

import java.util.Map;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.ExperimentUpdateEvent;
import edu.asu.commons.irrigation.model.ClientData;
import edu.asu.commons.irrigation.model.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 *
 * Event carrying the GroupDataModel and the time remaining to be sent to all participants each second.
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class InfrastructureUpdateEvent extends AbstractEvent implements ExperimentUpdateEvent {

    private static final long serialVersionUID = -8522536860601018690L;

    private final GroupDataModel groupDataModel;
    
	public InfrastructureUpdateEvent(Identifier id, GroupDataModel groupDataModel) {
	    super(id);
        this.groupDataModel = groupDataModel;
	}
	
    public GroupDataModel getGroupDataModel() {
        return groupDataModel;
    }
	
	public Map<Identifier,ClientData> getClientDataMap(){
		return groupDataModel.getClientDataMap();
	}
	
	public ClientData getClientData() {
	    return groupDataModel.getClientData(id);
	}

}
