package com.leon.connectors;

import com.leon.disruptors.DisruptorPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
@ConditionalOnProperty(value="jms.input.reader", havingValue = "true")
public class JmsReaderImpl implements InputReader, MessageListener
{
	private static final Logger logger = LoggerFactory.getLogger(JmsReaderImpl.class);
	public final ReplayProcessor<DisruptorPayload> processor = ReplayProcessor.create();
	private final FluxSink<DisruptorPayload> sink = processor.sink();
	private final String END_OF_STREAM = "END_OF_STREAM";

	public JmsReaderImpl() {}

	@Override
	public Flux<DisruptorPayload> read()
	{
		return processor;
	}

	@Override
	public void stop()
	{
	}

	@Override
	@org.springframework.jms.annotation.JmsListener(destination = "input.activemq")
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
}
