package com.leon.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
		"JSONField.processing=0",
		"processor.fieldListToExtract=field1,field2",
})
public class JSONFieldExtractProcessorTest
{
	@Autowired
	private JSONFieldExtractProcessorImpl processor;
	@Mock
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp()
	{
		processor.getListOfPropertiesToExtract();
	}

	@Test
	public void process_WhenFieldsExistInPayload_ShouldExtractAndReturnResultJson()
	{
		// Arrange
		String inputPayload = "{\"field1\": \"value1\", \"field2\": \"value2\", \"field3\": \"value3\"}";
		String expectedJson = "{\"field1\":\"value1\",\"field2\":\"value2\"}";
		// Act
		String result = processor.process(inputPayload);
		// Assert
		assertEquals(expectedJson, result);
	}

	@Test
	public void process_WhenNoFieldsExistInPayload_ShouldReturnEmptyPayload()
	{
		// Arrange
		String inputPayload = "{\"field3\": \"value3\", \"field4\": \"value4\"}";
		// Act
		String result = processor.process(inputPayload);
		// Assert
		assertEquals("", result);
	}

	@Test
	public void process_WhenEmptyPayload_ShouldReturnEmptyPayload()
	{
		// Arrange
		String inputPayload = "";
		// Act
		String result = processor.process(inputPayload);
		// Assert
		assertEquals(inputPayload, result);
	}

	@Test
	public void process_WhenInvalidJsonPayload_ShouldReturnOriginalPayload()
	{
		// Arrange
		String inputPayload = "Invalid JSON";
		// Act
		String result = processor.process(inputPayload);
		// Assert
		assertEquals(inputPayload, result);
	}

	@Test
	public void process_WhenIOExceptionOccurs_ShouldReturnOriginalPayload() throws Exception
	{
		// Arrange
		String invalidPayload = "{\"field1\" \"value1, \"field2\": \"value2\"}";
		// Act
		String result = processor.process(invalidPayload);
		// Assert
		assertEquals(invalidPayload, result);
	}
}

