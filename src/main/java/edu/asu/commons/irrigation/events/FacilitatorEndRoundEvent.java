package edu.asu.commons.irrigation.events;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.irrigation.server.ServerDataModel;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class FacilitatorEndRoundEvent extends AbstractEvent {

	private static final long serialVersionUID = -7917878646417155623L;
	private final ServerDataModel serverDataModel;
    
	public FacilitatorEndRoundEvent(Identifier id, ServerDataModel serverDataModel) {
		super(id);
		this.serverDataModel = serverDataModel;
	}
	
	public ServerDataModel getServerDataModel() {
        return serverDataModel;
    }

    public boolean isLastRound(){
	    return serverDataModel.getCurrentConfiguration().getParentConfiguration().isLastRound();
	}
    
}
