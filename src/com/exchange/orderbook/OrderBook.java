package com.exchange.orderbook;


import java.util.HashMap;
import java.util.TreeSet;

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
	
	/** Sorted tree to store all bid prices with maximum as first as head */
	private final TreeSet<Long> bids;
	/** map containing all Bid-Prices and corresponding First OrderEntry by arrival on the order book */
	private final HashMap<Long, OrderEntry> bidMap;

	/** Sorted tree to store all ask prices with minimum as first as head */
	private final TreeSet<Long> asks;
	/** map containing all Ask-Prices and corresponding First OrderEntry by arrival on the order book */
	private final HashMap<Long, OrderEntry> askMap;

	public OrderBook(int capacity) {
		
		this.bids = new TreeSet<>(Side.Buy.getComparator());
		this.bidMap = new HashMap<>(capacity);

		this.asks = new TreeSet<>(Side.Sell.getComparator());
		this.askMap = new HashMap<>(capacity);

		this.orderEntries = new HashMap<>(capacity);
	}

	public boolean addOrder(Order order, long price) {
		if (order.getSide() == Side.Buy)
			return addOrder(order, bids, bidMap, price);
		else 
			return addOrder(order, asks, askMap, price);
	}

	public boolean cancelOrder(Order order, long price) {
		if (order.getSide() == Side.Buy)
			return removeOrder(order, bids, bidMap, price);
		else 
			return removeOrder(order, asks, askMap, price);
	}


	private boolean addOrder(Order order, TreeSet<Long> prices,	HashMap<Long, OrderEntry> priceMap, long price) {
		if(orderEntries.containsKey(order.getOrderId()))
			return false;

		if (priceMap.containsKey(price)) {
			// If price level already exists, add order to end of the linked list
			OrderEntry newOrderEntry =  new OrderEntry(order.getOrderId(), order.getQuantity(), price);
			OrderEntry entry = orderEntries.get(priceMap.get(price).orderId);
			// Find last order
			while (entry.next != -1)
				entry = orderEntries.get(entry.next);
			// Add order entry to doubly linked list
			entry.next = newOrderEntry.orderId;
			newOrderEntry.prev = entry.orderId;
			orderEntries.put(newOrderEntry.orderId, newOrderEntry);
		} else {
			// If price level does not exists add new entry to map and set
			OrderEntry orderEntry =  new OrderEntry(order.getOrderId(), order.getQuantity(), price);
			orderEntries.put(order.getOrderId(), orderEntry);
			priceMap.put(price, orderEntry);
			prices.add(price);
		}
		return true;
	}

	private boolean removeOrder(Order order, TreeSet<Long> prices,
			HashMap<Long, OrderEntry> priceMap, long price) {
		if(!orderEntries.containsKey(order.getOrderId()))
			return false;
		
		if(!priceMap.containsKey(price)) {
			return false;
		}
		
		OrderEntry orderEntry = priceMap.get(price);
		if (orderEntry.orderId == order.getOrderId() && orderEntry.next == -1) {
			// if it is only order entry at the price level then remove it
			prices.remove(price);
			priceMap.remove(price);
		} else if (orderEntry.orderId == order.getOrderId() && orderEntry.next != -1) {
			// if it is first order entry in linked list
			orderEntry = orderEntries.remove(orderEntry.orderId);
			priceMap.put(price, orderEntries.get(orderEntry.next));
			orderEntries.remove(orderEntry.orderId);
		} else {
			// if order is somewhere in the linked list
			while (orderEntry.orderId != order.getOrderId() && orderEntry != null) {
				orderEntry = orderEntries.get(orderEntry.next);
			}
			// Change next pointer
			
			OrderEntry prevEntry = orderEntries.get(orderEntry.prev);
			prevEntry.next = orderEntry.next;
			if (orderEntry.next != -1) {
				OrderEntry nextEntry = orderEntries.get(orderEntry.next);
				// Change previous pointer
				nextEntry.prev = orderEntry.prev;
			}
			orderEntries.remove(orderEntry.orderId);
		}
		return true;
	}

	public TreeSet<Long> getBids() {
		return bids;
	}

	public TreeSet<Long> getAsks() {
		return asks;
	}

	public long getAvailableSellQtyAtPrice(Long price) {
		return getAvailableQtyAtPrice(price, askMap);
	}
	
	public long getAvailableBuyQtyAtPrice(Long price) {
		return getAvailableQtyAtPrice(price, bidMap);
	}

	private long getAvailableQtyAtPrice(Long price, HashMap<Long, OrderEntry> priceMap) {
		OrderEntry entry = priceMap.get(price);
		long totalQuantity = entry.getAvailableQty();
		while (entry.next != -1) {
			entry = orderEntries.get(entry.next);
			totalQuantity = totalQuantity + entry.getAvailableQty();
		}
		return totalQuantity;
	}
	
	public String printBook(int decimalPrecision) {
		if (bids.size() == 0 && asks.size() == 0)
			return "";
		TreeSet<Long> allPrices = new TreeSet<>(Side.Buy.getComparator());
		allPrices.addAll(bids);
		allPrices.addAll(asks);

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
		
		/** Quantity of the order */
		private long quantity;
		/** Quantity already executed */
		private long cumQty;
		
		/** Variables part of doubly linked list to store orders at same price level */
		// Next entry in list
		private int next = -1;
		// Previous entry in list
		private int prev = -1;
		
		public OrderEntry(int orderId, long quantity, double price) {
			this.orderId = orderId;
			this.quantity = quantity;
			this.cumQty = 0;

			this.prev = -1;
			this.next = -1;
		}

		public long getAvailableQty() {
			return quantity - cumQty;
		}

		public void setQuantity(long quantity) {
			this.quantity = quantity;
		}

		public void setCumQty(long cumQty) {
			this.cumQty = cumQty;
		}
	}
}
