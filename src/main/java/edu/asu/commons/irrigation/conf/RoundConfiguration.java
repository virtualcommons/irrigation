package edu.asu.commons.irrigation.conf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.asu.commons.conf.ExperimentRoundParameters;
import edu.asu.commons.util.Duration;

/**
 * $Id$
 * 
 * Configuration parameters for a given round in the irrigation experiment. 
 * Provides reward functions, etc. 
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class RoundConfiguration extends ExperimentRoundParameters.Base<ServerConfiguration> {

    private static final long serialVersionUID = -5053624886508752562L;

    private static final float DEFAULT_DOLLARS_PER_TOKEN = .05f;

    public RoundConfiguration() {
        super();
    }
    
    public RoundConfiguration(String resource) {
        super(resource);
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

    public int getMaximumTokenInvestment() {
        return getIntProperty("max-token-investment", 10);
    }

    /**
     * returns maximum number of tokens that could have been contributed
     * @return
     */
    public int getMaximumTotalInvestedTokens() {
        return getMaximumTokenInvestment() * getClientsPerGroup();
    }

    public int getMaximumInfrastructureEfficiency() {
        return getIntProperty("max-infrastructure-efficiency", 100);
    }

    public boolean isPracticeRound() {
        return getBooleanProperty("practice-round");
    }
    
    public String getPracticeRoundPaymentInstructions() {
        return getProperty("practice-round-payment-instructions", 
                "This is a practice round so the earnings mentioned are only for illustrative purposes and <b>will not count towards your actual earnings</b>.");
    }

    // FIXME: horrible hack.. figure out why this is here again.
    public boolean isSecondPracticeRound(){
        return getBooleanProperty("second-practice-round",false);
    }

    public int getClientsPerGroup() {
        return getIntProperty("clients-per-group", 5);
    }

    /**
     * Returns the dollars/token exchange rate.  $1 = 1, 50 cents = $.50, 1 penny per token = .01, etc.
     * 
     * FIXME: this should be a ServerConfiguration parameter unless we change it so
     * the client keeps track of total dollars earned per round instead of total tokens earned per round. 
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
    
    public boolean shouldResetInfrastructureEfficiency() {
    	return getBooleanProperty("reset-infrastructure-efficiency", false);
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
     * getChatDuration() seconds before the round begins.
     * 
     * @return
     */
    public boolean isChatEnabledBeforeRound() {
        return getBooleanProperty("chat-enabled-before-round", true);
    }

    public int getChatDuration() {
        return getIntProperty("chat-duration", 40);
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
