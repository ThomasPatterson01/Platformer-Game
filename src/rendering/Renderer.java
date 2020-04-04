package rendering;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import main.Main;
import matrix_math.Matrix4f;

import static org.lwjgl.opengl.GL32.*;

public class Renderer {
	
	private FloatBuffer vertices;
	private int numVertices;
	
	//stores locations of uniform variables in shader programs
	private int uniTex, uniModel, uniView, uniProjection;
	
	private ShaderProgram program;
	private VertexArrayObject vao;
	private VertexBufferObject vbo;
	
	private Matrix4f view, model, projection;
	
	public Renderer() {
		vao = new VertexArrayObject();
		vao.bind();
		
		vbo = new VertexBufferObject();
		vbo.bind(GL_ARRAY_BUFFER);
		
		//allocate space in out vertices buffer and on the GPU
		//for up to 131,072 floats = 0.5MB
		vertices = MemoryUtil.memAllocFloat(1310720);
		long size = vertices.capacity() * Float.BYTES;
		glBufferData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);
		
		//allow alpha blending 
		//(allows textures to be seen through transparent sections of other textures)
		glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        		
        //compile and link shader program, as well as set the default values for our uniform variables
        setupShaderProgram();
        
		numVertices = 0;
	}

	//creates a quad at the specified location with the specified dimensions, and maps the specified portion of the texture atlas to it
	//also allows for a color to be passed in, which will overlay onto the texture. Therefore a color of white (1, 1, 1) will not change the original texture
	public void drawTextureRegion(Texture texture, float x, float y, float width, float height, float regX, float regY, float regWidth, float regHeight, Color c, boolean preserveWhite) {
		int preserveWhiteLocation = program.getUniformLocation("preserveWhite"); 
		program.setUniform(preserveWhiteLocation, preserveWhite);
		
		//flips the y co-ord, so it can be specified from the top left of the texture atlas, rather than the bottom right, which could vary as more textures are added
		regY = texture.getHeight() - regY - regHeight;
		
		//vertex co-ordinates
		float x1 = x, x2 = x + width;
		float y1 = y, y2 = y + height;
		
		//texture co-ordinates
		float s1 = regX / texture.getWidth();
		float s2 = (regX + regWidth) / texture.getWidth();
		float t1 = regY / texture.getHeight();
		float t2 = (regY + regHeight) / texture.getHeight();
		
		//color values
		float r = c.getRed(), g = c.getGreen(), b = c.getBlue(), a = c.getAlpha();
		
		vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
	    vertices.put(x1).put(y2).put(r).put(g).put(b).put(a).put(s1).put(t2);
	    vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);

	    vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
	    vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);
	    vertices.put(x2).put(y1).put(r).put(g).put(b).put(a).put(s2).put(t1);
	    
	    numVertices += 6;
	} 
	
	//begin is called to start specifying a batch
	public void begin() {
		numVertices = 0;
	}
	
	//end is called when a batch has finished being specified
	//when called, the current batch is rendered
	public void end() {
		flush();
	}
	
	//renders the batch and resets for the next one
	private void flush() {
		vertices.flip();
		vao.bind();
		specifyVertexAttributes();
		program.use();
		
		vbo.bind(GL_ARRAY_BUFFER);
		vbo.uploadSubData(GL_ARRAY_BUFFER, 0, vertices);
		
		glDrawArrays(GL_TRIANGLES, 0, numVertices);
		
		vertices.clear();
		numVertices = 0;
	}
	
	private void setupShaderProgram() {
		
		Shader vertexShader, fragmentShader;
		
		//load and compile vertex and fragment shaders
		vertexShader = new Shader(GL_VERTEX_SHADER);
		vertexShader.loadSource("res/shaders/default.vert"); //set source code
		vertexShader.compile(); //compile the source code

		fragmentShader = new Shader(GL_FRAGMENT_SHADER);
		fragmentShader.loadSource("res/shaders/default.frag");
		fragmentShader.compile();
		
		//link shader objects to create shader program
		program = new ShaderProgram();
		program.attachShader(vertexShader);
		program.attachShader(fragmentShader);
		program.bindFragDataLocation(0, "fragColor"); //sets fragment shader output to colour number 0
		program.link();
		program.use();
		
		vertexShader.delete();
		fragmentShader.delete();
				
		//set texture uniform
		uniTex = program.getUniformLocation("texImage");
		program.setUniform(uniTex, 0);
				
		//set model uniform matrix
		uniModel = program.getUniformLocation("model");
		model = new Matrix4f();
		program.setUniform(uniModel, model);
				
		//set view uniform matrix
		uniView = program.getUniformLocation("view");
		view = new Matrix4f();
		program.setUniform(uniView, view);
        
		//set projection uniform matrix
		uniProjection = program.getUniformLocation("projection");
		projection = Matrix4f.orthographic(0f, Main.WIDTH, 0f, Main.HEIGHT, -1f, 1f);
		program.setUniform(uniProjection, projection);	
	}
	
	public void setModel(Matrix4f model) {
		this.model = model;
		program.setUniform(uniModel, model);
	}

	public void setView(Matrix4f view) {
		this.view = view;
		program.setUniform(uniView, view);
	}
		
	public void setProjection(Matrix4f projection) {
		this.projection = projection;
		program.setUniform(uniProjection, projection);
	}
	
	public Matrix4f getView() {
		return view;
	}

	public Matrix4f getModel() {
		return model;
	}

	public Matrix4f getProjection() {
		return projection;
	}

	//tell opengl how to get the vertex shader inputs out of the vbo
	private void specifyVertexAttributes() {
		
		//Vertex coordinates
		int posAttrib = program.getAttribLocation("position");
		program.enableVertexAttribArray(posAttrib);
		program.pointVertexAttribute(posAttrib, 2, GL_FLOAT, false, 8 * Float.BYTES, 0);
		
		//Colour
		int colAttrib = program.getAttribLocation("color");
		program.enableVertexAttribArray(colAttrib);
		program.pointVertexAttribute(colAttrib, 4, GL_FLOAT, false, 8 * Float.BYTES, 2 * Float.BYTES);
	
		//Texture coordinates
		int texAttrib = program.getAttribLocation("texcoord");
		program.enableVertexAttribArray(texAttrib);
		program.pointVertexAttribute(texAttrib, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
	}
}
