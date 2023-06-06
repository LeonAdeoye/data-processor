package com.leon.connectors;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@ConditionalOnProperty(value="mongodb.output.writer", havingValue = "true")
public class MongoDBClientConfig
{
	@Value("${mongodb.connection.uri}")
	private String connectionUri;

	@Bean
	public MongoClient mongo()
	{
		ConnectionString connectionString = new ConnectionString(connectionUri);
		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.build();

		return MongoClients.create(mongoClientSettings);
	}

	@Value("${mongodb.database.name}")
	private String databaseName;

	@Bean
	public MongoTemplate mongoTemplate()
	{
		return new MongoTemplate(mongo(), databaseName);
	}
}
