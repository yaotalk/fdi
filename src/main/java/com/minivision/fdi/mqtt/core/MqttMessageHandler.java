package com.minivision.fdi.mqtt.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MqttMessageHandler {
  //String value();
  
  @Target({ElementType.METHOD})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface TopicHandler {
    String value();
  }
  
  @Target({ElementType.METHOD})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface CmdHandler {
    @AliasFor("code")
    int[] value() default {};
    @AliasFor("value")
    int[] code() default {};
    int[] type() default {};
  }
  
  @Target({ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface MqttMessageParam {
    enum ParamType{
      clientId, username
    }
    ParamType value();
  }
  
  @Target({ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Deprecated
  public @interface MqttMessageJson {}
  
  @Target({ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  public @interface MqttMessageBody {}
  
}

