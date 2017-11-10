package com.minivision.fdi.rest.param;

import javax.validation.constraints.Max;

import com.minivision.ai.rest.param.RestParam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PageParam extends RestParam {
    private static final long serialVersionUID = 972119139991003154L;

    @ApiModelProperty(value = "起始位置，从0开始")
    private int offset = 0;
    @Max(value = 100, message = "每页条数最大为100")
    @ApiModelProperty(value = "每页条数，默认为20")
    private int limit = 20;
    
}
