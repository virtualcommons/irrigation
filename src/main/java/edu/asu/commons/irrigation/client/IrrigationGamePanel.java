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
 * The game interface screen shown during the round.
 * 
 * FIXME: needs refactoring, should merge IrrigationGameWindow functionality into this panel as well.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class IrrigationGamePanel extends JPanel {

	private static final long serialVersionUID = -3878952269498777014L;

	public IrrigationGameWindow irrigationGameWindow;

	public CanalPanel canalPanel;

	//this contains the CanalPanel
	private JPanel upperPanel = null;
	//this contains the upstream and downstream Panel
	private JPanel jPanelUpStreamWindow = null;
	private JPanel jPanelDownStreamWindow = null;
	private JPanel downloadScreenPanel = null;

	private ScoreBoxPanel scoreBoxPanel;

	public IrrigationClient client;

	public Dimension screenSize;

	public JProgressBar timeRemainingProgressBar = null;

	private JLabel timeRemainingLabel = null; 

	private JTextField timeRemainingTextField;

	private JLabel priorityjLabel = null;

	private JLabel totalAvailableFlowCapacityLabel = null;

	private JLabel maximumAvailableFlowCapacityLabel = null;

	private ClientDataModel clientDataModel;  //  @jve:decl-index=0:

	private MiddleWindowPanel middleWindowPanel;

	private JLabel dashBoardLabel = null;

	private JLabel scoreBoardLabel = null;

	public IrrigationGamePanel(Dimension screenSize, IrrigationClient client) {
		setName("Irrigation Game Panel");
		this.screenSize = screenSize;
		this.client = client;
		initGuiComponents();
	}
	/**
	 * Initializes the main game window.
	 * @return 
	 */
	private void initGuiComponents() {
		timeRemainingLabel = new JLabel();
		timeRemainingLabel.setBounds(new Rectangle(469, 39, 146, 23));
		timeRemainingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timeRemainingLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		timeRemainingLabel.setText("Time Left");
		timeRemainingTextField = new JTextField();
		timeRemainingTextField.setEditable(false);
		//		timeRemainingTextField.setBounds(new Rectangle(620, 39, 61);
		timeRemainingTextField.setText("50 sec");
		timeRemainingTextField.setBackground(Color.white);
		timeRemainingTextField.setFont(new Font("serif", Font.BOLD, 14));
		timeRemainingTextField.setForeground(new Color(102, 204, 255));
		timeRemainingTextField.setHorizontalAlignment(SwingConstants.CENTER);

		setLayout(new BorderLayout());
		add(getDownloadScreenPanel(), BorderLayout.CENTER);
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
			totalAvailableFlowCapacityLabel = new JLabel();
			totalAvailableFlowCapacityLabel.setBounds(new Rectangle(19, 17, 170, 16));
			totalAvailableFlowCapacityLabel.setText("Irrigation capacity: ");
			priorityjLabel = new JLabel();
			priorityjLabel.setBounds(new Rectangle(780, 16, 44, 16));
			priorityjLabel.setText("");
			downloadScreenPanel = new JPanel();
			downloadScreenPanel.setLayout(null);
			downloadScreenPanel.setName("downloadScreenPanel");
			downloadScreenPanel.setBackground(Color.white);
			downloadScreenPanel.add(getUpperPanel(),null);
			downloadScreenPanel.add(getTimeRemainingProgressBar(), null);
			downloadScreenPanel.add(timeRemainingLabel, null);
			downloadScreenPanel.add(timeRemainingTextField,null);

			downloadScreenPanel.add(totalAvailableFlowCapacityLabel, null);
			downloadScreenPanel.add(maximumAvailableFlowCapacityLabel, null);
			downloadScreenPanel.add(dashBoardLabel, null);
			downloadScreenPanel.add(scoreBoardLabel, null);
		}
		return downloadScreenPanel;
	}

	private JPanel getUpperPanel() {
		if (upperPanel == null) {
			upperPanel = new JPanel();
			upperPanel.setLayout(new BorderLayout());
		}
		return upperPanel;
	}

	private JPanel getCanalPanel() {
		canalPanel = new CanalPanel(clientDataModel);
		return canalPanel;
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
		if(irrigationGameWindow == null) {
			irrigationGameWindow = new IrrigationGameWindow(screenSize,client,this);
		}
		return irrigationGameWindow;
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
	public void updateClientStatus(final ClientDataModel clientDataModel) {
		////////////new code////////////////////////////////////////////////////////////
		Runnable createGuiRunnable = new Runnable(){
			public void run() {
				for(final ClientData clientData : clientDataModel.getClientDataMap().values()){
					timeRemainingTextField.setText( clientDataModel.getTimeLeft() +" second(s)" );
					timeRemainingProgressBar.setValue( (clientDataModel.getTimeLeft() / 1000) );
					maximumAvailableFlowCapacityLabel.setText(
							clientData.getGroupDataModel().getMaximumAvailableFlowCapacity() +" cubic feet per second (cfps)"
					);
					middleWindowPanel.update(clientData);
					if (clientDataModel.getPriority() == clientData.getPriority()) {
						irrigationGameWindow.update(clientData);
						//per parameter score panel
						scoreBoxPanel.update(clientData);
					}
					else{
						scoreBoxPanel.update(clientData);
					}
					if(clientData.isGateOpen()){
						canalPanel.openGates(clientData.getPriority());

					}
					else if(clientData.isPaused()){
						canalPanel.closeGates(clientData.getPriority());
					}
					if(clientData.isGateClosed()){
						canalPanel.closeGates(clientData.getPriority());
					}
				}
			}

		};
		try {
			SwingUtilities.invokeAndWait(createGuiRunnable);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		} 
		catch (InvocationTargetException e) {
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
	public void endRound() {
		Runnable createGuiRunnable = new Runnable(){
			public void run() {
				//Refreshing the screen and preparing the main Irrigation Window for the next round
				//provided we have already got the updatedClientDataMap
				irrigationGameWindow.endRound();
				upperPanel.removeAll();
				canalPanel.endRound();
			}
		};

		try {
			SwingUtilities.invokeAndWait(createGuiRunnable);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	/**
	 * assigns the priority to the window and prepares the irrigationWindowMap
	 */
	public void updateContributions(final ClientDataModel clientDataModel) {
		this.clientDataModel = clientDataModel;
		//Here a map is created with the map consisting of the
		//irrigation window as the value and the priority value as the key. This is handled here , because
		//the event contains the priority for every client, and the priority can act as a unique key for the
		//game window.
		fillPanels();

	}



	/**
	 * fills in the panels depending on the priority of the client
	 */
	private void fillPanels() {
		upperPanel.add(getCanalPanel(), BorderLayout.CENTER);
		downloadScreenPanel.add(getJPanelUpStreamWindow(),null);
		downloadScreenPanel.add(getJPanelDownStreamWindow(),null);
		//adding the in between Panel
		downloadScreenPanel.add(getJPanelMiddleWindow(),null);
	}

	private JPanel getJPanelMiddleWindow() {
		if(middleWindowPanel == null){
			if(clientDataModel != null){
				System.out.println("Main Irrigation Window clientGameState is not null");
			}
			middleWindowPanel = new MiddleWindowPanel(clientDataModel);
		}
		return middleWindowPanel;
	}

	public ClientDataModel getClientGameState() {
		return clientDataModel;
	}
}

//  @jve:decl-index=0:visual-constraint="10,-43"
