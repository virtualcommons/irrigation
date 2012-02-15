package edu.asu.commons.irrigation.model;

import java.io.Serializable;

import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
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

    private Identifier id;
    
    private GroupDataModel groupDataModel;

    private int waterCollected = 0;

    private int availableFlowCapacity;

    private int investedTokens;

    private int assignedNumber;
    
    private int correctQuizAnswers = 0;
    
    private RoundConfiguration roundConfiguration;

    private int totalTokens;

    private boolean gateOpen = false;

    // transient formatted earnings data set by RoundConfiguration while generating debriefing text
    private String quizEarnings;
    private String grandTotalIncome;
    private String totalDollarsEarnedThisRound;
    
    // represents whether or not this ClientData is for the client in question.
    private transient boolean self = false;

    public ClientData(Identifier id) {
        this.id = id;
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
    
    // FIXME: logic duplicated with ServerConfiguration.toPriorityString(int priority);
    private final static String[] PRIORITY_STRINGS = { "A", "B", "C", "D", "E" };

    public String getPriorityString() {
        // bounds check
        int priority = getPriority();
        if (priority >= 0 && priority < PRIORITY_STRINGS.length) {
            return PRIORITY_STRINGS[priority];
        }
        return "Position not found";
    }

    public void openGate(){
        gateOpen = true;
    }

    public void closeGate(){
        gateOpen = false;
    }

    /**
     * get and set the Tokens that are contributed by this client.
     *     * @param contributedTokens
     */
    public void setInvestedTokens(int investedTokens) {
        this.investedTokens = investedTokens;
    }

    public int getInvestedTokens(){
        return investedTokens;
    }
    
    public int getUninvestedTokens() {
        return roundConfiguration.getMaximumTokenInvestment() - investedTokens;
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
     * Resets this client data's instance variables at the end of the round.
     * 
     */
    public void resetEndRound() {
        closeGate();
        investedTokens = 0;
        waterCollected = 0;
    }

    public void resetAllTokens() {
        resetEndRound();
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
    
    public void addTokensEarnedThisRoundToTotal() {
        totalTokens += getAllTokensEarnedThisRound();
    }

    /**
     * Returns the current number of tokens given to this participant. 
     * @return
     */
    public int getAllTokensEarnedThisRound(){
        return getTokensEarnedFromWaterCollected() + getUninvestedTokens();
    }
    
    public String getTotalDollarsEarnedThisRound() {
    	return totalDollarsEarnedThisRound;
    }

    /**
     * Reward function table correlating water usage to tokens earned.
     * 
     * @param value
     * @return
     */
    public int getTokensEarnedFromWaterCollected() {
    	return ServerConfiguration.getTokensEarned(waterCollected);
    }


    public void collectWater() {
    	waterCollected += availableFlowCapacity;
    }
    
    public int getWaterCollected(){
        return waterCollected;
    }
	
	public void setId(Identifier id) {
	    this.id = id;
	}
	
    public boolean isImmediateNeighbor(ClientData otherClientData) {
        int thisPosition = getPriority();
        int otherPosition = otherClientData.getPriority();
        return (thisPosition == otherPosition) || (thisPosition == otherPosition + 1) || (thisPosition == otherPosition - 1);
    }

    public int getCorrectQuizAnswers() {
        return correctQuizAnswers;
    }

    public void setCorrectQuizAnswers(int correctQuizAnswers) {
        this.correctQuizAnswers = correctQuizAnswers;
    }

    public String getQuizEarnings() {
        return quizEarnings;
    }

    public void setQuizEarnings(String quizEarnings) {
        this.quizEarnings = quizEarnings;
    }

    public String getGrandTotalIncome() {
        return grandTotalIncome;
    }

    public void setGrandTotalIncome(String grandTotalIncome) {
        this.grandTotalIncome = grandTotalIncome;
    }

    public void setTotalDollarsEarnedThisRound(String totalDollarsEarnedThisRound) {
        this.totalDollarsEarnedThisRound = totalDollarsEarnedThisRound;
    }

    public boolean isSelf() {
        return self;
    }

    public void setSelf(boolean current) {
        this.self = current;
    }

    public void addCorrectQuizAnswers(int numberOfCorrectQuizAnswers) {
        this.correctQuizAnswers += numberOfCorrectQuizAnswers;
    }
}


