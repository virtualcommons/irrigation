/**
 * 
 */
package edu.asu.commons.irrigation.client;

/**
 * @author Sanket
 *
 */
public class Gate {

	private boolean openGate = false;
	
	private int height;
	
	private int width;
	
	private int x;
	
	private int y;
	
	private int openingsx;
	
	private int openingsy;
	
	private int openingsWidth;
		
	private int gateWidth = 20;
	
	private int openingsHeight;
	
	private int gateHeight = 50;
	
	private int x1;
	
	private int x2;
	
	private int defaultx1;
	
	private int defaulty1;
	
	private int y1;
	
	private int y2;
	
	private int priority;
	
	private double totalContributedBandwidth;
	
	public Gate(double totalContributedBandwidth, int priority){
		this.totalContributedBandwidth = totalContributedBandwidth;
		this.priority = priority;
		setWidth(198);
		setHeight(totalContributedBandwidth*0.8);
		setOpeningsWidth(gateWidth);
		setOpeningsX(priority);
		setOpeningsHeight(0);
		setY(100 - height);
		y1 = y2 = defaulty1 = 100;
		
		//set the openings
				
		setOpeningsY(0);
		
	}

	public void setOpeningsY(int openingsY) {
		// TODO Auto-generated method stub
		this.openingsy = 100 - openingsY;
		this.y1 = 100 - openingsY;
		this.y2 = 100 - openingsY;
		this.defaulty1 = 100 - openingsY;
	}

	public void setOpeningsWidth(int gateWidth) {
		// TODO Auto-generated method stub
		openingsWidth = gateWidth;
	}

	public void setOpeningsHeight(double gateHeight) {
		// TODO Auto-generated method stub
		openingsHeight = (int)(gateHeight)+this.gateHeight;
	}

	public void setY(int y) {
		// TODO Auto-generated method stub
	  this.y = y;
	}

	public void setWidth(int width) {
		// TODO Auto-generated method stub
		this.width = width;

	}
	
	public void setHeight(double height){
		this.height = (int)height;
	}
	
	public void setOpeningsX(int priority){
	 //System.out.println("In swicth");
		switch(priority){
		
		case 0 : x = 100;
				 openingsx = x + width -openingsWidth;
				 break;
		
		case 1: x = 100 + priority*width;
				openingsx = x + width - openingsWidth;
				
				break;
				
		case 2: x = 100 + priority* width;
				openingsx = x + width  - openingsWidth;
				
				break;
				
		case 3: x = 100 + priority*width;
				openingsx = x + width - openingsWidth;
				break;
				
		case 4: x = 100 + priority*width;
				openingsx = x + width - openingsWidth;
				
				break;
				
		case 5: x = 100 + priority*width;
				openingsx = x + width - openingsWidth;
				break;
		}
		x1 = openingsx;
		x2 = openingsx + gateWidth;
		defaultx1 = openingsx;
		 
	}

	public int getHeight() {
		// TODO Auto-generated method stub
		return height;
	}

	public int getWidth() {
		// TODO Auto-generated method stub
		return width;
	}

	public int getY() {
		// TODO Auto-generated method stub
		return y;
	}

	public int getX() {
		// TODO Auto-generated method stub
		return x;
	}
	
	public boolean isOpenGate(){
		if(openGate == true)
			return true;
		else
			return false;
	}

	public void setGateOpen(boolean openGate) {
		// TODO Auto-generated method stub
		this.openGate = openGate;
	}

	public int getOpeningsHeight() {
		// TODO Auto-generated method stub
		return openingsHeight;
	}

	public int getOpeningsX() {
		// TODO Auto-generated method stub
		return openingsx;
	}

	public int getOpeningsY() {
		// TODO Auto-generated method stub
		return openingsy;
	}

	public int getOpeningsWidth() {
		// TODO Auto-generated method stub
		return openingsWidth;
	}

	public int gety2() {
		// TODO Auto-generated method stub
		return y2;
	}

	public int gety1() {
		// TODO Auto-generated method stub
		return y1;
	}

	public int getx1() {
		// TODO Auto-generated method stub
		return x1;
	}

	public int getx2() {
		// TODO Auto-generated method stub
		return x2;
	}

	public void setx1(int x1) {
		// TODO Auto-generated method stub
		this.x1 = x1;
	}

	public int getGateWidth() {
		// TODO Auto-generated method stub
		return gateWidth;
	}

	public void sety1(int y1) {
		// TODO Auto-generated method stub
		this.y1 = y1;
	}

	public int getdefaultx1() {
		// TODO Auto-generated method stub
		return defaultx1;
	}

	public int getdefaulty1() {
		// TODO Auto-generated method stub
		return defaulty1;
	}

	public void sety2(int y2) {
		// TODO Auto-generated method stub
		this.y2 = y2;
	}
	
	
}
