package com.leon.services;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
public class ChronicleQueueWriterImpl implements OutputWriter
{
	private static final Logger logger = LoggerFactory.getLogger(ChronicleQueueWriterImpl.class);
	private Chronicle chronicle;

	@Override
	public void initialize(String chronicleFile)
	{
		try
		{
			File queueDir = Files.createTempDirectory(chronicleFile).toFile();
			chronicle = ChronicleQueueBuilder.indexed(queueDir).build();
		}
		catch(Exception e)
		{
			logger.error("Failed to initialize the chronicle queue writer because of exception: " + e.getMessage());
		}
	}

	@Override
	public void stop()
	{
		try
		{
			chronicle.close();
		}
		catch (IOException ioe)
		{
			logger.error("Failed to close chronicle queue because of exception: " + ioe.getMessage());
		}
	}

	@Override
	public void write(String textToWrite)
	{
		try
		{
			ExcerptAppender appender = chronicle.createAppender();
			appender.startExcerpt();

			appender.writeUTF(textToWrite);
			appender.finish();
		}
		catch(Exception e)
		{
			logger.error("Failed to write to chronicle queue because of exception: " + e.getMessage());
		}
	}
}
