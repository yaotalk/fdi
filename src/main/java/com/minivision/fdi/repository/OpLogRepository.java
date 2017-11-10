package com.minivision.fdi.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.minivision.fdi.entity.OpLog;

/**
 * 操作日志管理Dao
 * @author hughzhao
 * @2017年5月22日
 */
public interface OpLogRepository extends JpaRepository<OpLog, Long> {

	List<OpLog> findAll();
	
	List<OpLog> findByUsernameOrderByOpTimeDesc(String username);
	
	Page<OpLog> findByUsernameOrderByOpTimeDesc(String username, Pageable pageable);
	
	Page<OpLog> findByUsernameAndOpTimeBetweenOrderByOpTimeDesc(String username, Date startTime, Date endTime, Pageable pageable);
	
}
