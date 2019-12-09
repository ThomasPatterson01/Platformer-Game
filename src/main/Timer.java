package main;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Timer {

	private double lastLoopTime;
	private float timeCount;
	private int fps;
	private int fpsCount;
	private int ups;
	private int upsCount;
	
	public Timer() {
		lastLoopTime = getTime();
	}
	
	public double getTime() {
		return glfwGetTime();
	}
	
	//time between each call of this function (each loop)
	public float getDelta() {
		double time = getTime();
		float delta = (float) (time - lastLoopTime);
        lastLoopTime = time;
        timeCount += delta;
        return delta;
	}
	
	//called when frame is rendered or update is executed
	public void updateFPS() {
        fpsCount++;
    }
	public void updateUPS() {
        upsCount++;
    }
	
	//resets counts if necessary
	public void update() {
        if (timeCount > 1f) {
            fps = fpsCount;
            fpsCount = 0;

            ups = upsCount;
            upsCount = 0;

            timeCount -= 1f;
            
            Main.FPS = fps;
            
	        //output FPS and UPS
	        System.out.println("FPS: " + fps + " | UPS: " + ups);
        }
    }
	
	public int getFPS() {
        return fps > 0 ? fps : fpsCount;
    }
	
	public int getUPS() {
        return ups > 0 ? ups : upsCount;
    }
	
	public double getLastLoopTime() {
        return lastLoopTime;
    }
}
