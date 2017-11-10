package com.minivision.fdi.rest;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.minivision.fdi.entity.OpLog;
import com.minivision.fdi.exception.ServiceException;
import com.minivision.fdi.rest.param.OpLogParam;
import com.minivision.fdi.rest.result.common.PageResult;
import com.minivision.fdi.rest.result.common.RestResult;
import com.minivision.fdi.service.OpLogService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "api/v1/oplog", method = RequestMethod.POST)
@Api(tags = "OpLogApi", value = "IoT Platform OpLog Apis")
@Slf4j
public class OpLogApi {

  @Autowired
  private OpLogService opLogService;

  @RequestMapping("opLogs")
  @ApiOperation(value = "获取日志列表", notes = "获取日志列表")
  public RestResult<PageResult<OpLog>> findOpLogs(@Valid @ModelAttribute OpLogParam param, BindingResult errResult) {
    if (errResult.hasErrors()) {
      List<ObjectError> errorList = errResult.getAllErrors();
      for(ObjectError error : errorList){
        log.error(error.getDefaultMessage());
      }
      throw new ServiceException(errorList.get(0).getDefaultMessage());
    }
    
    if (StringUtils.isBlank(param.getStartTime())) {
      param.setStartTime("1900-01-01 00:00:00");
    } else {
      param.setStartTime(param.getStartTime() + " 00:00:00");
    }
    if (StringUtils.isBlank(param.getEndTime())) {
      param.setEndTime("2999-12-31 23:59:59");
    } else {
      param.setEndTime(param.getEndTime() + " 23:59:59");
    }
    
    PageResult<OpLog> result = new PageResult<>();
    try {
      Page<OpLog> pageResult = opLogService.findByUsernameAndOpTimeBetween(param);
      if (pageResult != null && pageResult.hasContent()) {
        result.setPages(pageResult.getTotalPages());
        result.setTotal(pageResult.getTotalElements());
        result.setRows(pageResult.getContent());
      }
    } catch (Throwable e) {
      log.error("获取日志列表失败", e);
      throw new ServiceException("获取日志列表失败");
    }

    return new RestResult<>(result);
  }

}
