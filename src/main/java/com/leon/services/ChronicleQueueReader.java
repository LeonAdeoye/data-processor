package com.leon.services;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptTailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ChronicleQueueReader
{
	private static final Logger logger = LoggerFactory.getLogger(ChronicleQueueReader.class);
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
			logger.error("Failed to initialize chronicle queue reader because of exception: " + e.getMessage());
		}
	}

	public void read() throws IOException
	{
		try
		{
			ExcerptTailer tailer = chronicle.createTailer();
			while (tailer.nextIndex()) {
				tailer.readUTF();
			}

			tailer.finish();
			tailer.close();
			chronicle.close();
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
	}
}
