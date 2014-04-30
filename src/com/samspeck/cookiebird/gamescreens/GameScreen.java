package com.samspeck.cookiebird.gamescreens;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.samspeck.cookiebird.CookieBirdGame;
import com.samspeck.cookiebird.engine.Vector2D;
import com.samspeck.cookiebird.gamescreens.components.GameScreenButton;
import com.samspeck.cookiebird.gamescreens.components.GameScreenComponent;
import com.samspeck.cookiebird.gamescreens.components.GameScreenImageComponent;
import com.samspeck.cookiebird.gamescreens.components.GameScreenTextBoxComponent;
import com.samspeck.cookiebird.gamescreens.components.GameScreenTextLineComponent;

public class GameScreen {

	protected CookieBirdGame game;
	private ArrayList<GameScreenComponent> components;
	private ArrayList<GameScreenTextLineComponent> textLineComponents;
	private ArrayList<GameScreenButton> buttons;
	private ArrayList<GameScreenImageComponent> imageComponents;
	private ArrayList<GameScreenTextBoxComponent> textBoxComponents;

	public GameScreen(CookieBirdGame game) {
		this.game = game;
		components = new ArrayList<GameScreenComponent>();
		textLineComponents = new ArrayList<GameScreenTextLineComponent>();
		buttons = new ArrayList<GameScreenButton>();
		imageComponents = new ArrayList<GameScreenImageComponent>();
		textBoxComponents = new ArrayList<GameScreenTextBoxComponent>();
	}

	public void load(String path) {
		JsonReader reader;
		try {
			// TODO: REMOVE THIS
			//String currentDir = System.getProperty("user.dir");
			//System.out.println("Current working dir:" + currentDir);
			// TODO: REMOVE THIS END
			//reader = new JsonReader(new BufferedReader(new FileReader(path)));
			reader = new JsonReader(new BufferedReader(new InputStreamReader(CookieBirdGame.class.getResourceAsStream(path))));
			JsonElement element = (new JsonParser()).parse(reader);
			if (element.isJsonObject()) {
				JsonObject screenObject = element.getAsJsonObject();
				JsonArray layout = screenObject.getAsJsonArray("layout");

				for (JsonElement e : layout) {
					JsonObject componentObject = e.getAsJsonObject();
					String type = componentObject.get("type").getAsString();
					if (type.equals("TextLineComponent")) {
						GameScreenTextLineComponent component = createTextLineComponentFromJsonObject(componentObject);
						addComponent(component);
					} else if (type.equals("Button")) {
						GameScreenButton button = createButtonFromJsonObject(componentObject);
						addComponent(button);
					} else if (type.equals("ImageComponent")) {
						GameScreenImageComponent imageComponent = createImageComponentFromJsonObject(componentObject);
						addComponent(imageComponent);
					} else if (type.equals("TextBoxComponent")) {
						GameScreenTextBoxComponent textBoxComponent = createTextBoxComponentFromJsonObject(componentObject);
						addComponent(textBoxComponent);
					}
				}
			}

			reader.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private GameScreenTextLineComponent createTextLineComponentFromJsonObject(JsonObject obj) {
		GameScreenTextLineComponent textComponent = new GameScreenTextLineComponent(game);
		setComponentAttributesFromJsonObject(obj, textComponent);
		textComponent.setText(obj.get("text").getAsString());
		String fontName = obj.get("font").getAsString();
		int size = obj.get("size").getAsInt();
		String[] styleNames = obj.get("style").getAsString().split(",");
		int style = 0;
		for (String styleName : styleNames) {
			switch (styleName.trim()) {
			case "Bold":
				style |= Font.BOLD;
				break;
			case "Italic":
				style |= Font.ITALIC;
				break;
			case "Plain":
				style |= Font.PLAIN;
				break;
			default:
				break;
			}
		}
		Font font = null;
		if (fontName.endsWith(".ttf")) {
			try {
				font = Font.createFont(
						Font.TRUETYPE_FONT,
						CookieBirdGame.class.getResourceAsStream("/fonts/"
								+ fontName));
			} catch (FontFormatException | IOException e) {
				e.printStackTrace();
			}
			font = font.deriveFont(style, (float) size);
		} else {
			font = new Font(fontName, style, size);
		}
		textComponent.setFont(font);
		JsonArray colorArray = obj.get("color").getAsJsonArray();
		textComponent
				.setColor(new Color(colorArray.get(0).getAsFloat(), colorArray
						.get(1).getAsFloat(), colorArray.get(2).getAsFloat()));
		return textComponent;
	}
	
	private GameScreenTextBoxComponent createTextBoxComponentFromJsonObject(JsonObject obj) {
		GameScreenTextBoxComponent textBoxComponent = new GameScreenTextBoxComponent(game);
		setComponentAttributesFromJsonObject(obj, textBoxComponent);
		textBoxComponent.setText(obj.get("text").getAsString());
		String fontName = obj.get("font").getAsString();
		int size = obj.get("size").getAsInt();
		String[] styleNames = obj.get("style").getAsString().split(",");
		int style = 0;
		for (String styleName : styleNames) {
			switch (styleName.trim()) {
			case "Bold":
				style |= Font.BOLD;
				break;
			case "Italic":
				style |= Font.ITALIC;
				break;
			case "Plain":
				style |= Font.PLAIN;
				break;
			default:
				break;
			}
		}
		Font font = null;
		if (fontName.endsWith(".ttf")) {
			try {
				font = Font.createFont(
						Font.TRUETYPE_FONT,
						CookieBirdGame.class.getResourceAsStream("/fonts/"
								+ fontName));
			} catch (FontFormatException | IOException e) {
				e.printStackTrace();
			}
			font = font.deriveFont(style, (float) size);
		} else {
			font = new Font(fontName, style, size);
		}
		textBoxComponent.setFont(font);
		JsonArray colorArray = obj.get("color").getAsJsonArray();
		textBoxComponent
				.setColor(new Color(colorArray.get(0).getAsFloat(), colorArray
						.get(1).getAsFloat(), colorArray.get(2).getAsFloat()));
		textBoxComponent.setSize(obj.get("width").getAsInt(), obj.get("height").getAsInt());
		return textBoxComponent;
	}

	private GameScreenButton createButtonFromJsonObject(JsonObject obj) {
		GameScreenButton button = new GameScreenButton(game);
		setComponentAttributesFromJsonObject(obj, button);
		String enabledImagePath = obj.get("enabled_image_path").getAsString();
		String pressedImagePath = obj.get("pressed_image_path").getAsString();
		String disabledImagePath = null;
		if (obj.get("disabled_image_path") != null)
			disabledImagePath = obj.get("disabled_image_path").getAsString();
		button.setOnClickMethod(obj.get("on_click").getAsString());
		button.loadImages(enabledImagePath, pressedImagePath, disabledImagePath);
		return button;
	}

	private GameScreenImageComponent createImageComponentFromJsonObject(
			JsonObject obj) {
		GameScreenImageComponent imageComponent = new GameScreenImageComponent(
				game);
		setComponentAttributesFromJsonObject(obj, imageComponent);
		String imagePath = obj.get("image_path").getAsString();
		imageComponent.loadImage(imagePath);
		return imageComponent;
	}

	private void setComponentAttributesFromJsonObject(JsonObject obj,
			GameScreenComponent component) {
		JsonArray positionArray = obj.get("position").getAsJsonArray();
		component.setPosition(new Vector2D(positionArray.get(0).getAsFloat(),
				positionArray.get(1).getAsFloat()));
		component.setId(obj.get("id").getAsString());
		component.setOrigin(obj.get("origin").getAsString());
		if (obj.get("visible") != null)
			component.setIsVisible(obj.get("visible").getAsBoolean());
	}

	public void addComponent(GameScreenComponent component) {
		if (component instanceof GameScreenTextLineComponent)
			textLineComponents.add((GameScreenTextLineComponent) component);
		else if (component instanceof GameScreenButton)
			buttons.add((GameScreenButton) component);
		else if (component instanceof GameScreenImageComponent)
			imageComponents.add((GameScreenImageComponent) component);
		else if (component instanceof GameScreenTextBoxComponent)
			textBoxComponents.add((GameScreenTextBoxComponent) component);
		components.add(component);
	}

	public void removeComponent(GameScreenComponent component) {
		if (component instanceof GameScreenTextLineComponent)
			textLineComponents.remove((GameScreenTextLineComponent) component);
		else if (component instanceof GameScreenButton)
			buttons.remove((GameScreenButton) component);
		else if (component instanceof GameScreenImageComponent)
			imageComponents.remove((GameScreenImageComponent) component);
		else if (component instanceof GameScreenTextBoxComponent)
			textBoxComponents.remove((GameScreenTextBoxComponent) component);
		components.remove(component);
	}

	public GameScreenComponent getComponentById(String id) {
		for (GameScreenComponent component : components) {
			if (component.getId().equals(id))
				return component;
		}
		return null;
	}

	public void mousePressed(MouseEvent e) {
		for (GameScreenButton button : buttons) {
			if (!button.isPressed()
					&& button.isEnabled()
					&& button.getBounds().contains(
							new Point(e.getX(), e.getY()))) {
				button.setPressed(true);
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		for (GameScreenButton button : buttons) {
			if (button.isPressed()) {
				button.setPressed(false);
				if (!button.getOnClickMethod().isEmpty()) {
					try {
						Method method = this.getClass().getMethod(
								button.getOnClickMethod());
						method.invoke(this);
					} catch (NoSuchMethodException | SecurityException
							| IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public void update() {
		for (GameScreenComponent com : components) {
			com.update();
		}
	}

	public void render(Graphics2D g) {
		for (GameScreenComponent com : components) {
			if (com.getIsVisible())
				com.render(g, game);
		}
	}
}
