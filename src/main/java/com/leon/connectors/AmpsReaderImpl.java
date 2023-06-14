package com.leon.connectors;

import com.crankuptheamps.client.Client;
import com.crankuptheamps.client.Message;
import com.crankuptheamps.client.exception.AMPSException;
import com.crankuptheamps.client.exception.ConnectionException;
import com.leon.disruptors.DisruptorPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;
import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(value="amps.input.reader", havingValue = "true")
public class AmpsReaderImpl implements InputReader
{
	private static final Logger logger = LoggerFactory.getLogger(AmpsReaderImpl.class);
	public final ReplayProcessor<DisruptorPayload> processor = ReplayProcessor.create();
	private final FluxSink<DisruptorPayload> sink = processor.sink();
	private Client amps = null;

	@Value("${input.reader.end.indicator}")
	private String endIndicator;
	@Value("${input.reader.amps.name}")
	private String name;
	@Value("${input.reader.amps.connection.string}")
	private String connectionString;
	@Value("${input.reader.amps.topic}")
	private String topic;
	@Value("${input.reader.amps.filter}")
	private String filter;

	@PostConstruct
	public void initialize()
	{
		try
		{
			logger.info("Instantiating client {} and initializing AMPS connection: {}", name, connectionString);
			amps = new Client(name);
			amps.connect(connectionString);
			amps.logon();
		}
		catch (AMPSException e)
		{
			logger.error(e.getMessage());
		}
	}

	@Override
	public Flux<DisruptorPayload> read()
	{
		try
		{
			for(Message message : (filter.isEmpty()) ? amps.subscribe(topic) : amps.subscribe(topic, filter))
			{
				if(message.getData().equals(endIndicator))
					sink.complete();
				else
					sink.next(new DisruptorPayload(message.getData()));
			}
		}
		catch (ConnectionException ce)
		{
			logger.error(ce.getMessage());
			sink.error(ce);
		}
		catch (AMPSException ae)
		{
			logger.error(ae.getMessage());
			sink.error(ae);
		}

		return processor;
	}

	@Override
	public void stop()
	{
		amps.disconnect();
		amps.close();
	}
}
