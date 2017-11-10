package com.minivision.fdi.rest.result.common;

import org.springframework.data.domain.Page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@ApiModel
public class PageResult<T> {

  @ApiModelProperty(value = "总页数")
  private int pages;
  @ApiModelProperty(value = "总条数")
  private long total;
  @ApiModelProperty(value = "每页数据列表")
  private List<T> rows;

  public PageResult(long total, List<T> rows) {
    this.total = total;
    this.rows = rows;
  }

  public PageResult(Page<T> page) {
    this.total = page.getTotalElements();
    this.rows = page.getContent();
  }

}
