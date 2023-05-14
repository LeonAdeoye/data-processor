package com.leon.services;

import com.leon.disruptors.DisruptorPayload;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.wire.ReadMarshallable;
import net.openhft.chronicle.wire.WireIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class ChronicleQueueReaderImpl implements InputReader
{
	private static final Logger logger = LoggerFactory.getLogger(ChronicleQueueReaderImpl.class);
	private ChronicleQueue queue;
	private ExcerptTailer tailer;
	private boolean completed = false;

	@Override
	public void initialize(String chronicleFile)
	{
		try
		{
			queue = ChronicleQueue.singleBuilder(chronicleFile).build();
			tailer = queue.createTailer();
		}
		catch(Exception e)
		{
			logger.error("Failed to initialize chronicle queue tailer because of exception: " + e.getMessage());
		}
	}

	@Override
	public Flux<DisruptorPayload> read()
	{
		logger.info("Reading chronicle queue and creating a Flux...");
		try
		{
			return Flux.create(emitter ->
			{
				ReadMarshallable marshallable = new ReadMarshallable() {
					@Override
					public void readMarshallable(WireIn wire) throws IORuntimeException
					{
						emitter.next(new DisruptorPayload(wire.read().text()));
					}
				};

				while (!completed)
				{
					tailer.readDocument(marshallable);
				}

				emitter.complete();
			});
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
		return Flux.empty();
	}

	@Override
	public void stop()
	{
		try
		{
			completed = true;
			queue.close();
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
	}
}
