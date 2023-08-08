package com.leon.handlers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class KrakenPriceSubProcessorTest {
	private KrakenPriceSubProcessorImpl krakenPriceSubProcessorImpl = new KrakenPriceSubProcessorImpl();

	@Test
	void process_shouldReturnPriceObjectForValidPayload() {
		// Arrange
		String validPayload = "[340, {\"a\": [\"31151.70000\", 0, \"0.00557338\"], \"b\": [\"31151.60000\", 6, \"6.96462684\"], \"c\": [\"31151.70000\", \"0.00006325\"], \"v\": [\"953.35181836\", \"4877.34274692\"], \"p\": [\"31351.04008\", \"31214.62863\"], \"t\": [13636, 50519], \"l\": [\"31100.00000\", \"30467.50000\"], \"h\": [\"31634.20000\", \"31790.80000\"], \"o\": [\"31486.10000\", \"30606.50000\"] }, \"ticker\", \"XBT/USD\"]";

		// Act
		String result = krakenPriceSubProcessorImpl.process(validPayload);

		// Assert
		Assertions.assertEquals("{\"type\": \"price\", \"source\": \"kraken.com\", \"best_ask\": 31151.70, \"best_bid\": 31151.60, \"close\": 31151.70, \"high\": 31634.20, \"low\": 31100.00, \"open\": 31486.10, \"vol_today\": 953.35, \"vol_24h\": 4877.34, \"vwap_today\": 31351.04, \"vwap_24h\": 31214.63, \"num_trades\": 13636, \"num_trades_24h\": 50519, \"symbol\": \"XBT/USD\"}", result);
	}

	@Test
	void process_shouldReturnErrorObjectForMissingPrices() {
		// Arrange
		String invalidPayload = "[340, {\"type\": \"invalid\"}, \"ticker\", \"XBT/USD\"]";

		// Act
		String result = krakenPriceSubProcessorImpl.process(invalidPayload);

		// Assert
		Assertions.assertEquals("{\"type\": \"price\", \"source\": \"kraken.com\", \"symbol\": \"XBT/USD\"}", result);
	}

	@Test
	void process_shouldReturnErrorObjectForMissingSymbol() {
		// Arrange
		String invalidPayload = "[340, {\"a\":[\"31151.70000\",0,\"0.00557338\"],\"b\":[\"31151.60000\",6,\"6.96462684\"],\"c\":[\"31151.70000\",\"0.00006325\"],\"v\":[\"953.35181836\",\"4877.34274692\"],\"p\":[\"31351.04008\",\"31214.62863\"],\"t\":[13636,50519],\"l\":[\"31100.00000\",\"30467.50000\"],\"h\":[\"31634.20000\",\"31790.80000\"],\"o\":[\"31486.10000\",\"30606.50000\"]}, \"ticker\"]";

		// Act
		String result = krakenPriceSubProcessorImpl.process(invalidPayload);

		// Assert
		Assertions.assertEquals("{\"type\": \"error\", \"source\": \"kraken.com\"}", result);
	}

	@Test
	void process_shouldReturnErrorObjectForInvalidPayload() {
		// Arrange
		String invalidPayload = "[340, \"invalid payload\"]";

		// Act
		String result = krakenPriceSubProcessorImpl.process(invalidPayload);

		// Assert
		Assertions.assertEquals("{\"type\": \"error\", \"source\": \"kraken.com\"}", result);
	}
}
