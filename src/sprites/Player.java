package sprites;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.geom.Rectangle2D;
import java.util.Random;

import main.AudioPlayer;
import main.Handler;
import main.Spawner;
import rendering.Renderer;
import rendering.Texture;

public class Player extends Sprite{
	
	private float terminalVel = 1000f;
	private float moveSpeed = 500f;
	private float jumpSpeed = 1200f;
	private boolean onBlock = false;
	private boolean onSlipBlock = false;
	private boolean slowingDown = false;
	private boolean crouching = false;
	private boolean alive = true;
	private boolean waitingToStand = false;
	private float maxHealth = 1f;
	private float health = maxHealth;
	private boolean invincible = false;
	private int invincibleTimer = 0;

	public Player(float x, float y) {
		super(x, y, 50, 80, 100, 0, 100, 200);
	}
	
	//left/a = move left
	//right/d = move right
	//space/up/w = jump
	//down/s = (on ground) crouch or (in air) fall quickly
	//when releasing a key, we must check that the player is actually moving in that direction before setting the velocity to 0
	//this allows the user to input: LEFT PRESS, RIGHT PRESS, LEFT RELEASE, and still move right after releasing the left key
	//When jumping, we must check that that the player is on a block, to prevent them from jumping in mid air
	@Override
	public void keyInput(int key, int action, int mods) {
		if (!alive) return;
				
		if ((key == GLFW_KEY_LEFT || key == GLFW_KEY_A) && action == GLFW_PRESS) {
            velX = -moveSpeed;
            slowingDown = false;
        } else if ((key == GLFW_KEY_RIGHT || key == GLFW_KEY_D) && action == GLFW_PRESS) {
            velX = moveSpeed;
            slowingDown = false;
        } 
		
		//dont stop immediately if we are on a SlipBlock
        else if ((key == GLFW_KEY_LEFT || key == GLFW_KEY_A) && action == GLFW_RELEASE) {
            if (velX < 0) { 
            	if (onSlipBlock) slowingDown = true;
            	else velX = 0;
            }
        } else if ((key == GLFW_KEY_RIGHT || key == GLFW_KEY_D) && action == GLFW_RELEASE) {
            if (velX > 0){ 
            	if (onSlipBlock) slowingDown = true;
            	else velX = 0;
            }
        } 
		//can only jump if we are actually on a block
        else if ((key == GLFW_KEY_SPACE || key == GLFW_KEY_UP || key == GLFW_KEY_W) && action == GLFW_PRESS) {
            if (onBlock) velY = jumpSpeed;
        }
		
		
        else if ((key == GLFW_KEY_DOWN || key == GLFW_KEY_S) && action == GLFW_PRESS) {
        	if (onBlock && !crouching) { 
        		height*=0.5f;
        		crouching = true;
        	}
        	else velY = -terminalVel;
		} else if ((key == GLFW_KEY_DOWN || key == GLFW_KEY_S) && action == GLFW_RELEASE) {
			if (crouching) { 
				waitingToStand = true;
			}
			else if (velY < 0) velY = -0.3f*terminalVel;
		}
	}
	
	@Override
	public void update(Handler handler, float delta) {		
		prevX = x; prevY = y;
		
		//if the player has not reached terminal velocity (and they're not on the ground), then add gravity
		//else, ensure the y velocity is exactly terminal
		if (-velY < terminalVel && !onBlock) velY -= 4 * Spawner.LEVEL.getGravity() * delta;
		
		//if dead, dont bother checking collisions, just fall
		if (!alive) {
			explode(handler);
			handler.setPlayer(null);
			return;
		}

		if (invincible){
			invincibleTimer--;
			if ((invincibleTimer/5) % 2 == 0) col.setAlpha(0.2f);
			else col.setAlpha(1f);
			if (invincibleTimer <= 0) {
				invincible = false;
				col.setAlpha(1f);
			}
		}
		
		//*****PLAYER/BLOCK COLLISIONS*****//
		//x and y collisions are detected and resolved independently by:
		//update x
		//check for x collisions
		//update y
		//check for y collisions
		
		//update x
		x += velX*delta;
		//check for and resolve collisions in x direction
		for (Block b : handler.getBlocks()) {
			if (b.getHitbox().intersects(getHitbox())){
				if (velX < 0) x = b.getX() + b.getWidth();
				if (velX > 0) x = b.getX() - width;
				break;
			}
		}
		
		//update y
		y += velY*delta;
		//check for and resolve collisions in y direction
		//if hit a block moving upwards, bounce off the block by reversing y
		for (Block b : handler.getBlocks()) {
			if (b.getHitbox().intersects(getHitbox())){
				if (velY < 0) {
					y = b.getY() + b.getHeight();
					if (-velY >= terminalVel) handler.getCamera().shake(1f);
					velY = 0;
				}
				if (velY > 0) {
					y = b.getY() - height;
					velY = 0;
				}
				break;
			}
		}
		
		//check for player/enemy collisions
		//if player moving down, the sprite dies.
		//else the player dies
		for (Enemy e : handler.getEnemies()) {
			if (!e.isAlive()) continue;
			if (e.getHitbox().intersects(getHitbox())) {
				if (velY < 0) {
					e.takeDamage(handler);
					float shakeAmplitude = -velY >= terminalVel ? 3f : 1f;
					handler.getCamera().shake(shakeAmplitude);
					velY = jumpSpeed;
					y = e.getY() + e.getHeight() + 5;
					return;
				} else {
					if (!invincible) {
						takeDamage(0.3f);
					}
				}
			}
		}
				
		//checks to see if the player is ontop of a block or not (within 10 pixels of the top of the block), and whether that block is a SlipBlock
		onBlock = false;
		onSlipBlock = false;
		for (Block b : handler.getBlocks()) {
			if (b.getHitbox().intersects(getOnBlockHitbox())){
				onBlock = true;
				if (b instanceof SlipBlock) onSlipBlock = true;
			}
		}
		
		//for skidding to a stop on a SlipBlock
		if (slowingDown) {
			//if not actually on a SlipBlock, then stop sliding
			if (!onSlipBlock) { 
				slowingDown = false;
				velX = 0f;
				return;
			}
			
			//moves velX closer to 0 by 30 pixels each time
			velX = velX - (velX/Math.abs(velX))*30;
			//if velX is within 30 of 0, stop
			if (Math.abs(velX) <= 30) {
				velX = 0;
				slowingDown = false;
			}
		}
		
		//slow down player when they move off a slipBlock
		if (!onSlipBlock && onBlock && velX != 0 && alive) {
			if (velX > 0) velX = moveSpeed;
			else velX = -moveSpeed;
		}
		
		//speed up player when they move onto a slipBlock
		if (onSlipBlock && velX != 0 && !slowingDown && alive) {
			if (velX > 0) velX = moveSpeed*1.35f;
			else velX = -moveSpeed*1.35f;
		}
		
		if (waitingToStand) {
			Rectangle2D.Float hb = new Rectangle2D.Float(x, y+1, width, height*2);
			boolean wouldFit = true;
			for (Block b : handler.getBlocks()) {
				if (b.getHitbox().intersects(hb)){
					wouldFit = false;
				}
			}
			
			if (wouldFit) {
				height *= 2f;
				crouching = false;
				waitingToStand = false;
			}
		}
	}

	public void die() {
		AudioPlayer.playSound("playerDeath", 1.0f);
		alive = false;
		health = 0;
		velX = 0;
		velY = 0;
	}

	public void takeDamage(float dmg){
		if (invincible) return;
		health -= dmg;
		if (health <= 0.0001f){
			die();
			health = 0;
		}
		invincibleTimer = 60;
		invincible = true;
	}
	
	//split the sprite into a 10x20 grid of particles, each with a corresponding texture region
	//the particles then move away from the centre of the sprite, fading over time
	private void explode(Handler handler) {
		int numPx = 10;
		int numPy = 20;
		
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
				
				handler.addParticle(new Particle(px, py, pwidth, pheight, ptx, pty, ptwidth, ptheight, col, pvelX*5, pvelY*5, 0));
			}
		}
	}
	
	//the 5 pixel depth directly below the player, used to check if the player is on top of a block or not
	public Rectangle2D.Float getOnBlockHitbox(){
		if (velX > 0) return new Rectangle2D.Float(x-10, y-5, width+10, 5);
		else if (velX < 0) return new Rectangle2D.Float(x, y-5, width+10, 5);
		return new Rectangle2D.Float(x, y-5, width, 5);
	}
	
	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public float getHeight() {
		if (crouching) return 2f*height;
		
		return height;
	}

	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public float getMaxHealth() {
		return maxHealth;
	}

	public boolean isInvincible(){
		return invincible;
	}
}
