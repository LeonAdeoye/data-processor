package com.leon.writers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(value="kdb.output.writer", havingValue = "true")
public class KdbWriterImpl implements OutputWriter
{
	// declare kdb connection member variable

	@Override
	public void write(String output)
	{

	}

	@PostConstruct
	public void initialize()
	{
		// initialize kdb connection

	}

	@Override
	public void stop()
	{
		// close kdb connection

	}
}
