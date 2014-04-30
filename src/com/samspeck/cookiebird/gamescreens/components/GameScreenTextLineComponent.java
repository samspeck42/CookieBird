package com.samspeck.cookiebird.gamescreens.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.samspeck.cookiebird.CookieBirdGame;

public class GameScreenTextLineComponent extends GameScreenComponent {
	
	private static final int PADDING = 6;
	
	private String text;
	private Font font;
	private Color color;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
		
		Graphics2D graphics = buffer.createGraphics();
		FontMetrics metrics = graphics.getFontMetrics(this.font);
		stringHeight = metrics.getHeight();
		ascent = metrics.getAscent();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	private Point renderPosition;
	private String displayText;
	private int stringHeight;
	private int stringWidth;
	private int ascent;
	private BufferedImage buffer;
	
	public GameScreenTextLineComponent(CookieBirdGame game) {
		super(game);
		this.text = null;
		this.font = null;
		this.color = null;
		this.renderPosition = new Point();
		this.displayText = null;
		stringHeight = 0;
		stringWidth = 0;
		ascent = 0;
		buffer = new BufferedImage(PADDING * 2, PADDING * 2, BufferedImage.TYPE_INT_ARGB);
	}
	
	@Override
	public void update() {
		// TODO
	}
	
	@Override
	public void render(Graphics2D g, ImageObserver obs) {
		updateDisplayText();
		updateRenderPosition();
		
		Graphics2D bufferGraphics = buffer.createGraphics();
		bufferGraphics.setBackground(new Color(0f, 0f, 0f, 0f));
		bufferGraphics.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
		bufferGraphics.setFont(font);
		Point bufferRenderPosition = new Point(PADDING, ascent + PADDING);
		
		if (font.getSize() >= 20) {
			bufferGraphics.setColor(Color.BLACK);
			int shadowOffset = 0;
			if (font.getSize() >= 50)
				shadowOffset = 2;
			else
				shadowOffset = 1;
			bufferGraphics.drawString(displayText, bufferRenderPosition.x - shadowOffset, bufferRenderPosition.y - shadowOffset);
			bufferGraphics.drawString(displayText, bufferRenderPosition.x + shadowOffset, bufferRenderPosition.y - shadowOffset);
			bufferGraphics.drawString(displayText, bufferRenderPosition.x - shadowOffset, bufferRenderPosition.y + shadowOffset);
			bufferGraphics.drawString(displayText, bufferRenderPosition.x + shadowOffset, bufferRenderPosition.y + shadowOffset);
			bufferGraphics.drawString(displayText, bufferRenderPosition.x + shadowOffset, bufferRenderPosition.y + (2 * shadowOffset));
		}
		else {
			bufferGraphics.setColor(new Color(0f, 0f, 0f, 0.8f));
			bufferGraphics.drawString(displayText, bufferRenderPosition.x, bufferRenderPosition.y + 1);
			bufferGraphics.drawString(displayText, bufferRenderPosition.x, bufferRenderPosition.y + 2);
		}
		bufferGraphics.setColor(color);
		bufferGraphics.drawString(displayText, bufferRenderPosition.x, bufferRenderPosition.y);
		
		bufferGraphics.dispose();
		
		RescaleOp op = new RescaleOp(new float[]{1f, 1f, 1f, alpha}, new float[4], null);
		g.drawImage(buffer, op, renderPosition.x - PADDING, renderPosition.y - (ascent + PADDING));
	}

	public void updateDisplayText() {
		displayText = "";
		String[] textArray = text.split("#");
		for (String str : textArray) {
			if (str.startsWith("$")) {
				try {
					String methodName = "get" + (char)(str.charAt(1) - 32) + str.substring(2);
					Method method = game.getClass().getMethod(methodName);
					int value = (int) method.invoke(game);
					str = "" + value;
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			displayText += str;
		}
	}

	public void updateRenderPosition() {
		Graphics2D graphics = buffer.createGraphics();
		FontMetrics fontMetrics = graphics.getFontMetrics(font);
		stringWidth = fontMetrics.stringWidth(displayText);
		renderPosition = new Point(Math.round(position.x), Math.round(position.y));
		switch (origin) {
		case "center":
			renderPosition.x -= stringWidth / 2;
			break;
		default:
			break;
		}
		
		if (buffer.getWidth() < (stringWidth + (PADDING * 2)) || buffer.getHeight() < (stringHeight + (PADDING * 2))) {
			buffer = new BufferedImage(stringWidth + (PADDING * 2), stringHeight + (PADDING * 2), BufferedImage.TYPE_INT_ARGB);
		}
	}

	public Point getRenderPosition() {
		return renderPosition;
	}

}
