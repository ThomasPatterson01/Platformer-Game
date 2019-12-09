package sprites;

import main.Handler;
import main.Main;
import matrix_math.Matrix4f;
import rendering.Color;
import rendering.Renderer;
import rendering.Texture;

public class Background extends Sprite{

	
	public Background(float tx, float ty, float twidth, float theight, Color c) {
		super(0, 0, Main.WIDTH, Main.HEIGHT, tx+1, ty, twidth, theight);
		col = c;
	}	
	
	public void update(Handler handler, float delta) {
		super.update(handler, delta);
		Player p = handler.getPlayer();
		if (p == null) return;
		
		//move the background along as the player moves
		//*0.3 so that a parallax effect is created
		x = -0.3f*p.getX();
	}
	
	//when rendered, the background is drawn in 2 regions, so it can wrap around as it moves
	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {		
		Matrix4f view = Matrix4f.translate(0, 0, 0);
		renderer.setView(view);


		float lerpX = (1 - alpha) * prevX + alpha * x;
		float lerpY = (1 - alpha) * prevY + alpha * y;
		
		//the x position of the seam between the regions
		float xPos = width - Math.abs(lerpX)%width;
		
		//for now, rendered in its own batch.
		renderer.begin();
		texture.bind();
		renderer.drawTextureRegion(texture, 0, lerpY, xPos, height, tx + twidth - twidth*(xPos/width), ty, twidth*(xPos/width), theight, col, true);
		renderer.drawTextureRegion(texture, xPos, lerpY, width-xPos, height, tx, ty, twidth - twidth*(xPos/width), theight, col, true);
		renderer.end();
	}

}
