package com.jack.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.jack.network.Client;
import com.jack.network.Dispatcher;
import com.jack.util.RefStrings;

public class Game_Server {
	
	public static ServerSocket dataServerSocket;
	public static ServerSocket chatServerSocket;
	public static ServerSocket echoServerSocket;

	public volatile static ArrayList<Client> players;
	
	public static void main(String[] args) {
		
		try {
			Process p = Runtime.getRuntime().exec("sudo ifconfig eth0 " + RefStrings.SERVER_IP);
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			System.out.println("Failed to set IP Address!");
			System.exit(0);
		}
		
		players = new ArrayList<Client>();
		
		startServers(10);
		
		Thread t1 = new Thread(new Dispatcher());
		t1.start();
		
	}
	
	private static boolean startServers(int counter) {
		try {
			dataServerSocket = new ServerSocket(RefStrings.DATA_PORT);
			System.out.println("----- Positions Server started on port " + RefStrings.DATA_PORT + " -----");
			echoServerSocket = new ServerSocket(RefStrings.ECHO_PORT);
			System.out.println("----- Echo Positions Server started on port " + RefStrings.ECHO_PORT + " -----");
			chatServerSocket = new ServerSocket(RefStrings.CHAT_PORT);
			System.out.println("----- Echo Chat Server started on port " + RefStrings.CHAT_PORT + " -----");
			return true;
		} catch (IOException e) {
			while(counter != 0) {
				System.out.println("----- Could not start servers on ports. -----");
				counter -= 1;
				
				try{TimeUnit.SECONDS.sleep(1);}catch(InterruptedException e1){}
				
				startServers(counter);
			}
				System.out.println("----- Could not start server. Please restart. -----");
				System.exit(1);
		}
		
		return false;
	}
	
	public static void registerPlayer(Client client) {
		
		players.add(client);
		
	}
	
	public static void deregisterPlayer(Client client) {
		
		players.remove(client);
		
	}

}
