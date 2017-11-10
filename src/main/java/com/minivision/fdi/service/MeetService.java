package com.minivision.fdi.service;

import com.minivision.fdi.rest.param.MeetAddParam;
import com.minivision.fdi.rest.param.MeetParam;
import com.minivision.fdi.rest.result.common.PageResult;
import com.minivision.fdi.rest.result.MeetingAddResult;
import com.minivision.fdi.rest.result.MeetingUpdateResult;
import com.minivision.fdi.entity.Meeting;
import com.minivision.fdi.exception.ServiceException;
import com.minivision.fdi.rest.param.MeetUpdateParam;

public interface MeetService {

      MeetingAddResult addMeeting(MeetAddParam addParam)throws ServiceException;

      PageResult<Meeting> findByPlat(MeetParam meetParam);

      MeetingUpdateResult update(MeetUpdateParam updateParam) throws ServiceException;

      MeetingUpdateResult delete(String id) throws ServiceException;
}
