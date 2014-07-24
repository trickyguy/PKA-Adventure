package com.pkadev.pkaadventure.utils;

import java.util.HashMap;
import java.util.Random;

public class MathUtil {
	
	private static HashMap<String, Double> doubleValueMap = new HashMap<String, Double>();
	private static HashMap<String, Integer> intValueMap = new HashMap<String, Integer>();
	
	private static Random random = new Random();
	
	/**
	 * @param reference "exp, woody_damage"
	 * @return level^2 * referencemultiplier + referenceyoffset
	 */
	public static int getValue(int level, String reference) {
		double lowest = ((Math.pow(level, 2) * getDouble(reference + "_multiplier")) + getInt(reference + "_yoffset"));
		double range = getDouble(reference + "_range");
		if (range == 0d) 
			return (int) lowest;
		
		double highest = lowest * getDouble(reference + "_range");
		return (int) lowest + random.nextInt((int) highest);
	}
	
	/**
	 * @param reference "skillexpreq-mult, expreq-mult"
	 * @return
	 */
	public static double getDouble(String reference) {
		if (doubleValueMap.containsKey(reference))
			return doubleValueMap.get(reference);
		else {
			double d = FileUtil.getDoubleValueFromConfig(FileUtil.config, "Math." + reference);
			setDouble(reference, d);
			return d;
		}
	}
	
	/**
	 * @param reference "skillexpreq-yoffset, expreq-yoffset"
	 * @return
	 */
	public static int getInt(String reference) {
		if (intValueMap.containsKey(reference))
			return intValueMap.get(reference);
		else {
			int i = FileUtil.getIntValueFromConfig(FileUtil.config, "Math." + reference);
			setInt(reference, i);
			return i;
		}
	}
	
	public static int[] getArray(int level, String[] references) {
		int[] array = new int[references.length];
		for (int i = 0; i < references.length; i++) {
			array[i] = getValue(level, references[i]);
		}
		return array;
	}
	
	private static void setDouble(String reference, double d) {
		doubleValueMap.put(reference, Double.valueOf(d));
	}
	
	private static void setInt(String reference, int i) {
		intValueMap.put(reference, Integer.valueOf(i));
	}
}
