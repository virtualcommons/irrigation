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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.irrigation.events.EndRoundEvent;
import edu.asu.commons.irrigation.events.QuizCompletedEvent;
import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.util.HtmlEditorPane;
import edu.asu.commons.util.HtmlEditorPane.FormActionEvent;

/**
 * $Id$
 * 
 * The root experiment window placed in the client's JFrame.  
 * 
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

    private JPanel investTokensPanel = null;

    private JPanel graphPanel;

    private JTextField investedTokensTextField;

    private JButton submitTokensButton;

    private InfrastructureEfficiencyChartPanel infrastructureEfficiencyChartPanel = null;

    private IrrigationClient client;
    
//    private MainIrrigationGameWindow mainIrrigationGameWindow;

    private MainIrrigationGameWindow irrigationGamePanel;

    private StringBuilder instructionsBuilder = new StringBuilder();

    private JTextArea contributionInformationTextArea;

    private JButton nextButton;

    private JPanel instructionsNavigationPanel;

    private JButton previousButton;

    private JPanel instructionsPanel;

    private int instructionNumber = 1;

    private JPanel submitTokenPanel;

    private HtmlEditorPane tokenInstructionsEditorPane;

    private JScrollPane tokenInstructionsScrollPane;

    private JLabel quizMessageLabel;

    private int pagesTraversed = 0;

    private JPanel pieChartPanel;

    private TokenInvestmentPieChartPanel pieChart;

    private CanalAnimationPanel canalAnimationPanel;

    private CardLayout cardLayout;
    
    private int numberOfGeneralInstructionPages;

    public ExperimentGameWindow(IrrigationClient client) {
        this.client = client;
        this.clientDataModel = client.getClientDataModel();
        this.numberOfGeneralInstructionPages = getServerConfiguration().getNumberOfGeneralInstructionPages();
    }
    
    void initialize() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        setInstructions(getGeneralInstructions(0));

        addToCardLayout(getInstructionsPanel());
//        irrigationGamePanel = new IrrigationGamePanel(client);
        irrigationGamePanel = new MainIrrigationGameWindow(client);
        addToCardLayout(irrigationGamePanel);
        addToCardLayout(getInvestTokensPanel());
        addToCardLayout(getChatPanel());
        addToCardLayout(getGraphPanel());
    }
    
    private void addToCardLayout(Component component) {
        add(component, component.getName());
    }

    private JPanel getInvestTokensPanel() {
        if (investTokensPanel == null) {
            investTokensPanel = new JPanel();
            investTokensPanel.setName("Invest tokens panel");
            investTokensPanel.setLayout(new BorderLayout());
            tokenInstructionsEditorPane = createInstructionsEditorPane();
            tokenInstructionsScrollPane = new JScrollPane(tokenInstructionsEditorPane);
            investTokensPanel.add(tokenInstructionsScrollPane, BorderLayout.CENTER);
            tokenInstructionsEditorPane.setCaretPosition(0);
            tokenInstructionsEditorPane.repaint();
            investTokensPanel.add(getSubmitTokenPanel(), BorderLayout.SOUTH);
            investTokensPanel.setBackground(Color.WHITE);
        }
        return investTokensPanel;
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
            instructionsPanel.add(getInstructionsNavigationPanel(), BorderLayout.PAGE_END);
        }
        return instructionsPanel;
    }
    
    private JPanel getInstructionsNavigationPanel() {
        if (instructionsNavigationPanel == null) {
            instructionsNavigationPanel = new JPanel();
            instructionsNavigationPanel.setLayout(new BorderLayout());
            instructionsNavigationPanel.add(getPreviousButton(), BorderLayout.LINE_START);
            instructionsNavigationPanel.add(getNextButton(), BorderLayout.LINE_END);
            // displays quiz messages (correct/incorrect answer).
            quizMessageLabel = new JLabel();
            quizMessageLabel.setHorizontalAlignment(JLabel.CENTER);
            instructionsNavigationPanel.add(quizMessageLabel, BorderLayout.CENTER);
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
                    quizMessageLabel.setText("");
                    // getting the next instruction Number
                    if (instructionNumber > 1) {
                        instructionNumber--;
                    }
                    previousButton.setEnabled(instructionNumber > 1);
                    nextButton.setEnabled(true);
                    setInstructions(getGeneralInstructions(instructionNumber,pagesTraversed));
                    // FIXME: get rid of hardcoded animation on page 5.  Should instead
                    // just be an animated gif or something more like 
                    // if instructions.hasAnimation()
                    if (instructionNumber == 5) {
                        getInstructionsPanel().add(getCanalAnimationPanel(), BorderLayout.PAGE_START);
                    }
                    else {
                        
                        getInstructionsPanel().remove(getCanalAnimationPanel());
                    }
                    getInstructionsPanel().revalidate();
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
                    quizMessageLabel.setText("");
                    previousButton.setEnabled(true);
                    if (instructionNumber >= pagesTraversed){
                        nextButton.setEnabled(false);
                    }
                    if (instructionNumber < numberOfGeneralInstructionPages) {
                        instructionNumber++;
                        setInstructions(getGeneralInstructions(instructionNumber,pagesTraversed));
                    }
                    if(instructionNumber == 5) {
                        getInstructionsPanel().add(getCanalAnimationPanel(), BorderLayout.PAGE_START);
                    }
                    else {
                        getInstructionsPanel().remove(getCanalAnimationPanel());
                    }
                    getInstructionsPanel().revalidate();
                }
            });

        }
        return nextButton;
    }

    private CanalAnimationPanel getCanalAnimationPanel() {
        if (canalAnimationPanel == null) {
            canalAnimationPanel = new CanalAnimationPanel(40);
        }
        return canalAnimationPanel;
    }

    /**
     * This returns the string of general instructions from the irrigation.xml file
     * where the general instructions are stored
     * @param pagesTraversed 
     * 
     * @return
     */
    private String getGeneralInstructions(int pageNumber, int pagesTraversed) {
        return getServerConfiguration().getGeneralInstructions(pageNumber, pagesTraversed, clientDataModel.getPriority());
    }

    private String getGeneralInstructions(int pageNumber) {
        return getServerConfiguration().getGeneralInstructions(pageNumber);
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
        repaint();
    }

    public void startRound(final RoundConfiguration configuration) {
        Runnable runnable = new Runnable() {
            public void run() {
                getCanalAnimationPanel().stopTimer();
                disableInstructions();
                addCenterComponent(irrigationGamePanel);
                irrigationGamePanel.startRound();
            }
        };
        SwingUtilities.invokeLater(runnable);
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
                getInstructionsEditorPane().setText(instructions);
                getInstructionsEditorPane().setCaretPosition(0);
                getInstructionsScrollPane().revalidate();
            }
        });
    }

    private ActionListener createQuizListener(final ServerConfiguration configuration) {
        return new ActionListener() {
            private Map<String, String> quizAnswers = configuration.getQuizAnswers();
            public void actionPerformed(ActionEvent e){
                FormActionEvent formEvent = (FormActionEvent) e;
                Properties responses = formEvent.getData();
                List<String> incorrectAnswers = new ArrayList<String>();
                responses.list(System.err);
                for (Map.Entry<Object, Object> entry : responses.entrySet()) {
                    String questionNumber = (String) entry.getKey();
                    if (questionNumber.charAt(0) == 'q') {
                        String response = (String) entry.getValue();
                        String correctAnswer = quizAnswers.get(questionNumber);
                        if (! response.equals(correctAnswer)) {
                            incorrectAnswers.add(questionNumber);
                        }
                    }
                }
                printIncorrectAnswerList(incorrectAnswers);
                if (incorrectAnswers.isEmpty()) {
                    nextButton.setEnabled(true);
                    pagesTraversed++;
                    //System.out.println("quiz :instruction number : "+instructionNumber+" pages traversed"+pagesTraversed);	
                    setInstructions(getGeneralInstructions(instructionNumber,pagesTraversed));
                    addCenterComponent(getInstructionsPanel());
                    quizMessageLabel.setForeground(Color.BLUE);
                    quizMessageLabel.setText("You answered the question(s) correctly.  Please continue to the next page by clicking 'Next'");
                    // notify the server and also notify the participant.
                    //StringBuilder builder = new StringBuilder(configuration.getInstructions());
                    //builder.append("<br><b>Congratulations!</b> You have answered all questions correctly.");
                    //setInstructions(builder.toString());
                    client.transmit(new QuizCompletedEvent(client.getId(),instructionNumber));
                }
                else {
                    nextButton.setEnabled(false);
                    quizMessageLabel.setForeground(Color.RED);
                    quizMessageLabel.setText("You did not answer the question(s) correctly. Please try again.");
                }
            }

            private void printIncorrectAnswerList(List<String> incorrectAnswers) {
                System.out.println("Size of the incorrect answers is :"+incorrectAnswers.size());
                for(int i=0;i<incorrectAnswers.size();i++){
                    System.out.println("Incorrect Answers :"+incorrectAnswers.get(i));
                }
            }
        };

    }

    public void displayTokenContributions(final ClientData clientData) {
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
                addCenterComponent(getGraphPanel());
            }
        });
        irrigationGamePanel.setClientDataModel(clientDataModel);
    }
    
    public JPanel getGraphPanel() {
        if (graphPanel == null) {
            graphPanel = new JPanel();
            graphPanel.setName("Graph panel");
            graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.Y_AXIS));
            graphPanel.add(getPieChartPanel());
            graphPanel.add(Box.createVerticalStrut(15));
            contributionInformationTextArea = new JTextArea();
            contributionInformationTextArea.setEditable(false);

            graphPanel.add(contributionInformationTextArea);
        }
        return graphPanel;
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


    public void updateTokenInstructionsPanel() {
        Runnable runnable = new Runnable() {
            public void run() {
                GroupDataModel group = clientDataModel.getGroupDataModel();
                RoundConfiguration roundConfiguration = clientDataModel.getRoundConfiguration();
                int infrastructureEfficiency = 0;
                if (roundConfiguration.isPracticeRound() || roundConfiguration.isFirstRound()) {
                    infrastructureEfficiency = roundConfiguration.getInitialInfrastructureEfficiency();
                }
                else {
                    infrastructureEfficiency = group.getInfrastructureEfficiency() - roundConfiguration.getInfrastructureDegradationFactor();
                }
                addCenterComponent(getInvestTokensPanel());
                StringBuilder builder = new StringBuilder();
                builder.append(
                        String.format(
                        		"<h2>The current infrastructure efficiency is %d%%.  The irrigation capacity is %d cubic feet per second and the water supply is %d cubic feet per second.</h2>",
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
        info("Updating round instructions: " + roundConfiguration.getInstructions());
        // if this is the first round, show the general instructions.
        if (roundConfiguration.isFirstRound()) {

        }
        else {
            instructionsBuilder.append(roundConfiguration.getInstructions());
            instructionsBuilder.append("<hr/>");
            int irrigationCapacity = clientDataModel.getGroupDataModel().getIrrigationCapacity();
            int clientCapacity = roundConfiguration.getMaximumClientFlowCapacity();
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
        addCenterComponent(getInstructionsPanel());
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
                    	updateTokenInstructionsPanel();
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
    
    private void disableInstructions() {
        quizMessageLabel.setText("");
        getInstructionsPanel().remove(getInstructionsNavigationPanel());
    }

    public void enableInstructions() {
        setInstructions(getGeneralInstructions(1,pagesTraversed));
        addCenterComponent(getInstructionsPanel());
    }
}
