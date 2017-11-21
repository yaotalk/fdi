package com.minivision.fdi.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.minivision.fdi.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User,Long>{

  User findByUsername(String username);
  
  Page<User> findByIdNot(Long id, Pageable page);
}
