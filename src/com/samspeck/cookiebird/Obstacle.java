package com.samspeck.cookiebird;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import com.samspeck.cookiebird.engine.Sprite;

public class Obstacle {
	
	private static final String OBSTACLE_IMAGE_PATH = "/sprites/carton.png";
	private static final int GAP_HEIGHT = 150;

	private Sprite obstacleSprite;
	private int gapPositionY;
	private CookieBirdGame game;
	
	public int positionX;

	public Obstacle(CookieBirdGame game) {
		obstacleSprite = new Sprite(1, 0);
		gapPositionY = CookieBirdGame.rand.nextInt(CookieBirdGame.FLOOR_POSITION_Y - GAP_HEIGHT - 100) + 50;
		positionX = 0;
		this.game = game;
	}
	
	public void load() {
		obstacleSprite.loadImage(OBSTACLE_IMAGE_PATH, game);
	}
	
	public int getWidth() {
		return obstacleSprite.getWidth();
	}
	
	public int getHeight() {
		return obstacleSprite.getHeight();
	}
	
	public void resetGap() {
		gapPositionY = CookieBirdGame.rand.nextInt(CookieBirdGame.FLOOR_POSITION_Y - GAP_HEIGHT - 100) + 50;
	}
	
	public void update() {
		positionX -= CookieBirdGame.SCROLL_SPEED;
	}
	
	public Rectangle[] getHitBoxes() {
		Rectangle[] hitBoxes = new Rectangle[4];
		hitBoxes[0] = new Rectangle(positionX, gapPositionY - obstacleSprite.getHeight(), getWidth(), getHeight() - 44);
		hitBoxes[1] = new Rectangle(positionX + 4, gapPositionY - 44, getWidth() - 8, 44);
		hitBoxes[2] = new Rectangle(positionX, gapPositionY + GAP_HEIGHT + 44, getWidth(), getHeight() - 44);
		hitBoxes[3] = new Rectangle(positionX + 4, gapPositionY + GAP_HEIGHT, getWidth() - 8, 44);
		return hitBoxes;
	}
	
	public void render(Graphics2D g, ImageObserver obs) {
		// draw top one
		obstacleSprite.render(g, obs, new Point(positionX, gapPositionY - obstacleSprite.getHeight()));
		
		// draw bottom one
		obstacleSprite.render(g, obs, new Point(positionX, gapPositionY + GAP_HEIGHT));
	}

}
