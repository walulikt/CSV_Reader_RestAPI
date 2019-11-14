package com.csvReader.csvreader.batch;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import com.csvReader.csvreader.model.User;
import com.csvReader.csvreader.repository.UserRepository;

@Component
public class LoadProcessor implements ItemProcessor<User, User> {

	private static final Logger logger = LoggerFactory.getLogger(LoadProcessor.class);
	
	public List<User> usersToWriteList = new ArrayList<>();
	public List<String> unaddedUsersList = new ArrayList<>(); 
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public User process(User user) throws Exception {
		String msg = "";
		if(user.getFirstName()==null || user.getFirstName().isEmpty()) {
			msg = "User can't be add to database: " + user.toString() +" no firstname present.";
			logger.info(msg);
			unaddedUsersList.add(msg);
			return null;
		};
		if(user.getLastName()==null || user.getLastName().isEmpty()) {
			msg="User can't be add to database: " + user.toString() +" no lastname present.";
			logger.info(msg);
			unaddedUsersList.add(msg);
			return null;
		};
		if(user.getBirthDate()==null) {
			msg ="User can't be add to database: " + user.toString() +" no birthdate present.";
			logger.info(msg);
			unaddedUsersList.add(msg);
			return null;
		}
		Integer userPhone = user.getPhoneNumber();
		if((userPhone>0 && userPhone<100000000) || userPhone>999999999) {
			msg ="User: " + user.toString() +" has wrong lenght of phnone number. User added to database with phone number 0.";
			logger.info(msg);
			unaddedUsersList.add(msg);
		}
		logger.info("Checking is there another user in database with phone number: " + userPhone.toString());
		User userTmp = new User();
		userTmp.setPhoneNumber(userPhone.toString()); 
		Example<User> userExample = Example.of(userTmp); 
		msg ="Phone number of user: " + user.toString() +" is already assigned to another user. User added to database with phone number 0. ";
		if(userPhone!=0 && userRepository.exists(userExample)) {
			logger.info(msg);
			unaddedUsersList.add(msg);
			user.setPhoneNumber("0");
		}
		logger.info("Checking is there another user in this transaction with phone number:  " + userPhone.toString());
		for (int i=0; i<usersToWriteList.size(); i++) {
			if(userPhone!=0 && usersToWriteList.get(i).getPhoneNumber().equals(userPhone)) {
				logger.info(msg);
				unaddedUsersList.add(msg);
				user.setPhoneNumber("0");
			}
		}
		user.setFirstName(user.getFirstName().toUpperCase());
		user.setLastName(user.getLastName().toUpperCase());
		usersToWriteList.add(user);
		return user;
	}

}
