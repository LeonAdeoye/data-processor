package com.leon.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KrakenPriceSubProcessor implements SubProcessor
{
	Logger logger = LoggerFactory.getLogger(KrakenPriceSubProcessor.class);

	@Override
	public String process(String payload)
	{
		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonPayload = objectMapper.readTree(payload);
			JsonNode prices = jsonPayload.get(1);
			StringBuilder builder = new StringBuilder("{\"type\": \"price\", \"source\": \"kraken.com\"");

			if(prices.has("a"))
			{
				JsonNode askArray = prices.get("a");
				builder.append(", \"best_ask\": ").append(askArray.get(0).asDouble());

			}

			if(prices.has("b"))
			{
				JsonNode bidArray = prices.get("b");
				builder.append(", \"best_bid\": ").append(bidArray.get(0).asDouble());
			}

			if(prices.has("c"))
			{
				JsonNode closeArray = prices.get("c");
				builder.append(", \"close\": ").append(closeArray.get(0).asDouble());
			}

			if(prices.has("h"))
			{
				JsonNode highArray = prices.get("h");
				builder.append(", \"high\": ").append(highArray.get(0).asDouble());
			}

			if(prices.has("l"))
			{
				JsonNode lowArray = prices.get("l");
				builder.append(", \"low\": ").append(lowArray.get(0).asDouble());
			}

			if(prices.has("o"))
			{
				JsonNode openArray = prices.get("o");
				builder.append(", \"open\": ").append(openArray.get(0).asDouble());
			}

			if(prices.has("v"))
			{
				JsonNode volumeArray = prices.get("v");
				builder.append(", \"vol_today\": ").append(volumeArray.get(0).asDouble());
				builder.append(", \"vol_24h\": ").append(volumeArray.get(1).asDouble());
			}

			if(prices.has("p"))
			{
				JsonNode volumeWeightedAveragePriceArray = prices.get("p");
				builder.append(", \"vwap_today\": ").append(volumeWeightedAveragePriceArray.get(0).asDouble());
				builder.append(", \"vwap_24h\": ").append(volumeWeightedAveragePriceArray.get(1).asDouble());
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
				return "{\"type\": \"error\", \"source\": \"kraken.com\"}";

			return builder.toString();
		}
		catch (JsonProcessingException e)
		{
			logger.error(e.getMessage());
		}

		return "{\"type\": \"error\", \"source\": \"kraken.com\"}";
	}
}
