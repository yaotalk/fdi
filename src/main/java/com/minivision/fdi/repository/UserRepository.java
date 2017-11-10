package com.minivision.fdi.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.minivision.fdi.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User,Long>{

  User findByUsername(String username);
}
