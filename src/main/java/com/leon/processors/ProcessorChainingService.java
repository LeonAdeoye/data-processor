package com.leon.processors;

public interface ProcessorChainingService
{
	String chainProcessing(String payload);
	public int getProcessorCount();
}
