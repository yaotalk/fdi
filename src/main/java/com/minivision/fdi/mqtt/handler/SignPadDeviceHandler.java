package com.minivision.fdi.mqtt.handler;

import com.minivision.fdi.device.DeviceService;
import com.minivision.fdi.domain.*;
import com.minivision.fdi.entity.Face;
import com.minivision.fdi.service.FaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.minivision.fdi.entity.BizConfig;
import com.minivision.fdi.entity.Device;
import com.minivision.fdi.entity.Meeting;
import com.minivision.fdi.mqtt.RequestFuture;
import com.minivision.fdi.mqtt.core.MqttMessageHandler;
import com.minivision.fdi.mqtt.core.MqttMessageHandler.CmdHandler;
import com.minivision.fdi.mqtt.core.MqttMessageHandler.MqttMessageParam;
import com.minivision.fdi.mqtt.core.MqttMessageHandler.MqttMessageParam.ParamType;
import com.minivision.fdi.mqtt.message.Status;
import com.minivision.fdi.mqtt.protocol.Packet;
import com.minivision.fdi.mqtt.protocol.Packet.Head;
import com.minivision.fdi.mqtt.protocol.Packet.Head.CmdCode;
import com.minivision.fdi.mqtt.protocol.Packet.Head.Type;
import com.minivision.fdi.mqtt.protocol.PacketUtils;
import com.minivision.fdi.mqtt.service.PublishMessageTemplate;
import com.minivision.fdi.repository.BizConfigRepository;
import com.minivision.fdi.repository.DeviceRepository;
import com.minivision.fdi.repository.MeetRepository;
import com.minivision.fdi.rest.param.CreateStatsParam;
import com.minivision.fdi.rest.result.common.PageResult;
import com.minivision.fdi.service.StatsService;

import lombok.extern.slf4j.Slf4j;

@Service
@MqttMessageHandler
@Slf4j
public class SignPadDeviceHandler {
  
  public static final String MODEL = "pad";
  @Autowired
  private PacketUtils packetUtils;
  @Autowired
  private PublishMessageTemplate messageTemplate;
  @Autowired
  private StatsService statsService;
  @Autowired
  private DeviceRepository deviceRepo;
  @Autowired 
  private MeetRepository meetRepository;
  @Autowired
  private BizConfigRepository bizConfigRepo;
  
  @Autowired
  private DeviceService deviceService;

  @Autowired FaceService faceService;
  
  public void setConfig(String clientId, BizConfig cfg) {
    Head h = packetUtils.buildRequestHead(CmdCode.CONFIG_SET);
    RequestFuture<Void> f = messageTemplate.sendRequest(packetUtils.getDeviceAddr(clientId, MODEL), new Packet<>(h, cfg), Void.class);
    f.getResponse().getBody();
  }
  
  public BizConfig getConfig(String clientId) {
    Head h = packetUtils.buildRequestHead(CmdCode.CONFIG_AQUIRE);
    RequestFuture<BizConfig> f = messageTemplate.sendRequest(packetUtils.getDeviceAddr(clientId, MODEL), new Packet<>(h), BizConfig.class);
    return f.getResponse().getBody();
  }
  
  public void bindFaceSet(String clientId, FaceSetMsg faceSet) {
    Head h = packetUtils.buildRequestHead(CmdCode.BIND_FACESET);
    RequestFuture<Void> f = messageTemplate.sendRequest(packetUtils.getDeviceAddr(clientId, MODEL), new Packet<>(h, faceSet), Void.class);
    f.getResponse().getBody();
  }
  
  public void refreshFaceSet(String clientId, PageResult<Meeting> faceSet) {
    Head h = packetUtils.buildRequestHead(CmdCode.REFRESH_FACESET);
    RequestFuture<Void> f = messageTemplate.sendRequest(packetUtils.getDeviceAddr(clientId, MODEL), new Packet<>(h, faceSet), Void.class);
    f.getResponse().getBody();
  }
  
  @CmdHandler(code = CmdCode.FACESET_BOUND)
  public void faceSetBind(FaceSetMsg faceSet) {
    //处理设备绑定某个人脸库的事件
    if (StringUtils.hasText(faceSet.getMeetingToken()) && StringUtils.hasText(faceSet.getDeviceSn())) {
      Device device = deviceRepo.findBySn(faceSet.getDeviceSn());
      if (device == null) {
        device = new Device();
        device.setSn(faceSet.getDeviceSn());
        device.setName(faceSet.getDeviceSn());
        device.setModel(MODEL);
      }
      device.setMeeting(meetRepository.findOne(faceSet.getMeetingToken()));
      deviceRepo.saveAndFlush(device);
      
      //推送配置给设备
      BizConfig config = bizConfigRepo.findByMeetingToken(faceSet.getMeetingToken());
      config = config == null ? bizConfigRepo.findByDeviceSn(faceSet.getDeviceSn()) : config;
      setConfig(faceSet.getDeviceSn(), config);
    }
  }
  
  @CmdHandler(code = CmdCode.CONNECT)
  public void online(@MqttMessageParam(ParamType.clientId) String clientId) {
    //处理设备上线的消息
    deviceService.deviceOnline(clientId);
  }
  
  @CmdHandler(code = CmdCode.DISCONNECT)
  public void offline(@MqttMessageParam(ParamType.clientId) String clientId) {
    
  }
  
  @CmdHandler(code = CmdCode.STATUS)
  public void statusReport(Status status) {
    System.err.println(status);
  }
  
  //设备上报识别日志
  @CmdHandler(code = CmdCode.REPORT_STATS)
  public void reportStats(CreateStatsParam stats) {
    try {
      statsService.createStats(stats);
    } catch (Exception e) {
      log.error("设备上报识别日志写入失败", e);
    }
  }
  
  @CmdHandler(code = CmdCode.FACE_SEARCH, type = Type.REQUEST)
  public Face faceRec(FaceRecMsg regMsg){
     Face face = faceService.search(regMsg);
     return face;
  }


  @CmdHandler(code = CmdCode.QR_SEARCH, type = Type.REQUEST)
  public Face qrRec(QrRecMsg qrRecMsg){
    Face face = faceService.searchByQrCode(qrRecMsg);
    return face;
  }
  
}
