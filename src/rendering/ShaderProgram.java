package rendering;

import static org.lwjgl.opengl.GL32.*;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryStack;

import matrix_math.Matrix4f;

public class ShaderProgram {
	
	private final int id;
	
	public ShaderProgram() {
		id = glCreateProgram();
	}
	
	public void attachShader(Shader shader) {
		glAttachShader(id, shader.getID());
	}
	
	public void bindFragDataLocation(int number, CharSequence name) {
		glBindFragDataLocation(id, number, name);
	}
	
	public int getUniformLocation(CharSequence name) {
		return glGetUniformLocation(id, name);
	}
	
	public void link() {
		glLinkProgram(id);
		
		//check link was successful
		int status = glGetProgrami(id, GL_LINK_STATUS);
		if (status != GL_TRUE) {
		    throw new RuntimeException(glGetProgramInfoLog(id));
		}
	}
	
	public void use() {
		glUseProgram(id);
	}
	
	public int getAttribLocation(CharSequence name) {
        return glGetAttribLocation(id, name);
    }

    
    public void enableVertexAttribArray(int location) {
        glEnableVertexAttribArray(location);
    }

    
    public void disableVertexAttribArray(int location) {
        glDisableVertexAttribArray(location);
    }

    
    public void pointVertexAttribute(int location, int size, int type, boolean normalise, int stride, int offset) {
        glVertexAttribPointer(location, size, type, normalise, stride, offset);
    }
    
    public void setUniform(int location, int value) {
        glUniform1i(location, value);
    }
    
    public void setUniform(int location, boolean value) {
        glUniform1i(location, value ? 1 : 0);
    }
    
    //convert  matrix to buffer and then bind to uniform location
    public void setUniform(int location, Matrix4f value) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            value.toBuffer(buffer);
            glUniformMatrix4fv(location, false, buffer);
        }
    }
	 
	public void delete() {
		glDeleteProgram(id);
	}
	
	public int getID() {
		return id;
	}
}
