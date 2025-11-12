package newMMIS_Subsystems;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ newMMIS_Subsystems.TestNGCustom.class })
public class MemberDay3 extends Login{
	
	String otherid, fname,ssn,casenum,dob,adr1,city,zip,dep,aid,agency, fileMem = " ", lastName = "MAZDAZ", beginDate="", openRsn="01", caseStatus="1";
	public static boolean endElig = false;
	
	@BeforeTest
    public void memberDay2Startup() throws Exception {
    	log("Starting Member Day 3 Subsystem......");
    }
	
	@BeforeMethod
	public void LoginCheck() throws Exception {
		Common.resetBase();
		testCheckDBLoginSuccessful();	
	}
	
    @Test
    public void test23388b() throws Exception{
    	TestNGCustom.TCNo="23388b";
    	log("//TC 23388b");

	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
	 	
	 	//get the 2 members that were linked
		sqlStatement = "select * from R_MEMBER_ELGJD040";
		colNames.add("MEMBER");
		colNames.add("MEM_FIRST");
		colNames.add("DATE_ISSUED");
		colNames.add("DATE_REQUESTED");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because no member was added for ID card generation");

		String Mem = colValues.get(0);
		String MemFname = colValues.get(1);
		String issDate = colValues.get(2);
		String reqDate = colValues.get(3);

		log("Member: "+Mem+" First Name: "+MemFname+" Issue Date: "+issDate+" Requested Date: "+reqDate);

		
		//Verify member ID card info
		Member.memberSearch(Mem);
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n110")).click();

        Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberIDPanel:IDCardList_0:IDCardBean_ColValue_issueDate")).getText().equals(issDate),"dteIssue on ID card mismatch");
        Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberIDPanel:IDCardList_0:IDCardBean_ColValue_IDCardNumber")).getText().equals("1"),"idAction on ID card mismatch");
        Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberIDPanel:IDCardList_0:IDCardBean_ColValue_idIssueRsn_codeDescription")).getText().equals("NEW MEMBER"),"issueRsn on ID card mismatch");
        Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberIDPanel:IDCardList_0:IDCardBean_ColValue_sourceID")).getText().equals("MHO"),"clerkID on ID card mismatch");
        Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberIDPanel:IDCardList_0:IDCardBean_ColValue_sourceCode")).getText().equals("P"),"source on ID card mismatch");
        Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberIDPanel:IDCardList_0:IDCardBean_ColValue_requestedDate")).getText().equals(reqDate),"dteReq on ID card mismatch");
	
        //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/eld04006.rpt.* | grep '"+Common.monthUNIX(issDate)+" "+Common.dayUNIX(issDate)+"'";
		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		log("The member report filename is: "+fileName);
		fileName = Common.chkCompress(fileName);
		
		//Verify member data in file
		command = "grep "+Mem+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired member";
		String outputText = Common.connectUNIX(command, error);

		Assert.assertTrue(outputText.contains(Mem), "Member id not found in file");
		Assert.assertTrue(outputText.contains(MemFname), "Member First name not found in file");
		Assert.assertTrue(outputText.contains("MAZDAZ"), "Member Last name not found in file");
        
    }
    
    @Test
    public void test23376b() throws Exception{
    	TestNGCustom.TCNo="23376b";
    	log("//TC 23376b");

    	
	 	//get member data
		sqlStatement = "select * from R_MEMBER_ELGJD012";
		colNames.add("PARENT_MEMBER");
		colNames.add("FILEDATE");
		colNames.add("LAST");
		colNames.add("FiRST");
		colNames.add("SSN");
		colNames.add("DOB");
		colNames.add("CASE");
		colNames.add("AID");
		colNames.add("DEP");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member link request");

		String parentMem = colValues.get(0);
		String fileDT = colValues.get(1);
		String lName = colValues.get(2);
		String fName = colValues.get(3);
		String ssn = colValues.get(4);
		String dob = colValues.get(5);
		String numCase = colValues.get(6);
		String aid = colValues.get(7);
		String dep = colValues.get(8);
		
		log("Parent Member: "+parentMem+" File Date: "+fileDT+" Duplicate Member Last name: "+lName+" Duplicate Member First Name: "+fName+" Duplicate Member SSN: "+ssn+" Duplicate Member DoB: "+dob+" Duplicate Member Case number: "+numCase+" Duplicate Member aid cat: "+aid+"Duplicate Member Dependent number: "+dep);
		
        //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/eld01201.rpt.* | grep '"+Common.monthUNIX(fileDT)+" "+Common.dayUNIX(fileDT)+"'";

		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		log(" The member report filename is: "+fileName);
		fileName = Common.chkCompress(fileName);

		//Verify duplicate member data in file
		command = "grep "+numCase+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired duplicate member";
		String outputText = Common.connectUNIX(command, error);

		Assert.assertTrue(outputText.contains(lName), "Dup Member Last name not found in file");
		Assert.assertTrue(outputText.contains(fName), "Dup Member First name not found in file");
		Assert.assertTrue(outputText.contains(ssn), "Dup Member SSN not found in file");
		Assert.assertTrue(outputText.contains(dob), "Dup Member DoB not found in file");
		Assert.assertTrue(outputText.contains(numCase), "Dup Member Case number not found in file");
		Assert.assertTrue(outputText.contains(aid), "Dup Member Aid Cat not found in file");
		Assert.assertTrue(outputText.contains(dep), "Dup Member Dependent number not found in file");
		
		//Verify error message in file
		command = "grep "+parentMem+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired error message with MMIS member";
		outputText = Common.connectUNIX(command, error);

		Assert.assertTrue(outputText.contains("Case/Aid Cat/De   "+parentMem+"     Case/Aid Cat/Dep NUM combo already exists on DB for a different Member ID    Reject "), "Did not get the message: Case/Aid Cat/De   "+parentMem+"     Case/Aid Cat/Dep NUM combo already exists on DB for a different Member ID    Reject ");
    }
	
}
