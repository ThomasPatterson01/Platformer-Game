package main;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

import matrix_math.Matrix4f;
import rendering.Renderer;
import sprites.Player;

public class Camera {
	
	private Handler handler;
	private float x, y, prevX, prevY;
	float velY, moveSpeed = 400f;
	private float maxOffset = 100;
	
	private float maxAmplitude = 0f;
	private float angle = 0f;
	private float shakeTimer = 0f;
	
	private float tiltTimer = new Random().nextFloat()*200;
	
	public Camera(Handler handler) {
		this.handler = handler;
	}
	
	//starts the shaking animation by setting the amplitude and resetting the timer
	public void shake(float amplitude) {
		AudioPlayer.playSound("groundShake", 0.1f);
		maxAmplitude = amplitude;
		shakeTimer = 0f;
	}
	
	
	public void update(float delta) {
		prevX = x; prevY = y;
		
		//get the player (and check they exist, if not return)
		Player p = handler.getPlayer();
		if (p == null) return;
		
		//set x to the x centre of the player sprite
		x = p.getX() + p.getWidth()/2;
		float py = p.getY() + p.getHeight()/2;
		
		//adjust y so that player is no more than maxOffset away from centre
		//apply velY to y, for when the player pans up and down using J/K
		y += velY * delta;
		if (py - y > maxOffset) y = py - maxOffset;
		else if (y - py > maxOffset) y = py + maxOffset;
		
		//works out what angle (in degrees) to adjust the game to, based on the planet tilt and shake
		if (Main.GAMESTATE == GameState.Game) {
			
			//shake follows the curve y=(e^-0.5)*(sin2x+cos2x) and is proportianal to the max ground tilt of the level
			//therefore the higher the max ground tilt of the planet, the more it will shake
			shakeTimer += 0.2f;
			angle = (float)((0.3f+Spawner.LEVEL.getMaxGroundTilt()/22.5)*maxAmplitude*Math.pow(Math.E, -0.5f*shakeTimer)*(Math.sin(2*shakeTimer)+Math.cos(2*shakeTimer)));
			
			//the actual tilt of the planet
			tiltTimer += 0.0003f;
			angle += 2*Spawner.LEVEL.getMaxGroundTilt()*(float)ImprovedNoise.noise(tiltTimer, 0, 0);
		}
	}
	
	//update the view matrix, so the player is in the centre, and the rotation is applied about the centre
	public void render(Renderer renderer, float alpha) {
		float lerpX = (1 - alpha) * prevX + alpha * x;
		float lerpY = (1 - alpha) * prevY + alpha * y;

		Matrix4f view = Matrix4f.translate(-lerpX + Main.WIDTH/2, -lerpY + Main.HEIGHT/2, 0).multiply(Matrix4f.translate(lerpX, lerpY, 0).multiply(Matrix4f.rotate(angle, 0, 0, 1).multiply(Matrix4f.translate(-lerpX, -lerpY, 0))));
		renderer.setView(view);
	}
	
	public void keyInput(int key, int action, int mods) {
		//move the camera up and down
		if (key == GLFW_KEY_J && action == GLFW_PRESS) {
            velY = -moveSpeed;
        } else if (key == GLFW_KEY_K && action == GLFW_PRESS) {
            velY = moveSpeed;
        } 
		
		//dont stop immediately if we are on a SlipBlock
        else if (key == GLFW_KEY_J && action == GLFW_RELEASE) {
            if (velY < 0) velY = 0;
        } else if (key == GLFW_KEY_K && action == GLFW_RELEASE) {
            if (velY > 0) velY = 0;
        } 
	}
	
	//get the rotated hitbox of the screen
	public Area getHitbox() {
		Area hitbox = new Area(new Rectangle2D.Float(x-Main.WIDTH/2, y-Main.HEIGHT/2, Main.WIDTH, Main.HEIGHT));
		AffineTransform af = new AffineTransform();
		af.rotate(-Math.toRadians(angle), x, y);
		hitbox = hitbox.createTransformedArea(af);
		return hitbox;
	}
	
	public float getY() {
		return y;
	}

	public float getX() {
		return x;
	}
}
