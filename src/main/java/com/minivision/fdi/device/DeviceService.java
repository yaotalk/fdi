package com.minivision.fdi.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minivision.fdi.entity.Device;

@Service
public class DeviceService {
  @Autowired
  private OnlineDeviceStore onlineDeviceStore;
  
  public void deviceOnline(String sn) {
    onlineDeviceStore.online(sn);
  }
  
  public void deviceOffline(String sn) {
    onlineDeviceStore.offline(sn);
  }
  
  public void deviceLost(String sn) {
    onlineDeviceStore.lost(sn);
  }
  
  public Device findDevice(String sn) {
    return onlineDeviceStore.findDevice(sn);
  }
}
