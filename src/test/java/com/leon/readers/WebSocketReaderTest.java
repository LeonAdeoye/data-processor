package com.leon.readers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.client.WebSocketClient;
import java.lang.reflect.Field;
import static org.mockito.Mockito.*;

class WebSocketReaderTest
{
	@Mock
	private WebSocketClient mockWebSocketClient;

	private WebSocketReaderImpl webSocketReader = new WebSocketReaderImpl();

	@BeforeEach
	void setUp() throws Exception
	{
		MockitoAnnotations.openMocks(this);
		setPrivateField(webSocketReader, "webSocketClient", mockWebSocketClient);
		setPrivateField(webSocketReader, "sourceUrl", "ws://example.com");
	}

	@Test
	void initialize_shouldOpenWebSocketConnectionAndDoHandshake() throws Exception
	{
		// Act
		webSocketReader.initialize();

		// Assert
		verify(mockWebSocketClient).doHandshake(eq(webSocketReader), eq("ws://example.com"));
	}

	// Helper method to set private fields using reflection
	private void setPrivateField(Object targetObject, String fieldName, Object value) throws Exception {
		Field field = targetObject.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(targetObject, value);
	}
}
