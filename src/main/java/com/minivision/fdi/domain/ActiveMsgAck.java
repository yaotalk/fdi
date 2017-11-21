package com.minivision.fdi.domain;

import lombok.Data;

@Data
public class ActiveMsgAck {
  private boolean activation;

  public ActiveMsgAck(boolean active) {
    this.activation = active;
  }
  
}
