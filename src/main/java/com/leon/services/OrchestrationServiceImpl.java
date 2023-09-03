package com.leon.services;

import com.leon.disruptors.DisruptorEvent;
import com.leon.disruptors.DisruptorService;
import com.leon.handlers.*;
import com.leon.readers.InputReader;
import com.leon.writers.OutputWriter;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import static java.lang.Thread.sleep;

@Service
public class OrchestrationServiceImpl implements OrchestrationService
{
	private static final Logger logger = LoggerFactory.getLogger(OrchestrationServiceImpl.class);
	@Autowired
	private DisruptorService inboundDisruptor;
	@Autowired
	private DisruptorService outboundDisruptor;
	@Autowired
	private InputReader inputReader;
	@Autowired
	private OutputWriter outputWriter;
	@Autowired
	private DataProcessingEventHandler dataProcessingEventHandler;
	@Autowired
	OutputEventHandler outputEventHandler;
	@Autowired
	private ApplicationContext applicationContext;

	@Value("${shutdown.sleep.duration}")
	private long shutdownSleepDuration;

	@Override
	public void start()
	{
		logger.info("Starting bootstrapping process...");
		dataProcessingEventHandler.setOutboundDisruptor(outboundDisruptor);
		inboundDisruptor.start("INBOUND", new JournalEventHandler(), dataProcessingEventHandler);
		outboundDisruptor.start("OUTBOUND", new JournalEventHandler(), outputEventHandler);

		inputReader.read().subscribe(
			inboundDisruptor::push,
			err -> logger.error(err.getMessage()),
			this::stop);
	}

	@Override
	public void stop()
	{
		try
		{
			logger.info("Completed processing. Shutting down all components in {} ms.", shutdownSleepDuration);
			sleep(shutdownSleepDuration);
		}
		catch(InterruptedException ie)
		{
			logger.error("Interrupted exception thrown while sleeping after completion of data processing.");
			Thread.currentThread().interrupt();
		}
		finally
		{
			inputReader.stop();
			outputWriter.stop();
			inboundDisruptor.stop();
			outboundDisruptor.stop();
			int exitCode = SpringApplication.exit(applicationContext, () -> 0);
			System.exit(exitCode);
		}
	}
}
