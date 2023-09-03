package com.leon.processors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
		"processor.include.filter=included",
		"processor.exclude.filter=excluded",
		"filter.processing=10"
})
public class FilterProcessorTest {
	@Autowired
	private FilterProcessorImpl processor;

	@Test
	public void process_whenValidPayloadWithInclusionFilter_ShouldReturnPayload()
	{
		// Arrange
		String payload = "This payload is included.";
		// Act
		String result = processor.process(payload);
		// Assert
		assertEquals(payload, result);
	}

	@Test
	public void process_whenBothMatchingExcludeAndIncludeFiltersAreSet_ShouldReturnEmptyPayload()
	{
		// Arrange
		String payload = "This payload has both included nor excluded filters but exclude takes priority";
		// Act
		String result = processor.process(payload);
		// Assert
		assertEquals("", result);
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
	public void process_whenPayloadMatchingExclusionFilter_ShouldReturnEmptyString()
	{
		// Arrange
		String payload = "This payload is excluded.";
		// Act
		String result = processor.process(payload);
		// Assert
		assertEquals("", result);
	}

	@Test
	public void getProcessingOrder_whenCalled_ShouldReturnIntegerMaxValue()
	{
		// Act
		int processingOrder = processor.getProcessingOrder();
		// Assert
		assertEquals(10, processingOrder);
	}
}
