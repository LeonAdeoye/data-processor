package com.leon.services;

import com.leon.disruptors.DisruptorService;
import com.leon.handlers.DataProcessingEventHandler;
import com.leon.handlers.JournalEventHandler;
import com.leon.handlers.OutputEventHandler;
import com.leon.connectors.InputReader;
import com.leon.connectors.OutputWriter;
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
	ApplicationContext applicationContext;

	@Value("${shutdown.sleep.duration}")
	private long shutdownSleepDuration;

	@Override
	public void start()
	{
		logger.info("Starting bootstrapping process...");
		dataProcessingEventHandler.setOutboundDisruptor(outboundDisruptor);
		outboundDisruptor.start("OUTBOUND", new JournalEventHandler(), new OutputEventHandler(outputWriter));
		inboundDisruptor.start("INBOUND", new JournalEventHandler(), dataProcessingEventHandler);

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
