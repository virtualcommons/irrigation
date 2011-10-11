package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.html.HTMLEditorKit;

import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.irrigation.events.EndRoundEvent;
import edu.asu.commons.irrigation.events.QuizResponseEvent;
import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.util.HtmlEditorPane;
import edu.asu.commons.util.HtmlEditorPane.FormActionEvent;

/**
 * $Id$
 * 
 * The root experiment window placed in the client's JFrame.  
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class ExperimentGameWindow extends JPanel {

    private static final long serialVersionUID = -5636795631355367711L;

    private ClientDataModel clientDataModel;
    
    private ChatPanel chatPanel;

    private HtmlEditorPane instructionsEditorPane;

    private JPanel tokenInvestmentPanel;
    private JPanel contributionInformationPanel;

    private JTextField investedTokensTextField;

    private InfrastructureEfficiencyChartPanel infrastructureEfficiencyChartPanel;

    private IrrigationClient client;
    
    private MainIrrigationGameWindow irrigationGamePanel;

    private StringBuilder instructionsBuilder = new StringBuilder();

    private JEditorPane contributionInformationEditorPane;

    private JButton nextButton;

    private JPanel instructionsNavigationPanel;

    private JButton previousButton;

    private JPanel instructionsPanel;

    private int currentQuizPageNumber = 1;

    private JPanel submitTokenPanel;

    private HtmlEditorPane tokenInstructionsEditorPane;

    private int quizzesAnswered = 0;

    private TokenInvestmentPieChartPanel pieChart;

//    private CanalAnimationPanel canalAnimationPanel;

    private CardLayout cardLayout;
    
    private Map<Integer, String> quizPageResponses = new HashMap<Integer, String>();

    private JLabel investedTokensLabel;

    public ExperimentGameWindow(IrrigationClient client) {
        this.client = client;
        this.clientDataModel = client.getClientDataModel();
        initialize();
    }
    
    private void initialize() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        addToCardLayout(getInstructionsPanel());
        irrigationGamePanel = new MainIrrigationGameWindow(client);
        addToCardLayout(irrigationGamePanel);
        addToCardLayout(getTokenInvestmentPanel());
        addToCardLayout(getChatPanel());
        addToCardLayout(getContributionInformationPanel());
        setInstructions(getServerConfiguration().getWelcomeInstructions());
    }
    
    private void addToCardLayout(Component component) {
        add(component, component.getName());
    }

    private JPanel getTokenInvestmentPanel() {
        if (tokenInvestmentPanel == null) {
            tokenInvestmentPanel = new JPanel();
            tokenInvestmentPanel.setName("Token investment panel");
            tokenInvestmentPanel.setLayout(new BorderLayout());
            tokenInstructionsEditorPane = createInstructionsEditorPane();
            JScrollPane tokenInstructionsScrollPane = new JScrollPane(tokenInstructionsEditorPane);
            tokenInvestmentPanel.add(tokenInstructionsScrollPane, BorderLayout.CENTER);
            tokenInvestmentPanel.add(getSubmitTokenPanel(), BorderLayout.SOUTH);
            tokenInvestmentPanel.setBackground(Color.WHITE);
        }
        return tokenInvestmentPanel;
    }

    private JPanel getSubmitTokenPanel() {
        if (submitTokenPanel == null) {
            submitTokenPanel = new JPanel();
            submitTokenPanel.setLayout(new BorderLayout());
            submitTokenPanel.add(getInvestedTokensLabel(), BorderLayout.PAGE_START);
            submitTokenPanel.add(getInvestedTokensTextField(), BorderLayout.CENTER);
            JButton submitTokensButton = new JButton("Invest");
            submitTokensButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    submitInvestedTokens();
                }
            });
            submitTokenPanel.add(submitTokensButton, BorderLayout.PAGE_END);
            return submitTokenPanel;
        }
        return submitTokenPanel;
    }

    private JPanel getInstructionsPanel() {
        if (instructionsPanel == null) {
            instructionsPanel = new JPanel();
            instructionsPanel.setName("Instructions panel");
            instructionsPanel.setLayout(new BorderLayout());
            JScrollPane instructionsScrollPane = new JScrollPane(getInstructionsEditorPane());
            instructionsScrollPane = new JScrollPane(getInstructionsEditorPane());
            instructionsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            instructionsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            instructionsPanel.add(instructionsScrollPane, BorderLayout.CENTER);
//            instructionsPanel.add(getQuizNavigationPanel(), BorderLayout.PAGE_END);
        }
        return instructionsPanel;
    }
    
    private JPanel getQuizNavigationPanel() {
        if (instructionsNavigationPanel == null) {
            instructionsNavigationPanel = new JPanel();
            instructionsNavigationPanel.setLayout(new BorderLayout());
            instructionsNavigationPanel.add(getPreviousButton(), BorderLayout.LINE_START);
            instructionsNavigationPanel.add(getNextButton(), BorderLayout.LINE_END);
        }
        return instructionsNavigationPanel;
    }

    private JButton getPreviousButton() {
        if (previousButton == null) {
            previousButton = new JButton();
            previousButton.setText("Previous");
            previousButton.setEnabled(false);
            previousButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // getting the next instruction Number
                    if (currentQuizPageNumber > 1) {
                        currentQuizPageNumber--;
                        setInstructions(getQuizPage());
                    }
                    previousButton.setEnabled(currentQuizPageNumber > 1);
                    nextButton.setEnabled(true);
                }
            });
        }
        return previousButton;
    }

    private JButton getNextButton() {
        if (nextButton == null) {
            nextButton = new JButton();
            nextButton.setText("Next");
            nextButton.setEnabled(false);
            nextButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    previousButton.setEnabled(true);
                    currentQuizPageNumber++;
                    if (currentQuizPageNumber <= getServerConfiguration().getNumberOfQuizPages()) {
                        setInstructions(getQuizPage());
                    }
                    else {
                        // this only works in between practice rounds #1 & #2 or after practice round 2
                        setInstructions(instructionsBuilder.toString());
                        nextButton.setEnabled(false);
                        disableQuiz();
                    }
                }
            });

        }
        return nextButton;
    }

//    private CanalAnimationPanel getCanalAnimationPanel() {
//        if (canalAnimationPanel == null) {
//            canalAnimationPanel = new CanalAnimationPanel(40);
//        }
//        return canalAnimationPanel;
//    }

    private String getQuizPage() {
        StringBuilder builder = new StringBuilder();
        String quizPage = getServerConfiguration().getQuizPage(currentQuizPageNumber);
        String quizPageResponse = quizPageResponses.get(currentQuizPageNumber);
        if (quizPageResponse == null) {
            builder.append(quizPage);
        }
        else {
            quizPage = quizPage.replace("<input type=\"submit\" name=\"submit\" value=\"Submit\">", "");
            builder.append(quizPage).append(quizPageResponse);
        }
        return builder.toString();
    }
    
    private ServerConfiguration getServerConfiguration() {
        return clientDataModel.getServerConfiguration();
    }
    
    private JLabel getInvestedTokensLabel() {
        if (investedTokensLabel == null) {
            investedTokensLabel = new JLabel();
        }
        return investedTokensLabel; 
    }

    private JTextField getInvestedTokensTextField() {
        if (investedTokensTextField == null) {
            investedTokensTextField = new JTextField();
            investedTokensTextField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                        submitInvestedTokens();
                    }
                }
            });
        }
        return investedTokensTextField;
    }

    private void submitInvestedTokens() {
        try {
            int token = Integer.parseInt(investedTokensTextField.getText());
            // validate token range
            if (token >= 0 && token <= 10) {
                client.transmitInvestedTokensEvent(token);
                investedTokensLabel.setText("");
                setInstructions("Please wait while the server computes your total flow capacity based on your group's total token contribution investment.");
                addCenterComponent(getInstructionsPanel());
            } 
            else {
                investedTokensLabel.setText("Please enter a number between 0 and 10");
            }
        }
        catch(NumberFormatException e){
            investedTokensLabel.setText("Please enter a number between 0 and 10");
        }
    }

    private HtmlEditorPane getInstructionsEditorPane() {
        if (instructionsEditorPane == null) {
            instructionsEditorPane = createInstructionsEditorPane();
            instructionsEditorPane.setName("Instructions editor pane");
            // create a quiz listener and then initialize the instructions.
            instructionsEditorPane.setActionListener(createQuizListener(getServerConfiguration()));
        }
        return instructionsEditorPane;
    }
    
    private HtmlEditorPane createInstructionsEditorPane() {
        HtmlEditorPane htmlEditorPane = new HtmlEditorPane();
        htmlEditorPane.setEditable(false);
        htmlEditorPane.setFont(new Font("LucidaSansRegular", Font.TRUETYPE_FONT, 16));
        htmlEditorPane.setBackground(Color.WHITE);
        return htmlEditorPane;

    }

    private void addCenterComponent(Component newCenterComponent) {
        cardLayout.show(this, newCenterComponent.getName());
        revalidate();
    }

    public void startRound() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                addCenterComponent(irrigationGamePanel);
                irrigationGamePanel.startRound();
            }
        });
    }

    public void update() {
        irrigationGamePanel.updateClientStatus(clientDataModel);
    }

    /*
     * updates the mainIrrigationGameWindow Panel and adds
     * instructionsScrollPane with the debriefing information
     */
    public void endRound(final EndRoundEvent event) {
        irrigationGamePanel.endRound();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                investedTokensTextField.setText("");
                addDebriefingText(event);
                addCenterComponent(getInstructionsPanel());
            }
        });
        info("ExperimentGameWindow finished cleanup, ending round completed.");
    }

    private void info(String message) {
        System.err.println(message);
    }

    /**
     * 
     * @param event
     */
    private void addDebriefingText(EndRoundEvent event) {
        double showUpPayment = clientDataModel.getServerConfiguration().getShowUpPayment();
        RoundConfiguration roundConfiguration = clientDataModel.getRoundConfiguration();
        // FIXME: move this to RoundConfiguration instead and then templatize using
        // StringTemplate
        instructionsBuilder.delete(0, instructionsBuilder.length());
        instructionsBuilder.append("<b>Results from the previous round</b>");
        instructionsBuilder.append(
                "<table border='3' cellpadding='5'><thead><th>Position</th><th>Initial token endowment</th><th>Tokens invested</th><th>Tokens not invested</th>" +
                "<th>Tokens earned from growing crops</th><th>Total tokens earned during this round</th>" +
                "<th>Dollars earned during this round</th><th>Total dollars earned (including show-up bonus)</th></thead>" +
                "<tbody>");

        for(ClientData clientData : clientDataModel.getClientDataSortedByPriority()) {
            String backgroundColor = clientData.getPriority() == clientDataModel.getPriority() ? "#FFFFCC" : "CCCCCC"; 
        	instructionsBuilder.append(
        			String.format("<tr align='center' bgcolor='%s'><td>%s</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>$%3.2f</td><td>$%3.2f</td></tr>",
        			        backgroundColor,
        			        clientData.getPriorityString(),
        					clientData.getMaximumTokenInvestment(),
        					clientData.getInvestedTokens(),
        					clientData.getUninvestedTokens(),
        					clientData.getTokensEarnedFromWaterCollected(),
        					clientData.getAllTokensEarnedThisRound(),
        					clientData.getTotalDollarsEarnedThisRound(),
        					clientData.getTotalDollarsEarned() + showUpPayment
        					));
        }
        
        ClientData clientData = clientDataModel.getClientData();
        instructionsBuilder.append("</tbody></table><hr>");
        instructionsBuilder.append(String.format("<h3>You (position %s) received $%3.2f this past round.  Your total earnings are $%3.2f, including the $%3.2f show up bonus.</h3>",
        		clientData.getPriorityString(), clientData.getTotalDollarsEarnedThisRound(), clientData.getTotalDollarsEarned()+showUpPayment, showUpPayment));
        //append the added practice round instructions
        
        if (roundConfiguration.isPracticeRound()) {
            instructionsBuilder.append(roundConfiguration.getPracticeRoundPaymentInstructions());
        }
        else if (event.isLastRound()) {
            instructionsBuilder.append(getServerConfiguration().getFinalInstructions());
        }
        instructionsBuilder.append("<hr>");
        displayInstructions(instructionsBuilder.toString());
    }

    // adding the instructions into the instruction Panel
    private void setInstructions(final String instructions) {
        setInstructions(instructions, false);
    }
    
    private void setInstructions(String instructions, boolean caretToEnd) {
        instructionsEditorPane.setText(instructions);
        int caretPosition = caretToEnd ? instructionsEditorPane.getDocument().getLength() - 1 : 0;
        instructionsEditorPane.setCaretPosition(caretPosition);
    }
    
    private void displayInstructions(final String instructions) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setInstructions(instructions);
                addCenterComponent(getInstructionsPanel());
            }
        });
    }
    
    private ActionListener createQuizListener(final ServerConfiguration configuration) {
        return new ActionListener() {
            private Map<String, String> quizAnswers = configuration.getQuizAnswers();
            public synchronized void actionPerformed(ActionEvent e) {
                if (quizPageResponses.containsKey(currentQuizPageNumber)) {
                    // this form has already been submit.
                    // shouldn't happen
                    // FIXME: report to user?
                    return;
                }
                FormActionEvent formEvent = (FormActionEvent) e;
                Properties responses = formEvent.getData();
                List<String> incorrectAnswers = new ArrayList<String>();
                responses.list(System.err);
                StringBuilder builder = new StringBuilder();
                TreeMap<String, String> sortedResponses = new TreeMap<String, String>();
                // sort responses so we can put them in order.
                for (Map.Entry<Object, Object> entry : responses.entrySet()) {
                    sortedResponses.put((String) entry.getKey(), (String) entry.getValue());
                }
                builder.append("<hr><h2>Results</h2><hr>");
                for (Map.Entry<String, String> entry : sortedResponses.entrySet()) {
                    String questionNumber = (String) entry.getKey();
                    if (questionNumber.charAt(0) == 'q') {
                        String number = questionNumber.substring(1, questionNumber.length());
                        String response = (String) entry.getValue();
                        if (response == null || response.trim().isEmpty()) {
                            // if any responses are empty, abort.
                            
                            return;
                        }
                        String correctAnswer = quizAnswers.get(questionNumber);
                        builder.append(String.format("<p><b>Question %s</b><br/>", number));
                        String color = "blue";
                        if (! response.equals(correctAnswer)) {
                            incorrectAnswers.add(questionNumber);
                            color = "red";
                        }
                        builder.append(String.format("Your answer: <font color='%s'>%s</font><br/>", color, response));
                        builder.append(String.format("Correct answer: %s<br/>", quizAnswers.get("a" + number)));
                        builder.append(quizAnswers.get( "explanation" + number )).append("</p>");
                    }
                }
                if (incorrectAnswers.isEmpty()) {
                	builder.append("<p>Congratulations, you got all of the questions correct.</p>");
                }
                else {
                	builder.append(String.format("<p>You answered %d questions incorrectly.  Please review the correct answers.</p>", incorrectAnswers.size()));
                }
                builder.append("<p><b>Please click the 'Next' button at the bottom right of the screen to continue.</b></p>");
                quizPageResponses.put(currentQuizPageNumber, builder.toString());
                // no matter what we move on to the next question page
                // tell them what was right and what was wrong.
                if (currentQuizPageNumber <= getServerConfiguration().getNumberOfQuizPages()) {
                    nextButton.setEnabled(true);
                }
                quizzesAnswered++;
                client.transmit(new QuizResponseEvent(client.getId(), currentQuizPageNumber, responses, incorrectAnswers));
                setInstructions(getQuizPage(), true);
            }
        };

    }

    public void displayContributionInformation(final ClientData clientData) {
    	GroupDataModel groupDataModel = clientData.getGroupDataModel();
        int totalContributedTokens = groupDataModel.getTotalContributedTokens();
        final StringBuilder builder = new StringBuilder();
        builder.append("<ul><li>Infrastructure efficiency before investment: ")
        	.append(groupDataModel.getInfrastructureEfficiencyBeforeInvestment())
        	.append("%</li>");
        builder.append("<li>Water delivery capacity before investment: ").append(groupDataModel.getIrrigationCapacityBeforeInvestment()).append(" cubic feet per second</li>");
        builder.append(
        		String.format(
        				"<li>Total group investment: %d tokens, increasing the infrastructure efficiency to %d%%</li>", 
        				totalContributedTokens, groupDataModel.getInfrastructureEfficiency()));
        if (groupDataModel.getIrrigationCapacity() > groupDataModel.getIrrigationCapacityBeforeInvestment()) {
        	builder.append("<li><b>Your group's investment has increased the water delivery capacity to ");
        }
        else {
        	builder.append("<li>Your group's investment was not enough to increase the water delivery capacity.  Your group's water delivery capacity is still ");
        }
        builder.append(groupDataModel.getIrrigationCapacity()).append(" cubic feet of water per second.</li><li>The amount of water available to pass through your irrigation canal is ")
            .append(groupDataModel.getActualWaterDeliveryCapacity()).append(" cubic feet per second</li>");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                contributionInformationEditorPane.setText(builder.toString());
                infrastructureEfficiencyChartPanel.initialize();
                pieChart.initialize(clientData);
                addCenterComponent(getContributionInformationPanel());
            }
        });
        irrigationGamePanel.setClientDataModel(clientDataModel);
    }
    
    public JPanel getContributionInformationPanel() {
        if (contributionInformationPanel == null) {
            contributionInformationPanel = new JPanel();
            contributionInformationPanel.setName("Graph panel");
            contributionInformationPanel.setLayout(new BoxLayout(contributionInformationPanel, BoxLayout.Y_AXIS));
            contributionInformationPanel.add(createPieChartPanel());
            contributionInformationPanel.add(Box.createVerticalStrut(15));
            contributionInformationEditorPane = new JEditorPane();
            contributionInformationEditorPane.setContentType("text/html");
            contributionInformationEditorPane.setEditorKit(new HTMLEditorKit());
            contributionInformationEditorPane.setEditable(false);
            contributionInformationEditorPane.setBackground(Color.WHITE);
            contributionInformationPanel.add(contributionInformationEditorPane);
        }
        return contributionInformationPanel;
    }

    private JPanel createPieChartPanel() {
        JPanel panel = new JPanel();
        infrastructureEfficiencyChartPanel = new InfrastructureEfficiencyChartPanel(client);
        pieChart = new TokenInvestmentPieChartPanel();
        GridLayout gridLayout = new GridLayout();
        gridLayout.setRows(1);
        gridLayout.setColumns(2);
        panel.setLayout(gridLayout);
        panel.add(infrastructureEfficiencyChartPanel);
        panel.add(pieChart);
        return panel;
    }


    public void showTokenInvestmentScreen() {
        Runnable runnable = new Runnable() {
            public void run() {
                GroupDataModel group = clientDataModel.getGroupDataModel();
                RoundConfiguration roundConfiguration = clientDataModel.getRoundConfiguration();
                int infrastructureEfficiency = 0;
                if (roundConfiguration.shouldResetInfrastructureEfficiency()) {
                    infrastructureEfficiency = roundConfiguration.getInitialInfrastructureEfficiency();
                }
                else {
                    infrastructureEfficiency = Math.max(0, group.getInfrastructureEfficiency() - roundConfiguration.getInfrastructureDegradationFactor());
                }
                addCenterComponent(getTokenInvestmentPanel());
                StringBuilder builder = new StringBuilder(
                        String.format(
                                "<h2>Current infrastructure efficiency: %d%%</h2>" 
                        + "<h2>Current water delivery capacity: %d cubic feet per second</h2>" 
                        + "<h2>Available water supply: %d cubic feet per second</h2>",
                        infrastructureEfficiency,
                        group.calculateIrrigationCapacity(infrastructureEfficiency),
                        roundConfiguration.getWaterSupplyCapacity()
                                ));
                builder.append(getServerConfiguration().getInvestmentInstructions());
                tokenInstructionsEditorPane.setText(builder.toString());
                getInvestedTokensTextField().requestFocusInWindow();
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    public void updateRoundInstructions(RoundConfiguration roundConfiguration) {
        if (! roundConfiguration.isFirstRound()) {
            instructionsBuilder.append(roundConfiguration.getInstructions());
            instructionsBuilder.append("<hr>");
            if (roundConfiguration.shouldResetInfrastructureEfficiency()) {
                int initialInfrastructureEfficiency = roundConfiguration.getInitialInfrastructureEfficiency();
            	instructionsBuilder.append(
            	        String.format("The irrigation infrastructure efficiency has been reset to %d%% with a corresponding water delivery capacity of %d cfps.", 
            	                initialInfrastructureEfficiency,
            	                clientDataModel.getGroupDataModel().calculateIrrigationCapacity(initialInfrastructureEfficiency)));
            	                
            }
            else {
                int initialInfrastructureEfficiency = clientDataModel.getGroupDataModel().getInfrastructureEfficiency();
                int degradationFactor = roundConfiguration.getInfrastructureDegradationFactor();
                int actualInfrastructureEfficiency = initialInfrastructureEfficiency - degradationFactor;
            	instructionsBuilder.append(
            			String.format("<p>The irrigation infrastructure efficiency carried over from the previous round is %d%% but has declined by %d and is now %d%% (%d cfps) at the start of this round.  " +
            					"The <b>available water supply is %d cfps</b>.</p><br><hr>",
            					initialInfrastructureEfficiency,
            					degradationFactor,
            					actualInfrastructureEfficiency,
            					clientDataModel.getGroupDataModel().calculateIrrigationCapacity(actualInfrastructureEfficiency),
            					roundConfiguration.getWaterSupplyCapacity()
            					));
            }
            displayInstructions(instructionsBuilder.toString());
        }
    }

    private ChatPanel getChatPanel() {
        if (chatPanel == null) {
            chatPanel = new ChatPanel(client);
            chatPanel.setName("Chat panel");
        }
        return chatPanel;
    }

    public void initializeChatWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                startTimer(getServerConfiguration().getChatDuration() * 1000L);
//                ChatPanel chatPanel = getChatPanel();
                chatPanel.initialize(clientDataModel.getAllClientIdentifiers());
                addCenterComponent( chatPanel );
                chatPanel.setFocusInChatField();
            }
        });
    }

    private Timer timer;
    private void startTimer(final long waitTime) {
        final long endTime = waitTime + System.currentTimeMillis();
        if (timer == null) {
            timer = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    final long timeRemaining = endTime - System.currentTimeMillis();
                    if (timeRemaining < 0) {
                    	showTokenInvestmentScreen();
                        getInvestedTokensTextField().requestFocusInWindow();
//                        chatPanel.displayMessage("", "---- chat round ending ----");
                        timer.stop();
                        timer = null;
                    }
                    else {
                        chatPanel.setTimeLeft(timeRemaining);
                    }
                }
            });
            timer.start();
        }
    }
    
    public void showQuiz() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setInstructions(getQuizPage());
                getInstructionsPanel().add(getQuizNavigationPanel(), BorderLayout.PAGE_END);
                getInstructionsPanel().revalidate();
            }
        });
    }
    
    /**
     * Should only be invoked when the instructions navigation panel is done.  
     * How do we know when it's done?        
     */
    private void disableQuiz() {
        getInstructionsPanel().remove(getQuizNavigationPanel());
        getInstructionsPanel().revalidate();
    }
    
    public void showGameScreenshot() {
        displayInstructions(getServerConfiguration().getGameScreenshotInstructions());
    }

    /** 
     * Invoked when the show instructions button is pressed.
     */
    public void showInstructions() {
        displayInstructions(getServerConfiguration().getInitialInstructions());
    }
}
