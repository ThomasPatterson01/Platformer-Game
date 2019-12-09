package rendering;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.io.File;
import java.io.FileInputStream;

public class Shader {

	private final int id;
	
	public Shader(int type) {
		id = glCreateShader(type);
	}
	
	public void source(CharSequence source) {
		glShaderSource(id, source);
	}
	
	//used to load entire contents of file as the shader source, instead of passing the code in
	public void loadSource(String path) {
		
		try {
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			
			fis.read(data);
			fis.close();
			
			String contents = new String(data, "UTF-8");
			source(contents);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void compile() {
		glCompileShader(id);
		
		//check compilation was successful
		int status = glGetShaderi(id, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetShaderInfoLog(id));
        }
	}
	
	public void delete() {
		glDeleteShader(id);
	}
	
	public int getID() {
		return id;
	}
}
