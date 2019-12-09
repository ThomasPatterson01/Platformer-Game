package rendering;

import java.util.ArrayList;

import matrix_math.Matrix4f;

public class Graph {
	private int x, y, width, height;
	private Line xAxis, yAxis;
	
	private ArrayList<Line> lines = new ArrayList<>();
	
	private Text xLabel;
	private Text yLabel;
	private Text maxXLabel;
	private Text maxYLabel;
	
	public Graph(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		//the axis are created
		xAxis = new Line(x, y, x+width, y, 3, new Color(1,1,1));
		yAxis = new Line(x, y, x, y+height, 3, new Color(1,1,1));
		
		//the labels on each axis are created
		//these are the names of each axis and the maximum values
		Font labelFont = new Font("Consolas", java.awt.Font.PLAIN, 20);
		xLabel = new Text("", x, y-30, labelFont, new Color(1,1,1));
		yLabel = new Text("", x, y, labelFont, new Color(1,1,1));
		maxXLabel = new Text("", x+width-10, y-30, labelFont, new Color(1,1,1));
		maxYLabel = new Text("", x+10, y+height-20, labelFont, new Color(1,1,1));
	}
	
	public void render(Renderer renderer, Texture texture, float alpha) {
		xAxis.render(renderer, texture, alpha);
		yAxis.render(renderer, texture, alpha);
		
		xLabel.render(renderer);
		
		//rotate y axis name, so it is upwards (by adjusting model matrix)
		Matrix4f model = Matrix4f.translate(yLabel.getX(), yLabel.getY(), 0).multiply(Matrix4f.rotate(90, 0, 0, 1).multiply(Matrix4f.translate(-yLabel.getX(), -yLabel.getY(), 0)));
		renderer.setModel(model);
		yLabel.render(renderer);
		//reset model matrix
		renderer.setModel(new Matrix4f());
		
		maxXLabel.render(renderer);
		maxYLabel.render(renderer);
		
		//draw the actual graph
		for (Line l : lines) {
			l.render(renderer, texture, alpha);
		}
	}
	
	public void showGraph(ArrayList<Integer> xData, ArrayList<Integer> yData) {
		int maxX = 0, maxY = 0;
		
		//work out the maximum values on each axis
		for (int d : xData) {
			if (d > maxX) maxX = d;
		}
		for (int d : yData) {
			if (d > maxY) maxY = d;
		}
		
		//set the maximum value labels for each axis
		maxXLabel.setText(Integer.toString(maxX));
		maxYLabel.setText(Integer.toString(maxY));
		
		//transformed values of the data
		ArrayList<Integer> newXData = new ArrayList<>();
		ArrayList<Integer> newYData = new ArrayList<>();
		
		//transform the data to its position on the graph
		for (int i = 0; i < xData.size(); i++) {
			int newX = (int)(((float)xData.get(i)/(float)maxX)*width);
			int newY = (int)(((float)yData.get(i)/(float)maxY)*height);
			
			newXData.add(newX);
			newYData.add(newY);
		}
		
		//clear lines from previous lines
		lines.clear();
		
		//add a line betwen each pair of consecuetive points
		for (int i = 0; i < xData.size()-1; i++) {
			
			//add the (x, y) of the graph to the points 
			int x1 = newXData.get(i) + x;
			int y1 = newYData.get(i) + y;
			int x2 = newXData.get(i+1) + x;
			int y2 = newYData.get(i+1) + y;
			lines.add(new Line(x1, y1, x2, y2, 2, new Color(1,1,1)));
		}
	}
	
	public void setXLabel(String label) {
		xLabel.setText(label);
	}
	
	public void setYLabel(String label) {
		yLabel.setText(label);
	}
}
