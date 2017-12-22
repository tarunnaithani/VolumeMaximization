package com.exchange;

import java.util.HashMap;

import com.exchange.data.Constants;
import com.exchange.data.Order;
import com.exchange.orderbook.OrderBook;

public class Exchange {
	
	private final HashMap<String, OrderBook> symbolBooks;

	public Exchange() {
		this.symbolBooks = new HashMap<String, OrderBook>();
	}
	
	public void sendOrder(Order order) {
		
		if(!symbolBooks.containsKey(order.getSymbol()) )
			symbolBooks.put(order.getSymbol(),new OrderBook(Constants.INTIAL_CAPACITY));
		OrderBook book = symbolBooks.get(order.getSymbol());
		book.addOrder(order);
		System.out.println(book.toString());
	}
	
	public void executeMatching() {
		
	}
	
	
}
