package com.samspeck.cookiebird.gamescreens.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import com.samspeck.cookiebird.CookieBirdGame;

public class GameScreenTextBoxComponent extends GameScreenComponent {
	
	private String text;
	private Font font;
	private Color color;
	private int width;
	private int height;
	
	private BufferedImage buffer;
	private Point renderPosition;
	private String displayText;
	private LineBreakMeasurer lineMeasurer;
	private int displayTextStart;
	private int displayTextEnd;

	public GameScreenTextBoxComponent(CookieBirdGame game) {
		super(game);
		text = "";
		font = null;
		color = null;
		width = 0;
		height = 0;
		buffer = null;
		renderPosition = new Point();
		displayText = "";
		lineMeasurer = null;
		displayTextStart = 0;
		displayTextEnd = 0;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(Graphics2D g, ImageObserver obs) {
		Graphics2D bufferGraphics = buffer.createGraphics();
		bufferGraphics.setBackground(new Color(0f, 0f, 0f, 0f));
		bufferGraphics.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
		bufferGraphics.setFont(font);
		
		String prevDisplayText = displayText;
		updateDisplayText();
		if (!displayText.equals(prevDisplayText) || lineMeasurer == null) {
			updateLineMeasurer(bufferGraphics);
		}
		
		Point bufferRenderPosition = new Point(0, 0);
		lineMeasurer.setPosition(displayTextStart);
		while (lineMeasurer.getPosition() < displayTextEnd) {
			TextLayout layout = lineMeasurer.nextLayout(width);
			bufferRenderPosition.y += layout.getAscent();
			
			bufferGraphics.setColor(new Color(0f, 0f, 0f, 0.8f));
			layout.draw(bufferGraphics, bufferRenderPosition.x, bufferRenderPosition.y + 1);
			layout.draw(bufferGraphics, bufferRenderPosition.x, bufferRenderPosition.y + 2);
			bufferGraphics.setColor(color);
			layout.draw(bufferGraphics, bufferRenderPosition.x, bufferRenderPosition.y);
			
			bufferRenderPosition.y += layout.getDescent() + layout.getLeading() + 2;
		}
		
		bufferGraphics.dispose();
		
		updateRenderPosition();
		RescaleOp op = new RescaleOp(new float[]{1f, 1f, 1f, alpha}, new float[4], null);
		g.drawImage(buffer, op, renderPosition.x, renderPosition.y);
	}
	
	private void updateLineMeasurer(Graphics2D g) {
		AttributedString attributedText = new AttributedString(displayText);
		attributedText.addAttribute(TextAttribute.FONT, font);
		AttributedCharacterIterator attributedTextIterator = attributedText.getIterator();
		displayTextStart = attributedTextIterator.getBeginIndex();
		displayTextEnd = attributedTextIterator.getEndIndex();
		lineMeasurer = new LineBreakMeasurer(attributedTextIterator, g.getFontRenderContext());
	}

	private void updateDisplayText() {
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
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getWidth() {
		return width;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	public int getHeight() {
		return height;
	}

}
