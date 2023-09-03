package com.leon.processors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value="filter.processing", matchIfMissing = false)
public class FilterProcessorImpl implements Processor
{
	@Value("${processor.include.filter}")
	private String includeFilter;
	@Value("${processor.exclude.filter}")
	private String excludeFilter;

	@Value("${filter.processing:0}")
	private int processingOrder;
	@Override
	public int getProcessingOrder()
	{
		 return this.processingOrder;
	}

	@Override
	public String process(String payload)
	{
		if(payload.isEmpty() || notApplicable(payload))
			return "";

		return payload;
	}

	private boolean notApplicable(String payload)
	{
		boolean result;
		if(excludeFilter != null && !excludeFilter.isEmpty() && payload.contains(excludeFilter))
			result = true;
		else if(includeFilter != null && !includeFilter.isEmpty() && !payload.contains(includeFilter))
			result = true;
		else
			result = false;

		return result;
	}
}
