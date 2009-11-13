package edu.asu.commons.irrigation.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

import edu.asu.commons.irrigation.server.ClientData;

/**
 * $Id$
 * 
 * @author Allen Lee, Sanket Joshi
 */
public class CanalPanel extends JPanel {

	private static final long serialVersionUID = -6178860067928578812L;

//	private Graphics2D graphics2D;

	//////////////////animation logic parameters//////////////////////////////
	private Timer timer;

	private final static int DELAY = 20;

	private Ball balls[];

	private final static int BALLCOUNT = 500;

	private Gate gate[] = new Gate[6];

	private int gateHeight = 20;

	private Random generator = new Random();

	private int maximumIrrigationCapacity;

	private ClientDataModel clientDataModel;

	private int numberOfGates = 6;

	private int serverHeight = 100;

	private int serverWidth = 100;

	private double bandWidthCanalHeightMapping = 0.8;

	private int gateBuffer = 20;

	////////////////////////////////////////////////////////////////////////////

	public CanalPanel(ClientDataModel clientDataModel) {
		super();
		//when totalContributed bandwidth = 1.0, you dont see the canal line
		setClientDataModel(clientDataModel);
		initialize();
	}
	
	public void setClientDataModel(ClientDataModel clientDataModel) {
	    this.maximumIrrigationCapacity = clientDataModel.getGroupDataModel().getMaximumAvailableWaterFlow();
	    this.clientDataModel = clientDataModel;
	}

	/**
	 *  
	 */
	private void initialize() {
//		this.setSize(new Dimension(1098,150));
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		//initialize each gate
		initializeGates();
		initializeBalls();
		timer = new Timer(DELAY, new Rebound()); // setup the Timer to do an action
		// every DELAY times.
		timer.start(); // starts the timer. 

	}
	
	private void initializeGates() {
	    for(int i=0;i<numberOfGates ;i++){
	        gate[i] = new Gate(maximumIrrigationCapacity,i);
	    }
	}

	protected void paintComponent(Graphics graphics){
		super.paintComponent(graphics); // needed!
		updateGUI();
		Graphics2D graphics2D = (Graphics2D) graphics;
		graphics2D.setColor(Color.BLUE);
		graphics2D.fillRect(0,0,serverHeight,serverWidth);
		//draw the other gates
		for(int i=0;i<numberOfGates-1;i++){
			graphics2D.setColor(Color.BLUE);
			graphics2D.fillRect(gate[i].getX(), gate[i].getY(), gate[i].getWidth(),gate[i].getHeight());
		}

		int numClients = clientDataModel.getAllClientIdentifiers().size();
		for(int i=0;i<numClients;i++) {
			graphics2D.setColor(Color.BLUE);
			graphics2D.fillRect(gate[i].getOpeningsX(), gate[i].getOpeningsY(),gate[i].getOpeningsWidth(),
					gate[i].getOpeningsHeight());
			graphics2D.setColor(Color.BLACK);
			graphics2D.drawLine(gate[i].getx1(),gate[i].gety1(),gate[i].getx2(),gate[i].gety2());
		}

		graphics2D.setColor(Color.BLUE);
		graphics2D.fillRect(gate[5].getX(), gate[5].getY(),10,gate[5].getHeight());

		//////////////////////Animation Logic////////////////////////////
		graphics.setColor(Color.white);
		//		int ballCounter = 0;
		for(int i=0;i<BALLCOUNT;i++){
		    if((balls[i].getPosition() == 1)||(balls[i].getPosition() == 2) || (balls[i].getPosition()==3)
		            || (balls[i].getPosition() == 4) || (balls[i].getPosition() == 5)){
		        if(!((!gate[balls[i].getPosition()-1].isGateOpen())&& balls[i].getY() >= (gate[balls[i].getPosition()-1].getY()+gate[balls[i].getPosition()-1].getHeight())))
		            graphics.fillOval(balls[i].x,balls[i].y,balls[i].getBallSize(), balls[i].getBallSize());
		    }
		    else{
		        if(balls[i].getPosition() != 0){
		            if(gate[balls[i].getPosition() - 6].getHeight() != 0)
		                graphics.fillOval(balls[i].x,balls[i].y,balls[i].getBallSize(), balls[i].getBallSize());
		        }
		        else{
		            graphics.fillOval(balls[i].x,balls[i].y,balls[i].getBallSize(), balls[i].getBallSize());
		        }
		    }
		}

	}



	private void updateGUI() {
		for(int i=1;i<6;i++){
			if(gate[i-1].isGateOpen()){
				if(!(gate[i-1].getHeight() - gateHeight < 0)){
					gate[i].setHeight(gate[i-1].getHeight() - gateHeight );

				}
				else{
					gate[i].setHeight(0);
				}
			}
			else{
				gate[i].setHeight(gate[i-1].getHeight());

			}
			//order matters here
			if(!(i == 5)){
				gate[i].setOpeningsHeight((maximumIrrigationCapacity*bandWidthCanalHeightMapping)
						- gate[i].getHeight());
				gate[i].setOpeningsY(gate[i].getOpeningsHeight() - 50);

				//opening the lid logic
				if(gate[i].isGateOpen()){
					gate[i].setx1(gate[i].getx2());
					if(!(gate[i].gety2()- gate[i].getGateWidth() < gate[i].getY()))
						gate[i].sety1(gate[i].gety2()- gate[i].getGateWidth());
					else
						gate[i].sety1(gate[i].getOpeningsY());
				}
				else{
					gate[i].setx1(gate[i].getdefaultx1());
					gate[i].sety1(gate[i].getdefaulty1());
				}

				if(gate[0].isGateOpen()){
					gate[0].setx1(gate[0].getx2());
					if(!(gate[0].gety2()- gate[0].getGateWidth() < gate[0].getY()))
						gate[0].sety1(gate[0].gety2()- gate[0].getGateWidth());
					else
						gate[0].sety1(gate[0].getY());
				}
				else{
					gate[0].setx1(gate[0].getdefaultx1());
					gate[0].sety1(gate[0].getdefaulty1());
				}

			}
		}
	}

	/*
	 * initialize the Balls
	 */
	private void initializeBalls() {
		balls = new Ball[BALLCOUNT];
		for(int i=0;i<BALLCOUNT;i++){
			balls[i] = new Ball(generator);
		}
	}

	// this is the private class the Timer will look at every DELAY seconds
	private class Rebound implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for(int i=0;i<BALLCOUNT;i++){
				//updateGUI();
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
	private void process(int i){
		switch(balls[i].getPosition()) {

		case 0: if((balls[i].x >= (serverWidth - gateBuffer ) && balls[i].x <= serverWidth) && (balls[i].y >= serverHeight-(int)(maximumIrrigationCapacity*bandWidthCanalHeightMapping) &&
				balls[i].y <= serverHeight)){
			balls[i].setPosition(1);
			setBounds(i);
		}
		//still in server
		else{
			setBounds(i);
			if(balls[i].x <= balls[i].xLowerBound || balls[i].x >= balls[i].xUpperBound-balls[i].getBallSize()){
				balls[i].moveX = balls[i].moveX * -1;
			}
			if(balls[i].y <= balls[i].yLowerBound || balls[i].y >= balls[i].yUpperBound-balls[i].getBallSize())
				balls[i].moveY = balls[i].moveY * -1;
		}
		break;

		case 1: if(gate[0].isGateOpen() && (balls[i].x >= gate[0].getOpeningsX() && balls[i].x <= (gate[0].getOpeningsX()+gateBuffer))
				&& (balls[i].y >= serverHeight - gateBuffer && balls[i].y <= serverHeight)){
			balls[i].setPosition(7);
			//directly pass in the information
			setBounds(i);
		}
		else{
			setBounds(i);
			if(balls[i].getX() > balls[i].xUpperBound){
				balls[i].setPosition(2);
			}
			if(balls[i].getY() >= balls[i].yUpperBound - balls[i].getBallSize() || balls[i].getY() <= balls[i].yLowerBound){
				balls[i].moveY = balls[i].moveY*-1;
			}
		}

		break;

		case 2: if(gate[1].isGateOpen() && (balls[i].x >= gate[1].getOpeningsX() && balls[i].x <= (gate[1].getOpeningsX()+gateBuffer))
				&& (balls[i].y >= serverHeight - gateBuffer && balls[i].y <= serverHeight)){
			balls[i].setPosition(8);
			//directly pass in the information
			setBounds(i);
		}
		else{
			setBounds(i);
			if(balls[i].getX() > balls[i].xUpperBound){
				balls[i].setPosition(3);
			}
			if(balls[i].getY() >= balls[i].yUpperBound - balls[i].getBallSize() || balls[i].getY() <= balls[i].yLowerBound){
				balls[i].moveY = balls[i].moveY*-1;
			}
		}
		break;


		case 3: if(gate[2].isGateOpen() && (balls[i].x >= gate[2].getOpeningsX() && balls[i].x <= (gate[2].getOpeningsX()+gateBuffer))
				&& (balls[i].y >= serverHeight - gateBuffer && balls[i].y <= serverHeight)){
			balls[i].setPosition(9);
			//directly pass in the information
			setBounds(i);
			/*	balls[i].xBOUNDSUPPER = 420;
				balls[i].xLowerBound = 400;
				balls[i].yBOUNDSUPPER = 150;
				balls[i].yBOUNDSLOWER = 80;
				balls[i].moveX = 0;
				balls[i].moveY = 3;*/
		}
		else{
			setBounds(i);
			if(balls[i].getX() > balls[i].xUpperBound){
				balls[i].setPosition(4);
			}
			if(balls[i].getY() >= balls[i].yUpperBound - balls[i].getBallSize() || balls[i].getY() <= balls[i].yLowerBound){
				balls[i].moveY = balls[i].moveY*-1;
			}
		}
		break;

		case 4: if(gate[3].isGateOpen() && (balls[i].x >= gate[3].getOpeningsX() && balls[i].x <= (gate[3].getOpeningsX()+gateBuffer))
				&& (balls[i].y >= serverHeight - gateBuffer && balls[i].y <= serverHeight)){
			balls[i].setPosition(10);
			//directly pass in the information
			setBounds(i);
		}
		else{
			setBounds(i);
			if(balls[i].getX() > balls[i].xUpperBound){
				balls[i].setPosition(5);
			}
			if(balls[i].getY() >= balls[i].yUpperBound - balls[i].getBallSize() || balls[i].getY() <= balls[i].yLowerBound){
				balls[i].moveY = balls[i].moveY*-1;
			}
		}
		break;

		case 5: if(gate[4].isGateOpen() && (balls[i].x >= gate[4].getOpeningsX() && balls[i].x <= (gate[4].getOpeningsX()+gateBuffer))
				&& (balls[i].y >= serverHeight - gateBuffer && balls[i].y <= serverHeight)){
			balls[i].setPosition(11);
			//directly pass in the information
			setBounds(i);
		}
		else {
			setBounds(i);
			if(balls[i].getX() > balls[i].xUpperBound){
				balls[i].setPosition(6);
			}
			if(balls[i].getY() >= balls[i].yUpperBound - balls[i].getBallSize() || balls[i].getY() <= balls[i].yLowerBound){
				balls[i].moveY = balls[i].moveY*-1;
			}
		}
		break;

		case 6: setBounds(i);
		if(balls[i].getX() > balls[i].xUpperBound){
			setBallInServer(i);
		}

		if(balls[i].getY() >= balls[i].yUpperBound - balls[i].getBallSize() || balls[i].getY() <= balls[i].yLowerBound){
			balls[i].moveY = balls[i].moveY*-1;
		}
		break;
		}

		//set balls back to the server when they complete their flow in the gates
		if((balls[i].getPosition() == 11) || (balls[i].getPosition() == 7) || (balls[i].getPosition() == 8)
				|| (balls[i].getPosition() == 9) || (balls[i].getPosition() == 10)){
			if(balls[i].getY() >= 150){
				setBallInServer(i);
			}
			else{
				setBounds(i);
			}
		}
	}


	private void setBallInServer(int i) {
		generator.setSeed(i*(i+1));
		balls[i].setX(generator.nextInt(serverWidth));
		balls[i].setY(generator.nextInt(serverHeight));
		balls[i].setPosition(0);
		if(balls[i].moveX == 0)
			balls[i].moveX = generator.nextInt(15);
		setBounds(i);
	}

	private void setBounds(int ballIndex) {
		// TODO Auto-generated method stub
		//ball is in the server
		if(balls[ballIndex].getPosition() == 0){
			balls[ballIndex].xUpperBound = serverWidth;
			balls[ballIndex].xLowerBound = 0;
			balls[ballIndex].yUpperBound = serverHeight;
			balls[ballIndex].yLowerBound = 0;
			//balls[ballIndex].moveX = generator.nextInt(6);
			//balls[ballIndex].moveY = generator.nextInt(6);
			//balls[ballIndex].moveX = 3;
			//balls[ballIndex].moveY = 3;
		}
		else{
			if((balls[ballIndex].getPosition() == 1)||(balls[ballIndex].getPosition() == 2) || (balls[ballIndex].getPosition()==3)
					|| (balls[ballIndex].getPosition() == 4) || (balls[ballIndex].getPosition() == 5)||(balls[ballIndex].getPosition() == 6)){

				balls[ballIndex].xUpperBound = gate[balls[ballIndex].getPosition() - 1].getX()+gate[balls[ballIndex].getPosition() - 1].getWidth();
				balls[ballIndex].xLowerBound = gate[balls[ballIndex].getPosition() - 1].getX();
				balls[ballIndex].yUpperBound = gate[balls[ballIndex].getPosition() - 1].getY()/*+gate[balls[ballIndex].getPosition() - 1].getHeight()*/;
				balls[ballIndex].yLowerBound = gate[balls[ballIndex].getPosition() - 1].getY();
			}
			//the ball is in one of the openings
			else{
				balls[ballIndex].xUpperBound = gate[balls[ballIndex].getPosition() - 7].getOpeningsX()+gate[balls[ballIndex].getPosition() - 7].getGateWidth();
				balls[ballIndex].xLowerBound = gate[balls[ballIndex].getPosition() - 7].getOpeningsX();
				balls[ballIndex].yUpperBound = gate[balls[ballIndex].getPosition() - 7].getOpeningsY()+gate[balls[ballIndex].getPosition() - 7].getOpeningsHeight();
				balls[ballIndex].yLowerBound = gate[balls[ballIndex].getPosition() - 7].getOpeningsY();
				//X change in motion
				balls[ballIndex].moveX = 0;
				//Y change in motion
				balls[ballIndex].moveY = 3;
			}
		}
	}

	public void openGate(int priority) {
		gate[priority].setGateOpen(true);
//		paintComponent(graphics2D);
		repaint();
		
	}

	public void closeGate(int priority) {
		gate[priority].setGateOpen(false);
		gate[priority].setx1(gate[priority].getdefaultx1());
		gate[priority].sety1(gate[priority].getdefaulty1());
		/*if(!(priority == 0))
		gate[priority].setHeight(gate[priority-1].getHeight());*/
		repaint();
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
	    for (ClientData clientData: clientDataModel.getClientDataMap().values()) {
	        closeGate(clientData.getPriority());
	    }
	}
}

