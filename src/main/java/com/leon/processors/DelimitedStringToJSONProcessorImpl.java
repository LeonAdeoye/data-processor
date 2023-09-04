package com.leon.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(value="delimitedStringToJSON.processing", matchIfMissing = false)
public class DelimitedStringToJSONProcessorImpl implements Processor
{
	private static final Logger logger = LoggerFactory.getLogger(DelimitedStringToJSONProcessorImpl.class);
	private ArrayList<String> listOfPropertiesToExtract;
	@Value("${delimitedStringToJSON.processing}")
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
		if (payload.isEmpty())
			return "";

		String[] values = payload.split(this.delimiter);

		if (values.length != listOfPropertiesToExtract.size())
		{
			logger.error("Number of values does not match the number of properties to extract. Payload is {}", payload);
			return "";
		}

		Map<String, String> jsonMap = new HashMap<>();

		for (int i = 0; i < values.length; i++)
			jsonMap.put(listOfPropertiesToExtract.get(i), values[i]);

		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(jsonMap);
		}
		catch (JsonProcessingException e)
		{
			logger.error("Error processing JSON: {} with payload", e.getMessage());
		}

		return "";
	}

	@Override
	public int getProcessingOrder()
	{
		return processingOrder;
	}
}
