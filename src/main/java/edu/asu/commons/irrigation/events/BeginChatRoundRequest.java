package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.FacilitatorRequest;
import edu.asu.commons.irrigation.model.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class BeginChatRoundRequest extends AbstractEvent implements FacilitatorRequest {
	
	private static final long serialVersionUID = -5770187653413351080L;
	
	private final GroupDataModel groupDataModel;

    public BeginChatRoundRequest(Identifier id){
		this(id, null);
	}
	
	public BeginChatRoundRequest(Identifier id, GroupDataModel groupDataModel) {
	    super(id);
	    this.groupDataModel = groupDataModel;
	}
	
	public GroupDataModel getGroupDataModel() {
	    return groupDataModel;
	}

}
