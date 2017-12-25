package com.exchange;

import java.util.HashMap;

import com.exchange.algo.ExchangeAlgo;
import com.exchange.data.Order;
import com.exchange.orderbook.OrderBook;

public class Exchange {
	private final HashMap<Integer, Order> orderStore;
	private final HashMap<String, OrderBook> symbolBooks;

	public Exchange() {
		this.symbolBooks = new HashMap<String, OrderBook>(ExchangeConstants.INITIAL_NUMBER_OF_SYMBOLS);
		this.orderStore = new HashMap<>(ExchangeConstants.INTIAL_CAPACITY_FOR_ORDERS);
	}

	public boolean sendOrder(Order order) {
		if(orderStore.containsKey(order.getOrderId()))
			return false;
		order.setTimestamp(System.currentTimeMillis());
		if (!symbolBooks.containsKey(order.getSymbol()))
			symbolBooks.put(order.getSymbol(), new OrderBook(ExchangeConstants.INITIAL_NUMBER_FOR_PRICE_LEVELS));
		OrderBook book = symbolBooks.get(order.getSymbol());

		boolean retVal = book.addOrder(order);
		if(retVal)
			orderStore.put(order.getOrderId(), order);
		
		return retVal;
	}

	public boolean cancelOrder(Order order) {

		if(!orderStore.containsKey(order.getOrderId()))
			return false;
		if (!symbolBooks.containsKey(order.getSymbol()))
			return false;

		OrderBook book = symbolBooks.get(order.getSymbol());
		boolean retVal = book.cancelOrder(order);
		if(retVal)
			orderStore.remove(order.getOrderId());
		return retVal;
	}
	
	public OrderBook getBookForSymbol(String symbol) {
		return symbolBooks.get(symbol);
	}

	public void executeMatchingAlgo(ExchangeAlgo exchangeAlgo) {

	}

}
