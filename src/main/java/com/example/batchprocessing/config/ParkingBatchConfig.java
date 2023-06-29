package com.example.batchprocessing.config;

import com.example.batchprocessing.listener.MyJobListener;
import com.example.system.models.ParkingSystem;

import com.example.system.repositories.ParkingSystemRepo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;

import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableBatchProcessing
@EnableJpaRepositories(basePackages = "com.example.system.repositories")
@ComponentScan(basePackages = "com.example.system")
public class ParkingBatchConfig {
    @Value("${csv.file.path}")
    private String csvFilePath;
    private final ParkingSystemRepo parkingSystemRepo;
    @Autowired
    private MyJobListener myJobListener;

    @Autowired
    public ParkingBatchConfig(ParkingSystemRepo parkingSystemRepo) {
        this.parkingSystemRepo = parkingSystemRepo;
    }

    @Bean
    public FlatFileItemReader<ParkingSystem> reader() {
        FlatFileItemReader<ParkingSystem> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(csvFilePath));
        reader.setName("csvReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper());
        return reader;
    }


    private LineMapper<ParkingSystem> lineMapper() {
        DefaultLineMapper<ParkingSystem> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter("," +
                "");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("systemId", "address", "firmwareVersion", "firstInstallDate", "identifier", "lastUpdate", "totalMoney", "workingStatus");

        BeanWrapperFieldSetMapper<ParkingSystem> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(ParkingSystem.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public ParkingSystemProcessor processor() {
        return new ParkingSystemProcessor();
    }

    @Bean
    public RepositoryItemWriter<ParkingSystem> writer() {
        RepositoryItemWriter<ParkingSystem> writer = new RepositoryItemWriter<>();
        writer.setRepository(parkingSystemRepo);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("csv-step", jobRepository).
                <ParkingSystem, ParkingSystem>chunk(10, transactionManager)
                .reader(reader())
                .processor(new ParkingSystemProcessor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("Parkingsystems", jobRepository)
                .listener(myJobListener)
                .flow(step1(jobRepository, transactionManager)).end().build();

    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }

}
