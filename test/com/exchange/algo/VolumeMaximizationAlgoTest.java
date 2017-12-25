package com.exchange.algo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.exchange.Exchange;
import com.exchange.common.TestHelper;

class VolumeMaximizationAlgoTest extends TestHelper{

	@Test
	void testVolumeMaximization() {
		ExchangeAlgo volumeMax = new VolumeMaximizationAlgo();
		Exchange exchange = new Exchange();
		exchange.sendOrder(createBuyOrder(1000, 100.0));
		
	}

}
