package com.minivision.fdi.entity.service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.minivision.ai.util.ChunkRequest;
import com.minivision.fdi.entity.Role;
import com.minivision.fdi.entity.User;
import com.minivision.fdi.repository.RoleRepository;
import com.minivision.fdi.repository.UserRepository;

@Service
public class UserService implements UserDetailsService{
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private RoleRepository roleRepository;
  
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if(user == null){
      throw new UsernameNotFoundException("user:["+username+"] not exist");
    }
    return user;
  }
  
  
  public boolean updatePwd(User user, String oldPwd, String newPwd){
    boolean matches = passwordEncoder.matches(oldPwd, user.getPassword());
    if(matches){
      user.setPassword(passwordEncoder.encode(newPwd));
      userRepository.save(user);
    }
    return matches;
  }

  public User saveOrUpdateUser(User user, String roleIds){
    String[] ids = StringUtils.split(roleIds, ",");
    if(ids != null){
      Set<Long> idList = Arrays.stream(ids).map(i -> Long.valueOf(i)).collect(Collectors.toSet());
      user.setRoles(getRoles(idList));
    }
    if(StringUtils.isNoneBlank(user.getRawPassword())){
      user.setPassword(passwordEncoder.encode(user.getRawPassword()));
    }
    return userRepository.save(user);
  }
  
  private List<Role> getRoles(Iterable<Long> ids) {
    return roleRepository.findAll(ids);
  }

  public void delete(long id){
    userRepository.delete(id);
  }
  
  public User get(long id){
    return userRepository.findOne(id);
  }
  
  public List<User> listAll(){
    return (List<User>) userRepository.findAll();
  }
  
  public Page<User> list(int offset, int limit){
    ChunkRequest chunkRequest = new ChunkRequest(offset, limit);
    return userRepository.findAll(chunkRequest);
  }
  
  public Page<User> listExcludeSuper(int offset, int limit){
    ChunkRequest chunkRequest = new ChunkRequest(offset, limit);
    return userRepository.findByIdNot(1l, chunkRequest);
  }
}
