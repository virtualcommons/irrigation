package edu.asu.commons.irrigation.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

import edu.asu.commons.irrigation.model.ClientData;

/**
 * $Id$
 * 
 * FIXME: completely rewrite horrifyingly convoluted animation logic
 * 
 * @author Sanket Joshi
 */
public class CanalPanel extends JPanel {

	private static final long serialVersionUID = -6178860067928578812L;

	// private Graphics2D graphics2D;

	// ////////////////animation logic parameters//////////////////////////////
	private Timer timer;

	private final static int DELAY = 20;

	private Ball[] balls;

	private final static int BALLCOUNT = 500;
	private final static int NUMBER_OF_GATES = 6;

	private Gate[] gates = new Gate[NUMBER_OF_GATES];

	private int gateHeight = 20;

	private Random generator = new Random();

	private int maximumIrrigationCapacity;

	private ClientDataModel clientDataModel;



	private int reservoirHeight = 100;

	private int reservoirWidth = 100;

	private double canalHeightMultiplier = 0.8;

	private int gateBuffer = 20;

	private boolean restrictedVisibility;

	// //////////////////////////////////////////////////////////////////////////

	public CanalPanel(ClientDataModel clientDataModel) {
		super();
		// when totalContributed bandwidth = 1.0, you dont see the canal line
		setClientDataModel(clientDataModel);
		initialize();
	}

	public void setClientDataModel(ClientDataModel clientDataModel) {
		this.maximumIrrigationCapacity = clientDataModel.getGroupDataModel().getMaximumAvailableWaterFlow();
		this.clientDataModel = clientDataModel;
		this.restrictedVisibility = clientDataModel.getRoundConfiguration().isRestrictedVisibility();
	}

	/**
	 *  
	 */
	private void initialize() {
		// this.setSize(new Dimension(1098,150));
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		// initialize each gate
		initializeGates();
		initializeBalls();
		timer = new Timer(DELAY, new Rebound()); // setup the Timer to do an
													// action
		// every DELAY times.
		timer.start(); // starts the timer.

	}

	private void initializeGates() {
		for (int i = 0; i < NUMBER_OF_GATES; i++) {
			gates[i] = new Gate(maximumIrrigationCapacity, i);
		}
	}

	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics); // needed!
		checkRestrictedVisibility();
		updateGates();
		Graphics2D graphics2D = (Graphics2D) graphics;
		graphics2D.setColor(Color.BLUE);
		graphics2D.fillRect(0, 0, reservoirHeight, reservoirWidth);
		// draw gates
		for (int i = 0; i < NUMBER_OF_GATES - 1; i++) {
			graphics2D.setColor(Color.BLUE);
			graphics2D.fillRect(gates[i].getX(), gates[i].getY(),
					gates[i].getWidth(), gates[i].getHeight());
		}
//		checkRestrictedVisibility();
		for (ClientData clientData : clientDataModel.getClientDataSortedByPriority()) {
			if (restrictedVisibility && ! clientDataModel.isImmediateNeighbor(clientData)) {
				continue;
			}
			int priority = clientData.getPriority();
			Gate gate = gates[priority];
			graphics2D.setColor(Color.BLUE);
			graphics2D.fillRect(gate.getOpeningsX(), gate.getOpeningsY(),
					gate.getOpeningsWidth(), gate.getOpeningsHeight());
			graphics2D.setColor(Gate.DEFAULT_COLOR);
			graphics2D.drawLine(gate.getx1(), gate.gety1(), gate.getx2(),
					gate.gety2());
		}

		graphics2D.setColor(Color.BLUE);
		graphics2D.fillRect(gates[5].getX(), gates[5].getY(), 10,
				gates[5].getHeight());

		// particle system animation logic for the balls
		graphics.setColor(Color.WHITE);
		// int ballCounter = 0;
		for (int i = 0; i < BALLCOUNT; i++) {
			if ((balls[i].getPosition() == 1) || (balls[i].getPosition() == 2)
					|| (balls[i].getPosition() == 3)
					|| (balls[i].getPosition() == 4)
					|| (balls[i].getPosition() == 5)) {
				if (!((!gates[balls[i].getPosition() - 1].isOpen()) && balls[i]
						.getY() >= (gates[balls[i].getPosition() - 1].getY() + gates[balls[i]
						.getPosition() - 1].getHeight())))
					graphics.fillOval(balls[i].x, balls[i].y,
							balls[i].getBallSize(), balls[i].getBallSize());
			} else {
				if (balls[i].getPosition() != 0) {
					if (gates[balls[i].getPosition() - 6].getHeight() != 0)
						graphics.fillOval(balls[i].x, balls[i].y,
								balls[i].getBallSize(), balls[i].getBallSize());
				} else {
					graphics.fillOval(balls[i].x, balls[i].y,
							balls[i].getBallSize(), balls[i].getBallSize());
				}
			}
		}

	}
	
	private void checkRestrictedVisibility() {
		// 2 cases to check for:
		if (! restrictedVisibility) return;
		List<ClientData> sortedClients = clientDataModel.getClientDataSortedByPriority();
		ClientData thisClient = clientDataModel.getClientData();
		int thisClientIndex = sortedClients.indexOf(thisClient);
		int upstreamNeighborIndex = thisClientIndex - 1;
		int downstreamNeighborIndex = thisClientIndex + 1;
		for (int i = 0; i < gates.length; i++) {
			if (i < upstreamNeighborIndex && upstreamNeighborIndex > 0) {
				ClientData upstreamNeighbor = sortedClients.get(upstreamNeighborIndex);
				gates[i].setHeight(upstreamNeighbor.getAvailableFlowCapacity());
			}
			else if (i > downstreamNeighborIndex && downstreamNeighborIndex < sortedClients.size()) {
				ClientData downstreamNeighbor = sortedClients.get(downstreamNeighborIndex);
				gates[i].setHeight(downstreamNeighbor.getAvailableFlowCapacity());
			}
		}
	}

	/**
	 * FIXME: needs major refactoring
	 */
	private void updateGates() {
		for (int i = 1; i < 6; i++) {
			if (gates[i - 1].isOpen()) {
				if (!(gates[i - 1].getHeight() - gateHeight < 0)) {
					gates[i].setHeight(gates[i - 1].getHeight() - gateHeight);

				} else {
					gates[i].setHeight(0);
				}
			} else {
				gates[i].setHeight(gates[i - 1].getHeight());

			}
			// order matters here
			if (!(i == 5)) {
				gates[i].setOpeningsHeight((maximumIrrigationCapacity * canalHeightMultiplier)
						- gates[i].getHeight());
				gates[i].setOpeningsY(gates[i].getOpeningsHeight() - 50);

				// opening gate logic
				if (gates[i].isOpen()) {
					gates[i].setx1(gates[i].getx2());
					if (!(gates[i].gety2() - gates[i].getGateWidth() < gates[i].getY()))
						gates[i].sety1(gates[i].gety2() - gates[i].getGateWidth());
					else
						gates[i].sety1(gates[i].getOpeningsY());
				} else {
					gates[i].setx1(gates[i].getdefaultx1());
					gates[i].sety1(gates[i].getdefaulty1());
				}

				if (gates[0].isOpen()) {
					gates[0].setx1(gates[0].getx2());
					if (!(gates[0].gety2() - gates[0].getGateWidth() < gates[0]
							.getY()))
						gates[0].sety1(gates[0].gety2()
								- gates[0].getGateWidth());
					else
						gates[0].sety1(gates[0].getY());
				} else {
					gates[0].setx1(gates[0].getdefaultx1());
					gates[0].sety1(gates[0].getdefaulty1());
				}

			}
		}
	}

	/*
	 * initialize the Balls
	 */
	private void initializeBalls() {
		balls = new Ball[BALLCOUNT];
		for (int i = 0; i < BALLCOUNT; i++) {
			balls[i] = new Ball(generator);
		}
	}

	// this is the private class the Timer will look at every DELAY seconds
	private class Rebound implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < BALLCOUNT; i++) {
				// updateGUI();
				balls[i].x += balls[i].moveX;
				balls[i].y += balls[i].moveY;
				process(i);
			}
			repaint();
		}
	}

	/*
	 * This will process the balls according to their position
	 */
	private void process(int i) {
		switch (balls[i].getPosition()) {

		case 0:
			if ((balls[i].x >= (reservoirWidth - gateBuffer) && balls[i].x <= reservoirWidth)
					&& (balls[i].y >= reservoirHeight
							- (int) (maximumIrrigationCapacity * canalHeightMultiplier) && balls[i].y <= reservoirHeight)) {
				balls[i].setPosition(1);
				setBounds(i);
			}
			// still in server
			else {
				setBounds(i);
				if (balls[i].x <= balls[i].xLowerBound
						|| balls[i].x >= balls[i].xUpperBound
								- balls[i].getBallSize()) {
					balls[i].moveX = balls[i].moveX * -1;
				}
				if (balls[i].y <= balls[i].yLowerBound
						|| balls[i].y >= balls[i].yUpperBound
								- balls[i].getBallSize())
					balls[i].moveY = balls[i].moveY * -1;
			}
			break;

		case 1:
			if (gates[0].isOpen()
					&& (balls[i].x >= gates[0].getOpeningsX() && balls[i].x <= (gates[0]
							.getOpeningsX() + gateBuffer))
					&& (balls[i].y >= reservoirHeight - gateBuffer && balls[i].y <= reservoirHeight)) {
				balls[i].setPosition(7);
				// directly pass in the information
				setBounds(i);
			} else {
				setBounds(i);
				if (balls[i].getX() > balls[i].xUpperBound) {
					balls[i].setPosition(2);
				}
				if (balls[i].getY() >= balls[i].yUpperBound
						- balls[i].getBallSize()
						|| balls[i].getY() <= balls[i].yLowerBound) {
					balls[i].moveY = balls[i].moveY * -1;
				}
			}

			break;

		case 2:
			if (gates[1].isOpen()
					&& (balls[i].x >= gates[1].getOpeningsX() && balls[i].x <= (gates[1]
							.getOpeningsX() + gateBuffer))
					&& (balls[i].y >= reservoirHeight - gateBuffer && balls[i].y <= reservoirHeight)) {
				balls[i].setPosition(8);
				// directly pass in the information
				setBounds(i);
			} else {
				setBounds(i);
				if (balls[i].getX() > balls[i].xUpperBound) {
					balls[i].setPosition(3);
				}
				if (balls[i].getY() >= balls[i].yUpperBound
						- balls[i].getBallSize()
						|| balls[i].getY() <= balls[i].yLowerBound) {
					balls[i].moveY = balls[i].moveY * -1;
				}
			}
			break;

		case 3:
			if (gates[2].isOpen()
					&& (balls[i].x >= gates[2].getOpeningsX() && balls[i].x <= (gates[2]
							.getOpeningsX() + gateBuffer))
					&& (balls[i].y >= reservoirHeight - gateBuffer && balls[i].y <= reservoirHeight)) {
				balls[i].setPosition(9);
				// directly pass in the information
				setBounds(i);
				/*
				 * balls[i].xBOUNDSUPPER = 420; balls[i].xLowerBound = 400;
				 * balls[i].yBOUNDSUPPER = 150; balls[i].yBOUNDSLOWER = 80;
				 * balls[i].moveX = 0; balls[i].moveY = 3;
				 */
			} else {
				setBounds(i);
				if (balls[i].getX() > balls[i].xUpperBound) {
					balls[i].setPosition(4);
				}
				if (balls[i].getY() >= balls[i].yUpperBound
						- balls[i].getBallSize()
						|| balls[i].getY() <= balls[i].yLowerBound) {
					balls[i].moveY = balls[i].moveY * -1;
				}
			}
			break;

		case 4:
			if (gates[3].isOpen()
					&& (balls[i].x >= gates[3].getOpeningsX() && balls[i].x <= (gates[3]
							.getOpeningsX() + gateBuffer))
					&& (balls[i].y >= reservoirHeight - gateBuffer && balls[i].y <= reservoirHeight)) {
				balls[i].setPosition(10);
				// directly pass in the information
				setBounds(i);
			} else {
				setBounds(i);
				if (balls[i].getX() > balls[i].xUpperBound) {
					balls[i].setPosition(5);
				}
				if (balls[i].getY() >= balls[i].yUpperBound
						- balls[i].getBallSize()
						|| balls[i].getY() <= balls[i].yLowerBound) {
					balls[i].moveY = balls[i].moveY * -1;
				}
			}
			break;

		case 5:
			if (gates[4].isOpen()
					&& (balls[i].x >= gates[4].getOpeningsX() && balls[i].x <= (gates[4]
							.getOpeningsX() + gateBuffer))
					&& (balls[i].y >= reservoirHeight - gateBuffer && balls[i].y <= reservoirHeight)) {
				balls[i].setPosition(11);
				// directly pass in the information
				setBounds(i);
			} else {
				setBounds(i);
				if (balls[i].getX() > balls[i].xUpperBound) {
					balls[i].setPosition(6);
				}
				if (balls[i].getY() >= balls[i].yUpperBound
						- balls[i].getBallSize()
						|| balls[i].getY() <= balls[i].yLowerBound) {
					balls[i].moveY = balls[i].moveY * -1;
				}
			}
			break;

		case 6:
			setBounds(i);
			if (balls[i].getX() > balls[i].xUpperBound) {
				setBallInServer(i);
			}

			if (balls[i].getY() >= balls[i].yUpperBound
					- balls[i].getBallSize()
					|| balls[i].getY() <= balls[i].yLowerBound) {
				balls[i].moveY = balls[i].moveY * -1;
			}
			break;
		}

		// set balls back to the server when they complete their flow in the
		// gates
		if ((balls[i].getPosition() == 11) || (balls[i].getPosition() == 7)
				|| (balls[i].getPosition() == 8)
				|| (balls[i].getPosition() == 9)
				|| (balls[i].getPosition() == 10)) {
			if (balls[i].getY() >= 150) {
				setBallInServer(i);
			} else {
				setBounds(i);
			}
		}
	}

	private void setBallInServer(int i) {
		generator.setSeed(i * (i + 1));
		balls[i].setX(generator.nextInt(reservoirWidth));
		balls[i].setY(generator.nextInt(reservoirHeight));
		balls[i].setPosition(0);
		if (balls[i].moveX == 0)
			balls[i].moveX = generator.nextInt(15);
		setBounds(i);
	}

	private void setBounds(int ballIndex) {
		// TODO Auto-generated method stub
		// ball is in the server
		if (balls[ballIndex].getPosition() == 0) {
			balls[ballIndex].xUpperBound = reservoirWidth;
			balls[ballIndex].xLowerBound = 0;
			balls[ballIndex].yUpperBound = reservoirHeight;
			balls[ballIndex].yLowerBound = 0;
			// balls[ballIndex].moveX = generator.nextInt(6);
			// balls[ballIndex].moveY = generator.nextInt(6);
			// balls[ballIndex].moveX = 3;
			// balls[ballIndex].moveY = 3;
		} else {
			if ((balls[ballIndex].getPosition() == 1)
					|| (balls[ballIndex].getPosition() == 2)
					|| (balls[ballIndex].getPosition() == 3)
					|| (balls[ballIndex].getPosition() == 4)
					|| (balls[ballIndex].getPosition() == 5)
					|| (balls[ballIndex].getPosition() == 6)) {

				balls[ballIndex].xUpperBound = gates[balls[ballIndex]
						.getPosition() - 1].getX()
						+ gates[balls[ballIndex].getPosition() - 1].getWidth();
				balls[ballIndex].xLowerBound = gates[balls[ballIndex]
						.getPosition() - 1].getX();
				balls[ballIndex].yUpperBound = gates[balls[ballIndex]
						.getPosition() - 1].getY()/*
												 * +gate[balls[ballIndex].
												 * getPosition() -
												 * 1].getHeight()
												 */;
				balls[ballIndex].yLowerBound = gates[balls[ballIndex]
						.getPosition() - 1].getY();
			}
			// the ball is in one of the openings
			else {
				balls[ballIndex].xUpperBound = gates[balls[ballIndex]
						.getPosition() - 7].getOpeningsX()
						+ gates[balls[ballIndex].getPosition() - 7]
								.getGateWidth();
				balls[ballIndex].xLowerBound = gates[balls[ballIndex]
						.getPosition() - 7].getOpeningsX();
				balls[ballIndex].yUpperBound = gates[balls[ballIndex]
						.getPosition() - 7].getOpeningsY()
						+ gates[balls[ballIndex].getPosition() - 7]
								.getOpeningsHeight();
				balls[ballIndex].yLowerBound = gates[balls[ballIndex]
						.getPosition() - 7].getOpeningsY();
				// X change in motion
				balls[ballIndex].moveX = 0;
				// Y change in motion
				balls[ballIndex].moveY = 3;
			}
		}
	}

	public void openGate(int priority) {
		gates[priority].open();
	}

	public void closeGate(int priority) {
		gates[priority].close();
	}

	public void startRound() {
		initializeGates();
		timer.start();
	}

	public void endRound() {
		timer.stop();
		closeAllGates();
	}

	private void closeAllGates() {
		for (ClientData clientData : clientDataModel.getClientDataMap()
				.values()) {
			closeGate(clientData.getPriority());
		}
	}
}
