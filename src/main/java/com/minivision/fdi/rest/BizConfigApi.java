package com.minivision.fdi.rest;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.minivision.fdi.annotation.Log;
import com.minivision.fdi.entity.BizConfig;
import com.minivision.fdi.exception.ServiceException;
import com.minivision.fdi.rest.param.CreateBizConfigParam;
import com.minivision.fdi.rest.param.QueryBizConfigParam;
import com.minivision.fdi.rest.param.UpdateBizConfigParam;
import com.minivision.fdi.rest.result.common.RestResult;
import com.minivision.fdi.service.BizConfigService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "api/v1/config", method = RequestMethod.POST)
@Api(tags = "BizConfigApi", value = "IoT Platform BizConfig Apis")
@Slf4j
public class BizConfigApi {

  @Autowired
  private BizConfigService bizConfigService;
  
  @RequestMapping(value = "createBizConfig")
  @ApiOperation(value = "新增配置", notes = "新增配置")
  @Log(module = "配置管理", operation = "新增配置")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "img", paramType = "form", dataType = "file"),
    @ApiImplicitParam(name = "audio", paramType = "form", dataType = "file")
  })
  public RestResult<BizConfig> createBizConfig(@Valid @ModelAttribute CreateBizConfigParam param, BindingResult errResult) {
    Assert.isTrue(StringUtils.hasText(param.getMeetingToken()) || StringUtils.hasText(param.getDeviceSn()), "配置关联实体ID和设备编号两者必须有一个有值");
    Assert.state(!(StringUtils.hasText(param.getMeetingToken()) && StringUtils.hasText(param.getDeviceSn())), "配置关联实体ID和设备编号两者不能同时有值");

    if (errResult.hasErrors()) {
      List<ObjectError> errorList = errResult.getAllErrors();
      for(ObjectError error : errorList){
        log.error(error.getDefaultMessage());
      }
      
      throw new ServiceException(errorList.get(0).getDefaultMessage());
    }
    
    BizConfig created = null;
    try {
      created = bizConfigService.createBizConfig(param);
    } catch (Throwable e) {
      log.error("新增配置失败", e);
      throw new ServiceException("新增配置失败");
    }
    
    return new RestResult<>(created);
  }
  
  @RequestMapping(value = "updateBizConfig")
  @ApiOperation(value = "修改配置", notes = "修改配置")
  @Log(module = "配置管理", operation = "修改配置")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "img", paramType = "form", dataType = "file"),
    @ApiImplicitParam(name = "audio", paramType = "form", dataType = "file")
  })
  public RestResult<BizConfig> updateBizConfig(@Valid @ModelAttribute UpdateBizConfigParam param, BindingResult errResult) {
    if (errResult.hasErrors()) {
      List<ObjectError> errorList = errResult.getAllErrors();
      for(ObjectError error : errorList){
        log.error(error.getDefaultMessage());
      }
      throw new ServiceException(errorList.get(0).getDefaultMessage());
    }
    
    BizConfig updated = null;
    try {
      updated = bizConfigService.updateBizConfig(param);
    } catch (Throwable e) {
      log.error("修改配置失败", e);
      throw new ServiceException("修改配置失败");
    }
    
    return new RestResult<>(updated);
  }
  
  @RequestMapping(value = "findBizConfig")
  @ApiOperation(value = "查询配置", notes = "查询配置")
  @Log(module = "配置管理", operation = "查询配置")
  public RestResult<BizConfig> findBizConfig(@Valid @ModelAttribute QueryBizConfigParam param, BindingResult errResult) {
    Assert.isTrue(StringUtils.hasText(param.getMeetingToken()) || StringUtils.hasText(param.getDeviceSn()), "配置关联实体ID和设备编号两者必须有一个有值");
    Assert.state(!(StringUtils.hasText(param.getMeetingToken()) && StringUtils.hasText(param.getDeviceSn())), "配置关联实体ID和设备编号两者不能同时有值");

    if (errResult.hasErrors()) {
      List<ObjectError> errorList = errResult.getAllErrors();
      for(ObjectError error : errorList){
        log.error(error.getDefaultMessage());
      }
      throw new ServiceException(errorList.get(0).getDefaultMessage());
    }
    
    BizConfig existed = null;
    try {
      existed = bizConfigService.findConfig(param);
    } catch (Throwable e) {
      log.error("查询配置失败", e);
      throw new ServiceException("查询配置失败");
    }
    
    if (existed == null) {
      throw new ServiceException("配置不存在");
    } 
    return new RestResult<>(existed);
  }
  
}
