package com.jack.entities;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.jack.main.Game_Main;


public class HealthPack extends Item {

	public int health;
	
	public HealthPack(int x, int y, int health){
		super(x, y);
		
		this.health = health;
		this.color = Color.RED;
		
		URL file = Game_Main.class.getClassLoader().getResource("com/jack/images/health_pack.bmp");
		try {
			this.image = ImageIO.read(new File(file.toString().substring(6)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Rectangle bounds(){
		return new Rectangle(cPos.x, cPos.y, 7, 7);
	}

}
