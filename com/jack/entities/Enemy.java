package com.jack.entities;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.TimeUnit;

import com.jack.main.Game_Main;
import com.jack.network.Connection;
import com.jack.util.Util;

public class Enemy extends Player {

	public Enemy(String username, Point cPos) {
		super(username, Color.CYAN, null);
		
		this.cPos = cPos;
		this.pPos = cPos;
		
		health = 5;
		totalHealth = 5;
		ammo.total = 5000;
		ammo.amount = 5000;
		
		activate(this);
	}
	
	private void activate(Enemy owner){
		Thread t = new Thread(new Runnable(){
			public void run() {
				while(health > 0) {
					
					try {
						TimeUnit.MILLISECONDS.sleep(500);
					} catch (InterruptedException e) {}
					
					if(inLineOfSight()) {
						Projectile proj = new Projectile(owner, cPos, Game_Main.player.cPos);
						shoot(proj);
					}
					
				}
			}
		});
		t.start();
	}
	
	private boolean inLineOfSight() {
		
		int lifeTime = 700;
		Point bulletPos = new Point(cPos);
		
		double angle = getAngle(Game_Main.player.cPos, bulletPos);
		
		while(lifeTime > 0) {
			
			double xDirection = Math.sin(angle) * 2;
			double yDirection = Math.cos(angle) * -2;
			
			double newX = bulletPos.x + xDirection;
			double newY = bulletPos.y + yDirection;
			
			Point p = new Point();
			p.setLocation(newX, newY);
			
			if(Util.inBoundaries(Game_Main.map.boundaries, new Rectangle(p.x, p.y, 2, 2))){
				return false;
			}
			
			if(Util.inArea(Game_Main.player.bounds(), cPos)){
				return true;
			}
			
			bulletPos.setLocation(newX, newY);
			lifeTime-=1;
		}
		return true;
		
	}
	
	private double getAngle(Point destination, Point origin) {
		return Math.atan2(destination.getY() - origin.getY(), 
				destination.getX() - origin.getX()) + Math.PI/2;
	}

}
