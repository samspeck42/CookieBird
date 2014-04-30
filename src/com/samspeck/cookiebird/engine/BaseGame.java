package com.samspeck.cookiebird.engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public abstract class BaseGame extends JPanel implements Runnable {
	
	private static final long serialVersionUID = 1608170509571061249L;
	private static final long FRAMERATE = 60;
	
	private Thread thread = null;
	private boolean gameInitialized = false;
	
	public BaseGame(){
		setBackground(Color.LIGHT_GRAY);
		setDoubleBuffered(true);
	}
	
	public void start(){
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if (thread != null && thread.isAlive()) { //&& gameInitialized) {
			Graphics2D g2d = (Graphics2D) g;
			render(g2d);
		}
		g.dispose();
	}

	@Override
	public void run() {
		//init();
		gameInitialized = true;
		long msPerFrame = 1000 / FRAMERATE;
		long lastTime = System.currentTimeMillis();
		long timeDiff = 0, sleepTime = 0;
		while(true){
			update();
			repaint();
			
			timeDiff = System.currentTimeMillis() - lastTime;
			sleepTime = msPerFrame - timeDiff;
			
			try {
				if(sleepTime > 0)
					Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			lastTime = System.currentTimeMillis();
		}
	}
	
	//public abstract void init();
	public abstract void update();
	public abstract void render(Graphics2D g);
}
