package com.samspeck.cookiebird.engine;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.samspeck.cookiebird.CookieBirdGame;

public class Sprite {

	private BufferedImage sprite;
	private BufferedImage[] frameImages;

	private int frameWidth;
	private int frameHeight;
	private int numFrames;
	private int delay;
	private int currentFrame = 0;
	private int currentDelay = 0;
	private float scale = 1;
	private double rotation = 0;
	private boolean isAnimating = false;

	public Sprite(int numFrames, int delay) {
		sprite = null;
		this.frameWidth = 0;
		this.frameHeight = 0;
		this.numFrames = numFrames;
		this.delay = delay;
		frameImages = new BufferedImage[numFrames];
	}

	public void loadImage(String imagePath, ImageObserver obs) {
		try {
			sprite = ImageIO.read(CookieBirdGame.class.getResourceAsStream(imagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frameWidth = sprite.getWidth() / numFrames;
		frameHeight = sprite.getHeight();
		BufferedImage frameImage = null;
		Graphics2D g = null;
		for (int i = 0; i < numFrames; i++) {
			frameImage = new BufferedImage(frameWidth, frameHeight,
					BufferedImage.TYPE_INT_ARGB);
			g = frameImage.createGraphics();
			g.drawImage(sprite, 0, 0, frameWidth, frameHeight, i * frameWidth,
					0, i * frameWidth + frameWidth, frameHeight, obs);
			frameImages[i] = frameImage;
			g.dispose();
		}
	}

	public void update() {
		if (isAnimating) {
			if (currentDelay >= delay) {
				currentFrame++;
				currentDelay = 0;
				if (currentFrame >= numFrames)
					currentFrame = 0;
			} else
				currentDelay++;
		}
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public float getScale() {
		return scale;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation % (2.0 * Math.PI);
	}

	public double getRotation() {
		return rotation;
	}

	public void resetAnimation() {
		currentFrame = 0;
		currentDelay = 0;
	}

	public int getWidth() {
		return frameWidth;
	}

	public int getHeight() {
		return frameHeight;
	}

	public boolean isAnimating() {
		return isAnimating;
	}
	
	public void startAnimating() {
		isAnimating = true;
	}
	
	public void stopAnimating() {
		resetAnimation();
		isAnimating = false;
	}

	public void render(Graphics2D g, ImageObserver obs, Vector2D pos) {
		render(g, obs, new Point(Math.round(pos.x), Math.round(pos.y)));
	}

	public void render(Graphics2D g, ImageObserver obs, Point pos) {
		int newWidth = (int) (frameWidth * scale);
		int newHeight = (int) (frameHeight * scale);
		int newPosX = pos.x + ((frameWidth - newWidth) / 2);
		int newPosY = pos.y + ((frameHeight - newHeight) / 2);
		AffineTransform transform = new AffineTransform();
		transform.translate(newPosX, newPosY);
		transform.scale(scale, scale);
		transform.rotate(rotation, frameWidth / 2, frameHeight / 2);
		g.drawImage(frameImages[currentFrame], transform, obs);
	}

}
