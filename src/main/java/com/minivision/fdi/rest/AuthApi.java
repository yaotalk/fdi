package com.minivision.fdi.rest;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.minivision.fdi.rest.result.common.RestResult;
import com.minivision.fdi.service.AuthService;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "api/v1/auth")
public class AuthApi {
  
  @Autowired
  private AuthService authService;

  @PreAuthorize("hasAuthority('MG_ROLE_EDIT')")
  @PostMapping("/role")
  @ApiOperation("创建新角色")
  @Log(module = "权限管理", operation = "创建新角色")
  public RestResult<Role> createRole(@Valid Role role, String permIds){
    Role createRole = authService.saveOrUpdateRole(role, permIds);
    return new RestResult<>(createRole);
  }
  
  @PreAuthorize("hasAuthority('MG_ROLE_EDIT')")
  @PutMapping("/role/{id}")
  @ApiOperation("更新角色信息")
  @Log(module = "权限管理", operation = "更新角色信息")
  public RestResult<Role> updateRole(@PathVariable(value="id") long id, @Valid @ModelAttribute("role") Role role, String permIds){
    Assert.notNull(role, "Role[id="+id+"] not exist");
    Role r = authService.saveOrUpdateRole(role, permIds);
    return new RestResult<Role>(r);
  }
  
  @ModelAttribute
  public void loadRole(@PathVariable(value="id", required=false) Long id, Model model){
      if(null != id){
         Role role = authService.getRole(id);
         model.addAttribute("role", role);
      }
  }
  
  @PreAuthorize("hasAuthority('MG_ROLE_EDIT')")
  @DeleteMapping("/role/{id}")
  @ApiOperation("删除角色")
  @Log(module = "权限管理", operation = "删除角色")
  public RestResult<Role> deleteRole(@PathVariable("id") long id){
    authService.deleteRole(id);
    return new RestResult<>();
  }
  
  @PreAuthorize("hasAuthority('MG_ROLE_QUERY')")
  @GetMapping("/role/{id}")
  @ApiOperation("获取角色信息")
  @Log(module = "权限管理", operation = "获取角色信息")
  public RestResult<Role> getRole(@PathVariable("id") long id, @ApiIgnore @ModelAttribute("role") Role role){
    return new RestResult<Role>(role);
  }
  
  @PreAuthorize("hasAuthority('MG_ROLE_QUERY')")
  @GetMapping("/roles")
  @ApiOperation("列出系统所有角色列表")
  @Log(module = "权限管理", operation = "列出系统所有角色列表")
  public RestResult<List<Role>> getRole(){
    List<Role> roles = authService.getRoles();
    return new RestResult<>(roles);
  }
  
  @PreAuthorize("hasAuthority('MG_ROLE_QUERY')")
  @GetMapping("/permissions")
  @ApiOperation("列出系统所有权限列表")
  @Log(module = "权限管理", operation = "列出系统所有权限列表")
  public RestResult<List<Permission>> listPermissions(){
    List<Permission> permssions = authService.getPermssions();
    return new RestResult<>(permssions);
  }
  
}
