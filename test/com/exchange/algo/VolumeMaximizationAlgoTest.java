	package com.exchange.algo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.exchange.Exchange;
import com.exchange.common.TestBase;
import com.exchange.orderbook.OrderBook;

class VolumeMaximizationAlgoTest extends TestBase{

	@Test
	void testVolumeMaximizationOnEmptyOrderBook() {
		VolumeMaximizationAlgo volumeMax = new VolumeMaximizationAlgo();
		MatchingResult result = volumeMax.execute(new OrderBook(10), DECIMAL_PRECISION);
		
		assertNotNull(volumeMax.name());
		assertFalse(result.matched());
		assertEquals(0, result.getMatchingVolume());
		assertEquals(0.0, result.getMatchingPrice());		
	}

	@Test
	void testVolumeMaximizationOnOneSidedOrderBook() {
		VolumeMaximizationAlgo volumeMax = new VolumeMaximizationAlgo();
		Exchange exchange = new Exchange();
		exchange.sendOrder(createBuyOrder(1000, 99.0));
		
		assertOrderBookAsExpected(
				"Buy		 	|	Sell			\n" + 
				"1000@99.0	| 				", exchange.getBookForSymbol(DEFAULT_SYMBOL));
		
		MatchingResult result = exchange.executeMatchingAlgo(volumeMax, DEFAULT_SYMBOL);
		
		assertFalse(result.matched());
		assertEquals(0, result.getMatchingVolume());
		assertEquals(0.0, result.getMatchingPrice());	
	}
	
	@Test
	void testVolumeMaximizationOnOrderBookWithNoMatch() {
		VolumeMaximizationAlgo volumeMax = new VolumeMaximizationAlgo();
		Exchange exchange = new Exchange();
		exchange.sendOrder(createBuyOrder(1000, 99.0));
		exchange.sendOrder(createSellOrder(1000, 100.0));
		
		assertOrderBookAsExpected(
				"Buy		 	|	Sell			\n" + 
				"		 	| 1000@100.0		\n" + 
				"1000@99.0	| 				", exchange.getBookForSymbol(DEFAULT_SYMBOL));
		
		MatchingResult result = exchange.executeMatchingAlgo(volumeMax, DEFAULT_SYMBOL);
		
		assertFalse(result.matched());
		assertEquals(0, result.getMatchingVolume());
		assertEquals(0.0, result.getMatchingPrice());		
	}
	

	@Test
	void testVolumeMaximizationOnOrderBookWithMatchingOrdersAtOnePriceLevel() {
		VolumeMaximizationAlgo volumeMax = new VolumeMaximizationAlgo();
		Exchange exchange = new Exchange();
		exchange.sendOrder(createBuyOrder(1000, 100.0));
		exchange.sendOrder(createSellOrder(1000, 100.0));
		
		assertOrderBookAsExpected("Buy		 |	Sell			\n" + 
								"1000@100.0	 | 1000@100.0	", exchange.getBookForSymbol(DEFAULT_SYMBOL));
		
		MatchingResult result = exchange.executeMatchingAlgo(volumeMax, DEFAULT_SYMBOL);
		assertTrue(result.matched());
		assertEquals(1000, result.getMatchingVolume());
		assertEquals(100.0, result.getMatchingPrice());		
	}

	@Test
	void testVolumeMaximizationWithMatchingOrdersAtTwoPriceLevel() {
		VolumeMaximizationAlgo volumeMax = new VolumeMaximizationAlgo();
		Exchange exchange = new Exchange();
		exchange.sendOrder(createBuyOrder(4000, 102.0));
		
		exchange.sendOrder(createSellOrder(5000, 99.0));
		
		assertOrderBookAsExpected("Buy		 |	Sell			\n" + 
								"4000@102.0	 | 		 		\n" + 
								"		 	 | 5000@99.0	"	, exchange.getBookForSymbol(DEFAULT_SYMBOL));
		
		MatchingResult result = exchange.executeMatchingAlgo(volumeMax, DEFAULT_SYMBOL);
		assertTrue(result.matched());
		assertEquals(4000, result.getMatchingVolume());
		assertEquals(102.0, result.getMatchingPrice());		
	}
	
	@Test
	void testVolumeMaximizationOnOrderBookWithMatchingOrdersAtMultiplePriceLevel() {
		VolumeMaximizationAlgo volumeMax = new VolumeMaximizationAlgo();
		Exchange exchange = new Exchange();
		exchange.sendOrder(createBuyOrder(1000, 99.0));
		exchange.sendOrder(createBuyOrder(1000, 100.0));
		exchange.sendOrder(createBuyOrder(1000, 101.0));
		exchange.sendOrder(createBuyOrder(1000, 102.0));
		
		exchange.sendOrder(createSellOrder(2000, 100.0));
		
		assertOrderBookAsExpected("Buy		 |	Sell			\n" + 
								"1000@102.0	 | 		 		\n" + 
								"1000@101.0	 | 		 		\n" + 
								"1000@100.0	 | 2000@100.0	\n" + 
								"1000@99.0	 | 		 		\n", exchange.getBookForSymbol(DEFAULT_SYMBOL));
		
		MatchingResult result = exchange.executeMatchingAlgo(volumeMax, DEFAULT_SYMBOL);
		assertTrue(result.matched());
		assertEquals(2000, result.getMatchingVolume());
		assertEquals(101.0, result.getMatchingPrice());		
	}
	
	@Test
	void testVolumeMaximizationWithMatchingOrdersAtFourPriceLevel() {
		VolumeMaximizationAlgo volumeMax = new VolumeMaximizationAlgo();
		Exchange exchange = new Exchange();
		exchange.sendOrder(createBuyOrder(1000, 99.0));
		exchange.sendOrder(createBuyOrder(1000, 100.0));
		exchange.sendOrder(createBuyOrder(1000, 101.0));
		exchange.sendOrder(createBuyOrder(1000, 102.0));
		
		exchange.sendOrder(createSellOrder(2000, 99.0));
		exchange.sendOrder(createSellOrder(2000, 100.0));
		exchange.sendOrder(createSellOrder(2000, 101.0));
		exchange.sendOrder(createSellOrder(2000, 102.0));
		
		assertOrderBookAsExpected("Buy		 |	Sell			\n" + 
								"1000@102.0	 | 2000@102.0	\n" + 
								"1000@101.0	 | 2000@101.0	\n" + 
								"1000@100.0	 | 2000@100.0	\n" + 
								"1000@99.0	 | 2000@99.0", exchange.getBookForSymbol(DEFAULT_SYMBOL));
		
		MatchingResult result = exchange.executeMatchingAlgo(volumeMax, DEFAULT_SYMBOL);
		assertTrue(result.matched());
		assertEquals(3000, result.getMatchingVolume());
		assertEquals(100.0, result.getMatchingPrice());		
	}	
	
}
