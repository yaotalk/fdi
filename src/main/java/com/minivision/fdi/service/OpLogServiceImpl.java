package com.minivision.fdi.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minivision.ai.util.ChunkRequest;
import com.minivision.fdi.common.CommonConstants;
import com.minivision.fdi.entity.OpLog;
import com.minivision.fdi.repository.OpLogRepository;
import com.minivision.fdi.rest.param.OpLogParam;

@Service
@Transactional(rollbackFor={Exception.class})
public class OpLogServiceImpl implements OpLogService {

  @Autowired
  private OpLogRepository opLogRepo;

  @Override
  public List<OpLog> findAll() {
    return opLogRepo.findAll();
  }

  @Override
  public OpLog create(OpLog opLog) {
    return opLogRepo.save(opLog);
  }

  @Override
  public Page<OpLog> findOpLogs(Pageable pageable) {
    return opLogRepo.findAll(pageable);
  }

  @Override
  public List<OpLog> findByUsername(String username) {
    return opLogRepo.findByUsernameOrderByOpTimeDesc(username);
  }

  @Override
  public Page<OpLog> findByUsername(OpLogParam param) {
    return opLogRepo.findByUsernameOrderByOpTimeDesc(param.getUsername(), new ChunkRequest(param.getOffset(), param.getLimit()));
  }

  @Override
  public Page<OpLog> findByUsernameAndOpTimeBetween(OpLogParam param) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.FULL_DATE_FORMAT);
    Date startDate = sdf.parse(param.getStartTime());
    Date endDate = sdf.parse(param.getEndTime());
    return opLogRepo.findByUsernameAndOpTimeBetweenOrderByOpTimeDesc(param.getUsername(), startDate, endDate, new ChunkRequest(param.getOffset(), param.getLimit()));
  }

}
