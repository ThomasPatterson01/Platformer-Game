package sprites;

import main.Handler;
import rendering.Color;

public class Particle extends Sprite {

	private final float lifeLength = 120f;
	private float lifeTimer = lifeLength;
	
	public Particle(float x, float y, float width, float height, float tx, float ty, float twidth, float theight, Color col, float velX, float velY) {
		super(x, y, width, height, tx, ty, twidth, theight);
		this.col = new Color(col.getRed(), col.getGreen(), col.getBlue());
		this.velX = velX; this.velY = velY;
	}
	
	@Override
	public void update(Handler handler, float delta) {
		super.update(handler, delta);
		
		//fade out over time until it is invisible and removed
		if (--lifeTimer < 0) handler.removeParticle(this);
		col.setAlpha(lifeTimer/lifeLength);
	}
}
