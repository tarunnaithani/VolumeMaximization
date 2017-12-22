package com.exchange.data;


import org.junit.Assert;
import org.junit.Test;

public class OrderTest {

	@Test
	public void testOrderCreation() {
		Order order = new Order(1, "0001.HK", Side.Buy, 100, 10.1);
		Assert.assertNotNull(order);
		
		Assert.assertEquals("0001.HK", order.getSymbol());
		Assert.assertEquals(Side.Buy, order.getSide());
		Assert.assertEquals(100, order.getQuantity());
		Assert.assertEquals(10100, order.getPrice(), 101000);
		
		Order order1 = new Order(1, "0001.HK", Side.Buy, 1, 0.001);
		Assert.assertNotNull(order1);
		
		Order order2 = new Order(1, "0001.HK", Side.Buy, Long.MAX_VALUE, 1000);
		Assert.assertNotNull(order2);

	}

	@Test
	public void testCreateValidations() {
		try{
			new Order(1, "0001.HK", null, 100, 10.1);
			Assert.fail("Side check not working in order  creation");
		}
		catch(IllegalArgumentException e)
		{}
		try{
			new Order(1, "0001.HK", Side.Buy, 0, 10.1);
			Assert.fail("Quantity check not working in order  creation");
		}
		catch(IllegalArgumentException e)
		{}
		try{
			new Order(1, "0001.HK", Side.Buy, 100, 0.0);
			Assert.fail("Price check not working in order  creation");
		}
		catch(IllegalArgumentException e)
		{}
		try{
			new Order(1, "0001.HK", Side.Buy, 100, 10.1234);
			Assert.fail("Price decimal check not working in order  creation");
		}
		catch(IllegalArgumentException e)
		{}
	}
}
