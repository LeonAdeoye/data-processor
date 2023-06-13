package com.leon.connectors;

import com.leon.disruptors.DisruptorPayload;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;

public class MongoDBReaderImpl implements InputReader
{
	private static final Logger logger = LoggerFactory.getLogger(MongoDBReaderImpl.class);

	@Value("${mongodb.reader.collection.name}")
	private String collectionName;
	@Value("${mongodb.reader.database.name}")
	private String databaseName;
	@Value("${mongodb.reader.connection.uri}")
	private String connectionURI;

	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection<Document> collection;
	private int counter = 0;

	@PostConstruct
	public void initialize()
	{
		try
		{
			logger.info("Initializing MongoDB connection to database {} and collection {}", databaseName, collectionName);
			client = MongoClients.create(connectionURI);
			database = client.getDatabase( databaseName);
			collection = database.getCollection(collectionName);
		}
		catch (Exception e)
		{
			logger.error("Exception thrown while initializing connection to Mongo DB: {}", e.getMessage());
		}
	}

	@Override
	public Flux<DisruptorPayload> read()
	{
		return null;
	}

	@Override
	public void stop()
	{
		try
		{
			logger.info("Closing MongoDB connection to database {} and collection {} after reading {} documents.", databaseName, collectionName, counter);
			client.close();
		}
		catch (Exception e)
		{
			logger.error("Exception thrown while closing connection to Mongo DB: {}.", e.getMessage());
		}
	}
}
