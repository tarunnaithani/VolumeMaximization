package com.exchange.orderbook;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import com.exchange.data.Order;
import com.exchange.data.Side;

class OrderBookTest {
	public static int ORDER_ID = 0;
	
	public static int getOrderId() {
		return ORDER_ID++;
	}

	private Order createBuyOrder(long qty, double price) {
		return new Order(getOrderId(), "0001.HK", Side.Buy, qty, price);
	}
	
	private Order createSellOrder(long qty, double price) {
		return new Order(getOrderId(), "0001.HK", Side.Sell, qty, price);
	}
	
	private void compare(String expected, OrderBook book) {
		System.out.println(book);
		assertEquals(trimmedString(expected), trimmedString(book.toString()));
	}
	
	private String trimmedString(String str) {
		return str.replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
	}
	
	@Test
	void testDuplicateOrder() {
		OrderBook book = new OrderBook(10);
		Order order = createBuyOrder(2000, 100.0004);
		assertEquals(true, book.addOrder(order));
		assertEquals(false,book.addOrder(order));

		compare("Buy				|	Sell \n" + 
				"2000@100.0004	| 		   ", book);
	}
	
	@Test
	void testMultipleBuyOrder() {
		OrderBook book = new OrderBook(10);
		assertEquals(true, book.addOrder(createBuyOrder(2000, 100.0004)));
		assertEquals(true, book.addOrder(createBuyOrder(2000, 100.0003)));
		assertEquals(true, book.addOrder(createBuyOrder(2000, 100.0001)));
		assertEquals(true, book.addOrder(createBuyOrder(2000, 100.0001)));

		compare("Buy				|	Sell \n" + 
				"2000@100.0004	| 		 \n" + 
				"2000@100.0003	| 		 \n" + 
				"4000@100.0001	| 		   ", book);
	}

	@Test
	void testMultipleSellOrder() {
		OrderBook book = new OrderBook(10);
		assertEquals(true, book.addOrder(createSellOrder(2000, 100.0001)));
		assertEquals(true, book.addOrder(createSellOrder(2000, 100.0001)));
		assertEquals(true, book.addOrder(createSellOrder(2000, 100.0002)));
		assertEquals(true, book.addOrder(createSellOrder(2000, 100.0003)));

		compare("Buy		|	Sell			\n" + 
				"		| 2000@100.0003	\n" + 
				"		| 2000@100.0002	\n" + 
				"		| 4000@100.0001	  ", book);
	}
	
	@Test
	void testMultipleBuySellOrder() {
		OrderBook book = new OrderBook(10);
		book.addOrder(createBuyOrder(2000, 100.0004));
		book.addOrder(createBuyOrder(2000, 100.0001) );
		book.addOrder(createBuyOrder(2000, 100.0001) );
		book.addOrder(createBuyOrder(2000, 100.0001));
		
		book.addOrder(createSellOrder(2000, 100.0001));
		book.addOrder(createSellOrder(2000, 100.0002));
		book.addOrder(createSellOrder(2000, 100.0003));
		book.addOrder(createSellOrder(2000, 100.0003));
		book.addOrder(createSellOrder(2000, 100.0003));

		compare("Buy				|	Sell			\n" + 
				"2000@100.0004	| 		 		\n" + 
				"		 		| 6000@100.0003	\n" + 
				"		 		| 2000@100.0002	\n" + 
				"6000@100.0001	| 2000@100.0001	", book);
	}

	@Test
	void testOneOrderWithCancel() {
		OrderBook book = new OrderBook(10);
		Order order = createBuyOrder(2000, 100.0004);
		assertEquals(true, book.addOrder(order));
		
		compare("Buy				|	Sell			\n" + 
				"2000@100.0004	| 		 		\n" 	, book);
		
		assertEquals(true, book.cancelOrder(order));
		compare("", book);
	}
	
	@Test
	void testOneOrderWithCancelWithInvalidId() {
		OrderBook book = new OrderBook(10);
		Order order = createBuyOrder(2000, 100.0004);
		assertEquals(true, book.addOrder(order));
		
		compare("Buy				|	Sell			\n" + 
				"2000@100.0004	| 		 		\n" 	, book);
		
		assertEquals(false, book.cancelOrder(createBuyOrder(2000, 100.0004)));
		compare("Buy				|	Sell			\n" + 
				"2000@100.0004	| 		 		\n" 	, book);
	}
	
	@Test
	void testOneOrderCancelWithInvalidPrice() {
		OrderBook book = new OrderBook(10);
		Order order = createBuyOrder(2000, 100.0004);
		assertEquals(true, book.addOrder(order));
		
		compare("Buy				|	Sell			\n" + 
				"2000@100.0004	| 		 		\n" 	, book);
		
		assertEquals(false, book.cancelOrder(new Order(order.getOrderId(), order.getSymbol(), order.getSide(), order.getQuantity(), 100.00)));
		compare("Buy				|	Sell			\n" + 
				"2000@100.0004	| 		 		\n" 	, book);
	}
	
	@Test
	void testThreeOrderWithCancelOnFirst() {
		OrderBook book = new OrderBook(10);
		Order o1 = createBuyOrder(2000, 100.0004);
		Order o2 = createBuyOrder(2000, 100.0004);
		Order o3 = createBuyOrder(2000, 100.0004); 
		assertEquals(true, book.addOrder(o1));
		assertEquals(true, book.addOrder(o2));
		assertEquals(true, book.addOrder(o3));
		
		
		compare("Buy				|	Sell			\n" + 
				"6000@100.0004	| 		 		", book);
		
		assertEquals(true, book.cancelOrder(o1));
		compare("Buy				|	Sell			\n" + 
				"4000@100.0004	| 		 		", book);
	}
	
	@Test
	void testThreeOrderWithCancelOnSecond() {
		OrderBook book = new OrderBook(10);
		Order o1 = createBuyOrder(2000, 100.0004);
		Order o2 = createBuyOrder(2000, 100.0004);
		Order o3 = createBuyOrder(2000, 100.0004); 
		book.addOrder(o1);
		book.addOrder(o2);
		book.addOrder(o3);
		
		
		compare("Buy				|	Sell			\n" + 
				"6000@100.0004	| 		 		", book);
		
		book.cancelOrder(o2);
		compare("Buy				|	Sell			\n" + 
				"4000@100.0004	| 		 		", book);
	}
	
	@Test
	void testThreeOrderWithCancelOnThird() {
		OrderBook book = new OrderBook(10);
		Order o1 = createBuyOrder(2000, 100.0004);
		Order o2 = createBuyOrder(2000, 100.0004);
		Order o3 = createBuyOrder(2000, 100.0004); 
		book.addOrder(o1);
		book.addOrder(o2);
		book.addOrder(o3);
		
		
		compare("Buy				|	Sell			\n" + 
				"6000@100.0004	| 		 		", book);
		
		book.cancelOrder(o3);
		compare("Buy				|	Sell			\n" + 
				"4000@100.0004	| 		 		", book);
	}
	
	@Test
	void testMultipleBuyAndSellWithAllCancelled() {
		OrderBook book = new OrderBook(10);
		Order b1 = createBuyOrder(2000, 100.0004);
		Order b2 = createBuyOrder(2000, 100.0004);
		Order b3 = createBuyOrder(2000, 100.0004);
		Order b4 = createBuyOrder(2000, 100.0005);
		Order b5 = createBuyOrder(2000, 100.0005);
		book.addOrder(b1);
		book.addOrder(b2);
		book.addOrder(b3);
		book.addOrder(b4);
		book.addOrder(b5);
		
		Order s1 = createSellOrder(2000, 100.0006);
		Order s2 = createSellOrder(2000, 100.0006);
		Order s3 = createSellOrder(2000, 100.0006);
		Order s4 = createSellOrder(2000, 100.0005);
		Order s5 = createSellOrder(2000, 100.0004);
		book.addOrder(s1);
		book.addOrder(s2);
		book.addOrder(s3);
		book.addOrder(s4);
		book.addOrder(s5);
		compare("Buy				|	Sell			\n" + 
				"		 		| 6000@100.0006	\n" + 
				"4000@100.0005	| 2000@100.0005	\n" + 
				"6000@100.0004	| 2000@100.0004	", book);
		
		book.cancelOrder(b1);
		book.cancelOrder(s1);
		book.cancelOrder(b2);
		book.cancelOrder(s2);
		book.cancelOrder(b3);
		book.cancelOrder(s3);
		book.cancelOrder(b4);
		book.cancelOrder(s4);
		book.cancelOrder(b5);
		book.cancelOrder(s5);
		
		compare("", book);
	}
}
