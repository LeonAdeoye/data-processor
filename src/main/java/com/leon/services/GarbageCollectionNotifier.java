package com.leon.services;

import com.sun.management.GarbageCollectionNotificationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.management.*;
import javax.management.openmbean.CompositeData;
import java.lang.management.*;
import java.util.List;
import java.util.Map;

@Service
public class GarbageCollectionNotifier
{
	private static final Logger logger = LoggerFactory.getLogger(GarbageCollectionNotifier.class);

	@PostConstruct
	public void createNotification()
	{
		List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
		for(GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans)
		{
			NotificationEmitter emitter = (NotificationEmitter) garbageCollectorMXBean;
			NotificationListener listener = (notification, handback) ->
			{
				if(notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION))
				{
					GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
					logger.info(info.getGcAction() + " " + info.getGcName() + " from " + info.getGcCause() + ", duration: " + info.getGcInfo().getDuration() + "ms");
					Map<String, MemoryUsage> memoryAfter = info.getGcInfo().getMemoryUsageAfterGc();
					for(Map.Entry<String, MemoryUsage> entry : memoryAfter.entrySet())
					{
						MemoryUsage memoryUsage = entry.getValue();
						logger.info("Name: {}, initial memory: {} Mb, committed memory: {} Mb, memory used: {} Mb.",
								entry.getKey(), memoryUsage.getInit()/1_000_000, memoryUsage.getCommitted()/1_000_000, memoryUsage.getUsed()/1_000_000);
					}
				}
			};
			emitter.addNotificationListener(listener, null, null);
		}
	}
}
