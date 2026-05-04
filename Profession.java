	package crm;
	
	import org.apache.poi.ss.usermodel.Row;
	import org.apache.poi.ss.usermodel.Sheet;
	import org.apache.poi.ss.usermodel.Workbook;
	import org.openqa.selenium.By;
	import org.openqa.selenium.JavascriptExecutor;
	import org.openqa.selenium.WebElement;
	
	import utilsCrm.crmFunctions;
	import utilsCrm.crmUtils;
	
	public class Profession extends crmDriver {
	
	    crmUtils exUtil = new crmUtils();
	    crmFunctions util = new crmFunctions();
	    private boolean navigatedToDep = false;
	
	    private String getCellValue(Row row, int cellIndex) {
	        if (row == null || row.getCell(cellIndex) == null) {
	            return "";
	        }
	        return row.getCell(cellIndex).toString().trim();
	    }
	
	    public boolean Profession(Workbook workbook, int lastRow) throws InterruptedException {
	
	        Sheet sheet = workbook.getSheet("Profession");
	        boolean allSuccess = true;
	        int col = 0;
	
	        for (int rw = 1; rw <= lastRow; rw++) {
	
	            Row row = sheet.getRow(rw);
	            if (row == null)
	                continue;
	
	            String TC_ID = getCellValue(row, 1);
	            String Profession = getCellValue(row, 2);
	            String edit = getCellValue(row, 3);
	            String delete = getCellValue(row, 4);
	            String editProfession = getCellValue(row, 5);
	
	            boolean addSuccess = false;
	            boolean editSuccess = false;
	            boolean deleteSuccess = false;
	            boolean finalStatus;
	
	            // ================= EXTENT TEST =================
	            test = extent.createTest(TC_ID, "Profession Test");
	            test.info("Profession Test Started for TC_ID : " + TC_ID);
	
	            try {
	
	                // ================= NAVIGATION (ONCE) =================
	                if (!navigatedToDep) {
	
	                    util.clickByXpath("/html/body/div[1]/header/nav/a");
	
	                    WebElement sidebar = driver.findElement(
	                            By.cssSelector("div.slimScrollDiv > section.sidebar"));
	
	                    ((JavascriptExecutor) driver)
	                            .executeScript("arguments[0].scrollTop = 1500;", sidebar);
	
	                    util.clickByXpath("/html/body/div/aside[1]/div/section/ul/li[36]/a");
	                    util.clickByXpath("/html/body/div/aside[1]/div/section/ul/li[36]/ul/li[9]/a");
	
	                    Thread.sleep(1500);
	                    navigatedToDep = true;
	                }
	
	                // ================= ADD =================
	                util.clickById("add_professions");
	                util.enterTextByXpath("//*[@id=\"profession\"]", Profession);
	                util.clickById("add_profession");
	                Thread.sleep(2000);
	
	                try {
	                    String addMessage = util.getSuccessMessage(
	                            "/html/body/div/div[1]/section[2]/div/div/div/div[2]/div[1]");
	
	                    if (addMessage != null && addMessage.toLowerCase().contains("add")) {
	                        addSuccess = true;
	                        test.pass("Profession Added Successfully : " + Profession);
	                    } else {
	                        crmFunctions.captureScreenshot("Add_Profession_Failed_" + TC_ID);
	                        util.clickByXpath("//*[@id=\"confirm-add\"]/div/div/div[1]/button/span[1]");
	                        addSuccess = false;
	                        test.fail("Profession Add Failed : " + Profession);
	                    }
	
	                } catch (Exception e) {
	                    addSuccess = false;
	                    test.fail("Exception during Profession Add");
	                }
	
	                // ================= EDIT =================
	                if (edit.equalsIgnoreCase("Yes")) {
	                    editSuccess = ProfessionEdit(Profession, editProfession, TC_ID);
	                }
	
	                // ================= DELETE =================
	                if (delete.equalsIgnoreCase("Yes")) {
	                    String ProfToDelete =
	                            edit.equalsIgnoreCase("Yes") ? editProfession : Profession;
	                    deleteSuccess = ProfessionDelete(ProfToDelete);
	                }
	
	                finalStatus = addSuccess
	                        && (!edit.equalsIgnoreCase("Yes") || editSuccess)
	                        && (!delete.equalsIgnoreCase("Yes") || deleteSuccess);
	
	            } catch (Exception e) {
	                finalStatus = false;
	                crmFunctions.captureScreenshot("Profession_Error_" + TC_ID);
	                test.fail("Unexpected Exception in Profession TC");
	            }
	
	            exUtil.writeToExcel(workbook, "Profession", rw, col, finalStatus);
	
	            if (finalStatus) {
	                test.pass("Profession Test Case PASSED");
	            } else {
	                test.fail("Profession Test Case FAILED");
	                allSuccess = false;
	            }
	        }
	
	        return allSuccess;
	    }
	
	    // ================= EDIT =================
	    public boolean ProfessionEdit(String Profession, String editProfession, String TC_ID)
	            throws InterruptedException {
	
	        try {
	
	            util.clickByXpath("//*[@id=\"profession_list_filter\"]/label/input");
	            util.enterTextByXpath("//*[@id=\"profession_list_filter\"]/label/input", Profession);
	            Thread.sleep(1000);
	
	            util.clickByXpath("//*[@id=\"edit\"]");
	            Thread.sleep(1000);
	
	            WebElement edProf = driver.findElement(By.id("ed_profession"));
	            edProf.clear();
	            edProf.sendKeys(editProfession);
	
	            util.clickById("update_profession");
	            Thread.sleep(1500);
	
	            String message = util.getSuccessMessage(
	                    "/html/body/div/div[1]/section[2]/div/div/div/div[2]/div[1]");
	
	            if (message != null && message.toLowerCase().contains("success")) {
	                test.pass("Profession Edited Successfully : " + editProfession);
	                return true;		
	            } else {
	                crmFunctions.captureScreenshot("Edit_Profession_Failed_" + TC_ID);
	                Thread.sleep(2000);
	                util.clickByXpath("//div[@id='confirm-edit']//button[@class='close']");
	                test.fail("Profession Edit Failed : " + editProfession);
	                return false;
	            }
	
	        } catch (Exception e) {
	            crmFunctions.captureScreenshot("Edit_Profession_Exception_" + TC_ID);
	            test.fail("Exception during Profession Edit");
	            return false;
	        }
	    }
	
	    // ================= DELETE =================
	    public boolean ProfessionDelete(String Profession) throws InterruptedException {
	
	        try {
	
	            util.enterTextByXpath("//*[@id=\"profession_list_filter\"]/label/input", Profession);
	            Thread.sleep(1000);
	
	            util.clickByXpath("//*[@id=\"profession_list\"]/tbody/tr[1]/td[3]/a[2]");
	            Thread.sleep(600);
	
	            util.clickByXpath("//*[@id=\"confirm-delete\"]/div/div/div[3]/a");
	            Thread.sleep(1500);
	
	            String message = util.getSuccessMessage(
	                    "/html/body/div/div[1]/section[2]/div/div/div/div[2]/div[1]");
	
	            if (message != null && message.toLowerCase().contains("success")) {
	                test.pass("Profession Deleted Successfully : " + Profession);
	                return true;
	            } else {
	                crmFunctions.captureScreenshot("Delete_Profession_Failed_" + Profession);
	                test.fail("Profession Delete Failed : " + Profession);
	                return false;
	            }
	
	        } catch (Exception e) {
	            crmFunctions.captureScreenshot("Delete_Profession_Exception_" + Profession);
	            test.fail("Exception during Profession Delete");
	            return false;
	        }
	    }
	}
