package menus;

import java.sql.ResultSet;
import java.sql.SQLException;

import main.AudioPlayer;
import main.DatabaseManager;
import main.GameState;
import main.Handler;
import main.Main;
import rendering.Color;
import rendering.Font;
import rendering.Renderer;
import rendering.Text;
import rendering.Texture;
import rendering.Window;
import sprites.Background;

public class SettingsMenu extends Menu{
	public static boolean VSYNC;
	public static boolean FPS_COUNTER;
	public static boolean TUTORIAL;
	public static int MUSIC_VOLUME;
	public static int SOUND_VOLUME;
	
	private Text title;
	private Text vsyncText;
	private Text fpsText;
	private Text tutText;
	private Text musicVolText;
	private Text soundVolText;
	
	private Button vsyncBtn;
	private Button fpsBtn;
	private Button tutBtn;
	private Button musicUp;
	private Button musicDown;
	private Button soundUp;
	private Button soundDown;
	private Button back;
	
	private Font textFont;
	
	Window window;
	
	//create all the necessary text/buttons as well as the background
	public SettingsMenu(Window window) {
		this.window = window;
		background = new Background(3200, 0, 1220, 720, new Color(1,1,1));
		
		loadSettings();
		
		Font titleFont = new Font("Consolas", java.awt.Font.PLAIN, 75);
		title = new Text("SETTINGS", 490, 630, titleFont, new Color(1,1,1));
		
		textFont = new Font("Consolas", java.awt.Font.PLAIN, 50);
		vsyncText = new Text("Vsync is " + (VSYNC ? "enabled" : "disabled"), 100, 530, textFont, new Color(1,1,1));
		
		Font buttonFont = new Font("Consolas", java.awt.Font.PLAIN, 40);
		vsyncBtn = new Button(910, 530, 340, 60, new Color(1,1,1));
		vsyncBtn.setHoverCol(new Color(1,1,0));
		vsyncBtn.setText(new Text("Toggle Vsync", 950, 535, buttonFont, new Color(0,0,0)));
		
		fpsText = new Text("FPS Counter is " + (FPS_COUNTER ? "enabled" : "disabled"), 100, 430, textFont, new Color(1,1,1));
		
		fpsBtn = new Button(810, 430, 440, 60, new Color(1,1,1));
		fpsBtn.setHoverCol(new Color(1,1,0));
		fpsBtn.setText(new Text("Toggle FPS Counter", 830, 435, buttonFont, new Color(0,0,0)));
		
		tutText = new Text("Tutorial is " + (TUTORIAL ? "enabled" : "disabled"), 100, 330, textFont, new Color(1,1,1));
		
		tutBtn = new Button(880, 330, 370, 60, new Color(1,1,1));
		tutBtn.setHoverCol(new Color(1,1,0));
		tutBtn.setText(new Text("Toggle Tutorial", 900, 335, buttonFont, new Color(0,0,0)));
		
		Font volumeButtonFont = new Font("Consolas", java.awt.Font.BOLD, 50);
		musicVolText = new Text("Music Volume is " + MUSIC_VOLUME, 100, 230, textFont, new Color(1,1,1));
		
		musicDown = new Button(620, 230, 40, 60, new Color(1,1,1));
		musicDown.setHoverCol(new Color(1,1,0));
		musicDown.setText(new Text("-", 627, 230, volumeButtonFont, new Color(0,0,0)));
		
		musicUp = new Button(690, 230, 40, 60, new Color(1,1,1));
		musicUp.setHoverCol(new Color(1,1,0));
		musicUp.setText(new Text("+", 697, 230, volumeButtonFont, new Color(0,0,0)));
		
		soundVolText = new Text("Sound Volume is " + SOUND_VOLUME, 100, 130, textFont, new Color(1,1,1));
		
		soundDown = new Button(620, 130, 40, 60, new Color(1,1,1));
		soundDown.setHoverCol(new Color(1,1,0));
		soundDown.setText(new Text("-", 627, 130, volumeButtonFont, new Color(0,0,0)));
		
		soundUp = new Button(690, 130, 40, 60, new Color(1,1,1));
		soundUp.setHoverCol(new Color(1,1,0));
		soundUp.setText(new Text("+", 697, 130, volumeButtonFont, new Color(0,0,0)));
		
		Font backButtonFont = new Font("Consolas", java.awt.Font.PLAIN, 30);
		back = new Button(900, 50, 300, 100, new Color(1,1,1));
		back.setHoverCol(new Color(1,1,0));
		back.setText(new Text("Save and return \n to Main Menu", 925, 60, backButtonFont, new Color(0,0,0)));
	}

	@Override
	public void update(Handler handler, float delta) {
		
	}

	//draw the background, text, buttons etc
	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {
		renderBackground(renderer, texture, alpha);
		
		title.render(renderer);
		vsyncText.render(renderer);
		fpsText.render(renderer);
		tutText.render(renderer);
		musicVolText.render(renderer);
		soundVolText.render(renderer);
		
		vsyncBtn.render(renderer, texture);
		fpsBtn.render(renderer, texture);
		tutBtn.render(renderer, texture);
		musicDown.render(renderer, texture);
		musicUp.render(renderer, texture);
		soundDown.render(renderer, texture);
		soundUp.render(renderer, texture);
		back.render(renderer, texture);
	}

	@Override
	public void keyInput(int key, int actions, int mods) {
		
	}

	@Override
	public void mouseButtonInput(int button, int action, int mods) {
		vsyncBtn.mouseButtonInput(button, action, mods);
		fpsBtn.mouseButtonInput(button, action, mods);
		tutBtn.mouseButtonInput(button, action, mods);
		musicDown.mouseButtonInput(button, action, mods);
		musicUp.mouseButtonInput(button, action, mods);
		soundDown.mouseButtonInput(button, action, mods);
		soundUp.mouseButtonInput(button, action, mods);
		back.mouseButtonInput(button, action, mods);
		
		
		//toggle vysnc and update corresponding text
		if (vsyncBtn.isClicked()) {
			vsyncBtn.setClicked(false);
			VSYNC = !VSYNC;
			vsyncText = new Text("Vsync is " + (VSYNC ? "enabled" : "disabled"), 100, 530, textFont, new Color(1,1,1));
			vsyncBtn.setHovering(false);
		}
		
		//toggle fps counter and update corresponding text
		else if (fpsBtn.isClicked()) {
			fpsBtn.setClicked(false);
			FPS_COUNTER = !FPS_COUNTER;
			fpsText = new Text("FPS Counter is " + (FPS_COUNTER ? "enabled" : "disabled"), 100, 430, textFont, new Color(1,1,1));
			fpsBtn.setHovering(false);
		}
		
		//toggle tutorial and update corresponding text
		else if (tutBtn.isClicked()) {
			tutBtn.setClicked(false);
			TUTORIAL = !TUTORIAL;
			tutText = new Text("Tutorial is " + (TUTORIAL ? "enabled" : "disabled"), 100, 330, textFont, new Color(1,1,1));
			tutBtn.setHovering(false);
		}
		
		//unless music volume is already min, decrease it
		else if (musicDown.isClicked()) {
			musicDown.setClicked(false);
			if (MUSIC_VOLUME > 0) MUSIC_VOLUME--;
			musicVolText = new Text("Music Volume is " + MUSIC_VOLUME, 100, 230, textFont, new Color(1,1,1));
		}
		
		//unless music volume is already max, increase it
		else if (musicUp.isClicked()) {
			musicUp.setClicked(false);
			if (MUSIC_VOLUME < 100) MUSIC_VOLUME++;
			musicVolText = new Text("Music Volume is " + MUSIC_VOLUME, 100, 230, textFont, new Color(1,1,1));
		}
		
		//unless sound volume is already min, decrease it
		else if (soundDown.isClicked()) {
			soundDown.setClicked(false);
			if (SOUND_VOLUME > 0) SOUND_VOLUME--;
			soundVolText = new Text("Sound Volume is " + SOUND_VOLUME, 100, 130, textFont, new Color(1,1,1));
		}
		
		//unless sound volume is already max, increase it
		else if (soundUp.isClicked()) {
			soundUp.setClicked(false);
			if (SOUND_VOLUME < 100) SOUND_VOLUME++;
			soundVolText = new Text("Sound Volume is " + SOUND_VOLUME, 100, 130, textFont, new Color(1,1,1));
		}
		
		//save settings and go back to main menu
		else if (back.isClicked()) {
			back.setClicked(false);
			Main.GAMESTATE = GameState.MainMenu;
			saveSettings();
			back.setHovering(false);
		}
	}

	@Override
	public void mousePosInput(double xpos, double ypos) {
		vsyncBtn.mousePosInput(xpos, ypos);
		fpsBtn.mousePosInput(xpos, ypos);
		tutBtn.mousePosInput(xpos, ypos);
		musicDown.mousePosInput(xpos, ypos);
		musicUp.mousePosInput(xpos, ypos);
		soundDown.mousePosInput(xpos, ypos);
		soundUp.mousePosInput(xpos, ypos);
		back.mousePosInput(xpos, ypos);
	}
	
	//load settings from database into static variables
	//either turns vsync on or off
	private void loadSettings() {
		ResultSet rs = DatabaseManager.executeSQL("SELECT * FROM Settings");
		try {
			while (rs.next()) {
				switch(rs.getString("Name")) {
				case "vsync":
					VSYNC = rs.getInt("Value") == 1;
					window.setVsync(VSYNC);
					break;
				case "fps_counter":
					FPS_COUNTER = rs.getInt("Value") == 1;
					break;
				case "tutorial":
					TUTORIAL = rs.getInt("Value") == 1;
					break;
				case "music_volume":
					MUSIC_VOLUME = rs.getInt("Value");
					break;
				case "sound_volume":
					SOUND_VOLUME = rs.getInt("Value");
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//save the settings to the database
	private void saveSettings() {
		
		//convert booleans to ints
		int vsync = VSYNC ? 1 : 0;
		int fps_counter = FPS_COUNTER ? 1 : 0;
		int tutorial = TUTORIAL ? 1 : 0;
		
		//update values in database
		DatabaseManager.executeSQL("UPDATE Settings SET Value="+vsync+" WHERE Name='vsync'");
		DatabaseManager.executeSQL("UPDATE Settings SET Value="+fps_counter+" WHERE Name='fps_counter'");
		DatabaseManager.executeSQL("UPDATE Settings SET Value="+tutorial+" WHERE Name='tutorial'");
		DatabaseManager.executeSQL("UPDATE Settings SET Value="+MUSIC_VOLUME+" WHERE Name='music_volume'");
		DatabaseManager.executeSQL("UPDATE Settings SET Value="+SOUND_VOLUME+" WHERE Name='sound_volume'");
		
		//update vsync and music volume
		window.setVsync(VSYNC);
		AudioPlayer.refreshMusicVolume();
	}
	
	
}
