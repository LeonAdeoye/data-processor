package com.leon.handlers;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface SubProcessor
{
	String process(String payload);
}
