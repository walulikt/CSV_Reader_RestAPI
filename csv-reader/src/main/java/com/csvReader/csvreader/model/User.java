package com.csvReader.csvreader.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
	
	@Id
	private String firstName;
	private String lastName;
	private LocalDate birthDate;
	private Integer phoneNumber;
	
	public User(String firstName, String lastName, LocalDate birthDate, Integer phoneNumber) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.phoneNumber = phoneNumber;
	}
	
	public User () {}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String csvBirthDate) throws ParseException {
		if(csvBirthDate==null || csvBirthDate.isEmpty() ) {
			this.birthDate = null;
		} else {
			Date bDate = new SimpleDateFormat("yyyy.MM.dd").parse(csvBirthDate.trim());
			LocalDate birthDate = bDate.toInstant()
			      .atZone(ZoneId.systemDefault())
			      .toLocalDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.M.dd");
			String formattedDate = birthDate.format(formatter);
			birthDate = LocalDate.parse(formattedDate, formatter);  
			this.birthDate = birthDate;
		};
	}

	public Integer getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumberString) {
		if(phoneNumberString==null || phoneNumberString.replaceAll("[^\\d.]", "").trim().isEmpty()) {
			this.phoneNumber = 0;
		} else {
			Integer phoneNumber = Integer.parseInt(phoneNumberString);
			this.phoneNumber = phoneNumber;
		};
	}

	@Override
	public String toString() {
		return "User [firstName=" + firstName + ", lastName=" + lastName + ", birthDate=" + birthDate + ", phoneNumber="
				+ phoneNumber + "]";
	}
}
