package com.minivision.fdi.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.minivision.fdi.entity.Meeting;


public interface MeetRepository extends JpaRepository<Meeting, String>,JpaSpecificationExecutor<Meeting> {
}
