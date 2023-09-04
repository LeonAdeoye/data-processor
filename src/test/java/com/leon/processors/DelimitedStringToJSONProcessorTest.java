package com.leon.processors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
		"delimitedStringToJSON.processing=1",
		"processor.delimiter=,",
		"processor.fieldListToExtract=firstName,surname,age"
})
public class DelimitedStringToJSONProcessorTest
{
	@Autowired
	private DelimitedStringToJSONProcessorImpl processor;

	@BeforeEach
	public void setUp()
	{
		processor.getListOfPropertiesToExtract();
	}

	@Test
	public void process_WhenPayloadIsEmpty_ReturnsEmptyString()
	{
		// Arrange
		String payload = "";

		// Act
		String result = processor.process(payload);

		// Assert
		assertEquals("", result);
	}

	@Test
	public void process_WhenValidPayload_ReturnsJSONString()
	{
		// Arrange
		String payload = "John,Doe,30";
		// Act
		String result = processor.process(payload);
		// Assert
		assertEquals("{\"firstName\":\"John\",\"surname\":\"Doe\",\"age\":\"30\"}", result);
	}

	@Test
	public void process_WhenPayloadDoesNotMatchProperties_ReturnsEmptyString()
	{
		// Arrange
		String payload = "John,Doe";

		// Act
		String result = processor.process(payload);

		// Assert
		assertEquals("", result);
	}
}
