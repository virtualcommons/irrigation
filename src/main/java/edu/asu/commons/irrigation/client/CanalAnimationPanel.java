package edu.asu.commons.irrigation.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * $Id$
 * 
 * The animation for the irrigation canal.
 * 
 * FIXME: needs heavy refactoring.
 * 
 * @author Sanket Joshi
 * @author Allen Lee
 * @version $Rev$
 */
public class CanalAnimationPanel extends JPanel {

    private static final long serialVersionUID = 2049547253093489218L;

    //private IrrigationClientGameState clientGameState;

    //////////////////animation logic parameters//////////////////////////////
    private Timer timer;

    private final int DELAY = 20;

    private Ball balls[];

    private int BALLCOUNT = 500;

    private Gate gate[] = new Gate[6];

    private int gateHeight = 20;

    Random generator = new Random();

    private boolean enableBallAnimation = true;

    private double totalContributedBandwidth;

    private long prev_time;

    private int gateCount = 0;

    ////////////////////////////////////////////////////////////////////////////

    public CanalAnimationPanel(double totalContributedBandwidth) {
        super();
        this.totalContributedBandwidth = totalContributedBandwidth;
        initialize();
    }

    /**
     * 
     * @return void
     */
    private void initialize() {
        this.setPreferredSize(new Dimension(1098,150));
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.white);
        //initializing the constructor for Gates
        for(int i=0;i<6;i++){
            gate[i] = new Gate(totalContributedBandwidth,i);
            //System.out.println("Gate "+i+"x : "+gate[i].getX()+"y : "+gate[i].getY());
        }

        initializexy();

        timer = new Timer(DELAY, new ReboundListener()); // setup the Timer to do an action
        // every DELAY times.
        prev_time = System.currentTimeMillis();
        timer.start(); // starts the timer. 
    }

    private void gateReboundListener(){

        openGates(gateCount );
        if(!(gateCount == 0))
            closeGates(gateCount-1);
        /*if(!gate[0].isOpenGate() && flagDownStream == false){
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
         */}



    protected void paintComponent(Graphics graphics){
        updateGUI();
        super.paintComponent(graphics); // needed!
        Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D = graphics2D;
        graphics2D.setColor(Color.BLUE);
        graphics2D.fillRect(0,0,100,100);
        //draw the other gates
        for(int i=0;i<5;i++){
            graphics2D.setColor(Color.BLUE);
            graphics2D.fillRect(gate[i].getX(), gate[i].getY(), gate[i].getWidth(),gate[i].getHeight());
            graphics2D.fillRect(gate[i].getOpeningsX(), gate[i].getOpeningsY(),gate[i].getOpeningsWidth(),
                    gate[i].getOpeningsHeight());
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawLine(gate[i].getx1(),gate[i].gety1(),gate[i].getx2(),gate[i].gety2());
        }

        graphics2D.setColor(Color.BLUE);
        graphics2D.fillRect(gate[5].getX(), gate[5].getY(),10,gate[5].getHeight());

        //////////////////////Animation Logic////////////////////////////
        // FIXME: good lord, refactor this.
        if (enableBallAnimation) {
            graphics.setColor(Color.white);
            for(int i=0;i<BALLCOUNT;i++){
                //get the position information of the ball. and then recheck the condition for height.
                if((balls[i].getPosition() == 1)||(balls[i].getPosition() == 2) || (balls[i].getPosition()==3)
                        || (balls[i].getPosition() == 4) || (balls[i].getPosition() == 5)){
                    if(!((!gate[balls[i].getPosition()-1].isOpenGate())&& balls[i].getY() >= (gate[balls[i].getPosition()-1].getY()+gate[balls[i].getPosition()-1].getHeight())))
                        graphics.fillOval(balls[i].x,balls[i].y,balls[i].getBallSize(), balls[i].getBallSize());
                }
                else {
                    if(balls[i].getPosition() != 0){
                        if(gate[balls[i].getPosition() - 6].getHeight() != 0) {
                            graphics.fillOval(balls[i].x,balls[i].y,balls[i].getBallSize(), balls[i].getBallSize());
                        }
                    }
                    else {
                        graphics.fillOval(balls[i].x,balls[i].y,balls[i].getBallSize(), balls[i].getBallSize());
                    }
                }
            }
        }

    }



    private void updateGUI() {
        for(int i=1;i<6;i++){
            if(gate[i-1].isOpenGate()){
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
                gate[i].setOpeningsHeight((totalContributedBandwidth*0.8)
                        - gate[i].getHeight());
                gate[i].setOpeningsY(gate[i].getOpeningsHeight() - 50);

                //opening the lid logic
                if(gate[i].isOpenGate()){
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

                if(gate[0].isOpenGate()){
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
    private void initializexy() {
        balls = new Ball[BALLCOUNT];
        if(balls == null){
            System.out.println("Ball is null");
        }

        for(int i=0;i<BALLCOUNT;i++){
            balls[i] = new Ball(generator);

        }
    }

    // this is the private class the Timer will look at every DELAY seconds
    private class ReboundListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if((System.currentTimeMillis() - prev_time) > 5000){
                //System.out.println("Change the time");
                prev_time = System.currentTimeMillis();
                gateCount++;
                if(gateCount > 4)
                    gateCount = 0;
                gateReboundListener();
            }
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

        case 0: 
            if((balls[i].x >= (100 - 20) && balls[i].x <= 100) 
                    && (balls[i].y >= 100-(int)(totalContributedBandwidth*0.8) 
                            && balls[i].y <= 100))
            {
                balls[i].setPosition(1);
                setBounds(i);
            }
            //still in server
            else {
                setBounds(i);
                if(balls[i].x <= balls[i].xBOUNDSLOWER || balls[i].x >= balls[i].xBOUNDSUPPER-balls[i].getBallSize()){
                    balls[i].moveX = balls[i].moveX * -1;
                }
                if(balls[i].y <= balls[i].yBOUNDSLOWER || balls[i].y >= balls[i].yBOUNDSUPPER-balls[i].getBallSize())
                    balls[i].moveY = balls[i].moveY * -1;
            }
            break;

        case 1: /*if(gate[0].isOpenGate() && (balls[i].x >= 400 && balls[i].x <= 420) && (balls[i].y >= 80 && 
				balls[i].y <= 100)){
         */	if(gate[0].isOpenGate() && (balls[i].x >= gate[0].getOpeningsX() && balls[i].x <= (gate[0].getOpeningsX()+20))
                 && (balls[i].y >= 80 && balls[i].y <= 100)){
             balls[i].setPosition(7);
             //directly pass in the information
             setBounds(i);
             /*System.out.println("X upper :"+balls[i].xBOUNDSUPPER+" Y Upper :"+balls[i].yBOUNDSUPPER
							+" X Lower"+balls[i].xBOUNDSLOWER+" X Upper"+balls[i].xBOUNDSUPPER);*/
             /*	balls[i].xBOUNDSUPPER = 420;
					balls[i].xBOUNDSLOWER = 400;
					balls[i].yBOUNDSUPPER = 150;
					balls[i].yBOUNDSLOWER = 80;
					balls[i].moveX = 0;
					balls[i].moveY = 3;*/
         }
         else{
             setBounds(i);
             /*System.out.println("X upper :"+balls[i].xBOUNDSUPPER+" Y Upper :"+balls[i].yBOUNDSUPPER
								+" X Lower"+balls[i].xBOUNDSLOWER+" X Upper"+balls[i].xBOUNDSUPPER);
              */
             if(balls[i].getX() > balls[i].xBOUNDSUPPER){
                 balls[i].setPosition(2);
             }
             if(balls[i].getY() >= balls[i].yBOUNDSUPPER - balls[i].getBallSize() || balls[i].getY() <= balls[i].yBOUNDSLOWER){
                 balls[i].moveY = balls[i].moveY*-1;
             }
         }

         break;

        case 2: if(gate[1].isOpenGate() && (balls[i].x >= gate[1].getOpeningsX() && balls[i].x <= (gate[1].getOpeningsX()+20))
                && (balls[i].y >= 80 && balls[i].y <= 100)){
            balls[i].setPosition(8);
            //directly pass in the information
            setBounds(i);
            /*	balls[i].xBOUNDSUPPER = 420;
				balls[i].xBOUNDSLOWER = 400;
				balls[i].yBOUNDSUPPER = 150;
				balls[i].yBOUNDSLOWER = 80;
				balls[i].moveX = 0;
				balls[i].moveY = 3;*/
        }
        else{
            setBounds(i);
            if(balls[i].getX() > balls[i].xBOUNDSUPPER){
                balls[i].setPosition(3);
            }
            if(balls[i].getY() >= balls[i].yBOUNDSUPPER - balls[i].getBallSize() || balls[i].getY() <= balls[i].yBOUNDSLOWER){
                balls[i].moveY = balls[i].moveY*-1;
            }
        }
        break;


        case 3: if(gate[2].isOpenGate() && (balls[i].x >= gate[2].getOpeningsX() && balls[i].x <= (gate[2].getOpeningsX()+20))
                && (balls[i].y >= 80 && balls[i].y <= 100)){
            balls[i].setPosition(9);
            //directly pass in the information
            setBounds(i);
            /*	balls[i].xBOUNDSUPPER = 420;
				balls[i].xBOUNDSLOWER = 400;
				balls[i].yBOUNDSUPPER = 150;
				balls[i].yBOUNDSLOWER = 80;
				balls[i].moveX = 0;
				balls[i].moveY = 3;*/
        }
        else{
            setBounds(i);
            if(balls[i].getX() > balls[i].xBOUNDSUPPER){
                balls[i].setPosition(4);
            }
            if(balls[i].getY() >= balls[i].yBOUNDSUPPER - balls[i].getBallSize() || balls[i].getY() <= balls[i].yBOUNDSLOWER){
                balls[i].moveY = balls[i].moveY*-1;
            }
        }
        break;

        case 4: if(gate[3].isOpenGate() && (balls[i].x >= gate[3].getOpeningsX() && balls[i].x <= (gate[3].getOpeningsX()+20))
                && (balls[i].y >= 80 && balls[i].y <= 100)){
            balls[i].setPosition(10);
            //directly pass in the information
            setBounds(i);
            /*	balls[i].xBOUNDSUPPER = 420;
				balls[i].xBOUNDSLOWER = 400;
				balls[i].yBOUNDSUPPER = 150;
				balls[i].yBOUNDSLOWER = 80;
				balls[i].moveX = 0;
				balls[i].moveY = 3;*/
        }
        else{
            setBounds(i);
            if(balls[i].getX() > balls[i].xBOUNDSUPPER){
                balls[i].setPosition(5);
            }
            if(balls[i].getY() >= balls[i].yBOUNDSUPPER - balls[i].getBallSize() || balls[i].getY() <= balls[i].yBOUNDSLOWER){
                balls[i].moveY = balls[i].moveY*-1;
            }
        }
        break;

        case 5: if(gate[4].isOpenGate() && (balls[i].x >= gate[4].getOpeningsX() && balls[i].x <= (gate[4].getOpeningsX()+20))
                && (balls[i].y >= 80 && balls[i].y <= 100)){
            balls[i].setPosition(11);
            //directly pass in the information
            setBounds(i);
            /*	balls[i].xBOUNDSUPPER = 420;
				balls[i].xBOUNDSLOWER = 400;
				balls[i].yBOUNDSUPPER = 150;
				balls[i].yBOUNDSLOWER = 80;
				balls[i].moveX = 0;
				balls[i].moveY = 3;*/
        }
        else{
            setBounds(i);
            if(balls[i].getX() > balls[i].xBOUNDSUPPER){
                balls[i].setPosition(6);
            }
            if(balls[i].getY() >= balls[i].yBOUNDSUPPER - balls[i].getBallSize() || balls[i].getY() <= balls[i].yBOUNDSLOWER){
                balls[i].moveY = balls[i].moveY*-1;
            }
        }
        break;

        case 6: setBounds(i);
        if(balls[i].getX() > balls[i].xBOUNDSUPPER){
            /*balls[i].setX(generator.nextInt(100));
   					balls[i].setY(100 - gate[0].getHeight()+ generator.nextInt(gate[0].getHeight()));
   					balls[i].setPosition(0);
   					setBounds(i);*/
            setBallInTank(i);
        }

        if(balls[i].getY() >= balls[i].yBOUNDSUPPER - balls[i].getBallSize() || balls[i].getY() <= balls[i].yBOUNDSLOWER){
            balls[i].moveY = balls[i].moveY*-1;
        }
        break;
        }

        if((balls[i].getPosition() == 11) || (balls[i].getPosition() == 7) || (balls[i].getPosition() == 8)
                || (balls[i].getPosition() == 9) || (balls[i].getPosition() == 10)){
            if(balls[i].getY() >= 150){
                /*balls[i].setX(generator.nextInt(100));
       					balls[i].setY(100 - gate[0].getHeight()+ generator.nextInt(gate[0].getHeight()));
       						//balls[i].setY(generator.nextInt(100));
       						balls[i].setPosition(0);
       						setBounds(i);
                 */
                setBallInTank(i);
            }
            else{
                setBounds(i);
                //System.out.println("X upper :"+balls[i].xBOUNDSUPPER+" Y Upper :"+balls[i].yBOUNDSUPPER
                //+" X Lower"+balls[i].xBOUNDSLOWER+" X Upper"+balls[i].xBOUNDSUPPER);
            }
        }
    }


    private void setBallInTank(int i) {
        // FIXME: is this supposed to be a random seed?
        generator.setSeed(i*(i+1));
        balls[i].setX(generator.nextInt(100));
        balls[i].setY(generator.nextInt(100));

        //balls[i].setY(100 - gate[0].getHeight()+ generator.nextInt(gate[0].getHeight()));
        balls[i].setPosition(0);
        if(balls[i].moveX == 0) {
            balls[i].moveX = generator.nextInt(15);
        }
        setBounds(i);
    }

    private void setBounds(int ballIndex) {
        // ball is in the server
        if(balls[ballIndex].getPosition() == 0){
            balls[ballIndex].xBOUNDSUPPER = 100;
            balls[ballIndex].xBOUNDSLOWER = 0;
            balls[ballIndex].yBOUNDSUPPER = 100;
            balls[ballIndex].yBOUNDSLOWER = 0;
            //balls[ballIndex].moveX = generator.nextInt(6);
            //balls[ballIndex].moveY = generator.nextInt(6);
            //balls[ballIndex].moveX = 3;
            //balls[ballIndex].moveY = 3;
        }
        else{
            if((balls[ballIndex].getPosition() == 1)||(balls[ballIndex].getPosition() == 2) || (balls[ballIndex].getPosition()==3)
                    || (balls[ballIndex].getPosition() == 4) || (balls[ballIndex].getPosition() == 5)||(balls[ballIndex].getPosition() == 6)){

                balls[ballIndex].xBOUNDSUPPER = gate[balls[ballIndex].getPosition() - 1].getX()+gate[balls[ballIndex].getPosition() - 1].getWidth();
                balls[ballIndex].xBOUNDSLOWER = gate[balls[ballIndex].getPosition() - 1].getX();
                balls[ballIndex].yBOUNDSUPPER = gate[balls[ballIndex].getPosition() - 1].getY()/*+gate[balls[ballIndex].getPosition() - 1].getHeight()*/;
                balls[ballIndex].yBOUNDSLOWER = gate[balls[ballIndex].getPosition() - 1].getY();
                //balls[ballIndex].moveX = 3;
                //balls[ballIndex].moveY = 3;

            }
            //the ball is in one of the openings
            else{
                balls[ballIndex].xBOUNDSUPPER = gate[balls[ballIndex].getPosition() - 7].getOpeningsX()+gate[balls[ballIndex].getPosition() - 7].getGateWidth();
                balls[ballIndex].xBOUNDSLOWER = gate[balls[ballIndex].getPosition() - 7].getOpeningsX();
                balls[ballIndex].yBOUNDSUPPER = gate[balls[ballIndex].getPosition() - 7].getOpeningsY()+gate[balls[ballIndex].getPosition() - 7].getOpeningsHeight();
                balls[ballIndex].yBOUNDSLOWER = gate[balls[ballIndex].getPosition() - 7].getOpeningsY();
                balls[ballIndex].moveX = 0;
                balls[ballIndex].moveY = 3;
            }
        }
    }

    public void openGates(int priority) {
        gate[priority].setGateOpen(true);
        // set the height of all the canal panels depending on the state of the game
        repaint();
    }

    public void closeGates(int priority) {
        gate[priority].setGateOpen(false);
        gate[priority].setx1(gate[priority].getdefaultx1());
        gate[priority].sety1(gate[priority].getdefaulty1());
        /*if(!(priority == 0))
		gate[priority].setHeight(gate[priority-1].getHeight());*/
        repaint();
    }

    public void endRound() {
        initializexy();
    }
}

