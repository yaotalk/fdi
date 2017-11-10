package com.minivision.fdi.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.minivision.ai.domain.IdEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity  
@Table(name = "t_user")
public class User extends IdEntity implements UserDetails{
  private static final long serialVersionUID = 7784904636048190623L;
  @Column(unique = true,nullable = false)
  private String username;
  private String password;
  private boolean enabled = true;
  
  @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
  //@JoinTable(name = "m_user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
  private Set<Role> roles;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> auth = new HashSet<>();
    for(Role role : roles) {
      auth.add(role);
      auth.addAll(role.getPermissions());
    }
    return auth;
  }


  @Override
  public String getPassword() {
    return password;
  }


  @Override
  public String getUsername() {
    return username;
  }


  @Override
  public boolean isAccountNonExpired() {
    return true;
  }


  @Override
  public boolean isAccountNonLocked() {
    return true;
  }


  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }


  @Override
  public boolean isEnabled() {
    return enabled;
  }



}
