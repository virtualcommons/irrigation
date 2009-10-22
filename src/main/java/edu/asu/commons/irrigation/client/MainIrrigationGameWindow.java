package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;

import edu.asu.commons.irrigation.server.ClientData;
import edu.asu.commons.util.HtmlEditorPane;



public class MainIrrigationGameWindow extends JPanel {

	//public ChartWindowPanelTokenBandwidth xySeriesDemo;

	public IrrigationGameWindow controlPanel;

	//public ActivitySummaryPanel activitySummaryWindow;

	public CanalPanel canalPanel;

	private static final long serialVersionUID = 1L;

	private JPanel jPanelMain = null;
	//this contains the CanalPanel
	private JPanel centerPanel = null;
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

	public JProgressBar timeLeftProgressBar;

	public JProgressBar timeRemainingjProgressBar = null;

	private JLabel timeRemainingjLabel = null; 

	private JTextField timeRemainingjText;

	private JLabel priorityjLabel = null;

	private JLabel totalContributedBandwidthjLabel = null;

	private JLabel totalContributedBandwidthTextjLabel = null;

	private ClientDataModel clientDataModel;  //  @jve:decl-index=0:

	private MiddleWindowPanel middleWindowPanel;

	private JLabel dashBoardLabel = null;

	private JLabel scoreBoardLabel = null;

	private JButton gateSwitchButton;

	protected boolean open;

	private JTextField waterCollectedTextField;

	private JLabel waterCollectedLabel;

	private JTextField tokensNotInvestedTextField;

	private JLabel tokensNotInvestedLabel;

	private JTextField tokensEarnedTextField;

	private JLabel tokensEarnedLabel;

	private JTextField totalTokensEarnedTextField;

	private JLabel totalTokensEarnedLabel;

	private JLabel irrigationCapacityLabel;

	private JTextField irrigationCapacityTextField;

	private HtmlEditorPane waterCollectedToTokensTable;

	private JScrollPane waterCollectedToTokensScrollPane;

	public MainIrrigationGameWindow(IrrigationClient client) {
		super();
		this.client = client;
		setName("main irrigation game window");
		initialize();
	}
	/**
	 * This method initializes this
	 *
	 * @return void
	 */

	private void initialize() {
		timeRemainingjLabel = new JLabel();
		timeRemainingjLabel.setBounds(new Rectangle(469, 39, 146, 23));
		timeRemainingjLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timeRemainingjLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		timeRemainingjLabel.setText("TIME REMAINING");
		timeRemainingjText = new JTextField();
		timeRemainingjText.setEditable(false);
		timeRemainingjText.setBounds(new Rectangle(620, 39, 61, 23));
		timeRemainingjText.setText("50 sec");
		timeRemainingjText.setBackground(Color.white);
		timeRemainingjText.setFont(new Font("serif", Font.BOLD, 14));
		timeRemainingjText.setForeground(new Color(102, 204, 255));
		timeRemainingjText.setHorizontalAlignment(SwingConstants.CENTER);

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
			jPanelMain.add(getIrrigationCapacityLabel(), BorderLayout.NORTH);
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
			scoreBoardLabel.setText("WATER COLLECTED TO TOKENS EARNED TABLE");
			dashBoardLabel = new JLabel();
			dashBoardLabel.setBounds(new Rectangle(13,225+100+35,530,20));
			dashBoardLabel.setHorizontalAlignment(SwingConstants.CENTER);
			dashBoardLabel.setText("YOUR DASHBOARD");
			totalContributedBandwidthTextjLabel = new JLabel();
			totalContributedBandwidthTextjLabel.setBounds(new Rectangle(200, 18, 55, 17));
			totalContributedBandwidthTextjLabel.setText("");
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
			downloadScreenPanel.add(getCenterPanel(),null);
			downloadScreenPanel.add(getTimeLeftProgressBar(), null);
			//			downloadScreenPanel.add(timeRemainingjLabel, null);
			//			downloadScreenPanel.add(timeRemainingjText,null);
			//			downloadScreenPanel.add(totalContributedBandwidthjLabel, null);
			//			downloadScreenPanel.add(totalContributedBandwidthTextjLabel, null);
			downloadScreenPanel.add(dashBoardLabel, null);
			downloadScreenPanel.add(scoreBoardLabel, null);
		}
		return downloadScreenPanel;
	}

	private JProgressBar getTimeLeftProgressBar() {
		if (timeLeftProgressBar == null) {
			timeLeftProgressBar = new JProgressBar(0, 50);
			timeLeftProgressBar.setBounds(new Rectangle(360, 15, 370, 17));
			timeLeftProgressBar.setStringPainted(true);
		}
		return timeLeftProgressBar;
	}
	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanel.setLayout(null);
			centerPanel.setBounds(new Rectangle(13, 64, 1098, 123));
		}
		return centerPanel;
	}

	private JPanel createCanalPanel(ClientDataModel clientDataModel) {
		canalPanel = new CanalPanel(clientDataModel);
		canalPanel.setSize(new Dimension(1098, 123));

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
			jPanelUpStreamWindow.setLayout(new BorderLayout());
			jPanelUpStreamWindow.setBackground(new Color(186, 226, 237));
			jPanelUpStreamWindow.setBounds(new Rectangle(13, 225+100+50, 530, 326));
			jPanelUpStreamWindow.add(getGateSwitchButton(), BorderLayout.CENTER);
		}
		return jPanelUpStreamWindow;
	}
	private final static String OPEN_GATE_LABEL = "Click here to open your gate";
	private final static String CLOSE_GATE_LABEL = "Click here to close your gate";
	private JButton getGateSwitchButton() {
		if (gateSwitchButton == null) {
			gateSwitchButton = new JButton(OPEN_GATE_LABEL);
			gateSwitchButton.setFont(new Font("sansserif", Font.TRUETYPE_FONT, 18));
			//			gateSwitchButton.setPreferredSize(new Dimension(100, 100));
			gateSwitchButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					open = !open;
					gateSwitchButton.setText( open ? CLOSE_GATE_LABEL : OPEN_GATE_LABEL );
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

	private JPanel getControlPanel() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(530, 326));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(getGateSwitchButton());

		JPanel bottomInformationPanel = new JPanel();
		GroupLayout layout = new GroupLayout(bottomInformationPanel);
		bottomInformationPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		GroupLayout.SequentialGroup horizontalGroup = layout.createSequentialGroup();
		ParallelGroup labelsGroup = layout.createParallelGroup();
		labelsGroup.addComponent(getWaterCollectedLabel()).addComponent(getTokensNotInvestedLabel()).addComponent(getTokensEarnedLabel()).addComponent(getTotalTokensEarnedLabel());

		horizontalGroup.addGroup(labelsGroup);

		ParallelGroup textFieldGroup = layout.createParallelGroup();
		textFieldGroup.addComponent(getWaterCollectedTextField());
		textFieldGroup.addComponent(getTokensNotInvestedTextField());
		textFieldGroup.addComponent(getTokensEarnedTextField());
		textFieldGroup.addComponent(getTotalTokensEarnedTextField());
		horizontalGroup.addGroup(textFieldGroup);
		layout.setHorizontalGroup(horizontalGroup);

		GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
		verticalGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(getWaterCollectedLabel()).addComponent(getWaterCollectedTextField()));

		verticalGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(getTokensNotInvestedLabel()).addComponent(getTokensNotInvestedTextField()));

		verticalGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(getTokensEarnedLabel()).addComponent(getTokensEarnedTextField()));

		verticalGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(getTotalTokensEarnedLabel()).addComponent(getTotalTokensEarnedTextField()));

		layout.setVerticalGroup(verticalGroup);
		panel.add(bottomInformationPanel);
		return panel;
	}

	private JTextField getWaterCollectedTextField() {
		if (waterCollectedTextField == null) {
			waterCollectedTextField = createTextField();
		}
		return waterCollectedTextField;
	}

	private JTextField createTextField() {
		JTextField textField = new JTextField();
		textField.setEditable(false);
		textField.setBackground(Color.LIGHT_GRAY);
		return textField;
	}

	private JLabel getWaterCollectedLabel() {
		if (waterCollectedLabel == null) {
			waterCollectedLabel = new JLabel("Total water applied to your field: ");
		}
		return waterCollectedLabel;
	}

	private JTextField getTokensNotInvestedTextField() {
		if (tokensNotInvestedTextField == null) {
			tokensNotInvestedTextField = createTextField();
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
			tokensEarnedTextField = createTextField();
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
			totalTokensEarnedTextField = createTextField();
		}
		return totalTokensEarnedTextField;

	}

	private JLabel getTotalTokensEarnedLabel() {
		if (totalTokensEarnedLabel == null) {
			totalTokensEarnedLabel = new JLabel("Total tokens earned this round: ");
		}
		return totalTokensEarnedLabel;
	}

	private JLabel getIrrigationCapacityLabel() {
		if (irrigationCapacityLabel == null) {
			irrigationCapacityLabel = new JLabel();
			irrigationCapacityLabel.setLabelFor(getIrrigationCapacityTextField());
			irrigationCapacityLabel.setFont(new Font("sansserif", Font.BOLD, 16));
		}
		return irrigationCapacityLabel;
	}

	private JTextField getIrrigationCapacityTextField() {
		if (irrigationCapacityTextField == null) {
			irrigationCapacityTextField = createTextField();
		}
		return irrigationCapacityTextField;
	}
	/**
	 * This method initializes jPanel3
	 * summary scoreboard
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelDownStreamWindow() {
		if (jPanelDownStreamWindow == null) {
			jPanelDownStreamWindow = new JPanel();
			jPanelDownStreamWindow.setLayout(new BorderLayout());
			jPanelDownStreamWindow.setBackground(new Color(186, 226, 237));
			jPanelDownStreamWindow.setBounds(new Rectangle(582, 225 + 100+50, 530, 326));
			jPanelDownStreamWindow.add(getWaterCollectedToTokensTable(),BorderLayout.CENTER);
		}
		return jPanelDownStreamWindow;
	}

	private JScrollPane getWaterCollectedToTokensTable() {
		if (waterCollectedToTokensTable == null) {
			waterCollectedToTokensTable = new HtmlEditorPane();
			waterCollectedToTokensTable.setEditable(false);
			waterCollectedToTokensTable.setText(client.getServerConfiguration().getWaterCollectedToTokensTable());
			waterCollectedToTokensScrollPane = new JScrollPane(waterCollectedToTokensTable);
			waterCollectedToTokensScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		return waterCollectedToTokensScrollPane;
	}

	private void setProgressBarColor(int timeLeft) {
		if (timeLeft < 10) {
//			timeLeftProgressBar.setForeground( Color.RED );
			timeLeftProgressBar.setBackground( Color.RED );
//			return Color.RED;
		}
		else {
			timeLeftProgressBar.setBackground( Color.GREEN );
		}
	}
	/**
	 * Should be invoked every second throughout the experiment, from a ClientUpdateEvent sent by the server.
	 */
	public void updateClientStatus(final ClientDataModel clientDataModel) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int timeLeft = clientDataModel.getTimeLeft();
				String timeLeftString = String.format("%d sec", timeLeft);
				timeLeftProgressBar.setValue( timeLeft );
				timeLeftProgressBar.setString(timeLeftString);
				setProgressBarColor(timeLeft);
				for (final ClientData clientData : clientDataModel.getClientDataMap().values()) {
					if (clientData.isGateOpen()) {
						canalPanel.openGate(clientData.getPriority());
					}
					else if(clientData.isPaused()){
						canalPanel.closeGate(clientData.getPriority());
					}
					else if(clientData.isGateClosed()){
						canalPanel.closeGate(clientData.getPriority());
					}
				}
				ClientData clientData = clientDataModel.getClientData();
				getWaterCollectedTextField().setText("" + clientData.getWaterCollected());
				getTokensNotInvestedTextField().setText("" + clientData.getUninvestedTokens());
				getTokensEarnedTextField().setText("" + clientData.getTokensEarnedFromWaterCollected());
				getTotalTokensEarnedTextField().setText("" + clientData.getAllTokensEarnedThisRound());
				//				getScoreBoxPanel().update(clientDataModel);
				getMiddleWindowPanel().update(clientDataModel);

			}

		});
	}
	//	//this event gets called every second in the experiment.
	//	public void updateSendFileProgress(final ClientDataModel clientGameState) {
	//		// TODO Auto-generated method stub
	//	    ////////////new code////////////////////////////////////////////////////////////
	//			Runnable createGuiRunnable = new Runnable(){
	//				public void run() {
	//					for(final ClientData clientData : clientGameState.getClientDataMap().values()){
	//						timeRemainingjText.setText((new Integer(((int)clientGameState.getTimeLeft()))).toString()+"sec");
	//						timeRemainingjProgressBar.setValue((int)clientGameState.getTimeLeft());
	//						totalContributedBandwidthTextjLabel.setText((new Double(clientData.getGroupDataModel().getMaximumAvailableFlowCapacity())).toString()+" cubic feet per second (cfps)");
	//						middleWindowPanel.update(clientGameState);
	//						if(clientGameState.getPriority() == clientData.getPriority()){
	//							controlPanel.update(clientData);
	//							//per parameter score panel
	////							scoreBoxPanel.update(clientData);
	//						}
	//						else{
	////							scoreBoxPanel.update(clientData);
	//						}
	//							if(clientData.isDownloading() == true){
	//								upperPanel.openGates(clientData.getPriority());
	//								
	//							}
	//							else if(clientData.isPaused() == true){
	//								upperPanel.closeGates(clientData.getPriority());
	//							}
	//							if(clientData.isStopped() == true){
	//								upperPanel.closeGates(clientData.getPriority());
	//							}
	//					}
	//				}
	//
	//			};
	//		try {
	//			SwingUtilities.invokeAndWait(createGuiRunnable);
	//		} catch (InterruptedException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} catch (InvocationTargetException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}		
	//	}

	/**
	 * changes the file button color to red when started downloading
	 * @param file
	 */

	/*
	 * updates the irrigation window
	 */
	public void endRound() {
		System.out.println("End of round.");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//				controlPanel.endRound();
				centerPanel.removeAll();
				canalPanel.endRound();
				gateSwitchButton.setText(OPEN_GATE_LABEL);
			}

		});
		open = false;
	}

	/**
	 * assigns the priority to the window and prepares the irrigationWindowMap
	 */
	public void setClientDataModel(final ClientDataModel clientDataModel) {
		this.clientDataModel = clientDataModel;
		//Here a map is created with the map consisting of the
		//irrigation window as the value and the priority value as the key. This is handled here , because
		//the event contains the priority for every client, and the priority can act as a unique key for the
		//game window.

		//checking the client priority and assigning the activity summary and client panel to the 
		//doownloadScreenPanel
		fillPanels(clientDataModel);

		//This is helpful, if we need to scale the game to more than two users. We can declare
		//more panels like middleStream Window, etc and add them in this switch case statement

		//case 2 : irrigationWindowMap.put(new Integer(clientData.getPriority()), middleStreamWindow);
	}

	public void startRound() {
		open = false;
		getMiddleWindowPanel().initialize(clientDataModel);
		int irrigationCapacity = clientDataModel.getGroupDataModel().getMaximumAvailableFlowCapacity();
		irrigationCapacityLabel.setText(
				String.format("Irrigation capacity: %d cubic feet per second (cfps)",
						irrigationCapacity));

		revalidate();
		//		mainIrrigationPanel.add(getJPanelUpStreamWindow(),null);
		//		mainIrrigationPanel.add(getJPanelDownStreamWindow(),null);
		//adding the in between Panel
		//		mainIrrigationPanel.add(getJPanelMiddleWindow(),null);
	}





	/**
	 * fills in the panels depending on the priority of the client
	 */
	public void fillPanels(ClientDataModel clientDataModel) {
		// TODO Auto-generated method stub
		centerPanel.add(createCanalPanel(clientDataModel));
		//switch(clientGameState.getPriority()){

		downloadScreenPanel.add(getJPanelUpStreamWindow(),null);
		downloadScreenPanel.add(getJPanelDownStreamWindow(),null);
		//adding the in between Panel
		downloadScreenPanel.add(getMiddleWindowPanel(),null);
		/*case 0 : downloadScreenPanel.add(getJPanelUpStreamWindow(0),null);
    		 	  downloadScreenPanel.add(getJPanelDownStreamWindow(0),null);
    		 	 //Assigning the Priorities on the Priority Label
    		 	 break;

		 case 1 : downloadScreenPanel.add(getJPanelUpStreamWindow(1),null);
    	 		  downloadScreenPanel.add(getJPanelDownStreamWindow(1),null);
    	 		 //Assigning the Priorities on the Priority Label
    	 		 break;
		 */		}

	private MiddleWindowPanel getMiddleWindowPanel() {
		// TODO Auto-generated method stub
		if(middleWindowPanel == null) {
			middleWindowPanel = new MiddleWindowPanel();
		}
		return middleWindowPanel;
	}

	public ClientDataModel getClientDataModel() {
		return clientDataModel;
	}
}