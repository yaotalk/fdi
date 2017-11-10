package com.minivision.fdi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minivision.fdi.entity.BizConfig;

public interface BizConfigRepository extends JpaRepository<BizConfig, Long> {

  BizConfig findByMeetingToken(String meetingToken);
  BizConfig findByDeviceSn(String deviceSn);
  
}
