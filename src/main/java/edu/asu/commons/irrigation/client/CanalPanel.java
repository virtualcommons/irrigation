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
 * FIXME: convoluted gate and animation logic is in dire need of refactoring / rewrite
 * 
 * @author Sanket Joshi, Allen Lee
 */
public class CanalPanel extends JPanel {

    private static final long serialVersionUID = -6178860067928578812L;

    // ////////////////animation logic parameters//////////////////////////////
    private Timer timer;

    private final static int DELAY = 20;

    private Ball[] particles;

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
        List<ClientData> sortedClientDataList = clientDataModel.getClientDataSortedByPriority();
        updateGates(sortedClientDataList);
        checkRestrictedVisibility(sortedClientDataList);
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setColor(Color.BLUE);
        graphics2D.fillRect(0, 0, reservoirHeight, reservoirWidth);

        // draws the canal leading up to the gates
        for (int i = 0; i < NUMBER_OF_GATES - 1; i++) {
            ClientData clientData = sortedClientDataList.get(i);
            if (clientDataModel.getClientData().isImmediateNeighbor(clientData)) {
                graphics2D.setColor(Color.BLUE);
                graphics2D.fillRect(gates[i].getX(), gates[i].getY(),
                        gates[i].getWidth(), gates[i].getHeight());
            }
            else {
                graphics2D.setColor(Color.GRAY);
                graphics2D.fillRect(gates[i].getX(), gates[i].getY(),
                        gates[i].getWidth(), gates[i].getHeight());
            }
        }
        for (ClientData clientData : sortedClientDataList) {
            int priority = clientData.getPriority();
            Gate gate = gates[priority];
            if (clientDataModel.getClientData().isImmediateNeighbor(clientData)) {
                graphics2D.setColor(Color.BLUE);
                graphics2D.fillRect(gate.getOpeningsX(), gate.getOpeningsY(),
                        gate.getOpeningsWidth(), gate.getOpeningsHeight());
                graphics2D.setColor(Gate.DEFAULT_COLOR);
                graphics2D.drawLine(gate.getx1(), gate.gety1(), gate.getx2(),
                        gate.gety2());
            }
        }

        graphics2D.setColor(Color.BLUE);
        graphics2D.fillRect(gates[5].getX(), gates[5].getY(), 10,
                gates[5].getHeight());

        // particle system animation logic for the balls
        graphics.setColor(Color.WHITE);
        // int ballCounter = 0;
        for (int i = 0; i < BALLCOUNT; i++) {
            if ((particles[i].getPosition() == 1) || (particles[i].getPosition() == 2)
                    || (particles[i].getPosition() == 3)
                    || (particles[i].getPosition() == 4)
                    || (particles[i].getPosition() == 5)) {
                if (!((gates[particles[i].getPosition() - 1].isClosed()) 
                        && particles[i].getY() >= (gates[particles[i].getPosition() - 1].getY() + gates[particles[i].getPosition() - 1].getHeight())))
                    graphics.fillOval(particles[i].x, particles[i].y,
                            particles[i].getBallSize(), particles[i].getBallSize());
            } else {
                if (particles[i].getPosition() != 0) {
                    if (gates[particles[i].getPosition() - 6].getHeight() != 0)
                        graphics.fillOval(particles[i].x, particles[i].y,
                                particles[i].getBallSize(), particles[i].getBallSize());
                } else {
                    graphics.fillOval(particles[i].x, particles[i].y,
                            particles[i].getBallSize(), particles[i].getBallSize());
                }
            }
        }
    }

    private void checkRestrictedVisibility(List<ClientData> sortedClients) {
        if (!restrictedVisibility)
            return;
        ClientData thisClient = clientDataModel.getClientData();
        int thisClientIndex = sortedClients.indexOf(thisClient);
        int upstreamNeighborIndex = thisClientIndex - 1;
        int downstreamNeighborIndex = thisClientIndex + 1;
        for (int i = 0; i < gates.length; i++) {
            if (i <= upstreamNeighborIndex && upstreamNeighborIndex > 0) {
                ClientData upstreamNeighbor = sortedClients.get(upstreamNeighborIndex);
                gates[i].setHeight(upstreamNeighbor.getAvailableFlowCapacity());
                gates[i].setOpeningsHeight((maximumIrrigationCapacity * canalHeightMultiplier) - gates[i].getHeight());
            }
            else if (i >= downstreamNeighborIndex && downstreamNeighborIndex < sortedClients.size()) {
                ClientData downstreamNeighbor = sortedClients.get(downstreamNeighborIndex);
                gates[i].setHeight(downstreamNeighbor.getAvailableFlowCapacity());
                gates[i].setOpeningsHeight((maximumIrrigationCapacity * canalHeightMultiplier) - gates[i].getHeight());
            }
        }
    }

    /**
     * FIXME: needs major refactoring
     */
    private void updateGates(List<ClientData> sortedClientDataList) {
        ClientData thisClientData= clientDataModel.getClientData();
        for (int i = 1; i < 6; i++) {
            if (gates[i - 1].isOpen()) {
                if (restrictedVisibility && ! thisClientData.isImmediateNeighbor(sortedClientDataList.get(i-1))) {
                    continue;
                }
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
                gates[i].setOpeningsHeight((maximumIrrigationCapacity * canalHeightMultiplier) - gates[i].getHeight());
                gates[i].setOpeningsY(gates[i].getOpeningsHeight() - 50);

                // opening gate logic
                if (gates[i].isOpen()) {
                    if (restrictedVisibility && ! thisClientData.isImmediateNeighbor(sortedClientDataList.get(i))) {
                        continue;
                    }
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
                    if (restrictedVisibility && ! thisClientData.isImmediateNeighbor(sortedClientDataList.get(0))) {
                        continue;
                    }
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
        particles = new Ball[BALLCOUNT];
        for (int i = 0; i < BALLCOUNT; i++) {
            particles[i] = new Ball(generator);
        }
    }

    // this is the private class the Timer will look at every DELAY seconds
    private class Rebound implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            List<ClientData> sortedClients = clientDataModel.getClientDataSortedByPriority();
            ClientData thisClientData = clientDataModel.getClientData();
            for (int i = 0; i < BALLCOUNT; i++) {
                // updateGUI();
                particles[i].x += particles[i].moveX;
                particles[i].y += particles[i].moveY;
                process(i, sortedClients, thisClientData);
            }
            repaint();
        }
    }

    /*
     * This will process the balls according to their position
     */
    private void process(int i, List<ClientData> sortedClients, ClientData thisClientData) {
        
        switch (particles[i].getPosition()) {

            case 0:
                if ((particles[i].x >= (reservoirWidth - gateBuffer) && particles[i].x <= reservoirWidth)
                        && (particles[i].y >= reservoirHeight - (int) (maximumIrrigationCapacity * canalHeightMultiplier) && particles[i].y <= reservoirHeight)) 
                {
                    particles[i].setPosition(1);
                    setBounds(i);
                }
                // still in server
                else {
                    setBounds(i);
                    if (particles[i].x <= particles[i].xLowerBound
                            || particles[i].x >= particles[i].xUpperBound
                                    - particles[i].getBallSize()) {
                        particles[i].moveX = particles[i].moveX * -1;
                    }
                    if (particles[i].y <= particles[i].yLowerBound
                            || particles[i].y >= particles[i].yUpperBound
                                    - particles[i].getBallSize())
                        particles[i].moveY = particles[i].moveY * -1;
                }
                break;

            case 1:
                if (gates[0].isOpen()
                        && (particles[i].x >= gates[0].getOpeningsX() && particles[i].x <= (gates[0].getOpeningsX() + gateBuffer))
                        && (particles[i].y >= reservoirHeight - gateBuffer && particles[i].y <= reservoirHeight)) 
                {
                    if (restrictedVisibility && sortedClients.get(0).isImmediateNeighbor(thisClientData) || ! restrictedVisibility) 
                    {
                        // if we are in the restricted visibility condition AND gate 0 is an immediate neighbor of this client OR we are not in a restricted visibility condition at all, put this ball in the
                        // gate (or at least I *think* this is what Sanket's god-awful code is doing).  
                        particles[i].setPosition(7);
                        // directly pass in the information
                        setBounds(i);
                        break;
                    }
                }
                // otherwise the gate is closed or we don't know enough about the gate, pass it on down
                setBounds(i);
                if (particles[i].getX() > particles[i].xUpperBound) {
                    particles[i].setPosition(2);
                }
                if (particles[i].getY() >= particles[i].yUpperBound
                        - particles[i].getBallSize()
                        || particles[i].getY() <= particles[i].yLowerBound) {
                    particles[i].moveY = particles[i].moveY * -1;
                }
                break;

            case 2:
                if (gates[1].isOpen()
                        && (particles[i].x >= gates[1].getOpeningsX() && particles[i].x <= (gates[1]
                                .getOpeningsX() + gateBuffer))
                        && (particles[i].y >= reservoirHeight - gateBuffer && particles[i].y <= reservoirHeight)) 
                {
                    if (!restrictedVisibility || (restrictedVisibility && sortedClients.get(1).isImmediateNeighbor(thisClientData))) {
                        particles[i].setPosition(8);
                        // directly pass in the information
                        setBounds(i);
                        break;
                    }
                } 
                setBounds(i);
                if (particles[i].getX() > particles[i].xUpperBound) {
                    particles[i].setPosition(3);
                }
                if (particles[i].getY() >= particles[i].yUpperBound
                        - particles[i].getBallSize()
                        || particles[i].getY() <= particles[i].yLowerBound) {
                    particles[i].moveY = particles[i].moveY * -1;
                }
                break;
            case 3:
                if (gates[2].isOpen()
                        && (particles[i].x >= gates[2].getOpeningsX() && particles[i].x <= (gates[2]
                                .getOpeningsX() + gateBuffer))
                        && (particles[i].y >= reservoirHeight - gateBuffer && particles[i].y <= reservoirHeight))
                {
                    if (!restrictedVisibility || (restrictedVisibility && sortedClients.get(2).isImmediateNeighbor(thisClientData))) {
                        particles[i].setPosition(9);
                        // directly pass in the information
                        setBounds(i);
                        break;
                    }
                }
                setBounds(i);
                if (particles[i].getX() > particles[i].xUpperBound) {
                    particles[i].setPosition(4);
                }
                if (particles[i].getY() >= particles[i].yUpperBound
                        - particles[i].getBallSize()
                        || particles[i].getY() <= particles[i].yLowerBound) {
                    particles[i].moveY = particles[i].moveY * -1;
                }
                break;
            case 4:
                if (gates[3].isOpen() 
                        && (particles[i].x >= gates[3].getOpeningsX() && particles[i].x <= (gates[3]
                                .getOpeningsX() + gateBuffer))
                        && (particles[i].y >= reservoirHeight - gateBuffer && particles[i].y <= reservoirHeight)) 
                {
                    if (!restrictedVisibility || (restrictedVisibility && !sortedClients.get(3).isImmediateNeighbor(thisClientData))) {
                        particles[i].setPosition(10);
                        // directly pass in the information
                        setBounds(i);
                        break;
                    }
                } 
                setBounds(i);
                if (particles[i].getX() > particles[i].xUpperBound) {
                    particles[i].setPosition(5);
                }
                if (particles[i].getY() >= particles[i].yUpperBound
                        - particles[i].getBallSize()
                        || particles[i].getY() <= particles[i].yLowerBound) {
                    particles[i].moveY = particles[i].moveY * -1;
                }
                break;
            case 5:
                if (gates[4].isOpen()
                        && (particles[i].x >= gates[4].getOpeningsX() && particles[i].x <= (gates[4]
                                .getOpeningsX() + gateBuffer))
                        && (particles[i].y >= reservoirHeight - gateBuffer && particles[i].y <= reservoirHeight)) 
                {
                    if (!restrictedVisibility || (restrictedVisibility && !sortedClients.get(4).isImmediateNeighbor(thisClientData))) {
                        particles[i].setPosition(11);
                        // directly pass in the information
                        setBounds(i);
                        break;
                    }
                } 
                setBounds(i);
                if (particles[i].getX() > particles[i].xUpperBound) {
                    particles[i].setPosition(6);
                }
                if (particles[i].getY() >= particles[i].yUpperBound
                        - particles[i].getBallSize()
                        || particles[i].getY() <= particles[i].yLowerBound) {
                    particles[i].moveY = particles[i].moveY * -1;
                }
                break;
            case 6:
                setBounds(i);
                if (particles[i].getX() > particles[i].xUpperBound) {
                    returnParticleToReservoir(i);
                }

                if (particles[i].getY() >= particles[i].yUpperBound
                        - particles[i].getBallSize()
                        || particles[i].getY() <= particles[i].yLowerBound) {
                    particles[i].moveY = particles[i].moveY * -1;
                }
                break;
        }

        // set balls back to the server when they complete their flow in the
        // gates
        if ((particles[i].getPosition() == 11) || (particles[i].getPosition() == 7)
                || (particles[i].getPosition() == 8)
                || (particles[i].getPosition() == 9)
                || (particles[i].getPosition() == 10)) {
            if (particles[i].getY() >= 150) {
                returnParticleToReservoir(i);
            } else {
                setBounds(i);
            }
        }
    }

    private void returnParticleToReservoir(int i) {
        generator.setSeed(i * (i + 1));
        particles[i].setX(generator.nextInt(reservoirWidth));
        particles[i].setY(generator.nextInt(reservoirHeight));
        particles[i].setPosition(0);
        if (particles[i].moveX == 0)
            particles[i].moveX = generator.nextInt(15);
        setBounds(i);
    }

    private void setBounds(int index) {
        // ball is in the server
        if (particles[index].getPosition() == 0) {
            particles[index].xUpperBound = reservoirWidth;
            particles[index].xLowerBound = 0;
            particles[index].yUpperBound = reservoirHeight;
            particles[index].yLowerBound = 0;
            // balls[ballIndex].moveX = generator.nextInt(6);
            // balls[ballIndex].moveY = generator.nextInt(6);
            // balls[ballIndex].moveX = 3;
            // balls[ballIndex].moveY = 3;
        } else {
            if ((particles[index].getPosition() == 1)
                    || (particles[index].getPosition() == 2)
                    || (particles[index].getPosition() == 3)
                    || (particles[index].getPosition() == 4)
                    || (particles[index].getPosition() == 5)
                    || (particles[index].getPosition() == 6)) {

                particles[index].xUpperBound = gates[particles[index].getPosition() - 1].getX()
                        + gates[particles[index].getPosition() - 1].getWidth();
                particles[index].xLowerBound = gates[particles[index].getPosition() - 1].getX();
                particles[index].yUpperBound = gates[particles[index].getPosition() - 1].getY()
                /*
                 * +gate[balls[ballIndex].
                 * getPosition() -
                 * 1].getHeight()
                 */;
                particles[index].yLowerBound = gates[particles[index].getPosition() - 1].getY();
            }
            // the ball is in one of the openings
            else {
                particles[index].xUpperBound = gates[particles[index]
                        .getPosition() - 7].getOpeningsX()
                        + gates[particles[index].getPosition() - 7].getGateWidth();
                particles[index].xLowerBound = gates[particles[index].getPosition() - 7].getOpeningsX();
                particles[index].yUpperBound = gates[particles[index].getPosition() - 7].getOpeningsY()
                        + gates[particles[index].getPosition() - 7].getOpeningsHeight();
                particles[index].yLowerBound = gates[particles[index].getPosition() - 7].getOpeningsY();
                // X change in motion
                particles[index].moveX = 0;
                // Y change in motion
                particles[index].moveY = 3;
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
        for (ClientData clientData : clientDataModel.getClientDataMap().values()) {
            closeGate(clientData.getPriority());
        }
    }
}
