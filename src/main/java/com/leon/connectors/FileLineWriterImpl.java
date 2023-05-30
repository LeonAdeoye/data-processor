package com.leon.connectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
@ConditionalOnProperty(value="file.output.writer", havingValue = "true")
public class FileLineWriterImpl implements OutputWriter
{
	private static final Logger logger = LoggerFactory.getLogger(FileLineWriterImpl.class);
	private BufferedWriter writer;

	@Value("${output.writer.file.path}")
	private String filePath;

	@Value("${output.writer.delimiter}")
	private String delimiter;

	private int count = 0;

	@PostConstruct
	public void initialize()
	{
		try
		{
			logger.info("Opening file: " + filePath + " for writing.");
			writer = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8, StandardOpenOption.CREATE,
					StandardOpenOption.WRITE, StandardOpenOption.APPEND);
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
			if(++count > 1 && !delimiter.isEmpty())
				writer.write(delimiter);

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
