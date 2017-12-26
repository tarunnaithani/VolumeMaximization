	package com.exchange.algo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.exchange.Exchange;
import com.exchange.common.TestBase;
import com.exchange.data.Execution;
import com.exchange.orderbook.OrderBook;
import com.exchange.util.ExchangeUtils;

class VolumeMaximizationAlgoTest extends TestBase{

	@Test
	void testVolumeMaximizationOnEmptyOrderBook() {
		VolumeMaximizationAlgo volumeMax = new VolumeMaximizationAlgo();
		MatchingResult result = volumeMax.execute(new OrderBook(10));
		
		assertNotNull(volumeMax.name());
		assertFalse(result.matched());
		assertEquals(0, result.getVolume());
		assertEquals(0.0, result.getPrice());		
	}

	@Test
	void testVolumeMaximizationOnOneSidedOrderBook() {
		VolumeMaximizationAlgo volumeMax = new VolumeMaximizationAlgo();
		Exchange exchange = new Exchange();
		exchange.sendOrder(createBuyOrder(1000, 99.0));
		
		assertOrderBookAsExpected(
				"Buy		 	|	Sell			\n" + 
				"1000@99.0	| 				", exchange.getBookForSymbol(DEFAULT_SYMBOL));
		
		MatchingResult result = exchange.runMatchingAlgo(volumeMax, DEFAULT_SYMBOL);
		
		assertFalse(result.matched());
		assertEquals(0, result.getVolume());
		assertEquals(0.0, result.getPrice());	
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
		
		MatchingResult result = exchange.runMatchingAlgo(volumeMax, DEFAULT_SYMBOL);
		
		assertFalse(result.matched());
		assertEquals(0, result.getVolume());
		assertEquals(0.0, result.getPrice());		
	}
	

	@Test
	void testVolumeMaximizationOnOrderBookWithMatchingOrdersAtOnePriceLevel() {
		VolumeMaximizationAlgo volumeMax = new VolumeMaximizationAlgo();
		Exchange exchange = new Exchange();
		exchange.sendOrder(createBuyOrder(1000, 100.0));
		exchange.sendOrder(createSellOrder(1000, 100.0));
		
		assertOrderBookAsExpected("Buy		 |	Sell			\n" + 
								"1000@100.0	 | 1000@100.0	", exchange.getBookForSymbol(DEFAULT_SYMBOL));
		
		MatchingResult result = exchange.runMatchingAlgo(volumeMax, DEFAULT_SYMBOL);
		assertTrue(result.matched());
		assertEquals(1000, result.getVolume());
		assertEquals(100.0, ExchangeUtils.convertPriceToDouble(result.getPrice(), DECIMAL_PRECISION));		
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
		
		MatchingResult result = exchange.runMatchingAlgo(volumeMax, DEFAULT_SYMBOL);
		assertTrue(result.matched());
		assertEquals(4000, result.getVolume());
		assertEquals(102.0, ExchangeUtils.convertPriceToDouble(result.getPrice(), DECIMAL_PRECISION));		
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
		
		MatchingResult result = exchange.runMatchingAlgo(volumeMax, DEFAULT_SYMBOL);
		assertTrue(result.matched());
		assertEquals(2000, result.getVolume());
		assertEquals(101.0, ExchangeUtils.convertPriceToDouble(result.getPrice(), DECIMAL_PRECISION));		
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
		
		MatchingResult result = exchange.runMatchingAlgo(volumeMax, DEFAULT_SYMBOL);
		assertTrue(result.matched());
		assertEquals(3000, result.getVolume());
		assertEquals(100.0, ExchangeUtils.convertPriceToDouble(result.getPrice(), DECIMAL_PRECISION));		
		List<Execution> exec = exchange.executeMatch(DEFAULT_SYMBOL, result.getPrice(), result.getVolume());
	}	
	
}
