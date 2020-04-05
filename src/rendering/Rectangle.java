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



    public void render(Renderer renderer, Texture texture) {
        //draw the rectangle
        renderer.begin();
        texture.bind();
        renderer.drawTextureRegion(texture, x, y, width , height, 626, 20, 1, 1, c, false);
        renderer.end();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Color getCol() {
        return c;
    }

    public void setCol(Color c) {
        this.c = c;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

}
