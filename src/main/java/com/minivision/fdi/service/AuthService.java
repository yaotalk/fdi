package com.minivision.fdi.service;

import java.util.List;

import com.minivision.fdi.entity.Permission;
import com.minivision.fdi.entity.Role;

public interface AuthService {
  void deleteRole(long id);
  Role getRole(long id);
  List<Role> getRoles();
  List<Permission> getPermssions();
  Role saveOrUpdateRole(Role role, String permIds);
}
