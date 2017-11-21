package com.minivision.fdi.rest;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minivision.fdi.annotation.Log;
import com.minivision.fdi.entity.MeetingManage;
import com.minivision.fdi.entity.Stats;
import com.minivision.fdi.exception.ServiceException;
import com.minivision.fdi.rest.param.CreateStatsParam;
import com.minivision.fdi.rest.param.QueryMeetingManageParam;
import com.minivision.fdi.rest.param.QueryStatsParam;
import com.minivision.fdi.rest.result.FaceRecognitionResult;
import com.minivision.fdi.rest.result.common.PageResult;
import com.minivision.fdi.rest.result.common.RestResult;
import com.minivision.fdi.service.MeetingManageService;
import com.minivision.fdi.service.StatsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "api/v1/stats", method = RequestMethod.POST)
@Api(tags = "StatsApi", value = "IoT Platform Stats Apis")
@Slf4j
public class StatsApi {

  @Autowired
  private StatsService statsService;
  
  @Autowired
  private MeetingManageService meetingService;
  
  @Autowired
  private ObjectMapper mapper;
  
  @RequestMapping(value = "createStats")
  @ApiOperation(value = "人脸识别日志记录", notes = "人脸识别日志记录")
  public RestResult<Stats> createStats(@Valid @ModelAttribute CreateStatsParam param, BindingResult errResult) {
    if (errResult.hasErrors()) {
      List<ObjectError> errorList = errResult.getAllErrors();
      for(ObjectError error : errorList){
        log.error(error.getDefaultMessage());
      }
      throw new ServiceException(errorList.get(0).getDefaultMessage());
    }
    
    Stats created = null;
    try {
      created = statsService.createStats(param);
    } catch (Throwable e) {
      log.error("人脸识别日志记录失败", e);
      throw new ServiceException("人脸识别日志记录失败");
    }
    
    return new RestResult<>(created);
  }
  
  @RequestMapping(value = "createBatch")
  @ApiOperation(value = "批量创建人脸识别日志", notes = "批量创建人脸识别日志")
  @ApiImplicitParams(value = {
      @ApiImplicitParam(name = "json", value = "人脸识别日志列表JSON字符串", paramType = "query", dataType = "String", required = true)
  })
  public RestResult<Integer> createBatch(@RequestParam("json") String json) {
    if (!StringUtils.hasText(json)) {
      return new RestResult<Integer>(0);
    }
    List<CreateStatsParam> list = null;
    try {
      list = mapper.readValue(json, new TypeReference<List<CreateStatsParam>>() {});
    } catch (IOException e) {
      log.error("JSON解析错误", e);
      return new RestResult<Integer>(0);
    }
    if (CollectionUtils.isEmpty(list)) {
      return new RestResult<Integer>(0);
    }
    List<Stats> created = null;
    try {
      created = statsService.createBatch(list);
    } catch (Throwable e) {
      log.error("批量创建人脸识别日志失败", e);
      int failed = list.size() - (created == null ? 0 : created.size());
      throw new ServiceException("批量创建人脸识别日志失败，failed：" + failed);
    }
    
    return new RestResult<>(created == null ? 0 : created.size());
  }
  
  @RequestMapping(value = "meetingStats")
  @ApiOperation(value = "签到人数统计", notes = "签到人数统计")
  @Log(module = "统计报表", operation = "签到人数统计")
  @PreAuthorize("hasAuthority('STAT_QUERY')")
  public RestResult<PageResult<MeetingManage>> meetingStats(@Valid @ModelAttribute QueryMeetingManageParam param, BindingResult errResult) {
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
  
  @RequestMapping(value = "recognitionList")
  @ApiOperation(value = "查询人脸比对信息", notes = "查询人脸比对信息")
  @Log(module = "统计报表", operation = "查询人脸比对信息")
  @PreAuthorize("hasAuthority('FACE_LOG_QUERY')")
  public RestResult<PageResult<FaceRecognitionResult>> recognitionList(@Valid @ModelAttribute QueryStatsParam param, BindingResult errResult) {
    if (errResult.hasErrors()) {
      List<ObjectError> errorList = errResult.getAllErrors();
      for(ObjectError error : errorList){
        log.error(error.getDefaultMessage());
      }
      throw new ServiceException(errorList.get(0).getDefaultMessage());
    }

    PageResult<FaceRecognitionResult> result = new PageResult<>();
    try {
      Page<FaceRecognitionResult> page = statsService.findByPage(param);
      if (page != null && page.hasContent()) {
        result.setPages(page.getTotalPages());
        result.setTotal(page.getTotalElements());
        result.setRows(page.getContent());
      }
    } catch (Throwable e) {
      log.error("查询人脸比对信息失败", e);
      throw new ServiceException("查询人脸比对信息失败");
    }
    
    return new RestResult<>(result);
  }
  
}
