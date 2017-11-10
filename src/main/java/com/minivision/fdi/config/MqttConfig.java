package com.minivision.fdi.config;

import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minivision.fdi.common.CommonConstants;

import io.moquette.server.Server;

@Configuration
public class MqttConfig {

  @Bean
  public Server mqttServer(){
    return new Server();
  }
  
  @Bean
  public ObjectMapper objectMapper(){
    ObjectMapper om = new ObjectMapper();
    om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    om.setDateFormat(new SimpleDateFormat(CommonConstants.FULL_DATE_FORMAT));
    //om.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
    return om;
  }
  
  @Bean
  public AntPathMatcher pathMatcher(){
    return new AntPathMatcher();
  }
}
