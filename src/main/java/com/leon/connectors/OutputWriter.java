package com.leon.connectors;

public interface OutputWriter
{
	void write(String output) throws Exception;
	void stop();
}
