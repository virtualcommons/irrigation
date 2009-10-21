package edu.asu.commons.irrigation.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.asu.commons.irrigation.server.ClientData;

/**
 * $Id$
 * 
 * 
 *
 * @author Sanket Joshi
 * @version $Rev$
 */
public class MiddleScorePanel extends JPanel {

    private static final long serialVersionUID = -1329890496417714604L;

    private JLabel positionText;

    private JLabel currentBandwidthText;

    private JLabel waterUsedTextField;

    private JLabel totalTokensEarnedTextField;

    private int priority;

    private ClientData clientData; 

    public MiddleScorePanel(int priority, ClientData clientData){
        super();
        this.priority = priority;
        initialize(clientData);
    }

    private void initialize(ClientData clientData) {
        this.setLayout(null);
        this.clientData = clientData;
        setBackground( clientData.getPriority() == priority ? Color.GREEN : Color.YELLOW);
        this.setPreferredSize(new Dimension(60,200));
        this.add(getPositionText(),null);
        this.add(getWaterUsedTextField(),null);
        this.add(getCurrentBandwidth(),null);
        this.add(getTokensCollected(),null);


    }

    private JLabel getTokensCollected() {
        if(totalTokensEarnedTextField == null){
            totalTokensEarnedTextField = new JLabel();
            totalTokensEarnedTextField.setBounds(new Rectangle(5,50+32+10+20+10+20+10+20+10,30,20));
            totalTokensEarnedTextField.setText("");
        }
        return totalTokensEarnedTextField;
    }

    private JLabel getCurrentBandwidth() {
        if(currentBandwidthText == null){
            currentBandwidthText = new JLabel();
            currentBandwidthText.setBounds(new Rectangle(5,50+32+10+20+10,30,20));
            currentBandwidthText.setText("");
        }
        return currentBandwidthText;
    }

    private JLabel getPositionText() {
        if (positionText == null){
            positionText = new JLabel(clientData.getPriorityString());
            positionText.setBounds(new Rectangle(5,50+32+10,30,20));
        }
        return positionText;
    }


    private JLabel getWaterUsedTextField() {
        if(waterUsedTextField == null){
            waterUsedTextField = new JLabel("0");
            waterUsedTextField.setBounds(new Rectangle(5,50+32+10+20+10+20+10,30,20));
        }
        return waterUsedTextField;
    }

    public void update(ClientData clientData) {
        this.clientData = clientData;
        currentBandwidthText.setText("" + clientData.getAvailableFlowCapacity());
        waterUsedTextField.setText("" + clientData.getWaterUsed());
        totalTokensEarnedTextField.setText("" + clientData.getAllTokensEarnedThisRound());

        if(clientData.isGateOpen() && clientData.getAvailableFlowCapacity() > 0) {
            // show that client is actively irrigating

        }
    }
}
