package com.leon.readers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.disruptors.DisruptorPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@ConditionalOnProperty(value="http.input.reader", havingValue = "true")
public class HttpReaderImpl implements InputReader
{
	private static final Logger logger = LoggerFactory.getLogger(HttpReaderImpl.class);
	@Value("${input.reader.http.url}")
	private String url = "";
	@Value("${input.reader.http.root.node}")
	private String rootNodeProperty = "";
	@Value("${input.reader.http.method:GET}")
	private String httpMethod;
	private HttpURLConnection connection;
	private final DirectProcessor<DisruptorPayload> dataProcessor = DirectProcessor.create();
	private final FluxSink<DisruptorPayload> dataSink = dataProcessor.sink();

	@Scheduled(fixedDelayString = "${input.reader.http.interval}")
	public void fetchAndProcessData()
	{
		Flux<DisruptorPayload> dataFlux = readData();
		dataFlux.subscribe(payload -> dataSink.next(payload));
	}

	public Flux<DisruptorPayload> readData()
	{
		return Flux.create(emitter ->
		{
			try
			{
				URL url = new URL(this.url);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod(httpMethod);

				try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
				{
					StringBuilder jsonContent = new StringBuilder();
					String line;

					while ((line = reader.readLine()) != null)
					{
						jsonContent.append(line);
					}

					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode rootNode = objectMapper.readTree(jsonContent.toString());
					JsonNode nodeToExtract = rootNode.get(this.rootNodeProperty);
					String dataToProcess = nodeToExtract.toString();
					emitter.next(new DisruptorPayload(dataToProcess));
				}

				connection.disconnect();
			}
			catch (IOException ioe)
			{
				logger.error("Exception thrown while reading from HTTP client: {}.", ioe.getMessage());
				emitter.error(ioe);
			}
		});
	}


	@Override
	public Flux<DisruptorPayload> read()
	{
		return dataProcessor;
	}

	@Override
	public void stop()
	{
	}
}
