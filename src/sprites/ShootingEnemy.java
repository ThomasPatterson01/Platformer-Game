package sprites;

import main.AudioPlayer;
import main.Handler;
import main.Spawner;
import matrix_math.Vector4f;
import rendering.*;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Random;

public class ShootingEnemy extends Enemy {

	private int shotInterval = 150;
	private int shotTimer = shotInterval;
	private boolean shooting = false;
	private boolean aimed = false;
	private float targetX, targetY;
	private float range = 500f;
	private Line shootingLine;
	
	private Animation shoot;
	
	public ShootingEnemy(float x, float y) {
		super(x, y, 50, 45, 404, 0, 100, 90);
		shoot = new Animation(tx, ty, twidth, theight, 0, theight, 8, 5);
		shoot.setMode(AnimationMode.REFLECT);
		col = Spawner.LEVEL.getShootingEnemyColor();
		scoreValue = 50;
		shootingLine = new Line(0,0,0,0, 1, col);
		moveSpeed = 150f;
		Random r = new Random();
		shotTimer = r.nextInt(120);
	}
	
	@Override
	public void update(Handler handler, float delta) {	
		super.update(handler,  delta);
		
		//shoot if shotTimer is low enough
		shotTimer--;
		if (shotTimer <= 0 && shooting) {
			shoot(handler);
			shotTimer = shotInterval;
			shooting = false;
			aimed = false;
			move = true;
		}else if (!shooting && shotTimer <= 40 && inRange(handler)) {
			currentAnimation = shoot;
			shooting = true;
			shotTimer = 40;
		}


		if (shooting && !aimed) {
			updateShootingLine(handler);
		}
	}
	
	private boolean inRange(Handler handler) {
		Player p = handler.getPlayer();
		if (p == null) return false;
		
		//the differences between the players centre of mass, and the centre of mass of the enemy
		float dx = p.getX() + p.getWidth()/2 - x - width/2;
		float dy = p.getY() + p.getHeight()/2 - y - height/2;
		
		//using a^2 + b^2 = c^2, check the player is within range
		if  (dx*dx + dy*dy > range*range) return false;

		//check we can see player
		float x1 = x + width / 2;
		float y1 = y + height / 2;
		float x2 = p.getX() + p.getWidth() / 2;
		float y2 = p.getY() + p.getHeight() / 2;

		float currentDist = (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);

		ArrayList<Block> blocks = handler.getBlocks();
		for (Block b : blocks){
			ArrayList<Vector4f> lines = b.getLines();
			for (Vector4f l : lines) {
				if (Line2D.linesIntersect(x1, y1, x2, y2, l.x, l.y, l.z, l.w)){
					float a1 = y2 - y1;
					float b1 = x1 - x2;
					float c1 = a1 * x1 + b1 * y1;

					float a2 = l.w - l.y;
					float b2 = l.x - l.z;
					float c2 = a2 * l.x + b2 * l.y;

					float d = a1 * b2 - a2 * b1;
					float interceptX = (b2 * c1 - b1 * c2) / d;
					float interceptY = (a1 * c2 - a2 * c1) / d;

					float dist = (interceptX-x1)*(interceptX-x1) + (interceptY-y1)*(interceptY-y1);
					if (dist < currentDist){
						return false;
					}
				}
			}
		}

		return true;
	}
	
	private void shoot(Handler handler) {
		Player p = handler.getPlayer();
		if (p == null) return;
		
		//the differences between the players centre of mass, and the centre of mass of the enemy
		float dx = targetX - x - width/2;
		float dy = targetY - y - height/2;;
		
		//the angle between the enemy and player which will be used for the bullet
		double angle = Math.atan2(dy, dx);
		//if (angle <= Math.PI/2 && angle > Math.PI/8) angle = Math.PI/8;
		//if (angle >= Math.PI/2 && angle < 7*Math.PI/8) angle = 7*Math.PI/8;
		
		//add the bullet, with an x direction of cos(angle) and a y direction of sin(angle)
		handler.addBullet(new Bullet(x+width/2, y+height/2, (float)Math.cos(angle), (float)Math.sin(angle), col));
		
		//play shooting sound effect
		AudioPlayer.playSound("shoot", 0.3f);
	}

	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {
		super.render(renderer, texture, alpha);
		if (shooting) shootingLine.render(renderer, texture);
	}

	private void updateShootingLine(Handler handler){
		Player p = handler.getPlayer();
		if (p == null) return;

		float x1 = x + width / 2;
		float y1 = y + height / 2;
		float px = p.getX() + p.getWidth() / 2;
		float py = p.getY() + p.getHeight() / 2;

		double angle = Math.atan2(py - y1, px - x1);
		float x2 = x1 + 1500 * (float) Math.cos(angle);
		float y2 = y1 + 1500 * (float) Math.sin(angle);

		float currentDist = (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);

		ArrayList<Block> blocks = handler.getBlocks();
		for (Block b : blocks){
			ArrayList<Vector4f> lines = b.getLines();
			for (Vector4f l : lines) {
				if (Line2D.linesIntersect(x1, y1, x2, y2, l.x, l.y, l.z, l.w)){
					float a1 = y2 - y1;
					float b1 = x1 - x2;
					float c1 = a1 * x1 + b1 * y1;

					float a2 = l.w - l.y;
					float b2 = l.x - l.z;
					float c2 = a2 * l.x + b2 * l.y;

					float d = a1 * b2 - a2 * b1;
					float interceptX = (b2 * c1 - b1 * c2) / d;
					float interceptY = (a1 * c2 - a2 * c1) / d;

					float dist = (interceptX-x1)*(interceptX-x1) + (interceptY-y1)*(interceptY-y1);
					if (dist < currentDist){
						currentDist = dist;
						x2 = interceptX;
						y2 = interceptY;
					}
				}
			}
		}

		targetX = x2;
		targetY = y2;
		shootingLine.setWidth(1);
		shootingLine.setCoords(x1, y1, x2, y2);

		if (shotTimer <= 20){
			aimed = true;
			move = false;
			shootingLine.setWidth(3);
		}
	}


}
