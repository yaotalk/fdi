package com.minivision.fdi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.moquette.server.Server;

@Configuration
public class MqttConfig {

  @Bean
  public Server mqttServer(){
    return new Server();
  }
  
}
