package com.leon.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.text.DecimalFormat;

@Component
@ConditionalOnProperty(value="kraken.price.processing", matchIfMissing = false)
public class KrakenPriceProcessorImpl implements Processor
{
	private static final Logger logger = LoggerFactory.getLogger(KrakenPriceProcessorImpl.class);
	private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
	private static final String erroneousResponse = "{\"type\": \"error\", \"source\": \"kraken.com\"}";

	@Value("${kraken.price.processing}")
	private int processingOrder;

	@Override
	public int getProcessingOrder()
	{
		return this.processingOrder;
	}

	@Override
	public String process(String payload)
	{
		if(payload.isEmpty())
			return "";

		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonPayload = objectMapper.readTree(payload);

			if(!jsonPayload.has(1))
				return erroneousResponse;

			JsonNode prices = jsonPayload.get(1);
			StringBuilder builder = new StringBuilder("{\"type\": \"price\", \"source\": \"kraken.com\"");

			if(prices.has("a"))
			{
				JsonNode askArray = prices.get("a");
				builder.append(", \"best_ask\": ").append(decimalFormat.format(askArray.get(0).asDouble()));
			}

			if(prices.has("b"))
			{
				JsonNode bidArray = prices.get("b");
				builder.append(", \"best_bid\": ").append(decimalFormat.format(bidArray.get(0).asDouble()));
			}

			if(prices.has("c"))
			{
				JsonNode closeArray = prices.get("c");
				builder.append(", \"close\": ").append(decimalFormat.format(closeArray.get(0).asDouble()));
			}

			if(prices.has("h"))
			{
				JsonNode highArray = prices.get("h");
				builder.append(", \"high\": ").append(decimalFormat.format(highArray.get(0).asDouble()));
			}

			if(prices.has("l"))
			{
				JsonNode lowArray = prices.get("l");
				builder.append(", \"low\": ").append(decimalFormat.format(lowArray.get(0).asDouble()));
			}

			if(prices.has("o"))
			{
				JsonNode openArray = prices.get("o");
				builder.append(", \"open\": ").append(decimalFormat.format(openArray.get(0).asDouble()));
			}

			if(prices.has("v"))
			{
				JsonNode volumeArray = prices.get("v");
				builder.append(", \"vol_today\": ").append(decimalFormat.format(volumeArray.get(0).asDouble()));
				builder.append(", \"vol_24h\": ").append(decimalFormat.format(volumeArray.get(1).asDouble()));
			}

			if(prices.has("p"))
			{
				JsonNode volumeWeightedAveragePriceArray = prices.get("p");
				builder.append(", \"vwap_today\": ").append(decimalFormat.format(volumeWeightedAveragePriceArray.get(0).asDouble()));
				builder.append(", \"vwap_24h\": ").append(decimalFormat.format(volumeWeightedAveragePriceArray.get(1).asDouble()));
			}

			if(prices.has("t"))
			{
				JsonNode numberOfTradesArray = prices.get("t");
				builder.append(", \"num_trades\": ").append(numberOfTradesArray.get(0).asInt());
				builder.append(", \"num_trades_24h\": ").append(numberOfTradesArray.get(1).asInt());
			}

			if(jsonPayload.has(3))
			{
				JsonNode symbol = jsonPayload.get(3);
				builder.append(", \"symbol\": \"").append(symbol.asText()).append("\"}");
			}
			else
				return erroneousResponse;

			return builder.toString();
		}
		catch (JsonProcessingException e)
		{
			logger.error(e.getMessage());
		}

		return erroneousResponse;
	}
}
