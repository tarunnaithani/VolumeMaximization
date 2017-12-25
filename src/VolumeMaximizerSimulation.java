

import java.util.Random;

import com.exchange.Exchange;
import com.exchange.algo.MatchingResult;
import com.exchange.algo.VolumeMaximizationAlgo;
import com.exchange.data.Order;
import com.exchange.data.Side;
import com.exchange.util.ExchangeUtils;

public class VolumeMaximizerSimulation {
	public static int DEFAULT_ORDER_ID = 0;
	public static final String DEFAULT_SYMBOL = "0005.HK";

	public static final double MEAN_PRICE_DISTRIBUTION = 50.0;
	public static final double STD_DEV_PRICE_DISTRIBUTION = 6.0;

	public static final double MEAN_QTY_DISTRIBUTION = 100000.0;
	public static final double STD_DEV_QTY_DISTRIBUTION = 20000.0;
	
	private static final Random random = new Random(System.currentTimeMillis());
	
	private static final long MILLIS_PER_SECOND = 1000;
	private static final long MILLIS_PER_MIN = 60 * MILLIS_PER_SECOND;
	
	private static final long TIME_INTERVAL_BETWEEN_ORDERS_IN_SECONDS = 30 * MILLIS_PER_SECOND;
	private static final long PROGRAM_RUN_TIME_IN_MINS = 15 * MILLIS_PER_MIN;

	public static void main(String[] args) {
		Exchange exchange = new Exchange();
		long startTime = System.currentTimeMillis();
		while ((System.currentTimeMillis() - startTime) <= PROGRAM_RUN_TIME_IN_MINS) {
			for (Side side :Side.values()) {
				Order order = createOrderFromDistribution(side);
				System.out.println("Sending Order, " + order);
				exchange.sendOrder(order);
			}
			try {
				Thread.sleep(TIME_INTERVAL_BETWEEN_ORDERS_IN_SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(exchange.getBookForSymbol(DEFAULT_SYMBOL));
		MatchingResult result = exchange.executeMatchingAlgo(new VolumeMaximizationAlgo(), DEFAULT_SYMBOL);
		if(result.matched()) {
			System.out.println("Match found in book,\n" + exchange.getBookForSymbol(DEFAULT_SYMBOL));
			System.out.println("Match maximum volume," + result.getMatchingVolume() + " at price," + result.getMatchingPrice());
		}
		else
			System.out.println("Match not found in book," + exchange.getBookForSymbol(DEFAULT_SYMBOL));
	}

	public static Order createOrderFromDistribution(Side side) {
		long qty = getLong(MEAN_QTY_DISTRIBUTION, STD_DEV_QTY_DISTRIBUTION);
		double price = getDoubleWithPrecision(MEAN_PRICE_DISTRIBUTION, STD_DEV_PRICE_DISTRIBUTION);
		
		return new Order(++DEFAULT_ORDER_ID, DEFAULT_SYMBOL, side, qty, price);
	}

	public static long getLong(double mean, double stdDeviation) {
		return (long)((random.nextGaussian() * stdDeviation ) + mean);
	}
	
	public static double getDoubleWithPrecision(double mean, double stdDeviation) {
		double value = (random.nextGaussian() * stdDeviation ) + mean;
		long price = ExchangeUtils.convertPriceToLong(value);
		return ExchangeUtils.convertPriceToDouble(price);
	}
}
