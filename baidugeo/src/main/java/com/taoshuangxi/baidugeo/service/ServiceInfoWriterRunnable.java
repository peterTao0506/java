package com.taoshuangxi.baidugeo.service;

import com.taoshuangxi.baidugeo.model.ServiceInfo;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;

import java.io.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * @author taoshuangxi
 * @date 2018-08-03
 * @description: 多线程写入文件,写入excel文件和sql文件
 */
public class ServiceInfoWriterRunnable  implements Runnable{
    private static final Logger logger = Logger.getLogger(ServiceInfoWriterRunnable.class);

    private LinkedBlockingDeque<ServiceInfo> geoQueue;

    public ServiceInfoWriterRunnable(LinkedBlockingDeque<ServiceInfo> geoQueue){
        this.geoQueue = geoQueue;
    }

    @Override
    public void run() {
        logger.info("文件写入线程启动执行");

        String userDir = System.getProperty("user.dir");
        File excelFile = new File(userDir + "\\"+ "serviceInfoGEO.xls");
        if(excelFile.exists()){
            excelFile.delete();
        }

        File sqlFile = new File(userDir + "\\"+ "serviceInfoSQL.sql");
        if(sqlFile.exists()){
            sqlFile.delete();
        }
        FileWriter sqlFileWriter = null;
        BufferedWriter bufferedSqlWriter = null;
        FileOutputStream excelOutputStream = null;
        try {
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("sheet1");
            sheet.setColumnWidth(0, 4000);
            sheet.setColumnWidth(1, 3500);
            HSSFFont font = wb.createFont();
            font.setFontName("Verdana");
            font.setBoldweight((short) 100);
            font.setFontHeight((short) 300);
            font.setColor(HSSFColor.BLUE.index);
            // 创建单元格样式
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            style.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            // 设置边框
            style.setBottomBorderColor(HSSFColor.RED.index);
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            style.setFont(font);// 设置字体
            int i =0;

            sqlFileWriter = new FileWriter(sqlFile);
            bufferedSqlWriter = new BufferedWriter(sqlFileWriter);

            while (true){
                try {
                    ServiceInfo serviceInfo = geoQueue.poll(30, TimeUnit.SECONDS);
                    if(serviceInfo != null) {
                        HSSFRow row = sheet.createRow(i);
                        row.setHeight((short) 500);
                        HSSFCell idCell = row.createCell(0);
                        idCell.setCellType(Cell.CELL_TYPE_STRING);
                        idCell.setCellValue(serviceInfo.getId());
                        HSSFCell codeCell = row.createCell(1);
                        codeCell.setCellType(Cell.CELL_TYPE_STRING);
                        codeCell.setCellValue(serviceInfo.getServiceCode());
                        HSSFCell nameCell = row.createCell(2);
                        nameCell.setCellType(Cell.CELL_TYPE_STRING);
                        nameCell.setCellValue(serviceInfo.getServiceName());
                        HSSFCell addressCell = row.createCell(3);
                        addressCell.setCellType(Cell.CELL_TYPE_STRING);
                        addressCell.setCellValue(serviceInfo.getAddress());
                        HSSFCell xPointCell = row.createCell(4);
                        xPointCell.setCellType(Cell.CELL_TYPE_STRING);
                        xPointCell.setCellValue(serviceInfo.getXpoint());
                        HSSFCell yPointCell = row.createCell(5);
                        yPointCell.setCellType(Cell.CELL_TYPE_STRING);
                        yPointCell.setCellValue(serviceInfo.getYpoint());
                        i++;

                        bufferedSqlWriter.write("update serviceInfo set xPoint = "+ serviceInfo.getXpoint() +" , yPoint = "+ serviceInfo.getYpoint() +" where service_code = "+ serviceInfo.getServiceCode() +" and service_name="+ serviceInfo.getServiceName()+";\r\n\r\n");
                    }else{
                        break;
                    }
                }catch (InterruptedException ex){
                    logger.error("线程出错", ex);
                    break;
                }
            }

            excelOutputStream = new FileOutputStream(excelFile);
            wb.write(excelOutputStream);
        } catch (Exception e) {
            logger.error("将爬取经纬度数据写入文件出错", e);
        }finally {
            close(bufferedSqlWriter);
            close(sqlFileWriter);
            close(excelOutputStream);
        }
    }

    private void close(Closeable closeable){
        if(closeable != null){
            try{
                closeable.close();
            }catch (Exception e){
            }
        }
    }
}
