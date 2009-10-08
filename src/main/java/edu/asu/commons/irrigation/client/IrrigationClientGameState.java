/**
 *
 */
package edu.asu.commons.irrigation.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.asu.commons.event.EventChannel;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.events.RoundStartedEvent;
import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class IrrigationClientGameState {

    private static final long serialVersionUID = -3424256672940188027L;

    private GroupDataModel groupDataModel;
    
    // FIXME: can obtain tokensConsumed from the clientDataMap now.

    public IrrigationClient client;

    private RoundConfiguration configuration;

    private int priority = 0;

    private long timeRemaining = 0;

    public IrrigationClientGameState(EventChannel channel, IrrigationClient client) {
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

    /**
     * Invoked on the client side only.
     */
    public synchronized void initialize(RoundStartedEvent event) {
        groupDataModel.clear();
        setTimeRemaining(event.getConfiguration().getRoundDuration().getDelta());
        setConfiguration(event.getConfiguration());
        groupDataModel = event.getGroupDataModel();
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

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public double getTimeRemaining() {
        return timeRemaining;
    }



    public RoundConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(RoundConfiguration configuration) {
        this.configuration = configuration;
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

}
