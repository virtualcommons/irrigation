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

    private JLabel availableFlowCapacityLabel;

    private JLabel waterCollectedLabel;

    private JLabel tokensCollectedLabel;

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
        this.add(getPositionLabel(),null);
        this.add(getWaterCollectedLabel(),null);
        this.add(getAvailableFlowCapacityLabel(),null);
        this.add(getTokensCollectedLabel(),null);


    }

    private JLabel getTokensCollectedLabel() {
        if(tokensCollectedLabel == null){
            tokensCollectedLabel = new JLabel();
            tokensCollectedLabel.setBounds(new Rectangle(5,50+32+10+20+10+20+10+20+10,30,20));
            tokensCollectedLabel.setText("");
        }
        return tokensCollectedLabel;
    }

    private JLabel getAvailableFlowCapacityLabel() {
        if(availableFlowCapacityLabel == null){
            availableFlowCapacityLabel = new JLabel();
            availableFlowCapacityLabel.setBounds(new Rectangle(5,50+32+10+20+10,30,20));
            availableFlowCapacityLabel.setText("");
        }
        return availableFlowCapacityLabel;
    }

    private JLabel getPositionLabel() {
        if (positionText == null){
            positionText = new JLabel(clientData.getPriorityString());
            positionText.setBounds(new Rectangle(5,50+32+10,30,20));
        }
        return positionText;
    }


    private JLabel getWaterCollectedLabel() {
        if(waterCollectedLabel == null){
            waterCollectedLabel = new JLabel("0");
            waterCollectedLabel.setBounds(new Rectangle(5,50+32+10+20+10+20+10,30,20));
        }
        return waterCollectedLabel;
    }

    public void update(ClientData clientData) {
        this.clientData = clientData;
        availableFlowCapacityLabel.setText("" + clientData.getAvailableFlowCapacity());
        waterCollectedLabel.setText("" + clientData.getWaterCollected());
        tokensCollectedLabel.setText("" + clientData.getAllTokensEarnedThisRound());

        if(clientData.isGateOpen() && clientData.getAvailableFlowCapacity() > 0) {
            // show that client is actively irrigating

        }
    }
}
