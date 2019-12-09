package sprites;

import java.awt.geom.Rectangle2D;
import java.util.Random;

import main.AudioPlayer;
import main.Handler;
import main.Spawner;
import menus.PostLevelMenu;
import rendering.Color;
import rendering.FadingText;
import rendering.Font;
import rendering.Text;

public class Enemy extends Sprite {
	
	private float terminalVel = 1000f;
	private float moveSpeed = 100f;
	private boolean onBlock = false;
	private boolean alive = true;
	protected int scoreValue = 25;

	public Enemy(float x, float y) {
		super(x, y, 50, 45, 200, 0, 100, 90);
		col = Spawner.LEVEL.getEnemyColor();
		velX = moveSpeed;
	}
	
	public Enemy(float x, float y, float width, float height, float tx, float ty, float twidth, float theight) {
		super(x, y, width, height, tx, ty, twidth, theight);
		col = new Color(0f, 1f, 0f);
		velX = moveSpeed;
	}

	@Override
	public void update(Handler handler, float delta) {		
		prevX = x; prevY = y;
		
		if (currentAnimation != null) {
			if (!currentAnimation.update(delta)){
				currentAnimation = null;
			}
		}
		
		//if dead, dont move, just disappear after deathTimer runs out
		if (!alive) {
			handler.removeEnemy(this);
			explode(handler);
			return;
		}
		
		//rebound off an edge, or stop if there is an edge on either side of the block
		//only check when not airborne
		if (onBlock) {
			switch(onEdge(handler)) {
			case 0:
				break;
			case 1:
				velX = 0;
				break;
			case 2:
				velX = moveSpeed;
				break;
			case 3:
				velX = -moveSpeed;
				break;
			}
		}
		
		//if the enemy has not reached terminal velocity (and they're not on the ground), then add gravity
		//else, ensure the y velocity is exactly terminal
		if (-velY < terminalVel && !onBlock) velY -= Spawner.LEVEL.getGravity() * delta;
		
		//*****ENEMY/BLOCK COLLISIONS*****//
		//x and y collisions are detected and resolved independently by:
		//update y
		//check for y collisions, storing the resolved y (what it will become after the update)
		//revert to original y
		//repeat for x
		//then update both x and y to the resolved values
		
		//update y
		y += velY*delta;
		float newY = y; //what the y will become
		//check for and resolve collisions in y direction
		//if hit a block moving upwards, bounce off the block by reversing y
		for (Block b : handler.getBlocks()) {
			if (b.getHitbox().intersects(getHitbox())){
				if (velY < 0) {
					newY = b.getY() + b.getHeight();
					velY = 0;
				}
				if (velY > 0) {
					newY = b.getY() - height;
					velY = 0;
				}
				break;
			}
		}
		//reset y
		y = prevY;
		
		//update x
		x += velX*delta;
		float newX = x; //what the x will become
		//check for and resolve collisions in x direction
		//if hit a block in x direction, turn around
		for (Block b : handler.getBlocks()) {
			if (b.getHitbox().intersects(getHitbox())){
				if (velX < 0) newX = b.getX() + b.getWidth();
				if (velX > 0) newX = b.getX() - width;
				velX *= -1;
				break;
			}
		}
		//reset x
		x = prevX;
		
		//update x and y
		x = newX;
		y = newY;		
		
		//checks to see if the enemy is ontop of a block or not (within 10 pixels of the top of the block)
		onBlock = false;
		for (Block b : handler.getBlocks()) {
			if (b.getHitbox().intersects(getOnBlockHitbox())){
				onBlock = true;
			}
		}
	}
	
	//0 = no edges
	//1 = edge on both sides
	//2 = edge on left
	//3 = edge on right
	private int onEdge(Handler handler) {
		
		//count number of intersected blocks
		//if more than 2, then no change is needed
		int count = 0;
		Rectangle2D.Float hitbox = getOnBlockHitbox();
		for (Block b : handler.getBlocks()) {
			if (hitbox.intersects(b.getHitbox())) count++;
		}
		if (count >= 2) return 0;
		
		
		boolean leftEdge = false, rightEdge = false;
		
		//if enemy were 5 pixels left, count the number of blocks again
		//if less than 2, there must be an edge on the left
		count = 0;
		hitbox = moveRect(hitbox, -5f, 0);
		for (Block b : handler.getBlocks()) {
			if (hitbox.intersects(b.getHitbox())) count++;
		}
		if (count <= 1) leftEdge = true;
		
		//if enemy were 5 pixels right, count the number of blocks again
		//if less than 2, there must be an edge on the right
		count = 0;
		hitbox = moveRect(hitbox, 10f, 0);
		for (Block b : handler.getBlocks()) {
			if (hitbox.intersects(b.getHitbox())) count++;
		}
		if (count <= 1) rightEdge = true;
		
		//determine the result based on which sides have edges
		if (leftEdge && rightEdge) return 1;
		if (!leftEdge && rightEdge) return 3;
		if (leftEdge && !rightEdge) return 2;
		else return 0;
	}
	
	//returns a translated rectangle
	private Rectangle2D.Float moveRect(Rectangle2D.Float rect, float x, float y){
		return new Rectangle2D.Float((float)(rect.getX() + x), (float)(rect.getY() + y), (float)rect.getWidth(), (float)rect.getHeight());
	}
	
	public void die(Handler handler) {
		AudioPlayer.playSound("enemyDeath", 0.5f);
		PostLevelMenu.LEVEL_SCORE += scoreValue;
		PostLevelMenu.LEVEL_KILLS++;
		alive = false;
		
		Font textFont = new Font("Consolas", java.awt.Font.BOLD, 30);
		Text planetName = new Text("+" + Integer.toString(scoreValue), x-5, y, textFont, new Color(col.getRed(), col.getGreen(), col.getBlue()));
		handler.addFadingText(new FadingText(planetName, 0, 60, 0.75f, 0, true));
	}
	
	//split the sprite into a 10x10 grid of particles, each with a corresponding texture region
	//the particles then move away from the centre of the sprite, fading over time
	private void explode(Handler handler) {
		int numPx = 10;
		int numPy = 10;
		
		Random r = new Random();
		for (int i = 0; i < numPx; i++) {
			for (int j = 0; j < numPy; j++) {
				
				//location and dimensions of particle
				float pwidth = width/numPx;
				float pheight = height/numPy;
				float px = x + i*pwidth;
				float py = y + j*pheight;
				
				//location and dimensions of texture region
				float ptwidth = twidth/numPx;
				float ptheight = theight/numPy;
				float ptx = tx + i*ptwidth;
				float pty = ty + (numPy-1-j)*ptheight;
				
				//velocity of particle
				float pvelX = px - (x+width/2) + r.nextFloat()*8-4;
				float pvelY = py - (y+height/2) + r.nextFloat()*8-4;
				
				handler.addParticle(new Particle(px, py, pwidth, pheight, ptx, pty, ptwidth, ptheight, col, pvelX*5, pvelY*5));
			}
		}
	}
	
	//the 2 pixel depth directly below the enemy, used to check if the enemy is on top of a block or not
	private Rectangle2D.Float getOnBlockHitbox(){
		return new Rectangle2D.Float(x, y-10, width, 11);
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
