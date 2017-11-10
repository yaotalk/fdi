package com.minivision.fdi.rest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minivision.fdi.entity.Permission;
import com.minivision.fdi.entity.Role;
import com.minivision.fdi.entity.User;

@RestController
@RequestMapping(value = "api/v1/user")
@PreAuthorize("isAuthenticated()")
public class UserApi {
  
  @GetMapping("/authority")
  public Collection<? extends GrantedAuthority> getAuthority(){
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return user.getAuthorities();
  }
  
  @GetMapping("/permssion")
  public Collection<Permission> getPermssions(){
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Set<Permission> perm = new HashSet<>();
    for(Role role : user.getRoles()) {
      perm.addAll(role.getPermissions());
    }
    return perm;
  }
  
  @GetMapping("/role")
  public Collection<Role> getRoles(){
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return user.getRoles();
  }
  
}
