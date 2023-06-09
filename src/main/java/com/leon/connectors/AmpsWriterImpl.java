package com.leon.connectors;

import com.crankuptheamps.client.Client;
import com.crankuptheamps.client.exception.AMPSException;
import com.crankuptheamps.client.exception.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(value="amps.output.writer", havingValue = "true")
public class AmpsWriterImpl implements OutputWriter
{
	private static final Logger logger = LoggerFactory.getLogger(AmpsWriterImpl.class);

	@Value("${output.writer.amps.name}")
	private String name;
	@Value("${output.writer.amps.connection.string}")
	private String connectionString;
	@Value("${output.writer.amps.topic}")
	private String topic;

	private Client amps = null;

	@PostConstruct
	public void initialize()
	{
		try
		{
			logger.info("Instantiating client {} and initializing AMPS connection: {}", name, connectionString);
			amps = new Client(name);
			amps.connect(connectionString);
			amps.logon();
		}
		catch (ConnectionException ce)
		{
			logger.error(ce.getMessage());
		}
		catch (AMPSException ae)
		{
			ae.printStackTrace();
		}
	}

	@Override
	public void write(String output)
	{
		//OUTPUT STRING IS JSON: "{\"symbol\":\"ABCD\",\"price\":100.0,\"quantity\":1000}"
		try
		{
			amps.publish(topic, output);
		}
		catch (AMPSException ae)
		{
			logger.error(ae.getMessage());
		}
	}

	@Override
	public void stop()
	{
		amps.disconnect();
		amps.close();
	}
}
