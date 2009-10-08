package edu.asu.commons.irrigation.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.asu.commons.event.EventChannel;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.net.Identifier;

/**
 * @author Sanket
 *
 */
public class ServerDataModel implements Serializable {

    private static final long serialVersionUID = -2633842942700901843L;

    // Maps client Identifiers to the Group that client belongs to.
    private final Map<Identifier, GroupDataModel> clientsToGroups = new HashMap<Identifier, GroupDataModel>();

    private RoundConfiguration currentConfiguration;
    
    private transient EventChannel eventChannel;
    
    public RoundConfiguration getCurrentConfiguration() {
        return currentConfiguration;
    }

    public void setCurrentConfiguration(RoundConfiguration currentConfiguration) {
        this.currentConfiguration = currentConfiguration;
    }

    public void addClient(ClientData clientData) {
        // iterate through all existing groups
        for (GroupDataModel group : getAllGroupDataModels()) {
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
        Set<GroupDataModel> groups = new HashSet<GroupDataModel>();
        groups.addAll(clientsToGroups.values());
        return groups;
    }

    public GroupDataModel getGroupDataModel(Identifier id) {
        GroupDataModel group = clientsToGroups.get(id);
        if (group == null) {
            System.err.println("\n\n\nXXX: clients to groups map: " + clientsToGroups + " this game state: " + this);
            throw new IllegalArgumentException("No group available for id:" + id);
        }
        return group;
    }


    public void addClientToGroup(ClientData clientData, GroupDataModel group) {
        System.err.println("Adding client: " + clientData.getId() + " to group: " + group + " in this game state: " + this);
//      clientData.setPosition(position);
        clientData.setRoundConfiguration(currentConfiguration);
        group.addClient(clientData);
        clientData.setGroupDataModel(group);
        clientsToGroups.put(clientData.getId(), group);
//      System.err.println("\n\n\nXXXX: groups is: " + clientsToGroups + " this game state is: " + this + "\n\n\n");
        //channel.handle(new AddClientEvent(clientData, group, clientData.getPosition()));
    }

    public Map<Identifier, ClientData> getClientDataMap(Identifier clientId) {
        GroupDataModel group = clientsToGroups.get(clientId);
        return group.getClientDataMap();
    }

    public void clear() {
        // XXX: we no longer remove the Groups from the ServerGameState since we want persistent groups.
        // This should be configurable?
        for (Iterator<GroupDataModel> iter = clientsToGroups.values().iterator(); iter.hasNext(); ) {
            GroupDataModel group = iter.next();
            group.clear();
            iter.remove();
        }
    }
    
    public void removeClient(Identifier id) {
    	clientsToGroups.get(id).removeClient(id);
        clientsToGroups.remove(id);
    }

    public EventChannel getEventChannel() {
        return eventChannel;
    }

    public void setEventChannel(EventChannel eventChannel) {
        this.eventChannel = eventChannel;
    }
}
