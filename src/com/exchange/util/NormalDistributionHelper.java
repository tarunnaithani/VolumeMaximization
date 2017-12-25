package com.exchange.util;

import java.util.Random;

/**
 * Helper class to draw values for problem simulation
 *
 */
public class NormalDistributionHelper {
	
	private static final Random random = new Random(System.currentTimeMillis());	
	
	public static long getLong(double mean, double stdDeviation) {
		return (long)((random.nextGaussian() * stdDeviation ) + mean);
	}
	
	public static double getDoubleWithPrecision(double mean, double stdDeviation, int precision) {
		double value = (random.nextGaussian() * stdDeviation ) + mean;
		double multiplier = Math.pow(10, precision);
		return (double)((long)(value * multiplier)) /multiplier;
	}
}
