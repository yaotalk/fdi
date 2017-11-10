package com.minivision.fdi.rest.result.common;

import lombok.Data;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@JsonInclude(Include.NON_EMPTY)
public class RestResult<T> {
  private String requestId = UUID.randomUUID().toString();
  private int timeUsed;
  private int status;
  private String message;
  private T data;

  public RestResult() {
    
  }

  public RestResult(T data) {
    this.data = data;
  }

  public RestResult(Throwable t) {
    this.message = t.getClass().getSimpleName()+": "+ t.getMessage();
    this.status = -1;
  }
  
  public RestResult(Throwable t, int status) {
    this.message = t.getClass().getSimpleName()+": "+ t.getMessage();
    this.status = status;
  }
  
  public RestResult(String message, int status) {
    this.message = message;
    this.status = status;
  }
}
