package com.leon.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Component
public class FileLineWriterImpl implements OutputWriter
{
	private static final Logger logger = LoggerFactory.getLogger(FileLineWriterImpl.class);
	private BufferedWriter writer;

	@Override
	public void initialize(String filePath)
	{
		try
		{
			logger.info("Opening file: " + filePath + " for writing.");
			writer = Files.newBufferedWriter(Paths.get(filePath));
		}
		catch(IOException ioe)
		{
			logger.error(ioe.getMessage());
			stop();
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
	public void stop()
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
