package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.asu.commons.irrigation.server.ClientData;

/**
 * $Id$
 * 
 * Displays the canal animation and so on.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class MainIrrigationGameWindow extends JPanel {

	//public ChartWindowPanelTokenBandwidth xySeriesDemo;

	public IrrigationGameWindow controlPanel;
	
	//public ActivitySummaryPanel activitySummaryWindow;
	
	public CanalPanel upperPanel;
	
	private static final long serialVersionUID = 1L;

	private JPanel jPanelMain = null;
	//this contains the CanalPanel
	private JPanel jPanelUpperWindow = null;
	//this contains the upstream and downstream Panel
	private JPanel jPanelUpStreamWindow = null;
	private JPanel jPanelDownStreamWindow = null;
	private JPanel downloadScreenPanel = null;
	
	private ScoreBoxPanel scoreBoxPanel;
	
	/**
	 * This is the default constructor
	 */
	public IrrigationClient client;

	public Dimension screenSize;

	public JProgressBar timeRemainingProgressBar = null;

	private JLabel timeRemainingLabel = null; 

	private JTextField timeRemainingTextField;

	private JLabel priorityjLabel = null;

	private JLabel totalContributedBandwidthjLabel = null;

	private JLabel maximumAvailableFlowCapacityLabel = null;

	private IrrigationClientGameState clientGameState;  //  @jve:decl-index=0:

	private MiddleWindowPanel middleWindowPanel;

	private JLabel dashBoardLabel = null;

	private JLabel scoreBoardLabel = null;

	public MainIrrigationGameWindow(Dimension screenSize, IrrigationClient client) {
		super();
		this.screenSize = screenSize;
		this.client = client;
		initialize();
	}
	/**
	 * Initializes the main game window.
	 * @return 
	 */
	private void initialize() {
		timeRemainingLabel = new JLabel();
		timeRemainingLabel.setBounds(new Rectangle(469, 39, 146, 23));
		timeRemainingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timeRemainingLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		timeRemainingLabel.setText("TIME REMAINING");
		timeRemainingTextField = new JTextField();
		timeRemainingTextField.setEditable(false);
		timeRemainingTextField.setBounds(new Rectangle(620, 39, 61, 23));
		timeRemainingTextField.setText("50 sec");
		timeRemainingTextField.setBackground(Color.white);
		timeRemainingTextField.setFont(new Font("serif", Font.BOLD, 14));
		timeRemainingTextField.setForeground(new Color(102, 204, 255));
		timeRemainingTextField.setHorizontalAlignment(SwingConstants.CENTER);

		this.setLayout(new BorderLayout(4,4));
		this.setSize(1130, 558);
		this.add(getJPanelMain(),null);
		
	}

	private JPanel getJPanelMain() {
		// TODO Auto-generated method stub
		if(jPanelMain == null){
			jPanelMain = new JPanel();
			jPanelMain.setLayout(new BorderLayout(4,4));
			jPanelMain.setBackground(Color.WHITE);
	        jPanelMain.setForeground(Color.BLACK);
	        jPanelMain.add(getDownloadScreenPanel(), BorderLayout.CENTER);
	        return jPanelMain;
			}
		return jPanelMain;
	}

	private JPanel getDownloadScreenPanel() {
		if(downloadScreenPanel == null){
			scoreBoardLabel = new JLabel();
			scoreBoardLabel.setBounds(new Rectangle(582,225+100+35,530,20));
			scoreBoardLabel.setHorizontalAlignment(SwingConstants.CENTER);
			scoreBoardLabel.setText("SUMMARY SCOREBOARD FOR YOU AND OTHER PLAYERS");
			dashBoardLabel = new JLabel();
			dashBoardLabel.setBounds(new Rectangle(13,225+100+35,530,20));
			dashBoardLabel.setHorizontalAlignment(SwingConstants.CENTER);
			dashBoardLabel.setText("YOUR DASHBOARD");
			maximumAvailableFlowCapacityLabel = new JLabel();
			maximumAvailableFlowCapacityLabel.setBounds(new Rectangle(200, 18, 55, 17));
			maximumAvailableFlowCapacityLabel.setText("");
			totalContributedBandwidthjLabel = new JLabel();
			totalContributedBandwidthjLabel.setBounds(new Rectangle(19, 17, 170, 16));
			totalContributedBandwidthjLabel.setText("Total Water Availability Capacity: ");
			priorityjLabel = new JLabel();
			priorityjLabel.setBounds(new Rectangle(780, 16, 44, 16));
			priorityjLabel.setText("");
			downloadScreenPanel = new JPanel();
			downloadScreenPanel.setLayout(null);
			downloadScreenPanel.setName("downloadScreenPanel");
			downloadScreenPanel.setBackground(Color.white);
			downloadScreenPanel.add(getJPanelUpperWindow(),null);
			downloadScreenPanel.add(getTimeRemainingProgressBar(), null);
			downloadScreenPanel.add(timeRemainingLabel, null);
			downloadScreenPanel.add(timeRemainingTextField,null);
			//downloadScreenPanel.add(upStreamjLabel, null);
			//downloadScreenPanel.add(downStreamjLabel, null);
			//downloadScreenPanel.add(jLabelgif1, null);
			//downloadScreenPanel.add(jLabelgif2, null);
			//downloadScreenPanel.add(priorityjLabel, null);
			downloadScreenPanel.add(totalContributedBandwidthjLabel, null);
			downloadScreenPanel.add(maximumAvailableFlowCapacityLabel, null);
			downloadScreenPanel.add(dashBoardLabel, null);
			downloadScreenPanel.add(scoreBoardLabel, null);
		}
		return downloadScreenPanel;
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelUpperWindow() {
		if (jPanelUpperWindow == null) {
			jPanelUpperWindow = new JPanel();
			jPanelUpperWindow.setLayout(null);
			jPanelUpperWindow.setBounds(new Rectangle(13, 64, 1098, 123));
		}
		return jPanelUpperWindow;
	}

	private JPanel getUpperPanel(IrrigationClientGameState clientGameState) {
		// TODO Auto-generated method stub
			upperPanel = new CanalPanel(clientGameState);
			upperPanel.setSize(new Dimension(1098, 123));
		
		return upperPanel;
	}

	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelUpStreamWindow() {
		if (jPanelUpStreamWindow == null) {
			jPanelUpStreamWindow = new JPanel();
			jPanelUpStreamWindow.setLayout(null);
			jPanelUpStreamWindow.setBackground(new Color(186, 226, 237));
			jPanelUpStreamWindow.setBounds(new Rectangle(13, 225+100+50, 530, 326));
			jPanelUpStreamWindow.add(getControlPanel(), null);
		}
		return jPanelUpStreamWindow;
	}

	private IrrigationGameWindow getControlPanel() {
		// TODO Auto-generated method stub
		if(controlPanel == null){
			controlPanel = new IrrigationGameWindow(screenSize,client,this);
			return controlPanel;
		}
		return controlPanel;
	}
	/**
	 * This method initializes jPanel3
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelDownStreamWindow() {
		if (jPanelDownStreamWindow == null) {
			jPanelDownStreamWindow = new JPanel();
			jPanelDownStreamWindow.setLayout(null);
			jPanelDownStreamWindow.setBackground(new Color(186, 226, 237));
			jPanelDownStreamWindow.setBounds(new Rectangle(582, 225 + 100+50, 530, 326));
		    jPanelDownStreamWindow.add(getScoreBoxPanel(),null);
		}
		return jPanelDownStreamWindow;
	}

	private ScoreBoxPanel getScoreBoxPanel() {
		// TODO Auto-generated method stub
		if(scoreBoxPanel == null){
			scoreBoxPanel = new ScoreBoxPanel(client);
			
			return scoreBoxPanel;
		}
		return scoreBoxPanel;
	}
	
	
	/**
	 * This method initializes TimeRemainingjProgressBar
	 *
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getTimeRemainingProgressBar() {
		if (timeRemainingProgressBar == null) {
			timeRemainingProgressBar = new JProgressBar(0, 50);
			timeRemainingProgressBar.setBounds(new Rectangle(360, 15, 370, 17));
			timeRemainingProgressBar.setBackground(Color.white);
			timeRemainingProgressBar.setForeground(new Color(51, 153, 255));
		}
		return timeRemainingProgressBar;
	}
	//this event gets called every second in the experiment.
	/**
	 * Shoudl be invoked every second throughout the experiment, from a ClientUpdateEvent sent by the server.
	 */
	public void updateClientStatus(final IrrigationClientGameState clientGameState) {
	    ////////////new code////////////////////////////////////////////////////////////
	    Runnable createGuiRunnable = new Runnable(){
	        public void run() {
	            for(final ClientData clientData : clientGameState.getClientDataMap().values()){
	                timeRemainingTextField.setText( clientGameState.getTimeLeft() +" second(s)" );
	                timeRemainingProgressBar.setValue( (clientGameState.getTimeLeft() / 1000) );
	                maximumAvailableFlowCapacityLabel.setText(
	                        clientData.getGroupDataModel().getMaximumAvailableFlowCapacity() +" cubic feet per second (cfps)"
	                        );
	                middleWindowPanel.update(clientData);
	                if (clientGameState.getPriority() == clientData.getPriority()) {
	                    controlPanel.update(clientData);
	                    //per parameter score panel
	                    scoreBoxPanel.update(clientData);
	                }
	                else{
	                    scoreBoxPanel.update(clientData);
	                }
	                if(clientData.isGateOpen()){
	                    upperPanel.openGates(clientData.getPriority());

	                }
	                else if(clientData.isPaused()){
	                    upperPanel.closeGates(clientData.getPriority());
	                }
	                if(clientData.isGateClosed()){
	                    upperPanel.closeGates(clientData.getPriority());
	                }
	            }
	        }

	    };
		try {
			SwingUtilities.invokeAndWait(createGuiRunnable);
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/**
	 * changes the file button color to red when started downloading
	 * @param file
	 */

	/*
	 * updates the irrigation window
	 */
	public void updateEndRoundEvent() {
		// TODO Auto-generated method stub
		System.out.println("End of round.");
		Runnable createGuiRunnable = new Runnable(){
			public void run() {
				 //TODO Auto-generated method stub
				 //Refreshing the screen and preparing the main Irrigation Window for the next round
				 //provided we have already got the updatedClientDataMap
				scoreBoxPanel.endRound();
				controlPanel.endRound();
				jPanelUpperWindow.removeAll();
				upperPanel.endRound();
			}
		};
		
			try {
				SwingUtilities.invokeAndWait(createGuiRunnable);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	/**
	 * assigns the priority to the window and prepares the irrigationWindowMap
	 */
	public void updateContributions(final IrrigationClientGameState clientGameState) {
	    this.clientGameState = clientGameState;
		//Here a map is created with the map consisting of the
	    //irrigation window as the value and the priority value as the key. This is handled here , because
	    //the event contains the priority for every client, and the priority can act as a unique key for the
	    //game window.
		
		//checking the client priority and assigning the activity summary and client panel to the 
		//doownloadScreenPanel
		fillPanels(clientGameState);
		
	       //This is helpful, if we need to scale the game to more than two users. We can declare
	        //more panels like middleStream Window, etc and add them in this switch case statement

	        //case 2 : irrigationWindowMap.put(new Integer(clientData.getPriority()), middleStreamWindow);
	 }
    
	
	
	/**
	 * fills in the panels depending on the priority of the client
	 */
	public void fillPanels(IrrigationClientGameState clientGameState) {
		// TODO Auto-generated method stub
		jPanelUpperWindow.add(getUpperPanel(clientGameState));
		//switch(clientGameState.getPriority()){
		
		downloadScreenPanel.add(getJPanelUpStreamWindow(),null);
	 	downloadScreenPanel.add(getJPanelDownStreamWindow(),null);
		//adding the in between Panel
	 	downloadScreenPanel.add(getJPanelMiddleWindow(),null);
		 /*case 0 : downloadScreenPanel.add(getJPanelUpStreamWindow(0),null);
    		 	  downloadScreenPanel.add(getJPanelDownStreamWindow(0),null);
    		 	 //Assigning the Priorities on the Priority Label
    		 	 break;

		 case 1 : downloadScreenPanel.add(getJPanelUpStreamWindow(1),null);
    	 		  downloadScreenPanel.add(getJPanelDownStreamWindow(1),null);
    	 		 //Assigning the Priorities on the Priority Label
    	 		 break;
*/		}
       
	private JPanel getJPanelMiddleWindow() {
		// TODO Auto-generated method stub
		if(middleWindowPanel == null){
			if(clientGameState != null){
				System.out.println("Main Irrigation Window clientGameState is not null");
			}
			middleWindowPanel = new MiddleWindowPanel(clientGameState);
		}
		return middleWindowPanel;
	}
	
	public IrrigationClientGameState getClientGameState() {
		// TODO Auto-generated method stub
		return clientGameState;
	}
}

  //  @jve:decl-index=0:visual-constraint="10,-43"
