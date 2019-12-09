package sprites;

import main.AudioPlayer;
import main.Handler;
import main.Spawner;
import rendering.Animation;
import rendering.AnimationMode;

public class ShootingEnemy extends Enemy {

	private int shotInterval = 120;
	private int shotTimer = shotInterval;
	private float range = 400f;
	
	private Animation shoot;
	
	public ShootingEnemy(float x, float y) {
		super(x, y, 50, 45, 400, 0, 100, 90);
		shoot = new Animation(tx, ty, twidth, theight, 0, theight, 8, 5);
		shoot.setMode(AnimationMode.REFLECT);
		col = Spawner.LEVEL.getShootingEnemyColor();
		scoreValue = 50;
	}
	
	@Override
	public void update(Handler handler, float delta) {	
		super.update(handler,  delta);
		
		
		//shoot if shotTimer is low enough
		shotTimer--;
		if (shotTimer <= 0 && inRange(handler)) {
			shoot(handler);
			shotTimer = shotInterval;
		}else if (shotTimer <= 40) {
			currentAnimation = shoot;
		}
	}
	
	private boolean inRange(Handler handler) {
		Player p = handler.getPlayer();
		if (p == null) return false;
		
		//the differences between the players centre of mass, and the centre of mass of the enemy
		float dx = p.getX() + p.getWidth()/2 - x - width/2;
		float dy = p.getY() + p.getHeight()/2 - y - height/2;
		
		//using a^2 + b^2 = c^2, check the player is within range
		return (dx*dx + dy*dy < range*range);
	}
	
	private void shoot(Handler handler) {
		Player p = handler.getPlayer();
		if (p == null) return;
		
		//the differences between the players centre of mass, and the centre of mass of the enemy
		float dx = p.getX() + p.getWidth()/2 - x - width/2;
		float dy = p.getY() + p.getHeight()/2 - y - height/2;
		
		//the angle between the enemy and player which will be used for the bullet
		double angle = Math.atan2(dy, dx);
		//if (angle <= Math.PI/2 && angle > Math.PI/8) angle = Math.PI/8;
		//if (angle >= Math.PI/2 && angle < 7*Math.PI/8) angle = 7*Math.PI/8;
		
		//add the bullet, with an x direction of cos(angle) and a y direction of sin(angle)
		handler.addBullet(new Bullet(x+width/2, y+height/2, (float)Math.cos(angle), (float)Math.sin(angle), col));
		
		//play shooting sound effect
		AudioPlayer.playSound("shoot", 0.3f);
	}

}
