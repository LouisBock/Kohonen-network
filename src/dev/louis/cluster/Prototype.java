package dev.louis.cluster;

import java.awt.Color;

import dev.louis.gliders.input.ClusterConfig;

public class Prototype extends Point {
	
	private double dir;
	private double speed;
	private double distToData;
	
	public Prototype(float x, float y, Color originalColor) {
		super(x, y, originalColor);
		radius *= 3;
	}
	
	public void tick() {
		x += speed*(Math.cos(dir));
		y += speed*(Math.sin(dir));
	}

	public void setDir(double dir) {
		this.dir = dir;
	}
	
	public void calculateSpeed(double strength) {
		this.speed = strength *(distToData/ClusterConfig.fps);
	}

	public void setDistToData(double distToData) {
		this.distToData = distToData;
	}
}
