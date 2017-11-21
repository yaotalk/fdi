package com.minivision.fdi.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.minivision.fdi.entity.Device;
import com.minivision.fdi.repository.DeviceRepository;

@Component
public class DefaultOnlineDeviceStoreImpl implements OnlineDeviceStore {
  
  private Map<String, Device> onlineDevices = new HashMap<>();
  private Map<String, Device> lostDevices = new HashMap<>();
  
  @Autowired
  private DeviceRepository deviceRepository;
  
  public void online(String sn) {
    Device device = deviceRepository.findBySn(sn);
    
    Assert.notNull(device, "Illegal device attempt to online, sn : "+ sn);
    
    deviceRepository.online(sn);
    device.setOnline(true);
    
    lostDevices.remove(sn);
    onlineDevices.put(sn, device);
  }
  
  public void offline(String sn) {
    lostDevices.remove(sn);
    onlineDevices.remove(sn);
    
    deviceRepository.offline(sn);
  }
  
  public void lost(String sn) {
    Device device = onlineDevices.remove(sn);
    if (device == null) {
      device = deviceRepository.findBySn(sn);
    }
    
    if(device != null) {
      device.setOnline(false);
      lostDevices.put(sn, device);
      deviceRepository.offline(sn);
    }
  }
  
  public List<Device> getOnlineDevices(){
    return onlineDevices.values().stream().collect(Collectors.toList());
  }

  @Override
  public Device findDevice(String sn) {
    return deviceRepository.findBySn(sn);
  }

  @Override
  public Device findOnlineDevice(String sn) {
    return onlineDevices.get(sn);
  }
  
}
