package com.minivision.fdi.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.minivision.fdi.mqtt.core.TopicMessageDeliver;
import com.minivision.fdi.service.DeviceService;

import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptAcknowledgedMessage;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ServerInterceptHandler implements InterceptHandler{
  
  @Autowired
  private TopicMessageDeliver topicMessageDeliver;
  
  @Autowired
  private DeviceService deviceService;

  @Override
  public void onConnect(InterceptConnectMessage connectMessage) {
    String id = connectMessage.getClientID();
    //deviceService.deviceOnline(id);
    log.info("device[" + id + "] connected..."); 
  }

  @Override
  public void onDisconnect(InterceptDisconnectMessage disconnectMessage) {
    String id = disconnectMessage.getClientID();
    log.info("device[" + id + "] disconnected...");
    deviceService.deviceOffline(id);
  }

  @Override
  public void onConnectionLost(InterceptConnectionLostMessage connectionLostMessage) {
    String id = connectionLostMessage.getClientID();
    log.info("device[" + id + "] lost...");
    deviceService.deviceLost(id);
  }

  @Override
  public void onPublish(InterceptPublishMessage publishMessage) {
    log.trace("receive a mqtt message: client[{}], topic[{}]", publishMessage.getClientID(), publishMessage.getTopicName());
    topicMessageDeliver.deliver(publishMessage);
  }

  @Override
  public void onSubscribe(InterceptSubscribeMessage subscribeMessage) {
    //TODO 当设备完成topic的订阅才标识其上线完成？实际上其未完成订阅时处于不可控状态。
    log.trace("subscribe message: client[{}], topic[{}]", subscribeMessage.getClientID(),subscribeMessage.getTopicFilter());
  }

  @Override
  public void onUnsubscribe(InterceptUnsubscribeMessage unsubscribeMessage) {
    log.trace("unsubscribe message: client[{}], topic[{}]", unsubscribeMessage.getClientID(), unsubscribeMessage.getTopicFilter());
  }

  @Override
  public void onMessageAcknowledged(InterceptAcknowledgedMessage acknowledgedMessage) {
    log.trace("acknowledge message: msg[{}], topic[{}]", acknowledgedMessage.getMsg(), acknowledgedMessage.getTopic());
  }

  @Override
  public String getID() {
    return this.getClass().getName();
  }

  @Override
  public Class<?>[] getInterceptedMessageTypes() {
    return null;
  }

}
