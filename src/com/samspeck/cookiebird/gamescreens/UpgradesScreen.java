package com.samspeck.cookiebird.gamescreens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.samspeck.cookiebird.CookieBirdGame;
import com.samspeck.cookiebird.Upgrade;
import com.samspeck.cookiebird.Upgrade.UpgradeType;
import com.samspeck.cookiebird.engine.Vector2D;
import com.samspeck.cookiebird.gamescreens.components.GameScreenButton;
import com.samspeck.cookiebird.gamescreens.components.GameScreenImageComponent;
import com.samspeck.cookiebird.gamescreens.components.GameScreenTextBoxComponent;
import com.samspeck.cookiebird.gamescreens.components.GameScreenTextLineComponent;

public class UpgradesScreen extends GameScreen {
	private static final String UPGRADES_PATH = "/upgrades.txt";
	private static final String SELECTION_HIGHLIGHT_IMAGE_PATH = "/icons/selection_highlight.png";
	private static final String PURCHASED_HIGHLIGHT_IMAGE_PATH = "/icons/purchased_highlight.png";
	private static final int NUM_ICON_COLUMNS = 6;
	private static final int NUM_ICON_ROWS = 2;
	private static final int ICON_MARGIN = 4;
	private static final int ICON_SIZE = 64;
	
	private ArrayList<Upgrade> upgrades;
	private ArrayList<Upgrade> purchasedUpgrades;
	private Upgrade selectedUpgrade;
	private ArrayList<GameScreenImageComponent> upgradeIconImageComponents;
	private HashMap<GameScreenImageComponent, Upgrade> upgradeMap;
	private BufferedImage selectionHighlight;
	private BufferedImage purchasedHighlight;
	private Point selectionHighlightPosition;

	public UpgradesScreen(CookieBirdGame game) {
		super(game);
		upgrades = new ArrayList<Upgrade>();
		purchasedUpgrades = new ArrayList<Upgrade>();
		selectedUpgrade = null;
		upgradeIconImageComponents = new ArrayList<GameScreenImageComponent>();
		upgradeMap = new HashMap<GameScreenImageComponent, Upgrade>();
		selectionHighlight = null;
		selectionHighlightPosition = new Point();
	}
	
	@Override
	public void load(String path) {
		super.load(path);
		
		// load upgrades
		JsonReader reader;
		try {
			// TODO: REMOVE THIS
			//String currentDir = System.getProperty("user.dir");
			//System.out.println("Current working dir:" + currentDir);
			// TODO: REMOVE THIS END
			reader = new JsonReader(new BufferedReader(new InputStreamReader(CookieBirdGame.class.getResourceAsStream(UPGRADES_PATH))));
			JsonElement element = (new JsonParser()).parse(reader);
			if (element.isJsonObject()) {
				JsonObject jsonObj = element.getAsJsonObject();
				JsonArray upgradesArray = jsonObj.getAsJsonArray("upgrades");
				
				for (JsonElement e : upgradesArray) {
					JsonObject upgradeObject = e.getAsJsonObject();
					Upgrade upgrade = createUpgradeFromJsonObject(upgradeObject);
					upgrades.add(upgrade);
				}
			}
			
			reader.close();
			
			selectionHighlight = ImageIO.read(CookieBirdGame.class.getResource(SELECTION_HIGHLIGHT_IMAGE_PATH));
			purchasedHighlight = ImageIO.read(CookieBirdGame.class.getResource(PURCHASED_HIGHLIGHT_IMAGE_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		resetScreen();
	}
	
	private Upgrade createUpgradeFromJsonObject(JsonObject upgradeObject) {
		Upgrade upgrade = new Upgrade();
		upgrade.setDescription(upgradeObject.get("description").getAsString());
		upgrade.setValue(upgradeObject.get("value").getAsInt());
		upgrade.setCost(upgradeObject.get("cost").getAsInt());
		upgrade.setType(upgradeObject.get("type").getAsString());
		String iconPath = upgradeObject.get("icon_image_path").getAsString();
		upgrade.loadIcon(iconPath);
		return upgrade;
	}
	
	public void resetScreen() {
		upgrades.removeAll(purchasedUpgrades);
		purchasedUpgrades.clear();
		for (GameScreenImageComponent upgradeIcon : upgradeIconImageComponents) {
			removeComponent(upgradeIcon);
		}
		upgradeIconImageComponents.clear();
		upgradeMap.clear();
		
		GameScreenImageComponent panel = (GameScreenImageComponent) getComponentById("upgrades_screen_panel");
		int panelXPos = Math.round(panel.getPosition().x) - (panel.getWidth() / 2);
		int panelYPos = Math.round(panel.getPosition().y) - (panel.getHeight() / 2);
		
		// add icon image components to screen
		int posX = ICON_MARGIN + panelXPos;
		int posY = ICON_MARGIN + panelYPos;
		for (int r = 0; r < NUM_ICON_ROWS; r++) {
			for (int c = 0; c < NUM_ICON_COLUMNS; c++) {
				int upgradeIndex = (r * NUM_ICON_COLUMNS) + c;
				if (upgradeIndex >= upgrades.size())
					break;
				
				Upgrade upgrade = upgrades.get(upgradeIndex);
				GameScreenImageComponent iconImageComponent = new GameScreenImageComponent(game);
				iconImageComponent.setPosition(new Vector2D(posX, posY));
				iconImageComponent.setOrigin("top_left");
				iconImageComponent.setImage(upgrade.getIcon());
				upgradeIconImageComponents.add(iconImageComponent);
				upgradeMap.put(iconImageComponent, upgrade);
				addComponent(iconImageComponent);
				
				posX += ICON_SIZE + ICON_MARGIN;
			}
			posX = ICON_MARGIN + panelXPos;
			posY += ICON_SIZE + ICON_MARGIN;
		}
		
		resetSelection();
	}
	
	private void resetSelection() {
		selectedUpgrade = null;
		GameScreenTextBoxComponent descriptionTextBox = (GameScreenTextBoxComponent) getComponentById("description_text_box");
		descriptionTextBox.setText("Select upgrade for description");
		GameScreenImageComponent cookieIconImage = (GameScreenImageComponent) getComponentById("cookie_icon_image");
		cookieIconImage.setIsVisible(false);
		GameScreenTextLineComponent upgradeCostText = (GameScreenTextLineComponent) getComponentById("upgrade_cost_text");
		upgradeCostText.setText("");
		GameScreenButton buyUpgradeButton = (GameScreenButton) getComponentById("buy_upgrade_button");
		buyUpgradeButton.setEnabled(false);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		
		for (GameScreenImageComponent upgradeIcon : upgradeIconImageComponents) {
			if (upgradeIcon.getBounds().contains(e.getPoint())) {
				if (!purchasedUpgrades.contains(upgradeMap.get(upgradeIcon))) {
					selectionHighlightPosition = new Point(upgradeIcon.getPositionAsPoint());
					selectedUpgrade = upgradeMap.get(upgradeIcon);
					
					// set description text box to selected upgrade's description
					GameScreenTextBoxComponent descriptionTextBox = (GameScreenTextBoxComponent) getComponentById("description_text_box");
					descriptionTextBox.setText(selectedUpgrade.getDescription());
					
					// set cost to selected upgrade's cost
					GameScreenImageComponent cookieIconImage = (GameScreenImageComponent) getComponentById("cookie_icon_image");
					if (!cookieIconImage.getIsVisible())
						cookieIconImage.setIsVisible(true);
					GameScreenTextLineComponent upgradeCostText = (GameScreenTextLineComponent) getComponentById("upgrade_cost_text");
					upgradeCostText.setText("" + selectedUpgrade.getCost());
					GameScreenButton buyUpgradeButton = (GameScreenButton) getComponentById("buy_upgrade_button");
					if (selectedUpgrade.getCost() <= game.getTotalNumCookies()) {
						upgradeCostText.setColor(Color.GREEN);
						buyUpgradeButton.setEnabled(true);
					}
					else {
						upgradeCostText.setColor(Color.RED);
						buyUpgradeButton.setEnabled(false);
					}
				}
			}
		}
	}

	@Override
	public void render(Graphics2D g) {
		super.render(g);
		if (selectedUpgrade != null)
			g.drawImage(selectionHighlight, selectionHighlightPosition.x, selectionHighlightPosition.y, game);
		for (GameScreenImageComponent upgradeIcon : upgradeIconImageComponents) {
			if (purchasedUpgrades.contains(upgradeMap.get(upgradeIcon)))
				g.drawImage(purchasedHighlight, upgradeIcon.getPositionAsPoint().x, 
						upgradeIcon.getPositionAsPoint().y, game);
		}
	}

	public void restartGame() {
		game.restartGame();
	}
	
	public void purchaseUpgrade() {
		if (selectedUpgrade != null && selectedUpgrade.getCost() <= game.getTotalNumCookies()) {
			game.decreaseTotalNumCookies(selectedUpgrade.getCost());
			if (selectedUpgrade.getType() == UpgradeType.IncreaseClickValue)
				game.increaseClickValue(selectedUpgrade.getValue());
			else if (selectedUpgrade.getType() == UpgradeType.IncreaseClickMultiplierIncrease)
				game.increaseClickMultiplierIncrease(selectedUpgrade.getValue());
			else if (selectedUpgrade.getType() == UpgradeType.DecreaseNumObstaclesToIncreaseMultiplier)
				game.decreaseNumObstaclesToIncreaseMultiplier(selectedUpgrade.getValue());
			
			purchasedUpgrades.add(selectedUpgrade);
			resetSelection();
		}
	}

}
