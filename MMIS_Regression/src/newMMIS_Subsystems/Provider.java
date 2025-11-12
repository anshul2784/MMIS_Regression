package newMMIS_Subsystems;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ newMMIS_Subsystems.TestNGCustom.class })
public class Provider extends Login{
	
	public boolean foundit = false;
	public boolean test22928foundit = false;
	public boolean refound = false;
	public String portalATN;
	public boolean portalEnroll = false;
	public boolean portalEnrollrel = false;
	public static String lName,fName, lNameR, lon, lat;
	public static String provTC23099;
	public static String recredProvID = "110005785A";
	public static String relEntityPid="110076470";
	public String profileUpdtPid="110000314";
	public static String endDate="12/31/2299";

	
	@BeforeTest
    public void provStartup() throws Exception {
    	log("Starting Provider Subsystem......");
    }
	
	@BeforeMethod
	public void LoginCheck() throws Exception {
		Common.resetBase();
		testCheckDBLoginSuccessful();	
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Provider")).click();
	}
	
    @Test
    public void test23135a() throws Exception{
    	TestNGCustom.TCNo="23135a";
    	log("//TC 23135a");
    	
        //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/prw53001.rpt.* | tail -1"; //grabs the latest gdg of the file
		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
		fileName = fileName.substring(fileName.length()-40);
		
		log(" The Provider Changes By Clerk ID Weekly Summary Report filename is: "+fileName);

		//Verify CLERK ID in report
		if ((env.equals("MO"))||(env.equals("AWSMO"))) {
			command = "grep DSMAMOD "+fileName;
			error = "The selected file is not correct/ or it is empty/ or it does not have DSMAMOD user id";
			String outputText = Common.connectUNIX(command, error);
			
			Assert.assertTrue(outputText.contains("DSMAMOD"), "File does not have DSMAMOD user id");
		} 
		else if (env.equals("PERF")) {
			command = "grep DSMAPERF "+fileName;
			error = "The selected file is not correct/ or it is empty/ or it does not have DSMAPERF user id";
			String outputText = Common.connectUNIX(command, error);
			
			Assert.assertTrue(outputText.contains("DSMAPERF"), "File does not have DSMAPERF user id");
		}
		else if ((env.equals("MO2"))||(env.equals("AWSMO2"))) {
			command = "grep DSMATSTX "+fileName;
			error = "The selected file is not correct/ or it is empty/ or it does not have DSMATSTX user id";
			String outputText = Common.connectUNIX(command, error);
			
			Assert.assertTrue(outputText.contains("DSMATSTX"), "File does not have DSMATSTX user id");
		}
		else if ((env.equals("PERF"))||(env.equals("AWSPERF"))) {
			command = "grep DSMAPERF "+fileName;
			error = "The selected file is not correct/ or it is empty/ or it does not have DSMAPERF user id";
			String outputText = Common.connectUNIX(command, error);

			Assert.assertTrue(outputText.contains("DSMAPERF"), "File does not have DSMAPERF user id");
		}
		else {
			command = "grep DSMAACC "+fileName;
			error = "The selected file is not correct/ or it is empty/ or it does not have DSMAACC user id";
			String outputText = Common.connectUNIX(command, error);
			
			Assert.assertTrue(outputText.contains("DSMAACC"), "File does not have DSMAACC user id");
		}


    }
    
    @Test
    public void test23135b() throws Exception{
    	TestNGCustom.TCNo="23135b";
    	log("//TC 23135b");
    	
    	//Get data from tables that needs to be verified in the report (Newly Enrolled Providers Report)
		sqlStatement ="select * from r_day2 where TC = '22463' and DES='Enrolled Prov ID'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no Prov ID generated");

		String providerID = colValues.get(0);
		log("Provider is: "+providerID);
		
		sqlStatement ="select * from r_day2 where TC = '22463' and DES='Enrolled Prov lName'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no Prov Last Name generated");

		String providerlName = colValues.get(0);
		log("Provider Last name is: "+providerlName);
		
		sqlStatement ="select * from r_day2 where TC = '22463' and DES='Enrolled Prov fName'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no Prov First Name generated");

		String providerfName = colValues.get(0);
		log("Provider First name is: "+providerfName);
    	
        //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/prd30001.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";
		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
//		fileName = fileName.substring(fileName.length()-40);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The Newly Enrolled Providers  Report filename is: "+fileName);

		//Verify data in file
		command = "grep "+providerID+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have provider "+providerID+" in it";
		String outputText = Common.connectUNIX(command, error);
		log("Output string from report is: "+outputText);
		
		Assert.assertTrue(outputText.contains(providerID), "File does not have provider "+providerID+" in it");
		Assert.assertTrue(outputText.contains(providerlName), "File does not have provider last name"+providerlName+" in it");
		Assert.assertTrue(outputText.contains(providerfName), "File does not have provider first name"+providerfName+" in it");
		
		//Generate provider welcome letter - This scenario was added on 3/20/2014 to increase the scope of regression suite as part of technical refresh.
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRptsAndLettersNavigatorPanel:ProviderReportsNavigator:GRP_g2")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRptsAndLettersNavigatorPanel:ProviderReportsNavigator:ITM_n107")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_ProviderId")).sendKeys(providerID);
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_ProviderLoc")).sendKeys("A");
		Common.search();
		//Verify letter name ang req date
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id48")).getText().equals("PRV-9008-R")), "PRV-9008-R Newly enrolled provider welcome Letter not found");
		Assert.assertTrue((driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:tbody_element']/tr/td[2]")).getText().equals(Common.convertSysdatecustom(-2))), "request date was not from this reg cycle(yest)");
		//Generate letter
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id48")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Generate and Print']")).click();
		log("Provider Welcome Batch Letter Name is: "+Common.fileName());

    }
  
    
    @Test
	public void test22461a() throws Exception {
		TestNGCustom.TCNo="22461a";
    	log("//TC 22461a");

		
    	//Reset prov Address in Base
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(profileUpdtPid);
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
	    Common.search();
	    Thread.sleep(5000);
	    if(driver.findElements(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).size()>0)
	    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	    else {
			driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(profileUpdtPid);
		    Common.search();
	    	
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10422")).click();
		    
		    //sort by end date
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityList:ProviderPublicHealthProgramEligibilityBean_ColHeader_financialPayer_finPayerShortDescription")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityList:ProviderPublicHealthProgramEligibilityBean_ColHeader_endDate")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityList:ProviderPublicHealthProgramEligibilityBean_ColHeader_endDate")).click();
		    
		    //click first program
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityList_0:ProviderPublicHealthProgramEligibilityBean_ColValue_prEnrollPgm_providerProgramDescription")).click();
			new Select(driver.findElement(By.xpath("//select[contains(@id,'MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_PrEnrollStatus')]"))).selectByVisibleText("ACTIVE - Pay");
			Common.save();
			
			driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).clear();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(profileUpdtPid);
		    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
		    Common.search();
		    Thread.sleep(5000);

	    }
	    
	    //Select Loc Name Address
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10414")).click();
	    //Click on billing address
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressList_0:ProviderLocationNameAddressBean_ColValue_prAddrCode_addressUsageDescription")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Maintain Address']")).click();
		//Enter the provider address
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:contactNameID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:contactNameID")).sendKeys("ANTILESSAMUELMD");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:address1ID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:address1ID")).sendKeys("133 MAPLE STREET");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:cityID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:cityID")).sendKeys("SPRINGFIELD");
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressDataPanel_MailState"))).selectByVisibleText("MA");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:zipID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:zipID")).sendKeys("01105");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:phoneID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:phoneID")).sendKeys("4137329201");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:emailID")).clear();
	    Common.update();
	    //Address verification
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).click();
		Common.saveAll();
		//Go to portal
//		String provList="td.leftPadding.leftAlign > span";
		
		Common.portalLogin();
		driver.findElement(By.linkText("Manage Provider Information")).click();
		driver.findElement(By.linkText("Maintain Profile")).click();
		driver.findElement(By.linkText("Update Your MassHealth Profile")).click();
//	    Assert.assertTrue(driver.findElement(By.cssSelector(provList)).getText().equals("Please select the provider you wish to update."));
		//driver.wait(5000);
		int i=0;
		int j=0;
		int a=1;
		int b=2;

		String pName = "ANTILES SAMUEL";
		for (;a<b; a++){
			for (;i<=j; i++){
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				if ((driver.findElements(By.xpath("//*[contains(@id,'"+i+":name')]")).size())>0) { 
					if (pName.equals(driver.findElement(By.xpath("//*[contains(@id,'"+i+":name')]")).getText())) { 
						driver.findElement(By.xpath("//*[contains(@id,'"+i+":name')]")).click();
							foundit=true;
					}
					else
						j=j+1;
				}
			}
			if ((!foundit) && ((driver.findElements(By.xpath("//*[contains(@id,'providerList:paginatoridx"+b+"')]")).size())>0)) {
				driver.findElement(By.xpath("//*[contains(@id,'providerList:paginatoridx"+b+"')]")).click();
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//			    Assert.assertTrue(driver.findElement(By.cssSelector(provList)).getText().equals("Please select the provider you wish to update."));
				b=b+1;
				i=i-1;
			}
		}
		if (!foundit)
			throw new SkipException("I couldnt find prov '"+pName+"' in portal in the list of providers, exiting now");
		
		//Perform the profile update
		//Enter legal entity data
		driver.findElement(By.xpath("//input[contains(@id,'birthDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'birthDate')]")).sendKeys("01/01/1991");
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityContact')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityContact')]")).sendKeys("me");
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_line1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_line1')]")).sendKeys("133 MAPLE STREET");
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_city')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_city')]")).sendKeys("SPRINGFIELD");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'legalEntityAddress_state')]"))).selectByVisibleText("Massachusetts");
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_zip')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_zip')]")).sendKeys("01105");
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_email')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_email')]")).sendKeys("ansh90@gmail.com");
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_phoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_phoneNumber')]")).sendKeys("4137329201");
	    
	    //Change address
		driver.findElement(By.xpath("//*[contains(@id,'MENUITEM_provPrflAddressType')]")).click();
		driver.findElement(By.xpath("//*[contains(@id,'0:dbaName')]")).click();
		driver.findElement(By.linkText("Billing Address")).click();
		driver.findElement(By.xpath("//input[contains(@id,'contact')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'contact')]")).sendKeys("SAMUEL ANTILES");
		driver.findElement(By.xpath("//input[contains(@id,'address_line1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'address_line1')]")).sendKeys("101 FEDERAL STREET");
		driver.findElement(By.xpath("//input[contains(@id,'address_city')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'address_city')]")).sendKeys("BOSTON");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'address_state')]"))).selectByVisibleText("Massachusetts");
		driver.findElement(By.xpath("//input[contains(@id,'address_zip')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'address_zip')]")).sendKeys("02110");
		driver.findElement(By.xpath("//input[contains(@id,'address_email')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'address_email')]")).sendKeys("ansh90@gmail.com");
		driver.findElement(By.xpath("//input[contains(@id,'address_phoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'address_phoneNumber')]")).sendKeys("4137329201");
		driver.findElement(By.xpath("//input[@class='buttonFunctional' and @alt='Update']")).click();

		//Submit the profile update
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Submit']")).click();
		driver.findElement(By.xpath("//input[contains(@id,'agreementAcceptanceIndicator')]")).click();
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Submit']")).click();
		
		Common.portalLogout();

	}
    
	@Test
	public void test22461b() throws Exception {
		TestNGCustom.TCNo="22461b";
    	log("//TC 22461b");

		if (!foundit) 
			Assert.assertTrue(false, "Not continuing with Profile Update test as it failed on portal or address change failed on Base");

		else {
			//Navigate to profile update panel
			driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateSearch")).click();
			driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateSearchBean_CriteriaPanel:ProfileUpdateSearchPanel_providerId")).sendKeys(profileUpdtPid);
			Common.search();
			
		    //sort by date received desc
			driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
			driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
			
			//select first row checkbox
//			driver.findElement(By.xpath("//*[@id='rowCheck[0]']")).click();
			driver.findElement(By.xpath("(//*[text()='READY FOR REVIEW'])[2]//../td[1]/input")).click();
			
			//click on Review button
			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Review']")).click();
			
			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressList_0:ProviderLocationNameAddressBean_ColValue_status")).getText().equals("Updated"), "The billing adddress did not come as updated"); 
			driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressList_0:ProviderLocationNameAddressBean_ColValue_status")).click();
			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:contactNameID")).getAttribute("value").equals("SAMUEL ANTILES"), "The contact name did not come as updated"); 
			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:address1ID")).getAttribute("value").equals("101 FEDERAL STREET"), "The adddress1 did not come as updated"); 
			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:cityID")).getAttribute("value").equals("BOSTON"), "The city did not come as updated"); 
			Assert.assertTrue(driver.findElement(By.xpath("//option[@selected='selected' and @value='MA']")).getAttribute("value").equals("MA"), "The state did not come as updated"); 
			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:zipID")).getAttribute("value").equals("02110"), "The zip did not come as updated"); 
			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:phoneID")).getAttribute("value").equals("(413)732-9201"), "The phone did not come as updated"); 
			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:emailID")).getAttribute("value").equals("ansh90@gmail.com"), "The email did not come as updated"); 
		    driver.findElement(By.id("ProviderLocationNameAddressPanel_cancelAction_btn")).click();

			//Change status to IN PROCESS
		    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderworkflowProfileUpdateStatusPanel:WorkflowStatusdropdown"))).selectByVisibleText("IN PROCESS");
		    Common.saveAll();
	//	    df30872_saveAll(); //remove after DF 20872 is fixed, and uncomment above line of Common.saveAll()
	    
		  //Select The WI again from PROFILE UPDATE panel
		    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateSearch")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateSearchBean_CriteriaPanel:ProfileUpdateSearchPanel_providerId")).sendKeys(profileUpdtPid);
		    Common.search();
		    
		    //sort by date received desc
			driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
			driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
			
			//select first row checkbox
//			driver.findElement(By.xpath("//*[@id='rowCheck[0]']")).click();
			driver.findElement(By.xpath("(//*[text()='IN PROCESS'])[2]//../td[1]/input")).click();

			
			//click on View button
			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Review']")).click();
			//Change status to COMPLETE
		    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderworkflowProfileUpdateStatusPanel:WorkflowStatusdropdown"))).selectByVisibleText("COMPLETE");
		    Common.saveAll();
	//	    df30872_saveAll(); //remove after DF 20872 is fixed, and uncomment above line of Common.saveAll()
	    
			//Verify prov address is changed
			String idProvider="110000314";
			driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).clear();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(idProvider);
		    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
		    Common.search();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
		    //Select Loc Name Address
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10414")).click();
		    //Click on remittance address
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressList_0:ProviderLocationNameAddressBean_ColValue_prAddrCode_addressUsageDescription")).click();
	
//			//Verify provider address
//			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:contactNameID")).getAttribute("value").equals("SAMUEL ANTILES"), "The contact name update failed"); 
//			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:address1ID")).getAttribute("value").equals("101 FEDERAL STREET"), "The adddress1 update failed"); 
//			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:cityID")).getAttribute("value").equals("BOSTON"), "The city update failed"); 
//			Assert.assertTrue(driver.findElement(By.xpath("//option[@selected='selected' and @value='MA']")).getAttribute("value").equals("MA"), "The state update failed"); 
//			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:zipID")).getAttribute("value").equals("02110"), "The zip update failed"); 
//			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:phoneID")).getAttribute("value").equals("(413)732-9201"), "The phone update failed"); 
//			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:emailID")).getAttribute("value").equals("ansh90@gmail.com"), "The email update failed");
//			Common.cancelAll();
			
			//Verify provider address
		    //Assert.assertTrue(driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:contactNameID']")).getAttribute("value").trim().equals("SAMUEL ANTILES"), "The contact name did not come as updated"); 
		    Assert.assertTrue(driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:FirstNameID']")).getAttribute("value").trim().equals("SAMUEL"), "The contact name did not come as updated"); 
		    Assert.assertTrue(driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:LastNameID']")).getAttribute("value").trim().contains("ANTILES"), "The contact name did not come as updated"); 
		    Assert.assertTrue(driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:contactNameID']")).getAttribute("value").trim().contains("SAMUEL"), "The contact name did not come as updated"); 
		    Assert.assertTrue(driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:contactNameID']")).getAttribute("value").trim().contains("ANTILES"), "The contact name did not come as updated"); 
		    Assert.assertTrue(driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:address1ID']")).getAttribute("value").trim().equals("101 FEDERAL STREET"), "The adddress1 did not come as updated"); 
			Assert.assertTrue(driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:cityID']")).getAttribute("value").trim().equals("BOSTON"), "The city did not come as updated"); 
			Assert.assertTrue(driver.findElement(By.xpath("//option[@selected='selected' and @value='MA']")).getAttribute("value").equals("MA"), "The state did not come as updated"); 
			Assert.assertTrue(driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:zipID']")).getAttribute("value").trim().equals("02110"), "The zip did not come as updated"); 
			Assert.assertTrue(driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:phoneID']")).getAttribute("value").trim().equals("(413)732-9201"), "The phone did not come as updated"); 
			Assert.assertTrue(driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:emailID']")).getAttribute("value").trim().equals("ansh90@gmail.com"), "The email did not come as updated");
			Common.cancelAll();
			
			//Check image
			//Select The WI again from PROFILE UPDATE panel
		    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateSearch")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateSearchBean_CriteriaPanel:ProfileUpdateSearchPanel_providerId")).sendKeys(profileUpdtPid);
		    Common.search();
		    
		    //sort by date received desc
			driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
			driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();

			
			driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss_0:_id15']")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList:ProviderImagesSearchResultBean_ColHeader_receiptDate")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList:ProviderImagesSearchResultBean_ColHeader_receiptDate")).click();
			Assert.assertTrue(driver.findElement(By.xpath("//*[contains(@id,'ProviderImagesWorkflowSearchResultPanel') and contains(@id,'0:') and contains(@id,'receiptDate') ]")).getText().equals(Common.convertSysdate()), "The profile update image is not in todays date"); 
			driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList_0:_id80']")).click();
		}
	}
	
	@Test
	public void test22463() throws Exception {
		TestNGCustom.TCNo="22463";
    	log("//TC 22463");
    	
    	lName=Common.generateRandomName();
    	fName=Common.generateRandomName();
    	String adr1="101 FEDERAL STREET";
    	String city="BOSTON";
    	String state ="Massachusetts";
    	String stateab ="MA";
    	String zip="02110";
    	String phone="6176176177";
    	String email="ansh90@gmail.com";
    	
    	//Store provider names
    	String SelSql="select * from r_day2 where TC = '22463' and DES='Enrolled Prov lName'";
    	String col="ID";
    	String DelSql="delete from r_day2 where TC = '22463'and DES='Enrolled Prov lName'";
    	String InsSql="insert into r_day2 values ('22463', '"+lName+"', 'Enrolled Prov lName', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '22463' and DES='Enrolled Prov fName'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '22463'and DES='Enrolled Prov fName'";
    	InsSql="insert into r_day2 values ('22463', '"+fName+"', 'Enrolled Prov fName', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);

		Common.portalLogin();
		driver.findElement(By.linkText("Manage Provider Information")).click();
		driver.findElement(By.linkText("Enrollment")).click();
		driver.findElement(By.linkText("Start an Enrollment Application")).click();
		conti();
		driver.findElement(By.xpath("//input[contains(@id,'lastName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'lastName')]")).sendKeys(lName);
		driver.findElement(By.xpath("//input[contains(@id,'firstName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'firstName')]")).sendKeys(fName);
		driver.findElement(By.xpath("//input[contains(@id,'providerSSN')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerSSN')]")).sendKeys(Common.generateRandomTaxID());
		driver.findElement(By.xpath("//input[contains(@id,'providerNPI')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerNPI')]")).sendKeys(Common.generateRandomTaxID()+"1");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'legalEntityType')]"))).selectByVisibleText("INDIVIDUAL PRACTITIONER");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'providerType')]"))).selectByVisibleText("PHYSICIAN");
		conti();
		conti();
		conti();
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationlastName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationlastName')]")).sendKeys(lName);
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationfirstName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationfirstName')]")).sendKeys(fName);
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationdateOfBirth')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationdateOfBirth')]")).sendKeys("01011966");
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationPIN')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationPIN')]")).sendKeys("8188");
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressLine1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressLine1')]")).sendKeys(adr1);
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressCity')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressCity')]")).sendKeys(city);
		new Select(driver.findElement(By.xpath("//select[contains(@id,'providerAddressState')]"))).selectByVisibleText(state);
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressZip')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressZip')]")).sendKeys(zip);
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressPhoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressPhoneNumber')]")).sendKeys(phone);
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressEmail')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressEmail')]")).sendKeys(email);
		//Add wait to solve captcha manually
		System.out.println("Solve captcha now. Waiting 40 seconds");
		Thread.sleep(40000);
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Submit']")).click();
		portalATN = driver.findElement(By.xpath("//*[contains(@id,'atn1')]")).getText();
		log(portalATN);
		conti();
		
		
		//Legal Entity
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_birthDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_birthDate')]")).sendKeys("01/01/1991");
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityContact')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityContact')]")).sendKeys(fName+" "+lName);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityAddr1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityAddr1')]")).sendKeys(adr1);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityCity')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityCity')]")).sendKeys(city);
		new Select(driver.findElement(By.xpath("//select[contains(@id,'provider_legalEntity_legalEntityState')]"))).selectByVisibleText(state);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityZip')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityZip')]")).sendKeys(zip);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityEmail')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityEmail')]")).sendKeys(email);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityPhoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityPhoneNumber')]")).sendKeys(phone);
		new Select(driver.findElement(By.xpath("//select[contains(@id,'provider_legalEntity_legalEntityOwnershipClass')]"))).selectByVisibleText("COUNTY");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'provider_legalEntity_legalEntityOwnershipType')]"))).selectByVisibleText("Corporation");
		driver.findElement(By.xpath("//input[@value='false' and contains(@name, 'provider_legalEntity_commercialUsageIndicator')]")).click();
		conti();
		
		//New Changes
		conti();
		conti();
		conti();
		conti();
		conti();
		driver.findElement(By.xpath("//input[contains(@id,'attestName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'attestName')]")).sendKeys("ANSHUL GANDHI");
		driver.findElement(By.xpath("//input[contains(@id,'attestDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'attestDate')]")).sendKeys(Common.convertSysdate());
		driver.findElement(By.xpath("//input[contains(@id,'attestTitle')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'attestTitle')]")).sendKeys("BA");
	    conti();
	    
	    
	    //Service Locations
	    newItem();
		driver.findElement(By.xpath("//input[contains(@id,'lastName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'lastName')]")).sendKeys(lName);
		driver.findElement(By.xpath("//input[contains(@id,'firstName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'firstName')]")).sendKeys(fName);
		driver.findElement(By.xpath("//input[contains(@id,'serviceLocationContact')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'serviceLocationContact')]")).sendKeys(fName+" "+lName);
		driver.findElement(By.xpath("//input[contains(@id,'address1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'address1')]")).sendKeys(adr1);	    
		driver.findElement(By.xpath("//input[contains(@id,'city')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'city')]")).sendKeys(city);
		new Select(driver.findElement(By.xpath("//select[contains(@id,'state')]"))).selectByVisibleText(state);
		driver.findElement(By.xpath("//input[contains(@id,'zip')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'zip')]")).sendKeys(zip);
		driver.findElement(By.xpath("//input[contains(@id,'email')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'email')]")).sendKeys(email);
		driver.findElement(By.xpath("//input[contains(@id,'phoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'phoneNumber')]")).sendKeys(phone);
		driver.findElement(By.xpath("//input[contains(@id,'phoneNumberForTTDTTY')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'phoneNumberForTTDTTY')]")).sendKeys(phone);
	    add();
	    conti();
	    
	    conti();
	    
	    //Identification Info
		driver.findElement(By.xpath("//*[contains(@id,'0:dbaName_Link')]")).click();
		new Select(driver.findElement(By.xpath("//select[contains(@id,'organizationType')]"))).selectByVisibleText("CHAIN");
		driver.findElement(By.xpath("//input[contains(@id,'deaNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'deaNumber')]")).sendKeys(Common.generateRandomTaxID());
		driver.findElement(By.xpath("//input[contains(@id,'deaEffectiveDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'deaEffectiveDate')]")).sendKeys(Common.convertSysdate());
		driver.findElement(By.xpath("//input[contains(@id,'deaEndDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'deaEndDate')]")).sendKeys("12/31/2299");
	    update();
	    conti();
	    
	    conti();
	    conti();
	    conti();
	    conti();
	    
	    //License Info
		driver.findElement(By.xpath("//*[contains(@id,'0:dbaName_Link')]")).click();
		newItem();
		driver.findElement(By.xpath("//input[contains(@id,'licenseNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'licenseNumber')]")).sendKeys(Common.generateRandomTaxID()+"6");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'agencyName')]"))).selectByVisibleText("MA BOARD OF REGISTRATION IN MEDICINE");
		driver.findElement(By.xpath("//input[contains(@id,'licenseEffectiveDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'licenseEffectiveDate')]")).sendKeys(Common.convertSysdate());
		driver.findElement(By.xpath("//input[contains(@id,'licenseEndDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'licenseEndDate')]")).sendKeys("12/31/2299");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'licenseState')]"))).selectByVisibleText(state);
	    add();
	    conti();
	    
	    conti();
	    conti();
	    
	    //PCC
		driver.findElement(By.xpath("//*[contains(@id,'0:dbaName_Link')]")).click();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_managedCareContact')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_managedCareContact')]")).sendKeys(Common.generateRandomName());
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_email')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_email')]")).sendKeys(email);
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_phoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_phoneNumber')]")).sendKeys(phone);
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_maxMembersRequested')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_maxMembersRequested')]")).sendKeys("99");
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_ageLessThan')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_ageLessThan')]")).sendKeys("99");
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_ageGreaterThan')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_ageGreaterThan')]")).sendKeys("11");
	    update();
	    conti();
	    
	    conti();
	    conti();
	    conti();
	    conti();
	    
	    //Trading Partner Agreement
		driver.findElement(By.xpath("//input[contains(@id,'agreementDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'agreementDate')]")).sendKeys(Common.convertSysdate());
		driver.findElement(By.xpath("//input[contains(@id,'legalName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalName')]")).sendKeys(Common.generateRandomName());
		driver.findElement(By.xpath("//input[contains(@id,'signature')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'signature')]")).sendKeys(Common.generateRandomName());
		driver.findElement(By.xpath("//input[contains(@id,'phoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'phoneNumber')]")).sendKeys("6176176777");
		driver.findElement(By.xpath("//input[contains(@id,'email')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'email')]")).sendKeys(email);
	    conti();
	    
//	    conti();
	    
	    //Provider Contract
		driver.findElement(By.xpath("//input[@type='radio' and @value='true']")).click();
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Submit']")).click();
		
		//Verify ATN
		Assert.assertTrue(driver.findElement(By.xpath("//form[@id='enrollmentConfirmation']/table/tbody/tr/td[2]/div/table/tbody/tr[11]/td/span[5]/i")).getText().equals(portalATN), "Provider enrollment confirmation failed on submit. ATN not present on confirmation page");
		Common.portalLogout();
		LoginCheck();
		
		//Complete enrollment on base
		//IN PROCESS
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(portalATN);
    	Common.search();
		driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("12 - IN PROCESS");
	    Common.save();
	    
		//READY TO ENROLL
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(portalATN);
    	Common.search();
		driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("33 - READY TO ENROLL");
	    Common.save();
	    
		
		//ID Issued
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(portalATN);
    	Common.search();
		driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
	    driver.findElement(By.linkText("Enroll Provider")).click();
	    
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_DelegatedCredentIndicator"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ManagedCareGroup"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_BillingAgent"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ManagedCareOrganization"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_HospitalAffiliation"))).selectByVisibleText("No");
	    
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_CityTownCode"))).selectByVisibleText("ACTON");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_ProviderOrganizationType"))).selectByVisibleText("CHAIN");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_UcpIndicator"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_Coverage24hr"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_BypassTPL"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_OrganizationCode"))).selectByVisibleText("Corporation");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_AcceptNewMembers"))).selectByVisibleText("No");
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_NxtSchdCredDate")).sendKeys(Common.convertSysdatecustom(365)); 

	   
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:ProviderEnrollPublicHealthProgramEligibility_NewButtonClay:ProviderEnrollPublicHealthProgramEligibilityList_newAction_btn")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:EnrollProgramDropDown_2"))).selectByVisibleText("ACUTE OUTPATIENT");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_PrEnrollStatus"))).selectByVisibleText("ACTIVE - Pay");
	    
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:ProviderSpecialtyAdd_NewButtonClay:ProviderSpecialtyAddList_newAction_btn")).click();
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:ProviderSpecialtySearchControl_CMD_SEARCH']/img")).click(); //Added /img at end, and changed to xpath for chrome because above line was not working without adding /img.
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:_id149:TypeSpecialitiySearchResults_0:column1Value")).click();

//	    Common.warning();
		Common.save();
		
		//Enrolled
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(portalATN);
    	Common.search();
		driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("34 - ENROLLED");
	    Common.save();
	    String enlProv=driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplProvIDSearchControl")).getAttribute("value");
		log("Provider Id enrolled from portal is "+enlProv);
		
		//Verify provider comes in search results
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(enlProv);
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	    
	    //Store Prov ID for day 2 report
    	SelSql="select * from r_day2 where TC = '22463' and DES='Enrolled Prov ID'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '22463'and DES='Enrolled Prov ID'";
    	InsSql="insert into r_day2 values ('22463', '"+enlProv+"', 'Enrolled Prov ID', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
	}
	
	//Helper functions for 20463
	public void conti() {
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Continue']")).click();
	}
	
	public void newItem() {
		driver.findElement(By.xpath("//input[@class='buttonFunctional' and @alt='New Item']")).click();
	}
	public void add() {
		driver.findElement(By.xpath("//input[@class='buttonFunctional' and @alt='Add']")).click();
	}
	public void update() {
		driver.findElement(By.xpath("//input[@class='buttonFunctional' and @alt='Update']")).click();
	}
	
	public void enrlWrklist(int r, String ATN) {
		//Navigate to enrollment worklist panel
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_EnrollmentWorklist")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklist_DropDownQueueType"))).selectByIndex(r);
	    Common.search();
	    //sort by dte received for faster search
	    driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:_id44")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:_id44")).click();
	
		int i=2;
		int j=2;

		if (ATN.equals(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr/td[2]")).getText()))
			driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr/td/input")).click();
		else {
			for (;i<=j; i++){
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				if ((driver.findElements(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr["+i+"]/td[2]")).size())>0) {
					if (ATN.equals(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr["+i+"]/td[2]")).getText())) { 
						driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
						driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr["+i+"]/td/input")).click();
					}
					else
						j=j+1;
				}
			}
		}

	    //Click on Search
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Review']")).click();
		
	}
	
	public void enrlWrklistNew(String queue, String ATN) {
		//Navigate to enrollment worklist panel
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_EnrollmentWorklist")).click();
		List<WebElement> list = new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklist_DropDownQueueType"))).getOptions();
		for (WebElement i:list) 
			if ((i.getText()).contains(queue)) {
				//This is to handle QA and PREAPPROVAL QA
				if (i.getText().contains("PREAPPROVAL QA"))
					continue;
				else {
					i.click();
					break;
				}
			}
	    Common.search();
	    //sort by dte received for faster search
	    driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:_id44")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:_id44")).click();
	
		int i=1; //item 1 on a page
		int j=2; //page 2
		boolean foundATN = false;

		while(true){
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			if ((driver.findElements(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr["+i+"]/td[2]")).size())>0) {
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				if (ATN.equals(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr["+i+"]/td[2]")).getText())) { 
					driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr["+i+"]/td/input")).click();
					foundATN=true;
					break;
				}
				else
					i++;
			}
			else {
				if ((driver.findElements(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults_DataScrolleridx"+j)).size())>0) {
					driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
					driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults_DataScrolleridx"+j)).click();
					i=1;
					j++;
				}
				else {
					driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
					break;
				}
			}
			
		}

		if(!foundATN)
			Assert.assertTrue(false, "ATN "+ATN+" not found on any page.");
		else
			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Review']")).click();
		
	}
	
	@Test
	public void test22464() throws Exception {
		TestNGCustom.TCNo="22464";
    	log("//TC 22464");
    	
    	lNameR=Common.generateRandomName();
    	String fNameR=Common.generateRandomName();
    	String adr1="101 FEDERAL STREET";
    	String city="BOSTON";
    	String state ="Massachusetts";
    	String stateab ="MA";
    	String zip="02110";
    	String phone="6176176177";
    	String email="ansh90@gmail.com";

		Common.portalLogin();
		driver.findElement(By.linkText("Manage Provider Information")).click();
		driver.findElement(By.linkText("Business Partners (non Provider)")).click();
		driver.findElement(By.linkText("Enroll as Business Partner")).click();
		
		driver.findElement(By.xpath("//input[contains(@id,'lastName')]")).sendKeys(lNameR);
		driver.findElement(By.xpath("//input[contains(@id,'firstName')]")).sendKeys(fNameR);
		driver.findElement(By.xpath("//input[contains(@id,'dob')]")).sendKeys("01/01/1970");
		driver.findElement(By.xpath("//input[contains(@id,'pin')]")).sendKeys("1234");
		driver.findElement(By.xpath("//input[contains(@id,'nonProviderLine1')]")).sendKeys(adr1);		
		driver.findElement(By.xpath("//input[contains(@id,'nonProviderCity')]")).sendKeys(city);
		new Select(driver.findElement(By.xpath("//select[contains(@id,'nonProviderState')]"))).selectByVisibleText(state);
		driver.findElement(By.xpath("//input[contains(@id,'nonProviderZip')]")).sendKeys(zip);
		driver.findElement(By.xpath("//input[contains(@id,'nonProviderPhoneNumber')]")).sendKeys(phone);
		driver.findElement(By.xpath("//input[contains(@id,'nonProviderEmail')]")).sendKeys(email);
		new Select(driver.findElement(By.xpath("//select[contains(@id,'relEntityType')]"))).selectByVisibleText("Billing Intermediary");
		//Add wait to solve captcha manually
		System.out.println("Solve captcha now. Waiting 40 seconds");
		Thread.sleep(40000);
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Submit']")).click();
		
		//Doing Business As (DBA) Address
		driver.findElement(By.xpath("//input[contains(@id,'dbaName')]")).sendKeys(lNameR);
		driver.findElement(By.xpath("//input[contains(@id,'line1')]")).sendKeys(adr1);
		driver.findElement(By.xpath("//input[contains(@id,'city')]")).sendKeys(city);
		new Select(driver.findElement(By.xpath("//select[contains(@id,'state')]"))).selectByVisibleText(state);
		driver.findElement(By.xpath("//input[contains(@id,'zip')]")).sendKeys(zip);
		driver.findElement(By.xpath("//input[contains(@id,'phoneNumber')]")).sendKeys(phone);
		driver.findElement(By.xpath("//input[contains(@id,'email')]")).sendKeys(email);
	    
	    //Information Address
		driver.findElement(By.xpath("//*[contains(@id,'sameAsDbaAddressInd')]")).click();
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Continue to next page']")).click();
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Continue to next page']")).click();
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Submit']")).click();
		
		//Get ATN from DB because no ATN displayed on POSC for Rel Entity enrollment
    	sqlStatement = "select sak_atn from t_pr_appln where rownum < 2 order by sak_atn desc";
    	colNames.add("SAK_ATN");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String portalATN = colValues.get(0);
		log(portalATN);
		Common.portalLogout();
		LoginCheck();
		
		
		//Complete enrollment on base
		//IN PROCESS
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(portalATN);
    	Common.search();
		driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("12 - IN PROCESS");
	    Common.save();
	    
		//READY TO ENROLL
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(portalATN);
    	Common.search();
		driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("33 - READY TO ENROLL");
	    Common.save();
	    
		
		//ID Issued
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(portalATN);
    	Common.search();
		driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
	    driver.findElement(By.linkText("Enroll Relationship Entity")).click();

	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_OwnershipType"))).selectByVisibleText("Corporation");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_DelegatedCredentIndicator"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ManagedCareGroup"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_BillingAgent"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ManagedCareOrganization"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_HospitalAffiliation"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ProviderLegalEntityType"))).selectByVisibleText("BUSINESS CORPORATION");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_CommercialInsurance"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ProviderOwnershipClass"))).selectByVisibleText("COUNTY");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_HospitalAffiliation"))).selectByVisibleText("No");

	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_CityTownCode"))).selectByVisibleText("ACTON");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_ProviderOrganizationType"))).selectByVisibleText("CHAIN");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_UcpIndicator"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_Coverage24hr"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_BypassTPL"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_OrganizationCode"))).selectByVisibleText("Corporation");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_AcceptNewMembers"))).selectByVisibleText("No");
	   
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:ProviderEnrollPublicHealthProgramEligibility_NewButtonClay:ProviderEnrollPublicHealthProgramEligibilityList_newAction_btn")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:EnrollProgramDropDown_2"))).selectByVisibleText("ACUTE OUTPATIENT");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_PrEnrollStatus"))).selectByVisibleText("ACTIVE - Pay");
	    
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:ProviderSpecialtyAdd_NewButtonClay:ProviderSpecialtyAddList_newAction_btn")).click();
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:ProviderSpecialtySearchControl_CMD_SEARCH']/img")).click(); //Added /img at end, and changed to xpath for chrome because above line was not working without adding /img.
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:_id147:TypeSpecialitiySearchResults_0:column1Value")).click();

		Common.save();
		
		//Enrolled
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(portalATN);
    	Common.search();
		driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("34 - ENROLLED");
		Common.saveAll();
		String enlRel=driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplProvIDSearchControl")).getAttribute("value");
		log("Relationship Entity enrolled from portal is "+enlRel);
		
		//Verify id does not come in provider search results
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(enlRel);
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
	    Common.search();
		Assert.assertTrue((driver.findElement(By.cssSelector("span.redreg")).getText().equals("***No records found***")), "Relation entity got returned in provider search results");
		
		//Verify id comes in rel entity search results
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelationshipEntity")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntitySearchBean_CriteriaPanel:ProvRelationshipEntitySearchCrit_ID")).sendKeys(enlRel);
	    Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntitySearchResults_0:_id17")).click();
	}
	
	public String getATN(int r, String name) {
		//Navigate to enrollment worklist panel
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_EnrollmentWorklist")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklist_DropDownQueueType"))).selectByIndex(r);
	    Common.search();
	    //sort by dte received for faster search
	    driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:_id44")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:_id44")).click();
	
		int i=2;
		int j=2;

		if (name.equals(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr/td[5]")).getText()))
			return driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr/td[2]")).getText();
		else {
			for (;i<=j; i++){
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				if ((driver.findElements(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr["+i+"]/td[5]")).size())>0) {
					if (name.equals(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr["+i+"]/td[5]")).getText())) { 
						driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
					}
					else
						j=j+1;
				}
			}
			return driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr["+(i-1)+"]/td[2]")).getText(); //(i-1) because the for loop does an i++ finally
		}		
	}
	
	public String getATNnew(String queue, String name) {
		//Navigate to enrollment worklist panel
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_EnrollmentWorklist")).click();
		List<WebElement> list = new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklist_DropDownQueueType"))).getOptions();
		for (WebElement i:list) 
			if ((i.getText()).contains(queue)) {
				//This is to handle QA and PREAPPROVAL QA
				if (i.getText().contains("PREAPPROVAL QA"))
					continue;
				else {
					i.click();
					break;
				}
			}
	    Common.search();
	    
	    //sort by dte received for faster search
	    driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:_id44")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:_id44")).click();
	    
		int i=1; //item 1 on a page
		int j=2; //page 2
		boolean foundATN = false;
		String atnno="";

		while(true){
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			if ((driver.findElements(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr["+i+"]/td[5]")).size())>0) {
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//				if (name.equals(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr["+i+"]/td[5]")).getText())) { 
				if ((driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr["+i+"]/td[5]")).getText().contains(name))) { 
					atnno=driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults:tbody_element']/tr["+i+"]/td[2]")).getText();
					foundATN=true;
					break;
				}
				else
					i++;
			}
			else {
				if ((driver.findElements(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults_DataScrolleridx"+j)).size())>0) {
					driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
					driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentWorkflowWorklistPanel:ProviderEnrollmentWorkflowWorklistResults_DataScrolleridx"+j)).click();
					i=1;
					j++;
				}
				else {
					driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
					break;
				}
			}
			
		}

		if(!foundATN)
			Assert.assertTrue(false, "ATN not found on any page with name: "+name);
		
		return atnno;

	}
	    
	@Test
	public void test22465() throws Exception {
		TestNGCustom.TCNo="22465";
    	log("//TC 22465");
    	
    	//Get relationship entity
    	sqlStatement = "select * from T_PR_prov  where cde_relationship_entity_type = '0099' and rownum <2";
    	colNames.add("ID_PROVIDER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String id = colValues.get(0);
    	
    	//Search for this rel entity
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelationshipEntity")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntitySearchBean_CriteriaPanel:ProvRelationshipEntitySearchCrit_ID")).sendKeys(id);
		Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntitySearchResults_0:_id17")).click();
		
		//Trading Partner Clients
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityNavigatorPanel:relationshipentitynavigator:ITM_n10610")).click();
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		if (driver.findElements(By.id("MMISForm:MMISBodyContent:ProviderTradingPartnerClientsPanel:ProviderTradingPartnerClientsList_0:ProviderTradingPartnerClientsBean_ColValue_ProviderIDSvcClient")).size()==0) {
			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
			driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTradingPartnerClientsPanel:ProviderRelationshipEntityClientDataPanel_ProviderIDClient")).sendKeys("110000014");
			driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTradingPartnerClientsPanel:ProviderRelationshipEntityClientDataPanel_CdeServiceLocClient")).sendKeys("C");
			driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTradingPartnerClientsPanel:ProviderRelationshipEntityClientDataPanel_EffectiveDte")).sendKeys(Common.convertSysdate());
			driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTradingPartnerClientsPanel:ProviderRelationshipEntityClientDataPanel_EndDte")).sendKeys("12/31/2299");
			Common.saveAll();
		}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		//Sort on end date desc
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTradingPartnerClientsPanel:ProviderTradingPartnerClientsList:ProviderTradingPartnerClientsBean_ColHeader_endDte")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTradingPartnerClientsPanel:ProviderTradingPartnerClientsList:ProviderTradingPartnerClientsBean_ColHeader_endDte")).click();

		//make end date = EoT
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTradingPartnerClientsPanel:ProviderTradingPartnerClientsList_0:ProviderTradingPartnerClientsBean_ColValue_ProviderIDSvcClient")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTradingPartnerClientsPanel:ProviderRelationshipEntityClientDataPanel_EndDte")).clear();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTradingPartnerClientsPanel:ProviderRelationshipEntityClientDataPanel_EndDte")).sendKeys("12/31/2299");
		Common.saveAll();
		
		//End Date the row now
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTradingPartnerClientsPanel:ProviderTradingPartnerClientsList_0:ProviderTradingPartnerClientsBean_ColValue_ProviderIDSvcClient")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTradingPartnerClientsPanel:ProviderRelationshipEntityClientDataPanel_EndDte")).clear();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTradingPartnerClientsPanel:ProviderRelationshipEntityClientDataPanel_EndDte")).sendKeys("12/31/2298");
		Common.saveAll();
		
		log("The relationship entity end dated was: "+id);
    	
	}
	
	@Test
	public void test22466() throws Exception {
		TestNGCustom.TCNo="22466";
    	log("//TC 22466");
    	
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_CaseTracking")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_caseDescription")).sendKeys(Common.generateRandomName());
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_clerkIDRefAgencyFrom")).sendKeys("CST");
		driver.findElement(By.xpath("/html/body/form/table/tbody/tr/td[2]/span/table[2]/tbody/tr/td/table/tbody/tr/td/div[2]/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[5]/td[2]/a/img")).click();
//		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:_id102:ClerkIDSearchCriteriaPanel:UserID")).sendKeys("AGANDHI");
//		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:_id102:ClerkIDSearchCriteriaPanel:SEARCH")).click();
//		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:_id102:ClerkIDSearchResults_0:column1Value")).click();
	    driver.findElement(By.xpath("//input[contains(@id,'ClerkIDSearchCriteriaPanel:UserID')]")).sendKeys("AGANDHI");
	    driver.findElement(By.xpath("//*[contains(@id,'ClerkIDSearchCriteriaPanel:SEARCH')]")).click();
	    driver.findElement(By.xpath("//*[contains(@id,'ClerkIDSearchResults_0:column1Value')]")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_ProvCaseType"))).selectByVisibleText("CST Provider Complaint");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_ProvCaseStatus"))).selectByVisibleText("Active Case Status");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_ProvCaseLevel"))).selectByVisibleText("IN-HOUSE");
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_CaseRefFromDate")).sendKeys(Common.convertSysdate());
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_CaseSecuredIndicator"))).selectByVisibleText("No");
//	    Thread.sleep(15000);
	    Common.saveAll();
	    
	    String caseNo = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_caseNumber")).getText();
	    log("Case Number: "+caseNo);
	    
	    //Add related providers
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingNavigatorPanel:ProviderCaseTrackingNavigator:ITM_n109")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
		
		//Select provider
		sqlStatement="select p.id_provider, h.cde_service_loc, n.name , x.num_tax_id, i.alt_id_provider from t_hist_directory h, t_pr_prov p, t_pr_nam n, t_pr_tax_id x, T_PR_IDENTIFIER i, T_PR_LOC_NM_ADR adr where n.sak_prov = p.sak_prov and x.sak_prov = p.sak_prov and i.sak_prov = p.sak_prov and i.cde_service_loc=h.cde_service_loc and i.cde_pr_id_type='NPI' and h.cde_clm_status = 'P' and h.sak_prov=p.sak_prov and h.dte_first_svc > "+HIPAA.datePaneltoSQL(Common.convertSysdatecustom(-90))+" and adr.SAK_SHORT_NAME = n.SAK_SHORT_NAME and adr.cde_service_loc = h.cde_service_loc and adr.sak_prov = p.sak_prov and rownum < 2";		
		System.out.println(sqlStatement);
		colNames.add("ID_PROVIDER");
		colNames.add("CDE_SERVICE_LOC");
		colNames.add("NAME");
		colNames.add("NUM_TAX_ID");
		colNames.add("ALT_ID_PROVIDER");
		colValues=Common.executeQuery(sqlStatement, colNames);
		String prov= colValues.get(0);
		String sl = colValues.get(1);
		String name = colValues.get(2);
		String tax = colValues.get(3);
		String npi = colValues.get(4);
		log("Prov ID:"+prov+" "+sl+" Prov Name:"+name+" Ta ID:"+tax+" NPI:"+npi);

		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseXrefPanel:ProviderCaseRefDataPanel_ProviderID")).sendKeys(prov);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseXrefPanel:ProviderCaseRefDataPanel_ProvLoc")).sendKeys(sl);
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		Common.saveAll();
		
	    log("Provider Added: "+prov+sl);
		
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseRelatedProvidersPanel:ProviderCase_ProviderCaseXrefList_0:ProviderCaseRefNewBean_ColValue_providerIDLoc")).getText().equals(prov+" "+sl)), "Mismatch on Prov ID/SL");
		Assert.assertTrue(name.contains(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseRelatedProvidersPanel:ProviderCase_ProviderCaseXrefList_0:ProviderCaseRefNewBean_ColValue_firstName")).getText().trim()), "Name Mismatch");
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseRelatedProvidersPanel:ProviderCase_ProviderCaseXrefList_0:ProviderCaseRefNewBean_ColValue_numTaxID")).getText().equals(tax)), "tax ID mismatch");
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseRelatedProvidersPanel:ProviderCase_ProviderCaseXrefList_0:ProviderCaseRefNewBean_ColValue_altIDProvider")).getText().equals(npi)), "npi mismatch");

		//Add related claims
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingNavigatorPanel:ProviderCaseTrackingNavigator:ITM_n108")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseRelatedClaimXrefPanel:ProviderCaseRelatedClaimXrefList_NewButtonClay:ProviderCaseRelatedClaimXrefList_newAction_btn")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Advanced Search']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseRelatedClaimXrefPanel:ClaimSearchPanel:ProviderCaseRelatedClaimSearchBean_CriteriaPanel:FDOS")).sendKeys(Common.convertSysdatecustom(-90));
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseRelatedClaimXrefPanel:ClaimSearchPanel:ProviderCaseRelatedClaimSearchBean_CriteriaPanel:TDOS")).sendKeys(Common.convertSysdate());
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseRelatedClaimXrefPanel:ClaimSearchPanel:ProviderCaseRelatedClaimSearchBean_CriteriaPanel:ClmProviderId")).sendKeys(prov+sl);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseRelatedClaimXrefPanel:ClaimSearchPanel:ProviderCaseRelatedClaimSearchBean_CriteriaPanel:SEARCH")).click();
		
		//Store ICN
		String icn = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseRelatedClaimXrefPanel:ClaimSearchPanel:ClaimSearchResultsDataTable_0:CTClaimSearchCol1Txt")).getText();

		
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseRelatedClaimXrefPanel:ClaimSearchPanel:ClaimSearchResultsDataTable_0:selected")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		Common.saveAll();
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseRelatedClaimXrefPanel:ProviderCaseRelatedClaimXrefList_0:ProviderCaseRelatedClaimXrefBean_ColValue_relatedClaim_historyDirectory_ICN")).getText().equals(icn)), "icn mismatch");
		
	    log("ICN selected: "+icn);

	}
	
	//old code with workflow
//	@Test
//	public void test22467() throws Exception {
//		TestNGCustom.TCNo="22467";
//    	log("//TC 22467");
//    	
//    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Admin")).click();
//    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_imaging")).click();
//    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImagingNavigatorPanel:ImagingMaintenance_id:GRP_provider")).click();
//    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImagingNavigatorPanel:ImagingMaintenance_id:ITM_n109")).click();
//    	
//    	//create WI for image
//    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocument_ReceiptDate")).sendKeys(Common.convertSysdate());
//    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocument_UploadFileName")).sendKeys(System.getProperty("user.dir")+"\\testngMO.xml");
////    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocumentDataPanel_ImageProviderDocumentTaxID")).sendKeys("");
//    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocumentDataPanel_ImageProviderDocumentTaxID")).sendKeys("1");
////    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocumentDataPanel_ImageProviderDocumentTaxID_CMD_SEARCH")).click(); //not working for chrome
//		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocumentDataPanel_ImageProviderDocumentTaxID_CMD_SEARCH']/img")).click();
//
//    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:_id281:ImageProviderDocumentSearchResults_2:column1Value")).click(); //get the 3rd prov from list to avoid getting 110000314, whic is also used for TC 22461
//    	Common.saveAll();
//    	
//    	//check WI and image
//    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Provider")).click();
//	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateWorklist")).click();
//    	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklist_DropDownQueueType"))).selectByIndex(2);
//	    Common.search();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:_id40")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:_id40")).click();
//	    
//	    //Check image
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults_0:_id20")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList:ProviderImagesSearchResultBean_SortColHeader_receiptDate")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList:ProviderImagesSearchResultBean_SortColHeader_receiptDate")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList_0:_id105")).click();
//
//	    //Click on WI
//	    driver.findElement(By.id("workList[0]")).click();
//		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Review']")).click();
//
//		//Change status to IN PROCESS
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderworkflowProfileUpdateStatusPanel:WorkflowStatusdropdown"))).selectByVisibleText("IN PROCESS");
//	    Common.save();
////	    df30872_saveAll(); //remove after DF 20872 is fixed, and uncomment above line of Common.saveAll()
//	    
//	    //Select The WI again from PROFILE UPDATE queue
//	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateWorklist")).click();
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklist_DropDownQueueType"))).selectByIndex(3);
//	    Common.search();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:_id40")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:_id40")).click();
//	    //Click on WI
//	    driver.findElement(By.id("workList[0]")).click();
//		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Review']")).click();
//		//Change status to COMPLETE
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderworkflowProfileUpdateStatusPanel:WorkflowStatusdropdown"))).selectByVisibleText("COMPLETE");
//	    Common.save();
////	    df30872_saveAll(); //remove after DF 20872 is fixed, and uncomment above line of Common.saveAll()
//	    
//	    log("The image name is: "+Common.fileName());
//    	
//	}
	
	@Test
	public void test22467() throws Exception {
		TestNGCustom.TCNo="22467";
    	log("//TC 22467");
    	
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Admin")).click();
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_imaging")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImagingNavigatorPanel:ImagingMaintenance_id:GRP_provider")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImagingNavigatorPanel:ImagingMaintenance_id:ITM_n109")).click();
    	
    	//create WI for image
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocument_ReceiptDate")).sendKeys(Common.convertSysdate());
    	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocumentDataPanel_DocumentType"))).selectByVisibleText("CER - CERTIFICATIONS");
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocument_UploadFileName")).sendKeys(System.getProperty("user.dir")+"\\testngMO.xml");
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocumentDataPanel_ImageProviderDocumentTaxID")).sendKeys("1");
//    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocumentDataPanel_ImageProviderDocumentTaxID_CMD_SEARCH")).click(); //not working for chrome
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocumentDataPanel_ImageProviderDocumentTaxID_CMD_SEARCH']/img")).click();
		
		//Store the Pid we are about to select
		String imgPid = driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:_id280:ImageProviderDocumentSearchResults_2:column1Value")).getText();
		log("Imaging Pid is: "+imgPid);
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:_id280:ImageProviderDocumentSearchResults_2:column1Value")).click(); //get the 3rd prov from list to avoid getting 110000314, whic is also used for TC 22461
    	Common.saveAll();
    	
    	//check WI and image
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Provider")).click();
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateSearch")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateSearchBean_CriteriaPanel:ProfileUpdateSearchPanel_providerId")).sendKeys(imgPid.substring(0, imgPid.length()-1));
	    Common.search();
	    
	    //sort by date received desc
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
		
	    //Check image
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss_0:_id15']")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList:ProviderImagesSearchResultBean_ColHeader_receiptDate")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList:ProviderImagesSearchResultBean_ColHeader_receiptDate")).click();
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList_0:_id80']")).click();

		//select first row checkbox
		driver.findElement(By.xpath("//*[@id='rowCheck[0]']")).click();
		
		//click on View button
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Review']")).click();

		//Change status to IN PROCESS
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderworkflowProfileUpdateStatusPanel:WorkflowStatusdropdown"))).selectByVisibleText("IN PROCESS");
	    Common.save();
//	    df30872_saveAll(); //remove after DF 20872 is fixed, and uncomment above line of Common.saveAll()
	    
	    //Select The WI again from PROFILE UPDATE panel
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateSearch")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateSearchBean_CriteriaPanel:ProfileUpdateSearchPanel_providerId")).sendKeys(imgPid.substring(0, imgPid.length()-1));
//    	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklist_DropDownQueueType"))).selectByIndex(2);
	    Common.search();
	    
	    //sort by date received desc
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
		
		//select first row checkbox
		driver.findElement(By.xpath("//*[@id='rowCheck[0]']")).click();
		
		//click on View button
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Review']")).click();
		
		//Change status to COMPLETE
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderworkflowProfileUpdateStatusPanel:WorkflowStatusdropdown"))).selectByVisibleText("COMPLETE");
	    Common.save();
//	    df30872_saveAll(); //remove after DF 20872 is fixed, and uncomment above line of Common.saveAll()
	    
	    log("The image name is: "+Common.fileName());
    	
	}
	
	@Test
	public void test22469() throws Exception {
		TestNGCustom.TCNo="22469";
    	log("//TC 22469");
    	
		String idProvider="110048577";
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(idProvider);
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	    
	    //Restricted services- Check for existing row, otherwise create new
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10423")).click();
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		if (driver.findElements(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:ProviderRestrictedServiceList_0:ProviderRestrictedServiceBean_ColValue_status")).size()>0) {
			//Sort by end date
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:ProviderRestrictedServiceList:ProviderRestrictedServiceBean_ColHeader_endDate")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:ProviderRestrictedServiceList:ProviderRestrictedServiceBean_ColHeader_endDate")).click();
		    //select the latest record now
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:ProviderRestrictedServiceList_0:ProviderRestrictedServiceBean_ColValue_status")).click();
		}
		else 
			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:ProviderRestrictedServiceDataPanel_Status1Code"))).selectByVisibleText("Active");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:ProviderRestrictedServiceDataPanel_EffectiveDate")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:ProviderRestrictedServiceDataPanel_EffectiveDate")).sendKeys(Common.convertSysdate());
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:ProviderRestrictedServiceDataPanel_EndDate")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:ProviderRestrictedServiceDataPanel_EndDate")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:ProviderRestrictedServiceDataPanel_IncludeExcludeIndicator"))).selectByVisibleText("Include");
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:ProviderRestDataPanel_RestrictionType"))).selectByVisibleText("Proc");
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:BoundDataSearchLowCodePanel_CMD_SEARCH")).click(); //not working for chrome
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:BoundDataSearchLowCodePanel_CMD_SEARCH']/img")).click();
	    driver.findElement(By.xpath("//input[contains(@id,'ProcedureCodePopUpSearchCriteriaPanel:ProcedureCode')]")).sendKeys("99203");
	    driver.findElement(By.xpath("//*[contains(@id,'ProcedureCodePopUpSearchCriteriaPanel:SEARCH')]")).click();
	    driver.findElement(By.xpath("//*[contains(@id,'ProcedureCodePopUpSearchResults_0')]")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:BoundDataSearchHighCodePanel_CMD_SEARCH")).click(); //not working for chrome
	    driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:BoundDataSearchHighCodePanel_CMD_SEARCH']/img")).click();
	    driver.findElement(By.xpath("//input[contains(@id,'ProcedureCodePopUpSearchCriteriaPanel:ProcedureCode')]")).sendKeys("99203");
	    driver.findElement(By.xpath("//*[contains(@id,'ProcedureCodePopUpSearchCriteriaPanel:SEARCH')]")).click();
	    driver.findElement(By.xpath("//*[contains(@id,'ProcedureCodePopUpSearchResults_0')]")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:ProviderEnrollProgramPopUpSearch")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRestrictedServicePanel:ProviderEnrollProgramPopUpSearch")).sendKeys("AOH");
	    Common.saveAll();
	    
	    //Get Claims Data
		sqlStatement="select * from R_CLAIMS_BILLING where TC = '22469'";
		colNames.add("TC"); 
		colNames.add("CT");  
		colNames.add("PROV_BILLING"); 
		colNames.add("AMT_BILLED");   
		colNames.add("TOB"); 
		colNames.add("NPI_ATTEND");   
		colNames.add("REFERRAL"); 
		colNames.add("PAS"); 
	    colValues=Common.executeQuery1(sqlStatement, colNames);
	    
	    String TC = colValues.get(0); 
	    String CT = colValues.get(1);  
	    String PROV_BILLING = colValues.get(2); 
	    String AMT_BILLED = colValues.get(3);   
	    String TOB = colValues.get(4); 
	    String NPI_ATTEND = colValues.get(5);   
	    String REFERRAL = colValues.get(6); 
	    String PAS = colValues.get(7); 
	    
	    String sql = "select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ base.* from t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid," +
				"t_cde_aid aid,t_re_aid_elig elig where " +
				"elig.sak_recip=base.sak_recip " +
				"and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
				"and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
				"and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
				"and pgm. CDE_PGM_HEALTH='STD' " +
				"and elig.DTE_END='22991231' " + 
				"and elig.cde_status1<>'H' " +
				"and not exists ( select sak_recip from t_re_pmp_assign asg where asg.sak_recip=base.sak_recip and asg.dte_end> 20130401) " +
				"and not exists ( select sak_recip from t_tpl_resource rs Where rs.Sak_Recip=Base.Sak_Recip) " +
				"and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip and hib.dte_end> 20130401) " +
				"And Not Exists ( Select Sak_Recip From t_hist_directory hist Where hist.Sak_Recip=Base.Sak_Recip) " +
				"and base.ind_active='Y' "+ 
				"and base.sak_recip > dbms_random.value * 6300000 " +
				"and rownum<2 ";
	  
	    Common.portalLogin();
    	SubmitClaims.initiateClaim();
    	SubmitClaims.M(TC, CT, PROV_BILLING, AMT_BILLED, TOB, NPI_ATTEND, REFERRAL, PAS, sql);
    	String clmNo = SubmitClaims.clmICN("prof");
       	log("ICN: "+clmNo);
       	
       	//Verify 1040/1041 edit
       	Common.portalLogout();
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Claims")).click();
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchBean_CriteriaPanel:ClmIcnNumber")).sendKeys(clmNo);
		Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchResultsDataTable_0:_id14")).click();
		//Error 1040
		driver.findElement(By.id("MMISForm:MMISBodyContent:PhysicianClaimNavigatorPanel:PhysicianClaimNavigator:ITM_PhysicianClaim10")).click();
		int rws=driver.findElements(By.xpath("//span[contains(@id,'ClaimErrorHdrBean_ColValue_errorCode')]")).size();
		boolean errFound = false;
		for (int i=0;i<rws;i++) {
			if (driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimErrorHdrPanel:ClaimErrorHdr_Errorlist_"+i+":ClaimErrorHdrBean_ColValue_errorCode")).getText().equals("1040")) {
				errFound=true;
				break;
			}
		}
		if (!errFound)
			Assert.assertTrue(false, "Claim error code 1040 was not found");
		//Error 1041
		errFound = false;
		for (int i=0;i<rws;i++) {
			if (driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimErrorHdrPanel:ClaimErrorHdr_Errorlist_"+i+":ClaimErrorHdrBean_ColValue_errorCode")).getText().equals("1041")) {
				errFound=true;
				break;
			}
		}
		if (!errFound)
			Assert.assertTrue(false, "Claim error code 1041 was not found");
	
	}
	
	@Test
	public void test22471() throws Exception {
		TestNGCustom.TCNo="22471";
    	log("//TC 22471");
    	
		String idProvider="110026771";
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(idProvider);
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
    	
	    //inpatient LoC
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:GRP_g101")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n1014")).click();
	    //Sort by end date
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateList:ProviderUbLevelOfCareRateBean_ColHeader_endDate")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateList:ProviderUbLevelOfCareRateBean_ColHeader_endDate")).click();
	    //Store existing data
	    //Make sure Total Rate>0
	    int i;
	    for (i=0;i<i+1;i++) {
	    	if (!(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateList_"+i+":ProviderUbLevelOfCareRateBean_ColValue_amount")).getText().equals("$0.00")))
	    		break;  	
	    }
	    
	    String plan = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateList_"+i+":ProviderUbLevelOfCareRateBean_ColValue_inpLoc_levelOfCareDescription")).getText();
	    String rate = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateList_"+i+":ProviderUbLevelOfCareRateBean_ColValue_amount")).getText();
	    String percent = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateList_"+i+":ProviderUbLevelOfCareRateBean_ColValue_prcntOfAmntBilled")).getText();
	    String certDate = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateList_"+i+":ProviderUbLevelOfCareRateBean_ColValue_certifiedDate")).getText();
	    
	    //End date this row
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateList_"+i+":ProviderUbLevelOfCareRateBean_ColValue_inpLoc_levelOfCareDescription")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateDataPanel_EndDate")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateDataPanel_EndDate")).sendKeys(Common.convertSysdate());
	    Common.saveAll();
	    
	    //Enter new one
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateDataPanel_InpLoc"))).selectByVisibleText(plan);
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateDataPanel_EffectiveDate")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateDataPanel_EffectiveDate")).sendKeys(Common.convertSysdatecustom(1));
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateDataPanel_EndDate")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateDataPanel_EndDate")).sendKeys("12/31/2299");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateDataPanel_Amount")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateDataPanel_Amount")).sendKeys(rate);
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateDataPanel_PrcntOfAmntBilled")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateDataPanel_PrcntOfAmntBilled")).sendKeys(percent);
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateDataPanel_CertifiedDate")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateDataPanel_CertifiedDate")).sendKeys(certDate);
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderUbLevelOfCareRatePanel:ProviderUbLevelOfCareRateList_0:ProviderUbLevelOfCareRateBean_ColValue_status")).getText().equals("Added")), "New row was not added");
		Common.saveAll();

	}
	
	@Test
	public void test23097() throws Exception {
		TestNGCustom.TCNo="23097";
    	log("//TC 23097");
    	
    	//get ATN
    	sqlStatement="select sak_atn from T_PR_APPLN where cde_prov_type =17 and rownum<2";
    	colNames.add("SAK_ATN");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String atn = colValues.get(0);
    	log("Nurse practitioner ATN is: "+atn);
    	
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(atn);
    	Common.search();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchResults_0:EnrollmentSearchPanelSpacer24")).click();
    	//Provider enrollment requirements
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ApplicationNavigatorPanel:providerapplicationnavigator:ITM_n111")).click();
    	//Sort asc on requirement description
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderEnrollmentRequirementProviderTypeXrefPanel:ProviderEnrollmentRequirementProviderTypeXrefPanelList:ProviderEnrollmentRequirementProviderTypeXrefBean_ColHeader_providerEnrollmentRequirement_providerRequirementType_requirementTypeDescription")).click();
		
    	int rws=driver.findElements(By.xpath("//span[contains(@id,'ProviderEnrollmentRequirementProviderTypeXrefBean_ColValue_providerEnrollmentRequirement_providerRequirementType_requirementTypeDescription')]")).size();
		boolean clear = false;
		for (int i=0;i<rws;i++) {
			if (driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderEnrollmentRequirementProviderTypeXrefPanel:ProviderEnrollmentRequirementProviderTypeXrefPanelList_"+i+":ProviderEnrollmentRequirementProviderTypeXrefBean_ColValue_providerEnrollmentRequirement_providerRequirementType_requirementTypeDescription")).getText().equals("DEA")) {
				clear=true;
				break;
			}
		}
		if (!clear)
			Assert.assertTrue(false, "DEA requirement was not found for nurse practitioner ATN");    	
		
	}
	
	@Test
	public void test23099a() throws Exception {
		TestNGCustom.TCNo="23099a";
    	log("//TC 23099a");
    	
    	//Get active prov whose last name is smith
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderSearchResult_Name")).sendKeys("SMITH");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
	    Common.search();
	    //Store this provider id
    	provTC23099=driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).getText();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	    
	    //Location name address
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10414")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressList_0:ProviderLocationNameAddressBean_ColValue_prAddrCode_addressUsageDescription")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressPanel_maintainAddressAction_btn")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:address1ID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:address1ID")).sendKeys("101 FEDERAL ST");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:cityID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:cityID")).sendKeys("BOSTON");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:zipID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:zipID")).sendKeys("02110");
	    
	    //Store current coordinates
	    lon = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:longitudeNumber")).getAttribute("value");
	    lat = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:latitudeNumber")).getAttribute("value");
	    
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Update']")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).click();
		Common.saveAll();
		
		log("Provider: "+provTC23099);
		log("For address type 'Billing', Old longitude: "+lon);
		log("For address type 'Billing', Old latitude: "+lat);
    	
	}
	
	@Test
	public void test23099b() throws Exception {
		TestNGCustom.TCNo="23099b";
    	log("//TC 23099b");
//    	provTC23099="110000298";
//    	lat = "900578.5179";
//    	lon = "236537.8776";
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(provTC23099);
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	    
	    //Location name address
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10414")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressList_0:ProviderLocationNameAddressBean_ColValue_prAddrCode_addressUsageDescription")).click();
    	
    	if (driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:longitudeNumber")).getAttribute("value").equals(lon))
    		Assert.assertTrue(false, "Longitude did not change");
    	if (driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:latitudeNumber")).getAttribute("value").equals(lat))
    		Assert.assertTrue(false, "Latitude did not change");
    	
		log("New longitude: "+driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:longitudeNumber")).getAttribute("value"));
		log("New latitude: "+driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:latitudeNumber")).getAttribute("value"));
		
		//Change the address back to prev one for next time
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressPanel_maintainAddressAction_btn")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:address1ID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:address1ID")).sendKeys("1054 KINGS HWY");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:cityID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:cityID")).sendKeys("NEW BEDFORD");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:zipID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:zipID")).sendKeys("02745");
	    
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Update']")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).click();
		Common.saveAll();
    	
	}
	
	@Test
	public void test23101a() throws Exception {
		TestNGCustom.TCNo="23101a";
    	log("//TC 23101a");
    	
		String idProvider="110000148";
		searchProv(idProvider);
	    //SERVICE LOCATION - set disbursement codes and billing indicator
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n104")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderServiceLocationPanel:ProviderServiceLocationDataPanel_DisbursementCode"))).selectByVisibleText("0 Pay");
	    new Select(driver.findElement(By.xpath("//select[contains(@id,'ProviderServiceLocationDataPanel_BillerIndicator')]"))).selectByVisibleText("Yes");
	    Common.saveAll();
	    
		idProvider="110000254";
		searchProv(idProvider);
	    //SERVICE LOCATION
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n104")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderServiceLocationPanel:ProviderServiceLocationDataPanel_DisbursementCode"))).selectByVisibleText("1 State Agency");
	    new Select(driver.findElement(By.xpath("//select[contains(@id,'ProviderServiceLocationDataPanel_BillerIndicator')]"))).selectByVisibleText("Yes");
	    Common.saveAll();

	    String[] part = new String[2];
	    part[0]="a";
	    part[1]="b";
       	
	    Common.portalLogin();
	    for (int i=0;i<2;i++) {
	    	//Get Claims Data
			sqlStatement="select * from R_CLAIMS_BILLING where TC = '23101"+part[i]+"'";
			colNames.add("TC"); 
			colNames.add("CT");  
			colNames.add("PROV_BILLING"); 
			colNames.add("AMT_BILLED");   
			colNames.add("TOB"); 
			colNames.add("NPI_ATTEND");   
			colNames.add("REFERRAL"); 
			colNames.add("PAS"); 
		    colValues=Common.executeQuery1(sqlStatement, colNames);
		    
		    String TC = colValues.get(0); 
		    String CT = colValues.get(1);  
		    String PROV_BILLING = colValues.get(2); 
		    String AMT_BILLED = colValues.get(3);   
		    String TOB = colValues.get(4); 
		    String NPI_ATTEND = colValues.get(5);   
		    String REFERRAL = colValues.get(6); 
		    String PAS = colValues.get(7); 
		    
		    String sql = "select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ base.* from t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid," +
					"t_cde_aid aid,t_re_aid_elig elig where " +
					"elig.sak_recip=base.sak_recip " +
					"and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
					"and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
					"and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
					"and pgm. CDE_PGM_HEALTH='STD' " +
					"and elig.DTE_END='22991231' " + 
					"and elig.cde_status1<>'H' " +
					"and not exists ( select sak_recip from t_re_pmp_assign asg where asg.sak_recip=base.sak_recip and asg.dte_end> 20130401) " +
					"and not exists ( select sak_recip from t_tpl_resource rs Where rs.Sak_Recip=Base.Sak_Recip) " +
					"and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip and hib.dte_end> 20130401) " +
					"And Not Exists ( Select Sak_Recip From t_hist_directory hist Where hist.Sak_Recip=Base.Sak_Recip) " +
					"and base.ind_active='Y' "+ 
					"and base.sak_recip > dbms_random.value * 6300000 " +
					"and rownum<2 ";
		  
		    
	    	SubmitClaims.initiateClaim();
	    	SubmitClaims.M(TC, CT, PROV_BILLING, AMT_BILLED, TOB, NPI_ATTEND, REFERRAL, PAS, sql);
	    	String clmNo = SubmitClaims.clmICN("prof");
	    	if (!(SubmitClaims.clmStatus().equals("Paid")))
	    		Assert.assertTrue(false, "The ICN "+clmNo+" is denied");
	       	log("ICN for prov ("+PROV_BILLING+") with disbursement code "+i+" is:"+clmNo);
	       	
	       	//Store ICN in DB for day 2
	    	String SelSql="select * from r_day2 where TC = '23101"+part[i]+"'";
	    	String col="ID";
	    	String DelSql="delete from r_day2 where TC = '23101"+part[i]+"'";
	    	String InsSql="insert into r_day2 values ('23101"+part[i]+"', '"+clmNo+"', 'ICN for disb code "+i+"', '"+Common.convertSysdate()+"')";
	    	Common.insertData(SelSql, col, DelSql, InsSql);
	    	Common.resetPortal();
	    }
	    
	    Common.portalLogout();
	}
	
	@Test
	public void test23101b() throws Exception {
		TestNGCustom.TCNo="23101b";
    	log("//TC 23101b");
    	
	    String[] part = new String[2];
	    part[0]="a";
	    part[1]="b";
	    
	    String[] prov = new String[2];
	    prov[0]="110000148";
	    prov[1]="110000254";
       		    
	    for (int i=0;i<2;i++) {
    	
		String idProvider=prov[i];
		
		sqlStatement ="select * from r_day2 where TC = '23101"+part[i]+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no icn present generated");
		String icn = colValues.get(0);
		
		log("Checking Finance subsystem for voucher no. for provider "+idProvider+" for icn "+icn);
		
		//Go to Finance>Payment
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Financial")).click();
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Payment")).click();
	    driver.findElement(By.xpath("//input[contains(@id,'ProviderID')]")).sendKeys(idProvider);
	    driver.findElement(By.xpath("//input[contains(@id,'checkProviderSearchControlID')]")).sendKeys("A");
	    Common.search();
	    
	    //Get Finance run date stamp from user
//    	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//    	String resp = reader.readLine(); 
//	    System.out.println(resp);
	    
	    String finDate = "10/13/2015";
	    
	    //sort by dte received for faster search
	    driver.findElement(By.id("MMISForm:MMISBodyContent:CheckSearchResults:CheckSearchResultsCol3Title")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:CheckSearchResults:CheckSearchResultsCol6Title")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:CheckSearchResults:CheckSearchResultsCol6Title")).click();

		int a=0; //item 1 on a page
		int j=2; //page 2
		boolean foundFinDt = false;

		while(true){
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			if ((driver.findElements(By.xpath("//*[contains(@id,'"+a+":CheckSearchResultsCol6Value')]")).size())>0) {
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				if (finDate.equals(driver.findElement(By.xpath("//*[contains(@id,'"+a+":CheckSearchResultsCol6Value')]")).getText())) {
					//Validate voucher no.
					if (idProvider.equals(prov[0])) {
						Assert.assertTrue(driver.findElement(By.xpath("//*[contains(@id,'"+a+":CheckSearchResultsCol4Value')]")).getText().substring(0, 1).equals("1"), "Voucher no. for "+idProvider+" did not start with 1");
						log("Voucher no. for "+idProvider+" starts with 1");
					}
					else {
						Assert.assertTrue(driver.findElement(By.xpath("//*[contains(@id,'"+a+":CheckSearchResultsCol4Value')]")).getText().substring(0, 1).equals("9"), "Voucher no. for "+idProvider+" did not start with 9");
						log("Voucher no. for "+idProvider+" starts with 9");
					}
					driver.findElement(By.xpath("//*[contains(@id,'"+a+":CheckSearchResultsCol1Value')]")).click();
					foundFinDt=true;
					break;
				}
				else
					a++;
			}
			else {
				if ((driver.findElements(By.id("MMISForm:MMISBodyContent:_id32idx"+j)).size())>0) {
					driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
					driver.findElement(By.id("MMISForm:MMISBodyContent:_id32idx"+j)).click();
					a=0;
					j++;
				}
				else {
					driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
					break;
				}
			}
			
		}

		if(!foundFinDt)
			Assert.assertTrue(false, "Fin Date "+finDate+" not found on any page.");

		//Validate ICN
		driver.findElement(By.linkText("Related Transactions")).click();
		
		a=0;
		boolean foundICN = false;
		while(true){
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			if ((driver.findElements(By.xpath("//*[contains(@id,'"+a+":_id17')]")).size())>0) {
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				if (icn.equals(driver.findElement(By.xpath("//*[contains(@id,'"+a+":_id17')]")).getText())) { 
					log("Found "+icn+" in Finance>Payment>Related Transactions panel for provider "+idProvider);
					foundICN=true;
					break;
				}
				else
					a++;
			}
			else{
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				break;
			}
			
		}
		
		if(!foundICN)
			Assert.assertTrue(false, "Did not find ICN "+icn+" in Finance>Payment>Related Transactions panel for provider "+idProvider);
		
		LoginCheck();

	    }
	    
	}
	
	@Test
	public void test23102() throws Exception {
		TestNGCustom.TCNo="23102";
    	log("//TC 23102");
    	
    	//get Prov
    	sqlStatement="Select p.Id_Provider From T_Pr_Prov P, T_Pr_Svc_Loc S, T_PR_SPEC cs Where P.Sak_Prov = S.Sak_Prov And S.Cde_Service_Loc = 'A' and p.sak_prov = cs.sak_prov and cs.dte_end = '22991231' and exists (select c.sak_prov from T_PR_SPEC c where c.sak_prov = p.sak_prov group by c.sak_prov having count(c.sak_prov)=1) and rownum<2";
    	colNames.add("ID_PROVIDER");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String certProv = colValues.get(0);
    	log("Cert Spec Prov is: "+certProv);
		searchProv(certProv);
    	
	    //Type and cert spec
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10430")).click();
	    //Sort by end date
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypePanel:ProviderSpecialtyPanel:ProviderSpecialtyList:ProviderSpecialtyBean_ColHeader_endDate")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypePanel:ProviderSpecialtyPanel:ProviderSpecialtyList:ProviderSpecialtyBean_ColHeader_endDate")).click();
	    //Make sure cert spec has end date=EOT
	    if (!(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypePanel:ProviderSpecialtyPanel:ProviderSpecialtyList_0:ProviderSpecialtyBean_ColValue_endDate")).getText().equals("12/31/2299")))
	    	throw new SkipException("Skipping this TC because cert spec has end date!=EOT, so nothing to end date");

	    //End date this row
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypePanel:ProviderSpecialtyPanel:ProviderSpecialtyList_0:ProviderSpecialtyBean_ColValue_status")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypePanel:ProviderSpecialtyPanel:ProviderSpecialtyDataPanel_EndDate")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypePanel:ProviderSpecialtyPanel:ProviderSpecialtyDataPanel_EndDate")).sendKeys(Common.convertSysdate());
	    Common.SaveWarnings();
	    
	    //Enter new one
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypePanel:ProviderSpecialtyPanel:ProviderSpecialtySearchControl_CMD_SEARCH")).click(); //Not working for chrome
	    driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderTypePanel:ProviderSpecialtyPanel:ProviderSpecialtySearchControl_CMD_SEARCH']/img")).click();
	    driver.findElement(By.xpath("//*[contains(@id,'TypeSpecialitiySearchResults_0')]")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypePanel:ProviderSpecialtyPanel:ProviderSpecialtyDataPanel_EffectiveDate")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypePanel:ProviderSpecialtyPanel:ProviderSpecialtyDataPanel_EffectiveDate")).sendKeys(Common.convertSysdatecustom(1));
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypePanel:ProviderSpecialtyPanel:ProviderSpecialtyDataPanel_EndDate")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypePanel:ProviderSpecialtyPanel:ProviderSpecialtyDataPanel_EndDate")).sendKeys("12/31/2299");
	    //Make it primary
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypePanel:ProviderSpecialtyPanel:ProviderSpecialtyPrimaryCheckBox")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		Common.SaveWarnings();

	}
	
	@Test
	public void test23103() throws Exception {
		TestNGCustom.TCNo="23103";
    	log("//TC 23103");
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
		//Codes-Affiliation type
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:ITM_n115")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAffiliationTypePanel:ProviderAffiliationTypeDataPanel_AffiliationTypeCode")).sendKeys("ZZ");
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAffiliationTypePanel:ProviderAffiliationTypeDataPanel_AffiliationTypeDescription")).sendKeys("AUTOMATION REGRESSION");
		Common.saveAll();
		Common.cancelAll();
		//Other-Other relationship
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:ITM_n129")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();

		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderOtherRelationshipPanel:ProviderOtherRelationshipDataPanel_OtherRelationshipName")).sendKeys("TPL REGRESSION TESTING");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderOtherRelationshipPanel:ProviderOtherRelationshipDataPanel_ProviderOtherRelType"))).selectByVisibleText("AUTOMATION REGRESSION");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderOtherRelationshipPanel:ProviderOtherRelationshipDataPanel_ContactFirstName")).sendKeys("TPL");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderOtherRelationshipPanel:ProviderOtherRelationshipDataPanel_ContactLastName")).sendKeys("REGRESSION");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderOtherRelationshipPanel:ProviderOtherRelationshipDataPanel_Street1")).sendKeys("101 FEDERAL ST");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderOtherRelationshipPanel:ProviderOtherRelationshipDataPanel_City")).sendKeys("BOSTON");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderOtherRelationshipPanel:ProviderOtherRelationshipDataPanel_State"))).selectByVisibleText("MA");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderOtherRelationshipPanel:ProviderOtherRelationshipDataPanel_Zip")).sendKeys("02110");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderOtherRelationshipPanel:ProviderOtherRelationshipDataPanel_EmailAddress")).sendKeys("ansh90@gmail.com");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderOtherRelationshipPanel:ProviderOtherRelationshipDataPanel_PhoneNumber")).sendKeys("8190199918");
	    Common.saveAll();
	    
	    //Get a provider
	    String affProv = "110003665";
	    log("The prov to which aff is added is: "+affProv);
	    
	    searchProv(affProv);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n100438")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
//		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAffiliationsPanel:OtherRelationshipSearchControl_CMD_SEARCH")).click(); //Not working for chrome
	    driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderAffiliationsPanel:OtherRelationshipSearchControl_CMD_SEARCH']/img")).click();

	    new Select(driver.findElement(By.xpath("//select[contains(@id,'ProviderOtherRelationshipPopUpSearchCriteriaPanel:OtherRelationshipType')]"))).selectByVisibleText("AUTOMATION REGRESSION");
	    driver.findElement(By.xpath("//*[contains(@id,'ProviderOtherRelationshipPopUpSearchCriteriaPanel:SEARCH')]")).click();
	    driver.findElement(By.xpath("//*[contains(@id,'ProviderOtherRelationshipPopUpSearchResults_0')]")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAffiliationsPanel:ProviderAffiliationsDataPanel_ProviderAffiliationType"))).selectByVisibleText("AUTOMATION REGRESSION");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		Common.saveAll();
		
		//Delete this record
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAffiliationsPanel:ProviderAffiliationsList_0:ProviderAffiliationsBean_ColValue_status")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Delete']")).click();
		//Declare Alert to handle Popup
		alert = driver.switchTo().alert();
		System.out.println(alert.getText());
		alert.accept();
		Common.saveAll();
		//Delete relationship
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
		//Other-Other relationship
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:ITM_n129")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderOtherRelationshipPanel:ProviderOtherRelationshipBean_CriteriaPanel:ProviderOtherRelationshipDataPanel_ProviderAffiliationTypeList"))).selectByVisibleText("AUTOMATION REGRESSION");
	    Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderOtherRelationshipPanel:ProviderOtherRelationshipSearchResults_0:ProviderOtherRelationshipBean_ColValue_status")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Delete']")).click();
		//Declare Alert to handle Popup
		alert = driver.switchTo().alert();
		System.out.println(alert.getText());
		alert.accept();
		Assert.assertTrue(driver.findElement(By.cssSelector("td.message-text")).getText().equals("Save Successful."));
		Common.cancelAll();
		//Delete Affiliation Type
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:GRP_RelatedDataCodes")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:ITM_n115")).click();
		//Sort desc on Affiliation Type (already sorted asc, so click elsewhere sort first(description column here))
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAffiliationTypePanel:ProviderAffiliationTypeList:ProviderAffiliationTypeBean_ColHeader_affiliationTypeDescription")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAffiliationTypePanel:ProviderAffiliationTypeList:ProviderAffiliationTypeBean_ColHeader_affiliationTypeCode")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAffiliationTypePanel:ProviderAffiliationTypeList:ProviderAffiliationTypeBean_ColHeader_affiliationTypeCode")).click();
		//Verify latest record (on top) is ZZ
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAffiliationTypePanel:ProviderAffiliationTypeList_0:ProviderAffiliationTypeBean_ColValue_affiliationTypeCode")).getText().equals("ZZ")), "The record you want to delete is not ZZ, ie not the one you entered for this test. Skipping deletion");
		//Select latest record (on top)
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAffiliationTypePanel:ProviderAffiliationTypeList_0:ProviderAffiliationTypeBean_ColValue_affiliationTypeCode")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Delete']")).click();
		//Declare Alert to handle Popup
		alert = driver.switchTo().alert();
		System.out.println(alert.getText());
		alert.accept();
		Assert.assertTrue(driver.findElement(By.cssSelector("td.message-text")).getText().equals("Save Successful."));
		
	}
	
	@Test
	public void test23105a() throws Exception {
		TestNGCustom.TCNo="23105a";
    	log("//TC 23105a");
    	
    	sqlStatement = "Select B.Id_Provider From T_Pr_Php_Elig A, T_Pr_Prov B Where A.Sak_Prov_Pgm = 36 And A.Dte_End = 22991231 And A.Dte_Inactive = 22991231 And A.Cde_Enroll_Status = '20' And A.Sak_Prov = B.Sak_Prov and exists (select c.sak_prov from T_Pr_Php_Elig c where c.sak_prov = b.sak_prov group by (c.sak_prov) having count(c.sak_prov) = 1) and rownum < 2";
    	colNames.add("ID_PROVIDER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String id = colValues.get(0);
    	log("Prov is: "+id);
    	
    	searchProv(id);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10422")).click();
		if (!(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityList_0:ProviderPublicHealthProgramEligibilityBean_ColValue_endDate")).getText().equals("12/31/2299") && driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityList_0:ProviderPublicHealthProgramEligibilityBean_ColValue_prEnrollStatus_enrollStatusDescriptionStatus")).getText().equals("ACTIVE - Pay")))
			throw new SkipException("Skipping this test as test provider's program elig end date is not EOT, or his elig is not ACTIVE-Pay");
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityList_0:ProviderPublicHealthProgramEligibilityBean_ColValue_prEnrollPgm_providerProgramDescription")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_PrEnrollStatus"))).selectByVisibleText("VOLUNTARY SUSPENSION PROVIDER WITHDRAWAL-NO 30 DAY NOTICE - No Pay");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_EndDate")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_EndDate")).sendKeys(Common.convertSysdatecustom(-1));
	    Common.saveAll();
	    
	    //Store provider in the DB
    	String SelSql="select * from r_day2 where TC = '23105'";
    	String col="ID";
    	String DelSql="delete from r_day2 where TC = '23105'";
    	String InsSql="insert into r_day2 values ('23105', '"+id+"', 'Vol Susp Prov', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
	}
	
	@Test
	public void test23105b() throws Exception {
		TestNGCustom.TCNo="23105b";
    	log("//TC 23105b");
    	
    	//Get data from tables that needs to be verified in the report
		sqlStatement ="select * from r_day2 where TC = '23105'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no Prov ID generated");
    	
		String volProv = colValues.get(0);
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRptsAndLettersNavigatorPanel:ProviderReportsNavigator:GRP_g2")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRptsAndLettersNavigatorPanel:ProviderReportsNavigator:ITM_n107")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_ProviderId")).sendKeys(volProv);
		Common.search();
		//Sort on generate date desc
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:_id55")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:_id55")).click();
		//Verify letter name ang req date
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id48")).getText().equals("PRV-9053-R")), "PRV-9053-R Letter not found");
		Assert.assertTrue((driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:tbody_element']/tr/td[2]")).getText().equals(Common.convertSysdatecustom(-1))), "request date was not from this reg cycle(yest)");
		//Generate letter
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id48")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Generate and Print']")).click();
		
	}
	
	@Test
	public void test23107() throws Exception {
		TestNGCustom.TCNo="23107";
    	log("//TC 23107");
    	
    	//get grpMemProv
    	sqlStatement="select id_provider from T_PR_prov a, T_PR_TYPE b where a.sak_prov=b.sak_prov and b.cde_service_loc  = 'A' and b.CDE_PROV_TYPE='01' and a.sak_prov not in (Select sak_prov From T_Pr_Grp_Mbr) and a.sak_prov not in (Select sak_prov_group From T_Pr_Grp_Mbr) and rownum<2";
    	colNames.add("ID_PROVIDER");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String grpMemProv = colValues.get(0);
    	log("Group member Prov is: "+grpMemProv);
    	
    	//get grpProv
    	sqlStatement="select id_provider from T_PR_prov a, T_PR_TYPE b  where a.sak_prov=b.sak_prov and b.cde_service_loc  = 'A' and b.CDE_PROV_TYPE='97' and a.sak_prov not in (Select sak_prov From T_Pr_Grp_Mbr) and a.sak_prov not in (Select sak_prov_group From T_Pr_Grp_Mbr) and rownum<2";
    	colNames.add("ID_PROVIDER");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String grpProv = colValues.get(0);
    	log("Group Prov is: "+grpProv);
    	
    	//Create a record
    	searchProv(grpMemProv);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10434")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvidergroupMemberPanel:ProviderGroupMemberDataPanel_ProviderID")).sendKeys(grpProv);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvidergroupMemberPanel:ProviderGroupMemberDataPanel_ProvLoc")).sendKeys("A");
		Common.saveAll();
		//End date this record
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvidergroupMemberPanel:ProvidergroupMemberList_0:ProviderGroupBean_ColValue_providerId")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvidergroupMemberPanel:ProviderGroupDataPanel_EndDate")).clear();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvidergroupMemberPanel:ProviderGroupDataPanel_EndDate")).sendKeys(Common.convertSysdate());
		Common.save();
		
		//verify record is end dated in grp prov
    	searchProv(grpProv);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10411")).click();
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProvidergroupPanel:ProvidergroupList_0:ProviderGroupMemberBean_ColValue_providerId")).getText().equals(grpMemProv)), "Group member Prov mismatch");
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProvidergroupPanel:ProvidergroupList_0:ProviderGroupMemberBean_ColValue_effectiveDate")).getText().equals("01/01/1900")), "Begin date mismatch");
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProvidergroupPanel:ProvidergroupList_0:ProviderGroupMemberBean_ColValue_endDate")).getText().equals(Common.convertSysdate())), "End date mismatch");
    	
	}
	
	@Test
	public void test23110a() throws Exception {
		TestNGCustom.TCNo="23110a";
    	log("//TC 23110a");
    	
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRptsAndLettersNavigatorPanel:ProviderReportsNavigator:ITM_n101")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();

		driver.findElement(By.id("MMISForm:MMISBodyContent:LabelReportPanel:TypeFromSearch")).sendKeys("24");
		driver.findElement(By.id("MMISForm:MMISBodyContent:LabelReportPanel:TypeToSearch")).sendKeys("24");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:LabelReportPanel:LabelReportDataPanel_StatusIndicator"))).selectByVisibleText("ACTIVE");
	    Common.save();
    	
	}
	
	@Test
	public void test23110b() throws Exception {
		TestNGCustom.TCNo="23110b";
    	log("//TC 23110b");
    	
    	//Verify unix report
		String command, error;

		//Get Desired Filename
//		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/prd01001.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/prd01001.rpt.* | tail -1"; //grabs the latest gdg of the file

		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
//		fileName = fileName.substring(fileName.length()-40);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log("The Provider Label Print Request Summary Information Report filename is: "+fileName);

		//Verify data in file
		command = "grep 24 "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep ACTIVE "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
    	
	}
	
	@Test
	public void test23113() throws Exception {
		TestNGCustom.TCNo="23113";
    	log("//TC 23113");
    	
    	//get npiProv
    	sqlStatement="select id_provider from T_PR_prov where sak_prov not in (Select sak_prov From T_PR_IDENTIFIER where CDE_PR_ID_TYPE = 'NPI') and rownum<2";
    	colNames.add("ID_PROVIDER");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String npiProv = colValues.get(0);
    	log("npi Prov is: "+npiProv);
    	
    	searchProv(npiProv);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n100436")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAlternateIDsPanel:ProviderIdentifierDataPanel_AltIdProvider")).sendKeys(Common.generateRandomTaxID()+"1");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAlternateIDsPanel:ProviderAlternateIDscodeProviderIdType"))).selectByVisibleText("National Provider ID");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAlternateIDsPanel:NPIVerfiedDropDown"))).selectByVisibleText("Yes");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAlternateIDsPanel:ProviderIdentifierDataPanel_DateProviderIdEffective")).sendKeys(Common.convertSysdate());
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAlternateIDsPanel:ProviderIdentifierDataPanel_DateProviderIdEnd")).sendKeys("12/31/2299");
	    driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
	    Common.save();
    	
	}
	
	@Test
	public void test23115a() throws Exception {
		TestNGCustom.TCNo="23115a";
    	log("//TC 23115a");
    	
    	//get adrProv- Note -this sql has info to keep off any license numbers containing a -, because that has problem in saving on panel. Also searching a lic tht contains 1, as it has less possibility of special characters
    	sqlStatement="select c.id_provider from T_PR_LOC_NM_ADR a, t_pr_adr b, t_pr_prov c where a.sak_prov=b.sak_prov and a.sak_prov = c.sak_prov and a.sak_short_address = b.sak_short_address and a.cde_service_loc = 'A' and a.ind_addr_type='P' and b.ind_undeliverable = 'N' and c.sak_prov not in (select d.sak_prov from T_PR_TAX_ID d , T_Pr_Hb_Lic e where d.num_tax_id=e.num_ssn and e.num_prov_lic like '%-%') and c.sak_prov in (select f.sak_prov from T_PR_TAX_ID f , T_Pr_Hb_Lic g where f.num_tax_id=g.num_ssn and g.num_prov_lic like '%1%') and a.sak_prov not in (3665) and  rownum < 2";
    	colNames.add("ID_PROVIDER");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String adrProv = colValues.get(0);
    	log("Undeliverable address Prov is: "+adrProv);
    	
    	searchProv(adrProv);
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10414")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressList_1:ProviderLocationNameAddressBean_ColValue_prAddrCode_addressUsageDescription")).click();
	    driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Maintain Address']")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressDataPanel_UnDeliverableIndicator"))).selectByVisibleText("Yes");
	    driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Update']")).click();
	    driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).click();
	    Common.save();
	    
	    //Get the no. of days from today when the weekly will run
	    int jobday=0;
	    Date sysdate=new Date();
	    int today=sysdate.getDay();
	    if (today==0)
	    	jobday=3;
	    if (today==1)
	    	jobday=2;
	    if (today==2)
	    	jobday=1;
	    if (today==4)
	    	jobday=6;
	    if (today==5)
	    	jobday=5;
	    if (today==6)
	    	jobday=4;
	    
	    
	    //Store provider in the DB
    	String SelSql="select * from r_day2 where TC = '23115'";
    	String col="ID";
    	String DelSql="delete from r_day2 where TC = '23115'";
    	String InsSql="insert into r_day2 values ('23115', '"+adrProv+"', 'Addr undeliverable Prov', '"+Common.convertSysdatecustom(jobday)+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
	    
	}
	
	@Test
	public void test23115b() throws Exception {
		TestNGCustom.TCNo="23115b";
    	log("//TC 23115b");
    	
	 	//get the prov that was submitted for undeliverable address
		sqlStatement = "Select * From R_Day2 Where Tc = '23115'";
		colNames.add("ID");
		colNames.add("DATE_REQUESTED");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no prov submitted for undeliverable address");

		String adrProv = colValues.get(0);
    	String rundate = colValues.get(1);
    	log("Undeliverable address Prov is: "+adrProv);
    	
    	//Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/prw01801.rpt.* | grep '"+Common.monthUNIX(rundate)+" "+Common.dayUNIX(rundate)+"'";
		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
//		fileName = fileName.substring(fileName.length()-40);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The Provider undeliverable address Report filename is: "+fileName);

		//Verify data in file
		command = "grep "+adrProv+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired undeliverable address prov";
		log(Common.connectUNIX(command, error));
	}
	
	@Test
	public void test23116() throws Exception {
		TestNGCustom.TCNo="23116";
    	log("//TC 23116");
    	
    	//get claim data
    	sqlStatement="select * from R_CLAIMS_BILLING where TC = '23116'";
    	colNames.add("TC");
    	colNames.add("CT");
    	colNames.add("PROV_BILLING");
    	colNames.add("AMT_BILLED");
    	colNames.add("TOB");
    	colNames.add("NPI_ATTEND");
    	colNames.add("REFERRAL");
    	colNames.add("PAS");
    	colValues = Common.executeQuery1(sqlStatement, colNames);
    	String testCase, CT, provider, Amount, TOB, NPI, referral, pas;
    	testCase = colValues.get(0);
    	CT = colValues.get(1);
    	provider = colValues.get(2);
    	Amount = colValues.get(3);
    	TOB = colValues.get(4);
    	NPI = colValues.get(5);
    	referral = colValues.get(6);
    	pas = colValues.get(7);
    	

    	//member sql for claim
    	String	sql = "select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ base.* from t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid," +
    				"t_cde_aid aid,t_re_aid_elig elig where " +
    				"elig.sak_recip=base.sak_recip " +
    				"and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
    				"and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
    				"and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
    				"and pgm. CDE_PGM_HEALTH='STD' " +
    				"and elig.DTE_END='22991231' " + 
    				"and elig.cde_status1<>'H' " +
    				"and not exists ( select sak_recip from t_re_pmp_assign asg where asg.sak_recip=base.sak_recip and asg.dte_end> 20130401) " +
    				"and not exists ( select sak_recip from t_tpl_resource rs Where rs.Sak_Recip=Base.Sak_Recip) " +
    				"and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip and hib.dte_end> 20130401) " +
    				"And Not Exists ( Select Sak_Recip From t_hist_directory hist Where hist.Sak_Recip=Base.Sak_Recip) " +
    				"and base.ind_active='Y' "+ 
    				"and base.sak_recip > dbms_random.value * 6300000 " +
    				"and rownum<2 ";

    	Common.portalLogin();
    	SubmitClaims.initiateClaim();
    	SubmitClaims.O(testCase, CT, provider, Amount, TOB, NPI, referral, pas, sql);
    	String clmNo = SubmitClaims.clmICN("inst");
       	log("ICN: "+clmNo);
    	
		//Verify claims subsystem
       	Common.portalLogout();
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Claims")).click();
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchBean_CriteriaPanel:ClmIcnNumber")).sendKeys(clmNo);
		Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchResultsDataTable_0:_id14")).click();
		Assert.assertTrue((driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:UB92ClaimHeaderPanel:UB92ClmBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[15]/td[2]")).getText().contains(provider.substring(0, 9))), "The prov was not found");
		Assert.assertTrue((driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:UB92ClaimHeaderPanel:UB92ClmBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[15]/td[2]")).getText().contains("B")), "The prov with service loc B was not found");

//		Assert.assertTrue((driver.findElement(By.id("")).getText().equals()), "");
//		driver.findElement(By.id("")).click();
//		driver.findElement(By.id("")).getText();
//		driver.findElement(By.id("")).sendKeys("");
//	    new Select(driver.findElement(By.id(""))).selectByVisibleText("");
	}
    	
	@Test
	public void test23117() throws Exception {
		TestNGCustom.TCNo="23117";
    	log("//TC 23117");
    	
    	//get case number, without any history of case letters
    	sqlStatement="select a.id_case from T_Pr_case a, T_PR_CASE_PROV_XREF b, t_pr_prov c where a.sak_case=b.sak_case and b.sak_prov = c.sak_prov and a.sak_atn <> '-1' and a.id_case not in (select id_case from t_letter_request where sak_letter  in ('85','64','69','70')) and rownum < 2";
    	colNames.add("ID_CASE");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String casenum = colValues.get(0);
    	log("Case No. is: "+casenum);
    	
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_CaseTracking")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingSearchBean_CriteriaPanel:ProviderCTSearch_CaseNumber_Id")).sendKeys(casenum);
		Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:CaseTrackingSearchResults_0:CaseTrackingSearchPanelSpacer2")).click();
		//letters
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingNavigatorPanel:ProviderCaseTrackingNavigator:GRP_g2")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingNavigatorPanel:ProviderCaseTrackingNavigator:ITM_n1005")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPRCApprovalProbation:DataPanel_PrcApprovalRelatedProviders"))).selectByIndex(1);
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPRCApprovalProbation:DataPanel_PrcApprovalSancAgency")).sendKeys("AUTOMATION REGRESSION");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPRCApprovalProbation:DataPanel_PrcApprovalEffectiveDate")).sendKeys(Common.convertSysdate());
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPRCApprovalProbation:DataPanel_PrcApprovalMeetingDate")).sendKeys(Common.convertSysdate());
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Create Letter']")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Generate and Print']")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
		Common.cancelAll();
		
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingNavigatorPanel:ProviderCaseTrackingNavigator:ITM_n1004")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPRCMeetingLetterPanel:DataPanel_SancAgency")).sendKeys("AUTOMATION REGRESSION");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPRCMeetingLetterPanel:DataPanel_SancEffectiveDate")).sendKeys(Common.convertSysdate());
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPRCMeetingLetterPanel:DataPanel_MeetingDate")).sendKeys(Common.convertSysdate());
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Create Letter']")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Generate and Print']")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
		Common.cancelAll();
		//Check Letter history
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingNavigatorPanel:ProviderCaseTrackingNavigator:GRP_g1")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingNavigatorPanel:ProviderCaseTrackingNavigator:ITM_n102")).click();
		//order asc by letter name
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseLetterHistoryPanel:LetterSearchResults:ProviderCaseLetterHistoryBean_ColHeader_letterId")).click();
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseLetterHistoryPanel:LetterSearchResults_0:ProviderCaseLetterHistoryBean_ColValue_letterId")).getText().equals("PRV-9046-R")), "9046 letter is not present in history");
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseLetterHistoryPanel:LetterSearchResults_1:ProviderCaseLetterHistoryBean_ColValue_letterId")).getText().equals("PRV-9047-R")), "9047 letter is not present in history");

	}
	
	@Test
	public void test23118() throws Exception {
		TestNGCustom.TCNo="23118";
    	log("//TC 23118");
    	
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProgramEligibilityList"))).selectByVisibleText("MANAGED CARE SITE");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_CountyList"))).selectByVisibleText("Essex");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	}
    	
	
	@Test
	public void test23119() throws Exception {
		TestNGCustom.TCNo="23119";
    	log("//TC 23119");
    	
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderTypePopUpSearchControl")).sendKeys("20");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_McProgramList"))).selectByVisibleText("A LIST OF SERVICES THAT REQUIRE REFERRAL");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	}
	
	@Test
	public void test23120() throws Exception {
		TestNGCustom.TCNo="23120";
    	log("//TC 23120");
    	
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderTypePopUpSearchControl")).sendKeys("07");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ServicingProvidersList"))).selectByVisibleText("Exclude servicing");
	    Common.search();
	    String oldProv = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).getText();
	    log("Old Prov is: "+oldProv);
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	    //Go to service loc and Change billing indicator from yes to no
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n104")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderServiceLocationPanel:ProviderServiceLocationDataPanel_BillerIndicator"))).selectByVisibleText("No");
	    Common.save();
	    
	    //Search again
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderTypePopUpSearchControl")).sendKeys("07");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ServicingProvidersList"))).selectByVisibleText("Exclude servicing");
	    Common.search();
	    if (driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).getText().equals(oldProv))
			Assert.assertTrue(false, "Previous provider was still returned");
	}
	
	@Test
	public void test23121() throws Exception {
		TestNGCustom.TCNo="23121";
    	log("//TC 23121");
    	
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderTypePopUpSearchControl")).sendKeys("01");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_CountyList"))).selectByVisibleText("Middlesex");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	}
	
	@Test
	public void test23122() throws Exception {
		TestNGCustom.TCNo="23122";
    	log("//TC 23122");
    	
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderSearchResult_Phone")).sendKeys("4136252305");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	}
	
	@Test
	public void test23123() throws Exception {
		TestNGCustom.TCNo="23123";
    	log("//TC 23123");
    	
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderTypePopUpSearchControl")).sendKeys("01");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:SpecialtySearchControl")).sendKeys("504");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	 
	}
	
	@Test
	public void test23124() throws Exception {
		TestNGCustom.TCNo="23124";
    	log("//TC 23124");
    	
    	//get atn for non enrolled provider
    	sqlStatement="select sak_atn from t_pr_appln where cde_status1 = '12' and rownum<2"; //12 is in process. Table is t_pr_cde_appln_status
    	colNames.add("SAK_ATN");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String atn = colValues.get(0);
    	log("ATN is: "+atn);
    	//Search ATN in enrollment tab
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(atn);
		Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchResults_0:EnrollmentSearchPanelSpacer24")).click();
		//Search ATN in Provider tab
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderSearchResult_ATN")).sendKeys(atn);
		Common.search();
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).size()>0)
			Assert.assertTrue(false, "Search was succesful for an ATN for a partially enrolled provider");
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}
	
	@Test
	public void test23125() throws Exception {
		TestNGCustom.TCNo="23125";
    	log("//TC 23125");
    	//Search by Prov ID
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys("110026858");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	    //Search by npi
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMiniSearchProviderID")).sendKeys("1073638045");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	    //Search by legacy ID
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMiniSearchProviderID")).sendKeys("001200445");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	 
	}
	
	@Test
	public void test23127() throws Exception {
		TestNGCustom.TCNo="23127";
    	log("//TC 23127");
    	
    	//get atn
    	sqlStatement="select sak_atn from t_pr_appln where cde_status1 = '12' and rownum<2"; //12 is in process. Table is t_pr_cde_appln_status
    	colNames.add("SAK_ATN");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String atn = colValues.get(0);
    	log("ATN is: "+atn);
    	//Search ATN in enrollment tab
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(atn);
		Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchResults_0:EnrollmentSearchPanelSpacer24")).click();
		//generate RTP Letter
		Common.cancelAll(); //to ensure no other panel is open with NEW button
		driver.findElement(By.id("MMISForm:MMISBodyContent:ApplicationNavigatorPanel:providerapplicationnavigator:ITM_n102")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationReturnToProviderPanel:ReasonTextId")).sendKeys("AUTOMATION REGRESSION");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Create RTP Letter']")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Generate and Print']")).click();
		log("RTP Letter Name is: "+Common.fileName());

	}
	
	@Test
	public void test23128() throws Exception {
		TestNGCustom.TCNo="23128";
    	log("//TC 23128");
    	
    	//get atn in cred complete status
       	//REQUESTED
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("01 - REQUESTED");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_RequestTypeCode"))).selectByVisibleText("Mail");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ReceivedDate")).sendKeys(Common.convertSysdate());
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderAppSpacertName")).sendKeys(Common.generateRandomName());
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Street1")).sendKeys("101 FEDERAL ST");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_City")).sendKeys("BOSTON");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_State"))).selectByVisibleText("MA");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ZipCode")).sendKeys("02110");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_PhoneNumber")).sendKeys("6176176177");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ContactName")).sendKeys("AG");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_AddressEmail")).sendKeys("ansh90@gmail.com");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_CommPrefCode"))).selectByVisibleText("email");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_RelationshipEntityType"))).selectByVisibleText("0001 Medical Service Provider");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplication_CdeProviderType"))).selectByVisibleText("01 PHYSICIAN");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_TaxIDNumber")).sendKeys(Common.generateRandomTaxID());
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_TaxIDType"))).selectByVisibleText("FEIN");
		Common.save();
		
		//Store ATN
		String baseATN = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Sak")).getText().trim();
		log("ATN is: "+baseATN);
		//Ready For Review
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("02 - READY FOR REVIEW");
	    Common.save();
		
		//IN PROCESS
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(baseATN);
    	Common.search();
		driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("12 - IN PROCESS");
	    Common.save();
	    
    	//Search ATN in enrollment tab
	    LoginCheck();
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(baseATN);
		Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchResults_0:EnrollmentSearchPanelSpacer24")).click();
		//Change status to denied
		driver.findElement(By.id("MMISForm:MMISBodyContent:ApplicationNavigatorPanel:providerapplicationnavigator:ITM_n1")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("17 - DENIED");
		Common.save();
		//generate Denial Letter
		Common.cancelAll(); //This is added as some panels are automatically coming as opened. one of them is comment and its interfering with the New button press below
		driver.findElement(By.id("MMISForm:MMISBodyContent:ApplicationNavigatorPanel:providerapplicationnavigator:ITM_n112")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderEnrollmentDenialLetterPanel:ReasonTextId")).sendKeys("AUTOMATION REGRESSION");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Create Denial Letter']")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Generate and Print']")).click();
		log("Denial Letter Name is: "+Common.fileName());

	}
	
	@Test
	public void test23129() throws Exception {
		TestNGCustom.TCNo="23129";
    	log("//TC 23129");
    	
    	lName=Common.generateRandomName();
    	fName=Common.generateRandomName();
    	String adr1="101 FEDERAL STREET";
    	String city="BOSTON";
    	String state ="Massachusetts";
    	String stateab ="MA";
    	String zip="02110";
    	String phone="6176176177";
    	String email="ansh90@gmail.com";
    	
    	Common.portalLogin();
		driver.findElement(By.linkText("Manage Provider Information")).click();
		driver.findElement(By.linkText("Enrollment")).click();
		driver.findElement(By.linkText("Start an Enrollment Application")).click();
		conti();
		driver.findElement(By.xpath("//input[contains(@id,'lastName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'lastName')]")).sendKeys(lName);
		driver.findElement(By.xpath("//input[contains(@id,'firstName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'firstName')]")).sendKeys(fName);
		driver.findElement(By.xpath("//input[contains(@id,'providerSSN')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerSSN')]")).sendKeys(Common.generateRandomTaxID());
		driver.findElement(By.xpath("//input[contains(@id,'providerNPI')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerNPI')]")).sendKeys(Common.generateRandomTaxID()+"1");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'legalEntityType')]"))).selectByVisibleText("INDIVIDUAL PRACTITIONER");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'providerType')]"))).selectByVisibleText("PHYSICIAN");
		conti();
		conti();
		conti();
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationlastName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationlastName')]")).sendKeys(lName);
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationfirstName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationfirstName')]")).sendKeys(fName);
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationdateOfBirth')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationdateOfBirth')]")).sendKeys("01011966");
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationPIN')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerRegistrationPIN')]")).sendKeys("8188");
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressLine1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressLine1')]")).sendKeys(adr1);
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressCity')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressCity')]")).sendKeys(city);
		new Select(driver.findElement(By.xpath("//select[contains(@id,'providerAddressState')]"))).selectByVisibleText(state);
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressZip')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressZip')]")).sendKeys(zip);
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressPhoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressPhoneNumber')]")).sendKeys(phone);
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressEmail')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'providerAddressEmail')]")).sendKeys(email);
		//Add wait to solve captcha manually
		System.out.println("Solve captcha now. Waiting 40 seconds");
		Thread.sleep(40000);
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Submit']")).click();
		String atnNo = driver.findElement(By.xpath("//*[contains(@id,'atn1')]")).getText();
		log("ATN no. is: "+atnNo);
		conti();
		
		
		//Legal Entity
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_birthDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_birthDate')]")).sendKeys("01/01/1991");
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityContact')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityContact')]")).sendKeys(fName+" "+lName);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityAddr1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityAddr1')]")).sendKeys(adr1);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityCity')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityCity')]")).sendKeys(city);
		new Select(driver.findElement(By.xpath("//select[contains(@id,'provider_legalEntity_legalEntityState')]"))).selectByVisibleText(state);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityZip')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityZip')]")).sendKeys(zip);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityEmail')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityEmail')]")).sendKeys(email);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityPhoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityPhoneNumber')]")).sendKeys(phone);
		new Select(driver.findElement(By.xpath("//select[contains(@id,'provider_legalEntity_legalEntityOwnershipClass')]"))).selectByVisibleText("COUNTY");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'provider_legalEntity_legalEntityOwnershipType')]"))).selectByVisibleText("Corporation");
		driver.findElement(By.xpath("//input[@value='false' and contains(@name, 'provider_legalEntity_commercialUsageIndicator')]")).click();
		conti();
		//Save Application
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Save Provider Enrollment Application in-process; can update and submit later']")).click();
		Common.portalLogout();
		
		//Retrieve Application
    	Common.portalLogin();
		driver.findElement(By.linkText("Manage Provider Information")).click();
		driver.findElement(By.linkText("Enrollment")).click();
		driver.findElement(By.linkText("Continue Application")).click();
		int i=0;
		int j=0;
		int a=1;
		int b=2;
		boolean atnFound=false;

		for (;a<b; a++){
			for (;i<=j; i++){
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				if ((driver.findElements(By.xpath("//*[contains(@id,'applicationList:"+i+"')]")).size())>0) { 
					if (atnNo.equals(driver.findElement(By.xpath("//*[contains(@id,'applicationList:"+i+"')]")).getText())) { 
						driver.findElement(By.xpath("//*[contains(@id,'applicationList:"+i+"')]")).click();
							atnFound=true;
					}
					else
						j=j+1;
				}
			}
			if ((!atnFound) && ((driver.findElements(By.xpath("//*[contains(@id,'applicationList:paginatoridx"+b+"')]")).size())>0)) {
				driver.findElement(By.xpath("//*[contains(@id,'applicationList:paginatoridx"+b+"')]")).click();
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				b=b+1;
				i=i-1;
			}
		}
		if (!atnFound)
			System.out.println("I couldnt find atn, exiting now");
		
		//Complete Enrollment to not pile up things
		//Legal Entity
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityContact')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityContact')]")).sendKeys(fName+" "+lName);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityAddr1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityAddr1')]")).sendKeys(adr1);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityCity')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityCity')]")).sendKeys(city);
		new Select(driver.findElement(By.xpath("//select[contains(@id,'provider_legalEntity_legalEntityState')]"))).selectByVisibleText(state);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityZip')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityZip')]")).sendKeys(zip);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityEmail')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityEmail')]")).sendKeys(email);
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityPhoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'provider_legalEntity_legalEntityPhoneNumber')]")).sendKeys(phone);
		new Select(driver.findElement(By.xpath("//select[contains(@id,'provider_legalEntity_legalEntityOwnershipClass')]"))).selectByVisibleText("COUNTY");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'provider_legalEntity_legalEntityOwnershipType')]"))).selectByVisibleText("Corporation");
		driver.findElement(By.xpath("//input[@value='false' and contains(@name, 'provider_legalEntity_commercialUsageIndicator')]")).click();
		conti();
		
		//New Changes
		conti();
		conti();
		conti();
		conti();
		conti();
		conti(); //extra conti(); because ownership and control panel is not enabled while enrollment, but when save and retrieve application, it comes alive.  Might be a defect

		driver.findElement(By.xpath("//input[contains(@id,'attestName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'attestName')]")).sendKeys("ANSHUL GANDHI");
		driver.findElement(By.xpath("//input[contains(@id,'attestDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'attestDate')]")).sendKeys(Common.convertSysdate());
		driver.findElement(By.xpath("//input[contains(@id,'attestTitle')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'attestTitle')]")).sendKeys("BA");
	    conti();
	    
	    
	    //Service Locations
	    newItem();
		driver.findElement(By.xpath("//input[contains(@id,'lastName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'lastName')]")).sendKeys(lName);
		driver.findElement(By.xpath("//input[contains(@id,'firstName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'firstName')]")).sendKeys(fName);
		driver.findElement(By.xpath("//input[contains(@id,'serviceLocationContact')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'serviceLocationContact')]")).sendKeys(fName+" "+lName);
		driver.findElement(By.xpath("//input[contains(@id,'address1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'address1')]")).sendKeys(adr1);	    
		driver.findElement(By.xpath("//input[contains(@id,'city')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'city')]")).sendKeys(city);
		new Select(driver.findElement(By.xpath("//select[contains(@id,'state')]"))).selectByVisibleText(state);
		driver.findElement(By.xpath("//input[contains(@id,'zip')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'zip')]")).sendKeys(zip);
		driver.findElement(By.xpath("//input[contains(@id,'email')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'email')]")).sendKeys(email);
		driver.findElement(By.xpath("//input[contains(@id,'phoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'phoneNumber')]")).sendKeys(phone);
		driver.findElement(By.xpath("//input[contains(@id,'phoneNumberForTTDTTY')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'phoneNumberForTTDTTY')]")).sendKeys(phone);
	    add();
	    conti();
	    
	    conti();
	    
	    //Identification Info
		driver.findElement(By.xpath("//*[contains(@id,'0:dbaName_Link')]")).click();
		new Select(driver.findElement(By.xpath("//select[contains(@id,'organizationType')]"))).selectByVisibleText("CHAIN");
		driver.findElement(By.xpath("//input[contains(@id,'deaNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'deaNumber')]")).sendKeys(Common.generateRandomTaxID());
		driver.findElement(By.xpath("//input[contains(@id,'deaEffectiveDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'deaEffectiveDate')]")).sendKeys(Common.convertSysdate());
		driver.findElement(By.xpath("//input[contains(@id,'deaEndDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'deaEndDate')]")).sendKeys("12/31/2299");
	    update();
	    conti();
	    
	    conti();
	    conti();
	    conti();
	    conti();
	    
	    //License Info
		driver.findElement(By.xpath("//*[contains(@id,'0:dbaName_Link')]")).click();
		newItem();
		driver.findElement(By.xpath("//input[contains(@id,'licenseNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'licenseNumber')]")).sendKeys(Common.generateRandomTaxID()+"6");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'agencyName')]"))).selectByVisibleText("MA BOARD OF REGISTRATION IN MEDICINE");
		driver.findElement(By.xpath("//input[contains(@id,'licenseEffectiveDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'licenseEffectiveDate')]")).sendKeys(Common.convertSysdate());
		driver.findElement(By.xpath("//input[contains(@id,'licenseEndDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'licenseEndDate')]")).sendKeys("12/31/2299");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'licenseState')]"))).selectByVisibleText(state);
	    add();
	    conti();
	    
	    conti();
	    conti();
	    
	    //PCC
		driver.findElement(By.xpath("//*[contains(@id,'0:dbaName_Link')]")).click();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_managedCareContact')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_managedCareContact')]")).sendKeys(Common.generateRandomName());
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_email')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_email')]")).sendKeys(email);
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_phoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_phoneNumber')]")).sendKeys(phone);
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_maxMembersRequested')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_maxMembersRequested')]")).sendKeys("99");
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_ageLessThan')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_ageLessThan')]")).sendKeys("99");
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_ageGreaterThan')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'pccInfo_ageGreaterThan')]")).sendKeys("11");
	    update();
	    conti();
	    
	    conti();
	    conti();
	    conti();
	    conti();
	    
	    //Trading Partner Agreement
		driver.findElement(By.xpath("//input[contains(@id,'agreementDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'agreementDate')]")).sendKeys(Common.convertSysdate());
		driver.findElement(By.xpath("//input[contains(@id,'legalName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalName')]")).sendKeys(Common.generateRandomName());
		driver.findElement(By.xpath("//input[contains(@id,'signature')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'signature')]")).sendKeys(Common.generateRandomName());
		driver.findElement(By.xpath("//input[contains(@id,'phoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'phoneNumber')]")).sendKeys("6176176777");
		driver.findElement(By.xpath("//input[contains(@id,'email')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'email')]")).sendKeys(email);
	    conti();
	    
//	    conti();
	    
	    //Provider Contract
		driver.findElement(By.xpath("//input[@type='radio' and @value='true']")).click();
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Submit']")).click();
		
		//Verify ATN
		String confirmationATN = driver.findElement(By.xpath("//form[@id='enrollmentConfirmation']/table/tbody/tr/td[2]/div/table/tbody/tr[11]/td/span[5]/i")).getText();
		Assert.assertTrue(confirmationATN.equals(atnNo), "Confirmation ATN: "+confirmationATN+" not qual to initial ATN: "+atnNo);
//		Assert.assertTrue(driver.findElement(By.xpath("//span[@id='enrollmentConfirmation:continueEnrollApplication_1_id0:continueEnrollApplication_1_id4:continueEnrollApplication_1_id36']/i")).getText().equals(atnNo), "Provider enrollment confirmation failed on submit. ATN not present on confirmation page");
		Common.portalLogout();

	}
	
	@Test
	public void test23483() throws Exception {
		TestNGCustom.TCNo="23483";
    	log("//TC 23483");
    	//Check if Pharmacy is present or not
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:ITM_n16")).click();
    	int i=0;
		int j=0;
		int a=1;
		int b=2;
		boolean pharmacyFound=false;
		String baseFlag = "No";

		for (;a<b; a++){
			for (;i<=j; i++){
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				if ((driver.findElements(By.id("MMISForm:MMISBodyContent:ProviderTypeCodePanel:ProviderTypeCodeList_"+i+":ProviderTypeCodeBean_ColValue_providerTypeDescription")).size())>0) {
					if ("PHARMACY".equals(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeCodePanel:ProviderTypeCodeList_"+i+":ProviderTypeCodeBean_ColValue_providerTypeDescription")).getText())) { 
						baseFlag = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeCodePanel:ProviderTypeCodeList_"+i+":ProviderTypeCodeBean_ColValue_webPortalIndicator")).getText();
						pharmacyFound=true;
						log("Web portal for pharmacy prov is "+baseFlag+" in base app");					
					}
					else
						j=j+1;
				}
			}
			if ((!pharmacyFound) && ((driver.findElements(By.id("MMISForm:MMISBodyContent:ProviderTypeCodePanel:ProviderTypeCodeScrolleridx"+b)).size())>0)) {
				driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeCodePanel:ProviderTypeCodeScrolleridx"+b)).click();
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				b=b+1;
				i=i-1;
			}
		}
		if (!pharmacyFound)
			throw new SkipException("Skipping this test beacuse PHARMACY provider type was not found in Base app");
		
		Common.portalLogin();
		driver.findElement(By.linkText("Manage Provider Information")).click();
		driver.findElement(By.linkText("Enrollment")).click();
		driver.findElement(By.linkText("Start an Enrollment Application")).click();
		conti();
		if (baseFlag.equals("Yes")) {
			new Select(driver.findElement(By.xpath("//select[contains(@id,'providerType')]"))).selectByVisibleText("PHARMACY");
			log("Successfully found PHARMACY in portal enrollment list");
		}
		else {
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			try {
				new Select(driver.findElement(By.xpath("//select[contains(@id,'providerType')]"))).selectByVisibleText("PHARMACY");
				log("Failed! Found PHARMACY in portal enrollment list, but its set to no in Base app");
				Assert.assertTrue(false, "Failed! Found PHARMACY in portal enrollment list, but its set to no in Base app");
			}
			catch (NoSuchElementException exception) {
				log("Success! There is no provider in portal enrollment list");
			}
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		}
		
		Common.portalLogout();	
	}
	
	@Test
	public void test23130() throws Exception {
		TestNGCustom.TCNo="23130";
    	log("//TC 23130");
    	
    	//get tpaProv-- Note -this sql has info to keep off any license numbers containing a -, because that has problem in saving on panel. Also searching a lic tht contains 1, as it has less possibility of special characters
    	sqlStatement="Select Id_Provider From T_Pr_Prov Where Id_Provider Not In (Select Substr(Custno, 1, 9) From T_Trading_Partner Where Custno Is Not Null) and sak_prov not in (select d.sak_prov from T_PR_TAX_ID d , T_Pr_Hb_Lic e where d.num_tax_id=e.num_ssn and e.num_prov_lic like '%-%') and sak_prov in (select f.sak_prov from T_PR_TAX_ID f , T_Pr_Hb_Lic g where f.num_tax_id=g.num_ssn and g.num_prov_lic like '%1%') And Rownum < 2";
    	colNames.add("ID_PROVIDER");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String tpaProv = colValues.get(0);
    	log("TPA Prov is: "+tpaProv);
    	
    	//Add TP details
    	searchProv(tpaProv);
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n1020")).click();
    	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TradingPartnerAgreementDetailsPanel:TradingPartnerAgreementDetailsDataPanel_CdeMt837I"))).selectByVisibleText("Web Portal Batch");
    	driver.findElement(By.id("MMISForm:MMISBodyContent:TradingPartnerAgreementDetailsPanel:TradingPartnerAgreementDetailsDataPanel_Contact1")).sendKeys("REGRESSION AUTOMATION");
    	driver.findElement(By.id("MMISForm:MMISBodyContent:TradingPartnerAgreementDetailsPanel:TradingPartnerAgreementDetailsDataPanel_Telephone1")).sendKeys("6176176177");
    	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TradingPartnerAgreementDetailsPanel:TradingPartnerAgreementDetailsDataPanel_CdeTpaStatus"))).selectByVisibleText("COMPLETED");
    	driver.findElement(By.id("MMISForm:MMISBodyContent:TradingPartnerAgreementDetailsPanel:TradingPartnerAgreementDetailsDataPanel_ReceivedDate")).sendKeys(Common.convertSysdate());
    	Common.save();
    	Common.cancelAll();
    	
    	//Assign TP summary
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n1024")).click();
    	new Select(driver.findElement(By.id("ProviderTpOXiTransSummaryPanelMultiplesink"))).selectByVisibleText("837I - 005010X223A2 Batch");
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTpOXiTransSummaryPanel:ProviderTpOXiTransSummaryPanelMultipleimage_button__left_single")).click();
    	Common.save();	
    }
	
	@Test
	public void test23131() throws Exception {
		TestNGCustom.TCNo="23131";
    	log("//TC 23131");
    	
    	//get eftProv
    	sqlStatement="select id_provider from T_PR_prov where sak_prov not in (select sak_prov from T_PR_EFT_ACCT) and rownum < 2";
    	colNames.add("ID_PROVIDER");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String eftProv = colValues.get(0);
    	log("EFT Prov is: "+eftProv);
    	
    	//Add EFT details
    	searchProv(eftProv);
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10409")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderEFTAccountPanel:ProviderEFTAccountDataPanel_ABANumber")).sendKeys(Common.generateRandomTaxID()); 
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderEFTAccountPanel:ProviderEFTAccountDataPanel_EFTAccountNumber")).click(); //clciking here for chromedriver to have time, as page refreshes after entering ABA number
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderEFTAccountPanel:ProviderEFTAccountDataPanel_EFTAccountNumber")).clear();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderEFTAccountPanel:ProviderEFTAccountDataPanel_EFTAccountNumber")).sendKeys(Common.generateRandomTaxID());
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderEFTAccountPanel:ProviderEFTAccountDataPanel_AccountTypeIndicator"))).selectByVisibleText("Checking");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderEFTAccountPanel:ProviderEFTAccountDataPanel_StatusEFTCode"))).selectByVisibleText("Active");
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderEFTAccountPanel:ProviderEFTAccountDataPanel_EmailAddress")).sendKeys("ansh90@gmail.com");
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderEFTAccountPanel:ProviderEFTAccountDataPanel_SignatureDate")).sendKeys(Common.convertSysdate());
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		Common.save();
	}
	
	@Test
	public void test23132() throws Exception {
		TestNGCustom.TCNo="23132";
    	log("//TC 23132");
    	
    	//get prov with billing address in SPRINGFIELD
    	sqlStatement="select id_provider from T_PR_prov where sak_prov in (select a.sak_prov from T_Pr_Adr a, T_PR_LOC_NM_ADR b where A.Sak_Short_Address=B.Sak_Short_Address and a.sak_prov=b.sak_prov and B.Ind_Addr_Type='B' and A.Adr_Mail_City = 'SPRINGFIELD' and B.Cde_Service_Loc = 'A') and sak_prov not in (select d.sak_prov from T_PR_TAX_ID d , T_Pr_Hb_Lic e where d.num_tax_id=e.num_ssn and e.num_prov_lic like '%-%') and sak_prov in (select f.sak_prov from T_PR_TAX_ID f , T_Pr_Hb_Lic g where f.num_tax_id=g.num_ssn and g.num_prov_lic like '%1%') and rownum < 2";
    	colNames.add("ID_PROVIDER");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String addProv = colValues.get(0);
    	log("Address Prov is: "+addProv);
    	
    	//Change address
    	searchProv(addProv);
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10414")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressList_0:ProviderLocationNameAddressBean_ColValue_prAddrCode_addressUsageDescription")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Maintain Address']")).click();
		
		//Get previous address
		String adr1 = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:address1ID")).getAttribute("value").trim();
		String city = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:cityID")).getAttribute("value").trim();
		String zip = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:zipID")).getAttribute("value").trim();
		
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:address1ID")).clear();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:address1ID")).sendKeys("101 FEDERAL ST");
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:address2ID")).clear();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:cityID")).clear();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:cityID")).sendKeys("BOSTON");
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:zipID")).clear();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:zipID")).sendKeys("02110");
	    //Address verification
    	Common.update();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).click();
		Common.save();
		Common.cancelAll();
		
		//Check Audit panel
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10414")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressList_0:ProviderLocationNameAddressBean_ColValue_prAddrCode_addressUsageDescription")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:LocationNameAddressPanel_CMD_AUDIT")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressAuditPanel:id_selectAll")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressAuditPanel:SEARCH")).click();
    	//Sort on sysdate desc
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressAuditPanel:ProviderLocationNameAddressAuditList:SystemDate23_id")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressAuditPanel:ProviderLocationNameAddressAuditList:SystemDate23_id")).click();
    	//Verify data
    	Assert.assertTrue((driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressAuditPanel:ProviderLocationNameAddressAuditList:tbody_element']/tr/td[5]/span")).getText().equals(adr1)), "adr1 mismatch. Expected: "+adr1);
    	Assert.assertTrue((driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressAuditPanel:ProviderLocationNameAddressAuditList:tbody_element']/tr/td[7]/span")).getText().equals(city)), "city mismatch. Expected: "+city);
    	Assert.assertTrue((driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressAuditPanel:ProviderLocationNameAddressAuditList:tbody_element']/tr/td[9]/span")).getText().equals(zip)), "zip mismatch. Expected: "+zip);
    	if (env.contains("PERF"))
    		Assert.assertTrue((driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressAuditPanel:ProviderLocationNameAddressAuditList:tbody_element']/tr/td[22]/span")).getText().equals("BPERF000")), "user id mismatch");
    	else
    		Assert.assertTrue((driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressAuditPanel:ProviderLocationNameAddressAuditList:tbody_element']/tr/td[22]/span")).getText().equals("BGENER00")), "user id mismatch");
    	Assert.assertTrue((driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressAuditPanel:ProviderLocationNameAddressAuditList:tbody_element']/tr/td[24]/span")).getText().contains(Common.convertSysdate())), "date mismatch");
    	Assert.assertTrue((driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProviderLocationNameAddressPanel:ProviderLocationNameAddressAuditPanel:ProviderLocationNameAddressAuditList:tbody_element']/tr/td[25]/span")).getText().equals("U")), "action mismatch");
 
	}
	
	@Test
	public void test23133() throws Exception {
		TestNGCustom.TCNo="23133";
    	log("//TC 23133");
    	
    	//Already submitted claim for prov 110026858B in TC 23116, so will just check the date for him on day 2
    	String dateprov="110026858";
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(dateprov);
	    Common.search();
	    //Select SVC loc B
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_1:_id16")).click();
	    Assert.assertTrue((driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:ProviderInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[24]/td[2]")).getText().equals(Common.convertSysdatecustom(-1))), "Provider's Last Claim Submission date mismatch");
	}
	
	@Test
	public void test23134() throws Exception {
		TestNGCustom.TCNo="23134";
    	log("//TC 23134");
    	String taxID = "042651776";
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderSearchResult_TaxId")).sendKeys(taxID);
	    Common.search();
	    Assert.assertTrue((driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProviderSearchResults:tbody_element']/tr/td[4]")).getText().contains("GATRA")), "GATRA prov was not returned");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_McProgramList"))).selectByVisibleText("HEBREW REHABILITATION");
	    Common.search();
	    Assert.assertTrue((driver.findElement(By.cssSelector("span.redreg")).getText().equals("***No records found***")), "Provider was returned in search. No prov should have returned");
	}
	
    @Test
	public void test23098() throws Exception {
    	//If failed, before rerun, delete these 2. First, prov>related data>type cert spec> select 07 therapist, and delete ZZZ automation regression. Second, prov>related data>cert spec, and delete ZZZ automation regression.
		TestNGCustom.TCNo="23098";
    	log("//TC 23098");
    	
    	String certSpec = "ZZZ";
    	
    	//Add new Cert Spec
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:ITM_n11")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSpecialtyCodePanel:ProviderSpecialtyCodeDataPanel_SpecialtyCode")).sendKeys(certSpec);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSpecialtyCodePanel:ProviderSpecialtyCodeDataPanel_ProviderSpecialtyDescription")).sendKeys("AUTOMATION REGRESSION");
		Common.save();
		Common.cancelAll();
		log("Cert Spec "+certSpec+"-AUTOMATION REGRESSION was added");
		
		//Select THERAPY in type cert spec and add above cert spec
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:ITM_n25")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyCodeSearchResults_6:ProviderTypeSpecialtyCodeBean_ColValue_typeCode")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
//		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyPanel:SpecialtySearchID_CMD_SEARCH")).click(); //Not working for chromedriver
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyPanel:SpecialtySearchID_CMD_SEARCH']/img")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyPanel:_id73:SpecialtySearchCriteriaPanel:Type")).sendKeys(certSpec);
		Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyPanel:_id73:SpecialtySearchResults_0:column1Value")).click();
//		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyPanel:TaxonomySearchID_CMD_SEARCH")).click(); //Not working for chromedriver
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyPanel:TaxonomySearchID_CMD_SEARCH']/img")).click();
		Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyPanel:_id73:TaxonomySearchResults_0:column1Value")).click();
		Common.save();
		
		//Go to Reference-Procedure
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Reference")).click();
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Procedure")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureSearchBean_CriteriaPanel:ProcedureSearchResultDataPanel_Procedure")).sendKeys("99293");
		Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureSearchResults_0:Procedure")).click();
		//Enter Restriction
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureHCPCSNavigatorPanel:ProcedureHCPCSNavigator:GRP_g100")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureHCPCSNavigatorPanel:ProcedureHCPCSNavigator:ITM_n113")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureLimitsList_0:ProcedureLimitsBean_ColValue_effectiveDate")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureSpecialityLimPanel:ProcedureSpecialtyLim_NewButtonClay:ProcedureSpecialtyLimList_newAction_btn")).click();
//		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureSpecialityLimPanel:RefProcedureRestrictionBaseInfoSpecialityCodeSearchControl_CMD_SEARCH")).click(); //Not working for chromedriver
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureSpecialityLimPanel:RefProcedureRestrictionBaseInfoSpecialityCodeSearchControl_CMD_SEARCH']/img")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureSpecialityLimPanel:_id25:DiagProvSpecSearchCriteriaPanel:SpecCodeFrom")).sendKeys(certSpec);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureSpecialityLimPanel:_id25:DiagProvSpecSearchCriteriaPanel:SEARCH")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureSpecialityLimPanel:_id25:DiagProvSpecSearchResults_0:column1Value")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureSpecialityLimPanel:ProcedureSpecialityLimEndDateSpacer")).sendKeys(Common.convertSysdatecustom(1));
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		Common.update();
		Common.save();
		Common.cancelAll();
		
		//Delete everything for next test
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Provider")).click();
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Reference")).click();
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Procedure")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureSearchBean_CriteriaPanel:ProcedureSearchResultDataPanel_Procedure")).sendKeys("99293");
		Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureSearchResults_0:Procedure")).click();
		//Enter Restriction
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureHCPCSNavigatorPanel:ProcedureHCPCSNavigator:GRP_g100")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureHCPCSNavigatorPanel:ProcedureHCPCSNavigator:ITM_n113")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureLimitsList_0:ProcedureLimitsBean_ColValue_effectiveDate")).click();
		//Sort on Cert Spec desc
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureSpecialityLimPanel:ProcedureSpecialityLimitList:ProcedureSpecialtyLimBean_ColHeader_endDate")).click(); //Sort on end date
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureSpecialityLimPanel:ProcedureSpecialityLimitList:ProcedureSpecialtyLimBean_ColHeader_prSpecCde_specialtyCode")).click(); //Cert Spec sort asc
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureSpecialityLimPanel:ProcedureSpecialityLimitList:ProcedureSpecialtyLimBean_ColHeader_prSpecCde_specialtyCode")).click(); //Cert Spec sort desc
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureSpecialityLimPanel:ProcedureSpecialityLimitList_0:ProcedureSpecialtyLimBean_ColValue_prSpecCde_specialtyCode")).getText().equals(certSpec)), "Did not select ZZZ for deletion at Proc code restriction. please delete manually before next test");
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureSpecialityLimPanel:ProcedureSpecialityLimitList_0:ProcedureSpecialtyLimBean_ColValue_prSpecCde_specialtyCode")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProcedureLimitsPanel:ProcedureSpecialityLimPanel:ProcedureSpecialtyLimPanel_deleteAction_btn")).click();
		//Declare Alert to handle Popup
		alert = driver.switchTo().alert();
		System.out.println(alert.getText());
		alert.accept();
		Common.save();
		Common.cancelAll();
		
		//Delete type cert spec
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Provider")).click();
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:ITM_n25")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyCodeSearchResults_6:ProviderTypeSpecialtyCodeBean_ColValue_typeCode")).click();
    	//Sort on Cert Spec desc
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyPanel:ProviderTypeSpecialtyBeanResults:ProviderTypeSpecialtyBean_ColHeader_PrTaxonomyCde_taxonomyCode")).click(); //TAXONOMY sort
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyPanel:ProviderTypeSpecialtyBeanResults:ProviderTypeSpecialtyBean_ColHeader_PrSpecCde_specialtyCode")).click(); //Cert Spec sort asc
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyPanel:ProviderTypeSpecialtyBeanResults:ProviderTypeSpecialtyBean_ColHeader_PrSpecCde_specialtyCode")).click(); //Cert Spec sort desc
		Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyPanel:ProviderTypeSpecialtyBeanResults_0:ProviderTypeSpecialtyBean_ColValue_PrSpecCde_specialtyCode")).getText().equals(certSpec)), "Did not select ZZZ for deletion at type cert spec. please delete manually before next test");
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderTypeSpecialtyCodePanel:ProviderTypeSpecialtyPanel:ProviderTypeSpecialtyBeanResults_0:ProviderTypeSpecialtyBean_ColValue_PrSpecCde_specialtyCode")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Delete']")).click();
		//Declare Alert to handle Popup
		alert = driver.switchTo().alert();
		System.out.println(alert.getText());
		alert.accept();
		Common.save();
		Common.cancelAll();
		
		//Delete CERT spec
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:ITM_n11")).click();
    	//Sort on Cert Spec desc
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSpecialtyCodePanel:ProviderSpecialtyCodeSearchResults:ProviderSpecialtyCodeBean_ColHeader_ProviderSpecialtyDescription")).click(); //Sort description
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSpecialtyCodePanel:ProviderSpecialtyCodeSearchResults:ProviderSpecialtyCodeBean_ColHeader_SpecialtyCode")).click(); //Cert Spec sort asc
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSpecialtyCodePanel:ProviderSpecialtyCodeSearchResults:ProviderSpecialtyCodeBean_ColHeader_SpecialtyCode")).click(); //Cert Spec sort desc
    	Assert.assertTrue((driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSpecialtyCodePanel:ProviderSpecialtyCodeSearchResults_0:ProviderSpecialtyCodeBean_ColValue_SpecialtyCode")).getText().equals(certSpec)), "Did not select ZZZ for deletion at cert spec. please delete manually before next test");
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSpecialtyCodePanel:ProviderSpecialtyCodeSearchResults_0:ProviderSpecialtyCodeBean_ColValue_SpecialtyCode")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Delete']")).click();
		//Declare Alert to handle Popup
		alert = driver.switchTo().alert();
		System.out.println(alert.getText());
		alert.accept();
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
		
		log("Cert Spec "+certSpec+"-AUTOMATION REGRESSION was deleted");

    }
	
	@Test
	public void test22472() throws Exception {
		TestNGCustom.TCNo="22472";
    	log("//TC 22472");
    
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRptsAndLettersNavigatorPanel:ProviderReportsNavigator:GRP_g2")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRptsAndLettersNavigatorPanel:ProviderReportsNavigator:ITM_n104")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ChangeLetterPanel:ChangeLetterBean_CriteriaPanel:ProviderSearchResultDataPanel_ProviderId")).sendKeys("110000050");
		Common.search();
		
		driver.findElement(By.id("MMISForm:MMISBodyContent:ChangeLetterPanel:ChangeLetterResults_0:ChangeLetterBean_ColValue_providerID")).click();
		//Verify Update request letter panel is returned
		Assert.assertTrue((driver.findElement(By.cssSelector("h4.panel-header")).getText().equals(" Update Request Letter")), "Update request letter panel did not show up");
		
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ChangeLetterPanel:RTPLetterPRV9009RPanel:ReasonTextId")).sendKeys("PRV 9009R Letter generation test by selenium");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Create RTP Letter']")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Generate and Print']")).click();
		log("PRV 9009R Letter File name is: "+Common.fileName());

	}
	
	//Old code with workflow
//	@Test
//	public void test23108() throws Exception {
//		TestNGCustom.TCNo="23108";
//    	log("//TC 23108");
//
//    	//REQUESTED
//    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
//		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("01 - REQUESTED");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_RequestTypeCode"))).selectByVisibleText("Mail");
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ReceivedDate")).sendKeys(Common.convertSysdate());
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderAppSpacertName")).sendKeys(Common.generateRandomName());
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Street1")).sendKeys("101 FEDERAL ST");
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_City")).sendKeys("BOSTON");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_State"))).selectByVisibleText("MA");
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ZipCode")).sendKeys("02110");
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_PhoneNumber")).sendKeys("6176176177");
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ContactName")).sendKeys("AG");
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_AddressEmail")).sendKeys("ansh90@gmail.com");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_CommPrefCode"))).selectByVisibleText("email");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_RelationshipEntityType"))).selectByVisibleText("0001 Medical Service Provider");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplication_CdeProviderType"))).selectByVisibleText("01 PHYSICIAN");
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_TaxIDNumber")).sendKeys(Common.generateRandomTaxID());
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_TaxIDType"))).selectByVisibleText("FEIN");
//		Common.save();
//		
//		//Store ATN
//		String baseATN = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Sak")).getText();
//		log("ATN is: "+baseATN);
//		//Ready For Review
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("02 - READY FOR REVIEW");
//	    Common.save();
//		
//		//In review
//	    //Adding a 5 minute delay because WI not showing up right away
//	    Thread.sleep(300000);
//	    enrlWrklistNew("UNASSIGNED",baseATN);
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("04 - IN REVIEW");
//	    Common.save();
//		
//		//Credentialing
//	    enrlWrklistNew("CREDENTIALING",baseATN);
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("05 - CREDENTIALING COMPLETE");
//	    Common.save();
//		
//		//QA
//	    enrlWrklistNew("QA",baseATN);
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("18 - QA COMPLETE");
//	    Common.save();
//		
//		//Ready to enroll
//	    enrlWrklistNew("APPROVAL",baseATN);
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("33 - READY TO ENROLL");
//	    Common.save();
//		
//		//ID Issued
//	    enrlWrklistNew("APPROVAL",baseATN);
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:id_EnrollProv")).click();
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_OwnershipType"))).selectByVisibleText("Corporation");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_DelegatedCredentIndicator"))).selectByVisibleText("No");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ManagedCareGroup"))).selectByVisibleText("No");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_BillingAgent"))).selectByVisibleText("No");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ManagedCareOrganization"))).selectByVisibleText("No");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_HospitalAffiliation"))).selectByVisibleText("No");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ProviderLegalEntityType"))).selectByVisibleText("BUSINESS CORPORATION");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_CommercialInsurance"))).selectByVisibleText("No");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ProviderOwnershipClass"))).selectByVisibleText("COUNTY");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_HospitalAffiliation"))).selectByVisibleText("No");
//	    
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_CityTownCode"))).selectByVisibleText("ACTON");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_ProviderOrganizationType"))).selectByVisibleText("CHAIN");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_UcpIndicator"))).selectByVisibleText("No");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_Coverage24hr"))).selectByVisibleText("No");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_BypassTPL"))).selectByVisibleText("No");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_OrganizationCode"))).selectByVisibleText("Corporation");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_AcceptNewMembers"))).selectByVisibleText("No");
//	   
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:ProviderEnrollPublicHealthProgramEligibility_NewButtonClay:ProviderEnrollPublicHealthProgramEligibilityList_newAction_btn")).click();
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:EnrollProgramDropDown_2"))).selectByVisibleText("MANAGED CARE SITE");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_PrEnrollStatus"))).selectByVisibleText("ACTIVE - Pay");
//	    
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:ProviderSpecialtyAdd_NewButtonClay:ProviderSpecialtyAddList_newAction_btn")).click();
////	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:ProviderSpecialtySearchControl_CMD_SEARCH")).click(); //Not working for chrome
//		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:ProviderSpecialtySearchControl_CMD_SEARCH']/img")).click();
//		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:_id149:TypeSpecialitiySearchResults_0:column1Value")).click();
//
//	    //IRS Tax Maintenance panel
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoNameID")).sendKeys(Common.generateRandomName());
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailStrt1")).sendKeys("101 FEDERAL ST");
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailCity")).sendKeys("BOSTON");
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailZip")).sendKeys("02110");
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailState"))).selectByVisibleText("MA");
//
//	    Common.save();
//		
//		//Enrolled
//	    enrlWrklistNew("ENROLLMENT",baseATN);
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("34 - ENROLLED");
//	    Common.save();
//		String enlProv=driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplProvIDSearchControl")).getAttribute("value");
//
//    	//Verify provider comes in search results
//		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).clear();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(enlProv);
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
//	    Common.search();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
//		log("Enrolled Provider type 01. Provider ID is "+enlProv);
//	    log("Program Eligibility of MANAGED CARE SITE was added.");
//
//	    //Add MC program
//		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:GRP_g102")).click();
//		driver.findElement(By.linkText("PMP")).click();
//	    Assert.assertTrue(driver.findElement(By.cssSelector("table.nested.panel > thead > tr > th > table > tbody > tr > td > h2.panel-header")).getText().trim().equals("PMP"), "PMP panel not found");
//		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
//	    Assert.assertTrue(driver.findElement(By.cssSelector("h3.panel-header")).getText().trim().equals("PMP Maintenance"), "PMP Maintenance panel not found");
//	    new Select(driver.findElement(By.xpath("//select[contains(@id,'McProgramList')]"))).selectByVisibleText("PCCP - A LIST OF SERVICES THAT REQUIRE REFERRAL");
//	    driver.findElement(By.xpath("//input[contains(@id,'Pho24HourNumber')]")).sendKeys(Common.generateRandomTaxID()+"0");
//	    new Select(driver.findElement(By.xpath("//select[contains(@id,'CurrentOnlyIndicator')]"))).selectByVisibleText("No");
//	    Common.saveAll();
//	    log("PCCP MC program was added");
//	    
//	    Common.cancelAll();
//	    //ADD PMP Panel Restrictions
//		driver.findElement(By.linkText("PMP")).click();
//		driver.findElement(By.linkText("PCCP")).click(); //This selects the first record that you just created
//		driver.findElement(By.linkText("PMP Panel Restrictions")).click();
//	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[3]/table/thead/tr/th/table/tbody/tr/td/h2")).getText().trim().equals("PMP Panel Restrictions"), "PMP Panel Restrictions panel not found");
//		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
//	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:PmpPanelRestrictPanel:PmpPanelRestrictBean_DataPanel']/thead/tr/td/table/tbody/tr/td/h3")).getText().trim().equals("PMP Panel Restrictions Maintenance"), "PMP Panel Restrictions Maintenance panel not found");
//	    new Select(driver.findElement(By.xpath("//select[contains(@id,'GenderRstrctnType')]"))).selectByVisibleText("Inclusion");
//	    new Select(driver.findElement(By.xpath("//select[contains(@id,'GenderRstrctnCode')]"))).selectByVisibleText("Male");
//	    Common.multiElements("//input[contains(@id,'EffectiveDate')]").sendKeys(Common.convertSysdate());
//	    Common.multiElements("//input[contains(@id,'EndDate')]").sendKeys(Common.convertSysdatecustom(2));
//	    Common.saveAll();
//	    log("PMP panel restriction was added");
//	    
//	    Common.cancelAll();
//	    //ADD PMP Panel Restrictions
//		driver.findElement(By.linkText("PMP")).click();
//		driver.findElement(By.linkText("PCCP")).click(); //This selects the first record that you just created
//		driver.findElement(By.linkText("PMP Panel Size Maintenance")).click();
//	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[3]/table/thead/tr/th/table/tbody/tr/td/h2")).getText().trim().equals("PMP Panel Size"), "PMP Panel Size panel not found");
//		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
//	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:PmpPanelSizePanel:PmpPanelSizeBean_DataPanel']/thead/tr/td/table/tbody/tr/td/h3")).getText().trim().equals("PMP Panel Size Maintenance"), "PMP Panel Size Maintenance panel not found");
//	    driver.findElement(By.xpath("//input[contains(@id,'MaxRecipsNumber')]")).clear();
//	    driver.findElement(By.xpath("//input[contains(@id,'MaxRecipsNumber')]")).sendKeys("50");
//	    Common.saveAll();
//	    log("PMP panel size was added");
//
//	    Common.cancelAll();
//	    //ADD PMP Performance Maintenance and History
//		driver.findElement(By.linkText("PMP")).click();
//		driver.findElement(By.linkText("PCCP")).click(); //This selects the first record that you just created
//		driver.findElement(By.linkText("PMP Performance Maintenance and History")).click();
//	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[3]/table/thead/tr/th/table/tbody/tr/td/h2")).getText().trim().equals("PMP Performance Maintenance and History"), "PMP Performance Maintenance and History panel not found");
//		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
//	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:PmpPerformanceHistoryMaintenancePanel:PmpPerformanceHistoryBean_DataPanel']/thead/tr/td/table/tbody/tr/td/h3")).getText().trim().equals("PMP Performance Maintenance"), "PMP Performance Maintenance panel not found");
//	    driver.findElement(By.xpath("//input[contains(@id,'pmpperformhistcde')]")).clear();
//	    driver.findElement(By.xpath("//input[contains(@id,'pmpperformhistcde')]")).sendKeys(Common.generateRandomTaxID().substring(6));
//	    driver.findElement(By.xpath("//input[contains(@id,'PmpPerformanceHistoryDataPanel_DscPerform')]")).clear();
//	    driver.findElement(By.xpath("//input[contains(@id,'PmpPerformanceHistoryDataPanel_DscPerform')]")).sendKeys(Common.generateRandomName().substring(0, 2)+" TEST");
//	    Common.multiElements("//input[contains(@id,'EffectiveDate')]").clear();
//	    Common.multiElements("//input[contains(@id,'EffectiveDate')]").sendKeys(Common.convertSysdate());
//	    Common.multiElements("//input[contains(@id,'EndDate')]").clear();
//	    Common.multiElements("//input[contains(@id,'EndDate')]").sendKeys(Common.convertSysdatecustom(2));
//	    Common.saveAll();
//	    log("PMP Performance Maintenance and History was added");
//	    
//	    Common.cancelAll();
//	    //ADD PMP Conversion Maintenance and History
//		driver.findElement(By.linkText("PMP")).click();
//		driver.findElement(By.linkText("PCCP")).click(); //This selects the first record that you just created
//		driver.findElement(By.linkText("PMP Conversion Maintenance and History")).click();
//	    Assert.assertTrue(driver.findElement(By.cssSelector("div.hscroll > table.nested.panel > thead > tr > th > table > tbody > tr > td > h2.panel-header")).getText().trim().equals("PMP Conversion Maintenance and History"), "PMP Conversion Maintenance and History panel not found");
//		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
//	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:ProvPmpConversionHistoryPanel:ProvPmpConversionHistoryBean_DataPanel']/thead/tr/td/table/tbody/tr/td/h3")).getText().trim().equals("PMP Conversion Selection Criteria"), "PMP Conversion Selection Criteria panel not found");
//	    new Select(driver.findElement(By.xpath("//select[contains(@id,'ConversionType')]"))).selectByVisibleText("PCC to PCC");
//	    //change below code to select PCCP from provider search instead of hardcoding
//	    driver.findElement(By.xpath("//input[contains(@id,'PmpId')]")).clear();
//	    driver.findElement(By.xpath("//input[contains(@id,'PmpId')]")).sendKeys("110000034");
//	    driver.findElement(By.xpath("//input[contains(@id,'SvcLoc')]")).clear();
//	    driver.findElement(By.xpath("//input[contains(@id,'SvcLoc')]")).sendKeys("F");
//	    driver.findElement(By.xpath("//input[contains(@id,'PreviousConversionMonthsNumber')]")).clear();
//	    driver.findElement(By.xpath("//input[contains(@id,'PreviousConversionMonthsNumber')]")).sendKeys("1");
//	    driver.findElement(By.xpath("//input[contains(@id,'FileTransferDate')]")).clear();
//	    driver.findElement(By.xpath("//input[contains(@id,'FileTransferDate')]")).sendKeys(Common.convertSysdate());
//	    driver.findElement(By.xpath("//input[contains(@id,'MailedDate')]")).clear();
//	    driver.findElement(By.xpath("//input[contains(@id,'MailedDate')]")).sendKeys(Common.convertSysdatecustom(2));
//	    driver.findElement(By.xpath("//input[contains(@id,'ConversionEffectiveDate')]")).clear();
//	    driver.findElement(By.xpath("//input[contains(@id,'ConversionEffectiveDate')]")).sendKeys(Common.convertSysdate());
//	    new Select(driver.findElement(By.xpath("//select[contains(@id,'DisenrollmentReasonCodeList')]"))).selectByValue("08");
//	    Common.saveAll();
//	    log("PMP Conversion Maintenance and History was added");
//	    
//	    Common.cancelAll();
//	    //ADD PMP Capitation History
//		driver.findElement(By.linkText("PMP")).click();
//		driver.findElement(By.linkText("PCCP")).click(); //This selects the first record that you just created
//		driver.findElement(By.linkText("PMP Capitation History")).click();
//	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[3]/table/thead/tr/th/table/tbody/tr/td/h2")).getText().trim().equals("PMP Capitation History"), "PMP Capitation History panel not found");
//	    new Select(driver.findElement(By.xpath("//select[contains(@id,'CapCategoryList')]"))).selectByVisibleText("MST MCO_ESSENTIAL");
//	    driver.findElement(By.xpath("//input[contains(@id,'CapitationDate')]")).sendKeys(Common.convertSysdate().substring(0, 2)+Common.convertSysdate().substring(5));
//	    Common.multiElements("//input[@class='buttonImage' and @alt='Search']").click();
//	    Assert.assertTrue(driver.findElement(By.cssSelector("span.redreg")).getText().trim().equals("***No records found***"), "***No records found*** message not found");
//	    Common.cancelAll();
//	    log("PMP Capitation History panel fields were tested. No record was added.");
//
//	}
	
	@Test
	public void test23108() throws Exception {
		TestNGCustom.TCNo="23108";
    	log("//TC 23108");

    	//REQUESTED
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("01 - REQUESTED");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_RequestTypeCode"))).selectByVisibleText("Mail");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ReceivedDate")).sendKeys(Common.convertSysdate());
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderAppSpacertName")).sendKeys(Common.generateRandomName());
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Street1")).sendKeys("101 FEDERAL ST");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_City")).sendKeys("BOSTON");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_State"))).selectByVisibleText("MA");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ZipCode")).sendKeys("02110");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_PhoneNumber")).sendKeys("6176176177");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ContactName")).sendKeys("AG");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_AddressEmail")).sendKeys("ansh90@gmail.com");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_CommPrefCode"))).selectByVisibleText("email");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_RelationshipEntityType"))).selectByVisibleText("0001 Medical Service Provider");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplication_CdeProviderType"))).selectByVisibleText("01 PHYSICIAN");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_TaxIDNumber")).sendKeys(Common.generateRandomTaxID());
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_TaxIDType"))).selectByVisibleText("FEIN");
		Common.save();
		
		//Store ATN
		String baseATN = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Sak")).getText();
		log("ATN is: "+baseATN);
		//Ready For Review
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("02 - READY FOR REVIEW");
	    Common.save();
		
		//IN PROCESS
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(baseATN);
    	Common.search();
		driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("12 - IN PROCESS");
	    Common.save();
	    
		//READY TO ENROLL
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(baseATN);
    	Common.search();
		driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("33 - READY TO ENROLL");
	    Common.save();
	    
	    //ID Issued
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(baseATN);
    	Common.search();
		driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:id_EnrollProv")).click();

	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_OwnershipType"))).selectByVisibleText("Corporation");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_DelegatedCredentIndicator"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ManagedCareGroup"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_BillingAgent"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ManagedCareOrganization"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_HospitalAffiliation"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ProviderLegalEntityType"))).selectByVisibleText("BUSINESS CORPORATION");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_CommercialInsurance"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ProviderOwnershipClass"))).selectByVisibleText("COUNTY");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_HospitalAffiliation"))).selectByVisibleText("No");
	    
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_CityTownCode"))).selectByVisibleText("ACTON");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_ProviderOrganizationType"))).selectByVisibleText("CHAIN");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_UcpIndicator"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_Coverage24hr"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_BypassTPL"))).selectByVisibleText("No");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_OrganizationCode"))).selectByVisibleText("Corporation");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_AcceptNewMembers"))).selectByVisibleText("No");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_NxtSchdCredDate")).sendKeys(Common.convertSysdatecustom(365));
	   
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:ProviderEnrollPublicHealthProgramEligibility_NewButtonClay:ProviderEnrollPublicHealthProgramEligibilityList_newAction_btn")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:EnrollProgramDropDown_2"))).selectByVisibleText("MANAGED CARE SITE");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_PrEnrollStatus"))).selectByVisibleText("ACTIVE - Pay");
	    
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:ProviderSpecialtyAdd_NewButtonClay:ProviderSpecialtyAddList_newAction_btn")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:ProviderSpecialtySearchControl_CMD_SEARCH")).click(); //Not working for chrome
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:ProviderSpecialtySearchControl_CMD_SEARCH']/img")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:_id149:TypeSpecialitiySearchResults_0:column1Value")).click();

	    //IRS Tax Maintenance panel
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoNameID")).sendKeys(Common.generateRandomName());
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailStrt1")).sendKeys("101 FEDERAL ST");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailCity")).sendKeys("BOSTON");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailZip")).sendKeys("02110");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailState"))).selectByVisibleText("MA");

	    Common.save();
	    String currStatus = new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).getFirstSelectedOption().getText();
	    Assert.assertTrue(currStatus.equals("39 - ID ISSUED"), "Status is not 39 - ID ISSUED. It is "+currStatus);

		//ENROLLED
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(baseATN);
    	Common.search();
		driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("34 - ENROLLED");
	    Common.save();
		String enlProv=driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplProvIDSearchControl")).getAttribute("value");

    	//Verify provider comes in search results
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(enlProv);
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderMainSearchResultDataPanel_ProviderStatusList"))).selectByVisibleText("Active");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
		log("Enrolled Provider type 01. Provider ID is "+enlProv);
	    log("Program Eligibility of MANAGED CARE SITE was added.");

	    //Add MC program
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:GRP_g102")).click();
		driver.findElement(By.linkText("PMP")).click();
	    Assert.assertTrue(driver.findElement(By.cssSelector("table.nested.panel > thead > tr > th > table > tbody > tr > td > h2.panel-header")).getText().trim().equals("PMP"), "PMP panel not found");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	    Assert.assertTrue(driver.findElement(By.cssSelector("h3.panel-header")).getText().trim().equals("PMP Maintenance"), "PMP Maintenance panel not found");
	    new Select(driver.findElement(By.xpath("//select[contains(@id,'McProgramList')]"))).selectByVisibleText("PCCP - A LIST OF SERVICES THAT REQUIRE REFERRAL");
	    driver.findElement(By.xpath("//input[contains(@id,'Pho24HourNumber')]")).sendKeys(Common.generateRandomTaxID()+"0");
	    new Select(driver.findElement(By.xpath("//select[contains(@id,'CurrentOnlyIndicator')]"))).selectByVisibleText("No");
	    Common.saveAll();
	    log("PCCP MC program was added");
	    
	    Common.cancelAll();
	    //ADD PMP Panel Restrictions
		driver.findElement(By.linkText("PMP")).click();
		driver.findElement(By.linkText("PCCP")).click(); //This selects the first record that you just created
		driver.findElement(By.linkText("PMP Panel Restrictions")).click();
	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[3]/table/thead/tr/th/table/tbody/tr/td/h2")).getText().trim().equals("PMP Panel Restrictions"), "PMP Panel Restrictions panel not found");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:PmpPanelRestrictPanel:PmpPanelRestrictBean_DataPanel']/thead/tr/td/table/tbody/tr/td/h3")).getText().trim().equals("PMP Panel Restrictions Maintenance"), "PMP Panel Restrictions Maintenance panel not found");
	    new Select(driver.findElement(By.xpath("//select[contains(@id,'GenderRstrctnType')]"))).selectByVisibleText("Inclusion");
	    new Select(driver.findElement(By.xpath("//select[contains(@id,'GenderRstrctnCode')]"))).selectByVisibleText("Male");
	    Common.multiElements("//input[contains(@id,'EffectiveDate')]").sendKeys(Common.convertSysdate());
	    Common.multiElements("//input[contains(@id,'EndDate')]").sendKeys(Common.convertSysdatecustom(2));
	    Common.saveAll();
	    log("PMP panel restriction was added");
	    
	    Common.cancelAll();
	    //ADD PMP Panel Restrictions
		driver.findElement(By.linkText("PMP")).click();
		driver.findElement(By.linkText("PCCP")).click(); //This selects the first record that you just created
		driver.findElement(By.linkText("PMP Panel Size Maintenance")).click();
	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[3]/table/thead/tr/th/table/tbody/tr/td/h2")).getText().trim().equals("PMP Panel Size"), "PMP Panel Size panel not found");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:PmpPanelSizePanel:PmpPanelSizeBean_DataPanel']/thead/tr/td/table/tbody/tr/td/h3")).getText().trim().equals("PMP Panel Size Maintenance"), "PMP Panel Size Maintenance panel not found");
	    driver.findElement(By.xpath("//input[contains(@id,'MaxRecipsNumber')]")).clear();
	    driver.findElement(By.xpath("//input[contains(@id,'MaxRecipsNumber')]")).sendKeys("50");
	    Common.saveAll();
	    log("PMP panel size was added");

	    Common.cancelAll();
	    //ADD PMP Performance Maintenance and History
		driver.findElement(By.linkText("PMP")).click();
		driver.findElement(By.linkText("PCCP")).click(); //This selects the first record that you just created
		driver.findElement(By.linkText("PMP Performance Maintenance and History")).click();
	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[3]/table/thead/tr/th/table/tbody/tr/td/h2")).getText().trim().equals("PMP Performance Maintenance and History"), "PMP Performance Maintenance and History panel not found");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:PmpPerformanceHistoryMaintenancePanel:PmpPerformanceHistoryBean_DataPanel']/thead/tr/td/table/tbody/tr/td/h3")).getText().trim().equals("PMP Performance Maintenance"), "PMP Performance Maintenance panel not found");
	    driver.findElement(By.xpath("//input[contains(@id,'pmpperformhistcde')]")).clear();
	    driver.findElement(By.xpath("//input[contains(@id,'pmpperformhistcde')]")).sendKeys(Common.generateRandomTaxID().substring(6));
	    driver.findElement(By.xpath("//input[contains(@id,'PmpPerformanceHistoryDataPanel_DscPerform')]")).clear();
	    driver.findElement(By.xpath("//input[contains(@id,'PmpPerformanceHistoryDataPanel_DscPerform')]")).sendKeys(Common.generateRandomName().substring(0, 2)+" TEST");
	    Common.multiElements("//input[contains(@id,'EffectiveDate')]").clear();
	    Common.multiElements("//input[contains(@id,'EffectiveDate')]").sendKeys(Common.convertSysdate());
	    Common.multiElements("//input[contains(@id,'EndDate')]").clear();
	    Common.multiElements("//input[contains(@id,'EndDate')]").sendKeys(Common.convertSysdatecustom(2));
	    Common.saveAll();
	    log("PMP Performance Maintenance and History was added");
	    
	    Common.cancelAll();
	    //ADD PMP Conversion Maintenance and History
		driver.findElement(By.linkText("PMP")).click();
		driver.findElement(By.linkText("PCCP")).click(); //This selects the first record that you just created
		driver.findElement(By.linkText("PMP Conversion Maintenance and History")).click();
	    Assert.assertTrue(driver.findElement(By.cssSelector("div.hscroll > table.nested.panel > thead > tr > th > table > tbody > tr > td > h2.panel-header")).getText().trim().equals("PMP Conversion Maintenance and History"), "PMP Conversion Maintenance and History panel not found");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:ProvPmpConversionHistoryPanel:ProvPmpConversionHistoryBean_DataPanel']/thead/tr/td/table/tbody/tr/td/h3")).getText().trim().equals("PMP Conversion Selection Criteria"), "PMP Conversion Selection Criteria panel not found");
	    new Select(driver.findElement(By.xpath("//select[contains(@id,'ConversionType')]"))).selectByVisibleText("PCC to PCC");
	    //change below code to select PCCP from provider search instead of hardcoding
	    driver.findElement(By.xpath("//input[contains(@id,'PmpId')]")).clear();
	    driver.findElement(By.xpath("//input[contains(@id,'PmpId')]")).sendKeys("110000034");
	    driver.findElement(By.xpath("//input[contains(@id,'SvcLoc')]")).clear();
	    driver.findElement(By.xpath("//input[contains(@id,'SvcLoc')]")).sendKeys("F");
	    driver.findElement(By.xpath("//input[contains(@id,'PreviousConversionMonthsNumber')]")).clear();
	    driver.findElement(By.xpath("//input[contains(@id,'PreviousConversionMonthsNumber')]")).sendKeys("1");
	    driver.findElement(By.xpath("//input[contains(@id,'FileTransferDate')]")).clear();
	    driver.findElement(By.xpath("//input[contains(@id,'FileTransferDate')]")).sendKeys(Common.convertSysdate());
	    driver.findElement(By.xpath("//input[contains(@id,'MailedDate')]")).clear();
	    driver.findElement(By.xpath("//input[contains(@id,'MailedDate')]")).sendKeys(Common.convertSysdatecustom(2));
	    driver.findElement(By.xpath("//input[contains(@id,'ConversionEffectiveDate')]")).clear();
	    driver.findElement(By.xpath("//input[contains(@id,'ConversionEffectiveDate')]")).sendKeys(Common.convertSysdate());
	    new Select(driver.findElement(By.xpath("//select[contains(@id,'DisenrollmentReasonCodeList')]"))).selectByValue("08");
	    Common.saveAll();
	    log("PMP Conversion Maintenance and History was added");
	    
	    Common.cancelAll();
	    //ADD PMP Capitation History
		driver.findElement(By.linkText("PMP")).click();
		driver.findElement(By.linkText("PCCP")).click(); //This selects the first record that you just created
		driver.findElement(By.linkText("PMP Capitation History")).click();
	    Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[3]/table/thead/tr/th/table/tbody/tr/td/h2")).getText().trim().equals("PMP Capitation History"), "PMP Capitation History panel not found");
	    new Select(driver.findElement(By.xpath("//select[contains(@id,'CapCategoryList')]"))).selectByVisibleText("MST MCO_ESSENTIAL");
	    driver.findElement(By.xpath("//input[contains(@id,'CapitationDate')]")).sendKeys(Common.convertSysdate().substring(0, 2)+Common.convertSysdate().substring(5));
	    Common.multiElements("//input[@class='buttonImage' and @alt='Search']").click();
	    Assert.assertTrue(driver.findElement(By.cssSelector("span.redreg")).getText().trim().equals("***No records found***"), "***No records found*** message not found");
	    Common.cancelAll();
	    log("PMP Capitation History panel fields were tested. No record was added.");

	}
	
    @Test
	public void test23481a() throws Exception {
		TestNGCustom.TCNo="23481a";
    	log("//TC 23481a");

		//Reset rel entity Address in Base
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelationshipEntity")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntitySearchBean_CriteriaPanel:ProvRelationshipEntitySearchCrit_ID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntitySearchBean_CriteriaPanel:ProvRelationshipEntitySearchCrit_ID")).sendKeys(relEntityPid);
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntitySearchResults_0:_id17")).click();
	    //Select Loc Name Address
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityNavigatorPanel:relationshipentitynavigator:ITM_n10601")).click();
	    //Click on DBA address
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:ProviderRelationshipEntityAddressList_1:ProviderRelationshipEntityAddressBean_ColValue_prAddrCode_addressUsageDescription")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Maintain Address']")).click();
		//Enter the provider address
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:address1ID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:address1ID")).sendKeys("133 MAPLE STREET");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:cityID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:cityID")).sendKeys("SPRINGFIELD");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:zipID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:zipID")).sendKeys("01105");
	    Common.update();
		Common.save();

		//Go to portal
		String provList="td.leftPadding.leftAlign > span";
		Common.portalLogin();
		driver.findElement(By.linkText("Manage Provider Information")).click();
		driver.findElement(By.linkText("Business Partners (non Provider)")).click();
		driver.findElement(By.linkText("Update Business Partner Profile")).click();
	    Assert.assertTrue(driver.findElement(By.cssSelector(provList)).getText().equals("Please select the Relationship Entity you wish to update."));
		int i=0;
		int j=0;
		int a=1;
		int b=2;

		String pName = "SMU ASSOCIATES";
		for (;a<b; a++){
			for (;i<=j; i++){
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				if ((driver.findElements(By.xpath("//*[contains(@id,'"+i+":name')]")).size())>0) { 
					if (pName.equals(driver.findElement(By.xpath("//*[contains(@id,'"+i+":name')]")).getText())) { 
						driver.findElement(By.xpath("//*[contains(@id,'"+i+":name')]")).click();
							refound=true;
					}
					else
						j=j+1;
				}
			}
			if ((!refound) && ((driver.findElements(By.xpath("//*[contains(@id,'providerList:paginatoridx"+b+"')]")).size())>0)) {
				driver.findElement(By.xpath("//*[contains(@id,'providerList:paginatoridx"+b+"')]")).click();
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			    Assert.assertTrue(driver.findElement(By.cssSelector(provList)).getText().equals("Please select the Relationship Entity you wish to update."));
				b=b+1;
				i=i-1;
			}
		}
		if (!refound)
			log("I couldnt find relationship entity, exiting now");
		
		//Perform the profile update
		
	    //Change address
		driver.findElement(By.xpath("//input[contains(@id,'line1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'line1')]")).sendKeys("101 FEDERAL STREET");
		driver.findElement(By.xpath("//input[contains(@id,'city')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'city')]")).sendKeys("BOSTON");
		driver.findElement(By.xpath("//input[contains(@id,'zip')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'zip')]")).sendKeys("02110");
		driver.findElement(By.xpath("//input[contains(@id,'email')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'email')]")).sendKeys("ansh90@gmail.com");
	    
	    //Just to prevent any errors, enter Info address email as well- this goes away at data refresh so need to add
		driver.findElement(By.xpath("//input[contains(@id,'infoAddressEmail')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'infoAddressEmail')]")).sendKeys("ansh90@gmail.com");
	    
	    driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Submit']")).click();
	    Common.getPageError("//table[contains(@id,'infoNPForm:updateBusinessPartnerProfile_2_id0')]/tbody/tr/td[2]/div/div[2]/table[2]/tbody/tr");
		Common.portalLogout();

	}
      
    //Old code with workflow
//    @Test
//	public void test23481b() throws Exception {
//		TestNGCustom.TCNo="23481b";
//    	log("//TC 23481b");
//	
//		//Navigate to profile update npanel
//	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateWorklist")).click();
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklist_DropDownQueueType"))).selectByIndex(2);
//	    Common.search();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:_id40")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:_id40")).click();
//	    
//	    //Check image
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults_0:_id20")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList:ProviderImagesSearchResultBean_SortColHeader_receiptDate")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList:ProviderImagesSearchResultBean_SortColHeader_receiptDate")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList_0:_id105")).click();
//
//	    //Click on WI
//	    driver.findElement(By.id("workList[0]")).click();
//		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Review']")).click();
//		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:ProviderRelationshipEntityAddressList_1:ProviderRelationshipEntityAddressBean_ColValue_status")).getText().equals("Updated"), "The DBA adddress did not come as updated"); 
//		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:ProviderRelationshipEntityAddressList_1:ProviderRelationshipEntityAddressBean_ColValue_status")).click();
//		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:address1ID")).getAttribute("value").equals("101 FEDERAL STREET"), "The adddress1 did not come as updated"); 
//		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:cityID")).getAttribute("value").equals("BOSTON"), "The city did not come as updated"); 
//		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:zipID")).getAttribute("value").equals("02110"), "The zip did not come as updated"); 
//	    driver.findElement(By.id("ProviderRelationshipEntityAddressPanel_cancelAction_btn")).click();
//
//		//Change status to IN PROCESS
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderworkflowProfileUpdateREInfoStatusPanel:WorkflowStatusdropdown"))).selectByVisibleText("IN PROCESS");
//	    Common.saveAll();
////	    df30872_saveAll(); //remove after DF 20872 is fixed, and uncomment above line of Common.saveAll()
//	    
//	    //Select The WI again from PROFILE UPDATE queue
//	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateWorklist")).click();
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklist_DropDownQueueType"))).selectByIndex(3);
//	    Common.search();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:_id40")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:_id40")).click();
//	    //Click on WI
//	    driver.findElement(By.id("workList[0]")).click();
//		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Review']")).click();
//		//Change status to COMPLETE
//	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderworkflowProfileUpdateREInfoStatusPanel:WorkflowStatusdropdown"))).selectByVisibleText("COMPLETE");
//	    Common.saveAll();
////	    df30872_saveAll(); //remove after DF 20872 is fixed, and uncomment above line of Common.saveAll()
//	    
//		//Verify rel entity address is changed
//		String idProvider="110076470";
//		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelationshipEntity")).click();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntitySearchBean_CriteriaPanel:ProvRelationshipEntitySearchCrit_ID")).clear();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntitySearchBean_CriteriaPanel:ProvRelationshipEntitySearchCrit_ID")).sendKeys(idProvider);
//	    Common.search();
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntitySearchResults_0:_id17")).click();
//	    //Select Loc Name Address
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityNavigatorPanel:relationshipentitynavigator:ITM_n10601")).click();
//	    //Click on DBA address
//	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:ProviderRelationshipEntityAddressList_1:ProviderRelationshipEntityAddressBean_ColValue_prAddrCode_addressUsageDescription")).click();
//		//Verify provider address
//		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:address1ID")).getAttribute("value").equals("101 FEDERAL STREET"), "The adddress1 update failed"); 
//		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:cityID")).getAttribute("value").equals("BOSTON"), "The city update failed"); 
//		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:zipID")).getAttribute("value").equals("02110"), "The zip update failed"); 
//		Common.cancelAll();
//		
//	}
    
    @Test
	public void test23481b() throws Exception {
		TestNGCustom.TCNo="23481b";
    	log("//TC 23481b");
	
    	//Navigate to profile update panel
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateSearchBean_CriteriaPanel:ProfileUpdateSearchPanel_providerId")).sendKeys(relEntityPid);
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateSearchBean_CriteriaPanel:ProfileUpdateSearchResultDataPanel_StatusList"))).selectByVisibleText("READY FOR REVIEW");
    	Common.search();
	    //sort by date received desc
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
	    
		//select first row checkbox
		driver.findElement(By.xpath("//*[@id='rowCheck[0]']")).click();
		
		//click on Review button
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Review']")).click();
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:ProviderRelationshipEntityAddressList_1:ProviderRelationshipEntityAddressBean_ColValue_status")).getText().equals("Updated"), "The DBA adddress did not come as updated"); 
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:ProviderRelationshipEntityAddressList_1:ProviderRelationshipEntityAddressBean_ColValue_status")).click();
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:address1ID")).getAttribute("value").equals("101 FEDERAL STREET"), "The adddress1 did not come as updated"); 
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:cityID")).getAttribute("value").equals("BOSTON"), "The city did not come as updated"); 
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:zipID")).getAttribute("value").equals("02110"), "The zip did not come as updated"); 
	    driver.findElement(By.id("ProviderRelationshipEntityAddressPanel_cancelAction_btn")).click();

		//Change status to IN PROCESS
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderworkflowProfileUpdateREInfoStatusPanel:WorkflowStatusdropdown"))).selectByVisibleText("IN PROCESS");
	    Common.saveAll();
//	    df30872_saveAll(); //remove after DF 20872 is fixed, and uncomment above line of Common.saveAll()
	    
	    //Select The WI again from PROFILE UPDATE queue
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateSearchBean_CriteriaPanel:ProfileUpdateSearchPanel_providerId")).sendKeys(relEntityPid);
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateSearchBean_CriteriaPanel:ProfileUpdateSearchResultDataPanel_StatusList"))).selectByVisibleText("IN PROCESS");
    	Common.search();
	    //sort by date received desc
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
		//select first row checkbox
		driver.findElement(By.xpath("//*[@id='rowCheck[0]']")).click();
		
		//click on Review button
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Review']")).click();
		//Change status to COMPLETE
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderworkflowProfileUpdateREInfoStatusPanel:WorkflowStatusdropdown"))).selectByVisibleText("COMPLETE");
	    Common.saveAll();
//	    df30872_saveAll(); //remove after DF 20872 is fixed, and uncomment above line of Common.saveAll()
	    
		//Verify rel entity address is changed
		String idProvider="110076470";
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelationshipEntity")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntitySearchBean_CriteriaPanel:ProvRelationshipEntitySearchCrit_ID")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntitySearchBean_CriteriaPanel:ProvRelationshipEntitySearchCrit_ID")).sendKeys(idProvider);
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntitySearchResults_0:_id17")).click();
	    //Select Loc Name Address
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityNavigatorPanel:relationshipentitynavigator:ITM_n10601")).click();
	    //Click on DBA address
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:ProviderRelationshipEntityAddressList_1:ProviderRelationshipEntityAddressBean_ColValue_prAddrCode_addressUsageDescription")).click();
		//Verify provider address
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:address1ID")).getAttribute("value").trim().equals("101 FEDERAL STREET"), "The adddress1 update failed"); 
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:cityID")).getAttribute("value").trim().equals("BOSTON"), "The city update failed"); 
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRelationshipEntityAddressPanel:zipID")).getAttribute("value").trim().equals("02110"), "The zip update failed"); 
		Common.cancelAll();
		
	    //Check image
		//Select The WI again from PROFILE UPDATE panel
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateSearch")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateSearchBean_CriteriaPanel:ProfileUpdateSearchPanel_providerId")).sendKeys(relEntityPid);
	    Common.search();
	    
	    //sort by date received desc
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss:_id28']/span")).click();

		
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProfileUpdateSearchResultss_0:_id15']")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList:ProviderImagesSearchResultBean_ColHeader_receiptDate")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList:ProviderImagesSearchResultBean_ColHeader_receiptDate")).click();
		Assert.assertTrue(driver.findElement(By.xpath("//*[contains(@id,'ProviderImagesWorkflowSearchResultPanel') and contains(@id,'0:') and contains(@id,'receiptDate') ]")).getText().equals(Common.convertSysdate()), "The profile update image is not in todays date"); 
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList_0:_id80']")).click();

		
	}
    
    
    @Test
	public void test22928a() throws Exception {
		TestNGCustom.TCNo="22928a";
    	log("//TC "+TestNGCustom.TCNo);
    	
    	String recredProvSSN = "013686499";
    	String recredProvName = "SMITH JONATHAN";

    	
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Admin")).click();
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_imaging")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImagingNavigatorPanel:ImagingMaintenance_id:GRP_provider")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImagingNavigatorPanel:ImagingMaintenance_id:ITM_n109")).click();
    	
    	//create WI for image
    	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocumentDataPanel_DocumentType"))).selectByVisibleText("PEC - RECREDENTIALING APPLICATION");
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocument_ReceiptDate")).sendKeys(Common.convertSysdate());
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocument_UploadFileName")).sendKeys(System.getProperty("user.dir")+"\\testngMO.xml");
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocumentDataPanel_ImageProviderDocumentTaxID")).clear();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocumentDataPanel_ImageProviderDocumentTaxID")).sendKeys(recredProvSSN);
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:ImageProviderDocumentDataPanel_ImageProviderDocumentTaxID_CMD_SEARCH")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:ImageProviderDocumentPanel:_id285:ImageProviderDocumentSearchResults_0:column1Value")).click();
    	Common.saveAll();
    	
    	//check WI and image in recred worklist unassigned queue
    	Thread.sleep(10000);
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Provider")).click();
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RecredWorklist")).click();
    	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklist_DropDownQueueType"))).selectByIndex(2);
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklistResults:_id40")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklistResults:_id40")).click();
	    
	    //Check that only 1 WI is created in the recred worklist queue for the given date- We do this by checking the date on the second WI 
	    //First make sure there is more than 1 WI
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
	    if (driver.findElements(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklistResults:tbody_element']/tr[2]/td[5]")).size()>0) {
	    	Assert.assertTrue(!(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklistResults:tbody_element']/tr[2]/td[5]")).getText().equals(Common.convertSysdate())), "There was more than one WI found in today's date in the recred worklist. Please check manually and do tc manually"); 
	    }
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		//Check that the first WI is for our provider id and for today's date
    	Assert.assertTrue(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklistResults:tbody_element']/tr/td[5]")).getText().equals(Common.convertSysdate()), "No WI found in today's date in recred worklist"); 
    	Assert.assertTrue(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklistResults:tbody_element']/tr/td[7]")).getText().equals(recredProvID), "WI not found for our recred provider"); 

    	//Check that no WI is created in the profile update worklist
    	Common.resetBase();
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Provider")).click();
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateWorklist")).click();
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:_id40")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:_id40")).click();
	  
	    //Count the no. of WIs on the page using date as target
	    int count =  driver.findElements(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:tbody_element']/*/td[5]")).size();
	    
	    //check the last WI's date, if its today's date, perform the test manually
	    if (count>1)
	    	Assert.assertTrue(!(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:tbody_element']/tr["+count+"]/td[5]")).getText().equals(Common.convertSysdate())), "There were lots of WIs found in today's date in the profile update worklist. Please check manually and do tc manually"); 

	    //Make sure our recred provider has not created a WI
    	Assert.assertTrue(!(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:tbody_element']/tr/td[8]")).getText().equals(recredProvID)), "Recred provider ID was found in profile update worklist"); 
	    if (count>1) {
		    for (int i=2;i<=count; i++)
		    	Assert.assertTrue(!(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:tbody_element']/tr["+count+"]/td[8]")).getText().equals(recredProvID)), "Recred provider ID was found in profile update worklist"); 
	    }

	    //Log on to portal and perform profile update
		String provList="td.leftPadding.leftAlign > span";
	    Common.resetPortal();
		driver.findElement(By.linkText("Manage Provider Information")).click();
		driver.findElement(By.linkText("Maintain Profile")).click();
		driver.findElement(By.linkText("Update Your MassHealth Profile")).click();
	    Assert.assertTrue(driver.findElement(By.cssSelector(provList)).getText().equals("Please select the provider you wish to update."));
		//driver.wait(5000);
		int i=0;
		int j=0;
		int a=1;
		int b=2;

		String pName = recredProvName;
		for (;a<b; a++){
			for (;i<=j; i++){
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				if ((driver.findElements(By.xpath("//*[contains(@id,'"+i+":name')]")).size())>0) { 
					if (pName.equals(driver.findElement(By.xpath("//*[contains(@id,'"+i+":name')]")).getText())) { 
						driver.findElement(By.xpath("//*[contains(@id,'"+i+":name')]")).click();
							test22928foundit=true;
					}
					else
						j=j+1;
				}
			}
			if ((!test22928foundit) && ((driver.findElements(By.xpath("//*[contains(@id,'providerList:paginatoridx"+b+"')]")).size())>0)) {
				driver.findElement(By.xpath("//*[contains(@id,'providerList:paginatoridx"+b+"')]")).click();
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			    Assert.assertTrue(driver.findElement(By.cssSelector(provList)).getText().equals("Please select the provider you wish to update."));
				b=b+1;
				i=i-1;
			}
		}
		if (!test22928foundit)
			System.out.println("I couldnt find prov, exiting now");
	    
		//Perform the profile update
		//Enter legal entity data
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityContact')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityContact')]")).sendKeys("me");
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_line1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_line1')]")).sendKeys("133 MAPLE STREET");
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_city')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_city')]")).sendKeys("SPRINGFIELD");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'legalEntityAddress_state')]"))).selectByVisibleText("Massachusetts");
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_zip')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_zip')]")).sendKeys("01105");
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_email')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_email')]")).sendKeys("ansh90@gmail.com");
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_phoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'legalEntity_legalEntityAddress_phoneNumber')]")).sendKeys("4137329201");
	    
	    //Change address
		driver.findElement(By.xpath("//*[contains(@id,'MENUITEM_provPrflAddressType')]")).click();
		driver.findElement(By.xpath("//*[contains(@id,'0:dbaName')]")).click();
		driver.findElement(By.linkText("Remittance Address")).click();
		driver.findElement(By.xpath("//input[contains(@id,'contact')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'contact')]")).sendKeys(recredProvName);
		driver.findElement(By.xpath("//input[contains(@id,'address_line1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'address_line1')]")).sendKeys("101 FEDERAL STREET");
		driver.findElement(By.xpath("//input[contains(@id,'address_city')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'address_city')]")).sendKeys("BOSTON");
		new Select(driver.findElement(By.xpath("//select[contains(@id,'address_state')]"))).selectByVisibleText("Massachusetts");
		driver.findElement(By.xpath("//input[contains(@id,'address_zip')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'address_zip')]")).sendKeys("02110");
		driver.findElement(By.xpath("//input[contains(@id,'address_email')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'address_email')]")).sendKeys("ansh90@gmail.com");
		driver.findElement(By.xpath("//input[contains(@id,'address_phoneNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'address_phoneNumber')]")).sendKeys("41"+Common.convertDatetoInt(Common.convertSysdate())); //Make the last 8 digits of the phone no. to be todays date. This is done to verify that image is produced correctly for today's date.
		driver.findElement(By.xpath("//input[@class='buttonFunctional' and @alt='Update']")).click();

		//Submit the profile update
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Submit']")).click();
		driver.findElement(By.xpath("//input[contains(@id,'agreementAcceptanceIndicator')]")).click();
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Submit']")).click();
    	
	}	
    
    @Test
	public void test22928b() throws Exception {
		TestNGCustom.TCNo="22928b";
    	log("//TC "+TestNGCustom.TCNo);
		if (!test22928foundit) 
			Assert.assertTrue(false, "Not continuing with Profile recred test as it failed on portal for address change or failed on Base");

		else {
			//check WI and image in recred worklist unassigned queue
		    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RecredWorklist")).click();
	    	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklist_DropDownQueueType"))).selectByIndex(2);
		    Common.search();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklistResults:_id40")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklistResults:_id40")).click();
		    
		    //Check that only 1 WI is created in the recred worklist queue for the given date- We do this by checking the date on the second WI 
		    //First make sure there is more than 1 WI
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		    if (driver.findElements(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklistResults:tbody_element']/tr[2]/td[5]")).size()>0) {
		    	Assert.assertTrue(!(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklistResults:tbody_element']/tr[2]/td[5]")).getText().equals(Common.convertSysdate())), "There was more than one WI found in today's date in the recred worklist. Please check manually and do tc manually"); 
		    }
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

			//Check that the first WI is for our provider id and for today's date
	    	Assert.assertTrue(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklistResults:tbody_element']/tr/td[5]")).getText().equals(Common.convertSysdate()), "No WI found in today's date in recred worklist"); 
	    	Assert.assertTrue(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklistResults:tbody_element']/tr/td[7]")).getText().equals(recredProvID), "WI not found for our recred provider"); 

	    	//Get Images
		    driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderRecredWorkflowWorklistResults_0:_id20")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList:ProviderImagesSearchResultBean_ColHeader_receiptDate")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList:ProviderImagesSearchResultBean_ColHeader_receiptDate")).click();

		    //Make sure the first two dates are today's date
			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList_0:ProviderImagesSearchResultBean_ColValue_receiptDate")).getText().equals(Common.convertSysdate()), "The first image is not in today's date"); 
			Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList_1:ProviderImagesSearchResultBean_ColValue_receiptDate")).getText().equals(Common.convertSysdate()), "The second image is not in today's date"); 
			
			//Make sure the third image (if present) is not in today's date
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		    if (driver.findElements(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList_2:ProviderImagesSearchResultBean_ColValue_receiptDate")).size()>0) {
		    	Assert.assertTrue(!(driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList_2:ProviderImagesSearchResultBean_ColValue_receiptDate")).getText().equals(Common.convertSysdate())), "There were more than 2 images found in today's date in the recred worklist image list. Please check manually and do tc manually"); 
		    }
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			
			//Download the two Images
		    driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList_0:_id105")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:RecredWorkflowWorklistPanel:ProviderImagesWorkflowSearchResultPanel:ProviderImagesSearchResultList_1:_id105")).click();

	    	//Check that no WI is created in the profile update worklist
	    	Common.resetBase();
	    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Provider")).click();
	    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ProfileUpdateWorklist")).click();
		    Common.search();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:_id40")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:_id40")).click();
		  
		    //Count the no. of WIs on the page using date as target
		    int count =  driver.findElements(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:tbody_element']/*/td[5]")).size();
		    
		    //check the last WI's date, if its today's date, perform the test manually
		    if (count>1)
		    	Assert.assertTrue(!(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:tbody_element']/tr["+count+"]/td[5]")).getText().equals(Common.convertSysdate())), "There were lots of WIs found in today's date in the profile update worklist. Please check manually and do tc manually"); 

		    //Make sure our recred provider has not created a WI
	    	Assert.assertTrue(!(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:tbody_element']/tr/td[8]")).getText().equals(recredProvID)), "Recred provider ID was found in profile update worklist"); 
		    if (count>1) {
			    for (int i=2;i<=count; i++)
			    	Assert.assertTrue(!(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:ProfileUpdateWorkflowWorklistPanel:ProviderProfileUpdateWorkflowWorklistResults:tbody_element']/tr["+count+"]/td[8]")).getText().equals(recredProvID)), "Recred provider ID was found in profile update worklist"); 
		    }
		}
    	
    	
    }
    
	@Test
	public void test35703() throws Exception {
		TestNGCustom.TCNo="35703";
    	log("//TC 35703");
    	
    	//Physician Hospital Affilialtion
    	
    	//Get Hospital provider
    	sqlStatement="select p.id_provider from t_pr_prov p, t_pr_type t "+
    			"where p.sak_prov = t.sak_prov "+
    			"and t.cde_prov_type = 70 "+
    			"and t.cde_service_loc='A' "+
    			"and p.sak_prov not in (select sak_prov from T_PR_AFF_PR_LOC_XREF) "+
    			"and p.sak_prov not in (select sak_prov from T_PR_GRP_MBR g) "+
    			"and rownum<2";
    	colNames.add("ID_PROVIDER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String hospID=colValues.get(0);
    	log("Hospital ID is "+hospID);
    	
    	//Get physician provider
    	sqlStatement="select p.id_provider from t_pr_prov p, t_pr_type t "+
    			"where p.sak_prov = t.sak_prov "+
    			"and t.cde_prov_type = 01 "+
    			"and t.cde_service_loc='A' "+
    			"and p.sak_prov not in (select sak_prov from T_PR_AFF_PR_LOC_XREF) "+
    			"and p.sak_prov not in (select sak_prov from T_PR_GRP_MBR g) "+
    			"and rownum<2";
    	colNames.add("ID_PROVIDER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String physID=colValues.get(0);
    	log("Physician ID is "+physID);
    	
    	//Search for physician
    	searchProv(physID);
		driver.findElement(By.linkText("Affiliations")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	    driver.findElement(By.xpath("//*[contains(@alt,'Provider ID/Loc pop-up search')]")).click();
	    driver.findElement(By.xpath("//input[contains(@id,'LevelofCareSearchCriteriaPanel:ProviderID')]")).sendKeys(hospID);
	    driver.findElement(By.xpath("//*[contains(@id,'LevelofCareSearchCriteriaPanel:SEARCH')]")).click();
		driver.findElement(By.linkText(hospID)).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderAffiliationsPanel:ProviderAffiliationsDataPanel_ProviderAffiliationType"))).selectByVisibleText("HOSPITAL/HLHC OR CHC");
	    driver.findElement(By.xpath("//input[contains(@id,'ProviderAffiliationsDataPanel_EffectiveDate')]")).sendKeys(Common.convertSysdate());
	    driver.findElement(By.xpath("//input[contains(@id,'ProviderAffiliationsDataPanel_EndDate')]")).sendKeys("12/31/2299");
	    Common.save();
	    
	    //Check Affiliation added to Hospital Provider
	    LoginCheck();
	    searchProv(hospID);
		driver.findElement(By.linkText("Affiliations")).click();
		String affType = driver.findElement(By.xpath("//*[contains(@id,'0:ProviderAffiliationsBean_ColValue_affiliationTypeDescription')]")).getText();
		String affProv = driver.findElement(By.xpath("//*[contains(@id,'0:ProviderAffiliationsBean_ColValue_providerIDLoc')]")).getText();
		String effDt = driver.findElement(By.xpath("//*[contains(@id,'0:ProviderAffiliationsBean_ColValue_effectiveDate')]")).getText();
		String endDt = driver.findElement(By.xpath("//*[contains(@id,'0:ProviderAffiliationsBean_ColValue_endDate')]")).getText();
		
		Assert.assertTrue(affType.equals("HOSPITAL/HLHC OR CHC"), "Affiliation type is not HOSPITAL/HLHC OR CHC"); 
		Assert.assertTrue(affProv.equals(physID+" / A"), "Affiliation provider is not "+physID+" / A"); 
		Assert.assertTrue(effDt.equals(Common.convertSysdate()), "Affiliation Effective dt is not "+Common.convertSysdate()); 
		Assert.assertTrue(endDt.equals("12/31/2299"), "Affiliation End dt is not 12/31/2299"); 
		
		log("Successfully tested Physician to hospital affiliation add");

	    //Update Affiliation end date
		driver.findElement(By.xpath("//*[contains(@id,'0:ProviderAffiliationsBean_ColValue_affiliationName')]")).click();
		driver.findElement(By.xpath("//input[contains(@id,'ProviderAffiliationsDataPanel_EndDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'ProviderAffiliationsDataPanel_EndDate')]")).sendKeys(Common.convertSysdatecustom(1));
	    Common.save();

	    //Check Affiliation end date is updated to physician
	    LoginCheck();
	    searchProv(physID);
		driver.findElement(By.linkText("Affiliations")).click();
		endDt = driver.findElement(By.xpath("//*[contains(@id,'0:ProviderAffiliationsBean_ColValue_endDate')]")).getText();
		Assert.assertTrue(endDt.equals(Common.convertSysdatecustom(1)), "Affiliation Effective dt is not "+Common.convertSysdatecustom(1));  
		log("Successfully tested Physician to hospital affiliation update");

	}
	
	@Test
	public void test39137() throws Exception {
		TestNGCustom.TCNo="39137";
    	log("//TC 39137");
    	
    	//Test 1 - Make sure RA is generated after claim is submitted for a provider 
    	log("Test 1 - Make sure RA is generated after claim is submitted for a provider");
  
		sqlStatement ="select * from r_day2 where TC = '23101a'";
		colNames.add("ID");
		colNames.add("DATE_REQUESTED");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no icn present generated");
		String icn = colValues.get(0);
		String dte_claim_submitted = colValues.get(1);
		log("ICN for provider 110000148A is "+icn+" submitted on "+dte_claim_submitted);
		
		//Check RA is generated after this date on T_Message
		sqlStatement="select * from "+
				"(select * from t_message order by dte_posted desc)a "+
				"where a.txt_message = 'Remittance Advice' and a.dsc_subject like '%110000148A%'  and rownum < 2";
		colNames.add("DSC_SUBJECT");
		colNames.add("DTE_POSTED");
		colValues = Common.executeQuery(sqlStatement, colNames);
		String dsc_subject = colValues.get(0);
		String dte_ra_posted = colValues.get(1);
		log("RA is "+dsc_subject+" posted on "+dte_ra_posted);
		
		//Validate that dte_ra_posted is after the dte_claim_submitted
		SimpleDateFormat sdf1= new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat sdf2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date clm_date=sdf1.parse(dte_claim_submitted);
		Date ra_date=sdf2.parse(dte_ra_posted);
		Assert.assertTrue(ra_date.after(clm_date), "RA date "+dte_ra_posted+" is not after claim date "+dte_claim_submitted);  
		
		log("Successfully validated that RA date "+dte_ra_posted+" is after claim date "+dte_claim_submitted);
		
    	//Test 2 - Make sure that a new Provider Enrollment is added to T_PR_SVC_LOC
    	log("Test 2 - Make sure that a new Provider Enrollment is added to T_PR_SVC_LOC");
    	
		sqlStatement="select sak_prov, cde_service_loc from "+
				"(select sak_prov, cde_service_loc, cde_status1 from T_PR_APPLN order by dte_last_status desc)a "+
				"where a.cde_status1 = 34  and rownum < 2";
		colNames.add("SAK_PROV");
		colNames.add("CDE_SERVICE_LOC");
		colValues = Common.executeQuery(sqlStatement, colNames);
		String sak_prov = colValues.get(0);
		String cde_service_loc = colValues.get(1);
		
		sqlStatement="select 1 from T_PR_SVC_LOC where sak_prov = "+sak_prov+" and cde_service_loc = '"+cde_service_loc+"'";
		colNames.add("1");
		colValues = Common.executeQuery(sqlStatement, colNames);
		String output = colValues.get(0);
		Assert.assertTrue(output.equals("1"), "New Provider Enrollment with sak_prov "+sak_prov+" and service location "+cde_service_loc+" is not added to T_PR_SVC_LOC"); 
		
		log("Successfully validated that a new Provider Enrollment with sak_prov "+sak_prov+" and service location "+cde_service_loc+" is added to T_PR_SVC_LOC");

    	//Test 3 - Make sure that MGD-0056-D COMMUNITY PARTNER DAILY ENROLLMENT ROSTERï¿½ Report is produced
		log("Test 3 - Make sure that MGD-0056-D COMMUNITY PARTNER DAILY ENROLLMENT ROSTERï¿½ Report is produced");

	    //Validate report
		String command, error, resp;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/mgd0056d.rpt.* | tail -1";
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("COMMUNITY PARTNER DAILY ENROLLMENT ROSTER REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		command = "awk 'c-->0;$0~s{if(b)for(c=b+1;c>1;c--)print r[(NR-c+1)%b];print;c=a}b{r[NR%b]=$0}' b=0 a=0 s=\"MAIN ST\" "+fileName; //. This is to print the next 4 lines after grep on string. "b" and "a" are the number of lines to print before and after string res_mem
		error = "The selected file is not correct/ or it is empty/ or it does not have any Member data with the st address of \"MAIN ST\" in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
		log("Successfully validated that COMMUNITY PARTNER DAILY ENROLLMENT ROSTER report is generated with Member data with the st address of \"MAIN ST\" in it");

	}
	
	// ********************************************************************* 
	//					New Provider TCs - Mahammad Start
	// *********************************************************************
	
	@Test
	public void test44135a() throws Exception {
		
		TestNGCustom.TCNo="44135";
	    log("//TC test44135a");
	    
	    sqlStatement="select * from t_pr_recred_event where cde_recred_event_status='S'";
	    colNames.add("sak_rtn");
	    colValues=Common.executeQuery(sqlStatement, colNames);
	    
	    String RTN=colValues.get(0);
	    
	    driver.findElement(By.xpath("//a[text()='re-credentialing']")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderRecredSearchBean_CriteriaPanel:EXTRABUTTON1")).click();
	    
	    driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderRecredEventSearchBean_CriteriaPanel:ProviderRecredEventSearchDataPanel_SakRtn']")).clear();
	    driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderRecredEventSearchBean_CriteriaPanel:ProviderRecredEventSearchDataPanel_SakRtn']")).sendKeys(RTN);
	    
	    Common.search();
	    
	    driver.findElement(By.id("MMISForm:MMISBodyContent:RecredEventSearchResultsDataTable_0:_id13")).click();
	    driver.findElement(By.xpath("//a[text()='Base Information']")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RecredEventBaseInformationPanel:RecredEventDataPanel_RecredStatusList"))).selectByVisibleText("Released");

		Common.saveAll();
		
	    Common.insertData_day2(TestNGCustom.TCNo, RTN, "RTN", Common.convertDatetoInt(Common.convertSysdate()));

	}
	
	@Test
	public void test44135b() throws Exception {
		
		TestNGCustom.TCNo="44135";
	    log("//TC test44135b");
	    
	    driver.findElement(By.linkText("rpts & letters")).click();
	    driver.findElement(By.linkText("Letters")).click();
	    driver.findElement(By.linkText("Letter Request Search")).click();
	    
	    //select Selected for Recred Batch Letter       
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_LetterList"))).selectByValue("87");
	    Common.search();

		//check letter PRV-9040-R if it is generated
		Assert.assertTrue(driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id48']")).size() > 0, "Did not get expected letter");

	}
	
	@Test
	public void test44118a() throws Exception {
		TestNGCustom.TCNo="44118";
    	log("//TC test44118a");
    	
    	String provIdFrom="110098308";
    	String provIdTo="110111275";
    	
    	String specialityFrom="003";
    	String specialityTo="059";

    	String ZipCodeFrom="02100";
    	String ZipCodeTo="02118";
    	
    	String countyFrom="01";
    	String countyTo="14";
    	
      	String typeFrom="01";
    	String typeTo="01";
    	
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRptsAndLettersNavigatorPanel:ProviderReportsNavigator:ITM_n101")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();

		driver.findElement(By.xpath("//input[contains(@id, 'TypeFromSearch')]")).sendKeys(typeFrom); 
		driver.findElement(By.xpath("//input[contains(@id, 'TypeToSearch')]")).sendKeys(typeTo);  
		driver.findElement(By.xpath("//input[contains(@id, 'SpecialtyForm1')]")).sendKeys(specialityFrom);
		driver.findElement(By.xpath("//input[contains(@id, 'SpecialtyTo1')]")).sendKeys(specialityTo);
		
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'CdeCntyFrom')]"))).selectByValue(countyFrom);
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'cdeCntyTo')]"))).selectByValue(countyTo);
		
		driver.findElement(By.xpath("//input[contains(@id, 'ZipCodeFrom')]")).sendKeys(ZipCodeFrom);
		driver.findElement(By.xpath("//input[contains(@id, 'ZipCodeTo')]")).sendKeys(ZipCodeTo);
		driver.findElement(By.xpath("//input[contains(@id, 'ProviderFromID')]")).sendKeys(provIdFrom); 
		driver.findElement(By.xpath("//input[contains(@id, 'ProviderToID')]")).sendKeys(provIdTo); 
		driver.findElement(By.xpath("//input[contains(@id, 'ServiceLocationCode')]")).sendKeys("A");
		
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'PrEnrollPgm')]"))).selectByVisibleText("ORDERING & REFERRING");
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'GroupIndivIndicator')]"))).selectByValue("I"); //individual

	    Common.save();
	    
	    //add data for day2
	    Common.insertData_day2(TestNGCustom.TCNo, provIdFrom, "provider ID from", Common.convertDatetoInt(Common.convertSysdate()));
	    Common.insertData_day2(TestNGCustom.TCNo, provIdTo, "provider ID to", Common.convertDatetoInt(Common.convertSysdate()));
	   
	    Common.insertData_day2(TestNGCustom.TCNo, specialityFrom, "speciality From", Common.convertDatetoInt(Common.convertSysdate()));
	    Common.insertData_day2(TestNGCustom.TCNo, specialityTo, "speciality To", Common.convertDatetoInt(Common.convertSysdate()));
	    
	    Common.insertData_day2(TestNGCustom.TCNo, ZipCodeFrom, "zipCode From", Common.convertDatetoInt(Common.convertSysdate()));
	    Common.insertData_day2(TestNGCustom.TCNo, ZipCodeTo, "zipCode To", Common.convertDatetoInt(Common.convertSysdate()));
	    
	    Common.insertData_day2(TestNGCustom.TCNo, countyFrom, "county From", Common.convertDatetoInt(Common.convertSysdate()));
	    Common.insertData_day2(TestNGCustom.TCNo, countyTo, "county To", Common.convertDatetoInt(Common.convertSysdate()));    
	    
	    Common.insertData_day2(TestNGCustom.TCNo, typeFrom, "type From", Common.convertDatetoInt(Common.convertSysdate()));
	    Common.insertData_day2(TestNGCustom.TCNo, typeTo, "type To", Common.convertDatetoInt(Common.convertSysdate()));

    	
	}
	
	
	@Test
	public void test44118b() throws Exception {
		
		TestNGCustom.TCNo="44118";
    	log("//TC test44118b");
    	
    	String specialityFrom=Common.getDataRday2_new(TestNGCustom.TCNo, "speciality From").get(0);
    	String specialityTo=Common.getDataRday2_new(TestNGCustom.TCNo, "speciality To").get(0);

    	String ZipCodeFrom=Common.getDataRday2_new(TestNGCustom.TCNo, "zipCode From").get(0);
    	String ZipCodeTo=Common.getDataRday2_new(TestNGCustom.TCNo, "zipCode To").get(0);
    	
    	String countyFrom=Common.getDataRday2_new(TestNGCustom.TCNo, "county From").get(0);
    	String countyTo=Common.getDataRday2_new(TestNGCustom.TCNo, "county To").get(0);
    	
      	String typeFrom=Common.getDataRday2_new(TestNGCustom.TCNo, "type From").get(0);
    	String typeTo=Common.getDataRday2_new(TestNGCustom.TCNo, "type To").get(0);
    	
    	//Verify unix report
		String command, error;
		
		//validate first file prd01001
		
		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/prd01101.rpt.* | tail -1"; //grabs the latest gdg of the file

		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The PROVIDER LISTING BY FLEXIBLE CRITERIA Report filename is: "+fileName);

		//Verify data in file
		command = "grep 'FROM   "+typeFrom+"       PHYSICIAN' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep 'TO   "+typeTo+"       PHYSICIAN' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep 'FROM   "+specialityFrom+"      Aerospace Medicine' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep 'TO   "+specialityTo+"      Pediatric, Cardiology' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep 'FROM   "+countyFrom+"       Barnstable' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep 'TO   "+countyTo+"       Worcester' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep 'ST' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep 'BOSTON, MA' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		//validate second file prd01002
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/prd01102.rpt.* | tail -1"; //grabs the latest gdg of the file

		error = "There was no report file found";
	    fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The PROVIDER LISTING BY FLEXIBLE CRITERIA statistics Report filename is: "+fileName);
		
		command = "grep '"+countyFrom+"' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep '"+specialityFrom+"' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep '"+specialityTo+"' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep '"+countyTo+"' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep '"+ZipCodeFrom+"' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep '"+ZipCodeTo+"' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
    	
	}

	@Test
	public void test44121() throws Exception {
		
		TestNGCustom.TCNo="test44121";
	    log("//TC test44121");
	    	
	    //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/prr01501.rpt.* | tail -1"; //grabs the latest gdg of the file

		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The PROVIDER CROSS REFERENCE filename is: "+fileName);

		//Verify data in file 
		command = "grep '110128219 A' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		
		//change the string name
		String output = Common.connectUNIX(command, error);
		System.out.println(output);
		log(output);

		sqlStatement="select sak_prov from T_PR_PROV where id_provider='110128219'";
		colNames.add("sak_prov");
		colValues=Common.executeQuery(sqlStatement, colNames);

		String sak_prov=colValues.get(0);
		
		sqlStatement="select num_prov_lic, cde_service_loc from t_pr_type where sak_prov='"+sak_prov+"'";
		colNames.add("num_prov_lic");
		colNames.add("cde_service_loc");

		colValues=Common.executeQuery(sqlStatement, colNames);

		String provLicenseNum=colValues.get(0);
		
		sqlStatement="select num_ssn, name, cde_lic_type from T_PR_HB_LIC where num_prov_lic='"+provLicenseNum+"'";
		colNames.add("num_ssn");
		colNames.add("name");
		colNames.add("cde_lic_type");

		colValues=Common.executeQuery(sqlStatement, colNames);

		String ssn=colValues.get(0);
		String name=colValues.get(1);
		String provLicenceType=colValues.get(2);
		System.out.println("ahsh " + name+"8");

		System.out.println("vaxjgx " + name.substring(name.indexOf(" ")) + name.substring(0, name.indexOf(" ")));
		
		Assert.assertTrue(output.contains(provLicenseNum), provLicenseNum +" was not found in file.");
		Assert.assertTrue(output.contains(provLicenceType), provLicenceType +" was not found in file.");
		Assert.assertTrue(output.contains(ssn), ssn +" was not found in file.");
		Assert.assertTrue(output.contains(name.substring(name.indexOf(" ")) +" "+ name.substring(0, name.indexOf(" "))), name.substring(name.indexOf(" ")) +" "+ name.substring(0, name.indexOf(" ")) +" was not found in file.");

	}
	
	
	@Test
	public void test44122() throws Exception {
		
		TestNGCustom.TCNo="test44122";
	    log("//TC test44122");
	    	
	    //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/prr01601.rpt.* | tail -1"; //grabs the latest gdg of the file

		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The PROVIDER CROSS REFERENCE filename is: "+fileName);
		
		//Verify data in file
		command = "grep '110010172  A' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
				
		String output = Common.connectUNIX(command, error);
		System.out.println(output);
		log(output);
		
		sqlStatement="select sak_prov from T_PR_PROV where id_provider='110010172'";
		colNames.add("sak_prov");
		colValues=Common.executeQuery(sqlStatement, colNames);

		String sak_prov=colValues.get(0);
		
		sqlStatement="select num_prov_lic, cde_service_loc from t_pr_type where sak_prov='"+sak_prov+"'";
		colNames.add("num_prov_lic");
		colNames.add("cde_service_loc");

		colValues=Common.executeQuery(sqlStatement, colNames);

		String provLicenseNum=colValues.get(0);
		
		sqlStatement="select num_ssn, name, cde_lic_type from T_PR_HB_LIC where num_prov_lic='"+provLicenseNum+"'";
		colNames.add("num_ssn");
		colNames.add("name");
		colNames.add("cde_lic_type");

		colValues=Common.executeQuery(sqlStatement, colNames);

		String ssn=colValues.get(0);
		String name=colValues.get(1);
		String provLicenceType=colValues.get(2);
		
		System.out.println(name.substring(name.indexOf(" "), name.lastIndexOf(" ")).trim());
		
		Assert.assertTrue(output.contains(provLicenseNum), provLicenseNum +" was not found in file.");
		Assert.assertTrue(output.contains(provLicenceType), provLicenceType +" was not found in file.");
		Assert.assertTrue(output.contains(ssn), ssn +" was not found in file.");
		Assert.assertTrue(output.contains(name.substring(0, name.indexOf(" "))), name +" was not found in file.");
		Assert.assertTrue(output.contains(name.substring(name.indexOf(" "), name.lastIndexOf(" ")).trim()), name +" was not found in file.");

	}
	

	@Test
	public void test44125() throws Exception {
		
		TestNGCustom.TCNo="test44125";
	    log("//TC test44125");
	    	
	    //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjd210.*.* | tail -1"; 


		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		//will look
		log(" The PROVIDER CROSS REFERENCE filename is: "+fileName);

		//Verify data in file
		command = "grep 'Level of care requests' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep 'Nursing Home Rate Records Updated' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));

		command = "grep 'Inpatient Hospital Rate Records Updated' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));

		command = "grep 'prvp210d completed' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));

		command = "grep 'job/PRVJD210 completed' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
	}
	
	
	@Test
	public void test44128() throws Exception {
		
		TestNGCustom.TCNo="test44128";
	    log("//TC test44128");
	    	
	    //Verify unix report
		String command, error;
		String provId="110091215";

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/prm01701.rpt.* | tail -1"; 

		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		log(" The PROVIDER CLIA CROSS REFERENCE is: "+fileName);

		//Verify data in file
		command = "grep '03D0871452    1326022732        110091215         A     ABRAZO SCOTTSDALE CAMPUS' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		
		String output = Common.connectUNIX(command, error);
		System.out.println(output);
		log(output);
		
		sqlStatement="select sak_prov from T_PR_PROV where id_provider='"+provId+"'";
		colNames.add("sak_prov");
		colValues=Common.executeQuery(sqlStatement, colNames);

		String sak_prov=colValues.get(0);
		
		sqlStatement="select name from t_pr_nam where name='ABRAZO SCOTTSDALE CAMPUS' and sak_prov='"+sak_prov+"'";
		colNames.add("name");
		colValues=Common.executeQuery(sqlStatement, colNames);

		sqlStatement="select num_clia from T_PR_CLIA_STAT where num_clia='03D0871452' and sak_prov='"+sak_prov+"' and cde_service_loc='A'";
		colNames.add("num_clia");
		colValues=Common.executeQuery(sqlStatement, colNames);
		
		command = "grep '* * END OF REPORT * *' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));

	}
	
	
	@Test
	public void test44129() throws Exception {
		
		TestNGCustom.TCNo="test44129";
	    log("//TC test44129");
	    	
	    //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/prw53001.rpt.* | tail -1"; 

		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		log(" The PROVIDER CHANGES BY CLERK ID WEEKLY SUMMARY is: "+fileName);
				
		command = "grep 'DSMA"+unixDir.toUpperCase()+"' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep 'Prov Serv Location Rec' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));

		command = "grep 'Prov Address Info' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
		command = "grep '* * END OF REPORT * *' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));
		
	}
	
	
	@Test
	public void test44132() throws Exception {
		
		//update with last name
		
		TestNGCustom.TCNo="test44132";
	    log("//TC test44132");
	    
	    //Verify unix report
		String command, error;
		
		String pid="110000571", svcLoc="A";

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/prm00201.rpt.* | tail -1"; 

		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		log(" The LICENSE EXPIRATION REPORT is: "+fileName);
				
		command = "grep '"+pid+"   "+svcLoc+"' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		String output=Common.connectUNIX(command, error);
		System.out.println(output);
		log(output);
		
		sqlStatement="select cde_lic_type, num_prov_lic from t_pr_type where sak_prov= (select sak_prov from t_pr_prov where id_provider='"+pid+"') and cde_service_loc='"+svcLoc+"'";
		colNames.add("cde_lic_type");
		colNames.add("num_prov_lic");

		colValues=Common.executeQuery(sqlStatement, colNames);
		
		String provLicenceType=colValues.get(0);
		String provLicenseNum=colValues.get(1);
		
		System.out.println("provider license type: "+provLicenceType );
		System.out.println("provider license number: "+provLicenseNum );
		
		log("provider license type: "+provLicenceType );
		log("provider license number: "+provLicenseNum );

		sqlStatement="select dte_end, name from T_PR_HB_LIC where num_prov_lic='"+provLicenseNum+"'";
		colNames.add("dte_end");
		colNames.add("name");

		colValues=Common.executeQuery(sqlStatement, colNames);
		
		String endDate=Common.convertDate(colValues.get(0));
		String firstName=colValues.get(1).substring(0, colValues.get(1).indexOf(" "));
		
		System.out.println("expiration date: "+endDate );
		System.out.println("first name: "+firstName );
		
		log("expiration date: "+endDate );
		log("first name: "+firstName );
		
		Assert.assertTrue(output.contains(provLicenseNum), provLicenseNum +" was not found in file.");
		Assert.assertTrue(output.contains(provLicenceType), provLicenceType +" was not found in file.");
		Assert.assertTrue(output.contains(firstName), firstName +" was not found in file.");
		Assert.assertTrue(output.contains(endDate), endDate +" was not found in file.");

	}

	@Test
	public void test44137() throws Exception {
		
		TestNGCustom.TCNo="test44137";
	    log("//TC test44137");
	    
	    //Verify unix report
	  	String command, error;
	  		
	  	//validate file prvjd055
	  		
	  	//Get Desired Filename
	  	command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjd055.*.* | tail -1"; 

	  	error = "There was no report file found";
	  	String fileName = Common.connectUNIX(command, error);
	  	fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
	  	fileName = Common.chkCompress(fileName);
	  	log("The Log file name for job PRVJD055 is: "+fileName);

	  	//same day
	  	command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJD055 completed "+Common.convertSysdate().substring(0, Common.convertSysdate().length()-4) + Common.convertSysdate().substring(Common.convertSysdate().length()-2)+"' "+fileName;

	  	error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
	  	log(Common.connectUNIX(command, error));
	  	
	}


	@Test
	public void test44139a() throws Exception {
		
		TestNGCustom.TCNo="44139";
	    log("//TC test44139a");
	    
	    sqlStatement = " Select B.Id_Provider, A.* From T_Pr_Php_Elig A, T_Pr_Prov B Where A.Sak_Prov_Pgm = 36 and A.Dte_End = 22991231 And A.Dte_Inactive = 22991231  And A.Sak_Prov = B.Sak_Prov \r\n" + 
	    		       " and exists (select c.sak_prov from T_Pr_Php_Elig c where c.sak_prov = b.sak_prov group by (c.sak_prov) having count(c.sak_prov) = 1) and A.cde_enroll_status ='20'";
    	colNames.add("ID_PROVIDER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String provId = colValues.get(0);
    	log("Prov is: "+provId);
    	
    	searchProv(provId);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10422")).click();
		
			if (!(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityList_0:ProviderPublicHealthProgramEligibilityBean_ColValue_endDate")).getText().equals("12/31/2299") && driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityList_0:ProviderPublicHealthProgramEligibilityBean_ColValue_prEnrollStatus_enrollStatusDescriptionStatus")).getText().equals("ACTIVE - Pay")))
				throw new SkipException("Skipping this test as test provider's program elig end date is not EOT, or his elig is not ACTIVE-Pay");
		
		//close active eligibility 
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityList_0:ProviderPublicHealthProgramEligibilityBean_ColValue_prEnrollPgm_providerProgramDescription")).click();

		driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_EndDate']")).clear();
		driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_EndDate']")).sendKeys(Common.convertSysdate());
		Common.saveAll();
		
		//open new eligibility with "VOLUNTARY SUSPENSION BUSINESS OR PRACTICE CLOSED - No Pay" reason
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibility_NewButtonClay:ProviderPublicHealthProgramEligibilityList_newAction_btn")).click();
	    
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:EnrollProgramDropDown"))).selectByVisibleText("PHYSICIAN SERVICES");
		driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_EffectiveDate']")).clear();
		driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_EffectiveDate']")).sendKeys(Common.convertSysdatecustom(1));
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_PrEnrollStatus"))).selectByVisibleText("VOLUNTARY SUSPENSION BUSINESS OR PRACTICE CLOSED - No Pay");

	    Common.saveAll();
	    
	    //Store provider in the DB
		Common.insertData_day2(TestNGCustom.TCNo, provId, "provider ID", Common.convertDatetoInt(Common.convertSysdate()));

	}
	
	@Test
	public void test44139b() throws Exception {
		
		TestNGCustom.TCNo="44139";
	    log("//TC test44139b");
	    
	    sqlStatement= "select ID from r_day2 where TC='"+TestNGCustom.TCNo+"'";
	    System.out.println(sqlStatement);
	    colNames.add("ID");
	    colValues=Common.executeQuery1(sqlStatement, colNames);
	    
	    String expProvId=colValues.get(0);
	    
	    driver.findElement(By.linkText("rpts & letters")).click();
	    driver.findElement(By.linkText("Letters")).click();
	    driver.findElement(By.linkText("Letter Request Search")).click();
	    
	    //select Provider Requests to Disenroll With 30 Days Notice Batch Letter        
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_LetterList"))).selectByValue("75");
		driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_ProviderId']")).sendKeys(expProvId);
	    Common.search();

		//click first letter
		if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:tbody_element']/tr[1]/td[5]")).getText().contains(expProvId)) {
			
			driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id48")).click();
			driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestSearchPanel_generateAndPrintWithButtonDispReset_btn")).click();

		}else 
			Assert.assertTrue(false, "Did not get expected result with provider Id: "+expProvId);
		
	}

	
	//110003671
	@Test
	public void test44140a() throws Exception {
		
		TestNGCustom.TCNo="44140";
	    log("//TC test44140a");
	    
	    sqlStatement = "Select B.Id_Provider From T_Pr_Php_Elig A, T_Pr_Prov B Where A.Sak_Prov_Pgm = 36 And A.Dte_End = 22991231 And A.Dte_Inactive = 22991231 And A.Cde_Enroll_Status = '20' And A.Sak_Prov = B.Sak_Prov and exists (select c.sak_prov from T_Pr_Php_Elig c where c.sak_prov = b.sak_prov group by (c.sak_prov) having count(c.sak_prov) = 1) and rownum < 2";
    	colNames.add("ID_PROVIDER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String provId = colValues.get(0);
    	log("Prov is: "+provId);
    	
    	searchProv(provId);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:ITM_n10422")).click();
		
			if (!(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityList_0:ProviderPublicHealthProgramEligibilityBean_ColValue_endDate")).getText().equals("12/31/2299") && driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityList_0:ProviderPublicHealthProgramEligibilityBean_ColValue_prEnrollStatus_enrollStatusDescriptionStatus")).getText().equals("ACTIVE - Pay")))
				throw new SkipException("Skipping this test as test provider's program elig end date is not EOT, or his elig is not ACTIVE-Pay");
		
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityList_0:ProviderPublicHealthProgramEligibilityBean_ColValue_prEnrollPgm_providerProgramDescription")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_PrEnrollStatus"))).selectByVisibleText("VOLUNTARY SUSPENSION PROVIDER WITHDRAWAL-NO 30 DAY NOTICE - No Pay");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_EndDate")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_EndDate")).sendKeys(Common.convertSysdatecustom(-1));
	    Common.saveAll();
	    
	    //Store provider in the DB
		Common.insertData_day2(TestNGCustom.TCNo, provId, "provider ID", Common.convertDatetoInt(Common.convertSysdate()));


	}
	
	
	@Test
	public void test44140b() throws Exception {
		
		TestNGCustom.TCNo="44140";
	    log("//TC test44140b");
	    
	    sqlStatement= "select ID from r_day2 where TC='"+TestNGCustom.TCNo+"'";
	    System.out.println(sqlStatement);
	    colNames.add("ID");
	    colValues=Common.executeQuery1(sqlStatement, colNames);
	    
	    String expProvId=colValues.get(0);
	    
	    driver.findElement(By.linkText("rpts & letters")).click();
	    driver.findElement(By.linkText("Letters")).click();
	    driver.findElement(By.linkText("Letter Request Search")).click();
	    
	    //select Provider Requests to Disenroll Without 30 Days Notice Batch Letter        
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_LetterList"))).selectByValue("76");
		driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_ProviderId']")).sendKeys(expProvId);

	    Common.search();

		//click first letter
		if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:tbody_element']/tr[1]/td[5]")).getText().contains(expProvId)) {
			
			driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id48")).click();
			driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestSearchPanel_generateAndPrintWithButtonDispReset_btn")).click();

		}else 
			Assert.assertTrue(false, "Did not get expected result with provider Id: "+expProvId);
		
	}

	

	@Test
	public void test44142() throws Exception {
		
		TestNGCustom.TCNo="test44142";
	    log("//TC test44142");
	    
	    //Verify unix report
	  	String command, error;
	  		
	  	//Get Desired Filename
	  	command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjd360.*.* | tail -1"; 

	  	error = "There was no report file found";
	  	String fileName = Common.connectUNIX(command, error);
	  	fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
	  	fileName = Common.chkCompress(fileName);
	  	log("The Job logs for the job PRVJD360 is: "+fileName);

	  	command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJD360 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
	  	error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
	  	log(Common.connectUNIX(command, error));
	  	
	}


	@Test
	public void test44149a() throws Exception {
		
		TestNGCustom.TCNo="44149a";
	    log("//TC 44149a");
	    
	    //get active prov Id
	    sqlStatement="select sak_case from t_pr_case where cde_case_status = '044' and id_case > dbms_random.value * 6300000 and rownum < 2";
	    sqlStatement="select sak_case from t_pr_case where sak_case='107152'";
	    colNames.add("sak_case");
	  	colValues=Common.executeQuery(sqlStatement, colNames);
	  	
	  	String caseNum=colValues.get(0);
		System.out.println("Case number: "+caseNum);
		log("Case number: "+caseNum);
		
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_CaseTracking")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingSearchBean_CriteriaPanel:ProviderCTSearch_CaseNumber_Id")).sendKeys(caseNum);
		Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:CaseTrackingSearchResults_0:CaseTrackingSearchPanelSpacer2")).click();
		
		//get prov Id for day2
		Thread.sleep(2000);
		String provId=new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseInformationBean_ProviderServiceLocationList"))).getFirstSelectedOption().getText();
		System.out.println("Provider id: "+provId);
		log("Provider id: "+provId);

		driver.findElement(By.linkText("Case Profile")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_ProvCaseStatus"))).selectByVisibleText("Terminated - Statutory Req.");
		Common.saveAll();
		
		Common.insertData_day2(TestNGCustom.TCNo.substring(0, TestNGCustom.TCNo.length()-1), caseNum, "Case number", Common.convertDatetoInt(Common.convertSysdate()));
		Common.insertData_day2(TestNGCustom.TCNo.substring(0, TestNGCustom.TCNo.length()-1), provId, "Provider ID", Common.convertDatetoInt(Common.convertSysdate()));

		
	}
	
	@Test
	public void test44149b() throws Exception {
		
		TestNGCustom.TCNo="44149b";
	    log("//TC 44149b");
	    
	    //Get data from tables that needs to be verified in the report (Newly Enrolled Providers Report)
	  	sqlStatement ="select * from r_day2 where TC = '"+TestNGCustom.TCNo.substring(0, TestNGCustom.TCNo.length()-1)+"' and DES='Provider ID'";
	  	colNames.add("ID");
	  	colNames.add("DATE_REQUESTED");

	  	colValues = Common.executeQuery1(sqlStatement, colNames);
	  	
	  		if (colValues.get(0).equals("null"))
	  		    throw new SkipException("Skipping this test because there was no Prov ID generated");

	  	String provID = colValues.get(0);
	  	log("Provider Id: "+provID);
	  	
		String dateRun = colValues.get(1);
	  	log("First day run date: "+ dateRun);
		
	  	String command, error;

	    //Get Desired Filename
	  	command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvj9049.*.* | tail -1"; 

	  	error = "There was no log file found for job PRVJD9049";
	  	String fileName = Common.connectUNIX(command, error);
	  	fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
	  	fileName = Common.chkCompress(fileName);
	  	log("The Log file name for job PRVJD9049 is: "+fileName);
	  	
	  	command = "grep '[110088022]' "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log(Common.connectUNIX(command, error));

	  	//command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJD9049 completed "+Common.convertSysdate().substring(0, Common.convertSysdate().length()-4) + Common.convertSysdate().substring(Common.convertSysdate().length()-2)+"' "+fileName;
	  	command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJ9049 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;

	  	error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
	  	log(Common.connectUNIX(command, error));
	  	
	  	String letterName="PRV-9049-R";
	  	
	  	driver.findElement(By.linkText("rpts & letters")).click();
	  	driver.findElement(By.linkText("Letters")).click();
	  	driver.findElement(By.linkText("Letter Request Search")).click();
	  	
	  	//select: Failure to Retain Provider Eligibility ~ 450.212 Batch Letter
	  	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_LetterList"))).selectByValue("62");;
	  	driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_ProviderId']")).sendKeys(provID);
	  	Common.search();
	  	
		Assert.assertTrue(driver.findElements(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id48")).size() > 0, "No records found with "+provID+ " and "+dateRun);
			
	}
	
@Test
	public void test44146a() throws Exception {

		TestNGCustom.TCNo="44146a";
		log("//TC 44146a");
		
		/*sqlStatement="select distinct l.CDE_VENDOR, a.* from t_pr_eft_acct a, t_pr_svc_loc l where a.sak_prov = l.sak_prov and a.cde_service_loc = l.cde_service_loc and l.cde_vendor <> ' ' and a.num_aba > dbms_random.value * 6300000 and rownum < 2";
		colNames.add("CDE_VENDOR");
		colNames.add("CDE_STATUS_EFT");
		colNames.add("NUM_ABA");
		colNames.add("NUM_EFT_ACCT");

		colValues=Common.executeQuery(sqlStatement, colNames);
		
		String vendor=colValues.get(0);
		System.out.println("Vendor from DB: "+vendor);
		log("Vendor from DB: "+vendor);
		
		if(!(vendor.substring(vendor.length()).equals("9")))
			vendor=vendor.substring(0,vendor.length()-1)+"9";
		else
			vendor=vendor.substring(0,vendor.length()-1)+"8";
			
		System.out.println("Vendor in file: "+vendor);
		log("Vendor in file: "+vendor);
		
		String EFTstatus=colValues.get(1);
		System.out.println("EFT status: "+EFTstatus);
		log("EFT status: "+EFTstatus);
		
		String transitNumber=colValues.get(2);
		System.out.println("Transit number: "+transitNumber);
		log("Transit number: "+transitNumber);
		
		String bankAccount=colValues.get(3);
		System.out.println("Bank account: "+bankAccount);
		log("Bank account: "+bankAccount);*/
		
		String provId="110152210";
		String serviceLoc="A";
		
		sqlStatement="select distinct l.CDE_VENDOR, a.* from t_pr_eft_acct a, t_pr_svc_loc l where a.sak_prov = l.sak_prov and l.sak_prov=(select sak_prov from t_pr_prov where id_provider='"+provId+"') and l.cde_service_loc='"+serviceLoc+"' and a.num_aba > dbms_random.value * 6300000 and rownum < 2";
		colNames.add("CDE_VENDOR");
		colNames.add("CDE_STATUS_EFT");
		colNames.add("NUM_ABA");
		colNames.add("NUM_EFT_ACCT");

		colValues=Common.executeQuery(sqlStatement, colNames);
		
		String vendor=colValues.get(0);
		System.out.println("Vendor : "+vendor);
		log("Vendor : "+vendor);
		
		String EFTstatus=colValues.get(1);
		System.out.println("EFT status from DB: "+EFTstatus);
		log("EFT status from DB: "+EFTstatus);
		
		EFTstatus=String.valueOf(Integer.valueOf(EFTstatus)+1);
		System.out.println("EFT status in file: "+EFTstatus);
		log("EFT status in file: "+EFTstatus);
		
		String transitNumber=colValues.get(2);
		System.out.println("Transit number: "+transitNumber);
		log("Transit number: "+transitNumber);
		
		String bankAccount=colValues.get(3);
		System.out.println("Bank account: "+bankAccount);
		log("Bank account: "+bankAccount);
		/*
		if(!bankAccount.endsWith("9")) 
			bankAccount=bankAccount.substring(0, bankAccount.length()-1)+"9";
		else 
			bankAccount=bankAccount.substring(0, bankAccount.length()-1)+"8";

		System.out.println("Bank account: in file: "+bankAccount);
		log("Bank account: in file: "+bankAccount);*/
		
		String inputFile="	<EFTUpdates>\r\n" + 
						 "	    <EFTUpdate \r\n" + 
				
							"			vendor_code_prefix=\""+vendor.substring(0,2)+"\"\r\n" + 
							"	        vendor_code_suffix=\""+vendor.substring(2)+""+"\"\r\n" + 
							"	        transit_number=\""+transitNumber+"\"\r\n" + 
							"	        eft_status=\""+EFTstatus+"\"\r\n" + 
							"	        type_of_account=\"2\"\r\n" + 
							"	        vendor_bank_account=\""+bankAccount+"\">\r\n" + 
				
						 "	     </EFTUpdate>\r\n" + 
						 "	</EFTUpdates>";

		log("Input file: "+inputFile);	
		
		String uploadFile=tempDirPath+"prw37001.dat.0001";
    	PrintWriter out = new PrintWriter(uploadFile);
    	out.println(inputFile);
    	out.close();
    	
		String copyTo = "/customer/dsma/"+unixDir+"/data01";
		Common.connectUNIXsftp(uploadFile, copyTo);
		log("Successfully transferred file to unix $DATADIR");
		
		//change permission
		String command, error;
		String uploadFileName=uploadFile.substring(uploadFile.lastIndexOf("\\")+1, uploadFile.length());
		System.out.println("input file name: "+uploadFileName);
		log("input file name: "+uploadFileName);
		
		command = "chmod 777 /customer/dsma/"+unixDir+"/data01/"+uploadFileName;
		error = "Could not change permission to 777";
		String resp = Common.connectUNIX(command, error);
		log("Successfully changed permission to 777 for file: "+uploadFileName);
		
		Common.insertData_day2(TestNGCustom.TCNo.substring(0, TestNGCustom.TCNo.length()-1), vendor, "Vendor number", Common.convertDatetoInt(Common.convertSysdate()));
		Common.insertData_day2(TestNGCustom.TCNo.substring(0, TestNGCustom.TCNo.length()-1), bankAccount, "Bank account", Common.convertDatetoInt(Common.convertSysdate()));
		Common.insertData_day2(TestNGCustom.TCNo.substring(0, TestNGCustom.TCNo.length()-1), (provId+serviceLoc), "Provider Id", Common.convertDatetoInt(Common.convertSysdate()));


	}	

@Test
public void test44136a() throws Exception {
	
	TestNGCustom.TCNo="44136";
    log("//TC test44136a");
    
    sqlStatement="select * from t_pr_php_elig a where a.cde_enroll_status ='20' and a.cde_service_loc='A' and not exists (select 1 from t_pr_php_elig php where php.sak_prov=a.sak_prov and php.cde_service_loc='B') and a.dte_end > dbms_random.value * 6300000 and rownum < 2";
    colNames.add("sak_prov");
    colValues=Common.executeQuery(sqlStatement, colNames);
    
    String sak_prov=colValues.get(0);
    
    sqlStatement="select id_provider from t_pr_prov where sak_prov='"+sak_prov+"'";
    colNames.add("id_provider");
    colValues=Common.executeQuery(sqlStatement, colNames);
    
    String provId=colValues.get(0);
    
    String DEAnumber=Common.generateRandomTaxID();
    String effDate=Common.firstDateOfPreviousMonth();
    
    log("Prov id: "+provId);
    log("DEA number: "+DEAnumber);
    log("Effective date: "+effDate);
    log("End date: "+endDate);

    searchProv(provId);

    driver.findElement(By.xpath("//a[text()='DEA']")).click();
    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderDEAPanel:ProviderDEA_NewButtonClay:ProviderDEAList_newAction_btn")).click();
    driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderDEAPanel:ProviderDEAPanel_deaNumber']")).sendKeys(DEAnumber);
    driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderDEAPanel:ProviderDEADataPanel_EffectiveDate']")).sendKeys(effDate);
    driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:ProviderDEAPanel:ProviderDEADataPanel_EndDate']")).sendKeys(endDate);
    
    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderDEAPanel:ProviderDEAPanel_verifyAction_btn")).click();
    driver.findElement(By.xpath("//*[@name='MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:HtmlMessages_CheckBox_Question1' and @value='Yes']")).click();
    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderNavigatorPanel:ProviderNavigator:_id265")).click();
    Common.saveAll();
    
    Common.insertData_day2(TestNGCustom.TCNo, provId, "provider ID", Common.convertDatetoInt(Common.convertSysdate()));
	Common.insertData_day2(TestNGCustom.TCNo, DEAnumber, "DEA", Common.convertDatetoInt(Common.convertSysdate()));
	Common.insertData_day2(TestNGCustom.TCNo, effDate, "EFF date", Common.convertDatetoInt(Common.convertSysdate()));
	Common.insertData_day2(TestNGCustom.TCNo, endDate, "END date", Common.convertDatetoInt(Common.convertSysdate()));

}


@Test
public void test44136b() throws Exception {
	
    TestNGCustom.TCNo="44136";
    log("//TC test44136b");
    
    String provId=Common.getDataRday2_new(TestNGCustom.TCNo, "provider ID").get(0); 
    String DEAnum=Common.getDataRday2_new(TestNGCustom.TCNo, "DEA").get(0); 
    String effDate=Common.getDataRday2_new(TestNGCustom.TCNo, "EFF date").get(0);
    String endDate=Common.getDataRday2_new(TestNGCustom.TCNo, "END date").get(0);

    String command = "ls -ltr /customer/dsma/"+unixDir+"/data01/prd05501.xml.* | tail -1"; 

    String error = "There was no report file found";
	String fileName = Common.connectUNIX(command, error);
	fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
	fileName = Common.chkCompress(fileName);
	
	log(" The ENROLLMENT TRACKING REPORT is: "+fileName);
			
	command = "grep 'alt_id_provider=\""+provId+"\"' "+fileName;
	error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
	String output = Common.connectUNIX(command, error);
	log(output);
	
	command = "grep 'num_dea=\""+DEAnum+"\"' "+fileName;
	error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
	output = Common.connectUNIX(command, error);
	log(output);
    
	command = "grep 'dte_effective=\""+Common.convertDate_yyyyHIFENmmHIFENdd(effDate)+"\"' "+fileName;
	error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
	output = Common.connectUNIX(command, error);
	log(output);
    
	command = "grep 'dte_end=\""+Common.convertDate_yyyyHIFENmmHIFENdd(endDate)+"\"' "+fileName;
	error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
	output = Common.connectUNIX(command, error);
	log(output);
    
	command = "grep 'dte_verified=\""+Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertGivendatecustom(Common.convertSysdate(), -1))+"\"' "+fileName;
	error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
	output = Common.connectUNIX(command, error);
	log(output);
    
	String firstFileName=fileName;
    //validate second file
	command = "ls -ltr /customer/dsma/"+unixDir+"/data01/prd05501.count.xml.* | tail -1"; 

	error = "There was no report file found";
	fileName = Common.connectUNIX(command, error);
	fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
	fileName = Common.chkCompress(fileName);
		
	log(" The <ControlRecords> is: "+fileName);
    
	command = "grep 'file_name=\""+firstFileName.substring(firstFileName.indexOf("prd05501"))+"\"' "+fileName;
	error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
	output = Common.connectUNIX(command, error);
	log(output);
    
	command = "grep 'dte_run=\""+Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdate())+"\"' "+fileName;
	error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
	output = Common.connectUNIX(command, error);
	log(output);
    
}
	
	// ********************************************************************* 
	//					New Provider TCs - Mahammad End
	// *********************************************************************
	
	// ********************************************************************* 
	//					New Provider TCs - Abujot Start
	// *********************************************************************

@Test
public void test44178() throws Exception {
	TestNGCustom.TCNo="test44178";

    log("//TC test44178");
    //Verify unix report
    String command, error;
   //validate first file prvjr161
           
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjr161.*.* | tail -1";
    error = "There was no log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job PRVJR161 is : "+fileName);
    
    //Verify job completed
   // command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJR161 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJR161 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
    
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    System.out.println(command);
    log(Common.connectUNIX(command, error));
}

@Test
public void test44177() throws Exception {
	TestNGCustom.TCNo="test44177";

    log("//TC test44177");
    //Verify unix report
    String command, error;
   //validate first file prvjm560 
    
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjm560.*.* | tail -1";
    error = "There was no log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job prvjm560 is : "+fileName);
    
    //Verify job completed
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJM560 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
    
   
           
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/prv0560m.rpt.* | tail -1";
    error = "There was no report file found";
    
    //String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    String dataFile = Common.connectUNIX(command, error);
    dataFile=dataFile.substring(dataFile.indexOf("/"), dataFile.length());
    
    
    dataFile = Common.chkCompress(dataFile);
    log("The MCE PROVIDER AFFILIATION report filename is: "+dataFile);
  
    //Verify data in file
    command = "grep 'END OF REPORT' "+dataFile;
    error = "The selected file "+dataFile+" does not have end of report message in it";
    log(Common.connectUNIX(command, error));
    
  //Get data from tables that needs to be verified in the report (Newly Enrolled Providers Report)
  		sqlStatement ="select p.id_provider, x.cde_service_loc from T_PR_AFF_PR_LOC_XREF x, t_pr_prov p"
  				+ " where x.sak_aff_prov = 31449"
  				+ " and x.cde_aff_SVC_LOC = 'E'"
  				+ " and x.cde_affiliation_type = 'MP'"
  				+ " and x.dte_end = 22991231"
  				+ " and x.sak_prov = p.sak_prov"
  				+ " and rownum < 2";
  		colNames.add("ID_PROVIDER");
  		colNames.add("CDE_SERVICE_LOC");
  		System.out.println(sqlStatement);
  		colValues = Common.executeQuery(sqlStatement, colNames);
 
		String providerID = colValues.get(0);
		String svcLoc = colValues.get(1);
		log("Provider is: "+providerID+ " service location is : "+svcLoc);
		
		command = "grep '"+providerID+"' "+dataFile;
        error = "The selected file "+dataFile+" does not have provider in it";
         
        String outputtext = Common.connectUNIX(command, error);
        log("Report has affiliated provider in this line : "+outputtext);
        Assert.assertTrue(outputtext.contains("12/31/2299"), "affiliated provider end date is not correct");
        
        // validate date
        String getYesterdaysDate = Common.convertSysdatecustom(-1);
        command = "grep '"+getYesterdaysDate+"' "+dataFile;
        error = "The selected file "+dataFile+" does not have the latest run date (wednesdays) in it";
        log(Common.connectUNIX(command, error));
}


@Test
public void test44173() throws Exception {
	TestNGCustom.TCNo="test44173";

    log("//TC test44173");
    //Verify unix report
    String command, error;
   //validate first file prvjr161
           
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjw375.*.* | tail -1";
    error = "There was no prvjd005*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job PRVJW375 is : "+fileName);
    
    //Verify job completed
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJW375 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
     
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
    
    //Get Desired DataFile - prvmcoenrollsumm.110025617D.*.*
    command = "ls -ltr /customer/dsma/"+unixDir+"/data01/prvmcoenrollsumm.110025617D.*.* | tail -1";
    error = "There was no prvmcoenrollsumm.110025617D.*.* data file found";
    String dataFile = Common.connectUNIX(command, error);
    dataFile=dataFile.substring(dataFile.indexOf("/"), dataFile.length());
    
    
    dataFile = Common.chkCompress(dataFile);
    log(" The data filename for job PRVJW375 is : "+dataFile);
    
    //Get data from tables that needs to be verified in the report (Newly Enrolled Providers Report)
		sqlStatement ="Select x.ID_NPI, x.NUM_TAX_ID from T_PR_APPLN  x, T_PR_CDE_APPLN_STATUS s where s.CDE_STATUS1 = '02' and x.ID_NPI != ' '";
		colNames.add("ID_NPI");
		colNames.add("NUM_TAX_ID");
		colValues = Common.executeQuery(sqlStatement, colNames);
		
		String ID_NPI = colValues.get(0);
	String NUM_TAX_ID = colValues.get(1);
		
	command = "grep "+ID_NPI+" "+dataFile;
    error = "The selected file "+dataFile+" does not have NPI_ID in it";
	 String outputtext = Common.connectUNIX(command, error);
     log("Report has affiliated ID_NPI in this line : "+outputtext);
     Assert.assertTrue(outputtext.contains(NUM_TAX_ID), "affiliated provider tax id is not correct");
     
     /////Get Desired DataFile - prvmcoenrollsumm.110088791A.*.*
     command = "ls -ltr /customer/dsma/"+unixDir+"/data01/prvmcoenrollsumm.110088791A.*.* | tail -1";
     error = "There was no prvmcoenrollsumm.110088791A.*.* data file found";
     String dataFile2 = Common.connectUNIX(command, error);
     dataFile2=dataFile2.substring(fileName.indexOf("/"), fileName.length());
     
     
     dataFile2 = Common.chkCompress(dataFile2);
     log(" The data filename for job PRVJW375 is : "+dataFile2);
     
     //Get data from tables that needs to be verified in the report (Newly Enrolled Providers Report)
		sqlStatement ="Select x.ID_NPI, x.NUM_TAX_ID from T_PR_APPLN  x, T_PR_CDE_APPLN_STATUS s where s.CDE_STATUS1 = '02' and x.ID_NPI != ' '";
		colNames.add("ID_NPI");
		colNames.add("NUM_TAX_ID");
		colValues = Common.executeQuery(sqlStatement, colNames);
		
		String ID_NPI_2 = colValues.get(0);
		String NUM_TAX_ID_2 = colValues.get(1);
		
		command = "grep "+ID_NPI_2+" "+dataFile;
    error = "The selected file "+dataFile2+" does not have NPI_ID in it";
		 String outputtext2 = Common.connectUNIX(command, error);
      log("Report has affiliated ID_NPI in this line : "+outputtext2);
      Assert.assertTrue(outputtext.contains(NUM_TAX_ID_2), "affiliated provider tax id is not correct");
    
}

@Test
public void test44169a() throws Exception {
	TestNGCustom.TCNo="test44169a";

    log("//TC test44169a");
    
    //create data for letter
    //get ATN
    sqlStatement ="select * from (Select a.SAK_ATN FROM T_PR_APPLN a where a.cde_status1 = 34 "
    		+ "and not exists (select 1 from t_pr_case c where a.SAK_ATN= c.SAK_ATN) "
    		+ "order by a.dte_last_status desc) WHERE ROWNUM = 1 ";
    
		colNames.add("SAK_ATN");
		colValues = Common.executeQuery(sqlStatement, colNames);
		String SAK_ATN = colValues.get(0);
		System.out.println(SAK_ATN+ "------- is ATN number");
		
		//Insert this ATTN in r_day2 db
    //Example-Store provider in the DB
    String SelSql="select * from r_day2 where TC = '44169a'";
    String col="ID";
    String DelSql="delete from r_day2 where TC = '44169a'";
    String InsSql="insert into r_day2 values ('44169a', '"+SAK_ATN+"', 'Prov ATN in 34 Status', '"+ Common.convertSysdate()+"')";
    Common.insertData(SelSql, col, DelSql, InsSql);
    System.out.println(SAK_ATN+ "-------ATN number saved in r_day2");

    //provider tab base app		
		
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_CaseTracking")).click();
		
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingSearchBean_CriteriaPanel:NEW")).click(); // click new to create new case

		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_caseDescription")).sendKeys("Testing for SAK_ATN "+SAK_ATN);	// case description
		//add ATN 
		driver.findElement(By.xpath("//*[@id=\"MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:DataPanelATNPopUpSearchControl_CMD_SEARCH\"]/img")).click(); // search for ATN by		
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:_id103:ATNPopUpSearchCriteriaPanel:Sak")).sendKeys(SAK_ATN); // enter ATN in search field
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:_id103:ATNPopUpSearchCriteriaPanel:SEARCH")).click(); // click search
		driver.findElement(By.xpath("//*[@id=\"MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:_id103:ATNPopUpSearchResults_0:column1Value\"]")).click(); // click first ATN
		//add assigned to to
		driver.findElement(By.xpath("//*[@id=\"MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:DataPanelAssignedToPopUpSearchControl_CMD_SEARCH\"]/img")).click();// search assigned to
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:_id103:ClerkIDSearchCriteriaPanel:SEARCH")).click(); // search button click to find assigned to
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:_id103:ClerkIDSearchResults_0:column1Value")).click();// get first user/assignee 
	   // case type, status,level, hidecaseFromView dropdown
	 	new Select (driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_ProvCaseType"))).selectByVisibleText("PROVIDER REVIEW COMMITTEE");
	 	new Select (driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_ProvCaseStatus"))).selectByVisibleText("Enrollment Approved with PRC Warning");
	 	new Select (driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_ProvCaseLevel"))).selectByVisibleText("Referred to PRC");
	 	new Select (driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_CaseSecuredIndicator"))).selectByVisibleText("No");
	 	// add closed/completed by
	 	driver.findElement(By.xpath("//*[@id=\"MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:DataPanelClosedByPopUpSearchControl_CMD_SEARCH\"]/img")).click();// search completed/closed by
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:_id103:ClerkIDSearchCriteriaPanel:SEARCH")).click(); // search button click to find assigned to
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:_id103:ClerkIDSearchResults_0:column1Value")).click();// get first user
		// assignment date and closed by date 
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_CaseAssignedDate")).sendKeys(Common.convertSysdate());
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderCaseTrackingProvCasePanel:ProviderCaseDataPanel_CaseCloseDate")).sendKeys(Common.convertSysdate());
		// save all
		Common.saveAll();
		}

@Test
public void test44169b() throws Exception {
	TestNGCustom.TCNo="test44169b";

    log("//TC test44169b");
    //Verify unix report
    String command, error;
   //validate first file prvj9045
           
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvj9045.*.* | tail -1";
    error = "There was no prvj9045*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job PRVJ9045 is : "+fileName);
    
    //Verify job completed
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJ9045 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
     
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
    
    //provider tab base app		
    validateLetterFor44169("PRV-9045-R", "44169a", "New Applicant Approval Batch Letter" );
    
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestSearchPanel_generateAndPrintWithButtonDispReset_btn")).click();// generate and print clicked
		
    
}


@Test
public void test44168() throws Exception {
	TestNGCustom.TCNo="test44168";

    log("//TC test44168");
    //Verify unix report
    String command, error;
   //validate first file prvjd361
           
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjd361.*.* | tail -1";
    error = "There was no prvjd361*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job PRVJD361 is : "+fileName);
    
    //Verify job completed
    //change
    //command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJD361 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJD361 completed "+Common.convertSysdatecustom(0).substring(0, Common.convertSysdatecustom(0).length()-4) + Common.convertSysdatecustom(0).substring(Common.convertSysdatecustom(0).length()-2)+"' "+fileName;
     
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
}


@Test
public void test44167b() throws Exception {
	TestNGCustom.TCNo="test44167b";

    log("//TC test44167b");
    //Verify unix report
    String command, error;
   //validate first file prvj9045
           
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvj9038.*.* | tail -1";
    error = "There was no prvj9045*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job PRVJ9045 is : "+fileName);
    
    //Verify job completed
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJ9038 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
     
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
    
    //provider tab base app		 
    validateLetter("PRV-9038-R", "44167a", "Provider Application Requested Not Recieved Batch Letter" );
		
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestSearchPanel_generateAndPrintWithButtonDispReset_btn")).click();// generate and print clicked
		
    
}

@Test
public void test44165() throws Exception {
	TestNGCustom.TCNo="test44165";

    log("//TC test44165");
    //Verify unix report
    String command, error;
   //validate first file prvjr161
           
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjw018.*.* | tail -1";
    error = "There was no prvjw018*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job prvjw018 is : "+fileName);
    
    //Verify job completed
   command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJW018 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
    
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
    
    //Get Desired DataFile - prvmcoenrollsumm.110025617D.*.*
    command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/prw01801.rpt.* | tail -1";
    error = "There was no prw01801.rpt.* data file found";
    String dataFile = Common.connectUNIX(command, error);
    dataFile=dataFile.substring(dataFile.indexOf("/"), dataFile.length());
    
    
    dataFile = Common.chkCompress(dataFile);
    log(" The data filename for job prvjw018 is : "+dataFile);
    
    //Get data from tables that needs to be verified in the report (Newly Enrolled Providers Report)
		sqlStatement ="select p.ID_PROVIDER, a.sak_prov, a.ADR_MAIL_CITY, a.ADR_MAIL_ZIP, s.cde_service_loc from t_pr_adr a, t_pr_loc_nm_adr s, T_PR_prov p"
				+ " where a.ind_undeliverable = 'Y'"
				+ " and a.sak_prov = s.sak_prov"
				+ " and a.sak_prov = p.sak_prov"
				+ " and s.sak_short_address = a.sak_short_address"
				+ " and rownum < 2";
		
		colNames.add("ID_PROVIDER");
		colNames.add("ADR_MAIL_CITY");
		colNames.add("ADR_MAIL_ZIP");
		colNames.add("CDE_SERVICE_LOC");
		colValues = Common.executeQuery(sqlStatement, colNames);
		
		String ID_PROVIDER = colValues.get(0);
	String ADR_MAIL_CITY = colValues.get(1);
	String ADR_MAIL_ZIP = colValues.get(2);
	String CDE_SERVICE_LOC = colValues.get(3);
	
	command = "grep "+ID_PROVIDER+" "+dataFile;
    error = "The selected file "+dataFile+" does not have NPI_ID in it";
	Common.connectUNIX(command, error);
     
	 command = "grep "+ADR_MAIL_CITY+" "+dataFile;
     error = "The selected file "+dataFile+" does not have Mail City in it";
     Common.connectUNIX(command, error);
     
     command = "grep "+ADR_MAIL_ZIP+" "+dataFile;
     error = "The selected file "+dataFile+" does not have Mail Zip in it";
     Common.connectUNIX(command, error); 
    
     command = "grep "+CDE_SERVICE_LOC+" "+dataFile;
     error = "The selected file "+dataFile+" does not have Service Loc in it";
     Common.connectUNIX(command, error); 
    
}

@Test
public void test44166() throws Exception {
	TestNGCustom.TCNo="test44166";

    log("//TC test44166");
    //Verify unix report
    String command, error;
   //validate first file prvjm600
           
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjm600.*.* | tail -1";
    error = "There was no prvjw018*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job prvjm600 is : "+fileName);
    
    //Verify job completed
   command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJM600 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
    
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
    
    // verify input data file
    command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/prv0600m.rpt.* | tail -1";
    error = "There was no prv0600m.rpt.* data file found";
    
    String dataFile = Common.connectUNIX(command, error);
    dataFile=dataFile.substring(dataFile.indexOf("/"), dataFile.length());
    
    
    dataFile = Common.chkCompress(dataFile);
    log(" The data filename for job prvjw018 is : "+dataFile);
    
    //Get data from tables that needs to be verified in the report (Newly Enrolled Providers Report)
		sqlStatement ="select p.ID_PROVIDER, a.sak_prov, x.ID_NPI, b.CDE_RECRED_REQ_STATUS , ad.cde_service_loc from T_pr_recred_req b, t_pr_adr a, t_pr_loc_nm_adr ad, T_PR_prov p, T_PR_APPLN x, t_pr_loc_nm_adr s"
				+ " where a.sak_prov = s.sak_prov"
				+ " and a.sak_prov = p.sak_prov"
				+ " and a.sak_prov = x.sak_prov"
				+ " and a.sak_prov = b.sak_prov"
				+ " and a.sak_prov = s.sak_prov"
				+ " and rownum < 2";
		
		colNames.add("ID_PROVIDER");
		colNames.add("ID_NPI");
		colNames.add("CDE_SERVICE_LOC");
		colValues = Common.executeQuery(sqlStatement, colNames);
		
		String ID_PROVIDER = colValues.get(0);
	String ID_NPI = colValues.get(1);
	String CDE_SERVICE_LOC = colValues.get(2);
	
	command = "grep "+ID_PROVIDER+" "+dataFile;
    error = "The selected file "+dataFile+" does not have ID_PROVIDER in it";
	Common.connectUNIX(command, error);
	
	command = "grep "+ID_NPI+" "+dataFile;
     error = "The selected file "+dataFile+" does not have ID_NPIin it";
     Common.connectUNIX(command, error);
	
     command = "grep "+CDE_SERVICE_LOC+" "+dataFile;
     error = "The selected file "+dataFile+" does not have Service Loc in it";
     Common.connectUNIX(command, error); 
    
     command = "grep PROVIDER RE-CREDENTIALING TRACKING REPORT "+dataFile;
     error = "The selected file "+dataFile+" does not have PROVIDER RE-CREDENTIALING TRACKING REPORT text in it";
     Common.connectUNIX(command, error); 
	
     command = "grep "+Common.convertSysdatecustom(-1)+" "+dataFile;
     error = "The selected file "+dataFile+" does not have yesterday's (i.e wednesday) date text in it";
     Common.connectUNIX(command, error); 
 
}



@Test
public void test44162b() throws Exception {
	TestNGCustom.TCNo="test44162b";

    log("//TC test44162b");
    //Verify unix report
    String command, error;
   //validate first file prvj9045
           
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvj9008.*.* | tail -1";
    error = "There was no prvj9008*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job PRVJ9008 is : "+fileName);
    
    //Verify job completed
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJ9008 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
     
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
    
    //provider tab base app		 
    validateLetter("PRV-9008-R", "44162", "Provider Welcome Batch Letter" );
		
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestSearchPanel_generateAndPrintWithButtonDispReset_btn")).click();// generate and print clicked
		}

@Test
public void test44161b() throws Exception {
	TestNGCustom.TCNo="test44161b";

    log("//TC test44161b");
    //Verify unix report
    String command, error;
   //validate first file prvj9045
           
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjw506.*.* | tail -1";
    error = "There was no prvjW506*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job prvjw506 is : "+fileName);
    
    //Verify job completed
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJW506 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
     
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
    
    // verify input data file
    command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/prm50601.rpt.* | tail -1";
    error = "There was no prm40401.rpt.* data file found";
    
    String dataFile = Common.connectUNIX(command, error);
    dataFile=dataFile.substring(dataFile.indexOf("/"), dataFile.length());
    
    
    dataFile = Common.chkCompress(dataFile);
    log("The data filename for job prvjm404 is : "+dataFile);
    System.out.println("The data filename for job prvjm404 is : "+dataFile);
    
    //Get data from r_day2 table
		sqlStatement ="select * from r_day2 where TC = '44161'";
		colNames.add("ID");
		colNames.add("DATE_REQUESTED");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		String ATN = colValues.get(0);
		String OwnerShip_date_chnage = colValues.get(1);
		
		
		System.out.println(ATN + "   "+ OwnerShip_date_chnage);
	command = "grep "+ATN+" "+dataFile; // validate ATN number
    error = "The selected file "+dataFile+" does not have Lic_Provider in it";
	Common.connectUNIX(command, error);
	
	command = "grep "+OwnerShip_date_chnage+" "+dataFile;
     error = "The selected file "+dataFile+" does not have exact date changed in it";
     Common.connectUNIX(command, error);
	
    }

@Test
public void test44161a() throws Exception {
	TestNGCustom.TCNo="test44161a";

    log("//TC test44161a");
        
    // get ATn by order desc &  for owner ship change do the following. 
   // sqlStatement ="select * from (Select SAK_ATN FROM T_PR_APPLN where IND_OWNER_CHG != ' ' and IND_OWNER_CHG != 'y' order by SAK_ATN desc ) where ROWNUM = 1";
    sqlStatement ="Select SAK_ATN FROM T_PR_APPLN where IND_OWNER_CHG = 'N' and cde_status1 = '12' and rownum < 2";
		
		colNames.add("SAK_ATN");
		
		colValues = Common.executeQuery(sqlStatement, colNames);
		
		String SAK_ATN = colValues.get(0);	
	
    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();// click enrollment
		
		driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(SAK_ATN);// click and Send ATN num
		driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:SEARCH")).click();// click search 

		System.out.println(SAK_ATN+"--------------is the ATNs-------------- herer");
		driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchResults_0:EnrollmentSearchPanelSpacer24")).click(); // click ATN num
		
		
		new Select (driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_OwnerShipChange"))).selectByVisibleText("Yes");// select Yes
		Thread.sleep(60000);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_OwnerShipChangeDate")).sendKeys(Common.convertSysdate());// send todays date
		//Thread.sleep(2000);
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_PreviousProviderId")).sendKeys("1212121212");// send provider-  anything is good
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_PreviousTaxId")).sendKeys("121221212");// send previous tax ID-  anything is good
		new Select (driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_PreviousTaxIdType"))).selectByVisibleText("SSN");// select SSN
		//driver.findElement(By.id("MMISForm:MMISBodyContent:ApplicationNavigatorPanel:providerapplicationnavigator:SAVEALL")).click();// send previous tax ID-  anything is good
		Common.save();
    //Example-Store provider in the DB
    String SelSql="select * from r_day2 where TC = '44161'";
    String col="ID";
    String DelSql="delete from r_day2 where TC = '44161'";
    String InsSql="insert into r_day2 values ('44161', '"+SAK_ATN+"', 'SAK_ATN Number for provider ', '"+ Common.convertSysdate()+"')";
    Common.insertData(SelSql, col, DelSql, InsSql);
    System.out.println(SAK_ATN+ "------SAK_ATN saved in r_day2");
			
    }

@Test
public void test44162a() throws Exception {
	TestNGCustom.TCNo="44162a";

     log("//TC 44162a");
			 
	 String relEntityName=Common.generateRandomName();
	 String adr1="101 FEDERAL STREET";
	 String city="BOSTON";
	 String state ="Massachusetts";
	 String stateab ="MA";
	 String zip="02110";
	 String phone="6176176177";
	 String email="ansh90@gmail.com";
	
	//Check Enrollment
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
	driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	

    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("01 - REQUESTED");
    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_RequestTypeCode"))).selectByVisibleText("Mail");
	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ReceivedDate")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ReceivedDate")).sendKeys(Common.convertSysdate());
	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderAppSpacertName")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderAppSpacertName")).sendKeys(relEntityName);
	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Street1")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Street1")).sendKeys(adr1); 
	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_City")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_City")).sendKeys(city);
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_State"))).selectByVisibleText(stateab);
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ZipCode")).clear();
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ZipCode")).sendKeys(zip);
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_PhoneNumber")).clear();
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_PhoneNumber")).sendKeys(phone);
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ContactName")).clear();
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_ContactName")).sendKeys(relEntityName);
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_AddressEmail")).clear();
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_AddressEmail")).sendKeys(email);
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_CommPrefCode"))).selectByVisibleText("email");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_RelationshipEntityType"))).selectByVisibleText("0001 Medical Service Provider"); 
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplication_CdeProviderType"))).selectByVisibleText("01 PHYSICIAN");
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_TaxIDNumber")).clear();
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_TaxIDNumber")).sendKeys(Common.generateRandomTaxID());
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_TaxIDType"))).selectByVisibleText("SSN");
    
  Common.save();
    //Store ATN
  String ATN = driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:ApplicationInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[2]/td[2]")).getText();
    
    //Ready for review
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("02 - READY FOR REVIEW");
  Common.save();
    
    //IN PROCESS
  driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
  driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(ATN);
  Common.search();
  driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("12 - IN PROCESS");
  Common.save();
  
    //READY TO ENROLL
  driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
  driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(ATN);
  Common.search();
  driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("33 - READY TO ENROLL");
  Common.save();
  
    
    //ID Issued
  driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
  driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(ATN);
  Common.search();
  driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
 // driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:id_EnrollRE")).click();
  driver.findElement(By.linkText("Enroll Provider")).click();
  
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_OwnershipType"))).selectByVisibleText("Corporation");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_DelegatedCredentIndicator"))).selectByVisibleText("No");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ManagedCareGroup"))).selectByVisibleText("No");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ProviderLegalEntityType"))).selectByVisibleText("BUSINESS CORPORATION");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_BillingAgent"))).selectByVisibleText("No");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_CommercialInsurance"))).selectByVisibleText("No");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ProviderOwnershipClass"))).selectByVisibleText("COUNTY");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_ManagedCareOrganization"))).selectByVisibleText("No");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollBaseInformationForEnrollPanel:ProviderDataPanel_HospitalAffiliation"))).selectByVisibleText("No");
  
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_CityTownCode"))).selectByVisibleText("ACTON");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_ProviderOrganizationType"))).selectByVisibleText("CHAIN");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_UcpIndicator"))).selectByVisibleText("No");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_Coverage24hr"))).selectByVisibleText("No");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_BypassTPL"))).selectByVisibleText("No");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_OrganizationCode"))).selectByVisibleText("Corporation");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollServiceLocationFromBasePanel:ProviderServiceLocationDataPanel_AcceptNewMembers"))).selectByVisibleText("No");
 
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:ProviderEnrollPublicHealthProgramEligibility_NewButtonClay:ProviderEnrollPublicHealthProgramEligibilityList_newAction_btn")).click();
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:EnrollProgramDropDown_2"))).selectByVisibleText("ACUTE OUTPATIENT");
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderEnrollPublicHealthProgramEligibilityPanel:ProviderPublicHealthProgramEligibilityDataPanel_PrEnrollStatus"))).selectByVisibleText("ACTIVE - Pay");
  
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:ProviderSpecialtyAdd_NewButtonClay:ProviderSpecialtyAddList_newAction_btn")).click();
//driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:ProviderSpecialtySearchControl_CMD_SEARCH")).click();
  driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:ProviderSpecialtySearchControl_CMD_SEARCH']/img")).click(); //Added /img at end, and changed to xpath for chrome because above line was not working without adding /img.
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderTypeAddPanel:ProviderSpecialtyAddPanel:_id149:TypeSpecialitiySearchResults_0:column1Value")).click();

  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoNameID")).clear();
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoNameID")).sendKeys(relEntityName);
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_Email")).clear();
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_Email")).sendKeys(email);
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailStrt1")).clear();
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailStrt1")).sendKeys(adr1);
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailCity")).clear();
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailCity")).sendKeys(city);
  new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailState"))).selectByVisibleText(stateab);
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailZip")).clear();
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_MailZip")).sendKeys(zip);
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_PhoneNumber")).clear();
  driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationTaxIDPanel:IrsW9InfoDataPanel_PhoneNumber")).sendKeys(phone);
  Common.save();

    
   //Enrolled
 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ApplicationSearch")).click();
 driver.findElement(By.id("MMISForm:MMISBodyContent:EnrollmentSearchBean_CriteriaPanel:EnrollmentSearch_ATN_Id")).sendKeys(ATN);
 Common.search();
 driver.findElement(By.xpath("//*[contains(@id,'MMISForm:MMISBodyContent:EnrollmentSearchResults_0:')]")).click();
 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplicationDataPanel_Status1Code"))).selectByVisibleText("34 - ENROLLED");
 Common.save();
 String enlPrv=driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderApplicationPanel:ProviderApplProvIDSearchControl")).getAttribute("value");
 System.out.println("Provider Id enrolled from portal is "+enlPrv);
    
    

		
		//Insert this ATTN in r_day2 db
    //Example-Store provider in the DB
    String SelSql="select * from r_day2 where TC = '44162'";
    String col="ID";
    String DelSql="delete from r_day2 where TC = '44162'";
    String InsSql="insert into r_day2 values ('44162', '"+ATN+"', 'Prov ATN after enrolling', '"+ Common.convertSysdate()+"')";
    Common.insertData(SelSql, col, DelSql, InsSql);
    System.out.println(ATN+ "-------ATN number saved in r_day2");
    
    String SelSql2="select * from r_day2 where TC = '44162' and DES = 'Prov ID after Enrolling'";
    String col2="ID";
    String DelSql2="delete from r_day2 where TC = '44162' and DES = 'Prov ID after Enrolling'";
    String InsSql2="insert into r_day2 values ('44162', '"+enlPrv+"', 'Prov ID after Enrolling', '"+ Common.convertSysdate()+"')";
    Common.insertData(SelSql2, col2, DelSql2, InsSql2);
    System.out.println(ATN+ "-------ATN number saved in r_day2");
}


@Test
public void test44160() throws Exception {
	TestNGCustom.TCNo="test44160";

    log("//TC test44160");
    //Verify unix report
    String command, error;
   //validate first file prvj9045
           
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjw601.*.* | tail -1";
    error = "There was no prvjw601*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job prvjw601 is : "+fileName);
    
    //Verify job completed
    command = "grep 'completed' "+fileName;
     
    error = "The selected log file "+fileName+" is not correct/or did not have completed message ";
    log(Common.connectUNIX(command, error));
    
    
    // verify input data file
    command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/prv0601w.rpt.* | tail -1";
    error = "There was no prv0601w.rpt.* data file found";
    
    String dataFile = Common.connectUNIX(command, error);
    dataFile=dataFile.substring(dataFile.indexOf("/"), dataFile.length());
    
    
    dataFile = Common.chkCompress(dataFile);
    log("The data filename for job prvjw601 is : "+dataFile);
    System.out.println("The data filename for job prvjw601 is : "+dataFile);
    
    //Get data from tables that needs to be verified in the report (Newly Enrolled Providers Report)
		sqlStatement ="SELECT * FROM (SELECT  a.ID_CASE, d.DSC_CASE_TYPE, c.DSC_CASE_STATUS FROM  T_PR_CASE a, T_PR_CASE_LVL b,  T_PR_CASE_STAT c, T_PR_CASE_TYPE d "
				+ "WHERE b.CDE_CASE_LVL = a.CDE_CASE_LEVEL AND c.CDE_CASE_STATUS = a.CDE_CASE_STATUS AND d.CDE_CASE_TYPE = a.CDE_CASE_TYPE AND a.CDE_CASE_STATUS = '044' "
				+ "ORDER BY a.ID_CLK_CASE_COORD,a.ID_CASE) WHERE ROWNUM =1";
		
		colNames.add("ID_CASE");
		colNames.add("DSC_CASE_TYPE");
		colNames.add("DSC_CASE_STATUS");
		colValues = Common.executeQuery(sqlStatement, colNames);
		
		String ID_CASE = colValues.get(0);
	String DSC_CASE_TYPE = colValues.get(1);
	String DSC_CASE_STATUS = colValues.get(2);
	
	command = "grep "+ID_CASE+" "+dataFile;
    error = "The selected file "+dataFile+" does not have ID_CASE in it";
	Common.connectUNIX(command, error);
	
	command = "grep "+DSC_CASE_TYPE+" "+dataFile;
     error = "The selected file "+dataFile+" does not have DSC_CASE_TYPE in it";
     Common.connectUNIX(command, error);
	
     command = "grep "+DSC_CASE_STATUS+" "+dataFile;
     error = "The selected file "+dataFile+" does not have DSC_CASE_STATUS in it";
     Common.connectUNIX(command, error); 
    
     //following does not work not sure why
     command = "grep 'CASE PROFILE TRACKING REPORT' "+dataFile;
     error = "The selected file "+dataFile+" does not have CASE PROFILE TRACKING REPORT text in it";
     Common.connectUNIX(command, error); 
	
     command = "grep '"+Common.convertSysdatecustom(-1)+"' "+dataFile;
     error = "The selected file "+dataFile+" does not have yesterday's (i.e wednesday) date text in it";
     Common.connectUNIX(command, error); 
 

    }

@Test
public void test44164a() throws Exception {
	TestNGCustom.TCNo="44164a";

    log("//TC 44164a");
   //get license from DB
     
  	//sqlStatement="select * from (select l.num_prov_lic, c.ID_PROVIDER  from t_pr_hb_lic l, t_pr_appln a , T_PR_PROV c where l.cde_status1 = '01' and l.num_ssn = a.num_tax_id and  a.sak_prov = c.sak_prov order by l.dte_end desc ) WHERE LENGTH(num_prov_lic) > 3 and ROWNUM < 2";
    sqlStatement = "select l.num_prov_lic, p.id_provider, t.cde_service_loc from t_pr_prov p, t_pr_hb_lic l, t_pr_type t WHERE p.sak_prov = t.sak_prov and t.num_prov_lic = l.num_prov_lic and l.cde_status1 = '01' and LENGTH(l.num_prov_lic) > 3 and l.dte_end = 22991231 and rownum < 2";
    colNames.add("num_prov_lic");	
  	colNames.add("ID_PROVIDER");
  	colValues = Common.executeQuery(sqlStatement, colNames);
  	System.out.println(sqlStatement);
  	System.out.println(colValues.get(0)+"  this is prov lic");
  	String num_prov_lic = colValues.get(0);
  	String ID_PROVIDER = colValues.get(1);
  	System.out.println(num_prov_lic + "   "+ID_PROVIDER);
	
	updateLicenseDate(num_prov_lic); // find and update license date to yesterday
		//Insert this ATTN in r_day2 db
    //Example-Store provider in the DB
    String SelSql="select * from r_day2 where TC = '44164'";
    String col="ID";
    String DelSql="delete from r_day2 where TC = '44164'";
    String InsSql="insert into r_day2 values ('44164', '"+ID_PROVIDER+"', 'Prov id of expiring license number : "+num_prov_lic+"', '"+ Common.convertSysdate()+"')";
    Common.insertData(SelSql, col, DelSql, InsSql);
    System.out.println(ID_PROVIDER+ "------ID_PROVIDER saved in r_day2");

}
@Test
public void test44164b() throws Exception {
	TestNGCustom.TCNo="test44164b";

    log("//TC test44164b");
    //Verify unix report
    String command, error;
   //validate first file prvj9045
           
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvj9055.*.* | tail -1";
    error = "There was no prvj9055.*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job PRVJ9055 is : "+fileName);
    
    //Verify job completed
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJ9055 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
     
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
    
    //provider tab base app		 
    validateLetter("PRV-9055-R", "44164", "Routine License Expiring Batch Letter" );
		
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestSearchPanel_generateAndPrintWithButtonDispReset_btn")).click();// generate and print clicked
		
}

public void test44159() throws Exception {
	TestNGCustom.TCNo="test44159";

    log("//TC test44159");
    //Verify unix report
    String command, error;
   //validate first file prvj9045
           
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjd650.*.* | tail -1";
    error = "There was no prvj9055*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job prvjd650 is : "+fileName);
    
    //Verify job completed
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJD650 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
     
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
   
}

@Test
public void test44155a() throws Exception {
	TestNGCustom.TCNo="test44155a";

    log("//TC 44155a");
   //get license from DB
     
  	//sqlStatement="select * from (select l.num_prov_lic, c.ID_PROVIDER, l.name  from t_pr_hb_lic l, t_pr_appln a , T_PR_PROV c where l.cde_status1 = '01' and l.num_ssn = a.num_tax_id and  a.sak_prov = c.sak_prov order by l.dte_end desc ) WHERE LENGTH(num_prov_lic) > 3 and ROWNUM < 2";
    sqlStatement = "select l.num_prov_lic, p.id_provider, l.name from t_pr_prov p, t_pr_hb_lic l, t_pr_type t WHERE p.sak_prov = t.sak_prov and t.num_prov_lic = l.num_prov_lic and l.cde_status1 = '01' and LENGTH(l.num_prov_lic) > 3 and l.dte_end = 22991231 and rownum < 2";
    colNames.add("num_prov_lic");	
  	colNames.add("ID_PROVIDER");
  	
  	colValues = Common.executeQuery(sqlStatement, colNames);
  	String num_prov_lic = colValues.get(0);
  	String ID_PROVIDER = colValues.get(1);
  	
  	String NAME = updateLicenseDate(num_prov_lic); // find and update license date to yesterday
	
		//Insert this ATTN in r_day2 db
    String SelSql="select * from r_day2 where TC = '44155'";
    String col="ID";
    String DelSql="delete from r_day2 where TC = '44155'";
    // following saves prov lic, name and todays date
    String InsSql="insert into r_day2 values ('44155', '"+num_prov_lic+"','"+NAME+"', '"+ Common.convertSysdate()+"')";
    //String InsSql="insert into r_day2 values ('22463', '"+lName+"', 'Enrolled Prov lName', '"+Common.convertSysdate()+"')";
    Common.insertData(SelSql, col, DelSql, InsSql);
    System.out.println(num_prov_lic+ "------Provider License is saved in r_day2");

}
@Test
public void test44155b() throws Exception {
	TestNGCustom.TCNo="test44155b";

    log("//TC test44155b");
    //Verify unix report
    String command, error;
     
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjm404.*.* | tail -1";
    error = "There was no prvjm404*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job prvjm404 is : "+fileName);
    
    //Verify job completed
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJM404 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
     
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
   
    // verify input data file
    command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/prm40401.rpt.* | tail -1";
    error = "There was no prm40401.rpt.* data file found";
    
    String dataFile = Common.connectUNIX(command, error);
    dataFile=dataFile.substring(dataFile.indexOf("/"), dataFile.length());
    
    
    dataFile = Common.chkCompress(dataFile);
    log("The data filename for job prvjm404 is : "+dataFile);
    System.out.println("The data filename for job prvjm404 is : "+dataFile);
    
    //Get data from r_day2 table
		sqlStatement ="select * from r_day2 where TC = '44155'";
		colNames.add("ID");
		colNames.add("DATE_REQUESTED");
		colNames.add("DES"); // this is the name of lic
		colValues = Common.executeQuery1(sqlStatement, colNames);
		System.out.println(colValues.get(0)+"  "+colValues.get(1)+"  "+colValues.get(2));
		String Lic_Provider = colValues.get(0);
		String Date_changed = colValues.get(1);
		String name = colValues.get(2); // here it will have name in it

		String Clerk_ID = "BGENER0";
		
		command = "grep 'LICENSE ACTIVITY REPORT' "+dataFile; // validate which report is it 
    error = "The selected file "+dataFile+" does not have Lic_Provider in it";
	log(Common.connectUNIX(command, error));
	
	
	command = "grep '"+Lic_Provider+"' "+dataFile; // validate provider License
    error = "The selected file "+dataFile+" does not have Lic_Provider in it expected : "+Lic_Provider;
	log(Common.connectUNIX(command, error));
	
     command = "grep '"+Clerk_ID+"' "+dataFile;
     error = "The selected file "+dataFile+" does not have Clerk_ID in it";
     log(Common.connectUNIX(command, error)); 
  
     command = "grep "+"U"+" "+dataFile;
     error = "The selected file "+dataFile+" does not have ACT : U in it";
     log(Common.connectUNIX(command, error)); 

     command = "grep "+name+" "+dataFile;;
     error = "The selected file "+dataFile+" does not have name in it";
     log(Common.connectUNIX(command, error)); 
   
}

@Test
public void test44154() throws Exception {
	TestNGCustom.TCNo="test44154";

    log("//TC test44154");
    //Verify unix report
    String command, error;
     
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjw602.*.* | tail -1";
    error = "There was no prvjw602.*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job prvjw602 is : "+fileName);
    
    //Verify job completed
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJW602 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
     
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
   
    // validating data file is following
  	command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/prv0602w.rpt.* | tail -1";
    error = "There was no prv0602w.rpt.* data file found";
    
    String dataFile = Common.connectUNIX(command, error);
    dataFile=dataFile.substring(dataFile.indexOf("/"), dataFile.length());
    
    
    dataFile = Common.chkCompress(dataFile);
    log("The data filename for job prvjw602 is : "+dataFile);
    System.out.println("The data filename for job prvjw602 is : "+dataFile);
    
    //Get data from  table
    sqlStatement="select a.SAK_CASE, b.DSC_CASE_TYPE, a.ID_CLK_ASSIGNED_TO, c.dsc_case_status from t_pr_case a, t_pr_case_type b, T_PR_CASE_STAT c"
  			+ " where a.cde_case_status = 44 and a.ID_CLK_ASSIGNED_TO != ' '"
  			+ " and b.CDE_CASE_TYPE = a.CDE_CASE_TYPE"
  			+ " and a.cde_case_status = c.cde_case_status"
  			+ " and ROWNUM < 2";
    
    System.out.println(sqlStatement);
  	colNames.add("SAK_CASE");	
  	colNames.add("DSC_CASE_TYPE");
  	colNames.add("ID_CLK_ASSIGNED_TO");	
  	colNames.add("DSC_CASE_STATUS");
  	colValues = Common.executeQuery(sqlStatement, colNames);
  	String SAK_CASE = colValues.get(0);
  	String DSC_CASE_TYPE = colValues.get(1);
  	String ID_CLK_ASSIGNED_TO = colValues.get(2).substring(1, 8); // get first 10 char as data file has first 8 
  	String DSC_CASE_STATUS = colValues.get(3).substring(1, 10); // get first 10 char as data file has first 10 
	
  	
  	command = "grep 'CASE MANAGEMENT SUMMARY REPORT' "+dataFile;
     error = "The selected file "+dataFile+" does not have SAK_CASE in it";
     Common.connectUNIX(command, error);
  	
	command = "grep "+SAK_CASE+" "+dataFile;
     error = "The selected file "+dataFile+" does not have SAK_CASE in it";
     Common.connectUNIX(command, error);
	
     command = "grep "+DSC_CASE_TYPE+" "+dataFile;
     error = "The selected file "+dataFile+" does not have DSC_CASE_TYPE in it";
     Common.connectUNIX(command, error); 
     
     command = "grep "+ID_CLK_ASSIGNED_TO+" "+dataFile;
     error = "The selected file "+dataFile+" does not have SAK_CASE in it";
     Common.connectUNIX(command, error);
	
     command = "grep "+DSC_CASE_STATUS+" "+dataFile;
     error = "The selected file "+dataFile+" does not have DSC_CASE_TYPE in it";
     Common.connectUNIX(command, error); 

}

public void test44153() throws Exception {
	TestNGCustom.TCNo="test44153";

    log("//TC test44153");
    //Verify unix report
    String command, error;
     
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjd312.*.* | tail -1";
    error = "There was no prvjd312*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job prvjd312 is : "+fileName);
    
    //Verify job completed
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/prvjd312 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
     
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
   
}



public void test44152() throws Exception {
	TestNGCustom.TCNo="test44152";

    log("//TC test44152");
    //Verify unix report
    String command, error;
     
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjw311.*.* | tail -1";
    error = "There was no prvjw311*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job prvjw311 is : "+fileName);
    
    //Verify job completed
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/prvjw311 completed "+Common.convertSysdatecustom(-1).substring(0, Common.convertSysdatecustom(-1).length()-4) + Common.convertSysdatecustom(-1).substring(Common.convertSysdatecustom(-1).length()-2)+"' "+fileName;
     
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
   
}

public void test44175() throws Exception {
	TestNGCustom.TCNo="test44175";

    log("//TC test44175");
    //Verify unix report
    String command, error;
     
    //Get Desired Filename
    command = "ls -ltr /customer/dsma/"+unixDir+"/logs/prvjd055.*.* | tail -1";
    error = "There was no prvjw311*.* log file found";
    
    String fileName = Common.connectUNIX(command, error);
    fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
    
    
    fileName = Common.chkCompress(fileName);
    log(" The log filename for job prvjd055 is : "+fileName);
    
    //Verify job completed
    command = "grep '/customer/dsma/"+unixDir.toLowerCase()+"/job/PRVJD055 completed "+Common.convertSysdate().substring(0, Common.convertSysdate().length()-4) + Common.convertSysdate().substring(Common.convertSysdate().length()-2)+"' "+fileName;
    
    error = "The selected log file "+fileName+" is not correct/or did not have the job completed message ";
    log(Common.connectUNIX(command, error));
   
}
	
	// ********************************************************************* 
	//					New Provider TCs - Abujot End
	// *********************************************************************
	
@Test
public void testSED() throws Exception {
	TestNGCustom.TCNo="testSED";

	//Verify unix report
			String command, error;

			//Get Desired Filename

//			command = "sed -u -n \"/id_provider=\\\"110000614\\\"/,/<\\/Provider>/p\" /customer/dsma/mod/data01/tpr90001.xml.0016"; //MO file
//			command = "sed -u -n \"/id_provider=\\\"110000614\\\"/,/<\\/Provider>/p\" /customer/dsma/acc/data01/tpr90001.xml.0014"; //UAT file
			
			command = "sed -u -n \"/100000970069/,/<\\/Member>/p\" /customer/dsma/acc/data01/eld06540.xml.0464";
			
//			command = "sed -u -n \"/id_provider=\\\"110000614\\\"/,/<\\/Provider>/p\" /customer/dsma/acc/data01/tpr90001.xml.0014"; //UAT file


			error = "There was no report file found for the command: "+command;
			String outfile_latterData = Common.connectUNIX(command, error);
//			command = "cat /home/agandhi/testRptFormat/latter.xml";
//			String latterData = Common.connectUNIX(command, error);
			log(outfile_latterData);


}
    
	public static void searchProv(String prov) {
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(prov);
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	}
	
	public static void df30872_saveAll() {
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		if ((driver.findElements(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).size())>0) {
			if (env.equals("POC"))
				driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Skip Change']")).click(); //This is because address validation server is not available in POC
			else
				driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).click();
		}
		if (driver.findElements(By.cssSelector("td.message-text")).size()==0) //This if loop is added if address validation does not return save successful
			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Workitem Status Update Successful."), "Save NOT successful Error Message: "+message+"..."); //Assert modified by Priya to include Login failure message in reports
	}
	
	public void validateLetterFor44169(String letterNameToBeValidated, String testCase, String letter) throws SQLException, InterruptedException {
		 sqlStatement = "Select * From R_Day2 Where Tc = '"+testCase+"'";
	        colNames.add("ID");
	        colNames.add("DATE_REQUESTED");
	        colValues = Common.executeQuery1(sqlStatement, colNames);
	        
	       String ID = colValues.get(0);
		   String DATE_REQUESTED = colValues.get(1);
		   
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
		
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRptsAndLettersNavigatorPanel:ProviderReportsNavigator:GRP_g2")).click();// click letters  chnage to link text
		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRptsAndLettersNavigatorPanel:ProviderReportsNavigator:ITM_n107")).click();// click letters request search chnage to link text
		
		new Select (driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_LetterList"))).selectByVisibleText(letter);
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestSearch_ATN_Id")).sendKeys(ID);// send ATN IN ATN field
		Thread.sleep(6000);
		
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:SEARCH")).click();// click search
		
		driver.findElement(By.xpath("//*[@id=\"MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults\"]/thead/tr/th[4]")).click(); // click member to sort
		driver.findElement(By.xpath("//*[@id=\"MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:_id51\"]/span")).click(); // click once req date to sort by decending order
		driver.findElement(By.xpath("//*[@id=\"MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:_id51\"]/span")).click(); // click twice req date to sort by decending order
		
		String letterName = driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id48")).getText(); // should be PRV-9045-R
		Assert.assertTrue(letterName.equals(letterNameToBeValidated), "PRV-9045-R not found : but found : "+ letterName);
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id48")).click();// click first letter after sorting
		
		String ATN_Num = driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestDataPanel_SakAtn")).getText();// get ATN
		String date = driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestDataPanel_RequestDate")).getText();// get request date 
		
	
		Assert.assertTrue(ATN_Num.equals(ID), "ATN number is different expected is : "+ ID + " But found : "+ ATN_Num); 
		Assert.assertTrue(date.equals(Common.convertSysdatecustom(-1)), "Date requested not found expected is: "+Common.convertSysdatecustom(-1)+" but found : "+ date);

		
		//Assert.assertTrue(date.equals(DATE_REQUESTED), "Date requested not found expected is: "+DATE_REQUESTED+" but found : "+ date);
		
	}
	
	public void validateLetter(String letterNameToBeValidated, String testCase, String letter) throws SQLException {
		sqlStatement = "Select * From R_Day2 Where Tc = '"+testCase+"'";
	        colNames.add("ID");
	        colNames.add("DATE_REQUESTED");
	        colValues = Common.executeQuery1(sqlStatement, colNames);
	        String ID = colValues.get(0);
		    String DATE_REQUESTED = colValues.get(1);
		    
		    String ATN_Num = null;
		    String date = null;
		    String providerID = null;
		    
 		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
 		
 		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRptsAndLettersNavigatorPanel:ProviderReportsNavigator:GRP_g2")).click();// click letters  chnage to link text
 		driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRptsAndLettersNavigatorPanel:ProviderReportsNavigator:ITM_n107")).click();// click letters request search chnage to link text
 		
 		new Select (driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_LetterList"))).selectByVisibleText(letter);
 		
 		if (testCase.equals("44162") || testCase.equals("44164")) {
 	 		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_ProviderId")).sendKeys(ID);// send provider ID in provider field
 		}
 		
 		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:SEARCH")).click();// click search
 		
 		if (!testCase.equals("44162") && !testCase.equals("44164")) {
 			driver.findElement(By.xpath("//*[@id=\"MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults\"]/thead/tr/th[4]")).click(); // click member to sort
 	 		driver.findElement(By.xpath("//*[@id=\"MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:_id51\"]/span")).click(); // click once req date to sort by decending order
 	 		driver.findElement(By.xpath("//*[@id=\"MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:_id51\"]/span")).click(); // click twice req date to sort by decending order
 	 		
 	 		String letterName = driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id48")).getText(); // should be PRV-9045-R
 	 		Assert.assertTrue(letterName.equals(letterNameToBeValidated), "PRV-9045-R not found : but found : "+ letterName);
 	 		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id48")).click();// click first letter after sorting
 			
 	 		 ATN_Num = driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestDataPanel_SakAtn")).getText();// get ATN
 	 		 date = driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestDataPanel_RequestDate")).getText();// get request date 
 		}
	}
 		
 		public String updateLicenseDate(String num_prov_lic) throws InterruptedException {		
 			//get license and update effective date to yesterday's date.
 			driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();// click related data 
 			driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:GRP_RelatedDataOther")).click();// click other
 			driver.findElement(By.id("MMISForm:MMISBodyContent:ProvRelatedDataNavigatorPanel:ProvRelatedDataNavigatorId:ITM_n21")).click();// click License
 			driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderHealthBoardLicenseSearchPanel:ProviderHealthBoardLicenseBean_CriteriaPanel:ProviderHealthBoardLicenseBeanSearchCrit_License")).sendKeys(num_prov_lic); // enter LIcense
 			driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderHealthBoardLicenseSearchPanel:ProviderHealthBoardLicenseBean_CriteriaPanel:SEARCH")).click(); // click search
 			driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderHealthBoardLicenseSearchPanel:ProviderHealthBoardLicenseBeanSearchList_0:ProviderHealthBoardLicenseBean_ColValue_providerLicenseNumber")).click(); // click first license row
 			String name = driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderHealthBoardLicenseSearchPanel:ProviderHealthBoardLicenseBeanSearchList_0:ProviderHealthBoardLicenseBean_ColValue_name")).getText(); // get license name and save in text 
 			driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderHealthBoardLicenseSearchPanel:ProviderHealthBoardLicenseDataPanel_EndDate")).clear(); // clear the end date
 			driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderHealthBoardLicenseSearchPanel:ProviderHealthBoardLicenseDataPanel_EndDate")).sendKeys(Common.convertSysdatecustom(-1)); // enter yesterday date
 			Common.save();
 			return name;
 		}
 		
}
