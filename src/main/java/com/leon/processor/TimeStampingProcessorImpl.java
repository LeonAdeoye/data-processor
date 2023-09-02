package com.leon.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value="timestamp.processing", matchIfMissing = false)
public class TimeStampingProcessorImpl implements Processor
{
	private static final Logger logger = LoggerFactory.getLogger(TimeStampingProcessorImpl.class);
	@Value("${timestamp.processing.field.name:time_stamp}")
	private String timeStampFieldName;

	@Value("${timestamp.processing}")
	private int order;

	@Override
	public int getOrder()
	{
		return this.order;
	}
	@Override
	public String process(String payload)
	{
		if(payload.isEmpty())
			return "";

		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(payload);
			ObjectNode timeStampNode = JsonNodeFactory.instance.objectNode();
			timeStampNode.set(this.timeStampFieldName, JsonNodeFactory.instance.numberNode(System.currentTimeMillis()));
			((ObjectNode) rootNode).setAll(timeStampNode);
			return rootNode.toString();
		}
		catch (JsonMappingException jme)
		{
			logger.error(jme.getMessage());
		}
		catch (JsonProcessingException jpe)
		{
			logger.error(jpe.getMessage());
		}

		return payload;
	}
}
