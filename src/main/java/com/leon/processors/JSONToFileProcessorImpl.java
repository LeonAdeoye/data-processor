package com.leon.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value="JSONtoFile.processing", matchIfMissing = false)
public class JSONToFileProcessorImpl implements Processor
{
	private static final Logger logger = LoggerFactory.getLogger(JSONToFileProcessorImpl.class);

	@Value("${JSONtoFile.processing}")
	private int processingOrder;

	@Override
	public String process(String payload)
	{
		if(payload.isEmpty())
			return "";

		return payload;
	}

	@Override
	public int getProcessingOrder()
	{
		return processingOrder;
	}
}
