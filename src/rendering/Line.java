package rendering;

import matrix_math.Matrix4f;

public class Line {
	
	private float x1, x2;
	private float y1, y2;
	private float width;
	private Color c;
	
	public Line(float x1, float y1, float x2, float y2, float width, Color c) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.width = width;
		this.c = c;
	}
	
	public void render(Renderer renderer, Texture texture) {
		//the midpoint of the 2 lines
		float midX = (x1+x2)/2;
		float midY = (y1+y2)/2;
		
		//the difference between the x values and the y values of the 2 points
		float dx = x2-x1;
		float dy = y2-y1;
		
		//the length of the line
		float length = (float)Math.sqrt(dx*dx + dy*dy);
		
		//the angle of the line
		double angle = Math.atan2(dy, dx);
		
		//rotate the model matrix the calculated angle around the midpoint
		Matrix4f model = Matrix4f.translate(midX, midY, 0).multiply(Matrix4f.rotate((float)Math.toDegrees(angle), 0, 0, 1).multiply(Matrix4f.translate(-midX, -midY, 0)));
		renderer.setModel(model);
		
		//draw the line
		renderer.begin();
		texture.bind();
		renderer.drawTextureRegion(texture, midX-length/2, midY-width/2, length , width, 626, 20, 1, 1, c, false);
		renderer.end();
		
		//reset the model matrix
		renderer.setModel(new Matrix4f());
	}

	public void setCoords(float x1, float y1, float x2, float y2){
		this.x1 = x1; this.y1 = y1;
		this.x2 = x2; this.y2 = y2;
	}

	public void setColor(Color c){
		this.c = c;
	}

	public void setWidth(int width){
		this.width = width;
	}

}
