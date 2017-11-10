package com.minivision.fdi.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.minivision.fdi.entity.OpLog;
import com.minivision.fdi.rest.param.OpLogParam;

/**
 * 操作日志管理Service
 * @author hughzhao
 * @2017年5月22日
 */
public interface OpLogService {

	List<OpLog> findAll();

	OpLog create(OpLog opLog);

	Page<OpLog> findOpLogs(Pageable pageable);

	List<OpLog> findByUsername(String username);
	
	Page<OpLog> findByUsername(OpLogParam param);
	
	Page<OpLog> findByUsernameAndOpTimeBetween(OpLogParam param) throws ParseException;
	
}
