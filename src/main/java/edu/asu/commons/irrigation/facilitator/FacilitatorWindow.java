/**
 * 
 */
package edu.asu.commons.irrigation.facilitator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.asu.commons.irrigation.events.BeginChatRoundRequest;
import edu.asu.commons.irrigation.events.DisplaySubmitTokenRequest;
import edu.asu.commons.irrigation.events.FacilitatorEndRoundEvent;
import edu.asu.commons.irrigation.events.ShowInstructionsRequest;
import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.irrigation.server.GroupDataModel;
import edu.asu.commons.irrigation.server.ServerDataModel;
import edu.asu.commons.net.Identifier;
import edu.asu.commons.util.HtmlEditorPane;

/**
 * @author Sanket
 *
 */
public class FacilitatorWindow extends JPanel {

    private Facilitator facilitator;

    private Dimension windowDimension;

    private static final long serialVersionUID = 1L;


    private JButton startRoundButton = null;

    private JButton beginChatButton;

    private HtmlEditorPane editorPane;
    private JScrollPane scrollPane;

    private JButton enableInstructionButton;
    /**
     * This is the default constructor
     */

    public FacilitatorWindow(Dimension dimension, Facilitator facilitator) {
        windowDimension = dimension;
        this.facilitator = facilitator;
        initialize();
    }

    /*
	public IrrigationFacilitatorWindow(){
	super();
	initialize();
	}
     */

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        setSize(windowDimension);
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(getStartRoundButton());
        buttonPanel.add(getBeginChatButton());
        buttonPanel.add(getEnableInstructionButton());
        add(buttonPanel, BorderLayout.NORTH);
        JPanel informationPanel = new JPanel();
        editorPane = new HtmlEditorPane();
        scrollPane = new JScrollPane(editorPane);
        informationPanel.add(scrollPane);
        add(informationPanel, BorderLayout.CENTER);
    }

    private JButton getEnableInstructionButton() {
        // TODO Auto-generated method stub
        if (enableInstructionButton == null) {
            enableInstructionButton = new JButton("Enable Instruction");
            enableInstructionButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    enableInstructionButton.setEnabled(false);
                    facilitator.transmit(new ShowInstructionsRequest(facilitator.getId()));
                }
            });
        }
        return enableInstructionButton;
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
                    else {
                        facilitator.transmit(new DisplaySubmitTokenRequest(facilitator.getId()));
                    }
                }
            });
        }
        return beginChatButton;
    }

    public Facilitator getFacilitator(){
        return facilitator;
    }

    /**
     * This method initializes Stop	
     * 	
     * @return javax.swing.JButton	
     */
    /**
     * This method initializes Start_Round_Button	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getStartRoundButton() {
        if (startRoundButton == null) {
            startRoundButton = new JButton();
            startRoundButton.setBounds(new Rectangle(180, 16, 136, 24));
            startRoundButton.setText("Start Round");
            startRoundButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                    facilitator.sendBeginRoundRequest();
                }
            });
        }
        return startRoundButton;
    }

    /**
     * This method initializes Stop_Round_Button1	
     * 	
     * @return javax.swing.JButton	
     */


    public void endRound(FacilitatorEndRoundEvent event) {
        ServerDataModel model = event.getServerDataModel();
        StringBuilder builder = new StringBuilder();
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
                    data.getTotalTokensEarned(), 
                    data.getTotalTokensEarned() * model.getRoundConfiguration().getDollarsPerToken(),
                    data.getTotalTokens() * model.getRoundConfiguration().getDollarsPerToken()+ facilitator.getConfiguration().getShowUpPayment()));
        }
        builder.append("</tbody></table><hr>");
        //FIXME: Could not understand how to evaluate .isLastRound(), hence using the 
        if (event.isLastRound()) {
            builder.append("<h2><font color='blue'>The experiment is over.  Please prepare payments.</font></h2>");
        }
        editorPane.setText(builder.toString());
        repaint();
    }

}  //  @jve:decl-index=0:visual-constraint="48,19"
