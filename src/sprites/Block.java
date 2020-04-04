package sprites;

import main.Spawner;
import rendering.Color;

public class Block extends Sprite{

	public boolean hitbox = true;
		
	public Block(float x, float y) {
		super(x, y, 50, 50, 0, 0, 100, 100);
		
		col = Spawner.LEVEL.getBlockColor();
	}

	public Block(float x, float y, float width, float height, float tx, float ty, float twidth, float theight) {
		super(x, y, width, height, tx, ty, twidth, theight);
		col = new Color(0f, 1f, 0f);
	}

	public boolean isHitbox() {
		return hitbox;
	}

	public void setHitbox(boolean hitbox) {
		this.hitbox = hitbox;
	}
}
