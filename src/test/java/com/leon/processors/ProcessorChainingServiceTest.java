package com.leon.processors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
	"timestamp.processing=2",
	"kraken.price.processing=3",
	"log.processing=4",
	"filter.processing=1"
})
public class ProcessorChainingServiceTest
{
	@Autowired
	private ProcessorChainingService processorChain;

	@BeforeEach
	public void setUp()
	{
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void chainProcessing_whenProcessorsExist_ShouldCallProcessOnEachProcessorInOrder()
	{
		// Arrange
		Processor processor1 = mock(Processor.class);
		Processor processor2 = mock(Processor.class);
		when(processor1.getProcessingOrder()).thenReturn(1);
		when(processor2.getProcessingOrder()).thenReturn(2);
		when(processor2.process("Sample payload")).thenReturn("Sample payload");
		when(processor1.process("Sample payload")).thenReturn("Sample payload");
		List<Processor> processors = Arrays.asList(processor2, processor1);
		ProcessorChainingServiceImpl chainingService = new ProcessorChainingServiceImpl(processors);
		// Act
		String result = chainingService.chainProcessing("Sample payload");
		// Assert
		assertEquals("Sample payload", result);
		verify(processor2).process("Sample payload");
		verify(processor1).process("Sample payload");
	}

	@Test
	public void getProcessorCount_whenProcessorsExist_ShouldReturnNumberOfProcessors()
	{
		// Arrange
		Processor processor1 = mock(Processor.class);
		Processor processor2 = mock(Processor.class);
		List<Processor> processors = Arrays.asList(processor2, processor1);
		ProcessorChainingServiceImpl chainingService = new ProcessorChainingServiceImpl(processors);
		// Act
		int count = chainingService.getProcessorCount();
		// Assert
		assertEquals(2, count);
	}

	@Test
	public void getProcessorCount_whenNoProcessorsExist_ShouldReturnZero()
	{
		// Arrange
		List<Processor> processors = new ArrayList<>();
		ProcessorChainingServiceImpl chainingService = new ProcessorChainingServiceImpl(processors);
		// Act
		int count = chainingService.getProcessorCount();
		// Assert
		assertEquals(0, count);
	}
}
