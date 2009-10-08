package edu.asu.commons.irrigation.conf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.asu.commons.conf.ExperimentRoundParameters;
import edu.asu.commons.util.Duration;

public class RoundConfiguration extends ExperimentRoundParameters.Base<ServerConfiguration> {

    private static final long serialVersionUID = -5053624886508752562L;

    private static final float DEFAULT_DOLLARS_PER_TOKEN = .05f;

    public RoundConfiguration() {
        super();
    }
    
    public RoundConfiguration(String resource) {
        super(resource);
    }

    /**
     * used to get the file Size given the File number from the current
     * configuration file
     */
    public int getFileSize(String fileNumber) {
        // FIXME: lift constants
        return getIntProperty(fileNumber, 250);
    }

    public int getMaximumClientFlowCapacity() {
        return getIntProperty("max-client-flow-capacity", 25);
    }

    public int getInitialInfrastructureEfficiency() {
        return getIntProperty("initial-infrastructure-efficiency", 75);
    }

    public int getInfrastructureDegradationFactor() {
        return getIntProperty("infrastructure-degradation-factor", 25);
    }

    public int getMaximumCanalFlowCapacity() {
        return getIntProperty("max-canal-flow-capacity", 40);
    }

    public double getFilesDownloadAwardConversion() {
        // FIXME: change String key to something more meaningful
        return getDoubleProperty("FilesDownload-Award-Conversion", 1.0d);
        //        return (double) Integer.parseInt(properties.getProperty("FilesDownload-Award-Conversion"));
    }

    public int getMaximumTokenContribution() {
        return getIntProperty("max-token-contribution", 10);
    }

    /**
     * returns maximum number of tokens that could have been contributed
     * @return
     */
    public int getMaximumTotalTokenContribution(){
        return getMaximumTokenContribution() * getClientsPerGroup();
    }

    public int getMaximumInfrastructureEfficiency() {
        return getIntProperty("maximum-infrastructure-efficiency", 100);
    }

    public int getBtmax() {
        return getIntProperty("Btmax");
    }

    public boolean isPracticeRound() {
        return getBooleanProperty("practice-round");
    }

    public boolean isSecondPracticeRound(){
        return getBooleanProperty("second-practice-round",false);
    }

    public int getClientsPerGroup() {
        return getIntProperty("clients-per-group", 0);
    }

    /**
     * Returns the dollars/token exchange rate.  $1 = 1, 50 cents = $.50, 1 penny per token = .01, etc.
     * 
     * 
     * @return
     */
    public double getDollarsPerToken() {
        return getDoubleProperty("dollars-per-token", DEFAULT_DOLLARS_PER_TOKEN); 
    }

    /**
     * for debugging purposes
     */
    public void report() {
        getProperties().list(System.err);
    }

    public String getInstructions() {
        return getStringProperty("instructions", 
                "<b>No instructions available for this round.</b>");
    }

    public boolean shouldDisplayGroupTokens() {
        return getBooleanProperty("display-group-tokens");
    }

    public boolean isQuizEnabled() {
        return getBooleanProperty("quiz");
    }

    public String getQuizInstructions() {
        return getStringProperty("quiz-instructions");
    }

    public Map<String, String> getQuizAnswers() {
        Properties properties = getProperties();
        if (isQuizEnabled()) {
            Map<String, String> answers = new HashMap<String, String>();
            for (int i = 1; properties.containsKey("q" + i); i++) {
                String key = "q" + i;
                String answer = properties.getProperty(key);
                answers.put(key, answer);
            }
            return answers;
        }
        return Collections.emptyMap();
    }

    /**
     * Returns true if the current round should have a communication session for
     * getCommunicationDuration() seconds before the round begins.
     * 
     * @return
     */
    public boolean isChatEnabledBeforeRound() {
        if (isPracticeRound()) {
            return false;
        }
        return getBooleanProperty("chat-enabled-before-round", true);
    }

    public int getChatDuration() {
        return getIntProperty("communication-duration", 40);
    }

    public double getAlpha() {
        return getDoubleProperty("TBM-alpha", 0.0d);
    }

    public double getA() {
        return getDoubleProperty("TBM-a", 0.0d);
    }

    public double getB() {
        return getDoubleProperty("TBM-b", 0.0d);
    }

    public double getAlphaAward() {
        return getDoubleProperty("FAM-alpha", 0.0d);
    }

    public double getA_award() {
        return getDoubleProperty("FAM-a", 0.0d);
    }

    public double getB_award() {
        return getDoubleProperty("FAM-b", 0.0d);
    }

    /**
     * Returns the duration of the round in seconds.  Set to default of 50 seconds per round.
     */
    @Override
    public Duration getRoundDuration() {
        return Duration.create(getIntProperty("round-duration", 50));
    }

    public boolean shouldRandomizeGroup() {
        return getBooleanProperty("randomize-groups", false);
    }


}
