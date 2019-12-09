package menus;

import main.GameState;
import main.Handler;
import main.Main;
import main.Spawner;
import matrix_math.Matrix4f;
import rendering.Color;
import rendering.Font;
import rendering.Renderer;
import rendering.Text;
import rendering.Texture;
import sprites.Background;

public class UI extends Menu{
	
	private Text planetName;
	private Text score;
	private Button pause;
	private Text fpsCounter;
	
	//create all the necessary text/buttons as well as the background
	public UI() {
		background = new Background(700, 0, 1210, 720, Spawner.LEVEL.getBlockColor());
		
		Font textFont = new Font("Consolas", java.awt.Font.PLAIN, 20);
		planetName = new Text("PLANET_NAME", 20, Main.HEIGHT-50, textFont, new Color(1,1,1));
		score = new Text("SCORE: 00000000", 20, Main.HEIGHT-30, textFont, new Color(1,1,1));
		
		pause = new Button(Main.WIDTH - 110, Main.HEIGHT - 40, 100, 30, new Color(1,1,1));
		pause.setHoverCol(new Color(1,1,0));
		pause.setText(new Text("PAUSE", Main.WIDTH - 88, Main.HEIGHT - 38, new Font("Consolas", java.awt.Font.ITALIC, 20), new Color(0,0,0)));
		
		fpsCounter = new Text("FPS: [FPS]", 200, Main.HEIGHT-30, textFont, new Color(0.5f,0.5f,0.5f));
	}

	//ensure the background is updated and that the score is accurate
	@Override
	public void update(Handler handler, float delta) {
		background.update(handler, delta);
		score.setText("SCORE: " + String.format("%08d", PostLevelMenu.LEVEL_SCORE + StatsMenu.SCORE));
		fpsCounter.setText("FPS: " + Main.FPS);
		planetName.setText(Spawner.LEVEL.getPlanetName());
		background.setCol(Spawner.LEVEL.getBlockColor());
	}

	//draw the background, text, buttons etc
	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {
		Matrix4f view = new Matrix4f();
		renderer.setView(view);
		planetName.render(renderer);
		score.render(renderer);
		pause.render(renderer, texture);
		if (SettingsMenu.FPS_COUNTER) fpsCounter.render(renderer);
	}

	@Override
	public void keyInput(int key, int actions, int mods) {
		
	}

	@Override
	public void mouseButtonInput(int button, int action, int mods) {
		pause.mouseButtonInput(button, action, mods);
		
		//pause the game
		if (pause.isClicked()) {
			pause.setClicked(false);
			Main.GAMESTATE = GameState.Pause;
			pause.setHovering(false);
		}
	}

	@Override
	public void mousePosInput(double xpos, double ypos) {
		pause.mousePosInput(xpos, ypos);
	}

}
