package org.xueliang.commons.util.excel;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;

/**
 * 将指定jsonArray，转换成Excel
 * @author XueLiang
 * 2017年5月11日 下午11:37:39
 */
public class ExcelWriteUtils {
    
    public final static String EXT_XLS = ".xls";
    public final static String EXT_XLSX = ".xlsx";
    private Workbook workbook;
    private OutputStream outputStream;
    
    public ExcelWriteUtils(OutputStream outputStream, String workbookType) throws IOException {
        this.outputStream = outputStream;
        if (workbookType.endsWith(EXT_XLSX)) {
            workbook = new XSSFWorkbook();
        } else if (workbookType.endsWith(EXT_XLS)) {
            workbook = new HSSFWorkbook();
        }
        if (workbook == null) {
            throw new IllegalArgumentException("invalid excel ext name: " + workbookType);
        }
    }
    
    public ExcelWriteUtils(Workbook workbook) {
        this.workbook = workbook;
    }
    
    /**
     * 根据jsonArray，创建一个sheet
     * @param jsonArray
     * @return
     */
    public Sheet toSheet(JSONArray jsonArray) {
        Sheet sheet = workbook.createSheet();
        for (int i = 0, len = jsonArray.length(); i < len; i++) {
            JSONArray jsonRowArray = jsonArray.getJSONArray(i);
            if (jsonRowArray == null) {
                continue;       //跳过空行
            }
            toRow(sheet, jsonRowArray);
        }
        return sheet;
    }
    
    /**
     * 根据jsonArray，在sheet内尾部追加一行
     * @param sheet
     * @param jsonArray
     * @return
     */
    public Row toRow(Sheet sheet, JSONArray jsonArray) {
        Row row = null;
        if (sheet.getLastRowNum() == 0 && sheet.getPhysicalNumberOfRows() == 0) {
            row = sheet.createRow(0);
        } else {
            row = sheet.createRow(sheet.getLastRowNum() + 1);
        }
        for (int i = 0, len = jsonArray.length(); i < len; i++) {
            row.createCell(i, HSSFCell.CELL_TYPE_STRING);
            setValue(row, i, jsonArray.optString(i));
        }
        return row;
    }
    
    /**
     * 给指定的单元格添加数据
     * @param row
     * @param colIndex
     * @param value
     */
    public void setValue(Row row, int colIndex, String value) {
        row.getCell(colIndex).setCellValue(value);
    }
    
    public void write() throws IOException {
        workbook.write(outputStream);
    }
    
    /**
     * 指定路径，将Excel转成JSONArray
     * @param pathname
     * @return
     * @throws IOException
     */
    public static void toExcel(OutputStream outputStream, JSONArray jsonArray) throws IOException {
        ExcelWriteUtils excelWriteUtils = new ExcelWriteUtils(outputStream, ExcelWriteUtils.EXT_XLSX);
        excelWriteUtils.toSheet(jsonArray);
        excelWriteUtils.write();
    }
}
