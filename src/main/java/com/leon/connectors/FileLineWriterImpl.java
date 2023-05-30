package com.leon.connectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@ConditionalOnProperty(value="file.output.writer", havingValue = "true")
public class FileLineWriterImpl implements OutputWriter
{
	private static final Logger logger = LoggerFactory.getLogger(FileLineWriterImpl.class);
	private BufferedWriter writer;

	@Value("${output.writer.file.path}")
	private String filePath;

	@PostConstruct
	public void initialize()
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
