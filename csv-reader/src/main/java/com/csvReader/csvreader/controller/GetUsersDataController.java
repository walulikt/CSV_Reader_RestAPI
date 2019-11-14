package com.csvReader.csvreader.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.csvReader.csvreader.model.User;
import com.csvReader.csvreader.model.UserDTO;
import com.csvReader.csvreader.repository.UserRepository;

@RestController
public class GetUsersDataController {
	
	private static final Logger logger = LoggerFactory.getLogger(GetUsersDataController.class);
	
	@Autowired
	JobLauncher joblauncher;
	
	@Autowired 
	UserRepository userRepository;
	
	@GetMapping
	@RequestMapping(
			  value = "/usersCount", 
			  method = RequestMethod.GET, 
			  headers = "Accept=application/json")
	@ResponseBody
	public String usersCount() {
		logger.info("Prepering user counter.");
		Long userCounter = userRepository.count();
		String outputMsg ="";
		if(userCounter>0) {
			outputMsg = "There are " + userCounter +" users in database";
		} else {
			outputMsg = "There are no users in database.";
		}
		logger.info("End of usersCont().");
		return outputMsg;
	}
	
	@GetMapping
	@RequestMapping(
			  value = "/oldestUser", 
			  method = RequestMethod.GET, 
			  headers = "Accept=application/json")
	@ResponseBody
	public String oldestUser() {
		logger.info("Searching oldest user..." );
		List<User> usersSortedByAge=userRepository.findAllByPhoneNumberPresentAndSort(0,Sort.by("birthDate").ascending());
		User oldestUser = null;
		String oldestUserMSG= "There are no users or users with phone number in database.";
		int age = 0;
		if(!usersSortedByAge.isEmpty()) {
			oldestUser = usersSortedByAge.get(0);
			        if ((oldestUser.getBirthDate() != null)) {
			        	age= Period.between(oldestUser.getBirthDate(), LocalDate.now()).getYears();
			        } 
			oldestUserMSG = "Oldest user with phone number in database is: "+ oldestUser.getFirstName()+", "+ oldestUser.getLastName() + ", "
					+ oldestUser.getBirthDate().toString() +" ("+age+" years old), "+ oldestUser.getPhoneNumber();
		}
		logger.info(oldestUserMSG);
		return oldestUserMSG;
	}
	
	@GetMapping
	@RequestMapping(
			  value = "/usersSortedByAge", 
			  method = RequestMethod.GET, 
			  headers = "Accept=application/json")
	@ResponseBody
	public String usersSortedByAge(Model model) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		logger.info("Starting usersSortedByAge()" );
		Page<User> pages = null;
		Pageable pageable = PageRequest.of(0, 5, Sort.by("birthDate").ascending());
		SortedMap<String,String> modelMap = new TreeMap<>();
		while(true) {
			pages = findAll(pageable);
			logger.info("Preparing page"+ (pages.getNumber() +1)+ " of " + pages.getTotalPages() );
			final Page<UserDTO> pageWithUserWithAge = pages.map(this::convertToUserDTO);
			model.addAttribute("number", pages.getNumber());
			model.addAttribute("totalPages", pages.getTotalPages());
			model.addAttribute("totalElements", pages.getTotalElements());
			model.addAttribute("size", pages.getSize());
			model.addAttribute("users", pageWithUserWithAge.getContent());
			modelMap.put(""+(pages.getNumber()+1), model.toString());
			if(!pages.hasNext())
				break;
			pageable = pages.nextPageable();
		}
		logger.info("All pages with users sorted by age sent");
		return modelMap.toString();
	}

	private Page<User> findAll(Pageable pageable) {
		return userRepository.findAll(pageable);
	}
	private UserDTO convertToUserDTO(User user) {
		int age= Period.between(user.getBirthDate(), LocalDate.now()).getYears();
		return new UserDTO(user.getFirstName(), user.getLastName(), user.getBirthDate(), user.getPhoneNumber(),age);
	}
	
	@GetMapping 
	@RequestMapping("/getUser/{lastName}")
	@ResponseBody
	public User findUserByName(@PathVariable String lastName) {
		logger.info("Prepering to find user by name.");
		User user = userRepository.findUserByLastName(lastName.toUpperCase());
		if(user==null) {
			logger.info("No user in database with name: " + lastName);
			user = new User();
		}
		return user;
	}
}
