package com.leon.handlers;

import com.leon.disruptors.DisruptorEvent;
import com.leon.connectors.OutputWriter;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputEventHandler implements EventHandler<DisruptorEvent>
{
	private static Logger logger = LoggerFactory.getLogger(OutputEventHandler.class);
	private OutputWriter writer;

	public OutputEventHandler(OutputWriter writer)
	{
		this.writer = writer;
	}

	@Override
	public void onEvent(DisruptorEvent disruptorEvent, long sequence, boolean endOfBatch) throws Exception
	{
		if(writer != null)
			this.writer.write(disruptorEvent.getPayload().toString());
	}
}
