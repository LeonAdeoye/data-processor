package com.leon.connectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(value="kdb.output.writer", havingValue = "true")
public class KdbWriterImpl implements OutputWriter
{
	@Override
	public void write(String output)
	{

	}

	@PostConstruct
	public void initialize()
	{

	}

	@Override
	public void stop()
	{

	}
}