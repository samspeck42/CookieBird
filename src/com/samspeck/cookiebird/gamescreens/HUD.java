package com.samspeck.cookiebird.gamescreens;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;

import com.samspeck.cookiebird.CookieBirdGame;
import com.samspeck.cookiebird.engine.Vector2D;
import com.samspeck.cookiebird.gamescreens.components.GameScreenImageComponent;
import com.samspeck.cookiebird.gamescreens.components.GameScreenTextLineComponent;

public class HUD extends GameScreen {
	
	private static final String NOTIFICATION_FONT_PATH = "/fonts/PixelSplitter-Bold.ttf";
	private static final float NOTIFICATION_SPEED = 1.0f;
	private static final float NOTIFICATION_FADE = 0.008f;
	
	private ArrayList<GameScreenTextLineComponent> notificationTextLineComponents;
	private Font notificationFont;

	public HUD(CookieBirdGame game) {
		super(game);
		notificationTextLineComponents = new ArrayList<GameScreenTextLineComponent>();
		try {
			notificationFont = Font.createFont(Font.TRUETYPE_FONT, CookieBirdGame.class.getResourceAsStream(NOTIFICATION_FONT_PATH));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addNotificationTextLineComponent(String text, int size, Color color, Vector2D position) {
		GameScreenTextLineComponent component = new GameScreenTextLineComponent(game);
		component.setText(text);
		component.setFont(notificationFont.deriveFont(Font.PLAIN, (float) size));
		component.setColor(color);
		component.setPosition(position);
		component.setOrigin("center");
		notificationTextLineComponents.add(component);
		addComponent(component);
	}
	
	@Override
	public void update() {
		super.update();
		for (int i = 0; i < notificationTextLineComponents.size(); i++) {
			GameScreenTextLineComponent component = notificationTextLineComponents.get(i);
			component.setPosition(new Vector2D(component.getPosition().x, component.getPosition().y - NOTIFICATION_SPEED));
			component.setAlpha(component.getAlpha() - NOTIFICATION_FADE);
			if (component.getAlpha() <= 0.0f) {
				notificationTextLineComponents.remove(component);
				removeComponent(component);
				i--;
			}
		}
	}

	@Override
	public void render(Graphics2D g) {
		GameScreenTextLineComponent numCookiesText = (GameScreenTextLineComponent) getComponentById("num_cookies_text");
		numCookiesText.updateDisplayText();
		numCookiesText.updateRenderPosition();
		GameScreenImageComponent cookieIconImage = (GameScreenImageComponent) getComponentById("cookie_icon_image");
		cookieIconImage.setPosition(new Vector2D(numCookiesText.getRenderPosition().x - cookieIconImage.getWidth() - 4, 
				numCookiesText.getRenderPosition().y - cookieIconImage.getHeight()));
		super.render(g);
	}

}
