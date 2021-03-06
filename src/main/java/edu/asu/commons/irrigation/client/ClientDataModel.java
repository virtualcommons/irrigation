package edu.asu.commons.irrigation.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.asu.commons.event.EventChannel;
import edu.asu.commons.experiment.DataModel;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.irrigation.events.ClientUpdateEvent;
import edu.asu.commons.irrigation.events.RoundStartedEvent;
import edu.asu.commons.irrigation.model.ClientData;
import edu.asu.commons.irrigation.model.GroupDataModel;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * The client side data model, simply wraps a GroupDataModel.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class ClientDataModel implements DataModel<ServerConfiguration, RoundConfiguration> {

    private static final long serialVersionUID = -3424256672940188027L;

    private GroupDataModel groupDataModel;

    private IrrigationClient client;

    private ServerConfiguration serverConfiguration;
    private RoundConfiguration roundConfiguration;
    private final EventChannel eventChannel;

    private int timeLeft = 0;

    public ClientDataModel(EventChannel channel, IrrigationClient client) {
        this.eventChannel = channel;
        this.client = client;
        this.serverConfiguration = client.getServerConfiguration();
    }

    public ClientData getClientData() {
        return groupDataModel.getClientData(getId());
    }

    public String getPriorityString() {
        return getClientData().getPriorityString();
    }

    public Identifier getId() {
        return client.getId();
    }

    public List<Identifier> getAllClientIdentifiers() {
        return new ArrayList<Identifier>(groupDataModel.getAllClientIdentifiers());
    }

    public synchronized void initialize(RoundStartedEvent event) {
        groupDataModel.clear();
        setGroupDataModel(event.getGroupDataModel());
        setTimeLeft(getRoundConfiguration().getRoundDurationInSeconds());
    }

    public int getPriority() {
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
        Map<Identifier, ClientData> clientDataMap = groupDataModel.getClientDataMap();
        // used by StringTemplate to determine whether or not the ClientData it's rendering values for
        // is this client.
        getClientData().setSelf(true);
        return clientDataMap;
    }

    public List<ClientData> getClientDataSortedByPriority() {
        Map<Identifier, ClientData> clientDataMap = getClientDataMap();
        ArrayList<ClientData> clientDataList = new ArrayList<ClientData>(clientDataMap.values());
        Collections.sort(clientDataList);
        return clientDataList;
    }

    public int getWaterSupplyCapacity() {
        return roundConfiguration.getWaterSupplyCapacity();
    }

    public int getIrrigationCapacity() {
        return groupDataModel.getIrrigationCapacity();
    }

    public EventChannel getEventChannel() {
        return eventChannel;
    }

    public boolean isImmediateNeighbor(ClientData otherClientData) {
        return getClientData().isImmediateNeighbor(otherClientData);
    }

    @Override
    public ServerConfiguration getExperimentConfiguration() {
        return serverConfiguration;
    }

    public List<ClientData> getOrderedVisibleClients() {
        if (getRoundConfiguration().isRestrictedVisibility()) {
            // FIXME: replace hard-coded immediate neighbor check with field of vision radius from RoundConfiguration
            ArrayList<ClientData> neighbors = new ArrayList<ClientData>();
            List<ClientData> sortedClients = getClientDataSortedByPriority();
            ClientData thisClientData = getClientData();
            int thisClientIndex = sortedClients.indexOf(thisClientData);
            if (thisClientIndex > 0) {
                // upstream neighbor
                neighbors.add(sortedClients.get(thisClientIndex - 1));
            }
            // this is needed for the charts, but probably weird for general-purpose usage
            neighbors.add(thisClientData);
            if (thisClientIndex < sortedClients.size() - 1) {
                // downstream neighbor
                neighbors.add(sortedClients.get(thisClientIndex + 1));
            }
            return neighbors;
        }
        else {
            return getClientDataSortedByPriority();
        }
    }

}
