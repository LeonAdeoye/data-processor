package com.leon.connectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component
@ConditionalOnProperty(value="jms.input.reader", havingValue = "true")
public class JmsErrorHandler implements ErrorHandler
{
	private static final Logger logger = LoggerFactory.getLogger(JmsErrorHandler.class);

	@Override
	public void handleError(Throwable t)
	{
		logger.error("Within default jms error handler, error thrown : {}", t.getMessage());
	}
}
