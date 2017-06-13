package com.jack.main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.jack.entities.Player;
import com.jack.entities.Projectile;
import com.jack.util.Util;

import javax.swing.JTextArea;
import javax.swing.JButton;

public class Frame {

	public JFrame frame;
	public JTextField chatTextField;
	public JPanel gameView;
	public JTextPane chatTextPane;
	public JTextArea connectionTextArea;
	public JButton connectButton;
	
	public static int gameViewHeight;
	public static int gameViewWidth;

	public Frame() {
		initFrame();
		initEvents();
		initPlayerEvents();
	}

	private void initPlayerEvents() {
		
		gameView.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent key) {
				
				Game_Main.player.keyPressed(key);
				
			}
			
			@Override
			public void keyReleased(KeyEvent key) {
				
				Game_Main.player.keyReleased(key);
				
			}
		});
		
		gameView.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouse) {
				//pew pew...
				Projectile proj = new Projectile(Game_Main.player, Game_Main.player.cPos, mouse.getPoint());
				Game_Main.player.shoot(proj);
				Game_Main.player.connection.echoProjectile(proj);
			}
		});
		
	}

	private void initEvents() {
		
		connectButton.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseClicked(MouseEvent mouse) {
				
				Game_Main.player.connection.connect();
				
			}
			
		});
		
		gameView.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseMoved(MouseEvent mouse) {
				for (Player player : Game_Main.players) {
					if (Util.inArea(player.bounds(), mouse.getPoint())) {
						player.showMiniHUD = true;
					} else {
						player.showMiniHUD = false;
					}
				}
			}
		});
		
		gameView.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseClicked(MouseEvent mouse){
				
				gameView.requestFocus();
				
			}
			
		});
		
		gameView.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent arg0) {
				
				
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				
				Game_Main.player.stop();
				
			}
			
		});
		
		chatTextField.addKeyListener(new KeyAdapter(){
			
			@Override
			public void keyPressed(KeyEvent key){
				
				if(key.getKeyCode() == KeyEvent.VK_ENTER) {
					
					chatTextPane.setEditable(true);
					String message = chatTextField.getText();
					appendToPane(chatTextPane, Game_Main.player.username, Game_Main.player.color);
					appendToPane(chatTextPane, ": " + message + "\n", Color.BLACK);
					Game_Main.player.connection.echoChat(message);
					chatTextField.setText("");
					chatTextPane.setEditable(false);
					
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
		
		
	}

	private void initFrame() {
		frame = new JFrame();
		frame.setBounds(100, 100, 937, 760);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		gameView = new GameView();
		gameView.setBounds(10, 10, 700, 700);
		frame.getContentPane().add(gameView);
		
		gameViewHeight = gameView.getHeight();
		gameViewWidth = gameView.getWidth();
		
		chatTextField = new JTextField();
		chatTextField.setBounds(720, 690, 191, 20);
		frame.getContentPane().add(chatTextField);
		chatTextField.setColumns(10);
		
		chatTextPane = new JTextPane();
		JScrollPane chatScrollPane = new JScrollPane(chatTextPane);
		chatScrollPane.setBounds(720, 279, 191, 400);
		frame.getContentPane().add(chatScrollPane);
		chatTextPane.setEditable(false);
		
		connectionTextArea = new JTextArea();
		JScrollPane connectionScrollPane = new JScrollPane(connectionTextArea);
		connectionScrollPane.setBounds(720, 44, 191, 224);
		frame.getContentPane().add(connectionScrollPane);
		connectionTextArea.setEditable(false);
		
		connectButton = new JButton("Go Online!");
		connectButton.setBounds(720, 10, 191, 23);
		frame.getContentPane().add(connectButton);
	}
}
