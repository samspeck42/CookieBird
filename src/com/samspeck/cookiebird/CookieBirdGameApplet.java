package com.samspeck.cookiebird;

import java.applet.Applet;
import java.awt.Dimension;

public class CookieBirdGameApplet extends Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2548676164819811907L;

	@Override
	public void init() {
		CookieBirdGame game = new CookieBirdGame();
		game.setPreferredSize(new Dimension(CookieBirdGame.SCREEN_WIDTH, CookieBirdGame.SCREEN_HEIGHT));
		setSize(new Dimension(CookieBirdGame.SCREEN_WIDTH, CookieBirdGame.SCREEN_HEIGHT));
		add(game);
		setVisible(true);
		game.start();
	}
	
	

}
