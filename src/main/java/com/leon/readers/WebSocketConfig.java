package com.leon.readers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Configuration
@EnableWebSocket
@ConditionalOnProperty(value="websocket.input.reader", havingValue = "true")
public class WebSocketConfig implements WebSocketConfigurer
{
	@Autowired
	private WebSocketReaderImpl webSocketReader;

	private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
	{
		logger.info("registering web socket handler");
		registry.addHandler(webSocketReader, "/crypto-price").setAllowedOrigins("*");
	}
}
