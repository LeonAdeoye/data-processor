package com.leon.connectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value="mongodb.output.writer", havingValue = "true")
public class MongoDBWriterImpl implements OutputWriter
{
	@Override
	public void write(String output)
	{

	}

	@Override
	public void initialize(String filePath)
	{

	}

	@Override
	public void stop()
	{

	}
}
