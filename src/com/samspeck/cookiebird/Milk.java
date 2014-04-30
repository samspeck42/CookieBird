package com.samspeck.cookiebird;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;

import com.samspeck.cookiebird.engine.Sprite;

public class Milk {
	
	private final static String IMAGE_PATH = "/ground.png";

	private Sprite milkSprite;
	private int positionX1;
	private int positionX2;
	private int positionY;
	private CookieBirdGame game;
	
	public Milk(CookieBirdGame game) {
		milkSprite = new Sprite(1, 0);
		positionX1 = 0;
		positionX2 = 0;
		positionY = CookieBirdGame.FLOOR_POSITION_Y - 20;
		this.game = game;
	}
	
	public void load() {
		milkSprite.loadImage(IMAGE_PATH, game);
		positionX2 = milkSprite.getWidth();
	}
	
	public void update() {
		positionX1 -= CookieBirdGame.SCROLL_SPEED;
		positionX2 -= CookieBirdGame.SCROLL_SPEED;
		if (positionX1 <= - milkSprite.getWidth())
			positionX1 = positionX2 + milkSprite.getWidth();
		if (positionX2 <= - milkSprite.getWidth())
			positionX2 = positionX1 + milkSprite.getWidth();
	}
	
	public void render(Graphics2D g, ImageObserver obs) {
		milkSprite.render(g, obs, new Point(positionX1, positionY));
		milkSprite.render(g, obs, new Point(positionX2, positionY));
	}
}
