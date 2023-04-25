package com.leon.disruptors;

import com.lmax.disruptor.EventHandler;

public interface DisruptorService
{
	void start(String name, EventHandler<DisruptorEvent> journalHandler, EventHandler<DisruptorEvent> actionEventHandler);
	void push(DisruptorPayload payLoad);
	void stop();
}
