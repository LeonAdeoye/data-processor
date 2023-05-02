package com.leon.services;

import com.leon.disruptors.DisruptorService;
import com.leon.disruptors.JournalEventHandler;
import com.leon.disruptors.OutputEventHandler;
import com.leon.factory.DataProcessingService;
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
public class BootstrapServiceImpl implements BootstrapService
{
	private static final Logger logger = LoggerFactory.getLogger(BootstrapServiceImpl.class);
	private boolean hasStarted = false;

	@Autowired
	private DisruptorService inboundDisruptor;
	@Autowired
	private DisruptorService outboundDisruptor;
	@Autowired
	BeanFactory beanFactory;

	private InputReader inputReader;
	private OutputWriter outputWriter;
	private DataProcessingService dataProcessingService;

	@Value("${input.reader.file.path}")
	private String filePath;

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
			InputReader inputReader = (InputReader) factory.getBean("inputReader");

			inputReader.readLines(filePath).subscribe(
					inboundDisruptor::push,
					err ->
					{
						logger.error(err.getMessage());
					},
					() ->
					{
						logger.info("Completed processing of input.");
					}
			);
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
		inboundDisruptor.start("INBOUND", new JournalEventHandler(), new DataProcessingServiceImpl());
		outboundDisruptor.start("OUTBOUND", new JournalEventHandler(), new OutputEventHandler());
		logger.info("Initialization of bootstrapping process completed.");
	}
}
