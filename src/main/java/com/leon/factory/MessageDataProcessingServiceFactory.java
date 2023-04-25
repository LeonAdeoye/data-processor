package com.leon.factory;

import com.leon.services.DataProcessingServiceImpl;

public abstract class MessageDataProcessingServiceFactory extends DataProcessingServiceFactory
{
	public DataProcessingService createDataProcessor()
	{
		return new DataProcessingServiceImpl();
	}
}
