package com.leon.services;

import com.leon.disruptors.DisruptorPayload;
import reactor.core.publisher.Flux;

public interface InputReader
{
	void initialize();
	Flux<DisruptorPayload> read();
	Flux<DisruptorPayload> read(String filePath);
	void shutdown();
}
