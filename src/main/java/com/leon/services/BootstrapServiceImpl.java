package com.leon.services;

import com.leon.disruptors.DisruptorService;
import com.leon.disruptors.JournalEventHandler;
import com.leon.disruptors.OutputEventHandler;
import com.leon.factory.DataProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class BootstrapServiceImpl implements BootstrapService
{
	private static final Logger logger = LoggerFactory.getLogger(BootstrapServiceImpl.class);
	@Autowired
	private DisruptorService inboundDisruptor;
	@Autowired
	private DisruptorService outboundDisruptor;

	private boolean hasStarted = false;
	private DataProcessingService dataProcessingService;

	public BootstrapServiceImpl()
	{

	}

	@Override
	public void start()
	{
		if(!hasStarted)
		{
			hasStarted = true;
			logger.info("Started bootstrapper");
		}
		else
			logger.error("Bootstrapper has already started.");

	}

	@Override
	public void stop()
	{
		inboundDisruptor.stop();
		outboundDisruptor.stop();
	}

	@Override
	@PostConstruct
	public void initialize()
	{
		inboundDisruptor.start("INBOUND", new JournalEventHandler(), new DataProcessingServiceImpl());
		outboundDisruptor.start("OUTBOUND", new JournalEventHandler(), new OutputEventHandler());
	}
}
