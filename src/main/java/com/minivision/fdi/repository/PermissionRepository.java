package com.minivision.fdi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minivision.fdi.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
  
}
