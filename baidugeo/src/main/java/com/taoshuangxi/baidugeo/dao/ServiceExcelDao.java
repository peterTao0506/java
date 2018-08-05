package com.taoshuangxi.baidugeo.dao;

import com.taoshuangxi.baidugeo.model.ServiceInfo;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author taoshuangxi
 * @date 2018-08-03
 * 原始场所信息来自excel文件
 */
public class ServiceExcelDao extends ServiceDao{
    private static final Logger logger = Logger.getLogger(ServiceExcelDao.class);

    @Override
    public List<ServiceInfo> getServiceInfoList(String excelFilePath) {
        List<ServiceInfo> serviceInfoList = new ArrayList<>();

        try {
            InputStream input = new FileInputStream(excelFilePath); // 建立输入流
            Workbook wb = null;
            // 根据文件格式(2003或者2007)来初始化
            if (excelFilePath.endsWith("xlsx")) {
                wb = new XSSFWorkbook(input);
            }else {
                wb = new HSSFWorkbook(input);
            }
            Sheet sheet = wb.getSheetAt(0); // 获得第一个表单

            Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
            while (rows.hasNext()) {
                ServiceInfo serviceInfo = new ServiceInfo();
                Row row = rows.next(); // 获得行数据
                Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
                while (cells.hasNext()) {
                    Map<String, BigDecimal> result = new HashMap<>();
                    Cell cell = cells.next();
                    String value = getCellValue(cell);

                    if (cell.getColumnIndex() == 0) {
                        serviceInfo.setId(value);
                        if(value.equals("id")){
                            break;
                        }
                    }else if (cell.getColumnIndex() == 1) {
                        serviceInfo.setServiceCode(value);
                    }else if (cell.getColumnIndex() == 2) {
                        serviceInfo.setServiceName(value);
                    }else if (cell.getColumnIndex() == 3) {
                        serviceInfo.setAddress(value);
                    }
                }
                serviceInfoList.add(serviceInfo);
            }
        } catch (Exception ex) {
            //标准用法，使用log4j, logback之类的日志收集器
            logger.error("读取excel文件出错", ex);
        }
        return serviceInfoList;
    }

    private String getCellValue(Cell cell) {
        Object result = "";
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    result = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    result = cell.getNumericCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    result = cell.getBooleanCellValue();
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    result = cell.getCellFormula();
                    break;
                case Cell.CELL_TYPE_ERROR:
                    result = cell.getErrorCellValue();
                    break;
                case Cell.CELL_TYPE_BLANK:
                    break;
                default:
                    break;
            }
        }
        return result.toString();
    }
}
