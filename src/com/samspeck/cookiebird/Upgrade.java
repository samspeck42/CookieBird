package com.samspeck.cookiebird;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Upgrade {
	
	private String description;
	private BufferedImage icon;
	private int value;
	private int cost;
	private UpgradeType type;
	
	public void loadIcon(String path) {
		try {
			icon = ImageIO.read(CookieBirdGame.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

	public UpgradeType getType() {
		return type;
	}

	public void setType(UpgradeType type) {
		this.type = type;
	}
	
	public void setType(String typeName) {
		this.type = UpgradeType.valueOf(typeName);
	}
	
	public BufferedImage getIcon() {
		return icon;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public enum UpgradeType {
		IncreaseClickValue,
		IncreaseClickMultiplierIncrease,
		DecreaseNumObstaclesToIncreaseMultiplier,
		None
	}
}
