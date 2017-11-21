package com.minivision.fdi.mqtt.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Component;

import com.minivision.fdi.entity.Device;
import com.minivision.fdi.service.DeviceService;

import io.moquette.spi.security.IAuthenticator;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FaceDeviceAuthenticator implements IAuthenticator {
  @Autowired
  private DeviceService deviceService;
  @Autowired
  private StandardPasswordEncoder passwordEncoder;
  
  @Override
  public boolean checkValid(String clientId, String username, byte[] password) {
    Device device = deviceService.findDevice(clientId);
    if(device == null){
      log.warn("Illegal device attempt to connect, sn: {}", clientId);
      return false;
    }
    String model = device.getModel();
    String sn = device.getSn();
    
    boolean matches = passwordEncoder.matches(model+"::"+sn, Utf8.decode(password));
    //boolean matches = passwordEncoder.matches("pad::8", Utf8.decode(password));
    
    if(!matches){
      log.warn("Device attempt to login with wrong password : {}", device);
    }
    return matches;
  }
}
