package com.exchange.algo;

import com.exchange.orderbook.OrderBook;

public interface MatchingAlgo {
	
	/**
	 * @return name of Matching Algorithm
	 */
	public String name();
		
	/**
	 * Runs Algorithm on the passed order book
	 * @param orderBook on which algorithm needs to be run
	 * @return result of execution
	 */
	public MatchingResult execute(OrderBook orderBook);

}
