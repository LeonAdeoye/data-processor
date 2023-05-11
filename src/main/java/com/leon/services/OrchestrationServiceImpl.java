package com.leon.services;

import com.leon.disruptors.DisruptorService;
import com.leon.disruptors.JournalEventHandler;
import com.leon.disruptors.OutputEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

@Service
public class OrchestrationServiceImpl implements OrchestrationService
{
	private static final Logger logger = LoggerFactory.getLogger(OrchestrationServiceImpl.class);
	private boolean hasStarted = false;

	@Autowired
	private DisruptorService inboundDisruptor;
	@Autowired
	private DisruptorService outboundDisruptor;
	@Autowired
	BeanFactory beanFactory;

	private InputReader inputReader;
	private OutputWriter outputWriter;

	@Value("${input.reader.file.path}")
	private String readerFilePath;

	@Value("${output.writer.file.path}")
	private String writerFilePath;

	@Autowired
	private DataProcessingEventHandler dataProcessingEventHandler;

	@Override
	public void start()
	{
		if(!hasStarted)
		{
			logger.info("Starting bootstrapping process...");
			hasStarted = true;

			ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"bean-factory.xml"});
			BeanFactory factory = context;

			OutputWriter outputWriter = (OutputWriter) factory.getBean("outputWriter");
			outputWriter.initialize(writerFilePath);
			InputReader inputReader = (InputReader) factory.getBean("inputReader");
			inputReader.initialize(readerFilePath);

			inputReader.read().subscribe(
					inboundDisruptor::push,
					err ->
					{
						logger.error(err.getMessage());
					},
					() ->
					{
						logger.info("Completed processing of input.");
					});
		}
		else
			logger.error("Bootstrapper has already started.");
	}

	@Override
	public void stop()
	{
		inboundDisruptor.stop();
		outboundDisruptor.stop();
	}

	@Override
	@PostConstruct
	public void initialize()
	{
		logger.info("Initializing bootstrapping process...");
		dataProcessingEventHandler.setOutboundDisruptor(outboundDisruptor);
		inboundDisruptor.start("INBOUND", new JournalEventHandler(), dataProcessingEventHandler);
		outboundDisruptor.start("OUTBOUND", new JournalEventHandler(), new OutputEventHandler());
		logger.info("Initialization of bootstrapping process completed.");
	}
}
