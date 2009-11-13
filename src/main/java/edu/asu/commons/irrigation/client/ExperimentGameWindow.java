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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

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

    private JScrollPane instructionsScrollPane;

    private HtmlEditorPane instructionsEditorPane;

    private JPanel tokenInvestmentPanel;

    private JPanel contributionInformationPanel;

    private JTextField investedTokensTextField;

    private JButton submitTokensButton;

    private InfrastructureEfficiencyChartPanel infrastructureEfficiencyChartPanel;

    private IrrigationClient client;
    
//    private MainIrrigationGameWindow mainIrrigationGameWindow;

    private MainIrrigationGameWindow irrigationGamePanel;

    private StringBuilder instructionsBuilder = new StringBuilder();

    private JTextArea contributionInformationTextArea;

    private JButton nextButton;

    private JPanel instructionsNavigationPanel;

    private JButton previousButton;

    private JPanel instructionsPanel;

    private int currentQuizPageNumber = 1;

    private JPanel submitTokenPanel;

    private HtmlEditorPane tokenInstructionsEditorPane;

    private JScrollPane tokenInstructionsScrollPane;

    private int quizzesAnswered = 0;

    private JPanel pieChartPanel;

    private TokenInvestmentPieChartPanel pieChart;

//    private CanalAnimationPanel canalAnimationPanel;

    private CardLayout cardLayout;
    
    private int numberOfQuestionPages;
    
    private Map<Integer, String> quizPageResponses = new HashMap<Integer, String>();


    public ExperimentGameWindow(IrrigationClient client) {
        this.client = client;
        this.clientDataModel = client.getClientDataModel();
        this.numberOfQuestionPages = getServerConfiguration().getNumberOfQuestionPages();
    }
    
    void initialize() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setInstructions(getServerConfiguration().getWelcomeInstructions());
        addToCardLayout(getInstructionsPanel());
//        irrigationGamePanel = new IrrigationGamePanel(client);
        irrigationGamePanel = new MainIrrigationGameWindow(client);
        addToCardLayout(irrigationGamePanel);
        addToCardLayout(getTokenInvestmentPanel());
        addToCardLayout(getChatPanel());
        addToCardLayout(getContributionInformationPanel());
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
            tokenInstructionsScrollPane = new JScrollPane(tokenInstructionsEditorPane);
            tokenInvestmentPanel.add(tokenInstructionsScrollPane, BorderLayout.CENTER);
            tokenInstructionsEditorPane.setCaretPosition(0);
            tokenInstructionsEditorPane.repaint();
            tokenInvestmentPanel.add(getSubmitTokenPanel(), BorderLayout.SOUTH);
            tokenInvestmentPanel.setBackground(Color.WHITE);
        }
        return tokenInvestmentPanel;
    }

    private JPanel getSubmitTokenPanel() {
        if (submitTokenPanel == null) {
            submitTokenPanel = new JPanel();
            submitTokenPanel.setLayout(new BorderLayout());
            submitTokenPanel.add(getInvestedTokensTextField(), BorderLayout.CENTER);
            submitTokenPanel.add(getSubmitTokensButton(), BorderLayout.EAST);
            return submitTokenPanel;
        }
        return submitTokenPanel;
    }
    
    private JScrollPane getInstructionsScrollPane() {
        if (instructionsScrollPane == null) {
            instructionsScrollPane = new JScrollPane(getInstructionsEditorPane());
            instructionsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            instructionsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }
        return instructionsScrollPane;
    }

    private JPanel getInstructionsPanel() {
        if (instructionsPanel == null) {
            instructionsPanel = new JPanel();
            instructionsPanel.setName("Instructions panel");
            instructionsPanel.setLayout(new BorderLayout());
            instructionsPanel.add(getInstructionsScrollPane(), BorderLayout.CENTER);
            instructionsPanel.add(getQuizNavigationPanel(), BorderLayout.PAGE_END);
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
                    if (currentQuizPageNumber >= quizzesAnswered){
                        nextButton.setEnabled(false);
                    }
                    if (currentQuizPageNumber < numberOfQuestionPages) {
                        currentQuizPageNumber++;
                        setInstructions(getQuizPage());
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

    private JButton getSubmitTokensButton() {
        if (submitTokensButton == null) {
            submitTokensButton = new JButton();
            submitTokensButton.setText("Invest");
            submitTokensButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    submitInvestedTokens();
                }
            });
        }
        return submitTokensButton;
    }

    private void submitInvestedTokens() {
        try {
            int token = Integer.parseInt(investedTokensTextField.getText());
            // validating token range
            if (token >= 0 && token <= 10) {
                client.transmitInvestedTokensEvent(token);
                setInstructions("Please wait while the server computes your total flow capacity based on your group's total token contribution investment.");
                addCenterComponent(getInstructionsPanel());
            } 
            else {
                investedTokensTextField.setText("");
                instructionsBuilder.delete(0, instructionsBuilder.length());
                instructionsBuilder.append("<h3>Please enter your tokens within the range 0 - 10</h3>");
                instructionsBuilder.append(getServerConfiguration().getInvestmentInstructions());
                tokenInstructionsEditorPane.setText(instructionsBuilder.toString());
            }
        }
        catch(NumberFormatException e){
            investedTokensTextField.setText("");
            instructionsBuilder.delete(0, instructionsBuilder.length());
            instructionsBuilder.append("<h3>You only have between 0 and 10 to invest.  Please choose a number between 0 and 10 and try again.</h3>");
            instructionsBuilder.append(getServerConfiguration().getInvestmentInstructions());
            tokenInstructionsEditorPane.setText(instructionsBuilder.toString());
        }
    }

    private HtmlEditorPane getInstructionsEditorPane() {
        if (instructionsEditorPane == null) {
            instructionsEditorPane = createInstructionsEditorPane();
            instructionsEditorPane.setName("Instructions editor pane");
            // create a quiz listener and then initialize the instructions.
            instructionsEditorPane.setActionListener(createQuizListener(getServerConfiguration()));
            instructionsEditorPane.setCaretPosition(0);
            instructionsEditorPane.setBackground(Color.WHITE);
        }
        return instructionsEditorPane;
    }
    
    private HtmlEditorPane createInstructionsEditorPane() {
        HtmlEditorPane htmlEditorPane = new HtmlEditorPane();
        htmlEditorPane.setEditable(false);
        htmlEditorPane.setFont(new Font("sansserif", Font.TRUETYPE_FONT, 14));
        return htmlEditorPane;

    }

    private void addCenterComponent(Component newCenterComponent) {
        cardLayout.show(this, newCenterComponent.getName());
        revalidate();
    }

    public void startRound(final RoundConfiguration configuration) {
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
        instructionsBuilder.delete(0, instructionsBuilder.length());
        instructionsBuilder.append("<b>You are at position " + clientDataModel.getPriorityString());
        instructionsBuilder.append(
                "<table border='3' cellpadding='5'><thead><th>Position</th><th>Initial token endowment</th><th>Tokens invested</th><th>Tokens not invested</th>" +
                "<th>Tokens earned from growing crops</th><th>Total tokens earned during this round</th>" +
                "<th>Dollars earned during this round</th><th>Total dollars earned (including show-up bonus)</th></thead>" +
                "<tbody>");
        double showUpBonus = clientDataModel.getServerConfiguration().getShowUpPayment();
        RoundConfiguration roundConfiguration = clientDataModel.getRoundConfiguration();
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
        					clientData.getTotalDollarsEarned() + showUpBonus
        					));
        }
        
        ClientData clientData = clientDataModel.getClientData();
        instructionsBuilder.append("</tbody></table><hr/>");
        instructionsBuilder.append(String.format("<h3>You received $%3.2f this past round.  Your total earnings are $%3.2f, including the $%3.2f show up bonus.</h3>",
        		clientData.getTotalDollarsEarnedThisRound(), clientData.getTotalDollarsEarned()+showUpBonus, showUpBonus));
        //append the added practice round instructions
        
        if (roundConfiguration.isPracticeRound() || roundConfiguration.isSecondPracticeRound()) {
            instructionsBuilder.append(roundConfiguration.getPracticeRoundPaymentInstructions());
        }
        else if (event.isLastRound()) {
            instructionsBuilder.append(getServerConfiguration().getFinalInstructions());
        }
        instructionsBuilder.append("<hr/>");
        setInstructions(instructionsBuilder.toString());
    }

    // adding the instructions into the instruction Panel
    private void setInstructions(final String instructions) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                instructionsEditorPane.setText(instructions);
                instructionsEditorPane.setCaretPosition(0);
                addCenterComponent(getInstructionsPanel());
                getInstructionsScrollPane().revalidate();
            }
        });
    }
    
    private ActionListener createQuizListener(final ServerConfiguration configuration) {
        return new ActionListener() {
            private Map<String, String> quizAnswers = configuration.getQuizAnswers();
            public synchronized void actionPerformed(ActionEvent e) {
                if (quizPageResponses.containsKey(currentQuizPageNumber)) {
                    // this form has already been submit.
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
                builder.append("<hr/><h2>Results</h2>");
                for (Map.Entry<String, String> entry : sortedResponses.entrySet()) {
                    String questionNumber = (String) entry.getKey();
                    if (questionNumber.charAt(0) == 'q') {
                        String number = questionNumber.substring(1, questionNumber.length());
                        String response = (String) entry.getValue();
                        String correctAnswer = quizAnswers.get(questionNumber);
                        builder.append(String.format("<p><b>Question %s</b><br/>", number));
                        String color = "blue";
                        if (! response.equals(correctAnswer)) {
                            incorrectAnswers.add(questionNumber);
                            color = "red";
                        }
                        builder.append(String.format("Your answer: <font color='%s'>%s</font><br/>", color, response));
                        builder.append(String.format("Correct answer: %s<br/>", correctAnswer));
                        builder.append(quizAnswers.get( "qDescriptiveAnswer" + number )).append("</p>");
                    }
                }
                quizPageResponses.put(currentQuizPageNumber, builder.toString());
                // no matter what we move on to the next question page
                // tell them what was right and what was wrong.
                if (currentQuizPageNumber < numberOfQuestionPages) {
                    nextButton.setEnabled(true);
                }
                quizzesAnswered++;
                client.transmit(new QuizResponseEvent(client.getId(), currentQuizPageNumber, responses, incorrectAnswers));
                setInstructions(getQuizPage());
            }
        };

    }

    public void displayContributionInformation(final ClientData clientData) {
    	GroupDataModel groupDataModel = clientData.getGroupDataModel();
        int totalContributedTokens = groupDataModel.getTotalContributedTokens();
        final StringBuilder builder = new StringBuilder();
        builder.append("Infrastructure efficiency before investment: ")
        	.append(groupDataModel.getInfrastructureEfficiencyBeforeInvestment())
        	.append("%\n");
        builder.append("Irrigation capacity before investment: ").append(groupDataModel.getIrrigationCapacityBeforeInvestment()).append(" cubic feet per second\n\n");
        builder.append(
        		String.format(
        				"Your group invested a total of %d tokens, increasing the infrastructure efficiency to %d%%.\n", 
        				totalContributedTokens, groupDataModel.getInfrastructureEfficiency()));
        if (groupDataModel.getIrrigationCapacity() > groupDataModel.getIrrigationCapacityBeforeInvestment()) {
        	builder.append("Your group's investment has increased the irrigation capacity to ");
        }
        else {
        	builder.append("Your group's investment was not enough to increase the irrigation capacity.  Your group's irrigation capacity is still ");
        }
        builder.append(groupDataModel.getIrrigationCapacity()).append(" cubic feet of water per second.  The amount of water available to pass through your irrigation canal is ")
            .append(groupDataModel.getActualFlowCapacity());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                contributionInformationTextArea.setText(builder.toString());
                infrastructureEfficiencyChartPanel.initialize();
                pieChart.setClientData(clientData);
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
            contributionInformationPanel.add(getPieChartPanel());
            contributionInformationPanel.add(Box.createVerticalStrut(15));
            contributionInformationTextArea = new JTextArea();
            contributionInformationTextArea.setEditable(false);
            contributionInformationPanel.add(contributionInformationTextArea);
        }
        return contributionInformationPanel;
    }

    private JPanel getPieChartPanel() {
        if (pieChartPanel == null) {
            pieChartPanel = new JPanel();
            infrastructureEfficiencyChartPanel = new InfrastructureEfficiencyChartPanel(client);
            pieChart = new TokenInvestmentPieChartPanel();
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setColumns(2);
            pieChartPanel.setLayout(gridLayout);
            pieChartPanel.add(infrastructureEfficiencyChartPanel);
            pieChartPanel.add(pieChart);
        }
        return pieChartPanel;
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
                    infrastructureEfficiency = group.getInfrastructureEfficiency() - roundConfiguration.getInfrastructureDegradationFactor();
                }
                addCenterComponent(getTokenInvestmentPanel());
                StringBuilder builder = new StringBuilder();
                builder.append(
                        String.format(
                        		"<h2>Current infrastructure efficiency: %d%%</h2>" +
                        		"<h2>Current irrigation capacity: %d cubic feet per second</h2>" +
                        		"<h2>Available water supply: %d cubic feet per second</h2>",
                                infrastructureEfficiency,
                                group.calculateFlowCapacity(infrastructureEfficiency),
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
            instructionsBuilder.append("<hr/>");
            int irrigationCapacity = clientDataModel.getGroupDataModel().getIrrigationCapacity();
//            int clientCapacity = roundConfiguration.getMaximumClientFlowCapacity();
            if (roundConfiguration.shouldResetInfrastructureEfficiency()) {
            	instructionsBuilder.append("The irrigation infrastructure efficiency is currently 75%.");
            }
            else {
            	instructionsBuilder.append(
            			String.format("<p>The current infrastructure efficiency is %d%% but will be degraded by %d%% during this round." +
            					"The current irrigation capacity is %d cfps and the available water supply is %d cfps.</p><hr/>",
            					clientDataModel.getGroupDataModel().getInfrastructureEfficiency(),
            					roundConfiguration.getInfrastructureDegradationFactor(),
            					irrigationCapacity,
            					roundConfiguration.getWaterSupplyCapacity()
            					));
            }
            setInstructions(instructionsBuilder.toString());
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
                ChatPanel chatPanel = getChatPanel();
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
                getInstructionsPanel().add(getQuizNavigationPanel(), BorderLayout.PAGE_END);
                getInstructionsPanel().revalidate();
            }
        });
        setInstructions(getQuizPage());
    }
    
    /**
     * Should only be invoked when the instructions navigation panel is done.  
     * How do we know when it's done?  When the user adds a new      
     */
    public void disableQuiz() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getInstructionsPanel().remove(getQuizNavigationPanel());
                getInstructionsPanel().revalidate();
            }
        });
    }
    
    public void showGameScreenshot() {
        setInstructions(getServerConfiguration().getGameScreenshotInstructions());
    }

    /** 
     * Invoked when the show instructions button is pressed.
     */
    public void showInstructions() {
        setInstructions(getServerConfiguration().getInitialInstructions());
    }
}
