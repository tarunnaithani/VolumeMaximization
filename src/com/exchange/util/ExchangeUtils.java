package com.exchange.util;

public class ExchangeUtils {
	
	public static long convertPriceToLong(double price, int precision) {
		precision = Math.min(precision, 8);
		long multiplier = (long)Math.pow(10, precision);
		return (long)(price * multiplier);
	}

	public static double convertPriceToDouble(Long price, int precision) {
		precision = Math.min(precision, 8);
		long multiplier = (long)Math.pow(10, precision);
		return ((double)price)/multiplier;
	}

}
