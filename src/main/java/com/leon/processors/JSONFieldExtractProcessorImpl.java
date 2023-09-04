package com.leon.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(value="JSONField.processing", matchIfMissing = false)
public class JSONFieldExtractProcessorImpl implements Processor
{
	private static final Logger logger = LoggerFactory.getLogger(JSONFieldExtractProcessorImpl.class);

	@Value("${JSONField.processing}")
	private int processingOrder;
	@Value("${processor.fieldListToExtract}")
	private String fieldListToExtract;

	private ArrayList<String> listOfPropertiesToExtract;

	@PostConstruct
	public void getListOfPropertiesToExtract()
	{
		String[] values = this.fieldListToExtract.split(",");
		this.listOfPropertiesToExtract = new ArrayList<>(Arrays.asList(values));
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
			ObjectMapper resultMapper = new ObjectMapper();
			JsonNode resultNode = resultMapper.createObjectNode();

			for (String property : listOfPropertiesToExtract)
			{
				if (rootNode.has(property))
				{
					JsonNode extractedNode = rootNode.get(property);
					((com.fasterxml.jackson.databind.node.ObjectNode) resultNode).set(property, extractedNode);
				}
				else
					logger.warn("Property: {} not found in JSON: {}", property, rootNode);
			}

			return resultNode.size() > 0 ? resultNode.toString() : "";
		}
		catch (IOException ioe)
		{
			logger.error("Error processing JSON: {}", ioe.getMessage());
		}

		return payload;
	}

	@Override
	public int getProcessingOrder()
	{
		return processingOrder;
	}
}
