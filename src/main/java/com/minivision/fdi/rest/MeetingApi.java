package com.minivision.fdi.rest;

import com.minivision.fdi.rest.result.common.PageResult;
import com.minivision.fdi.rest.result.common.RestResult;
import com.minivision.fdi.rest.result.MeetingAddResult;
import com.minivision.fdi.rest.result.MeetingUpdateResult;
import com.minivision.fdi.rest.param.MeetAddParam;
import com.minivision.fdi.rest.param.MeetUpdateParam;
import com.minivision.fdi.service.MeetService;
import com.minivision.fdi.annotation.Log;
import com.minivision.fdi.entity.Meeting;
import com.minivision.fdi.rest.param.MeetParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/meeting")
@Api(tags = "meeting", value = "meeting Apis")
@PreAuthorize("hasAuthority('FACE_MEETING')")
public class MeetingApi {

  @Autowired
  private MeetService meetService;

  @GetMapping("list")
  @ApiOperation(value="会议列表", notes="会议列表")
  @Log(module = "人脸库管理", operation = "查询会议列表")
  public RestResult<PageResult<Meeting>> list(MeetParam meetParam) {
    PageResult<Meeting> faceSets = meetService.findByPlat(meetParam);
    return new RestResult<>(faceSets);
  }

  @PostMapping("create")
  @ApiOperation(value="创建会议", notes="创建会议")
  @Log(module = "人脸库管理", operation = "创建会议")
  public RestResult<MeetingAddResult> add(@ModelAttribute @Validated MeetAddParam meetAddParam) throws Exception{
    MeetingAddResult meetingAddResult = meetService.addMeeting(meetAddParam);
    return new RestResult<>(meetingAddResult);
  }

  @PostMapping("update")
  @ApiOperation(value="更新会议", notes="更新会议")
  @Log(module = "人脸库管理", operation = "更新会议信息")
  public RestResult<MeetingUpdateResult> update(@ModelAttribute @Validated MeetUpdateParam updateParam)throws Exception{
     MeetingUpdateResult meetingUpdateResult =  meetService.update(updateParam);
     return  new RestResult<>(meetingUpdateResult);
  }

  @PostMapping("delete")
  @ApiOperation(value="删除会议", notes="删除会议")
  @Log(module = "人脸库管理", operation = "删除会议")
  public RestResult<MeetingUpdateResult> delete(@RequestParam("token") String id) throws Exception{
    MeetingUpdateResult meetingUpdateResult = meetService.delete(id);
    return  new RestResult<>(meetingUpdateResult);
  }

}
