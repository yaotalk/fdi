package com.minivision.fdi.aspect;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minivision.fdi.annotation.Log;
import com.minivision.fdi.common.CommonConstants;
import com.minivision.fdi.common.ParamUtils;
import com.minivision.fdi.entity.OpLog;
import com.minivision.fdi.rest.result.common.RestResult;
import com.minivision.fdi.service.OpLogService;

import lombok.extern.slf4j.Slf4j;

/**
 * 用于记录操作日志和方法调用时间的切面
 * @author hughzhao
 * @2017年5月22日
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

  @Autowired
  private OpLogService opLogService;
  
  @Autowired
  private ObjectMapper mapper;

  /**
   * 记录操作日志
   * @param joinPoint
   * @param log
   * @param retVal
   * @throws Throwable
   */
  @AfterReturning(pointcut = "@annotation(com.minivision.fdi.annotation.Log) && @annotation(logAnno)", returning="retVal")
  public void doLog(JoinPoint joinPoint, Log logAnno, Object retVal) throws Throwable {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();

    String url = request.getRequestURL().toString();
    String paramString = ParamUtils.getParamString(request);
    if (!logAnno.ignoreArgs()) {
      log.info("{}---->{}", url, paramString);
    }
    String ip = request.getRemoteAddr();
    // save in DB
    OpLog opLog = new OpLog();
    Authentication user = SecurityContextHolder.getContext().getAuthentication();
    //获取操作账户用户名
    String username = user == null ? "anonymous" : user.getName();
    opLog.setUsername(StringUtils.hasText(username) ? username : "anonymous");
    opLog.setIp(ip);
    opLog.setModule(logAnno.module());
    opLog.setOperation(logAnno.operation());
    opLog.setOpTime(new Date());
    opLog.setRequest(request.getMethod() + " " + url + ":" + (logAnno.ignoreArgs() ? "" : paramString));
    opLog.setResponse(retVal instanceof RestResult<?> ? mapper.writeValueAsString(retVal) : (retVal instanceof String ? (String) retVal : ""));
    String requestTxt = opLog.getRequest();
    String responseTxt = opLog.getResponse();
    if (requestTxt.length() > CommonConstants.OPLOG_DATA_LIMIT) {
      opLog.setRequest(requestTxt.substring(0, CommonConstants.OPLOG_DATA_LIMIT));
    }
    if (responseTxt.length() > CommonConstants.OPLOG_DATA_LIMIT) {
      opLog.setResponse(responseTxt.substring(0, CommonConstants.OPLOG_DATA_LIMIT));
    }

    try {
      opLogService.create(opLog);
    } catch (Throwable e) {
      log.error("写操作日志发生异常", e);
    }
  }

}
