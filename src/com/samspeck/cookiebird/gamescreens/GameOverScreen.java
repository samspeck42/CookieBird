package com.samspeck.cookiebird.gamescreens;

import com.samspeck.cookiebird.CookieBirdGame;

public class GameOverScreen extends GameScreen {

	public GameOverScreen(CookieBirdGame game) {
		super(game);
	}
	
	public void restartGame() {
		game.restartGame();
	}
	
	public void openUpgradesScreen() {
		game.openUpgradesScreen();
	}

}
