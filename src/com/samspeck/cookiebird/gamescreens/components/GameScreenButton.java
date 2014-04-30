package com.samspeck.cookiebird.gamescreens.components;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.samspeck.cookiebird.CookieBirdGame;

public class GameScreenButton extends GameScreenComponent {
	
	private boolean isEnabled;
	private boolean isPressed;
	private int width;
	private int height;
	private String onClickMethod;
	
	private BufferedImage enabledImage;
	private BufferedImage disabledImage;
	private BufferedImage pressedImage;
	private Point renderPosition;
	
	public GameScreenButton(CookieBirdGame game) {
		super(game);
		renderPosition = new Point();
		isEnabled = true;
		isPressed = false;
		width = 0;
		height = 0;
		this.onClickMethod = null;
	}
	
	public void loadImages(String enabledImagePath, String pressedImagePath, String disabledImagePath) {
		try {
			enabledImage = ImageIO.read(CookieBirdGame.class.getResourceAsStream(enabledImagePath));
			pressedImage = ImageIO.read(CookieBirdGame.class.getResourceAsStream(pressedImagePath));
			if (disabledImagePath != null && !disabledImagePath.isEmpty())
				disabledImage = ImageIO.read(CookieBirdGame.class.getResourceAsStream(disabledImagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		width = enabledImage.getWidth();
		height = enabledImage.getHeight();
		updateRenderPosition();
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
	public void render(Graphics2D g, ImageObserver obs) {
		updateRenderPosition();
		
		if (isEnabled || disabledImage == null) {
			if (!isPressed)
				g.drawImage(enabledImage, Math.round(renderPosition.x), Math.round(renderPosition.y), obs);
			else
				g.drawImage(pressedImage, Math.round(renderPosition.x), Math.round(renderPosition.y), obs);
		}
		else {
			g.drawImage(disabledImage, Math.round(renderPosition.x), Math.round(renderPosition.y), obs);
		}
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(Math.round(renderPosition.x), Math.round(renderPosition.y), width, height);
	}

	public boolean isPressed() {
		return isPressed;
	}

	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getOnClickMethod() {
		return onClickMethod;
	}

	public void setOnClickMethod(String onClickMethod) {
		this.onClickMethod = onClickMethod;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
