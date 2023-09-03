package com.leon.processors;

public interface Processor
{
	String process(String payload);

	int getProcessingOrder();
}
