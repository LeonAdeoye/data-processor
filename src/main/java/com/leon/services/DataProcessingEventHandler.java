package com.leon.services;

import com.leon.disruptors.DisruptorEvent;
import com.leon.disruptors.DisruptorService;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DataProcessingEventHandler implements EventHandler<DisruptorEvent>
{
	private static final Logger logger = LoggerFactory.getLogger(DataProcessingEventHandler.class);

	private DisruptorService outboundDisruptor;

	public void setOutboundDisruptor(DisruptorService outboundDisruptor)
	{
		this.outboundDisruptor = outboundDisruptor;
	}

	@Override
	public void onEvent(DisruptorEvent disruptorEvent, long sequence, boolean endOfBatch)
	{
		process(disruptorEvent);
	}

	private void process(DisruptorEvent disruptorEvent)
	{
		logger.info(disruptorEvent.getPayload().toString());
		this.outboundDisruptor.push(disruptorEvent.getPayload());
	}
}
