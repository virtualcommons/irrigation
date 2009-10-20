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
    
    /**
     * Returns the appropriate general instructions text, performing logic for adding
     * positions and quizzes.   
     * 
     * @param instructionPageNumber
     * @param pagesTraversed
     * @return
     */
    public String getGeneralInstructions(
            int instructionPageNumber,
            int pagesTraversed, 
            int clientPosition) 
    {
        // FIXME: get rid of hard coded instruction page constants.
        if (instructionPageNumber < 11) {
            StringBuilder builder = new StringBuilder();
            if (instructionPageNumber == 4) {
                builder.append("Your position: ").append(toPriorityString(clientPosition));
            }
            builder.append(getGeneralInstructions(instructionPageNumber));
            if (isUndisruptedFlowRequired()) {
                builder.append(getUndisruptedFlowInstructions());
            }
            // if the current instruction page number is greater than the number of pages traversed, then 
            // we need to render the quizzes.  Otherwise, we don't. 
            if (instructionPageNumber > pagesTraversed) {
                builder.append( getQuizQuestion(instructionPageNumber) );
            }
            return builder.toString();
        }
        else {
            return getGeneralInstructions(instructionPageNumber);
        }
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

    public Map<String, String> getQuizAnswers() {
        Properties properties = assistant.getProperties();
        Map<String, String> answers = new HashMap<String, String>();
        for (int i = 1; properties.containsKey("q" + i); i++) {
            String key = "q" + i;
            String answer = properties.getProperty(key);
            answers.put(key, answer);
        }
        return answers;
    }
    
    public String getQuizQuestion(int pageNumber) {
        return assistant.getProperty("general-instructionsq" + pageNumber);
    }

    public String getGeneralInstructions(int pageNumber) {
        return assistant.getProperty(
                "general-instructions"+pageNumber, 
                "<b>No instructions available for this round</b>");
    }

    public String getFinalInstructions() {
        return assistant.getProperty("final-instructions", "<b>The experiment is now over.  Thanks for participating!</b>");
    }
    
    public String getInvestmentInstructions() {
        return assistant.getProperty("investment-instructions");
    }
    
    public int getNumberOfGeneralInstructionPages() {
        return assistant.getIntProperty("general-instruction-pages", 8);
    }

}
