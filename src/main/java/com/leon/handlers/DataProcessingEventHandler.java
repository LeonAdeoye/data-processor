package com.leon.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leon.disruptors.DisruptorEvent;
import com.leon.disruptors.DisruptorPayload;
import com.leon.disruptors.DisruptorService;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DataProcessingEventHandler implements EventHandler<DisruptorEvent>
{
	private static final Logger logger = LoggerFactory.getLogger(DataProcessingEventHandler.class);

	@Value("${input.reader.include.filter}")
	private String includeFilter;

	@Value("${input.reader.exclude.filter}")
	private String excludeFilter;

	@PostConstruct
	public void initialize()
	{
		logger.info("DataProcessingEventHandler Include filter: {}", includeFilter);
		logger.info("DataProcessingEventHandler Exclude filter: {}", excludeFilter);
	}

	private SubProcessor subProcessor;

	private DisruptorService outboundDisruptor;

	public void setOutboundDisruptor(DisruptorService outboundDisruptor)
	{
		this.outboundDisruptor = outboundDisruptor;
	}

	public void setSubProcessor(SubProcessor subProcessor)
	{
		this.subProcessor = subProcessor;
	}

	@Override
	public void onEvent(DisruptorEvent disruptorEvent, long sequence, boolean endOfBatch)
	{
		process(disruptorEvent);
	}

	private void process(DisruptorEvent disruptorEvent)
	{
		String payload = disruptorEvent.getPayload().getPayload().toString();
		logger.trace("Before filtering, received this: {}", payload);

		if(filterOut(payload))
			return;

		logger.debug("After filtering will process this: {}", payload);
		String result = subProcessor.process(payload);
		this.outboundDisruptor.push(new DisruptorPayload(result));
	}

	private boolean filterOut(String payload)
	{
		boolean result;
		if(excludeFilter != null && !excludeFilter.isEmpty() && payload.contains(excludeFilter))
			result = true;
		else if(includeFilter != null && !includeFilter.isEmpty() && !payload.contains(includeFilter))
			result = true;
		else
			result = false;

		return result;
	}

}
