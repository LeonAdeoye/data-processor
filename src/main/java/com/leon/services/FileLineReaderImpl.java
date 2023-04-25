package com.leon.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

public class FileLineReaderImpl implements FileReader
{
	private static final Logger logger = LoggerFactory.getLogger(FileLineReaderImpl.class);

	@Value("${file.line.reader.filepath}")
	private String filePath;

	@Override
	public String read()
	{
		return null;
	}

	@Override
	public void shutdown()
	{

	}

	@PostConstruct
	@Override
	public void initialize()
	{
		// Open output file
	}
}
