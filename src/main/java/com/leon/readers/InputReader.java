package com.leon.readers;

import com.leon.disruptors.DisruptorPayload;
import reactor.core.publisher.Flux;

public interface InputReader
{
	Flux<DisruptorPayload> read();
	void stop();
}
