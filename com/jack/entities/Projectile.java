package com.jack.entities;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import com.jack.main.Game_Main;
import com.jack.util.Util;

public class Projectile {

	public Player owner;
	public int speed;
	public int damage;
	public int sizeX;
	public int sizeY;
	public Color color;
	public int lifeTime;
	public Point cPos;
	public Point pPos;
	public Point nPos;
	public double angle;
	
	public Point origin;
	public Point destination;
	
	public Projectile(Player player, Point origin, Point destination) {
		this.owner = player;
		this.speed = 10;
		this.origin = origin;
		this.destination = destination;
		this.cPos = origin;
		this.angle = getAngle();
		this.sizeX = 2;
		this.sizeY = 2;
		color = Color.YELLOW;	
		lifeTime = 700;
		damage = 1;
	}
	
	private double getAngle() {
		return Math.atan2(destination.getY() - origin.getY(), 
				destination.getX() - origin.getX()) + Math.PI/2;
	}
	
	public Rectangle bounds(){
		return new Rectangle(cPos.x, cPos.y, sizeX, sizeY);
	}
	
	public boolean move() {
		
		if(lifeTime > 0) {
			
			double xDirection = Math.sin(angle) * speed;
			double yDirection = Math.cos(angle) * -speed;
			
			double newX = cPos.x + xDirection;
			double newY = cPos.y + yDirection;
			
			Point p = new Point();
			p.setLocation(newX, newY);
			nPos = p;
			
			if(Util.inBoundaries(Game_Main.map.boundaries, new Rectangle(p.x, p.y, sizeX, sizeY))){
				return false;
			}
			
			pPos = cPos;
			cPos.setLocation(newX, newY);
			lifeTime-=1;
			return true;
		}
		return false;
	}

}
