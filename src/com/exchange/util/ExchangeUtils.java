package com.exchange.util;

import com.exchange.ExchangeConstants;

public class ExchangeUtils {

	public static long convertPriceToLong(double price) {
		return (long)(price * ExchangeConstants.MAX_DECIMAL_PRECISION);
	}

	public static double convertPriceToDouble(Long price) {
		return ((double)price)/ExchangeConstants.MAX_DECIMAL_PRECISION;
	}

}
