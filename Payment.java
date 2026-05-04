package crm;

import java.text.DecimalFormat;
import java.time.LocalDate;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.Keys;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import utilsCrm.crmFunctions;
import utilsCrm.crmUtils;

public class Payment extends crmDriver {

	crmUtils exUtil = new crmUtils();
	crmFunctions util = new crmFunctions();
	private boolean navigatedToPayment = false;

	// ================= CELL VALUE =================
	private String getCellValue(Row row, int cellIndex) {
		if (row == null || row.getCell(cellIndex) == null) {
			return "";
		}
		DataFormatter formatter = new DataFormatter();
		return formatter.formatCellValue(row.getCell(cellIndex)).trim();
	}

	// ================= MAIN METHOD =================
	public boolean Payment(Workbook workbook, int lastRow) throws InterruptedException {

		Sheet sheet = workbook.getSheet("Payment");
		boolean allSuccess = true;
		int col = 0;

		for (int rw = 1; rw <= lastRow; rw++) {

			Row row = sheet.getRow(rw);
			if (row == null)
				continue;

			// ===== Excel Data =====
			String TC_ID = getCellValue(row, 1);
			String customerMobile = getCellValue(row, 2);
			String schemeCode = getCellValue(row, 3);
			String Branch = getCellValue(row, 4);
			String Employee = getCellValue(row, 5);
			//	String Amount = getCellValue(row, 6);
			String Cash = getCellValue(row, 7);
			String cardDetails = getCellValue(row,8);
			String cardName = getCellValue(row, 9);
			String Type = getCellValue(row, 10);
			String Device = getCellValue(row, 11);
			String cardNo = getCellValue (row, 12);
			String cardAmount = getCellValue(row, 13);
			String approvalNo = getCellValue(row, 14);
			String netBanking = getCellValue(row, 15);
			String TypeBnk = getCellValue(row, 16);
			String BankDevice = getCellValue(row, 17);
			String paymentDate = getCellValue(row, 18);
			String refNo = getCellValue(row, 19);
			String BnkAmount = getCellValue(row, 20);

			String chequeDetails = getCellValue(row, 21);
			String chequeDate = getCellValue(row, 22);
			String cardBank = getCellValue (row, 23);
			String chequeNo = getCellValue (row, 24);
			String ifscCode = getCellValue(row,25);
			String chequeAmt = getCellValue(row, 26);

			String voucher = getCellValue(row,27);
			String voucherCode = getCellValue(row, 28);
			String voucherValue = getCellValue(row, 29);
			String weightGram = getCellValue(row,30);
			String schemeName = getCellValue(row, 31);
			String flexible = getCellValue(row, 32);
			boolean addSuccess = false;
			boolean finalStatus;

			// ================= EXTENT =================
			test = extent.createTest(TC_ID, "Payment Test");
			test.info("Payment Test Started for TC_ID : " + TC_ID);

			try {

				// ================= NAVIGATION (ONCE) =================
				if (!navigatedToPayment) {
					WebElement payEma = driver.findElement(By.xpath("/html/body/div/header/nav/button/a"));//  not allocated ,properties file
					payEma.click();
					navigatedToPayment = true;
				}

				// ================= ADD PAYMENT =================
				try {

					if (crmFunctions.isValidMobile(customerMobile)) {
						util.enterTextById("mobile_number", customerMobile);
					} else {
						System.out.println(customerMobile + " Invalid number");
						test.warning("Invalid Customer Mobile Number : " + customerMobile);
					}

					Thread.sleep(2000);
					try {
						WebElement mobileField = driver.findElement(By.id("mobile_number"));
						mobileField.sendKeys(Keys.ARROW_DOWN);
						mobileField.sendKeys(Keys.ENTER);
					} catch (Exception e) {
						System.out.println("Enter the valid number");
					}

					LocalDate today = LocalDate.now();
					int year = today.getYear();
					int month = today.getMonthValue();

					if (month <= 3) {
						year = year - 1;
					}

					String financialYear = String.valueOf(year).substring(2);
					String financialCode = financialYear + "-" + schemeCode;
					System.out.println(financialCode);
					Thread.sleep(2000);

					util.clickById("select2-scheme_account-container");
					util.enterTextByXpath("/html/body/span/span/span[1]/input", financialCode);
					driver.findElement(By.xpath("/html/body/span/span/span[1]/input")).sendKeys(Keys.ENTER);

					Thread.sleep(1000);
					util.clickById("select2-branch_select-container");
					util.enterTextByXpath("/html/body/span/span/span[1]/input", Branch);
					driver.findElement(By.xpath("/html/body/span/span/span[1]/input")).sendKeys(Keys.ENTER);

					Thread.sleep(1000);
					util.clickById("select2-employee_select-container");
					util.enterTextByXpath("/html/body/span/span/span[1]/input", Employee);
					driver.findElement(By.xpath("/html/body/span/span/span[1]/input")).sendKeys(Keys.ENTER);

					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("window.scrollBy(0, 500);");
					//Thread.sleep(2000);
					//===========================================================================================================================================
					if (weightGram != null && !weightGram.trim().isEmpty()) {

						String formattedValue = String.format("%.3f", Double.parseDouble(weightGram));

						driver.findElement(By.xpath("//input[@name='weight_gold' and @value='" + formattedValue + "']")).click();
					}

					if (flexible != null && !flexible.trim().isEmpty()) {
						WebElement amtClear = driver.findElement(By.id("total_amt"));
						amtClear.clear();
						util.enterTextById("total_amt", flexible);
						Thread.sleep(2000);
					}

					String received = driver.findElement(By.id("total_amt")).getAttribute("value").trim();

					System.out.println(received);

					// Calculate total first
					long total = Long.parseLong(Cash)+ Long.parseLong(cardAmount)+ Long.parseLong(BnkAmount)+ Long.parseLong(chequeAmt)+ Long.parseLong(voucherValue);

					String receivedAmount;
					String excelAmount;

					if ((weightGram == null || weightGram.isEmpty()) 
							&& (flexible == null || flexible.isEmpty())) {

						receivedAmount = String.valueOf(received)+".00";
						excelAmount = String.valueOf(total);

					} else {

						receivedAmount =String.valueOf(received)+".00";
						excelAmount = String.valueOf(total) + ".00";
					}

					System.out.println(excelAmount);
					System.out.println(receivedAmount);
					System.out.println(total);
					//=================================================================================================================================================

					switch (schemeName) {

					case "Amount":
						  break;
						  
					case "Weight" :
						String formattedValue = String.format("%.3f", Double.parseDouble(weightGram));
						driver.findElement(By.xpath("//input[@name='weight_gold' and @value='" + formattedValue + "']")).click();
						break;
						
					case "amountToWeight" :
						break;s
						
					case "FlexibleAmount" :
						WebElement amtClear = driver.findElement(By.id("total_amt"));
						amtClear.clear();
						util.enterTextById("total_amt", flexible);
						Thread.sleep(2000);
						break;
						
					case "amountToWeightBOA" :
						
						break;
						
						// ===== SELECT WEIGHT FROM EXCEL =====

						if (excelAmount.equalsIgnoreCase(receivedAmount))
						{
							util.clickById("proced");

							if (Cash != null && !Cash.trim().isEmpty() 
									&& crmFunctions.isValidAmount(Cash)) {

								util.enterTextById("make_pay_cash", Cash);
							}

							// ====  Credit / DebitCard   ====  //

							if (cardDetails.equalsIgnoreCase("Yes")) {
								util.clickById("card_detail_modal");
								Thread.sleep(2000);

								if (cardName != "") {

									util.selectByVisibleText(By.cssSelector("select.card_name"), cardName);
								}

								if (!Type.trim().isEmpty()) {

									util.selectByVisibleText(By.cssSelector("select.card_type"), Type);
								}
								if (!Device.trim().isEmpty()) {

									util.selectByVisibleText(By.cssSelector("select.form-control.id_device"), Device);
								}

								util.enterTextByXpath("//*[@id=\"card_details\"]/tbody/tr/td[4]/input", cardNo);

								if (crmFunctions.isValidAmount(cardAmount)
										&& !cardAmount.trim().isEmpty()) {

									util.enterTextByXpath("//*[@id=\"card_details\"]/tbody/tr/td[5]/input", cardAmount);

								}else {

									System.out.println(cardAmount + " Invalid Amount");
									test.warning("Invalid Amount : " + cardAmount);
								}


								util.enterTextByXpath("//*[@id=\"cardref_no_0\"]", approvalNo);						
								util.clickById("add_newcc");

								String creditCard = driver.findElement(By.cssSelector("td.mode_CC")).getAttribute("value");
								String debitCard = driver.findElement(By.cssSelector("td.mode_DC")).getAttribute("value");

								if (creditCard == null || creditCard.isEmpty() ||
										debitCard == null || debitCard.isEmpty())
								{
									util.handleAlertIfPresent();
									util.clickByXpath("//*[@id=\"card-detail-modal\"]/div/div/div[3]/button");
								}

							}

							//  ====   // NetBanking  ===  //

							if (netBanking.equalsIgnoreCase("Yes")) {
								util.clickById("netbankmodal");
								System.out.println("Bank entered");
								Thread.sleep(3000);
								if (TypeBnk != "") {

									util.selectByVisibleText(By.cssSelector("select.nb_type"), TypeBnk);
								}

								if (BankDevice != null && !BankDevice.trim().isEmpty()) {

									util.selectByVisibleText(By.cssSelector("select.id_bank"), BankDevice);
								}

								if (!paymentDate.isEmpty()) {
									util.clearAndType(By.cssSelector("input[data-date-format='yyyy-mm-dd']"), paymentDate);
								}

								util.enterTextById("nbref_no_0", refNo);

								if (!BnkAmount.isEmpty() 
										&& crmFunctions.isValidAmount(BnkAmount)) {

									util.enterTextByXpath("//*[@id=\"net_bankdetails\"]/tr/td[6]/input", BnkAmount);

								} else {

									System.out.println(BnkAmount + " Invalid Amount");

								}

								util.clickByXpath("//*[@id=\"add_newnb\"]");

								try {
									String bankamt = driver.findElement(By.cssSelector("span.NB")).getText();
									double value = Double.parseDouble(bankamt);

									DecimalFormat df = new DecimalFormat("0.00");
									String formattedValue = df.format(value);

									System.out.println(formattedValue);

								} catch (Exception e) {

									System.out.println("Error getting bank amount: " + e.getMessage());
									util.clickByXpath("//*[@id=\"net_banking_modal\"]/div/div/div[3]/button");
								}

							}

							//  ==== Cheque ====//

							if (chequeDetails.equalsIgnoreCase("Yes")) {
								util.clickById("cheque_modal");
								Thread.sleep(1000);

								if (!chequeDate.isEmpty()) {
									util.enterTextByXpath("//input[@class='cheque_date']", chequeDate);
								}

								if (!cardBank.isEmpty()) {
									util.selectByVisibleText(By.cssSelector("select.bank_name"), cardBank);
								}

								if (!Branch.isEmpty()) {
									util.enterTextByXpath("(//table[@id='chq_details']//input)[2]", Branch);
								}

								try {
									util.enterTextById("chq_no_0", chequeNo);
									util.enterTextByXpath("//input[@name='cheque_details[bank_IFSC][]']", ifscCode);
								} catch (Exception e) {
									System.out.println("Check the excel Sheet for invalid entries");
								}

								if (!chequeAmt.isEmpty() && crmFunctions.isValidAmount(chequeAmt)) {
									util.enterTextByXpath("//input[@name='cheque_details[payment_amount][]']", chequeAmt);
								} else {
									System.out.println(chequeAmt + " Invalid Amount");
								}

								util.clickById("add_newchq");

								try {
									String chequeAmount = driver.findElement(By.xpath("//*[@id=\"payment_modes\"]/tbody/tr[5]/td[3]")).getText();
									System.out.println(chequeAmount);
									chequeAmount = chequeAmount.replaceAll("[^0-9.]", "");
									double value = Double.parseDouble(chequeAmount);
									int finalValue = (int) value;
									System.out.println(finalValue);

								} catch (Exception e) {
									util.handleAlertIfPresent();
									System.out.println("Error getting bank amount: " + e.getMessage());
									util.clickByXpath("//*[@id=\"cheque-detail-modal\"]/div/div/div[3]/button");
								}
							}

							if (voucher.equalsIgnoreCase("Yes")) {

								util.clickByXpath("//*[@id=\"vch_modal\"]");

								if (!voucherCode.isEmpty()) {
									util.enterTextByXpath("//*[@id=\"vch_details\"]/tbody/tr/td[1]/input", voucherCode);
								}

								if (!voucherValue.isEmpty()) {
									util.enterTextByXpath("//*[@id=\"vch_details\"]/tbody/tr/td[2]/input", voucherValue);
								}

								util.clickByXpath("//*[@id=\"vch_newvch\"]");

								try {
									String voucherAmt = driver.findElement(By.xpath("//*[@id=\"payment_modes\"]/tbody/tr[7]/td[3]")).getText();
									System.out.println(voucherAmt);
									voucherAmt = voucherAmt.replaceAll("[^0-9.]", "");
									double value = Double.parseDouble(voucherAmt);
									int finalValue = (int) value;
									System.out.println(finalValue);

								} catch (Exception e) {
									util.handleAlertIfPresent();
									System.out.println("Error getting Voucher: " + e.getMessage());
									util.clickByXpath("//*[@id=\"vch-detail-modal\"]/div/div/div[3]/button");
									test.fail("The voucher amount exceeed the maximum amount");
								}
							}

							String totalAmt = driver.findElement(By.xpath("//*[@id=\"payment_modes\"]/tfoot/tr[1]/th[3]")).getText();
							System.out.println(totalAmt);
							if (totalAmt.equals(excelAmount)) {
								System.out.println("Clicked save");
								util.clickByXpath("//*[@id=\"btn-submit\"]/label[2]");
							} else {
								util.clickByXpath("//*[@id=\"pay_form\"]/div[5]/div/div/div[2]/button");
								System.out.println("The given Value dosent matched");
								test.fail("The payment amount exceeded the received amount");
							}
						}


						String message = util.getSuccessMessage("/html/body/div[1]/div[1]/section[2]/div/div/div/div[2]/div[2]");

						String searchBoxXpath = "//div[@id='payment_list_filter']//input[1]";
						util.enterTextByXpath(searchBoxXpath,customerMobile );
						String accNo = util.getSuccessMessage("//*[@id=\"payment_list\"]/tbody/tr[1]/td[8]");
						System.out.println(accNo);


						if (message != null && message.toLowerCase().contains("success")) {
							addSuccess = true;
							test.pass("Payment Added Successfully : " + TC_ID);
						} else {
							crmFunctions.captureScreenshot("Payment_Add_Failed_" + TC_ID);
							addSuccess = false;
							test.fail("Payment Add Failed : " + TC_ID);
						}

					} catch (Exception e) {
						addSuccess = false;
						crmFunctions.captureScreenshot("Payment_Add_Error_" + TC_ID);
						test.fail("Unexpected Exception in Payment Add");
					}



					// ================= FINAL STATUS =================
					finalStatus = addSuccess;

					// ================= WRITE RESULT =================
					exUtil.writeToExcel(workbook, "Payment", rw, col, finalStatus);

					if (finalStatus) {

						test.pass("Payment Test Case PASSED");
					} else {
						test.fail("Payment Test Case FAILED");
						allSuccess = false;
					}

				} catch (Exception e) {
					allSuccess = false;
					crmFunctions.captureScreenshot("Payment_Main_Error");
					test.fail("Unexpected Exception in Payment Main");
				}
			}

			return allSuccess;
		}
	}



