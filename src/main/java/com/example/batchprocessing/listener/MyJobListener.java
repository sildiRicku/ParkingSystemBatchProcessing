package com.example.batchprocessing.listener;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class MyJobListener implements JobExecutionListener {
    private final Logger logger = Logger.getLogger(MyJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("logger before job");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("logger after job");
    }
}
