package com.exchange.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ExchangeUtilsTest {

	@Test
	void testDoubleConversion() { 
		new ExchangeUtils();// Just for increasing coverage!!
		assertEquals(10009999, ExchangeUtils.convertPriceToDouble(10009999l, 0));
		assertEquals(1000999.9, ExchangeUtils.convertPriceToDouble(10009999l, 1));
		assertEquals(100099.99, ExchangeUtils.convertPriceToDouble(10009999l, 2));
		assertEquals(10009.999, ExchangeUtils.convertPriceToDouble(10009999l, 3));
		assertEquals(1000.9999, ExchangeUtils.convertPriceToDouble(10009999l, 4));
		
	}

	@Test
	void testLongConversion() {
		assertEquals(1000l, ExchangeUtils.convertPriceToLong(1000.9999, 0));
		assertEquals(10009l, ExchangeUtils.convertPriceToLong(1000.9999, 1));
		assertEquals(100099l, ExchangeUtils.convertPriceToLong(1000.9999, 2));
		assertEquals(1000999l, ExchangeUtils.convertPriceToLong(1000.9999, 3));
		assertEquals(10009999l, ExchangeUtils.convertPriceToLong(1000.9999, 4));
	}

}
