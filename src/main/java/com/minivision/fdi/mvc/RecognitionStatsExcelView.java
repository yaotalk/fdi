package com.minivision.fdi.mvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import com.minivision.fdi.common.CommonConstants;
import com.minivision.fdi.rest.result.FaceRecognitionResult;

/**
 * 人脸比对信息统计报表导出
 * @author hughzhao
 * @2017年11月3日
 */
public class RecognitionStatsExcelView extends AbstractXlsView {

  @Override
  protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
      HttpServletRequest request, HttpServletResponse response) throws Exception {
    @SuppressWarnings("unchecked")
    Page<FaceRecognitionResult> data = (Page<FaceRecognitionResult>) model.get("data");

    List<FaceRecognitionResult> statsList = null;
    if (data != null && data.hasContent()) {
      statsList = data.getContent();
    } else {
      statsList = Collections.emptyList();
    }

    // 设置文件名
    response.setHeader("Content-Disposition", "attachment;filename=" + new String("人脸比对信息统计".getBytes(), "ISO-8859-1") + ".xls");  

    // sheet的名称  
    HSSFSheet sheet = (HSSFSheet) workbook.createSheet("人脸比对信息统计"); 
    HSSFCellStyle headerStyle = (HSSFCellStyle) workbook.createCellStyle(); //标题样式
    headerStyle.setAlignment(HorizontalAlignment.CENTER);
    headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    HSSFFont headerFont = (HSSFFont) workbook.createFont();    //标题字体
    headerFont.setBold(true);
    headerFont.setFontHeightInPoints((short)12);
    headerStyle.setFont(headerFont);
    short width = 20, height = 25 * 16;
    sheet.setDefaultColumnWidth(width);

    HSSFRow row = null;  
    HSSFCell cell = null;

    String[] titles = {"会议名称","会议地址","公司名称","姓名","是否VIP","人脸照片","抓拍照片","识别结果","相似度","识别时间"};

    // 行号  
    int rowIndex = 0;  
    // 列号  
    int cellIndex = 0;  

    // 通过sheet对象增加一行  
    row = sheet.createRow(rowIndex++);
    for (String title : titles) {
      // 通过row对象增加一列  
      cell = row.createCell(cellIndex++);
      cell.setCellStyle(headerStyle);
      // 设置列的内容  
      cell.setCellValue(title);
    }
    sheet.getRow(0).setHeight(height);

    HSSFCellStyle contentStyle = (HSSFCellStyle) workbook.createCellStyle(); //内容样式
    contentStyle.setAlignment(HorizontalAlignment.CENTER);
    for (FaceRecognitionResult recognition : statsList) {
      // 列号清零  
      cellIndex = 0;
      // 增加一行  
      row = sheet.createRow(rowIndex++); 
      row.setRowStyle(contentStyle);
      // 增加一列  
      cell = row.createCell(cellIndex++);  
      // 设置列的内容  
      cell.setCellStyle(contentStyle);
      cell.setCellValue(Optional.ofNullable(recognition.getMeetingName()).orElse(""));
      // 增加一列  
      cell = row.createCell(cellIndex++);  
      // 设置列的内容  
      cell.setCellStyle(contentStyle);
      cell.setCellValue(Optional.ofNullable(recognition.getAddress()).orElse(""));
      // 增加一列  
      cell = row.createCell(cellIndex++);  
      // 设置列的内容  
      cell.setCellStyle(contentStyle);
      cell.setCellValue(Optional.ofNullable(recognition.getCompanyName()).orElse(""));
      // 增加一列  
      cell = row.createCell(cellIndex++); 
      // 设置列的内容  
      cell.setCellStyle(contentStyle);
      cell.setCellValue(Optional.ofNullable(recognition.getName()).orElse(""));
      // 增加一列  
      cell = row.createCell(cellIndex++); 
      // 设置列的内容  
      cell.setCellStyle(contentStyle);
      if (recognition.getVip() != null) {
        cell.setCellValue(recognition.getVip()?"是":"否");
      } else {
        cell.setCellValue("");
      }

      // 增加一列  
      cell = row.createCell(cellIndex++); 
      // 设置列的内容  
      cell.setCellStyle(contentStyle);
      cell.setCellValue(Optional.ofNullable(recognition.getImgPath()).orElse(""));
      // 增加一列  
      cell = row.createCell(cellIndex++); 
      // 设置列的内容  
      cell.setCellStyle(contentStyle);
      cell.setCellValue(Optional.ofNullable(recognition.getCapImgUrl()).orElse(""));
      // 增加一列  
      cell = row.createCell(cellIndex++); 
      // 设置列的内容  
      cell.setCellStyle(contentStyle);
      if (recognition.getSuccess() != null) {
        cell.setCellValue(recognition.getSuccess()?"成功":"失败");
      } else {
        cell.setCellValue("");
      }
      // 增加一列  
      cell = row.createCell(cellIndex++); 
      // 设置列的内容  
      cell.setCellStyle(contentStyle);
      cell.setCellValue(Optional.ofNullable(recognition.getConfidence()).orElse(0.0f));
      // 增加一列  
      cell = row.createCell(cellIndex++); 
      // 设置列的内容  
      cell.setCellStyle(contentStyle);
      cell.setCellValue(DateFormatUtils.format(recognition.getDetectTime(), CommonConstants.FULL_DATE_FORMAT));
    }
  }

}
