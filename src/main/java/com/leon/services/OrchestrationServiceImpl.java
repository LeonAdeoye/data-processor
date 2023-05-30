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
import org.springframework.stereotype.Service;

@Service
public class OrchestrationServiceImpl implements OrchestrationService
{
	private static final Logger logger = LoggerFactory.getLogger(OrchestrationServiceImpl.class);
	private boolean hasStarted = false;

	@Autowired
	private DisruptorService inboundDisruptor;
	@Autowired
	private DisruptorService outboundDisruptor;

	@Autowired(required = true)
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
						logger.info("Completed processing of input.");
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
	}
}
