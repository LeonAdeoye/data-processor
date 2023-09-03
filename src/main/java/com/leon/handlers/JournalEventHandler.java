package com.leon.handlers;

import com.leon.disruptors.DisruptorEvent;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JournalEventHandler implements EventHandler<DisruptorEvent>
{
	private static Logger logger = LoggerFactory.getLogger(JournalEventHandler.class);

	@Override
	public void onEvent(DisruptorEvent event, long sequence, boolean enfOfBatch)
	{
		logger.debug(event.getPayload().toString());
	}
}
