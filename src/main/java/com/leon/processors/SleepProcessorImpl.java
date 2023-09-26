package com.leon.processors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.Random;

@Component
@ConditionalOnProperty(value="sleep.processing", matchIfMissing = false)
public class SleepProcessorImpl implements Processor
{
    @Value("${sleep.processor.time.random: false}")
    private boolean isRandom;
    @Value("${sleep.processor.time.seconds}")
    private int sleepTime;
    @Value("${sleep.processing}")
    private int processingOrder;

    @Override
    public int getProcessingOrder()
    {
        return this.processingOrder;
    }

    @PostConstruct
    public void initialize()
    {
        if(isRandom)
        {
            int randomNumberBetweenOneAndSleepTime = new Random().nextInt(sleepTime) + 1;
            this.sleepTime = randomNumberBetweenOneAndSleepTime * 1000;
        }
        else
            this.sleepTime = sleepTime * 1000;
    }

    @Override
    public String process(String payload)
    {
        try
        {
            Thread.sleep(sleepTime);
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
        finally
        {
            return payload;
        }
    }
}
