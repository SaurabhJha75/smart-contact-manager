package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.model.Contact;
import com.smart.model.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	//pagination
	
	//currentPage=page
	//contact per page = 5
	@Query("from Contact as c where c.user.id =:userId"  )
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pePageable);
	
	//for searching contacts
	public List<Contact> findByNameContainingAndUser(String name, User user);
}
