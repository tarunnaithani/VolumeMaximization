package com.exchange.algo;

public class MatchingResult {
	
	/** Whether matching was successful or not */
	private final boolean matched;
	/** Price at which match was found */
	private final double matchingPrice;
	/** Volume matched */
	private final long matchingVolume;
	
	public MatchingResult(boolean result, double matchingPrice, long matchingVolume) {
		this.matched = result;
		this.matchingPrice = matchingPrice;
		this.matchingVolume = matchingVolume;
	}
	public boolean matched() {
		return matched;
	}
	public double getMatchingPrice() {
		return matchingPrice;
	}
	public long getMatchingVolume() {
		return matchingVolume;
	}

}
