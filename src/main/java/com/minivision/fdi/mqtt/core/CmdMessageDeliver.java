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
import com.minivision.fdi.mqtt.core.DeviceCmdHandler.CmdHandler;
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
  
  public static final Map<String, Map<Integer, Map<Integer, MsgHandlerMethodContext>>> cmdHandlers = new HashMap<>();
  
  //private static final Map<Integer, Map<Integer, MsgHandlerMethodContext>> msgHandlers = new HashMap<>();
  
  @PostConstruct
  private void init(){
    Map<String, Object> beans = context.getBeansWithAnnotation(DeviceCmdHandler.class);

    for (Object instance : beans.values()) {
      StandardAnnotationMetadata s = new StandardAnnotationMetadata(instance.getClass());
      Map<String, Object> map = s.getAnnotationAttributes(DeviceCmdHandler.class.getName());
      //DeviceCmdHandler annotation = instance.getClass().getAnnotation(DeviceCmdHandler.class);
      //System.out.println(Arrays.toString(annotation.model()));
      //System.out.println(Arrays.toString(annotation.value()));
      String[] models = (String[]) map.get("model");
      //System.out.println(Arrays.toString(models));
      Set<MethodMetadata> methods = s.getAnnotatedMethods(CmdHandler.class.getName());
      for (MethodMetadata t : methods) {
        StandardMethodMetadata m = (StandardMethodMetadata) t;
        Map<String, Object> methodMeta = m.getAnnotationAttributes(CmdHandler.class.getName());
        for(String model: models){
          cmdHandlers.putIfAbsent(model, new HashMap<>());
          Map<Integer, Map<Integer, MsgHandlerMethodContext>> modelCmdHandlers = cmdHandlers.get(model);
          int[] codes = (int[]) methodMeta.get("code");
          for(int code: codes){
            modelCmdHandlers.putIfAbsent(code, new HashMap<>());
            Map<Integer, MsgHandlerMethodContext> codeMap = modelCmdHandlers.get(code);
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
  }
  
  public MsgHandlerMethodContext getProcessMethod(String model, int code, int type) {
    Map<Integer, Map<Integer, MsgHandlerMethodContext>> modelCmdMap = cmdHandlers.get(model);
    if(modelCmdMap == null){
      log.warn("No handler class found for device model: {}", model);
      return null;
    }
    
    Map<Integer, MsgHandlerMethodContext> codeMap = modelCmdMap.get(code);
    if(codeMap == null){
      log.warn("No handler method found for device model:{}, code:{}", model, code);
      return null;
    }
    
    MsgHandlerMethodContext methodContext = codeMap.get(type);
    return methodContext == null? codeMap.get(null): methodContext;
  }
  
  public void deliver(int code, int type, String model, String clientId, String username, Head head, JsonParser bodyParser) {
    MsgHandlerMethodContext methodContext = getProcessMethod(model, code, type);
    
    if(methodContext == null){
      log.warn("No handler method found for device model:{}, code:{}, type:{}", model, code, type);
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
      log.error("request error", e);
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
