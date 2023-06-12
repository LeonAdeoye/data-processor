package com.leon.services;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value="check.json.validity", havingValue = "true")
public class JsonValidator
{
	private static final Logger logger = LoggerFactory.getLogger(JsonValidator.class);
	private static final ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

	public static boolean isValid(String json)
	{
		try
		{
			mapper.readTree(json);
		}
		catch (JacksonException e)
		{
			logger.error("Exception thrown {} because of invalid JSON: {}", e.getMessage(), json);
			return false;
		}
		return true;
	}
}
