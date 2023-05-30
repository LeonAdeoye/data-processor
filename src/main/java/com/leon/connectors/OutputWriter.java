package com.leon.connectors;

public interface OutputWriter
{
	void write(String output);
	void initialize(String filePath);
	void stop();
}
