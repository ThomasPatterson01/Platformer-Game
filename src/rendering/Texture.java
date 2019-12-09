package rendering;

import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

public class Texture {
	
	private final int id;
	private int width, height;
	
	public Texture() {
		id = glGenTextures();
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	private void setParameter(int name, int value) {
		glTexParameteri(GL_TEXTURE_2D, name, value);
	}
	
	//load texture data and find out its width and height
	public static Texture loadTexture(String path) {
		ByteBuffer data;
		int width, height;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer comp = stack.mallocInt(1);
			IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            
			stbi_set_flip_vertically_on_load(true); //so that the origin is in the bottom left
			data = stbi_load(path, w, h, comp, 4);
			
			// check loading was successful
			if (data == null) {
			    throw new RuntimeException("Failed to load a texture file!\n" + stbi_failure_reason());
			}
			
			 width = w.get();
	         height = h.get();
		}
		
		//create and upload the actual texture using the read data
		return createTexture(width, height, data);
		
	}
	
	//creates a new texture object for this texture, and uploads the data to openGL
	public static Texture createTexture(int width, int height, ByteBuffer data) {
		Texture texture = new Texture();
		texture.setWidth(width);
		texture.setHeight(height);
		
		texture.bind();
		
		//set texture parameters
		texture.setParameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE); //co-ordinates outside of 0 and 1 are clamped to the range 0-1
		texture.setParameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		texture.setParameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST); //used when scaling, selects the nearest co-ordinate's value
		texture.setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		//upload the actual data
		//the GL_RGBA8 stuff just says we are using rgba scheme with 8 bits per value (so 32 in total)
		texture.uploadData(GL_RGBA8, width, height, GL_RGBA, data);
		
		return texture;
	}
	
	public void uploadData(int internalFormat, int width, int height, int format, ByteBuffer data) {
		glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, data);
	}
	
	public void delete() {
		glDeleteTextures(id);
	}
	
	public int getID() {
		return id;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
