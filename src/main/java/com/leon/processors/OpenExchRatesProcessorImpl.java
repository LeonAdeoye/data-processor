package com.leon.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.text.DecimalFormat;

@Component
@ConditionalOnProperty(value="open.exchange.rates.processing", matchIfMissing = false)
public class OpenExchRatesProcessorImpl implements Processor
{
	private static final Logger logger = LoggerFactory.getLogger(OpenExchRatesProcessorImpl.class);
	private static final DecimalFormat decimalFormat = new DecimalFormat("0.000");
	private static final String erroneousResponse = "{\"type\": \"error\", \"source\": \"openexchangerates.org\"}";

	@Value("${open.exchange.rates.processing}")
	private int processingOrder;

	@Override
	public int getProcessingOrder()
	{
		return this.processingOrder;
	}

	@Override
	public String process(String payload)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectMapper resultMapper = new ObjectMapper();
		ObjectNode resultNode = resultMapper.createObjectNode();

		try
		{
			resultNode.put("type", "rates");
			resultNode.put("source", "openexchangerates.org");

			JsonNode rootNode = objectMapper.readTree(payload);

			if (rootNode.has("rates"))
			{
				JsonNode ratesNode = rootNode.get("rates");
				ArrayNode ratesArray = objectMapper.createArrayNode();

				ratesNode.fields().forEachRemaining(entry ->
				{
					ObjectNode currencyNode = objectMapper.createObjectNode();
					currencyNode.put("currency", entry.getKey());
					currencyNode.put("rate", decimalFormat.format(entry.getValue().asDouble()));
					ratesArray.add(currencyNode);
				});

				resultNode.set("rates", ratesArray);
				return objectMapper.writeValueAsString(resultNode);
			}
		}
		catch (JsonParseException | JsonMappingException e)
		{
			logger.error("Error: {} parsing JSON: {}", e.getMessage(), payload);
		}
		catch (JsonProcessingException e)
		{
			logger.error("Error: {} processing JSON: {}", e.getMessage(), payload);
		}
		catch (IOException e)
		{
			logger.error("Error {} reading JSON: {}", e.getMessage(), payload);
		}

		return erroneousResponse;
	}
}

