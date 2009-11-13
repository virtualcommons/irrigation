package edu.asu.commons.irrigation.facilitator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import edu.asu.commons.irrigation.events.BeginChatRoundRequest;
import edu.asu.commons.irrigation.events.FacilitatorEndRoundEvent;
import edu.asu.commons.irrigation.events.ShowGameScreenshotRequest;
import edu.asu.commons.irrigation.events.ShowInstructionsRequest;
import edu.asu.commons.irrigation.events.ShowQuizRequest;
import edu.asu.commons.irrigation.events.ShowTokenInvestmentScreenRequest;
import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.irrigation.server.ServerDataModel;
import edu.asu.commons.net.Identifier;
import edu.asu.commons.util.HtmlEditorPane;

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

    private JButton startRoundButton = null;

    private JButton beginChatButton;

    private HtmlEditorPane editorPane;
    private JScrollPane scrollPane;

    private JButton showInstructionsButton;
    
    private JButton showQuizButton;
    
    private JButton showScreenshotButton;
    
    private JButton displayInvestmentButton;
    
    private StringBuilder builder = new StringBuilder();

	private JButton beginExperimentButton;
    /**
     * This is the default constructor
     */

    public FacilitatorWindow(Facilitator facilitator) {
        this.facilitator = facilitator;
        initGuiComponents();
    }

    /**
     * 
     * @return void
     */
    private void initGuiComponents() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(getShowInstructionsButton());
        buttonPanel.add(getBeginChatButton());
        buttonPanel.add(getDisplayInvestmentButton());
        buttonPanel.add(getShowScreenshotButton());
        buttonPanel.add(getStartRoundButton());
        buttonPanel.add(getShowQuizButton());
        buttonPanel.add(getStartRoundOverrideButton());
        add(buttonPanel, BorderLayout.NORTH);
        editorPane = new HtmlEditorPane();
        editorPane.setEditable(false);
        scrollPane = new JScrollPane(editorPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton getShowInstructionsButton() {
        if (showInstructionsButton == null) {
            showInstructionsButton = new JButton("Show Instructions");
            showInstructionsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
//                    enableInstructionButton.setEnabled(false);
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
                    //At present default isChatEnabledBeforeRound() value isscreen false
                    if (facilitator.getCurrentRoundConfiguration().isChatEnabledBeforeRound()) {
                        facilitator.transmit(new BeginChatRoundRequest(facilitator.getId()));
                    }
                }
            });
        }
        return beginChatButton;
    }
    
    private JButton getStartRoundOverrideButton() {
    	if (beginExperimentButton == null) {
    		beginExperimentButton = new JButton("Override");
    		beginExperimentButton.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent event) {
    				facilitator.sendStartRoundOverride();
    			}
    		});
    	}
    	return beginExperimentButton;
    }
    
    private JButton getDisplayInvestmentButton() {
        if (displayInvestmentButton == null) {
            displayInvestmentButton = new JButton("Show Investment Screen");
            displayInvestmentButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    facilitator.transmit(new ShowTokenInvestmentScreenRequest(facilitator.getId()));
                }
            });
        }
        return displayInvestmentButton;
    }

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
    
    public void setText(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                editorPane.setText(text);
                scrollPane.revalidate();
            }
        });
    }

    /**
     * This method initializes Stop_Round_Button1	
     * 	
     * @return javax.swing.JButton	
     */
    public void endRound(FacilitatorEndRoundEvent event) {
        builder = new StringBuilder();
        ServerDataModel model = event.getServerDataModel();
        builder.append("<h3>Facilitator Debriefing:</h3>");
        builder.append("<table><thead><th>Participant</th><th>Current tokens</th><th>Current Income</th><th>Total Income</th></thead><tbody>");
        Map<Identifier, ClientData> clientDataMap = new HashMap<Identifier, ClientData>();
        for (GroupDataModel group: model.getAllGroupDataModels()) {
            clientDataMap.putAll(group.getClientDataMap());
        }
        TreeSet<Identifier> orderedSet = new TreeSet<Identifier>(clientDataMap.keySet());
        for (Identifier clientId : orderedSet) {
            ClientData data = clientDataMap.get(clientId);
            // FIXME: hack... inject the configuration into the client data so that getIncome() will return something appropriate.
            // should just refactor getIncome or remove it from ClientData entirely.
            builder.append(String.format(
                    "<tr><td>%s</td>" +
                    "<td align='center'>%d</td>" +
                    "<td align='center'>$%3.2f</td>" +
                    "<td align='center'>$%3.2f</td></tr>",
                    clientId.toString(), 
                    data.getAllTokensEarnedThisRound(), 
                    data.getAllTokensEarnedThisRound() * model.getRoundConfiguration().getDollarsPerToken(),
                    data.getTotalTokens() * model.getRoundConfiguration().getDollarsPerToken()+ facilitator.getConfiguration().getShowUpPayment()));
        }
        builder.append("</tbody></table><hr>");
        //FIXME: Could not understand how to evaluate .isLastRound(), hence using the 
        if (event.isLastRound()) {
            builder.append("<h2><font color='blue'>The experiment is over.  Please prepare payments.</font></h2>");
        }
        setText(builder.toString());
    }
    
    public void addInstructions(String instructions) {
        builder.append(instructions);
        setText(builder.toString());
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
