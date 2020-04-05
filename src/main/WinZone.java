package main;

import menus.PostLevelMenu;
import rendering.Color;
import rendering.Renderer;
import rendering.Texture;
import sprites.Player;

public class WinZone extends Zone {
	
	private float endLevelTimer = 60;
	private boolean hit = false;
	
	private static boolean litUp = false;
	
	public WinZone(float x, float y, float width, float height) {
		super(x, y, width, height);
	}

	public void update(Handler handler, float delta) {
		
		//if touching player, light up and register as being hit
		Player p = handler.getPlayer();
		if (p != null && p.getHitbox().intersects(getHitbox())) { 
			hit = true;
			litUp = true;
		}
		
		//once hit, count down for a second
		//once counted down, move to post level menu, and reset everything
		if (hit) {
			if (--endLevelTimer <= 0) {
				Main.GAMESTATE = GameState.PostLevelMenu;
				PostLevelMenu.LEVEL_TIME_END = (long)(System.currentTimeMillis());
				hit = false;
				endLevelTimer = 60;
				litUp = false;
			}
		}
	}
	
	//draw a checkered pattern (like a finish flag)
	@Override
	public void render(Renderer renderer, Texture texture, float alpha) {
		
		//so the flag lights up when hit by player
		float transparency = litUp ? 0.9f : 0.4f;
		
		//for now, rendered in its own batch.
		renderer.begin();
		texture.bind();
		renderer.drawTextureRegion(texture, x, y, width/2, height/2, 626, 20, 1, 1, new Color(1,1,1, transparency), true);
		renderer.drawTextureRegion(texture, x+width/2, y+height/2, width/2, height/2, 626, 20, 1, 1, new Color(1,1,1, transparency), true);
		renderer.drawTextureRegion(texture, x+width/2, y, width/2, height/2, 626, 20, 1, 1, new Color(0,0,0, transparency), true);
		renderer.drawTextureRegion(texture, x, y+height/2, width/2, height/2, 626, 20, 1, 1, new Color(0,0,0, transparency), true);
		renderer.end();
	}

}
