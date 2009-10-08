/**
 * 
 */
package edu.asu.commons.irrigation.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.asu.commons.irrigation.server.ClientData;

/**
 * @author Sanket
 *
 */
public class ActivitySummaryPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2400441194759984763L;
	private JLabel currentBandwidthTextjLabel = null;
	private JLabel filesDownloadedjLabel = null;
	private JLabel filesDownloadedTextjLabel = null;
	private JLabel tokensCollectedjLabel = null;
	private JLabel currentBandwidthjLabel = null;
	private JLabel tokensCollectedTextjLabel = null;
	
	private JLabel positionjLabel = null;
	private JLabel positionTextjLabel = null;
	
	private ClientData clientData;  //  @jve:decl-index=0:
	/**
	 * This method initializes 
	 * 
	 */
	public ActivitySummaryPanel(int i) {
		super();
		initialize(i);
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize(int i) {
        tokensCollectedTextjLabel = new JLabel();
        tokensCollectedTextjLabel.setText("");
        currentBandwidthjLabel = new JLabel();
        currentBandwidthjLabel.setText("Current Flow Capacity:");
        tokensCollectedjLabel = new JLabel();
        tokensCollectedjLabel.setText("Tokens Collected:");
        filesDownloadedTextjLabel = new JLabel();
        filesDownloadedTextjLabel.setText("");
        filesDownloadedjLabel = new JLabel();
        filesDownloadedjLabel.setText("Crops Grown:");
        currentBandwidthTextjLabel = new JLabel();
        currentBandwidthTextjLabel.setText("");
        
        positionjLabel = new JLabel();
        positionjLabel.setText("Position :");
        
        positionTextjLabel = new JLabel();
        positionTextjLabel.setText("");
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.setRows(4);
        gridLayout.setColumns(2);
        this.setLayout(gridLayout);
        this.setSize(new Dimension(430,126));
        switch(i)
        {
        case 0: this.setBackground(Color.PINK);
        		break;
        	
        case 1: this.setBackground(Color.GREEN);
        		break;
        		
        case 2: this.setBackground(Color.WHITE);
        		break;
        		
        case 3: this.setBackground(Color.CYAN);
        		break;
        
        case 4: this.setBackground(Color.YELLOW);
        		break;
        }
        this.add(positionjLabel, null);
        this.add(positionTextjLabel, null);
        this.add(currentBandwidthjLabel, null);
        this.add(currentBandwidthTextjLabel, null);
        this.add(filesDownloadedjLabel, null);
        this.add(filesDownloadedTextjLabel, null);
        this.add(tokensCollectedjLabel, null);
        this.add(tokensCollectedTextjLabel, null);
	}

	public void update(ClientData data) {
		this.clientData = data;
		// TODO Auto-generated method stub
		//System.out.println("Client Data Prioriity"+clientData.getPriority()+"Avaiable bandwidth"+
				//clientData.getAvailableBandwidth());
		
		Runnable createGuiRunnable = new Runnable(){
			public void run() {
				//System.out.println("In the UPdate facilitator");
				positionTextjLabel.setText(new Integer(clientData.getPriority()).toString());
				tokensCollectedTextjLabel.setText(new Integer(clientData.getTotalTokensEarned()).toString());
				if(clientData.getAvailableFlowCapacity() > 25){
					currentBandwidthTextjLabel.setText(new Double(25).toString());
				}
				else
					currentBandwidthTextjLabel.setText(new Double(clientData.getAvailableFlowCapacity()).toString());
				filesDownloadedTextjLabel.setText(new Integer(clientData.getCropsGrown()).toString());	
			}
		};
		 SwingUtilities.invokeLater(createGuiRunnable);
	}

	public void endRound() {
		// TODO Auto-generated method stub
		currentBandwidthTextjLabel.setText("");
		filesDownloadedTextjLabel.setText("");
		tokensCollectedTextjLabel.setText("");
		
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
