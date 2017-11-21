package com.minivision.fdi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.minivision.fdi.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {
  
  Device findBySn(String sn);
  
  long deleteByIdIn(List<Long> ids);
  
  List<Device> findByMeetingToken(String meetingToken);
  
  @Modifying(clearAutomatically = true)
  @Query(value = "update device d set d.activated = 1,d.activate_time = now() where d.sn = ?1", nativeQuery = true)
  void activate(String deviceSn);
  
  @Modifying(clearAutomatically = true)
  @Query(value = "update device d set d.online = 1 where d.sn = ?1", nativeQuery = true)
  void online(String deviceSn);
  
  @Modifying(clearAutomatically = true)
  @Query(value = "update device d set d.online = 0 where d.sn = ?1", nativeQuery = true)
  void offline(String deviceSn);

}
