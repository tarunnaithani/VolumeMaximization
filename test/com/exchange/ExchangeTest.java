package com.exchange;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.exchange.algo.MatchingAlgo;
import com.exchange.algo.MatchingResult;
import com.exchange.common.TestBase;
import com.exchange.data.Order;
import com.exchange.data.Side;
import com.exchange.orderbook.OrderBook;

class ExchangeTest extends TestBase{

	@Test
	void testOrderForMultipleSymbols() {
		Exchange exchange = new Exchange();
		assertTrue(exchange.sendOrder(new Order(getOrderId(), DEFAULT_SYMBOL, Side.Buy, 1000, 10.0)));
		assertTrue(exchange.sendOrder(new Order(getOrderId(), DEFAULT_SYMBOL, Side.Sell, 1000, 11.0)));
		
		assertTrue(exchange.sendOrder(new Order(getOrderId(), "0001.HK", Side.Buy, 2000, 21.0)));
		assertTrue(exchange.sendOrder(new Order(getOrderId(), "0001.HK", Side.Sell, 2000, 22.0)));
		
		assertOrderBookAsExpected("Buy		|	Sell		\n" + 
								"		 	| 1000@11.0	\n" + 
								"1000@10.0	| 		 \n" , exchange.getBookForSymbol(DEFAULT_SYMBOL));
		assertOrderBookAsExpected("Buy		|	Sell		\n" + 
								"		 	| 2000@22.0	\n" + 
								"2000@21.0	| 		 \n" , exchange.getBookForSymbol("0001.HK"));
	}

	@Test
	void testOrderValidation() {
		Exchange exchange = new Exchange();
		Order order = new Order(getOrderId(), DEFAULT_SYMBOL, Side.Buy, -1000, 10.0);
		assertFalse(exchange.sendOrder(order));
		
		order = new Order(getOrderId(), DEFAULT_SYMBOL, Side.Buy, 10,-10.0);
		assertFalse(exchange.sendOrder(order));
		
		order = new Order(getOrderId(), DEFAULT_SYMBOL, Side.Buy, 10,0.0);
		assertFalse(exchange.sendOrder(order));
		
		order = new Order(getOrderId(), DEFAULT_SYMBOL, Side.Buy, 0, 10.0);
		assertFalse(exchange.sendOrder(order));
	}

	@Test
	void testMaxMinDecimalPrecisionInPrice() {
		Exchange exchange = new Exchange(5);
		
		double minPrecision = (double)1/Math.pow(10, 5);
		double maxPrecision = (double)1/(Math.pow(10, 5) * 10) - minPrecision;
		double minPrice = 1 + minPrecision;
		double maxPrice = 1 + maxPrecision;
		
		exchange.sendOrder(createBuyOrder(2000, minPrice));
		exchange.sendOrder(createBuyOrder(2000, maxPrice));
		
		exchange.sendOrder(createSellOrder(2000, minPrice));
		exchange.sendOrder(createSellOrder(2000, maxPrice));
		assertOrderBookAsExpected(
				"Buy		 		|	Sell			\n" + 
				"2000@1.00001 	| 2000@1.00001	\n" + 
				"2000@0.99999 	| 2000@0.99999", exchange.getBookForSymbol(DEFAULT_SYMBOL), 5);
	}
	
	@Test
	void testDuplicateOrder() {
		Exchange exchange = new Exchange();
		Order order = new Order(getOrderId(), DEFAULT_SYMBOL, Side.Buy, 1000, 10.0);
		assertTrue(exchange.sendOrder(order));
		assertTrue(exchange.sendOrder(new Order(getOrderId(), DEFAULT_SYMBOL, Side.Sell, 1000, 11.0)));
		
		assertOrderBookAsExpected("Buy		|	Sell		\n" + 
								"		 	| 1000@11.0	\n" + 
								"1000@10.0	| 		 \n" , exchange.getBookForSymbol(DEFAULT_SYMBOL));
		
		//Duplicate order
		assertFalse(exchange.sendOrder(order));
		assertOrderBookAsExpected("Buy		|	Sell		\n" + 
				"		 	| 1000@11.0	\n" + 
				"1000@10.0	| 		 \n" , exchange.getBookForSymbol(DEFAULT_SYMBOL));
		
		assertTrue(exchange.cancelOrder(order));
	}
	
	@Test
	void testMatchingAlgo() {
		MatchingAlgo algo = new MatchingAlgo() {
			
			@Override
			public String name() {
				return "TestAlgo";
			}
			
			@Override
			public MatchingResult execute(OrderBook orderBook, int decimalPrecision) {
				return new MatchingResult(true, 1000, 1000);
			}
		};
		Exchange exchange = new Exchange();
		Order order = new Order(getOrderId(), DEFAULT_SYMBOL, Side.Buy, 1000, 10.0);
		assertTrue(exchange.sendOrder(order));
		assertTrue(exchange.sendOrder(new Order(getOrderId(), DEFAULT_SYMBOL, Side.Sell, 1000, 11.0)));
		
		assertOrderBookAsExpected("Buy		|	Sell		\n" + 
								"		 	| 1000@11.0	\n" + 
								"1000@10.0	| 		 \n" , exchange.getBookForSymbol(DEFAULT_SYMBOL));
				
		assertTrue(exchange.executeMatchingAlgo(algo, DEFAULT_SYMBOL).matched());
		
		assertFalse(exchange.executeMatchingAlgo(algo, "0001.HK").matched());
	}
	
	@Test
	void testCancelOrderForMultipleSymbols() {
		Exchange exchange = new Exchange();
		Order order = new Order(getOrderId(), DEFAULT_SYMBOL, Side.Buy, 1000, 10.0);
		assertTrue(exchange.sendOrder(order));
		assertTrue(exchange.sendOrder(new Order(getOrderId(), DEFAULT_SYMBOL, Side.Sell, 1000, 11.0)));
		
		Order order2 = new Order(getOrderId(), "0001.HK", Side.Buy, 2000, 21.0);
		assertTrue(exchange.sendOrder(order2));
		assertTrue(exchange.sendOrder(new Order(getOrderId(), "0001.HK", Side.Sell, 2000, 22.0)));
		
		assertOrderBookAsExpected("Buy		|	Sell		\n" + 
								"		 	| 1000@11.0	\n" + 
								"1000@10.0	| 		 \n" , exchange.getBookForSymbol(DEFAULT_SYMBOL));
		assertOrderBookAsExpected("Buy		|	Sell		\n" + 
								"		 	| 2000@22.0	\n" + 
								"2000@21.0	| 		 \n" , exchange.getBookForSymbol("0001.HK"));
		assertTrue(exchange.cancelOrder(order));
		assertTrue(exchange.cancelOrder(order2));
		assertOrderBookAsExpected("Buy		|	Sell		\n" + 
								"		 	| 1000@11.0	", exchange.getBookForSymbol(DEFAULT_SYMBOL));
		assertOrderBookAsExpected("Buy		|	Sell		\n" + 
								"		 	| 2000@22.0	\n", exchange.getBookForSymbol("0001.HK"));
	}

	@Test
	void testDuplicateCancelOrder() {
		Exchange exchange = new Exchange();
		Order order = new Order(getOrderId(), DEFAULT_SYMBOL, Side.Buy, 1000, 10.0);
		assertTrue(exchange.sendOrder(order));
		assertTrue(exchange.sendOrder(new Order(getOrderId(), DEFAULT_SYMBOL, Side.Sell, 1000, 11.0)));
		
		assertOrderBookAsExpected("Buy		|	Sell		\n" + 
								"		 	| 1000@11.0	\n" + 
								"1000@10.0	| 		 \n" , exchange.getBookForSymbol(DEFAULT_SYMBOL));

		//Cancel non existent symbol
		Order order1 = new Order(order.getOrderId(), "0001.HK", Side.Buy, 1000, 10.0);
		assertFalse(exchange.cancelOrder(order1));
		
		assertTrue(exchange.cancelOrder(order));
		
		//Duplicate cancel
		assertFalse(exchange.cancelOrder(order));
		
	}

}
