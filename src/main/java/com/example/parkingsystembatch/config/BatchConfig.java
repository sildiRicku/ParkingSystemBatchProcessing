package com.example.parkingsystembatch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.system.repositories")
@ComponentScan(basePackages = "com.example.system")
@EnableBatchProcessing
public class BatchConfig {

}
