package com.minivision.fdi.rest;
import com.minivision.fdi.rest.param.FaceAddParam;
import com.minivision.fdi.rest.param.FaceDelParam;
import com.minivision.fdi.rest.param.FaceUpdateParam;
import com.minivision.fdi.rest.result.FaceAddResult;
import com.minivision.fdi.rest.result.FaceDelResult;
import com.minivision.fdi.rest.result.FaceUpdateResult;
import com.minivision.fdi.rest.result.common.PageResult;
import com.minivision.fdi.rest.result.common.RestResult;
import com.minivision.fdi.annotation.Log;
import com.minivision.fdi.entity.Face;
import com.minivision.fdi.rest.param.FaceParam;
import com.minivision.fdi.service.FaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/face")
@Api(tags = "face", value = "face Apis")
public class FaceApi {

    @Autowired
    private FaceService faceService;

    @GetMapping("list")
    @ApiOperation(value="list", notes="face list")
    @Log(module = "人脸库管理", operation = "查询人脸信息")
    public RestResult<PageResult<Face>> faces(FaceParam faceParam){
          PageResult<Face> faces = faceService.findFaceInfos(faceParam);
          return  new RestResult<>(faces);
    }

    @PostMapping("create")
    @ApiOperation(value = "add face",notes = "add face")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "imgFile", paramType = "form", dataType = "file",value = "最大不超过10M")})
    @Log(module = "人脸库管理", operation = "添加人脸")
    public RestResult<FaceAddResult> add(@Validated @ModelAttribute FaceAddParam faceAddParam) throws Exception {

        FaceAddResult faceAddResult = faceService.addFace(faceAddParam);
        return new RestResult<>(faceAddResult);
    }

    @PostMapping("update")
    @ApiOperation(value = "update face",notes = "update face")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "imgFile", paramType = "form", dataType = "file",value ="最大不超过10M" )})
    @Log(module = "人脸库管理", operation = "更新人脸信息")
    public RestResult<FaceUpdateResult> update(@Validated @ModelAttribute  FaceUpdateParam faceUpdateParam) throws Exception{
        FaceUpdateResult faceUpdateResult = faceService.update(faceUpdateParam);
        return new RestResult<>(faceUpdateResult);
    }

    @PostMapping("delete")
    @Log(module = "人脸库管理", operation = "删除人脸")
    public RestResult<FaceDelResult> delete(@Validated @ModelAttribute FaceDelParam faceDelParam) throws Exception{
        return new RestResult<>(faceService.delete(faceDelParam));
    }

}

