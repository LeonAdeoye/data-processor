package com.leon.services;

import com.leon.disruptors.DisruptorEvent;
import com.leon.factory.DataProcessingService;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DataProcessingServiceImpl implements DataProcessingService, EventHandler<DisruptorEvent>
{
	private static final Logger logger = LoggerFactory.getLogger(DataProcessingServiceImpl.class);

	public DataProcessingServiceImpl()
	{

	}

	@Override
	public void process()
	{

	}

	@Override
	public void onEvent(DisruptorEvent disruptorEvent, long sequence, boolean endOfBatch)
	{
		logger.info(disruptorEvent.getPayload().toString());
		// OutputWriter
	}
}
