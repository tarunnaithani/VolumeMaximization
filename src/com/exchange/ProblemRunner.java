package com.exchange;

import com.exchange.data.Order;
import com.exchange.data.Side;
import com.exchange.util.NormalDistribution;

public class ProblemRunner {
	public static int DEFAULT_ORDER_ID = 0;
	public static final String DEFAULT_SYMBOL = "0005.HK";

	public static final double MEAN_PRICE_DISTRIBUTION = 50.0;
	public static final double STD_DEV_PRICE_DISTRIBUTION = 6.0;
	public static final int DEFAULT_PRICE_PRECISION = 4;

	public static final double MEAN_QTY_DISTRIBUTION = 100000.0;
	public static final double STD_DEV_QTY_DISTRIBUTION = 20000.0;

	public static void main(String[] args) {
		Exchange exchange = new Exchange();	

		for (int i = 0; i < 10; i++) {
			for (Side side :Side.values()) {
				Order order = createOrderFromDistribution(side);
				System.out.println("Sending Order, " + order);
				exchange.sendOrder(order);
			}
		}
		

	}

	public static Order createOrderFromDistribution(Side side) {
		long qty = NormalDistribution.getLong(STD_DEV_QTY_DISTRIBUTION, MEAN_QTY_DISTRIBUTION);
		double price = NormalDistribution.getDouble(STD_DEV_PRICE_DISTRIBUTION, MEAN_PRICE_DISTRIBUTION, DEFAULT_PRICE_PRECISION);
		
		return new Order(++DEFAULT_ORDER_ID, DEFAULT_SYMBOL, side, qty, price);
	}

}
