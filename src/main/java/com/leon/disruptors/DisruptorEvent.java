package com.leon.disruptors;

public class DisruptorEvent
{
	private DisruptorPayload disruptorPayload;

	public DisruptorPayload getPayload()
	{
		return disruptorPayload;
	}

	public void setPayload(DisruptorPayload disruptorPayload)
	{
		this.disruptorPayload = disruptorPayload;
	}

	@Override
	public String toString()
	{
		return "DisruptorEvent{payload=" + disruptorPayload + '}';
	}
}
