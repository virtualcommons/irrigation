/**
 *
 */
package edu.asu.commons.irrigation.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.asu.commons.event.EventChannel;
import edu.asu.commons.experiment.DataModel;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
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
    
    private RoundConfiguration roundConfiguration;

    private int priority = 0;

    private int timeLeft = 0;

    public ClientDataModel(EventChannel channel, IrrigationClient client) {
        this.client = client;
    }

    public ClientData getClientData() {
        return groupDataModel.getClientData( getId() );
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
        setTimeLeft( (int) (getRoundConfiguration().getRoundDuration().getDelta() / 1000L) );
    }

    /**
     * Setting priority for this client.
     */
    public void setPriority(int priority){
        this.priority  = priority;
    }

    public int getPriority(){
        return priority;
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
    
    public void setGroupDataModel(GroupDataModel groupDataModel) {
        this.groupDataModel = groupDataModel;
        setRoundConfiguration(groupDataModel.getRoundConfiguration());
    }

    public GroupDataModel getGroupDataModel() {
        return groupDataModel;
    }
    
    public Map<Identifier, ClientData> getClientDataMap() {
        return groupDataModel.getClientDataMap();
    }


}
