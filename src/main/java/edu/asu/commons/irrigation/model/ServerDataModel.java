package edu.asu.commons.irrigation.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import edu.asu.commons.event.EventChannel;
import edu.asu.commons.experiment.DataModel;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class ServerDataModel implements DataModel<ServerConfiguration, RoundConfiguration> {

    private static final long serialVersionUID = -2633842942700901843L;

    private transient final Logger logger = Logger.getLogger(getClass().getName());

    // maps client Identifiers to the Group that client belongs to.
    private final Map<Identifier, GroupDataModel> clientsToGroups = new HashMap<Identifier, GroupDataModel>();

    private RoundConfiguration roundConfiguration;

    private transient EventChannel eventChannel;

    public RoundConfiguration getRoundConfiguration() {
        return roundConfiguration;
    }

    public void setRoundConfiguration(RoundConfiguration roundConfiguration) {
        this.roundConfiguration = roundConfiguration;
        for (GroupDataModel group : clientsToGroups.values()) {
            group.setRoundConfiguration(roundConfiguration);
        }
    }

    public synchronized void addClient(ClientData clientData) {
        // iterate through all existing groups
        for (GroupDataModel group : clientsToGroups.values()) {
            if (!group.isFull()) {
                addClientToGroup(clientData, group);
                return;
            }
        }
        GroupDataModel group = new GroupDataModel(this);
        addClientToGroup(clientData, group);
        return;
    }

    public Set<GroupDataModel> getAllGroupDataModels() {
        return new HashSet<GroupDataModel>(clientsToGroups.values());
    }

    public GroupDataModel getGroupDataModel(Identifier id) {
        return clientsToGroups.get(id);
    }

    private void addClientToGroup(ClientData clientData, GroupDataModel group) {
        clientData.setRoundConfiguration(roundConfiguration);
        group.addClient(clientData);
        clientData.setGroupDataModel(group);
        clientsToGroups.put(clientData.getId(), group);
    }

    public Map<Identifier, ClientData> getClientDataMap(Identifier clientId) {
        GroupDataModel group = clientsToGroups.get(clientId);
        return group.getClientDataMap();
    }

    public Map<Identifier, ClientData> getClientDataMap() {
        Map<Identifier, ClientData> clientDataMap = new HashMap<Identifier, ClientData>();
        for (Map.Entry<Identifier, GroupDataModel> entry : clientsToGroups.entrySet()) {
            Identifier id = entry.getKey();
            GroupDataModel group = entry.getValue();
            clientDataMap.put(id, group.getClientData(id));
        }
        return clientDataMap;
    }

    public void clear() {
        for (Iterator<GroupDataModel> iter = clientsToGroups.values().iterator(); iter.hasNext();) {
            GroupDataModel group = iter.next();
            group.clear();
            iter.remove();
        }
    }

    public void removeClient(Identifier id) {
        GroupDataModel groupDataModel = clientsToGroups.remove(id);
        if (groupDataModel == null) {
            logger.warning("No group data model associated with id: " + id);
            return;
        }
        groupDataModel.removeClient(id);
    }

    public EventChannel getEventChannel() {
        return eventChannel;
    }

    public void setEventChannel(EventChannel eventChannel) {
        this.eventChannel = eventChannel;
    }

    @Override
    public List<Identifier> getAllClientIdentifiers() {
        return new ArrayList<Identifier>(clientsToGroups.keySet());
    }

    @Override
    public ServerConfiguration getExperimentConfiguration() {
        return roundConfiguration.getParentConfiguration();
    }

    public String generateFacilitatorDebriefing() {
        return roundConfiguration.generateFacilitatorDebriefing(this);
    }
}
