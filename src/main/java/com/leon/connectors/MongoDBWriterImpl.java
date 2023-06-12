package com.leon.connectors;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(value="mongodb.output.writer", havingValue = "true")
public class MongoDBWriterImpl implements OutputWriter
{
	private static final Logger logger = LoggerFactory.getLogger(MongoDBWriterImpl.class);

	@Value("${mongodb.collection.name}")
	private String collectionName;
	@Value("${mongodb.database.name}")
	private String databaseName;
	@Value("${mongodb.connection.uri}")
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
	public void write(String output)
	{
		try
		{
			collection.insertOne(Document.parse(output));
			counter++;
		}
		catch (Exception e)
		{
			logger.error("Exception thrown while writing to Mongo DB: {}", e.getMessage());
		}
	}

	@Override
	public void stop()
	{
		logger.info("Closing MongoDB connection after writing {} documents", counter);
		client.close();
	}
}
