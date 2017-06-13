package com.jack.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.jack.entities.Player;
import com.jack.main.Game_Server;

public class Dispatcher implements Runnable {
	
	private Socket dataSocket;
	private Socket chatSocket;
	private Socket echoSocket;
	
	private Scanner dataIn;
	private PrintWriter dataOut;
	
	private Scanner echoIn;
	private PrintWriter echoOut;
	
	private Scanner chatIn;
	private PrintWriter chatOut;
	
	private Player player;
	
	private Client client;

	@Override
	public void run() {

		dispatch();
		
	}

	private void dispatch() {
		while(true) {
			if(listen()) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						Handler handle = new Handler(client);
						if(handle.register()) {
							handle.handle();
						} else {
							handle.disconnect();
						}
					}
				});
				t.start();
			}
		}
	}

	private boolean listen() {
		try {
			
			dataSocket = Game_Server.dataServerSocket.accept();
			chatSocket = Game_Server.chatServerSocket.accept();
			echoSocket = Game_Server.echoServerSocket.accept();
			
			dataOut = new PrintWriter(dataSocket.getOutputStream());
			dataIn = new Scanner(dataSocket.getInputStream());
			
			chatOut = new PrintWriter(chatSocket.getOutputStream());
			chatIn = new Scanner(chatSocket.getInputStream());
			
			echoOut = new PrintWriter(echoSocket.getOutputStream());
			echoIn = new Scanner(echoSocket.getInputStream());
			
			client = new Client(dataSocket, chatSocket, echoSocket, dataOut, dataIn, chatOut, chatIn, echoOut, echoIn, player);
			
			return true;
		} catch (IOException e) {
			System.out.println("Client not accepted.");
			return false;
		}
	}

}
