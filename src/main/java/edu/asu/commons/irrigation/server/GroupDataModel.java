package edu.asu.commons.irrigation.server;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import edu.asu.commons.event.EventChannel;
import edu.asu.commons.experiment.DataModel;
import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * Data model for a single Group of clients.  Each Group manages its own unique resource shared among
 * its own participants.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class GroupDataModel implements DataModel<RoundConfiguration> {

    private static final long serialVersionUID = 5817418171228817123L;

    private final Map<Identifier, ClientData> clients = new LinkedHashMap<Identifier, ClientData>();

    private transient ServerDataModel serverDataModel;
    
    private transient Logger logger = Logger.getLogger(GroupDataModel.class.getName());

    private int currentlyAvailableFlowCapacity = 0;
    private int maximumAvailableFlowCapacity = 0;
    
    private int infrastructureEfficiency;
    
    private int initialInfrastructureEfficiency;
    
    private int totalContributedTokens = 0;
    
    public GroupDataModel(ServerDataModel serverDataModel) {
        this.serverDataModel = serverDataModel;
    }

    public ClientData getClientData(Identifier id) {
        return clients.get(id);
    }

    public Set<Identifier> getClientIdentifiers() {
        return Collections.unmodifiableSet(clients.keySet());
    }
    /** Here instead of tokens there would be get in general tokens, or the profit earned
     * but at present it would be just currentTokens
     *  
     * @param id
     * @return
     */
    public int getAward(Identifier id) {
        ClientData state = (ClientData) clients.get(id);
        if (state == null) {
            // FIXME: perhaps we should just return 0 instead.
            throw new IllegalArgumentException(
                    "no client state available for: " + id);
        }
        return state.getTotalTokensEarned();
    }

    public void addClient(ClientData clientData) {
        clients.put(clientData.getId(), clientData);
        clientData.setAssignedNumber(clients.size());
    }

    public void removeClient(Identifier id) {
        clients.remove(id);
    }

    public boolean isFull() {
        return clients.size() == serverDataModel.getRoundConfiguration().getClientsPerGroup();
    }

    public void clear() {
        clients.clear();
    }

    public void setServerDataModel(ServerDataModel state) {
        this.serverDataModel = state;
    }

    public Map<Identifier, ClientData> getClientDataMap() {
        return Collections.unmodifiableMap(clients);
    }

    public int getCurrentlyAvailableFlowCapacity(){
        return currentlyAvailableFlowCapacity;
    }

    public RoundConfiguration getRoundConfiguration() {
        return serverDataModel.getRoundConfiguration();
    }

    //get totalContributed tokens for this round
    public int getTotalContributedTokens() {
        return totalContributedTokens;
    }
    
    public void calculateTotalFlowCapacity() {
        // for practice round and first round, initialize to initial infrastructure efficiency
    	//setting the total contributed Bandwidth = 0 , so thatfor every round,
    	// fresh totalContributed tokens are calculated
    	totalContributedTokens = 0;
        for(ClientData clientData : getClientDataMap().values()) {
            totalContributedTokens += clientData.getContributedTokens();
        }
        getLogger().info("total contributed tokens: " + totalContributedTokens);
    	updateInfrastructureEfficiency(totalContributedTokens);
        
        currentlyAvailableFlowCapacity = maximumAvailableFlowCapacity = getFlowCapacity();
        getLogger().info("maximum available flow capacity = "+ maximumAvailableFlowCapacity);
    }
    
    private void updateInfrastructureEfficiency(int totalContributedTokens) {
    	RoundConfiguration roundConfiguration = getRoundConfiguration();
        int currentRoundNumber = roundConfiguration.getRoundNumber();
        // initialize infrastructure efficiency
        System.err.println("current round number: " + currentRoundNumber);
    	System.err.println("initial infrastructure efficiency: " + infrastructureEfficiency);
        if (roundConfiguration.isPracticeRound() || roundConfiguration.isFirstRound()) {
            System.err.println("initializing infrastructure efficiency to default initial value: " + roundConfiguration.getInitialInfrastructureEfficiency());
            infrastructureEfficiency = roundConfiguration.getInitialInfrastructureEfficiency();
        }
        else {
            // degrade by infrastructure-degradation-factor, clamp at 0
            getLogger().info("degrading infrastructure efficiency: " + infrastructureEfficiency);
            infrastructureEfficiency = Math.max(infrastructureEfficiency - roundConfiguration.getInfrastructureDegradationFactor(), 0);
            getLogger().info("New infrastructure efficiency: " + infrastructureEfficiency);
        }
        // set original infrastructure efficiency before token contributions
        initialInfrastructureEfficiency = infrastructureEfficiency;
        // add total invested tokens to infrastructure efficiency, clamp at
        // 100
        infrastructureEfficiency = Math.min(100, totalContributedTokens + infrastructureEfficiency);
        System.err.println("total infrastructure efficiency: " + infrastructureEfficiency);
    }
    
    /**
     * This function maps flow capacity to infrastructure efficiency.  Returns an int representing
     * the flow capacity given the infrastructure efficiency.
     * this needs to be designed.
     * @param totalTokens
     * @return
     */
    public int calculateFlowCapacity(final int infrastructureEfficiency) {
    	if (infrastructureEfficiency <= 45) {
    		return 0;
    	}
    	else if (infrastructureEfficiency <= 51) {
    		return 5;
    	}
    	else if (infrastructureEfficiency <= 55) {
    		return 10;
    	}
    	else if (infrastructureEfficiency <= 58) {
    		return 15;
    	}
    	else if (infrastructureEfficiency <= 61) {
    		return 20;
    	}
    	else if (infrastructureEfficiency <= 65) {
    		return 25;
    	}
    	else if (infrastructureEfficiency <= 70) {
    		return 30;
    	}
    	else if (infrastructureEfficiency <= 80) {
    		return 35;
    	}
    	else if (infrastructureEfficiency <= 100) {
    		return 40;
    	}
        return 40;
    }
    
    public int getInitialFlowCapacity() {
    	return calculateFlowCapacity(initialInfrastructureEfficiency);
    }
    
    public int getFlowCapacity() {
    	return calculateFlowCapacity(infrastructureEfficiency);
    }
    
    public void resetCurrentlyAvailableFlowCapacity() {
        currentlyAvailableFlowCapacity = maximumAvailableFlowCapacity;
    }

    public void allocateFlowCapacity(ClientData clientData) {
        if (currentlyAvailableFlowCapacity >= clientData.getMaximumIndividualFlowCapacity()) {
            currentlyAvailableFlowCapacity -= clientData.getMaximumIndividualFlowCapacity();
//            setAvailableBandwidth(clientData.getTotalContributedBandwidth() - clientData.getDeliveryBandwidth());
            clientData.setAvailableFlowCapacity(clientData.getMaximumIndividualFlowCapacity());
        } 
        else {
            clientData.setAvailableFlowCapacity(currentlyAvailableFlowCapacity);
            currentlyAvailableFlowCapacity = 0;
        }
        clientData.allocateFlowCapacity(currentlyAvailableFlowCapacity);        
    }


    public int getMaximumAvailableFlowCapacity() {
        return maximumAvailableFlowCapacity;
    }
    
    public EventChannel getEventChannel() {
        return serverDataModel.getEventChannel();
    }

	public int getInfrastructureEfficiency() {
		return infrastructureEfficiency;
	}

	public int getInitialInfrastructureEfficiency() {
		return initialInfrastructureEfficiency;
	}
	
	public Logger getLogger() {
	    if (logger == null) {
	        logger = Logger.getLogger(getClass().getName());
	    }
	    return logger;
	}

}

