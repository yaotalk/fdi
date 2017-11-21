package com.minivision.fdi.rest.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class FaceAddParam {

    @NotNull
    @NotBlank
    @ApiModelProperty(required = true,value = "会议token")
    private String meetToken;

    @NotNull
    @ApiModelProperty(required = true)
    private String name;

    private Boolean vip;

    @ApiModelProperty(value = "职务")
    private String position;

    private String phoneNumber;

    private String companyName;

    private String idCard;

    @ApiModelProperty(value = "图片，最大不超过10MB")
    MultipartFile imgFile;

    @ApiModelProperty(value = "二维码")
    private String qrCode;

    @ApiModelProperty(value = "预留字段")
    private String reserveFir;

    @ApiModelProperty(value = "预留字段")
    private String reserveSec;

    @ApiModelProperty(value = "预留字段")
    private String reserveThi;
}
