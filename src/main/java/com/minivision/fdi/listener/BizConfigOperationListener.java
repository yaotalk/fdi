package com.minivision.fdi.listener;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.minivision.fdi.domain.ConfigUpdateMsg;
import com.minivision.fdi.entity.BizConfig;
import com.minivision.fdi.entity.Device;
import com.minivision.fdi.event.BizConfigUpdateEvent;
import com.minivision.fdi.mqtt.handler.SignPadDeviceSender;
import com.minivision.fdi.repository.DeviceRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BizConfigOperationListener {
  
  @Autowired
  private SignPadDeviceSender padHandler;
  
  @Autowired
  private DeviceRepository deviceRepo;
  
  @Autowired
  @Qualifier("mqttSenderWorker")
  private ExecutorService mqttSenderWorker;

  @EventListener
  public void configPostUpdate(BizConfigUpdateEvent event) {
      BizConfig config = event.getConfig();
      log.info("Listening BizConfig Post Update : " + config);
      //向设备推送新的配置
      ConfigUpdateMsg msg = new ConfigUpdateMsg();
      try {
        BeanUtils.copyProperties(config, msg);
        if (StringUtils.hasText(config.getMeetingToken())) {
          List<Device> devices = deviceRepo.findByMeetingToken(config.getMeetingToken());
          if (!CollectionUtils.isEmpty(devices)) {
            mqttSenderWorker.execute(() -> {
              try {
                for (Device device : devices) {
                  padHandler.setConfig(device.getSn(), msg);
                }
              } catch (Throwable e) {
                log.error("MQTT异常", e);
              }
            });
          }
        }
        
        if (StringUtils.hasText(config.getDeviceSn())) {
          mqttSenderWorker.execute(() -> {
            try {
              padHandler.setConfig(config.getDeviceSn(), msg);
            } catch (Throwable e) {
              log.error("MQTT异常", e);
            }
          });
        }
      } catch (Throwable e) {
        log.error("向设备推送新的配置发生异常", e);
      }
  }
  
}
