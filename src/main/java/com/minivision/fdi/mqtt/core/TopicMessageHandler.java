package com.minivision.fdi.mqtt.core;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.core.JsonParser;
import com.minivision.fdi.mqtt.MessageContext;
import com.minivision.fdi.mqtt.RequestFuture;
import com.minivision.fdi.mqtt.core.MqttMessageHandler.MqttMessageParam;
import com.minivision.fdi.mqtt.core.MqttMessageHandler.TopicHandler;
import com.minivision.fdi.mqtt.core.MqttMessageHandler.MqttMessageParam.ParamType;
import com.minivision.fdi.mqtt.protocol.Packet;
import com.minivision.fdi.mqtt.protocol.PacketUtils;
import com.minivision.fdi.mqtt.protocol.Packet.Head;
import com.minivision.fdi.mqtt.protocol.Packet.Head.Type;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@MqttMessageHandler
public class TopicMessageHandler {

  @Autowired
  private PacketUtils packetUtils;
  
  @Autowired
  private MessageContext messageContext;
  
  @Autowired
  private CmdMessageDeliver codeMessageDeliver;
  
  @TopicHandler("/s/{model}")
  public void msgProcess(@MqttMessageParam(ParamType.clientId) String clientId, @MqttMessageParam(ParamType.username) String username, ByteBuf payload, @PathVariable("model") String model) {
    try {
      byte[] payloadBytes = new byte[payload.readableBytes()];
      payload.getBytes(0, payloadBytes);
      log.info("receive raw message: {}", new String(payloadBytes, "utf-8"));
      JsonParser parser = packetUtils.createParser(payloadBytes);
      Head head = packetUtils.parseHead(parser);
      log.info("receive a packet, head : {}", head);
      int code = head.getCode();
      int type = head.getType();
      if(Type.isResponse(type)){
        handleGenericResponse(head, parser);
        return;
      }
      codeMessageDeliver.deliver(code, type, model, clientId, username, head, parser);
    } catch (IOException e) {
      log.error("deserialize payload error", e);
    } finally {
      payload.release();
    }
  }
  
  @SuppressWarnings({"rawtypes", "unchecked"})
  private void handleGenericResponse(Head h, JsonParser parser) throws IOException {
    RequestFuture<?> req = messageContext.remove(h.getId());
    if (req == null) {
        log.error("No request found for a response, maybe timeout, just discard it. Head : {}", h);
        return;
    }
    try {
        Object body = packetUtils.parseBody(parser, req.getResponseBodyType());
        Packet response = new Packet<>(h, body);
        req.setResponse(response);
    } catch (Exception e) {
        log.error("parseBody error, head: {}", h, e);
        req.fail(e);
    }
  }
  
}
