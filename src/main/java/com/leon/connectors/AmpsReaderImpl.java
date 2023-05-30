package com.leon.connectors;

import com.leon.disruptors.DisruptorPayload;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@ConditionalOnProperty(value="amps.input.reader", havingValue = "true")
public class AmpsReaderImpl implements InputReader
{
	@Override
	public void initialize(String filePath, String endOfStream)
	{

	}

	@Override
	public Flux<DisruptorPayload> read()
	{
		return null;
	}

	@Override
	public void stop()
	{

	}
}
