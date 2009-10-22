package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
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
import edu.asu.commons.util.HtmlEditorPane;

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
	private JPanel centerPanel;

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

	private HtmlEditorPane waterCollectedToTokensTable;

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
		add(getCenterPanel(), BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.PAGE_END);
	}

	private JPanel getBottomPanel() {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.add(getGateSwitchButton());
		
		JPanel bottomMostPanel = new JPanel();
		bottomMostPanel.setLayout(new BoxLayout(bottomMostPanel, BoxLayout.X_AXIS));
		

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
		
		bottomMostPanel.add(bottomInformationPanel);
		
		bottomMostPanel.add(getWaterCollectedToTokensTable());
		
		bottomPanel.add(bottomMostPanel);
		return bottomPanel;
	}

	
	private HtmlEditorPane getWaterCollectedToTokensTable() {
		if (waterCollectedToTokensTable == null) {
			waterCollectedToTokensTable = new HtmlEditorPane();
			waterCollectedToTokensTable.setEditable(false);
			waterCollectedToTokensTable.setText(client.getServerConfiguration().getWaterCollectedToTokensTable());
		}
		return waterCollectedToTokensTable;
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

	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
//			centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
//			centerPanel.setLayout(new BorderLayout());
//			centerPanel.add(getMiddleWindowPanel(), BorderLayout.PAGE_END);
//			upperPanel.add(getScoreBoxPanel(), BorderLayout.PAGE_END);
		}
		return centerPanel;
	}


	private JButton getGateSwitchButton() {
		if (gateSwitchButton == null) {
			gateSwitchButton = new JButton("Open gate");
//			gateSwitchButton.setPreferredSize(new Dimension(100, 100));
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
		JPanel panel = new JPanel();
		canalPanel = new CanalPanel(clientDataModel);
		canalPanel.setSize(new Dimension(1098, 123));
		panel.add(canalPanel);
		panel.setBounds(new Rectangle(13, 64, 1098, 123));
		return panel;
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
				String timeLeftString = String.format("%d sec", timeLeft);
				timeLeftProgressBar.setValue( timeLeft );
				timeLeftProgressBar.setString(timeLeftString);
				timeLeftProgressBar.setForeground( getProgressBarColor(timeLeft) );
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
				waterCollectedTextField.setText("" + clientData.getWaterCollected());
				tokensNotInvestedTextField.setText("" + clientData.getUninvestedTokens());
				tokensEarnedTextField.setText("" + clientData.getTokensEarnedFromWaterCollected());
				totalTokensEarnedTextField.setText("" + clientData.getAllTokensEarnedThisRound());
//				getScoreBoxPanel().update(clientDataModel);
				getMiddleWindowPanel().update(clientDataModel);

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
				    centerPanel.removeAll();
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
	 * Updates all GUI related components that are dependent on the client data model being properly initialized
	 * with the GroupDataModel, RoundConfiguration, ClientData, etc.
	 */
	public void startRound() {
		open = false;
		centerPanel.add(createCanalPanel(), null);
		centerPanel.add(getMiddleWindowPanel(), null);
//		getWaterCollectedToTokensTable().setText(clientDataModel.getServerConfiguration().getWaterCollectedToTokensTable());
//		getScoreBoxPanel().initialize(clientDataModel);
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

