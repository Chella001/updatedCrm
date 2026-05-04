package utilsCrm;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import crm.crmDriver;

public class crmFunctions extends crmDriver {

	private WebDriverWait wait;

	public crmFunctions() {
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	}

	// ---------- CLICK ----------
	public void clickById(String id) {
		try {
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
			try {
				element.click();
				System.out.println(id + " has been clicked");
			} catch (Exception normalClickFailed) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
			}
		} catch (Exception e) {
			System.out.println(id + " is not visible");
		}
	}

	
	public void clickByXpath(String xpath) {
		try {
			WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
			try {
				element.click();
				System.out.println("Xpath has been clicked");
			} catch (Exception normalClickFailed) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
			}
		} catch (Exception e) {
			System.out.println("Xpath is not visible: " + xpath);
		}
	}

	// ---------- ENTER TEXT ----------
	public void enterTextById(String id, String text) {
		try {
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
			element.clear();
			element.sendKeys(text);
		} catch (Exception e) {
			System.out.println("Text entry failed for ID: " + id);
		}
	}

	public void enterTextByXpath(String xpath, String text) {
		try {
			WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			element.clear();
			element.sendKeys(text);
		} catch (Exception e) {
			System.out.println("Text entry failed for Xpath: " + xpath);
		}
	}
	
	 public WebElement getElementById(String id) {
	        try {
	            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
	        } catch (Exception e) {
	            System.out.println(id + " is not visible");
	            return null;
	        }
	    }
	 
	 public String getSuccessMessage(String xpath) {
	        try {
	            WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
	            return message.getText();
	        } catch (Exception e) {
	            System.out.println("Success message path not found: " + xpath);
	            return null;
	        }
	    }
	   public void enterTextByCss(String cssSelector, String text) {
	        try {
	            WebElement element = getElementByCss(cssSelector);
	            element.clear();
	            element.sendKeys(text);
	            Thread.sleep(300);
	            element.sendKeys(Keys.ENTER);
	            System.out.println(text +" has been entered");

	        } catch (Exception e) {
	            System.out.println("Text entry failed for CSS: " + cssSelector);
	        }
	    }

	private WebElement getElementByCss(String cssSelector) {
		// TODO Auto-generated method stub
		return null;
	}
	  // Scroll 
	public void scrollIntoViewByXpath(String xpath) {
	    WebElement element = driver.findElement(By.xpath(xpath));
	    JavascriptExecutor js = (JavascriptExecutor) driver;
	    js.executeScript(
	        "arguments[0].scrollIntoView({block:'nearest', inline:'center'});",
	        element
	    );
	}

	public static boolean isValidMobile(String mobile) {
        if (mobile == null) return false;
        return mobile.trim().matches("^[6-9][0-9]{9}$");
    }
	
	//Amount
	public static boolean isValidAmount(String amount) {
	    if (amount == null) return false;

	    amount = amount.trim();   // Fix: remove unwanted spaces

	    return amount.matches("\\d+(\\.\\d{1,2})?");
	}

    // ================= EMPLOYEE CODE VALIDATION =================
    public static boolean isValidEmployeeCode(String empCode) {
        if (empCode == null) return false;
        return empCode.trim().length() >= 3;
    }
    
    public void clearAndType(By locator, String value) {
        WebElement element = driver.findElement(locator);
        element.clear();
        element.sendKeys(value);
    }

     // DownArrow and Select
    
    public void selectAutoSuggestionById(String id) {

        WebElement element = driver.findElement(By.id(id));

        element.sendKeys(Keys.BACK_SPACE);
        //Thread.sleep(1000);
        element.sendKeys(Keys.ARROW_DOWN);
        element.sendKeys(Keys.ARROW_DOWN);
        element.sendKeys(Keys.ENTER);
    }
    
    // DropDown functions
    
    public void selectByVisibleText(By locator, String value) {

        if (value == null || value.trim().isEmpty()) {
            System.out.println("Dropdown value is empty");
            return;
        }

        WebElement dropdown = driver.findElement(locator);
        Select select = new Select(dropdown);
        select.selectByVisibleText(value.trim());
    }

 // ---------- ALERT HANDLING ----------
    public String handleAlertIfPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());

            String alertText = alert.getText();
            System.out.println("Alert Message: " + alertText);

            alert.accept();   // Click OK button

            return alertText;

        } catch (TimeoutException e) {
            // No alert appeared within 5 seconds
            return null;
        } catch (NoAlertPresentException e) {
            return null;
        }
    }

    
	// ---------- SCREENSHOT ----------
	public static void captureScreenshot(String testName) {
		try {
			File folder = new File("C:\\Users\\Chella\\OneDrive\\Pictures\\Screenshots");
			if (!folder.exists())
				folder.mkdirs();

			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String destPath = folder + "\\" + testName + "_" + timestamp + ".png";

			FileHandler.copy(src, new File(destPath));
			System.out.println("Screenshot saved at: " + destPath);
		} catch (Exception e) {
			System.out.println("Failed to capture screenshot: " + e.getMessage());
		}
	}
}
