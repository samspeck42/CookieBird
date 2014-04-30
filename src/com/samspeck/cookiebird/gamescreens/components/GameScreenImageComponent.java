package com.samspeck.cookiebird.gamescreens.components;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.samspeck.cookiebird.CookieBirdGame;

public class GameScreenImageComponent extends GameScreenComponent {
	
	private int width;
	private int height;
	private BufferedImage image;
	private Point renderPosition;
	
	
	public GameScreenImageComponent(CookieBirdGame game) {
		super(game);
		width = 0;
		height = 0;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(Math.round(renderPosition.x), Math.round(renderPosition.y), width, height);
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
		width = this.image.getWidth();
		height = this.image.getHeight();
		updateRenderPosition();
	}
	
	public void loadImage(String imagePath) {
		try {
			image = ImageIO.read(CookieBirdGame.class.getResourceAsStream(imagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		width = image.getWidth();
		height = image.getHeight();
		updateRenderPosition();
	}

	@Override
	public void render(Graphics2D g, ImageObserver obs) {
		updateRenderPosition();
		g.drawImage(image, Math.round(renderPosition.x), Math.round(renderPosition.y), obs);
	}

	private void updateRenderPosition() {
		renderPosition = new Point(Math.round(position.x), Math.round(position.y));
		switch (origin) {
		case "center":
			renderPosition.x -= width / 2;
			renderPosition.y -= height / 2;
			break;
		default:
			break;
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
