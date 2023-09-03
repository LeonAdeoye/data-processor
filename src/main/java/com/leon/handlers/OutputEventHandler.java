package com.leon.handlers;

import com.leon.disruptors.DisruptorEvent;
import com.leon.writers.OutputWriter;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutputEventHandler implements EventHandler<DisruptorEvent>
{
	private static Logger logger = LoggerFactory.getLogger(OutputEventHandler.class);
	private final OutputWriter writer;

	@Autowired
	public OutputEventHandler(OutputWriter writer)
	{
		this.writer = writer;
	}

	@Override
	public void onEvent(DisruptorEvent disruptorEvent, long sequence, boolean endOfBatch) throws Exception
	{
		if(writer != null)
			this.writer.write(disruptorEvent.getPayload().getPayload());
	}
}
