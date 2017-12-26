
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.exchange.Exchange;
import com.exchange.algo.MatchingResult;
import com.exchange.algo.VolumeMaximizationAlgo;
import com.exchange.data.Execution;
import com.exchange.data.Order;
import com.exchange.data.Side;
import com.exchange.util.ExchangeUtils;

public class VolumeMaximizerSimulation {
	/** Modify to change starting order id in program */
	public static int DEFAULT_ORDER_ID = 0;
	/** Modify to change symbol used in program */
	public static final String DEFAULT_SYMBOL = "0005.HK";
	/** Modify to change decimal precision of price at exchange level */
	public static final int DECIMAL_PRECISION = 5;

	/** Modify to change how price is drawn from normal distribution */
	public static final double MEAN_FOR_PRICE = 50.0;
	public static final double STD_DEVIATION_FOR_PRICE = 6.0;

	/** Modify to change how quantity is drawn from normal distribution */
	public static final double MEAN_FOR_QUANTITY = 100000.0;
	public static final double STD_DEVIATION_FOR_QUANTITY = 20000.0;

	/** DO NOT MODIFY */
	private static final long MILLIS_PER_SECOND = 1000;
	/** DO NOT MODIFY */
	private static final long MILLIS_PER_MIN = 60 * MILLIS_PER_SECOND;

	/**
	 * Modify to change minimum time interval in seconds between order send
	 * operation, each operation sends 1 buy and 1 sell order
	 */
	private static final long TIME_INTERVAL_BETWEEN_ORDERS_IN_SECONDS = 30 * MILLIS_PER_SECOND;
	/** Modify to change total program run duration in minutes */
	private static final long PROGRAM_RUN_TIME_IN_MINS = 15 * MILLIS_PER_MIN;

	private static final Random random = new Random(System.currentTimeMillis());

	public static void main(String[] args) {
		Exchange exchange = new Exchange(DECIMAL_PRECISION);

		long startTime = System.currentTimeMillis();
		System.out.println("Order send operations started at," + new Date(startTime));

		do {
			for (Side side : Side.values()) {
				Order order = createOrderFromDistribution(side);
				System.out.println("Sending Order, " + order);
				exchange.sendOrder(order);
			}
			try {
				Thread.sleep(TIME_INTERVAL_BETWEEN_ORDERS_IN_SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while ((System.currentTimeMillis() - startTime) <= PROGRAM_RUN_TIME_IN_MINS);

		long endTime = System.currentTimeMillis();
		System.out.println("Order send operations started at," + new Date(endTime));

		System.out.println(
				"Order Book so far,\n" + exchange.getBookForSymbol(DEFAULT_SYMBOL).printBook(DECIMAL_PRECISION));
		MatchingResult result = exchange.runMatchingAlgo(new VolumeMaximizationAlgo(), DEFAULT_SYMBOL);
		if (result.matched()) {
			System.out.println("Match found, maximum volume," + result.getMatchingVolume() + " at price,"
					+ result.getMatchingPrice());
			List<Execution> executions = exchange.executeMatch(DEFAULT_SYMBOL, result.getMatchingPrice(),
					result.getMatchingVolume());
			for(Execution exec: executions)
				System.out.println(exec);
		} else
			System.out.println("Match not found in book,"
					+ exchange.getBookForSymbol(DEFAULT_SYMBOL).printBook(DECIMAL_PRECISION));
	}

	public static Order createOrderFromDistribution(Side side) {
		long qty = getLong(MEAN_FOR_QUANTITY, STD_DEVIATION_FOR_QUANTITY);
		double price = getDoubleWithPrecision(MEAN_FOR_PRICE, STD_DEVIATION_FOR_PRICE);

		return new Order(++DEFAULT_ORDER_ID, DEFAULT_SYMBOL, side, qty, price);
	}

	public static long getLong(double mean, double stdDeviation) {
		return (long) ((random.nextGaussian() * stdDeviation) + mean);
	}

	public static double getDoubleWithPrecision(double mean, double stdDeviation) {
		double value = (random.nextGaussian() * stdDeviation) + mean;
		long price = ExchangeUtils.convertPriceToLong(value, DECIMAL_PRECISION);
		return ExchangeUtils.convertPriceToDouble(price, DECIMAL_PRECISION);
	}
}
