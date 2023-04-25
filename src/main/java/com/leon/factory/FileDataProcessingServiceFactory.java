package com.leon.factory;

import com.leon.services.DataProcessingServiceImpl;

public abstract class FileDataProcessingServiceFactory extends DataProcessingServiceFactory
{
	public DataProcessingService createDataProcessor()
	{
		return new DataProcessingServiceImpl();
	}
}