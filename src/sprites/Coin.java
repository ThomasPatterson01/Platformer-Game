package sprites;

import main.Handler;
import menus.PostLevelMenu;
import rendering.Color;
import rendering.FadingText;
import rendering.Font;
import rendering.Text;

public class Coin extends Pickup {

    public Coin(float x, float y) {
        super(x, y, 30, 30, 5823, 0, 50, 50);
    }

    @Override
    public void update(Handler handler, float delta) {
        super.update(handler, delta);

        //if collide with player, kill player and remove bullet
        Player p = handler.getPlayer();
        if (p != null && p.getHitbox().intersects(getHitbox()) && p.isAlive()) {
            PostLevelMenu.LEVEL_SCORE += 10;
            Particle.explode(this, (int)width/5, (int)height/5, 8, 8, 0, 0, handler);
            handler.removePickup(this);

            Font textFont = new Font("Consolas", java.awt.Font.BOLD, 30);
            Text planetName = new Text("+10", x-5, y, textFont, new Color(1, 212f/255, 0));
            handler.addFadingText(new FadingText(planetName, 0, 60, 0.75f, 0, true));
        }
    }

}
