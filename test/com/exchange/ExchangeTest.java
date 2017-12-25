package com.exchange;

import org.junit.jupiter.api.Test;

import com.exchange.common.TestHelper;

class ExchangeTest extends TestHelper{

	@Test
	void testOrderForMultipleSymbols() {
		Exchange exchange = new Exchange();
		exchange.sendOrder(createBuyOrder(1000, 10.0));
	}

	void testOrderForCancelOrderForMultipleSymbols() {
	}

}
