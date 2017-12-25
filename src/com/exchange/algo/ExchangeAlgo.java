package com.exchange.algo;

import com.exchange.orderbook.OrderBook;

public interface ExchangeAlgo {
	
	public String name();
	
	public String result();
	
	public boolean execute(OrderBook orderBook);

}
