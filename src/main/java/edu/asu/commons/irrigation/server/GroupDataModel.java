package edu.asu.commons.irrigation.server;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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

    private RoundConfiguration roundConfiguration;

	private transient Logger logger = Logger.getLogger(GroupDataModel.class.getName());

    private int currentlyAvailableFlowCapacity = 0;
    private int maximumAvailableFlowCapacity = 0;
    
    private int infrastructureEfficiency;
    // infrastructure efficiency before investment (but post decline)
    private int initialInfrastructureEfficiency;
    
    private int totalContributedTokens = 0;
    
    public GroupDataModel(ServerDataModel serverDataModel) {
        setRoundConfiguration(serverDataModel.getRoundConfiguration());
    }

    public ClientData getClientData(Identifier id) {
        return clients.get(id);
    }

    public Set<Identifier> getClientIdentifiers() {
        return Collections.unmodifiableSet(clients.keySet());
    }

    public void addClient(ClientData clientData) {
        clients.put(clientData.getId(), clientData);
        clientData.setAssignedNumber(clients.size());
    }
    
    public int size() {
    	return clients.size();
    }

    public void removeClient(Identifier id) {
        clients.remove(id);
    }

    public boolean isFull() {
        return clients.size() == getRoundConfiguration().getClientsPerGroup();
    }

    public void clear() {
        clients.clear();
    }

    public Map<Identifier, ClientData> getClientDataMap() {
        return Collections.unmodifiableMap(clients);
    }

    public int getAvailableClientFlowCapacity() {
        return Math.min(currentlyAvailableFlowCapacity, getRoundConfiguration().getMaximumClientFlowCapacity());
    }

    public RoundConfiguration getRoundConfiguration() {
        return roundConfiguration;
    }

    public int getTotalContributedTokens() {
        return totalContributedTokens;
    }
    
    public void calculateTotalFlowCapacity() {
        // for practice round and first round, initialize to initial infrastructure efficiency
    	//setting the total contributed Bandwidth = 0 , so that for every round,
    	// fresh totalContributed tokens are calculated
    	totalContributedTokens = 0;
        for(ClientData clientData : getClientDataMap().values()) {
            totalContributedTokens += clientData.getInvestedTokens();
        }
    	updateInfrastructureEfficiency(totalContributedTokens);
        currentlyAvailableFlowCapacity = maximumAvailableFlowCapacity = getFlowCapacity();
    }
    
    private void updateInfrastructureEfficiency(int totalContributedTokens) {
    	RoundConfiguration roundConfiguration = getRoundConfiguration();
        // initialize infrastructure efficiency
        if ( roundConfiguration.shouldResetInfrastructureEfficiency() ) {
            infrastructureEfficiency = roundConfiguration.getInitialInfrastructureEfficiency();
        }
        else {
            // degrade by infrastructure-degradation-factor, clamp at 0
            infrastructureEfficiency = Math.max(infrastructureEfficiency - roundConfiguration.getInfrastructureDegradationFactor(), 0);
        }
        // set original infrastructure efficiency before token contributions
        getLogger().info("initial infrastructure efficiency: " + infrastructureEfficiency);
        initialInfrastructureEfficiency = infrastructureEfficiency;
        // add total invested tokens to infrastructure efficiency, clamp at
        // 100
        infrastructureEfficiency = Math.min(100, totalContributedTokens + infrastructureEfficiency);
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
    	return Math.min(calculateFlowCapacity(initialInfrastructureEfficiency), getRoundConfiguration().getMaximumCanalFlowCapacity());
    }
    
    public int getFlowCapacity() {
    	return Math.min(calculateFlowCapacity(infrastructureEfficiency), getRoundConfiguration().getMaximumCanalFlowCapacity());
    }
    
    public void resetCurrentlyAvailableFlowCapacity() {
        currentlyAvailableFlowCapacity = maximumAvailableFlowCapacity;
    }

    public void allocateFlowCapacity(ClientData clientData) {
    	int maximumClientFlowCapacity = getRoundConfiguration().getMaximumClientFlowCapacity();
        if (currentlyAvailableFlowCapacity >= maximumClientFlowCapacity) {
            currentlyAvailableFlowCapacity -= maximumClientFlowCapacity;
            clientData.setAvailableFlowCapacity(maximumClientFlowCapacity);
        } 
        else {
            clientData.setAvailableFlowCapacity(currentlyAvailableFlowCapacity);
            currentlyAvailableFlowCapacity = 0;
        }
        clientData.collectWater();        
    }


    public int getMaximumAvailableFlowCapacity() {
        return maximumAvailableFlowCapacity;
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
	
    
    public void setRoundConfiguration(RoundConfiguration roundConfiguration) {
		this.roundConfiguration = roundConfiguration;
	}

}

