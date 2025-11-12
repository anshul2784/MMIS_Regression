package newMMIS_Subsystems;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ newMMIS_Subsystems.TestNGCustom.class })
public class MemberDay2 extends Login{
	
	String otherid, fname,ssn,casenum,dob,adr1,city,zip,dep,aid,agency, fileMem = " ", lastName = "MAZDAZ", beginDate="", openRsn="01", caseStatus="1";
	public static boolean endElig = false;
	
	@BeforeTest
    public void memberDay2Startup() throws Exception {
    	log("Starting Member Day 2 Subsystem......");
    }
	
	@BeforeMethod
	public void LoginCheck() throws Exception {
		Common.resetBase();
		testCheckDBLoginSuccessful();	
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
	}
	
    @Test
    public void test22554b_23403() throws Exception{
    	TestNGCustom.TCNo="22554b_23403";
    	log("//TC 22554b_23403");
    		 	
	 	//get the 2 members that were linked
		sqlStatement = "select * from R_MEMBERLINK";
		colNames.add("INACTIVE_ID");
		colNames.add("ACTIVE_ID");
		colNames.add("LAST");
		colNames.add("FIRST");
		colNames.add("FILEDATE");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member link request");

		String inactMem = colValues.get(0);
		String actMem = colValues.get(1);
		String lName = colValues.get(2);
		String fName = colValues.get(3);
		String fileDate = colValues.get(4);
		
		log("Inactive Member: "+inactMem+" Active Member: "+actMem+" Last Name: "+lName+" First name: "+fName+" File Date: "+fileDate);
		
		//Verify member link has occured
        driver.findElement(By.xpath("//*[@id='MMISForm:MMISMenu:_MENUITEM_Search']")).click();
        driver.findElement(By.xpath("//*[@id='MMISForm:MMISMenu:_MENUITEM_Search']")).click();
        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(inactMem);
        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
        System.out.println(driver.findElement(By.cssSelector("span.listTextBlack")).getText());
        System.out.println("This Member ["+inactMem+" ] is linked to another member whose Member ID is: ["+actMem+" ] ");

        Assert.assertTrue(driver.findElement(By.cssSelector("span.listTextBlack")).getText().equals("This Member ["+inactMem+" ] is linked to another member whose Member ID is: ["+actMem+" ]"),"Member link did not happen");

        //Select Inactive member
        driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
        Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[17]/td[2]")).getText().equals("Inactive"),"Inactive member link status is not Inactive");
    	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_Navigatoritem1")).click();
        Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:LinkHistoryPanel:LinkHistoryList_0:LinkHistoryBean_ColValue_currentId")).getText().equals(actMem),"Active member ID not present");
        Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:LinkHistoryPanel:LinkHistoryList_0:LinkHistoryBean_ColValue_previousId")).getText().equals(inactMem),"Inactive member ID not present");
        Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:LinkHistoryPanel:LinkHistoryList_0:LinkHistoryBean_ColValue_linkStatus")).getText().equals("L - Link"),"Link Status not set to L-Link");
        Common.cancelAll();
        
        //Select Active member
        Member.memberSearch(actMem);
        Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[17]/td[2]")).getText().equals("Active"),"Active member link status is not Active");
    	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_Navigatoritem1")).click();
        Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:LinkHistoryPanel:LinkHistoryList_0:LinkHistoryBean_ColValue_currentId")).getText().equals(actMem),"Active member ID not present");
        Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:LinkHistoryPanel:LinkHistoryList_0:LinkHistoryBean_ColValue_previousId")).getText().equals(inactMem),"Inactive member ID not present");
        Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:LinkHistoryPanel:LinkHistoryList_0:LinkHistoryBean_ColValue_linkStatus")).getText().equals("L - Link"),"Link Status not set to L-Link");

        //Verify unix report
		String command, error;

		//Get Desired Filename
//		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/eld01701.rpt.* | grep '"+Common.monthUNIX(fileDate)+" "+Common.dayUNIX(fileDate)+"'";
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/eld01701.rpt.* | tail -1"; //grabs the latest gdg of the file

		error = "There was no report file found for the command: "+command;
		String fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);		
		log(" The member report filename is: "+fileName);

		//Verify duplicate member data in file
		command = "grep "+inactMem+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired members that were linked";
		String outputText = Common.connectUNIX(command, error);

		Assert.assertTrue(outputText.contains(inactMem), "Inactive Member not found in file");
		Assert.assertTrue(outputText.contains(actMem), "Active Member not found in file");
		Assert.assertTrue(outputText.contains(lName), "Active Member Last name not found in file");
		Assert.assertTrue(outputText.contains(fName), "Active Member First name not found in file");

    }
    
    @Test
    public void test23472b() throws Exception{
    	TestNGCustom.TCNo="23472b";
    	log("//TC 23472b");
    	
	 	//get the tracking number
		sqlStatement = "select * from R_MMQ";
		colNames.add("TRKNO");
		colNames.add("MEMBER");
		colNames.add("PROV");
		colNames.add("SUBMIT_DATE");
		colNames.add("FNAME");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member link request");

		String trkNo = colValues.get(0);
		String member = colValues.get(1);
		String prov = colValues.get(2);
		String submitDT = colValues.get(3);
		String fName = colValues.get(4);
		
		log("Tracking: "+trkNo+" Member: "+member+" "+" Provider: "+prov+" Submit date: "+submitDT+" First Name: "+fName);
		
    	Common.portalLogin();
//		driver.findElement(By.linkText("Manage Members")).click();
//		driver.findElement(By.linkText("Long Term Care")).click();
		driver.findElement(By.linkText("Manage Batch Files")).click();
//		driver.findElement(By.linkText("Download MMQ Responses")).click();
		driver.findElement(By.linkText("Download Batch File")).click();
		List<WebElement> list = new Select(driver.findElement(By.xpath("//select[contains(@id,'providerID')]"))).getOptions();
		for (WebElement j:list) 
			if ((j.getText()).contains("110025810")) {
				j.click();
				break;
			}
		driver.findElement(By.xpath("//input[contains(@id,'trackingNo')]")).sendKeys(trkNo);
		driver.findElement(By.xpath("//input[@class='buttonFunctional' and @alt='Search']")).click();
		
		//Select the MMQ response File
		driver.findElement(By.xpath("//*[contains(@id,'0:fileLink')]")).click();
    	Thread.sleep(2000);
 	
    	//Store and verify output response
    	FileFilter fileFilter = new WildcardFileFilter("MNARALA1*.txt");
    	File[] mmqfiles = new File(tempDirPath).listFiles(fileFilter);
    	String output=FileUtils.readFileToString(mmqfiles[0]);


    	String extectedResponse = "The MMQ batch file /edi/data/"+unixDir+"/web/MMQ/in/"+uid.toUpperCase().substring(0, 7)+"[0-9]{18}.FILE was processed on "+submitDT.substring(0,4)+"/"+submitDT.substring(4,6)+"/"+submitDT.substring(6,8)+". \r\n\r\n"+
    			 				  "The file contained 1 records:  1 were valid.  0  were invalid.\r\n"+
    			 				  "The file contained 0 records that had errors that prevented processing.\r\n"+
    			 				  "The file contained 1 MMQ's. 1 were approved.  0 were rejected. \r\n\r\n"+
    			 				  "PROVIDER ID/SVC LOC -- "+prov+"\r\n\r\n"+
    			 				  "MEMBER ID     MEMBER LAST NAME     MEMBER FIRST NAME  EFFECTIVE DATE  STATUS \r\n"+
    			 				  "------------  -------------------  -----------------  --------------  -------\r\n"+
    			 				  member+"\0  \0P\0                    \0\0"+fName+"\0           \0"+submitDT+"\0        \0APPROVED\0    \r\n\r\n"+  
    			 				  "The file has been processed successfully\r\n"+
    			 				  "";
    	System.out.println("Begin\n"+output+"\nend\nBegin\n"+extectedResponse+"\nend");
    	Assert.assertTrue(output.matches(extectedResponse), "Did not get the expected MMQ response");
    	
    	//Verify Base App
    	Member.validateMMQ(member, "S", "259.6");
    	//Verify LoC End Date
    	if (Integer.parseInt(Common.convertSysdate().substring(6, 10))%4==0) //Leap year check
        	if(!(driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareList_0:LevelOfCareBean_ColValue_endDate")).getText().substring(0, 5).equals("08/31"))&&!(driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareList_0:LevelOfCareBean_ColValue_endDate")).getText().substring(0, 5).equals("02/29")))
        		Assert.assertTrue(false, "LoC C End date mismatch");
    	else
        	if(!(driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareList_0:LevelOfCareBean_ColValue_endDate")).getText().substring(0, 5).equals("08/31"))&&!(driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareList_0:LevelOfCareBean_ColValue_endDate")).getText().substring(0, 5).equals("02/28")))
        		Assert.assertTrue(false, "LoC C End date mismatch");

    }
    
    @Test
    public void test23375b() throws Exception{
    	TestNGCustom.TCNo="23375b";
    	log("//TC 23375b");
    	
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_EPSDT")).click();
	 	
	 	//get the member for EPSDT Letter
		sqlStatement = "select * from R_EPSDT_LETTER";
		colNames.add("MEMBER");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member enrolled for EPSDT letter");

		String partMem = colValues.get(0);
		log("EPSDT Letter Member is: "+partMem);
		
		//Verify EPSDT Letter
        driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
        driver.findElement(By.id("MMISForm:MMISBodyContent:EPSDTRptsAndLettersNavigatorPanel:EPSDTRptsAndLettersNavigationId:ITM_n4")).click();
        driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_MemberId")).sendKeys(partMem);
        Common.search();
        driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id58")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Generate and Print']")).click();

    }
    
    @Test
    public void test32960b() throws Exception{
    	TestNGCustom.TCNo="32960b";
    	log("//TC 32960b");
    	
	 	//get the 2 members that were linked, and clinical information data
		//INACTIVE MEMBER
		sqlStatement = "select * from r_day2 where TC = '32960' and DES='Inactive member'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no Inactive member found");
		String inactMem = colValues.get(0);
		log("Inactive Member is: "+inactMem);
		
		//ACTIVE MEMBER
		sqlStatement = "select * from r_day2 where TC = '32960' and DES='active member'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no active member found");
		String actMem = colValues.get(0);
		log("Active Member is: "+actMem);
		
		//CLINICAL INFORMATION
		//ServiceRequestCode
		sqlStatement = "select * from r_day2 where TC = '32960' and DES='ServiceRequestCode'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no ServiceRequestCode found");
		String ServiceRequestCode = colValues.get(0);
		log("ServiceRequestCode is: "+ServiceRequestCode);
		
		//AssessmentDate
		sqlStatement = "select * from r_day2 where TC = '32960' and DES='AssessmentDate'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no AssessmentDate found");
		String AssessmentDate = colValues.get(0);
		log("AssessmentDate is: "+AssessmentDate);
		
		//EvalReason
		sqlStatement = "select * from r_day2 where TC = '32960' and DES='EvalReason'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no EvalReason found");
		String EvalReason = colValues.get(0);
		log("EvalReason is: "+EvalReason);
		
		//AssessmentReason
		sqlStatement = "select * from r_day2 where TC = '32960' and DES='AssessmentReason'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no AssessmentReason found");
		String AssessmentReason = colValues.get(0);
		log("AssessmentReason is: "+AssessmentReason);
		
		//PaceApprovalDate
		sqlStatement = "select * from r_day2 where TC = '32960' and DES='PaceApprovalDate'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no PaceApprovalDate found");
		String PaceApprovalDate = colValues.get(0);
		log("PaceApprovalDate is: "+PaceApprovalDate);

		//Verify member link has occurred
        driver.findElement(By.xpath("//*[@id='MMISForm:MMISMenu:_MENUITEM_Search']")).click();
        driver.findElement(By.xpath("//*[@id='MMISForm:MMISMenu:_MENUITEM_Search']")).click();
        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(inactMem);
        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
        Assert.assertTrue(driver.findElement(By.cssSelector("span.listTextBlack")).getText().equals("This Member ["+inactMem+" ] is linked to another member whose Member ID is: ["+actMem+" ]"),"Member link did not happen");
        log("Verified statement on panel: "+driver.findElement(By.cssSelector("span.listTextBlack")).getText());
        

        //Select Inactive member
        LoginCheck();
        Member.memberSearch(inactMem);
        Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[17]/td[2]")).getText().equals("Inactive"),"Inactive member link status is not Inactive");
        log("The Link Status on the Member Information panel is displayed as 'Inactive', for member "+inactMem);
        
        //Select Active member
        LoginCheck();
        Member.memberSearch(actMem);
        Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[17]/td[2]")).getText().equals("Active"),"Active member link status is not Active");
        log("The Link Status on the Member Information panel is displayed as 'Active', for member "+actMem);
        
        //Verify Clinical information is transferred
        driver.findElement(By.linkText("Clinical Information")).click();
        Assert.assertTrue(driver.findElement(By.xpath("//*[contains(@id,'0:') and contains(@id,'displayServiceCode')]")).getText().equals(ServiceRequestCode),"ServiceRequestCode is not "+ServiceRequestCode);
        Assert.assertTrue(driver.findElement(By.xpath("//*[contains(@id,'0:') and contains(@id,'assessmentDate')]")).getText().equals(AssessmentDate),"AssessmentDate is not "+AssessmentDate);
        Assert.assertTrue(driver.findElement(By.xpath("//*[contains(@id,'0:') and contains(@id,'assessReasonCode')]")).getText().substring(0, 1).equals(AssessmentReason),"AssessmentReason is not "+AssessmentReason);
        Assert.assertTrue(driver.findElement(By.xpath("//*[contains(@id,'0:') and contains(@id,'ratingCategoryScoreCode')]")).getText().substring(0, 1).equals(EvalReason),"EvalReason is not "+EvalReason);
        Assert.assertTrue(driver.findElement(By.xpath("//*[contains(@id,'0:') and contains(@id,'approvalPaceDate')]")).getText().equals(PaceApprovalDate),"PaceApprovalDate is not "+PaceApprovalDate);
        log("The Clinical Information for (FROM) member is copied to (TO) member");
    	    	
    }
    
//    @Test
//    public void test32959b() throws Exception{
//    	TestNGCustom.TCNo="32959b";
//    	log("//TC 32959b - Member link with ICO optout Test Case");
//    	
//	 	//get the 2 members that were linked, and ICO OPT out data
//		//INACTIVE MEMBER
//		sqlStatement = "select * from r_day2 where TC = '32959' and DES='Inactive member'";
//		colNames.add("ID");
//		colValues = Common.executeQuery1(sqlStatement, colNames);
//		if (colValues.get(0).equals("null"))
//		    throw new SkipException("Skipping this test because there was no Inactive member found");
//		String inactMem = colValues.get(0);
//		log("Inactive Member is: "+inactMem);
//		
//		//ACTIVE MEMBER
//		sqlStatement = "select * from r_day2 where TC = '32959' and DES='active member'";
//		colNames.add("ID");
//		colValues = Common.executeQuery1(sqlStatement, colNames);
//		if (colValues.get(0).equals("null"))
//		    throw new SkipException("Skipping this test because there was no active member found");
//		String actMem = colValues.get(0);
//		log("Active Member is: "+actMem);
//		
//		//icoOptoutEffDt
//		sqlStatement = "select * from r_day2 where TC = '32959' and DES='icoOptoutEffDt'";
//		colNames.add("ID");
//		colValues = Common.executeQuery1(sqlStatement, colNames);
//		if (colValues.get(0).equals("null"))
//		    throw new SkipException("Skipping this test because there was no icoOptoutEffDt found");
//		String icoOptoutEffDt = colValues.get(0);
//		log("icoOptoutEffDt is: "+icoOptoutEffDt);
//
//		//Verify member link has occurred
//        driver.findElement(By.xpath("//*[@id='MMISForm:MMISMenu:_MENUITEM_Search']")).click();
//        driver.findElement(By.xpath("//*[@id='MMISForm:MMISMenu:_MENUITEM_Search']")).click();
//        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
//        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(inactMem);
//        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
//        Assert.assertTrue(driver.findElement(By.cssSelector("span.listTextBlack")).getText().equals("This Member ["+inactMem+" ] is linked to another member whose Member ID is: ["+actMem+" ]"),"Member link did not happen");
//        log("Verified statement on panel: "+driver.findElement(By.cssSelector("span.listTextBlack")).getText());
//        
//
//        //Select Inactive member
//        LoginCheck();
//        Member.memberSearch(inactMem);
//        Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[16]/td[2]")).getText().equals("Inactive"),"Inactive member link status is not Inactive");
//        log("The Link Status on the Member Information panel is displayed as 'Inactive', for member "+inactMem);
//        
//        //Select Active member
//        LoginCheck();
//        Member.memberSearch(actMem);
//        Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[16]/td[2]")).getText().equals("Active"),"Active member link status is not Active");
//        log("The Link Status on the Member Information panel is displayed as 'Active', for member "+actMem);
//        
//        //Verify ICO OPT out data is transferred
//    	driver.findElement(By.linkText("ICO Opt Out")).click();
//    	String optOutEffDT=driver.findElement(By.xpath("//*[contains(@id,'MemberOptOutPanel') and contains(@id,'0:') and contains(@id,'effectiveDate')]")).getText();
//    	String optOutEndDT=driver.findElement(By.xpath("//*[contains(@id,'MemberOptOutPanel') and contains(@id,'0:') and contains(@id,'endDate')]")).getText();
//    	String optOutStatus=driver.findElement(By.xpath("//*[contains(@id,'MemberOptOutPanel') and contains(@id,'0:') and contains(@id,'optOutStatusDescription')]")).getText();
//
//        Assert.assertTrue(optOutEffDT.equals(icoOptoutEffDt),"icoOptoutEffDt is not "+icoOptoutEffDt+". It is "+optOutEffDT);
//        Assert.assertTrue(optOutEndDT.equals("12/31/2299"),"icoOptoutEffDt is not 12/31/2299. It is "+optOutEndDT);
//        Assert.assertTrue(optOutStatus.equals("Active"),"icoOptoutEffDt is not Active. It is "+optOutStatus);
//        log("ICO OPT out data is transferred.");
//
//        //Verify ICO MC assignment is end dated
//        Common.cancelAll();
//    	driver.findElement(By.linkText("PMP Assignment History - SU")).click();
//    	String mcPgm=driver.findElement(By.xpath("//*[contains(@id,'RePmpAssignSuHistoryPanel') and contains(@id,'0:') and contains(@id,'pgmHealthCode')]")).getText();
//    	String mcEffDT=driver.findElement(By.xpath("//*[contains(@id,'RePmpAssignSuHistoryPanel') and contains(@id,'0:') and contains(@id,'effectiveDate')]")).getText();
//    	String mcEndDT=driver.findElement(By.xpath("//*[contains(@id,'RePmpAssignSuHistoryPanel') and contains(@id,'0:') and contains(@id,'endDate')]")).getText();
//
//        Assert.assertTrue(mcPgm.equals("ICO"),"mcPgm is not ICO. It is "+mcPgm);
//        Assert.assertTrue(mcEffDT.equals(Common.getFirstDay()),"mcEffDT is not "+Common.getFirstDay()+". It is "+mcEffDT);
//        Assert.assertTrue(mcEndDT.equals(Common.getLastDay()),"mcEndDT is not "+Common.getLastDay()+". It is "+mcEndDT);
//        
//        driver.findElement(By.xpath("//*[contains(@id,'RePmpAssignSuHistoryPanel') and contains(@id,'0:') and contains(@id,'pgmHealthCode')]")).click();
//		String mcStopRsn=new Select(driver.findElement(By.xpath("//select[contains(@id,'StopReasonDropDownEntries')]"))).getFirstSelectedOption().getText();
//        Assert.assertTrue(mcStopRsn.contains("ZE"),"mcStopRsn is not ZE. It is "+mcStopRsn);
//        log("Verified the below MC information");
//        log("MC Assignment is "+mcPgm);
//        log("MC EffDT is "+mcEffDT);
//        log("MC EndDT is "+mcEndDT);
//        log("MC Stop rsn is "+mcStopRsn);
//
//    }
    
    @Test
    public void test32959b() throws Exception{
    	TestNGCustom.TCNo="32959b";
    	log("//TC 32959b - Member link with ICO optout Test Case");
    	
	 	//get the 2 members that were linked, and ICO OPT out data, and ICO MC Program eff date for TO member
		//INACTIVE MEMBER
		sqlStatement = "select * from r_day2 where TC = '32959' and DES='Inactive member'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no Inactive member found");
		String inactMem = colValues.get(0);
		log("Inactive Member is: "+inactMem);
		
		//ACTIVE MEMBER
		sqlStatement = "select * from r_day2 where TC = '32959' and DES='active member'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no active member found");
		String actMem = colValues.get(0);
		log("Active Member is: "+actMem);
		
		//icoOptoutEffDt
		sqlStatement = "select * from r_day2 where TC = '32959' and DES='icoOptoutEffDt'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no icoOptoutEffDt found");
		String icoOptoutEffDt = colValues.get(0);
		log("icoOptoutEffDt is: "+icoOptoutEffDt);
		
		//icoOptoutEndDt
		sqlStatement = "select * from r_day2 where TC = '32959' and DES='icoOptoutEndDt'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no icoOptoutEndDt found");
		String icoOptoutEndDt = colValues.get(0);
		log("icoOptoutEndDt is: "+icoOptoutEndDt);
		
		//icoOptoutStatus
		sqlStatement = "select * from r_day2 where TC = '32959' and DES='icoOptoutStatus'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no icoOptoutStatus found");
		String icoOptoutStatus = colValues.get(0);
		log("icoOptoutStatus is: "+icoOptoutStatus);
		
		//pmpEffDt
		sqlStatement = "select * from r_day2 where TC = '32959' and DES='pmpEffDt'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no pmpEffDt found");
		String pmpEffDt = colValues.get(0);
		log("pmpEffDt is: "+pmpEffDt);

		//Verify member link has occurred
        driver.findElement(By.xpath("//*[@id='MMISForm:MMISMenu:_MENUITEM_Search']")).click();
        driver.findElement(By.xpath("//*[@id='MMISForm:MMISMenu:_MENUITEM_Search']")).click();
        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(inactMem);
        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
        Assert.assertTrue(driver.findElement(By.cssSelector("span.listTextBlack")).getText().equals("This Member ["+inactMem+" ] is linked to another member whose Member ID is: ["+actMem+" ]"),"Member link did not happen");
        log("Verified statement on panel: "+driver.findElement(By.cssSelector("span.listTextBlack")).getText());
        

        //Select Inactive member
        LoginCheck();
        Member.memberSearch(inactMem);
        Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[17]/td[2]")).getText().equals("Inactive"),"Inactive member link status is not Inactive");
        log("The Link Status on the Member Information panel is displayed as 'Inactive', for member "+inactMem);
        
        //Select Active member
        LoginCheck();
        Member.memberSearch(actMem);
        Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[17]/td[2]")).getText().equals("Active"),"Active member link status is not Active");
        log("The Link Status on the Member Information panel is displayed as 'Active', for member "+actMem);
        
        //Verify ICO OPT out data is transferred
    	driver.findElement(By.linkText("ICO Opt Out")).click();
    	String optOutEffDT=driver.findElement(By.xpath("//*[contains(@id,'MemberOptOutPanel') and contains(@id,'0:') and contains(@id,'effectiveDate')]")).getText();
    	String optOutEndDT=driver.findElement(By.xpath("//*[contains(@id,'MemberOptOutPanel') and contains(@id,'0:') and contains(@id,'endDate')]")).getText();
    	String optOutStatus=driver.findElement(By.xpath("//*[contains(@id,'MemberOptOutPanel') and contains(@id,'0:') and contains(@id,'optOutStatusDescription')]")).getText();

        Assert.assertTrue(optOutEffDT.equals(icoOptoutEffDt),"icoOptoutEffDt is not "+icoOptoutEffDt+". It is "+optOutEffDT);
        Assert.assertTrue(optOutEndDT.equals(icoOptoutEndDt),"icoOptoutEffDt is not "+icoOptoutEndDt+". It is "+optOutEndDT);
        Assert.assertTrue(optOutStatus.equals(icoOptoutStatus),"icoOptoutEffDt is not "+icoOptoutStatus+". It is "+optOutStatus);
        log("ICO OPT out data is transferred.");

        //Verify ICO MC assignment is end dated
        Common.cancelAll();
    	driver.findElement(By.linkText("PMP Assignment History - SU")).click();
    	String mcPgm=driver.findElement(By.xpath("//*[contains(@id,'RePmpAssignSuHistoryPanel') and contains(@id,'0:') and contains(@id,'pgmHealthCode')]")).getText();
    	String mcEffDT=driver.findElement(By.xpath("//*[contains(@id,'RePmpAssignSuHistoryPanel') and contains(@id,'0:') and contains(@id,'effectiveDate')]")).getText();
    	String mcEndDT=driver.findElement(By.xpath("//*[contains(@id,'RePmpAssignSuHistoryPanel') and contains(@id,'0:') and contains(@id,'endDate')]")).getText();

        Assert.assertTrue(mcPgm.equals("ICO"),"mcPgm is not ICO. It is "+mcPgm);
        Assert.assertTrue(mcEffDT.equals(pmpEffDt),"mcEffDT is not "+pmpEffDt+". It is "+mcEffDT);
        Assert.assertTrue(mcEndDT.equals(Common.getLastDay()),"mcEndDT is not "+Common.getLastDay()+". It is "+mcEndDT);
        
        driver.findElement(By.xpath("//*[contains(@id,'RePmpAssignSuHistoryPanel') and contains(@id,'0:') and contains(@id,'pgmHealthCode')]")).click();
		String mcStopRsn=new Select(driver.findElement(By.xpath("//select[contains(@id,'RePmpAssignSuHistoryPanel') and contains(@id,'StopReason')]"))).getFirstSelectedOption().getText();
        Assert.assertTrue(mcStopRsn.contains("ZE"),"mcStopRsn is not ZE. It is "+mcStopRsn);
        log("Verified the below MC information");
        log("MC Assignment is "+mcPgm);
        log("MC EffDT is "+mcEffDT);
        log("MC EndDT is "+mcEndDT);
        log("MC Stop rsn is "+mcStopRsn);

    }
    
    @Test
    public void test32913b() throws Exception{
    	TestNGCustom.TCNo="32913b";
    	log("//TC 32913-Auto link the duplicate member IDs - 1 Test Case--Part B");
    	
	 	//get the 2 members that were linked
    	//First Member
		sqlStatement = "select * from r_day2 where TC = '32913' and DES='first member'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no active member found");
		String inactMem = colValues.get(0);
		log("The first member has become inactive. Inactive Member is: "+inactMem);

		//Second Member
		sqlStatement = "select * from r_day2 where TC = '32913' and DES='second member'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no active member found");
		String actMem = colValues.get(0);
		log("The second member has become active.Active Member is: "+actMem);
		
		//Last Name
		sqlStatement = "select * from r_day2 where TC = '32913' and DES='last name'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no active member found");
		String lName = colValues.get(0);
		log("Last Name is: "+lName);
		
		//First Name
		sqlStatement = "select * from r_day2 where TC = '32913' and DES='first name'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no active member found");
		String fName = colValues.get(0);
		log("First Name is: "+fName);

		
		//Verify member link has occurred
        driver.findElement(By.xpath("//*[@id='MMISForm:MMISMenu:_MENUITEM_Search']")).click();
        driver.findElement(By.xpath("//*[@id='MMISForm:MMISMenu:_MENUITEM_Search']")).click();
        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(inactMem);
        driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
        Assert.assertTrue(driver.findElement(By.cssSelector("span.listTextBlack")).getText().equals("This Member ["+inactMem+" ] is linked to another member whose Member ID is: ["+actMem+" ]"),"Member link did not happen");
        log("Verified statement on panel: "+driver.findElement(By.cssSelector("span.listTextBlack")).getText());
        
        //Select Inactive member
        LoginCheck();
        Member.memberSearch(inactMem);
        Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[17]/td[2]")).getText().equals("Inactive"),"Inactive member link status is not Inactive");
        log("The Link Status on the Member Information panel is displayed as 'Inactive', for member "+inactMem);
        
        //Select Active member
        LoginCheck();
        Member.memberSearch(actMem);
        Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[17]/td[2]")).getText().equals("Active"),"Active member link status is not Active");
        log("The Link Status on the Member Information panel is displayed as 'Active', for member "+actMem);
        
        //Check the ELG-0032-D report after ELGJD060 and ELGJD017 run
        String command, error;

		//Get Desired Filename
//		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/eld01701.rpt.* | tail -1"; //grabs the latest gdg of the file
        command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/eld01701.rpt.2276.Z";
		error = "There was no report file found for the command: "+command;
		String fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);		
		log(" The member report filename is: "+fileName);

		//Verify duplicate member data in file
		command = "grep "+inactMem+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired members that were linked";
		String outputText = Common.connectUNIX(command, error);

		Assert.assertTrue(outputText.contains(inactMem), "Inactive Member not found in file");
		Assert.assertTrue(outputText.contains(actMem), "Active Member not found in file");
		Assert.assertTrue(outputText.contains(lName), "Active Member Last name not found in file");
		Assert.assertTrue(outputText.contains(fName), "Active Member First name not found in file");
		log("Report contains inactive member, active member, last name and first name as below");
		log(outputText);

    }
    
    @Test
    public void test32914b() throws Exception{
    	TestNGCustom.TCNo="32914b";
    	log("//TC 32914b-Auto link the duplicate member IDs - 2 Test Case--Part B");
    	
	 	//get the 2 members that were linked
    	//First Member
		sqlStatement = "select * from r_day2 where TC = '32914' and DES='first member'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no active member found");
		String first = colValues.get(0);
		log("The first member is: "+first);

		//Second Member
		sqlStatement = "select * from r_day2 where TC = '32914' and DES='second member'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no active member found");
		String second = colValues.get(0);
		log("The second member is: "+second);
        
        //Select first member
        LoginCheck();
        Member.memberSearch(first);
        Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[17]/td[2]")).getText().equals(" "),"First member link status is not Blank");
        log("The Link Status on the Member Information panel is displayed as a blank, for member "+first);
        
        //Select second member
        LoginCheck();
        Member.memberSearch(second);
        Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[17]/td[2]")).getText().equals(" "),"second member link status is not Blank");
        log("The Link Status on the Member Information panel is displayed as a blank, for member "+second);
        

    }
    
    @Test
    public void test32955b() throws Exception{
    	TestNGCustom.TCNo="32955b";
    	log("//TC 32955b-Crossed MID detected - original MID existing Test Case");
    	
    	//Get the two members
    	//Original member
		sqlStatement = "select * from r_day2 where TC = '32955' and DES='original_memBer'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no original_memBer found");

		String original_memBer = colValues.get(0);
		log("original_memBer is: "+original_memBer);
		
		//Crossed member
		sqlStatement = "select * from r_day2 where TC = '32955' and DES='Crossed_memBer'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no Crossed_memBer found");

		String Crossed_memBer = colValues.get(0);
		log("Crossed_memBer is: "+Crossed_memBer);
		
		//Aid Cat
		sqlStatement = "select * from r_day2 where TC = '32955' and DES='CDE_AID_CATEGORY'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no CDE_AID_CATEGORY found");

		String CDE_AID_CATEGORY = colValues.get(0);
		log("CDE_AID_CATEGORY is: "+CDE_AID_CATEGORY);
		
		//SSN
		sqlStatement = "select * from r_day2 where TC = '32955' and DES='SSN'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no SSN found");

		String SSN = colValues.get(0);
		log("SSN is: "+SSN);
		
		//Case no.
		sqlStatement = "select * from r_day2 where TC = '32955' and DES='CASENUM'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no SSN found");

		String CASENUM = colValues.get(0);
		log("CASENUM is: "+CASENUM);
		
        //Verify unix report
		String command, error;

		//Get Desired Filename for eld01201.rpt (ELG-0003-D)
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/eld01201.rpt.* | tail -1"; //grabs the latest gdg of the file
		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);		
		log(" The MA21 ELIGIBILITY UPDATE ERROR REPORT filename is: "+fileName);

		//Verify data in file
		command = "grep "+original_memBer+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have original_memBer "+original_memBer+" in it";
		String outputText = Common.connectUNIX(command, error);
		log("Output string from report is: "+outputText);
		
		Assert.assertTrue(outputText.contains(CDE_AID_CATEGORY), "File does not have CDE_AID_CATEGORY "+CDE_AID_CATEGORY+" in it");
		Assert.assertTrue(outputText.contains(SSN), "File does not have CDE_AID_CATEGORY "+SSN+" in it");
		Assert.assertTrue(outputText.contains(CASENUM), "File does not have CASENUM "+CASENUM+" in it");
		
		command = "grep "+Crossed_memBer+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have Crossed_memBer "+Crossed_memBer+" in it";
		outputText = Common.connectUNIX(command, error);
		log("Output string from report is: "+outputText);
		
		String warningMsg = "Warning Member ID on txn is crossed with id_medicaid retrieved";
		Assert.assertTrue(outputText.contains(warningMsg), "File does not have warningMsg "+warningMsg+" in it");
		
		log("Successfully validated that MA21 ELIGIBILITY UPDATE ERROR REPORT has Original Member, Crossed Member, Aid Cat, SSN, Case number and Warning Message");
		
		//Get Desired Filename for eld01207.rpt (ELG-0007-D)
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/eld01207.rpt.* | tail -1"; //grabs the latest gdg of the file
		error = "There was no report file found";
		fileName = Common.connectUNIX(command, error);
		fileName = fileName.substring(fileName.length()-40);
		
		log(" The DAILY MA21 ERROR COUNT REPORT filename is: "+fileName);

		//Verify data in file
		String warningCode = "2388";
		command = "grep "+warningCode+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have warningCode "+warningCode+" in it";
		outputText = Common.connectUNIX(command, error);
		log("Output string from report is: "+outputText);
		
		Assert.assertTrue(outputText.contains(original_memBer), "File does not have original_memBer "+original_memBer+" in it");
		Assert.assertTrue(outputText.contains(warningMsg), "File does not have warningMsg "+warningMsg+" in it");
		Assert.assertTrue(outputText.contains(CASENUM), "File does not have CASENUM "+CASENUM+" in it");
		
		log("Successfully validated that DAILY MA21 ERROR COUNT REPORT has Original Member, Case number and Warning Message");
    }
    
    @Test
    public void test32956b() throws Exception{
    	TestNGCustom.TCNo="32955b";
    	log("//TC 32956b-Crossed MID detected - no match found Test Case");    	
    	
    	//Get the two members
    	//Original member
		sqlStatement = "select * from r_day2 where TC = '32956' and DES='original_memBer'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no original_memBer found");

		String original_memBer = colValues.get(0);
		log("original_memBer is: "+original_memBer);
		
		//Crossed member
		sqlStatement = "select * from r_day2 where TC = '32956' and DES='Crossed_memBer'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no Crossed_memBer found");

		String Crossed_memBer = colValues.get(0);
		log("Crossed_memBer is: "+Crossed_memBer);
		
		//Aid Cat
		sqlStatement = "select * from r_day2 where TC = '32956' and DES='CDE_AID_CATEGORY'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no CDE_AID_CATEGORY found");

		String CDE_AID_CATEGORY = colValues.get(0);
		log("CDE_AID_CATEGORY is: "+CDE_AID_CATEGORY);
		
		//SSN
		sqlStatement = "select * from r_day2 where TC = '32956' and DES='SSN'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no SSN found");

		String SSN = colValues.get(0);
		log("SSN is: "+SSN);
		
		//Case no.
		sqlStatement = "select * from r_day2 where TC = '32956' and DES='CASENUM'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no SSN found");

		String CASENUM = colValues.get(0);
		log("CASENUM is: "+CASENUM);
		
        //Verify unix report
		String command, error;

		//Get Desired Filename for eld01201.rpt (ELG-0003-D)
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/eld01201.rpt.* | tail -1"; //grabs the latest gdg of the file
		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);		
		log(" The MA21 ELIGIBILITY UPDATE ERROR REPORT filename is: "+fileName);

		//Verify data in file
		command = "grep "+original_memBer+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have original_memBer "+original_memBer+" in it";
		String outputText = Common.connectUNIX(command, error);
		log("Output string from report is: "+outputText);
		
		Assert.assertTrue(outputText.contains(CDE_AID_CATEGORY), "File does not have CDE_AID_CATEGORY "+CDE_AID_CATEGORY+" in it");
		Assert.assertTrue(outputText.contains(SSN), "File does not have CDE_AID_CATEGORY "+SSN+" in it");
		Assert.assertTrue(outputText.contains(CASENUM), "File does not have CASENUM "+CASENUM+" in it");
		
		command = "grep "+Crossed_memBer+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have Crossed_memBer "+Crossed_memBer+" in it";
		outputText = Common.connectUNIX(command, error);
		log("Output string from report is: "+outputText);
		
		String warningMsg = "Warning Member ID on txn is crossed with id_medicaid retrieved";
		Assert.assertTrue(outputText.contains(warningMsg), "File does not have warningMsg "+warningMsg+" in it");
		
		log("Successfully validated that MA21 ELIGIBILITY UPDATE ERROR REPORT has Original Member, Crossed Member, Aid Cat, SSN, Case number and Warning Message");
		
		//Get Desired Filename for eld01207.rpt (ELG-0007-D)
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/eld01207.rpt.* | tail -1"; //grabs the latest gdg of the file
		error = "There was no report file found";
		fileName = Common.connectUNIX(command, error);
		fileName = fileName.substring(fileName.length()-40);
		
		log(" The DAILY MA21 ERROR COUNT REPORT filename is: "+fileName);

		//Verify data in file
		String warningCode = "2388";
		command = "grep "+warningCode+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have warningCode "+warningCode+" in it";
		outputText = Common.connectUNIX(command, error);
		log("Output string from report is: "+outputText);
		
		Assert.assertTrue(outputText.contains(original_memBer), "File does not have original_memBer "+original_memBer+" in it");
		Assert.assertTrue(outputText.contains(warningMsg), "File does not have warningMsg "+warningMsg+" in it");
		Assert.assertTrue(outputText.contains(CASENUM), "File does not have CASENUM "+CASENUM+" in it");
		
		log("Successfully validated that DAILY MA21 ERROR COUNT REPORT has Original Member, Case number and Warning Message");
    }
    
}
