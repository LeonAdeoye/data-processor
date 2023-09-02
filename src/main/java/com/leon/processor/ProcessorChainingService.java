package com.leon.processor;

public interface ProcessorChainingService
{
	String chainProcessing(String payload);
	public int getProcessorCount();
}
