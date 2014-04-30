 package com.samspeck.cookiebird;
 
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.samspeck.cookiebird.engine.BaseGame;
import com.samspeck.cookiebird.engine.Vector2D;
import com.samspeck.cookiebird.gamescreens.GameOverScreen;
import com.samspeck.cookiebird.gamescreens.HUD;
import com.samspeck.cookiebird.gamescreens.UpgradesScreen;

public class CookieBirdGame extends BaseGame implements MouseListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1265038899830060633L;
	private static final String RES_ROOT = "";
	private static JFrame frame;
	private static final int RESTART_TRANSITION_TIME = 50;
	private static final int SCREEN_FLASH_TIME = 20;
	public static final int SCREEN_WIDTH = 440;
	public static final int SCREEN_HEIGHT = 660;
	public static final int SCROLL_SPEED = 3;
	public static final int FLOOR_POSITION_Y = 520;
	public static final int NUM_OBSTACLES = 5;
	public static final int OBSTACLE_DISTANCE = 240;
	public static Random rand = new Random();
	
	public GameState gameState;
	public long lastClickTimer;
	
	private Cookie cookie;
	private LinkedList<Obstacle> obstacles;
	private Obstacle nextObstacle;
	private Milk milk;
	
	private int numObstaclesPassed;
	private int totalNumCookies;
	private int numCookiesThisRound;
	private int highestNumCookies;
	
	private int clickValue;
	private int clickMultiplier;
	private int clickMultiplierIncrease;
	private int numObstaclesToIncreaseMultiplier;
	
	private GameOverScreen gameOverScreen;
	private HUD hud;
	private UpgradesScreen upgradesScreen;
	
	private boolean obstacleHit;
	private BufferedImage backdropImage;
	private boolean isRestarting = false;
	private int restartTransitionTimer = 0;
	private boolean isScreenFlashing = false;
	private int screenFlashTimer = 0;

	public int getNumCookiesThisRound() {
		return numCookiesThisRound;
	}

	public int getHighestNumCookies() {
		return highestNumCookies;
	}
	
	public int getNumObstaclesPassed() {
		return numObstaclesPassed;
	}
	
	public int getTotalNumCookies() {
		return totalNumCookies;
	}
	
	public CookieBirdGame() {
		cookie = new Cookie(this);
		milk = new Milk(this);
		gameOverScreen = new GameOverScreen(this);
		hud = new HUD(this);
		upgradesScreen = new UpgradesScreen(this);
		obstacles = new LinkedList<Obstacle>();
		obstacleHit = false;
		totalNumCookies = 0;
		numCookiesThisRound = 0;
		highestNumCookies = 0;
		clickValue = 1;
		clickMultiplier = 1;
		clickMultiplierIncrease = 1;
		numObstaclesToIncreaseMultiplier = 10;
		addMouseListener(this);
		
		cookie.load();
		milk.load();
		gameOverScreen.load("/screens/game_over_screen.txt");
		hud.load("/screens/hud.txt");
		upgradesScreen.load("/screens/upgrades_screen.txt");
		try {
			backdropImage = ImageIO.read(CookieBirdGame.class.getClassLoader().getResourceAsStream("backdrop.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cookie.position.x = SCREEN_WIDTH / 4;
		cookie.acceleration.y = 0.4f;

		resetGame();
	}
	
	public void openUpgradesScreen() {
		upgradesScreen.resetScreen();
		gameState = GameState.UpgradesScreen;
	}
	
	public void restartGame() {
		if (!isRestarting) {
			isRestarting = true;
			restartTransitionTimer = 0;
		}
	}
	
	public void startScreenFlash() {
		isScreenFlashing = true;
	}

	public void resetGame() {
		cookie.reset();
		numObstaclesPassed = 0;
		numCookiesThisRound = 0;
		clickMultiplier = 1;
		obstacleHit = false;
		setObstacles();
		nextObstacle = obstacles.get(0);
		gameState = GameState.Starting;
		lastClickTimer = 0;
	}

	private void setObstacles() {
		obstacles.clear();
		int positionX = (int) (SCREEN_WIDTH * 1.5);
		for(int i = 0; i < NUM_OBSTACLES; i++) {
			Obstacle obs = new Obstacle(this);
			obs.load();
			obs.positionX = positionX;
			obstacles.add(obs);
			positionX += OBSTACLE_DISTANCE;
		}
	}

	@Override
	public void update() {
		cookie.update();
		hud.update();
		if (gameState == GameState.Playing) {
			
			if (!obstacleHit) {
				for (Obstacle ob : obstacles)
					ob.update();
				milk.update();
				
				// check if passed next obstacle
				int distance = nextObstacle.positionX - (int)cookie.position.x;
				if (distance <= 0) {
					numObstaclesPassed++;
					int nextIndex = (obstacles.indexOf(nextObstacle) + 1) % obstacles.size();
					nextObstacle = obstacles.get(nextIndex);
					
					if (numObstaclesPassed % numObstaclesToIncreaseMultiplier == 0) {
						increaseClickMultiplier();
					}
				}
				
				for (Obstacle ob : obstacles) {
					if (ob.positionX < -ob.getWidth())
						resetObstacle(ob);
					
					// check if obstacle was hit
					for (Rectangle obsHitBox : ob.getHitBoxes()) {
						if(cookie.getHitBox().intersects(obsHitBox)) {
							obstacleHit = true;
							startScreenFlash();
							break;
						}
					}
				}
			}
	
			if (cookie.position.y >= (FLOOR_POSITION_Y - cookie.getWidth())) {
				if (!obstacleHit)
					startScreenFlash();
				if (numCookiesThisRound > highestNumCookies)
					highestNumCookies = numCookiesThisRound;
				gameState = GameState.GameOver;
			}
			
			lastClickTimer++;
		}
		else if (gameState == GameState.Starting) {
			milk.update();
		}
		else if (gameState == GameState.GameOver) {
			gameOverScreen.update();
		}
		else if (gameState == GameState.UpgradesScreen) {
			upgradesScreen.update();
		}
		
		handleScreenEffects();
	}

	private void increaseClickMultiplier() {
		clickMultiplier += clickMultiplierIncrease;
		hud.addNotificationTextLineComponent("Click Multiplier +" + clickMultiplierIncrease, 21, Color.GREEN, 
				new Vector2D(SCREEN_WIDTH / 2, SCREEN_HEIGHT * 1 / 3));
	}

	private void handleScreenEffects() {
		if (isRestarting) {
			restartTransitionTimer++;
			if (restartTransitionTimer == (RESTART_TRANSITION_TIME / 2)) {
				resetGame();
			}
			else if (restartTransitionTimer >= RESTART_TRANSITION_TIME) {
				isRestarting = false;
				restartTransitionTimer = 0;
			}
		}
		if (isScreenFlashing) {
			screenFlashTimer++;
			if (screenFlashTimer >= SCREEN_FLASH_TIME) {
				isScreenFlashing = false;
				screenFlashTimer = 0;
			}
		}
	}

	private void resetObstacle(Obstacle ob) {
		int index = obstacles.indexOf(ob);
		Obstacle prevOb = null;
		if (index > 0)
			prevOb = obstacles.get(index - 1);
		else
			prevOb = obstacles.getLast();
		
		ob.resetGap();
		ob.positionX = prevOb.positionX + OBSTACLE_DISTANCE;
	}

	@Override
	public void render(Graphics2D g) {
		renderGame(g);
		hud.render(g);
		if (gameState == GameState.GameOver) {
			gameOverScreen.render(g);
		}
		else if (gameState == GameState.UpgradesScreen) {
			upgradesScreen.render(g);
		}
		renderScreenEffects(g);
//		g.setColor(Color.GREEN);
//		g.drawRect(Math.round(cookie.position.x), Math.round(cookie.position.y), cookie.getWidth(), cookie.getHeight());
//		g.setColor(Color.BLUE);
//		for (Obstacle ob : obstacles) {
//			for (Rectangle rect : ob.getHitBoxes()) {
//				g.drawRect(rect.x, rect.y, rect.width, rect.height);
//			}
//		}
	}

	private void renderScreenEffects(Graphics2D g) {
		if (isRestarting) {
			float alpha = 1.0f - (Math.abs((float) (RESTART_TRANSITION_TIME / 2) - restartTransitionTimer) / (RESTART_TRANSITION_TIME / 2));
			alpha = Math.max(0f, alpha);
			alpha = Math.min(1f, alpha);
			g.setColor(new Color(0f, 0f, 0f, alpha));
			g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		}
		if (isScreenFlashing) {
			float alpha = 1.0f - ((float) screenFlashTimer / SCREEN_FLASH_TIME);
			alpha = Math.max(0f, alpha);
			alpha = Math.min(1f, alpha);
			g.setColor(new Color(1f, 1f, 1f, alpha));
			g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		}
	}

	private void renderGame(Graphics2D g) {
		g.drawImage(backdropImage, 0, 0, this);
		for (Obstacle ob : obstacles)
			ob.render(g, this);
		cookie.render(g, this);
		milk.render(g, this);
	}

	@Override
	public void mousePressed(MouseEvent m) {
		if (gameState == GameState.Starting) {
			gameState = GameState.Playing;
		}
		if (gameState == GameState.Playing && !obstacleHit && cookie.position.y > 0) {
			if(cookie.getHitBox().contains(new Point(m.getX(), m.getY()))) {
				// cookie clicked
				incrementCookies();
				cookie.setScale(0.85f);
				int numCookies = clickValue * clickMultiplier;
				hud.addNotificationTextLineComponent("+" + numCookies, 14, Color.WHITE, 
						new Vector2D(m.getX() + (5 - rand.nextInt(10)), 
								((float) m.getY()) - (cookie.getHeight() * 1.5f)));
			}
			cookie.velocity.y = -8;
			cookie.rotationalVelocity = -Math.toRadians(8);
			cookie.rotationalAcceleration = 0;
			lastClickTimer = 0;
		}
		else if (gameState == GameState.GameOver) {
			gameOverScreen.mousePressed(m);
		}
		else if (gameState == GameState.UpgradesScreen) {
			upgradesScreen.mousePressed(m);
		}
	}

	private void incrementCookies() {
		int numCookies = clickValue * clickMultiplier;
		totalNumCookies += numCookies;
		numCookiesThisRound += numCookies;
	}

	@Override
	public void mouseReleased(MouseEvent m) {
		cookie.setScale(1);
		if (gameState == GameState.GameOver) {
			gameOverScreen.mouseReleased(m);
		}
		else if (gameState == GameState.UpgradesScreen) {
			upgradesScreen.mouseReleased(m);
		}
	}
	
	public void increaseClickValue(int value) {
		clickValue += value;
	}
	
	public void increaseClickMultiplierIncrease(int value) {
		clickMultiplierIncrease += value;
	}
	
	public void decreaseNumObstaclesToIncreaseMultiplier(int value) {
		numObstaclesToIncreaseMultiplier -= value;
	}
	
	public void decreaseTotalNumCookies(int amount) {
		totalNumCookies -= amount;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createGUIAndStart();
				//createAppletGUIAndStart();
			}
		});
		
	}

	private static void createGUIAndStart() {
		CookieBirdGame game = new CookieBirdGame();
		game.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		frame = new JFrame();
		frame.add(game);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Cookie Bird");
		frame.setResizable(false);
		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
		game.start();
	}
	
	public enum GameState {
		Starting,
		Playing,
		GameOver,
		UpgradesScreen
	}

}
