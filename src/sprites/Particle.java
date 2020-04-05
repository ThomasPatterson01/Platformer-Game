package sprites;

import main.Handler;
import rendering.Color;
import rendering.Rectangle;

import java.util.Random;

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

	public static void explode(Sprite s, int numPx, int numPy, float speedX, float speedY, float accX, float accY, Handler handler){

		Random r = new Random();
		for (int i = 0; i < numPx; i++) {
			for (int j = 0; j < numPy; j++) {

				//location and dimensions of particle
				float pwidth = s.getWidth()/numPx;
				float pheight = s.getHeight()/numPy;
				float px = s.getX() + i*pwidth;
				float py = s.getY() + j*pheight;

				//location and dimensions of texture region
				float ptwidth = s.getTwidth()/numPx;
				float ptheight = s.getTheight()/numPy;
				float ptx = s.getTx() + i*ptwidth;
				float pty = s.getTy() + (numPy-1-j)*ptheight;

				//velocity of particle
				float pvelX = px - (s.getX()+s.getWidth()/2) + r.nextFloat()*8-4;
				float pvelY = py - (s.getY()+s.getHeight()/2) + r.nextFloat()*8-4;

				Particle p = new Particle(px, py, pwidth, pheight, ptx, pty, ptwidth, ptheight, s.getCol(), pvelX*speedX, pvelY*speedY, 0);
				p.setAcceleration(accX, accY);
				handler.addParticle(p);
			}
		}
	}

	public static void dissolve(Rectangle r, int numPx, int numPy, float speedX, float speedY, Handler handler) {

		Random ra = new Random();
		for (int i = (int)r.getX(); i < r.getX()+r.getWidth(); i+=5) {
			for (int j = (int)r.getY(); j < r.getY()+r.getHeight(); j+=5) {

				//location and dimensions of particle
				float pwidth = r.getWidth()/numPx;
				float pheight = r.getHeight()/numPy;

				//velocity of particle
				float pvelX = ra.nextFloat()*20-10;
				float pvelY = j - (r.getY()+r.getHeight()/2) + ra.nextFloat()*8-4;

				Particle p = new Particle(i, j, pwidth, pheight, 626, 20, 1, 1, r.getCol(), pvelX*speedX, pvelY*speedY, (int)r.getWidth()/20-(int)((i-r.getX())/20));
				p.setAcceleration(0, -300);
				p.setFixedScreenLocation(true);
				handler.addParticle(p);
			}
		}
	}
}
