package rendering;

import static org.lwjgl.opengl.GL32.*;

import java.nio.FloatBuffer;

public class VertexBufferObject {

	private final int id;
	
	public VertexBufferObject() {
		id = glGenBuffers();
	}
	
	public void bind(int target) {
		glBindBuffer(target, id);
	}
	
	public void uploadData(int target, FloatBuffer data, int usage) {
		glBufferData(target, data, usage);
	}
	
	//called instead of upload data if we have already uploaded some data, and want more
	public void uploadSubData(int target, int offset, FloatBuffer data) {
		glBufferSubData(target, offset, data);
	}
	
	public void uploadNull(int target, int size, int usage) {
		glBufferData(target, size, usage);
	}
	
	public void delete() {
		glDeleteBuffers(id);
	}
	
	public int getID() {
		return id;
	}
}
