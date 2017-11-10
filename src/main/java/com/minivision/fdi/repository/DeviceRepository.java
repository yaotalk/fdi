package com.minivision.fdi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minivision.fdi.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {
  
  Device findBySn(String sn);
  
  long deleteByIdIn(List<Long> ids);
  
  List<Device> findByMeetingToken(String meetingToken);

}
