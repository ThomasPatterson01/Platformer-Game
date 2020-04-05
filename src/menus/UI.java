package menus;

import main.GameState;
import main.Handler;
import main.Main;
import main.Spawner;
import matrix_math.Matrix4f;
import matrix_math.Vector4f;
import rendering.*;
import sprites.Background;
import sprites.Particle;
import sprites.Player;

import java.util.ArrayList;

public class UI extends Menu{
	
	private Text planetName;
	private Text score;
	private Button pause;
	private Text fpsCounter;

	private ArrayList<Line> healthOutline = new ArrayList<>();
	private Rectangle health;
	private float prevHealth = 0;
	
	//create all the necessary text/buttons as well as the background
	public UI() {
		background = new Background(707, 0, 1210, 720, Spawner.LEVEL.getBlockColor());
		
		Font textFont = new Font("Consolas", java.awt.Font.PLAIN, 20);
		planetName = new Text("PLANET_NAME", 20, Main.HEIGHT-50, textFont, new Color(1,1,1));
		score = new Text("SCORE: 00000000", 20, Main.HEIGHT-30, textFont, new Color(1,1,1));
		
		pause = new Button(Main.WIDTH - 110, Main.HEIGHT - 40, 100, 30, new Color(1,1,1));
		pause.setHoverCol(new Color(1,1,0));
		pause.setText(new Text("PAUSE", Main.WIDTH - 88, Main.HEIGHT - 38, new Font("Consolas", java.awt.Font.ITALIC, 20), new Color(0,0,0)));
		
		fpsCounter = new Text("FPS: [FPS]", 200, Main.HEIGHT-30, textFont, new Color(0.5f,0.5f,0.5f));

		float healthX = 560, healthY = Main.HEIGHT-70;
		float healthW = 800, healthH = 30;

		health = new Rectangle(healthX, healthY, healthW, healthH, new Color(0, 1, 0));
		healthOutline.add(new Line(healthX, healthY, healthX, healthY+healthH, 3, new Color(1, 1, 1)));
		healthOutline.add(new Line(healthX, healthY, healthX+healthW, healthY, 3, new Color(1, 1, 1)));
		healthOutline.add(new Line(healthX+healthW, healthY, healthX+healthW, healthY+healthH, 3, new Color(1, 1, 1)));
		healthOutline.add(new Line(healthX, healthY+healthH, healthX+healthW, healthY+healthH, 3, new Color(1, 1, 1)));
	}

	//ensure the background is updated and that the score is accurate
	@Override
	public void update(Handler handler, float delta) {
		background.update(handler, delta);
		score.setText("SCORE: " + String.format("%08d", PostLevelMenu.LEVEL_SCORE + StatsMenu.SCORE));
		fpsCounter.setText("FPS: " + Main.FPS);
		planetName.setText(Spawner.LEVEL.getPlanetName());
		background.setCol(Spawner.LEVEL.getBlockColor());

		Player p = handler.getPlayer();
		if (p == null) return;
		if (p.getHealth() == prevHealth) return;
		if (p.getHealth() < prevHealth){
			health.setWidth(800f*(p.getHealth())/p.getMaxHealth());
			Rectangle lost = new Rectangle(560+800f*(p.getHealth())/p.getMaxHealth(),  Main.HEIGHT-70, 800f*(prevHealth - p.getHealth())/p.getMaxHealth(),  30, new Color(0, 1, 0));
			Particle.dissolve(lost, (int)lost.getWidth()/5, (int)lost.getHeight()/5, 8, 8, handler);
			prevHealth = p.getHealth();
		}else{
			if (p.getHealth() - prevHealth < p.getMaxHealth()/100) {
				prevHealth = p.getHealth();
			}else{
				prevHealth += p.getMaxHealth()/100;
			}
			health.setWidth(800f*(prevHealth)/p.getMaxHealth());
		}


	}

	//draw the background, text, buttons etc
	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {
		Matrix4f view = new Matrix4f();
		renderer.setView(view);
		planetName.render(renderer);
		score.render(renderer);
		pause.render(renderer, texture);
		health.render(renderer, texture);
		for (Line l : healthOutline) l.render(renderer, texture);
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
