package com.exchange;

import java.util.HashMap;

import com.exchange.algo.ExchangeAlgo;
import com.exchange.data.Order;
import com.exchange.orderbook.OrderBook;

/**
 * Main class to store Exchange related order books and information about order received.
 * It provides functions to send/cancel order to book. 
 *
 */
public class Exchange {
	
	/** Map to store orders live in exchange at any moment, stored them as key value pair, OrderId -> Order */
	private final HashMap<Integer, Order> orderStore;
	/** Map to store Order books live in exchange at any moment, stored them as key value pair, Symbol -> OrderBook */
	private final HashMap<String, OrderBook> symbolBooks;

	public Exchange() {
		this.symbolBooks = new HashMap<String, OrderBook>(ExchangeConstants.INITIAL_NUMBER_OF_SYMBOLS);
		this.orderStore = new HashMap<>(ExchangeConstants.INTIAL_CAPACITY_FOR_ORDERS);
	}

	/**
	 * API call to send order to corresponding order book
	 * It also runs duplicate check based on order information 
	 * present with the exchange
	 * 
	 * @param order order to be sent
	 * @return true if success, else false
	 */
	public boolean sendOrder(Order order) {
		//Check if order id already exists
		if(orderStore.containsKey(order.getOrderId()))
			return false;
		//set time as when order was received by exchange
		order.setTimestamp(System.currentTimeMillis());
		
		//If this is first order for symbol then create new OrderBook
		if (!symbolBooks.containsKey(order.getSymbol()))
			symbolBooks.put(order.getSymbol(), new OrderBook(ExchangeConstants.INITIAL_NUMBER_FOR_PRICE_LEVELS));
		OrderBook book = symbolBooks.get(order.getSymbol());

		//Add order to the Order Book
		boolean retVal = book.addOrder(order);
		
		//If addition to book success then add the order to store
		if(retVal)
			orderStore.put(order.getOrderId(), order);
		
		return retVal;
	}

	/**
	 * API call to cancel order from corresponding order book
	 * It also runs validation to check if order exists at exchange
	 * based on order information present with the exchange
	 * 
	 * @param order order to be sent
	 * @return true if success, else false
	 */
	public boolean cancelOrder(Order order) {
		//Check order exists 
		if(!orderStore.containsKey(order.getOrderId()))
			return false;
		//check book exists
		if (!symbolBooks.containsKey(order.getSymbol()))
			return false;

		OrderBook book = symbolBooks.get(order.getSymbol());
		boolean retVal = book.cancelOrder(order);
		//if removal success then remove from store as well 
		if(retVal)
			orderStore.remove(order.getOrderId());
		return retVal;
	}
	
	/**
	 * retrieves order book for a symbol
	 * @param symbol for which order book needs to be retrieved
	 * @return instance of OrderBook
	 */
	public OrderBook getBookForSymbol(String symbol) {
		return symbolBooks.get(symbol);
	}

	/**
	 * API to run matching algorithm on a OrderBook
	 *  
	 * @param exchangeAlgo Instance of Algorithm to be executed  
	 * @param symbol symbol for which order book needs to be retrieved
	 * @return true if success, else false
	 */
	public boolean executeMatchingAlgo(ExchangeAlgo exchangeAlgo, String symbol) {
		if(symbolBooks.containsKey(symbol))
			return exchangeAlgo.execute(symbolBooks.get(symbol));
		return false;
	}

}
