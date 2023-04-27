package com.leon.services;

import com.leon.disruptors.DisruptorPayload;
import reactor.core.publisher.Flux;

public interface InputReader
{
	Flux<DisruptorPayload> read();
	void shutdown();
	void initialize();
}
