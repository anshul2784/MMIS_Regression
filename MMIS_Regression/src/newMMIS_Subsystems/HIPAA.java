package newMMIS_Subsystems;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ newMMIS_Subsystems.TestNGCustom.class })
public class HIPAA extends Login {
	
	String SelSql, col,  DelSql,  InsSql;
	
	@BeforeTest
    public void HIPAAStartup() throws Exception {
    	log("Starting HIPAA Subsystem......");    	
    }
	
	@BeforeMethod
	public void LoginCheck() throws Exception {
		Common.resetBase();
		testCheckDBLoginSuccessful();	
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_HP")).click();
	}
	
    @Test
    public void test32144a() throws Exception{
    	TestNGCustom.TCNo="32144a";
    	log("//TC 32144a - Create Claims history request");
    	
    	//Get Member with atleast last 2 years of claims history
    	sqlStatement="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ a.ID_MEDICAID from t_re_base a, t_hist_directory b where a.sak_recip=b.sak_recip And b.cde_clm_status = 'P' And b.cde_partition_id = '01' and b.dte_first_svc between '"+datePaneltoSQL(Common.convertSysdatecustom(-730))+"' and '"+datePaneltoSQL(Common.convertSysdate())+"' and A.Ind_Active='Y'  And not Exists  (Select distinct 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid And Rownum < 100) and a.sak_recip not in (select sak_recip from T_Re_other_Address where cde_agency = 'HPO') And Rownum < 2";
    	System.out.println(sqlStatement);
    	
    	colNames.add("ID_MEDICAID");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String Mem = colValues.get(0);
    	
    	log("Member: "+Mem);
    	
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();

		//New request
		driver.findElement(By.linkText("New Request")).click();
		//Access
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNewRequestPanel:DRSDataRedaction_DRSTypeGroup11")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpMemberAmendRequestDataPanel_HpRequestPurposeList"))).selectByVisibleText("Other");
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpAccessRequestInfo_RequestDate")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpMemberAmendRequestDataPanel_HpRequestStatusList"))).selectByVisibleText("Open");
		
	 	//Save All
	 	Common.SaveWarnings();

		//Request Comment Information Panel
	 	comments();
	 	
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_claimsHistoryData")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_ClaimsHistFromDate")).clear();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_ClaimsHistFromDate")).sendKeys(Common.convertSysdatecustom(-800)); //Approx 2+ years ago
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_ClaimsHistThruDate")).clear();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_ClaimsHistThruDate")).sendKeys(Common.convertSysdate());
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
		
		//Insert Member in DB
		SelSql="select * from R_DAY2 where tc = '32144'";
		col="ID";
		DelSql="delete from R_DAY2 where tc = '32144'";  
		InsSql="insert into  R_DAY2 values ('32144', '"+Mem+"', 'Claims History Data', '"+Common.convertSysdate()+"')";
		Common.insertData(SelSql, col, DelSql, InsSql);
		
    }
    
    @Test
    public void test32145a() throws Exception{
    	TestNGCustom.TCNo="32145a";
    	log("//TC 32145a - Create PA request");
    	
    	//Get Member with atleat last 2 years of claims history
    	sqlStatement="Select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ A.Id_Medicaid, C.id_provider, d.cde_service_loc  From T_Re_Base A, T_Pa_Pauth B, t_pr_prov C, t_pa_line_item d Where A.Sak_Recip = B.Sak_Recip and d.sak_pa_serv_prov=c.sak_prov and b.prior_auth_num like 'P%' and b.CDE_MEDIA_TYPE = '2' and b.sak_pa=d.sak_pa and d.sak_pa_serv_prov <>'-1' and b.sak_diag <> '-1'  and A.Ind_Active='Y'  And not Exists  (Select distinct 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid And Rownum < 100) and a.sak_recip not in (select sak_recip from T_Re_other_Address where cde_agency = 'HPO') and a.dte_birth < 20050101 and Rownum < 2"; //Member > 18 years old
    	System.out.println(sqlStatement);
    	
    	colNames.add("ID_MEDICAID");
    	colNames.add("ID_PROVIDER");
    	colNames.add("CDE_SERVICE_LOC");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String Mem = colValues.get(0);
    	String Prov = colValues.get(1);
    	String Svc = colValues.get(2);
    	
    	log("Member: "+Mem);
    	
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();

		//New request
		driver.findElement(By.linkText("New Request")).click();
		//Access
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNewRequestPanel:DRSDataRedaction_DRSTypeGroup11")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpMemberAmendRequestDataPanel_HpRequestPurposeList"))).selectByVisibleText("Other");
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpAccessRequestInfo_RequestDate")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpMemberAmendRequestDataPanel_HpRequestStatusList"))).selectByVisibleText("Open");
	 	
	 	//Save All
//	 	Common.SaveWarnings();
		Common.saveAll();
		
		//Request Comment Information Panel
	 	comments();
	 	
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_PriorAuthProviderNumber")).sendKeys(Prov+Svc);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			String message=driver.findElement(By.cssSelector("td.message-text")).getText();
			Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
		
		//Insert Member in DB
		SelSql="select * from R_DAY2 where tc = '32145'";
		col="ID";
		DelSql="delete from R_DAY2 where tc = '32145'";  
		InsSql="insert into  R_DAY2 values ('32145', '"+Mem+"', 'Prior Authorization Data', '"+Common.convertSysdate()+"')";
		Common.insertData(SelSql, col, DelSql, InsSql);
		
    }
    
    @Test
    public void test22400a() throws Exception{
    	TestNGCustom.TCNo="22400a";
    	log("//TC 22400a - Create Member Demographics Request");
    	
    	//Get Member with atleat last 2 years of claims history
    	sqlStatement="Select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ A.Id_Medicaid From T_Re_Base A, T_Re_medicare_b B, T_Re_Pmp_Assign C, T_Re_Mmq D Where A.Sak_Recip=B.Sak_Recip And A.Sak_Recip = C.Sak_Recip And C.Cde_Status1<>'H' And A.Sak_Recip = D.Sak_Recip and A.Ind_Active='Y' and b.dte_end = '22991231' and b.dte_effective < b.dte_end And not Exists  (Select distinct 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid And Rownum < 100) and a.sak_recip not in (select sak_recip from T_Re_other_Address where cde_agency = 'HPO') and a.sak_recip not in (select sak_recip from T_RECIP_LINK_XREF) and a.sak_recip not in (select SAK_RCP_PURGED from T_RECIP_LINK_XREF) and a.dte_death = 0 And Rownum < 2";
    	System.out.println(sqlStatement);
    	
    	colNames.add("ID_MEDICAID");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String Mem = colValues.get(0);
    	
    	log("Member: "+Mem);
    	
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();

		//New request
		driver.findElement(By.linkText("New Request")).click();
		//Access
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNewRequestPanel:DRSDataRedaction_DRSTypeGroup11")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpMemberAmendRequestDataPanel_HpRequestPurposeList"))).selectByVisibleText("Other");
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpAccessRequestInfo_RequestDate")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpMemberAmendRequestDataPanel_HpRequestStatusList"))).selectByVisibleText("Open");
	 	
	 	//Save All
	 	Common.SaveWarnings();
	 	
		//Request Comment Information Panel
	 	comments();
	 	
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_memberDemographicData")).click();
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
		
		//Insert Member in DB
		SelSql="select * from R_DAY2 where tc = '22400'";
		col="ID";
		DelSql="delete from R_DAY2 where tc = '22400'";  
		InsSql="insert into  R_DAY2 values ('22400', '"+Mem+"', 'Member Demographics Data', '"+Common.convertSysdate()+"')";
		Common.insertData(SelSql, col, DelSql, InsSql);
		
    }
    
    @Test
    public void test32137a() throws Exception{
    	TestNGCustom.TCNo="32137a";
    	log("//TC 32137a - Prior Authorization History - No Data Test Case");
    	
    	//Get Member with PA history
    	sqlStatement="Select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ A.Id_Medicaid From T_Re_Base A Where A.Sak_Recip not in (select sak_recip from T_Pa_Pauth B) and A.Ind_Active='Y'  And not Exists  (Select distinct 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid And Rownum < 100) and a.sak_recip not in (select sak_recip from T_Re_other_Address where cde_agency = 'HPO') And Rownum < 2";
    	System.out.println(sqlStatement);
    	
    	colNames.add("ID_MEDICAID");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String Mem = colValues.get(0);

    	
    	log("Member: "+Mem);
    	
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();

		//New request
		driver.findElement(By.linkText("New Request")).click();
		//Access
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNewRequestPanel:DRSDataRedaction_DRSTypeGroup11")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpMemberAmendRequestDataPanel_HpRequestPurposeList"))).selectByVisibleText("Other");
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpAccessRequestInfo_RequestDate")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpMemberAmendRequestDataPanel_HpRequestStatusList"))).selectByVisibleText("Open");
	 	
	 	//Save All
	 	Common.SaveWarnings();
	 	
		//Request Comment Information Panel
	 	comments();
	 	
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//No search criteria, so below line is commented as we are not entering any search criteria
//		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_PriorAuthProviderNumber")).sendKeys(Prov+Svc);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			String message=driver.findElement(By.cssSelector("td.message-text")).getText();
			Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
		
		//Insert Member in DB
		SelSql="select * from R_DAY2 where tc = '32137'";
		col="ID";
		DelSql="delete from R_DAY2 where tc = '32137'";  
		InsSql="insert into  R_DAY2 values ('32137', '"+Mem+"', 'No Prior Authorization Data', '"+Common.convertSysdate()+"')";
		Common.insertData(SelSql, col, DelSql, InsSql);
		
    }
    
    public static void comments() {
		//Request Comment Information Panel
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestCommentPanel:HpRequestComment_NewButtonClay:HpRequestCommentList_newAction_btn")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestCommentPanel:HpCommentDataPanel_DscRequestComment")).sendKeys("Regression Automation");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestCommentPanel:HpCommentPanel_addAction_btn")).click();
	 	Common.SaveWarnings();
    }
    
    //Day 2
    
    @Test
    public void test32144b() throws Exception{
    	TestNGCustom.TCNo="32144b";
    	log("//TC 32144b - This TC hits report button on panel on Day 2");
    	
	 	//get the member for Claims History Data
		sqlStatement = "select * from R_DAY2 where tc = '32144'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member found for Claims History Data request");

		String Mem = colValues.get(0);
		log("Member is: "+Mem);

    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();
		
		//Go to Request History
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPMemberNavigatorPanel:HPMemberNavigatorId:ITM_n2")).click();
		//Select the request ID
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestHistoryMemberPanel:HpRequestHistoryMemberList_0:HpRequestHistoryMemberBean_ColValue_idMemberReq")).click();

		//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='2']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_claimsHistoryData")).click();
		//Check for alerts
		Common.isAlertPresent();
		Common.isAlertPresent();
		//re-check the checkbox after alerts
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_claimsHistoryData")).click();
		//re-check the checkbox in case no alerts were present
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@id='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_claimsHistoryData' and @checked='true']")).size()==0)
			driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_claimsHistoryData")).click();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
		
		//Verify claims history list panel
		int Claims_selection_panel_size = driver.findElements(By.cssSelector("table.nested.panel > thead > tr > th > table > tbody > tr > td > h3.panel-header")).size();
		Assert.assertTrue(Claims_selection_panel_size != 0,"No Claims selection panel was displayed on Claims redact for Member "+Mem);
//		Assert.assertTrue(driver.findElement(By.cssSelector("table.nested.panel > thead > tr > th > table > tbody > tr > td > h3.panel-header")).getText().equals(" Claims History List"),"No Claims selection panel was displayed");

        //Store at least one claim
        String Claim = driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:HPClaimHistListPagePanel:ClaimHistListPanel:HPClaimHistList:tbody_element']/tr/td[2]")).getText();
        //Store Claim in DB
		SelSql="select * from R_DAY2 where tc = '32144b'";
		col="ID";
		DelSql="delete from R_DAY2 where tc = '32144b'";  
		InsSql="insert into  R_DAY2 values ('32144b', '"+Claim+"', 'Claim number for Claim history report', '"+Common.convertSysdate()+"')";
		Common.insertData(SelSql, col, DelSql, InsSql);
		
		log("\r\nICN on panel is: "+Claim);
		
		//Hit Report
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Report']")).click();
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("No records have been redacted."), "Request NOT submitted successfully. Error Message: "+message+"...");
		
		//Generate letter
		genLtr(Mem);
    }
    
    @Test
    public void test32145b() throws Exception{
    	TestNGCustom.TCNo="32145b";
    	log("//TC 32145b - This TC hits report button on panel on Day 2");
    	
	 	//get the member for Claims History Data
		sqlStatement = "select * from R_DAY2 where tc = '32145'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member found for Member Demographic Data request");

		String Mem = colValues.get(0);
		log("Member is: "+Mem);

    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();
		
		//Go to Request History
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPMemberNavigatorPanel:HPMemberNavigatorId:ITM_n2")).click();
		//Select the request ID
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestHistoryMemberPanel:HpRequestHistoryMemberList_0:HpRequestHistoryMemberBean_ColValue_idMemberReq")).click();

		//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='2']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//Check for alerts
		Common.isAlertPresent();
		Common.isAlertPresent();
		//re-check the checkbox after alerts
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//re-check the checkbox in case no alerts were present
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@id='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData' and @checked='true']")).size()==0)
			driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
		
		//Verify Prior Authorization History List panel
//		int PA_Hist_list_panel_size = driver.findElements(By.cssSelector("h3.panel-header")).size();
//		Assert.assertTrue(PA_Hist_list_panel_size != 0,"No Prior Authorization History List panel was displayed on PA redact for Member "+Mem);
		Assert.assertTrue(driver.findElement(By.cssSelector("h3.panel-header")).getText().equals(" Prior Authorization History List"),"No Prior Authorization History List panel was displayed");

		//Get Prior auth data
//		//Sort on something/anything--sort is not working on this panel, so commenting all sorts. If you run into problems with null pointer exception, change the xpath of row in the target
//		driver.findElement(By.id("MMISForm:MMISBodyContent:HPPriorAuthHistListPagePanel:PriorAuthHistListPanel:HPPriorAuthHistList:_id74")).click();
//		//Sort desc start date
//		driver.findElement(By.id("MMISForm:MMISBodyContent:HPPriorAuthHistListPagePanel:PriorAuthHistListPanel:HPPriorAuthHistList:_id81")).click();
//		driver.findElement(By.id("MMISForm:MMISBodyContent:HPPriorAuthHistListPagePanel:PriorAuthHistListPanel:HPPriorAuthHistList:_id81")).click();
		
		//Comment 4 lines below if Access Fee Payment Information panel comes up which also has no records found. It is coming up automatically and interfering with Prior Authorization History List Panel
//		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
//		int noPAdataFound_size = driver.findElements(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[7]/table/tbody/tr/td/div/table/tbody/tr/td/span")).size(); //this line checks for ***No records found*** message in Prior Authorization History List Panel
//		Assert.assertTrue(noPAdataFound_size != 0,"***No records found*** for PA redact for Member "+Mem);
//		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		String paBegDate = driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:HPPriorAuthHistListPagePanel:PriorAuthHistListPanel:HPPriorAuthHistList:tbody_element']/tr/td[3]")).getText();
		

		
//        //Sort desc dollars
//		driver.findElement(By.id("MMISForm:MMISBodyContent:HPPriorAuthHistListPagePanel:PriorAuthHistListPanel:HPPriorAuthHistList:_id93")).click();
//		driver.findElement(By.id("MMISForm:MMISBodyContent:HPPriorAuthHistListPagePanel:PriorAuthHistListPanel:HPPriorAuthHistList:_id93")).click();
        String amt = driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:HPPriorAuthHistListPagePanel:PriorAuthHistListPanel:HPPriorAuthHistList:tbody_element']/tr/td[6]")).getText();
		

      
//        //Sort desc diag code
//		driver.findElement(By.id("MMISForm:MMISBodyContent:HPPriorAuthHistListPagePanel:PriorAuthHistListPanel:HPPriorAuthHistList:_id121")).click();
//		driver.findElement(By.id("MMISForm:MMISBodyContent:HPPriorAuthHistListPagePanel:PriorAuthHistListPanel:HPPriorAuthHistList:_id121")).click();
        String proc = driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:HPPriorAuthHistListPagePanel:PriorAuthHistListPanel:HPPriorAuthHistList:tbody_element']/tr/td[13]")).getText();


    
		log("\r\nPrior auth data on panel, PA Begin Date: "+paBegDate+" Claim $ Amount: "+amt+" Proc Code: "+proc);
		if(paBegDate.equals("")||amt.equals("")||proc.equals(""))
			Assert.assertTrue(false,"Check values for PA Begin Date: "+paBegDate+" Claim $ Amount: "+amt+" Proc Code: "+proc+" One or more is empty, so will cause null pointer exception");

        
        //Store Prior auth data in DB
		SelSql="select * from R_DAY2 where tc = '32145b1'";
		col="ID";
		DelSql="delete from R_DAY2 where tc = '32145b1'";  
		InsSql="insert into  R_DAY2 values ('32145b1', '"+paBegDate+"', 'paBegDate', '"+Common.convertSysdate()+"')";
		Common.insertData(SelSql, col, DelSql, InsSql);
		
		SelSql="select * from R_DAY2 where tc = '32145b2'";
		col="ID";
		DelSql="delete from R_DAY2 where tc = '32145b2'";  
		InsSql="insert into  R_DAY2 values ('32145b2', '"+amt+"', 'amt', '"+Common.convertSysdate()+"')";
		Common.insertData(SelSql, col, DelSql, InsSql);
		
		SelSql="select * from R_DAY2 where tc = '32145b3'";
		col="ID";
		DelSql="delete from R_DAY2 where tc = '32145b3'";  
		InsSql="insert into  R_DAY2 values ('32145b3', '"+proc+"', 'proc', '"+Common.convertSysdate()+"')";
		Common.insertData(SelSql, col, DelSql, InsSql);
		

		
		//Hit Report
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Report']")).click();
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Request submitted successfully."), "Request NOT submitted successfully. Error Message: "+message+"...");
		
		//Generate letter
		genLtr(Mem);
    }
    
    @Test
    public void test22400b() throws Exception{
    	TestNGCustom.TCNo="22400b";
    	log("//TC 22400b - This TC hits report button on panel on Day 2");
    	
	 	//get the member for Claims History Data
		sqlStatement = "select * from R_DAY2 where tc = '22400'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member found for Member Demographic Data request");

		String Mem = colValues.get(0);
		log("Member is: "+Mem);

    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();
		
		//Go to Request History
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPMemberNavigatorPanel:HPMemberNavigatorId:ITM_n2")).click();
		//Select the request ID
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestHistoryMemberPanel:HpRequestHistoryMemberList_0:HpRequestHistoryMemberBean_ColValue_idMemberReq")).click();

		//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='2']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_memberDemographicData")).click();
		//Check for alerts
		Common.isAlertPresent();
		Common.isAlertPresent();
		//re-check the checkbox after alerts
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_memberDemographicData")).click();
		//re-check the checkbox in case no alerts were present
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@id='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_memberDemographicData' and @checked='true']")).size()==0)
			driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_memberDemographicData")).click();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
		
		//Verify Member Demographic History List panel
		Assert.assertTrue(driver.findElement(By.cssSelector("h3.panel-header")).getText().equals(" Member Demographic History List"),"No Member Demographic History List panel was displayed");
		//Verify Permission To Share (PSI) Information panel
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[6]/table/tbody/tr/td/div[2]/table/thead/tr/th/table/tbody/tr/td/h3")).getText().equals(" Permission To Share (PSI) Information"),"No Permission To Share (PSI) Information panel was displayed");
		//Verify Eligibility Representative Designation (ERD) Information panel
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[6]/table/tbody/tr/td/div[3]/table/thead/tr/th/table/tbody/tr/td/h3")).getText().equals(" Eligibility Representative Designation (ERD) Information"),"No Eligibility Representative Designation (ERD) Information panel was displayed");
		//Verify Medicaid Eligibility History panel
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[6]/table/tbody/tr/td/div[4]/table/thead/tr/th/table/tbody/tr/td/h3")).getText().equals(" Medicaid Eligibility History"),"No Medicaid Eligibility History panel was displayed");
		//Verify Medicare Coverage History panel
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[6]/table/tbody/tr/td/div[5]/table/thead/tr/th/table/tbody/tr/td/h3")).getText().equals(" Medicare Coverage History"),"No Medicare Coverage History panel was displayed");
		//Verify Managed Care Enrollment History panel
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[6]/table/tbody/tr/td/div[6]/table/thead/tr/th/table/tbody/tr/td/h3")).getText().equals(" Managed Care Enrollment History"),"No Managed Care Enrollment History panel was displayed");
		//Verify Long Term Care Admission History panel
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[6]/table/tbody/tr/td/div[7]/table/thead/tr/th/table/tbody/tr/td/h3	")).getText().equals(" Long Term Care Admission History"),"No Long Term Care Admission History panel was displayed");
		//Verify  Request Comment Information panel
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[7]/table/thead/tr/th/table/tbody/tr/td/h2")).getText().equals(" Request Comment Information"),"No Request Comment Information panel was displayed");

        
        
        //Get Member Demographics info
        String eligBegDate = driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpMedicaidEligibilityPanel:HpMedEligSearchResultsDataTable_0:HpMedEligCol4Txt")).getText();
        String MedBegDate = driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpMedicareCoverageHistoryPanel:HpMedCovList_0:HpMedCoverageHistoryBean_ColValue_effectiveDate")).getText();
        String PmpBegDate = driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpManagedCarePanel:HpMCareEnrollList_0:HpManagedCareBean_ColValue_enrollmentBeginDate")).getText();
        String LtcBegDate = driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HPLTCAdmissionPanel:HPLTCAdmissionList_0:HpLTCAdmissionBean_ColValue_admissionDate")).getText();

        //Store Member Demographics info in DB
		SelSql="select * from R_DAY2 where tc = '22400b1'";
		col="ID";
		DelSql="delete from R_DAY2 where tc = '22400b1'";  
		InsSql="insert into  R_DAY2 values ('22400b1', '"+eligBegDate+"', 'eligBegDate', '"+Common.convertSysdate()+"')";
		Common.insertData(SelSql, col, DelSql, InsSql);
		
		SelSql="select * from R_DAY2 where tc = '22400b2'";
		col="ID";
		DelSql="delete from R_DAY2 where tc = '22400b2'";  
		InsSql="insert into  R_DAY2 values ('22400b2', '"+MedBegDate+"', 'MedBegDate', '"+Common.convertSysdate()+"')";
		Common.insertData(SelSql, col, DelSql, InsSql);
		
		SelSql="select * from R_DAY2 where tc = '22400b3'";
		col="ID";
		DelSql="delete from R_DAY2 where tc = '22400b3'";  
		InsSql="insert into  R_DAY2 values ('22400b3', '"+PmpBegDate+"', 'PmpBegDate', '"+Common.convertSysdate()+"')";
		Common.insertData(SelSql, col, DelSql, InsSql);
		
		SelSql="select * from R_DAY2 where tc = '22400b4'";
		col="ID";
		DelSql="delete from R_DAY2 where tc = '22400b4'";  
		InsSql="insert into  R_DAY2 values ('22400b4', '"+LtcBegDate+"', 'LtcBegDate', '"+Common.convertSysdate()+"')";
		Common.insertData(SelSql, col, DelSql, InsSql);
		
		log("\r\nMember Demographics data on panel, Elig Begin Date: "+eligBegDate+" Medicare Begin Date: "+MedBegDate+" PmP Begin Date: "+PmpBegDate+" LTC Begin Date: "+LtcBegDate);
		
		//Hit Report
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Report']")).click();
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Request NOT submitted successfully. Error Message: "+message+"...");
		
		//Generate letter
		genLtr(Mem);
    }
    
    @Test
    public void test32137b() throws Exception{
    	TestNGCustom.TCNo="32137b";
    	log("//TC 32137b - This TC validates no records found Day 2");
    	
	 	//get the member for Claims History Data
		sqlStatement = "select * from R_DAY2 where tc = '32137'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member found for Member Demographic Data request");

		String Mem = colValues.get(0);
		log("Member is: "+Mem);

    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();
		
		//Go to Request History
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPMemberNavigatorPanel:HPMemberNavigatorId:ITM_n2")).click();
		//Select the request ID
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestHistoryMemberPanel:HpRequestHistoryMemberList_0:HpRequestHistoryMemberBean_ColValue_idMemberReq")).click();

		//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='2']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//Check for alerts
		Common.isAlertPresent();
		Common.isAlertPresent();
		//re-check the checkbox after alerts
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//re-check the checkbox in case no alerts were present
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@id='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData' and @checked='true']")).size()==0)
			driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
		
		//Verify Prior Authorization History List panel
		Assert.assertTrue(driver.findElement(By.cssSelector("h3.panel-header")).getText().equals(" Prior Authorization History List"),"No Prior Authorization History List panel was displayed");
        
		//Validate no records found
		Assert.assertTrue(driver.findElement(By.xpath("/html/body/form/table/tbody/tr/td[2]/span/table[2]/tbody/tr/td/table/tbody/tr/td/div[7]/table/tbody/tr/td/div/table/tbody/tr/td/span")).getText().equals("***No records found***"),"Records were found in Prior Authorization History List panel");
		log("Successfully validated that ***No records found** in Prior Authorization History List panel ");

    }
    
    @Test
    public void test32140() throws Exception{
    	TestNGCustom.TCNo="32140";
    	log("//TC 32140 - Prior Authorization History List panel - Version Test Case");
    	
	 	//get the member for Claims History Data
		sqlStatement = "select * from R_DAY2 where tc = '32145'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member found for Member Demographic Data request");

		String Mem = colValues.get(0);
		log("Member is: "+Mem);

    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();
		
		//Go to Request History
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPMemberNavigatorPanel:HPMemberNavigatorId:ITM_n2")).click();
		//Select the request ID
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestHistoryMemberPanel:HpRequestHistoryMemberList_0:HpRequestHistoryMemberBean_ColValue_idMemberReq")).click();

		//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='2']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//Check for alerts
		Common.isAlertPresent();
		Common.isAlertPresent();
		//re-check the checkbox after alerts
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//re-check the checkbox in case no alerts were present
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@id='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData' and @checked='true']")).size()==0)
			driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
		
		//Verify Prior Authorization History List panel
		int PA_Hist_list_panel_size = driver.findElements(By.cssSelector("h3.panel-header")).size();
		Assert.assertTrue(PA_Hist_list_panel_size != 0,"No Prior Authorization History List panel was displayed on PA redact for Member "+Mem);
//		Assert.assertTrue(driver.findElement(By.cssSelector("h3.panel-header")).getText().equals(" Prior Authorization History List"),"No Prior Authorization History List panel was displayed");
        
		//Validate ICD version
		Assert.assertTrue(driver.findElement(By.xpath("/html/body/form/table/tbody/tr/td[2]/span/table[2]/tbody/tr/td/table/tbody/tr/td/div[7]/table/tbody/tr/td/div/table/tbody/tr/td/table/thead/tr/th[12]/a/span")).getText().equals("ICD Version"),"ICD Version not found in Prior Authorization History List panel");
		log("ICD Version found in Prior Authorization History List panel ");

    }
    
    @Test
    public void test32142() throws Exception{
    	TestNGCustom.TCNo="32142";
    	log("//TC 32142 - Claims Extract Test Case");
    	
	 	//get the member for Claims History Data
		sqlStatement = "select * from R_DAY2 where tc = '32144'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member found for Claims History Data request");

		String Mem = colValues.get(0);
		log("Member is: "+Mem);

    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();
		
		//Go to Request History
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPMemberNavigatorPanel:HPMemberNavigatorId:ITM_n2")).click();
		//Select the request ID
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestHistoryMemberPanel:HpRequestHistoryMemberList_0:HpRequestHistoryMemberBean_ColValue_idMemberReq")).click();

		//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='2']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_claimsHistoryData")).click();
		//Check for alerts
		Common.isAlertPresent();
		Common.isAlertPresent();
		//re-check the checkbox after alerts
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_claimsHistoryData")).click();
		//re-check the checkbox in case no alerts were present
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@id='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_claimsHistoryData' and @checked='true']")).size()==0)
			driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_claimsHistoryData")).click();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
		
		//Verify claims history list panel
		Assert.assertTrue(driver.findElement(By.cssSelector("table.nested.panel > thead > tr > th > table > tbody > tr > td > h3.panel-header")).getText().equals(" Claims History List"),"No Claims selection panel was displayed");

		//Validate ICD version
		Assert.assertTrue(driver.findElement(By.xpath("/html/body/form/table/tbody/tr/td[2]/span/table[2]/tbody/tr/td/table/tbody/tr/td/div[9]/table/tbody/tr/td/div[2]/table/tbody/tr/td/table/thead/tr/th[16]/a/span")).getText().equals("ICD Version"),"ICD Version not found in Claims History List panel");
		log("ICD Version found in Claims History List panel ");
    }
    
    
    public static void genLtr(String Member) throws Exception {
    	
		Common.cancelAll();
		//Go to Request History
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPMemberNavigatorPanel:HPMemberNavigatorId:ITM_n2")).click();
		//Select the request ID
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestHistoryMemberPanel:HpRequestHistoryMemberList_0:HpRequestHistoryMemberBean_ColValue_idMemberReq")).click();
		//Store req number
		String req = driver.findElement(By.xpath("//span[contains(@id,'HpMemberAmendRequestDataPanel_IdMemberReq') and @class='readOnlyText']")).getText();
		log("\r\nRequest no. : "+req);
		//Store Member name
		String nam = driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:HpMemberInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[3]/td[2]")).getText();
		nam=nam.substring(0, nam.lastIndexOf(",")-1); //get only last name
		
		//Get mailing address from table
		sqlStatement = "select a.* from t_re_other_address a, t_re_base b where a.sak_recip = b.sak_recip and a.cde_ADDR_USAGE = 'MM'  and b.id_medicaid = "+Member;
		colNames.add("ADDRESS_LINE_1");
		colNames.add("ADR_CITY");
		colNames.add("ADR_STATE");
		colNames.add("ADR_ZIP_CODE");
		colValues = Common.executeQuery(sqlStatement, colNames);
	
		String adr1 = colValues.get(0);
		String cityStateZip = colValues.get(1)+" "+colValues.get(2)+" "+colValues.get(3);

		
		
//		String adr1 = driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:HpMemberInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[4]/td[2]")).getText();
//		String cityStateZip = driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:HpMemberInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[6]/td[2]")).getText();

		//Click Letter
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Letter']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Key")).sendKeys(Member);
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPage_SearchButton")).click();
		//Verify req no. and member info
//		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_RequestId")).getText().equals(req), "Request number mismatch");
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Name")).getAttribute("value").contains(nam), "Name mismatch");
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Address1")).getAttribute("value").trim().equals(adr1), "Street adress 1 mismatch");
		Assert.assertTrue(cityStateZip.contains(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_City")).getAttribute("value").trim()), "city mismatch");
		Assert.assertTrue(cityStateZip.contains(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_State")).getAttribute("value").trim()), "State mismatch");
		Assert.assertTrue(cityStateZip.contains(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Zip")).getAttribute("value").trim().substring(0, 5)), "zip mismatch");

		//Enter text to come in letter
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPFreeFormLttrPanel:HPLetterRequestPageDataPanel_Dear")).sendKeys(TestNGCustom.TCNo);
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPFreeFormLttrPanel:HPLetterRequestPageDataPanel_LetterText")).sendKeys("Regression Automation for "+TestNGCustom.TCNo);
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPFreeFormLttrPanel:HPLetterRequestPageDataPanel_Sincerely")).sendKeys("Anshul Gandhi");
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Generate and Print']")).click();
    	log("The filename is: "+Common.fileName());
    	
    }
    
    //Day 3
    
    @Test
    public void test32144c() throws Exception{
    	TestNGCustom.TCNo="32144c";
    	log("//TC 32144c");
    	
    	//Get Claim meant to be found in the report
		sqlStatement = "select * from R_DAY2 where tc = '32144b'";
		colNames.add("ID");
		colNames.add("DATE_REQUESTED");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no claim selected for Claims history HIPAA report");

		String Clm = colValues.get(0);
		String DateR = colValues.get(1);
    	
        //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/hip00020.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";
		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
//		fileName = fileName.substring(fileName.length()-40);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The HIPAA Claims history report filename is: "+fileName);

		//Verify Claim in file
		command = "grep "+Clm+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired Claim number";
		log("\r\n"+Common.connectUNIX(command, error));
		
		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/hip00030.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";
		error = "There was no report file found";
		fileName = Common.connectUNIX(command, error);
//		fileName = fileName.substring(fileName.length()-40);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log("\r\nThe HIPAA Claims history report filename is: "+fileName);

		//Verify Claim in file
		command = "grep "+Clm+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired Claim number";
		log("\r\n"+Common.connectUNIX(command, error));
		
		closeCase("32144");
    	
    }
    
    @Test
    public void test32145c() throws Exception{
    	TestNGCustom.TCNo="32145c";
    	log("//TC 32145c");
    	
    	//Get PA data meant to be found in the report
    	String[] paData = new String[3];
    	paData[0]="paBegDate";
    	paData[1]="amt";
    	paData[2]="proc";
    	
    	for (int i=0;i<3;i++) {
		sqlStatement = "select * from R_DAY2 where tc = '32145b"+(i+1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no PA data selected for PA HIPAA report");
		paData[i] = colValues.get(0);
    	}

        //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/hip0040.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";

		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
//		fileName = fileName.substring(fileName.length()-40);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The HIPAA PA report filename is: "+fileName);

		//Verify PA Data in file
		command = "grep "+paData[0]+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired PA begin date data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep "+paData[1]+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired PA amount data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep "+paData[2]+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired PA proc code data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		//Get Desired Filename
//		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/hip0050.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/hip0050.rpt.* | tail -1"; //grabs the latest gdg of the file

		error = "There was no report file found";
		fileName = Common.connectUNIX(command, error);
//		fileName = fileName.substring(fileName.length()-40);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log("\r\nThe HIPAA PA report filename is: "+fileName);

		//Verify PA Data in file
		command = "grep "+paData[0]+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired PA begin date data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep "+paData[1]+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired PA amount data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		closeCase("32145");
    	
    }
    
    @Test
    public void test22400c() throws Exception{
    	TestNGCustom.TCNo="22400c";
    	log("//TC 22400c");
    	
    	//Get PA data meant to be found in the report
    	String[] memData = new String[4];
    	memData[0]="eligBegDate";
    	memData[1]="MedBegDate";
    	memData[2]="PmpBegDate";
    	memData[3]="LtcBegDate";
    	
    	for (int i=0;i<4;i++) {
		sqlStatement = "select * from R_DAY2 where tc = '22400b"+(i+1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no Member data selected for Member demographics report");
		memData[i] = colValues.get(0);
    	}

        //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/hip0060.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";
		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
//		fileName = fileName.substring(fileName.length()-40);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The HIPAA Member demographics report filename is: "+fileName);

		//Verify Member Data in file
		command = "grep "+memData[0]+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired Member elig data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep "+memData[1]+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired Member medicare data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep "+memData[2]+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired Member managed care(pmp) data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep "+memData[3]+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired Member LTC data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/hip0070.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";
		error = "There was no report file found";
		fileName = Common.connectUNIX(command, error);
//		fileName = fileName.substring(fileName.length()-40);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log("\r\nThe HIPAA Member demographics report filename is: "+fileName);

		//Verify Member Data in file
		command = "grep "+memData[0]+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired Member elig data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep "+memData[1]+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired Member medicare data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep "+memData[2]+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired Member managed care(pmp) data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep "+memData[3]+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired Member LTC data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		closeCase("22400");
    	
    }
    
    public static void closeCase(String tc) throws Exception {
    	
	 	//get the member for Claims History Data
		sqlStatement = "select * from R_DAY2 where tc = '"+tc+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member found for Claims History Data request");

		String Mem = colValues.get(0);
		
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();
		
		//Go to Request History
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPMemberNavigatorPanel:HPMemberNavigatorId:ITM_n2")).click();
		//Select the request ID
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestHistoryMemberPanel:HpRequestHistoryMemberList_0:HpRequestHistoryMemberBean_ColValue_idMemberReq")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpMemberAmendRequestDataPanel_HpRequestStatusList"))).selectByVisibleText("Closed-Approved");
		
	 	//Save All
	 	Common.SaveWarnings();
    }
    
    @Test
    public void test22385() throws Exception{
    	TestNGCustom.TCNo="22385";
    	log("//TC 22385");
    	
    	//Get Member 
    	sqlStatement="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ a.ID_MEDICAID from t_re_base a Where a.Ind_Active='Y' and a.sak_recip not in (select sak_recip from T_Re_other_Address where cde_agency = 'HPO') And not Exists  (Select 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid) And Rownum < 2";
    	System.out.println(sqlStatement);
    	
    	colNames.add("ID_MEDICAID");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String Mem = colValues.get(0);
    	
    	log("Member: "+Mem);
    	
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();

		//New request
		driver.findElement(By.linkText("New Request")).click();
		//Complaint
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNewRequestPanel:DRSDataRedaction_DRSTypeGroup16")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		new Select(driver.findElement(By.xpath("//select[contains(@id,'HpMemberAmendRequestDataPanel_HpRequestPurposeList')]"))).selectByVisibleText("Other");
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpComplaintRquestPanel:HpMemberAmendRequestDataPanel_ReceiptDateforComplain")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Open");
		
	 	//Save All
	 	Common.SaveWarnings();

		//Request Comment Information Panel
	 	comments();
	 	
		//Generate letter
		genLtr(Mem);
		
		//Close case
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Closed-Approved");
	 	Common.SaveWarnings();
	 	
    }
    
    @Test
    public void test22386_test23447() throws Exception{
    	TestNGCustom.TCNo="22386_23447";
    	log("//TC 22386_23447");
    	
    	//Get Member 
    	sqlStatement="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ a.ID_MEDICAID from t_re_base a Where a.Ind_Active='Y' and a.sak_recip not in (select sak_recip from T_Re_other_Address where cde_agency = 'HPO') And not Exists  (Select 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid) And Rownum < 2";
    	System.out.println(sqlStatement);
    	
    	colNames.add("ID_MEDICAID");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String Mem = colValues.get(0);
    	
    	log("Member: "+Mem);
    	
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();

		//New request
		driver.findElement(By.linkText("New Request")).click();
		//Access
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNewRequestPanel:DRSDataRedaction_DRSTypeGroup11")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		new Select(driver.findElement(By.xpath("//select[contains(@id,'HpMemberAmendRequestDataPanel_HpRequestPurposeList')]"))).selectByVisibleText("Other");
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpAccessRequestInfo_RequestDate")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Open");
		
	 	//Save All
	 	Common.SaveWarnings();
	 	
		//Store req number
		String req = driver.findElement(By.xpath("//span[contains(@id,'HpMemberAmendRequestDataPanel_IdMemberReq') and @class='readOnlyText']")).getText();

		//Request Comment Information Panel
	 	comments();
	 	
		//Generate letter
		genLtr(Mem);
		
		//Close case
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Closed-Approved");
	 	Common.SaveWarnings();
	 	
	 	//23447
	 	Common.cancelAll();
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();
		//Request History
		driver.findElement(By.linkText("Request History")).click();
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestHistoryMemberPanel:HpRequestHistoryMemberList_0:HpRequestHistoryMemberBean_ColValue_idMemberReq")).getText().equals(req), "Request number '"+req+"' not found in request history");

    }
    
    @Test
    public void test22387() throws Exception{
    	TestNGCustom.TCNo="22387";
    	log("//TC 22387");
 
    	String reqName = Common.generateRandomName()+" REGRESSION";
    	//Search HIPAA REQUESTOR SEARCH
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RequestorSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorSearchBean_CriteriaPanel:HpRequestorSearchResultsDataPanel_Organisation")).sendKeys(reqName);
	 	Common.search();
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorSearchResultsNoRecordsMessage")).getText().equals("***No records found***"), "***No records found*** was not found");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorSearchBean_CriteriaPanel:HpRequestorSearchResultsDataPanel_Organisation")).clear();
	 	
	 	//Create new requestor
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorEditPanel:HpRequestorDataPanel_RequestorType2")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorEditPanel:HpRequestorDataPanel_Organisation")).sendKeys(reqName);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorEditPanel:HpRequestorDataPanel_Address1")).sendKeys("100 hancock st");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorEditPanel:HpRequestorDataPanel_City")).sendKeys("QUINCY");
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorEditPanel:HpRequestorDataPanel_StateList"))).selectByVisibleText("MA");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorEditPanel:HpRequestorDataPanel_ZipCode")).sendKeys("02171");
	 	Common.saveAll();
	 	
	 	String reqID=driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:HpRequestorInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[2]/td[2]")).getText();
	 	log("Req ID: "+reqID+", Requestor Name: "+reqName);

	 	//Search again for this requestor
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RequestorSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorSearchBean_CriteriaPanel:HpRequestorSearchResultsDataPanel_Organisation")).sendKeys(reqName);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorSearchResultsList_0:_id14")).click();
		
    	//Get Member 
    	sqlStatement="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ a.ID_MEDICAID from t_re_base a Where a.Ind_Active='Y' and a.sak_recip not in (select sak_recip from T_Re_other_Address where cde_agency = 'HPO') And not Exists  (Select 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid) And Rownum < 2";
    	System.out.println(sqlStatement);
    	
    	colNames.add("ID_MEDICAID");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String Mem = colValues.get(0);
    	
    	log("Member: "+Mem);
		
		//New request
		driver.findElement(By.linkText("New Request")).click();
		//Enter member
	 	driver.findElement(By.xpath("//img[@alt='Member ID pop-up search']")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:_id217:MemberIDSearchCriteriaPanel:CurrentID")).sendKeys(Mem);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:_id217:MemberIDSearchCriteriaPanel:SEARCH")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:_id217:MemberIDSearchResults_0:column1Value")).click();
	 	//Enter rest of stuff
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpDisclosureRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Open");
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_StatusDate")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpDisclosureRequestDataPanel_HpRequestPurposeList')]"))).selectByVisibleText("Other");
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_ReceiptDate")).sendKeys(Common.convertSysdate());
		Common.saveAll();

		//Request Comment Information Panel
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestCommentPanels:HpDisclosureRequestComment_NewButtonClay:HpDisclosureRequestCommentList_newAction_btn")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestCommentPanels:HpDisclosureCommentDataPanel_DscRequestComment")).sendKeys("Regression Automation");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestCommentPanels:HpDisclosureRequestCommentPanels_addAction_btn")).click();
	 	Common.saveAll();

	 	//Generate Letter
		Common.cancelAll();
		//Go to Request History
		driver.findElement(By.linkText("Request History")).click();
		//Select the request ID
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestHistoryDisclosurePanel:HpRequestHistoryDisclosureList_0:HpRequestHistoryDisclosureBean_ColValue_disclosureReqID")).click();
		//Store req number
		String reqNo = driver.findElement(By.xpath("//span[contains(@id,'HpDisclosureRequestDataPanel_DisclosureReqID') and @class='readOnlyText']")).getText();
		log("\r\nRequest no. : "+reqNo);
		//Store Member name and address
		String nam = driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:HpRequestorInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[3]/td[2]")).getText();
		String adr1 = driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:HpRequestorInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[4]/td[2]")).getText();
		String cityStateZip = driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:HpRequestorInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[6]/td[2]")).getText();

		//Click Letter
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Letter']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Key")).sendKeys(reqID);
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPage_SearchButton")).click();
		//Verify member info
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Name")).getAttribute("value").trim().equals(nam), "Name mismatch");
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Address1")).getAttribute("value").trim().equals(adr1), "Street adress 1 mismatch");
		Assert.assertTrue(cityStateZip.contains(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_City")).getAttribute("value").trim()), "city mismatch");
		Assert.assertTrue(cityStateZip.contains(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_State")).getAttribute("value").trim()), "State mismatch");
		Assert.assertTrue(cityStateZip.contains(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Zip")).getAttribute("value").trim().substring(0, 5)), "zip mismatch");

		//Enter text to come in letter
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPFreeFormLttrPanel:HPLetterRequestPageDataPanel_Dear")).sendKeys(TestNGCustom.TCNo);
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPFreeFormLttrPanel:HPLetterRequestPageDataPanel_LetterText")).sendKeys("Regression Automation for "+TestNGCustom.TCNo);
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPFreeFormLttrPanel:HPLetterRequestPageDataPanel_Sincerely")).sendKeys("Anshul Gandhi");
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Generate and Print']")).click();
    	log("The filename is: "+Common.fileName());
        
		//Close case
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpDisclosureRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Closed-Approved");
	 	Common.SaveWarnings();
    }
    
    @Test
    public void test22389() throws Exception{
    	TestNGCustom.TCNo="22389";
    	log("//TC 22389");
    	
    	//Get Member 
    	sqlStatement="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ a.ID_MEDICAID from t_re_base a Where a.Ind_Active='Y' and A.DTE_DEATH = 0 and A.DTE_BIRTH > 19000101 and a.sak_recip not in (select sak_recip from T_Re_other_Address where cde_agency = 'HPO') And not Exists  (Select 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid) And Rownum < 2";
    	System.out.println(sqlStatement);
    	
    	colNames.add("ID_MEDICAID");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String Mem = colValues.get(0);
    	
    	log("Member: "+Mem);
    	
	 	//Navigate to member subsystem
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
	 	Member.memberSearch(Mem);

	 	//Add member HPO Address
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n131")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:OtherAddressPanel:OtherAddressDataPanel_AddressLine1")).sendKeys("100 main st");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:OtherAddressPanel:OtherAddressDataPanel_AddressCity")).sendKeys("boston");
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:OtherAddressPanel:OtherAddressDataPanel_AddressState"))).selectByVisibleText("MA");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:OtherAddressPanel:OtherAddressDataPanel_AddressZip")).sendKeys("02129");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		//Save all - needs to be done twice due to address validation, so cannot use generic
		if ((driver.findElements(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).size())>0) 
			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).click();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");

    	
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_HP")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();

		//New request
		driver.findElement(By.linkText("New Request")).click();
		//Confidential Communication
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNewRequestPanel:DRSDataRedaction_DRSTypeGroup14")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPConfCommPanel:HPConfComm_RequestDate")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HPConfCommPanel:HpMemberAmendRequestDataPanel_HpApprovedByList"))).selectByIndex(1);
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Open");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HPConfCommPanel:HpMemberAmendRequestDataPanel_Address1")).sendKeys("101 federal st");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HPConfCommPanel:HpMemberAmendRequestDataPanel_City")).sendKeys("boston");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HPConfCommPanel:HpMemberAmendRequestDataPanel_State")).sendKeys("MA");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HPConfCommPanel:HpMemberAmendRequestDataPanel_Zip")).sendKeys("02456");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HPConfCommPanel:HpMemberAmendRequestDataPanel_EffectiveDate")).sendKeys(Common.convertSysdate());

	 	//Save All
	 	Common.SaveWarnings();
		
		
		//Request Comment Information Panel
	 	comments();
	 	
		//Generate letter
		Common.cancelAll();
		//Go to Request History
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPMemberNavigatorPanel:HPMemberNavigatorId:ITM_n2")).click();
		//Select the request ID
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestHistoryMemberPanel:HpRequestHistoryMemberList_0:HpRequestHistoryMemberBean_ColValue_idMemberReq")).click();
		//Store req number
		String req = driver.findElement(By.xpath("//span[contains(@id,'HpMemberAmendRequestDataPanel_IdMemberReq') and @class='readOnlyText']")).getText();
		log("\r\nRequest no. : "+req);
		//Store Member name
		String nam = driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:HpMemberInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[3]/td[2]")).getText();
		nam=nam.substring(0, nam.lastIndexOf(",")-1); //get only last name

		//Click Letter
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Letter']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Key")).sendKeys(Mem);
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPage_SearchButton")).click();
		//Verify member info and zip
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Name")).getAttribute("value").contains(nam), "Name mismatch");
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Address1")).getAttribute("value").trim().equals("100 MAIN ST"), "Street adress 1 mismatch");
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_City")).getAttribute("value").trim().equals("BOSTON"), "city mismatch");
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_State")).getAttribute("value").trim().equals("MA"), "State mismatch");
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Zip")).getAttribute("value").trim().substring(0, 5).equals("02129"), "zip mismatch");

		//Enter text to come in letter
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPFreeFormLttrPanel:HPLetterRequestPageDataPanel_Dear")).sendKeys(TestNGCustom.TCNo);
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPFreeFormLttrPanel:HPLetterRequestPageDataPanel_LetterText")).sendKeys("Regression Automation for "+TestNGCustom.TCNo);
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPFreeFormLttrPanel:HPLetterRequestPageDataPanel_Sincerely")).sendKeys("Anshul Gandhi");
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Generate and Print']")).click();
    	log("The filename is: "+Common.fileName());

		//Close case
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Closed-Approved");
	 	Common.SaveWarnings();
	 	
    }
    
    @Test
    public void test22391() throws Exception{
    	TestNGCustom.TCNo="22391";
    	log("//TC 22391");
    	
    	//Get Member 
    	sqlStatement="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ a.ID_MEDICAID from t_re_base a Where a.Ind_Active='Y' and a.sak_recip not in (select sak_recip from T_Re_other_Address where cde_agency = 'HPO') And not Exists  (Select 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid) And Rownum < 2";
    	System.out.println(sqlStatement);
    	
    	colNames.add("ID_MEDICAID");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String Mem = colValues.get(0);
    	
    	log("Member: "+Mem);
    	
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();

		//New request
		driver.findElement(By.linkText("New Request")).click();
		//Access
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNewRequestPanel:DRSDataRedaction_DRSTypeGroup11")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		new Select(driver.findElement(By.xpath("//select[contains(@id,'HpMemberAmendRequestDataPanel_HpRequestPurposeList')]"))).selectByVisibleText("Other");
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpAccessRequestInfo_RequestDate")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Open");
		
	 	//Save All
	 	Common.SaveWarnings();

		//Request Comment Information Panel
	 	comments();
	 	
	 	//npp
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPMemberNavigatorPanel:HPMemberNavigatorId:ITM_n6")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNPPMailingHistoryPanel:HpNPPMailing_RequestType2")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"..."); //Assert modified by Priya to include Login failure message in reports
		//Sort on NPP date sent descending
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNPPMailingHistoryPanel:HpMailingHistoryList:HpNPPMailingHistoryBean_ColHeader_dteNPPSent")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNPPMailingHistoryPanel:HpMailingHistoryList:HpNPPMailingHistoryBean_ColHeader_dteNPPSent")).click();
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HpNPPMailingHistoryPanel:HpMailingHistoryList_0:HpNPPMailingHistoryBean_ColValue_dteNPPSent")).getText().equals(Common.convertSysdate()), "NPP record not created in today's date");
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HpNPPMailingHistoryPanel:HpMailingHistoryList_0:HpNPPMailingHistoryBean_ColValue_mailingType")).getText().equals("Member Requested"), "Mailing type is not 'Member Requested'");

		//Generate letter
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_MailTo"))).selectByVisibleText("Member");
		String req = driver.findElement(By.xpath("//span[contains(@id,'HpMemberAmendRequestDataPanel_IdMemberReq') and @class='readOnlyText']")).getText();
		log("Request no. : "+req);
		//Store Member name and address
		String nam = driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:HpMemberInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[3]/td[2]")).getText();
		nam=nam.substring(0, nam.lastIndexOf(",")-1); //get only last name
		String adr1 = driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:HpMemberInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[4]/td[2]")).getText();
		String cityStateZip = driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:HpMemberInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[6]/td[2]")).getText();

		//Enter member Id and generate letter
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Key")).sendKeys(Mem);
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPage_SearchButton")).click();
		//Verify member info
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Name")).getAttribute("value").contains(nam), "Name mismatch");
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Address1")).getAttribute("value").trim().equals(adr1), "Street adress 1 mismatch");
		Assert.assertTrue(cityStateZip.contains(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_City")).getAttribute("value").trim()), "city mismatch");
		Assert.assertTrue(cityStateZip.contains(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_State")).getAttribute("value").trim()), "State mismatch");
		Assert.assertTrue(cityStateZip.contains(driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPLetterRequestPageDataPanel_Zip")).getAttribute("value").trim().substring(0, 5)), "zip mismatch");

		//Enter text to come in letter
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPFreeFormLttrPanel:HPLetterRequestPageDataPanel_Dear")).sendKeys(TestNGCustom.TCNo);
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPFreeFormLttrPanel:HPLetterRequestPageDataPanel_LetterText")).sendKeys("Regression Automation for "+TestNGCustom.TCNo);
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPFreeFormLttrPanel:HPLetterRequestPageDataPanel_Sincerely")).sendKeys("Anshul Gandhi");
        driver.findElement(By.id("MMISForm:MMISBodyContent:HPLetterRequestPagePanel:HPFreeFormLttrPanel:HPFreeFormLttrPanel_submitAction_btn")).click();
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Generate and Print']")).click();
    	log("The filename is: "+Common.fileName());

		//Close case
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Closed-Approved");
	 	Common.SaveWarnings();
	 	
    }
    
    @Test
    public void test22393_test22395_test22401() throws Exception{
    	TestNGCustom.TCNo="22393_22395_22401";
    	log("//TC 22393_22395_22401");
    	
    	//23395
    	//HIPAA Worklist
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_WorkList")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpWorkListSearch_AllWorkInProgress")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpWorkListSearch_MemberRequests")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpWorkListSearch_3rdPartyDisclosures")).click();
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='search']")).click();
		Assert.assertTrue(driver.findElement(By.cssSelector("h3.panel-header")).getText().equals(" Member Request List"), "Member Request List panel did not appear");
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div/table/tbody/tr/td/div[2]/table/thead/tr/th/table/tbody/tr/td/h3")).getText().equals(" Due Today"), "Due Today panel did not appear(Member)");
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div/table/tbody/tr/td/div[3]/table/thead/tr/th/table/tbody/tr/td/h3")).getText().equals(" Past Due Today"), "Past Due Today panel did not appear(Member)");
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div/table/tbody/tr/td/div[4]/table/thead/tr/th/table/tbody/tr/td/h3")).getText().equals(" Disclosure Request List"), "Disclosure Request List panel did not appear");
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div/table/tbody/tr/td/div[5]/table/thead/tr/th/table/tbody/tr/td/h3")).getText().equals(" Due Today"), " Due Today panel did not appear(Disclosure)");
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div/table/tbody/tr/td/div[6]/table/thead/tr/th/table/tbody/tr/td/h3")).getText().equals(" Past Due Today"), "Past Due Today panel did not appear(Disclosure)");

		//Check there is data in panels
		Assert.assertTrue(driver.findElements(By.xpath("//a[contains(@id, 'MMISForm:MMISBodyContent:HpMemberRequestSearchAllResultsList')]")).size()>0,"No Records found in Member Request List panel");
		Assert.assertTrue(driver.findElements(By.xpath("//a[contains(@id, 'MMISForm:MMISBodyContent:HpMemberRequestSearchPastDueTodayResultsList')]")).size()>0,"No Records found in Past Due Today panel(Member)");
		Assert.assertTrue(driver.findElements(By.xpath("//a[contains(@id, 'MMISForm:MMISBodyContent:HpDisclosureRequestSearchAllResultsList')]")).size()>0,"No Records found in Disclosure Request List panel");
		Assert.assertTrue(driver.findElements(By.xpath("//a[contains(@id, 'MMISForm:MMISBodyContent:HpDisclosureRequestSearchPastDueTodayResultsList')]")).size()>0,"No Records found in Past Due Today panel(Disclosure)");

		//Clear all panels
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='clear']")).click();

    	//23393
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpWorkListSearch_AllWorkInProgress")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpWorkListSearch_MemberRequests")).click();
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='search']")).click();
	 	
	 	//Sort on status
        driver.findElement(By.id("MMISForm:MMISBodyContent:HpMemberRequestSearchPastDueTodayResultsList:HpMemberRequestSearchPastDueTodayResultsBean_ColHeader_requestStatus_dscStatus")).click();

	 	//Select request ID
        String req = driver.findElement(By.id("MMISForm:MMISBodyContent:HpMemberRequestSearchPastDueTodayResultsList_1:_id30")).getText();//Selecting 2nd record (_1:_id30) because the first record (_0:_id30) was member < 18 years old
        log("Request ID: "+req);
        driver.findElement(By.id("MMISForm:MMISBodyContent:HpMemberRequestSearchPastDueTodayResultsList_1:_id30")).click();
        
        //Confirm Access Fee Payment Information Panel displays
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[3]/table/thead/tr/th/table/tbody/tr/td/h2")).getText().equals(" Access Fee Payment Information"), "Access Fee Payment Information Panel did not display'");

        //Verify Req and print member
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpMemberAmendRequestDataPanel_IdMemberReq")).getText().equals(req), "Request ID mismatch");
		log("Member ID: "+driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:HpMemberInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[2]/td[2]")).getText());

        
		//Request Comment Information Panel
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestCommentPanel:HpRequestComment_NewButtonClay:HpRequestCommentList_newAction_btn")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestCommentPanel:HpCommentDataPanel_DscRequestComment")).sendKeys("Closing this case");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestCommentPanel:HpCommentPanel_addAction_btn")).click();
	 	Common.SaveWarnings();

		//Close case
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Closed-Approved");
	 	Common.SaveWarnings();
	 	
    	//22401
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_WorkList")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpWorkListSearch_AllWorkInProgress")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpWorkListSearch_3rdPartyDisclosures")).click();
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='search']")).click();
	 	
	 	//Sort on status
        driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclosureRequestSearchPastDueTodayResultsList:HpDisclosureRequestSearchPastDueTodayResultsBean_ColHeader_requestStatus_dscStatus")).click();

	 	//Select request ID
        req = driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclosureRequestSearchPastDueTodayResultsList_0:_id57")).getText();
        log("Request ID: "+req);
        driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclosureRequestSearchPastDueTodayResultsList_0:_id57")).click();
        
        //Confirm Access Fee Payment Information Panel displays
		Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[3]/table/thead/tr/th/table/tbody/tr/td/h2")).getText().equals(" Access Fee Payment Information"), "Access Fee Payment Information Panel did not display'");

        //Verify Req and print member
		Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_DisclosureReqID")).getText().equals(req), "Request ID mismatch");
		log("Member ID: "+driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_MedicaidID")).getCssValue("value"));

		//Request Comment Information Panel
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestCommentPanels:HpDisclosureRequestComment_NewButtonClay:HpDisclosureRequestCommentList_newAction_btn")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestCommentPanels:HpDisclosureCommentDataPanel_DscRequestComment")).sendKeys("Closing this case");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestCommentPanels:HpDisclosureRequestCommentPanels_addAction_btn")).click();
	 	Common.SaveWarnings();

		//Close case
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpDisclosureRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Closed-Disregard");
	 	Common.SaveWarnings();
    	
    }
    
    @Test
    public void test22394() throws Exception{
    	TestNGCustom.TCNo="22394";
    	log("//TC 22394");
    	
    	//Get Member 
    	sqlStatement="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ a.ID_MEDICAID from t_re_base a Where a.Ind_Active='Y' and a.sak_recip not in (select sak_recip from T_Re_other_Address where cde_agency = 'HPO') And not Exists  (Select 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid) And Rownum < 2";
    	System.out.println(sqlStatement);
    	
    	colNames.add("ID_MEDICAID");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String Mem = colValues.get(0);
    	
    	log("Member: "+Mem);
    	
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();

		//New request
		driver.findElement(By.linkText("New Request")).click();
		//Disclosure restriction
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNewRequestPanel:DRSDataRedaction_DRSTypeGroup12")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		new Select(driver.findElement(By.xpath("//select[contains(@id,'HpMemberAmendRequestDataPanel_HpRequestPurposeList')]"))).selectByVisibleText("Other");
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpMemberDisclosureRestrictionInformationPanel:HpMemberAmendRequestDataPanel_ReceiptDateforDsclRest")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Open");
		
	 	//Save All
	 	Common.SaveWarnings();

		//Request Comment Information Panel
	 	comments();
	 	
		//Generate letter
		genLtr(Mem);
		
		//Close case
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Closed-Approved");
	 	Common.SaveWarnings();
	 	
    }
    
    @Test
    public void test23142() throws Exception{
    	TestNGCustom.TCNo="23142";
    	log("//TC 23142");
    	String Mem = "";
    	
    	for(int i=0;i<2;i++) {
        	//Get Member 
    		if (i==0) 
    			sqlStatement="select a.ID_MEDICAID from t_re_base a Where a.Ind_Active='Y' and a.sak_recip not in (select sak_recip from T_Re_contact)  And Rownum < 2";
    		else
    			sqlStatement="select a.ID_MEDICAID from t_re_base a Where a.Ind_Active='Y' and a.sak_recip in (select sak_recip from T_Re_contact)  And Rownum < 2";
    		        	
        	colNames.add("ID_MEDICAID");
        	colValues = Common.executeQuery(sqlStatement, colNames);
        	Mem = colValues.get(0);
        	    	
        	//Search HIPAA subsystem for this member
    	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
    	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
    	 	Common.search();
    		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();
    		
    		//Member Auth REP/PSI
    		driver.findElement(By.id("MMISForm:MMISBodyContent:HPMemberNavigatorPanel:HPMemberNavigatorId:ITM_n3")).click();
    		if (i==0) 
    			Assert.assertTrue(driver.findElement(By.cssSelector("td.redreg")).getText().equals("***No records found***"), "the message '***No records found***' was not found");
    		else
    			Assert.assertTrue(driver.findElements(By.xpath("//span[contains(@id,'MMISForm:MMISBodyContent:HPMembrAuthRepInfoPanel:MemberAuthRepInfoResults_0:HPMemberAuthRepInfoBean_ColValue_caseIDNumber')]")).size()>0, "'***No records found***' was found when some records were expected to be found");

    	}
    	
    	log("Member: "+Mem);
    }
    
    @Test
    public void test23143() throws Exception{
    	TestNGCustom.TCNo="23143";
    	log("//TC 23143");
    	
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HPNavigatorPanel:HPReportNavigatorId:GRP_letters")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HPNavigatorPanel:HPReportNavigatorId:ITM_n105")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_LetterGroupList"))).selectByVisibleText("HIPAA Privacy");
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_LetterList"))).selectByVisibleText("Decision Notice");
		Common.search();
		Assert.assertTrue(driver.findElements(By.xpath("//a[contains(@id,'id17')]")).size()>0, "'***No records found***' was found when some records were expected to be found");
	 	//Sort on member
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:_id28")).click();
		//log member
    	log("Member for HIPAA decision notice: "+driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:tbody_element']/tr/td[4]")).getText());
    	//Produce letter
		driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id17")).click();
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Generate and Print']")).click();
    	log("The filename is: "+Common.fileName());

    }
    
    @Test
    public void test23446() throws Exception{
    	TestNGCustom.TCNo="23446";
    	log("//TC 23446");
    	
    	//Get Member 
    	sqlStatement="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ a.ID_MEDICAID from t_re_base a Where a.Ind_Active='Y' and a.sak_recip not in (select sak_recip from T_Re_other_Address where cde_agency = 'HPO') And not Exists  (Select 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid) And Rownum < 2";
    	System.out.println(sqlStatement);
    	
    	colNames.add("ID_MEDICAID");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String Mem = colValues.get(0);
    	
    	log("Member: "+Mem);
    	
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();

		//New request
		driver.findElement(By.linkText("New Request")).click();
		//Amendment
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNewRequestPanel:DRSDataRedaction_DRSTypeGroup13")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		new Select(driver.findElement(By.xpath("//select[contains(@id,'HpMemberAmendRequestDataPanel_HpRequestPurposeList')]"))).selectByVisibleText("Other");
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpMemberAmendRequestPanel:HpMemberAmendRequestDataPanel_ReceiptDateForAmendment")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Open");
		
	 	//Save All
	 	Common.SaveWarnings();

		//Request Comment Information Panel
	 	comments();
	 	
		//Generate letter
		genLtr(Mem);
		
		//Close case
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Closed-Approved");
	 	Common.SaveWarnings();
	 	
    }
    
    @Test
    public void test22399a() throws Exception{
    	TestNGCustom.TCNo="22399a";
    	log("//TC 22399a");
    	
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:HPNavigatorPanel:HPReportNavigatorId:ITM_n101")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
		//Declare Alert to handle Popup
		alert = driver.switchTo().alert();
		System.out.println(alert.getText());
		alert.accept();
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Successfully sent request for the Request Type Aging Report."), "Save NOT successful Error Message: "+message+"...");
		log(message);
	
    }
    
    @Test
    public void test22399b() throws Exception{
    	TestNGCustom.TCNo="22399b";
    	log("//TC 22399b");
    	
        //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/hip00080.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";
		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
//		fileName = fileName.substring(fileName.length()-40);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The HIPAA Request Type Aging Report filename is: "+fileName);

		//Verify data in report
		command = "grep REQUEST "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep ACCESS "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep AMENDMENT "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep ACCOUNTING "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep RESTRICTIONS "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep CONFIDENTIAL "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep COMPLAINTS "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep 3rd "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep GRAND "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired data";
		log("\r\n"+Common.connectUNIX(command, error));
	
    }
    
    @Test
    public void test22390a() throws Exception{
    	TestNGCustom.TCNo="22390a";
    	log("//TC 22390a");
    	
    	//Requestor search>enter any valid req no.
    	sqlStatement="Select ID_REQUESTOR From T_Hp_Requestor WHERE ind_requestor_name_type = 'O' and ROWNUM<2";
    	colNames.add("ID_REQUESTOR");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String requestor = colValues.get(0);
    	
    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RequestorSearch")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorSearchBean_CriteriaPanel:HpRequestorSearchResultsDataPanel_RequestorID")).sendKeys(requestor);
    	Common.search();
    	//Get Requestor name for storing in DB for reports later
    	String reqName = driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorSearchResultsList_0:_id14")).getText().substring(0, 19);
    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorSearchResultsList_0:_id14")).click();
    	
    	//New request
    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorNavigatorPanel:HPRequestorNavigatorId:ITM_Hpn1")).click();
    	//Get Member 
    	sqlStatement="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ a.ID_MEDICAID from t_re_base a Where a.Ind_Active='Y' and a.sak_recip not in (select sak_recip from T_Re_other_Address where cde_agency = 'HPO') And not Exists  (Select 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid) And Rownum < 2";
    	System.out.println(sqlStatement);
    	
    	colNames.add("ID_MEDICAID");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String Mem = colValues.get(0);
    	
    	log("Member: "+Mem);
//    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_MedicaidID_CMD_SEARCH")).click(); //not working for chromedriver
    	driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_MedicaidID_CMD_SEARCH']/img")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:_id217:MemberIDSearchCriteriaPanel:CurrentID")).sendKeys(Mem);
    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:_id217:MemberIDSearchCriteriaPanel:SEARCH")).click();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:_id217:MemberIDSearchResults_0:column1Value")).click();
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_HpRequestStatusList"))).selectByVisibleText("Open");
    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_StatusDate")).clear();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_StatusDate")).sendKeys(Common.convertSysdate());
    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_DisclosureDate")).clear();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_DisclosureDate")).sendKeys(Common.convertSysdate());
    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_ReceiptDate")).clear();
    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_ReceiptDate")).sendKeys(Common.convertSysdate());
    	//This is added because the checkbox below refreshes the page as soon as you check it, and then it unchecks itself, so this while loop makes sure that you check this checkbox
    	while(true) {
    		if(driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_BoolAcctDisclIndicator")).isSelected())
    			break;
    		driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_BoolAcctDisclIndicator")).click();
    	}
    	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_DisclosureDescription")).sendKeys("REGRESSION AUTOMATION");
    	Common.save();
    	String reqID = driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_DisclosureReqID")).getText();
    	log ("Request ID is: "+reqID);
    	
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();
		//New request
		driver.findElement(By.linkText("New Request")).click();
		//Accounting of disclosure
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNewRequestPanel:DRSDataRedaction_DRSTypeGroup15")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		new Select(driver.findElement(By.xpath("//select[contains(@id,'HpMemberAmendRequestDataPanel_HpRequestPurposeList')]"))).selectByVisibleText("Court Order");
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccountingOfDisclRequestPanel:HpMemberAmendRequestDataPanel_ReceiptDatefrpAccounting")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Open");
		
	 	//Save All
	 	Common.SaveWarnings();
	 	
	 	//Store request ID
	 	String reqID_22390 = driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccountingOfDisclRequestPanel:HpMemberAmendRequestDataPanel_IdMemberReq")).getText();
    	log ("Request ID in member search is: "+reqID_22390);

	 	//Report - Save All
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Report']")).click();
	 	Common.SaveWarnings();

		//Request Comment Information Panel
	 	comments();
	 	
		//Generate letter
		genLtr(Mem);
		
		//Close case
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpMemberAmendRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Closed-Approved");
	 	Common.SaveWarnings();
	 	
	 	//Store data in tables
    	String SelSql="select * from r_day2 where TC = '22390' and DES='MEMBER'";
    	String col="ID";
    	String DelSql="delete from r_day2 where TC = '22390' and DES='MEMBER'";
    	String InsSql="insert into r_day2 values ('22390', '"+Mem+"', 'MEMBER', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '22390' and DES='REQUESTOR'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '22390' and DES='REQUESTOR'";
    	InsSql="insert into r_day2 values ('22390', '"+reqName+"', 'REQUESTOR', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '22390' and DES='REQ_ID_MemberSrch'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '22390' and DES='REQ_ID_MemberSrch'";
    	InsSql="insert into r_day2 values ('22390', '"+reqID_22390+"', 'REQ_ID_MemberSrch', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    }
    
    @Test
    public void test22390b() throws Exception{
    	TestNGCustom.TCNo="22390b";
    	log("//TC 22390b");
    	
    	//Get Data to be checked in report
		sqlStatement ="select * from r_day2 where TC = '22390' and DES='MEMBER'";
		colNames.add("ID");
		colNames.add("DATE_REQUESTED");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member submitted HIPAA MEMBER ACCOUNTING OF DISCLOSURES Report");

		String Mem = colValues.get(0);
		String DateR = colValues.get(1);
		
		sqlStatement ="select * from r_day2 where TC = '22390' and DES='REQUESTOR'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no requestor submitted HIPAA MEMBER ACCOUNTING OF DISCLOSURES Report");

		String requestor = colValues.get(0);
		
		sqlStatement ="select * from r_day2 where TC = '22390' and DES='REQ_ID_MemberSrch'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no request ID (in member search) submitted HIPAA MEMBER ACCOUNTING OF DISCLOSURES Report");

		String req_id_memSrch = colValues.get(0);
    	
        //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/hip00010.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";
		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
//		fileName = fileName.substring(fileName.length()-40);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The HIPAA MEMBER ACCOUNTING OF DISCLOSURES Report filename is: "+fileName);

		//Verify data in report
		command = "grep "+Mem+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired member";
		log("\r\n"+Common.connectUNIX(command, error));
    	
		command = "grep "+requestor+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired requestor";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep Court "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired purpose";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep AUTOMATION "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired Accounting of disclosure summary";
		String outStr = Common.connectUNIX(command, error);
		log("\r\n"+outStr);
		
		Assert.assertTrue((outStr.contains(DateR)), "The Disclosure date is missing or incorrect");
		
		command = "grep "+req_id_memSrch+" "+fileName;
		error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired member search req ID";
		log("\r\n"+Common.connectUNIX(command, error));

    }
    
    @Test
    public void test32131() throws Exception{
    	TestNGCustom.TCNo="32131";
    	log("//TC 32131 - Validate ICD 9 button");
    	
    	memberDRS();
    	
    	String icdFrom = "27801";
    	String icdTo = "49300";
	 	
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//Select ICD 9 radio button
		driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_listOfICDVersion1' and @value='9']")).click();
		//Enter ICD 9 diag codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_DiagCodeFrom")).sendKeys(icdFrom);
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_DiagCodeTo")).sendKeys(icdTo);
		log("diagICD9 From: "+icdFrom+" diagICD9 To: "+icdTo);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
	
		log("Successfully entered ICD 9 diag codes for Member DRS");
		Common.cancelAll();

		requestorDRS();
		
    	icdFrom = "8540";
    	icdTo = "8540";
		
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_priorAuthData")).click();
		//Select ICD 9 radio button
		driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_listOfICDVersion1' and @value='9']")).click();
		//Enter ICD 9 diag codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_DiagCodeFrom")).sendKeys(icdFrom);
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_DiagCodeTo")).sendKeys(icdTo);
		log("diagICD9 From: "+icdFrom+" diagICD9 To: "+icdTo);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
	
		log("Successfully entered ICD 9 diag codes for Requestor DRS");
		Common.cancelAll();
    }
    
    @Test
    public void test32132() throws Exception{
    	TestNGCustom.TCNo="32132";
    	log("//TC 32132 - Validate ICD 10 button");
    	
    	memberDRS();
    	
    	String icdFrom = "M169";
    	String icdTo = "M169";
	 	
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//Make sure ICD 10 radio button is selected by default
		String icdVal = driver.findElement(By.xpath("//input[contains(@id,'MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_listOfICDVersion') and @checked='checked']")).getAttribute("value");
		Assert.assertTrue(icdVal.equals("10"), "ICD value is not defaulted to 10");
		//Enter ICD 10 diag codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_DiagCodeFrom")).sendKeys(icdFrom);
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_DiagCodeTo")).sendKeys(icdTo);
		log("diagICD10 From: "+icdFrom+" diagICD10 To: "+icdTo);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
	
		log("Successfully entered ICD 10 diag codes for Member DRS");
		Common.cancelAll();

		requestorDRS();
		
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_priorAuthData")).click();
		//Make sure ICD 10 radio button is selected by default
		icdVal = driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_listOfICDVersion2' and @checked='checked']")).getAttribute("value");
		Assert.assertTrue(icdVal.equals("10"), "ICD value is not defaulted to 10");
		//Enter ICD 10 diag codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_DiagCodeFrom")).sendKeys(icdFrom);
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_DiagCodeTo")).sendKeys(icdTo);
		log("diagICD10 From: "+icdFrom+" diagICD10 To: "+icdTo);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
	
		log("Successfully entered ICD 10 diag codes for Requestor DRS");
		Common.cancelAll();
    }
    
    @Test
    public void test32133() throws Exception{
    	TestNGCustom.TCNo="32133";
    	log("//TC 32133 - Validate ICD Help Tip");
    	
    	memberDRS();
	 	
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		//Check ICD Help Tip
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_listOfICDVersion_Error_Highlight")).click();
		//Store the current window Handle
		winHandleCurrent=driver.getWindowHandle();
		
		//Switch to new window
		for(String winHandle:driver.getWindowHandles())
			driver.switchTo().window(winHandle);
		
		//Validate Help text
		Assert.assertTrue(driver.findElement(By.cssSelector("div.helpDiv")).getText().equals("ICD Version\nThe version number of the International Classification of Diseases associated with the diagnosis or the ICD procedure code."), "Did not get the ICD Help Tip text");
		log("Successfully validated ICD Help Tip text for Member DRS");
		
		//Close the second window
		driver.close();
		
		//Switch back to main window
		Common.switchToMainWin();
		
		Common.cancelAll();

		requestorDRS();
		
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		//Check ICD Help Tip
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_listOfICDVersion_Error_Highlight")).click();
		//Store the current window Handle
		winHandleCurrent=driver.getWindowHandle();
		
		//Switch to new window
		for(String winHandle:driver.getWindowHandles())
			driver.switchTo().window(winHandle);
		
		//Validate Help text
		Assert.assertTrue(driver.findElement(By.cssSelector("div.helpDiv")).getText().equals("ICD Version\nThe version number of the International Classification of Diseases associated with the diagnosis or the ICD procedure code."), "Did not get the ICD Help Tip text");
		log("Successfully validated ICD Help Tip text for Requestor DRS");
		
		//Close the second window
		driver.close();
		
		//Switch back to main window
		Common.switchToMainWin();
		
		Common.cancelAll();

    }
    
    @Test
    public void test32134() throws Exception{
    	TestNGCustom.TCNo="32134";
    	log("//TC 32134 - Validate Error Message Displays FROM - Diag");
    	
    	memberDRS();
    	
    	String icdTo = "M169";
	 	
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//Make sure ICD 10 radio button is selected by default
		String icdVal = driver.findElement(By.xpath("//input[contains(@id,'MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_listOfICDVersion') and @checked='checked']")).getAttribute("value");
		Assert.assertTrue(icdVal.equals("10"), "ICD value is not defaulted to 10");
		//Enter ICD 10 diag codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_DiagCodeTo")).sendKeys(icdTo);
		log("diagICD10 To: "+icdTo);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Enter both the Diag Code From and Diag Code To."), "Did not get the edit 'Enter both the Diag Code From and Diag Code To.' for Member DRS. Instead got '"+message+"'");
	
		log("Successfully validated edit 'Enter both the Diag Code From and Diag Code To.' for Member DRS");
		Common.cancelAll();

		requestorDRS();
		
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_priorAuthData")).click();
		//Make sure ICD 10 radio button is selected by default
		icdVal = driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_listOfICDVersion2' and @checked='checked']")).getAttribute("value");
		Assert.assertTrue(icdVal.equals("10"), "ICD value is not defaulted to 10");
		//Enter ICD 10 diag codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_DiagCodeTo")).sendKeys(icdTo);
		log("diagICD10 To: "+icdTo);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Enter both the Diag Code From and Diag Code To."), "Did not get the edit 'Enter both the Diag Code From and Diag Code To.' for Requestor DRS. Instead got '"+message+"'");
	
		log("Successfully validated edit 'Enter both the Diag Code From and Diag Code To.' for Requestor DRS");
		Common.cancelAll();
    }
    
    @Test
    public void test32135() throws Exception{
    	TestNGCustom.TCNo="32135";
    	log("//TC 32135 - Validate Error Message Displays THRU - Diag");
    	
    	memberDRS();
    	
    	String icdFrom = "M169";
	 	
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//Enter diag codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_DiagCodeFrom")).sendKeys(icdFrom);
		log("diagICD10 From: "+icdFrom);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Enter both the Diag Code From and Diag Code To."), "Did not get the edit 'Enter both the Diag Code From and Diag Code To.' for Member DRS. Instead got '"+message+"'");
	
		log("Successfully validated edit 'Enter both the Diag Code From and Diag Code To.' for Member DRS");
		Common.cancelAll();

		requestorDRS();
		
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_priorAuthData")).click();
		//Enter diag codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_DiagCodeFrom")).sendKeys(icdFrom);
		log("diagICD10 From: "+icdFrom);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Enter both the Diag Code From and Diag Code To."), "Did not get the edit 'Enter both the Diag Code From and Diag Code To.' for Requestor DRS. Instead got '"+message+"'");
	
		log("Successfully validated edit 'Enter both the Diag Code From and Diag Code To.' for Requestor DRS");
		Common.cancelAll();
    }
    
    @Test
    public void test32136() throws Exception{
    	TestNGCustom.TCNo="32136";
    	log("//TC 32136 - Validate Error Message Displays Invalid Proc");
    	
    	memberDRS();
    	
    	String icdFrom = "080nx1z";
    	String icdTo = "080nx1z";
	 	
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//Enter proc codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_ProcCodeFrom")).sendKeys(icdFrom);
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_ProcCodeTo")).sendKeys(icdTo);
		log("Proc Code From: "+icdFrom+" Proc Code To: "+icdTo);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("One of the Procedure Code entered is invalid."), "Did not get the edit 'One of the Procedure Code entered is invalid.' for Member DRS. Instead got '"+message+"'");
	
		log("Successfully validated edit 'One of the Procedure Code entered is invalid.' for Member DRS");
		Common.cancelAll();

		requestorDRS();
		
    	icdFrom = "00f53z1";
    	icdTo = "00f53z1";
		
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_priorAuthData")).click();
		//Enter proc codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_ProcCodeFrom")).sendKeys(icdFrom);
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_ProcCodeTo")).sendKeys(icdTo);
		log("Proc Code From: "+icdFrom+" Proc Code To: "+icdTo);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("One of the Procedure Code entered is invalid."), "Did not get the edit 'One of the Procedure Code entered is invalid.' for Requestor DRS. Instead got '"+message+"'");
	
		log("Successfully validated edit 'One of the Procedure Code entered is invalid.' for Requestor DRS");
		Common.cancelAll();
    }
    
    @Test
    public void test32138() throws Exception{
    	TestNGCustom.TCNo="32138";
    	log("//TC 32138 - Validate Error Message FROM - Proc");
    	
    	memberDRS();
    	
    	String icdTo = "080NX1Z";
	 	
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//Enter proc codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_ProcCodeTo")).sendKeys(icdTo);
		log("Proc Code To: "+icdTo);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Enter both the Procedure Code From and Procedure Code To."), "Did not get the edit 'Enter both the Procedure Code From and Procedure Code To.' for Member DRS. Instead got '"+message+"'");
	
		log("Successfully validated edit 'Enter both the Procedure Code From and Procedure Code To.' for Member DRS");
		Common.cancelAll();

		requestorDRS();
		
    	icdTo = "080NX1Z";
		
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_priorAuthData")).click();
		//Enter proc codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_ProcCodeTo")).sendKeys(icdTo);
		log("Proc Code To: "+icdTo);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Enter both the Procedure Code From and Procedure Code To."), "Did not get the edit 'Enter both the Procedure Code From and Procedure Code To.' for Requestor DRS. Instead got '"+message+"'");
	
		log("Enter both the Procedure Code From and Procedure Code To.' for Requestor DRS");
		Common.cancelAll();
    }
    
    @Test
    public void test32139() throws Exception{
    	TestNGCustom.TCNo="32139";
    	log("//TC 32139 - Validate Error Message THRU - Proc");
    	
    	memberDRS();
    	
    	String icdFrom = "080NX1Z";
	 	
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:DRSDataRedaction_priorAuthData")).click();
		//Enter proc codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionPanel:HpDRSDataRedactionDataPanel_ProcCodeFrom")).sendKeys(icdFrom);
		log("Proc Code From: "+icdFrom);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Enter both the Procedure Code From and Procedure Code To."), "Did not get the edit 'Enter both the Procedure Code From and Procedure Code To.' for Member DRS. Instead got '"+message+"'");
	
		log("Successfully validated edit 'Enter both the Procedure Code From and Procedure Code To.' for Member DRS");
		Common.cancelAll();

		requestorDRS();
		
    	icdFrom = "080NX1Z";
		
	 	//DRS
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='DRS']")).click();
		driver.findElement(By.xpath("//input[@name='MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_DRSTypeGroup' and @value='1']")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:DRSDataRedaction_priorAuthData")).click();
		//Enter proc codes
		driver.findElement(By.id("MMISForm:MMISBodyContent:DRSDataRedactionRequestorPanel:HpDRSDataRedactionDataPanel_ProcCodeFrom")).sendKeys(icdFrom);
		log("Proc Code From: "+icdFrom);
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Submit']")).click();
	    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int i=0;i<length;i++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Enter both the Procedure Code From and Procedure Code To."), "Did not get the edit 'Enter both the Procedure Code From and Procedure Code To.' for Requestor DRS. Instead got '"+message+"'");
	
		log("Enter both the Procedure Code From and Procedure Code To.' for Requestor DRS");
		Common.cancelAll();
    }
    
    public void memberDRS() throws Exception {
    	//Member DRS
    	log ("Starting Member DRS");
    	//Get Member 
    	sqlStatement="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ a.ID_MEDICAID from t_re_base a Where a.Ind_Active='Y' and not Exists  (Select 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid) And Rownum < 2";
    	System.out.println(sqlStatement);
    	
    	colNames.add("ID_MEDICAID");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String Mem = colValues.get(0);
    	
    	log("Member: "+Mem);
    	
    	//Search HIPAA subsystem for this member
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_MemberSearch")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRecipientSearchBean_CriteriaPanel:HpMemberSearch_MemberID")).sendKeys(Mem);
	 	Common.search();
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id13")).click();

		//New request
		driver.findElement(By.linkText("New Request")).click();
		//Access
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpNewRequestPanel:DRSDataRedaction_DRSTypeGroup11")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpMemberAmendRequestDataPanel_HpRequestPurposeList"))).selectByVisibleText("Other");
		driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpAccessRequestInfo_RequestDate")).sendKeys(Common.convertSysdate());
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpMemberAmendRequestDataPanel_HpRequestStatusList"))).selectByVisibleText("Open");
	 	
	 	//Save All
	 	Common.SaveWarnings();
	 	
    	String reqID = driver.findElement(By.id("MMISForm:MMISBodyContent:HpAccessRequestPanel:HpMemberAmendRequestDataPanel_IdMemberReq")).getText();
    	log ("Request ID is: "+reqID);
    }
    
    public void requestorDRS() throws Exception {
    	//Requestor DRS
    			log ("Starting Requestor DRS");
    	    	String reqName = Common.generateRandomName()+" REGRESSION";
    		 	//Create new requestor
    		 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RequestorSearch")).click();
    			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
    		 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorEditPanel:HpRequestorDataPanel_RequestorType2")).click();
    		 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorEditPanel:HpRequestorDataPanel_Organisation")).sendKeys(reqName);
    		 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorEditPanel:HpRequestorDataPanel_Address1")).sendKeys("100 hancock st");
    		 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorEditPanel:HpRequestorDataPanel_City")).sendKeys("QUINCY");
    			new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorEditPanel:HpRequestorDataPanel_StateList"))).selectByVisibleText("MA");
    		 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpRequestorEditPanel:HpRequestorDataPanel_ZipCode")).sendKeys("02171");
    		 	Common.saveAll();
    		 	String requestorID=driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:HpRequestorInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[2]/td[2]")).getText();
    		 	log("Requestor ID: "+requestorID+", Requestor Name: "+reqName);
    			
    	    	//Get Member 
    	    	sqlStatement="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ a.ID_MEDICAID from t_re_base a Where a.Ind_Active='Y' and not Exists (Select 1 From T_Hp_Member_Request C Where A.Id_Medicaid=C.Id_Medicaid) And Rownum < 2";    	
    	    	colNames.add("ID_MEDICAID");
    	    	colValues = Common.executeQuery(sqlStatement, colNames);
    	    	String Mem = colValues.get(0);
    	    	
    	    	log("Member: "+Mem);
    			
    			//New request
    			driver.findElement(By.linkText("New Request")).click();
    			//Enter member
    		 	driver.findElement(By.xpath("//img[@alt='Member ID pop-up search']")).click();
    		 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:_id217:MemberIDSearchCriteriaPanel:CurrentID")).sendKeys(Mem);
    		 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:_id217:MemberIDSearchCriteriaPanel:SEARCH")).click();
    		 	driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:_id217:MemberIDSearchResults_0:column1Value")).click();
    		 	//Enter rest of stuff
    			new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpDisclosureRequestDataPanel_HpRequestStatusList')]"))).selectByVisibleText("Open");
    			driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_StatusDate")).sendKeys(Common.convertSysdate());
    			new Select(driver.findElement(By.xpath("//select[contains(@id, 'HpDisclosureRequestDataPanel_HpRequestPurposeList')]"))).selectByVisibleText("Other");
    			driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_ReceiptDate")).sendKeys(Common.convertSysdate());
    			Common.saveAll();
    			
    	    	String reqID = driver.findElement(By.id("MMISForm:MMISBodyContent:HpDisclRequestPanel:HpDisclosureRequestDataPanel_DisclosureReqID")).getText();
    	    	log ("Request ID is: "+reqID);
    }
    
//	Assert.assertTrue((driver.findElement(By.id("")).getText().equals()), "");
//	driver.findElement(By.id("")).click();
//	driver.findElement(By.id("")).getText();
//	driver.findElement(By.id("")).clear();
//	driver.findElement(By.id("")).sendKeys("");
//    new Select(driver.findElement(By.id(""))).selectByVisibleText("");
    
	//Get date string from MM/dd/YYYY  to yyyyMMdd format
	public static String datePaneltoSQL(String panelDate) {
		return panelDate.substring(6, 10)+panelDate.substring(0, 2)+panelDate.substring(3, 5);
	}
}
