package edu.asu.commons.irrigation.client;

import java.util.Random;

public class Ball{
	
	public boolean isInServer;
	
	public boolean isInFirstCanal;
	
	public boolean isInSecondCanal;
	
	private int size = 2;
	
	public int x;
	
	public int y;
	
  	public int moveX;
   	public int moveY;
   	
	public int xBOUNDSUPPER;
	public int xBOUNDSLOWER;
	public int yBOUNDSUPPER;
	public int yBOUNDSLOWER;
	
	//just one global variable speciying the ten potential positions of the balls
	private int position;
	
	public Ball(Random generator){
		
		this.x = generator.nextInt(100);
		this.y = generator.nextInt(100);
		this.xBOUNDSUPPER = 100;
		this.yBOUNDSUPPER = 100;
		this.xBOUNDSLOWER = 0;
		this.yBOUNDSLOWER = 0;
		this.moveX = generator.nextInt(15);
		this.moveY = generator.nextInt(10);
		/*this.moveX = 3;
		this.moveY = 3;
		*/
		setPosition(0);
	}
	
	public void setPosition(int position) {
		// TODO Auto-generated method stub
		
			//System.out.println("Setting the position :"+position);
		
		this.position = position;
		
	}

	public int getBallSize(){
		return size;
	}
	
	public boolean getIsInServer(){
		return isInServer;
	}
	
	public boolean getIsInFirstCanal(){
		return isInFirstCanal;
	}
	
	public boolean getIsInSecondCanal(){
		return isInSecondCanal;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}

	public int getPosition() {
		// TODO Auto-generated method stub
		return position;
	}

	public int  getX() {
		// TODO Auto-generated method stub
		return x;
	}

	/*
	 * will set the upper and the lower bounds for a ball depending on its position
	 */
	/*public void setBounds(int position) {
		// TODO Auto-generated method stub
		
	}*/

	public int getY() {
		// TODO Auto-generated method stub
		return y;
	}
	
}