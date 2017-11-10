package com.minivision.fdi.device;

import java.util.List;

import com.minivision.fdi.entity.Device;

public interface OnlineDeviceStore {

  public void online(String sn);
  
  public void offline(String sn);
  
  public void lost(String sn);
  
  public Device findDevice(String sn);
  
  public Device findOnlineDevice(String sn);
  
  public List<Device> getOnlineDevices(); 
  
}
