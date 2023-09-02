package com.leon.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value="log.processing", matchIfMissing = false)
public class LoggingOnlyProcessorImpl implements Processor
{
	private static final Logger logger = LoggerFactory.getLogger(LoggingOnlyProcessorImpl.class);

	@Value("${log.processing}")
	private int processingOrder;

	@Override
	public int getProcessingOrder()
	{
		return this.processingOrder;
	}

	@Override
	public String process(String payload)
	{
		if(payload.isEmpty())
			return "";

		logger.info("Logging payload: {}", payload);

		return payload;
	}
}
