package com.leon;

import com.leon.services.BootstrapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.annotation.PostConstruct;

@SpringBootApplication
public class App
{
    @Autowired
    BootstrapService bootstrapService;

    public static void main(String[] args)
    {
        SpringApplication.run(App.class, args);
    }

    @PostConstruct
    public void start()
    {
        bootstrapService.start();
    }
}