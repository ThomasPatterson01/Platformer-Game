package main;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import menus.PostLevelMenu;
import rendering.Color;
import rendering.FadingText;
import rendering.Font;
import rendering.Text;
import sprites.*;

public class Spawner {
	
	private Handler handler;
	public static int CURRENT_SEED, SEED;
	public static Level LEVEL = Level.Pluto;
	private int xOffset = 0;
	private int yOffset = 0;
	
	public Spawner(Handler handler) {
		this.handler = handler;
		handler.getMainMenu().setSpawner(this);
		handler.getPostLevelMenu().setSpawner(this);
	}
	
	//read the level file and spawn the corresponding level
	public void spawnLevel() {
		
		//reset the previous level
		PostLevelMenu.LEVEL_TIME_START = (long)(System.currentTimeMillis());
		xOffset = 0;
		yOffset = 0;
		clear();
		
		//add fading text of the planet's name bottom left
		Font textFont = new Font("Consolas", java.awt.Font.PLAIN, 100);
		Text planetName = new Text(LEVEL.getPlanetName(), 20, 20, textFont, new Color(1,1,1, 0.7f));
		handler.addFadingText(new FadingText(planetName, 0, 0, 1, 120, false));
		
		//if we are on the tutorial level, show some instructions to help the player
		//these are in the form of fading texts, with a life of Integer.MAX_VALUE, so they will only fade after 1.1 years
		if (LEVEL == Level.Pluto) {
			textFont = new Font("Consolas", java.awt.Font.PLAIN, 30);
			String instructions = "Press LEFT/RIGHT to move ";
			Text instructionText = new Text(instructions, 300, 500, textFont, new Color(1,1,1));
			handler.addFadingText(new FadingText(instructionText, 0, 0, 1, Integer.MAX_VALUE, true));
			
			instructions = "Press UP to jump";
			instructionText = new Text(instructions, 1500, 250, textFont, new Color(1,1,1));
			handler.addFadingText(new FadingText(instructionText, 0, 0, 1, Integer.MAX_VALUE, true));
			
			instructions = "Press DOWN while on the ground to crouch";
			instructionText = new Text(instructions, 5300, 250, textFont, new Color(1,1,1));
			handler.addFadingText(new FadingText(instructionText, 0, 0, 1, Integer.MAX_VALUE, true));
			
			instructions = "Press DOWN while in the air to fall quickly";
			instructionText = new Text(instructions, 6700, 250, textFont, new Color(1,1,1));
			handler.addFadingText(new FadingText(instructionText, 0, 0, 1, Integer.MAX_VALUE, true));
			
			instructions = "Press K/J to pan the camera up and down";
			instructionText = new Text(instructions, 7500, 0, textFont, new Color(1,1,1));
			handler.addFadingText(new FadingText(instructionText, 0, 0, 1, Integer.MAX_VALUE, true));
		}
		
		//the file path of the level file
		String filepath = "res/levels/" + LEVEL.getPlanetName() + ".txt";
		
		try {
			//read in the lines of the file
			//if contains .png, spawn that set section
			//else spawn a random of section of the specified length
			List<String> allLines = Files.readAllLines(Paths.get(filepath));
			for (String line : allLines) {
				if (line.contains(".png")) {
					spawnSetSection(line);
				}else {
					spawnRandSection(Integer.parseInt(line));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//load a predertermined section from an image file
	public void spawnSetSection(String path){
		
		//load the actual image from the file, printing an error if it is not found
		BufferedImage section;
		try {
			section = ImageIO.read(new FileInputStream(path));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		//get the width and height of the image
		int w = section.getWidth(), h = section.getHeight();
		
		//the x and y location of the final block
		//used to calculate the offset needed for the next section
		int finalX = 0, finalY = 0;
		
		//whether or not we have spawned a block yet
		boolean spawnedBlock = false;
		
		//loop through each pixel, and obtain the r, g, b values from each one
		//depending on the colour, spawn a different sprite in that location
		//(255,0,0) = Player
		//(0,255,0) = Block
		//(255,255,0) = MeltingBlock
		//(255,0,255) = SlipBlock
		//(0,0,255) = Enemy
		//(0,255,255) = ShootingEnemy
		//(0,0,0) = KillZone
		//(255,123,0) = WinZone
		int pixel, r, g, b;
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				//h-y-1 flips the image so it is spawned the right way up
				pixel = section.getRGB(x, h-y-1);
				r = (pixel >> 16) & 0xff;
				g = (pixel >> 8) & 0xff;
				b = pixel & 0xff;
				
				if (r == 255 && g == 0 && b == 0) {
					handler.setPlayer(new Player(xOffset+x*50, yOffset+y*50));
				}
				else if (r == 0 && g == 255 && b == 0) {
					
					//if the first block to be spawned, adjust offset for this location, so all the sections line up
					if (!spawnedBlock) {
						yOffset -= y*50;
						spawnedBlock = true;
					}
					
					handler.addBlock(new Block(xOffset+x*50, yOffset+y*50));
					
					//if further right and up than every before, set the final x and y
					if (xOffset+x*50 >= finalX) {
						finalX = xOffset+x*50;
						finalY = yOffset+y*50;
					}
					
				}else if (r == 255 && g == 255 && b == 0) {
					
					//if the first block to be spawned, adjust offset for this location, so all the sections line up
					if (!spawnedBlock) {
						yOffset -= y*50;
						spawnedBlock = true;
					}
					
					handler.addBlock(new MeltingBlock(xOffset+x*50, yOffset+y*50, (float)(4*ImprovedNoise.noise(x*0.03f, 0, 0))));
					
					//if further right and up than every before, set the final x and y
					if (xOffset+x*50 >= finalX) {
						finalX = xOffset+x*50;
						finalY = yOffset+y*50;
					}
					
				}else if (r == 255 && g == 0 && b == 255) {
					
					//if the first block to be spawned, adjust offset for this location, so all the sections line up
					if (!spawnedBlock) {
						yOffset -= y*50;
						spawnedBlock = true;
					}
					
					handler.addBlock(new SlipBlock(xOffset+x*50, yOffset+y*50));
					
					//if further right and up than every before, set the final x and y
					if (xOffset+x*50 >= finalX) {
						finalX = xOffset+x*50;
						finalY = yOffset+y*50;
					}
					
				}
				else if (r == 0 && g == 0 && b == 255) {
					handler.addEnemy(new Enemy(xOffset+x*50, yOffset+y*50));
				}else if (r == 0 && g == 255 && b == 255) {
					handler.addEnemy(new ShootingEnemy(xOffset+x*50, yOffset+y*50));
				}
				else if (r == 0 && g == 0 && b == 0) {
					handler.addZone(new KillZone(xOffset+x*50, yOffset+y*50, 50, 50));
				}else if (r == 255 && g == 123 && b == 0) {
					handler.addZone(new WinZone(xOffset+x*50, yOffset+y*50, 50, 50));
				}else if (r == 255 && g == 0 && b == 123) {
					handler.addPickup(new HealthPickup(xOffset+x*50, yOffset+y*50));
				}
				else if (r == 123 && g == 0 && b == 255) {
					handler.addPickup(new Coin(xOffset+x*50, yOffset+y*50));
				}
			}
		}
		
		//set the offset up for the next section
		xOffset = finalX+50;
		yOffset = finalY;
	}
	
	public void spawnRandSection(int length) {
		float noiseOffset = Spawner.CURRENT_SEED/1000f;
		float noiseInterval = 0.03f;
		int coinStretch = 0;
		float coinHeight = 0;
		
		//increment the current seed so it is not identical to the previous level
		Spawner.CURRENT_SEED += 100000;
		
		//the x and y location of the final block
		//used to calculate the offset needed for the next section
		int finalX = 0, finalY = 0;
		
		for (float noiseSeed = 0; noiseSeed < length*noiseInterval; noiseSeed+=noiseInterval) {
			
			//the actual perlin noise value is generated. The value supplied increments by noiseInterval each time.
			//the lower noiseInterval, the smoother the heights will be
			//the value is then converted to a block height in the range -5 to 5 blocks
			double noiseValue = ImprovedNoise.noise(noiseSeed+noiseOffset, 1, 1);
			int height = (int)Math.round(noiseValue*10)*50;
			
			//set the final x and y
			finalX = (int)(xOffset+noiseSeed*50/noiseInterval);
			finalY = (int)(yOffset+height);
			
			//adjust the offset so that the sections line up properly
			if (noiseSeed == 0) yOffset -= height;

			//powerups
			//HEALTH: every 100 blocks
			//otherwise could be coin stretch
			if (Math.round(noiseSeed/noiseInterval) % 100 == 75){
				handler.addPickup(new HealthPickup(xOffset+noiseSeed*50/noiseInterval, yOffset+height+250));
			}else {
				coinStretch--;
				if (coinStretch >= 0 && coinHeight > height+10) {
					handler.addPickup(new Coin(xOffset + noiseSeed * 50 / noiseInterval, coinHeight));
				} else if (coinStretch < -10) {
					Random r = new Random((long) (CURRENT_SEED + noiseSeed * 20000));
					int p = r.nextInt(150);
					if (p <= 10) coinStretch = p + 4;
					coinHeight = yOffset + height + (1 + r.nextInt(4)) * 50;
				}
			}

			//add kill zone below
			handler.addZone(new KillZone(xOffset+noiseSeed*50/noiseInterval, yOffset+height-600, 50, 50));
			
			//creates a ceiling (4 blocks deep) when the ceiling curve is below a certain value
			//seed is divided by 5 to lengthen the ceilings
			float ceilingProbability = 0.4f;
			double ceilingNoiseValue = ImprovedNoise.noise((noiseSeed+noiseOffset+100)/5f, 1, 1);
			if (ceilingNoiseValue+0.5 < ceilingProbability) {
				for (int h = height+400; h < height+550; h+=50) handler.addBlock(new Block(xOffset+noiseSeed*50/noiseInterval, yOffset+h));
			}
			
			//Another perlin curve for whether there is a hole in the floor or not.
			//The seed is multiplied by 3 to make the holes not too long
			//To prevent uncrossable gaps, every 5th block is guaranteed to exist
			float holeProbability = 0.3f;
			double holeNoiseValue = ImprovedNoise.noise((noiseSeed+noiseOffset+200)*2f, 1, 1);
			if (holeNoiseValue+0.5 < holeProbability && Math.round(noiseSeed/noiseInterval)%5 != 0) continue;

			int blockDepth = 3;

			//decide what type of block based on a separate perlin curve - so that the type is not dependent on height
			//set block depth to 0 if melting, so that the player can fall through
			double blockTypeNoiseValue = ImprovedNoise.noise((noiseSeed+noiseOffset+300)/2f, 1, 1);
			boolean slip = false, melt = false;
			float slipProbability = LEVEL.getSlipProbability();
			float meltProbability = LEVEL.getMeltProbability();
			if (blockTypeNoiseValue+0.5 < slipProbability) slip = true;
			if (blockTypeNoiseValue+0.5 > 1-meltProbability){ melt = true; blockDepth = 0;}
			
			//create the top block based on what type it should be
			//then create the underlying blocks based on what the blockDepth is
			Block b;
			if (slip) b = new SlipBlock(xOffset+noiseSeed*50/noiseInterval, yOffset+height);
			else if (melt) b = new MeltingBlock(xOffset+noiseSeed*50/noiseInterval, yOffset+height, (float)(4*ImprovedNoise.noise(noiseSeed, 0, 0)));
			else b = new Block(xOffset+noiseSeed*50/noiseInterval, yOffset+height);
			handler.addBlock(b);
			for (int h = height-blockDepth*50; h < height; h+=50){
				Block bl = new Block(xOffset+noiseSeed*50/noiseInterval, yOffset+h);
				handler.addBlock(bl);
			}
			
			
			//create an enemy every few blocks (but not at the start because the player spawns there)
			//this is based on a simple random float (not perlin) because consistency is not relevant
			Random r = new Random((long) (CURRENT_SEED+noiseSeed*10000));
			float shootingProbability = LEVEL.getShootingEnemyProbability();
			if (Math.round(noiseSeed/noiseInterval)%LEVEL.getEnemyFrequency() == 0 && noiseSeed != 0) {
				if (r.nextFloat() < shootingProbability) handler.addEnemy(new ShootingEnemy(xOffset+noiseSeed*50/noiseInterval, yOffset+height+50));
				else handler.addEnemy(new Enemy(xOffset+noiseSeed*50/noiseInterval, yOffset+height+50));
			}
		}
		
		//set the offset up for the next section
		xOffset = finalX+50;
		yOffset = finalY;
	}
	
	//clear all sprites from the handler
	public void clear() {
		handler.clearBlocks();
		handler.clearEnemies();
		handler.clearBullets();
		handler.clearZones();
		handler.clearParticles();
		handler.clearShootingStars();
		handler.clearFadingTexts();
		handler.clearPickups();
		handler.setPlayer(null);
	}
}
