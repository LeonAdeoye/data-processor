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
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FileLineReaderImpl implements InputReader
{
	private static final Logger logger = LoggerFactory.getLogger(FileLineReaderImpl.class);

	private long count;

	@Value("${input.reader.file.path}")
	private String filePath;

	@Value("${input.reader.payload.type:#{null}}")
	private Optional<String> payloadType;

	@Override
	public Flux<DisruptorPayload> readLines()
	{
		logger.info("Reading file: " + filePath + " and creating a Flux...");
		return Flux.create(emitter ->
		{
			try (Stream<String> linesStream = Files.lines(Paths.get(filePath)))
			{
				linesStream.map(DisruptorPayload::new).forEach(emitter::next);
				emitter.complete();
			}
			catch(IOException ioe)
			{
				emitter.error(ioe);
				logger.error(ioe.getMessage());
			}
		});
	}

	@Override
	public Flux<DisruptorPayload> readLines(String filePath)
	{
		logger.info("Reading file: " + filePath + " and creating a Flux...");
		return Flux.create(emitter ->
		{
			try (Stream<String> linesStream = Files.lines(Paths.get(filePath)))
			{
				linesStream.map(DisruptorPayload::new).forEach(emitter::next);
				emitter.complete();
			}
			catch(IOException ioe)
			{
				emitter.error(ioe);
				logger.error(ioe.getMessage());
			}
		});
	}

	@PostConstruct
	public void initialize()
	{
	}

	@Override
	public void shutdown()
	{

	}

	@Override
	public long getLinesRead()
	{
		return count;
	}
}
