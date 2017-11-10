package com.minivision.fdi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.minivision.fdi.entity.Stats;

public interface StatsRepository extends JpaRepository<Stats, Long> {
  
  @Query(value = "select count(distinct face_id) from stats where meeting_token = ?1", nativeQuery = true)
  int findDistinctByMeetingToken(String meetingToken);

}
