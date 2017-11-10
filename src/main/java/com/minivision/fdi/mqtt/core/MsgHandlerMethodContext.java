package com.minivision.fdi.mqtt.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.minivision.fdi.mqtt.core.MqttMessageHandler.MqttMessageBody;
import com.minivision.fdi.mqtt.core.MqttMessageHandler.MqttMessageParam;
import com.minivision.fdi.mqtt.protocol.Packet;
import com.minivision.fdi.mqtt.protocol.Packet.Head;
import com.minivision.fdi.mqtt.protocol.Packet.Head.Type;
import com.minivision.fdi.mqtt.protocol.PacketUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MsgHandlerMethodContext {
  private Object instance;
  private Method method;
  
  public MsgHandlerMethodContext(Object instance, Method method) {
    this.instance = instance;
    this.method = method;
  }
  
  public Packet<?> process(String clientId, String username, Head head, JsonParser parser) throws Exception {
      Parameter[] ps = method.getParameters();
      List<Object> args = new ArrayList<>();
      for(Parameter p : ps){
        Object arg = searchArg(p, clientId, username, head, parser);
        args.add(arg);
      }
      
      Object o = method.invoke(instance, args.toArray());
      
      log.trace("invoke message process method, service:{}, name:{}, data:{}", instance.getClass().getName(), method.getClass().getName(), args);
      
      if (o == null) {
          //if (method.getReturnType() == void.class || method.getReturnType() == Void.class) //should be notify message
              //return null;
          o = Type.RESPONSE_OK;// default set return type to ok
      }

      Packet<?> rs;
      if (o instanceof Integer) {// type
          rs = new Packet<Object>(PacketUtils.getInstance().buildResponseHead(head, (int) o));
      } else if (o instanceof Head) {// head
          rs = new Packet<Object>((Head) o);
      } else if (o instanceof Packet) {
          rs = (Packet<?>) o;
      } else {// should be Packet body
          rs = new Packet<Object>(PacketUtils.getInstance().buildResponseHead(head, Type.RESPONSE_OK), o);
      }
      return rs;
  }
  
  private Object searchArg(Parameter p , String clientId, String username, Head head, JsonParser parser){
    if(p.getType().equals(Head.class)){
      return head;
    }
    if(p.getType().equals(String.class)){
      MqttMessageParam annotation = p.getDeclaredAnnotation(MqttMessageParam.class);
      switch(annotation.value()){
        case clientId:
          return clientId; 
        case username:
          return username;
      }
    }
    
    try {
      return PacketUtils.getInstance().parseBody(parser, p.getType());
    } catch (IOException e) {
      if(p.isAnnotationPresent(MqttMessageBody.class)){
        throw new RuntimeException("json deserialize error", e);
      }
    }
    return null;
  }

}
