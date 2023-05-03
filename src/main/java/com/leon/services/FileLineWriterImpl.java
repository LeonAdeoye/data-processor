package com.leon.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileLineWriterImpl implements OutputWriter
{
	private static final Logger logger = LoggerFactory.getLogger(FileLineWriterImpl.class);

	private Path path;
	private BufferedWriter writer;

	@Override
	public void initialize(String filePath)
	{
		try
		{
			logger.info("Opening file: " + filePath + " for writing.");
			path = Paths.get(filePath);
			writer = Files.newBufferedWriter(path);
		}
		catch(IOException ioe)
		{
			logger.error(ioe.getMessage());
			shutdown();
		}
	}

	@Override
	public void write(String output)
	{
		try
		{
			logger.info("Writing output: " + output);
			writer.write(output);
		}
		catch(IOException io)
		{
			logger.error(io.getMessage());
		}
	}

	@Override
	public void shutdown()
	{
		if(writer != null)
		{
			try
			{
				writer.close();
			}
			catch(IOException io)
			{
				logger.error(io.getMessage());
			}
		}
	}
}
