package edu.asu.commons.irrigation.client;

import java.awt.Color;

/**
 * FIXME: Refactor this class.
 * 
 * @author Sanket
 */
public class Gate {
    
    public final static Color DEFAULT_COLOR = Color.black;

	private boolean open = false;
	
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
	
	public Gate(double totalContributedBandwidth, int priority){
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
		this.openingsy = 100 - openingsY;
		this.y1 = 100 - openingsY;
		this.y2 = 100 - openingsY;
		this.defaulty1 = 100 - openingsY;
	}

	public void setOpeningsWidth(int gateWidth) {
		openingsWidth = gateWidth;
	}

	public void setOpeningsHeight(double gateHeight) {
		openingsHeight = (int)(gateHeight)+this.gateHeight;
	}

	public void setY(int y) {
	  this.y = y;
	}

	public void setWidth(int width) {
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
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int getY() {

		return y;
	}

	public int getX() {
		return x;
	}
	
	public boolean isOpen(){
		return open;
	}
	
	public boolean isClosed() {
	    return ! open;
	}
	
	public void open() {
	    setOpen(true);
	}
	public void close() {
	    setOpen(false);
	}

	private void setOpen(boolean open) {
		this.open = open;
		if (! open) {
		    setx1(getdefaultx1());
		    sety1(getdefaulty1());
		}
	}

	public int getOpeningsHeight() {
		return openingsHeight;
	}

	public int getOpeningsX() {
		return openingsx;
	}

	public int getOpeningsY() {
		return openingsy;
	}

	public int getOpeningsWidth() {
		return openingsWidth;
	}

	public int gety2() {
		return y2;
	}

	public int gety1() {
		return y1;
	}

	public int getx1() {
		return x1;
	}

	public int getx2() {
		return x2;
	}

	public void setx1(int x1) {
		this.x1 = x1;
	}

	public int getGateWidth() {
		return gateWidth;
	}

	public void sety1(int y1) {
		this.y1 = y1;
	}

	public int getdefaultx1() {
		return defaultx1;
	}

	public int getdefaulty1() {
		return defaulty1;
	}

	public void sety2(int y2) {
		this.y2 = y2;
	}
	
}
