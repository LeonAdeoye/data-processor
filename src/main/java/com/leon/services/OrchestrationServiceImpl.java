package com.leon.services;

import com.leon.App;
import com.leon.disruptors.DisruptorService;
import com.leon.handlers.DataProcessingEventHandler;
import com.leon.handlers.JournalEventHandler;
import com.leon.handlers.OutputEventHandler;
import com.leon.connectors.InputReader;
import com.leon.connectors.OutputWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;
import static java.lang.Thread.sleep;

@Service
public class OrchestrationServiceImpl implements OrchestrationService
{
	private static final Logger logger = LoggerFactory.getLogger(OrchestrationServiceImpl.class);
	private boolean hasStarted = false;

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

	@Override
	public void start()
	{
		if(!hasStarted)
		{
			logger.info("Starting bootstrapping process...");
			hasStarted = true;
			dataProcessingEventHandler.setOutboundDisruptor(outboundDisruptor);
			outboundDisruptor.start("OUTBOUND", new JournalEventHandler(), new OutputEventHandler(outputWriter));
			inboundDisruptor.start("INBOUND", new JournalEventHandler(), dataProcessingEventHandler);

			inputReader.read().subscribe(
					inboundDisruptor::push,
					err ->
					{
						logger.error(err.getMessage());
					},
					() ->
					{
						logger.info("Completed processing of input. Shutting down all processing components in 10 seconds...");
						try
						{
							sleep(10000);
						}
						catch(InterruptedException ie)
						{
							logger.error("Interrupted exception thrown while sleeping after completion of data processing.");
						}
						finally
						{
							this.stop();
						}
					});
		}
		else
			logger.error("Bootstrapper has already started.");
	}

	@Override
	public void stop()
	{
		inputReader.stop();
		outputWriter.stop();
		inboundDisruptor.stop();
		outboundDisruptor.stop();
		int exitCode = SpringApplication.exit(App.context, () -> 0);
		System.exit(exitCode);
	}
}
