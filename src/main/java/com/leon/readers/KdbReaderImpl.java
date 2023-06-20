package com.leon.readers;

import com.leon.disruptors.DisruptorPayload;
import reactor.core.publisher.Flux;

public class KdbReaderImpl implements InputReader
{
	@Override
	public Flux<DisruptorPayload> read()
	{
		return Flux.empty();
	}

	@Override
	public void stop()
	{

	}
}
