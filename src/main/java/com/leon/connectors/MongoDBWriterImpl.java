package com.leon.connectors;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(value="mongodb.output.writer", havingValue = "true")
public class MongoDBWriterImpl implements OutputWriter
{
	private static final Logger logger = LoggerFactory.getLogger(MongoDBWriterImpl.class);
	private MongoClient mongoClient;

	@Override
	public void write(String output)
	{
		//mongoClient.write(output);
	}

	@Override
	public void stop()
	{
		mongoClient.close();

	}

	@PostConstruct
	public void initialize()
	{
		// Initialize connection to MongoDB
		try
		{
			mongoClient = MongoClients.create("mongodb://localhost:27017");
			MongoDatabase database = mongoClient.getDatabase("test");
			MongoCollection<org.bson.Document> collection = database.getCollection("test");
			/*collection.insertMany(new ArrayList<>() {{
				add(new org.bson.Document("name", "MongoDB")
						.append("type", "database")
						.append("count", 1)
						.append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
						.append("info", new org.bson.Document("x", 203).append("y", 102)));
			}});*/

		}
		catch(Exception exception)
		{
			logger.error(exception.getMessage());
		}
	}
}
