package rendering;

public class Animation {

	private int frameNumber, frameCount;
	private float x, y, width, height;
	private float stepX, stepY;
	private final int timeBetweenFrames;
	private int timer;
	private AnimationMode mode = AnimationMode.DEFAULT;
	private boolean reflected = false;
	
	public Animation(float x, float y, float width, float height, float stepX, float stepY, int images, int timer) {
		this.x = x; this.y = y;
		this.width = width; this.height = height;
		this.stepX = stepX; this.stepY = stepY;
		this.frameCount = images; 
		this.timer = timeBetweenFrames = timer;
	}
	
	public boolean update(float delta) {
		if (--timer <= 0) {
			
			if (frameNumber == frameCount-1) {
				switch(mode) {
				case DEFAULT:
					resetAnimation();
					return false;
				case REPEAT:
					resetAnimation();
					return true;
				case REFLECT:
					stepX *= -1;
					stepY *= -1;
					frameNumber = 0;
					if (reflected) {
						resetAnimation();
						return false;
					}
					reflected = true;
					break;
				case REFLECT_REPEAT:
					stepX *= -1;
					stepY *= -1;
					frameNumber = 0;
				}
			}
			
			timer = timeBetweenFrames;
			frameNumber++;
			x += stepX; y += stepY;
		}
		
		return true;
	}
	
	public void resetAnimation() {
		timer = timeBetweenFrames;
		x -= stepX*frameNumber;
		y -= stepY*frameNumber;
		frameNumber = 0;
		reflected = false;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public AnimationMode getMode() {
		return mode;
	}

	public void setMode(AnimationMode mode) {
		this.mode = mode;
	}
}
