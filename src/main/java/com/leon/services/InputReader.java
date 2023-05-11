package com.leon.services;

import com.leon.disruptors.DisruptorPayload;
import reactor.core.publisher.Flux;

public interface InputReader
{
	void initialize(String filePath);
	Flux<DisruptorPayload> read();
	void shutdown();
}
