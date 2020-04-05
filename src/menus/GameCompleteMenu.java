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

public class GameCompleteMenu extends Menu{
	
	private Text title;
	private Text score;
	private Text kills;
	private Text time;
	private Text seed;
	
	private Button back;
	
	private Handler handler;
	
	//create all the necessary text/buttons as well as the background
	public GameCompleteMenu(Handler handler) {
		this.handler = handler;
		
		background = new Background(4490, 0, 1220, 720, new Color(1,1,1));
		
		Font titleFont = new Font("Consolas", java.awt.Font.PLAIN, 75);
		title = new Text("GAME COMPLETE", 400, 630, titleFont, new Color(1,1,1));
		
		Font textFont = new Font("Consolas", java.awt.Font.PLAIN, 50);
		String scoreStr = "Score...........................[SCORE] \nHigh score......................[HSCORE]";
		score = new Text(scoreStr, 90, 500, textFont, new Color(1,1,1));
		
		String killsStr = "Kills...........................[KILLS] \nMost kills......................[HKILLS]";
		kills = new Text(killsStr, 90, 350, textFont, new Color(1,1,1));
		
		String timeStr = "Time............................[TIME] \nFastest time....................[HTIME]";
		time = new Text(timeStr, 90, 200, textFont, new Color(1,1,1));
		
		seed = new Text("SEED: [SEED]", 90, 50, textFont, new Color(1,1,1));
		
		back = new Button(910, 50, 350, 80, new Color(1,1,1));
		back.setHoverCol(new Color(1,1,0));
		back.setText(new Text("Back to menu", 925, 60, textFont, new Color(0,0,0)));
	}

	//set the text values to the up to date values, to ensure they are accurate when they are next viewed
	@Override
	public void update(Handler handler, float delta) {
		String scoreStr = "Score..........................." + String.format("%08d",StatsMenu.SCORE) +  "\nHigh score......................"+String.format("%08d",StatsMenu.HSCORE);
		score.setText(scoreStr);
		
		String killsStr = "Kills..........................." + StatsMenu.KILLS + "\nMost kills......................"+StatsMenu.HKILLS;
		kills.setText(killsStr);
		
		//convert the time in seconds, to minutes and seconds for when it is displayed
		float time = StatsMenu.TIME;
		int minutes = (int)(time / (60 * 1000));
		float seconds = (time / 1000) % 60;
		
		String currentTimeStr = String.format("%02d:%05.2f", minutes, seconds);
		
		time = StatsMenu.HTIME;
		minutes = (int)(time / (60 * 1000));
		seconds = (time / 1000) % 60;
		String htimeStr = StatsMenu.HTIME == 0 ? "N/A" : String.format("%02d:%05.2f", minutes, seconds);;
		String timeStr = "Time............................" + currentTimeStr + "\nFastest time...................."+htimeStr;
		this.time.setText(timeStr);
		
		//convert the seed to hex, padded with leading zeroes
		String seedText = Integer.toHexString(Spawner.SEED).toUpperCase();
		seedText = "SEED: " + ("0000000" + seedText).substring(seedText.length());
		seed.setText(seedText);
	}

	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {
		renderBackground(renderer, texture, alpha);
		
		title.render(renderer);
		score.render(renderer);
		kills.render(renderer);
		time.render(renderer);
		seed.render(renderer);
		
		back.render(renderer, texture);
	}

	@Override
	public void keyInput(int key, int actions, int mods) {
		
	}

	@Override
	public void mouseButtonInput(int button, int action, int mods) {
		back.mouseButtonInput(button, action, mods);
		
		//save attempt
		//go to main menu
		if (back.isClicked()) {
			back.setClicked(false);
			back.setHovering(false);
			
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
