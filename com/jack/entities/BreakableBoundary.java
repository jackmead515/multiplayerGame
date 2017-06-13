package com.jack.entities;

import java.awt.Color;

public class BreakableBoundary extends Boundary {

	public int health;

	public BreakableBoundary(int x, int y, int width, int height, int health) {
		super(x, y, width, height);
		
		this.health = health;
		this.color = new Color(102, 102, 102);
	}
	
	public void takeDamage(int damage){
		if(health - damage < 0){
			health = 0;
		}else{
			health -= damage;
		}
	}
	
	public void setHealth(int health){
		this.health = health;
	}
	
	public int getHealth(){
		return health;
	}

}
