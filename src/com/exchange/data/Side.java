package com.exchange.data;

import java.util.Comparator;

/**
 * List of Sides allowed for an order
 */
public enum Side {
	Buy('B'), Sell('S');

	char ch;
	Comparator<Long> comparator;

	public Comparator<Long> getComparator() {
		return comparator;
	}

	private Side(char ch) {
		this.ch =ch;
		this.comparator = createComparator(ch);
	}

	private Comparator<Long> createComparator(char ch) {
		if (ch == 'B')
			return new PriceMaxComparator();
		else
			return new PriceMinComparator();

	}

	private class PriceMinComparator implements Comparator<Long> {
		@Override
		public int compare(Long o1, Long o2) {
			if (o2.longValue() == o1.longValue())
				return 0;
			else if (o2.longValue() > o1.longValue())
				return -1;
			else
				return 1;
		}
	}

	private class PriceMaxComparator implements Comparator<Long> {
		@Override
		public int compare(Long o1, Long o2) {
			if (o2.longValue() == o1.longValue())
				return 0;
			else if (o2.longValue() > o1.longValue())
				return 1;
			else
				return -1;
		}
	}
}
