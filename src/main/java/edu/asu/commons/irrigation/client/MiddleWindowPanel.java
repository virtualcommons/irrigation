/**
 * 
 */
package edu.asu.commons.irrigation.client;


import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.asu.commons.irrigation.server.ClientData;

/**
 * @author Sanket
 *
 */
public class MiddleWindowPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2892921110280857458L;

	//Map<Integer, MiddleScorePanel>middleScorePanelMap = new LinkedHashMap<Integer, MiddleScorePanel>();
	Map<Integer, JLabel>jLabelMap = new LinkedHashMap<Integer, JLabel>();  //  @jve:decl-index=0:
	
	Map<Integer, MiddleScorePanel> middleScorePanelMap = new LinkedHashMap<Integer, MiddleScorePanel>();

	JLabel positionLabel; 
	JLabel availableBandwidthLabel; 
	JLabel filesDownLoadedLabel;
	JLabel tokensCollectedLabel;
	
	
	private ClientDataModel clientGameState;

	public MiddleWindowPanel(ClientDataModel clientGameState){
		super();
		this.clientGameState = clientGameState;
		initialize();
		
		
	}

	private void initialize() {
		// TODO Auto-generated method stub
		//this.setLayout(null);
		
		this.setLayout(null);
		this.setBounds(new Rectangle(13,100 + 100 - 50,1093,100+50+50));
		this.setSize(new Dimension(1093,200));
		
		positionLabel = new JLabel();
		positionLabel.setBounds(new Rectangle(80,50+32+10,150,20));
		positionLabel.setText("Position");
		
		availableBandwidthLabel = new JLabel();
		availableBandwidthLabel.setBounds(new Rectangle(80,50+32+10+20+10,150,20));
		availableBandwidthLabel.setText("Available Flow Capacity");
		
		filesDownLoadedLabel = new JLabel();
		filesDownLoadedLabel.setBounds(new Rectangle(80,50+32+10+20+10+20+10, 150,20));
		filesDownLoadedLabel.setText("Crops Grown");
		
		tokensCollectedLabel = new JLabel();
		tokensCollectedLabel.setBounds(new Rectangle(80,50+32+10+20+10+20+10+20+10, 150,20));
		tokensCollectedLabel.setText("Tokens Collected");
		
		this.add(positionLabel, null);
		this.add(availableBandwidthLabel, null);
		this.add(filesDownLoadedLabel, null);
		this.add(tokensCollectedLabel, null);
		
		for(int i=0;i<clientGameState.getRoundConfiguration().getClientsPerGroup();i++){
			/*JLabel jTextLabel = new JLabel();
			jLabelMap.put(new Integer(i), jTextLabel);
			jTextLabel.setBounds(new Rectangle((258 + 20 + i*198),35,60,50));
			this.add(jTextLabel,null);
		*/
		if(this.clientGameState != null)
			System.out.println("Middle Window clientGameState is not null");
		MiddleScorePanel middleScorePanel = new MiddleScorePanel(i,this.clientGameState);
		middleScorePanel.setBounds(new Rectangle((258 + 20 + i*198)-20,0,60,100+50+50));
		middleScorePanelMap.put(new Integer(i), middleScorePanel);
		this.add(middleScorePanel, null);
			
		}

	}

	public void update(ClientData clientData) {
		// TODO Auto-generated method stub
		//jLabelMap.get(new Integer(clientData.getPriority())).setText(new Integer(clientData.getFilesDownloaded()).toString());
		middleScorePanelMap.get(new Integer(clientData.getPriority())).update(clientData);
	}
	
}
