package com.csvReader.csvreader.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.csvReader.csvreader.controller.LoadController;
import com.csvReader.csvreader.model.User;
import com.csvReader.csvreader.repository.UserRepository;

@Component
public class DBWirter implements ItemWriter<User> {
	
	private static final Logger logger = LoggerFactory.getLogger(LoadController.class);
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public void write(List<? extends User> users) throws Exception {
		logger.info("Writing "+users.size() +" users to database.");
		userRepository.saveAll(users);
		logger.info("Writing users to database completed.");
	}
}