package com.exchange;

public class ExchangeConstants {

	/** Maximum decimal precision supported by exchange, currently configured for 4 decimal places */
	public static long MAX_DECIMAL_PRECISION = 10000l;

	/** Initial capacity for price levels in an order book */
	public static int INITIAL_NUMBER_FOR_PRICE_LEVELS = 1000;

	/** Initial capacity for number of order books */
	public static int INITIAL_NUMBER_OF_SYMBOLS = 1000;

	/** Initial capacity for Orders at Exchange */
	public static int INTIAL_CAPACITY_FOR_ORDERS = 1000;

}
