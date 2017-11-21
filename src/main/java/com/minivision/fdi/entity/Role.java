package com.minivision.fdi.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "t_role")
@ApiModel
public class Role implements GrantedAuthority {
  private static final long serialVersionUID = 6263094516846594656L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(hidden = true)
  protected Long id;
  private String name; 
  private String nickName;
  @Column(length = 20)
  private String description;
  
  @ManyToMany(targetEntity = Permission.class, fetch = FetchType.EAGER)
  @ApiModelProperty(hidden = true)
  private List<Permission> permissions = new ArrayList<>();
  
  @Override
  public String getAuthority() {
    return "ROLE_"+name;
  }

}
