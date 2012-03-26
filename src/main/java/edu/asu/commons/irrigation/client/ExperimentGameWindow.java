package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.conf.ServerConfiguration;
import edu.asu.commons.irrigation.events.EndRoundEvent;
import edu.asu.commons.irrigation.events.QuizResponseEvent;
import edu.asu.commons.irrigation.model.ClientData;
import edu.asu.commons.ui.HtmlEditorPane;
import edu.asu.commons.ui.HtmlEditorPane.FormActionEvent;
import edu.asu.commons.ui.UserInterfaceUtils;

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
    
    private GamePanel irrigationGamePanel;

    private StringBuilder instructionsBuilder = new StringBuilder();

    private JEditorPane contributionInformationEditorPane;

    private JPanel instructionsPanel;

    private JPanel submitTokenPanel;

    private HtmlEditorPane tokenInstructionsEditorPane;

    private TokenContributionChartPanel tokenContributionChartPanel;

//    private CanalAnimationPanel canalAnimationPanel;

    private CardLayout cardLayout;
    
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
        irrigationGamePanel = new GamePanel(client);
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
            tokenInstructionsEditorPane = UserInterfaceUtils.createInstructionsEditorPane();
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
            instructionsEditorPane = UserInterfaceUtils.createInstructionsEditorPane();
            instructionsEditorPane.setName("Instructions editor pane");
            // create a quiz listener and then initialize the instructions.
            instructionsEditorPane.setActionListener(createQuizListener(getServerConfiguration()));
        }
        return instructionsEditorPane;
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
                showDebriefing(event.isLastRound());
                addCenterComponent(getInstructionsPanel());
            }
        });
        info("ExperimentGameWindow finished cleanup, ending round completed.");
    }

    private void info(String message) {
        System.err.println(message);
    }
    
    public void showDebriefing(boolean showExitInstructions) {
        instructionsBuilder.delete(0, instructionsBuilder.length());
        instructionsBuilder.append(clientDataModel.getRoundConfiguration().generateClientDebriefing(clientDataModel, showExitInstructions));
        setInstructions(instructionsBuilder.toString());
    }
    
    private void setInstructions(String instructions) {
        instructionsEditorPane.setText(instructions);
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
                FormActionEvent formEvent = (FormActionEvent) e;
                Properties actualAnswers = formEvent.getData();
                System.err.println("actual answers: " + actualAnswers);
                List<String> incorrectQuestionNumbers = new ArrayList<String>();
                List<String> correctQuestionNumbers = new ArrayList<String>();
                List<String> missingQuestions = new ArrayList<String>();
                for (Map.Entry<String, String> entry : quizAnswers.entrySet()) {
                    String questionNumber = entry.getKey();
                    String number = questionNumber.substring(1);
                    String correctAnswer = entry.getValue();
                    String actualAnswer = actualAnswers.getProperty(questionNumber);
                    if (actualAnswer == null || actualAnswer.trim().isEmpty()) {
                    	missingQuestions.add(number);
                    	continue;
                    }
                    ((correctAnswer.equals(actualAnswer)) ? correctQuestionNumbers : incorrectQuestionNumbers).add(questionNumber); 
                }
                int numberOfMissingQuestions = missingQuestions.size();
                if (numberOfMissingQuestions > 0) {
                	Collections.sort(missingQuestions);
                	JOptionPane.showMessageDialog(ExperimentGameWindow.this, "Please enter a quiz answer for questions " + missingQuestions);
                	return;
                }
                else if (numberOfMissingQuestions == 1) {
                	JOptionPane.showMessageDialog(ExperimentGameWindow.this, "Please enter a quiz answer for question " + missingQuestions.get(0));
                	return;
                }
                setQuestionColors(correctQuestionNumbers, "blue");
                setQuestionColors(incorrectQuestionNumbers, "red");
                QuizResponseEvent event = new QuizResponseEvent(client.getId(), actualAnswers, incorrectQuestionNumbers);
                System.err.println("Correct answers: " + event.getNumberOfCorrectQuizAnswers());
                clientDataModel.getClientData().addCorrectQuizAnswers(event.getNumberOfCorrectQuizAnswers());
                client.transmit(event);
                setInstructions(getServerConfiguration().getQuizResults(incorrectQuestionNumbers, actualAnswers));
            }
        };

    }
    
    private void setQuestionColors(List<String> questionNumbers, String color) {
        HTMLEditorKit editorKit = (HTMLEditorKit) instructionsEditorPane.getEditorKit();
        StyleSheet styleSheet = editorKit.getStyleSheet();
        for (String questionNumber : questionNumbers) {
            String styleString = String.format(".%s { color: %s; }", questionNumber, color);
            styleSheet.addRule(styleString);
        }
    }

    public void displayContributionInformation(final ClientData clientData) {
    	final RoundConfiguration configuration = clientDataModel.getRoundConfiguration();
    	final String contributionSummary = configuration.generateContributionSummary(clientData);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                contributionInformationEditorPane.setText(contributionSummary);
                infrastructureEfficiencyChartPanel.initialize();
                tokenContributionChartPanel.initialize(clientDataModel);
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
            contributionInformationPanel.add(createTokenContributionChartPanel());
            contributionInformationPanel.add(Box.createVerticalStrut(15));
            contributionInformationEditorPane = UserInterfaceUtils.createInstructionsEditorPane();
            contributionInformationPanel.add(contributionInformationEditorPane);
        }
        return contributionInformationPanel;
    }

    private JPanel createTokenContributionChartPanel() {
        JPanel panel = new JPanel();
        infrastructureEfficiencyChartPanel = new InfrastructureEfficiencyChartPanel(client);
        tokenContributionChartPanel = new TokenContributionChartPanel();
        GridLayout gridLayout = new GridLayout();
        gridLayout.setRows(1);
        gridLayout.setColumns(2);
        panel.setLayout(gridLayout);
        panel.add(infrastructureEfficiencyChartPanel);
        panel.add(tokenContributionChartPanel);
        return panel;
    }


    public void showTokenInvestmentScreen() {
        Runnable runnable = new Runnable() {
            public void run() {
                RoundConfiguration roundConfiguration = clientDataModel.getRoundConfiguration();
                tokenInstructionsEditorPane.setText(roundConfiguration.generateInvestmentInstructions(clientDataModel));
                addCenterComponent(getTokenInvestmentPanel());
                getInvestedTokensTextField().requestFocusInWindow();
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    public void updateRoundInstructions() {
        RoundConfiguration roundConfiguration = clientDataModel.getRoundConfiguration();
        if (! roundConfiguration.isFirstRound()) {
            instructionsBuilder.append(roundConfiguration.generateUpdatedInstructions(clientDataModel));
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
                setInstructions(getServerConfiguration().getQuizInstructions());
                getInstructionsPanel().revalidate();
            }
        });
    }
    
    public void showGameScreenshot() {
        displayInstructions(getServerConfiguration().getGameScreenshotInstructions());
    }

    /** 
     * Invoked when the show instructions button is pressed.
     */
    public void showInstructions() {
    	if (clientDataModel == null || clientDataModel.getRoundConfiguration().isFirstRound()) {
            displayInstructions(getServerConfiguration().getInitialInstructions());    		
    	}
    	else {
    		displayInstructions(clientDataModel.getRoundConfiguration().getInstructions());
    	}
    }
}
