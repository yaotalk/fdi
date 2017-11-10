package com.minivision.fdi.exception;

import com.minivision.fdi.faceplat.ex.FacePlatException;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceException extends RuntimeException{

  private static final long serialVersionUID = 6758208187486526027L;
  public static final int UNKNOWN_EXCEPTION = -1;
  private int errCode;
  private String message;
  private Throwable cause;

  public ServiceException(int errCode,String message) {
    this.errCode = errCode;
    this.message = message;
  }

  public ServiceException(String message) {
    this.errCode = UNKNOWN_EXCEPTION;
    this.message = message;
  }

  public ServiceException(Throwable cause) {
    if(cause instanceof FacePlatException){
      FacePlatException e = (FacePlatException)cause;
      this.errCode  =  e.getErrCode();
      this.message = e.getMessage();
      this.cause = e.getCause();
    }
    else  {
      this.errCode = UNKNOWN_EXCEPTION;
      this.message = cause.getMessage();
      this.cause = cause;
    }
  }
}
