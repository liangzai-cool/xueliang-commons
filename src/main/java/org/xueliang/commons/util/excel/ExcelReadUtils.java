package org.xueliang.commons.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读取Excel，将其转为JSONArray
 * @author XueLiang
 * @since 2016-03-26 01:38
 */
public class ExcelReadUtils {
	
	/** 用来记录日志 **/
	private final static Logger log = LoggerFactory.getLogger(ExcelReadUtils.class);
	
	private final static String EXT_XLS = ".xls";
	private final static String EXT_XLSX = ".xlsx";
	private final static DecimalFormat df = new DecimalFormat("00");
	private Workbook workbook;
	
	public ExcelReadUtils(String pathname) throws IOException {
	    this(new File(pathname));
    }
	
    public ExcelReadUtils(File file) throws IOException {
        String fileName = file.getName();
        InputStream inputStream = new FileInputStream(file);
        if (fileName.endsWith(EXT_XLSX)) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (fileName.endsWith(EXT_XLS)) {
            workbook = new HSSFWorkbook(inputStream);
        }
        inputStream.close();
        if (workbook == null) {
            throw new IllegalArgumentException("invalid excel: " + fileName);
        }
        workbook.close();
    }
    
    public ExcelReadUtils(Workbook workbook) {
        this.workbook = workbook;
    }
	
	/**
	 * 指定路径，将Excel转成JSONArray
	 * @param pathname
	 * @return
	 * @throws IOException
	 */
	public static JSONArray toJSONArray(String pathname) throws IOException {
		File file = new File(pathname);
		return toJSONArray(file);
	}
	
	/**
	 * 指定文件对象，将Excel转成JSONArray
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static JSONArray toJSONArray(File file) throws IOException {
		ExcelReadUtils excelUtils = new ExcelReadUtils(file);
		return toJSONArray(excelUtils.workbook);
	}
	
	/**
	 * 将指定的Excel转成JSONArray
	 * @param workbook
	 * @return
	 */
	public static JSONArray toJSONArray(Workbook workbook) {
		ExcelReadUtils excelUtils = new ExcelReadUtils(workbook);
		return excelUtils.toJSONArray();
	}
	
	/**
	 * 将Excel转成JSONArray
	 * @return
	 */
	public JSONArray toJSONArray() {
		JSONArray jsonExcel = new JSONArray();
		for (int i = 0, len = workbook.getNumberOfSheets(); i < len; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			JSONArray jsonSheet = sheetToJSONArray(sheet);
			jsonExcel.put(jsonSheet);
		}
		return jsonExcel;
	}
	
	/**
	 * 将指定的表格，转成JSONArray
	 * @param sheet
	 * @return
	 */
	public JSONArray sheetToJSONArray(Sheet sheet) {
		JSONArray jsonSheet = new JSONArray();
		for (int i = 0, len = sheet.getLastRowNum(); i <= len; i++) {
			Row row = sheet.getRow(i);
			if (row == null) {	//跳过空行
				continue;
			}
			JSONArray jsonRow = rowToJSONArray(row);
			jsonSheet.put(jsonRow);
		}
		return jsonSheet;
	}
	
	/**
	 * 将指定的行转成JSONArray
	 * @param row
	 * @return
	 */
	public JSONArray rowToJSONArray(Row row) {
		JSONArray jsonRow = new JSONArray();
		for (int i = 0, len = row.getLastCellNum(); i < len; i++) {
			Object object = getValue(row, i);
			jsonRow.put(object);
		}
		return jsonRow;
	}
	
	/**
	 * 根据坐标获取单元格内的数据
	 * @param rowIndex 行索引(从0开始)
	 * @param colIndex 列索引(从0开始)
	 * @return 单元格内的数据
	 */
	public Object getValue(Sheet sheet, int rowIndex, int colIndex) {
		Cell cell = sheet.getRow(rowIndex).getCell(colIndex);
		return getValue(cell);
	}
	
	/**
	 * 获取指定的行中，指定列的单元格内的数据
	 * @param row
	 * @param colIndex
	 * @return
	 */
	public Object getValue(Row row, int colIndex) {
		Cell cell = row.getCell(colIndex);
		return getValue(cell);
	}
	
	/**
	 * 获取指定单元格内的数据
	 * @param cell
	 * @return
	 */
	public Object getValue(Cell cell) {
		Object cellValue = "";
		int type = typeOfCell(cell);
		switch (type) {
			case Cell.CELL_TYPE_STRING:								//数
				cellValue = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:							//布尔类型
				cellValue = df.format(cell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_BOOLEAN:							//错误类型
				cellValue = cell.getBooleanCellValue();
				break;
			case Cell.CELL_TYPE_ERROR:								//公式
				cellValue = cell.getErrorCellValue();
				break;
			case Cell.CELL_TYPE_FORMULA:							//空白
				cellValue = cell.getNumericCellValue();
				break;
			case Cell.CELL_TYPE_BLANK:
				break;
			default:
				try {
					cellValue = cell.getDateCellValue();
				} catch (NullPointerException e) {
					log.debug("null on getDateCellValue");
				} catch (Exception e) {
					log.debug("null on getDateCellValue: " + e.getMessage());
				}
				break;
		}
		return cellValue;
	}
	
	/**
	 * 获取指定坐标的单元格的其类型
	 * @param rowIndex
	 * @param colIndex
	 * @return
	 */
	public int typeOfCell(Sheet sheet, int rowIndex, int colIndex) {
		return typeOfCell(sheet.getRow(rowIndex).getCell(colIndex));
	}
	
	/**
	 * 获取单元格的类型
	 * @param cell
	 * @return
	 */
	public int typeOfCell(Cell cell) {
		if (cell == null) {
			return -1;
		}
		return cell.getCellType();
	}
	
	/**
     * 给指定单元格设置值
     * @param sheetnum
     * @param rownum
     * @param cellnum
     * @param value
     * @return
     */
    public boolean setValue(int sheetnum, int rownum, int cellnum, Object value) {
        workbook.getSheetAt(sheetnum).getRow(rownum).getCell(cellnum).setCellValue((String) value);
        return true;
    }
}
