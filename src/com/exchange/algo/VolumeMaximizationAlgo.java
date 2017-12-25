package com.exchange.algo;

import com.exchange.orderbook.OrderBook;

public class VolumeMaximizationAlgo implements ExchangeAlgo{

	public final String name = "Volume Maximization";
	private long highestVolume = 0;
	private double price = 0.0;
	
	@Override
	public String name() {
		return name;
	}

	@Override
	public String result() {
		return "Highest volume," + highestVolume + " at price, " + price;
	}

	@Override
	public boolean execute(OrderBook orderBook) {
		return false;
	}
	
	

}
