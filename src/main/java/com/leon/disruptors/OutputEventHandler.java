package com.leon.disruptors;

import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputEventHandler implements EventHandler<DisruptorEvent>
{
	private static Logger logger = LoggerFactory.getLogger(OutputEventHandler.class);

	@Override
	public void onEvent(DisruptorEvent disruptorEvent, long sequence, boolean endOfBatch)
	{
		logger.info(disruptorEvent.getPayload().toString());
	}
}
