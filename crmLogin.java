package crm;

import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import utilsCrm.crmFunctions;
import utilsCrm.crmUtils;

import java.time.Duration;
import java.util.List;

public class crmLogin extends crmDriver {

	private crmFunctions util;

	public crmLogin() {
		util = new crmFunctions();
	}

	// Safe Excel read method
	private String getCellValue(List<String> row, int index) {
		if (row == null || row.size() <= index || row.get(index) == null) {
			return "";
		}
		return row.get(index).trim();
	}

	public boolean LoginPage(WebDriver driver, Workbook workbook, int returnLastRowNo) {

		crmUtils exUtil = new crmUtils();
		boolean overallStatus = true;

		List<List<String>> credentials = exUtil.openExcel("Login", workbook);

		//Count only valid TC rows (Non-empty TC_ID)
		int totalRows = 0;
		for (int i = 1; i < credentials.size(); i++) {
			String tc = getCellValue(credentials.get(i), 1);
			if (!tc.isEmpty()) {
				totalRows++;
			}
		}

		for (int i = 1; i < credentials.size(); i++) { // skip header
			try {
				List<String> row = credentials.get(i);

				String TC_ID = getCellValue(row, 1).trim();
				String username = getCellValue(row, 2).trim();	
				String password = getCellValue(row, 4).trim();
				String url = getCellValue(row, 3).trim();

				if (TC_ID.isEmpty()) {
					System.out.println("Skipping row " + i + " because TC_ID is empty");
					continue;
				}

				System.out.println("Running Login TC => " + TC_ID);

				boolean status = login(driver, url, username, password, returnLastRowNo, TC_ID, totalRows);

				exUtil.writeToExcel(workbook, "Login", i, 0, status);

				if (!status)
					overallStatus = false;

			} catch (Exception e) {
				e.printStackTrace();
				overallStatus = false;
			}
		}
		return overallStatus;
	}

	// ============================ Function To Login ============================
	public boolean login(WebDriver driver, String url, String username, String password, int returnLastRowNo,
			String TC_ID, int totalRows) {

		String TotalTime = new java.text.SimpleDateFormat("HH-mm-ss").format(new java.util.Date());

		// Extent Test Create
		test = extent.createTest(TC_ID, "Login Test");

		try {
			if (url == null || url.trim().isEmpty()) {
				test.fail("URL is Empty");
				return false;
			}

			driver.get(url);
			driver.manage().window().maximize();

			//Normal Login First
			util.enterTextById("username", username);
			util.enterTextById("password", password);
			util.clickById("submit_login");

			Thread.sleep(2000);
			util.handleAlertIfPresent();

			String verifyText = "";
			try {
				verifyText = driver.findElement(By.xpath("/html/body/div[1]/header/a/span[1]")).getText();
			} catch (NoSuchElementException e) {
				verifyText = "";
			}

			if (verifyText.contains("LOGIMAX")) {

				test.pass("Login Successful");

				//Logout only if there are multiple valid TC rows
				if (totalRows > 1) {
					try {
						WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

						WebElement dev = wait.until(ExpectedConditions.elementToBeClickable(
								By.xpath("/html/body/div[1]/header/nav/div/ul/li[7]")));
						dev.click();

						WebElement logout = wait.until(ExpectedConditions.elementToBeClickable(
								By.xpath("/html/body/div[1]/header/nav/div/ul/li[7]/ul/li[2]/div[2]/a")));
						logout.click();
						Thread.sleep(2000);

					} catch (Exception logoutEx) {
						System.out.println("Logout failed for " + TC_ID + " but continuing next TC...");
						test.info("Logout failed, continuing execution: " + logoutEx.getMessage());
					}
				}

				return true;

			} else {

				//LOGIN FAIL BLOCK
				System.out.println("LogFail");
				crmFunctions.captureScreenshot("LoginFailed_" + TC_ID + TotalTime);
				test.fail("Login Failed");

				//clear fields if fail
				try {
					driver.findElement(By.id("username")).clear();
					driver.findElement(By.id("password")).clear();
				} catch (Exception clearEx) {
				}

				return false;
			}

		} catch (Exception e) {
			crmFunctions.captureScreenshot("LoginFailed_Exception_" + TC_ID + TotalTime);
			test.info(e);
			test.fail("Login Failed - Exception");

			e.printStackTrace();
			return false;
		}
	}
}
