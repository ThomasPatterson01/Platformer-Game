package sprites;

import main.Handler;
import rendering.Color;

public class Bullet extends Sprite {
	
	private float bulletSpeed = 150f;
	private float bulletTimer = 60f*400f/bulletSpeed; //so that the bullet will only travel 400 pixeks
	private boolean lethal = false; //not lethal for first half second to make it fair
	private int lethalityTimer = 30;
	
	public Bullet(float x, float y, float velX, float velY, Color c) {
		super(x, y, 10, 10, 300, 0, 100, 100);
		col = c;
		
		//velX and velY are passed in as values between 0 and 1, so we must multiply by the desired speed of the bullet
		this.velX = velX*bulletSpeed;
		this.velY = velY*bulletSpeed;
	}
	
	@Override
	public void update(Handler handler, float delta) {
		super.update(handler, delta);
		
		//if the bullet timer runs out, remove the bullet
		if (--bulletTimer <= 0) {
			handler.removeBullet(this);
			return;
		}
		
		//make the bullet lethal after a second
		if (--lethalityTimer <= 0 && !lethal) lethal = true;
		
		//if collide with block, remove bullet
		for (Block b : handler.getBlocks()) {
			if (b.getHitbox().intersects(getHitbox())) {
				handler.removeBullet(this);
			}
		}
		
		//if collide with player, kill player and remove bullet
		if (lethal) { 
			Player p = handler.getPlayer();
			if (p != null && p.getHitbox().intersects(getHitbox()) && p.isAlive()) {
				p.die();
				handler.removeBullet(this);
			}
		}
		
		
		
	}

}
