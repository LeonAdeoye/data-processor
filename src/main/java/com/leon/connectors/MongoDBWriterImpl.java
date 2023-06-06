package com.leon.connectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(value="mongodb.output.writer", havingValue = "true")
public class MongoDBWriterImpl implements OutputWriter
{
	private static final Logger logger = LoggerFactory.getLogger(MongoDBWriterImpl.class);
	@Autowired
	private MongoDBClientConfig mongoDBClientConfig;

	@Override
	public void write(String output)
	{
		mongoDBClientConfig.mongoTemplate().insert(output);
	}

	@Override
	public void stop()
	{
		logger.info("Closing MongoDB connection...");
	}
}
