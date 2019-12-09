package menus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import main.DatabaseManager;
import main.GameState;
import main.Handler;
import main.Main;
import main.Spawner;
import rendering.Color;
import rendering.Font;
import rendering.Graph;
import rendering.Renderer;
import rendering.Text;
import rendering.Texture;
import sprites.Background;

public class StatsMenu extends Menu{

	//stats from current attempt
	public static int SCORE;
	public static int KILLS;
	public static int TIME;
	
	//totals from all previous attempts
	private int TSCORE;
	private int TKILLS;
	private int TTIME;
	private int TLEVEL;
	
	//best from previous attemps
	public static int HSCORE;
	public static int HKILLS;
	public static int HTIME;
	public static int HLEVEL;
	
	//seeds of bests from previous attempts
	private int HSCOREseed;
	private int HKILLSseed;
	private int HTIMEseed;
	private int HLEVELseed;
	
	private Text title;
	
	private Text score;
	private Text kills;
	private Text time;
	private Text level;
	
	private Button scoreBtn;
	private Button killsBtn;
	private Button timeBtn;
	private Button levelBtn;
	
	private Button back;
	
	//data for each stats from the previous attempts
	private ArrayList<Integer> seedData = new ArrayList<>();
	private ArrayList<Integer> scoreData = new ArrayList<>();
	private ArrayList<Integer> killData = new ArrayList<>();
	private ArrayList<Integer> timeData = new ArrayList<>();
	private ArrayList<Integer> levelData = new ArrayList<>();
	private ArrayList<Integer> attemptData = new ArrayList<>();

	private Graph graph;
	
	//create all the necessary text/buttons as well as the background
	public StatsMenu() {
		background = new Background(3200, 0, 1220, 720, new Color(1,1,1));
		
		Font titleFont = new Font("Consolas", java.awt.Font.PLAIN, 75);
		title = new Text("STATISTICS", 400, 630, titleFont, new Color(1,1,1));
		
		Font textFont = new Font("Consolas", java.awt.Font.PLAIN, 35);
		score = new Text("Total Score: [TSCORE] \nHigh Score: [HSCORE] (Seed: [SEED])", 20, 550, textFont, new Color(1,1,1));
		kills = new Text("Total Kills: [TKILLS] \nMost Kills: [HKILLS] (Seed: [SEED])", 20, 400, textFont, new Color(1,1,1));
		time = new Text("Total Time: [TTIME] \nFastest Time: [HTIME] (Seed: [SEED])", 20, 250, textFont, new Color(1,1,1));
		level = new Text("Total Levels: [TLEVEL] \nMost Levels: [HLEVEL] (Seed: [SEED])", 20, 100, textFont, new Color(1,1,1));
		
		scoreBtn = new Button(20, 500, 400, 50, new Color(1,1,1));
		scoreBtn.setHoverCol(new Color(1,1,0));
		scoreBtn.setText(new Text("Show score on graph", 40, 500, textFont, new Color(0,0,0)));
		
		killsBtn = new Button(20, 350, 400, 50, new Color(1,1,1));
		killsBtn.setHoverCol(new Color(1,1,0));
		killsBtn.setText(new Text("Show kills on graph", 40, 350, textFont, new Color(0,0,0)));
		
		timeBtn = new Button(20, 200, 400, 50, new Color(1,1,1));
		timeBtn.setHoverCol(new Color(1,1,0));
		timeBtn.setText(new Text("Show time on graph", 50, 200, textFont, new Color(0,0,0)));
		
		levelBtn = new Button(20, 50, 400, 50, new Color(1,1,1));
		levelBtn.setHoverCol(new Color(1,1,0));
		levelBtn.setText(new Text("Show levels on graph", 30, 50, textFont, new Color(0,0,0)));
		
		Font backButtonFont = new Font("Consolas", java.awt.Font.PLAIN, 50);
		back = new Button(910, 50, 350, 80, new Color(1,1,1));
		back.setHoverCol(new Color(1,1,0));
		back.setText(new Text("Back to menu", 925, 60, backButtonFont, new Color(0,0,0)));
		
		graph = new Graph(800, 200, 400, 400);
		graph.setXLabel("Attempt");
		
		//load stats and set graph to show the score by default
		loadStats();
		refreshStats();
		graph.setYLabel("Score");
		graph.showGraph(attemptData, scoreData);
	}
	
	@Override
	public void update(Handler handler, float delta) {
		
	}

	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {
		renderBackground(renderer, texture, alpha);
		
		title.render(renderer);
		score.render(renderer);
		kills.render(renderer);
		time.render(renderer);
		level.render(renderer);
		
		scoreBtn.render(renderer, texture);
		killsBtn.render(renderer, texture);
		timeBtn.render(renderer, texture);
		levelBtn.render(renderer, texture);
		
		back.render(renderer, texture);
		
		graph.render(renderer, texture, alpha);
	}

	@Override
	public void keyInput(int key, int actions, int mods) {
		
	}

	@Override
	public void mouseButtonInput(int button, int action, int mods) {
		back.mouseButtonInput(button, action, mods);
		scoreBtn.mouseButtonInput(button, action, mods);
		killsBtn.mouseButtonInput(button, action, mods);
		timeBtn.mouseButtonInput(button, action, mods);
		levelBtn.mouseButtonInput(button, action, mods);
		
		//go back to main menu
		if (back.isClicked()) {
			back.setClicked(false);
			back.setHovering(false);
			Main.GAMESTATE = GameState.MainMenu;
		}
		//show score on graph
		else if(scoreBtn.isClicked()) {
			scoreBtn.setClicked(false);
			scoreBtn.setHovering(false);
			graph.setYLabel("Score");
			graph.showGraph(attemptData, scoreData);
		}
		//show kills on graph
		else if(killsBtn.isClicked()) {
			killsBtn.setClicked(false);
			killsBtn.setHovering(false);
			graph.setYLabel("Kills");
			graph.showGraph(attemptData, killData);
		}
		//show time on graph
		else if(timeBtn.isClicked()) {
			timeBtn.setClicked(false);
			timeBtn.setHovering(false);
			graph.setYLabel("Time");
			graph.showGraph(attemptData, timeData);
		}
		//show level on graph
		else if(levelBtn.isClicked()) {
			levelBtn.setClicked(false);
			levelBtn.setHovering(false);
			graph.setYLabel("Levels Completed");
			graph.showGraph(attemptData, levelData);
		}
	}

	@Override
	public void mousePosInput(double xpos, double ypos) {
		back.mousePosInput(xpos, ypos);
		scoreBtn.mousePosInput(xpos, ypos);
		killsBtn.mousePosInput(xpos, ypos);
		timeBtn.mousePosInput(xpos, ypos);
		levelBtn.mousePosInput(xpos, ypos);
	}
	
	private void refreshStats() {
		//reset best stats
		HSCORE = 0;
		HKILLS = 0;
		HTIME = Integer.MAX_VALUE;
		HLEVEL = 0;
		
		//reset total stats
		TSCORE = 0;
		TKILLS = 0;
		TTIME = 0;
		TLEVEL = 0;
		
		//iterate through each record
		for (int i = 0; i < seedData.size(); i++) {
			int seed = seedData.get(i);
			int score = scoreData.get(i);
			int kills = killData.get(i);
			int time = timeData.get(i);
			int level = levelData.get(i);
			
			//add stats to total
			TSCORE += score;
			TKILLS += kills;
			TTIME += time;
			TLEVEL += level;
			
			//if better than best so far, set this to the best
			if (score > HSCORE) {
				HSCORE = score;
				HSCOREseed = seed;
			}
			if (kills > HKILLS) {
				HKILLS = kills;
				HKILLSseed = seed;
			}
			//for time, must ensure game was actually completed
			if (time < HTIME && level == 8) {
				HTIME = time;
				HTIMEseed = seed;
			}
			if (level > HLEVEL) {
				HLEVEL = level;
				HLEVELseed = seed;
			}
		}
		
		//if time is still default, then the game has never been completed, so set stats to 0
		if (HTIME == Integer.MAX_VALUE) {
			HTIME = 0;
			HTIMEseed = 0;
		}
		
		//convert the score seed to hex, padded with leading zeroes
		String seedText = Integer.toHexString(HSCOREseed).toUpperCase();
		seedText = ("0000000" + seedText).substring(seedText.length());
		
		//set score stats
		score.setText("Total Score: "+TSCORE+"\nHigh Score: "+HSCORE+ " (Seed: "+seedText+")");
		
		//convert the kills seed to hex, padded with leading zeroes
		seedText = Integer.toHexString(HKILLSseed).toUpperCase();
		seedText = ("0000000" + seedText).substring(seedText.length());
		
		//set kills stats
		kills.setText("Total Kills: "+TKILLS+"\nMost Kills: "+HKILLS+ " (Seed: "+seedText+")");
		
		//convert the time seed to hex, padded with leading zeroes
		seedText = Integer.toHexString(HTIMEseed).toUpperCase();
		seedText = ("0000000" + seedText).substring(seedText.length());
		
		
		String fastTimeText;
		//if game has never been completed, set seed and fastest time to N/A
		if (HTIME == 0) {
			seedText = "N/A";
			fastTimeText = "N/A";
		}
		//otherwise convert the time in seconds, to minutes and seconds for when it is displayed
		else {
			int minutes = (int)(HTIME / (60 * 1000));
			float seconds = ((float)HTIME / 1000) % 60;
			fastTimeText = String.format("%02d:%05.2f", minutes, seconds);
		}
		
		//convert the total time in seconds, to minutes and seconds for when it is displayed
		int minutes = (int)(TTIME / (60 * 1000));
		float seconds = ((float)TTIME / 1000) % 60;
		String totalTimeText = String.format("%02d:%05.2f", minutes, seconds);
		
		//set time stats
		time.setText("Total Time: "+totalTimeText+"\nFastest Time: "+fastTimeText+ " (Seed: "+seedText+")");
		
		//convert the level seed to hex, padded with leading zeroes
		seedText = Integer.toHexString(HLEVELseed).toUpperCase();
		seedText = ("0000000" + seedText).substring(seedText.length());
		
		//set level stats
		level.setText("Total Levels: "+TLEVEL+"\nMost Levels: "+HLEVEL+ " (Seed: "+seedText+")");
		
		//refresh the graph, defaulting to score on the y axis
		graph.setYLabel("Score");
		graph.showGraph(attemptData, scoreData);
	}
	
	//reset the stats for the current attempt
	public static void resetAttemptStats() {
		SCORE = KILLS = TIME = 0;
	}
	
	//save the current attempt to the database
	public void saveAttempt() {
		
		//get the values for each
		int seed = Spawner.SEED;
		int score = SCORE;
		int kills = KILLS;
		int time = TIME;
		int level = Spawner.LEVEL.ordinal();
		
		//the tutorial level does not count, so remove it
		if (level > 0) level--;
		
		//if completed, add the final level
		if (Main.GAMESTATE == GameState.GameCompleteMenu) level++;
		
		//add the record to the database
		String sql = "INSERT INTO Attempts(Seed, Score, Kills, Time, LevelCompleted) VALUES("+seed+","+score+","+kills+","+time+","+level+");";
		DatabaseManager.executeSQL(sql);
		
		//add the data from this attempt to the already loaded data
		seedData.add(seed);
		scoreData.add(score);
		killData.add(kills);
		timeData.add(time);
		levelData.add(level);
		attemptData.add(attemptData.size()+1);
		
		//refresh the stats so the current attempt is included in them
		refreshStats();
	}
	
	//load the stats from the database when the program is first launched
	private void loadStats() {
		
		//load everything from Attempts
		ResultSet rs = DatabaseManager.executeSQL("SELECT * FROM Attempts");
		try {
			while (rs.next()) {
				//data is loaded into the arraylists
				seedData.add(rs.getInt("Seed"));
				scoreData.add(rs.getInt("Score"));
				killData.add(rs.getInt("Kills"));
				timeData.add(rs.getInt("Time"));
				levelData.add(rs.getInt("LevelCompleted"));
				attemptData.add(attemptData.size()+1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
