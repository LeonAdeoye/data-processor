package com.leon.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
public class ProcessorChainingServiceImpl implements ProcessorChainingService
{
	private static final Logger logger = LoggerFactory.getLogger(ProcessorChainingServiceImpl.class);
	private final List<Processor> processors;

	@Autowired
	public ProcessorChainingServiceImpl(List<Processor> processors)
	{
		this.processors = processors;

		processors.sort(Comparator.comparingInt(Processor::getOrder));

		for (int order = 0; order < processors.size(); order++)
		{
			processors.get(order);
			logger.info("Processor belonging to class: {} added to processor chain, and executed in this order: {}", processors.get(order).getClass().getSimpleName(), order);
		}
	}

	@Override
	public String chainProcessing(String payload)
	{
		for (Processor processor : processors)
			payload = processor.process(payload);

		return payload;
	}

	@Override
	public int getProcessorCount()
	{
		return processors.size();
	}
}
