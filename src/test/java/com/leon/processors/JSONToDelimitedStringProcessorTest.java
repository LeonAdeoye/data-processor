package com.leon.processors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
		"JSONToDelimitedString.processing=0",
		"processor.delimiter=,",
		"processor.fieldListToExtract=firstName,lastName,email"
})
public class JSONToDelimitedStringProcessorTest
{
	@Autowired
	private JSONToDelimitedStringProcessorImpl processor;

	@BeforeEach
	public void setUp()
	{
		processor.getListOfPropertiesToExtract();
	}

	@Test
	public void process_whenPayloadIsEmpty_shouldReturnEmptyString()
	{
		// Act
		String result = processor.process("");
		// Assert
		assertEquals("", result);
	}

	@Test
	public void process_whenValidPayload_shouldConvertToDelimitedString()
	{
		// Arrange
		String jsonPayload = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\"}";
		String expectedResult = "John,Doe,john@example.com";
		// Act
		String result = processor.process(jsonPayload);
		// Assert
		assertEquals(expectedResult, result);
	}

	@Test
	public void process_whenMissingPropertyInPayload_shouldReturnEmptyStringForMissingProperty()
	{
		// Arrange
		String jsonPayload = "{\"firstName\":\"John\",\"email\":\"john@example.com\"}";
		String expectedResult = "John,,john@example.com";
		// Act
		String result = processor.process(jsonPayload);
		// Assert
		assertEquals(expectedResult, result);
	}

	@Test
	public void process_whenMissingAllPropertyInPayload_shouldReturnMultipleEmptyStrings()
	{
		// Arrange
		String jsonPayload = "{\"age\":5,\"hobby\":\"Chess\"}";
		String expectedResult = ",,";
		// Act
		String result = processor.process(jsonPayload);
		// Assert
		assertEquals(expectedResult, result);
	}
}
