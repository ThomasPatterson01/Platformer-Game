package rendering;

import main.Handler;
import matrix_math.Matrix4f;

public class FadingText {
	
	private Text text;
	private float velX, velY;
	private float velFade; //how quickly the text fades
	private int waitFade; //how long to wait before beginning to fade
	private boolean inGame; //whether the text moves with the rest of the game (true), or whether it is fixed on the screen (false)
	
	public FadingText(Text text, float velX, float velY, float velTransparency, int waitTransparency, boolean inGame) {
		this.text = text;
		this.velX = velX;
		this.velY = velY;
		this.velFade = velTransparency;
		this.waitFade = waitTransparency;
		this.inGame = inGame;
	}
	
	public void update(Handler handler, float delta) {
		//update text position from velX and velY
		text.setX(text.getX() + velX*delta);
		text.setY(text.getY() + velY*delta);
		
		//if started to fade
		if (--waitFade <= 0) {
			//decrease alpha value
			float newAlpha = text.getColor().getAlpha() - velFade*delta;
			//if alpah < 0, remove the text
			if (newAlpha <= 0) {
				handler.removeFadingText(this);
			}else {
				text.getColor().setAlpha(newAlpha);
			}
		}
	}
	
	public void render(Renderer renderer) {
		//so the view matrix can be reset afterwards
		Matrix4f oldView = renderer.getView();
		
		//if fixed on screen, reset view matrix, then reset it afterwards
		if (!inGame) {
			renderer.setView(new Matrix4f());
		}
		
		text.render(renderer);
		
		renderer.setView(oldView);
	}
}
