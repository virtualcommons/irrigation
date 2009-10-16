package edu.asu.commons.irrigation.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.events.FileDownloadedEvent;
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
 * @author <a href='anonymouslee@gmail.com'>Allen Lee</a>, Deepali Bhagvat
 * @version $Revision$
 */

public class ClientData implements Serializable {

    private static final long serialVersionUID = 5281922601551921005L;

    private final Identifier id;
    
    private GroupDataModel groupDataModel;

    private String fileNumber;
    
    private List<String> filesDownloadedList = new ArrayList<String>();
    
    private boolean fileDownloaded = false;

    private int fileSize;

    private double percentFileDownloaded = 0;

    private double downloadedFileSize = 0;

    private int cropsGrown = 0;

    // maximum available bandwidth
    private double maximumIndividualFlowCapacity;
    /**
     * current download speed
     */
    private double availableFlowCapacity = 25.0d;

    private int investedTokens;

    private int assignedNumber;

    private RoundConfiguration roundConfiguration;

    private int totalTokens;

    private int tokensCollectedLastRound;

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
    
    // cap on the size of the entire stream (e.g,. 50 kbps)
    public double getMaximumAvailableBandwidth(){
        return groupDataModel.getMaximumAvailableFlowCapacity();
    }

    // cap on the size of the stream that can be delivered to the client (e.g., 25 kbps)
    public void setMaximumIndividualFlowCapacity(double deliveryBandwidth){
        this.maximumIndividualFlowCapacity = deliveryBandwidth;
    }

    public double getMaximumIndividualFlowCapacity(){
        return maximumIndividualFlowCapacity;
    }

    /**
     * The actual bandwidth allocated to this client.
     */
    public void setAvailableFlowCapacity(double availableFlowCapacity){
        this.availableFlowCapacity = availableFlowCapacity;
    }

    public double getAvailableFlowCapacity(){
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
     * set and get the current file_no from the client.The corresponding file sizes and their numbers 
     * are listed down in the configuration file. Thus I can get their sizes.
     * 
     */
    public void setFileNumber(String fileNumber){
        this.fileNumber = fileNumber;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public double getPercentFileDownload(){
        return percentFileDownloaded;
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

    public void setPaused(){
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
    public void reset() {
        resetFileInformation();
        closeGate();
        investedTokens = 0;
        //adding number of files to be downloaded = 0 per round
        cropsGrown = 0;
        filesDownloadedList.clear();
    }

    public void resetAllTokens() {
        reset();
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

    public int getTokensCollectedLastRound() {
        return tokensCollectedLastRound;
    }

    public RoundConfiguration getRoundConfiguration(){
        return roundConfiguration;
    }
    /**
     * This would initialize the clientData before the start of each download.
     *
     */
    public void init(double availableFlowCapacity) {
        resetFileInformation();
        maximumIndividualFlowCapacity = getRoundConfiguration().getMaximumIndividualFlowCapacity();
        //currentBandwidth = totalContributedBandwidth;
        this.availableFlowCapacity = availableFlowCapacity;
    }
    
    private void resetFileInformation() {
        fileSize = 0;
        fileNumber = "";
        downloadedFileSize = 0;
        percentFileDownloaded = 0;
        closeGate();
    }
    
    public void award() {
        totalTokens += getTotalTokensEarned();
    }

    /**
     * Returns the current number of tokens given to this participant. 
     * @return
     */
    public int getTotalTokensEarned(){
        return calculateTokensEarned() + (getRoundConfiguration().getMaximumInvestedTokens() - investedTokens);
    }

    /**
     * complex functions can be defined here
     * @param value
     * @return
     */

    private int calculateTokensEarned() {
    	switch (cropsGrown) {
    	case 0: case 1: 
    		return 0;
    	case 2:
    		return 6;
    	case 3:
    		return 22;
    	case 4:
    		return 28;
    	case 5:
    		return 30;
    	default:
    		throw new IllegalStateException("Should be a number between 0-5, was " + cropsGrown + " instead.");
    	}
//        double alpha,b,a,num,deno;
//        /**
//         * This would be taken by a proper configuration file
//         */
//        alpha = getRoundConfiguration().getAlphaAward();
//        a = getRoundConfiguration().getA_award();
//        b = getRoundConfiguration().getB_award();
//        num = alpha * Math.pow((double)filesDownloaded,b);
//        deno = Math.pow(a,b)+Math.pow(filesDownloaded,b);
//
//        /**
//         * until u get the final equation, you can go ahead with
//         * these values..
//         */
//        return (int)(Math.round(num/deno));
        //return no_fileDownloaded;
    }
    /**
     * calculates the percent of the file downloaded
     *
     */
    /**
     *Here check whether the percentageFile > 100, then init the file download
     *else simply increment the filesize. If we increment the file size and then
     *check whether the filesize > 100 and then invoke completedownload, that information 
     *is not send to the client, as the sending file progress events are in the same loop
     *as processDownload function
     */
    public void allocateFlowCapacity(double availableBandwidth) {
        assert gateOpen;
        fileDownloaded = false;
        incrementFileDownloadSize();
    }
    
    private void completeFileDownload() {
        cropsGrown++;
        fileDownloaded = true;
        filesDownloadedList.add(fileNumber);
        groupDataModel.getEventChannel().handle(new FileDownloadedEvent(id, fileNumber));
        resetFileInformation();
        //init(availableBandwidth);
    }
    
    /**
     * This would increament the percentage of the file downloaded
     * by Bi
     *  
     */
    public void incrementFileDownloadSize(){
        downloadedFileSize += availableFlowCapacity;
        percentFileDownloaded = (downloadedFileSize/fileSize)*100;
        if (percentFileDownloaded >= 100) {
        	//filesDownloaded++;
        	percentFileDownloaded = 100;
            completeFileDownload();
        }
    
    }

    public int getCropsGrown(){
        return cropsGrown;
    }

	public boolean isFileDownloaded() {
		return fileDownloaded;
	}

	public List<String> getDownloadListArray() {
		return filesDownloadedList;
	}
	

}


