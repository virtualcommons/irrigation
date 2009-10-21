package edu.asu.commons.irrigation.server;

import java.io.Serializable;

import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.net.Identifier;

/**
 * $Id$
 * 
 * Stores client-specific information used by the server. 
 *
 * FIXME: need additional fields:  
 * total tokens collected in a round
 * minus tokens subtracted by others
 * minus tokens subtracted by sanctioning others
 * 
 * @author <a href='mailto:allen.lee@asu.edu'>Allen Lee</a>, Deepali Bhagvat
 * @version $Revision$
 */

public class ClientData implements Serializable {

    private static final long serialVersionUID = 5281922601551921005L;

    private final Identifier id;
    
    private GroupDataModel groupDataModel;

    private int waterUsed = 0;

    private int availableFlowCapacity;

    private int investedTokens;

    private int assignedNumber;

    private RoundConfiguration roundConfiguration;

    private int totalTokens;

    private boolean paused = false;

    private boolean gateOpen = false;

    public ClientData(Identifier id) {
        this.id = id;
    }

    public boolean isPaused(){
        return paused;
    }

    public boolean isGateClosed(){
        return ! gateOpen;
    }

    public boolean isGateOpen(){
        return gateOpen;
    }

    /**
     * The actual bandwidth allocated to this client.
     */
    public void setAvailableFlowCapacity(int availableFlowCapacity) {
        this.availableFlowCapacity = availableFlowCapacity;
    }

    public int getAvailableFlowCapacity(){
        return availableFlowCapacity;
    }

    public int getPriority() {
        return getAssignedNumber() - 1;
    }
    
    private final static String[] PRIORITY_STRINGS = { "A", "B", "C", "D", "E" };

    public String getPriorityAsString() {
        // bounds check
        int priority = getPriority();
        if (priority >= 0 && priority < PRIORITY_STRINGS.length) {
            return PRIORITY_STRINGS[priority];
        }
        return "Position not found";
    }

    /**
     * here the parameters isPaused, isStopped and isStartDownload would change.
     * The server every time would check the clients queue. It would be a queue of clients requests.
     * and it then would check these bits...and then act accordingly. These would be the operations
     * that the users are allowed to do.
     */

    public void openGate(){
        gateOpen = true;
        paused = false;
    }

    public void pause(){
        paused = true;
        gateOpen = false;
    }
    
    public void unpause() {
        paused = false;
        gateOpen = true;
    }

    public void closeGate(){
        gateOpen = false;
        paused = false;
    }

    /**
     * get and set the TOkens that are contributed by this client.
     *     * @param contributedTokens
     */
    public void setInvestedTokens(int investedTokens) {
        this.investedTokens = investedTokens;
    }

    public int getInvestedTokens(){
        return investedTokens;
    }
    
    public int getUninvestedTokens() {
        return roundConfiguration.getMaximumInvestedTokens() - investedTokens;
    }

    public GroupDataModel getGroupDataModel() {
        return groupDataModel;
    }
    public void setGroupDataModel(GroupDataModel group) {
        this.groupDataModel = group;
    }
    public Identifier getId() {
        return id;
    }

    public void setRoundConfiguration(RoundConfiguration roundConfiguration) {
        this.roundConfiguration = roundConfiguration;
    }
/**
 * clearing the clientData at the end of each round
 *
 */
    public void endRound() {
        closeGate();
        investedTokens = 0;
        //adding number of files to be downloaded = 0 per round
        waterUsed = 0;
    }

    public void resetAllTokens() {
        endRound();
        totalTokens = 0;
    }

    public int getAssignedNumber() {
        return assignedNumber;
    }

    public void setAssignedNumber(int assignedNumber) {
        this.assignedNumber = assignedNumber;
    }

    public int getTotalTokens() {
        return totalTokens;
    }


    public RoundConfiguration getRoundConfiguration(){
        return roundConfiguration;
    }
    /**
     * This would initialize the clientData before the start of each download.
     *
     */
    public void init(int availableFlowCapacity) {
    	closeGate();
    	setAvailableFlowCapacity(availableFlowCapacity);
    }
    
    public void award() {
        totalTokens += getTotalTokensEarned();
    }

    /**
     * Returns the current number of tokens given to this participant. 
     * @return
     */
    public int getTotalTokensEarned(){
        return waterToTokenFunction() + getUninvestedTokens();
    }

    /**
     * Reward function table correlating water usage to tokens earned.
     * 
     * @param value
     * @return
     */
    public int waterToTokenFunction() {
    	if (waterUsed < 150) {
    		return 0;
    	}
    	else if (waterUsed < 200) {
    		return 1;
    	}
    	else if (waterUsed < 250) {
    		return 4;
    	}
    	else if (waterUsed < 300) {
    		return 10;
    	}
    	else if (waterUsed < 350) {
    		return 15;
    	}
    	else if (waterUsed < 400) {
    		return 18;
    	}
    	else if (waterUsed < 500) {
    		return 19;
    	}
    	else if (waterUsed < 550) {
    		return 20;
    	}
    	else if (waterUsed < 650) {
    		return 19;
    	}
    	else if (waterUsed < 700) {
    		return 18;
    	}
    	else if (waterUsed < 750) {
    		return 15;
    	}
    	else if (waterUsed < 800) {
    		return 10;
    	}
    	else if (waterUsed < 850) {
    		return 4;
    	}
    	else if (waterUsed < 900) {
    		return 1;
    	}
    	else {
    		return 0;
    	}
    }

    public void collectWater() {
    	waterUsed += availableFlowCapacity;
    }
    
    public int getWaterUsed(){
        return waterUsed;
    }

}


