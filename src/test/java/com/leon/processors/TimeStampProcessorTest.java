package com.leon.processors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
		"timestamp.processing=10",
		"timestamp.processing.field.name=time_stamp"
})
public class TimeStampProcessorTest
{
	@Autowired
	private TimeStampingProcessorImpl processor;

	@Test
	public void process_whenValidPayloadWithTimeStamping_ShouldAddTimeStampField()
	{
		// Arrange
		String payload = "{\"data\":\"example\"}";
		// Act
		String result = processor.process(payload);
		// Assert
		assertEquals(true, result.contains("\"time_stamp\":"));
	}

	@Test
	public void process_whenEmptyPayload_ShouldReturnEmptyString()
	{
		// Arrange
		String payload = "";
		// Act
		String result = processor.process(payload);
		// Assert
		assertEquals("", result);
	}

	@Test
	public void getProcessingOrder_whenCalled_ShouldReturnProcessingOrder()
	{
		// Act
		int processingOrder = processor.getProcessingOrder();
		// Assert
		assertEquals(10, processingOrder); // Change to the actual expected processing order value
	}

	@Test
	public void process_whenInvalidJsonPayload_ShouldReturnOriginalPayload()
	{
		// Arrange
		String payload = "Invalid JSON Payload";
		// Act
		String result = processor.process(payload);
		// Assert
		assertEquals(payload, result);
	}
}
