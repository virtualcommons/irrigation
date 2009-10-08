package edu.asu.commons.irrigation.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.util.ResourceLoader;

/**
 * $Id$
 * 
 * 
 *
 * @author Sanket Joshi
 * @version $Rev$
 */
public class MiddleScorePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1329890496417714604L;
	
	private JLabel positionText;
	
	private JLabel currentBandwidthText;
		
	private JLabel filesDownloadedText;
	
	private JLabel tokensText;
	
	private JLabel fileLabel;
	
	private int priority;
	
	private ClientData clientData;  //  @jve:decl-index=0:

	private IrrigationClientGameState clientGameState;  //  @jve:decl-index=0:
	
	public MiddleScorePanel(int priority,IrrigationClientGameState clientGameState){
		super();
		this.priority = priority;
		/*this.clientGameState = clientGameState;
		*/
		initialize(clientGameState);
	}

	private void initialize(IrrigationClientGameState clientGameState) {
		// TODO Auto-generated method stub
		/*GridLayout gridLayout = new GridLayout();
		gridLayout.setColumns(1);
		gridLayout.setRows(4);
		this.setLayout(gridLayout);
		*/
		this.setLayout(null);
		this.clientGameState = clientGameState;
		/*if(this.clientGameState == null)
			System.out.println("client GameState is null in MiddleScorePanel initialize");
		*/
		if(clientGameState.getPriority() == priority)
		this.setBackground(Color.GREEN);
		else
			this.setBackground(Color.YELLOW);
		this.setSize(new Dimension(60,200));
		
//		URL fileUrl = ResourceLoader.getResourceAsUrl("images/file.gif");
		this.add(getFileLabel());
		this.add(getPositionText(priority),null);
		this.add(getFilesDownLoadedText(priority),null);
		this.add(getCurrentBandwidth(),null);
		this.add(getTokensCollected(),null);
		
		
	}

	private JLabel getFileLabel() {
		// TODO Auto-generated method stub
		if(fileLabel == null){
			//URL fileUrl = getClass().getResource("images/file.gif");
			URL fileUrl = ResourceLoader.getResourceAsUrl("images/file.gif");
			fileLabel = new JLabel();
			fileLabel.setBounds(new Rectangle(10,50,30,32));
			fileLabel.setIcon(new ImageIcon(fileUrl));
			fileLabel.setVisible(false);
		}
		return fileLabel;
	}

	private JLabel getTokensCollected() {
		// TODO Auto-generated method stub
		if(tokensText == null){
			tokensText = new JLabel();
			tokensText.setBounds(new Rectangle(5,50+32+10+20+10+20+10+20+10,30,20));
			tokensText.setText("");
		}
		return tokensText;
	}

	private JLabel getCurrentBandwidth() {
		// TODO Auto-generated method stub
		if(currentBandwidthText == null){
			currentBandwidthText = new JLabel();
			currentBandwidthText.setBounds(new Rectangle(5,50+32+10+20+10,30,20));
			currentBandwidthText.setText("");
		}
		return currentBandwidthText;
	}

	private JLabel getPositionText(int priority) {
		// TODO Auto-generated method stub
		if(positionText == null){
			positionText = new JLabel();
			positionText.setBounds(new Rectangle(5,50+32+10,30,20));
			switch(priority){
			case 0: positionText.setText("A");
					break;
			
			case 1: positionText.setText("B");
					break;
			
			case 2: positionText.setText("C");
					break;
			
			case 3: positionText.setText("D");
					break;
					
			case 4: positionText.setText("E");
			break;
			
			}	
		}
		
		return positionText;
	}
	

	private JLabel getFilesDownLoadedText(int priority) {
		// TODO Auto-generated method stub
		if(filesDownloadedText == null){
			filesDownloadedText = new JLabel();
			filesDownloadedText.setBounds(new Rectangle(5,50+32+10+20+10+20+10,30,20));
			filesDownloadedText.setText(new Integer(priority).toString());
		}
		return filesDownloadedText;
	}

	public void update(ClientData clientData) {
		// TODO Auto-generated method stub

		this.clientData = clientData;
		
		if(clientData.getAvailableFlowCapacity() > 25){
			currentBandwidthText.setText(new Double(25).toString());
		}
		else
			currentBandwidthText.setText(new Double(clientData.getAvailableFlowCapacity()).toString());
		
		filesDownloadedText.setText(new Integer(clientData.getCropsGrown()).toString());
		tokensText.setText(new Integer(clientData.getTotalTokensEarned()).toString());
		
		if(clientData.isGateOpen() == true && clientData.getAvailableFlowCapacity() > 0)
			fileLabel.setVisible(true);
		else
			fileLabel.setVisible(false);
	}
}
