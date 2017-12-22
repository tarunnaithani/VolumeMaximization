package com.exchange.util;

import org.junit.jupiter.api.Test;

class NormalDistributionTest {
	
	@Test
	void test() {
		
		int totalCount = 2000000;	
		double[] values = new double[totalCount];
		double sum = 0.0;
		
		for(int i=0; i< totalCount; i++) {
			values[i] = NormalDistribution.getDouble(1.0, 0, 1);
			sum = sum + values[i];
		}
		double mean = sum/ totalCount;
		System.out.println("Mean," + mean);
		sum = 0.0;
		for(double val: values) {
			sum = sum + Math.pow(mean - val, 2);
		}
		
		double std = Math.sqrt(sum/ totalCount);
		System.out.println("srd deviation," + std);
	}

}
