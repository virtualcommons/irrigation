/**
 *
 */
package edu.asu.commons.irrigation.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.asu.commons.irrigation.conf.RoundConfiguration;
import edu.asu.commons.irrigation.server.ClientData;

/**
 * @author Sanket
 * 
 * FIXME: holy crap this class needs some major hygiene.  Get rid of 
 * file1, file2, file3, file4...file10 button crap.
 */
public class IrrigationGameWindow extends JPanel {

	private static final int TOTAL_CROPS_AVAILABLE = 5;

	private static final long serialVersionUID = 1L;

	public int token;

	private JPanel jPanelIrrigationGameWindow = null;

	private JPanel jPanelIrrigationWindow = null;

	private JPanel jPanelIrrigationWindowLeft = null;

	private JPanel jPanelIrrigationWindowRight = null;

	private JPanel jPanelIrrigationWindowLeftUp = null;

	private JPanel jPanelIrrigationWindowLeftDown = null;

	private JPanel jPanelIrrigationGameWindowLeftUpFiles = null;

	public JButton file1 = null;

	public JButton file2 = null;

	public JButton file3 = null;

	public JButton file4 = null;

	public JButton file5 = null;

	public JButton file6 = null;

	public JButton file7 = null;

	public JButton file8 = null;

	public JButton file9 = null;

	public JButton file10 = null;

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

	private Dimension screenSize;

	private IrrigationClient client;

	private String name = null;

	public ChartWindowPanel xySeriesDemo;

	public String fileNo;

	private boolean fileClicked = false;

	// private final IrrigationClientGameState state;
	public JButton pauseDownload = null;

	public boolean isDownloaded[] = new boolean[10];

	private ClientData clientData; // @jve:decl-index=0:

	private JLabel currentWaterAvailabilityJLabel = null;

	private JLabel currentBandwidthAvailableTextjLabel = null;

	private MainIrrigationGameWindow mainIrrigationWindow;

	public IrrigationGameWindow(Dimension screenSize, IrrigationClient client,
			MainIrrigationGameWindow mainIrrigationWindow) {
		this.mainIrrigationWindow = mainIrrigationWindow;
		this.client = client;
		// this.state = client.getClientGameState();
		// FIXME: set the actual screen size dimensions after this JPanel has
		// been initialized...
		// this.screenSize = getParent().getSize();
		this.screenSize = screenSize;
		// this.channel = state.getEventChannel();
		// feed subject view the avaiable screen size so that
		// it can adjust appropriately when given a board size
		// Dimension viewSize = getViewSize();
		// subjectView = new SubjectView(viewSize, state);
		// debriefingView = new DebriefingView(viewSize);
		// initGuiComponents();
		// super();
		initialize();
	}

	/**
	 * Initializes the GUI.
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new CardLayout());
		// this.setSize(new Dimension(660, 418));
		// this.setSize(screenSize.width/2, screenSize.height/2);
		this.setSize(530, 326);
		this.add(getJPanelIrrigationGameWindow(),
				getJPanelIrrigationGameWindow().getName());
		/**
		 * initiallizing the file download list to false as no files are
		 * downloaded initially.
		 */
		for (int i = 0; i < 10; i++) {
			isDownloaded[i] = false;
		}
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
			jPanelIrrigationGameWindow.add(getJPanelIrrigationWindow(),
					getJPanelIrrigationWindow().getName());
		}
		return jPanelIrrigationGameWindow;
	}

	/**
	 * This method initializes jPanel3
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelIrrigationWindow() {
		if (jPanelIrrigationWindow == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			jPanelIrrigationWindow = new JPanel();
			jPanelIrrigationWindow.setLayout(gridLayout);
			jPanelIrrigationWindow.setName("jPanel3");
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
//			jPanelIrrigationWindowRight.add(
//					getJPanelIrrigationGameWindowRightDown(10), null);
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
			jPanelIrrigationWindowLeftUp.add(
					getJPanelIrrigationGameWindowLeftUpFiles(),
					BorderLayout.CENTER);
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
			jPanelIrrigationGameWindowLeftUpFiles.setBackground(new Color(238,
					238, 238));
			jPanelIrrigationGameWindowLeftUpFiles.add(getFile1(), null);
			jPanelIrrigationGameWindowLeftUpFiles.add(getFile2(), null);
			jPanelIrrigationGameWindowLeftUpFiles.add(getFile3(), null);
			jPanelIrrigationGameWindowLeftUpFiles.add(getFile4(), null);
			jPanelIrrigationGameWindowLeftUpFiles.add(getFile5(), null);
//			jPanelIrrigationGameWindowLeftUpFiles.add(getFile6(), null);
//			jPanelIrrigationGameWindowLeftUpFiles.add(getFile7(), null);
//			jPanelIrrigationGameWindowLeftUpFiles.add(getFile8(), null);
//			jPanelIrrigationGameWindowLeftUpFiles.add(getFile9(), null);
//			jPanelIrrigationGameWindowLeftUpFiles.add(getFile10(), null);
		}
		return jPanelIrrigationGameWindowLeftUpFiles;
	}

	/**
	 * This method initializes File1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFile1() {
		if (file1 == null) {
			file1 = new JButton();
			file1.setText("Grow");
			file1.setForeground(Color.black);
			file1.setSelected(true);
			file1.setBackground(new Color(186, 236, 237));
			file1.setBounds(new Rectangle(48, 5, 63, 15));
			// TODO Auto-generated method stub
			file1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					/**
					 * undisrupted Bandwidth Extensions
					 */
					if(clientData != null){
						if(clientData.getAvailableFlowCapacity() > 0){
							fileClicked = true;
							//file1.setBackground(Color.RED);
							file1.setEnabled(false);
							fileNo = new String("F1");
							startDownload(fileNo);
						}
					}
					else
						System.out.println("ClientData is null");
				}
			});
		}
		return file1;
	}

	/**
	 * this will start the download for fileNo
	 * 
	 * @param fileNo
	 */
	private void startDownload(String fileNo) {
		// TODO Auto-generated method stub
		/*
		 * System.out.println("Before downloading file " + fileNo + "Current
		 * time" + System.currentTimeMillis() / 1000);
		 */
		
		client.startDownload(fileNo);
		disableAllFiles();
		initEnable();
		

	}

	/**
	 * this will enable those files that are yet to be downloaded and change
	 * their color to gray.
	 */
	public void enableFiles() {
		// TODO Auto-generated method stub
		int i;
		for (i = 0; i < TOTAL_CROPS_AVAILABLE; i++) {
			if (isDownloaded[i] == false && fileClicked == false && !clientData.isDownloading()) {
				(getFile(i)).setBackground(new Color(186, 226, 237));
				(getFile(i)).setEnabled(true);
			}
		}
		// System.out.println("i reached is :"+i);
	}

	private JButton getFile(int fileNumber) {
		switch (fileNumber) {

		case 0:
			return file1;

		case 1:
			return file2;

		case 2:
			return file3;

		case 3:
			return file4;

		case 4:
			return file5;

		case 5:
			return file6;

		case 6:
			return file7;

		case 7:
			return file8;

		case 8:
			return file9;

		case 9:
			return file10;

		default:
			return file1;
		}
	}

	/**
	 * This method initializes File2
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFile2() {
		if (file2 == null) {
			file2 = new JButton();
			file2.setText("Grow");
			file2.setBackground(new Color(186, 236, 237));
			file2.setBounds(new Rectangle(113, 5, 63, 15));
			file2.addActionListener(new java.awt.event.ActionListener() {

				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(clientData.getAvailableFlowCapacity() > 0){
						fileClicked = true;
						//file2.setBackground(Color.RED);
						file2.setEnabled(false);
						fileNo = new String("F2");
						startDownload(fileNo);
					}
				}
			});
		}
		return file2;
	}

	/**
	 * This method initializes File3
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFile3() {
		if (file3 == null) {
			file3 = new JButton();
			file3.setText("Grow");
			file3.setBackground(new Color(186, 236, 237));
			file3.setBounds(new Rectangle(178, 5, 63, 15));
			file3.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(clientData.getAvailableFlowCapacity() > 0){
//						file3.setBackground(Color.RED);
						file3.setEnabled(false);
						fileNo = new String("F3");
						startDownload(fileNo);
					}
				}
			});
		}
		return file3;
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
	 * This method initializes StartDownload
	 * 
	 * @return javax.swing.JButton
	 */
	private void disableAllFiles() {
		// TODO Auto-generated method stub
		(getFile(getFileNumber(fileNo))).setBackground(Color.RED);
		for (int i = 0; i < TOTAL_CROPS_AVAILABLE; i++) {
			if(isDownloaded[i] == false && getFile(i).isEnabled()){
				(getFile(i)).setEnabled(false);
			}
			
		}
	}

	private int getFileNumber(String fileNo) {
		// TODO Auto-generated method stub
		if(fileNo.equalsIgnoreCase("F1"))
			return 0;
		if(fileNo.equalsIgnoreCase("F2"))
			return 1;
		if(fileNo.equalsIgnoreCase("F3"))
			return 2;
		if(fileNo.equalsIgnoreCase("F4"))
			return 3;
		if(fileNo.equalsIgnoreCase("F5"))
			return 4;
		if(fileNo.equalsIgnoreCase("F6"))
			return 5;
		if(fileNo.equalsIgnoreCase("F7"))
			return 6;
		if(fileNo.equalsIgnoreCase("F8"))
			return 7;
		if(fileNo.equalsIgnoreCase("F9"))
			return 8;
		if(fileNo.equalsIgnoreCase("F10"))
			return 9;
		return -1;
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
					getCurrentWaterAvailabilityJLabel(), null);
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
			jPanelIrrigationGameWindowRightUp1.add(getProfitEarnedtxt(), null);
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
	private JTextField getProfitEarnedtxt() {
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
								client.startPause(fileNo);
							} else if (pauseDownload.getText() == "Resume") {
								pauseDownload.setText("Pause");
								// StopDownload.setEnabled(true);
								disableAllFiles();
								client.startDownload(fileNo);
							}
						}
					});
		}
		return pauseDownload;
	}

	/**
	 * This method initializes File4
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFile4() {
		if (file4 == null) {
			file4 = new JButton();
			file4.setBounds(new Rectangle(48, 30, 63, 15));
			file4.setBackground(new Color(186, 236, 237));
			file4.setText("Grow");
			file4.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(clientData.getAvailableFlowCapacity()>0){
						//file4.setBackground(Color.RED);
						file4.setEnabled(false);
						fileNo = new String("F4");
						startDownload(fileNo);
					}

				}
			});
		}
		return file4;
	}

	/**
	 * This method initializes File5
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFile5() {
		if (file5 == null) {
			file5 = new JButton();
			file5.setBounds(new Rectangle(115, 30, 63, 15));
			file5.setBackground(new Color(186, 236, 237));
			file5.setText("Grow");
			file5.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(clientData.getAvailableFlowCapacity()>0){
//						file5.setBackground(Color.RED);
						file5.setEnabled(false);
						fileNo = new String("F5");
						startDownload(fileNo);	
					}
				}
			});
		}
		return file5;
	}

	/**
	 * This method initializes File6
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFile6() {
		if (file6 == null) {
			file6 = new JButton();
			file6.setBounds(new Rectangle(179, 30, 60, 14));
			file6.setBackground(new Color(186, 236, 237));
			file6.setText("100");
			file6.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(clientData.getAvailableFlowCapacity()>0){
//						file6.setBackground(Color.RED);
						file6.setEnabled(false);
						fileNo = new String("F6");
						startDownload(fileNo);	
					}
					
				}
			});
		}
		return file6;
	}

	/**
	 * This method initializes File7
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFile7() {
		if (file7 == null) {
			file7 = new JButton();
			file7.setBounds(new Rectangle(50, 60, 55, 17));
			file7.setBackground(new Color(186, 236, 237));
			file7.setText("100");
			file7.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(clientData.getAvailableFlowCapacity()>0){
//						file7.setBackground(Color.RED);
						file7.setEnabled(false);
						fileNo = new String("F7");
						startDownload(fileNo);	
					}
					
				}
			});
		}
		return file7;
	}

	/**
	 * This method initializes File8
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFile8() {
		if (file8 == null) {
			file8 = new JButton();
			file8.setBounds(new Rectangle(117, 59, 55, 17));
			file8.setBackground(new Color(186, 236, 237));
			file8.setText("100");
			file8.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(clientData.getAvailableFlowCapacity()>0){
//						file8.setBackground(Color.RED);
						file8.setEnabled(false);
						fileNo = new String("F8");
						startDownload(fileNo);
					
					}
				}
			});
		}
		return file8;
	}

	/**
	 * This method initializes File9
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFile9() {
		if (file9 == null) {
			file9 = new JButton();
			file9.setBounds(new Rectangle(180, 60, 55, 15));
			file9.setBackground(new Color(186, 236, 237));
			file9.setText("100");
			file9.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(clientData.getAvailableFlowCapacity()>0){
//						file9.setBackground(Color.RED);
						file9.setEnabled(false);
						fileNo = new String("F9");
						startDownload(fileNo);
					}
				}
			});
		}
		return file9;
	}

	/**
	 * This method initializes File10
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getFile10() {
		if (file10 == null) {
			file10 = new JButton();
			file10.setBounds(new Rectangle(118, 90, 55, 15));
			file10.setBackground(new Color(186, 236, 237));
			file10.setText("100");
			file10.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(clientData.getAvailableFlowCapacity()>0){
//						file10.setBackground(Color.RED);
						file10.setEnabled(false);
						fileNo = new String("F10");
						startDownload(fileNo);
						
					}
				}
			});
		}
		return file10;
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
	 * refreshes the file buttons for the main Irrgation window
	 */
	public void refreshButtons() {
		// TODO Auto-generated method stub
		file1.setEnabled(true);
		file2.setEnabled(true);
		file3.setEnabled(true);
		file4.setEnabled(true);
		file5.setEnabled(true);
//		file6.setEnabled(true);
//		file7.setEnabled(true);
//		file8.setEnabled(true);
//		file9.setEnabled(true);
//		file10.setEnabled(true);
		// refreshing the status for the files as not downloaded
		for (int i = 0; i < 10; i++)
			isDownloaded[i] = false;

		file1.setBackground(new Color(186, 226, 237));
		file2.setBackground(new Color(186, 226, 237));
		file3.setBackground(new Color(186, 226, 237));
		file4.setBackground(new Color(186, 226, 237));
		file5.setBackground(new Color(186, 226, 237));
//		file6.setBackground(new Color(186, 226, 237));
//		file7.setBackground(new Color(186, 226, 237));
//		file8.setBackground(new Color(186, 226, 237));
//		file9.setBackground(new Color(186, 226, 237));
//		file10.setBackground(new Color(186, 226, 237));
	}

	public void update(ClientData data) {
		// TODO Auto-generated method stub
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

				fileDownloadedProgressBar.setValue((int) clientData
						.getPercentFileDownload());
				// checks whether the file has been downlaoded
				if (clientData.isFileDownloaded() == true) {

					profitEarnedtxt.setText(new Integer(clientData.getTotalTokensEarned())
							.toString());
					// StopDownload.setEnabled(false);
					pauseDownload.setEnabled(false);
					updateFileFinished(clientData.getDownloadListArray());
				}
				else
					if(clientData.isDownloading() || clientData.isPaused()){
						disableAllFiles();	
						initEnable();
					}
				/**
				 * undisrupted Bandwidth extensions
				 */
				if(clientData.getAvailableFlowCapacity()<=0 && client.getServerConfiguration().isUndisruptedBandwidth()){
					pauseDownload.setEnabled(false);
					updateFileFinished(clientData.getDownloadListArray());
							
				}
					
			}
		};
		SwingUtilities.invokeLater(createGuiRunnable);
	}

	/**
	 * changes the File button color to green when finished downloading
	 * 
	 * @param file
	 */
	private void updateFileFinished(List<String> fileDownloadedList) {
		// TODO Auto-generated method stub
		fileClicked = false;
		for (int i = 0; i < fileDownloadedList.size(); i++) {
			String file = fileDownloadedList.get(i);
			if (file.equalsIgnoreCase("F1"))
				paintGreen(0);
			if (file.equalsIgnoreCase("F2"))
				paintGreen(1);
			if (file.equalsIgnoreCase("F3"))
				paintGreen(2);
			if (file.equalsIgnoreCase("F4"))
				paintGreen(3);
			if (file.equalsIgnoreCase("F5"))
				paintGreen(4);
			/*
			if (file.equalsIgnoreCase("F6"))
				paintGreen(5);
			if (file.equalsIgnoreCase("F7"))
				paintGreen(6);
			if (file.equalsIgnoreCase("F8"))
				paintGreen(7);
			if (file.equalsIgnoreCase("F9"))
				paintGreen(8);
			if (file.equalsIgnoreCase("F10"))
				paintGreen(9);
				*/
		}
		enableFiles();
	}

	private void paintGreen(final int file) {
		// TODO Auto-generated method stub
		// Runnable createGuiRunnable = new Runnable(){
		// public void run() {
		// TODO Auto-generated method stub

		(getFile(file)).setBackground(Color.GREEN);
		(getFile(file)).setEnabled(false);
		isDownloaded[file] = true;
		
		// }
		// };

		// SwingUtilities.invokeLater(createGuiRunnable);
	}

	/**
	 * This method initializes currentBandwidthAvailablejLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private JLabel getCurrentWaterAvailabilityJLabel() {
		if (currentWaterAvailabilityJLabel == null) {
			currentWaterAvailabilityJLabel = new JLabel();
			currentWaterAvailabilityJLabel.setText("Your available flow capacity:");
			currentWaterAvailabilityJLabel.setBounds(new Rectangle(17, 15,
					153, 16));
		}
		return currentWaterAvailabilityJLabel;
	}
} // @jve:decl-index=0:visual-constraint="20,10"
