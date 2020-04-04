package menus;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Random;

import main.GameState;
import main.Handler;
import main.Level;
import main.Main;
import main.Spawner;
import rendering.Color;
import rendering.Font;
import rendering.Renderer;
import rendering.Text;
import rendering.Texture;
import sprites.Background;

public class MainMenu extends Menu  {
	
	private Button play;
	private String seed = "";
	private Text seedText;
	private Button settings;
	private Button stats;
	private Button quit;
	
	private Spawner spawner;

	//create all the necessary text/buttons as well as the background
	public MainMenu() {
		background = new Background(1920, 0, 1220, 720, new Color(1,1,1));
		
		play = new Button(0, 200, 1280, 50, new Color(1,1,1));
		play.setVisibleBackground(false);
		play.setHoverCol(new Color(0,1,0));
		play.setBackgroundShape("rectangle");
		play.setText(new Text("PLAY", 30, 200, new Font("Consolas", java.awt.Font.BOLD, 40), new Color(1,1,1)));
		
		settings = new Button(0, 150, 1280, 50, new Color(1,1,1));
		settings.setVisibleBackground(false);
		settings.setHoverCol(new Color(0,1,0));
		settings.setBackgroundShape("rectangle");
		settings.setText(new Text("SETTINGS", 30, 150, new Font("Consolas", java.awt.Font.BOLD, 40), new Color(1,1,1)));
		
		stats = new Button(0, 100, 1280, 50, new Color(1,1,1));
		stats.setVisibleBackground(false);
		stats.setHoverCol(new Color(0,1,0));
		stats.setBackgroundShape("rectangle");
		stats.setText(new Text("STATS", 30, 100, new Font("Consolas", java.awt.Font.BOLD, 40), new Color(1,1,1)));
		
		quit = new Button(0, 50, 1280, 50, new Color(1,1,1));
		quit.setVisibleBackground(false);
		quit.setHoverCol(new Color(0,1,0));
		quit.setBackgroundShape("rectangle");
		quit.setText(new Text("QUIT", 30, 50, new Font("Consolas", java.awt.Font.BOLD, 40), new Color(1,1,1)));
	
		seedText = new Text("SEED: _______", 150, 200, new Font("Consolas", java.awt.Font.BOLD, 40), new Color(0.5f,0.5f,0.5f));
	}
	
	@Override
	public void update(Handler handler, float delta) {
	
	}

	//draw the background, text, buttons etc
	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {
		renderBackground(renderer, texture, alpha);
		
		play.render(renderer, texture);
		settings.render(renderer, texture);
		stats.render(renderer, texture);
		quit.render(renderer, texture);
		
		seedText.render(renderer);
	}

	//if a valid key is pressed and the seed is not full, append it to the seed
	@Override
	public void keyInput(int key, int action, int mods) {
		if (action != GLFW_PRESS) return;
		switch(key) {
		case GLFW_KEY_0:
			if (seed.length() < 7)
				seed += "0";
			break;
		case GLFW_KEY_1:
			if (seed.length() < 7)
				seed += "1";
			break;
		case GLFW_KEY_2:
			if (seed.length() < 7)
				seed += "2";
			break;
		case GLFW_KEY_3:
			if (seed.length() < 7)
				seed += "3";
			break;
		case GLFW_KEY_4:
			if (seed.length() < 7)
				seed += "4";
			break;
		case GLFW_KEY_5:
			if (seed.length() < 7)
				seed += "5";
			break;
		case GLFW_KEY_6:
			if (seed.length() < 7)
				seed += "6";
			break;
		case GLFW_KEY_7:
			if (seed.length() < 7)
				seed += "7";
			break;
		case GLFW_KEY_8:
			if (seed.length() < 7)
				seed += "8";
			break;
		case GLFW_KEY_9:
			if (seed.length() < 7)
				seed += "9";
			break;
		case GLFW_KEY_A:
			if (seed.length() < 7)
				seed += "A";
			break;
		case GLFW_KEY_B:
			if (seed.length() < 7)
				seed += "B";
			break;
		case GLFW_KEY_C:
			if (seed.length() < 7)
				seed += "C";
			break;
		case GLFW_KEY_D:
			if (seed.length() < 7)
				seed += "D";
			break;
		case GLFW_KEY_E:
			if (seed.length() < 7)
				seed += "E";
			break;
		case GLFW_KEY_F:
			if (seed.length() < 7)
				seed += "F";
			break;
		case GLFW_KEY_BACKSPACE:
			if (seed.length() > 0)
				seed = seed.substring(0,seed.length()-1);
			break;
		}
		
		//render the seed, greyed out if nothing entered
		seedText.setText("SEED: " + repeat("_", 7-seed.length()) + seed);
		if (seed.length() > 0) seedText.setColor(new Color(1,1,1));
		else seedText.setColor(new Color(0.5f, 0.5f, 0.5f));
	}
	
	//returns a string containing s concatenated n times
	private String repeat(String s, int n) {
		String repeated = "";
		for (int i = 0; i < n; i++) repeated += s;
		return repeated;
	}

	@Override
	public void mouseButtonInput(int button, int action, int mods) {
		play.mouseButtonInput(button, action, mods);
		settings.mouseButtonInput(button, action, mods);
		stats.mouseButtonInput(button, action, mods);
		quit.mouseButtonInput(button, action, mods);
		
		//start game
		//if there is a seed inputted, send it to the spawner, else send a randomly generated one
		if (play.isClicked()) {
			play.setClicked(false);
			Main.GAMESTATE = GameState.Game;
			Spawner.LEVEL = SettingsMenu.TUTORIAL ? Level.Pluto : Level.Mercury;
			if (seed.length() == 7) {
				Spawner.SEED = Spawner.CURRENT_SEED = Integer.parseInt(seed, 16);
				
			}else {
				Random r = new Random();
				Spawner.SEED = Spawner.CURRENT_SEED = r.nextInt(268435456);
			}
    		spawner.spawnLevel();
			play.setHovering(false);
			
			//reset the seed text
			seed = "";
			seedText.setText("SEED: _______");
			seedText.setColor(new Color(0.5f, 0.5f, 0.5f));
		}
		//go to the settings menu
		if (settings.isClicked()) {
			settings.setClicked(false);
			Main.GAMESTATE = GameState.SettingsMenu;
			settings.setHovering(false);
		}
		//go to the stats menu
		if (stats.isClicked()) {
			stats.setClicked(false);
			Main.GAMESTATE = GameState.StatsMenu;
			stats.setHovering(false);
		}
		//quit the game
		if (quit.isClicked()) {
			quit.setClicked(false);
			quit.setHovering(false);
			System.exit(0);
		}
	}

	@Override
	public void mousePosInput(double xpos, double ypos) {
		play.mousePosInput(xpos, ypos);
		settings.mousePosInput(xpos, ypos);
		stats.mousePosInput(xpos, ypos);
		quit.mousePosInput(xpos, ypos);
	}
	
	public void setSpawner(Spawner spawner) {
		this.spawner = spawner;
	}

}
