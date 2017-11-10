package com.minivision.fdi.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import com.minivision.ai.domain.IdEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity  
@Table(name = "t_permission")
public class Permission extends IdEntity implements GrantedAuthority {
  private static final long serialVersionUID = -2110367552097830924L;
  private String name; 
  private String nickName;
  
  @Override
  public String getAuthority() {
    return name;
  }
  
}
