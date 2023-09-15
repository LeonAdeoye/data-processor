package com.leon.readers;

import com.leon.disruptors.DisruptorPayload;
import kx.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.FluxSink;

import static com.fasterxml.jackson.databind.util.StdDateFormat.DATE_FORMAT_STR_ISO8601;

@Service
public class KdbReaderImpl implements InputReader
{
	class DataProcessorMsgHandler implements Connection.MsgHandler
	{
		@Override
		public void processMsg(Connection conn, byte msgType, Object msg) throws IOException
		{
			// Your implementation here
		}
	}

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

	@Value("${kdb.reader.synchronous}")
	private boolean synchronous;

	private Connection connection = null;

	private static final Class<?>[] expectedTypes =
	{
			String[].class,
			java.sql.Timestamp[].class,
			java.sql.Date[].class,
			double[].class,
			java.sql.Time[].class,
			long[].class,
			int[].class,
			boolean[].class,
			float[].class,
			short[].class,
			byte[].class,
			char[].class
	};

	private <T> T castKDBValue(Object columnValue, int row)
	{
		switch(Connection.getKDBType(columnValue))
		{
			case 11: // String
				return (T) ((String[]) columnValue)[row];
			case 9: // Double
				return (T) Double.valueOf(((double[]) columnValue)[row]);
			case 7: // Long
				return (T) Long.valueOf(((long[]) columnValue)[row]);
			case 14: // LocalDate
				return (T) ((java.sql.Date[]) columnValue)[row];
			case 6: // Integer
				return (T) Integer.valueOf(((int[]) columnValue)[row]);
			case 1: // Boolean
				return (T) Boolean.valueOf(((boolean[]) columnValue)[row]);
			case 2: // Boolean
				return (T) ((UUID[]) columnValue)[row];
			case 4: // Byte
				return (T) Byte.valueOf(((byte[]) columnValue)[row]);
			case 5: // Short
				return (T) Short.valueOf(((short[]) columnValue)[row]);
			case 8: // Float
				return (T) Float.valueOf(((float[]) columnValue)[row]);
			case 10: // Char
				return (T) Character.valueOf(((char[]) columnValue)[row]);
			case 12: // Instant
				return (T) ((Instant[]) columnValue)[row];
			case 15: // LocalDateTime
				return (T) ((java.sql.Timestamp[]) columnValue)[row];
			default:
				throw new IllegalArgumentException("Unsupported array type");
		}
	}

	private <T> T castAndGetValue(Class<T> expectedType, Object columnValue, int row)
	{
		Object castedValue = expectedType.cast(columnValue);

		logger.info("Casted type: " + castedValue.getClass().getName() + " KDB type: " + Connection.getKDBType(columnValue));

		if (castedValue instanceof String[])
		{
			return (T) ((String[]) castedValue)[row];
		}
		else if (castedValue instanceof java.sql.Timestamp[])
		{
			return (T) ((java.sql.Timestamp[]) castedValue)[row];
		}
		else if (castedValue instanceof java.sql.Date[])
		{
			return (T) ((java.sql.Date[]) castedValue)[row];
		}
		else if (castedValue instanceof java.sql.Time[])
		{
			return (T) ((java.sql.Time[]) castedValue)[row];
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
		else if(castedValue instanceof UUID[])
		{
			return (T) ((UUID[]) castedValue)[row];
		}
		else
		{
			throw new IllegalArgumentException("Unsupported array type");
		}
	}

	private void invoke(FluxSink<DisruptorPayload> emitter) throws Exception
	{
		logger.info(String.format("Connecting to host: %s on port: %d.", host, port));
		connection = new Connection(host, port, username + ":" + password, false);

		logger.info(String.format("Synchronously executing query: %s", query));
		Connection.Result result = (Connection.Result) connection.invoke(query);

		final Map<String, Object> columnValuesMap = new HashMap<>();
		final Map<Integer, ObjectNode> jsonMap = new HashMap<>();
		final long rowCount = ((Object[]) result.columnValuesArrayOfArray[0]).length;
		logger.info("Synchronously processing {} rows", rowCount);
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT_STR_ISO8601));

		for(int ColumnIndex = 0; ColumnIndex < result.columnNames.length; ++ColumnIndex)
		{
			final String columnName = result.columnNames[ColumnIndex];
			columnValuesMap.put(columnName, result.columnValuesArrayOfArray[ColumnIndex]);

			for (int row = 0; row < rowCount; ++row)
			{
				if(!jsonMap.containsKey(row))
					jsonMap.put(row, JsonNodeFactory.instance.objectNode());

				final Object columnValue = columnValuesMap.get(columnName);

				/*for (int typeCount = 0; typeCount < expectedTypes.length; ++typeCount)
				{
					Class<?> expectedType = expectedTypes[typeCount];
					if (expectedType.isInstance(columnValue))
					{
						final ObjectNode jsonObject = jsonMap.get(row);
						jsonObject.set(columnName, objectMapper.valueToTree(castAndGetValue(expectedType, columnValue, row)));
						jsonMap.put(row, jsonObject);
						break;
					}
				}*/
				final ObjectNode jsonObject = jsonMap.get(row);
				jsonObject.set(columnName, objectMapper.valueToTree(castKDBValue(columnValue, row)));
				jsonMap.put(row, jsonObject);
			}
		}

		jsonMap.values().forEach(jsonObject -> emitter.next(new DisruptorPayload(jsonObject.toString())));
		emitter.complete();
	}

	private void invokeAsync(FluxSink<DisruptorPayload> emitter) throws Exception
	{
		logger.info(String.format("Connecting to host: %s on port: %d.", host, port));
		connection = new Connection(host, port, username + ":" + password, false);

		logger.info(String.format("Asynchronously executing query: %s", query));
		connection.invokeAsync(query);
		Connection.Result result = (Connection.Result) connection.invoke(query);

		// TODO
		connection.setMsgHandler(new Connection.MsgHandler()
		{
			@Override
			public void processMsg(Connection Connection, byte msgType, Object msg) throws IOException
			{
				kx.Connection.MsgHandler.super.processMsg(Connection, msgType, msg);
			}
		});

		final Map<String, Object> columnValuesMap = new HashMap<>();
		final Map<Integer, ObjectNode> jsonMap = new HashMap<>();
		final long rowCount = ((Object[]) result.columnValuesArrayOfArray[0]).length;
		logger.info("Asynchronously processing {} rows", rowCount);
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

	@Override
	public Flux<DisruptorPayload> read()
	{
		return Flux.create(emitter ->
		{
			try
			{
				if(synchronous)
					invoke(emitter);
				else
					invokeAsync(emitter);  // TODO: WIP
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
