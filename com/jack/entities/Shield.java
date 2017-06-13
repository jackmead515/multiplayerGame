package com.jack.entities;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

public class Shield {

	public boolean on;
	public Color color;
	public int delta;
	public int duration;
	public int maxDuration;

	public Shield(int duration, Color color) {
		this.color = color;
		this.duration = duration;
		this.maxDuration = duration;
	}
	
	public void addDuration(int time) {
		if(duration + time >= maxDuration){
			duration = maxDuration;
		} else {
			duration += time;
		}
	}
	
	public void setDuration(int time) {
		this.duration = time;
	}
	
	public int getDuration() {
		return duration;
	}

	public void focus() {
		Thread t = new Thread(new Runnable(){

			public void run() {
				
				try {
					on = true;
					
					int start = (int) System.currentTimeMillis();
					
					while(System.currentTimeMillis() + start <= start + getDuration()){
						for(int i = 15; i < 50; i++) {
							delta = i;
							TimeUnit.MILLISECONDS.sleep(100);
						}
						
						for(int i = 50; i > 15; i--) {
							delta = i;
							TimeUnit.MILLISECONDS.sleep(100);
						}
					}	
				} catch (InterruptedException e){}
				
				on = false;
				delta = 15;
				
			}
			
		});
		t.start();
	}
	
	public void decharge() {
		Thread t = new Thread(new Runnable(){

			public void run() {
				
				try {
				
					while(on){
					
						for(int i = maxDuration; i >= duration; i--){
							duration = i;
							TimeUnit.MILLISECONDS.sleep(100);
						}
						
					}
					
				} catch (InterruptedException e){}
				
			}
			
		});
		t.start();
	}
	
	public void recharge() {
		Thread t = new Thread(new Runnable(){

			public void run() {
				
				try {
				
					while(!on){
					
						for(int i = duration; i <= maxDuration; i++){
							duration = i;
							TimeUnit.MILLISECONDS.sleep(100);
						}
						
					}
					
				} catch (InterruptedException e){}
				
			}
			
		});
		t.start();
	}

}
