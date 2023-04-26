package com.leon.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
public class FileLineReaderImpl implements InputReader
{
	private static final Logger logger = LoggerFactory.getLogger(FileLineReaderImpl.class);

	@Value("${input.reader.file.path}")
	private String filePath;

	private void readLinesFromFile(Consumer<String> consumer) throws IOException
	{
		try (Stream<String> linesStream = Files.lines(Paths.get(filePath)))
		{
			linesStream.forEach(line -> consumer.accept(line));
		}
	}


	@Override
	public void read()
	{
		logger.info("Reading file: " + filePath);
		try
		{
			readLinesFromFile(logger::info);
		}
		catch(IOException ioe)
		{
			logger.error(ioe.getMessage());
		}
	}

	@Override
	public void shutdown()
	{

	}

	@PostConstruct
	@Override
	public void initialize()
	{
		logger.info("Opening file: " + filePath + " for reading.");
		read();
	}
}
