package utilsCrm;

import org.apache.poi.ss.usermodel.*;

import crm.crmDriver;

import java.io.*;
import java.util.*;

public class crmUtils extends crmDriver {

	// === Read data from Excel sheet ===
	public List<List<String>> openExcel(String sheetName, Workbook workbook) {
		List<List<String>> data = new ArrayList<>();
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			System.out.println("Sheet not found: " + sheetName);
			return data;
		}

		DataFormatter formatter = new DataFormatter();

		for (Row row : sheet) {
			List<String> rowData = new ArrayList<>();

			int lastCell = row.getLastCellNum();

			for (int j = 0; j < lastCell; j++) {
				Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				rowData.add(formatter.formatCellValue(cell));
			}

			data.add(rowData);
		}
		return data;
	}

	// === Write PASS/FAIL with color in the status cell except master sheet  ===
	public void writeToExcel(Workbook workbook, String sheetName, int rowIndex, int colIndex, boolean status) {
		try {
			Sheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				System.out.println("Sheet not found while writing: " + sheetName);
				return;
			}

			Row row = sheet.getRow(rowIndex);
			if (row == null)
				row = sheet.createRow(rowIndex);

			Cell cell = row.getCell(colIndex);
			if (cell == null)
				cell = row.createCell(colIndex);

			// Create font and style
			CellStyle style = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBold(true);
			font.setColor(status ? IndexedColors.GREEN.getIndex() : IndexedColors.RED.getIndex());
			style.setFont(font);

			// Write result and apply style
			cell.setCellValue(status ? "PASS" : "FAIL");
			cell.setCellStyle(style);

			// Save workbook
			synchronized (this) {
				try (FileOutputStream fos = new FileOutputStream(Excelpath)) {
					workbook.write(fos);
				}
			}

		} catch (Exception e) {
			System.out.println("Error writing result at row: " + rowIndex);
			e.printStackTrace();
		}
	}

	// === Counted pass and fail have been updated in the master sheet ===
	public void updateSummaryToMaster(Workbook workbook, String sheetName, String excelPath) {
		Sheet dataSheet = workbook.getSheet(sheetName);
		Sheet masterSheet = workbook.getSheet("Master");

		if (dataSheet == null || masterSheet == null) {
			System.out.println("Sheet not found: " + sheetName);
			return;
		}

		int passCount = 0;
		int failCount = 0;

		DataFormatter formatter = new DataFormatter();

		for (int i = 1; i <= dataSheet.getLastRowNum(); i++) {
			Row row = dataSheet.getRow(i);
			if (row == null)
				continue;

			Cell statusCell = row.getCell(0);
			if (statusCell == null)
				continue;

			String status = formatter.formatCellValue(statusCell).trim();

			if (status.equalsIgnoreCase("PASS"))
				passCount++;
			else if (status.equalsIgnoreCase("FAIL"))
				failCount++;
		}

		for (int i = 1; i <= masterSheet.getLastRowNum(); i++) {
			Row row = masterSheet.getRow(i);
			if (row == null)
				continue;

			Cell funcCell = row.getCell(0);
			if (funcCell != null && funcCell.getStringCellValue().trim().equalsIgnoreCase(sheetName)) {
				Cell summaryCell = row.getCell(2);
				if (summaryCell == null)
					summaryCell = row.createCell(2);
				summaryCell.setCellValue(passCount + " Pass " + failCount + " Fail");
				break;
			}
		}

		try (FileOutputStream fos = new FileOutputStream(excelPath)) {
			workbook.write(fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Update total time in Master Sheet
	public void updateTimeToMaster(Workbook workbook, String functionName, String timeTaken, String excelPath) {

		Sheet masterSheet = workbook.getSheet("Master");

		if (masterSheet == null) {
			System.out.println("Master sheet not found!");
			return;
		}

		for (int i = 1; i <= masterSheet.getLastRowNum(); i++) {
			Row row = masterSheet.getRow(i);
			if (row == null)
				continue;

			Cell funcCell = row.getCell(0); // Function name

			if (funcCell != null && funcCell.getStringCellValue().trim().equalsIgnoreCase(functionName)) {

				Cell timeCell = row.getCell(3); // Time Column
				if (timeCell == null)
					timeCell = row.createCell(3);

				timeCell.setCellValue(timeTaken);
				break;
			}
		}

		try (FileOutputStream fos = new FileOutputStream(excelPath)) {
			workbook.write(fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// === Get last row number for a given sheet ===
	public int returnLastRowNo(String sheetName, Workbook workbook) {
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			System.out.println("Sheet not found: " + sheetName);
			return 0;
		}
		return sheet.getLastRowNum();
	}
}
