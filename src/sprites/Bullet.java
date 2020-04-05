package sprites;

import main.Handler;
import rendering.Color;

public class Bullet extends Sprite {
	
	private float bulletSpeed = 500f;
	private float bulletTimer = 60f*5000f/bulletSpeed; //so that the bullet will only travel 5000 pixels then despawn

	public Bullet(float x, float y, float velX, float velY, Color c) {
		super(x, y, 10, 10, 303, 0, 100, 100);
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
		//if (--lethalityTimer <= 0 && !lethal) lethal = true;
		
		//if collide with block, remove bullet
		for (Block b : handler.getBlocks()) {
			if (b.getHitbox().intersects(getHitbox())) {
				handler.removeBullet(this);
			}
		}
		
		//if collide with player, kill player and remove bullet
		Player p = handler.getPlayer();
		if (p != null && p.getHitbox().intersects(getHitbox()) && p.isAlive() && !p.isInvincible()) {
			p.takeDamage(0.2f);
			handler.removeBullet(this);
		}
		
		
		
	}

}
