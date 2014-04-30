package com.samspeck.cookiebird.gamescreens.components;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ImageObserver;

import com.samspeck.cookiebird.CookieBirdGame;
import com.samspeck.cookiebird.engine.Vector2D;

public abstract class GameScreenComponent {

	protected Vector2D position;
	protected String id;
	protected String origin;
	protected float alpha;
	protected boolean isVisible;
	
	public Vector2D getPosition() {
		return position;
	}
	
	public Point getPositionAsPoint() {
		return new Point(Math.round(position.x), Math.round(position.y));
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public boolean getIsVisible() {
		return isVisible;
	}
	
	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	
	protected CookieBirdGame game;
	
	public GameScreenComponent(CookieBirdGame game) {
		this.position = Vector2D.zero();
		this.game = game;
		id = "";
		origin = null;
		alpha = 1.0f;
		isVisible = true;
	}
	
	public abstract void update();
	public abstract void render(Graphics2D g, ImageObserver obs);
}
