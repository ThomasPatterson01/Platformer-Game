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
import sprites.Background;

public class GameOverMenu extends Menu{
	
	private Text title;
	private Text planetReached;
	private Text stats;
	private Button back;
	
	private Handler handler;
	
	//create all the necessary text/buttons as well as the background
	public GameOverMenu(Handler handler) {
		this.handler = handler;
		background = new Background(626, 20, 1, 1, new Color(0,0,0));
		
		Font titleFont = new Font("Consolas", java.awt.Font.PLAIN, 75);
		title = new Text("GAME OVER", 470, 630, titleFont, new Color(1,1,1));
		
		Font textFont = new Font("Consolas", java.awt.Font.PLAIN, 50);
		planetReached = new Text("YOU WILL FOREVER LIE ON [PLANET_NAME]", 250, 500, textFont, new Color(1,1,1));
		
		String statsStr = "Score...........................[SCORE] \nTime............................[TIME] \nKills...........................[KILLS] \nSeed............................[SEED]";
		stats = new Text(statsStr, 90, 200, textFont, new Color(1,1,1));
		
		back = new Button(910, 50, 350, 80, new Color(1,1,1));
		back.setHoverCol(new Color(1,1,0));
		back.setText(new Text("Back to menu", 925, 60, textFont, new Color(0,0,0)));
	}
		
	//set the text values to the up to date values, to ensure they are accurate when they are next viewed
	@Override
	public void update(Handler handler, float delta) {
		planetReached.setText("YOU WILL FOREVER LIE ON " + Spawner.LEVEL.getPlanetName());
		
		//convert the time in seconds, to minutes and seconds for when it is displayed
		float time =  (PostLevelMenu.LEVEL_TIME_END - PostLevelMenu.LEVEL_TIME_START);		
		time += StatsMenu.TIME;
		int minutes = (int)(time / (60 * 1000));
		float seconds = (time / 1000) % 60;
		String timeStr = String.format("%02d:%05.2f", minutes, seconds);
		
		//convert the seed to hex, padded with leading zeroes
		String seedText = Integer.toHexString(Spawner.SEED).toUpperCase();
		seedText = ("0000000" + seedText).substring(seedText.length());
		
		String statsStr = "Score..........................."+ String.format("%08d",PostLevelMenu.LEVEL_SCORE+StatsMenu.SCORE) + "\nTime............................" + timeStr + "\nKills..........................." + (PostLevelMenu.LEVEL_KILLS + StatsMenu.KILLS) + "\nSeed............................" + seedText;
		stats.setText(statsStr);
	}

	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {
		renderBackground(renderer, texture, alpha);
		
		title.render(renderer);
		planetReached.render(renderer);
		stats.render(renderer);
		
		back.render(renderer, texture);
	}

	@Override
	public void keyInput(int key, int actions, int mods) {
		
	}

	@Override
	public void mouseButtonInput(int button, int action, int mods) {
		back.mouseButtonInput(button, action, mods);
		
		if (back.isClicked()) {
			back.setClicked(false);
			back.setHovering(false);
			
			StatsMenu.SCORE += PostLevelMenu.LEVEL_SCORE;
			StatsMenu.TIME += (int) (PostLevelMenu.LEVEL_TIME_END - PostLevelMenu.LEVEL_TIME_START);
			StatsMenu.KILLS += PostLevelMenu.LEVEL_KILLS;
			handler.getStatsMenu().saveAttempt();
			
			Main.GAMESTATE = GameState.MainMenu;
			StatsMenu.resetAttemptStats();
			PostLevelMenu.resetLevelStats();
		}
	}

	@Override
	public void mousePosInput(double xpos, double ypos) {
		back.mousePosInput(xpos, ypos);
	}
	

}
