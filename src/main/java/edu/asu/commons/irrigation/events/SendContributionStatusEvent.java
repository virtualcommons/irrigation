package edu.asu.commons.irrigation.events;

import java.util.Map;

import edu.asu.commons.event.AbstractEvent;
import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class SendContributionStatusEvent extends AbstractEvent {

    private static final long serialVersionUID = -8522536860601018690L;

    private final GroupDataModel groupDataModel;


	public SendContributionStatusEvent(Identifier id, GroupDataModel groupDataModel) {
	    super(id);
        this.groupDataModel = groupDataModel;
	}
    
    public GroupDataModel getGroupDataModel() {
        return groupDataModel;
    }
	
	public Map<Identifier,ClientData> getClientDataMap(){
		//for(ClientData clientData : clientDataMap.values())
			//System.out.println("Getting the percent download from send file progress :"+clientData.getPercentFileDownload()+"Fileno"+clientData.getFileNumber());
		return groupDataModel.getClientDataMap();
	}
	


}
