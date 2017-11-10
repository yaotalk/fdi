package com.minivision.fdi.mqtt.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.minivision.fdi.mqtt.core.MqttMessageHandler.CmdHandler;
import com.minivision.fdi.mqtt.protocol.Packet;
import com.minivision.fdi.mqtt.protocol.PacketUtils;
import com.minivision.fdi.mqtt.protocol.Packet.Head;
import com.minivision.fdi.mqtt.protocol.Packet.Head.Type;
import com.minivision.fdi.mqtt.service.PublishMessageTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CmdMessageDeliver {
  @Autowired
  private PacketUtils packetUtils;
  
  @Autowired
  private ApplicationContext context;
  
  @Autowired
  private PublishMessageTemplate messageTemplate;
  
  private static final Map<Integer, Map<Integer, MsgHandlerMethodContext>> msgHandlers = new HashMap<>();
  
  @PostConstruct
  private void init(){
    Map<String, Object> beans = context.getBeansWithAnnotation(MqttMessageHandler.class);

    for (Object instance : beans.values()) {
      StandardAnnotationMetadata s = new StandardAnnotationMetadata(instance.getClass());
      Set<MethodMetadata> methods = s.getAnnotatedMethods(CmdHandler.class.getName());
      for (MethodMetadata t : methods) {
        StandardMethodMetadata m = (StandardMethodMetadata) t;
        Map<String, Object> methodMeta = m.getAnnotationAttributes(CmdHandler.class.getName());
        int[] codes = (int[]) methodMeta.get("code");
        for(int code: codes){
          Map<Integer, MsgHandlerMethodContext> codeMap = msgHandlers.get(code);
          if(codeMap == null){
            codeMap = new HashMap<>();
            msgHandlers.put(code, codeMap);
          }
          int[] types = (int[]) methodMeta.get("type");
          Method h = m.getIntrospectedMethod();
          h.setAccessible(true);
          log.info("Found cmdCode handler method: {}, meta={}", h, methodMeta);
          MsgHandlerMethodContext context = new MsgHandlerMethodContext(instance, h);
          if(types.length == 0){
            codeMap.put(null, context);
          }else{
            for(int type: types){
              codeMap.put(type, context);
            }
          }
        }
      }
    }
  }
  
  public MsgHandlerMethodContext getProcessMethod(int code, int type) {
    Map<Integer, MsgHandlerMethodContext> codeMap = msgHandlers.get(code);
    MsgHandlerMethodContext methodContext = null;
    if(codeMap != null){
      methodContext = codeMap.get(type);
      if(methodContext == null){
        methodContext = codeMap.get(null);
      }
    }
    return methodContext;
  }
  
  public void deliver(int code, int type, String model, String clientId, String username, Head head, JsonParser bodyParser) {
    MsgHandlerMethodContext methodContext = getProcessMethod(code, type);
    
    if(methodContext == null){
      log.warn("No handler method for code: {}, type: {}", code, type);
      if(type == Type.REQUEST){
        responseBadRequest(packetUtils.getDeviceAddr(clientId, model), head);
      }
      return;
    }
    
    try {
      Packet<?> res = methodContext.process(clientId, username, head, bodyParser);
      if(type == Type.REQUEST){
        log.info("send a response: {}", res);
        messageTemplate.sendTo(packetUtils.getDeviceAddr(clientId, model), res);
      }
    } catch (Exception e) {
      e.printStackTrace();
      if(type == Type.REQUEST){
        responseBadRequest(packetUtils.getDeviceAddr(clientId, model), head);
      }
    }
  }
  
  private void responseBadRequest(String topic, Head h){
    Packet<?> response = new Packet<Object>(packetUtils.buildResponseHead(h, Type.RESPONSE_BAD_REQ));
    messageTemplate.sendTo(topic, response);
  }
}
