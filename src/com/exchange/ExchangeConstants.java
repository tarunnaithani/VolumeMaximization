package com.exchange;

public interface ExchangeConstants {
	/** Initial capacity for price levels in an order book */
	public static int DEFAULT_DECIMAL_PRECISION = 4;
	
	/** Initial execution id counter used across exchange */
	public static int EXCHANGE_EXECUTION_ID = 0;
	
	/** Initial capacity for price levels in an order book */
	public static int DEFAULT_INITIAL_ORDERBOOK_CAPACITY = 1000;

	/** Initial capacity for number of order books */
	public static int DEFAULT_INITIAL_SYMBOLS_COUNT = 1000;

	/** Initial capacity for Orders at Exchange */
	public static int DEFAULT_INITIAL_ORDERS_COUNT = 1000;
}
