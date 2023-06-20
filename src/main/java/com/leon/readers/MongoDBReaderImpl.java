package com.leon.readers;

import com.leon.disruptors.DisruptorPayload;
import com.mongodb.client.*;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(value="mongodb.input.reader", havingValue = "true")
public class MongoDBReaderImpl implements InputReader
{
	private static final Logger logger = LoggerFactory.getLogger(MongoDBReaderImpl.class);

	@Value("${mongodb.reader.collection.name}")
	private String collectionName;
	@Value("${mongodb.reader.database.name}")
	private String databaseName;
	@Value("${mongodb.reader.connection.uri}")
	private String connectionURI;
	@Value("${mongodb.reader.find.filter}")
	private String filter;

	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection<Document> collection;
	private int counter = 0;

	@PostConstruct
	public void initialize()
	{
		try
		{
			logger.info("Initializing MongoDB connection to database {} and collection {} for .", databaseName, collectionName);
			client = MongoClients.create(connectionURI);
			database = client.getDatabase( databaseName);
			collection = database.getCollection(collectionName);
		}
		catch (Exception e)
		{
			logger.error("Exception thrown while initializing connection to Mongo DB: {} for reading.", e.getMessage());
		}
	}

	@Override
	public Flux<DisruptorPayload> read()
	{
		logger.info("Reading MongoDB records from collection {} and database: {}", collectionName, databaseName);
		return Flux.create(emitter ->
		{
			try
			{
				FindIterable<Document> documents = filter.isEmpty() ? collection.find() : collection.find(Document.parse(filter));
				documents.forEach((Document document) ->
				{
					counter++;
					emitter.next(new DisruptorPayload(document.toJson()));
				});
				emitter.complete();
			}
			catch(Exception e)
			{
				emitter.error(e);
				logger.error(e.getMessage());
			}
		});
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
