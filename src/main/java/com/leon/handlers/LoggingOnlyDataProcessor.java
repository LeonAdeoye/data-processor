package com.leon.handlers;

import com.leon.disruptors.DisruptorEvent;
import com.leon.disruptors.DisruptorPayload;
import com.leon.disruptors.DisruptorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lmax.disruptor.EventHandler;
import org.springframework.stereotype.Service;

@Service
public class LoggingOnlyDataProcessor implements EventHandler<DisruptorEvent>
{
	private static final Logger logger = LoggerFactory.getLogger(LoggingOnlyDataProcessor.class);

	private DisruptorService outboundDisruptor;

	public void setOutboundDisruptor(DisruptorService outboundDisruptor)
	{
		this.outboundDisruptor = outboundDisruptor;
	}

	private void process(DisruptorEvent disruptorEvent)
	{
		String payload = disruptorEvent.getPayload().getPayload().toString();
		logger.info("Logging payload: {}", payload);
		outboundDisruptor.push(new DisruptorPayload(payload));
	}

	@Override
	public void onEvent(DisruptorEvent disruptorEvent, long sequence, boolean endOfBatch)
	{
		process(disruptorEvent);
	}

}
