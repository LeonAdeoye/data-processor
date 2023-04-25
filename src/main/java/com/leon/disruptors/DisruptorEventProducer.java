package com.leon.disruptors;

import com.lmax.disruptor.RingBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisruptorEventProducer
{
	private static final Logger logger = LoggerFactory.getLogger(DisruptorEventProducer.class);

	private final RingBuffer<DisruptorEvent> ringBuffer;

	public DisruptorEventProducer(RingBuffer<DisruptorEvent> ringBuffer)
	{
		this.ringBuffer = ringBuffer;
	}

	public void onData(DisruptorPayload payload)
	{
		long sequence  = ringBuffer.next();
		try
		{
			DisruptorEvent event = ringBuffer.get(sequence);
			event.setPayload(payload);
		}
		finally
		{
			ringBuffer.publish(sequence);
		}
	}
}