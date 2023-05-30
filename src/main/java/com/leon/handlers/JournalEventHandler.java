package com.leon.handlers;

import com.leon.disruptors.DisruptorEvent;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JournalEventHandler implements EventHandler<DisruptorEvent>
{
	private static Logger logger = LoggerFactory.getLogger(JournalEventHandler.class);

	@Override
	public void onEvent(DisruptorEvent event, long sequence, boolean enfOfBatch)
	{
		logger.info(event.getPayload().toString());
	}
}