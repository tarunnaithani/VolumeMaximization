package com.exchange;

import static com.exchange.ExchangeConstants.DEFAULT_DECIMAL_PRECISION;
import static com.exchange.ExchangeConstants.DEFAULT_INITIAL_ORDERBOOK_CAPACITY;
import static com.exchange.ExchangeConstants.DEFAULT_INITIAL_ORDERS_COUNT;
import static com.exchange.ExchangeConstants.DEFAULT_INITIAL_SYMBOLS_COUNT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.exchange.algo.MatchingAlgo;
import com.exchange.algo.MatchingResult;
import com.exchange.data.Execution;
import com.exchange.data.Order;
import com.exchange.orderbook.OrderBook;
import com.exchange.util.ExchangeUtils;

/**
 * Main class to store Exchange related order books and information about order
 * received. It provides functions to send/cancel order to book.
 *
 */
public class Exchange {
	
	/** Maximum precision supported by exchange */
	private final int decimalPrecision;
	
	/** Initial capacity of order entries in Order book */
	private final int orderBookCapacity;
	
	/**
	 * Map to store orders live in exchange at any moment, stored them as key value
	 * pair, OrderId -> Order
	 */
	private final HashMap<Integer, Order> orderStore;
	
	/**
	 * Map to store Order books live in exchange at any moment, stored them as key
	 * value pair, Symbol -> OrderBook
	 */
	private final HashMap<String, OrderBook> symbolBooks;

	public Exchange() {
		this(DEFAULT_DECIMAL_PRECISION, DEFAULT_INITIAL_SYMBOLS_COUNT, DEFAULT_INITIAL_ORDERS_COUNT, DEFAULT_INITIAL_ORDERBOOK_CAPACITY);
	}

	public Exchange(int decimalPrecision) {
		this(decimalPrecision, DEFAULT_INITIAL_SYMBOLS_COUNT, DEFAULT_INITIAL_ORDERS_COUNT, DEFAULT_INITIAL_ORDERBOOK_CAPACITY);
	}
	
	public Exchange(int decimalPrecision, int symbolCount, int orderCount, int orderBookCapacity) {
		this.decimalPrecision = decimalPrecision;
		this.orderBookCapacity = orderBookCapacity;
		this.symbolBooks = new HashMap<String, OrderBook>(symbolCount);
		this.orderStore = new HashMap<>(orderCount);
	}

	/**
	 * API call to send order to corresponding order book It also runs duplicate
	 * check based on order information present with the exchange
	 * 
	 * @param order
	 *            order to be sent
	 * @return true if success, else false
	 */
	public boolean sendOrder(Order order) {
		if (!validatePriceAndQty(order))
			return false;
		// Check if order id already exists
		if (orderStore.containsKey(order.getOrderId()))
			return false;
		// set time as when order was received by exchange
		order.setTimestamp(System.currentTimeMillis());

		// If this is first order for symbol then create new OrderBook
		if (!symbolBooks.containsKey(order.getSymbol()))
			symbolBooks.put(order.getSymbol(), new OrderBook(orderBookCapacity));
		OrderBook book = symbolBooks.get(order.getSymbol());

		// Add order to the Order Book
		boolean retVal = book.addOrder(order, ExchangeUtils.convertPriceToLong(order.getPrice(), decimalPrecision));

		// If addition to book success then add the order to store
		if (retVal)
			orderStore.put(order.getOrderId(), order);

		return retVal;
	}

	private boolean validatePriceAndQty(Order order) {
		if (order.getPrice() <= 0)
			return false;
		if (order.getQuantity() <= 0)
			return false;
		return true;
	}

	/**
	 * API call to cancel order from corresponding order book It also runs
	 * validation to check if order exists at exchange based on order information
	 * present with the exchange
	 * 
	 * @param order
	 *            order to be sent
	 * @return true if success, else false
	 */
	public boolean cancelOrder(Order order) {
		// Check order exists
		if (!orderStore.containsKey(order.getOrderId()))
			return false;
		// check book exists
		if (!symbolBooks.containsKey(order.getSymbol()))
			return false;

		OrderBook book = symbolBooks.get(order.getSymbol());
		boolean retVal = book.removeOrder(order, ExchangeUtils.convertPriceToLong(order.getPrice(), decimalPrecision));
		// if removal success then remove from store as well
		if (retVal)
			orderStore.remove(order.getOrderId());
		return retVal;
	}

	/**
	 * retrieves order book for a symbol
	 * 
	 * @param symbol
	 *            for which order book needs to be retrieved
	 * @return instance of OrderBook
	 */
	public OrderBook getBookForSymbol(String symbol) {
		return symbolBooks.get(symbol);
	}

	/**
	 * API to run matching algorithm on OrderBook for a symbol
	 * 
	 * @param exchangeAlgo
	 *            Instance of Algorithm to be executed
	 * @param symbol
	 *            symbol for which order book needs to be retrieved
	 * @return true if success, else false
	 */
	public MatchingResult runMatchingAlgo(MatchingAlgo exchangeAlgo, String symbol) {
		if (symbolBooks.containsKey(symbol))
			return exchangeAlgo.execute(symbolBooks.get(symbol));
		return new MatchingResult(false, 0, 0);
	}
	
	public List<Execution> executeMatch(String symbol, long price, long quantity) {
		if (symbolBooks.containsKey(symbol))
			return symbolBooks.get(symbol).execute(price, quantity);
		return new ArrayList<>();
	}

}
