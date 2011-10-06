package edu.asu.commons.irrigation.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    
    private transient Logger logger = Logger.getLogger(GroupDataModel.class.getName());

    private final Map<Identifier, ClientData> clients = new LinkedHashMap<Identifier, ClientData>();

    private RoundConfiguration roundConfiguration;

    private transient ServerDataModel serverDataModel;


    private int currentlyAvailableFlowCapacity = 0;
    private int maximumAvailableWaterFlow = 0;
    
    private int infrastructureEfficiency;
    // infrastructure efficiency before investment (but post decline)
    private int infrastructureEfficiencyBeforeInvestment;
    
    private int totalContributedTokens = 0;
    
    
    public GroupDataModel(ServerDataModel serverDataModel) {
        this.serverDataModel = serverDataModel;
        setRoundConfiguration(serverDataModel.getRoundConfiguration());
    }

    public ClientData getClientData(Identifier id) {
        return clients.get(id);
    }

    public List<Identifier> getAllClientIdentifiers() {
        return new ArrayList<Identifier>(clients.keySet());
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
    
    public void initializeInfrastructure() {
        // for practice round and first round, initialize to initial infrastructure efficiency
    	//setting the total contributed Bandwidth = 0 , so that for every round,
    	// fresh totalContributed tokens are calculated
    	totalContributedTokens = 0;
        for(ClientData clientData : getClientDataMap().values()) {
            totalContributedTokens += clientData.getInvestedTokens();
        }
    	updateInfrastructureEfficiency(totalContributedTokens);
        currentlyAvailableFlowCapacity = maximumAvailableWaterFlow = getActualWaterDeliveryCapacity();
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
        infrastructureEfficiencyBeforeInvestment = infrastructureEfficiency;
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
    public int calculateIrrigationCapacity(final int infrastructureEfficiency) {
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
    
    public int getIrrigationCapacityBeforeInvestment() {
//    	return Math.min(calculateFlowCapacity(initialInfrastructureEfficiency), getRoundConfiguration().getWaterSupplyCapacity());
        return calculateIrrigationCapacity(infrastructureEfficiencyBeforeInvestment);
    }
    
    /**
     * Returns the theoretical maximum amount of water that the infrastructure can handle.
     * This is independent of the actual water supply. 
     */
    public int getIrrigationCapacity() {
        return calculateIrrigationCapacity(infrastructureEfficiency);
//    	return Math.min(calculateFlowCapacity(infrastructureEfficiency), getRoundConfiguration().getWaterSupplyCapacity());
    }

    /**
     * Returns the actual maximum amount of water that can pass through the canal, which is the minimum
     * of the irrigation capacity and the water supply. 
     * @return
     */
    public int getActualWaterDeliveryCapacity() {
        return Math.min(getIrrigationCapacity(), getRoundConfiguration().getWaterSupplyCapacity());
    }
    
    public void resetCurrentlyAvailableFlowCapacity() {
        currentlyAvailableFlowCapacity = maximumAvailableWaterFlow;
    }

    public void allocateWater(ClientData clientData) {
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

    public int getMaximumAvailableWaterFlow() {
        return maximumAvailableWaterFlow;
    }

    public int getInfrastructureEfficiency() {
        return infrastructureEfficiency;
    }

    public int getInfrastructureEfficiencyBeforeInvestment() {
        return infrastructureEfficiencyBeforeInvestment;
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

    @Override
    public EventChannel getEventChannel() {
        return serverDataModel.getEventChannel();
    }

    public ServerDataModel getServerDataModel() {
        return serverDataModel;
    }

    public void setServerDataModel(ServerDataModel serverDataModel) {
        this.serverDataModel = serverDataModel;
    }

}

