package com.exchange.orderbook;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import com.exchange.data.Execution;
import com.exchange.data.ExecutionType;
import com.exchange.data.Order;
import com.exchange.data.Side;
import com.exchange.util.ExchangeUtils;

/**
 * Class to maintain OrderBook for a given set of orders. 
 * -> Contains method to add/remove order to/from appropriate price level on Bid or Ask side. 
 */
public class OrderBook {

	/** map containing all order entries on the order book */
	private final HashMap<Integer, OrderEntry> orderEntries;
	
	/** map containing all Bid-Prices and corresponding First OrderEntry by arrival on the order book */
	private final TreeMap<Long, OrderEntry> bidMap;

	/** map containing all Ask-Prices and corresponding First OrderEntry by arrival on the order book */
	private final TreeMap<Long, OrderEntry> askMap;

	public OrderBook(int capacity) {
		this.bidMap = new TreeMap<>(Side.Buy.getComparator());
		this.askMap = new TreeMap<>(Side.Sell.getComparator());
		this.orderEntries = new HashMap<>(capacity);
	}

	/**
	 * Add order to order book at given price
	 * 
	 * @param order
	 *            to be added to book
	 * @param price
	 *            price in long representing price level
	 * @return true if success, else false
	 */
	public boolean addOrder(Order order, long price) {
		if(orderEntries.containsKey(order.getOrderId()))
			return false;
		else if (order.getSide() == Side.Buy)
			return addOrder(order, bidMap, price);
		else 
			return addOrder(order, askMap, price);
	}

	private boolean addOrder(Order order, TreeMap<Long, OrderEntry> priceMap, long price) {
		if (priceMap.containsKey(price)) {
			// If price level already exists, add order to end of the linked list
			OrderEntry newOrderEntry =  new OrderEntry(order.getOrderId(), order.getQuantity(), price);
			OrderEntry entry = orderEntries.get(priceMap.get(price).orderId);
			// Find last order
			while (entry.next != -1)
				entry = orderEntries.get(entry.next);
			// Add order entry to doubly linked list
			entry.next = newOrderEntry.orderId;
			orderEntries.put(newOrderEntry.orderId, newOrderEntry);
		} else {
			// If price level does not exists add new entry to map and set
			OrderEntry orderEntry =  new OrderEntry(order.getOrderId(), order.getQuantity(), price);
			orderEntries.put(order.getOrderId(), orderEntry);
			priceMap.put(price, orderEntry);
		}
		return true;
	}

	/**
	 * Remove order from order book at given price
	 * 
	 * @param order
	 *            to be removed to book
	 * @param price
	 *            price in long representing price level
	 * @return true if success, else false
	 */
	public boolean removeOrder(Order order, long price) {
		if(!orderEntries.containsKey(order.getOrderId()))
			return false;
		else if (order.getSide() == Side.Buy)
			return removeOrder(orderEntries.get(order.getOrderId()), bidMap, price);
		else 
			return removeOrder(orderEntries.get(order.getOrderId()), askMap, price);
	}

	private boolean removeOrder(OrderEntry entry, TreeMap<Long, OrderEntry> priceMap, long price) {
		if(!priceMap.containsKey(price))
			return false;
		
		OrderEntry orderEntry = priceMap.get(price);
		if (orderEntry.orderId == entry.orderId && orderEntry.next == -1) {
			// if it is only order entry at the price level then remove it
			priceMap.remove(price);
		} else if (orderEntry.orderId == entry.orderId && orderEntry.next != -1) {
			// if it is first order entry in linked list
			priceMap.put(price, orderEntries.get(orderEntry.next));
			orderEntries.remove(orderEntry.orderId);
		} else {
			// if order is somewhere in the linked list
			while (orderEntry.next != entry.orderId && orderEntry != null)
				orderEntry = orderEntries.get(orderEntry.next);
			// Change next pointer
			OrderEntry removalEntry = orderEntries.get(orderEntry.next);
			orderEntry.next = removalEntry.next;
			orderEntries.remove(removalEntry.orderId);
		}
		return true;
	}

	public TreeSet<Long> getBids() {
		TreeSet<Long> set = new TreeSet<>(Side.Buy.getComparator());
		set.addAll(bidMap.keySet());
		return set;
	}

	public TreeSet<Long> getAsks() {
		TreeSet<Long> set = new TreeSet<>(Side.Sell.getComparator());
		set.addAll(askMap.keySet());
		return set;
	}

	public long getAvailableSellQtyAtPrice(long price) {
		return getAvailableQtyAtPrice(price, askMap);
	}
	
	public long getAvailableBuyQtyAtPrice(long price) {
		return getAvailableQtyAtPrice(price, bidMap);
	}

	private long getAvailableQtyAtPrice(long price, TreeMap<Long, OrderEntry> priceMap) {
		OrderEntry entry = priceMap.get(price);
		long totalQuantity = entry.getAvailableQty();
		while (entry.next != -1) {
			entry = orderEntries.get(entry.next);
			totalQuantity = totalQuantity + entry.getAvailableQty();
		}
		return totalQuantity;
	}
	
	/**
	 * creates execution at specified price and removes fully filled orders from
	 * order book
	 * 
	 * @param price
	 *            price at which match needs to be done
	 * @param matchVolume
	 *            total volume for which execution should be created
	 * @return list of executions
	 */
	public List<Execution> execute(long price, long matchVolume) {
		List<Execution> executions = new ArrayList<>();
		executions.addAll(getExecutions(price, matchVolume, bidMap, getBids())); // Buy side executions
		executions.addAll(getExecutions(price, matchVolume, askMap, getAsks())); // Sell side executions
		return executions;
	}

	private List<Execution> getExecutions(long executionPrice, long matchVolume, TreeMap<Long, OrderEntry> priceMap, TreeSet<Long> prices) {
		List<Execution> executions = new ArrayList<>();
		for (Long price : prices.headSet(executionPrice, true)) {
			OrderEntry entry = priceMap.get(price);
			do{
				long execQty = Math.min(entry.getAvailableQty(), matchVolume);
				executions.add(new Execution(entry.orderId, execQty, executionPrice, entry.getAvailableQty() == execQty? ExecutionType.FULL : ExecutionType.PARTIAL));
				entry.cumQty = entry.cumQty + execQty;
				if (entry.getAvailableQty() == 0)
					removeOrder(entry, priceMap, entry.price);
				matchVolume = matchVolume - execQty;
				
			}while (entry.next != -1) ;
			if(matchVolume == 0)
				break;
		}
		return executions;
	}
	
	public String printBook(int decimalPrecision) {
		if (bidMap.size() == 0 && askMap.size() == 0)
			return "";

		TreeSet<Long> allPrices = new TreeSet<>(Side.Buy.getComparator());
		allPrices.addAll(getBids());
		allPrices.addAll(getAsks());

		StringBuffer buffer = new StringBuffer(1024);
		buffer.append("Buy").append("\t\t|\t").append("Sell");
		for (Long price: allPrices) {
			buffer.append("\n");
			// If bid level exists, print it
			if (bidMap.containsKey(price))
				buffer.append(getAvailableBuyQtyAtPrice(price) + "@" + ExchangeUtils.convertPriceToDouble(price, decimalPrecision)).append("\t");
			else
				buffer.append("\t\t");
			
			buffer.append(" | ");
			// If ask level exists, print it
			if (askMap.containsKey(price))
				buffer.append(getAvailableSellQtyAtPrice(price) + "@" + ExchangeUtils.convertPriceToDouble(price, decimalPrecision)).append("\t");
			else
				buffer.append("\t\t ");
		}
		return buffer.toString();
	}

	private class OrderEntry{
		/** Unique id for the order */
		private final int orderId;
		
		/** Price of the order */
		private long price;
		/** Quantity of the order */
		private long quantity;
		/** Quantity already executed */
		private long cumQty;
		
		// Next entry in list
		private int next = -1;
		
		public OrderEntry(int orderId, long quantity, long price) {
			this.orderId = orderId;
			this.quantity = quantity;
			this.price =  price;
			this.cumQty = 0;

			this.next = -1;
		}

		public long getAvailableQty() {
			return quantity - cumQty;
		}
	}
}
