package com.exchange.util;

import java.util.Random;

public class NormalDistribution {
	
	private static Random random = new Random();
	
	public static long getLong(double stdDeviation, double mean) {
		return (long)((random.nextGaussian() * stdDeviation ) + mean);
	}
	
	public static double getDouble(double stdDeviation, double mean, int precision) {
		double value = (random.nextGaussian() * stdDeviation ) + mean;
		double multiplier = Math.pow(10, precision);
		return (double)((long)(value * multiplier)) /multiplier;
	}
}
