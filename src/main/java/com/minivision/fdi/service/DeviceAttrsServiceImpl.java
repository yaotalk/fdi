package com.minivision.fdi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minivision.fdi.repository.DeviceAttrsRepository;

@Service
@Transactional(rollbackFor={Exception.class})
public class DeviceAttrsServiceImpl implements DeviceAttrsService {

  @Autowired
  private DeviceAttrsRepository deviceAttrsRepo;
  
}
