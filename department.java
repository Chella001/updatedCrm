	package crm;
	
	import org.apache.poi.ss.usermodel.Row;
	import org.apache.poi.ss.usermodel.Sheet;
	import org.apache.poi.ss.usermodel.Workbook;
	import org.openqa.selenium.By;
	import org.openqa.selenium.JavascriptExecutor;
	import org.openqa.selenium.WebElement;
	
	import utilsCrm.crmFunctions;
	import utilsCrm.crmUtils;
	
	public class department extends crmDriver {
	
	    crmUtils exUtil = new crmUtils();
	    crmFunctions util = new crmFunctions();
	    private boolean navigatedToDep = false;
	
	    private String getCellValue(Row row, int cellIndex) {
	        if (row == null || row.getCell(cellIndex) == null) {
	            return "";
	        }
	        return row.getCell(cellIndex).toString().trim();
	    }
	
	    public boolean department(Workbook workbook, int lastRow) throws InterruptedException {
	
	        Sheet sheet = workbook.getSheet("Department");
	        boolean allSuccess = true;
	        int col = 0;
	
	        for (int rw = 1; rw <= lastRow; rw++) {
	
	            Row row = sheet.getRow(rw);
	            if (row == null)
	                continue;
	
	            String TC_ID = getCellValue(row, 1);
	            String Department = getCellValue(row, 2);
	            String edit = getCellValue(row, 3);
	            String delete = getCellValue(row, 4);
	            String editDepartment = getCellValue(row, 5);
	
	            boolean addSuccess = false;
	            boolean editSuccess = false;
	            boolean deleteSuccess = false;
	            boolean finalStatus;
	
	            // ================= EXTENT TEST =================
	            test = extent.createTest(TC_ID, "Department Test");
	            test.info("Department Test Started for TC_ID : " + TC_ID);
	
	            try {
	
	                // ================= NAVIGATION (ONCE) =================
	                if (!navigatedToDep) {
	
	                    util.clickByXpath("/html/body/div[1]/header/nav/a");
	
	                    WebElement sidebar = driver.findElement(
	                            By.cssSelector("div.slimScrollDiv > section.sidebar"));
	
	                    ((JavascriptExecutor) driver)
	                            .executeScript("arguments[0].scrollTop = 1500;", sidebar);
	
	                    util.clickByXpath("/html/body/div/aside[1]/div/section/ul/li[36]/a");
	                    util.clickByXpath("/html/body/div/aside[1]/div/section/ul/li[36]/ul/li[6]/a");
	
	                    Thread.sleep(1500);
	                    navigatedToDep = true;
	                }
	
	                // ================= ADD =================
	                util.clickById("add_dpt");
	                util.enterTextByXpath("//*[@id=\"department\"]", Department);
	                util.clickById("add_dept");
	                Thread.sleep(2000);
	
	                try {
	                    String addMessage = util.getSuccessMessage(
	                            "/html/body/div/div[1]/section[2]/div/div/div/div[2]/div[1]");
	
	                    if (addMessage != null && addMessage.toLowerCase().contains("add")) {
	                        addSuccess = true;
	                        test.pass("Department Added Successfully : " + Department);
	                    } else {
	                        crmFunctions.captureScreenshot("Add_Department_Failed_" + TC_ID);
	                        util.clickByXpath("//*[@id=\"confirm-add\"]/div/div/div[1]/button/span[1]");
	                        addSuccess = false;
	                        test.fail("Department Add Failed : " + Department);
	                    }
	
	                } catch (Exception e) {
	                    addSuccess = false;
	                    test.fail("Exception during Department Add");
	                }
	
	                // ================= EDIT =================
	                if (edit.equalsIgnoreCase("Yes")) {
	                    editSuccess = departmentEdit(Department, editDepartment, TC_ID);
	                }
	
	                // ================= DELETE =================
	                if (delete.equalsIgnoreCase("Yes")) {
	                    String deptToDelete =
	                            edit.equalsIgnoreCase("Yes") ? editDepartment : Department;
	                    deleteSuccess = departmentDelete(deptToDelete);
	                }
	
	                finalStatus = addSuccess
	                        && (!edit.equalsIgnoreCase("Yes") || editSuccess)
	                        && (!delete.equalsIgnoreCase("Yes") || deleteSuccess);
	
	            } catch (Exception e) {
	                finalStatus = false;
	                crmFunctions.captureScreenshot("Department_Error_" + TC_ID);
	                test.fail("Unexpected Exception in Department TC");
	            }
	
	            exUtil.writeToExcel(workbook, "Department", rw, col, finalStatus);
	
	            if (finalStatus) {
	                test.pass("Department Test Case PASSED");
	            } else {
	                test.fail("Department Test Case FAILED");
	                allSuccess = false;
	            }
	        }
	
	        return allSuccess;
	    }
	
	    // ================= EDIT =================
	    public boolean departmentEdit(String Department, String editDepartment, String TC_ID)
	            throws InterruptedException {
	
	        try {
	
	            util.clickByXpath("//*[@id=\"deptf_list_filter\"]/label/input");
	            util.enterTextByXpath("//*[@id=\"dept_list_filter\"]/label/input", Department);
	            Thread.sleep(1000);
	
	            util.clickByXpath("//*[@id=\"edit\"]");
	            Thread.sleep(1000);
	
	            WebElement edDept = driver.findElement(By.id("ed_dept"));
	            edDept.clear();
	            edDept.sendKeys(editDepartment);
	
	            util.clickById("update_dept");
	            Thread.sleep(1500);
	
	            String message = util.getSuccessMessage("/html/body/div/div[1]/section[2]/div/div/div/div[2]/div[1]");
	
	            if (message != null && message.toLowerCase().contains("success")) {
	                test.pass("Department Edited Successfully : " + editDepartment);
	                return true;
	            } else {
	                crmFunctions.captureScreenshot("Edit_Department_Failed_" + TC_ID);
	                util.clickByXpath("//*[@id=\"confirm-edit\"]/div/div/div[1]/button/span[1]");
	                test.fail("Department Edit Failed : " + editDepartment);
	                return false;
	            }
	
	        } catch (Exception e) {
	            crmFunctions.captureScreenshot("Edit_Department_Exception_" + TC_ID);
	            test.fail("Exception during Department Edit");
	            return false;
	        }
	    }
	
	    // ================= DELETE =================
	    public boolean departmentDelete(String Department) throws InterruptedException {
	
	        try {
	
	            util.enterTextByXpath("//*[@id=\"dept_list_filter\"]/label/input", Department);
	            Thread.sleep(1000);
	
	            util.clickByXpath("//*[@id=\"dept_list\"]/tbody/tr[1]/td[3]/a[2]");
	            Thread.sleep(600);
	
	            util.clickByXpath("//*[@id=\"confirm-delete\"]/div/div/div[3]/a");
	            Thread.sleep(1500);
	
	            String message = util.getSuccessMessage(
	                    "/html/body/div/div[1]/section[2]/div/div/div/div[2]/div[1]");
	
	            if (message != null && message.toLowerCase().contains("success")) {
	                test.pass("Department Deleted Successfully : " + Department);
	                return true;
	            } else {
	                crmFunctions.captureScreenshot("Delete_Department_Failed_" + Department);
	                test.fail("Department Delete Failed : " + Department);
	                return false;
	            }
	
	        } catch (Exception e) {
	            crmFunctions.captureScreenshot("Delete_Department_Exception_" + Department);
	            test.fail("Exception during Department Delete");
	            return false;
	        }
	    }
	}
