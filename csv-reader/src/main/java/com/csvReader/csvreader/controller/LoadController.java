package com.csvReader.csvreader.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.csvReader.csvreader.batch.LoadProcessor;

@RestController
public class LoadController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoadController.class);
	
	@Autowired
	JobLauncher joblauncher;
	
	@Autowired @Qualifier("loadJob")
	Job loadJob;
	
	@Autowired
	LoadProcessor loadProcessor;
	
	@GetMapping
	@RequestMapping("/load")
	public String load() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		
		Map<String, JobParameter> maps = new HashMap<>();
		maps.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters jobParameters = new JobParameters(maps);
		JobExecution jobExecution= joblauncher.run(loadJob, jobParameters);
		logger.info("JobExecution: "+ jobExecution.getStatus());
		String finalMSG ="";
		if(!loadProcessor.unaddedUsersList.isEmpty()) {
			for (String msg: loadProcessor.unaddedUsersList){
				finalMSG = finalMSG + msg +" \n"; 
			}
			loadProcessor.unaddedUsersList.clear();
		}
		loadProcessor.usersToWriteList.clear();
		while (jobExecution.isRunning()) {
			logger.info("...");
		}
		return finalMSG+"\n"+jobExecution.getStatus();
	}
}
