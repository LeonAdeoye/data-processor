package com.leon.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnProperty(value="JSONToDelimitedString.processing", matchIfMissing = false)
public class JSONToDelimitedStringProcessorImpl implements Processor
{
	private static final Logger logger = LoggerFactory.getLogger(JSONToDelimitedStringProcessorImpl.class);
	private ArrayList<String> listOfPropertiesToExtract;
	@Value("${JSONToDelimitedString.processing}")
	private int processingOrder;
	@Value("${processor.fieldListToExtract}")
	private String fieldListToExtract;

	@Value("${processor.delimiter}")
	private String delimiter;

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

		ObjectMapper objectMapper = new ObjectMapper();

		try
		{
			JsonNode jsonNode = objectMapper.readTree(payload);
			List<String> extractedValues = new ArrayList<>();

			for (String property : listOfPropertiesToExtract)
			{
				if(jsonNode.has(property))
				{
					JsonNode propertyNode = jsonNode.get(property);
					extractedValues.add(propertyNode.asText());
				}
				else
					extractedValues.add("");
			}

			return String.join(delimiter, extractedValues);
		}
		catch (IOException e)
		{
			logger.error("Error processing JSON payload: " + e.getMessage());
		}

		return payload;
	}

	@Override
	public int getProcessingOrder()
	{
		return processingOrder;
	}
}
