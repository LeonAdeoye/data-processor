package com.leon;

import com.leon.services.OrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJms
@SpringBootApplication
@EnableScheduling
public class App implements CommandLineRunner
{
    public static ConfigurableApplicationContext context;

    @Autowired
	private OrchestrationService orchestrationService;

    public static void main(String[] args)
    {
        context = SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args)
    {
        orchestrationService.start();
    }
}