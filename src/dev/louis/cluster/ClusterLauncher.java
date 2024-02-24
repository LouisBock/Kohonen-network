package dev.louis.cluster;

import dev.louis.gliders.input.ClusterConfig;

public class ClusterLauncher {
	
	private static ClusterApp game;
	
	public static void main(String[] args) {
		setGame(1);
	}
	
	public static void setGame(int config) {
		if(game != null) game.stopRunning();
		
		if(config > 0 && config <= 8) {
			ClusterConfig.create("res//clusterConfig" + config + ".txt");
		} else {
			ClusterConfig.create("res//clusterConfig1.txt");
		}
		
		game = new ClusterApp();
		game.start();
	}
}
