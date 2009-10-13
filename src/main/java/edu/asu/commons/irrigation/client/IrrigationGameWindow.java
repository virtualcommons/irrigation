package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.server.ClientData;

/**
 * $Id$
 * 
 * Primary JPanel used to display the active interface for the experiment.
 * Organizes and contains all other components.
 *
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */

public class IrrigationGameWindow extends JPanel {

    private static final long serialVersionUID = -1737783671891265064L;

	private JPanel jPanelIrrigationGameWindow = null;

	private JPanel jPanelIrrigationWindow = null;

	private JPanel jPanelIrrigationWindowLeft = null;

	private JPanel jPanelIrrigationWindowRight = null;

	private JPanel jPanelIrrigationWindowLeftUp = null;

	private JPanel jPanelIrrigationWindowLeftDown = null;

	private JPanel jPanelIrrigationGameWindowLeftUpFiles = null;

	public JButton openGateButton = null;

	private JPanel jPanelIrrigationGameWindowLeftDownButtons = null;

	private JPanel jPanelIrrigationGameWindowLeftDownProgress = null;

	// public JButton StopDownload = null;

	public JProgressBar fileDownloadedProgressBar = null;

	public JPanel jPanelIrrigationGameWindowRightDown = null;

	private JPanel jPanelIrrigationWindowRightUp = null;

	private JPanel jPanelIrrigationGameWindowRightUp2 = null;

	private JPanel jPanelIrrigationGameWindowRightUp1 = null;

	private JPanel jPanelIrrigationGameWindowRightUp3 = null;

	private JLabel availableBandwidthLabel = null;

	public JProgressBar availableBandwidthTxt = null;

	private JLabel profitEarned = null;

	public JTextField profitEarnedtxt = null;

	private JLabel fileDownloadedtxt = null;

	private IrrigationClient irrigationClient;

	private String name = null;

	public ChartWindowPanel xySeriesDemo;

	public JButton pauseDownload = null;

	private ClientData clientData; // @jve:decl-index=0:

	private JLabel availableFlowCapacityLabel = null;

	private JLabel currentBandwidthAvailableTextjLabel = null;

	private MainIrrigationGameWindow mainIrrigationWindow;

	public IrrigationGameWindow(Dimension screenSize, IrrigationClient client,
			MainIrrigationGameWindow mainIrrigationWindow) {
		this.mainIrrigationWindow = mainIrrigationWindow;
		this.irrigationClient = client;
		initialize(screenSize);
	}

	/**
	 * Initializes the GUI.
	 * 
	 * @return void
	 */
	private void initialize(Dimension screenSize) {
	    // why use card layout?  
		this.setLayout(new CardLayout());
		this.setSize(530, 326);
		this.add(getJPanelIrrigationGameWindow(), getJPanelIrrigationGameWindow().getName());
	}

	public void startRound(final RoundConfiguration experimentRoundConfiguration) {
		/**
		 * Here the logic would come for the starting of round, initialization ,
		 * starting of the GUI, etc, etc.
		 */
		initEnable();
	}

	// public void endRound(EndRoundEvent event){
	public void endRound() {
		/**
		 * here the end code and debreifing logic should be written
		 */
		refreshButtons();
		profitEarnedtxt.setText("0");
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIrrigationGameWindow() {
		if (jPanelIrrigationGameWindow == null) {
			jPanelIrrigationGameWindow = new JPanel();
			jPanelIrrigationGameWindow.setLayout(new CardLayout());
			jPanelIrrigationGameWindow.setName("jPanel");
			jPanelIrrigationGameWindow.add(getJPanelIrrigationWindow(), getJPanelIrrigationWindow().getName());
		}
		return jPanelIrrigationGameWindow;
	}

	/**
	 * Returns the primary irrigation window.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIrrigationWindow() {
		if (jPanelIrrigationWindow == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			jPanelIrrigationWindow = new JPanel();
			jPanelIrrigationWindow.setLayout(gridLayout);
			jPanelIrrigationWindow.setName("Primary Irrigation Window");
			jPanelIrrigationWindow.add(getJPanelIrrigationWindowLeft(), null);
			jPanelIrrigationWindow.add(getJPanelIrrigationWindowRight(), null);
		}
		return jPanelIrrigationWindow;
	}

	/**
	 * These methods would be updated externally by irrigationClientGameState.
	 */
	public void updateFileDownloaded(double percentFileDownloaded) {

	}

	public void updateProfitEarned(final int award) {
		profitEarnedtxt.setText(new Integer(award).toString());

	}

	/**
	 * This method initializes jPanel31
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIrrigationWindowLeft() {
		if (jPanelIrrigationWindowLeft == null) {
			GridLayout gridLayout3 = new GridLayout();
			gridLayout3.setRows(2);
			jPanelIrrigationWindowLeft = new JPanel();
			jPanelIrrigationWindowLeft.setLayout(gridLayout3);
			jPanelIrrigationWindowLeft.add(getJPanelIrrigationWindowLeftUp(),
					null);
			jPanelIrrigationWindowLeft.add(getJPanelIrrigationWindowLeftDown(),
					null);
		}
		return jPanelIrrigationWindowLeft;
	}

	/**
	 * This method initializes jPanel32
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getJPanelIrrigationWindowRight() {
		if (jPanelIrrigationWindowRight == null) {
			GridLayout gridLayout5 = new GridLayout();
			gridLayout5.setRows(2);
			jPanelIrrigationWindowRight = new JPanel();
			jPanelIrrigationWindowRight.setLayout(gridLayout5);
			jPanelIrrigationWindowRight.add(
					getJPanelIrrigationGameWindowRightUp1(), null);
			jPanelIrrigationWindowRight.add(getJPanelIrrigationWindowRightUp(),
					null);
		}
		return jPanelIrrigationWindowRight;
	}

	/**
	 * This method initializes jPanel311
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIrrigationWindowLeftUp() {
		if (jPanelIrrigationWindowLeftUp == null) {
			jPanelIrrigationWindowLeftUp = new JPanel();
			jPanelIrrigationWindowLeftUp.setLayout(new BorderLayout());
			jPanelIrrigationWindowLeftUp.add(getJPanelIrrigationGameWindowLeftUpFiles(), BorderLayout.CENTER);
		}
		return jPanelIrrigationWindowLeftUp;
	}

	/**
	 * This method initializes jPanel312
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIrrigationWindowLeftDown() {
		if (jPanelIrrigationWindowLeftDown == null) {
			GridLayout gridLayout4 = new GridLayout();
			gridLayout4.setRows(2);
			jPanelIrrigationWindowLeftDown = new JPanel();
			jPanelIrrigationWindowLeftDown.setLayout(gridLayout4);
			jPanelIrrigationWindowLeftDown.add(
					getJPanelIrrigationGameWindowLeftDownProgress(), null);
			jPanelIrrigationWindowLeftDown.add(
					getJPanelIrrigationGameWindowLeftDownButtons(), null);
		}
		return jPanelIrrigationWindowLeftDown;
	}

	/**
	 * This method initializes jPanel3111
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIrrigationGameWindowLeftUpFiles() {
		if (jPanelIrrigationGameWindowLeftUpFiles == null) {
			jPanelIrrigationGameWindowLeftUpFiles = new JPanel();
			jPanelIrrigationGameWindowLeftUpFiles.setLayout(null);
			jPanelIrrigationGameWindowLeftUpFiles.setVisible(true);
			jPanelIrrigationGameWindowLeftUpFiles.setBackground(new Color(238, 238, 238));
			jPanelIrrigationGameWindowLeftUpFiles.add(getOpenGateButton(), null);
		}
		return jPanelIrrigationGameWindowLeftUpFiles;
	}

	/**
	 * This method initializes File1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOpenGateButton() {
		if (openGateButton == null) {
			openGateButton = new JButton();
			openGateButton.setText("Grow");
			openGateButton.setForeground(Color.black);
			openGateButton.setSelected(true);
			openGateButton.setBackground(new Color(186, 236, 237));
			openGateButton.setBounds(new Rectangle(48, 5, 63, 15));
			// TODO Auto-generated method stub
			openGateButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					/**
					 * undisrupted Bandwidth Extensions
					 */
					if(clientData != null){
						if(clientData.getAvailableFlowCapacity() > 0){
							//file1.setBackground(Color.RED);
							openGateButton.setEnabled(false);
							openGate();
						}
					}
					else
						System.out.println("ClientData is null");
				}
			});
		}
		return openGateButton;
	}

	/**
	 * this will start the download for fileNo
	 * 
	 * @param fileNo
	 */
	private void openGate() {
		/*
		 * System.out.println("Before downloading file " + fileNo + "Current
		 * time" + System.currentTimeMillis() / 1000);
		 */
		
		irrigationClient.openGate();
		initEnable();
		

	}

	/**
	 * This method initializes jPanel3121
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIrrigationGameWindowLeftDownButtons() {
		if (jPanelIrrigationGameWindowLeftDownButtons == null) {
			jPanelIrrigationGameWindowLeftDownButtons = new JPanel();
			jPanelIrrigationGameWindowLeftDownButtons.setLayout(null);
			/*
			 * jPanelIrrigationGameWindowLeftDownButtons.add(getStopDownload(),
			 * null);
			 */
			jPanelIrrigationGameWindowLeftDownButtons.add(getPauseDownload(),
					null);
		}
		return jPanelIrrigationGameWindowLeftDownButtons;
	}

	/**
	 * This method initializes jPanel3122-
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIrrigationGameWindowLeftDownProgress() {
		if (jPanelIrrigationGameWindowLeftDownProgress == null) {
			fileDownloadedtxt = new JLabel();
			fileDownloadedtxt.setText("% Crop Grown:");
			fileDownloadedtxt.setBounds(new Rectangle(64, 32, 145, 16));
			jPanelIrrigationGameWindowLeftDownProgress = new JPanel();
			jPanelIrrigationGameWindowLeftDownProgress.setLayout(null);
			jPanelIrrigationGameWindowLeftDownProgress.add(getFileDownloaded(),
					null);
			jPanelIrrigationGameWindowLeftDownProgress.add(fileDownloadedtxt,
					null);
		}
		return jPanelIrrigationGameWindowLeftDownProgress;
	}

	/**
	 * This method initializes StopDownload
	 * 
	 * @return javax.swing.JButton
	 */
	// Removing the Stop Button Functionality
	/*
	 * private JButton getStopDownload() { if (StopDownload == null) {
	 * StopDownload = new JButton(); StopDownload.setText("Stop ");
	 * StopDownload.setBounds(new Rectangle(16, 6, 103, 25));
	 * StopDownload.setBackground(new Color(186, 226, 237));
	 * StopDownload.setEnabled(false); StopDownload.addActionListener(new
	 * java.awt.event.ActionListener() { public void
	 * actionPerformed(java.awt.event.ActionEvent e) {
	 * PauseDownload.setEnabled(true); StopDownload.setEnabled(true);
	 * client.stopDownload(fileNo); enableFiles(); initDisable(); }
	 *//**
		 * just check this and implement later
		 * 
		 * @param fileNo
		 */
	/*
	 * 
	 * }); } return StopDownload; }
	 */

	/**
	 * This method initializes FileDownloaded
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getFileDownloaded() {
		if (fileDownloadedProgressBar == null) {
			fileDownloadedProgressBar = new JProgressBar();
			fileDownloadedProgressBar.setBounds(new Rectangle(63, 15, 148, 14));
			fileDownloadedProgressBar.setBackground(Color.white);
			fileDownloadedProgressBar.setForeground(Color.green);
			fileDownloadedProgressBar
					.setFont(new Font("Dialog", Font.BOLD, 12));
			fileDownloadedProgressBar.setStringPainted(true);
		}
		return fileDownloadedProgressBar;
	}

	/**
	 * This method initializes jPanel321
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIrrigationWindowRightUp() {
		if (jPanelIrrigationWindowRightUp == null) {
			GridLayout gridLayout7 = new GridLayout();
			gridLayout7.setRows(1);
			GridLayout gridLayout6 = new GridLayout();
			gridLayout6.setRows(1);
			jPanelIrrigationWindowRightUp.add(
					getJPanelIrrigationGameWindowRightUp2(), null);
			jPanelIrrigationWindowRightUp.add(
					getJPanelIrrigationGameWindowRightUp1(), null);
		}
		return jPanelIrrigationWindowRightUp;
	}

	/**
	 * This method initializes jPanel322
	 * 
	 * @return javax.swing.JPanel
	 */

	public JPanel getJPanelIrrigationGameWindowRightDown(int file) {
		if (jPanelIrrigationGameWindowRightDown == null) {
			jPanelIrrigationGameWindowRightDown = new JPanel();
			jPanelIrrigationGameWindowRightDown.setLayout(null);
			jPanelIrrigationGameWindowRightDown.setSize(530 / 2, 326 / 2);
			jPanelIrrigationGameWindowRightDown.add(getJGraph(file));
		}
		return jPanelIrrigationGameWindowRightDown;
	}

	public JPanel getJGraph(int numberFileDownloaded) {
		// TODO Auto-generated method stub
		if (xySeriesDemo == null) {
			xySeriesDemo = new ChartWindowPanel(numberFileDownloaded);
			xySeriesDemo.setVisible(true);
		}
		return xySeriesDemo;
	}

	/**
	 * This method initializes jPanel321
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIrrigationGameWindowRightUp1() {
		if (jPanelIrrigationWindowRightUp == null) {
			GridLayout gridLayout8 = new GridLayout();
			gridLayout8.setRows(3);
			jPanelIrrigationWindowRightUp = new JPanel();
			jPanelIrrigationWindowRightUp.setLayout(gridLayout8);
			jPanelIrrigationWindowRightUp.add(getJPanel32122(), null);
			jPanelIrrigationWindowRightUp.add(
					getJPanelIrrigationGameWindowRightUp2(), null);
			jPanelIrrigationWindowRightUp.add(
					getJPanelIrrigationGameWindowRightUp3(), null);
		}
		return jPanelIrrigationWindowRightUp;
	}

	/**
	 * This method initializes jPanel3211
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIrrigationGameWindowRightUp2() {
		if (jPanelIrrigationGameWindowRightUp2 == null) {
			currentBandwidthAvailableTextjLabel = new JLabel();
			currentBandwidthAvailableTextjLabel.setBounds(new Rectangle(180,
					15, 65, 16));
			currentBandwidthAvailableTextjLabel.setText("");
			availableBandwidthLabel = new JLabel();
			availableBandwidthLabel.setText("% of capacity available:");
			availableBandwidthLabel.setBounds(new Rectangle(8, 5, 254, 18));
			jPanelIrrigationGameWindowRightUp2 = new JPanel();
			jPanelIrrigationGameWindowRightUp2.setLayout(null);
			jPanelIrrigationGameWindowRightUp2.add(
					getAvailableFlowCapacityLabel(), null);
			jPanelIrrigationGameWindowRightUp2.add(
					currentBandwidthAvailableTextjLabel, null);
		}
		return jPanelIrrigationGameWindowRightUp2;
	}

	/**
	 * This method initializes jPanel3212
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel32122() {
		if (jPanelIrrigationGameWindowRightUp1 == null) {
			profitEarned = new JLabel();
			profitEarned.setText("Tokens Collected:");
			profitEarned.setBounds(new Rectangle(17, 18, 114, 21));
			jPanelIrrigationGameWindowRightUp1 = new JPanel();
			jPanelIrrigationGameWindowRightUp1.setLayout(null);
			jPanelIrrigationGameWindowRightUp1.add(profitEarned, null);
			jPanelIrrigationGameWindowRightUp1.add(getProfitEarnedTextField(), null);
		}
		return jPanelIrrigationGameWindowRightUp1;
	}

	/**
	 * This method initializes jPanel3213
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIrrigationGameWindowRightUp3() {
		if (jPanelIrrigationGameWindowRightUp3 == null) {
			jPanelIrrigationGameWindowRightUp3 = new JPanel();
			jPanelIrrigationGameWindowRightUp3.setLayout(null);
			jPanelIrrigationGameWindowRightUp3.add(availableBandwidthLabel,
					null);
			jPanelIrrigationGameWindowRightUp3.add(getAvailableBandwidthtxt(),
					null);
		}
		return jPanelIrrigationGameWindowRightUp3;
	}

	/**
	 * This method initializes AvailableBandwidthtxt
	 * 
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getAvailableBandwidthtxt() {
		if (availableBandwidthTxt == null) {
			availableBandwidthTxt = new JProgressBar();
			availableBandwidthTxt.setMaximum(25);
			availableBandwidthTxt.setBounds(new Rectangle(15, 31, 240, 14));
			availableBandwidthTxt.setStringPainted(true);
			availableBandwidthTxt.setBackground(Color.white);
			availableBandwidthTxt.setForeground(new Color(51, 153, 255));
			availableBandwidthTxt.setValue(25);
		}
		return availableBandwidthTxt;
	}

	/**
	 * This method initializes ProfitEarnedtxt
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getProfitEarnedTextField() {
		if (profitEarnedtxt == null) {
			profitEarnedtxt = new JTextField();
			profitEarnedtxt.setText(new Integer(mainIrrigationWindow
					.getClientGameState().getClientData().getTotalTokensEarned())
					.toString());
			profitEarnedtxt.setBounds(new Rectangle(161, 17, 86, 21));
			profitEarnedtxt.setFont(new Font("Dialog", Font.PLAIN, 14));
			profitEarnedtxt.setEditable(false);
		}
		return profitEarnedtxt;
	}

	/**
	 * This method initializes PauseDownload
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getPauseDownload() {
		if (pauseDownload == null) {
			pauseDownload = new JButton();
			pauseDownload.setBounds(new Rectangle(40, 7, 191, 25));
			pauseDownload.setText("Pause");
			pauseDownload.setBackground(new Color(186, 226, 237));
			pauseDownload.setEnabled(false);
			pauseDownload.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {

							if (pauseDownload.getText() == "Pause") {
								pauseDownload.setText("Resume");
								// StopDownload.setEnabled(true);
								irrigationClient.pause();
							} else if (pauseDownload.getText() == "Resume") {
								pauseDownload.setText("Pause");
								irrigationClient.openGate();
							}
						}
					});
		}
		return pauseDownload;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void initEnable() {
		// TODO Auto-generated method stub
		// StartDownload.setEnabled(false);
		// StopDownload.setEnabled(true);
		pauseDownload.setEnabled(true);
	}

	public void initDisable() {
		// TODO Auto-generated method stub
		// StartDownload.setEnabled(false);
		// StopDownload.setEnabled(false);
		pauseDownload.setEnabled(false);
	}

	/**
	 * refreshes the file buttons for the main irrigation window
	 */
	public void refreshButtons() {
		openGateButton.setEnabled(true);
		openGateButton.setBackground(new Color(186, 226, 237));
	}

	public void update(ClientData data) {
		this.clientData = data;
		Runnable createGuiRunnable = new Runnable() {
			public void run() {
				availableBandwidthTxt.setValue((int) clientData
						.getAvailableFlowCapacity());
				if (clientData.getAvailableFlowCapacity() > 25) {
					currentBandwidthAvailableTextjLabel.setText(new Double(25)
							.toString());
				} else {
					currentBandwidthAvailableTextjLabel.setText(new Double(
							clientData.getAvailableFlowCapacity()).toString());
				}

				/*
				 * if(clientData.getAvailableBandwidth() ==
				 * clientData.getMaximumAvailableBandwidth()){
				 * bandwidthusedOtherTextjLabel.setText("0"); } else{
				 * bandwidthusedOtherTextjLabel.setText(new
				 * Double(clientData.getMaximumAvailableBandwidth()-
				 * clientData.getAvailableBandwidth()).toString()); }
				 */

				fileDownloadedProgressBar.setValue((int) clientData.getPercentFileDownload());
				// checks whether the file has been downlaoded
				if (clientData.isFileDownloaded()) {

					profitEarnedtxt.setText(new Integer(clientData.getTotalTokensEarned())
							.toString());
					// StopDownload.setEnabled(false);
					pauseDownload.setEnabled(false);
				}
				else if(clientData.isGateOpen() || clientData.isPaused()){
						initEnable();
				}
				/**
				 * undisrupted Bandwidth extensions
				 */
				if(clientData.getAvailableFlowCapacity() <= 0 && 
				        irrigationClient.getServerConfiguration().isUndisruptedFlowRequired() )
				{
					pauseDownload.setEnabled(false);
				}
					
			}
		};
		SwingUtilities.invokeLater(createGuiRunnable);
	}

	/**
	 * This method initializes currentBandwidthAvailablejLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private JLabel getAvailableFlowCapacityLabel() {
		if (availableFlowCapacityLabel == null) {
			availableFlowCapacityLabel = new JLabel();
			availableFlowCapacityLabel.setText("Your available flow capacity:");
			availableFlowCapacityLabel.setBounds(new Rectangle(17, 15,
					153, 16));
		}
		return availableFlowCapacityLabel;
	}
} 
