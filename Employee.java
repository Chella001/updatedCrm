package crm;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import utilsCrm.crmFunctions;
import utilsCrm.crmUtils;

public class Employee extends crmDriver {

	crmUtils exUtil = new crmUtils();
	crmFunctions util = new crmFunctions();
	private boolean navigatedToEmployee = false;

	// ================= CELL VALUE =================
	private String getCellValue(Row row, int cellIndex) {
		if (row == null || row.getCell(cellIndex) == null) {
			return "";
		}
		DataFormatter formatter = new DataFormatter();
		return formatter.formatCellValue(row.getCell(cellIndex)).trim();
	}

	// ================= MAIN METHOD =================
	public boolean Employee(Workbook workbook, int lastRow) throws InterruptedException {

		Sheet sheet = workbook.getSheet("Employee");
		boolean allSuccess = true;
		int col = 0;

		for (int rw = 1; rw <= lastRow; rw++) {

			Row row = sheet.getRow(rw);
			if (row == null)
				continue;

			String TC_ID = getCellValue(row, 1);
			String firstName = getCellValue (row, 2);
			String lastName = getCellValue (row, 3);
			String empCode = getCellValue (row, 4);
			String Doj = getCellValue (row, 5);
			String Designation = getCellValue(row, 6);
			String Department = getCellValue (row, 7);
			String Active = getCellValue (row, 8);
			String add1 = getCellValue (row, 9);
			String add2 = getCellValue (row, 10);
			String add3 = getCellValue (row, 11);
			String pinCode = getCellValue (row, 12);
			String Country = getCellValue(row ,13);
			String State = getCellValue (row, 14);
			String City = getCellValue (row, 15);
			String Phone = getCellValue (row, 16);
			String Mobile = getCellValue (row, 17);
			String Email = getCellValue (row, 18);

			String userName = getCellValue (row, 19);
			String Password = getCellValue (row, 20);
			String userType = getCellValue(row, 21);
			String Branch = getCellValue (row, 22);
			String employeeImg = getCellValue(row, 23);
			String edit = getCellValue(row, 24);
			String editName = getCellValue(row, 25);
			String editLastName = getCellValue(row, 26);
			String editEmpCode = getCellValue(row, 27);
			String editDepartment = getCellValue(row, 28);
			String editDesignation = getCellValue(row, 29);
			String editUserName = getCellValue(row, 30);
			String editPwd = getCellValue(row, 31);
			String delete = getCellValue(row, 32);
			String path = "C:\\Users\\Chella\\eclipse-workspace\\mavenCrm\\Test_Images";

			boolean addSuccess = false;
			boolean editSuccess = false;
			boolean deleteSuccess = false;
			boolean finalStatus;

			// ================= EXTENT =================
			test = extent.createTest(TC_ID, "Employee Test");
			test.info("Employee Test Started for TC_ID : " + TC_ID);

			try {

				// ================= NAVIGATION (ONCE) =================
				if (!navigatedToEmployee) {

					util.clickByXpath("/html/body/div/header/nav/a");

					WebElement sidebar = driver.findElement(
							By.cssSelector("div.slimScrollDiv > section.sidebar"));

					((JavascriptExecutor) driver)
					.executeScript("arguments[0].scrollTop = 1500;", sidebar);

					util.clickByXpath("/html/body/div/aside[1]/div/section/ul/li[36]/a");
					util.clickByXpath("/html/body/div/aside[1]/div/section/ul/li[36]/ul/li[4]/a/span");


					navigatedToEmployee = true;
				}

				// ================= ADD =================
				try {
					util.clickById("add_employee");
					Thread.sleep(1000);
					util.enterTextById("firstname", firstName);
					util.enterTextById("lastname", lastName);


					if (crmFunctions.isValidEmployeeCode(empCode)) {
						util.enterTextById("emp_code", empCode);
					} else {
						test.warning("Employee Code length < 3 : " + empCode);
					}

					util.enterTextById("date_of_join", Doj);

					util.clickByXpath("//*[@id=\"emp_join\"]/div/div[1]/div[2]/div[7]/div/span/span[1]/span/span[2]");
					util.enterTextByXpath("/html/body/span/span/span[1]/input", Department);
					driver.findElement(By.xpath("/html/body/span/span/span[1]/input")).sendKeys(Keys.ENTER);

					util.clickByXpath("//*[@id=\"emp_join\"]/div/div[1]/div[2]/div[8]/div/span/span[1]/span/span[2]");
					util.enterTextByXpath("/html/body/span/span/span[1]/input", Designation);
					driver.findElement(By.xpath("/html/body/span/span/span[1]/input")).sendKeys(Keys.ENTER);

					if (Active.equalsIgnoreCase("No")){
						util.clickByXpath("//*[@id=\"emp_join\"]/div/div[1]/div[2]/div[9]/div/div/div/div/span[2]");
					}
					else {
						System.out.println("Active is enabled");
					}

					util.enterTextById("address1", add1);
					util.enterTextById("address2", add2);
					util.enterTextById("address3", add3);

					util.clickByXpath(" //*[@id=\"emp_join\"]/div/div[2]/div[2]/div[1]/span/span[1]/span/span[2]");
					util.enterTextByXpath("/html/body/span/span/span[1]/input", Country);

					driver.findElement(By.xpath("/html/body/span/span/span[1]/input"))
					.sendKeys(Keys.ARROW_DOWN);
					driver.findElement(By.xpath("/html/body/span/span/span[1]/input")).sendKeys(Keys.ENTER);

					util.clickByXpath("//*[@id=\"emp_join\"]/div/div[2]/div[2]/div[2]/span/span[1]/span/span[2]");
					util.enterTextByXpath("/html/body/span/span/span[1]/input", State);
					driver.findElement(By.xpath("/html/body/span/span/span[1]/input")).sendKeys(Keys.ENTER);

					util.clickByXpath("//*[@id=\"emp_join\"]/div/div[2]/div[2]/div[3]/span/span[1]/span/span[2]");
					util.enterTextByXpath("/html/body/span/span/span[1]/input", City);
					driver.findElement(By.xpath("/html/body/span/span/span[1]/input")).sendKeys(Keys.ENTER);

					util.enterTextByXpath("(//input[@id='phone'])[2]", Phone);

					if (crmFunctions.isValidMobile(Mobile)) {
						util.enterTextById("mobile", Mobile);
					} else {
						System.out.println(Mobile + "Invalid number");
						test.warning("Invalid Customer Mobile Number : " + Mobile);
					}

					util.enterTextById("email", Email);
					util.enterTextByXpath("(//input[@id='pincode'])[2]", pinCode);

					util.enterTextByXpath("(//label[contains(.,'User name *')]/following::input)[1]", userName);
					util.enterTextByXpath("//*[@id=\"passwd\"]", Password);


					util.clickById("select2-profile-container");
					util.enterTextByXpath("/html/body/span/span/span[1]/input", userType);
					driver.findElement(By.xpath("/html/body/span/span/span[1]/input")).sendKeys(Keys.ENTER);

					if (Branch != null && !Branch.isEmpty()) {

						String[] branches = Branch.split(",");

						for (String br : branches) {

							br = br.trim();

							util.clickByXpath("//*[@id=\"emp_join\"]/div/div[3]/div[4]/div/span/span[1]/span/ul/li/input");
							util.clickByXpath("//input[contains(@class,'select2-search__field')]");

							util.enterTextByXpath("//input[contains(@class,'select2-search__field')]",br);

							driver.findElement(By.xpath("//input[contains(@class,'select2-search__field')]")).sendKeys(Keys.ENTER);
						}
					}

					driver.findElement(By.cssSelector("input#emp_image")).sendKeys(path+ "\\"+employeeImg + ".jpg");
					util.clickByXpath("//button[@type='submit']");
					try {
						String addMessage = util.getSuccessMessage(
								"/html/body/div/div[1]/section[2]/div/div/div/div[2]/div[1]");

						if (addMessage != null && addMessage.toLowerCase().contains("add")) {
							addSuccess = true;
							test.pass("Employee Added Successfully : " + TC_ID);
						} else {
							crmFunctions.captureScreenshot("Add_Employee_Failed_" + TC_ID);
							util.clickByXpath("//button[@class='btn btn-primary']/following-sibling::button[1]");
							addSuccess = false;
							test.fail("Department Add Failed : " +TC_ID);
						}

						} catch (Exception e) {
						addSuccess = false;
						test.fail("Exception during Employee Add");
					}
					// ================= EDIT =================
					if (edit.equalsIgnoreCase("Yes")) {
						try {
							util.enterTextByCss("input.form-control.input-sm", Mobile);
							util.clickByXpath("//*[@id=\"emp_list\"]/tbody/tr/td[11]/div/button");
							util.clickByXpath("//*[@id=\"emp_list\"]/tbody/tr/td[11]/div/ul/li[1]/a");
							Thread.sleep(1000);
							util.clearAndType(By.id("firstname"), editName);
							util.clearAndType(By.id("lastname"), editLastName);
							util.clearAndType(By.id("emp_code"),editEmpCode);

							util.clickByXpath("//*[@id=\"select2-dept-container\"]");
							util.enterTextByXpath("/html/body/span/span/span[1]/input", editDepartment);
							driver.findElement(By.xpath("/html/body/span/span/span[1]/input")).sendKeys(Keys.ENTER);

							util.clickByXpath("//*[@id=\"select2-designation-container\"]");
							util.enterTextByXpath("/html/body/span/span/span[1]/input", editDesignation);
							driver.findElement(By.xpath("/html/body/span/span/span[1]/input")).sendKeys(Keys.ENTER);

							util.clearAndType(By.xpath("(//label[contains(.,'User name *')]/following::input)[1]"), editUserName);
							System.out.println(editPwd);
							util.clearAndType(By.cssSelector("input#passwd"), editPwd);

							util.clickByXpath("//button[@type='submit']");
							String message = util.getSuccessMessage( "/html/body/div/div[1]/section[2]/div/div/div/div[2]/div[1]");
							if (message != null && message.toLowerCase().contains("success")) {
								test.pass("Employee Edited Successfully : " + TC_ID);
								editSuccess = true;
							} else {
								crmFunctions.captureScreenshot("Edit_Employee_Failed_" + TC_ID);
								util.clickByXpath("//*[@id=\"emp_join\"]/div/div[5]/div/div/button[2]");
								test.fail("Employee Edit Failed : " + TC_ID);
								editSuccess = false;
							}

						} catch (Exception e) {
							editSuccess = false;
							crmFunctions.captureScreenshot("Employee_Edit_Error_" + TC_ID);
						}
					}

					// ================= DELETE =================
					if (delete.equalsIgnoreCase("Yes")) {
						try {
							System.out.println("Delete entered");
							util.enterTextByXpath("//input[@class='form-control input-sm']", userName);
							Thread.sleep(1000);

							util.clickByXpath("//button[contains(@class,'dropdown-toggle') and @data-toggle='dropdown']");
							Thread.sleep(600);

							util.clickByXpath("//a[contains(@class,'btn-del') and @data-target='#confirm-delete']");
							Thread.sleep(1500);

							util.clickByXpath("//a[contains(@class,'btn btn-danger')]");
							
							String message = util.getSuccessMessage(
									"/html/body/div/div[1]/section[2]/div/div/div/div[2]/div[1]");

							if (message != null && message.toLowerCase().contains("success")) {
								test.pass("Designation Deleted Successfully : " + Designation);
								deleteSuccess = true;
							} else {
								crmFunctions.captureScreenshot("Delete_Designation_Failed_" + Designation);
								test.fail("Designation Delete Failed : " + Designation);
								deleteSuccess = false;
							}



						} catch (Exception e) {
							deleteSuccess = false;
							crmFunctions.captureScreenshot("Employee_Delete_Error_" + TC_ID);
						}
					}

					// ================= FINAL STATUS =================
					finalStatus = addSuccess
							&& (!edit.equalsIgnoreCase("Yes") || editSuccess)
							&& (!delete.equalsIgnoreCase("Yes") || deleteSuccess);

				} catch (Exception e) {
					finalStatus = false;
					crmFunctions.captureScreenshot("Employee_Error_" + TC_ID);
					test.fail("Unexpected Exception in Employee");
				}

				// ================= WRITE RESULT =================
				exUtil.writeToExcel(workbook, "Employee", rw, col, finalStatus);

				if (finalStatus) {
					test.pass("Employee Test Case PASSED");
				} else {
					test.fail("Employee Test Case FAILED");
					allSuccess = false;
				}

			} catch (Exception e) {
				allSuccess = false;
				crmFunctions.captureScreenshot("Employee_Main_Error");
				test.fail("Unexpected Exception in Employee Main");
			}
		}

		return allSuccess;
	}
}
