package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
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
	private JPanel mainIrrigationPanel = null;

	private ScoreBoxPanel scoreBoxPanel;

	public IrrigationClient client;

	public Dimension screenSize;

	public JProgressBar timeRemainingProgressBar;

	private JLabel timeRemainingLabel;

	private JTextField timeRemainingTextField;

	private JButton gateSwitchButton;

	private JLabel irrigationCapacityLabel;

	//	private JLabel maximumAvailableFlowCapacityLabel = null;

	private ClientDataModel clientDataModel;  //  @jve:decl-index=0:

	private MiddleWindowPanel middleWindowPanel;

	//	private JLabel dashBoardLabel = null;

	//	private JLabel scoreBoardLabel = null;

	private JLabel totalTokensEarnedLabel;

	private JLabel tokensEarnedLabel;

	private JLabel tokensNotInvestedLabel;

	private JLabel waterUsedLabel;

	private JTextField waterUsedTextField;

	private JTextField tokensNotInvestedTextField;

	private JTextField tokensEarnedTextField;

	private JTextField totalTokensEarnedTextField;

	private boolean open;

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


		JPanel topmostPanel = new JPanel();
		topmostPanel.setLayout(new BoxLayout(topmostPanel, BoxLayout.X_AXIS));
		topmostPanel.add(getIrrigationCapacityLabel());

		JPanel timeRemainingPanel = new JPanel();
		timeRemainingPanel.setLayout(new BoxLayout(timeRemainingPanel, BoxLayout.Y_AXIS));
		timeRemainingPanel.add(getTimeRemainingProgressBar());
		timeRemainingPanel.add(getTimeRemainingPanel());

		topmostPanel.add(timeRemainingPanel);


		setLayout(new BorderLayout());
		add(topmostPanel, BorderLayout.PAGE_START);
		add(getUpperPanel(), BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.PAGE_END);
	}

	private JPanel getBottomPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(getGateSwitchButton());

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(4, 2));
		bottomPanel.add(getWaterUsedLabel());
		bottomPanel.add(getWaterUsedTextField());
		bottomPanel.add(getTokensNotInvestedLabel());
		bottomPanel.add(getTokensNotInvestedTextField());
		bottomPanel.add(getTokensEarnedLabel());
		bottomPanel.add(getTokensEarnedTextField());
		bottomPanel.add(getTotalTokensEarnedLabel());
		bottomPanel.add(getTotalTokensEarnedTextField());

		panel.add(bottomPanel);
		return panel;
	}

	private JTextField getWaterUsedTextField() {
		if (waterUsedTextField == null) {
			waterUsedTextField = new JTextField();
			waterUsedTextField.setEditable(false);
		}
		return waterUsedTextField;
	}

	private JLabel getWaterUsedLabel() {
		if (waterUsedLabel == null) {
			waterUsedLabel = new JLabel("Water units used: ");
		}
		return waterUsedLabel;
	}

	private JTextField getTokensNotInvestedTextField() {
		if (tokensNotInvestedTextField == null) {
			tokensNotInvestedTextField = new JTextField();
			tokensNotInvestedTextField.setEditable(false);
		}
		return tokensNotInvestedTextField;
	}
	private JLabel getTokensNotInvestedLabel() {
		if (tokensNotInvestedLabel == null) {
			tokensNotInvestedLabel = new JLabel("Tokens not invested: ");
		}
		return tokensNotInvestedLabel;
	}

	private JTextField getTokensEarnedTextField() {
		if (tokensEarnedTextField == null) {
			tokensEarnedTextField = new JTextField();
			tokensEarnedTextField.setEditable(false);
		}
		return tokensEarnedTextField;
	}
	private JLabel getTokensEarnedLabel() {
		if (tokensEarnedLabel == null) {
			tokensEarnedLabel = new JLabel("Tokens earned by crop production: ");
		}
		return tokensEarnedLabel;
	}

	private JTextField getTotalTokensEarnedTextField() {
		if (totalTokensEarnedTextField == null) {
			totalTokensEarnedTextField = new JTextField();
			totalTokensEarnedTextField.setEditable(false);
		}
		return totalTokensEarnedTextField;

	}

	private JLabel getTotalTokensEarnedLabel() {
		if (totalTokensEarnedLabel == null) {
			totalTokensEarnedLabel = new JLabel("Total tokens earned this round: ");
		}
		return totalTokensEarnedLabel;
	}

	private JPanel getTimeRemainingPanel() {
		timeRemainingLabel = new JLabel("Time left: ");
		timeRemainingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timeRemainingLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		timeRemainingTextField = new JTextField("50 sec");
		timeRemainingTextField.setEditable(false);
		timeRemainingTextField.setBackground(Color.white);
		timeRemainingTextField.setFont(new Font("serif", Font.BOLD, 14));
		timeRemainingTextField.setForeground(new Color(102, 204, 255));
		timeRemainingTextField.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(timeRemainingLabel);
		panel.add(timeRemainingTextField);
		return panel;
	}

	private JLabel getIrrigationCapacityLabel() {
		if (irrigationCapacityLabel == null) {
			irrigationCapacityLabel = new JLabel("Irrigation capacity: ");
		}
		return irrigationCapacityLabel;
	}

	//	private JPanel getMainIrrigationPanel() {
	//		if (mainIrrigationPanel == null) {
	//			irrigationCapacityLabel = new JLabel();
	//			irrigationCapacityLabel.setText("Irrigation capacity: ");
	//			mainIrrigationPanel = new JPanel();
	//
	//
	//			mainIrrigationPanel.setName("downloadScreenPanel");
	//			mainIrrigationPanel.setBackground(Color.white);
	//			mainIrrigationPanel.add(getUpperPanel(),null);
	//			mainIrrigationPanel.add(timeRemainingLabel, null);
	//			mainIrrigationPanel.add(timeRemainingTextField,null);
	//
	//			mainIrrigationPanel.add(irrigationCapacityLabel, null);
	//			mainIrrigationPanel.add(maximumAvailableFlowCapacityLabel, null);
	//			mainIrrigationPanel.add(dashBoardLabel, null);
	//			mainIrrigationPanel.add(scoreBoardLabel, null);
	//		}
	//		return mainIrrigationPanel;
	//	}

	private JPanel getUpperPanel() {
		if (upperPanel == null) {
			upperPanel = new JPanel();
			upperPanel.setLayout(new BorderLayout());
			upperPanel.add(getScoreBoxPanel(), BorderLayout.PAGE_END);
		}
		return upperPanel;
	}


	private JButton getGateSwitchButton() {
		if (gateSwitchButton == null) {
			gateSwitchButton = new JButton("Open Gate");
			gateSwitchButton.setPreferredSize(new Dimension(60, 60));
			gateSwitchButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					open = !open;
					gateSwitchButton.setText( open ? "Close gate" : "Open gate");
					if (open) {
						client.openGate();
					}
					else {
						client.closeGate();
					}
				}
			});
		}
		return gateSwitchButton;
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
			scoreBoxPanel = new ScoreBoxPanel();
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
		Runnable createGuiRunnable = new Runnable(){
			public void run() {
				int timeLeft = clientDataModel.getTimeLeft();
				int irrigationCapacity = clientDataModel.getGroupDataModel().getMaximumAvailableFlowCapacity();
				timeRemainingTextField.setText( timeLeft + " sec");
				timeRemainingProgressBar.setValue( timeLeft );
				irrigationCapacityLabel.setText(
						String.format("Irrigation capacity: %d cubic feet per second (cfps)",
								irrigationCapacity));
				for(final ClientData clientData : clientDataModel.getClientDataMap().values()){
					if (clientData.isGateOpen()) {
						canalPanel.openGates(clientData.getPriority());
					}
					else if(clientData.isPaused()){
						canalPanel.closeGates(clientData.getPriority());
					}
					else if(clientData.isGateClosed()){
						canalPanel.closeGates(clientData.getPriority());
					}
				}
				ClientData clientData = clientDataModel.getClientData();
				waterUsedTextField.setText("" + clientData.getWaterUsed());
				tokensNotInvestedTextField.setText("" + clientData.getUninvestedTokens());
				tokensEarnedTextField.setText("" + clientData.waterToTokenFunction());
				totalTokensEarnedTextField.setText("" + clientData.getTotalTokensEarned());
				scoreBoxPanel.update(clientDataModel);
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
	}


	/**
	 * fills in the panels depending on the priority of the client
	 */
	public void startRound() {
		upperPanel.add(getCanalPanel(), BorderLayout.CENTER);
		open = false;
		scoreBoxPanel.initialize(clientDataModel);
		revalidate();
		//		mainIrrigationPanel.add(getJPanelUpStreamWindow(),null);
		//		mainIrrigationPanel.add(getJPanelDownStreamWindow(),null);
		//adding the in between Panel
		//		mainIrrigationPanel.add(getJPanelMiddleWindow(),null);
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

	public ClientDataModel getClientDataModel() {
		return clientDataModel;
	}
}

