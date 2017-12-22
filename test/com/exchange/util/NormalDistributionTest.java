package com.exchange.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.exchange.ProblemRunner;
import com.exchange.data.Order;
import com.exchange.data.Side;

class NormalDistributionTest {
	
	@Test
	void testOrderCreationUsingNormalDistribution() {
		
		Order order = ProblemRunner.createOrderFromDistribution(Side.Buy);
		assertNotNull(order);
		assertEquals(true, order.getPrice() > 0);
		assertEquals(true, order.getQuantity() > 0);
		assertEquals(true, order.getTimestamp() > 0);
		
		order = ProblemRunner.createOrderFromDistribution(Side.Sell);
		assertNotNull(order);
		assertEquals(true, order.getPrice() > 0);
		assertEquals(true, order.getQuantity() > 0);
		assertEquals(true, order.getTimestamp() > 0);
	}

}
