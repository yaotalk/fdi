package com.minivision.fdi.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.minivision.fdi.entity.Stats;
import com.minivision.fdi.rest.param.CreateStatsParam;
import com.minivision.fdi.rest.param.QueryStatsParam;
import com.minivision.fdi.rest.result.FaceRecognitionResult;

public interface StatsService {

  Stats createStats(CreateStatsParam stats);
  List<Stats> createBatch(List<CreateStatsParam> statsList);
  
  Page<FaceRecognitionResult> findByPage(QueryStatsParam param);
  
}
