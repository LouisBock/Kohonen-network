package dev.louis.cluster;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

import dev.louis.gliders.display.Display;
import dev.louis.gliders.input.ClusterConfig;
import dev.louis.gliders.input.KeyManager;
import dev.louis.gliders.input.MouseManager;

public class ClusterApp implements Runnable{
	
	private Display display;
	private int width, height, clusterCount;
	private float timeToMove;
	private double neighborStrength;
	private double learningRate;
	public String title;
	
	private boolean running = false;
	private Thread thread;
	private int fps;
	
	private BufferStrategy bs;
	private Graphics g;
	private final Color clearCol = new Color(0, 0, 0, 16);
	
	//Input
	private KeyManager keyManager;
	private MouseManager mouseManager;
	
	private Prototype[][] prototypes;
	private Datapoint[] data;
	private int dataIndex = 0;
	
	public ClusterApp() {
		title = "Clustersearch";
		fps = ClusterConfig.fps;
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		timeToMove = ClusterConfig.timeToMove;
		neighborStrength = ClusterConfig.neighborStrength;
		learningRate = ClusterConfig.learningRate;
		clusterCount = ClusterConfig.clusterCount;
		prototypes = new Prototype[ClusterConfig.prototypeCount][ClusterConfig.prototypeCount];
		data = new Datapoint[ClusterConfig.dataCount];
		
		keyManager = new KeyManager();
		mouseManager = new MouseManager();
	}
	
	private void init() {
		display = new Display(title, width, height);
		display.getFrame().addKeyListener(keyManager);
		display.getCanvas().addMouseListener(mouseManager);
		
		display.getCanvas().createBufferStrategy(3);
		bs = display.getCanvas().getBufferStrategy();
		//background black
		g = bs.getDrawGraphics();
		Color fullClear = new Color(clearCol.getRed(), clearCol.getGreen(), clearCol.getBlue(), 255);
		g.setColor(fullClear);
		g.fillRect(0, 0, width, height);
		bs.show();
		g.dispose();
		
		initPoints();
	}
	
	private void tick() {
		keyManager.tick();
		mouseManager.tick();
		
		for(Prototype[] ps : prototypes) {
			for(Prototype p : ps) {
				p.tick();
			}
		}
		
		if(keyManager.space) running = false;
	}
	
	private void render() {
		g = bs.getDrawGraphics();
		
		g.setColor(clearCol);
		g.fillRect(0, 0, width, height);
		
		//Draw Here!
		
		for(Datapoint d : data) {
			d.render(g);
		}
		
		for(Prototype[] ps : prototypes) {
			for(Prototype p : ps) {
				p.render(g);
			}
		}
		
		//End Drawing!
		
		bs.show();
		g.dispose();
	}
	
	private void step() {
		data[dataIndex].resetColor();
		data[dataIndex].resetRadius();
	
		//dataIndex
		if(dataIndex >= data.length -1) {
			neighborStrength *= 0.9;
			dataIndex = 0;
		} else {
			dataIndex++;
		}
		
		data[dataIndex].setCol(new Color(0, 255, 0));
		data[dataIndex].setRadius(ClusterConfig.radius * 3);
		
		calculateDirections(data[dataIndex]);
		int[] closest = calculateDistancesAndGetClosest(data[dataIndex]);
		calculateStrengths(closest);
	}
	
	public void run() {
		init();
		long second = 1000000000;
		double timePerTick = second / fps;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		
		while(running) {
			now = System.nanoTime();
			delta += (now - lastTime)/ timePerTick;
			timer += now - lastTime;
			lastTime = now;
			
			if(delta >= 1) {
				tick();
				render();
				delta--;
			}
			
			if (timer >= second * timeToMove) {
				step();
				timer = 0;
			}
		}
		stop();
	}
	
	private void initPoints() {
		for(int j = 0; j < prototypes.length; j++) {
			for(int i = 0; i < prototypes[j].length; i++) {
				if(ClusterConfig.randomPos) {
					prototypes[j][i] = new Prototype((float) (Math.random() * width), (float) (Math.random() * height),
							new Color((float)j/prototypes.length, 0.f, (float)i/prototypes[j].length * 0.8f + 0.2f));
				} else {
					prototypes[j][i] = new Prototype((float)j/prototypes.length * width, (float)i/prototypes[j].length * height, 
							new Color((float)j/prototypes.length, 0.f, (float)i/prototypes[j].length * 0.8f + 0.2f));
				}
			}
		}
			
		for(int i = 0; i < data.length; i++) {
			
			float x = (float) (5*i%clusterCount)/clusterCount * width;
			float y = (float) (i%clusterCount)/clusterCount * height;
			data[i] = new Datapoint((float) (x + Math.random() * width * 0.1), (float) (y + Math.random() * height * 0.1), new Color(255, 255, 255));
			//no clustering
			//data[i] = new Datapoint((float) (Math.random() * width), (float) (Math.random() * height), new Color(255, 255, 255));
		}
		
		
		
		data[dataIndex].setCol(new Color(0, 255, 0));
		data[dataIndex].setRadius(10);
	}
	
	private void calculateDirections(Datapoint point) {
		for(Prototype[] ps : prototypes) {
			for(Prototype p : ps) {
				double dir = Math.atan((point.getY() - p.getY())/(point.getX() - p.getX()));
				if(point.getX() - p.getX() < 0) {
					dir += Math.PI;
				}
				p.setDir(dir);
			}
		}
	}
	
	private int[] calculateDistancesAndGetClosest(Datapoint point) {
		int[] closest = new int[2];
		double minDist = Double.MAX_VALUE;
		
		for(int j = 0; j < prototypes.length; j++) {
			for(int i = 0; i < prototypes[j].length; i++) {
				Prototype p = prototypes[j][i];
				double dist = Math.sqrt(Math.pow(point.getX() - p.getX(), 2) + Math.pow(point.getY() - p.getY(), 2));
				p.setDistToData(dist);
				if(dist < minDist) {
					closest[0] = j;
					closest[1] = i;
					minDist = dist;
				}
			}
		}
		
		return closest;
	}
	
	private void calculateStrengths(int[] indices) {
		for(int j = 0; j < prototypes.length; j++) {
			for(int i = 0; i < prototypes[j].length; i++) {
				int dist = Math.abs(indices[0] - j) + Math.abs(indices[1] - i);
				//e^(-dist/2*neighborStrength^2)
				double strength =  Math.pow(Math.E, ((-dist)/(2*Math.pow(neighborStrength, 2))));
				prototypes[j][i].calculateSpeed(strength*learningRate);
			}
		}
	}
	
	public KeyManager getKeyManager() {
		return keyManager;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public synchronized void start() {
		if(running) 
			return;
		
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() {
		running = false;
		display.getFrame().dispose();
		try {
			if(thread.isAlive()) thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void stopRunning() {
		running = false;
	}
}