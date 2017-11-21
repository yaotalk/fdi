package com.minivision.fdi.mqtt.protocol;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minivision.fdi.mqtt.MqttConstant;
import com.minivision.fdi.mqtt.ex.MqttOpException;
import com.minivision.fdi.mqtt.protocol.Packet.Head;
import com.minivision.fdi.mqtt.protocol.Packet.Head.Type;

@Component
public class PacketUtils {

  @Autowired
  private ObjectMapper mapper;
  
  private static PacketUtils packetUtils;

  public static PacketUtils getInstance() {
    return packetUtils;
  }

  @PostConstruct
  public void init() {
    packetUtils = this;
  }

  public Head buildResponseHead(Head req, int type) {
    Head head = new Head(req.getId(), req.getCode(), type);
    return head;
  }

  public Head buildRequestHead(int code) {
    Head head = new Head(getNextId(), code, Type.REQUEST);
    return head;
  }

  public Head buildNotifyHead(int code) {
    Head head = new Head(getNextId(), code, Type.NOTIFY);
    return head;
  }
  
  public Head parseHead(JsonParser p) throws IOException {
    p.nextToken(); // JsonToken.START_OBJECT
    assertToken(JsonToken.START_OBJECT, p);

    p.nextToken(); // head
    assertField("head", p);

    p.nextToken();// JsonToken.START_OBJECT
    assertToken(JsonToken.START_OBJECT, p);
    Head h = mapper.readValue(p, Head.class);
    return h;
  }
  
  public <T> T parseBody(JsonParser p, Class<T> type) throws IOException {
    if (type == null || type == Void.class) {
      // no body expected,just return null even if actually has a body
      return null;
    }

    p.nextToken();// body
    if (p.hasCurrentToken() && "body".equals(p.getCurrentName())) {
      p.nextToken();// JsonToken.START_OBJECT
      return mapper.readValue(p, type);
    }
    return null;
  }
  
/*  public <T> Packet<T> parse(ByteBuf buf, Class<T> type) throws IOException {
    JsonParser p = createParser(buf.array());
    Head head = parseHead(p);
    T body = parseBody(p, type);
    return new Packet<T>(head, body);
  }*/
  
  private void assertToken(JsonToken expect, JsonParser p) throws JsonParseException {
    if (p.getCurrentToken() != expect)
      throw new JsonParseException(p,
          String.format("Invalid token, expected %s and actually %s", expect, p.getCurrentToken()),
          p.getTokenLocation());
  }

  private void assertField(String fieldName, JsonParser p) throws IOException {
    assertToken(JsonToken.FIELD_NAME, p);

    String name = p.getCurrentName();
    if (!fieldName.equals(name))
      throw new JsonParseException(p,
          String.format("Invalid field, expected %s and actually %s", fieldName, name),
          p.getTokenLocation());
  }

  public byte[] serialize(Packet<?> p){
    try {
      return mapper.writeValueAsBytes(p);
    }catch (JsonProcessingException e) {
      throw new MqttOpException("packet serialize error", e);
    }
  }

  public JsonParser createParser(byte[] json) throws IOException, JsonParseException {
    JsonParser p = mapper.getFactory().createParser(json);
    return p;
  }
  
  private SecureRandom r = new SecureRandom();
  private AtomicLong seq = new AtomicLong(Math.abs(r.nextLong() / 10));

  public long getNextId() {
    long id = seq.incrementAndGet();
    if (id < 0 || id == Long.MAX_VALUE) {
      seq.compareAndSet(id, Math.abs(r.nextLong() / 10));
    }
    return id;
  }

  public String getDeviceAddr(String sn, String model) {
    return MqttConstant.DEVICE_TOPIC_PREFIX+"/"+model+"/"+sn;
  }
  
}
