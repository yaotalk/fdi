package com.minivision.fdi.service;

import com.minivision.fdi.domain.FaceRecMsg;
import com.minivision.fdi.domain.QrRecMsg;
import com.minivision.fdi.rest.param.FaceAddParam;
import com.minivision.fdi.rest.param.FaceDelParam;
import com.minivision.fdi.rest.param.FaceUpdateParam;
import com.minivision.fdi.rest.result.FaceAddResult;
import com.minivision.fdi.rest.result.FaceDelResult;
import com.minivision.fdi.rest.result.FaceUpdateResult;
import com.minivision.fdi.rest.result.common.PageResult;
import com.minivision.fdi.entity.Face;
import com.minivision.fdi.exception.ServiceException;
import com.minivision.fdi.rest.param.FaceParam;

public interface FaceService {

    PageResult<Face> findFaceInfos(FaceParam faceParam);

    FaceAddResult addFace(FaceAddParam faceAddParam) throws ServiceException;

    FaceUpdateResult update(FaceUpdateParam faceUpdateParam) throws ServiceException;

    FaceDelResult delete(FaceDelParam faceDelParam);

    Face search(FaceRecMsg faceRecMsg) throws RuntimeException;

    Face searchByQrCode(QrRecMsg regMsg);
}
