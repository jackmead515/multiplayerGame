package com.jack.main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jack.entities.AmmoPack;
import com.jack.entities.Boundary;
import com.jack.entities.BreakableBoundary;
import com.jack.entities.Enemy;
import com.jack.entities.HealthPack;
import com.jack.entities.Item;
import com.jack.entities.Player;
import com.jack.entities.Projectile;
import com.jack.load.Map;
import com.jack.load.MapLoader;
import com.jack.network.Connection;
import com.jack.util.RefStrings;
import com.jack.util.Util;

public class Game_Main {
	
	public static Frame window;
	
	public static Player player;
	
	public volatile static ArrayList<Player> players;
	
	public static Map map;
	
	public static int fps;

	public static void main(String[] args) {
		
        String email = JOptionPane.showInputDialog(null, "Email: ");
        String password = JOptionPane.showInputDialog(null, "Password: ");
        
        if(!validateUser(email, password)) {
        	JOptionPane.showMessageDialog(null, 
        	"Invalidate username and password. Check your "
        	+ "network settings or provide a proper username and password.");
        	
        	System.exit(1);
        }
		
		player = new Player(email, Color.BLUE, new Connection());
		
		players = new ArrayList<Player>();
		
		players.add(player);
		
		loadMap();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new Frame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		try {
			TimeUnit.SECONDS.sleep(2);
	    } catch (InterruptedException e) {}
		gameLoop();
		
	}
	
	private static void loadMap() {
		File mapFile = null;
		
		JFileChooser mapChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".map files", "map");
		mapChooser.setFileFilter(filter);
		if(mapChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
			mapFile = mapChooser.getSelectedFile();
		}
		 
		if(mapFile == null) {
			map = new Map(new ArrayList<Boundary>(), new ArrayList<Item>(), "null");
		} else {
			map = MapLoader.load(mapFile.toPath());
		}
	}
	
	private static boolean validateUser(String email, String password) {
		HttpURLConnection connection = null;

		try {
			
			String postString = "email=" + email + "&password=" + password;
			
			URL url = new URL("http://" + RefStrings.SERVER_IP + ":3000/gamelogin");
			connection = (HttpURLConnection) url.openConnection();
			
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			connection.setDoOutput(true);
			
			connection.getOutputStream().write(postString.getBytes());
			connection.getOutputStream().flush();
			connection.getOutputStream().close();
			
			int code = 400;
			code = connection.getResponseCode();
			
			if(code == 200) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
	private static void tick() {
		
		for(int i = 0; i < players.size(); i++) {
			
			Player eplayer = players.get(i);

			if(eplayer.getHealth() <= 0){
				players.remove(i);
				continue;
			}
			
			eplayer.move();
			
			if(eplayer.cPos.x != eplayer.pPos.x || 
			   eplayer.cPos.y != eplayer.pPos.y && 
			   eplayer.username.equals(player.username)){
				
				eplayer.connection.echoPosition();
			}
			
			ArrayList<Projectile> projs = eplayer.liveAmmo;
			
			for(int c = 0; c < projs.size(); c++){
				Projectile proj = projs.get(c);
				
				if(proj.move()) {
					for(Player hitPlayer : players) {
						if(!proj.owner.username.equals(hitPlayer.username)) {
							if(Util.intersects(proj.bounds(), hitPlayer.bounds()) && !hitPlayer.shield.on) {
								hitPlayer.takeDamage(proj.damage);
								eplayer.liveAmmo.remove(c);
								break;
							}
						}
					}	
				} else {
					for(int p = 0; p < map.boundaries.size(); p++) {
						Boundary boundary = map.boundaries.get(p);
						if(boundary instanceof BreakableBoundary) {
							BreakableBoundary bound = (BreakableBoundary) boundary;
							if(Util.intersects(new Rectangle(proj.nPos.x, proj.nPos.y, proj.sizeX, proj.sizeY), bound)) {
								bound.takeDamage(proj.damage);
								if(bound.getHealth() <= 0) {
									map.boundaries.remove(p);
								}
							}
						}
					}
				eplayer.liveAmmo.remove(c);
				}
			}
			
			for(int f = 0 ; f < map.items.size(); f++){
				Item item = map.items.get(f);
				
				if(Util.intersects(eplayer.bounds(), item.bounds())){
					if(item instanceof HealthPack){
						eplayer.addHealth(((HealthPack) item).health);
						map.items.remove(f);
						continue;
					}
					if(item instanceof AmmoPack){
						eplayer.addAmmo(((AmmoPack) item).amount);
						map.items.remove(f);
						continue;
					}
				}
			}
		}
	}
	
	private static void paint() {
		try {
			//window.gameView.paintImmediately(0, 0, 700, 700);
			window.gameView.repaint();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void gameLoop() {
		
		long lastLoopTime = System.nanoTime();
		int targetFPS = 60;
		long optimalTime = 1000000000 / targetFPS;   
		int lastFpsTime = 0;

		   while (true) {
			     
			      long now = System.nanoTime();
			      long updateLength = now - lastLoopTime;
			      lastLoopTime = now;
			      
			      double timesPerFrame = updateLength / ((double)optimalTime);
	
			      lastFpsTime += updateLength;
			      fps++;
			      
			      if (lastFpsTime >= 1000000000) {
			    	 GameView.fps = fps;
			         lastFpsTime = 0;
			         fps = 0;
			      }
			      
			      tick();
			      
			      paint();

			      try {
			    	  Thread.sleep( Math.abs((lastLoopTime-System.nanoTime() + optimalTime)/1000000));
			      } catch(Exception e) {
			    	  e.printStackTrace();
			      }
			}
	}
	
	

}
