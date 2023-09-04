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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;


@Scope("prototype")
@Service
public class DisruptorServiceImpl implements DisruptorService
{
	private static final Logger logger = LoggerFactory.getLogger(DisruptorServiceImpl.class);
	private String name;
	private long counter = 0;
	private Disruptor<DisruptorEvent> disruptor;
	private DisruptorEventProducer producer;
	private boolean hasStarted;
	private Instant start;

	@Value("${disruptor.buffer.size:4096}")
	private int bufferSize;

	@Value("${shutdown.sleep.duration:5000}")
	private long shutdownSleepDuration;

	@Override
	public void start(String name, EventHandler<DisruptorEvent> journalHandler, EventHandler<DisruptorEvent> actionEventHandler)
	{
		this.name = name;
		counter = 0;
		// The factory for the event
		DisruptorEventFactory factory = new DisruptorEventFactory();

		// Construct the Disruptor
		disruptor = new Disruptor<>(factory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new BusySpinWaitStrategy());
		logger.info("Created {} disruptor with buffer size: {}", name, bufferSize);

		disruptor.handleEventsWith(journalHandler, actionEventHandler);

		// Start the Disruptor, starts all threads running
		disruptor.start();
		logger.info("Started {} disruptor.", name);

		// Get the ring buffer from the Disruptor to be used for publishing.
		RingBuffer<DisruptorEvent> ringBuffer = disruptor.getRingBuffer();
		producer = new DisruptorEventProducer(ringBuffer);
		hasStarted = true;
		logger.info("Instantiated producer for {} disruptor.", name);
	}

	@Scheduled(cron = "*/1 * * * * *")
	public void logTelemetry()
	{
		if(hasStarted)
		{
			RingBuffer<DisruptorEvent> ringBuffer = disruptor.getRingBuffer();
			if(ringBuffer.remainingCapacity() != bufferSize)
			{
				logger.debug("{} Ring buffer's current depth: {}", name, ringBuffer.getCursor() - ringBuffer.getMinimumGatingSequence());
				logger.debug("{} Ring buffer's remaining capacity: {}", name, ringBuffer.remainingCapacity());
			}
		}
	}

	@Override
	public void push(DisruptorPayload payLoad)
	{
		if(counter++ == 0)
			start = Instant.now();

		producer.onData(payLoad);
	}

	@Override
	public void stop()
	{
		if(hasStarted)
		{
			hasStarted = false;
			Instant end = Instant.now();
			logger.info("start:{} end:{}", start, end);
			//logger.info("{} events were processed by {} disruptor. Time taken approximately {} ms.", counter, name, Duration.between(start, end).toMillis() - shutdownSleepDuration);
			disruptor.halt();
			logger.info("Halted {} disruptor", name);
			disruptor.shutdown();
			logger.info("Shutdown {} disruptor", name);
		}
	}
}
