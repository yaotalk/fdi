package com.minivision.fdi.rest.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FaceUpdateParam {

    @NotBlank
    @ApiModelProperty(required = true)
    private String id;

    @ApiModelProperty(value = "不填时默认原始值")
    private String name;

    @ApiModelProperty(value = "不填时默认原始值")
    private String phoneNumber;

    @ApiModelProperty(value = "不填时默认原始值")
    private String companyName;

    @ApiModelProperty(value = "不填时默认原始值")
    private Boolean vip;

    @ApiModelProperty(value = "不填时默认原始值")
    private String position;

    @ApiModelProperty(value = "不填时默认原始值")
    private String idCard;

    @ApiModelProperty(value = "图片，最大不超过10MB,不填时默认原始值")
    private MultipartFile imgFile;

    @ApiModelProperty(value = "二维码,不填时默认原始值")
    private String qrCode;

    @ApiModelProperty(value = "预留字段,不填时默认原始值")
    private String reserveFir;

    @ApiModelProperty(value = "预留字段,不填时默认原始值")
    private String reserveSec;

    @ApiModelProperty(value = "预留字段,不填时默认原始值")
    private String reserveThi;

}
