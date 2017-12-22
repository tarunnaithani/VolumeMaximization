package com.exchange.orderbook;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.TreeSet;

import com.exchange.data.Constants;
import com.exchange.data.Order;
import com.exchange.data.Side;

/**
 * Class to maintain OrderBook for a given set of orders. 
 * -> Contains method to add/remove order to/from appropriate price level on Bid or Ask side. 
 * -> Contains method to execute order from the given order book state.
 */
public class OrderBook {

	private final HashMap<Integer, Order> orderStore;
	private final HashMap<Integer, OrderEntry> orderEntries;
	
	private final PriorityQueue<PriceLevel> bids;
	private final HashMap<Long, PriceLevel> bidMap;

	private final PriorityQueue<PriceLevel> asks;
	private final HashMap<Long, PriceLevel> askMap;

	public OrderBook(int capacity) {
		this.bids = new PriorityQueue<>(capacity, new PriceMaxComparator());
		this.bidMap = new HashMap<>(capacity);

		this.asks = new PriorityQueue<>(capacity, new PriceMinComparator());
		this.askMap = new HashMap<>(capacity);

		this.orderEntries = new HashMap<>();
		this.orderStore = new HashMap<>();
	}

	public void addOrder(Order order) {
		if (order.getSide() == Side.Buy)
			addOrder(order, bids, bidMap);
		else if (order.getSide() == Side.Sell)
			addOrder(order, asks, askMap);
		else
			throw new IllegalArgumentException("Side not supported");
	}

	public void cancelOrder(Order order) {
		if (order.getSide() == Side.Buy)
			removeOrder(order, bids, bidMap);
		else if (order.getSide() == Side.Sell)
			removeOrder(order, asks, askMap);
		else
			throw new IllegalArgumentException("Side not supported");
	}
	/**
	 * Add order to the book
	 * 
	 * @param order
	 *            to be added to the book
	 * @param queue
	 *            price queue for the side
	 * @param map
	 *            price order map for the side
	 */
	private void addOrder(Order order, PriorityQueue<PriceLevel> queue,	HashMap<Long, PriceLevel> map) {
		if(orderStore.containsKey(order.getOrderId()))
			return;
		
		orderStore.put(order.getOrderId(), order);

		if (map.containsKey(order.getPrice())) {
			// If price level already exists, add order to linked list
			OrderEntry newOrderEntry =  new OrderEntry(order.getOrderId(), order.getQuantity(), order.getPrice());
			OrderEntry entry = orderEntries.get(map.get(order.getPrice()).orderEntry.orderId);
			// Find last order
			while (entry.getNext() != -1)
				entry = orderEntries.get(entry.getNext());
			// Add order entry to doubly linked list
			entry.setNext(newOrderEntry.getOrderId());
			newOrderEntry.setPrev(entry.getOrderId());
			orderEntries.put(newOrderEntry.getOrderId(), newOrderEntry);
		} else {
			// If price level does not exists add new entry to map and queue
			PriceLevel priceLevel = new PriceLevel(order.getPrice());
			OrderEntry orderEntry =  new OrderEntry(order.getOrderId(), order.getQuantity(), order.getPrice());
			orderEntries.put(order.getOrderId(), orderEntry);
			priceLevel.setOrderEntry(orderEntry);
			map.put(order.getPrice(), priceLevel);
			queue.add(priceLevel);
		}
	}

	/**
	 * Remove order from the order book
	 * 
	 * @param order
	 *            to be removed from the book
	 * @param queue
	 *            price queue for the side
	 * @param map
	 *            price order map for the side
	 */
	private void removeOrder(Order order, PriorityQueue<PriceLevel> queue,
			HashMap<Long, PriceLevel> map) {

		if(!orderStore.containsKey(order.getOrderId()))
			return;
		
		
		if(!map.containsKey(order.getPrice())) {
			return;
		}
		
		PriceLevel priceLevel = map.get(order.getPrice());
		OrderEntry orderEntry = priceLevel.getOrderEntry();
		if (orderEntry.orderId == order.getOrderId() && orderEntry.getNext() == -1) {
			// if it is only order entry at the price level then remove it
			queue.remove(new PriceLevel(order.getPrice()));
			map.remove(order.getPrice());
		} else if (orderEntry.orderId == order.getOrderId() && orderEntry.getNext() != -1) {
			// if it is first order entry in linked list
			priceLevel.setOrderEntry(orderEntries.get(orderEntry.getNext()));
			orderEntries.remove(orderEntry.getOrderId());
		} else {
			// if order is somewhere in the linked list
			while (orderEntry.getOrderId() != order.getOrderId() && orderEntry != null) {
				orderEntry = orderEntries.get(orderEntry.getNext());
			}
			// Change next pointer
			
			OrderEntry prevEntry = orderEntries.get(orderEntry.getPrev());
			prevEntry.setNext(orderEntry.getNext());
			if (orderEntry.getNext() != -1) {
				OrderEntry nextEntry = orderEntries.get(orderEntry.getNext());
				// Change previous pointer
				nextEntry.setPrev(orderEntry.getPrev());
			}
			orderEntries.remove(orderEntry.getOrderId());
		}
		orderStore.remove(order.getOrderId());
	}

	

	@Override
	public String toString() {
		if (bids.size() == 0 && asks.size() == 0)
			return "";
		TreeSet<PriceLevel> allPrices = new TreeSet<>(new PriceMaxComparator());
		for(PriceLevel price: bids)
			if(!allPrices.contains(price)) 
				allPrices.add(price);
		for(PriceLevel price: asks)
			if(!allPrices.contains(price)) 
				allPrices.add(price);
		
		StringBuffer buffer = new StringBuffer(1024);
		buffer.append("Buy").append("\t\t|\t").append("Sell");
		for (PriceLevel priceLevel: allPrices) {
			buffer.append("\n");
			// If bid level exists, print it
			if (bidMap.containsKey(priceLevel.getPrice()))
			{
				OrderEntry entry = bidMap.get(priceLevel.getPrice()).orderEntry;
				long totalQuantity = entry.getAvailableQty();
				while (entry.getNext() != -1) {
					entry = orderEntries.get(entry.getNext());
					totalQuantity = totalQuantity + entry.getAvailableQty();
				}
				buffer.append(totalQuantity + "@" + new Double(priceLevel.getPrice())/Constants.MAX_DECIMAL_PRECISION);
				buffer.append("\t");
			}
			else
				buffer.append("\t\t");
			
			buffer.append(" | ");
			// If ask level exists, print it
			if (askMap.containsKey(priceLevel.getPrice()))
			{
				OrderEntry entry = askMap.get(priceLevel.getPrice()).orderEntry;
				long totalQuantity = entry.getAvailableQty();
				while (entry.getNext() != -1) {
					entry = orderEntries.get(entry.getNext());
					totalQuantity = totalQuantity + entry.getAvailableQty();
				}
				buffer.append(totalQuantity + "@" + new Double(priceLevel.getPrice())/Constants.MAX_DECIMAL_PRECISION);
				buffer.append("\t");
			}
			else
				buffer.append("\t\t ");
		}

		return buffer.toString();
	}

	private class PriceMinComparator implements Comparator<PriceLevel> {
		@Override
		public int compare(PriceLevel o1, PriceLevel o2) {
			if(o2.price == o1.price)
				return 0;
			else if(o2.price > o1.price)
				return -1;
			else
				return 1;
		}
	}

	private class PriceMaxComparator implements Comparator<PriceLevel> {
		@Override
		public int compare(PriceLevel o1, PriceLevel o2) {
			if(o2.price == o1.price)
				return 0;
			else if(o2.price > o1.price)
				return 1;
			else
				return -1;
		}
	}
	
	private class PriceLevel{
		final long price;
		OrderEntry orderEntry;
		
		public PriceLevel(long price) {
			this.price = price;
		}

		public OrderEntry getOrderEntry() {
			return orderEntry;
		}

		public void setOrderEntry(OrderEntry orderEntry) {
			this.orderEntry = orderEntry;
		}

		public long getPrice() {
			return price;
		}

		@Override
		public int hashCode() {
			return (int) price;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PriceLevel other = (PriceLevel) obj;
			if (price != other.price)
				return false;
			return true;
		}
		
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

		public long getQuantity() {
			return quantity;
		}

		public void setQuantity(long quantity) {
			this.quantity = quantity;
		}

		public long getCumQty() {
			return cumQty;
		}

		public void setCumQty(long cumQty) {
			this.cumQty = cumQty;
		}

		public int getNext() {
			return next;
		}

		public void setNext(int next) {
			this.next = next;
		}

		public int getPrev() {
			return prev;
		}

		public void setPrev(int prev) {
			this.prev = prev;
		}

		public int getOrderId() {
			return orderId;
		}
		

		
	}
}
