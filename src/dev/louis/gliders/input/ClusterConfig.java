package dev.louis.gliders.input;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class ClusterConfig {
	private static LinkedList<String> input = new LinkedList<String>();
    private static HashMap<String, String> hashMap = new HashMap<String, String>();
    
    public static int fps, prototypeCount, dataCount, clusterCount;
    public static boolean randomPos;
    public static float radius, timeToMove;
	public static double neighborStrength, learningRate;
    
    /**
     * this method reads the gameConfig from the file of the given path 
     * @param path this is needed because by running this method in different projects like core or test the path differs
     */
    public static void create(String path) {
    	read(path);
    	write();
    }

	private static void read(String path) {
		//clear old input
    	input.clear();
    	hashMap.clear();
    	
        try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			String line = in.readLine();
			while(line != null) {
				input.add(line);
				line = in.readLine();
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
        //creating key-value-pairs
        for(String line : input) {
        	String[] configEntry = line.split(":");
        	hashMap.put(configEntry[0], configEntry[1]);
        }
	}
    
	private static void write() {
		fps = Integer.parseInt(hashMap.get("fps"));
		prototypeCount = Integer.parseInt(hashMap.get("prototypeCount"));
		dataCount = Integer.parseInt(hashMap.get("dataCount"));
		clusterCount = Integer.parseInt(hashMap.get("clusterCount"));
		
		randomPos = Boolean.parseBoolean(hashMap.get("randomPos"));
		
		radius = Float.parseFloat(hashMap.get("radius"));
		timeToMove = Float.parseFloat(hashMap.get("timeToMove"));
		
		neighborStrength = Double.parseDouble(hashMap.get("neighborStrength"));
		learningRate = Double.parseDouble(hashMap.get("learningRate"));
	}
}

