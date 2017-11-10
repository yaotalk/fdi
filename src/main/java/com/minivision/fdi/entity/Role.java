package com.minivision.fdi.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;

import com.minivision.ai.domain.IdEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "t_role")
public class Role extends IdEntity implements GrantedAuthority {
  private static final long serialVersionUID = 6263094516846594656L;
  private String name; 
  private String nickName;
  @Column(length = 20)
  private String description;
  
  @ManyToMany(targetEntity = Permission.class, fetch = FetchType.EAGER)  
  //@JoinTable(name = "m_role_permission", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "permission_id")})  
  private List<Permission> permissions = new ArrayList<>();
  
  @Override
  public String getAuthority() {
    return "ROLE_"+name;
  }

}
