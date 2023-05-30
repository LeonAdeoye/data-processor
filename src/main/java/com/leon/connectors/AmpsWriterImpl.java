package com.leon.connectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value="amps.output.writer", havingValue = "true")
public class AmpsWriterImpl implements OutputWriter
{
	@Override
	public void write(String output)
	{

	}

	@Override
	public void stop()
	{

	}
}
