package rendering;

import main.Handler;
import matrix_math.Matrix4f;
import sprites.Particle;

import java.util.Random;

public class Rectangle {

    private float x, y, width, height;
    private Color c;

    public Rectangle(float x, float y, float width, float height, Color c){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.c = c;
    }

    public void dissolve(Handler handler) {

        Random r = new Random();
        for (int i = (int)x; i < x+width; i+=5) {
            for (int j = (int)y; j < y+height; j+=5) {

                //location and dimensions of particle
                float pwidth = 5;
                float pheight = 5;

                //velocity of particle
                float pvelX = r.nextFloat()*20-10;
                float pvelY = j - (y+height/2) + r.nextFloat()*8-4;

                Particle p = new Particle(i, j, pwidth, pheight, 620, 20, 1, 1, c, pvelX*8, pvelY*8, (int)width/20-(int)((i-x)/20));
                p.setAcceleration(0, 300);
                p.setFixedScreenLocation(true);
                handler.addParticle(p);
            }
        }
    }

    public void render(Renderer renderer, Texture texture) {
        //draw the rectangle
        renderer.begin();
        texture.bind();
        renderer.drawTextureRegion(texture, x, y, width , height, 620, 20, 1, 1, c, false);
        renderer.end();
    }

    public void setWidth(float width){
        this.width = width;
    }

}
