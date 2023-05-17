package com.leon.inputOutput;

import com.leon.disruptors.DisruptorPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import reactor.core.publisher.Flux;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;

public class JmsReaderImpl implements MessageListener, InputReader
{

	private static final Logger logger = LoggerFactory.getLogger(JmsReaderImpl.class);
	private final ReplayProcessor<DisruptorPayload> processor;
	private final FluxSink<DisruptorPayload> sink;
	private final String END_OF_STREAM = "END_OF_STREAM";

	public JmsReaderImpl()
	{
		processor = ReplayProcessor.create();
		sink = processor.sink();
	}

	@Override
	@JmsListener(destination = "input.activemq")
	public void onMessage(Message message)
	{
		try
		{
			if (message instanceof TextMessage)
			{
				TextMessage textMessage = (TextMessage) message;
				logger.info("Received message: " + textMessage.getText());
				if(textMessage.getText().equals(END_OF_STREAM))
					sink.complete();
				else
					sink.next(new DisruptorPayload(textMessage.getText()));
			}
		}
		catch (Exception e)
		{
			logger.error("Received Exception with processing message from JMS listener: " + e.getLocalizedMessage());
			sink.error(e);
		}
	}

	@Override
	public void initialize(String filePath, String endOfStream)
	{
	}

	@Override
	public Flux<DisruptorPayload> read()
	{
		return processor;
	}

	@Override
	public void stop()
	{
	}
}
