package menus;

import java.awt.geom.Rectangle2D;

import static org.lwjgl.glfw.GLFW.*;

import rendering.Color;
import rendering.Renderer;
import rendering.Text;
import rendering.Texture;

public class Button {
	
	private float x, y, width, height;
	private float tx, ty, twidth, theight;
	private Text text;
	private boolean hovering, clicked; 
	private boolean visibleBackground = true, hoverVisibleBackground = true;
	private String backgroundShape = "squircle";
	private Color col, hoverCol;
	private Color textCol, hoverTextCol;
	
	public Button(float x, float y, float width, float height, Color col) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.col = this.hoverCol = col;
		
		tx = 600; ty = 0;
		twidth = 100; theight = 60;
	}
	
	public void render(Renderer renderer, Texture texture) {
		//if the background is currently visible
		if (hovering ? hoverVisibleBackground : visibleBackground) {
			renderer.begin();
			texture.bind();
			//render the squircle or rectangle background of the button
			if (backgroundShape == "squircle")
				renderer.drawTextureRegion(texture, x, y, width, height, tx, ty, twidth, theight, hovering ? hoverCol : col, false);
			else if (backgroundShape == "rectangle")
				renderer.drawTextureRegion(texture, x, y, width, height, tx+twidth/3, ty+theight/3, 1, 1, hovering ? hoverCol : col, false);
			renderer.end();
		}
		//if there is text, render that in the correct color
		if (text != null) {
			text.setColor(hovering ? hoverTextCol : textCol);
			text.render(renderer);
		}
	}
	
	public void mouseButtonInput(int button, int action, int mods) {
		if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS && hovering) {
			clicked = true;
		}
	}
	
	public void mousePosInput(double xpos, double ypos) {
		hovering = getButtonHitbox().contains(xpos, ypos);
	}
	
	private Rectangle2D.Float getButtonHitbox() {
		return new Rectangle2D.Float(x, y, width, height);
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

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
		if (textCol == null) textCol = text.getColor();
		if (hoverTextCol == null) hoverTextCol = text.getColor();
	}

	public boolean isVisibleBackground() {
		return visibleBackground;
	}

	public void setVisibleBackground(boolean visibleBackground) {
		this.visibleBackground = visibleBackground;
	}
	
	public boolean isHoverVisibleBackground() {
		return visibleBackground;
	}

	public void setHoverVisibleBackground(boolean hoverVisibleBackground) {
		this.hoverVisibleBackground = hoverVisibleBackground;
	}

	public Color getCol() {
		return col;
	}

	public void setCol(Color col) {
		this.col = col;
	}

	public Color getHoverCol() {
		return hoverCol;
	}

	public void setHoverCol(Color hoverCol) {
		this.hoverCol = hoverCol;
	}

	public Color getTextCol() {
		return textCol;
	}

	public void setTextCol(Color textCol) {
		this.textCol = textCol;
	}

	public Color getHoverTextCol() {
		return hoverTextCol;
	}

	public void setHoverTextCol(Color hoverTextCol) {
		this.hoverTextCol = hoverTextCol;
	}

	public boolean isHovering() {
		return hovering;
	}
	
	public void setHovering(boolean hovering) {
		this.hovering = hovering;
	}

	public boolean isClicked() {
		return clicked;
	}
	
	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}

	public String getBackgroundShape() {
		return backgroundShape;
	}

	public void setBackgroundShape(String backgroundShape) {
		this.backgroundShape = backgroundShape;
	}
	
	
}
