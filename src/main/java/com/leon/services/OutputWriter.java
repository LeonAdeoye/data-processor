package com.leon.services;

public interface OutputWriter
{
	void write(String output);
	void initialize(String filePath);
	void shutdown();
}
