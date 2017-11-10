package com.minivision.fdi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.minivision.fdi.entity.MeetingManage;

public interface MeetingManageRepository extends JpaRepository<MeetingManage, Long>, JpaSpecificationExecutor<MeetingManage> {

  @Modifying(clearAutomatically = true)
  @Query("update MeetingManage m set m.attendance = ?1 where m.token = ?2")
  void updateAttendance(Integer amount, String meetingToken);
  
  @Modifying(clearAutomatically = true)
  @Query("update MeetingManage m set m.attendance = m.attendance + ?1 where m.token = ?2")
  void incrementAttendance(Integer amount, String meetingToken);
  
  @Modifying(clearAutomatically = true)
  @Query("update MeetingManage m set m.attendance = m.attendance + 1 where m.token = ?1")
  void incrementAttendance(String meetingToken);
  
  @Modifying(clearAutomatically = true)
  @Query("update MeetingManage m set m.enrollment = ?1 where m.token = ?2")
  void updateEnrollment(Integer amount, String meetingToken);
  
  @Modifying(clearAutomatically = true)
  @Query("update MeetingManage m set m.enrollment = m.enrollment + ?1 where m.token = ?2")
  void incrementEnrollment(Integer amount, String meetingToken);
  
  @Modifying(clearAutomatically = true)
  @Query("update MeetingManage m set m.enrollment = m.enrollment + 1 where m.token = ?1")
  void incrementEnrollment(String meetingToken);
  
  MeetingManage findByToken(String meetingToken);
  
  void deleteByToken(String meetingToken);
  
  void deleteByIdIn(List<Long> ids);
  
}
