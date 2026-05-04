package crm;

import java.io.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilsCrm.crmUtils;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

//  Extent Report imports
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class crmDriver {

	public static WebDriver driver;
	public static WebDriverWait wait;
	public static Workbook workbook;

	public static String Excelpath = "C:\\Users\\Chella\\eclipse-workspace\\mavenCrm\\crmSource.xlsx";

	public static ExtentReports extent;
	public static ExtentTest test;
	public static ExtentSparkReporter sparkReporter;

	// === SETUP ===
	public void setup() throws IOException, InterruptedException {

		//  Extent Report setup
		sparkReporter = new ExtentSparkReporter("crmAutomationReport.html");
		extent = new ExtentReports();
		extent.attachReporter(sparkReporter);
		System.out.println("Extent initialized");

		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
		//FIX — auto handle popup alerts
		options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);

		driver = new ChromeDriver(options);

		driver.manage().window().maximize();

		//  Extent test create + info
		test = extent.createTest("CRM Automation Test", "Opening Chrome and maximizing window");
		test.info("Chrome browser launched and maximized");

		// Close any open Excel instance
		Runtime.getRuntime().exec("taskkill /F /IM excel.exe");

		FileInputStream fis = new FileInputStream(Excelpath);
		workbook = new XSSFWorkbook(fis);
		fis.close();
	}

	// === TEARDOWN ===
	public void tearDown() {
		try {
			if (workbook != null)
				workbook.close();

			if (driver != null) {
				driver.close();
				driver.quit();
			}

			//  Extent flush
			if (extent != null)
				extent.flush();

			System.out.println("Closed browser and Excel.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// === MAIN EXECUTION FLOW ===
	public static void main(String[] args) throws IOException, InterruptedException {

		long startTime = System.currentTimeMillis();
		String startTimeStr = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(startTime));
		System.out.println(startTimeStr);

		crmDriver base = new crmDriver();
		base.setup();

		try {
			crmUtils xlutil = new crmUtils();
			crmLogin log = new crmLogin();
			DataFormatter formatter = new DataFormatter();

			Sheet masterSheet = workbook.getSheet("Master");
			int lastRow = masterSheet.getLastRowNum();

			for (int i = 1; i <= lastRow; i++) {

				Row row = masterSheet.getRow(i);
				if (row == null)
					continue;

				String functionName = formatter.formatCellValue(row.getCell(0)).trim();
				String execStatus = formatter.formatCellValue(row.getCell(1)).trim();

				if (execStatus.equalsIgnoreCase("Yes")) {

					System.out.println(" Running: " + functionName);
					boolean status = false;

					// Start time for each function
					long fnStart = System.currentTimeMillis();

					switch (functionName.toLowerCase()) {

					case "login":
						int loginLastRow = xlutil.returnLastRowNo("Login", workbook);
						status = log.LoginPage(driver, workbook, loginLastRow);
						xlutil.updateSummaryToMaster(workbook, "Login", Excelpath);
						break;
						
					case "department":
					    department dep = new department();
					    int depLastRow = xlutil.returnLastRowNo("Department", workbook);
					    status = dep.department(workbook, depLastRow);
					    xlutil.updateSummaryToMaster(workbook, "Department", Excelpath);
					    break;
					    
					case "designation":
					    Designation des = new Designation();
					    int desLastRow = xlutil.returnLastRowNo("Designation", workbook);
					    status = des.Designation(workbook, desLastRow);
					    xlutil.updateSummaryToMaster(workbook, "Designation", Excelpath);
					    break;
					    
					case "profession":
					    Profession prof = new Profession();
					    int profLastRow = xlutil.returnLastRowNo("Profession", workbook);
					    status = prof.Profession(workbook, profLastRow);
					    xlutil.updateSummaryToMaster(workbook, "Profession", Excelpath);
					    break;
					    
					case "customer":
					    Customer cust = new Customer();
					    int custLastRow = xlutil.returnLastRowNo("Customer", workbook);
					    status = cust.Customer(workbook, custLastRow);
					    xlutil.updateSummaryToMaster(workbook, "Customer", Excelpath);
					    break;
					    
					case "employee":
					    Employee emp = new Employee();
					    int empLastRow = xlutil.returnLastRowNo("Employee", workbook);
					    status = emp.Employee(workbook, empLastRow);
					    xlutil.updateSummaryToMaster(workbook, "Employee", Excelpath);
					    break;
					    
					case "createaccount":
					    createAccount acc = new createAccount();
					    int accLastRow = xlutil.returnLastRowNo("createAccount", workbook);
					    status = acc.createAccount(workbook, accLastRow);
					    xlutil.updateSummaryToMaster(workbook, "createAccount", Excelpath);
					    break;
					    
					case "payment":
					    Payment pay = new Payment();
					    int payLastRow = xlutil.returnLastRowNo("Payment", workbook);
					    status = pay.Payment(workbook, payLastRow);
					    xlutil.updateSummaryToMaster(workbook, "Payment", Excelpath);
					    break;




					default:
						System.out.println("No matching function: " + functionName);
						break;
					}

					// End time for each function
					long fnEnd = System.currentTimeMillis();
					long durationSec = (fnEnd - fnStart) / 1000;
					String totalTime = durationSec + " Sec";

					// Update time column in master
					xlutil.updateTimeToMaster(workbook, functionName, totalTime, Excelpath);

				} else {
					System.out.println("Skipping: " + functionName);
				}
			}

			// Save final Excel changes
			try (FileOutputStream fos = new FileOutputStream(Excelpath)) {
				workbook.write(fos);
			}

			System.out.println("Master sheet updated successfully.");

		} catch (Exception e) {
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		String endTimeStr = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(endTime));

		long durationSec = (endTime - startTime) / 1000;
		System.out.println("🕒 Test Execution Ended at: " + endTimeStr);
		System.out.println("⏱ Total Duration: " + durationSec + " seconds");

		base.tearDown();
	}
}
