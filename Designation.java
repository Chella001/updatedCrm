package crm;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import utilsCrm.crmFunctions;
import utilsCrm.crmUtils;

public class Designation extends crmDriver {

    crmUtils exUtil = new crmUtils();
    crmFunctions util = new crmFunctions();
    private boolean navigatedToDes = false;

    private String getCellValue(Row row, int cellIndex) {
        if (row == null || row.getCell(cellIndex) == null) {
            return "";
        }
        return row.getCell(cellIndex).toString().trim();
    }

    public boolean Designation(Workbook workbook, int lastRow) throws InterruptedException {

        Sheet sheet = workbook.getSheet("Designation");
        boolean allSuccess = true;
        int col = 0;

        for (int rw = 1; rw <= lastRow; rw++) {

            Row row = sheet.getRow(rw);
            if (row == null)
                continue;

            String TC_ID = getCellValue(row, 1);
            String Designation = getCellValue(row, 2);
            String edit = getCellValue(row, 3);
            String delete = getCellValue(row, 4);
            String editDesignation = getCellValue(row, 5);

            boolean addSuccess = false;
            boolean editSuccess = false;
            boolean deleteSuccess = false;
            boolean finalStatus;

            // ================= EXTENT TEST =================
            test = extent.createTest(TC_ID, "Designation Test");
            test.info("Designation Test Started for TC_ID : " + TC_ID);

            try {

                // ================= NAVIGATION (ONCE) =================
                if (!navigatedToDes) {

                    util.clickByXpath("/html/body/div[1]/header/nav/a");

                    WebElement sidebar = driver.findElement(
                            By.cssSelector("div.slimScrollDiv > section.sidebar"));

                    ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].scrollTop = 1500;", sidebar);

                    util.clickByXpath("/html/body/div/aside[1]/div/section/ul/li[36]/a");
                    util.clickByXpath("/html/body/div/aside[1]/div/section/ul/li[36]/ul/li[8]/a");

                    Thread.sleep(1500);
                    navigatedToDes = true;
                }

                // ================= ADD =================
                util.clickById("add_designation");
                util.enterTextByXpath("//*[@id=\"designation\"]", Designation);
                util.clickById("add_design");
                Thread.sleep(2000);

                try {
                    String addMessage = util.getSuccessMessage(
                            "/html/body/div/div[1]/section[2]/div/div/div/div[2]/div[1]");

                    if (addMessage != null && addMessage.toLowerCase().contains("add")) {
                        addSuccess = true;
                        test.pass("Designation Added Successfully : " + Designation);
                    } else {
                        crmFunctions.captureScreenshot("Add_Designation_Failed_" + TC_ID);
                        util.clickByXpath("//*[@id=\"confirm-add\"]/div/div/div[1]/button/span[1]");
                        addSuccess = false;
                        test.fail("Designation Add Failed : " + Designation);
                    }

                } catch (Exception e) {
                    addSuccess = false;
                    test.fail("Exception during Designation Add");
                }

                // ================= EDIT =================
                if (edit.equalsIgnoreCase("Yes")) {
                    editSuccess = DesignationEdit(Designation, editDesignation, TC_ID);
                }

                // ================= DELETE =================
                if (delete.equalsIgnoreCase("Yes")) {
                    String deptToDelete =
                            edit.equalsIgnoreCase("Yes") ? editDesignation : Designation;
                    deleteSuccess = DesignationDelete(deptToDelete);
                }

                finalStatus = addSuccess
                        && (!edit.equalsIgnoreCase("Yes") || editSuccess)
                        && (!delete.equalsIgnoreCase("Yes") || deleteSuccess);

            } catch (Exception e) {
                finalStatus = false;
                crmFunctions.captureScreenshot("Designation_Error_" + TC_ID);
                test.fail("Unexpected Exception in Designation TC");
            }

            exUtil.writeToExcel(workbook, "Designation", rw, col, finalStatus);

            if (finalStatus) {
                test.pass("Designation Test Case PASSED");
            } else {
                test.fail("Designation Test Case FAILED");
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    // ================= EDIT =================
    public boolean DesignationEdit(String Designation, String editDesignation, String TC_ID)
            throws InterruptedException {

        try {

            util.clickByXpath("//*[@id=\"design_list_filter\"]/label/input");
            util.enterTextByXpath("//*[@id=\"design_list_filter\"]/label/input", Designation);
            Thread.sleep(1000);

            util.clickByXpath("//*[@id=\"edit\"]/i");
            Thread.sleep(1000);

            WebElement edDept = driver.findElement(By.id("ed_design"));
            edDept.clear();
            edDept.sendKeys(editDesignation);

            util.clickById("update_design");
            Thread.sleep(1500);

            String message = util.getSuccessMessage(
                    "/html/body/div/div[1]/section[2]/div/div/div/div[2]/div[1]");

            if (message != null && message.toLowerCase().contains("success")) {
                test.pass("Designation Edited Successfully : " + editDesignation);
                return true;
            } else {
                crmFunctions.captureScreenshot("Edit_Designation_Failed_" + TC_ID);
                util.clickByXpath("//*[@id=\"confirm-edit\"]/div/div/div[1]/button/span[1]");
                test.fail("Designation Edit Failed : " + editDesignation);
                return false;
            }

        } catch (Exception e) {
            crmFunctions.captureScreenshot("Edit_Designation_Exception_" + TC_ID);
            test.fail("Exception during Designation Edit");
            return false;
        }
    }

    // ================= DELETE =================
    public boolean DesignationDelete(String Designation) throws InterruptedException {

        try {

            util.enterTextByXpath("//*[@id=\"design_list_filter\"]/label/input", Designation);
            Thread.sleep(1000);

            util.clickByXpath("//*[@id=\"design_list\"]/tbody/tr/td[3]/a[2]");
            Thread.sleep(600);

            util.clickByXpath("//*[@id=\"confirm-delete\"]/div/div/div[3]/a");
            Thread.sleep(1500);

            String message = util.getSuccessMessage(
                    "/html/body/div/div[1]/section[2]/div/div/div/div[2]/div[1]");

            if (message != null && message.toLowerCase().contains("success")) {
                test.pass("Designation Deleted Successfully : " + Designation);
                return true;
            } else {
                crmFunctions.captureScreenshot("Delete_Designation_Failed_" + Designation);
                test.fail("Designation Delete Failed : " + Designation);
                return false;
            }

        } catch (Exception e) {
            crmFunctions.captureScreenshot("Delete_Designation_Exception_" + Designation);
            test.fail("Exception during Designation Delete");
            return false;
        }
    }
}
