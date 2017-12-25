package com.exchange.common;

import static org.junit.Assert.assertEquals;

import com.exchange.data.Order;
import com.exchange.data.Side;
import com.exchange.orderbook.OrderBook;

public class TestHelper {
	public static int ORDER_ID = 0;
	public static String DEFAULT_SYMBOL = "0005.HK";
	
	public static int getOrderId() {
		return ORDER_ID++;
	}

	public Order createBuyOrder(long qty, double price) {
		return new Order(getOrderId(), DEFAULT_SYMBOL, Side.Buy, qty, price);
	}
	
	public Order createSellOrder(long qty, double price) {
		return new Order(getOrderId(), DEFAULT_SYMBOL, Side.Sell, qty, price);
	}

	public final void assertOrderBookAsExpected(String expected, OrderBook book) {
		System.out.println(book);
		assertEquals(trimmedString(expected), trimmedString(book.toString()));
	}
	
	public final String trimmedString(String str) {
		return str.replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
	}

}
