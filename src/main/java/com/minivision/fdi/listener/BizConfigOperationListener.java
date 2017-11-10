package com.minivision.fdi.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.minivision.fdi.entity.BizConfig;
import com.minivision.fdi.entity.Device;
import com.minivision.fdi.event.BizConfigUpdateEvent;
import com.minivision.fdi.mqtt.handler.SignPadDeviceHandler;
import com.minivision.fdi.repository.DeviceRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BizConfigOperationListener {
  
  @Autowired
  private SignPadDeviceHandler padHandler;
  
  @Autowired
  private DeviceRepository deviceRepo;

  @EventListener
  public void configPostUpdate(BizConfigUpdateEvent event) {
      BizConfig config = event.getConfig();
      log.info("Listening BizConfig Post Update : " + config);
      //向设备推送新的配置
      try {
        if (StringUtils.hasText(config.getMeetingToken())) {
          List<Device> devices = deviceRepo.findByMeetingToken(config.getMeetingToken());
          if (!CollectionUtils.isEmpty(devices)) {
            for (Device device : devices) {
              padHandler.setConfig(device.getSn(), config);
            }
          }
        }
        
        if (StringUtils.hasText(config.getDeviceSn())) {
          padHandler.setConfig(config.getDeviceSn(), config);
        }
      } catch (Throwable e) {
        log.error("向设备推送新的配置发生异常", e);
      }
  }
  
}
