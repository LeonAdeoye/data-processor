package com.leon.processor;

public interface Processor
{
	String process(String payload);

	int getProcessingOrder();
}
