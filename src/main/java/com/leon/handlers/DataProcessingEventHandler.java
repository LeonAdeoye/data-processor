package com.leon.handlers;

import com.leon.disruptors.DisruptorEvent;
import com.leon.disruptors.DisruptorPayload;
import com.leon.disruptors.DisruptorService;
import com.leon.processors.ProcessorChainingService;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataProcessingEventHandler implements EventHandler<DisruptorEvent>
{
	private final ProcessorChainingService processorChainingService;
	@Autowired
	DataProcessingEventHandler(ProcessorChainingService processorChainingService)
	{
		this.processorChainingService = processorChainingService;
	}
	private DisruptorService outboundDisruptor;

	public void setOutboundDisruptor(DisruptorService outboundDisruptor)
	{
		this.outboundDisruptor = outboundDisruptor;
	}

	private void process(DisruptorEvent disruptorEvent)
	{
		String payload = disruptorEvent.getPayload().getPayload().toString();

		if(processorChainingService.getProcessorCount() > 0)
			payload = processorChainingService.chainProcessing(payload);

		if(outboundDisruptor == null || payload.isEmpty())
			return;

		outboundDisruptor.push(new DisruptorPayload(payload));
	}

	@Override
	public void onEvent(DisruptorEvent disruptorEvent, long sequence, boolean endOfBatch)
	{
		process(disruptorEvent);
	}
}
