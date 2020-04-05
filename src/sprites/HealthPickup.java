package sprites;

import main.Handler;
import menus.PostLevelMenu;
import rendering.Color;
import rendering.FadingText;
import rendering.Font;
import rendering.Text;

public class HealthPickup extends Pickup {

    public HealthPickup(float x, float y) {
        super(x, y, 40, 40, 5772, 0, 50, 50);
    }

    @Override
    public void update(Handler handler, float delta) {
        super.update(handler, delta);

        //if collide with player, kill player and remove bullet
        Player p = handler.getPlayer();
        if (p != null && p.getHitbox().intersects(getHitbox()) && p.isAlive()) {
            if (p.getMaxHealth() - p.getHealth() < 0.0001f){
                PostLevelMenu.LEVEL_SCORE += 500;
                Font textFont = new Font("Consolas", java.awt.Font.BOLD, 30);
                Text planetName = new Text("+500", x-5, y, textFont, new Color(1, 0, 0));
                handler.addFadingText(new FadingText(planetName, 0, 60, 0.75f, 0, true));
            }else {
                p.setHealth(Math.min(p.getMaxHealth(), p.getHealth() + 0.25f));
            }
            Particle.explode(this, (int)width/5, (int)height/5, 15, 15, 0, 0, handler);
            handler.removePickup(this);
        }
    }

}
