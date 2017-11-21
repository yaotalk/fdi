package com.minivision.fdi.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.minivision.fdi.rest.result.common.RestResult;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class MvcExceptionHandler {

  @ExceptionHandler(ServiceException.class)
  @ResponseBody
  public RestResult<?> handleFacePlatException(HttpServletRequest request, HttpServletResponse response, ServiceException ex) {
    log.error("",ex);
    return new RestResult<>(ex, ex.getErrCode());
  }
  
  @ExceptionHandler(ServletRequestBindingException.class)
  @ResponseBody
  public RestResult<?> handleRequestException(HttpServletRequest request, HttpServletResponse response, ServletRequestBindingException ex) {
    log.error("",ex);
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    return new RestResult<>(ex, HttpStatus.BAD_REQUEST.value());
  }
  
  
  @ExceptionHandler(AccessDeniedException.class)
  @ResponseBody
  public Object handleException(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) {
    log.error(ex.getMessage());
    response.setStatus(HttpStatus.FORBIDDEN.value());
    return new RestResult<>(ex, HttpStatus.FORBIDDEN.value());
  }

  @ExceptionHandler(Throwable.class)
  @ResponseBody
  public Object handleException(HttpServletRequest request, HttpServletResponse response, Throwable ex) {
    log.error("",ex);
    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    return new RestResult<>(ex, HttpStatus.INTERNAL_SERVER_ERROR.value());
  }
  
}
