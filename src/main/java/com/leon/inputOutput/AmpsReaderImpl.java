package com.leon.inputOutput;

import com.leon.disruptors.DisruptorPayload;
import reactor.core.publisher.Flux;

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
