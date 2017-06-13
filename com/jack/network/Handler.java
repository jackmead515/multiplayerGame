package com.jack.network;

import java.awt.Color;
import java.awt.Point;

import com.jack.entities.Player;
import com.jack.entities.Projectile;
import com.jack.main.Game_Main;
import com.jack.main.Game_Server;
import com.jack.util.RefStrings;

public class Handler {
	
	private Client client;

	public Handler(Client client) {
		this.client = client;
	}

	public void handle() {
		
		Thread t = new Thread(new Runnable(){
			public void run(){
				handleUpdate();
			}
		});
		t.start();
		
		Thread t2 = new Thread(new Runnable(){
			public void run(){
				handleChat();
			}
		});
		t2.start();
		
	}

	private void handleChat() {
		try {
			while(true) {
				
				String message = client.chatIn.nextLine();
				
				System.out.println(client.player.username + ": " + message);
				
				for(Client other : Game_Server.players) {
					if(!other.player.username.equals(client.player.username)) {
						
						other.chatOut.println(client.player.username);
						other.chatOut.println(message);
						other.chatOut.flush();
						
					}
				}
				
			}
		} catch (Exception e){

		} finally {
			disconnect();
		}
	}

	private void handleUpdate() {
		try {
			while(true) {
				
				String cmd = client.dataIn.nextLine();
				
				if(cmd.equals(RefStrings.CMD_DEREGISTERPLAYER)) {
					break;
				} else if (cmd.equals(RefStrings.CMD_UPDATEPOSITION)) {
					
					int x = client.dataIn.nextInt();
					int y = client.dataIn.nextInt();
					client.player.cPos.setLocation(x, y);
					
					System.out.println("X: " + x + " || Y: " + y);
					
					echoPosition(x, y);
					
				} else if (cmd.equals(RefStrings.CMD_UPDATEPROJ)) {
					
					String className = client.dataIn.nextLine();
					int originX = client.dataIn.nextInt();
					int originY = client.dataIn.nextInt();
					int destX = client.dataIn.nextInt();
					int destY = client.dataIn.nextInt();
					
					Projectile proj;
					
					switch(className) {
						case "Projectile":
							proj = new Projectile(client.player, new Point(originX, originY), new Point(destX, destY));
							echoProjectile(proj, className);
						default:
							break;
					}
					
					System.out.println(client.player.username + " shot a " + className);
					
				}
				
			}
		} catch (Exception e){

		} finally {
			disconnect();
		}
	}

	private void echoProjectile(Projectile proj, String className) {
		for(Client other : Game_Server.players){
			if(!other.player.username.equals(client.player.username)) {
				
				other.echoOut.println(RefStrings.CMD_UPDATEPROJ);
				other.echoOut.println(client.player.username);
				other.echoOut.println(className);
				other.echoOut.print(proj.origin.x + " ");
				other.echoOut.print(proj.origin.y + " ");
				other.echoOut.print(proj.destination.x + " ");
				other.echoOut.print(proj.destination.y + " \n");
				other.echoOut.flush();
				
			}
		}
	}

	private void echoPosition(int x, int y) {
		
		for(Client other : Game_Server.players) {
			if(!other.player.username.equals(client.player.username)){
				
				other.echoOut.println(RefStrings.CMD_UPDATEPOSITION);
				other.echoOut.println(client.player.username);
				other.echoOut.print(x + " ");
				other.echoOut.print(y + " \n");
				other.echoOut.flush();
				
			}
		}
		
	}

	public boolean register() {

		String username = client.dataIn.nextLine();
		String color = client.dataIn.nextLine();

		Player player = new Player(username, Color.getColor(color, Color.RED), null);
		client.player = player;
		
		Game_Server.registerPlayer(client);
	
		//so other people can see you...
		for(Client other : Game_Server.players) {
			if(!other.player.username.equals(client.player.username)){
				other.echoOut.println(RefStrings.CMD_REGISTERPLAYER);
				other.echoOut.println(client.player.username);
				other.echoOut.println(client.player.color);
				other.echoOut.flush();
			}
		}
		
		//so you can see other people...
		for(Client other : Game_Server.players) {
			if(!other.player.username.equals(client.player.username)){
				client.echoOut.println(RefStrings.CMD_REGISTERPLAYER);
				client.echoOut.println(other.player.username);
				client.echoOut.println(other.player.color);
				client.echoOut.flush();
			}
		}
	
		System.out.println(player.username + " has joined!");
		
		return true;
	}

	public void disconnect() {
		
		try {
			for(Client other : Game_Server.players) {
				if(!other.player.username.equals(client.player.username)){
					other.echoOut.println(RefStrings.CMD_DEREGISTERPLAYER);
					other.echoOut.println(client.player.username);
					other.echoOut.flush();
				}
			}
			
			client.chatIn.close();
			client.chatOut.close();
			client.dataIn.close();
			client.dataOut.close();
			client.echoIn.close();
			client.echoOut.close();
			
			client.chatSocket.close();
			client.echoSocket.close();
			
		} catch (Exception e){

		} finally {
			
			System.out.println(client.player.username + " has disconnected.");
			
			Game_Server.deregisterPlayer(client);
			
		}
	}

}
