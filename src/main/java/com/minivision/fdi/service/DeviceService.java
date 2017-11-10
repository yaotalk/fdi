package com.minivision.fdi.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.minivision.fdi.entity.Device;
import com.minivision.fdi.rest.param.CreateDeviceParam;
import com.minivision.fdi.rest.param.QueryDeviceParam;
import com.minivision.fdi.rest.param.UpdateDeviceParam;

public interface DeviceService {

  Device createDevice(CreateDeviceParam device);
  
  List<Device> createBatch(Iterable<Device> devices);
  
  Device updateDevice(UpdateDeviceParam device);
  
  void deleteDevice(Long deviceId);
  
  void deleteBatch(List<Long> deviceIds);
  
  Page<Device> findByPage(QueryDeviceParam param);
  
}
