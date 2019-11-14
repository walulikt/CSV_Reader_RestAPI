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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClearDBController {

	private static final Logger logger = LoggerFactory.getLogger(ClearDBController.class);
	
	@Autowired
	JobLauncher joblauncher;
	
	@Autowired  @Qualifier("clearDBJob")
	Job clearDBJob;
	
	@GetMapping
	@RequestMapping(
			  value = "/clearDB", 
			  method = RequestMethod.DELETE, 
			  headers = "Accept=application/json")
	@ResponseBody
	public String clearDB() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		Map<String, JobParameter> maps = new HashMap<>();
		maps.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters jobParameters = new JobParameters(maps);
		JobExecution jobExecution= joblauncher.run(clearDBJob, jobParameters);
		logger.info("JobExecution: "+ jobExecution.getStatus());
		while (jobExecution.isRunning()) {
			logger.info("...");
		}
		return "Deleting data from database has finished with status: "+jobExecution.getStatus();
	}
}
