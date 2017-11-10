package com.minivision.fdi.rest.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class FaceDelParam {

    @NotNull(message = "ids must not be null")
    @ApiModelProperty(value = "逗号分割",required = true)
    private String ids;

    @NotNull(message = "faceSetToken must not be null")
    @ApiModelProperty(required = true)
    private String meetToken;

}
