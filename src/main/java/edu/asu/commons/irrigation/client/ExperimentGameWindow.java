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
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class ExperimentGameWindow extends JPanel {

    private static final long serialVersionUID = -5636795631355367711L;

    private ClientDataModel clientDataModel;

    private JScrollPane instructionsScrollPane;

    private HtmlEditorPane instructionsEditorPane;

    private JPanel tokenScreenPanel = null;

    private JPanel graphPanel;

    private JTextField enterTokens;

    private JButton submitTokensButton;

    private ChartWindowPanelTokenBandwidth xySeriesDemo = null;

    private IrrigationClient client;

    private IrrigationGamePanel irrigationGamePanel;

    private StringBuilder instructionsBuilder = new StringBuilder();

    private int totalContributedTokensPerGroup;

    private JTextArea contributionStatusInformationjTextArea;

    private JButton nextButton;

    private JPanel instructionsNavigationPanel;

    private JButton previousButton;

    private JPanel instructionsPanel;

    private int instructionNumber = 1;

    private JPanel submitTokenPanel;

    private HtmlEditorPane tokenInstructionsEditorPane;

    private JScrollPane tokenInstructionsScrollPane;

    private int NUMBER_INSTRUCTIONS = 9;

    private JLabel quizLabel;

    private JLabel quizAnimationLabel = new JLabel();

    private int pagesTraversed = 0;

    private JPanel animationInstructionPanel;

    private JPanel animationPanel;

    private JPanel navigationAnimationPanel;

    private JButton nextAnimationButton;

    private JButton previousAnimationButton;

    private HtmlEditorPane animationInstructionsEditorPane;

    private JScrollPane animationInstructionsScrollPane;

    private JPanel chartPanel;

    private PieChart pieChart;

    public ClientData clientData;

    private CanalAnimationPanel canalAnimationPanel;

    private CardLayout cardLayout;

    private JLabel infrastructureEfficiencyLabel = new JLabel("Current infrastructure efficiency: ");

    public ExperimentGameWindow(Dimension screenSize, IrrigationClient client) {
        this.client = client;
        this.clientDataModel = client.getClientDataModel();
        // FIXME: set the actual screen size dimensions after this JPanel has
        // been initialized...
        // this.screenSize = getParent().getSize();
        // feed subject view the available screen size so that
        // it can adjust appropriately when given a board size
        initGuiComponents(screenSize);
    }

    private void initGuiComponents(Dimension screenSize) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        instructionsScrollPane = new JScrollPane(getInstructionsEditorPane());
        instructionsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        instructionsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // create a quiz listener and then initialize the instructions.
        instructionsEditorPane.setActionListener(createQuizListener(client.getServerConfiguration()));
        instructionsEditorPane.setCaretPosition(0);
        setInstructions(getGeneralInstructions(0));

        add(getInstructionsPanel(), getInstructionsPanel().getName());
        irrigationGamePanel = new IrrigationGamePanel(screenSize, client);
        add(irrigationGamePanel, irrigationGamePanel.getName());
        // add any other panels that need to be switched out.
        // FIXME: see if we can simplify the number of instructions panes that Sanket has created.
        add(getInstructionsEditorPane());
    }


    private JPanel getTokenScreenPanel() {
        if (tokenScreenPanel == null) {
            tokenScreenPanel = new JPanel();
            tokenScreenPanel.setLayout(new BorderLayout());
            tokenInstructionsEditorPane = createInstructionsEditorPane();
            tokenInstructionsScrollPane = new JScrollPane(tokenInstructionsEditorPane);
            tokenScreenPanel.add(tokenInstructionsScrollPane, BorderLayout.CENTER);
            //setInstructions(getGeneralInstructions(11));
            //            StringBuilder tokenSubmissionInstructionsBuilder = new StringBuilder("<h3>The current infrastructure efficiency is: ");
            //            
            //            tokenSubmissionInstructionsBuilder.append("% </h3>");
            //            tokenSubmissionInstructionsBuilder.append(
            StringBuilder tokenSubmissionInstructionsBuilder = new StringBuilder(getGeneralInstructions(11, pagesTraversed));
            tokenInstructionsEditorPane.setText(tokenSubmissionInstructionsBuilder.toString());
            tokenInstructionsEditorPane.setCaretPosition(0);
            tokenInstructionsEditorPane.repaint();
            tokenScreenPanel.add(getSubmitTokenPanel(), BorderLayout.SOUTH);
            tokenScreenPanel.add(infrastructureEfficiencyLabel, BorderLayout.NORTH);
            tokenScreenPanel.setBackground(Color.WHITE);
        }
        updateInfrastructureEfficiencyLabel();
        return tokenScreenPanel;
    }

    public void updateInfrastructureEfficiencyLabel() {
        GroupDataModel group = client.getClientDataModel().getGroupDataModel();
        RoundConfiguration roundConfiguration = client.getRoundConfiguration();
        int infrastructureEfficiency = 0;
        if (roundConfiguration.isPracticeRound() || roundConfiguration.isFirstRound()) {
            infrastructureEfficiency = roundConfiguration.getInitialInfrastructureEfficiency();
        }
        else {
            System.err.println("group was not null, efficiency is: " + group.getInfrastructureEfficiency() + " degrading by " + client.getRoundConfiguration().getInfrastructureDegradationFactor());
            infrastructureEfficiency = group.getInfrastructureEfficiency() - roundConfiguration.getInfrastructureDegradationFactor();
        }
        infrastructureEfficiencyLabel.setText("Current infrastructure efficiency: " + infrastructureEfficiency);
        infrastructureEfficiencyLabel.repaint();
    }

    private JPanel getSubmitTokenPanel() {
        if (submitTokenPanel == null) {
            submitTokenPanel = new JPanel();
            submitTokenPanel.setLayout(new BorderLayout());
            submitTokenPanel.add(getEnterTokens(), BorderLayout.CENTER);
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
            instructionsNavigationPanel.add(getNextButton(), BorderLayout.LINE_START);
            instructionsNavigationPanel.add(getPreviousButton(), BorderLayout.LINE_END);
            // this is just to add spacing I think.
            quizLabel = new JLabel();
            instructionsNavigationPanel.add(quizLabel, BorderLayout.CENTER);
        }
        return instructionsNavigationPanel;
    }

    private JButton getNextButton() {
        if (nextButton == null) {
            nextButton = new JButton();
            nextButton.setText("Next");
            nextButton.setEnabled(false);
            nextButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    quizLabel.setText("");

                    previousButton.setEnabled(true);
                    if(instructionNumber >= pagesTraversed){
                        nextButton.setEnabled(false);
                    }

                    // getting the next instruction Number
                    if (instructionNumber == NUMBER_INSTRUCTIONS) {

                    } 
                    else
                        instructionNumber++;
                    System.out.println("instruction number : "+instructionNumber+" pages traversed"+pagesTraversed);
                    setInstructions(getGeneralInstructions(instructionNumber,pagesTraversed));
                    if(instructionNumber == 5){
                        quizAnimationLabel.setText("");
                        addCenterComponent(getAnimationInstructionPane());
                        //addCenterComponent(getTokenScreenPanel());
                    }
                    else {
                        addCenterComponent(getInstructionsPanel());
                    }
                }

            });

        }
        return nextButton;
    }

    private JPanel getAnimationInstructionPane() {
        if (animationInstructionPanel == null) {
            animationInstructionPanel = new JPanel();
            animationInstructionPanel.setName("Animated instruction panel");
            animationInstructionPanel.setLayout(new BorderLayout());
            animationInstructionPanel.add(getAnimationPane(), BorderLayout.CENTER);

            animationInstructionPanel.add(getNavigationAnimationPanel(), BorderLayout.SOUTH);
            animationInstructionPanel.setBackground(Color.WHITE);
            return animationInstructionPanel;
        }
        //System.out.println("instruction number : "+instructionNumber+" pages traversed"+pagesTraversed);
        animationInstructionsEditorPane.setText(getGeneralInstructions(instructionNumber,pagesTraversed));
        return animationInstructionPanel;
    }


    private JPanel getNavigationAnimationPanel() {
        if (navigationAnimationPanel == null) {

            navigationAnimationPanel = new JPanel();
            navigationAnimationPanel.setLayout(new BorderLayout());
            navigationAnimationPanel.add(getNextAnimationButton(), BorderLayout.EAST);
            navigationAnimationPanel.add(getPreviousAnimationButton(), BorderLayout.WEST);

            quizAnimationLabel.setText("");
            navigationAnimationPanel.add(quizAnimationLabel, BorderLayout.CENTER);
        }
        return navigationAnimationPanel;
    }

    private JButton getPreviousAnimationButton() {
        if (previousAnimationButton == null) {
            previousAnimationButton = new JButton();
            previousAnimationButton.setText("Previous");
            previousAnimationButton.setEnabled(true);
            previousAnimationButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    quizLabel.setText("");
                    // getting the next instruction Number
                    nextButton.setEnabled(true);
                    if (instructionNumber == 1) {

                    } else
                        instructionNumber--;
                    if(instructionNumber > 1)
                        previousButton.setEnabled(true);
                    else
                        previousButton.setEnabled(false);
                    System.out.println("instruction number : "+instructionNumber+" pages traversed"+pagesTraversed);	
                    setInstructions(getGeneralInstructions(instructionNumber,pagesTraversed));
                    addCenterComponent(getInstructionsPanel());
                }
            });
            return previousAnimationButton;

        }
        return previousAnimationButton;
    }

    private JButton getNextAnimationButton() {
        if (nextAnimationButton == null) {
            nextAnimationButton = new JButton();
            nextAnimationButton.setText("Next");
            nextAnimationButton.setEnabled(false);
            nextAnimationButton.addActionListener(new java.awt.event.ActionListener() {


                public void actionPerformed(java.awt.event.ActionEvent e) {
                    quizLabel.setText("");
                    previousButton.setEnabled(true);
                    if(instructionNumber >= pagesTraversed)
                        nextButton.setEnabled(false);
                    // getting the next instruction Number
                    if (instructionNumber != NUMBER_INSTRUCTIONS) {
                        instructionNumber++;
                    }
                    System.out.println("instruction number : "+instructionNumber+" pages traversed"+pagesTraversed);	
                    setInstructions(getGeneralInstructions(instructionNumber,pagesTraversed));
                    if(instructionNumber == 5){
                        addCenterComponent(getAnimationInstructionPane());
                        //addCenterComponent(getTokenScreenPanel());
                    }
                    else
                        addCenterComponent(getInstructionsPanel());
                }

            });

        }
        return nextAnimationButton;

    }

    private JPanel getAnimationPane() {
        if (animationPanel == null) {
            animationPanel = new JPanel();
            animationPanel.setLayout(new BorderLayout());
            //FIXME: Here the animation panel needs to be decided on fly
            //canalPanelAnimation = new CanalPanelAnimation(40);
            canalAnimationPanel = new CanalAnimationPanel(40);
            animationPanel.add(canalAnimationPanel,BorderLayout.CENTER);
            animationInstructionsEditorPane = createInstructionsEditorPane();
            animationInstructionsScrollPane = new JScrollPane(animationInstructionsEditorPane);
            animationInstructionsEditorPane.setActionListener(createQuizListener(client.getServerConfiguration()));
            animationInstructionsEditorPane.setCaretPosition(0);
            animationPanel.add(animationInstructionsScrollPane, BorderLayout.SOUTH);
            //setInstructions(getGeneralInstructions(11));
            //System.out.println("instruction number : "+instructionNumber+" pages traversed"+pagesTraversed);
            animationInstructionsEditorPane.setText(getGeneralInstructions(instructionNumber,pagesTraversed));
            animationInstructionsEditorPane.setCaretPosition(0);
            animationInstructionsEditorPane.repaint();
            animationPanel.setBackground(Color.WHITE);
            return animationPanel;
        }

        return animationPanel;
    }

    private JButton getPreviousButton() {
        if (previousButton == null) {
            previousButton = new JButton();
            previousButton.setText("Previous");
            previousButton.setEnabled(false);
            previousButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    quizLabel.setText("");
                    // getting the next instruction Number
                    nextButton.setEnabled(true);
                    if (instructionNumber == 1) {

                    } else
                        instructionNumber--;
                    if(instructionNumber > 1)
                        previousButton.setEnabled(true);
                    else
                        previousButton.setEnabled(false);
                    System.out.println("instruction number : "+instructionNumber+" pages traversed"+pagesTraversed);
                    setInstructions(getGeneralInstructions(instructionNumber,pagesTraversed));
                    if(instructionNumber == 5){
                        quizAnimationLabel.setText("");
                        nextAnimationButton.setEnabled(true);
                        addCenterComponent(getAnimationInstructionPane());
                        //addCenterComponent(getTokenScreenPanel());
                    }
                    else
                        addCenterComponent(getInstructionsPanel());
                }
            });
            return previousButton;

        }
        return previousButton;
    }

    /**
     * This returns the string of general instructions from the round0.xml files
     * where the general instructions are stored
     * @param pagesTraversed 
     * 
     * @return
     */
    private String getGeneralInstructions(int pageNumber, int pagesTraversed) {
        /*if(pageNumber == 2){
    		if(client.getClientGameState().getPriority()== 0)
    			return client.getServerConfiguration().getGeneralInstructions(20);
    		else
    			return client.getServerConfiguration().getGeneralInstructions(21);
    	}*/

        return client.getServerConfiguration().getGeneralInstructions(pageNumber,pagesTraversed,
                client.getClientDataModel().getPriority());
    }

    private String getGeneralInstructions(int pageNumber) {
        return client.getServerConfiguration().getGeneralInstructions(pageNumber);
    }

    private JTextField getEnterTokens() {
        if (enterTokens == null) {
            enterTokens = new JTextField();
            enterTokens.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    submitInvestedTokens();
                }
            });
        }

        return enterTokens;
    }

    private JButton getSubmitTokensButton() {
        if (submitTokensButton == null) {
            submitTokensButton = new JButton();
            submitTokensButton.setText("Invest");
            submitTokensButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try{
                        submitInvestedTokens();	
                    }
                    catch(NumberFormatException e1){
                        enterTokens.setText("");
                        instructionsBuilder.delete(0, instructionsBuilder.length());
                        instructionsBuilder.append("\nPlease Enter your tokens within the range 0 -10");
                        instructionsBuilder.append(getGeneralInstructions(11,pagesTraversed));
                        tokenInstructionsEditorPane.setText(instructionsBuilder.toString());
                    }
                }


            });
            return submitTokensButton;
        }
        return submitTokensButton;
    }

    private void submitInvestedTokens() throws NumberFormatException {
        int token = Integer.parseInt(enterTokens.getText());
        // validating token range
        if (token >= 0 && token <= 10) {
            client.transmitTokenContributed(token);
            updateInstructions("Please wait while the server computes your total flow capacity based on your group's total token contribution investment.");
            addCenterComponent(getInstructionsEditorPane());
        } 
        else {
            enterTokens.setText("");
            instructionsBuilder.delete(0, instructionsBuilder.length());
            instructionsBuilder.append("\nPlease Enter your tokens within the range 0 - 10");
            instructionsBuilder.append(getGeneralInstructions(11,pagesTraversed));
            tokenInstructionsEditorPane.setText(instructionsBuilder.toString());
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    private HtmlEditorPane getInstructionsEditorPane() {
        // JEditorPane pane = new JEditorPane("text/html",
        // "Costly Sanctioning Experiment");
        if (instructionsEditorPane == null) {
            instructionsEditorPane = createInstructionsEditorPane();
        }
        return instructionsEditorPane;
    }
    
    private HtmlEditorPane createInstructionsEditorPane() {
        HtmlEditorPane htmlEditorPane = new HtmlEditorPane();
        htmlEditorPane.setPreferredSize(new Dimension(400, 400));
        htmlEditorPane.setEditable(false);
        htmlEditorPane.setFont(new Font("serif", Font.PLAIN, 12));
        return htmlEditorPane;

    }



    private void addCenterComponent(Component newCenterComponent) {
        cardLayout.show(this, newCenterComponent.getName());
        //        if (currentCenterComponent != null) {
        //            currentCenterComponent.setVisible(false);
        //            remove(currentCenterComponent);
        //            add(newCenterComponent, BorderLayout.CENTER);
        //            newCenterComponent.setVisible(true);
        //            invalidate();
        //            validate();
        //            newCenterComponent.repaint();
        //            repaint();
        //        }
        //        currentCenterComponent = newCenterComponent;
    }

    public void startRound(final RoundConfiguration configuration) {

        // currentExperimentConfiguration = configuration;
        Runnable runnable = new Runnable() {
            public void run() {
//                addCenterComponent(subjectWindow);
                requestFocusInWindow();

            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    /*
     * This method could be merged somehow with the startRoundEvent. We can get
     * rid of the sendContributionStatus then
     */
    public void updateContributions() {
        irrigationGamePanel.updateContributions(clientDataModel);
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
                enterTokens.setText("");
                addDebriefingText(event);
                // generate debriefing text from data culled from the Event
                addCenterComponent(instructionsEditorPane);
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

    private void addDebriefingText(EndRoundEvent event) {
        instructionsBuilder.delete(0, instructionsBuilder.length());

        Map<Identifier,ClientData>clientDataMap = event.getClientDataMap();

        //create a map of clientData based on priority as the index structure
        Map<Integer,ClientData>clientDataMapIndexInteger = new LinkedHashMap<Integer,ClientData>();

        String position = "Your position is :";
        position = position+mapClientPosition(client.getClientDataModel().getPriority());
        for(ClientData clientData : clientDataMap.values()){
            clientDataMapIndexInteger.put(new Integer(clientData.getPriority()), clientData);
        }
        // FIXME: this is horrendous
        instructionsBuilder.append(String.format(
                position+
                "<table><thead><th></th><th></th><th>A</th><th></th><th>B</th><th></th><th>C</th><th></th><th>D</th><th></th><th>E</th></thead><tbody>"+
                "<tr><td>A Initial endowment</td><td></td><td>10</td><td></td><td>10</td><td></td><td>10</td><td></td><td>10</td><td></td><td>10</td></tr>"+
                "<tr><td>B Infrastructure investment</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td></tr>"+
                "<tr><td>C Kept endowment (A - B)</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td></tr>"+
                "<tr><td>D Return from growing crops</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td></tr>"+
                "<tr><td>E Total tokens earned in last round(D+C)</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td><td></td><td>%d</td></tr>"+
                "<tr><td>F Total dollars earned in this round (E*0.1$)</td><td></td><td>%3.2f</td><td></td><td>%3.2f</td><td></td><td>%3.2f</td><td></td><td>%3.2f</td><td></td><td>%3.2f</td></tr>",
                clientDataMapIndexInteger.get(new Integer(0)).getContributedTokens(),
                clientDataMapIndexInteger.get(new Integer(1)).getContributedTokens(),
                clientDataMapIndexInteger.get(new Integer(2)).getContributedTokens(),
                clientDataMapIndexInteger.get(new Integer(3)).getContributedTokens(),
                clientDataMapIndexInteger.get(new Integer(4)).getContributedTokens(),

                10 - clientDataMapIndexInteger.get(new Integer(0)).getContributedTokens(),
                10 - clientDataMapIndexInteger.get(new Integer(1)).getContributedTokens(),
                10 - clientDataMapIndexInteger.get(new Integer(2)).getContributedTokens(),
                10 - clientDataMapIndexInteger.get(new Integer(3)).getContributedTokens(),
                10 - clientDataMapIndexInteger.get(new Integer(4)).getContributedTokens(),

                clientDataMapIndexInteger.get(new Integer(0)).getTotalTokensEarned() - (10 - clientDataMapIndexInteger.get(new Integer(0)).getContributedTokens()),
                clientDataMapIndexInteger.get(new Integer(1)).getTotalTokensEarned() - (10 - clientDataMapIndexInteger.get(new Integer(1)).getContributedTokens()),
                clientDataMapIndexInteger.get(new Integer(2)).getTotalTokensEarned() - (10 - clientDataMapIndexInteger.get(new Integer(2)).getContributedTokens()),
                clientDataMapIndexInteger.get(new Integer(3)).getTotalTokensEarned() - (10 - clientDataMapIndexInteger.get(new Integer(3)).getContributedTokens()),
                clientDataMapIndexInteger.get(new Integer(4)).getTotalTokensEarned() - (10 - clientDataMapIndexInteger.get(new Integer(4)).getContributedTokens()),

                clientDataMapIndexInteger.get(new Integer(0)).getTotalTokensEarned(),
                clientDataMapIndexInteger.get(new Integer(1)).getTotalTokensEarned(),
                clientDataMapIndexInteger.get(new Integer(2)).getTotalTokensEarned(),
                clientDataMapIndexInteger.get(new Integer(3)).getTotalTokensEarned(),
                clientDataMapIndexInteger.get(new Integer(4)).getTotalTokensEarned(),

                (float)clientDataMapIndexInteger.get(new Integer(0)).getRoundConfiguration().getDollarsPerToken()*clientDataMapIndexInteger.get(new Integer(0)).getTotalTokensEarned(),
                (float)clientDataMapIndexInteger.get(new Integer(1)).getRoundConfiguration().getDollarsPerToken()*clientDataMapIndexInteger.get(new Integer(1)).getTotalTokensEarned(),
                (float)clientDataMapIndexInteger.get(new Integer(2)).getRoundConfiguration().getDollarsPerToken()*clientDataMapIndexInteger.get(new Integer(2)).getTotalTokensEarned(),
                (float)clientDataMapIndexInteger.get(new Integer(3)).getRoundConfiguration().getDollarsPerToken()*clientDataMapIndexInteger.get(new Integer(3)).getTotalTokensEarned(),
                (float)clientDataMapIndexInteger.get(new Integer(4)).getRoundConfiguration().getDollarsPerToken()*clientDataMapIndexInteger.get(new Integer(4)).getTotalTokensEarned()

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
                (float)event.getClientData().getRoundConfiguration().getDollarsPerToken()*event.getClientData().getTotalTokensEarned(),
                (float)event.getClientData().getRoundConfiguration().getDollarsPerToken()*event.getClientData().getTotalTokens(),
                (float)event.getClientData().getRoundConfiguration().getDollarsPerToken()*event.getClientData().getTotalTokens()+5.00
        ));
        //append the added practice round instructions
        if(client.getRoundConfiguration().isPracticeRound())
            instructionsBuilder.append(" However, this is a practice round and the earnings mentioned are only for illustrative purposes " +
            "and will not count for the actual payments");

        setInstructions(instructionsBuilder.toString());
        if (event.isLastRound()) {
            instructionsBuilder
            .append(String
                    .format(
                            "<p><b>The experiment is now almost over.  There is a survey"
                            + " for you to fill out"
                            + " while the facilitator prepares your earnings.  When the facilitator is ready "
                            + "and you have completed the survey, your computer number will be called.  You will then go to "
                            + "the room next door to receive payment.</b></p>",
                            event.getId()));
            setInstructions(instructionsBuilder.toString());
        }
    }

    private String mapClientPosition(int priority) {
        if (priority == 0) {
            return "A";
        } else if (priority == 1) {
            return "B";
        } else if (priority == 2) {
            return "C";
        } else if (priority == 3) {
            return "D";
        } else if (priority == 4) {
            return "E";
        }
        return "Position not found";
    }

    // adding the instructions into the instruction Panel
    private void setInstructions(String s) {
        instructionsEditorPane.setText(s);
        instructionsEditorPane.setCaretPosition(0);
        invalidate();
        validate();
        instructionsScrollPane.requestFocusInWindow();
    }


    private ActionListener createQuizListener(final ServerConfiguration configuration) {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e){
                // System.err.println("In action performed with event: " + e);
                FormActionEvent formEvent = (FormActionEvent) e;
                Properties actualAnswers = formEvent.getData();
                System.out.println("Form data"+formEvent.getData().toString());
                //                actualAnswers.list(System.err);
                List<String> incorrectAnswers = new ArrayList<String>();
                // iterate through expected answers
                for (Map.Entry<String, String> entry : configuration.getQuizAnswers().entrySet()) {
                    //here just check those questions that come in those instructions

                    String questionNumber = entry.getKey();
                    String expectedAnswer = entry.getValue();

                    //System.out.println("Actual answer"+actualAnswers.getProperty(questionNumber));
                    switch(instructionNumber){
                    case 1: if(questionNumber.equalsIgnoreCase("q1")){
                        //System.out.println("Entering the string equal");
                        incorrectAnswers = addIncorrectAnswerList(expectedAnswer,actualAnswers,questionNumber, incorrectAnswers);
                    }
                    break;

                    case 2: if(questionNumber.equalsIgnoreCase("q2")){
                        //System.out.println("Entering the string equal");
                        incorrectAnswers = addIncorrectAnswerList(expectedAnswer,actualAnswers,questionNumber, incorrectAnswers);
                    }
                    break;

                    case 3: if(questionNumber.equalsIgnoreCase("q3") || questionNumber.equalsIgnoreCase("q4") ){
                        //System.out.println("Entering the string equal");
                        incorrectAnswers = addIncorrectAnswerList(expectedAnswer,actualAnswers,questionNumber, incorrectAnswers);
                    }
                    break;

                    case 4: if(questionNumber.equalsIgnoreCase("q5")){
                        //System.out.println("Entering the string equal");
                        incorrectAnswers = addIncorrectAnswerList(expectedAnswer,actualAnswers,questionNumber, incorrectAnswers);
                    }
                    break;

                    case 5: if(questionNumber.equalsIgnoreCase("q6") || questionNumber.equalsIgnoreCase("q7") ){
                        //System.out.println("Entering the string equal");
                        incorrectAnswers = addIncorrectAnswerList(expectedAnswer,actualAnswers,questionNumber, incorrectAnswers);
                    }
                    break;

                    case 6: if(questionNumber.equalsIgnoreCase("q8")){
                        //System.out.println("Entering the string equal");
                        incorrectAnswers = addIncorrectAnswerList(expectedAnswer,actualAnswers,questionNumber, incorrectAnswers);
                    }
                    break;
                    case 7: if(questionNumber.equalsIgnoreCase("q9") || questionNumber.equalsIgnoreCase("q10") ){
                        //System.out.println("Entering the string equal");
                        incorrectAnswers = addIncorrectAnswerList(expectedAnswer,actualAnswers,questionNumber, incorrectAnswers);
                    }
                    break;

                    case 8: if(questionNumber.equalsIgnoreCase("q11")){
                        //System.out.println("Entering the string equal");
                        incorrectAnswers = addIncorrectAnswerList(expectedAnswer,actualAnswers,questionNumber, incorrectAnswers);
                        System.out.println("Expected question"+questionNumber);
                        System.out.println("Expected Answer"+expectedAnswer);
                    }
                    break;
                    }

                }
                printIncorrectAnswerList(incorrectAnswers);
                if (incorrectAnswers.isEmpty()) {
                    nextButton.setEnabled(true);
                    if(instructionNumber == 5){
                        nextAnimationButton.setEnabled(true);
                        quizAnimationLabel.setForeground(Color.BLUE);
                        quizAnimationLabel.setText("  Congratulations, your answer is correct.  You may go to the next instructions page by clicking 'Next'");
                    }

                    pagesTraversed++;
                    //System.out.println("quiz :instruction number : "+instructionNumber+" pages traversed"+pagesTraversed);	
                    setInstructions(getGeneralInstructions(instructionNumber,pagesTraversed));
                    if(instructionNumber == 5) {
                        addCenterComponent(getAnimationInstructionPane());
                    }
                    else {
                        addCenterComponent(getInstructionsPanel());
                    }
                    quizLabel.setForeground(Color.BLUE);
                    quizLabel.setText("  Congratulations, your answer is correct.  You may go to the next page by clicking 'Next'");
                    // notify the server and also notify the participant.
                    //StringBuilder builder = new StringBuilder(configuration.getInstructions());
                    //builder.append("<br><b>Congratulations!</b> You have answered all questions correctly.");
                    //setInstructions(builder.toString());
                    client.transmit(new QuizCompletedEvent(client.getId(),instructionNumber));

                }
                else {
                    if(instructionNumber == 5){
                        quizAnimationLabel.setForeground(Color.RED);
                        quizAnimationLabel.setText(" Sorry, your answer is incorrect. Please try again.");
                    }
                    nextButton.setEnabled(false);
                    quizLabel.setForeground(Color.RED);
                    quizLabel.setText(" Sorry, your answer is incorrect. Please try again.");
                    // FIXME: highlight the incorrect answers?
                    //StringBuilder builder = new StringBuilder().append(instructionsBuilder);
                    //builder.append("<br><b><font color='red'>You have answered some questions incorrectly.  Please try again and resubmit.</font></b>");
                    //setInstructions(builder.toString());
                }
            }

            private List<String> addIncorrectAnswerList(String expectedAnswer, Properties actualAnswers, String questionNumber, List<String> incorrectAnswers) {
                // TODO Auto-generated method stub
                if (! expectedAnswer.equals(actualAnswers.getProperty(questionNumber)) ) {
                    // flag the incorrect response
                    incorrectAnswers.add(questionNumber);
                }
                return incorrectAnswers;
            }

            private void printIncorrectAnswerList(List<String> incorrectAnswers) {
                // TODO Auto-generated method stub
                System.out.println("Size of the incorrect answers is :"+incorrectAnswers.size());
                for(int i=0;i<incorrectAnswers.size();i++){
                    System.out.println("Incorrect Answers :"+incorrectAnswers.get(i));
                }
            }
        };

    }

    public void updateInstructions(final String s) {
        // setting the initial instructions
        // FIXME: endRound should do the addCenterComponent(..)
        //addCenterComponent(instructionsEditorPane);
        setInstructions(s);
    }

    public void updateGraphDisplay(final ClientData clientData) {
        // TODO Auto-generated method stub
        this.clientData = clientData;
        totalContributedTokensPerGroup = clientData.getGroupDataModel().getTotalContributedTokens();
        contributionStatusInformationjTextArea = new JTextArea();
        contributionStatusInformationjTextArea.setEditable(false);

        // adding the Contribution Status Information to the Text Area
        DecimalFormat df = new DecimalFormat("#.##");
        String contributionStatusInformation = "Initial infrastructure efficiency: " + clientData.getGroupDataModel().getInitialInfrastructureEfficiency() + "%"
        + "\n\nInitial flow capacity: " + clientData.getGroupDataModel().getInitialFlowCapacity() + " cubic feet per second "
        + "\n\nActual infrastructure efficiency: " + clientData.getGroupDataModel().getInfrastructureEfficiency() + "%"
        + "\n\nActual total flow capacity: "
        + df.format(clientData.getGroupDataModel().getMaximumAvailableFlowCapacity()) + " cubic feet per second \n\n"
        + "\n\nMaximum tokens that could have been contributed: "
        + client.getRoundConfiguration().getMaximumTotalTokenContribution()
        + "\n\nTotal tokens contributed: "
        + totalContributedTokensPerGroup
        + "\n\nYour token contribution: "
        + clientData.getContributedTokens()
        + "\n\nTotal flow capacity that could have been generated: "
        + client.getRoundConfiguration().getMaximumCanalFlowCapacity()
        + " cubic feet per second";


        contributionStatusInformationjTextArea.setText(contributionStatusInformation);

        graphPanel = new JPanel();
        GridLayout gridGraphPanel = new GridLayout();
        gridGraphPanel.setColumns(2);
        graphPanel.setLayout(gridGraphPanel);

        Runnable runnable = new Runnable() {
            public void run() {
                graphPanel.add(getChartPanel(clientData), null);
                graphPanel.add(contributionStatusInformationjTextArea,null);
                addCenterComponent(graphPanel);
                requestFocusInWindow();
            }


        };
        SwingUtilities.invokeLater(runnable);
    }

    private JPanel getChartPanel(ClientData clientData) {
        // TODO Auto-generated method stub
        chartPanel = new JPanel();

        xySeriesDemo = new ChartWindowPanelTokenBandwidth(
                client);
        xySeriesDemo.setVisible(true);

        pieChart = new PieChart(clientData.getGroupDataModel(),client);

        GridLayout gridLayout = new GridLayout();
        gridLayout.setRows(2);
        gridLayout.setColumns(1);
        chartPanel.setLayout(gridLayout);
        chartPanel.add(xySeriesDemo);
        chartPanel.add(pieChart);
        return chartPanel;
    }


    public void updateSubmitTokenScreenDisplay() {
        Runnable runnable = new Runnable() {
            public void run() {
                //mainIrrigationWindow.setup(configuration);
                // reset the amount of time left in the round on food eaten
                // label to the value from the configuration file.
                // this is NOT dynamic; once the StartRoundEvent is fired off
                // by the server no new clients can connect because the round
                // has begun.
                /*roundEndsOn = (configuration.getRoundTime() * 1000L) + System.currentTimeMillis();
                    update();*/
                addCenterComponent(getTokenScreenPanel());
                getEnterTokens().requestFocusInWindow();
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    public void updateRoundInstructions(String instructions, int priority) {
        instructionsBuilder.append(instructions);
        updateInstructions(instructionsBuilder.toString());
        addCenterComponent(instructionsEditorPane);
    }

    private ChatPanel chatPanel;
    private ChatPanel getChatPanel() {
        if (chatPanel == null) {
            chatPanel = new ChatPanel();
            chatPanel.setClient(client);
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
                chatPanel.getJTextField().requestFocus();
                System.err.println("Done adding chat panel...");
                //stop the animation in the instructions

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
                        addCenterComponent(getTokenScreenPanel());
                        getEnterTokens().requestFocusInWindow();
                        timer.stop();
                        timer = null;
                    }
                    else {
                        chatPanel.setTimeRemaining(timeRemaining);
                    }
                }

                private void sleep() {
                    // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        setInstructions(getGeneralInstructions(1,pagesTraversed));
        addCenterComponent(getInstructionsPanel());

    }
}
