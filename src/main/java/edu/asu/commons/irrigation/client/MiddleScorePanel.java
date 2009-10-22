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

    private JLabel availableWaterLabel;

    private JLabel waterCollectedLabel;

    private JLabel tokensEarnedLabel;
    
    private JLabel tokensNotInvestedLabel;
    private JLabel totalTokensEarnedLabel;

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
        this.add(getAvailableWaterLabel(),null);
        this.add(getTokensEarnedLabel(),null);
//        this.add(getTokensNotInvestedLabel(),null);
//        this.add(getTotalTokensEarnedLabel(),null);
    }
    
    private JLabel getTotalTokensEarnedLabel() {
    	if (totalTokensEarnedLabel == null) {
    		totalTokensEarnedLabel = new JLabel();
    		totalTokensEarnedLabel.setBounds(new Rectangle(5,10+32+10+20+10+20+10+20+10+30+30,30,20));
    	}
    	return totalTokensEarnedLabel;
    }
    
    private JLabel getTokensNotInvestedLabel() {
    	if (tokensNotInvestedLabel == null) {
    		tokensNotInvestedLabel = new JLabel(String.valueOf(clientData.getUninvestedTokens()));
    		tokensNotInvestedLabel.setBounds(new Rectangle(5,10+32+10+20+10+20+10+20+10+30,30,20));
    	}
    	return tokensNotInvestedLabel;
    }

    private JLabel getTokensEarnedLabel() {
        if(tokensEarnedLabel == null){
            tokensEarnedLabel = new JLabel();
            tokensEarnedLabel.setBounds(new Rectangle(5,10+32+10+20+10+20+10+20+10,30,20));
        }
        return tokensEarnedLabel;
    }

    private JLabel getAvailableWaterLabel() {
        if(availableWaterLabel == null){
            availableWaterLabel = new JLabel();
            availableWaterLabel.setBounds(new Rectangle(5,10+32+10+20+10,30,20));
        }
        return availableWaterLabel;
    }

    private JLabel getPositionLabel() {
        if (positionText == null){
            positionText = new JLabel(clientData.getPriorityString());
            positionText.setBounds(new Rectangle(5,10+32+10,30,20));
        }
        return positionText;
    }


    private JLabel getWaterCollectedLabel() {
        if(waterCollectedLabel == null){
            waterCollectedLabel = new JLabel("0");
            waterCollectedLabel.setBounds(new Rectangle(5,10+32+10+20+10+20+10,30,20));
        }
        return waterCollectedLabel;
    }

    public void update(ClientData clientData) {
        this.clientData = clientData;
        availableWaterLabel.setText("" + clientData.getAvailableFlowCapacity());
        waterCollectedLabel.setText("" + clientData.getWaterCollected());
        tokensEarnedLabel.setText("" + clientData.getTokensEarnedFromWaterCollected());

        if(clientData.isGateOpen() && clientData.getAvailableFlowCapacity() > 0) {
            // show that client is actively irrigating

        }
    }
}
