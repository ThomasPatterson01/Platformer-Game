package rendering;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;

public class Window {

	private boolean vsync;
	
	private final long id;	
	
	public Window(int width, int height, CharSequence title, boolean vsync) {
		this.vsync = vsync;
		
		//set up window properties
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		/*glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);*/
		
		
		//¡yeet us a nice window!
		id = glfwCreateWindow(width, height, title, glfwGetPrimaryMonitor(), NULL);
		glfwSetWindowPos(id, 100, 100);
		
		//check window creation was successful
		if (id == NULL) {
		    glfwTerminate();
		    throw new RuntimeException("Failed to create the GLFW window");
		}
		
		//openGL context needed for rendering or something
		glfwMakeContextCurrent(id);
		GL.createCapabilities();
		GLUtil.setupDebugMessageCallback();
		
		//turn vsync on (1) or off (0)
        glfwSwapInterval(vsync ? 1 : 0);
	}
	
	public boolean isClosing() {
		return glfwWindowShouldClose(id);
	}
	
	public void update() {
        glfwSwapBuffers(id);
        glfwPollEvents();
    }
	
	public void delete() {
		glfwDestroyWindow(id);
	}
	
	public boolean isVsync() {
		return vsync;
	}

	//enable or disable vsync as needed
	public void setVsync(boolean vsync) {
		if (vsync && !this.vsync) {
			glfwSwapInterval(1);
			this.vsync = vsync;
		}
		else if (!vsync && this.vsync) {
			glfwSwapInterval(0);
			this.vsync = vsync;
		}
	}

	public long getID() {
		return id;
	}
}
