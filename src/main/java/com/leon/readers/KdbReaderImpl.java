package com.leon.readers;

import com.leon.disruptors.DisruptorPayload;
import kx.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class KdbReaderImpl implements InputReader
{
	private static final Logger logger = LoggerFactory.getLogger(KdbReaderImpl.class);

	@Value("${kdb.reader.username}")
	String username;

	@Value("${kdb.reader.password}")
	private String password;

	@Value("${kdb.reader.port}")
	private int port;

	@Value("${kdb.reader.hostname}")
	private String host;

	@Value("${kdb.reader.query}")
	private String query;

	private Connection connection = null;

	private static final Class<?>[] expectedTypes =
	{
			String[].class,
			java.sql.Date[].class,
			double[].class,
			java.sql.Timestamp[].class,
			java.sql.Time[].class,
			long[].class,
			int[].class,
			boolean[].class,
			float[].class,
			short[].class,
			byte[].class,
			char[].class
	};

	private <T> T castAndGetValue(Class<T> expectedType, Object columnValue, int row)
	{
		Object castedValue = expectedType.cast(columnValue);
		if (castedValue instanceof String[])
		{
			return (T) ((String[]) castedValue)[row];
		}
		else if (castedValue instanceof java.sql.Date[])
		{
			return (T) ((java.sql.Date[]) castedValue)[row];
		}
		else if (castedValue instanceof java.sql.Time[])
		{
			return (T) ((java.sql.Time[]) castedValue)[row];
		}
		else if (castedValue instanceof java.sql.Timestamp[])
		{
			return (T) ((java.sql.Timestamp[]) castedValue)[row];
		}
		else if (castedValue instanceof int[])
		{
			return (T) Integer.valueOf(((int[]) castedValue)[row]);
		}
		else if (castedValue instanceof long[])
		{
			return (T) Long.valueOf(((long[]) castedValue)[row]);
		}
		else if (castedValue instanceof boolean[])
		{
			return (T) Boolean.valueOf(((boolean[]) castedValue)[row]);
		}
		else if (castedValue instanceof float[])
		{
			return (T) Float.valueOf(((float[]) castedValue)[row]);
		}
		else if (castedValue instanceof double[])
		{
			return (T) Double.valueOf(((double[]) castedValue)[row]);
		}
		else if (castedValue instanceof short[])
		{
			return (T) Short.valueOf(((short[]) castedValue)[row]);
		}
		else if (castedValue instanceof byte[])
		{
			return (T) Byte.valueOf(((byte[]) castedValue)[row]);
		}
		else if (castedValue instanceof char[])
		{
			return (T) Character.valueOf(((char[]) castedValue)[row]);
		}
		else
		{
			throw new IllegalArgumentException("Unsupported array type");
		}
	}

	@Override
	public Flux<DisruptorPayload> read()
	{
		return Flux.create(emitter ->
		{
			try
			{
				logger.info(String.format("Connecting to host: %s on port: %d.", host, port));
				connection = new Connection(host, port, username + ":" + password, false);

				logger.info(String.format("Executing query: %s", query));
				Connection.Result result = (Connection.Result) connection.invoke(query);

				final Map<String, Object> columnValuesMap = new HashMap<>();
				final Map<Integer, ObjectNode> jsonMap = new HashMap<>();
				final long rowCount = ((Object[]) result.columnValuesArrayOfArray[0]).length;
				final ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

				for(int ColumnIndex = 0; ColumnIndex < result.columnNames.length; ++ColumnIndex)
				{
					final String columnName = result.columnNames[ColumnIndex];
					columnValuesMap.put(columnName, result.columnValuesArrayOfArray[ColumnIndex]);

					for (int row = 0; row < rowCount; ++row)
					{
						if(!jsonMap.containsKey(row))
							jsonMap.put(row, JsonNodeFactory.instance.objectNode());

						final Object columnValue = columnValuesMap.get(columnName);

						for (int typeCount = 0; typeCount < expectedTypes.length; ++typeCount)
						{
							Class<?> expectedType = expectedTypes[typeCount];
							if (expectedType.isInstance(columnValue))
							{
								final ObjectNode jsonObject = jsonMap.get(row);
								jsonObject.set(columnName, objectMapper.valueToTree(castAndGetValue(expectedType, columnValue, row)));
								jsonMap.put(row, jsonObject);
								break;
							}
						}
					}
				}

				jsonMap.values().forEach(jsonObject -> emitter.next(new DisruptorPayload(jsonObject.toString())));
				emitter.complete();
			}
			catch(Exception exception)
			{
				emitter.error(exception);
				logger.error(exception.getMessage());
			}
		});
	}

	@Override
	public void stop()
	{

	}
}
