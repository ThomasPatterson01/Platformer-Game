package main;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.glfw.*;

import rendering.Renderer;
import rendering.Texture;
import rendering.Window;

public class Main {
	
	private GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
	
	private final int TARGET_UPS = 60;
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static int FPS = 0;
	
	public static GameState GAMESTATE;
	
	private Timer timer;
	private Window window;
	private Renderer renderer;
	private Texture texture;
	private Handler handler;
	private Camera camera;
	@SuppressWarnings("unused")
	private Spawner spawner;
	
	private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		
		//P = pause/unpause
	    @Override
	    public void invoke(long window, int key, int scancode, int action, int mods) {
	        if (key == GLFW_KEY_P && action == GLFW_PRESS) {
	        	if (GAMESTATE == GameState.Pause) GAMESTATE = GameState.Game;
	        	else if (GAMESTATE == GameState.Game) GAMESTATE = GameState.Pause;
	        }
	        
	        keyInput(key, action, mods);
	    }
	};
	
	
	private GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {

	    @Override
	    public void invoke(long window, int button, int action, int mods) {
	       mouseButtonInput(button, action, mods);
	    }
	};
	
	private GLFWCursorPosCallback mousePosCallback = new GLFWCursorPosCallback() {

	    @Override
	    public void invoke(long window, double xpos, double ypos) {
	       mousePosInput(xpos, Main.HEIGHT - ypos);
	    }
	};
	
	public Main() {
		
		//set up all the key systems
		//e.g. GLFW, the window, renderer, the texture atlas ..etc
		init();
		
		//start game
		GAMESTATE = GameState.MainMenu;
		
		//begin the game loop
		//updates, renders and processes input
		gameLoop();
		
		//delete all resources
		freeResources();
	}
	
	private void init() {
		//in case we get an error from GLFW init
		glfwSetErrorCallback(errorCallback);
		
		//initialise GLFW
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		
		//start the window
		window = new Window(WIDTH, HEIGHT, "Platformer", true);
		glfwSetKeyCallback(window.getID(), keyCallback);
		glfwSetMouseButtonCallback(window.getID(), mouseButtonCallback);
		glfwSetCursorPosCallback(window.getID(), mousePosCallback);
		
		//initialise the renderer
		renderer = new Renderer();
		
		//load texture atlas
		texture = Texture.loadTexture("res/images/mainTexture.png");
		
		handler = new Handler(window);
		
		//create camera
		camera = new Camera(handler);
		handler.setCamera(camera);
				
		spawner = new Spawner(handler);		
		
		//load the music and sounds needed
		AudioPlayer.load();
		AudioPlayer.playMusic("backgroundMusic", 1.0f);
	}
	
	private void gameLoop() {
		
		//initialise game loop timer
		timer = new Timer();
		        
        //gameLoop variables
        float delta;
        float accumulator = 0f;
        final float interval = 1f / TARGET_UPS;
        float alpha;
        
		//main loop
		while (!window.isClosing()) {
			
			//get delta time and update the accumulator 
            delta = timer.getDelta();
            accumulator += delta;

            //update game and timer UPS if enough time has passed
            while (accumulator >= interval) {
                update(interval);
                timer.updateUPS();
                accumulator -= interval;
            }

            //calculate alpha value for interpolation between previous and next frame
            alpha = accumulator / interval;

            //render game and update timer FPS
            render(alpha);
            timer.updateFPS();

            //update timer 
            timer.update();
            
            //swap buffers and poll events
            window.update();
		}
		        
	}
	
	//delete resources
	public void freeResources() {
		window.delete();
        glfwTerminate();
        errorCallback.free();
        AudioPlayer.close();
	}  
	
	//process input
	public void keyInput(int key, int action, int mods) {
		handler.keyInput(key, action, mods);
	}
	
	public void mouseButtonInput(int button, int action, int mods) {
		handler.mouseButtonInput(button, action, mods);
	}
	
	public void mousePosInput(double xpos, double ypos) {
		handler.mousePosInput(xpos, ypos);
	}
	
	//update all sprites
	public void update(float delta) {
		handler.update(delta);
		camera.update(delta);
	}
	
	//render all sprites
	public void render(float alpha) {
		
		//reset the buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		handler.render(renderer, texture, alpha);
	}
	
    public static void main(String[] args) {
    	new Main();
    }

}
