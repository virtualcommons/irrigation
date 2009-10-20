package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import edu.asu.commons.net.Identifier;
import edu.asu.commons.util.HtmlEditorPane;
import edu.asu.commons.util.HtmlEditorPane.FormActionEvent;

/**
 * $Id$
 * 
 * The root experiment window placed in the client's JFrame.  
 * 
 * FIXME: refactor this class.
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

    private ChartWindowPanelTokenBandwidth xySeriesDemo = null;

    private IrrigationClient client;

    private IrrigationGamePanel irrigationGamePanel;

    private StringBuilder instructionsBuilder = new StringBuilder();

    private int totalContributedTokensPerGroup;

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

    private PieChart pieChart;

    private CanalAnimationPanel canalAnimationPanel;

    private CardLayout cardLayout;
    
    private int numberOfGeneralInstructionPages;

    private JLabel infrastructureEfficiencyLabel = new JLabel("Current infrastructure efficiency: ");

    public ExperimentGameWindow(IrrigationClient client) {
        this.client = client;
        this.clientDataModel = client.getClientDataModel();
        this.numberOfGeneralInstructionPages = getServerConfiguration().getNumberOfGeneralInstructionPages();
    }
    
    void initialize(Dimension screenSize) {
        initGuiComponents(screenSize);
    }

    private void initGuiComponents(Dimension screenSize) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        instructionsScrollPane = new JScrollPane(getInstructionsEditorPane());
        instructionsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        instructionsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // create a quiz listener and then initialize the instructions.
        instructionsEditorPane.setActionListener(createQuizListener(getServerConfiguration()));
        instructionsEditorPane.setCaretPosition(0);
        setInstructions(getGeneralInstructions(0));

        addToCardLayout(getInstructionsPanel());
        irrigationGamePanel = new IrrigationGamePanel(screenSize, client);
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
            tokenInstructionsEditorPane.setText(getServerConfiguration().getInvestmentInstructions());
            tokenInstructionsEditorPane.setCaretPosition(0);
            tokenInstructionsEditorPane.repaint();
            investTokensPanel.add(getSubmitTokenPanel(), BorderLayout.SOUTH);
            investTokensPanel.add(infrastructureEfficiencyLabel, BorderLayout.NORTH);
            investTokensPanel.setBackground(Color.WHITE);
        }
        return investTokensPanel;
    }

    public void updateInfrastructureEfficiencyLabel() {
        GroupDataModel group = clientDataModel.getGroupDataModel();
        RoundConfiguration roundConfiguration = clientDataModel.getRoundConfiguration();
        int infrastructureEfficiency = 0;
        if (roundConfiguration.isPracticeRound() || roundConfiguration.isFirstRound()) {
            infrastructureEfficiency = roundConfiguration.getInitialInfrastructureEfficiency();
        }
        else {
            System.err.println("group was not null, efficiency is: " + group.getInfrastructureEfficiency() + " degrading by " + clientDataModel.getRoundConfiguration().getInfrastructureDegradationFactor());
            infrastructureEfficiency = group.getInfrastructureEfficiency() - roundConfiguration.getInfrastructureDegradationFactor();
        }
        infrastructureEfficiencyLabel.setText("Current infrastructure efficiency: " + infrastructureEfficiency);
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

    private JPanel getInstructionsPanel() {
        if (instructionsPanel == null) {
            instructionsPanel = new JPanel();
            instructionsPanel.setName("General instructions panel");
            instructionsPanel.setLayout(new BorderLayout());
            instructionsPanel.add(instructionsScrollPane, BorderLayout.CENTER);
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
                    // just be an animated gif or something like that.
                    // should be more like "if instructions.hasAnimation()"
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
//                    else {
//                        setInstructions(clientDataModel.getRoundConfiguration().getInstructions());
//                    }
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

    private JPanel getCanalAnimationPanel() {
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
                instructionsBuilder.append("\nPlease enter your tokens within the range 0 - 10");
                instructionsBuilder.append(getServerConfiguration().getInvestmentInstructions());
                tokenInstructionsEditorPane.setText(instructionsBuilder.toString());
            }
        }
        catch(NumberFormatException e){
            investedTokensTextField.setText("");
            instructionsBuilder.delete(0, instructionsBuilder.length());
            instructionsBuilder.append("\nYou only have between 0 and 10 to invest.  Please choose a number between 0 and 10 and try again.");
            instructionsBuilder.append(getServerConfiguration().getInvestmentInstructions());
            tokenInstructionsEditorPane.setText(instructionsBuilder.toString());
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    private HtmlEditorPane getInstructionsEditorPane() {
        if (instructionsEditorPane == null) {
            instructionsEditorPane = createInstructionsEditorPane();
            instructionsEditorPane.setName("Instructions editor pane");
        }
        return instructionsEditorPane;
    }
    
    private HtmlEditorPane createInstructionsEditorPane() {
        HtmlEditorPane htmlEditorPane = new HtmlEditorPane();
        htmlEditorPane.setEditable(false);
        htmlEditorPane.setFont(new Font("serif", Font.PLAIN, 12));
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
                addCenterComponent(irrigationGamePanel);
                requestFocusInWindow();
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    public void update() {
        irrigationGamePanel.updateClientStatus(clientDataModel);
    }

    /*
     * updates the mainIrrigationGameWindow Panel and adds
     * instructionsScrollPane with the debreifing information
     */
    public void updateEndRoundEvent(final EndRoundEvent event) {
        irrigationGamePanel.endRound();
        Runnable runnable = new Runnable() {
            public void run() {
                investedTokensTextField.setText("");
                addDebriefingText(event);
                // generate debriefing text from data culled from the Event
//                addCenterComponent(instructionsEditorPane);
                // FIXME: this is probably wrong.
                addCenterComponent(getInstructionsPanel());
            }
        };
        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        info("IrrigationGameWindow finished cleanup, ending round completed.");
    }

    private void info(String message) {
        System.err.println(message);
    }

    /**
     * FIXME: needs serious refactoring
     * @param event
     */
    private void addDebriefingText(EndRoundEvent event) {
        instructionsBuilder.delete(0, instructionsBuilder.length());

        Map<Identifier,ClientData> clientDataMap = event.getClientDataMap();

        String positionString = "Your position is: " + event.getClientData().getPriorityAsString();
        ClientData[] allClientData = new ClientData[clientDataMap.size()];
        for(ClientData clientData : clientDataMap.values()) {
            allClientData[clientData.getPriority()] = clientData;
        }
        ClientData clientData = event.getClientData();
        RoundConfiguration roundConfiguration = clientData.getRoundConfiguration();
        double dollarsPerToken = roundConfiguration.getDollarsPerToken();
        // FIXME: wow
        instructionsBuilder.append(String.format(
                positionString +
                "<table><thead><th></th><th></th><th>A</th><th></th><th>B</th><th></th><th>C</th><th></th><th>D</th><th></th><th>E</th></thead><tbody>"+
                "<tr><td>A Initial endowment</td><td></td><td>10</td><td></td><td>10</td><td></td><td>10</td><td></td><td>10</td><td></td><td>10</td></tr>"+
                "<tr><td>B Infrastructure investment</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td></tr>"+
                "<tr><td>C Kept endowment (A - B)</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td></tr>"+
                "<tr><td>D Return from growing crops</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td></tr>"+
                "<tr><td>E Total tokens earned in last round(D+C)</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td></tr>"+
                "<tr><td>F Total dollars earned in this round (E*0.1$)</td><td></td><td>%3.2f</td><td></td><td>%3.2f</td><td></td><td>%3.2f</td><td></td><td>%3.2f</td><td></td><td>%3.2f</td></tr>",
                allClientData[0].getInvestedTokens(),
                allClientData[1].getInvestedTokens(),
                allClientData[2].getInvestedTokens(),
                allClientData[3].getInvestedTokens(),
                allClientData[4].getInvestedTokens(),
                
                allClientData[0].getUninvestedTokens(),
                allClientData[1].getUninvestedTokens(),
                allClientData[2].getUninvestedTokens(),
                allClientData[3].getUninvestedTokens(),
                allClientData[4].getUninvestedTokens(),

                allClientData[0].getTotalTokensEarned() - allClientData[0].getUninvestedTokens(),
                allClientData[1].getTotalTokensEarned() - allClientData[1].getUninvestedTokens(),
                allClientData[2].getTotalTokensEarned() - allClientData[2].getUninvestedTokens(),
                allClientData[3].getTotalTokensEarned() - allClientData[3].getUninvestedTokens(),
                allClientData[4].getTotalTokensEarned() - allClientData[4].getUninvestedTokens(),

                allClientData[0].getTotalTokensEarned(),
                allClientData[1].getTotalTokensEarned(),
                allClientData[2].getTotalTokensEarned(),
                allClientData[3].getTotalTokensEarned(),
                allClientData[4].getTotalTokensEarned(),

                (float)dollarsPerToken*allClientData[0].getTotalTokensEarned(),
                (float)dollarsPerToken*allClientData[1].getTotalTokensEarned(),
                (float)dollarsPerToken*allClientData[2].getTotalTokensEarned(),
                (float)dollarsPerToken*allClientData[3].getTotalTokensEarned(),
                (float)dollarsPerToken*allClientData[4].getTotalTokensEarned()

                /*	
        		event.getClientData().getContributedTokens(),otherClientData.getContributedTokens(),
        		10 - event.getClientData().getContributedTokens(),10 - otherClientData.getContributedTokens(),
        		event.getClientData().getAward()-(10 - event.getClientData().getContributedTokens()),
        		otherClientData.getAward()-(10 - otherClientData.getContributedTokens()),
        		event.getClientData().getAward(),otherClientData.getAward(),
        		(float)event.getClientData().getRoundConfiguration().getDollarsPerToken()*event.getClientData().getAward(),
        		(float)otherClientData.getRoundConfiguration().getDollarsPerToken()*otherClientData.getAward()*/
        ));
        instructionsBuilder.append("</tbody></table><hr>");
        instructionsBuilder.append(String.format("Summary: You received $1.00 at the beginning of the round and had " +
                "$%3.2f at the end of the round. Your earnings in the experiments so far are $%3.2f plus the $5.00 " +
                "showup fee, for a grand total of $%3.2f",
                (float)dollarsPerToken*clientData.getTotalTokensEarned(),
                (float)dollarsPerToken*clientData.getTotalTokens(),
                (float)dollarsPerToken*clientData.getTotalTokens() + getServerConfiguration().getShowUpPayment()
        ));
        //append the added practice round instructions
        if(clientDataModel.getRoundConfiguration().isPracticeRound()) {
            instructionsBuilder.append(" However, this is a practice round and the earnings mentioned are only for illustrative purposes " +
            "and will not count towards your actual payments");
        }
        else if (event.isLastRound()) {
            instructionsBuilder.append(getServerConfiguration().getFinalInstructions());
        }
        setInstructions(instructionsBuilder.toString());
    }

    // adding the instructions into the instruction Panel
    private void setInstructions(final String instructions) {
//        System.err.println("Setting instructions: " + instructions);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                instructionsEditorPane.setText(instructions);
                instructionsEditorPane.setCaretPosition(0);
                instructionsScrollPane.revalidate();
            }
        });
    }

    private ActionListener createQuizListener(final ServerConfiguration configuration) {
        return new ActionListener() {
            private Map<String, String> quizAnswers = configuration.getQuizAnswers();
            public void actionPerformed(ActionEvent e){
                // System.err.println("In action performed with event: " + e);
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

    public void updateGraphDisplay(final ClientData clientData) {
        totalContributedTokensPerGroup = clientData.getGroupDataModel().getTotalContributedTokens();
        DecimalFormat df = new DecimalFormat("#.##");
        final String contributionInformation = 
            "Initial infrastructure efficiency: " + clientData.getGroupDataModel().getInitialInfrastructureEfficiency() + "%"
            + "\n\nInitial flow capacity: " + clientData.getGroupDataModel().getInitialFlowCapacity() + " cubic feet per second "
            + "\n\nActual infrastructure efficiency: " + clientData.getGroupDataModel().getInfrastructureEfficiency() + "%"
            + "\n\nActual total flow capacity: "
            + df.format(clientData.getGroupDataModel().getMaximumAvailableFlowCapacity()) + " cubic feet per second \n\n"
            + "\n\nMaximum tokens that could have been contributed: "
            + clientDataModel.getRoundConfiguration().getMaximumTotalInvestedTokens()
            + "\n\nTotal tokens contributed: "
            + totalContributedTokensPerGroup
            + "\n\nYour token contribution: "
            + clientData.getInvestedTokens()
            + "\n\nTotal flow capacity that could have been generated: "
            + clientDataModel.getRoundConfiguration().getMaximumCanalFlowCapacity()
            + " cubic feet per second";


        
        Runnable runnable = new Runnable() {
            public void run() {
                contributionInformationTextArea.setText(contributionInformation);
                pieChart.setClientData(clientData);
                addCenterComponent(getGraphPanel());
            }
        };

        SwingUtilities.invokeLater(runnable);
        irrigationGamePanel.updateContributions(clientDataModel);
    }
    
    public JPanel getGraphPanel() {
        if (graphPanel == null) {
            graphPanel = new JPanel();
            graphPanel.setName("Graph panel");
            GridLayout gridLayout = new GridLayout();
            gridLayout.setColumns(2);
            graphPanel.setLayout(gridLayout);
            graphPanel.add(getPieChartPanel(), null);
            contributionInformationTextArea = new JTextArea();
            contributionInformationTextArea.setEditable(false);
            graphPanel.add(contributionInformationTextArea,null);
        }
        return graphPanel;
    }

    private JPanel getPieChartPanel() {
        if (pieChartPanel == null) {
            pieChartPanel = new JPanel();
            xySeriesDemo = new ChartWindowPanelTokenBandwidth(client);
            xySeriesDemo.setVisible(true);
            pieChart = new PieChart();
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(2);
            gridLayout.setColumns(1);
            pieChartPanel.setLayout(gridLayout);
            pieChartPanel.add(xySeriesDemo);
            pieChartPanel.add(pieChart);
        }
        return pieChartPanel;
    }


    public void updateSubmitTokenScreenDisplay() {
        Runnable runnable = new Runnable() {
            public void run() {
                addCenterComponent(getInvestTokensPanel());
                updateInfrastructureEfficiencyLabel();
                getInvestedTokensTextField().requestFocusInWindow();
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    public void updateRoundInstructions(RoundConfiguration roundConfiguration) {
        System.err.println("Updating round instructions: " + roundConfiguration.getInstructions());
        // if this is the first round, show the general instructions.
        if (roundConfiguration.isFirstRound()) {
//            setInstructions(roundConfiguration.getParentConfiguration().getInitialInstructions());
        }
        else {
            instructionsBuilder.append(roundConfiguration.getInstructions());
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
                //canalPanelAnimation.stopTimer();
                startTimer(clientDataModel.getRoundConfiguration().getChatDuration() * 1000L);
                ChatPanel chatPanel = getChatPanel();
                chatPanel.initialize(clientDataModel.getAllClientIdentifiers());
                addCenterComponent( chatPanel );
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
                        sleep();
                        addCenterComponent(getInvestTokensPanel());
                        getInvestedTokensTextField().requestFocusInWindow();
                        timer.stop();
                        timer = null;
                    }
                    else {
                        chatPanel.setTimeRemaining(timeRemaining);
                    }
                }

                private void sleep() {
                    long prevTime = System.currentTimeMillis();
                    while((System.currentTimeMillis() - prevTime) < 5000) { 
                        //System.out.println("Prev "+prevTime);
                        //System.out.println("Current "+System.currentTimeMillis());
                        chatPanel.setEnabled(false);
                    }

                }
            });
            timer.start();
        }
    }

    public void enableInstructions() {
        setInstructions(getGeneralInstructions(1,pagesTraversed));
        addCenterComponent(getInstructionsPanel());

    }
}
