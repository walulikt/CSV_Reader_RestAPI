package com.csvReader.csvreader.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.csvReader.csvreader.repository.UserRepository;

public class ClearDBTasklet  implements Tasklet  {
	
	private static final Logger logger = LoggerFactory.getLogger(ClearDBTasklet.class);
	private UserRepository userRepository;
	
	public ClearDBTasklet (UserRepository userRepository) {
		this.userRepository=userRepository;
	}
		
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.info("Deleting entities from database in progres...");
		if(userRepository.count()>0)
		userRepository.deleteAll();
	return RepeatStatus.FINISHED;
	}
}
