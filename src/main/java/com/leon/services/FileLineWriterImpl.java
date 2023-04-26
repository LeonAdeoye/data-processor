package com.leon.services;

import com.leon.disruptors.DisruptorEvent;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class FileLineWriterImpl implements OutputWriter, EventHandler<DisruptorEvent>
{
	private static final Logger logger = LoggerFactory.getLogger(FileLineWriterImpl.class);
	@Value("${output.writer.file.path}")
	private String filePath;

	@PostConstruct
	@Override
	public void initialize()
	{
		logger.info("Opening file: " + filePath + " for writing.");
	}

	@Override
	public void write(String output)
	{

	}

	@Override
	public void onEvent(DisruptorEvent event, long sequence, boolean enfOfBatch)
	{
		logger.info(event.getPayload().toString());
		write(event.getPayload().toString());
	}
	@Override
	public void shutdown()
	{

	}
}
