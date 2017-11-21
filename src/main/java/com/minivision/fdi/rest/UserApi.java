package com.minivision.fdi.rest;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minivision.fdi.annotation.Log;
import com.minivision.fdi.entity.Permission;
import com.minivision.fdi.entity.Role;
import com.minivision.fdi.entity.User;
import com.minivision.fdi.entity.service.UserService;
import com.minivision.fdi.rest.param.PageParam;
import com.minivision.fdi.rest.result.common.PageResult;
import com.minivision.fdi.rest.result.common.RestResult;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "api/v1/user")
public class UserApi {
  @Autowired
  private UserService userService;
  
  @Autowired
  private SessionRegistry sessionRegistry;
  
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/authority")
  public Collection<? extends GrantedAuthority> getAuthority(){
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return user.getAuthorities();
  }
  
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/permssion")
  public Collection<Permission> getPermssions(){
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Set<Permission> perm = new HashSet<>();
    for(Role role : user.getRoles()) {
      perm.addAll(role.getPermissions());
    }
    return perm;
  }
  
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/role")
  public Collection<Role> getRoles(){
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return user.getRoles();
  }
  
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/updatePwd")
  @ApiOperation("当前用户修改自己密码")
  public RestResult<Boolean> updatePwd(String oldPwd, String newPwd){
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    boolean updatePwd = userService.updatePwd(user, oldPwd, newPwd);
    return new RestResult<Boolean>(updatePwd);
  }
  
  @PreAuthorize("hasAuthority('MG_ROLE_EDIT')")
  @PostMapping()
  @ApiOperation("创建新账户")
  @Log(module = "权限管理", operation = "创建新账户")
  public RestResult<User> createUser(@Valid User user, String password, String roleIds){
    user.setRawPassword(password);
    User u = userService.saveOrUpdateUser(user,roleIds);
    return new RestResult<>(u);
  }
  
  @PreAuthorize("hasAuthority('MG_ROLE_EDIT')")
  @PutMapping("/{id}")
  @ApiOperation("更新账户信息")
  @Log(module = "权限管理", operation = "更新账户信息")
  public RestResult<User> updateUser(@Valid @ModelAttribute("user") User user, @PathVariable(value="id") long id, String password, String roleIds){
    Assert.notNull(user, "User[id="+id+"] not exist");
    user.setRawPassword(password);
    User u = userService.saveOrUpdateUser(user, roleIds);
    
    if(!user.isEnabled() || !StringUtils.isEmpty(password)){
      List<SessionInformation> sessions = sessionRegistry.getAllSessions(user, false);
      for(SessionInformation s: sessions){
        s.expireNow();
      }
    }
    
    return new RestResult<User>(u);
  }
  
  @ModelAttribute
  public void loadUser(@PathVariable(value="id", required=false) Long id, Model model){
      if(null != id){
         User user = userService.get(id);
         model.addAttribute("user", user);
      }
  }
  
  @PreAuthorize("hasAuthority('MG_ROLE_EDIT')")
  @DeleteMapping("/{id}")
  @ApiOperation("删除帐号")
  @Log(module = "权限管理", operation = "删除帐号")
  public RestResult<Role> delete(@PathVariable("id") long id, @ModelAttribute("user") @ApiIgnore User user){
    userService.delete(id);
    List<SessionInformation> sessions = sessionRegistry.getAllSessions(user, false);
    for(SessionInformation s: sessions){
      s.expireNow();
    }
    return new RestResult<>();
  }
  
  @PreAuthorize("hasAuthority('MG_ROLE_QUERY') or (#id == principal.id)")
  @GetMapping("/{id}")
  @ApiOperation("获取账户信息")
  @Log(module = "权限管理", operation = "获取账户信息")
  public RestResult<User> getUser(@PathVariable("id") long id, @ModelAttribute("user") @ApiIgnore User user){
    return new RestResult<User>(user);
  }
  
  @PreAuthorize("hasAuthority('MG_ROLE_QUERY')")
  @GetMapping()
  @ApiOperation("账户列表")
  @Log(module = "权限管理", operation = "查询账户列表")
  public RestResult<PageResult<User>> getUsers(PageParam page){
    //Page<User> p = userService.list(page.getOffset(), page.getLimit());
    Page<User> p = userService.listExcludeSuper(page.getOffset(), page.getLimit());
    return new RestResult<>(new PageResult<>(p));
  }
  
}
