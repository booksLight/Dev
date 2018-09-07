package com.boot.edge.service.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.tools.ant.types.resources.selectors.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.boot.edge.service.ExcelReader;
import com.boot.edge.ui.StockVO;
import com.boot.edge.util.TransformUtil;

@Service
public class ExcelReaderImpl implements ExcelReader {

	private static final Logger logger = LoggerFactory.getLogger(ExcelReaderImpl.class);

	@Override
	public StockVO readXls(final String xlsFilePath) throws IOException, InvalidFormatException {
		StockVO stockVO = null;
		Integer NO_OF_WROK_BOOKS = 0;

		if (xlsFilePath != null) {
			Workbook workbook;
			workbook = openWorkbook(xlsFilePath);
			NO_OF_WROK_BOOKS = workbook.getNumberOfSheets();
			logger.info("\n Number of Sheets found : " + NO_OF_WROK_BOOKS);
			for (Sheet sheet : workbook) {

				if (hasSheetEnpity(sheet)) {
					logger.info("\n Sheet " + sheet.getSheetName() + " is empty.");
				} else {
					logger.info("\n Sheet title :" + sheet.getSheetName());
					for (Row row : sheet) {
						if(sheet.getSheetName().equalsIgnoreCase("Amul")) {
							for (Cell cell : row) {								
								if(cell.getRowIndex() > 1) {
									System.out.println("\n Cell Row, Column ="+cell.getRowIndex()+", "+cell.getColumnIndex());
									stockVO = valuesByCellType(cell, workbook);
									System.out.println(stockVO.toString());
								}								
							}

						}
							System.out.println(" ");
					}
				}
			}

			return stockVO;
		}
		return new StockVO();
	}

	private StockVO valuesByCellType(Cell cell, Workbook workbook) throws IOException, InvalidFormatException {
		StockVO stockVO = new StockVO();
		TransformUtil tu = new TransformUtil();
		Date date = new Date();
		 Timestamp timeStamp = new Timestamp(date.getMillis());	
		
		stockVO.setCode(cell.getAddress().toString().equalsIgnoreCase("B4") ? cell.getRichStringCellValue().getString(): "NA");	
		stockVO.setDate(cell.getAddress().toString().equalsIgnoreCase("C4") ? tu.convert2Timestamp(cell.getDateCellValue().toString()): timeStamp);
		stockVO.setTitle(cell.getAddress().toString().equalsIgnoreCase("D4") ? cell.getRichStringCellValue().getString(): "N-A" );
		
		
		/*stockVO.setProductCode(productCode);
		stockVO.setItemCode(itemCode);
		stockVO.setCode(cell.getAddress().toString().equalsIgnoreCase("A4") ? cell.getRichStringCellValue().getString(), null);		
		stockVO.setDate(cell.getAddress().toString().equalsIgnoreCase("C3") ? cell.getDateCellValue(): null);
		
		
		
		stockVO.setNumber(number);
		
		
		stockVO.setTitle(title);
		stockVO.setQty(qty);			
		stockVO.setUom(uom);
		stockVO.setRate(rate);
		stockVO.setValue(value);
		stockVO.setOffer(offer);
		stockVO.setIsActive(isActive);
		stockVO.setDiscription(discription);
		*/
		logger.info("DAddress=" + cell.getAddress());
		/*if(cell.getAddress().toString().equalsIgnoreCase("A3")) {
			logger.info("A3 Value =" + Math.round(cell.getNumericCellValue()));
		}*/
		
		
		/*switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			logger.info("D1=" + cell.getBooleanCellValue());
			break;
		case STRING:
			logger.info("D2=" +cell.getRichStringCellValue().getString());
			break;
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				logger.info("DATE=" + cell.getDateCellValue());
			} else {
				logger.info("D3=" + cell.getNumericCellValue());
			}
			break;
		case FORMULA:
			valueByFormula(cell, workbook, stockVO);
			break;
		case BLANK:
			logger.info("");
			break;
		default:
			logger.info("");
		}
		logger.info(" ");
	*/	
		return stockVO;
	}

	private void valueByFormula(Cell cell, Workbook workbook, StockVO stockVO)
			throws IOException, InvalidFormatException {
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		CellValue cellValue = evaluator.evaluate(cell);
		int cellType = evaluator.evaluateFormulaCell(cell);
		evaluator.evaluateAll();
		switch (cellType) {
		case Cell.CELL_TYPE_BOOLEAN:
			logger.info("" + cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_STRING:
			logger.info(cell.getRichStringCellValue().getString());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				logger.info("" + cell.getDateCellValue());
			} else {
				logger.info("" + cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_FORMULA:
			// logger.info(cell.getCellFormula());
			logger.info(cell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			logger.info("");
			break;
		default:
			logger.info("");
		}
	}

	private boolean hasSheetEnpity(Sheet sheet) throws IOException, InvalidFormatException {
		return isCellEmpty(sheet.getRow(3).getCell(3));
	}

	public boolean isCellEmpty(final Cell cell) {
		if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			return true;
		}

		if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().isEmpty()) {
			return true;
		}

		return false;
	}

	private Workbook openWorkbook(final String xlsFilePath) throws IOException, InvalidFormatException {
		Workbook workbook = WorkbookFactory.create(new File(xlsFilePath));
		return workbook;
	}

}
