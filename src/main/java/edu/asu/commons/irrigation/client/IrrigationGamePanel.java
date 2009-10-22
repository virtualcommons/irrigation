package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;

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

//	private IrrigationGameWindow irrigationGameWindow;

	private CanalPanel canalPanel;

	//this contains the CanalPanel
	private JPanel upperPanel;

	private ScoreBoxPanel scoreBoxPanel;

	private IrrigationClient client;

//	private Dimension screenSize;

	private JProgressBar timeLeftProgressBar;

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

	private JLabel waterCollectedLabel;

	private JTextField waterCollectedTextField;

	private JTextField tokensNotInvestedTextField;

	private JTextField tokensEarnedTextField;

	private JTextField totalTokensEarnedTextField;

	private boolean open;

    private JTextField irrigationCapacityTextField;

	public IrrigationGamePanel(IrrigationClient client) {
		setName("Irrigation Game Panel");
		this.client = client;
		initGuiComponents();
	}
	/**
	 * Initializes the main game window.
	 * @return 
	 */
	private void initGuiComponents() {


		JPanel topmostPanel = new JPanel();
		topmostPanel.setLayout(new BoxLayout(topmostPanel, BoxLayout.Y_AXIS));
		topmostPanel.add(getIrrigationCapacityLabel());
		topmostPanel.add(getTimeLeftProgressBar());

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
		GroupLayout layout = new GroupLayout(bottomPanel);
		bottomPanel.setLayout(layout);
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
//		bottomPanel.add(getWaterUsedLabel());
//		bottomPanel.add(getWaterUsedTextField());
//		bottomPanel.add(getTokensNotInvestedLabel());
//		bottomPanel.add(getTokensNotInvestedTextField());
//		bottomPanel.add(getTokensEarnedLabel());
//		bottomPanel.add(getTokensEarnedTextField());
//		bottomPanel.add(getTotalTokensEarnedLabel());
//		bottomPanel.add(getTotalTokensEarnedTextField());

		panel.add(bottomPanel);
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
		}
		return irrigationCapacityLabel;
	}
	
	private JTextField getIrrigationCapacityTextField() {
	    if (irrigationCapacityTextField == null) {
	        irrigationCapacityTextField = createTextField();
	    }
	    return irrigationCapacityTextField;
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
			gateSwitchButton = new JButton("Open gate");
			gateSwitchButton.setPreferredSize(new Dimension(100, 100));
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

	private JPanel createCanalPanel() {
		canalPanel = new CanalPanel(clientDataModel);
		return canalPanel;
	}

//	private IrrigationGameWindow getIrrigationGameWindow() {
//		if(irrigationGameWindow == null) {
//			irrigationGameWindow = new IrrigationGameWindow(screenSize,client,this);
//		}
//		return irrigationGameWindow;
//	}
	
//	/**
//     * This method initializes jPanel1
//     *
//     * @return javax.swing.JPanel
//     */
//    private JPanel getJPanelUpStreamWindow() {
//        if (jPanelUpStreamWindow == null) {
//            jPanelUpStreamWindow = new JPanel();
//            jPanelUpStreamWindow.setLayout(null);
//            jPanelUpStreamWindow.setBackground(new Color(186, 226, 237));
//            jPanelUpStreamWindow.setBounds(new Rectangle(13, 225+100+50, 530, 326));
//            jPanelUpStreamWindow.add(getIrrigationGameWindow(), null);
//        }
//        return jPanelUpStreamWindow;
//    }
//	/**
//	 * This method initializes jPanel3
//	 *
//	 * @return javax.swing.JPanel
//	 */
//	private JPanel getJPanelDownStreamWindow() {
//		if (jPanelDownStreamWindow == null) {
//			jPanelDownStreamWindow = new JPanel();
//			jPanelDownStreamWindow.setLayout(null);
//			jPanelDownStreamWindow.setBackground(new Color(186, 226, 237));
//			jPanelDownStreamWindow.setBounds(new Rectangle(582, 225 + 100+50, 530, 326));
//			jPanelDownStreamWindow.add(getScoreBoxPanel(),null);
//		}
//		return jPanelDownStreamWindow;
//	}

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
	private JProgressBar getTimeLeftProgressBar() {
		if (timeLeftProgressBar == null) {
			timeLeftProgressBar = new JProgressBar(0, 50);
			timeLeftProgressBar.setStringPainted(true);
		}
		return timeLeftProgressBar;
	}
	
	private Color getProgressBarColor(int timeLeft) {
	    if (timeLeft < 10) {
	        return Color.RED;
	    }
	    return Color.BLACK;
	}
	
	/**
	 * Should be invoked every second throughout the experiment, from a ClientUpdateEvent sent by the server.
	 */
	public void updateClientStatus(final ClientDataModel clientDataModel) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int timeLeft = clientDataModel.getTimeLeft();
				int irrigationCapacity = clientDataModel.getGroupDataModel().getMaximumAvailableFlowCapacity();
				String timeLeftString = String.format("%d sec", timeLeft);
				timeLeftProgressBar.setValue( timeLeft );
				timeLeftProgressBar.setString(timeLeftString);
				timeLeftProgressBar.setForeground( getProgressBarColor(timeLeft) );
				irrigationCapacityLabel.setText(
						String.format("Irrigation capacity: %d cubic feet per second (cfps)",
								irrigationCapacity));
				for (final ClientData clientData : clientDataModel.getClientDataMap().values()) {
				    System.err.println("changing canal gate for client: " + clientData.getPriority() + ":" + clientData.isGateOpen());
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
				waterCollectedTextField.setText("" + clientData.getWaterCollected());
				tokensNotInvestedTextField.setText("" + clientData.getUninvestedTokens());
				tokensEarnedTextField.setText("" + clientData.getTokensEarnedFromWaterCollected());
				totalTokensEarnedTextField.setText("" + clientData.getAllTokensEarnedThisRound());
				getScoreBoxPanel().update(clientDataModel);
//				getMiddleWindowPanel().update(clientDataModel);

			}

		});
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
				gateSwitchButton.setText("Open gate");
				if (canalPanel != null) {
				    System.err.println("Removing canal panel");
				    upperPanel.remove(canalPanel);
					canalPanel.endRound();
				}
				System.err.println("ending round for canal panel");
			    canalPanel = null;
			}
		};
		open = false;
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

	public void setClientDataModel(final ClientDataModel clientDataModel) {
		this.clientDataModel = clientDataModel;
	}


	/**
	 * fills in the panels depending on the priority of the client
	 */
	public void startRound() {
		open = false;
		upperPanel.add(createCanalPanel(), BorderLayout.CENTER);
		getScoreBoxPanel().initialize(clientDataModel);
		getMiddleWindowPanel().initialize(clientDataModel);
		revalidate();
		//		mainIrrigationPanel.add(getJPanelUpStreamWindow(),null);
		//		mainIrrigationPanel.add(getJPanelDownStreamWindow(),null);
		//adding the in between Panel
		//		mainIrrigationPanel.add(getJPanelMiddleWindow(),null);
	}

	private MiddleWindowPanel getMiddleWindowPanel() {
		if(middleWindowPanel == null){
			middleWindowPanel = new MiddleWindowPanel();
		}
		return middleWindowPanel;
	}

	public ClientDataModel getClientDataModel() {
		return clientDataModel;
	}
}

