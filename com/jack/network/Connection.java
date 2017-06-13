package com.jack.network;

import java.awt.Color;
import java.awt.Point;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.jack.entities.Player;
import com.jack.entities.Projectile;
import com.jack.main.Frame;
import com.jack.main.Game_Main;
import com.jack.util.RefStrings;

public class Connection {

	public boolean connectionStatus;
	
	public Client client;

	public void connect() {
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					
					Socket dataSocket = new Socket(RefStrings.SERVER_IP, RefStrings.DATA_PORT);
					Socket chatSocket = new Socket(RefStrings.SERVER_IP, RefStrings.CHAT_PORT);
					Socket echoSocket = new Socket(RefStrings.SERVER_IP, RefStrings.ECHO_PORT);
					
					PrintWriter dataOut = new PrintWriter(dataSocket.getOutputStream());
					Scanner dataIn = new Scanner(dataSocket.getInputStream());
					
					PrintWriter chatOut = new PrintWriter(chatSocket.getOutputStream());
					Scanner chatIn = new Scanner(chatSocket.getInputStream());
					
					PrintWriter echoOut = new PrintWriter(echoSocket.getOutputStream());
					Scanner echoIn = new Scanner(echoSocket.getInputStream());
					
					client = new Client(dataSocket, chatSocket, echoSocket, dataOut, dataIn, chatOut, chatIn, echoOut, echoIn, Game_Main.player);
					
					connectionStatus = true;
					goOnline();
				}catch(Exception e){
					Game_Main.window.connectionTextArea.append("Failed to connect to server!\n");
					connectionStatus = false;
				}	
			}
		});
		t.start();
	}
	
	public void disconnect() {
		
		try {
			client.dataOut.println(RefStrings.CMD_DEREGISTERPLAYER);
			client.dataOut.flush();
			
			client.chatIn.close();
			client.chatOut.close();
			client.dataIn.close();
			client.dataOut.close();
			client.echoIn.close();
			client.echoOut.close();
			
			client.chatSocket.close();
			client.echoSocket.close();
			client.dataSocket.close();
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			
		}
		
	}

	private void goOnline() {
		if(connectionStatus){
			register();
			echoPosition();
			echoListen();
			chatListen();
			
			Game_Main.window.connectionTextArea.append("Connected to server!\n");
		}
	}
	
	private void chatListen() {
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				
				try {
					while(true) {
						
						String username = client.chatIn.nextLine();
						String message = client.chatIn.nextLine();
						
						for(Player other : Game_Main.players){
							if(other.username.equals(username)){
								Game_Main.window.chatTextPane.setEditable(true);
								appendToPane(Game_Main.window.chatTextPane, username, other.color);
								appendToPane(Game_Main.window.chatTextPane, ": " + message + "\n", Color.BLACK);
								Game_Main.window.chatTextPane.setEditable(false);
							}
						}
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
				}
				
			}
			
			private void appendToPane(JTextPane tp, String msg, Color c) {
		        StyleContext sc = StyleContext.getDefaultStyleContext();
		        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		        int len = tp.getDocument().getLength();
		        tp.setCaretPosition(len);
		        tp.setCharacterAttributes(aset, false);
		        tp.replaceSelection(msg);
			}
			
		});
		t.start();
	}

	private void register() {
		client.dataOut.println(client.player.username);
		client.dataOut.println(client.player.color);
		client.dataOut.flush();
	}

	public void echoChat(String message) {
		if(connectionStatus) {
			client.chatOut.println(message);
			client.chatOut.flush();
		}
	}

	public void echoPosition() {
		if(connectionStatus) {
			client.dataOut.println(RefStrings.CMD_UPDATEPOSITION);
			client.dataOut.print(client.player.cPos.x + " ");
			client.dataOut.print(client.player.cPos.y + " \n");
			client.dataOut.flush();
		}
	}
	
	public void echoProjectile(Projectile proj){
		if(connectionStatus){
			client.dataOut.println(RefStrings.CMD_UPDATEPROJ);
			client.dataOut.println(proj.getClass().getName().substring(proj.getClass().getName().lastIndexOf(".")+1));
			client.dataOut.print(proj.origin.x + " ");
			client.dataOut.print(proj.origin.y + " ");
			client.dataOut.print(proj.destination.x + " ");
			client.dataOut.print(proj.destination.y + " \n");
			client.dataOut.flush();
		}
	}
	
	private void echoListen() {
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				
				try{
					while(true) {
						
						String cmd = client.echoIn.nextLine();
						
						if (cmd.equals(RefStrings.CMD_DEREGISTERPLAYER)) {
							
							String username = client.echoIn.nextLine();
							
							for(int i = 0; i < Game_Main.players.size(); i++) {
								if(Game_Main.players.get(i).username.equals(username)) {
									Game_Main.players.remove(i);
									break;
								}
							}
							
						} else if (cmd.equals(RefStrings.CMD_REGISTERPLAYER)) {
							
							String username = client.echoIn.nextLine();
							String color = client.echoIn.nextLine();
							
							Game_Main.players.add(new Player(username, Color.getColor(color, Color.RED), null));
							
						} else if (cmd.equals(RefStrings.CMD_UPDATEPOSITION)) {
							
							String username = client.echoIn.nextLine();
							int x = client.echoIn.nextInt();
							int y = client.echoIn.nextInt();
							
							for(Player player : Game_Main.players) {
								if(player.username.equals(username)) {
									player.cPos.setLocation(x, y);
									break;
								}
							}
							
							
						} else if (cmd.equals(RefStrings.CMD_UPDATEPROJ)) {
							
							String username = client.echoIn.nextLine();
							String className = client.echoIn.nextLine();
							int originX = client.echoIn.nextInt();
							int originY = client.echoIn.nextInt();
							int destX = client.echoIn.nextInt();
							int destY = client.echoIn.nextInt();
							
							for(Player player : Game_Main.players) {
								if(player.username.equals(username)) {
									switch(className) {
										case "Projectile":
											player.liveAmmo.add(new Projectile(player, new Point(originX, originY), new Point(destX, destY)));
											break;
										default:
											break;
									}
									break;
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					
				}
				
			}
			
		});
		t.start();
	}

}
