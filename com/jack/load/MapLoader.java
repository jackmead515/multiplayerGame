package com.jack.load;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import com.jack.entities.AmmoPack;
import com.jack.entities.Boundary;
import com.jack.entities.BreakableBoundary;
import com.jack.entities.Enemy;
import com.jack.entities.HealthPack;
import com.jack.entities.Item;
import com.jack.main.Game_Main;
import com.jack.util.Util;

public class MapLoader {

	public static Map load(Path path) {
		ArrayList<Boundary> boundaries = new ArrayList<Boundary>();
		ArrayList<Item> items = new ArrayList<Item>();
		
		File file = new File(path.toString());
		try {
			Scanner scan = new Scanner(file);
			
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				if(!line.startsWith("#")) {
					readLine(line, boundaries, items);	
				}
				
			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String name = path.getFileName().toString().substring(0, path.getFileName().toString().indexOf(".map"));
		return new Map(boundaries, items, name);
	}
	
	private static void readLine(String line, ArrayList<Boundary> boundaries, ArrayList<Item> items){
		
	    int health = 0;
	    Rectangle bounds = null;
	    String name = null;
	    
			switch(getCode(line)){
			
				case "bu" :
					// Boundary
					bounds = getBounds(line);
				    
				    boundaries.add(new Boundary(bounds.x, bounds.y, bounds.width, bounds.height));
				    break;
				case "bb" :
					// Breakable Boundary
					bounds = getBounds(line);
					health = getSpecialAmount(line);
				    
				    boundaries.add(new BreakableBoundary(bounds.x, bounds.y, bounds.width, bounds.height, health));
				    break;
				case "ia" :
					// Ammo Item
					bounds = getHalfBounds(line);
					health = getSpecialAmount(line);
					
					items.add(new AmmoPack(bounds.x, bounds.y, health)); 
					break;
				case "ih" :
					// Health Item
					bounds = getHalfBounds(line);
					health = getSpecialAmount(line);
					
					items.add(new HealthPack(bounds.x, bounds.y, health));
					break;
				case "eb" :
					// Basic Enemy
					
					bounds = getHalfBounds(line);
					name = getSpecialStringAmount(line);
					Enemy enemy1 = new Enemy(name, new Point(bounds.x, bounds.y));
					Game_Main.players.add(enemy1);
					
					break;
					
					// is -- Shield Item
					// ii -- Invincible Item
					// iam -- Mega Ammo Item
					// ib -- Bomb Item

				default :
					break;
			
			}
		
	}
	
	private static Rectangle getBounds(String line) {
		
		int x = Integer.parseInt(line.substring(line.indexOf("(")+1, Util.ordinalIndexOf(line, ",", 1)));
		int y = Integer.parseInt(line.substring(Util.ordinalIndexOf(line, ",", 1)+1, Util.ordinalIndexOf(line, ",", 2)));
	    int width = Integer.parseInt(line.substring(Util.ordinalIndexOf(line, ",", 2)+1, Util.ordinalIndexOf(line, ",", 3)));
	    int height = Integer.parseInt(line.substring(Util.ordinalIndexOf(line, ",", 3)+1, line.indexOf(")")));
		
		return new Rectangle(x, y, width, height);
	}
	
	private static Rectangle getHalfBounds(String line) {
		
		int x = Integer.parseInt(line.substring(line.indexOf("(")+1, Util.ordinalIndexOf(line, ",", 1)));
		int y = Integer.parseInt(line.substring(Util.ordinalIndexOf(line, ",", 1)+1, line.indexOf(")")));
		
		return new Rectangle(x, y, 1, 1);
	}
	
	private static int getSpecialAmount(String line) {
		return Integer.parseInt(line.substring(line.indexOf("[")+1, line.indexOf("]")));
	}
	
	private static String getSpecialStringAmount(String line) {
		return line.substring(line.indexOf("[")+1, line.indexOf("]"));
	}
	
	private static String getCode(String line){
		return line.substring(0, line.indexOf("("));
	}


}
