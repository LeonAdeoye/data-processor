package com.leon.readers;

import com.leon.disruptors.DisruptorPayload;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.RollCycles;
import net.openhft.chronicle.wire.ReadMarshallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(value="chronicle.queue.input.reader", havingValue = "true")
public class ChronicleQueueReaderImpl implements InputReader
{
	private static final Logger logger = LoggerFactory.getLogger(ChronicleQueueReaderImpl.class);
	private ChronicleQueue queue;
	private ExcerptTailer tailer;
	private boolean completed = false;

	@Value("${input.reader.file.path}")
	private String chronicleFile;

	@Value("${input.reader.end.indicator}")
	private String endIndicator;

	@PostConstruct
	public void initialize()
	{
		try
		{
			this.queue = ChronicleQueue.singleBuilder(chronicleFile).rollCycle(RollCycles.DAILY).build();
			this.tailer = queue.createTailer();
		}
		catch(Exception e)
		{
			logger.error("Failed to initialize chronicle queue tailer because of exception: {}", e.getMessage());
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

				ReadMarshallable marshallable = wire ->
				{
					String message = wire.read().text();

					if(endIndicator.equals(message))
					{
						logger.info("End indicator received. Reading of chronicle queue completed.");
						this.completed = true;
						emitter.complete();
					}

					emitter.next(new DisruptorPayload(message));
				};

				while (!completed)
				{
					tailer.readDocument(marshallable);
				}
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
