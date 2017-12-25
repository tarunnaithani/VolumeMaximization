package com.exchange.data;

import java.util.Date;

/**
 * Class to store order information
 */
public class Order {

	/** Unique id for the order */
	private final int orderId;

	/** Symbol of the order */
	private final String symbol;
	/** Side of the order */
	private final Side side;
	/** Quantity of the order */
	private final long quantity;
	/** Limit price for the order */
	private final double price;

	/** Time when order was created */
	private long timestamp = 0;

	public Order(int orderId, String symbol, Side side, long quantity, double price) {
		this.orderId = orderId;
		this.symbol = symbol;
		this.side = side;
		this.quantity = quantity;
		this.price = price;
		this.timestamp = System.currentTimeMillis();
	}

	public int getOrderId() {
		return this.orderId;
	}

	public Side getSide() {
		return side;
	}

	public String getSymbol() {
		return symbol;
	}

	public long getQuantity() {
		return quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", timestamp=" + new Date(timestamp) + ", symbol=" + symbol + ", side=" + side + ", quantity=" + quantity
				+ ", price=" + price + "]";
	}	
	
	

}
