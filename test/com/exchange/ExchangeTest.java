package com.exchange;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.exchange.algo.ExchangeAlgo;
import com.exchange.common.TestHelper;
import com.exchange.data.Order;
import com.exchange.data.Side;
import com.exchange.orderbook.OrderBook;

class ExchangeTest extends TestHelper{

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
		ExchangeAlgo algo = new ExchangeAlgo() {
			
			@Override
			public String result() {
				return "Success";
			}
			
			@Override
			public String name() {
				return "TestAlgo";
			}
			
			@Override
			public boolean execute(OrderBook orderBook) {
				return true;
			}
		};
		Exchange exchange = new Exchange();
		Order order = new Order(getOrderId(), DEFAULT_SYMBOL, Side.Buy, 1000, 10.0);
		assertTrue(exchange.sendOrder(order));
		assertTrue(exchange.sendOrder(new Order(getOrderId(), DEFAULT_SYMBOL, Side.Sell, 1000, 11.0)));
		
		assertOrderBookAsExpected("Buy		|	Sell		\n" + 
								"		 	| 1000@11.0	\n" + 
								"1000@10.0	| 		 \n" , exchange.getBookForSymbol(DEFAULT_SYMBOL));
				
		assertTrue(exchange.executeMatchingAlgo(algo, DEFAULT_SYMBOL));
		assertEquals("Success", algo.result());
		
		assertFalse(exchange.executeMatchingAlgo(algo, "0001.HK"));
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
