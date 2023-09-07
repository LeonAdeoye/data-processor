package com.leon.handlers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leon.disruptors.DisruptorEvent;
import com.leon.disruptors.DisruptorPayload;
import com.leon.disruptors.DisruptorService;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.text.DecimalFormat;

@Component
@ConditionalOnProperty(value="open.exchange.rates.processing", matchIfMissing = false)
public class FxRatesProcessingEventHandler implements EventHandler<DisruptorEvent>
{
	private static final Logger logger = LoggerFactory.getLogger(FxRatesProcessingEventHandler.class);
	private DisruptorService outboundDisruptor;

	public void setOutboundDisruptor(DisruptorService outboundDisruptor)
	{
		this.outboundDisruptor = outboundDisruptor;
	}

	private void process(DisruptorEvent disruptorEvent)
	{
		String payload = disruptorEvent.getPayload().getPayload().toString();
		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(payload);

			if (rootNode.has("rates"))
			{
				JsonNode ratesNode = rootNode.get("rates");
				ratesNode.fields().forEachRemaining(entry ->
				{
					ObjectNode currencyNode = objectMapper.createObjectNode();
					currencyNode.put("currency", entry.getKey());
					currencyNode.put("rate", entry.getValue().asDouble());
					currencyNode.put("type", "fx-rate");
					currencyNode.put("source", "openexchangerates.org");
					outboundDisruptor.push(new DisruptorPayload(currencyNode.toString()));
				});
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
	}

	@Override
	public void onEvent(DisruptorEvent disruptorEvent, long sequence, boolean endOfBatch)
	{
		process(disruptorEvent);
	}
}
