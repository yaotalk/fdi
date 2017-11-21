package com.minivision.fdi.mqtt.handler;

import com.minivision.fdi.domain.*;
import com.minivision.fdi.service.DeviceService;
import com.minivision.fdi.service.FaceService;
import com.minivision.fdi.service.MeetService;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.minivision.fdi.entity.BizConfig;
import com.minivision.fdi.entity.Device;
import com.minivision.fdi.entity.Meeting;
import com.minivision.fdi.mqtt.core.DeviceCmdHandler;
import com.minivision.fdi.mqtt.core.DeviceCmdHandler.CmdHandler;
import com.minivision.fdi.mqtt.core.MqttMessageHandler.MqttMessageParam;
import com.minivision.fdi.mqtt.core.MqttMessageHandler.MqttMessageParam.ParamType;
import com.minivision.fdi.mqtt.message.Status;
import com.minivision.fdi.mqtt.protocol.ActiveCodeUtil;
import com.minivision.fdi.mqtt.protocol.Packet.Head.CmdCode;
import com.minivision.fdi.mqtt.protocol.Packet.Head.Type;
import com.minivision.fdi.repository.BizConfigRepository;
import com.minivision.fdi.repository.DeviceRepository;
import com.minivision.fdi.repository.MeetRepository;
import com.minivision.fdi.rest.param.CreateStatsParam;
import com.minivision.fdi.rest.param.MeetParam;
import com.minivision.fdi.rest.result.common.PageResult;
import com.minivision.fdi.service.StatsService;

@Service
@DeviceCmdHandler("pad")
public class SignPadDeviceHandler {

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

  @Autowired
  FaceService faceService;

  @Autowired
  private MeetService meetingService;

  @Autowired
  private ActiveCodeUtil codeUtil;

  @CmdHandler(code = CmdCode.FACESET_BIND)
  public BizConfig faceSetBind(FaceSetMsg faceSet) {
    Device device = deviceRepo.findBySn(faceSet.getDeviceSn());
    Meeting meeting = meetRepository.findOne(faceSet.getMeetingToken());
    if(meeting == null){
      throw new IllegalArgumentException("meeting["+faceSet.getMeetingToken()+"] not exists.");
    }
    device.setMeeting(meeting);
    deviceRepo.saveAndFlush(device);

    BizConfig config = bizConfigRepo.findByMeetingToken(faceSet.getMeetingToken());
    config = config == null ? bizConfigRepo.findByDeviceSn(faceSet.getDeviceSn()) : config;
    return config;
  }

  @CmdHandler(code = CmdCode.CONNECT)
  public void online(@MqttMessageParam(ParamType.clientId) String clientId) {
    deviceService.deviceOnline(clientId);
  }

  @CmdHandler(code = CmdCode.DISCONNECT)
  public void offline(@MqttMessageParam(ParamType.clientId) String clientId) {

  }

  @CmdHandler(code = CmdCode.STATUS)
  public void statusReport(Status status) {
    //TODO 
  }

  // 设备上报识别日志
  @CmdHandler(code = CmdCode.REPORT_STATS)
  public void reportStats(CreateStatsParam stats) {
     statsService.createStats(stats);
  }

  @CmdHandler(code = CmdCode.FACE_SEARCH, type = Type.REQUEST)
  public FaceMsg faceRec(FaceRecMsg regMsg) {
    return faceService.search(regMsg);
  }


  @CmdHandler(code = CmdCode.QR_SEARCH, type = Type.REQUEST)
  public FaceMsg qrRec(QrRecMsg qrRecMsg) {
    return faceService.searchByQrCode(qrRecMsg);
  }

  @CmdHandler(code = CmdCode.DETECT_ACTIVE, type = Type.REQUEST)
  public ActiveMsgAck detectActive(ActiveMsg msg) {
    Device device = deviceService.findDevice(msg.getSn());
    Boolean activated = device.getActivated();
    return new ActiveMsgAck(activated);
  }

  @CmdHandler(code = CmdCode.ACTIVE, type = Type.REQUEST)
  public ActiveMsgAck active(ActiveMsg msg) {
    String code = msg.getActivateCode();
    String sn = msg.getSn();
    Device device = deviceService.findDevice(sn);
    boolean match = codeUtil.match(code, device.getModel(), device.getSn());
    if (match) {
      deviceService.activateDevice(sn);
      return new ActiveMsgAck(true);
    } else {
      return new ActiveMsgAck(false);
    }
  }

  @CmdHandler(code = CmdCode.LIST_MEETING, type = Type.REQUEST)
  public List<Meeting> listMeeting() {
    MeetParam param = new MeetParam();
    param.setDeadLine(new Date().getTime());
    PageResult<Meeting> page = meetingService.findByPlat(param);
    return page.getRows();
  }
}
