package rendering;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryUtil;

public class Font {
	
	private int fontHeight;
	private Texture texture;
	private Color c;
	
	//stores a glyph for each object, so that during rendering the location of the char on the texture can be recalled
	private Map<Character, Glyph> glyphs = new HashMap<>();
	
	
	public Font(String fontName, int style, int size) {
		c = new Color(0,0,0);
		createTexture(fontName, style, size);
	}
	
	//create a texture atlas with every character on it, in the specified font, size and style
	//characters are white against a transparent background so that the color can be changed when rendered by overlaying another color
	private void createTexture(String fontName, int style, int size) {
		java.awt.Font font = new java.awt.Font(fontName, style, size);
		
		boolean antiAlias = true;
		int imageWidth = 0;
		int imageHeight = 0;

		//get total width of chars, and max height of chars
		//chars below 32 are control codes, so these are skipped
		//127 is also a null code, so is also skipped
		for (int i = 32; i < 256; i++) {
		    if (i == 127) continue;
		    
		    //get an image of the character
		    char c = (char) i;
		    BufferedImage ch = createCharImage(font, c, antiAlias);
		    
		    //if char is not available in this font
		    if (ch == null) continue;
		    
		    //add width onto total, and maximise height 
		    imageWidth += ch.getWidth();
		    imageHeight = Math.max(imageHeight, ch.getHeight());
		}

		fontHeight = imageHeight;
		
		//the buferred image which will eventually become the texture
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		
		int x = 0;
		
		//create a glyph for each char, and add it to the glyph map
		//draw each char onto the image
		for (int i = 32; i < 256; i++) {
			if (i == 127) {
			    continue;
			}
			char c = (char) i;
			BufferedImage charImage = createCharImage(font, c, antiAlias);
	
			//add glyph
			int charWidth = charImage.getWidth();
			int charHeight = charImage.getHeight();
			Glyph ch = new Glyph(charWidth, charHeight, x, image.getHeight() - charHeight);
			glyphs.put(c, ch);
			
			//draw image
			g.drawImage(charImage, x, 0, null);
			x += ch.width;
		}
		
		// Flip image Horizontal to get the origin to bottom left
        AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
        transform.translate(0, -image.getHeight());
        AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = operation.filter(image, null);
        
        //Get charWidth and charHeight of image
        int width = image.getWidth();
        int height = image.getHeight();

        //Get pixel data of image
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        //Put pixel data into a ByteBuffer, so it can be used to create the texture
        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //Pixel as RGBA: 0xAARRGGBB
                int pixel = pixels[i * width + j];
                //Red component 0xAARRGGBB >> 16 = 0x0000AARR
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                //Green component 0xAARRGGBB >> 8 = 0x00AARRGG
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                //Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB
                buffer.put((byte) (pixel & 0xFF));
                //Alpha component 0xAARRGGBB >> 24 = 0x000000AA
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        
        //create the actual texture
        buffer.flip();
        texture = Texture.createTexture(width, height, buffer);
        MemoryUtil.memFree(buffer);
	}
	
	//get a buffered image of a specific char
	private BufferedImage createCharImage(java.awt.Font font, char c, boolean antiAlias) {
		
		//dummy 1x1 image just to get a Graphics object, so we can find the needed width and height
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		
		//get font metrics for the java.awt.Font, and enable antialias if needed
		Graphics2D g = image.createGraphics();
		if (antiAlias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		g.dispose();
		
		//get the width for this char, and the height for this font
		int charWidth = metrics.charWidth(c);
		int charHeight = metrics.getHeight();
		
		//create an image with the correct dimensions
		image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		
		//enable antialias if needed
		if (antiAlias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//draw character
		g.setFont(font);
		g.setPaint(java.awt.Color.WHITE);
		g.drawString(String.valueOf(c), 0, metrics.getAscent()); //ascent is the distance from the base of the font to the top of (most) chars
		g.dispose();
		
		return image;
	}
	
	//draw the texture
	public void render(Renderer renderer, CharSequence text, float x, float y) {
		
		//find the number of lines in the input text by counting the '\n's
		int lines = 1;
		for(int i = 0; i < text.length(); i++) {
		    char ch = text.charAt(i);
		    if(ch == '\n') {
		        lines++;
		    }
		}
		
		//get total height of the text
		int textHeight = lines * fontHeight;
		
		float drawX = x;
		float drawY = y;
		
		//if multiline, drawY should be at the base of the top line
		if(textHeight > fontHeight) {
		    drawY += textHeight - fontHeight;
		}
		
		//begin batch
		texture.bind();
		renderer.begin();
		
		//render each char in the input text
		for (int i = 0; i < text.length(); i++) {
		    char ch = text.charAt(i);
		    
		    //if newline char, reset x, decrease y, and continue
		    if (ch == '\n') {
		        drawY -= fontHeight;
		        drawX = x;
		        continue;
		    }
		    
		    //skip carriage returns
		    if (ch == '\r') continue;
		    
		    //render the glyph
		    Glyph g = glyphs.get(ch);
		    renderer.drawTextureRegion(texture, drawX, drawY, g.width, g.height, g.x, g.y, g.width, g.height, c, false);
		    drawX += g.width;
		}
		
		//end batch
		renderer.end();
	}
	
	public void setColor(float r, float g, float b, float a) {
		c.setRed(r);
		c.setGreen(g);
		c.setBlue(b);
		c.setAlpha(a);
	}
	
	//Glyph is used to store the dimensions and location of a character within the texture
	private class Glyph{
		public final int width;
	    public final int height;
	    public final int x;
	    public final int y;

	    public Glyph(int width, int height, int x, int y) {
	        this.width = width;
	        this.height = height;
	        this.x = x;
	        this.y = y;
	    }
	}
}
