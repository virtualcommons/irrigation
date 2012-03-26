package edu.asu.commons.irrigation.facilitator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import edu.asu.commons.event.ShowInstructionsRequest;
import edu.asu.commons.irrigation.events.BeginChatRoundRequest;
import edu.asu.commons.irrigation.events.FacilitatorEndRoundEvent;
import edu.asu.commons.irrigation.events.ShowGameScreenshotRequest;
import edu.asu.commons.irrigation.events.ShowQuizRequest;
import edu.asu.commons.ui.HtmlEditorPane;
import edu.asu.commons.ui.UserInterfaceUtils;

/**
 * $Id$
 * 
 * Basic facilitator interface for driving the experiment.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class FacilitatorWindow extends JPanel {

    private static final long serialVersionUID = 3607885359444962888L;

    private Facilitator facilitator;

    private JButton startRoundButton;

    private JButton beginChatButton;

    private HtmlEditorPane informationEditorPane;
    private HtmlEditorPane messageEditorPane;
    private JScrollPane informationScrollPane;

    private JButton showInstructionsButton;

    private JButton showQuizButton;

    private JButton showScreenshotButton;

    // private JButton displayInvestmentButton;

    private StringBuilder builder = new StringBuilder();

    private JButton overrideButton;

    private JButton stopRoundButton;

    /**
     * This is the default constructor
     */

    public FacilitatorWindow(Facilitator facilitator) {
        this.facilitator = facilitator;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        addToBoxLayout(buttonPanel, getShowInstructionsButton());
        addToBoxLayout(buttonPanel, getShowInstructionsButton());
        addToBoxLayout(buttonPanel, getShowScreenshotButton());
        addToBoxLayout(buttonPanel, getBeginChatButton());
        // addToBoxLayout(buttonPanel, getDisplayInvestmentButton());
        addToBoxLayout(buttonPanel, getStartRoundButton());
        addToBoxLayout(buttonPanel, getShowQuizButton());
        addToBoxLayout(buttonPanel, getStartRoundOverrideButton());
        addToBoxLayout(buttonPanel, getStopRoundButton());
        add(buttonPanel, BorderLayout.NORTH);
        informationEditorPane = UserInterfaceUtils.createInstructionsEditorPane();
        informationScrollPane = new JScrollPane(informationEditorPane);
        informationScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        informationScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel messagePanel = new JPanel(new BorderLayout());
        JLabel messagePanelLabel = new JLabel("System messages");
        messagePanelLabel.setFont(UserInterfaceUtils.DEFAULT_PLAIN_FONT);
        messagePanel.add(messagePanelLabel, BorderLayout.NORTH);
        Dimension minimumSize = new Dimension(600, 50);
        messagePanel.setMinimumSize(minimumSize);
        informationScrollPane.setMinimumSize(minimumSize);
        messageEditorPane = UserInterfaceUtils.createInstructionsEditorPane();
        JScrollPane messageScrollPane = new JScrollPane(messageEditorPane);
        messagePanel.add(messageScrollPane, BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, informationScrollPane, messagePanel);
        add(splitPane, BorderLayout.CENTER);
        double proportion = 0.7d;
        splitPane.setDividerLocation(proportion);
        splitPane.setResizeWeight(proportion);
    }

    private void addToBoxLayout(JPanel buttonPanel, JButton button) {
        buttonPanel.add(button);
        buttonPanel.add(Box.createHorizontalStrut(5));
    }

    public void addMessage(String message) {
        try {
            messageEditorPane.getDocument().insertString(0, "-----\n" + message + "\n", null);
        } catch (BadLocationException exception) {
            exception.printStackTrace();
        }
    }

    private JButton getShowInstructionsButton() {
        if (showInstructionsButton == null) {
            showInstructionsButton = new JButton("Show Instructions");
            showInstructionsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    // enableInstructionButton.setEnabled(false);
                    facilitator.transmit(new ShowInstructionsRequest(facilitator.getId()));
                }
            });
        }
        return showInstructionsButton;
    }

    /**
     * This method initializes Start_Facilitator_Button
     * 
     * @return javax.swing.JButton
     */

    private JButton getBeginChatButton() {
        if (beginChatButton == null) {
            beginChatButton = new JButton("Begin Chat");
            beginChatButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    // At present default isChatEnabledBeforeRound() value isscreen false
                    if (facilitator.getCurrentRoundConfiguration().isChatEnabledBeforeRound()) {
                        facilitator.transmit(new BeginChatRoundRequest(facilitator.getId()));
                    }
                }
            });
        }
        return beginChatButton;
    }

    private JButton getStartRoundOverrideButton() {
        if (overrideButton == null) {
            overrideButton = new JButton("Override");
            overrideButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    facilitator.sendStartRoundOverride();
                }
            });
        }
        return overrideButton;
    }

    // private JButton getDisplayInvestmentButton() {
    // if (displayInvestmentButton == null) {
    // displayInvestmentButton = new JButton("Show Investment Screen");
    // displayInvestmentButton.addActionListener(new ActionListener() {
    // public void actionPerformed(ActionEvent event) {
    // facilitator.transmit(new ShowTokenInvestmentScreenRequest(facilitator.getId()));
    // }
    // });
    // }
    // return displayInvestmentButton;
    // }

    public Facilitator getFacilitator() {
        return facilitator;
    }

    private JButton getStartRoundButton() {
        if (startRoundButton == null) {
            startRoundButton = new JButton("Start round");
            startRoundButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    facilitator.sendBeginRoundRequest();
                }
            });
        }
        return startRoundButton;
    }

    private JButton getStopRoundButton() {
        if (stopRoundButton == null) {
            stopRoundButton = new JButton("Stop round");
            stopRoundButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    facilitator.sendEndRoundRequest();
                }
            });
        }
        return stopRoundButton;
    }

    private JButton getShowQuizButton() {
        if (showQuizButton == null) {
            showQuizButton = new JButton("Show quiz");
            showQuizButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    facilitator.transmit(new ShowQuizRequest(facilitator.getId()));
                }
            });
        }
        return showQuizButton;
    }

    public void setInstructions(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                informationEditorPane.setText(text);
                informationScrollPane.revalidate();
            }
        });
    }

    /**
     * 
     * @param event
     */
    public void endRound(FacilitatorEndRoundEvent event) {
        setInstructions(event.getServerDataModel().generateFacilitatorDebriefing());
    }

    public void addInstructions(String instructions) {
        builder.append(instructions);
        setInstructions(builder.toString());
    }

    private JButton getShowScreenshotButton() {
        if (showScreenshotButton == null) {
            showScreenshotButton = new JButton("Show screenshot");
            showScreenshotButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    facilitator.transmit(new ShowGameScreenshotRequest(facilitator.getId()));
                }
            });
        }
        return showScreenshotButton;
    }

}
