package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.FacilitatorRequest;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class BeginCommunicationRequest extends AbstractEvent implements FacilitatorRequest {
	
	private static final long serialVersionUID = -5770187653413351080L;
	
	private final GroupDataModel groupDataModel;

    public BeginCommunicationRequest(Identifier id){
		this(id, null);
	}
	
	public BeginCommunicationRequest(Identifier id, GroupDataModel groupDataModel) {
	    super(id);
	    this.groupDataModel = groupDataModel;
	}
	
	public GroupDataModel getGroupDataModel() {
	    return groupDataModel;
	}

}
