package rendering;

public class Text {
	
	private String text;
	private Color c;
	private Font f;
	private float x, y;
	
	public Text(String text, float x, float y, Font f, Color c) {
		this.text = text;
		this.f = f;
		this.x = x;
		this.y = y;
		this.c = c;
	}
	
	public void render(Renderer renderer) {
		//update color in case the same font object is being used somewhere else with a different color
		f.setColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		f.render(renderer, text, x, y);
	}
	
	public void setColor(Color c) {
		this.c = c;
	}
	
	public Color getColor() {
		return c;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
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
	
	
}
