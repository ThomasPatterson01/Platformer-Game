package main;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Random;

import menus.GameCompleteMenu;
import menus.GameOverMenu;
import menus.MainMenu;
import menus.PauseMenu;
import menus.PostLevelMenu;
import menus.SettingsMenu;
import menus.StatsMenu;
import menus.UI;
import rendering.FadingText;
import rendering.Renderer;
import rendering.Texture;
import rendering.Window;
import sprites.*;

public class Handler {
	private ArrayList<Block> blocks = new ArrayList<>();
	private ArrayList<Enemy> enemies = new ArrayList<>();
	private ArrayList<Zone> zones = new ArrayList<>();
	private ArrayList<Bullet> bullets = new ArrayList<>();
	private ArrayList<Particle> particles = new ArrayList<>();
	private ArrayList<ShootingStar> shootingStars = new ArrayList<>();
	private ArrayList<FadingText> fadingTexts = new ArrayList<>();
	private ArrayList<Pickup> pickups = new ArrayList<>();
	
	private Player player;
	private Camera camera;
	
	private UI ui;
	private PauseMenu pauseMenu;
	private MainMenu mainMenu;
	private SettingsMenu settingsMenu;
	private PostLevelMenu postLevelMenu;
	private GameOverMenu gameOverMenu;
	private GameCompleteMenu gameCompleteMenu;
	private StatsMenu statsMenu;
	
	private int gameOverTimer = 180;
	
	public Handler(Window window) {
		ui = new UI();
		pauseMenu = new PauseMenu();
		mainMenu = new MainMenu();
		settingsMenu = new SettingsMenu(window);
		postLevelMenu = new PostLevelMenu();
		gameOverMenu = new GameOverMenu(this);
		gameCompleteMenu = new GameCompleteMenu(this);
		statsMenu = new StatsMenu();
	}

	public void update(float delta) {
		
		//update the correct objects based on what state the game is in
		switch(Main.GAMESTATE) {
		case Game:
			
			//if there is no shooting star, create a new one at the top of the screen
			if (shootingStars.size() == 0) {
				Random r = new Random();
				float sx = r.nextInt(Main.WIDTH) - 400 + 200;
				float sy = Main.HEIGHT+50;
				float sVelX = r.nextFloat();
				sVelX = sVelX < 0.5 ? sVelX*200 - 500 : sVelX*200 + 200;
				float sVelY = -r.nextInt(30) - 40;
				shootingStars.add(new ShootingStar(sx, sy, sVelX, sVelY));
			}
			
			ui.update(this, delta);
			
			if (player != null) player.update(this, delta);
			for (int i = blocks.size()-1; i > -1; i--) {
				blocks.get(i).update(this, delta);
			}
			
			//cant be a for-each loop because the sprite may try to remove itself from the list, causing a crash.
			//runs through backwards so that no sprites are skipped if an item is removed
			for (int i = enemies.size()-1; i > -1; i--) {
				enemies.get(i).update(this, delta);
			}
			for (int i = bullets.size()-1; i > -1; i--) {
				bullets.get(i).update(this, delta);
			}
			for (int i = particles.size()-1; i > -1; i--) {
				particles.get(i).update(this, delta);
			}
			for (int i = shootingStars.size()-1; i > -1; i--) {
				shootingStars.get(i).update(this, delta);
			}
			for (int i = fadingTexts.size()-1; i > -1; i--) {
				fadingTexts.get(i).update(this, delta);
			}
			for (int i = pickups.size()-1; i > -1; i--) {
				pickups.get(i).update(this, delta);
			}
			for (Zone z : zones) {
				z.update(this, delta);
			}
			
			//when the player is null (they must be dead), end the game after 3 seconds
			if (player == null) {
				gameOverTimer--;
				if (gameOverTimer <= 0) {
					Main.GAMESTATE = GameState.GameOverMenu;
					gameOverTimer = 180;
					PostLevelMenu.LEVEL_TIME_END = (long)(System.currentTimeMillis()) - 3000;
				}
			}else gameOverTimer = 180;
			break;
		case Pause:
			pauseMenu.update(this, delta);
			break;
		case MainMenu:
			mainMenu.update(this, delta);
			break;
		case SettingsMenu:
			settingsMenu.update(this, delta);
			break;
		case PostLevelMenu:
			postLevelMenu.update(this, delta);
			break;
		case GameOverMenu:
			gameOverMenu.update(this, delta);
			break;
		case GameCompleteMenu:
			gameCompleteMenu.update(this, delta);
			break;
		case StatsMenu:
			statsMenu.update(this, delta);
			break;
		default:
			break;
		}
	}
	
	public void render(Renderer renderer, Texture texture, float alpha) {
		Area camHitbox = camera.getHitbox();
		
		//render the correct objects based on what state the game is in
		switch(Main.GAMESTATE) {
		case Game:
			ui.renderBackground(renderer, texture, alpha);
			
			for (ShootingStar s : shootingStars) {
				s.render(renderer, texture, alpha);
			}
			camera.render(renderer, alpha);

			for (Pickup p : pickups) {
				p.render(renderer, texture, alpha);
			}
			for (Block b : blocks) {
				if (camera != null && (camHitbox.intersects(b.getHitbox()) || b.isFixedScreenLocation())) {
					b.render(renderer, texture, alpha);
				}
			}
			for (Enemy e : enemies) {
				if (camera != null && (camHitbox.intersects(e.getHitbox()) || e.isFixedScreenLocation())) {
					e.render(renderer, texture, alpha);
				}
			}
			for (Bullet b : bullets) {
				if (camera != null && (camHitbox.intersects(b.getHitbox()) || b.isFixedScreenLocation())) {
					b.render(renderer, texture, alpha);
				}
			}

			for (Particle p : particles) {
				if (camera != null && (camHitbox.intersects(p.getHitbox()) || p.isFixedScreenLocation())) {
					p.render(renderer, texture, alpha);
				}
			}
			for (Zone z : zones) {
				if (camera != null && camHitbox.intersects(z.getHitbox())) {
					z.render(renderer, texture, alpha);
				}
			}
			for (FadingText f : fadingTexts) {
				f.render(renderer);
			}

			
			if (player != null) player.render(renderer, texture, alpha);


			ui.render(renderer, texture, alpha);

			break;
			
		case Pause:
			ui.renderBackground(renderer, texture, 0);
			
			for (ShootingStar s : shootingStars) {
				if (camera != null && camHitbox.intersects(s.getHitbox())) {
					s.render(renderer, texture, alpha);
				}
			}
			
			camera.render(renderer, 0);

			for (Pickup p : pickups) {
				p.render(renderer, texture, alpha);
			}
			for (Block b : blocks) {
				if (camera != null && camHitbox.intersects(b.getHitbox())) {
					b.render(renderer, texture, 0);
				}
			}
			for (Enemy e : enemies) {
				if (camera != null && camHitbox.intersects(e.getHitbox())) {
					e.render(renderer, texture, 0);
				}
			}
			for (Bullet b : bullets) {
				if (camera != null && camHitbox.intersects(b.getHitbox())) {
					b.render(renderer, texture, 0);
				}
			}
			for (Particle p : particles) {
				if (camera != null && camHitbox.intersects(p.getHitbox())) {
					p.render(renderer, texture, alpha);
				}
			}
			for (FadingText f : fadingTexts) {
				f.render(renderer);
			}

			
			if (player != null) player.render(renderer, texture, 0);


			ui.render(renderer, texture, alpha);

			pauseMenu.render(renderer, texture, alpha);
			break;
		case MainMenu:
			mainMenu.render(renderer, texture, alpha);
			break;
		case SettingsMenu:
			settingsMenu.render(renderer, texture, alpha);
			break;
		case PostLevelMenu:
			postLevelMenu.render(renderer, texture, alpha);
			break;
		case GameOverMenu:
			gameOverMenu.render(renderer, texture, alpha);
			break;
		case GameCompleteMenu:
			gameCompleteMenu.render(renderer, texture, alpha);
			break;
		case StatsMenu:
			statsMenu.render(renderer, texture, alpha);
			break;
		default:
			break;
		}
		
	}
	
	//process key input
	public void keyInput(int key, int action, int mods) {
		switch(Main.GAMESTATE) {
		case Game:
			if (player != null) player.keyInput(key, action, mods);
			if (camera != null) camera.keyInput(key, action, mods);
			break;
		case MainMenu:
			mainMenu.keyInput(key, action, mods);
			break;
		default:
			break;
		}
	}
	
	//process mouse button input
	public void mouseButtonInput(int button, int action, int mods) {
		switch(Main.GAMESTATE) {
		case Game:
			ui.mouseButtonInput(button, action, mods);
			break;
		case Pause:
			pauseMenu.mouseButtonInput(button, action, mods);
			break;
		case MainMenu:
			mainMenu.mouseButtonInput(button, action, mods);
			break;
		case SettingsMenu:
			settingsMenu.mouseButtonInput(button, action, mods);
			break;
		case PostLevelMenu:
			postLevelMenu.mouseButtonInput(button, action, mods);
			break;
		case GameOverMenu:
			gameOverMenu.mouseButtonInput(button, action, mods);
			break;
		case GameCompleteMenu:
			gameCompleteMenu.mouseButtonInput(button, action, mods);
			break;
		case StatsMenu:
			statsMenu.mouseButtonInput(button, action, mods);
			break;
		default:
			break;
		}
	}
	
	//process mouse position input
	public void mousePosInput(double xpos, double ypos) {
		switch(Main.GAMESTATE) {
		case Game:
			ui.mousePosInput(xpos, ypos);
			break;
		case Pause:
			pauseMenu.mousePosInput(xpos, ypos);
			break;
		case MainMenu:
			mainMenu.mousePosInput(xpos, ypos);
			break;
		case SettingsMenu:
			settingsMenu.mousePosInput(xpos, ypos);
			break;
		case PostLevelMenu:
			postLevelMenu.mousePosInput(xpos, ypos);
			break;
		case GameOverMenu:
			gameOverMenu.mousePosInput(xpos, ypos);
			break;
		case GameCompleteMenu:
			gameCompleteMenu.mousePosInput(xpos, ypos);
			break;
		case StatsMenu:
			statsMenu.mousePosInput(xpos, ypos);
			break;
		default:
			break;
		}
	}
	
	public void addBlock(Block b) {
		blocks.add(b);
	}
	public void removeBlock(Block b) {
		blocks.remove(b);
	}
	public ArrayList<Block> getBlocks() {
		return blocks;
	}
	public void clearBlocks() {
		blocks.clear();
	}
	
	public void addEnemy(Enemy e) {
		enemies.add(e);
	}
	public void removeEnemy(Enemy e) {
		enemies.remove(e);
	}
	public ArrayList<Enemy> getEnemies() {
		return enemies;
	}
	public void clearEnemies() {
		enemies.clear();
	}
	
	public void addZone(Zone z) {
		zones.add(z);
	}
	public void removeZone(Zone z) {
		zones.remove(z);
	}
	public ArrayList<Zone> getZones() {
		return zones;
	}
	public void clearZones() {
		zones.clear();
	}
	
	public void addBullet(Bullet b) {
		bullets.add(b);
	}
	public void removeBullet(Bullet b) {
		bullets.remove(b);
	}
	public ArrayList<Bullet> getBullets() {
		return bullets;
	}
	public void clearBullets() {
		bullets.clear();
	}
	
	public void addParticle(Particle p) {
		particles.add(p);
	}
	public void removeParticle(Particle p) {
		particles.remove(p);
	}
	public ArrayList<Particle> getParticles() {
		return particles;
	}
	public void clearParticles() {
		particles.clear();
	}
	
	public void addShootingStar(ShootingStar s) {
		shootingStars.add(s);
	}
	public void removeShootingStar(ShootingStar s) {
		shootingStars.remove(s);
	}
	public ArrayList<ShootingStar> getShootingStars() {
		return shootingStars;
	}
	public void clearShootingStars() {
		shootingStars.clear();
	}

	public void addFadingText(FadingText f) {fadingTexts.add(f);}
	public void removeFadingText(FadingText f) {
		fadingTexts.remove(f);
	}
	public ArrayList<FadingText> getFadingTexts() {
		return fadingTexts;
	}
	public void clearFadingTexts() {
		fadingTexts.clear();
	}

	public void addPickup(Pickup p) {
		pickups.add(p);
	}
	public void removePickup(Pickup p) {
		pickups.remove(p);
	}
	public ArrayList<Pickup> getPickups() {
		return pickups;
	}
	public void clearPickups() {
		pickups.clear();
	}

	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public Camera getCamera() {
		return camera;
	}
	public void setCamera(Camera camera) {
		this.camera = camera;
	}	
	
	public UI getUi() {
		return ui;
	}
	public void setUi(UI ui) {
		this.ui = ui;
	}

	public PauseMenu getPauseMenu() {
		return pauseMenu;
	}

	public void setPauseMenu(PauseMenu pauseMenu) {
		this.pauseMenu = pauseMenu;
	}

	public MainMenu getMainMenu() {
		return mainMenu;
	}

	public void setMainMenu(MainMenu mainMenu) {
		this.mainMenu = mainMenu;
	}

	public SettingsMenu getSettingsMenu() {
		return settingsMenu;
	}

	public void setSettingsMenu(SettingsMenu settingsMenu) {
		this.settingsMenu = settingsMenu;
	}

	public PostLevelMenu getPostLevelMenu() {
		return postLevelMenu;
	}

	public void setPostLevelMenu(PostLevelMenu postLevelMenu) {
		this.postLevelMenu = postLevelMenu;
	}

	public GameOverMenu getGameOverMenu() {
		return gameOverMenu;
	}

	public void setGameOverMenu(GameOverMenu gameOverMenu) {
		this.gameOverMenu = gameOverMenu;
	}

	public GameCompleteMenu getGameCompleteMenu() {
		return gameCompleteMenu;
	}

	public void setGameCompleteMenu(GameCompleteMenu gameCompleteMenu) {
		this.gameCompleteMenu = gameCompleteMenu;
	}

	public StatsMenu getStatsMenu() {
		return statsMenu;
	}

	public void setStatsMenu(StatsMenu statsMenu) {
		this.statsMenu = statsMenu;
	}
	
}
