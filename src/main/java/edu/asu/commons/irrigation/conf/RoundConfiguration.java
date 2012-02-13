package edu.asu.commons.irrigation.conf;

import java.text.NumberFormat;

import org.stringtemplate.v4.ST;

import edu.asu.commons.conf.ExperimentRoundParameters;
import edu.asu.commons.irrigation.client.ClientDataModel;
import edu.asu.commons.irrigation.model.ClientData;
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

    public int getWaterSupplyCapacity() {
        return getIntProperty("max-canal-flow-capacity", 30);
    }

    public int getMaximumTokenInvestment() {
        return getIntProperty("max-token-investment", 10);
    }
    
    public int getTokenEndowment() {
        return getIntProperty("token-endowment");
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
                "This was a practice round so the earnings mentioned are only for illustrative purposes and <b>do not count towards your actual earnings</b>.");
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
        return getDoubleProperty("dollars-per-token"); 
    }

    /**
     * for debugging purposes
     */
    public void report() {
        getProperties().list(System.err);
    }
    
    public boolean shouldResetInfrastructureEfficiency() {
    	return isFirstRound() || getBooleanProperty("reset-infrastructure-efficiency", false);
    }

    /**
     * Returns the instructions for this round.  If undefined at the round level it uses default instructions at the parent ServerConfiguration level.
     */
    public String getInstructions() {
        ST template = createStringTemplate(getProperty("instructions", getParentConfiguration().getSameAsPreviousRoundInstructions()));
        // FIXME: probably should just lift these out into methods on RoundConfiguration
        // and refer to them as self.durationInMinutes or self.dollarsPerTokenCurrencyString, etc.
        template.add("duration", inMinutes(getDuration()) + " minutes");
        template.add("dollarsPerToken", toCurrencyString(getDollarsPerToken()));
        if (isRestrictedVisibility()) {
            template.add("specialInstructions", getRestrictedVisibilityInstructions());
        }
        return template.render();
    }
    
    private String getRestrictedVisibilityInstructions() {
        return render(getProperty("restricted-visibility-instructions"));
    }

    public boolean shouldDisplayGroupTokens() {
        return getBooleanProperty("display-group-tokens");
    }

    public boolean isQuizEnabled() {
        return getBooleanProperty("quiz");
    }

    public String getQuizPage() {
        return getStringProperty("quiz-page");
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

    /**
     * Returns the duration of the round in seconds.  Set to default of 50 seconds per round.
     */
    @Override
    public Duration getRoundDuration() {
        return Duration.create(getRoundDurationInSeconds());
    }
    
    public int getRoundDurationInSeconds() {
        return getIntProperty("round-duration", 50);
    }

    public boolean shouldRandomizeGroup() {
        return getBooleanProperty("randomize-groups", false);
    }

    public boolean isRestrictedVisibility() {
        return getFieldOfVision() > 0;
    }
    
    /**
     * Returns the number of neighbors visible on both sides of the participant.  A negative value signifies that
     * participants can see everything.
     * @return the number of neighbors visible to either side of each participant.
     */
    public int getFieldOfVision() {
        return getIntProperty("field-of-vision", -1);
    }
    
    public String getClientDebriefingTemplate() {
        return getProperty("client-debriefing");
    }
    
    private void populateClientEarnings(ClientData data, ServerConfiguration serverConfiguration, NumberFormat currencyFormatter) {
        data.setGrandTotalIncome(currencyFormatter.format(serverConfiguration.getTotalIncome(data)));
        data.setTotalDollarsEarnedThisRound(currencyFormatter.format(serverConfiguration.getTotalTokenEarnings(data)));
        data.setQuizEarnings(currencyFormatter.format(serverConfiguration.getQuizEarnings(data)));
    }

    public String generateClientDebriefing(ClientDataModel clientDataModel, boolean showExitInstructions) {
        ST st = createStringTemplate(getClientDebriefingTemplate());
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        for (ClientData clientData: clientDataModel.getClientDataMap().values()) {
            populateClientEarnings(clientData, getParentConfiguration(), formatter);
        }
        st.add("clientData", clientDataModel.getClientData());
        st.add("dataModel", clientDataModel);
        st.add("showUpPayment", formatter.format(getParentConfiguration().getShowUpPayment()));
        st.add("showExitInstructions", showExitInstructions);
        return st.render();
    }
    
    public String generateContributionSummary(ClientData clientData) {
        ST st = createStringTemplate(getContributionSummaryTemplate());
        st.add("clientData", clientData);
        st.add("groupDataModel", clientData.getGroupDataModel());
        return st.render();
    }

    private String getContributionSummaryTemplate() {
        return getProperty("contribution-summary");
    }
    
    public int getChatDuration() {
    	return getIntProperty("chat-duration");
    }

}
