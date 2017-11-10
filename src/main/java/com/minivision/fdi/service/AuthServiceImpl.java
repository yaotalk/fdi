package com.minivision.fdi.service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minivision.fdi.entity.Permission;
import com.minivision.fdi.entity.Role;
import com.minivision.fdi.repository.PermissionRepository;
import com.minivision.fdi.repository.RoleRepository;

@Service
public class AuthServiceImpl extends BaseService implements AuthService{
  
  @Autowired
  private RoleRepository roleRepository;
  
  @Autowired
  private PermissionRepository permissionRepository;

  @Override
  public Role saveOrUpdateRole(Role role, String permIds) {
    //Role role = getRole(id);
    //Assert.notNull(role, "Role[id = "+id+"] not exist.");
    //role.setNickName(nickName);
    String[] ids = StringUtils.split(permIds, ",");
    if(ids != null){
      Set<Long> idList = Arrays.stream(ids).map(i -> Long.valueOf(i)).collect(Collectors.toSet());
      role.setPermissions(getPermssions(idList));
    }
    return roleRepository.saveAndFlush(role);
  }

  @Override
  public void deleteRole(long id) {
    roleRepository.delete(id);
  }

  @Override
  public Role getRole(long id) {
    return roleRepository.findOne(id);
  }

  private List<Permission> getPermssions(Iterable<Long> ids) {
    return permissionRepository.findAll(ids);
  }

  @Override
  public List<Role> getRoles() {
    return roleRepository.findAll();
  }

  @Override
  public List<Permission> getPermssions() {
    return permissionRepository.findAll();
  }
}
