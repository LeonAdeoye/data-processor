package com.leon.readers;

import com.leon.disruptors.DisruptorPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;
import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(value="websocket.input.reader", havingValue = "true")
public class WebSocketReaderImpl extends TextWebSocketHandler implements InputReader
{
	Logger logger = LoggerFactory.getLogger(WebSocketReaderImpl.class);
	@Value("${websocket.reader.source.url}")
	private String sourceUrl;
	@Value("${input.reader.end.indicator}:")
	private String endIndicator;
	@Value("${websocket.reader.subscribe.request}")
	private String subscribeRequest;
	private final WebSocketClient webSocketClient = new StandardWebSocketClient();
	private final ReplayProcessor<DisruptorPayload> processor = ReplayProcessor.create();
	private final FluxSink<DisruptorPayload> sink = processor.sink();

	@PostConstruct
	public void initialize()
	{
		logger.info("Opening web socket connection to URL: {}", sourceUrl);
		try
		{
			webSocketClient.doHandshake(this, sourceUrl);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception
	{
		logger.info("Successfully connected to WebSocket source.");
		session.sendMessage(new TextMessage(subscribeRequest));
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception
	{
		String messageText = message.getPayload();
		if(!endIndicator.isEmpty() && messageText.equals(endIndicator))
		{
			logger.info("Received end indicator: " + endIndicator);
			sink.complete();
		}
		else
			sink.next(new DisruptorPayload(messageText));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception
	{
		if(endIndicator.isEmpty())
			sink.complete();

		logger.info("Connection closed: " + status.getReason());
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
