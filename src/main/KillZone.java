package main;

import java.util.ArrayList;

import sprites.Block;
import sprites.Player;

public class KillZone extends Zone {
	
	public KillZone(float x, float y, float width, float height) {
		super(x, y, width, height);
	}

	public void update(Handler handler, float delta) {
		//if touching player, and player is not dead yet, kill the player
		Player p = handler.getPlayer();
		if (p != null && p.getHitbox().intersects(getHitbox()) && p.isAlive()) p.die();
		
		//if block (will only be a falling melting block) hits the killzone, remove it
		ArrayList<Block> bs = handler.getBlocks();
		for (int i = bs.size()-1; i >= 0; i--) {
			if (bs.get(i).getHitbox().intersects(getHitbox())) handler.removeBlock(bs.get(i));
		}
	}

}
