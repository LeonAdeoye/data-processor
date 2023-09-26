package com.leon.readers;

import com.leon.disruptors.DisruptorPayload;
import kx.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.FluxSink;

import static com.fasterxml.jackson.databind.util.StdDateFormat.DATE_FORMAT_STR_ISO8601;

@Service
@ConditionalOnProperty(value="kdb.input.reader", havingValue = "true")
public class KdbReaderImpl implements InputReader
{
	class KdbProcessorMsgHandler implements Connection.MsgHandler
	{
		@Override
		public void processMsg(Connection conn, byte msgType, Object msg) throws IOException
		{
			logger.info("process message.");
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

	private <T> T castAndGetValue(Object columnValue, int row)
	{
		switch(Connection.getKDBType(columnValue))
		{
			case 11: // String
				return (T) ((String[]) columnValue)[row];
			case 9: // Double
				return (T) Double.valueOf(((double[]) columnValue)[row]);
			case 7: // Long
				return (T) Long.valueOf(((long[]) columnValue)[row]);
			case 14: // Date
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
			case 15: // DateTime
				return (T) ((java.util.Date[]) columnValue)[row];
			case 19: // Time
				return (T) ((java.sql.Time[]) columnValue)[row];
			default:
				throw new IllegalArgumentException("Unsupported array type: " + Connection.getKDBType(columnValue));
		}
	}

	void parseKdbResponse(Connection.Result result, FluxSink<DisruptorPayload> emitter)
	{
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
				final ObjectNode jsonObject = jsonMap.get(row);
				jsonObject.set(columnName, objectMapper.valueToTree(castAndGetValue(columnValue, row)));
				jsonMap.put(row, jsonObject);
			}
		}

		jsonMap.values().forEach(jsonObject -> emitter.next(new DisruptorPayload(jsonObject.toString())));
	}

	private void invoke(FluxSink<DisruptorPayload> emitter) throws Exception
	{
		logger.info(String.format("Connecting to host: %s on port: %d.", host, port));
		connection = new Connection(host, port, username + ":" + password, false);
		logger.info(String.format("Synchronously executing query: %s", query));
		Connection.Result result = (Connection.Result) connection.invoke(query);
		parseKdbResponse(result, emitter);
		emitter.complete();
	}

	private void invokeAsync(FluxSink<DisruptorPayload> emitter) throws Exception
	{
		logger.info(String.format("Connecting to host: %s on port: %d.", host, port));
		connection = new Connection(host, port, username + ":" + password, false);
		logger.info(String.format("Asynchronously executing query: %s", query));
		connection.invokeAsync(query);
		Connection.Result result = (Connection.Result) connection.invoke(query);
		// TODO: figure out how to process async response
		connection.setMsgHandler(new KdbProcessorMsgHandler());
		parseKdbResponse(result, emitter);
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
