package com.minivision.fdi.mqtt.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;

import com.minivision.fdi.mqtt.core.MqttMessageHandler.MqttMessageParam;

import io.moquette.interception.messages.InterceptPublishMessage;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TopicHandlerMethodContext {
  
  private Object instance;
  private Method method;
  
  public TopicHandlerMethodContext(Object instance, Method method) {
    this.instance = instance;
    this.method = method;
  }
  
  public Object process(InterceptPublishMessage message, Map<String, String> pathVariables) {
    try {
      Parameter[] ps = method.getParameters();
      List<Object> args = new ArrayList<>();
      for(Parameter p : ps){
        Object arg = searchArg(p, message, pathVariables);
        args.add(arg);
      }
      
      Object object = method.invoke(instance, args.toArray());
      log.trace("invoke message process method, service:{}, name:{}, data:{}", instance.getClass().getName(), method.getClass().getName(), args);
      return object;
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      log.error("invoke message process method error", e);
    }
    return null;
  }
  
  private Object searchArg(Parameter p , InterceptPublishMessage message, Map<String, String> pathVariables){
    if(p.getType().equals(InterceptPublishMessage.class)) {
      return message;
    }
    if(p.getType().equals(ByteBuf.class)){
      return message.getPayload();
    }
    /*if(p.isAnnotationPresent(MqttMessageJson.class)){
      try {
        return OBJECT_MAPPER.readValue(message.getPayload().array(), p.getType());
      } catch (IOException e) {
        throw new RuntimeException("json deserialize error", e);
      }
    }*/
    if(p.getType().equals(String.class)){
      MqttMessageParam paramAnnotation = p.getDeclaredAnnotation(MqttMessageParam.class);
      if(paramAnnotation != null){
        switch(paramAnnotation.value()){
          case clientId:
            return message.getClientID(); 
          case username:
            return message.getUsername();
        }
      }
      
      PathVariable pathAnnotation = p.getDeclaredAnnotation(PathVariable.class);
      if(pathAnnotation != null){
        String pathVariable = pathAnnotation.value();
        if(pathVariable == null){
          pathVariable = p.getName();
        }
        return pathVariables.get(pathVariable);
      }
    }
    
    return null;
  }
  
}
