package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.server.ClientData;
/**
 * $Id$
 * 
 * Primary game interface window for the irrigation game.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>, Sanket Joshi
 * @version $Rev$
 */
public class MainIrrigationGameWindow extends JPanel {

    private static final long serialVersionUID = 5900368694556557132L;

	private CanalPanel canalPanel;

	private JPanel jPanelMain = null;
	//this contains the CanalPanel
	private JPanel centerPanel = null;
	//this contains the upstream and downstream Panel
	private JPanel jPanelUpStreamWindow = null;
	private JPanel jPanelDownStreamWindow = null;
	private JPanel mainInterfacePanel = null;

	private IrrigationClient client;

	private JProgressBar timeLeftProgressBar;

	private ClientDataModel clientDataModel;  //  @jve:decl-index=0:

	private MiddleWindowPanel middleWindowPanel;

	private JLabel gateSwitchLabel = null;

	private JLabel scoreBoardLabel = null;

	private JButton gateSwitchButton;

	private boolean open;

//	private JTextField waterCollectedTextField;
//	private JLabel waterCollectedLabel;
//	private JTextField tokensNotInvestedTextField;
//	private JLabel tokensNotInvestedLabel;
//	private JTextField tokensEarnedTextField;
//	private JLabel tokensEarnedLabel;
//	private JTextField totalTokensEarnedTextField;
//	private JLabel totalTokensEarnedLabel;
	
	private JLabel irrigationCapacityLabel;
	private JLabel waterSupplyLabel;

	private JTextField irrigationCapacityTextField;
	private JTextField waterSupplyTextField;

	public MainIrrigationGameWindow(IrrigationClient client) {
		super();
		this.client = client;
		setName("main irrigation game window");
		initGuiComponents();
	}

	private void initGuiComponents() {
		this.setLayout(new BorderLayout(4,4));
		this.setSize(1130, 558);
		this.add(getPanel(),null);

	}

	private JPanel getPanel() {
		if(jPanelMain == null){
			jPanelMain = new JPanel();
			jPanelMain.setLayout(new BorderLayout(4,4));
			jPanelMain.setBackground(Color.WHITE);
			jPanelMain.setForeground(Color.BLACK);
			JPanel upperPanel = new JPanel();
			upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.X_AXIS));
			upperPanel.add(getIrrigationCapacityLabel());
			upperPanel.add(Box.createHorizontalGlue());
			upperPanel.add(getWaterSupplyLabel());
			jPanelMain.add(upperPanel, BorderLayout.NORTH);
			jPanelMain.add(getMainInterfacePanel(), BorderLayout.CENTER);
			return jPanelMain;
		}
		return jPanelMain;
	}

	private JPanel getMainInterfacePanel() {
		if(mainInterfacePanel == null){
			scoreBoardLabel = new JLabel("");
			scoreBoardLabel.setBounds(new Rectangle(582,225+100+35,530,20));
			scoreBoardLabel.setHorizontalAlignment(SwingConstants.CENTER);
			gateSwitchLabel = new JLabel("");
			gateSwitchLabel.setBounds(new Rectangle(13,225+100+35,530,20));
			gateSwitchLabel.setHorizontalAlignment(SwingConstants.CENTER);
			mainInterfacePanel = new JPanel();
			mainInterfacePanel.setLayout(null);
			mainInterfacePanel.setName("Main interface panel");
			mainInterfacePanel.setBackground(Color.WHITE);
			mainInterfacePanel.add(getCenterPanel(),null);
			mainInterfacePanel.add(getTimeLeftProgressBar(), null);
			mainInterfacePanel.add(gateSwitchLabel, null);
			mainInterfacePanel.add(scoreBoardLabel, null);
		}
		return mainInterfacePanel;
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

	private JPanel getCanalPanel(ClientDataModel clientDataModel) {
	    if (canalPanel == null) {
	        canalPanel = new CanalPanel(clientDataModel);
	        canalPanel.setSize(new Dimension(1098, 123));
	    }
	    else {
	        canalPanel.setClientDataModel(clientDataModel);
	    }
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
//			jPanelUpStreamWindow.add(getControlPanel(), BorderLayout.SOUTH);
		}
		return jPanelUpStreamWindow;
	}
	private final static String OPEN_GATE_LABEL = "OPEN YOUR GATE";
	private final static String CLOSE_GATE_LABEL = "CLOSE YOUR GATE";
	private JButton getGateSwitchButton() {
		if (gateSwitchButton == null) {
			gateSwitchButton = new JButton(OPEN_GATE_LABEL);
			gateSwitchButton.setFont(new Font("sansserif", Font.TRUETYPE_FONT, 18));
			gateSwitchButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					open = !open;
					if (open) {
						client.openGate();
						gateSwitchButton.setText(CLOSE_GATE_LABEL);
					}
					else {
						client.closeGate();
						gateSwitchButton.setText(OPEN_GATE_LABEL);
					}
				}
			});
		}
		return gateSwitchButton;
	}
	

//	private JPanel getControlPanel() {
//		JPanel bottomInformationPanel = new JPanel();
//		GroupLayout layout = new GroupLayout(bottomInformationPanel);
//		bottomInformationPanel.setLayout(layout);
//		layout.setAutoCreateContainerGaps(true);
//		layout.setAutoCreateGaps(true);
//
//		GroupLayout.SequentialGroup horizontalGroup = layout.createSequentialGroup();
//		ParallelGroup labelsGroup = layout.createParallelGroup();
//		labelsGroup.addComponent(getWaterCollectedLabel()).addComponent(getTokensNotInvestedLabel()).addComponent(getTokensEarnedLabel()).addComponent(getTotalTokensEarnedLabel());
//
//		horizontalGroup.addGroup(labelsGroup);
//
//		ParallelGroup textFieldGroup = layout.createParallelGroup();
//		textFieldGroup.addComponent(getWaterCollectedTextField());
//		textFieldGroup.addComponent(getTokensNotInvestedTextField());
//		textFieldGroup.addComponent(getTokensEarnedTextField());
//		textFieldGroup.addComponent(getTotalTokensEarnedTextField());
//		horizontalGroup.addGroup(textFieldGroup);
//		layout.setHorizontalGroup(horizontalGroup);
//
//		GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
//		verticalGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
//				.addComponent(getWaterCollectedLabel()).addComponent(getWaterCollectedTextField()));
//
//		verticalGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
//				.addComponent(getTokensNotInvestedLabel()).addComponent(getTokensNotInvestedTextField()));
//
//		verticalGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
//				.addComponent(getTokensEarnedLabel()).addComponent(getTokensEarnedTextField()));
//
//		verticalGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
//				.addComponent(getTotalTokensEarnedLabel()).addComponent(getTotalTokensEarnedTextField()));
//
//		layout.setVerticalGroup(verticalGroup);
//		return bottomInformationPanel;
//	}

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setEditable(false);
//      textField.setBackground(Color.LIGHT_GRAY);
        textField.setBackground(Color.YELLOW);
        return textField;
    }	
	
//	private JTextField getWaterCollectedTextField() {
//		if (waterCollectedTextField == null) {
//			waterCollectedTextField = createTextField();
//		}
//		return waterCollectedTextField;
//	}
//
//
//
//	private JLabel getWaterCollectedLabel() {
//		if (waterCollectedLabel == null) {
//			waterCollectedLabel = new JLabel("Total water applied to your field: ");
//		}
//		return waterCollectedLabel;
//	}
//
//	private JTextField getTokensNotInvestedTextField() {
//		if (tokensNotInvestedTextField == null) {
//			tokensNotInvestedTextField = createTextField();
//		}
//		return tokensNotInvestedTextField;
//	}
//	private JLabel getTokensNotInvestedLabel() {
//		if (tokensNotInvestedLabel == null) {
//			tokensNotInvestedLabel = new JLabel("Tokens not invested: ");
//		}
//		return tokensNotInvestedLabel;
//	}
//
//	private JTextField getTokensEarnedTextField() {
//		if (tokensEarnedTextField == null) {
//			tokensEarnedTextField = createTextField();
//		}
//		return tokensEarnedTextField;
//	}
//	private JLabel getTokensEarnedLabel() {
//		if (tokensEarnedLabel == null) {
//			tokensEarnedLabel = new JLabel("Tokens earned by crop production: ");
//		}
//		return tokensEarnedLabel;
//	}
//
//	private JTextField getTotalTokensEarnedTextField() {
//		if (totalTokensEarnedTextField == null) {
//			totalTokensEarnedTextField = createTextField();
//		}
//		return totalTokensEarnedTextField;
//
//	}
//
//	private JLabel getTotalTokensEarnedLabel() {
//		if (totalTokensEarnedLabel == null) {
//			totalTokensEarnedLabel = new JLabel("Total tokens earned this round: ");
//		}
//		return totalTokensEarnedLabel;
//	}
	
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
	
	private JLabel getWaterSupplyLabel() {
	    if (waterSupplyLabel == null) {
	        waterSupplyLabel = new JLabel("Water supply: ");
	        waterSupplyLabel.setLabelFor(getWaterSupplyTextField());
	        waterSupplyLabel.setFont(new Font("sansserif", Font.BOLD, 16));
	    }
	    return waterSupplyLabel;
	}
	private JTextField getWaterSupplyTextField() {
	    if (waterSupplyTextField == null) {
	        waterSupplyTextField = createTextField();
	    }
	    return waterSupplyTextField;
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
	
	private XYSeries currentWaterAppliedSeries = new XYSeries("Current water applied");

	private ChartPanel getWaterCollectedToTokensTable() {
        final XYSeries tokensEarnedSeries = new XYSeries("Tokens earned from water applied");
        
        for (int waterApplied = 0; waterApplied < 1000; waterApplied++) {
        	int tokensEarned = RoundConfiguration.getTokensEarned(waterApplied);
        	tokensEarnedSeries.add(waterApplied, tokensEarned);
        }
        
        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(tokensEarnedSeries);
        data.addSeries(currentWaterAppliedSeries);
		JFreeChart chart = ChartFactory.createXYLineChart(
                "Water applied to tokens earned",
                "Cubic feet of water applied to your field",
                "Tokens earned",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
		chart.setAntiAlias(true);
		return new ChartPanel(chart);
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
				// FIXME: figure out how to reliably set the progress bar colors regardless of OS.
//				setProgressBarColor(timeLeft);
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
//				getWaterCollectedTextField().setText("" + clientData.getWaterCollected());
//				getTokensNotInvestedTextField().setText("" + clientData.getUninvestedTokens());
//				getTokensEarnedTextField().setText("" + clientData.getTokensEarnedFromWaterCollected());
//				getTotalTokensEarnedTextField().setText("" + clientData.getAllTokensEarnedThisRound());
				getMiddleWindowPanel().update(clientDataModel);
				
				currentWaterAppliedSeries.clear();
				for (int i = 0; i <= clientData.getTokensEarnedFromWaterCollected(); i++) {
					currentWaterAppliedSeries.add(clientData.getWaterCollected(), i);
				}
			}

		});
	}

	/**
	 * end of round interface cleanup.
	 */
	public void endRound() {
		System.out.println("End of round.");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
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

	}

	public void startRound() {
		open = false;
		getMiddleWindowPanel().initialize(clientDataModel);
        centerPanel.add(getCanalPanel(clientDataModel));
        mainInterfacePanel.add(getJPanelUpStreamWindow(),null);
        mainInterfacePanel.add(getJPanelDownStreamWindow(),null);
        mainInterfacePanel.add(getMiddleWindowPanel(),null);
		canalPanel.startRound();
		int irrigationCapacity = clientDataModel.getIrrigationCapacity();
		int waterSupply = clientDataModel.getWaterSupplyCapacity();
		irrigationCapacityLabel.setText(
				String.format("Water delivery capacity: %d cubic feet per second (cfps)",
						irrigationCapacity));
		waterSupplyLabel.setText(
		        String.format("Available water supply: %d cubic feet per second (cfps)",
		                waterSupply));
		revalidate();
	}

	private MiddleWindowPanel getMiddleWindowPanel() {
		if(middleWindowPanel == null) {
			middleWindowPanel = new MiddleWindowPanel();
		}
		return middleWindowPanel;
	}

	public ClientDataModel getClientDataModel() {
		return clientDataModel;
	}
}