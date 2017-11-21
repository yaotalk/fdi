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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/face")
@Api(tags = "face", value = "face Apis")
@PreAuthorize("hasAuthority('FACE_PEOPLE')")
public class FaceApi {

    @Autowired
    private FaceService faceService;

    @GetMapping("list")
    @ApiOperation(value="查询人员信息", notes="查询人员信息")
    @Log(module = "人脸库管理", operation = "查询人脸信息")
    public RestResult<PageResult<Face>> faces(FaceParam faceParam){
          PageResult<Face> faces = faceService.findFaceInfos(faceParam);
          return  new RestResult<>(faces);
    }

    @PostMapping("create")
    @ApiOperation(value = "添加人员",notes = "添加人员")
    @Log(module = "人脸库管理", operation = "添加人脸")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "imgFile", paramType = "form", dataType = "__file",value = "图片，最大不超过10MB")})
    public RestResult<FaceAddResult> add(@Validated @ModelAttribute FaceAddParam faceAddParam) throws Exception {

        FaceAddResult faceAddResult = faceService.addFace(faceAddParam);
        return new RestResult<>(faceAddResult);
    }

    @PostMapping("update")
    @ApiOperation(value = "更新人员信息",notes = "更新人员信息")
    @Log(module = "人脸库管理", operation = "更新人脸信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "imgFile", paramType = "form", dataType = "__file",value = "图片，最大不超过10MB")})
    public RestResult<FaceUpdateResult> update(@Validated @ModelAttribute  FaceUpdateParam faceUpdateParam) throws Exception{
        FaceUpdateResult faceUpdateResult = faceService.update(faceUpdateParam);
        return new RestResult<>(faceUpdateResult);
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除人员",notes = "删除人员")
    @Log(module = "人脸库管理", operation = "删除人脸")
    public RestResult<FaceDelResult> delete(@Validated @ModelAttribute FaceDelParam faceDelParam) throws Exception{
        return new RestResult<>(faceService.delete(faceDelParam));
    }

}

