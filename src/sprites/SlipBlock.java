package sprites;

import rendering.Color;
import rendering.Renderer;
import rendering.Texture;

public class SlipBlock extends Block{
	
	public SlipBlock(float x, float y) {
		super(x, y);
	}
	
	//need to override the render function in order to add an extra texture region
	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {
		
		float lerpX = (1 - alpha) * prevX + alpha * x;
		float lerpY = (1 - alpha) * prevY + alpha * y;
		
		//for now, rendered in its own batch.
		renderer.begin();
		texture.bind();
		renderer.drawTextureRegion(texture, lerpX, lerpY, width, height, tx, ty, twidth, theight, col, true);
		//a cap is drawn over the top of the block with an light blue filter applied
		renderer.drawTextureRegion(texture, lerpX, lerpY + height-10, width, 10, 505, ty, 100, 20, new Color(0f,0.75f, 1f), true);
		renderer.end();
	}

}
