package edu.asu.commons.irrigation.conf;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
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
public class ServerConfiguration extends ExperimentConfiguration.Base<ServerConfiguration, RoundConfiguration> {

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

    public static String toPriorityString(int clientPriority) {
        // bounds check
        if (clientPriority >= 0 && clientPriority < PRIORITY_STRINGS.length) {
            return PRIORITY_STRINGS[clientPriority];
        }
        return "Position not found";
    }

    public boolean isUndisruptedFlowRequired() {
        return getBooleanProperty("undisrupted-flow-required", false);
    }

    public String getUndisruptedFlowInstructions() {
        return getProperty("undisrupted-flow-instructions", "");
    }

    public double getShowUpPayment() {
        return getDoubleProperty("showup-payment", 5.0d);
    }

    public double getMaximumPayment() {
        return getDoubleProperty("maximum-payment", 40.0d);
    }

    public String getInitialInstructions() {
        ST template = createStringTemplate(getProperty("initial-instructions"));
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        template.add("showUpPayment", formatter.format(getShowUpPayment()));
        template.add("dollarsPerToken", formatter.format(getDollarsPerToken()));
        template.add("quizCorrectAnswerReward", formatter.format(getQuizCorrectAnswerReward()));
        template.add("maximumPayment", formatter.format(getMaximumPayment()));
        return template.render();
    }

    public String getChatInstructions() {
        return render(getProperty("chat-instructions"));
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

    public double getTokenEarningsThisRound(ClientData data) {
        return data.getAllTokensEarnedThisRound() * getDollarsPerToken();
    }

    public double getQuizEarnings(ClientData data) {
        return data.getCorrectQuizAnswers() * getQuizCorrectAnswerReward();
    }

    public String getRestrictedVisibilityInstructions() {
        return render(getProperty("restricted-visibility-instructions"));
    }

    public boolean isRestrictedVisibility() {
        return getFieldOfVision() > 0;
    }

    /**
     * Returns the number of neighbors visible on both sides of the participant. A negative value signifies that
     * participants can see everything.
     * 
     * @return the number of neighbors visible to either side of each participant.
     */
    public int getFieldOfVision() {
        return getIntProperty("field-of-vision", -1);
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
        }
        return answers;
    }

    public String getQuizQuestion(int pageNumber) {
        return getProperty("general-instructionsq" + pageNumber);
    }

    public String getQuizInstructions() {
        return render(getProperty("quiz-instructions"));
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

    public int getRoundDuration() {
        return getIntProperty("round-duration", 50);
    }

    public String getGameScreenshotInstructions() {
        return render(getProperty("game-screenshot-instructions"));
    }

    public String getSameAsPreviousRoundInstructions() {
        return getProperty("same-as-previous-round-instructions");
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

    public String getQuizResults(List<String> incorrectQuestionNumbers, Map<Object, Object> actualAnswers) {
        ST template = createStringTemplate(getProperty("quiz-results"));
        // FIXME: actual answers includes the submit button, so there's an off-by-one that we need to deal with.
        int totalQuestions = actualAnswers.size() - 1;
        int numberCorrect = totalQuestions - incorrectQuestionNumbers.size();
        template.add("allCorrect", incorrectQuestionNumbers.isEmpty());
        template.add("numberCorrect", numberCorrect);
        template.add("totalQuestions", totalQuestions);
        template.add("totalQuizEarnings", toCurrencyString(getQuizCorrectAnswerReward() * numberCorrect));
        for (String incorrectQuestionNumber : incorrectQuestionNumbers) {
            template.add("incorrect_" + incorrectQuestionNumber, String.format("Your answer, %s, was incorrect.", actualAnswers.get(incorrectQuestionNumber)));
        }
        return template.render();
    }

}
