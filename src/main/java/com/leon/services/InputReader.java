package com.leon.services;

import com.leon.disruptors.DisruptorPayload;
import reactor.core.publisher.Flux;

public interface InputReader
{
	Flux<DisruptorPayload> readLines();
	Flux<DisruptorPayload> readLines(String filePath);
	void shutdown();
}
