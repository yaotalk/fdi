package com.minivision.fdi.service;

import com.minivision.fdi.entity.BizConfig;
import com.minivision.fdi.rest.param.CreateBizConfigParam;
import com.minivision.fdi.rest.param.QueryBizConfigParam;
import com.minivision.fdi.rest.param.UpdateBizConfigParam;

public interface BizConfigService {
  
  BizConfig createBizConfig(CreateBizConfigParam param) throws Exception;
  BizConfig updateBizConfig(UpdateBizConfigParam param) throws Exception;
  BizConfig findConfig(QueryBizConfigParam param);
  
}
