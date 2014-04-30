package com.samspeck.cookiebird;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import com.samspeck.cookiebird.CookieBirdGame.GameState;
import com.samspeck.cookiebird.engine.Sprite;
import com.samspeck.cookiebird.engine.Vector2D;

public class Cookie extends Sprite {

	private final static String IMAGE_PATH = "/sprites/winged-cookie.png";
	private final static float AMPLITUDE = 5.0f;
	private final static float FREQUENCY = 0.02f;
	public final static double DOWNWARD_ROTATIONAL_ACCELERATION = Math.toRadians(0.2);

	public Vector2D position;
	public Vector2D velocity;
	public Vector2D acceleration;
	public double rotationalVelocity;
	public double rotationalAcceleration;

	private CookieBirdGame game;
	private float yOffset;
	private long oscillationTimer;

	public Cookie(CookieBirdGame game) {
		super(4, 4);
		this.game = game;
		position = Vector2D.zero();
		velocity = Vector2D.zero();
		acceleration = Vector2D.zero();
		rotationalVelocity = 0;
		rotationalAcceleration = 0;
		yOffset = 0;
		oscillationTimer = 0;
	}
	
	public void load() {
		loadImage(IMAGE_PATH, game);
	}

	@Override
	public void update() {
		super.update();
		if (game.gameState == GameState.Playing) {
			velocity.x += acceleration.x;
			velocity.y += acceleration.y;
			position.x += velocity.x;
			position.y += velocity.y;

			rotationalVelocity += rotationalAcceleration;
			double rot = getRotation() + rotationalVelocity;
			if (rot < -Math.toRadians(20)) {
				rot = -Math.toRadians(20);
				rotationalVelocity = 0;
			} else if (rot > Math.PI / 2.0) {
				rot = Math.PI / 2.0;
			}
			setRotation(rot);

			if (game.lastClickTimer >= 20) {
				rotationalAcceleration = DOWNWARD_ROTATIONAL_ACCELERATION;
			}

			if ((getRotation() <= 0) && !isAnimating())
				startAnimating();
			else if ((getRotation() > 0) && isAnimating())
				stopAnimating();
		}
		else if (game.gameState == GameState.Starting || game.gameState == GameState.GameOver || game.gameState == GameState.UpgradesScreen) {
			yOffset = (float) (AMPLITUDE * Math.sin(2 * Math.PI * FREQUENCY * oscillationTimer));
			oscillationTimer++;
		}
	}

	public void reset() {
		position.y = CookieBirdGame.SCREEN_HEIGHT / 2 - getHeight();
		velocity = Vector2D.zero();
		setRotation(0);
		rotationalVelocity = 0;
		rotationalAcceleration = 0;
		yOffset = 0;
		oscillationTimer = 0;
		startAnimating();
	}

	public Rectangle getHitBox() {
		return new Rectangle(Math.round(position.x), Math.round(position.y),
				getWidth(), getHeight());
	}

	public void render(Graphics2D g, ImageObserver obs) {
		if (game.gameState == GameState.Playing)
			super.render(g, obs, position);
		else 
			super.render(g, obs, new Vector2D(position.x, position.y + yOffset));
	}

}
