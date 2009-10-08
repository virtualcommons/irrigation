/**
 * 
 */
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



/**
 * @author Sanket
 *
 */
public class CanalPanelAnimation extends JPanel{

	private static final long serialVersionUID = 1L;

	/**
	 * This is the default constructor
	 */
	private int x1UpStream;

	private int y1UpStream;

	private int x2UpStream = 420;

	private int y2UpStream = 100;

	private int y2DownStream = 100;

	private int x2DownStream = 950;

	private int y1DownStream;

	private int x1DownStream;

	private Graphics2D graphics2D;

	private boolean flagDownStream = false;

	private boolean flagUpStream = false;
	
	private double totalContributedBandwidth;
	
	//////////////////animation logic parameters//////////////////////////////
 	private Timer timer;
   	
   	private final int DELAY = 20;

   	private Ball balls[];
   	
   	private int BALLCOUNT = 500;

	private long prev_time;

	////////////////////////////////////////////////////////////////////////////

	public CanalPanelAnimation(double totalContributedBandwidth) {
		super();
		this.totalContributedBandwidth = totalContributedBandwidth;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(1098,150);
		this.setLayout(new GridBagLayout());
		this.setBackground(Color.white);
		
		initializexy();
 		
 		timer = new Timer(DELAY, new ReboundListener()); // setup the Timer to do an action
 														// every DELAY times.
 		prev_time = System.currentTimeMillis();
 		timer.start(); // starts the timer. 
 
 	}

	private void gateReboundListener(){

		if(flagUpStream == false && flagDownStream == false){
			System.out.println("Opening gates");
			closeGates(0);
			openGates(1);
		}
		else
		if(flagUpStream == false && flagDownStream == true)
		{
			openGates(0);
		}
		else
		if(flagUpStream == true && flagDownStream == true)
		{
			closeGates(1);
			//openGates(1);
		}
		else
		if(flagUpStream == true && flagDownStream == false){
			closeGates(0);
			//closeGates(1);
		}
}

	protected void paintComponent(Graphics graphics){
		super.paintComponent(graphics); // needed!
		Graphics2D graphics2D = (Graphics2D)graphics;
		this.graphics2D = graphics2D;
		//this.graphics2D.setColor(new Color(186,226,237));
		this.graphics2D.setColor(Color.BLUE);
		this.graphics2D.fillRect(0,0,100,100);
		//graphics2D.fillRoundRect(5,25,100,100,10,10);
		//this.graphics2D.setColor(new Color(186,226,237));
		if((int)(totalContributedBandwidth) == 1)
			this.graphics2D.fillRect(0, 100 - 2,420,2);
		else
		/*this.graphics2D.fillRect(0, 100-(int)(totalContributedBandwidth*0.8),1093, (int)(totalContributedBandwidth*0.8));
		this.graphics2D.fillRect(400, 100, 20, 50);
		this.graphics2D.fillRect(930, 100, 20, 50);
		*/
		
		this.graphics2D.fillRect(0, 100-(int)(totalContributedBandwidth*0.8),420, (int)(totalContributedBandwidth*0.8));
		if(flagUpStream == true){
			this.graphics2D.fillRect(420, 100-(int)(totalContributedBandwidth*0.8),(950-420), (int)(totalContributedBandwidth*0.8)-20);
				//here if downstream is alsoo open, dont show the remaining part
				if(flagDownStream == false)
					this.graphics2D.fillRect(950, 100-(int)(totalContributedBandwidth*0.8),(1093-950), (int)(totalContributedBandwidth*0.8)-20);
				else
					this.graphics2D.fillRect(950, 100-(int)(totalContributedBandwidth*0.8),0,0);
		
				this.graphics2D.fillRect(930,100 - 20, 20, 50+20);
			
		}
		
		else{
			this.graphics2D.fillRect(420, 100-(int)(totalContributedBandwidth*0.8),(950-420), (int)(totalContributedBandwidth*0.8));
			if(flagDownStream == false)
				this.graphics2D.fillRect(950, 100-(int)(totalContributedBandwidth*0.8),(1093-950), (int)(totalContributedBandwidth*0.8));
			else{
				if(totalContributedBandwidth <= 25){
					this.graphics2D.fillRect(950, 100-(int)(totalContributedBandwidth*0.8),0,0);
				}
				else
					this.graphics2D.fillRect(950, 100-(int)(totalContributedBandwidth*0.8),(1093-950), (int)(totalContributedBandwidth*0.8)-20);
			}
				
			this.graphics2D.fillRect(930, 100, 20, 50);
		}
				
		this.graphics2D.fillRect(400, 100, 20, 50);
					
		this.graphics2D.setColor(Color.BLACK);
		if(flagUpStream == false){
			x1UpStream = x2UpStream - 20;
			y1UpStream = y2UpStream;	
			//resetting the position of downstream gate
			//y2DownStream = y2DownStream + 20;
			if(y2DownStream == 100-20)
				y2DownStream = 100;
		}
		if(flagDownStream == false){
			x1DownStream = x2DownStream - 20;
			y1DownStream = y2DownStream;	
		}
		drawLid(x1UpStream,y1UpStream,x2UpStream,y2UpStream);
		drawLid(x1DownStream,y1DownStream,x2DownStream,y2DownStream);
		
		//////////////////////Animation Logic////////////////////////////
		graphics.setColor(Color.white);
		int ballCounter = 0;
		for(int i=0;i<BALLCOUNT;i++){
			if(!(balls[i].isInServer == false && totalContributedBandwidth == 0)){
				if(!(flagDownStream == true && flagUpStream == true && balls[i].x >= 950))	
			if(!(flagUpStream == true && totalContributedBandwidth <=25 && balls[i].x >=420)){
				if(flagUpStream == true && balls[i].x >=420 && totalContributedBandwidth >=25 ){
					if(!(flagDownStream == false && balls[i].y >= 80))
					//if(ballCounter < 20){
						graphics.fillOval(balls[i].x,balls[i].y,balls[i].getBallSize(), balls[i].getBallSize());
						//ballCounter++;
					//}
				}
				else{
					
					graphics.fillOval(balls[i].x,balls[i].y,balls[i].getBallSize(), balls[i].getBallSize()); // paints a whole new circle, with changing coords
					
				}
					
			}
				
			}
		}
	}
	
	private void initializexy() {
		// TODO Auto-generated method stub
		balls = new Ball[BALLCOUNT];
		if(balls == null){
			System.out.println("Ball is null");
		}
		for(int i=0;i<BALLCOUNT;i++){
			Random generator = new Random();
			balls[i] = new Ball(generator);
			if(balls[i] == null){
				System.out.println("Ball i is null");
			}
		}
		Random generator = new Random();
		System.out.println("Here it fails");
		balls[0].x = 40;
		balls[0].y = 50;
		
		for(int i=1;i<BALLCOUNT;i++){
			balls[i].x = generator.nextInt(100);
			balls[i].y = generator.nextInt(100);
			balls[i].xBOUNDSUPPER = 100;
			balls[i].yBOUNDSUPPER = 100;
			balls[i].xBOUNDSLOWER = 0;
			balls[i].yBOUNDSLOWER = 0;
			balls[i].moveX = generator.nextInt(6);
			balls[i].moveY = generator.nextInt(6);
			balls[i].isInServer = true;
			balls[i].isInSecondCanal = false;
			balls[i].isInFirstCanal = false;
		}
	}

	// this is the private class the Timer will look at every DELAY seconds
	private class ReboundListener implements ActionListener{
		Random generator = new Random();
		public void actionPerformed(ActionEvent e) {
			if((System.currentTimeMillis() - prev_time) > 5000){
				System.out.println("Change the time");
				prev_time = System.currentTimeMillis();
				gateReboundListener();
			}
			for(int i=0;i<BALLCOUNT;i++){
					balls[i].x += balls[i].moveX;
					balls[i].y += balls[i].moveY;	
				
				if(balls[i].isInServer == true){
					if((balls[i].x >= (100-20) && balls[i].x <= 100) && (balls[i].y >= 100-(int)(totalContributedBandwidth*0.8) &&
							balls[i].y <= 100)){
						balls[i].isInServer = false;
						balls[i].moveX = 3;
						balls[i].moveY = 3;
						balls[i].xBOUNDSUPPER = 1093;
						balls[i].xBOUNDSLOWER = 0;
						balls[i].yBOUNDSUPPER = 100;
						balls[i].yBOUNDSLOWER = 100-(int)(totalContributedBandwidth*0.8);
					}
					else{
						if(balls[i].x <= balls[i].xBOUNDSLOWER || balls[i].x >= balls[i].xBOUNDSUPPER-balls[i].getBallSize()){
							balls[i].moveX = balls[i].moveX * -1;
						}
						if(balls[i].y <= balls[i].yBOUNDSLOWER || balls[i].y >= balls[i].yBOUNDSUPPER-balls[i].getBallSize())
							balls[i].moveY = balls[i].moveY * -1;
					}
				}
				//within the canal
				else{
					// the upstream gate is open
					if(flagUpStream == true){
						if((balls[i].x >= 400 && balls[i].x <= 420) && (balls[i].y >= 80 && balls[i].y <= 100)){
							balls[i].isInFirstCanal = true;
							balls[i].xBOUNDSUPPER = 420;
							balls[i].xBOUNDSLOWER = 400;
							balls[i].yBOUNDSUPPER = 150;
							balls[i].yBOUNDSLOWER = 80;
							balls[i].moveX = 0;
							balls[i].moveY = 3;
						}
						if(balls[i].isInFirstCanal == true)
						if(balls[i].y <= balls[i].yBOUNDSLOWER || balls[i].y >= balls[i].yBOUNDSUPPER){
							balls[i].x = generator.nextInt(100);
							balls[i].y = generator.nextInt(100);
							balls[i].isInServer = true;
							balls[i].isInFirstCanal = false;
							balls[i].moveX = 3;
							balls[i].moveY = 3;
							balls[i].xBOUNDSUPPER = 100;
							balls[i].xBOUNDSLOWER = 0;
							balls[i].yBOUNDSUPPER = 100;
							balls[i].yBOUNDSLOWER = 0;
						}
						if(balls[i].x <= balls[i].xBOUNDSLOWER || balls[i].x >= balls[i].xBOUNDSUPPER-balls[i].getBallSize())
							balls[i].moveX = balls[i].moveX * -1;
					}
					
					if(flagDownStream == true){
						int yUpperBound;
						if(flagUpStream == true){
							 yUpperBound = 100 - (int)((totalContributedBandwidth)*0.8);
						}
						else
							yUpperBound = 80;
						if((balls[i].x >= 930 && balls[i].x <= 950) && (balls[i].y >= yUpperBound && balls[i].y <= 100)){
							balls[i].isInSecondCanal = true;
							balls[i].xBOUNDSUPPER = 950;
							balls[i].xBOUNDSLOWER = 930;
							balls[i].yBOUNDSUPPER = 150;
							balls[i].yBOUNDSLOWER = 80;
							balls[i].moveX = 0;
							balls[i].moveY = 3;
						}
						if(balls[i].isInSecondCanal == true)
						if(balls[i].y <= balls[i].yBOUNDSLOWER || balls[i].y >= balls[i].yBOUNDSUPPER){
							balls[i].x = generator.nextInt(100);
							balls[i].y = generator.nextInt(100);
							balls[i].isInServer = true;
							balls[i].isInSecondCanal = false;
							balls[i].xBOUNDSUPPER = 100;
							balls[i].xBOUNDSLOWER = 0;
							balls[i].yBOUNDSUPPER = 100;
							balls[i].yBOUNDSLOWER = 0;
							balls[i].moveX =3;
							balls[i].moveY =3;
						}
						if(balls[i].x <= balls[i].xBOUNDSLOWER || balls[i].x >= balls[i].xBOUNDSUPPER-balls[i].getBallSize())
							balls[i].moveX = balls[i].moveX * -1;
					}
					
					//ball goes out of the scope of the canal
					if(balls[i].isInFirstCanal == false && balls[i].isInSecondCanal == false){
					if(balls[i].x <= balls[i].xBOUNDSLOWER || balls[i].x >= balls[i].xBOUNDSUPPER-balls[i].getBallSize()){
						balls[i].x = generator.nextInt(100);
						balls[i].y = generator.nextInt(100);
						balls[i].moveX = 3;
						balls[i].isInServer = true;
						balls[i].xBOUNDSUPPER = 100;
						balls[i].xBOUNDSLOWER = 0;
						balls[i].yBOUNDSUPPER = 100;
						balls[i].yBOUNDSLOWER = 0;
					}
					if(balls[i].y <= balls[i].yBOUNDSLOWER || balls[i].y >= balls[i].yBOUNDSUPPER-balls[i].getBallSize())
						balls[i].moveY = balls[i].moveY * -1;
					}
				}
			}
			repaint();
		}
	}

	private void drawLid(int x1, int y1, int x2, int y2) {
		// TODO Auto-generated method stub
		graphics2D.drawLine(x1,y1,x2,y2);
	}

	public void openGates(int priority) {
		// TODO Auto-generated method stub
		switch(priority){
		case 0 :	x1UpStream = x2UpStream;
					if(totalContributedBandwidth < 25){
						y1UpStream = y2UpStream - (int)(totalContributedBandwidth*0.8);
					}
					else{
						y1UpStream = y2UpStream - 20; 
						//changing the position of downstream gate
						//y2DownStream = y2DownStream - 20;
						
					}
					if(y2DownStream == 100)
						y2DownStream = y2DownStream - 20;	
					flagUpStream = true;
					break;
					
		case 1 :    x1DownStream = x2DownStream;
					if(totalContributedBandwidth < 25){
						y1DownStream = y2DownStream - (int)(totalContributedBandwidth*0.8);
						//for the new animation
						//y2DownStream = y1DownStream;
					}
					else
					y1DownStream = y2DownStream - 20; 
					if(y1DownStream < (100-totalContributedBandwidth*0.8)){
						y1DownStream = y2DownStream - (int)(totalContributedBandwidth*0.8 - 20);
					}
					flagDownStream = true;
					break;
		
		}
		paintComponent(graphics2D);
	}

	public void closeGates(int priority) {
		// TODO Auto-generated method stub
		switch (priority){
		case 0 : flagUpStream = false;
				 break;
				 
		case 1 : flagDownStream = false;
				 break;
		}
	}

	public void endRound() {
		// TODO Auto-generated method stub
		flagDownStream = false;
		flagDownStream = false;
		totalContributedBandwidth = 0;
		x2UpStream = 420;

		y2UpStream = 100;

		y2DownStream = 100;

		x2DownStream = 950;
		
		initializexy();
	}
}
