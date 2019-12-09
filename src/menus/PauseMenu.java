package menus;

import main.GameState;
import main.Handler;
import main.Main;
import main.Spawner;
import rendering.Color;
import rendering.Font;
import rendering.Renderer;
import rendering.Text;
import rendering.Texture;

public class PauseMenu extends Menu {

	private Text title;
	private Text planet;
	private Text gravity;
	private Text temp;
	private Text seed;
	
	private Button resume;
	private Button quit;
	private Button backgroundShape;
	
	//create all the necessary text/buttons as well as the background
	public PauseMenu(){
		background = null;
		
		Font titleFont = new Font("Consolas", java.awt.Font.PLAIN, 65);
		title = new Text("PAUSED", 540, 530, titleFont, new Color(1,1,1));
		
		Font textFont = new Font("Consolas", java.awt.Font.PLAIN, 25);
		planet = new Text("LEVEL: [PLANET_NAME]", 520, 500, textFont, new Color(1,1,1));
		gravity = new Text("GRAVITY: [GRAVITY_LEVEL]", 520, 470, textFont, new Color(1,1,1));
		temp = new Text("TEMPERATURE: [TEMP]", 520, 440, textFont, new Color(1,1,1));
		seed = new Text("SEED = [SEED]", 520, 370, textFont, new Color(1,1,1));
		
		resume = new Button(470, 270, 340, 60, new Color(1,1,1));
		resume.setHoverCol(new Color(1,1,0));
		resume.setText(new Text("RESUME", 555, 270, new Font("Consolas", java.awt.Font.BOLD, 50), new Color(0,0,0)));
		
		quit = new Button(470, 200, 340, 60, new Color(1,1,1));
		quit.setHoverCol(new Color(1,1,0));
		quit.setText(new Text("QUIT", 585, 200, new Font("Consolas", java.awt.Font.BOLD, 50), new Color(0,0,0)));
		
		backgroundShape = new Button(440, 190, 400, 430, new Color(0.4f,0.4f,0.4f));
	}

	//ensure the stats displayed are up to date
	@Override
	public void update(Handler handler, float delta) {
		planet.setText("LEVEL: " + Spawner.LEVEL.getPlanetName());
		gravity.setText("GRAVITY: " + Spawner.LEVEL.getActualGravity() + "g");
		temp.setText("TEMPERATURE: " + Spawner.LEVEL.getActualTemp() + "°C");
		
		//convert the seed to hex, padded with leading zeroes
		String seedText = Integer.toHexString(Spawner.SEED).toUpperCase();
		seed.setText("SEED: " + ("0000000" + seedText).substring(seedText.length()));
	}

	//draw the background, text, buttons etc
	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {
		backgroundShape.render(renderer, texture);
		
		title.render(renderer);
		planet.render(renderer);
		gravity.render(renderer);
		temp.render(renderer);
		seed.render(renderer);
		
		resume.render(renderer, texture);
		quit.render(renderer, texture);
	}

	@Override
	public void keyInput(int key, int actions, int mods) {
		
	}

	@Override
	public void mouseButtonInput(int button, int action, int mods) {
		resume.mouseButtonInput(button, action, mods);
		quit.mouseButtonInput(button, action, mods);
		
		//resume game
		if (resume.isClicked()) {
			resume.setClicked(false);
			Main.GAMESTATE = GameState.Game;
			resume.setHovering(false);
		}
		//quit to main menu
		else if (quit.isClicked()) {
			quit.setClicked(false);
			Main.GAMESTATE = GameState.MainMenu;
			StatsMenu.resetAttemptStats();
			PostLevelMenu.resetLevelStats();
			quit.setHovering(false);
		}
	}

	@Override
	public void mousePosInput(double xpos, double ypos) {
		resume.mousePosInput(xpos, ypos);
		quit.mousePosInput(xpos, ypos);
	}
}
