package com.minivision.fdi.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.minivision.ai.util.ChunkRequest;
import com.minivision.fdi.common.BeanPropertyUtils;
import com.minivision.fdi.device.OnlineDeviceStore;
import com.minivision.fdi.entity.Device;
import com.minivision.fdi.mqtt.protocol.ActiveCodeUtil;
import com.minivision.fdi.repository.DeviceRepository;
import com.minivision.fdi.repository.MeetRepository;
import com.minivision.fdi.rest.param.CreateDeviceParam;
import com.minivision.fdi.rest.param.QueryDeviceParam;
import com.minivision.fdi.rest.param.UpdateDeviceParam;

@Service
@Transactional(rollbackFor={Exception.class})
public class DeviceServiceImpl implements DeviceService {
  
  @Autowired
  private DeviceRepository deviceRepo;
  
  @Autowired 
  private MeetRepository meetRepository;
  
  @Autowired
  private ActiveCodeUtil activeCodeUtil;
  
  @Override
  public Device createDevice(CreateDeviceParam device) {
    Device deviceBase = new Device();
    BeanUtils.copyProperties(device, deviceBase);
    if (StringUtils.hasText(device.getMeetingToken())) {
      deviceBase.setMeeting(meetRepository.findOne(device.getMeetingToken()));
    }
    return deviceRepo.saveAndFlush(deviceBase);
  }

  @Override
  public List<Device> createBatch(Iterable<Device> devices) {
    return deviceRepo.save(devices);
  }

  @Override
  public Device updateDevice(UpdateDeviceParam device) {
    Device existed = deviceRepo.findOne(device.getDeviceId());
    BeanPropertyUtils.copyProperties(device, existed);
    if (StringUtils.hasText(device.getMeetingToken())) {
      existed.setMeeting(meetRepository.findOne(device.getMeetingToken()));
    }
    return deviceRepo.saveAndFlush(existed);
  }

  @Override
  public void deleteDevice(Long deviceId) {
    deviceRepo.delete(deviceId);
  }

  @Override
  public void deleteBatch(List<Long> deviceIds) {
    //deviceRepo.deleteInBatch(deviceRepo.findAll(deviceIds));
    deviceRepo.deleteByIdIn(deviceIds);
  }

  @Override
  public Page<Device> findByPage(QueryDeviceParam param) {
    ExampleMatcher matcher = ExampleMatcher.matching()
        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        .withIgnoreCase(true);
    Device device = new Device();
    BeanUtils.copyProperties(param, device);
    Example<Device> example = Example.of(device, matcher);
    return deviceRepo.findAll(example, new ChunkRequest(param.getOffset(), param.getLimit(), new Sort(Direction.DESC, "updateTime")));
  }

  @Override
  public void activateDevice(String deviceSn) {
    deviceRepo.activate(deviceSn);
  }
  
  
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

  @Override
  public String getActivationCode(String model, String sn) {
    return activeCodeUtil.generateActiveCode(model, sn);
  }
  
}
