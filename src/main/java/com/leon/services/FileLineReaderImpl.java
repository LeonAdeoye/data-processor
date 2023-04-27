package com.leon.services;

import com.leon.disruptors.DisruptorPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FileLineReaderImpl implements InputReader
{
	private static final Logger logger = LoggerFactory.getLogger(FileLineReaderImpl.class);

	@Value("${input.reader.file.path}")
	private String filePath;

	@Value("${input.reader.payload.type}")
	private Optional<String> payloadType;


	private List<DisruptorPayload> readLinesFromFile() throws IOException
	{
		List<DisruptorPayload> payloads = new ArrayList<>();
		try (Stream<String> linesStream = Files.lines(Paths.get(filePath)))
		{
			linesStream.forEach(line -> payloads.add(new DisruptorPayload(payloadType.orElse(filePath), line)));
		}
		return payloads;
	}


	@Override
	public Flux<DisruptorPayload> read()
	{
		logger.info("Reading file: " + filePath + " and returning a Flux...");
		try
		{
			return Flux.fromIterable(readLinesFromFile());
		}
		catch(IOException ioe)
		{
			logger.error(ioe.getMessage());
		}
		return Flux.empty();
	}

	@Override
	public void shutdown()
	{

	}

	@PostConstruct
	@Override
	public void initialize()
	{
	}
}
