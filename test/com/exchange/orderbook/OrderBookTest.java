package com.exchange.orderbook;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import com.exchange.common.TestHelper;
import com.exchange.data.Order;

class OrderBookTest extends TestHelper{
	private void addOrderToBookWithSuccess(Order order, OrderBook book) {
		assertTrue(book.addOrder(order));
	}

	private void addOrderToBookWithFailure(Order order, OrderBook book) {
		assertFalse(book.addOrder(order));
	}

	@Test
	void testDuplicateOrder() {
		OrderBook orderBook = new OrderBook(10);
		Order order = createBuyOrder(2000, 100.0004);
		addOrderToBookWithSuccess(order, orderBook);
		
		assertOrderBookAsExpected("Buy				|	Sell \n" + 
				"2000@100.0004	| 		   ", orderBook);
		
		addOrderToBookWithFailure(order, orderBook);

		assertOrderBookAsExpected("Buy				|	Sell \n" + 
				"2000@100.0004	| 		   ", orderBook);
	}

	@Test
	void testMultipleBuyOrder() {
		OrderBook orderBook = new OrderBook(10);
		addOrderToBookWithSuccess(createBuyOrder(2000, 100.0004), orderBook);
		addOrderToBookWithSuccess(createBuyOrder(2000, 100.0003), orderBook);
		addOrderToBookWithSuccess(createBuyOrder(2000, 100.0001), orderBook);
		addOrderToBookWithSuccess(createBuyOrder(2000, 100.0001), orderBook);

		assertOrderBookAsExpected("Buy				|	Sell \n" + 
				"2000@100.0004	| 		 \n" + 
				"2000@100.0003	| 		 \n" + 
				"4000@100.0001	| 		   ", orderBook);
	}

	@Test
	void testMultipleSellOrder() {
		OrderBook orderBook = new OrderBook(10);
		addOrderToBookWithSuccess(createSellOrder(2000, 100.0001), orderBook);
		addOrderToBookWithSuccess(createSellOrder(2000, 100.0001), orderBook);
		addOrderToBookWithSuccess(createSellOrder(2000, 100.0002), orderBook);
		addOrderToBookWithSuccess(createSellOrder(2000, 100.0003), orderBook);

		assertOrderBookAsExpected("Buy		|	Sell			\n" + 
				"		| 2000@100.0003	\n" + 
				"		| 2000@100.0002	\n" + 
				"		| 4000@100.0001	  ", orderBook);
	}
	
	@Test
	void testMultipleBuySellOrder() {
		OrderBook orderBook = new OrderBook(10);
		addOrderToBookWithSuccess(createBuyOrder(2000, 100.0004), orderBook);
		addOrderToBookWithSuccess(createBuyOrder(2000, 100.0001), orderBook);
		addOrderToBookWithSuccess(createBuyOrder(2000, 100.0001), orderBook);
		addOrderToBookWithSuccess(createBuyOrder(2000, 100.0001), orderBook);
		
		addOrderToBookWithSuccess(createSellOrder(2000, 100.0001), orderBook);
		addOrderToBookWithSuccess(createSellOrder(2000, 100.0002), orderBook);
		addOrderToBookWithSuccess(createSellOrder(2000, 100.0003), orderBook);
		addOrderToBookWithSuccess(createSellOrder(2000, 100.0003), orderBook);
		addOrderToBookWithSuccess(createSellOrder(2000, 100.0003), orderBook);

		assertOrderBookAsExpected("Buy				|	Sell			\n" + 
				"2000@100.0004	| 		 		\n" + 
				"		 		| 6000@100.0003	\n" + 
				"		 		| 2000@100.0002	\n" + 
				"6000@100.0001	| 2000@100.0001	", orderBook);
	}

	@Test
	void testOneOrderWithCancel() {
		OrderBook book = new OrderBook(10);
		Order order = createBuyOrder(2000, 100.0004);
		addOrderToBookWithSuccess(order, book);
		
		assertOrderBookAsExpected("Buy				|	Sell			\n" + 
				"2000@100.0004	| 		 		\n" 	, book);
		
		assertTrue(book.cancelOrder(order));
		assertOrderBookAsExpected("", book);
	}
	
	@Test
	void testOneOrderWithCancelWithInvalidId() {
		OrderBook book = new OrderBook(10);
		Order order = createBuyOrder(2000, 100.0004);
		addOrderToBookWithSuccess(order, book);
		
		assertOrderBookAsExpected("Buy				|	Sell			\n" + 
				"2000@100.0004	| 		 		\n" 	, book);
		
		assertFalse(book.cancelOrder(createBuyOrder(2000, 100.0004)));
		assertOrderBookAsExpected("Buy				|	Sell			\n" + 
				"2000@100.0004	| 		 		\n" 	, book);
	}
	
	@Test
	void testOneOrderCancelWithInvalidPrice() {
		OrderBook book = new OrderBook(10);
		Order order = createBuyOrder(2000, 100.0004);
		addOrderToBookWithSuccess(order, book);
		
		assertOrderBookAsExpected("Buy				|	Sell			\n" + 
				"2000@100.0004	| 		 		\n" 	, book);
		
		assertFalse(book.cancelOrder(new Order(order.getOrderId(), order.getSymbol(), order.getSide(), order.getQuantity(), 100.00)));
		assertOrderBookAsExpected("Buy				|	Sell			\n" + 
				"2000@100.0004	| 		 		\n" 	, book);
	}
	
	@Test
	void testThreeOrderWithCancelOnFirst() {
		OrderBook book = new OrderBook(10);
		Order o1 = createBuyOrder(2000, 100.0004);
		Order o2 = createBuyOrder(2000, 100.0004);
		Order o3 = createBuyOrder(2000, 100.0004); 
		addOrderToBookWithSuccess(o1, book);
		addOrderToBookWithSuccess(o2, book);
		addOrderToBookWithSuccess(o3, book);
		
		
		assertOrderBookAsExpected("Buy				|	Sell			\n" + 
				"6000@100.0004	| 		 		", book);
		
		assertTrue(book.cancelOrder(o1));
		assertOrderBookAsExpected("Buy				|	Sell			\n" + 
				"4000@100.0004	| 		 		", book);
	}
	
	@Test
	void testThreeOrderWithCancelOnSecond() {
		OrderBook book = new OrderBook(10);
		Order o1 = createBuyOrder(2000, 100.0004);
		Order o2 = createBuyOrder(2000, 100.0004);
		Order o3 = createBuyOrder(2000, 100.0004); 
		addOrderToBookWithSuccess(o1, book);
		addOrderToBookWithSuccess(o2, book);
		addOrderToBookWithSuccess(o3, book);
		
		assertOrderBookAsExpected("Buy				|	Sell			\n" + 
				"6000@100.0004	| 		 		", book);
		
		assertTrue(book.cancelOrder(o2));
		assertOrderBookAsExpected("Buy				|	Sell			\n" + 
				"4000@100.0004	| 		 		", book);
	}
	
	@Test
	void testThreeOrderWithCancelOnThird() {
		OrderBook book = new OrderBook(10);
		Order o1 = createBuyOrder(2000, 100.0004);
		Order o2 = createBuyOrder(2000, 100.0004);
		Order o3 = createBuyOrder(2000, 100.0004); 
		addOrderToBookWithSuccess(o1, book);
		addOrderToBookWithSuccess(o2, book);
		addOrderToBookWithSuccess(o3, book);
		
		
		assertOrderBookAsExpected("Buy				|	Sell			\n" + 
				"6000@100.0004	| 		 		", book);
		
		assertTrue(book.cancelOrder(o3));
		assertOrderBookAsExpected("Buy				|	Sell			\n" + 
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
		addOrderToBookWithSuccess(b1, book);
		addOrderToBookWithSuccess(b2, book);
		addOrderToBookWithSuccess(b3, book);
		addOrderToBookWithSuccess(b4, book);
		addOrderToBookWithSuccess(b5, book);
		
		Order s1 = createSellOrder(2000, 100.0006);
		Order s2 = createSellOrder(2000, 100.0006);
		Order s3 = createSellOrder(2000, 100.0006);
		Order s4 = createSellOrder(2000, 100.0005);
		Order s5 = createSellOrder(2000, 100.0004);
		addOrderToBookWithSuccess(s1, book);
		addOrderToBookWithSuccess(s2, book);
		addOrderToBookWithSuccess(s3, book);
		addOrderToBookWithSuccess(s4, book);
		addOrderToBookWithSuccess(s5, book);
		
		assertOrderBookAsExpected("Buy				|	Sell			\n" + 
				"		 		| 6000@100.0006	\n" + 
				"4000@100.0005	| 2000@100.0005	\n" + 
				"6000@100.0004	| 2000@100.0004	", book);
		
		assertTrue(book.cancelOrder(b1));
		assertTrue(book.cancelOrder(s1));
		assertTrue(book.cancelOrder(b2));
		assertTrue(book.cancelOrder(s2));
		assertTrue(book.cancelOrder(b3));
		assertTrue(book.cancelOrder(s3));
		assertTrue(book.cancelOrder(b4));
		assertTrue(book.cancelOrder(s4));
		assertTrue(book.cancelOrder(b5));
		assertTrue(book.cancelOrder(s5));
		
		assertOrderBookAsExpected("", book);
	}
}
