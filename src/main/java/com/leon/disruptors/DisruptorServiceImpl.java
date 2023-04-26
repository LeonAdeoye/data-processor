package com.leon.disruptors;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lmax.disruptor.EventHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Scope("prototype")
@Service
public class DisruptorServiceImpl implements DisruptorService
{
	private static final Logger logger = LoggerFactory.getLogger(DisruptorServiceImpl.class);
	private String name;
	private long counter = 0;
	private Disruptor<DisruptorEvent> disruptor;
	private DisruptorEventProducer producer;
	@Value("${disruptor.buffer.size}")
	private int bufferSize;

	@Override
	public void start(String name, EventHandler<DisruptorEvent> journalHandler, EventHandler<DisruptorEvent> actionEventHandler)
	{
		this.name = name;
		counter = 0;
		// The factory for the event
		DisruptorEventFactory factory = new DisruptorEventFactory();

		// Construct the Disruptor
		disruptor = new Disruptor<DisruptorEvent>(factory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new BusySpinWaitStrategy());
		logger.info("Created " + name + " disruptor with buffer size: " + bufferSize);

		disruptor.handleEventsWith(journalHandler, actionEventHandler);

		// Start the Disruptor, starts all threads running
		disruptor.start();
		logger.info("Started " + name + " disruptor.");

		// Get the ring buffer from the Disruptor to be used for publishing.
		RingBuffer<DisruptorEvent> ringBuffer = disruptor.getRingBuffer();
		producer = new DisruptorEventProducer(ringBuffer);
		logger.info("Instantiated producer for " + name + " disruptor.");
	}

	@Override
	public void push(DisruptorPayload payLoad)
	{
		producer.onData(payLoad);
		counter++;
	}

	@Override
	public void stop()
	{
		logger.info(counter + " events were processed by " + name + " disruptor");
		disruptor.halt();
		logger.info("Halted " + name + " disruptor");
		disruptor.shutdown();
		logger.info("Shutdown " + name + " disruptor");
	}
}
