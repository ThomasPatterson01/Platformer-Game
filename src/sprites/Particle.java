package sprites;

import main.Handler;
import rendering.Color;

public class Particle extends Sprite {

	private final float lifeLength = 120f;
	private float lifeTimer = lifeLength;
	private float accX = 0, accY = 0;
	private int waitDelay = 0;
	
	public Particle(float x, float y, float width, float height, float tx, float ty, float twidth, float theight, Color col, float velX, float velY, int waitDelay) {
		super(x, y, width, height, tx, ty, twidth, theight);
		this.col = new Color(col.getRed(), col.getGreen(), col.getBlue());
		this.velX = velX; this.velY = velY;
		this.waitDelay = waitDelay;
	}
	
	@Override
	public void update(Handler handler, float delta) {
		if (--waitDelay > 0) return;
		super.update(handler, delta);

		velX += accX * delta;
		velY += accY * delta;

		//fade out over time until it is invisible and removed
		if (--lifeTimer < 0) {
			handler.removeParticle(this);
		}
		col.setAlpha(lifeTimer/lifeLength);
	}

	public void setAcceleration(float ax, float ay){
		accX = ax;
		accY = ay;
	}
}
