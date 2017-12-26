package com.exchange.algo;

/**
 * Stores result of running Matching Algorithm on an order book
 *
 */
public class MatchingResult {

	/** Whether matching was successful or not */
	private final boolean matched;
	/** Price at which match was found */
	private final long matchingPrice;
	/** Volume matched */
	private final long matchingVolume;

	public MatchingResult(boolean result, long matchingPrice, long matchingVolume) {
		this.matched = result;
		this.matchingPrice = matchingPrice;
		this.matchingVolume = matchingVolume;
	}

	public boolean matched() {
		return matched;
	}

	public long getPrice() {
		return matchingPrice;
	}

	public long getVolume() {
		return matchingVolume;
	}

}
