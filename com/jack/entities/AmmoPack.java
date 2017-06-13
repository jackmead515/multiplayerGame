package com.jack.entities;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.jack.main.Game_Main;

public class AmmoPack extends Item {
	
	public int amount;
	
	public AmmoPack(int x, int y, int amount){
		super(x, y);
		
		this.amount = amount;
		this.color = Color.GRAY;
		
		URL file = Game_Main.class.getClassLoader().getResource("com/jack/images/ammo_pack.bmp");
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
