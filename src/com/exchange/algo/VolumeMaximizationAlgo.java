package com.exchange.algo;

import java.util.TreeSet;

import com.exchange.data.Side;
import com.exchange.orderbook.OrderBook;
import com.exchange.util.ExchangeUtils;

/**
 * Matching Algorithm to traverse through a given book and find highest price at which maximum volume can be matched 
 *
 */
public class VolumeMaximizationAlgo implements MatchingAlgo {

	public final String name = "Volume Maximization";

	@Override
	public String name() {
		return name;
	}

	@Override
	public MatchingResult execute(OrderBook orderBook) {
		long highestVolume = 0;
		double matchingPrice = 0.0;
		
		//Get all prices from book in descending order 
		TreeSet<Long> allPrices = new TreeSet<>(Side.Buy.getComparator());
		TreeSet<Long> bids = orderBook.getBids();
		TreeSet<Long> asks = orderBook.getAsks();
		allPrices.addAll(bids);
		allPrices.addAll(asks);

		for (Long price : allPrices) {
			// At each price level get buy and sell qty available
			long buyQty = 0;
			for (Long bidPrice : bids.headSet(price, true)) 
				buyQty = buyQty + orderBook.getAvailableBuyQtyAtPrice(bidPrice);
			
			long sellQty = 0;
			for (Long askPrice : asks.headSet(price, true)) 
				sellQty = sellQty + orderBook.getAvailableSellQtyAtPrice(askPrice);
			
			//Quantity that can be matched is minimum of available buy and sell qty
			long totalMatchingQty = Math.min(buyQty, sellQty);

			//If new volume is higher than previously seen volume and use it as maximum along with price
			if (totalMatchingQty > highestVolume) {
				highestVolume = totalMatchingQty;
				matchingPrice = ExchangeUtils.convertPriceToDouble(price);
			}
		}

		if (matchingPrice > 0 && highestVolume > 0)
			return new MatchingResult(true, matchingPrice, highestVolume);
		else
			return new MatchingResult(false, matchingPrice, highestVolume);
	}
}
