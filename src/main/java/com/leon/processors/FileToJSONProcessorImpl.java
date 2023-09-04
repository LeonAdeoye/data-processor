package com.leon.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value="fileToJSON.processing", matchIfMissing = false)
public class FileToJSONProcessorImpl implements Processor
{
	private static final Logger logger = LoggerFactory.getLogger(FileToJSONProcessorImpl.class);

	@Value("${fileToJSON.processing}")
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
