package edu.asu.commons.irrigation.client;

import java.util.Random;

public class Ball {
	
	private int size = 2;
	
	public int x;
	
	public int y;
	
  	public int moveX;
   	public int moveY;
   	
	public int xUpperBound;
	public int xLowerBound;
	public int yUpperBound;
	public int yLowerBound;
	
	//just one global variable speciying the ten potential positions of the balls
	private int position;
	
	public Ball(Random generator){
		
		this.x = generator.nextInt(100);
		this.y = generator.nextInt(100);
		this.xUpperBound = 100;
		this.yUpperBound = 100;
		this.xLowerBound = 0;
		this.yLowerBound = 0;
		this.moveX = generator.nextInt(15);
		this.moveY = generator.nextInt(10);
		/*this.moveX = 3;
		this.moveY = 3;
		*/
		setPosition(0);
	}
	
	public void setPosition(int position) {
		this.position = position;
	}

	public int getBallSize(){
		return size;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}

	public int getPosition() {
		return position;
	}

	public int  getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
}