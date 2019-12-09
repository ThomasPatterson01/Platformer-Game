package menus;

import main.Handler;
import rendering.Renderer;
import rendering.Texture;
import sprites.Background;

public abstract class Menu {
	protected Background background;
	
	public abstract void update(Handler handler, float delta);
	public abstract void render(Renderer renderer, Texture texture, float alpha);
	public void renderBackground(Renderer renderer, Texture texture, float alpha) { background.render(renderer, texture, alpha); }
	public abstract void keyInput(int key, int actions, int mods);
	public abstract void mouseButtonInput(int button, int action, int mods);
	public abstract void mousePosInput(double xpos, double ypos);
}
