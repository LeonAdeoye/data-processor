package com.leon.writers;

import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(value="chronicle.queue.output.writer", havingValue = "true")
public class ChronicleQueueWriterImpl implements OutputWriter
{
	private static final Logger logger = LoggerFactory.getLogger(ChronicleQueueWriterImpl.class);
	private ChronicleQueue queue;
	private ExcerptAppender appender;

	@Value("${output.writer.file.path}")
	private String chronicleFile;

	@PostConstruct
	public void initialize(String chronicleFile)
	{
		try
		{
			queue = ChronicleQueue.singleBuilder(chronicleFile).build();
			appender = queue.acquireAppender();
		}
		catch(Exception e)
		{
			logger.error("Failed to initialize the chronicle queue writer because of exception: {}", e.getMessage());
		}
	}

	@Override
	public void stop()
	{
		try
		{
			queue.close();
		}
		catch (Exception e)
		{
			logger.error("Failed to close chronicle queue because of exception: {}", e.getMessage());
		}
	}

	@Override
	public void write(String textToWrite)
	{
		try
		{
			appender.wire().write().text(textToWrite);
		}
		catch(Exception e)
		{
			logger.error("Failed to write to chronicle queue because of exception: {}", e.getMessage());
		}
	}
}
