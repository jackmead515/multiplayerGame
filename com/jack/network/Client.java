package com.jack.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.jack.entities.Player;

public class Client {
	
	public Player player;
	
	public Socket dataSocket;
	public Socket chatSocket;
	public Socket echoSocket;
	
	public Scanner dataIn;
	public PrintWriter dataOut;
	
	public Scanner echoIn;
	public PrintWriter echoOut;
	
	public Scanner chatIn;
	public PrintWriter chatOut;
	
	public Client(Socket dataSocket, Socket chatSocket, Socket echoSocket, PrintWriter dataOut,
			Scanner dataIn, PrintWriter chatOut, Scanner chatIn, PrintWriter echoOut,
			Scanner echoIn, Player player) {
		
		this.player = player;
		
		this.dataSocket = dataSocket;
		this.chatSocket = chatSocket;
		this.echoSocket = echoSocket;
		this.dataIn = dataIn;
		this.dataOut = dataOut;
		this.echoIn = echoIn;
		this.echoOut = echoOut;
		this.chatIn = chatIn;
		this.chatOut = chatOut;
		
	}
	
	

}
