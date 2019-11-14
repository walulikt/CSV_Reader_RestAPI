package com.csvReader.csvreader.config;

import java.net.MalformedURLException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.csvReader.csvreader.batch.LoadProcessor;
import com.csvReader.csvreader.model.User;

@Configuration
@EnableBatchProcessing
public class SpringBatchLoadConfig {
	
	@Bean
	public Job loadJob(JobBuilderFactory jobBuilderFactory, @Qualifier("loadStep")Step loadStep) {
		Job job = jobBuilderFactory.get("loadJob").incrementer(new RunIdIncrementer()).start(loadStep).build();
		return job;
	}

	@Bean
	public Step loadStep(StepBuilderFactory stepBuilderFactory, ItemReader<User> loadReader,
		LoadProcessor loadProcessor, ItemWriter<User> itemWriter) {
		return stepBuilderFactory.get("File-load")
				.<User, User>chunk(100)
				.reader(loadReader)
				.processor(loadProcessor)
				.writer(itemWriter)
				.build();
	}

	@Bean
	public FlatFileItemReader<User> loadReader(@Value("${input}") Resource resource) throws MalformedURLException {
		FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<User>();
		flatFileItemReader.setResource(resource);
		flatFileItemReader.setName("CSV-reader");
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}

	@Bean
	public LineMapper<User> lineMapper() {
		DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<User>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(";");
		lineTokenizer.setNames(new String[] { "First_name", "Last_name", "Birth_date", "Phone_number" });
		lineTokenizer.setStrict(false);
		lineTokenizer.setIncludedFields(0, 1, 2, 3);
		BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<User>();
		fieldSetMapper.setTargetType(User.class);
		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		return defaultLineMapper;
	}
}
