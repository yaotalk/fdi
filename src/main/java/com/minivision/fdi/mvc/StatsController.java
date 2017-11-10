package com.minivision.fdi.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.minivision.fdi.annotation.Log;
import com.minivision.fdi.rest.param.QueryMeetingManageParam;
import com.minivision.fdi.rest.param.QueryStatsParam;
import com.minivision.fdi.service.MeetingManageService;
import com.minivision.fdi.service.StatsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("stats")
@Api(tags = "StatsExport")
public class StatsController {

  @Autowired
  private MeetingManageService meetingService;

  @Autowired
  private StatsService statsService;

  @GetMapping("exportMeetingStats")
  @ApiOperation(value = "签到人数统计报表导出", notes = "签到人数统计报表导出")
  @Log(module = "统计报表", operation = "签到人数统计报表导出")
  public ModelAndView exportMeetingStats(QueryMeetingManageParam param) {
    return new ModelAndView(new MeetingStatsExcelView()).addObject("data", meetingService.findByPage(param));
  }

  @GetMapping("exportRecognitionStats")
  @ApiOperation(value = "人脸比对信息统计报表导出", notes = "人脸比对信息统计报表导出")
  @Log(module = "统计报表", operation = "人脸比对信息统计报表导出")
  public ModelAndView exportRecognitionStats(QueryStatsParam param) {
    return new ModelAndView(new RecognitionStatsExcelView()).addObject("data", statsService.findByPage(param));
  }

}
