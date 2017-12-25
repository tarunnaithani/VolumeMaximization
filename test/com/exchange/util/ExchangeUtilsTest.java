package com.exchange.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ExchangeUtilsTest {

	@Test
	void testDoubleConversion() {
		assertEquals(1000.9999, ExchangeUtils.convertPriceToDouble(10009999l));
	}

	@Test
	void testLongConversion() {
		assertEquals(10009999l, ExchangeUtils.convertPriceToLong(1000.9999));
	}

}
