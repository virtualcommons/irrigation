package edu.asu.commons.irrigation.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.stringtemplate.v4.ST;

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
    protected RoundConfiguration createRoundConfiguration(String roundConfigurationFile) {
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
    	String initialInstructions = assistant.getProperty("initial-instructions", "");
    	ST template = new ST(initialInstructions, '{', '}');
    	// FIXME: make this dynamic for all properties and change property names to camel case instead of hyphenated.
    	// e.g., something like:
//    	for (Map.Entry<Object, Object> entry: assistant.getProperties().entrySet()) {
//    	    template.add(entry.getKey().toString(), entry.getValue());
//    	}
    	template.add("chatDuration", getChatDuration());
    	return template.render();
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
    	String chatInstructions = assistant.getProperty("chat-instructions", "");
    	if (chatInstructions.contains("%d")) {
    		return String.format(chatInstructions, getChatDuration());
    	}
    	return chatInstructions;
    }

    public String getGameScreenshotInstructions() {
        return assistant.getProperty("game-screenshot-instructions");
    }

}
