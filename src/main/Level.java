package main;

import rendering.Color;

public enum Level {
	Pluto(500f, 0.7f, 0f, new Color(0.71f, 0.396f, 0.114f),
			0.0f, new Color(1.0f, 0.7f, 0.4f),
			new Color(0.0f, 0.0f, 0.0f), 12, 0f, "PLUTO",
			"0.071", "-240"),
	Neptune(1000f, 0.6f, 0f, new Color(0f, 0.498f, 1.0f),
			0.0f, new Color(0.4f, 0.8f, 1.0f),
			new Color(0.0f, 0.0f, 0.0f), 10, 0f, "NEPTUNE",
			"1.12", "-218"),
	Uranus(890f, 0.5f, 0f, new Color(0.4f, 1.0f, 1.0f),
			0.2f, new Color(0.8f, 0.2f, 1.0f),
			new Color(1.0f, 1.0f, 0.0f), 10, 0f, "URANUS",
			"0.889", "-224"),
	Saturn(903f, 0.4f, 0.2f, new Color(0.802f, 0.645f, 0.441f),
			0.3f, new Color(0.6f, 0.8f, 0.3f),
			new Color(1.0f, 0.5f, 0.0f), 9, 0f, "SATURN",
			"0.916", "-178"),
	Jupiter(1200f, 0.3f, 0.3f, new Color(1.0f, 0.912f, 0.72f),
			0.4f, new Color(0.2f, 0.6f, 1.0f),
			new Color(0.8f, 0.8f, 0.4f), 9, 20f, "JUPITER",
			"2.360", "-145"),
	Mars(646f, 0.2f, 0.4f, new Color(0.8f, 0.0f, 0.0f),
			0.5f, new Color(1.0f, 0.6f, 0.6f),
			new Color(0.4f, 0.4f, 0.8f), 8, 25f, "MARS",
			"0.377", "-55"),
	Earth(943f, 0f, 0.5f, new Color(0.0f, 1.0f, 0.0f),
			0.6f, new Color(1.0f, 1.0f, 0.0f),
			new Color(1.0f, 0.0f, 0.0f), 8, 30f, "EARTH",
			"1.000", "14"),
	Venus(898f, 0f, 0.6f, new Color(1.0f, 1.0f, 0.0f),
			0.7f, new Color(0.0f, 1.0f, 0.0f),
			new Color(0.0f, 0.0f, 1.0f), 7, 35f, "VENUS",
			"0.907", "460"),
	Mercury(646f, 0f, 0.7f, new Color(0.659f, 0.576f, 0.49f),
			0.8f, new Color(1.0f, 0.65f, 0.0f),
			new Color(0.6f, 0.3f, 0.0f), 6, 45f, "MERCURY",
			"0.378", "167");
	
	private Level(float gravity, float slipProbability, float meltProbability, Color blockColor, float shootingEnemyProbability, Color enemyColor, Color shootingEnemyColor, int enemyFrequency, float maxGroundTilt, String planetName, String actualGravity, String actualTemp) {
		this.gravity = gravity;
		this.slipProbability = slipProbability;
		this.meltProbability = meltProbability;
		this.blockColor = blockColor;
		this.shootingEnemyProbability = shootingEnemyProbability;
		this.enemyColor = enemyColor;
		this.shootingEnemyColor = shootingEnemyColor;
		this.enemyFrequency = enemyFrequency;
		this.maxGroundTilt = maxGroundTilt;
		this.planetName = planetName;
		this.actualGravity = actualGravity;
		this.actualTemp = actualTemp;
	}
	
	private float gravity;
	private float slipProbability, meltProbability;
	private Color blockColor;
	private float shootingEnemyProbability;
	private Color enemyColor;
	private Color shootingEnemyColor;
	private int enemyFrequency;
	private float maxGroundTilt;
	private String planetName;
	private String actualGravity;
	private String actualTemp;
	
	public float getGravity() {
		return gravity;
	}
	
	public float getSlipProbability() {
		return slipProbability;
	}
	
	public float getMeltProbability() {
		return meltProbability;
	}
	
	public Color getBlockColor() {
		return blockColor;
	}
	
	public float getShootingEnemyProbability() {
		return shootingEnemyProbability;
	}
	
	public Color getEnemyColor() {
		return enemyColor;
	}
	
	public Color getShootingEnemyColor() {
		return shootingEnemyColor;
	}
	
	public int getEnemyFrequency() {
		return enemyFrequency;
	}
	
	public float getMaxGroundTilt() {
		return maxGroundTilt;
	}
	
	public String getPlanetName() {
		return planetName;
	}
	
	public String getActualGravity() {
		return actualGravity;
	}
	
	public String getActualTemp() {
		return actualTemp;
	}
}
