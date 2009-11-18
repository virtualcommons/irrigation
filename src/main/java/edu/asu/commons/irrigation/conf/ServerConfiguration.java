package edu.asu.commons.irrigation.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.asu.commons.conf.ExperimentConfiguration;

/**
 * $Id$
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
 * @version $Revision$
 */
public class ServerConfiguration 
extends ExperimentConfiguration.Base<RoundConfiguration> {

    private static final long serialVersionUID = 7867208205942476733L;

    private final static String CONFIGURATION_FILE_NAME = "irrigation.xml";

    private final static String DEFAULT_LOG_FILE_DESTINATION = "irrigation.log";

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
    protected RoundConfiguration createConfiguration(String roundConfigurationFile) {
        return new RoundConfiguration(roundConfigurationFile);
    }

    @Override
    protected String getServerConfigurationFilename() {
        return CONFIGURATION_FILE_NAME;
    }
    
    private final static String[] PRIORITY_STRINGS = { "A", "B", "C", "D", "E" };

    public String toPriorityString(int clientPriority) {
        // bounds check
        if (clientPriority >= 0 && clientPriority < PRIORITY_STRINGS.length) {
            return PRIORITY_STRINGS[clientPriority];
        }
        return "Position not found";
    }

    public boolean isUndisruptedFlowRequired(){
        return assistant.getBooleanProperty("undisrupted-flow-required", false);
    }

    public String getUndisruptedFlowInstructions() {
        return assistant.getProperty("undisrupted-flow-instructions", "");
    }

    public double getShowUpPayment() {
        return assistant.getDoubleProperty("showup-payment", 5.0d);
    }

    public String getInitialInstructions() {
        return assistant.getProperty("initial-instructions");
    }
    
    public String getWelcomeInstructions() {
        return assistant.getProperty("welcome-instructions");
    }

    public Map<String, String> getQuizAnswers() {
        Properties properties = assistant.getProperties();
        Map<String, String> answers = new HashMap<String, String>();
        for (int i = 1; properties.containsKey("q" + i); i++) {
            String key = "q" + i;
            String answer = properties.getProperty(key);
            answers.put(key, answer);
            String quizExplanationKey = "explanation" + i;
            String quizExplanation = properties.getProperty(quizExplanationKey);
            answers.put(quizExplanationKey, quizExplanation);
            String descriptiveAnswerKey = "a" + i;
            answers.put(descriptiveAnswerKey, properties.getProperty(descriptiveAnswerKey));
        }
        return answers;
    }
    
    public String getQuizQuestion(int pageNumber) {
        return assistant.getProperty("general-instructionsq" + pageNumber);
    }

    public String getQuizPage(int pageNumber) {
        return assistant.getProperty("quiz-page"+pageNumber); 
    }
    
    public String getWaterCollectedToTokensTable() {
    	return assistant.getProperty("water-collected-to-tokens-table");
    }

    public String getFinalInstructions() {
        return assistant.getProperty("final-instructions", "<b>The experiment is now over.  Thanks for participating!</b>");
    }
    
    public String getInvestmentInstructions() {
        return assistant.getProperty("investment-instructions");
    }
    
    public int getNumberOfQuizPages() {
        return assistant.getIntProperty("question-pages", 2);
    }
    
    public int getChatDuration() {
        return assistant.getIntProperty("chat-duration", 60);
    }

    public String getChatInstructions() {
    	return assistant.getProperty("chat-instructions", 
    			String.format(
    					"<h3>Chat Instructions</h3><p>You now have the opportunity to chat for %d seconds.  You can discuss whatever you want" +
    					" related to the experiment with some restrictions.  You may not promise the other participant(s) side " +
    					"payments or threaten them with any consequence (e.g., physical violence) after the experiment is finished.  Also, you may not reveal your real identity." +
    					"We are monitoring chat traffic - if we notice a violation of the rules we will remove the group from the room until the other groups are finished with the experiment.</p>" +
    					"<p>You can send messages by typing in the text field at the bottom of the screen and then hit return or click send.  " +
    					"The time left for the discussion is displayed above the text field at the bottom of the screen.</p>",
    					getChatDuration())
    	);
    }

    public String getGameScreenshotInstructions() {
        return assistant.getProperty("game-screenshot-instructions");
    }

}
