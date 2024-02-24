package dev.louis.cluster;

import java.awt.Color;
import java.awt.Graphics;

import dev.louis.gliders.input.ClusterConfig;

public abstract class Point {

	protected Color col, originalColor;
	protected float radius;
	protected float x, y;
	
	public Point(float x, float y, Color originalColor) {
		radius = ClusterConfig.radius;
		this.x = x;
		this.y = y;
		this.originalColor = originalColor;
		resetColor();
	}

	public void render(Graphics g) {
		g.setColor(col);
		g.fillOval((int) x, (int) y, (int) radius, (int) radius);
	}
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public Color getCol() {
		return col;
	}

	public void setCol(Color col) {
		this.col = col;
	}
	
	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public void resetColor() {
		col = originalColor;
	}
	
	public void resetRadius() {
		radius = ClusterConfig.radius;
	}
}
