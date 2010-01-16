package edu.asu.commons.irrigation.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import edu.asu.commons.event.EventChannel;
import edu.asu.commons.experiment.DataModel;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.irrigation.events.ClientUpdateEvent;
import edu.asu.commons.irrigation.events.RoundStartedEvent;
import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * The client side data model, simply wraps a GroupDataModel.
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class ClientDataModel implements DataModel<RoundConfiguration> { 

    private static final long serialVersionUID = -3424256672940188027L;

    private GroupDataModel groupDataModel;
    
    private IrrigationClient client;
   
    private ServerConfiguration serverConfiguration;
    private RoundConfiguration roundConfiguration;

    private int timeLeft = 0;

    public ClientDataModel(EventChannel channel, IrrigationClient client) {
        this.client = client;
        this.serverConfiguration = client.getServerConfiguration();
    }

    public ClientData getClientData() {
        return groupDataModel.getClientData( getId() );
    }
    
    public String getPriorityString() {
    	return getClientData().getPriorityString();
    }
    

    public Identifier getId() {
        return client.getId();
    }
    
    public List<Identifier> getAllClientIdentifiers() {
        return new ArrayList<Identifier>(groupDataModel.getClientIdentifiers());
    }

     public synchronized void initialize(RoundStartedEvent event) {
        groupDataModel.clear();
        setGroupDataModel(event.getGroupDataModel());
        setTimeLeft( getRoundConfiguration().getRoundDurationInSeconds() );
    }

    public int getPriority(){
        return getClientData().getPriority();
    }
    
    public void update(ClientUpdateEvent clientUpdateEvent) {
        setGroupDataModel(clientUpdateEvent.getGroupDataModel());
        setTimeLeft(clientUpdateEvent.getTimeLeft());
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public RoundConfiguration getRoundConfiguration() {
        return roundConfiguration;
    }

    public void setRoundConfiguration(RoundConfiguration roundConfiguration) {
        this.roundConfiguration = roundConfiguration;
    }
    
    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }
    
    public void setGroupDataModel(GroupDataModel groupDataModel) {
        this.groupDataModel = groupDataModel;
    }

    public GroupDataModel getGroupDataModel() {
        return groupDataModel;
    }
    
    public Map<Identifier, ClientData> getClientDataMap() {
        return groupDataModel.getClientDataMap();
    }
    
    public List<ClientData> getClientDataSortedByPriority() {
		ArrayList<ClientData> clientDataList = new ArrayList<ClientData>(getGroupDataModel().getClientDataMap().values());
		// sort by position.
		Collections.sort(clientDataList, new Comparator<ClientData>() {
			public int compare(ClientData a, ClientData b) {
				return new Integer(a.getPriority()).compareTo(b.getPriority());
			}
		});
		return clientDataList;
    }

    public int getWaterSupplyCapacity() {
        return roundConfiguration.getWaterSupplyCapacity();
    }

    public int getIrrigationCapacity() {
        return groupDataModel.getIrrigationCapacity();
    }


}
