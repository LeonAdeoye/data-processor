package com.leon.writers;

public interface OutputWriter
{
	void write(String output) throws Exception;
	void stop();
}
