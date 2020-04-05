package sprites;

import java.util.ArrayList;

import main.Handler;
import matrix_math.Matrix4f;
import rendering.Color;
import rendering.Renderer;
import rendering.Texture;

public class ShootingStar extends Sprite{
	
	private ArrayList<StarTrail> trail = new ArrayList<>();
	
	private float angle = 0;

	public ShootingStar(float x, float y, float velX, float velY) {
		super(x, y, 25, 25, 0, 0, 0, 0);
		this.velX = velX;
		this.velY = velY;
	}
	
	@Override
	public void update(Handler handler, float delta) {
		super.update(handler, delta);
		
		//rotate slightly
		angle++;
		
		//add another trail element
		trail.add(new StarTrail(x, y, angle));
	
		//update the trails
		for (int i = trail.size()-1; i > -1; i--) {
			trail.get(i).update(handler, delta);
		}
		
		//if over 1200 pixels from player, its gone off the screen so remove it
		float dx = 640 - x;
		float dy = 360 - y;
		if (dx*dx + dy*dy > 1200*1200) {
			handler.removeShootingStar(this);
		}
	}
	
	//render all the trails
	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {		
		for (int i = trail.size()-1; i > -1; i--) {
			trail.get(i).render(renderer, texture, alpha);
		}
	}
	
	private class StarTrail extends Sprite{
		
		private float size = 15;
		private float angle;

		public StarTrail(float x, float y, float angle) {
			super(x, y, 25, 25, 626, 20, 1, 1);
			//Color c = Spawner.LEVEL.getBlockColor();
			//col = new Color(c.getRed(), c.getGreen(), c.getBlue());
			col = new Color(1,1,0);
			this.angle = angle;
		}
		
		@Override
		public void update(Handler handler, float delta) {
			super.update(handler, delta);
			
			//get more transparent and small over time
			col.setAlpha(size/25);
			size -= 0.25f;
			x += 0.125f;
			y += 0.125f;
			
			if (size <= 0) {
				trail.remove(this);
			}
		}
		
		@Override
		public void render(Renderer renderer, Texture texture, float alpha) {
			float lerpX = (1 - alpha) * prevX + alpha * x;
			float lerpY = (1 - alpha) * prevY + alpha * y;
			
			//rotate the trail to the right angle
			Matrix4f model = Matrix4f.translate(lerpX, lerpY, 0).multiply(Matrix4f.rotate(angle, 0, 0, 1).multiply(Matrix4f.translate(-lerpX, -lerpY, 0)));
			renderer.setModel(model);
						
			//for now, rendered in its own batch.
			renderer.begin();
			texture.bind();
			renderer.drawTextureRegion(texture, lerpX, lerpY, size, size, tx, ty, twidth, theight, col, true);
			renderer.end();
			
			renderer.setModel(new Matrix4f());
			
		}
		
		
	}
}
