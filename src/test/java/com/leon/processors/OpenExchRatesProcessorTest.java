package com.leon.processors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;

@SpringBootTest
@TestPropertySource(properties = {
		"open.exchange.rates.processing=0",
		"log.processing=1"
})
class OpenExchRatesProcessorTest
{
	@Autowired
	private OpenExchRatesProcessorImpl processor;
	private ObjectMapper objectMapper;
	private ObjectMapper resultMapper;

	@BeforeEach
	void setUp()
	{
		objectMapper = new ObjectMapper();
		resultMapper = new ObjectMapper();
	}

	@Test
	void process_WithValidPayload_ReturnsProcessedData()
	{
		// Arrange
		String validPayload = "{\"rates\": {\"USD\": 10, \"GBP\": 7, \"EUR\": 9}}";
		ObjectNode expectedNode = resultMapper.createObjectNode();
		expectedNode.put("type", "rates");
		expectedNode.put("source", "openexchangerates.org");
		expectedNode.putArray("rates")
				.add(resultMapper.createObjectNode().put("currency", "USD").put("rate", "10.000"))
				.add(resultMapper.createObjectNode().put("currency", "GBP").put("rate", "7.000"))
				.add(resultMapper.createObjectNode().put("currency", "EUR").put("rate", "9.000"));
		// Act
		String result = processor.process(validPayload);
		// Assert
		assertEquals(expectedNode.toString(), result);
	}

	@Test
	void process_WithMissingRates_ReturnsError() {
		// Arrange
		String payloadWithoutRates = "{\"someKey\": 123}";
		// Act
		String result = processor.process(payloadWithoutRates);
		// Assert
		assertEquals("{\"type\": \"error\", \"source\": \"openexchangerates.org\"}", result);
	}

	@Test
	void process_WithInvalidPayload_ReturnsError() {
		// Arrange
		String invalidPayload = "invalid_json_here";
		// Act
		String result = processor.process(invalidPayload);
		// Assert
		assertEquals("{\"type\": \"error\", \"source\": \"openexchangerates.org\"}", result);
	}
}


