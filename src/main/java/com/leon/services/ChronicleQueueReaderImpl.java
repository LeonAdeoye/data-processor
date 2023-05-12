package com.leon.services;

import com.leon.disruptors.DisruptorPayload;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptTailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class ChronicleQueueReaderImpl implements InputReader
{
	private static final Logger logger = LoggerFactory.getLogger(ChronicleQueueReaderImpl.class);
	private Chronicle chronicle;

	@Override
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

	@Override
	public Flux<DisruptorPayload> read()
	{
		try
		{
			logger.info("Reading chronicle queue and creating a Flux...");
			ExcerptTailer tailer = chronicle.createTailer();
			return Flux.create(emitter ->
			{
				while (tailer.nextIndex())
				{
					emitter.next(new DisruptorPayload(tailer.readUTF()));
					// TODO
					logger.info("chronicle queue data: " + tailer.readUTF());
				}
				emitter.complete();
				tailer.finish();
				tailer.close();
			});
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
		return Flux.empty();
	}

	@Override
	public void shutdown()
	{
		try
		{
			chronicle.close();
		}
		catch(IOException ioe)
		{
			logger.error(ioe.getMessage());
		}
	}
}
