package com.leon.services;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ChronicleQueueWriter
{
	private static final Logger logger = LoggerFactory.getLogger(ChronicleQueueWriter.class);
	private Chronicle chronicle;

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

	public void write(String textToWrite) throws IOException
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
