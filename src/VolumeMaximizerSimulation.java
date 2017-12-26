
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
	public static int DEFAULT_ORDER_ID = 0;
	public static final String DEFAULT_SYMBOL = "0005.HK";
	
	/** Decimal precision for price at exchange level */
	public static final int DECIMAL_PRECISION = 5;

	/** Normal distribution parameters to draw price */
	public static final double MEAN_FOR_PRICE = 50.0;
	public static final double STD_DEVIATION_FOR_PRICE = 6.0;

	/** Normal distribution parameters to draw quantity */
	public static final double MEAN_FOR_QUANTITY = 100000.0;
	public static final double STD_DEVIATION_FOR_QUANTITY = 20000.0;

	/** Time interval in seconds between order send operation */
	private static final int WAIT_INTERVAL_IN_SECONDS = 30;
	/** Total program run time in minutes */
	private static final int PROGRAM_RUN_TIME_IN_MINS = 15;

	private static final Random random = new Random(System.currentTimeMillis());

	public static void main(String[] args) {
		Exchange exchange = new Exchange(DECIMAL_PRECISION);
		long startTime = System.currentTimeMillis();
		System.out.println("Order send operations started at," + new Date(startTime));
		
		//Send orders to exchange
		do {
			createPairOfBuySellOrders(exchange);
			waitForTimeInSeconds(WAIT_INTERVAL_IN_SECONDS);
		} while (hasProgramRuntimeElapsed(startTime, PROGRAM_RUN_TIME_IN_MINS));

		long endTime = System.currentTimeMillis();
		System.out.println("Order send operations complete at," + new Date(endTime));
		
		System.out.println("Order Book,");
		System.out.println(exchange.getBookForSymbol(DEFAULT_SYMBOL).printBook(DECIMAL_PRECISION));
		
		//Run Volume maximization Algorithm on order book
		MatchingResult rs = exchange.runMatchingAlgo(new VolumeMaximizationAlgo(), DEFAULT_SYMBOL);
		if (rs.matched()) {
			System.out.println("Match found, Highest volume," + rs.getVolume() + 
					" at price," + ExchangeUtils.convertPriceToDouble(rs.getPrice(), DECIMAL_PRECISION));
			
			//Get executions
			List<Execution> executions = exchange.executeMatch(DEFAULT_SYMBOL, rs.getPrice(),	rs.getVolume());
			
			//Print executions received
			for(Execution exec: executions)
				System.out.println(exec.print(DECIMAL_PRECISION));
		} else
			System.out.println("Match not found");
	}

	private static void waitForTimeInSeconds(int time) {
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static boolean hasProgramRuntimeElapsed(long startTime, int programRunTime) {
		long millisInMin = 60 * 1000;
		return (System.currentTimeMillis() - startTime) <= (programRunTime * millisInMin);
	}

	private static void createPairOfBuySellOrders(Exchange exchange) {
		for (Side side : Side.values()) {
			Order order = createOrderFromDistribution(side);
			System.out.println("Sending Order, " + order);
			exchange.sendOrder(order);
		}
	}

	private static Order createOrderFromDistribution(Side side) {
		long qty = getLong(MEAN_FOR_QUANTITY, STD_DEVIATION_FOR_QUANTITY);
		double price = getDoubleWithPrecision(MEAN_FOR_PRICE, STD_DEVIATION_FOR_PRICE);

		return new Order(++DEFAULT_ORDER_ID, DEFAULT_SYMBOL, side, qty, price);
	}

	private static long getLong(double mean, double stdDeviation) {
		return (long) ((random.nextGaussian() * stdDeviation) + mean);
	}

	private static double getDoubleWithPrecision(double mean, double stdDeviation) {
		double value = (random.nextGaussian() * stdDeviation) + mean;
		long price = ExchangeUtils.convertPriceToLong(value, DECIMAL_PRECISION);
		return ExchangeUtils.convertPriceToDouble(price, DECIMAL_PRECISION);
	}
}
