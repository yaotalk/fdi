package com.minivision.fdi.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minivision.fdi.service.AuthService;
import com.minivision.fdi.util.SpringContextUtil;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity  
@Table(name = "t_user")
@ApiModel
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty(hidden = true)
  protected Long id;

  private static final long serialVersionUID = 7784904636048190623L;
  @Column(unique = true,nullable = false)
  private String username;
  
  @ApiModelProperty(hidden = true)
  @JsonIgnore
  private String password;
  
  @ApiModelProperty(hidden = true)
  @JsonIgnore
  private transient String rawPassword;
  
  private boolean enabled = true;
  
  @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
  //@JoinTable(name = "m_user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
  @ApiModelProperty(hidden = true)
  private List<Role> roles;
  
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(hidden = true)
  private Date createTime;
  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(hidden = true)
  private Date updateTime;

  @Override
  @JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> auth = new HashSet<>();
    if(this.id == 1){
      auth.add(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));
      AuthService authService = SpringContextUtil.getBean(AuthService.class);
      auth.addAll(authService.getPermssions());
      return auth;
    }
    
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
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }


  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }


  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }


  @Override
  public boolean isEnabled() {
    return enabled;
  }


  public List<Role> getRoles() {
    return roles;
  }


  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }


  public Date getCreateTime() {
    return createTime;
  }


  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }


  public Date getUpdateTime() {
    return updateTime;
  }


  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }


  public Long getId() {
    return id;
  }


  public void setUsername(String username) {
    this.username = username;
  }


  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getRawPassword() {
    return rawPassword;
  }


  public void setRawPassword(String rawPassword) {
    this.rawPassword = rawPassword;
  }


  public void setPassword(String password) {
    this.password = password;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    User other = (User) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  
}
