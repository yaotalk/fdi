package com.minivision.fdi.mqtt.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minivision.fdi.annotation.Unused;
import com.minivision.fdi.domain.ConfigUpdateMsg;
import com.minivision.fdi.domain.FaceSetMsg;
import com.minivision.fdi.entity.BizConfig;
import com.minivision.fdi.entity.Meeting;
import com.minivision.fdi.mqtt.RequestFuture;
import com.minivision.fdi.mqtt.protocol.Packet;
import com.minivision.fdi.mqtt.protocol.Packet.Head;
import com.minivision.fdi.mqtt.protocol.Packet.Head.CmdCode;
import com.minivision.fdi.mqtt.protocol.PacketUtils;
import com.minivision.fdi.mqtt.service.PublishMessageTemplate;
import com.minivision.fdi.service.FaceService;

@Service
public class SignPadDeviceSender {
  
  public static final String MODEL = "pad";
  @Autowired
  private PacketUtils packetUtils;
  @Autowired
  private PublishMessageTemplate messageTemplate;
  @Autowired 
  FaceService faceService;
  
  public void setConfig(String clientId, ConfigUpdateMsg cfg) {
    Head h = packetUtils.buildRequestHead(CmdCode.CONFIG_SET);
    RequestFuture<Void> f = messageTemplate.sendRequest(packetUtils.getDeviceAddr(clientId, MODEL), new Packet<>(h, cfg), Void.class);
    f.getResponse().getBody();
  }
  
  @Unused("从客户端获取当前配置，暂时没有处理")
  public BizConfig getConfig(String clientId) {
    Head h = packetUtils.buildRequestHead(CmdCode.CONFIG_AQUIRE);
    RequestFuture<BizConfig> f = messageTemplate.sendRequest(packetUtils.getDeviceAddr(clientId, MODEL), new Packet<>(h), BizConfig.class);
    return f.getResponse().getBody();
  }
  
  @Unused("后台编辑设备信息时绑定人脸库通知客户端，暂时没有处理")
  public void bindFaceSet(String clientId, FaceSetMsg faceSet) {
    Head h = packetUtils.buildRequestHead(CmdCode.BIND_FACESET);
    RequestFuture<Void> f = messageTemplate.sendRequest(packetUtils.getDeviceAddr(clientId, MODEL), new Packet<>(h, faceSet), Void.class);
    f.getResponse().getBody();
  }
  
  public void refreshFaceSet(String clientId, Meeting msg) {
    Head h = packetUtils.buildRequestHead(CmdCode.REFRESH_FACESET);
    RequestFuture<Void> f = messageTemplate.sendRequest(packetUtils.getDeviceAddr(clientId, MODEL), new Packet<>(h, msg), Void.class);
    f.getResponse().getBody();
  }
  
  public void unbindFaceSet(String clientId) {
    Head h = packetUtils.buildRequestHead(CmdCode.UNBIND_FACESET);
    RequestFuture<Void> f = messageTemplate.sendRequest(packetUtils.getDeviceAddr(clientId, MODEL), new Packet<>(h), Void.class);
    f.getResponse().getBody();
  }
}
