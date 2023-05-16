package com.leon.inputOutput;

import com.leon.disruptors.DisruptorPayload;
import reactor.core.publisher.Flux;

public interface InputReader
{
	void initialize(String filePath, String endOfStream);
	Flux<DisruptorPayload> read();
	void stop();
}
