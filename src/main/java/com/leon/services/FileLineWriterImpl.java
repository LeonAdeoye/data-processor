package com.leon.services;

import com.leon.disruptors.DisruptorEvent;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;

public class FileLineWriterImpl implements FileWriter, EventHandler<DisruptorEvent>
{
	private static final Logger logger = LoggerFactory.getLogger(FileLineWriterImpl.class);
	@Value("${file.line.writer.filepath}")
	private String filepath;

	@PostConstruct
	@Override
	public void initialize()
	{
		// Open output file
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
