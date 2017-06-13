package com.jack.entities;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Item {

	public Point cPos;
	public Color color;
	public BufferedImage image;
	
	public Item(int x, int y) {
		this.cPos = new Point(x, y);
		this.color = Color.BLACK;
		this.image = null;
	}
	
	public Rectangle bounds(){
		return new Rectangle(cPos.x, cPos.y, 5, 5);
	}

}
