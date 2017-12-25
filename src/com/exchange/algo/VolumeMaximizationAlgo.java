package com.exchange.algo;

import java.util.TreeSet;

import com.exchange.data.Side;
import com.exchange.orderbook.OrderBook;
import com.exchange.util.ExchangeUtils;

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
		
		TreeSet<Long> allPrices = new TreeSet<>(Side.Buy.getComparator());
		TreeSet<Long> bids = orderBook.getBids();
		TreeSet<Long> asks = orderBook.getAsks();
		allPrices.addAll(bids);
		allPrices.addAll(asks);

		for (Long price : allPrices) {
			System.out.println("Price," + price);
			long buyQty = 0;
			for (Long bidPrice : bids.headSet(price, true)) {
				System.out.println("Bid Price," + bidPrice);
				buyQty = buyQty + orderBook.getAvailableBuyQtyAtPrice(bidPrice);
			}
			long sellQty = 0;
			for (Long askPrice : asks.headSet(price, true)) {
				System.out.println("Ask Price," + askPrice);
				sellQty = sellQty + orderBook.getAvailableSellQtyAtPrice(askPrice);
			}
			long totalMatchingQty = Math.min(buyQty, sellQty);
			System.out.println();
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
