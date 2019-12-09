package menus;

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

public class PostLevelMenu extends Menu{
	
	public static int LEVEL_SCORE = 0;
	public static int LEVEL_KILLS = 0;
	public static long LEVEL_TIME_START = 0;
	public static long LEVEL_TIME_END = 0;
	
	private Text title;
	private Text levelStats;
	private Text attemptStats;
	private Button next;
	
	private Spawner spawner;
	

	//create all the necessary text/buttons as well as the background
	public PostLevelMenu() {
		background = new Background(700, 0, 1210, 720, Spawner.LEVEL.getBlockColor());
		
		Font titleFont = new Font("Consolas", java.awt.Font.PLAIN, 75);
		title = new Text("[PLANET_NAME] COMPLETE", 330, 630, titleFont, new Color(1,1,1));
		
		Font textFont = new Font("Consolas", java.awt.Font.PLAIN, 50);
		String levelStatsStr = "Score...........................[SCORE] \nTime............................[TIME] \nKills...........................[KILLS]";
		levelStats = new Text(levelStatsStr, 90, 400, textFont, new Color(1,1,1));
		
		String attemptStatsStr = "Total Score.....................[SCORE] \nTotal Time......................[TIME] \nTotal Kills.....................[KILLS]";
		attemptStats = new Text(attemptStatsStr, 90, 180, textFont, new Color(1,1,1));
		
		next = new Button(1010, 50, 150, 80, new Color(1,1,1));
		next.setHoverCol(new Color(1,1,0));
		next.setText(new Text("Next", 1030, 60, textFont, new Color(0,0,0)));
	}
	
	//set the text values to the up to date values, to ensure they are accurate when they are next viewed
	@Override
	public void update(Handler handler, float delta) {
		background.setCol(Spawner.LEVEL.getBlockColor());
		title.setText(Spawner.LEVEL.getPlanetName() + " COMPLETE");
		
		//convert the time in seconds, to minutes and seconds for when it is displayed
		float time = (int) (LEVEL_TIME_END - LEVEL_TIME_START);
		int minutes = (int)(time / (60 * 1000));
		float seconds = (time / 1000) % 60;
		String timeStr = String.format("%02d:%05.2f", minutes, seconds);
		
		String levelStatsStr = "Score..........................." + String.format("%08d",LEVEL_SCORE) + "\nTime............................" + timeStr + "\nKills..........................." + LEVEL_KILLS;
		levelStats.setText(levelStatsStr);
		
		//convert the time in seconds, to minutes and seconds for when it is displayed
		time += StatsMenu.TIME;
		minutes = (int)(time / (60 * 1000));
		seconds = (time / 1000) % 60;
		timeStr = String.format("%02d:%05.2f", minutes, seconds);
		
		String attemptStatsStr = "Total Score....................."+ String.format("%08d",LEVEL_SCORE+StatsMenu.SCORE) + "\nTotal Time......................" + timeStr + "\nTotal Kills....................." + (LEVEL_KILLS + StatsMenu.KILLS);
		attemptStats.setText(attemptStatsStr);
	}

	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {
		renderBackground(renderer, texture, alpha);
		
		title.render(renderer);
		levelStats.render(renderer);
		attemptStats.render(renderer);
		
		next.render(renderer, texture);
	}

	@Override
	public void keyInput(int key, int actions, int mods) {
		
	}

	@Override
	public void mouseButtonInput(int button, int action, int mods) {
		next.mouseButtonInput(button, action, mods);
		
		//add level stats to overall stats
		//reset level stats
		//if last level -> game complete menu
		//else -> next level
		if (next.isClicked()) {
			next.setClicked(false);
			next.setHovering(false);

			StatsMenu.SCORE += LEVEL_SCORE;
			StatsMenu.TIME += (int) (LEVEL_TIME_END - LEVEL_TIME_START);
			StatsMenu.KILLS += LEVEL_KILLS;
			
			//give player 300 points for completing level (not pluto though)
			if (Spawner.LEVEL != Level.Pluto) StatsMenu.SCORE += 300;
			
			resetLevelStats();

			int level = Spawner.LEVEL.ordinal();
			if (level != 8) {
				Spawner.LEVEL = Level.values()[level+1];
				spawner.spawnLevel();
				Main.GAMESTATE = GameState.Game;
			}else {
				Main.GAMESTATE = GameState.GameCompleteMenu;
			}
			
		}
	}

	@Override
	public void mousePosInput(double xpos, double ypos) {
		next.mousePosInput(xpos, ypos);
	}

	public void setSpawner(Spawner spawner) {
		this.spawner = spawner;
	}
	
	public static void resetLevelStats() {
		LEVEL_SCORE = LEVEL_KILLS = 0;
	}
}
