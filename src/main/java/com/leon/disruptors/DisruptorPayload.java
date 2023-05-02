package com.leon.disruptors;

public class DisruptorPayload
{
	private String payloadType;
	private String payload;

	public DisruptorPayload(String payloadType, String payload)
	{
		this.payloadType = payloadType;
		this.payload = payload;
	}

	public DisruptorPayload(String payload)
	{
		this.payloadType = "";
		this.payload = payload;
	}

	public String getPayloadType()
	{
		return payloadType;
	}

	public String getPayload()
	{
		return payload;
	}

	@Override
	public String toString()
	{
		return "DisruptorPayload{" + "payloadType='" + payloadType + '\'' + ", payload='" + payload + '\'' + '}';
	}
}
