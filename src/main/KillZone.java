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
	}

}
