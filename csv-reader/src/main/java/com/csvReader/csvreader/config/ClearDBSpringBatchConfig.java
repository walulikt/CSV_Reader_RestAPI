package com.csvReader.csvreader.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.csvReader.csvreader.batch.ClearDBTasklet;
import com.csvReader.csvreader.repository.UserRepository;

@Configuration
@EnableBatchProcessing
public class ClearDBSpringBatchConfig {
	
	@Autowired
	private UserRepository userRepository;
	
	@Bean
	public Job clearDBJob(JobBuilderFactory jobBuilderFactory, @Qualifier("clearDBStep")Step clearDBStep) {
		return jobBuilderFactory.get("clearDBJob")
				.incrementer(new RunIdIncrementer())
				.start(clearDBStep).build();
	}
	
	@Bean
    protected Step clearDBStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory
          .get("clearDBStep")
          .tasklet(new ClearDBTasklet(userRepository))
          .build();
    }
}
