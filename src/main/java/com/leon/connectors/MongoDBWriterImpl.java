package com.leon.connectors;

import com.leon.services.JsonValidator;
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
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(value="mongodb.output.writer", havingValue = "true")
public class MongoDBWriterImpl implements OutputWriter
{
	private static final Logger logger = LoggerFactory.getLogger(MongoDBWriterImpl.class);

	@Value("${mongodb.writer.collection.name}")
	private String collectionName;
	@Value("${mongodb.writer.database.name}")
	private String databaseName;
	@Value("${mongodb.writer.connection.uri}")
	private String connectionURI;
	@Value("${check.json.validity:false}")
	private boolean checkJsonValidity;
	@Value("${mongodb.writer.batch.size:1}")
	private int batchSize;

	private List<Document> batch;
	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection<Document> collection;
	private int counter = 0;
	private int batchCounter = 0;

	@PostConstruct
	public void initialize()
	{
		try
		{
			logger.info("Initializing MongoDB connection to database {} and collection {}", databaseName, collectionName);
			client = MongoClients.create(connectionURI);
			database = client.getDatabase( databaseName);
			collection = database.getCollection(collectionName);
			batch = new ArrayList<>(batchSize);
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
			// Either checkJsonValidity is false or checkJsonValidity is true and output is also valid JSON.
			if(!checkJsonValidity || (checkJsonValidity && JsonValidator.isValid(output)))
			{
				if(batchSize > 1)
				{
					batch.add(Document.parse(output));
					if(++batchCounter == batchSize)
					{
						logger.info("Writing batch of {} documents to MongoDB", batchCounter);
						collection.insertMany(batch);
						counter += batchCounter;
						batch.clear();
						batchCounter = 0;
					}
				}
				else
				{
					collection.insertOne(Document.parse(output));
					counter++;
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Exception thrown while writing to Mongo DB: {}", e.getMessage());
		}
	}

	@Override
	public void stop()
	{
		if(batchCounter > 0)
		{
			logger.info("Before closing the client connection, writing remaining batch of {} documents to MongoDB", batchCounter);
			collection.insertMany(batch);
			counter += batchCounter;
			batch.clear();
			batchCounter = 0;
		}

		logger.info("Closing MongoDB connection to database {} and collection {} after writing {} documents", databaseName, collectionName, counter);
		client.close();
	}
}
