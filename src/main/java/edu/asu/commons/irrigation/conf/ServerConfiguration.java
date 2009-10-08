package edu.asu.commons.irrigation.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.asu.commons.conf.ExperimentConfiguration;

/**
 * $Id: ServerConfiguration.java 131 2009-04-30 01:52:21Z alllee $
 * 
 * Contains the know-how for parsing and programmatically accessing the forager
 * server's configuration file properties. The forager server's config file
 * specifies per-server settings, per-experiment settings are managed by
 * ExperimentConfiguration.
 * 
 * FIXME: Recoverable exceptions that are handled shouldn't spit out their stack
 * trace without some additional info stating that they are mostly harmless.
 * 
 * FIXME: make utility/helper methods in ExperimentRoundConfiguration accessible
 * here via abstract superclass or something.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @see edu.asu.csid.irrigation.RoundConfiguration,
 *      edu.asu.csid.conf.ExperimentConfiguration
 * @version $Revision: 131 $
 */
public class ServerConfiguration 
extends ExperimentConfiguration.Base<RoundConfiguration> {

	private static final long serialVersionUID = 7867208205942476733L;

	private final static String CONFIGURATION_FILE_NAME = "irrigation.xml";

	private final static String DEFAULT_LOG_FILE_DESTINATION = "irrigation.log";

	//FIXME: can move this parameter to the configuration files

	public ServerConfiguration() {
		super();
	}

	public ServerConfiguration(String configurationDirectory) {
		super(configurationDirectory);
	}

	public String getLogFileDestination() {
		return assistant.getStringProperty("log", DEFAULT_LOG_FILE_DESTINATION);
	}

	public String getPersistenceDirectory() {
		return assistant.getStringProperty("save-dir", "data");
	}

	public boolean shouldUpdateFacilitator() {
		return assistant.getBooleanProperty("update-facilitator");
	}

	@Override
	protected RoundConfiguration createConfiguration(
			String roundConfigurationResource) {
		return new RoundConfiguration(roundConfigurationResource);
	}

	@Override
	protected String getServerConfigurationFilename() {
		return CONFIGURATION_FILE_NAME;
	}

	/**
	 * getting the general welcome instructions
	 * 
	 * @param instructionNumber
	 * @param pagesTraversed
	 * @return
	 */
	public String getGeneralInstructions(int instructionNumber,
			int pagesTraversed,int clientPriority) {
		String position = "";
		String undisruptedBandwidthInstruction = "";
		//System.out.println("Instruction Nnumber :"+instructionNumber+" Pages Traversed :"+pagesTraversed);
		
		if (instructionNumber != 11) {
			if (instructionNumber > pagesTraversed) {
				if (instructionNumber == 4) {
					//instructionNumber = getNewInstructionNumber(clientPriority);
					position = "\n YOUR POSITION : "+getPosition(clientPriority);
				}
				if(instructionNumber == 5 && isUndisruptedBandwidth()){
					undisruptedBandwidthInstruction=assistant.getStringProperty("general-instructions"+"-undisruptedBandwidth");
				}
				
				return (position+assistant.getStringProperty("general-instructions"
						+ instructionNumber
						)+undisruptedBandwidthInstruction+assistant
						.getStringProperty("general-instructionsq"
								+ instructionNumber));
			} else {
				if (instructionNumber == 4) {
					//instructionNumber = getNewInstructionNumber(clientPriority);
					position = "\n YOUR POSITION : "+getPosition(clientPriority);
				}
				if(instructionNumber == 5 && isUndisruptedBandwidth()){
					undisruptedBandwidthInstruction=assistant.getStringProperty("general-instructions"+"-undisruptedBandwidth");
				}
				return position+assistant.getStringProperty("general-instructions"
						+ instructionNumber,
						"<b>No instructions available for this round</b>")+undisruptedBandwidthInstruction;
			}

		}
		
		return assistant.getStringProperty("general-instructions"
				+ instructionNumber,
				"<b>No instructions available for this round</b>");

	}

	private String getPosition(int clientPriority) {
		// TODO Auto-generated method stub
		switch(clientPriority){
		case 0 : return "A";
		case 1 : return "B";
		case 2:  return "C";
		case 3:  return "D";
		case 4:  return "E";
		}
		return "Position not found";
	}

	//overriding method isLastRound
	public boolean isLastRound(){
		System.out.println("The Current round Number is :"+getCurrentRoundNumber());
		if(getCurrentRoundNumber() == assistant.getIntProperty("number-of-rounds")-1)
			return true;
		else
			return false;
	}
	
	public boolean isUndisruptedBandwidth(){
		if(assistant.getBooleanProperty("undisrupted-bandwidth")== true)
			return true;
		return false;
	}
	
	private int getNewInstructionNumber(int clientPriority) {
		// TODO Auto-generated method stub
		switch(clientPriority){
		case 0 : return 40;
				 
		case 1: return 41;
						
		}
		//FIXME: AT present just returning case 1 's case when clientPriority is 2,3,4. Need to change this.
		if(clientPriority == 2 || clientPriority == 3 || clientPriority == 4){
			return 41;
		}
		return -1;
	}

	public String getShowUpFees() {
		// TODO Auto-generated method stub
		return assistant.getStringProperty("showup-fees", "5");
	}

	public Map<String, String> getQuizAnswers() {
		//System.out.println("I am in the beginning of getquiz");
		Properties properties = assistant.getProperties();
		//System.out.println("I get the properties");
		// if (isQuizEnabled()) {
		Map<String, String> answers = new HashMap<String, String>();
		for (int i = 1; properties.containsKey("q" + i); i++) {
			String key = "q" + i;
			String answer = properties.getProperty(key);
			answers.put(key, answer);
		}
		//System.out.println("Answers Size :" + answers.size());
		return answers;
		// }
		// return Collections.emptyMap();
	}

	public String getGeneralInstructions(int pageNumber) {
		return assistant.getStringProperty("general-instructions"
				+pageNumber,
				"<b>No instructions available for this round</b>");
	}

}
