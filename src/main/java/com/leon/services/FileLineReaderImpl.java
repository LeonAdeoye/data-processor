package com.leon.services;

import com.leon.disruptors.DisruptorPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Component
public class FileLineReaderImpl implements InputReader
{
	private static final Logger logger = LoggerFactory.getLogger(FileLineReaderImpl.class);
	private String filePath;

	@Override
	public Flux<DisruptorPayload> read()
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
	public void initialize(String filePath, String endOfStream)
	{
		this.filePath = filePath;
	}

	@Override
	public void stop()
	{

	}
}
