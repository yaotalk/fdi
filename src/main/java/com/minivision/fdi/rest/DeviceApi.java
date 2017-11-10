package com.minivision.fdi.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minivision.fdi.annotation.Log;
import com.minivision.fdi.entity.Device;
import com.minivision.fdi.exception.ServiceException;
import com.minivision.fdi.rest.param.CreateDeviceParam;
import com.minivision.fdi.rest.param.QueryDeviceParam;
import com.minivision.fdi.rest.param.UpdateDeviceParam;
import com.minivision.fdi.rest.result.common.PageResult;
import com.minivision.fdi.rest.result.common.RestResult;
import com.minivision.fdi.service.DeviceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "api/v1/device", method = RequestMethod.POST)
@Api(tags = "DeviceApi", value = "IoT Platform Device Apis")
@Slf4j
public class DeviceApi {

  @Autowired
  private DeviceService deviceService;
  
  @RequestMapping(value = "createDevice")
  @ApiOperation(value = "新增设备", notes = "新增设备")
  @Log(module = "设备管理", operation = "新增设备")
  public RestResult<Device> createDevice(@Valid @ModelAttribute CreateDeviceParam param, BindingResult errResult) {
    if (errResult.hasErrors()) {
      List<ObjectError> errorList = errResult.getAllErrors();
      for(ObjectError error : errorList){
        log.error(error.getDefaultMessage());
      }
      throw new ServiceException(errorList.get(0).getDefaultMessage());
    }
    
    Device created = null;
    try {
      created = deviceService.createDevice(param);
    } catch (Throwable e) {
      log.error("新增设备失败", e);
      throw new ServiceException((e instanceof DataIntegrityViolationException ? "设备已存在" : "新增设备失败"));
    }
    
    return new RestResult<>(created);
  }
  
  @RequestMapping(value = "updateDevice")
  @ApiOperation(value = "修改设备信息", notes = "修改设备信息")
  @Log(module = "设备管理", operation = "修改设备信息")
  public RestResult<Device> updateDevice(@Valid @ModelAttribute UpdateDeviceParam param, BindingResult errResult) {
    if (errResult.hasErrors()) {
      List<ObjectError> errorList = errResult.getAllErrors();
      for(ObjectError error : errorList){
        log.error(error.getDefaultMessage());
      }
      throw new ServiceException(errorList.get(0).getDefaultMessage());
    }
    
    Device updated = null;
    try {
      updated = deviceService.updateDevice(param);
    } catch (Throwable e) {
      log.error("修改设备信息失败", e);
      throw new ServiceException("修改设备信息失败");
    }
    
    return new RestResult<>(updated);
  }
  
  @RequestMapping(value = "deleteDevice")
  @ApiOperation(value = "删除设备", notes = "删除设备")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "id", value = "设备ID", paramType = "query", dataType = "long", required = true)
  })
  @Log(module = "设备管理", operation = "删除设备")
  public RestResult<String> deleteDevice(@RequestParam("id") long id) {
    if (id <= 0) {
      throw new ServiceException("设备ID无效");
    }

    try {
      deviceService.deleteDevice(id);
    } catch (Throwable e) {
      log.error("删除设备失败", e);
      throw new ServiceException("删除设备失败");
    }
    
    return new RestResult<>("");
  }
  
  @RequestMapping(value = "deleteBatch")
  @ApiOperation(value = "批量删除设备", notes = "批量删除设备")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "deviceIds", value = "设备ID，多个值以英文逗号分隔", paramType = "query", dataType = "String", required = true)
  })
  @Log(module = "设备管理", operation = "批量删除设备")
  public RestResult<String> deleteBatch(@RequestParam("deviceIds") String deviceIds) {
    if (!StringUtils.hasText(deviceIds)) {
      throw new ServiceException("请指定要删除的设备ID！");
    }
    
    try {
      deviceService.deleteBatch(StringUtils.commaDelimitedListToSet(deviceIds).stream().map(s -> NumberUtils.parseNumber(s, Long.class)).collect(Collectors.toList()));
    } catch (Throwable e) {
      log.error("批量删除设备失败", e);
      throw new ServiceException("批量删除设备失败");
    }
    
    return new RestResult<>("");
  }
  
  @RequestMapping(value = "deviceList")
  @ApiOperation(value = "获取设备列表", notes = "获取设备列表")
  @Log(module = "设备管理", operation = "查询设备")
  public RestResult<PageResult<Device>> deviceList(@Valid @ModelAttribute QueryDeviceParam param, BindingResult errResult) {
    if (errResult.hasErrors()) {
      List<ObjectError> errorList = errResult.getAllErrors();
      for(ObjectError error : errorList){
        log.error(error.getDefaultMessage());
      }
      throw new ServiceException(errorList.get(0).getDefaultMessage());
    }

    PageResult<Device> result = new PageResult<>();
    try {
      Page<Device> page = deviceService.findByPage(param);
      if (page != null && page.hasContent()) {
        result.setPages(page.getTotalPages());
        result.setTotal(page.getTotalElements());
        result.setRows(page.getContent());
      }
    } catch (Throwable e) {
      log.error("获取设备列表失败", e);
      throw new ServiceException("获取设备列表失败");
    }
    
    return new RestResult<>(result);
  }
  
}
