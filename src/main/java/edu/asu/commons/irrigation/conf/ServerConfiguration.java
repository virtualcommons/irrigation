package edu.asu.commons.irrigation.conf;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.stringtemplate.v4.NumberRenderer;
import org.stringtemplate.v4.ST;

import edu.asu.commons.conf.ExperimentConfiguration;
import edu.asu.commons.irrigation.model.ClientData;

/**
 * $Id$
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */
public class ServerConfiguration 
extends ExperimentConfiguration.Base<RoundConfiguration> {

    private static final double DEFAULT_QUIZ_CORRECT_ANSWER_REWARD = 0.50d;

    private static final long serialVersionUID = 7867208205942476733L;

    private static final String CONFIGURATION_FILE_NAME = "irrigation.xml";

    private static final String DEFAULT_LOG_FILE_DESTINATION = "irrigation.log";
    
    private static final double DEFAULT_DOLLARS_PER_TOKEN = 0.05d;

    public ServerConfiguration() {
        super();
    }

    public ServerConfiguration(String configurationDirectory) {
        super(configurationDirectory);
    }

    public String getLogFileDestination() {
        return getStringProperty("log", DEFAULT_LOG_FILE_DESTINATION);
    }

    public String getPersistenceDirectory() {
        return getStringProperty("save-dir", "data");
    }

    public boolean shouldUpdateFacilitator() {
        return getBooleanProperty("update-facilitator");
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
        return getBooleanProperty("undisrupted-flow-required", false);
    }

    public String getUndisruptedFlowInstructions() {
        return getProperty("undisrupted-flow-instructions", "");
    }

    public double getShowUpPayment() {
        return getDoubleProperty("showup-payment", 5.0d);
    }

    public String getInitialInstructions() {
    	ST template = createStringTemplate(getProperty("initial-instructions"));
    	template.groupThatCreatedThisInstance.registerRenderer(Number.class, new NumberRenderer());
    	NumberFormat formatter = NumberFormat.getCurrencyInstance();
    	template.add("showUpPayment", formatter.format(getShowUpPayment()));
        template.add("dollarsPerToken", formatter.format(getDollarsPerToken()));
        template.add("quizCorrectAnswerReward", formatter.format(getQuizCorrectAnswerReward()));
    	return template.render();
    }
    
    public String getChatInstructions() {
        return createStringTemplate(getProperty("chat-instructions")).render();
    }
    
    public String getChatDurationInMinutes() {
        long minutes = inMinutes(getChatDuration());
        return String.format("%d minute%s", minutes, (minutes > 1) ? "s" : "");
    }
    
    public double getTotalIncome(ClientData data) {
        return getTotalTokenEarnings(data) + getShowUpPayment() + getQuizEarnings(data);
    }
    
    public double getTotalTokenEarnings(ClientData data) {
        return data.getTotalTokens() * getDollarsPerToken();
    }
    
    public double getQuizEarnings(ClientData data) {
        return data.getCorrectQuizAnswers() * getQuizCorrectAnswerReward();
    }
    
    public String getRestrictedVisibilityInstructions() {
        return getProperty("restrictedVisibilityInstructions");
    }
    
    public boolean isRestrictedVisibility() {
        return getBooleanProperty("restrictedVisibility");
    }
    
    public double getQuizCorrectAnswerReward() {
        return getDoubleProperty("quiz-correct-answer-reward", DEFAULT_QUIZ_CORRECT_ANSWER_REWARD);
    }
    
    public double getMaximumQuizEarnings() {
        return getQuizCorrectAnswerReward() * getNumberOfQuizQuestions();
    }
    
    public String getWelcomeInstructions() {
        return getProperty("welcome-instructions");
    }
    
    public int getNumberOfQuizQuestions() {
        return getIntProperty("numberOfQuizQuestions", 6);
    }

    public Map<String, String> getQuizAnswers() {
        Properties properties = getProperties();
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
        return getProperty("general-instructionsq" + pageNumber);
    }

    public String getQuizPage(int pageNumber) {
        return getProperty("quiz-page"+pageNumber); 
    }
    
    public String getWaterCollectedToTokensTable() {
    	return getProperty("water-collected-to-tokens-table");
    }

    public String getFinalInstructions() {
        return getProperty("final-instructions", "<b>The experiment is now over.  Thanks for participating!</b>");
    }
    
    public String getInvestmentInstructions() {
        return render(getProperty("investment-instructions"));
    }
    
    public int getNumberOfQuizPages() {
        return getIntProperty("quiz-pages", 2);
    }
    
    public int getChatDuration() {
        return getIntProperty("chat-duration", 60);
    }


    public String getGameScreenshotInstructions() {
        return getProperty("game-screenshot-instructions");
    }
    
    public String getSameAsPreviousRoundInstructions() {
        return createStringTemplate(getProperty("same-as-previous-round-instructions")).render();
    }

    public String getClientDebriefingTemplate() {
        return getProperty("client-debriefing");
    }

    public double getDollarsPerToken() {
        return getDoubleProperty("dollars-per-token", DEFAULT_DOLLARS_PER_TOKEN);
    }
    
    public int getTokenEndowment() {
        return getIntProperty("token-endowment", 10);
    }

    public static int getTokensEarned(int waterCollected) {
    	if (waterCollected < 150) {
    		return 0;
    	}
    	else if (waterCollected < 200) {
    		return 1;
    	}
    	else if (waterCollected < 250) {
    		return 4;
    	}
    	else if (waterCollected < 300) {
    		return 10;
    	}
    	else if (waterCollected < 350) {
    		return 15;
    	}
    	else if (waterCollected < 400) {
    		return 18;
    	}
    	else if (waterCollected < 500) {
    		return 19;
    	}
    	else if (waterCollected < 550) {
    		return 20;
    	}
    	else if (waterCollected < 650) {
    		return 19;
    	}
    	else if (waterCollected < 700) {
    		return 18;
    	}
    	else if (waterCollected < 750) {
    		return 15;
    	}
    	else if (waterCollected < 800) {
    		return 10;
    	}
    	else if (waterCollected < 850) {
    		return 4;
    	}
    	else if (waterCollected < 900) {
    		return 1;
    	}
    	else {
    		return 0;
    	}
    }

}
