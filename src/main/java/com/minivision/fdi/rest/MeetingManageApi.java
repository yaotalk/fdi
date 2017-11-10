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
import com.minivision.fdi.entity.MeetingManage;
import com.minivision.fdi.exception.ServiceException;
import com.minivision.fdi.rest.param.CreateMeetingManageParam;
import com.minivision.fdi.rest.param.QueryMeetingManageParam;
import com.minivision.fdi.rest.param.UpdateMeetingManageParam;
import com.minivision.fdi.rest.result.common.PageResult;
import com.minivision.fdi.rest.result.common.RestResult;
import com.minivision.fdi.service.MeetingManageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "api/v1/meeting", method = RequestMethod.POST)
@Api(tags = "MeetingApi", value = "IoT Platform Meeting Apis")
@Slf4j
public class MeetingManageApi {

  @Autowired
  private MeetingManageService meetingService;
  
  @RequestMapping(value = "createMeeting")
  @ApiOperation(value = "新增会议", notes = "新增会议", hidden = true)
  @Log(module = "会议管理", operation = "新增会议")
  public RestResult<MeetingManage> createMeeting(@Valid @ModelAttribute CreateMeetingManageParam param, BindingResult errResult) {
    if (errResult.hasErrors()) {
      List<ObjectError> errorList = errResult.getAllErrors();
      for(ObjectError error : errorList){
        log.error(error.getDefaultMessage());
      }
      throw new ServiceException(errorList.get(0).getDefaultMessage());
    }
    
    MeetingManage created = null;
    try {
      created = meetingService.createMeeting(param);
    } catch (Throwable e) {
      log.error("新增会议失败", e);
      throw new ServiceException((e instanceof DataIntegrityViolationException ? "会议已存在" : "新增会议失败"));
    }
    
    return new RestResult<>(created);
  }
  
  @RequestMapping(value = "updateMeeting")
  @ApiOperation(value = "修改会议信息", notes = "修改会议信息", hidden = true)
  @Log(module = "会议管理", operation = "修改会议信息")
  public RestResult<MeetingManage> updateMeeting(@Valid @ModelAttribute UpdateMeetingManageParam param, BindingResult errResult) {
    if (errResult.hasErrors()) {
      List<ObjectError> errorList = errResult.getAllErrors();
      for(ObjectError error : errorList){
        log.error(error.getDefaultMessage());
      }
      throw new ServiceException(errorList.get(0).getDefaultMessage());
    }
    
    MeetingManage updated = null;
    try {
      updated = meetingService.updateMeeting(param);
    } catch (Throwable e) {
      log.error("修改会议信息失败", e);
      throw new ServiceException("修改会议信息失败");
    }
    
    return new RestResult<>(updated);
  }
  
  @RequestMapping(value = "deleteMeeting")
  @ApiOperation(value = "删除会议", notes = "删除会议", hidden = true)
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "id", value = "会议ID", paramType = "query", dataType = "long", required = true)
  })
  @Log(module = "会议管理", operation = "删除会议")
  public RestResult<String> deleteMeeting(@RequestParam("id") long id) {
    if (id <= 0) {
      throw new ServiceException("会议ID无效");
    }

    try {
      meetingService.deleteMeeting(id);
    } catch (Throwable e) {
      log.error("删除会议失败", e);
      throw new ServiceException("删除会议失败");
    }
    
    return new RestResult<>("");
  }
  
  @RequestMapping(value = "deleteBatch")
  @ApiOperation(value = "批量删除会议", notes = "批量删除会议", hidden = true)
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "meetingIds", value = "会议ID，多个值以英文逗号分隔", paramType = "query", dataType = "String", required = true)
  })
  @Log(module = "会议管理", operation = "批量删除会议")
  public RestResult<String> deleteBatch(@RequestParam("meetingIds") String meetingIds) {
    if (!StringUtils.hasText(meetingIds)) {
      throw new ServiceException("请指定要删除的会议ID！");
    }
    
    try {
      meetingService.deleteBatch(StringUtils.commaDelimitedListToSet(meetingIds).stream().map(s -> NumberUtils.parseNumber(s, Long.class)).collect(Collectors.toList()));
    } catch (Throwable e) {
      log.error("批量删除会议失败", e);
      throw new ServiceException("批量删除会议失败");
    }
    
    return new RestResult<>("");
  }
  
  @RequestMapping(value = "meetingList")
  @ApiOperation(value = "获取会议列表", notes = "获取会议列表", hidden = true)
  @Log(module = "会议管理", operation = "查询会议")
  public RestResult<PageResult<MeetingManage>> meetingList(@Valid @ModelAttribute QueryMeetingManageParam param, BindingResult errResult) {
    if (errResult.hasErrors()) {
      List<ObjectError> errorList = errResult.getAllErrors();
      for(ObjectError error : errorList){
        log.error(error.getDefaultMessage());
      }
      throw new ServiceException(errorList.get(0).getDefaultMessage());
    }

    PageResult<MeetingManage> result = new PageResult<>();
    try {
      Page<MeetingManage> page = meetingService.findByPage(param);
      if (page != null && page.hasContent()) {
        result.setPages(page.getTotalPages());
        result.setTotal(page.getTotalElements());
        result.setRows(page.getContent());
      }
    } catch (Throwable e) {
      log.error("获取会议列表失败", e);
      throw new ServiceException("获取会议列表失败");
    }
    
    return new RestResult<>(result);
  }
  
}
