package com.csvReader.csvreader.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.csvReader.csvreader.model.User;

public interface UserRepository extends JpaRepository<User, Integer>,  PagingAndSortingRepository<User, Integer> {

	@Query
	User findUserByLastName (@Param ("lastName") String lastName);
	
	@Query("Select u from User u where phoneNumber > 0")
	List<User> findAllByPhoneNumberPresentAndSort(int nmb, Sort sort);
}
