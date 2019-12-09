package main;

import java.awt.geom.Rectangle2D;

import rendering.Renderer;
import rendering.Texture;

public abstract class Zone {
	protected float x, y, width, height;
	
	public Zone(float x, float y, float width, float height) {
		this.x = x; this.y = y;
		this.width = width; this.height = height;
	}

	public abstract void update(Handler handler, float delta);
	
	public void render(Renderer renderer, Texture texture, float alpha) {}
	
	public Rectangle2D.Float getHitbox() {
		return new Rectangle2D.Float(x, y, width, height);
	}
}
