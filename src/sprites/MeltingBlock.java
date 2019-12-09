package sprites;

import main.Handler;
import main.Spawner;
import matrix_math.Matrix4f;
import rendering.Color;
import rendering.Renderer;
import rendering.Texture;

public class MeltingBlock extends Block{

	private boolean melting = false;
	private int meltTimer = 30;
	private boolean melted = false;
	private float terminalVel = 1000f;
	private float fallAngle = 0;
	private float fallAngleVelocity;
	
	public MeltingBlock(float x, float y, float fallAngleVelocity) {
		super(x, y);
		this.fallAngleVelocity = fallAngleVelocity;
	}
	
	public void update(Handler handler, float delta) {
		super.update(handler, delta);
		
		//if melted, and terminal velocity not reached, apply gravity
		if (-velY < terminalVel && melted) velY -= Spawner.LEVEL.getGravity() * delta;
			
		if (melted) fallAngle += fallAngleVelocity;
		
		if (melting){
			//if meltTimer <= 0 then the block has melted
			if (--meltTimer <= 0) {
				melted = true;
			}
			return;
		}
		
		//if collide with player, then the block is melting
		Player p = handler.getPlayer();
		if (p != null && p.getOnBlockHitbox().intersects(getHitbox()))
			melting = true;
		
		
	}
	
	//need to override the render function in order to add an extra texture region
	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {
		
		
		float lerpX = (1 - alpha) * prevX + alpha * x;
		float lerpY = (1 - alpha) * prevY + alpha * y;
		
		//rotate the block as it falls
		Matrix4f model = Matrix4f.translate(lerpX+width, lerpY+height, 0).multiply(Matrix4f.rotate(fallAngle, 0, 0, 1).multiply(Matrix4f.translate(-lerpX-width, -lerpY-height, 0)));
		renderer.setModel(model);
		
		//for now, rendered in its own batch.
		renderer.begin();
		texture.bind();
		renderer.drawTextureRegion(texture, lerpX, lerpY, width, height, tx, ty, twidth, theight, col, true);
		//a cap is drawn over the top of the block with an orange filter applied
		renderer.drawTextureRegion(texture, lerpX, lerpY + height-10, width, 10, 500, ty, 100, 20, new Color(1f, 0.64f, 0f), true); 
		renderer.end();
		renderer.setModel(new Matrix4f());
	}
}
