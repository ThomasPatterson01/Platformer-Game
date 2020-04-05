package sprites;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import main.Handler;
import matrix_math.Matrix4f;
import matrix_math.Vector4f;
import rendering.Animation;
import rendering.Color;
import rendering.Renderer;
import rendering.Texture;

public abstract class Sprite {

	protected float x, y, width, height;
	protected float tx, ty, twidth, theight;
	
	protected float velX, velY;
	protected float prevX, prevY;
	
	protected Animation currentAnimation;
	protected boolean fixedScreenLocation = false;
	
	protected Color col;
	
	public Sprite(float x, float y, float width, float height, float tx, float ty, float twidth, float theight) {
		
		prevX = this.x = x; prevY = this.y = y;
		this.width = width; this.height = height;
		
		this.tx = tx; this.ty = ty;
		this.twidth = twidth; this.theight = theight;
		
		col = new Color(1, 1, 1);
	}
	
	public void keyInput(int key, int action, int mods) {
		
	}
	
	public void mouseInput(int button, int action, int mods) {
		
	}
	
	//update x,y position
	public void update(Handler handler, float delta) {
		prevX = x; prevY = y;
		x += delta * velX;
		y += delta * velY;
				
		if (currentAnimation != null) {
			if (!currentAnimation.update(delta)){
				currentAnimation = null;
			}
		}
	}
	
	//render to screen, interpolating between the last and current position
	public void render(Renderer renderer, Texture texture, float alpha) {

		Matrix4f oldView = renderer.getView();

		//if fixed on screen, reset view matrix, then reset it afterwards
		if (fixedScreenLocation) {
			renderer.setView(new Matrix4f());
		}

		float lerpX = (1 - alpha) * prevX + alpha * x;
		float lerpY = (1 - alpha) * prevY + alpha * y;
		
		float tx = this.tx, ty = this.ty, twidth = this.twidth, theight = this.theight;
		if (currentAnimation != null) {
			tx = currentAnimation.getX();
			ty = currentAnimation.getY();
			twidth = currentAnimation.getWidth();
			theight = currentAnimation.getHeight();
		}
		
		//for now, rendered in its own batch.
		renderer.begin();
		texture.bind();
		renderer.drawTextureRegion(texture, lerpX, lerpY, width, height, tx, ty, twidth, theight, col, true);
		renderer.end();

		renderer.setView(oldView);
	}
	
	public Rectangle2D.Float getHitbox() {
		return new Rectangle2D.Float(x, y, width, height);
	}

	public ArrayList<Vector4f> getLines(){
		ArrayList<Vector4f> lines = new ArrayList<>();
		lines.add(new Vector4f(x, y, x, y+height));
		lines.add(new Vector4f(x, y, x+width, y));
		lines.add(new Vector4f(x+width, y, x+width, y+height));
		lines.add(new Vector4f(x, y+height, x+width, y+height));
		return lines;
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
		return col;
	}

	public void setCol(Color col) {
		this.col = col;
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

	public float getTx() {
		return tx;
	}

	public void setTx(float tx) {
		this.tx = tx;
	}

	public float getTy() {
		return ty;
	}

	public void setTy(float ty) {
		this.ty = ty;
	}

	public float getTwidth() {
		return twidth;
	}

	public void setTwidth(float twidth) {
		this.twidth = twidth;
	}

	public float getTheight() {
		return theight;
	}

	public void setTheight(float theight) {
		this.theight = theight;
	}

	public float getVelX() {
		return velX;
	}

	public void setVelX(float velX) {
		this.velX = velX;
	}

	public float getVelY() {
		return velY;
	}

	public void setVelY(float velY) {
		this.velY = velY;
	}
	
	public void setFixedScreenLocation(boolean b){
		fixedScreenLocation = b;
	}

	public boolean isFixedScreenLocation(){
		return fixedScreenLocation;
	}

}
