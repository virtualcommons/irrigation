package edu.asu.commons.irrigation.events;

import java.util.Map;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.event.ExperimentUpdateEvent;
import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 * carries all the information till the end of the round. 
 *
 */
public class EndRoundEvent extends AbstractEvent implements ExperimentUpdateEvent  {
	
	private static final long serialVersionUID = 6165820512151646176L;

	private final boolean lastRound;
	
	private final GroupDataModel groupDataModel;
	
	public EndRoundEvent(Identifier id, GroupDataModel groupDataModel, boolean lastRound){
		super(id);
		this.lastRound = lastRound;
        this.groupDataModel = groupDataModel;
	}
	/**
	 * returns all the clients in this group
	 * @return
	 */
	
	public Map<Identifier, ClientData> getClientDataMap(){
		return groupDataModel.getClientDataMap();
	}
    
    public ClientData getClientData() {
        return getClientDataMap().get(id);
    }
	
	public boolean isLastRound(){
		return lastRound;
	}

}
