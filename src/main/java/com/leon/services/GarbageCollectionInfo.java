package com.leon.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.management.*;
import java.lang.management.*;
import java.util.Arrays;
import java.util.Set;

@Service
public class GarbageCollectionInfo
{
	private static final Logger logger = LoggerFactory.getLogger(GarbageCollectionInfo.class);

	@Scheduled(cron = "*/30 * * * * *") // Runs every 30 seconds
	public void logInfo()
	{
		try
		{
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName gcObjectName = new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",*");
			Set<ObjectName> gcObjectNames = mBeanServer.queryNames(gcObjectName, null);

			for (ObjectName name : gcObjectNames)
			{
				GarbageCollectorMXBean gcBean = ManagementFactory.newPlatformMXBeanProxy(mBeanServer, name.toString(), GarbageCollectorMXBean.class);
				// Retrieve garbage collection information
				logger.info("Name: " + gcBean.getName());
				logger.info("Collection count: " + gcBean.getCollectionCount());
				logger.info("Collection time: " + gcBean.getCollectionTime());
				String[] memoryPoolNames = gcBean.getMemoryPoolNames();
				logger.info("Memory pools: " + Arrays.toString(memoryPoolNames));
				// Retrieve information about associated memory pools
				for (String poolName : memoryPoolNames)
				{
					// TODO
					MemoryPoolMXBean poolBean = ManagementFactory.newPlatformMXBeanProxy(mBeanServer, poolName, MemoryPoolMXBean.class);
					logger.info("Memory Pool: {}, type: {}, usage: {}" + poolBean.getName(), poolBean.getType(), poolBean.getUsage());
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Exception thrown will gathering info on the GC: " + e.getMessage());
		}
	}
}
