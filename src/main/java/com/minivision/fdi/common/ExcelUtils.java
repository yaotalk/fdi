package com.minivision.fdi.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.minivision.fdi.entity.BizConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ExcelUtils {

  public static String getCellValue(Cell cell) {  
    String cellValue = "";  
    if(cell == null){  
      return cellValue;  
    }  
    //把数字当成String来读，避免出现1读成1.0的情况  
    if(cell.getCellTypeEnum() == CellType.NUMERIC){  
      cell.setCellType(CellType.STRING);  
    }  
    //判断数据的类型  
    switch (cell.getCellTypeEnum()) {  
      case NUMERIC: //数字  
        cellValue = String.valueOf(cell.getNumericCellValue());  
        break;  
      case STRING: //字符串  
        cellValue = cell.getStringCellValue();  
        break;  
      case BOOLEAN: //Boolean  
        cellValue = String.valueOf(cell.getBooleanCellValue());  
        break;  
      case FORMULA: //公式  
        cellValue = cell.getCellFormula();  
        break;  
      case BLANK: //空值   
        cellValue = "";  
        break;  
      case ERROR: //故障  
        cellValue = "非法字符";  
        break;  
      default:  
        cellValue = "未知类型";  
        break;  
    }  
    return cellValue;  
  }

  public static Workbook createWorkbook(InputStream is, String excelFileName) throws IOException {
    if (excelFileName.endsWith(".xls")) {
      return new HSSFWorkbook(is);
    }else if (excelFileName.endsWith(".xlsx")) {
      return new XSSFWorkbook(is);
    }
    return null;
  }

  public static <T> List<T> importDataFromExcel(Class<T> dataType, InputStream is, String excelFileName) {
    List<T> list = new ArrayList<>();
    
    try {
      if (is == null || is.available() == 0) {
        return list;
      }
      //创建工作簿
      Workbook workbook = createWorkbook(is, excelFileName);
      if (workbook == null) {
        return list;
      }
      //创建工作表sheet
      Sheet sheet = workbook.getSheetAt(0);
      //获取sheet中数据的行数
      int rows = sheet.getPhysicalNumberOfRows();
      //获取表头单元格个数
      int cells = sheet.getRow(0).getPhysicalNumberOfCells();
      //利用反射，给JavaBean的属性进行赋值
      Field[] fields = dataType.getDeclaredFields();
      //第一行为标题栏，从第二行开始取数据
      for (int i = 1; i < rows; i++) {
        Row row = sheet.getRow(i);
        int index = 0;
        T t = dataType.newInstance();
        while (index < cells) {
          Cell cell = row.getCell(index);
          if (null == cell) {
            cell = row.createCell(index);
          }
          cell.setCellType(CellType.STRING);
          String text = cell.getStringCellValue();

          Field field = fields[index];
          String fieldName = field.getName();
          String methodName = "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
          Method setMethod = dataType.getMethod(methodName, new Class[]{field.getType()});
          Object value = text;
          if (field.getType() == Boolean.class) {
            value = text == null ? null : Boolean.valueOf(text);
          }
          if (field.getType() == Integer.class) {
            value = text == null ? null : Integer.valueOf(text);
          }
          if (field.getType() == Long.class) {
            value = text == null ? null : Long.valueOf(text);
          }
          if (field.getType() == Float.class) {
            value = text == null ? null : Float.valueOf(text);
          }
          if (field.getType() == Double.class) {
            value = text == null ? null : Double.valueOf(text);
          }
          if (field.getType() == Byte.class) {
            value = text == null ? null : Byte.valueOf(text);
          }
          if (field.getType() == Short.class) {
            value = text == null ? null : Short.valueOf(text);
          }
          if (field.getType() == Date.class) {
            SimpleDateFormat format = new SimpleDateFormat(CommonConstants.FULL_DATE_FORMAT);
            value = text == null ? null : format.parse(text);
          }
          setMethod.invoke(t, new Object[]{value});
          index++;
        }
        list.add(t);
      }
    } catch (Throwable e) {
      log.error("读取Excel文件失败", e);
    }
    return list;
  }

  public static void exportExcel(Workbook workbook, String sheetTitle, String[] titles, List<Object[]> dataList) {
    // sheet的名称  
    HSSFSheet sheet = (HSSFSheet) workbook.createSheet(sheetTitle); 
    //标题样式
    HSSFCellStyle headerStyle = (HSSFCellStyle) workbook.createCellStyle(); 
    headerStyle.setAlignment(HorizontalAlignment.CENTER);
    headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    //标题字体
    HSSFFont headerFont = (HSSFFont) workbook.createFont();    
    headerFont.setBold(true);
    headerFont.setFontHeightInPoints((short)12);
    headerStyle.setFont(headerFont);
    short width = 20, height = 25 * 16;
    sheet.setDefaultColumnWidth(width);

    HSSFRow row = null;  
    HSSFCell cell = null;

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

    //内容样式
    HSSFCellStyle contentStyle = (HSSFCellStyle) workbook.createCellStyle(); 
    contentStyle.setAlignment(HorizontalAlignment.CENTER);
    for (Object[] values : dataList) {
      // 列号清零  
      cellIndex = 0;
      // 增加一行  
      row = sheet.createRow(rowIndex++); 
      row.setRowStyle(contentStyle);
      for (Object object : values) {
        // 增加一列  
        cell = row.createCell(cellIndex++);  
        // 设置列的内容  
        cell.setCellStyle(contentStyle);
        if (object instanceof Date) {
          cell.setCellValue(DateFormatUtils.format((Date)object, CommonConstants.FULL_DATE_FORMAT));
        } else {
          cell.setCellValue(Optional.ofNullable("" + object).orElse(""));
        }
      }
    }
  }
  
  public static void main(String[] args) {
    String filePath = "C:\\Users\\hughzhao\\Desktop\\test.xlsx";
    try(FileInputStream inputStream = new FileInputStream(filePath);) {
      List<BizConfig> list = importDataFromExcel(BizConfig.class, inputStream, "test.xlsx");
      System.out.println(list);
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

}
