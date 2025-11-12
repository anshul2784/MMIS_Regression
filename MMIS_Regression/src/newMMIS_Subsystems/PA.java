package newMMIS_Subsystems;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class PA extends Login{
	public static String clmNo;
	public static String status;
	public static boolean tc23180,tc23182,tc22501=false;
	public static String paNum23485;
	public static String paNum23486;
	public static String paNum23488;
	public static String paNum23180;
	public static String paNum23181;                                                                                                                                                                                                                                                                                                                                                                                                                                       
	public static String paNum23182;
	public static String paNum23188;
	public static String trackNum2;
	public static String providerId;
	public static String memberId,provider,svcloc,modifier,units,authModifier,authUnits;
	public static String[] procCode= new String[] {"99202", "99203", "99204", "99205", "99211", "99212", "99213", "99214", "99215", "99217", "99218", "99219", "99220","99221", "99222", "99223", "99224", "99225", "99226", "99231", "99232", "99233", "99234", "99235", "99236","99238" };
	
	public static Statement statement = null;
	public static String  SelSql, col,  DelSql;
	static String InsSql;
	static int i;
	
		
	
    @BeforeTest
    public void paStartup() throws Exception {
    log("Starting PA Subsystem......");
    }

	
	@BeforeMethod
	public void LoginCheck() throws Exception {
		Common.resetBase();
		testCheckDBLoginSuccessful();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PriorAuth")).click();
	}
	
	
	
	
	public static void warnings(){
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
		int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
         for(int i=1;i<=length;i++)
          {
          driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:HtmlMessages_CheckBox_Warnings"+i)).click();
          }
		}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}
	
	
	
	public static String randomMemberid() throws SQLException{
		sqlStatement ="select id_medicaid from t_re_base where ind_active = 'Y' and sak_recip > dbms_random.value * 6300000 and rownum < 2";
		colNames.add("id_medicaid");
	 	colValues=Common.executeQuery(sqlStatement, colNames );
	    String memberId=colValues.get(0);
	    return memberId;
	    }
	
	public static String claimsQuery() throws SQLException{
		sqlStatement ="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ base.* from  t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid,t_re_elig e," +
			          "t_cde_aid aid,t_re_aid_elig elig where " +
			          "elig.sak_recip=base.sak_recip " +
			          "and base.sak_recip = e.sak_recip "+
			          "and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
			          "and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
			          "and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
			          "and pgm. CDE_PGM_HEALTH='STD' " +
			          "and elig.DTE_END='22991231' " + 
			          "and not exists ( select sak_recip from t_re_pmp_assign asg where asg.sak_recip=base.sak_recip and asg.dte_end> 20130401) " +
			          "and not exists ( select sak_recip from t_tpl_resource rs where rs.sak_recip=base.sak_recip and rs.dte_end> 20130401) " +
			          "and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip and hib.dte_end> 20130401) " +
			          "and not exists (select sak_recip from t_pa_pauth pa where pa.sak_recip=base.sak_recip) "+
			          "and base.ind_active='Y' "+ 
			          "and base.sak_recip > dbms_random.value * 6300000 " +
			          "and rownum<2";
		colNames.add("id_medicaid");
	 	colValues=Common.executeQuery(sqlStatement, colNames );
	    String memberId=colValues.get(0);
	    return memberId;
	}
	
	
	     public static void updateSql(String memberId,String tcNo) throws SQLException{
		 String sql="update r_pa set MEMBERID="+memberId+" where TCNO='"+tcNo+"'";
         colNames.add("ID_MEDICAID");
         Common.executeQuery(sql,colNames);
	     }
	
	 
	 public static String getMemId(String tcNo) throws Exception{
		 String sql="select * from R_DAY2 where tc='"+tcNo+"'";
		 colNames.add("ID");
		 colValues=Common.executeQuery1(sql,colNames);
		 String memberId=colValues.get(0);
		 return memberId;
	     }
	 
	 @Test
	 //Submit a claim for PA(PA has two line items)  *MEMBERID HARDCODED*
	 public void test23485() throws Exception{
	 TestNGCustom.TCNo="23485";
	 log("//TC 23485");
	 memberId=claimsMemQuery("23485");
     createPABase("23485","DME",memberId);
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 
     Thread.sleep(12000);
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
     changeStatus("0","IN REVIEW","DME");
    // Thread.sleep(12000);
     changeStatus("0","APPROVED","DME");
     new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_LetterIndicator"))).selectByValue("N");//No Print
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItem_NewButtonClay:PaLineItemList_newAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_providerID")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_providerID")).sendKeys("110029098");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:ProviderIDSearchControl")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:ProviderIDSearchControl")).sendKeys("A");
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_ReqProc")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_ReqProc")).sendKeys("K0007");//K0007
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_Modifier")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_Modifier")).sendKeys("KH");//KH
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_UntSvcRequirementQuantity")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_UntSvcRequirementQuantity")).sendKeys("3");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaRequirementEffectiveDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaRequirementEffectiveDate")).sendKeys(Common.convertSysdate());
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaRequirementEndDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaRequirementEndDate")).sendKeys(Common.convertSysdate());
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_AuthProc")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_AuthProc")).sendKeys("K0007");//K0007
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_AuthorizedModifier")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_AuthorizedModifier")).sendKeys("KH");//KH
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_UntSvcAthQuantity")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_UntSvcAthQuantity")).sendKeys("3");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaAuthorizationEffectiveDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaAuthorizationEffectiveDate")).sendKeys(Common.convertSysdate());
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaAuthorizationEndDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaAuthorizationEndDate")).sendKeys(Common.convertSysdate());
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaymentMethod"))).selectByVisibleText("Pay System Price");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDMEPanel_addAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 Thread.sleep(12000);
	 changeStatus("0","IN REVIEW","DME");//row num is 0 for 2nd line item as newly created is coming to first for which rownum will be 0
	 changeStatus("0","APPROVED","DME");
    // driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	 paNum23485=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim();
	 System.out.println("PA# from tc#23485: "+paNum23485);
	 log("PA num:"+paNum23485);
	 Common.cancelAll();
	 Common.portalLogin();
	 String pas=paNum23485;	 
	 String provider="110029098A";
	// String memberId=getMemId("23485");
	 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
	 advSubmitClaims.initiateClaim();
	 advSubmitClaims.pos="12 - HOME";//There is contract billing rule for POS for procedure codes we are using so need to submit with POS:12
	 advSubmitClaims.orderingProv[0]="110000254";
	 advSubmitClaims.orderingProv[1]="110000254";
	 advSubmitClaims.M("23485", "M", provider, "3000.00", "", "0", "", pas, memberSql,"0","0");//added if loop in procedure tab to enter PA#
	 clmNo =  advSubmitClaims.clmICN("prof");
	 status=driver.findElement(By.xpath("//span[contains(@id,'claimStatusText')]")).getText().trim();
	 System.out.println("ICN from TC#23485: "+clmNo);
	 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id,'claimStatusText')]")).getText().trim().contains("Paid"),"The claim is not paid");
	 log("ICN:"+clmNo);
	 Common.portalLogout();
	 }
	 
	
	 
	 @Test
	 //Submit a claim for PA(PA has modifier and claim doesn't have modifier) *MEMBERID HARDCODED*
	 public void test23486() throws Exception{
	 TestNGCustom.TCNo="23486";
	 log("//TC 23486 & 23487");
	 memberId=claimsMemQuery("23486");
	 //memberId="100218366605";
	 createPABase("23486","DME",memberId);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	 paNum23486=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim();
	 Thread.sleep(12000);
	 Common.cancelAll();
	 
	//paNum23486="P212510002";
	 //memberId="100225908027";
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Information")).click();
	 
	 //Searching PA# in base
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaMiniSearchPriorAuthorizationInput")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaMiniSearchPriorAuthorizationInput")).sendKeys(paNum23486);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaMiniSearch_SearchButton")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n132")).click();
	 Thread.sleep(12000);
	 changeStatus("0","IN REVIEW","DME");
	 changeStatus("0","APPROVED","DME");
     paNum23486=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim();
	 System.out.println("PAnum from TC#23486: "+paNum23486);
	 log("PA num:"+paNum23486);
	 Common.portalLogin();
	 String pas=paNum23486;
	 String provider="110029098A";
	// String memberId=getMemId("23486");
	 
	 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
	 //String memberSql="select * from t_re_base where ID_MEDICAID='100218366605'";
	 advSubmitClaims.initiateClaim();
	 advSubmitClaims.pos="12 - HOME";//There is contract billing rule for POS for procedure codes we are using so need to submit with POS:12
	 advSubmitClaims.orderingProv[0]="110000254";
	 advSubmitClaims.M("23486", "M", provider, "3000.00", "", "0", "", pas, memberSql,"0","0");//need to change the POS to HOME to get expected results
	 clmNo = advSubmitClaims.clmICN("prof");
	 status=driver.findElement(By.xpath("//span[contains(@id,'claimStatusText')]")).getText().trim();
	 System.out.println("ICN from TC#23486:"+clmNo);
	 log("ICN23486 denied with edit,3015:"+clmNo);
	 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id,'claimStatusText')]")).getText().trim().equals("Denied"),"The claim submitted is not denied for modifier mismatch");
	 driver.findElement(By.xpath("//input[contains(@id, 'btnCancelClaims')]")).click();
	 //TC#23487 Submit a claim for PA(PA and claim have modifiers)
	// driver.findElement(By.linkText("Manage Claims and Payments")).click();
	 driver.findElement(By.linkText("Inquire Claim Status")).click();
	 new Select(driver.findElement(By.xpath("//select[contains(@id, 'billingProviderID')]"))).selectByValue("110029098A");
	 driver.findElement(By.xpath("//input[contains(@id, 'icn')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id, 'icn')]")).sendKeys(clmNo);
	 driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click();
	 driver.findElement(By.xpath("//*[contains(@id, '0:icn')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id, 'btnResubmit')]")).click();
	 driver.findElement(By.id("professionalBillingTab:_MENUITEM_Procedures")).click();
	 driver.findElement(By.xpath("//span[contains(@id, ':0:item')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id, 'modifier1')]")).sendKeys("RR");
	 driver.findElement(By.xpath("//input[contains(@id, 'update_Button')]")).click();
	 driver.findElement(By.id("proceduresTab:_MENUITEM_Confirmation")).click();
	 driver.findElement(By.xpath("//input[contains(@id, 'btnSubmit')]")).click();
	 String ICN2=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
	 System.out.println("ICN from tc#23487: "+ICN2);
	 log("ICN23487:"+ICN2);
	 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim().equals("Paid"),"The claim submitted is not paid after resubmit");
	 }
	 
	 
	 @Test
	 //Submit a claim for PA(PA has no modifier and claim have modifier)
	 public void test23488() throws Exception{
	 TestNGCustom.TCNo="23488";
	 log("//TC 23488");
	 memberId=claimsMemQuery("23488");
	 //memberId="100215081322";
     createPABase("23488","DME",memberId);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	 Thread.sleep(12000);
	 changeStatus("0","IN REVIEW","DME");
	// Thread.sleep(12000);
     changeStatus("0","APPROVED","DME");
    
	 paNum23488=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim();
	 System.out.println("PAnum from TC#23488: "+paNum23488);
	 log("PA num:"+paNum23488);  
	 String pas=paNum23488;  
	 Common.portalLogin();
	 String provider="110029098A";  
	 
	/* String pas="P193300001";
	 memberId="100223003615";*/
			 
	// String memberId=getMemId("23488");
	 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
	 advSubmitClaims.pos="12 - HOME";
	 advSubmitClaims.orderingProv[0]="110000254";
	 advSubmitClaims.initiateClaim();
	 advSubmitClaims.M("23488", "M", provider, "3000.00", "", "0", "", pas, memberSql,"0","0");
	 clmNo = advSubmitClaims.clmICN("prof");
	 status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
     System.out.println("ICN from TC#23488:"+clmNo);
	 log("ICN:"+clmNo);
	 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim().equals("Denied"),"The claim submitted is not denied for modifier mismatch");
	 }
	 
	 
	 
	 public static String searchClaim(String icn){
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Claims")).click();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchBean_CriteriaPanel:ClmIcnNumber")).sendKeys(icn);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchResultsDataTable_0:_id14")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PhysicianClaimNavigatorPanel:PhysicianClaimNavigator:ITM_PhysicianClaim14")).click();//Clicking Prior Authorization link
	 String unitsUsed=driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimPriorAuthListBeanPanel:ClaimPriorAuthListSearchResults_0:ClaimPriorAuthListBean_ColValue_usedPaQuantity")).getText().trim();
	 return unitsUsed;
	 }
	 
	 @Test
	 //PA units reset when associated claim voided
	 public void test22597() throws Exception{
	 TestNGCustom.TCNo="22597";
	 log("//TC 22597");
	 memberId=claimsMemQuery("22597");
	 createPABase("22597","DME",memberId);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 
	 Thread.sleep(12000);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	 changeStatus("0","IN REVIEW","DME");
	 Thread.sleep(12000);
     changeStatus("0","APPROVED","DME");  
     //driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	 String paNum22597=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim();
	 System.out.println("PAnum from TC#22597: "+paNum22597);
	 log("PA num:"+paNum22597); 
	 String pas=paNum22597; 
	 
	// String pas="P21061000E";
	 Common.portalLogin();
	
	 String provider="110029098A"; 
	 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
    // String memberSql="select * from t_re_base where ID_MEDICAID='100221255894'";
	 advSubmitClaims.pos="12 - HOME";
	 advSubmitClaims.initiateClaim();
	 advSubmitClaims.orderingProv[0]="110000254";
	 advSubmitClaims.M("22597", "M", provider, "50.00", "", "0", "", pas, memberSql,"0","0");
	 clmNo =  advSubmitClaims.clmICN("prof");
	 status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
     System.out.println("ICN from TC#22597:"+clmNo);
	 log("Paid ICN:"+clmNo);
	 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim().equals("Paid"),"The claim submitted is not Paid");
	 Common.portalLogout();
	 String unitsUsed=searchClaim(clmNo);
	 Assert.assertTrue(unitsUsed.equals("8"), "The units used in PA after claim submission are not 8");
	 log("Units used after Claim is Paid: "+unitsUsed);
	 Common.portalLogin();
	 driver.findElement(By.linkText("Manage Claims and Payments")).click();
	 driver.findElement(By.linkText("Inquire Claim Status")).click();
	 new Select(driver.findElement(By.xpath("//select[contains(@id, 'billingProviderID')]"))).selectByValue("110029098A");
	 driver.findElement(By.xpath("//input[contains(@id, 'icn')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id, 'icn')]")).sendKeys(clmNo);
	 driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click();
	 driver.findElement(By.xpath("//*[contains(@id, '0:icn')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id,'btnVoid')]")).click();
	 /*new Select(driver.findElement(By.xpath("//select[contains(@id, 'claimNoteTypeDropDown')]"))).selectByValue("ADD");
	 driver.findElement(By.xpath("//textarea[contains(@id, 'claimNoteDescTextArea')]")).sendKeys("Regresion Testing");*/
	 driver.findElement(By.xpath("//input[contains(@id, 'btnConfirm')]")).click();
	 String voidIcn=driver.findElement(By.xpath("//span[contains(@id, 'icnText')]")).getText().trim();
	 log("Void ICN: "+voidIcn);
	 Common.portalLogout();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PriorAuth")).click();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_PriorAuthorizationNumber")).sendKeys(pas);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n132")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PALineItemDMEList_0:PaDMELineItemBean_ColValue_status")).click();
	 //Taking the screenshot of the page as the data is grayed out and cannot be checked through assert statement
     Common.screenShot("PA22597");
     log("Attched screenshot of the page with units reset back to 10");
     System.out.println("at the end of TC test22597");
     }
	 
	 
	 
	 @Test
	 //PA cut back
	 public void test31392() throws Exception{
	 TestNGCustom.TCNo="31392";
	 log("//TC 31392");
	 memberId=claimsMemQuery("31392");
	 //memberId="100205403536";
	 createPABase("31392","DME",memberId);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 Thread.sleep(12000);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	 changeStatus("0","IN REVIEW","DME");
	 Thread.sleep(12000);
     changeStatus("0","APPROVED","DME");
    // driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	 String paNum31392=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim();
	 System.out.println("PAnum from TC#31392: "+paNum31392);
	 log("PA num:"+paNum31392);
	 Common.portalLogin();
	 String pas=paNum31392;
	 String provider="110029098A";
	// String memberId=getMemId("31392");
	 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
	 advSubmitClaims.pos="12 - HOME";
	 advSubmitClaims.orderingProv[0]="110000254";
	 advSubmitClaims.initiateClaim();
	 advSubmitClaims.M("31392", "M", provider, "50.00", "", "0", "", pas, memberSql,"0","0");
	 clmNo = advSubmitClaims.clmICN("prof");
	 status=driver.findElement(By.xpath("//span[contains(@id,'claimStatusText')]")).getText().trim();
     System.out.println("ICN from TC#31392:"+clmNo);
	 log("Paid ICN:"+clmNo);
	 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id,'claimStatusText')]")).getText().trim().equals("Paid"),"The claim submitted is not Paid");
	 Common.portalLogout();
	 String unitsUsed=searchClaim(clmNo);
	 Assert.assertTrue(unitsUsed.equals("10"), "The units used in PA after claim submission are not 10");
	 log("Units used after Claim is Paid: "+unitsUsed);
	 }
	 
	 
	 @Test
	 //Provider view notifications on web portal
	 public static void test22501a() throws Exception{
	 TestNGCustom.TCNo="22501a";
	 log("//TC22501");
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Admin")).click();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_AlertNotification")).click();
	 driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:radioUserType']/tbody/tr[3]/td/label/input")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:UsersGroupSearchConsole_SubmitButton")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvMessageConsole_Search_Button")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvidersMessagingSearchCriteriaPanel:ProviderID_id")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvidersMessagingSearchCriteriaPanel:ProviderID_id")).sendKeys("110048577");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvidersMessagingSearchCriteriaPanel:_id149")).click();  
	 driver.findElement(By.xpath("//*[@id='MassHealthProviderMessagingSelectUserPanelsource']/option")).click(); 
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvidersMessagingSearchCriteriaPanel:MassHealthProviderMessagingSelectUserPanelimage_button__right_single")).click();  
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvidersMessagingSearchCriteriaPanel:_id155")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvMessageConsole_SubjectValue")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvMessageConsole_SubjectValue")).sendKeys("Letter Group Name-Testing");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvMessageConsole_ExpDate")).sendKeys(Common.convertSysdatecustom(10));
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvMessageConsole_BodyValue")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvMessageConsole_BodyValue")).sendKeys("Letter Group Name-Testing");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvMessageConsole_LetterValue")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvMessagingConsole_InnerFileGrid_0_FileUpload")).sendKeys(tempDirPath+"PA.txt");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvMessagingConsole_AddAttachmentButton")).click();  
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProvMessagingConsolePanel:ProvMessageConsole_PostButton")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/div/table/tbody/tr/td/table[1]/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td[2]")).getText().trim().equals("Providers messaging alert/notification saved successfully."),"Providers messaging alert/notification is not saved successfully");
	 log("Provider notification has been saved sauccessfully through base");
	// driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PriorAuth")).click();
	 tc22501=true;
	 }
	 
	 @Test
	 //Provider view notifications on web portal
	 public static void test22501b() throws Exception{
	 TestNGCustom.TCNo="22501b";
	/* if (!tc22501) 
			Assert.assertTrue(false, "Not continuing with TC#22501b as 22501a(Provider View Notifications) failed");*/
	 Common.portalLogin();
	 driver.findElement(By.linkText("Manage Correspondence and Reporting")).click();
	 driver.findElement(By.linkText("View Notifications")).click();
	 new Select(driver.findElement(By.xpath("//select[contains(@id,'providerID')]"))).selectByValue("110048577A");
	 driver.findElement(By.xpath("//input[contains(@id,'search_Button')]")).click();
	 //change do 22501as
	 Assert.assertTrue(driver.findElement(By.id("viewNotifications:j_id_id9pc3:data")).getText().trim().contains(Common.convertSysdate()),"The provider notification with today's date is not found in the portal");
	 Assert.assertTrue(driver.findElement(By.id("viewNotifications:j_id_id9pc3:data")).getText().trim().contains("Letter Group Name-Testing"),"The provider notification is not found in the portal");
	 log("Checking Provider View Notification on Web portal is done");
	 }
	 
	 
	 @Test
	 //Verify claim list panel display  
	 public static void test23178() throws Exception{
	 TestNGCustom.TCNo="23178";
	 log("//TC 23178");
	 String sql="select x.sak_pa from T_PA_ITEM_DTL_XREF x,T_PA_PAUTH p where x.sak_pa=p.sak_pa and p.CDE_AUTH_TYPE='P' and rownum<2";
	 colNames.add("SAK_PA");
	 colValues=Common.executeQuery(sql,colNames);
	 String trackNum=colValues.get(0);
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).sendKeys(trackNum);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n129")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[2]/table/thead/tr/th/table/tbody/tr/td[1]/h2")).getText().trim().equals("Claim List"),"The claim list panel is not displayed");
	 log("Verifying claim list panel is done");
	 }
	
	@Test
	 //Cornelius Due Date Header never deferred  *NEED TO PASS TRACK# FROM 22499*
	 public static void test23179() throws Exception{
	 TestNGCustom.TCNo="23179";
	 log("//TC 23179");
	 String sql="select prior_auth_num from T_PA_PAUTH where DTE_INFO_RECVD='0' and rownum<2";
	 colNames.add("PRIOR_AUTH_NUM");
	 colValues=Common.executeQuery(sql,colNames);
	 String paNum=colValues.get(0);
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_PriorAuthorizationNumber")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_PriorAuthorizationNumber")).sendKeys(paNum);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[10]/td[2]")).getText().trim().equals(""),"The deferred date in PA information panel is not empty");
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[11]/td[2]")).getText().trim().equals(""),"The Information Response Date in PA information panel is not empty");
	 log("Checking the Cornelius Date displayed on PA Information panel is correct when PA was never deferred is done");
	 }
	
	
	@Test
	 //Verifying no of days=0   DONE
	 public static void test23180() throws Exception{
	 TestNGCustom.TCNo="23180";
	 log("//TC 23180");
	 //randomMemberid();
	 memberId=randomMemberid();
	 createPABase("23180","",memberId);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	 paNum23180=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim();
	 System.out.println("PAnum from TC#23180: "+paNum23180);
	 log("PA num:"+paNum23180);
	 Thread.sleep(12000);
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PALineItemBasicList_0:PaLineItemBean_ColValue_status")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaLineitemStat"))).selectByVisibleText("IN REVIEW");//id is different didn't call common method
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PALineItemBasicList_0:PaLineItemBean_ColValue_status")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaLineitemStat"))).selectByVisibleText("DEFERRED");//id is different didn't call common method
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaIacXrefPanel:PaIacXref_NewButtonClay:PaIacXrefList_newAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaIacXrefPanel:PaReasonCodeDataPanel_reasonCode")).sendKeys("1011");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaIacXrefPanel:PaIacXrefPanel_addAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PALineItemBasicList_0:PaLineItemBean_ColValue_status")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n105")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaAdditionalInformationPanel:AdditionalInformation_NewButtonClay:AddTLInfoList_newAction_btn")).click();
	 driver.findElement(By.cssSelector("img[alt=\"Additional Information Type pop-up search\"]")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaAdditionalInformationPanel:_id42:PaAddtionalInformationTypePopupSearchCriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaAdditionalInformationPanel:_id42:PaAddtionalInformationTypePopupSearchResults_0:column1Value")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaAdditionalInformationPanel:PaAdditionalInfoDataPanel_CdeTrans"))).selectByVisibleText("EL-ELECTRONIC ONLY");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaAdditionalInformationPanel:PaAdditionalInfoDataPanel_TxtComment")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaAdditionalInformationPanel:PaAdditionalInfoDataPanel_TxtComment")).sendKeys("Test");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[10]/td[2]")).getText().trim().equals(Common.convertSysdate()),"The deferred date for PA submitted is not system date");
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[5]/td[2]")).getText().trim().equals(Common.convertSysdate()),"The date received for PA submitted is not system date and no of days is not equal to zero");
	 SelSql="select * from R_PA where tcno = '23180p'";
	 col="PANUM";
	 DelSql="delete from R_PA where tcno = '23180p'";  
	 InsSql="insert into  R_PA values ('23180p',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','"+paNum23180+"')";
	 Common.insertData(SelSql, col, DelSql, InsSql);
	 tc23180=true;
	 }
	
	
	@Test
	 //Cornelius Due Date deferred greater than 4 days 
	 public static void test23181() throws Exception{
	    TestNGCustom.TCNo="23181";
	    log("//TC 23181");
        memberId=randomMemberid();
		createPABase("23181","DME",memberId);
		driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
 	    warnings();
	    Common.saveAll();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	    paNum23181=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim();
		System.out.println("PA Num from TCNo23181: "+paNum23181);
		log("PA num:"+paNum23181);
	    Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[6]/td[2]")).getText().trim().equals(Common.convertSysdatecustom(15)),"The cornelius days is not greater than 4 days");
	    Thread.sleep(12000);
	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n132")).click();
	    changeStatus("0","IN REVIEW","DME");
		statusToDeferred("0");
		driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PALineItemDMEList_0:PaDMELineItemBean_ColValue_status")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n105")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:PaAdditionalInformationPanel:AdditionalInformation_NewButtonClay:AddTLInfoList_newAction_btn")).click();
		driver.findElement(By.cssSelector("img[alt=\"Additional Information Type pop-up search\"]")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:PaAdditionalInformationPanel:_id42:PaAddtionalInformationTypePopupSearchCriteriaPanel:SEARCH")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:PaAdditionalInformationPanel:_id42:PaAddtionalInformationTypePopupSearchResults_0:column1Value")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaAdditionalInformationPanel:PaAdditionalInfoDataPanel_CdeTrans"))).selectByVisibleText("EL-ELECTRONIC ONLY");
		driver.findElement(By.id("MMISForm:MMISBodyContent:PaAdditionalInformationPanel:PaAdditionalInfoDataPanel_TxtComment")).clear();
		driver.findElement(By.id("MMISForm:MMISBodyContent:PaAdditionalInformationPanel:PaAdditionalInfoDataPanel_TxtComment")).sendKeys("Test");
		driver.findElement(By.id("MMISForm:MMISBodyContent:PaAdditionalInformationPanel:PaAdditionalInformationPanel_addAction_btn")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
		warnings();
		Common.saveAll();
	    }
	
	
	
	 @Test
	 //Cornelius Due Date for Deferred request response < 4 days *MEMBERID HARDCODED*
	 public void test23182() throws Exception{
	 TestNGCustom.TCNo="23182";
	 log("//TC 23182");
//	 sqlStatement ="select id_medicaid from t_re_base where ind_active = 'Y' and sak_recip > dbms_random.value * 6300000 and rownum < 2";
//	 colNames.add("id_medicaid");//0
//	 colValues=Common.executeQuery(sqlStatement, colNames );
	 SelSql="select * from R_PA where TCNO = '23182'";
	 col="TCNO";
	 DelSql="delete from R_PA where TCNO ='23182'";  
	 InsSql="insert into  R_PA values ('23182','DM','"+randomMemberid()+"','110026789A','110026789A',' ','A4927','A4927',' ',' ','2880','2880',' ')";
	 Common.insertData(SelSql, col, DelSql, InsSql);
	 createPABase("23182","DME",randomMemberid());
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 Thread.sleep(12000);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	 changeStatus("0","IN REVIEW","DME");
	 //check for base info tab is opened or not need to sript
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_LetterIndicator"))).selectByVisibleText("Batch");
	 statusToDeferred("0");
	 paNum23182=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim();
	 log("PA num:"+paNum23182);
	 SelSql="select * from R_DAY2 where TC = '23182'";
	 col="ID";
	 DelSql="delete from R_DAY2 where TC = '23182'";  
	 InsSql="insert into  R_DAY2 values ('23182','"+colValues.get(0)+"','Member id from TCno23182,PANUM is "+paNum23182+"',' ')";
	 Common.insertData(SelSql, col, DelSql, InsSql);
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[6]/td[2]")).getText().trim().equals(Common.convertSysdatecustom(15)),"The cornelius due date for deferred request response is not less than 4 days");
	 SelSql="select * from R_PA where tcno = '23182p'";
	 col="PANUM";
	 DelSql="delete from R_PA where tcno = '23182p'";  
	 InsSql="insert into  R_PA values ('23182p',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','"+paNum23182+"')";
	 Common.insertData(SelSql, col, DelSql, InsSql);
	 tc23182=true;
	  }
	 
	 @Test
	 //Create Nightly Report(summary and utilization) Requests
	 public static void test23183() throws Exception{
	 TestNGCustom.TCNo="23183";
	 log("//TC 23183");
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
	 reports("Prior Authorization Provider Summary Report","PHYSICIAN");
	 reports("Prior Authorization Provider Utilization Report","PHYSICIAN");
	 log("Nightly report request is done. Need to check the reports on Day2");
	 //Check the reports next day 1.Report PAU-0020-R,2.Report PAU-0022-R in KC document direct
	 //NEED TO CHECK THE TEST CASE
	 }
	 
	 
	 public static void reports(String title,String type){
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaReportNavigatorPanel:PaReportNavigatorId:ITM_n2")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaNightlyReport:PaNightlyReportDataPanel_ReportTitle"))).selectByVisibleText(title);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNightlyReport:PaNightlyReportDataPanel_DateFrom")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNightlyReport:PaNightlyReportDataPanel_DateFrom")).sendKeys(Common.firstDateOfPreviousMonth());
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNightlyReport:PaNightlyReportDataPanel_DateTo")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNightlyReport:PaNightlyReportDataPanel_DateTo")).sendKeys(Common.lastDateOfPreviousMonth());
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaNightlyReport:PaNightlyReportDataPanel_TypeCode"))).selectByVisibleText(type);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNightlyReport:PaNightlyReportPanel_saveBtnAction_btn")).click();
	 String message=driver.findElement(By.cssSelector("td.message-text")).getText();
	 Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
	 Common.cancelAll();	 
	 }
	
	@Test
	 //Insert/Update External text defaults  
	 public static void test23184_23185() throws Exception{
	 TestNGCustom.TCNo="23184_23185";
	 log("//TC 23184_23185");
	 if (!tc23180) 
			Assert.assertTrue(false, "Not continuing with TC#23184_23185 as 23180 failed because need to pass PAnum from 23180");
	 else{
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_PriorAuthorizationNumber")).click();
	 System.out.println("PA num:"+paNum23180);
	 log("PA num:"+paNum23180);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_PriorAuthorizationNumber")).sendKeys(getPaNum("23180p"));
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n103")).click();
	 extText("Member","Test","A");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 //Testcase#23185 Insert/Update Internal text defaults
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n126")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaIntTextPanel:PaIntText_NewButtonClay:PaIntTextList_newAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaIntTextPanel:PaIntTextDataPanel_PaTextDescription")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaIntTextPanel:PaIntTextDataPanel_PaTextDescription")).sendKeys("Test");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
     Common.saveAll();
     Common.cancelAll();
       }
	 }
	
	
	@Test
	 //Header Level External Text
	 public void test23186() throws Exception{
	 TestNGCustom.TCNo="23186";
	 log("//TC 23186");
	 if (!tc23182) 
			Assert.assertTrue(false, "Not continuing with TC#23186 as 23182 failed because need to pass PAnum from 23180");
	 else{
	 log("PA num:"+paNum23182);
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_PriorAuthorizationNumber")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_PriorAuthorizationNumber")).sendKeys(getPaNum("23182p"));
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n132")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n103")).click();
	 extText("Provider","This is comment for the provider only at header level","A");
	 extText("Member","This is comment for the member only at header level","A");
	 extText("Both","This is comment for the both only at header level","A");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n1")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_LetterIndicator"))).selectByVisibleText("Batch");	
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	   }
	 log("Validating Header level External text is done");
	 }
	
	@Test
	 //Line Level External Text
	 public void test23188_23191() throws Exception{
	 TestNGCustom.TCNo="23188_23191";
	 log("//TC 23188_23191");
	 String memberid=randomMemberid();
     sqlStatement=("select * from r_pa where TCNO='23188'");
	 colNames.add("REQPROVIDERID");//0
	 colNames.add("PROCCODE"); //1
	 colNames.add("AUTHPROCCODE");//2
	 colNames.add("UNITS");//3
	 colNames.add("AUTHUNITS");//4
	 colValues=Common.executeQuery1(sqlStatement, colNames );
	 String provider=colValues.get(0).substring(0, colValues.get(0).length()-1),
	          svcloc=colValues.get(0).substring(colValues.get(0).length()-1);
	 String proccode=colValues.get(1);
	 String units=colValues.get(3);
	 createPABase("23188","DME",memberid);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	 paNum23188=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim();
	 log("PA num:"+paNum23188);
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_PriorAuthorizationNumber")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_PriorAuthorizationNumber")).sendKeys(paNum23188);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n132")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItem_NewButtonClay:PaLineItemList_newAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_providerID")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_providerID")).sendKeys(provider);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:ProviderIDSearchControl")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:ProviderIDSearchControl")).sendKeys(svcloc);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_ReqProc")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_ReqProc")).sendKeys(proccode);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_UntSvcRequirementQuantity")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_UntSvcRequirementQuantity")).sendKeys(units);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaRequirementEffectiveDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaRequirementEffectiveDate")).sendKeys(Common.convertSysdate());
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaRequirementEndDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaRequirementEndDate")).sendKeys(Common.convertSysdatecustom(365));
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_AuthProc")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_AuthProc")).sendKeys(proccode);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_UntSvcAthQuantity")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_UntSvcAthQuantity")).sendKeys(units);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaAuthorizationEffectiveDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaAuthorizationEffectiveDate")).sendKeys(Common.convertSysdate());
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaAuthorizationEndDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaAuthorizationEndDate")).sendKeys(Common.convertSysdatecustom(365));
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaymentMethod"))).selectByVisibleText("Pay System Price");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDMEPanel_addAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PALineItemDMEList_0:PaDMELineItemBean_ColValue_status")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n1")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_LetterIndicator"))).selectByVisibleText("No Print");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 Thread.sleep(12000);
	 changeStatus("0","IN REVIEW","DME");
	 changeStatus("0","APPROVED","DME");
	 changeStatus("1","IN REVIEW","DME");
	 statusToDeferred("1");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PALineItemDMEList_0:PaDMELineItemBean_ColValue_status")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n103")).click();
	 extText("Provider","This is comment for provider at line level","B");
	 extText("Member","This is comment for member at line level","B");
	 extText("Both","This is comment for both at line level","B");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 Common.cancelAll();
	 //TC#23191--PA Info - Cornelius Date system calculated
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[10]/td[2]")).getText().trim().contains(""),"The deffered date in PA information panel is not empty");
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[11]/td[2]")).getText().trim().contains(""),"The information response date in PA information panel is not empty");
	 log("TC#23191--Checking the Cornelius Date displayed on PA Information panel is correct when PA was never deferred is done");
	 }
	
	
	public static void extText(String comment,String desc,String linum){
	driver.findElement(By.id("MMISForm:MMISBodyContent:PaExtTextPanel:PaExtText_NewButtonClay:PaExtTextList_newAction_btn")).click();
    driver.findElement(By.id("MMISForm:MMISBodyContent:PaExtTextPanel:PaExtTextDataPanel_LineItemNumber")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:PaExtTextPanel:PaExtTextDataPanel_LineItemNumber")).sendKeys(linum);
	driver.findElement(By.id("MMISForm:MMISBodyContent:PaExtTextPanel:PaExtTextDataPanel_PaTextDescription")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:PaExtTextPanel:PaExtTextDataPanel_PaTextDescription")).sendKeys(desc);
	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaExtTextPanel:PaExtTextDataPanel_IndComment"))).selectByVisibleText(comment);
	driver.findElement(By.id("MMISForm:MMISBodyContent:PaExtTextPanel:PaExtTextPanel_addAction_btn")).click();
	}
	
	
	@Test
	 //Validate the Requesting Provider Search function    DONE
	 public static void test23190() throws Exception{
	 TestNGCustom.TCNo="23190";
	 log("//TC 23190_23219");
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_ProviderId")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_ProviderId")).sendKeys("110048577");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:ProviderIDSearchControl")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:ProviderIDSearchControl")).sendKeys("A");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 int rownum =driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:MainPaSearchResults:tbody_element']/tr")).size();
     for(i=1;i<=rownum;i++)
     {
        if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:MainPaSearchResults:tbody_element']/tr["+i+"]/td[3]")).getText().equals("PHYSICIAN ADULT"))
     	    {
     		break;
	        }
     }
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_"+(i-1)+":_id13")).click();
	// Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[3]/td[2]")).getText().trim().contains("VEEN"),"The search result is not for the requesting provider id:110048577A");
	 //TC#23219:Update a PA Line row
	 driver.findElement(By.linkText("Line Item")).click();
	 driver.findElement(By.linkText("Saved")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_UntSvcRequirementQuantity")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_UntSvcRequirementQuantity")).sendKeys("10");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemPanel_updateAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PALineItemBasicList_0:PaLineItemBean_ColValue_untSvcRequirementQuantity")).getText().trim().equals("10"),"Updating PA line row is not successfull");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PALineItemBasicList_0:PaLineItemBean_ColValue_status")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_UntSvcRequirementQuantity")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_UntSvcRequirementQuantity")).sendKeys("7");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemPanel_updateAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 log("TC# 23219 is included in TC#23190.Both are PASS");
	 }
	
	
	
	 public static void submitPA(String tcNo) throws SQLException{
		 if(tcNo.equals("23192"))
		 {
			 randomMemberid();
		 }
  		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:NEW")).click();
  	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_PaAssignCode"))).selectByVisibleText("PHYSICIAN ADULT");
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_ReBase")).clear();
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_ReBase")).sendKeys(colValues.get(0));
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauth_ProvIdBox")).sendKeys("110029098");
  	                          
  	  	driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_PrSvcLoc")).sendKeys("A");
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauth_ProvIdBox")).sendKeys("110087267");
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_PrSvcLoc")).sendKeys("A");
  	    driver.findElement(By.xpath("//input[contains(@id, 'ReqContactNameId')]")).sendKeys("PAregression");
        driver.findElement(By.xpath("//input[contains(@id, 'ReqContactPhoneId')]")).sendKeys("6176176177");
       // driver.findElement(By.xpath("//input[contains(@id, 'ReqContactFaxId')]")).sendKeys("6176176177");
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItem_NewButtonClay:PaLineItemList_newAction_btn")).click();
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_providerID")).sendKeys("110087267");
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:ProviderIDSearchControl")).sendKeys("A");
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_ReqProc")).sendKeys("0001F");
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_UntSvcRequirementQuantity")).clear();
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_UntSvcRequirementQuantity")).sendKeys("9");
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaRequirementEffectiveDate")).clear();
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaRequirementEffectiveDate")).sendKeys(Common.convertSysdate());
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaRequirementEndDate")).clear();
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaRequirementEndDate")).sendKeys(Common.convertSysdate());
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemPanel_addAction_btn")).click();
  	    driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
  	     }
	 
	 @Test
	 //Checking for duplicate PA    DONE
	 public static void test23192() throws Exception{
	 TestNGCustom.TCNo="23192";
	 log("//TC 23192");
	 submitPA("23192");
	 warnings();
	 Common.saveAll();
	 Common.cancelAll();
	 submitPA("23192a");
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td[2]")).getText().trim().contains("Duplicate on Tracking Number"),"Validation for error mess:Duplicate on Tracking Number failed");
	 log("Checking for duplicate PA message in Base is done");
	 Common.cancelAll();
	 }
	 
	 //change code to check all error message
	 @Test
	 //PA Line Item Void check for claims that have paid Test Case TRACKING NUM HARD CODED
	 public static void test23193() throws Exception{
	 TestNGCustom.TCNo="23193";
	 log("//TC 23193");
	 memberId=claimsMemQuery("23193");
     createPABase("23193","DME",memberId);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 Thread.sleep(12000);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	 changeStatus("0","IN REVIEW","DME");
	 changeStatus("0","APPROVED","DME");
	 //driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	 String paNum23193=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim();
	 System.out.println("PAnum from TC#23193: "+paNum23193);
	 log("PA num:"+paNum23193);  
	 String pas=paNum23193;  
	 Common.portalLogin();
	
	 String provider="110029098A";
	
		
	// String memberId=getMemId("23193");
	 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
	 advSubmitClaims.pos="12 - HOME";
	 advSubmitClaims.orderingProv[0]="110000254";
	 advSubmitClaims.initiateClaim();
	 advSubmitClaims.M("23193", "M", provider, "3000.00", "", "0", "", pas, memberSql,"0","0");
	 clmNo = advSubmitClaims.clmICN("prof");
	 status=driver.findElement(By.xpath("//span[contains(@id,'claimStatusText')]")).getText().trim();
     System.out.println("ICN from TC#23193:"+clmNo);
	 log("ICN:"+clmNo);
	 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id,'claimStatusText')]")).getText().trim().equals("Paid"),"The claim submitted is not paid");
	 Common.portalLogout();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PriorAuth")).click();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_PriorAuthorizationNumber")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_PriorAuthorizationNumber")).sendKeys(pas);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n129")).click();
   //driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n133")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n132")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PALineItemDMEList_0:PaDMELineItemBean_ColValue_paLineItemNumber")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaLineitemStat"))).selectByVisibleText("VOID");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 
	 boolean isInvalid=false;
	 if(driver.findElements(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr")).size() > 2) {
		 
		 for (int i = 2; i <= driver.findElements(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr")).size(); i++) {
			
			 if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr["+i+"]/td[2]")).getText().trim().contains("VOID is invalid")) 
				 isInvalid=true;
			 
		 }
		 
	 }
	 
	// Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td[2]")).getText().trim().contains("VOID is invalid"),"The VOID is invalid warning message is not found");
	 Assert.assertTrue(isInvalid,"The VOID is invalid warning message is not found");

	 
	 Common.cancelAll();
	 }
	 
	 
	 public void serProvSrch(String assgn,String tcNo) throws SQLException{
		 sqlStatement="select * from r_pa where TCNO='"+tcNo+"'";
		 colNames.add("ASSGNMENT");//0
		 colNames.add("MEMBERID");//1
		 colNames.add("SERPROVIDERID");//2
		 colValues=Common.executeQuery1(sqlStatement,colNames);
		 String provider=colValues.get(2).substring(0, colValues.get(2).length()-1),
		          svcloc=colValues.get(2).substring(colValues.get(2).length()-1);
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:NEW")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_PaAssignCode"))).selectByValue(colValues.get(0));
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_ReBase")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_ReBase")).sendKeys(colValues.get(1));
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauth_ProvIdBox")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauth_ProvIdBox")).sendKeys(provider);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_PrSvcLoc")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_PrSvcLoc")).sendKeys(svcloc);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgn+"Panel:PaLineItem_NewButtonClay:PaLineItemList_newAction_btn")).click();
	 driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaLineItem"+assgn+"Panel:ProviderIDSearchControl_CMD_SEARCH']/img")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgn+"Panel:_id42:ServiceAuthorizationProviderSearchCriteriaPanel:ProviderID")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgn+"Panel:_id42:ServiceAuthorizationProviderSearchCriteriaPanel:ProviderID")).sendKeys(provider);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgn+"Panel:_id42:ServiceAuthorizationProviderSearchCriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgn+"Panel:_id42:ServiceAuthorizationProviderSearchResults_0:column1Value")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgn+"Panel:PaLineItemDataPanel_ServiceProviderName")).click();
		 
	 }
	
	
	 @Test
	 //Basic Medical panel validate Service Provider Search
	 public void test23194() throws Exception{
	 TestNGCustom.TCNo="23194";
	 log("//TC 23194");
	 serProvSrch("","23194");
	 Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_ServiceProviderName")).getText().trim().contains("BICKLEY"),"The Basic Medical panel service provider:BICKLEY,BARRY T search is not successfull");
	 log("Validating Service provider search in Basic Medical panel is done");
	 Common.cancelAll();
	 }
	 
	
	 @Test
	 //DME panel validate Service Provider Search
	 public void test23195() throws Exception{
	 TestNGCustom.TCNo="23195";
	 log("//TC 23195");
	 serProvSrch("DME","23195");
	 Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_ServiceProviderName")).getText().trim().contains("DENMARK'S"),"The DME panel service provider:DENMARK'S INC N B search is not successfull");
	 log("Validating service provider search in DME panel is done");
	 Common.cancelAll();
	 }
	 
	 
	 @Test
	 //Therapy panel validate Service Provider Search
	 public void test23196() throws Exception{
	 TestNGCustom.TCNo="23196";
	 log("//TC 23196");
	 serProvSrch("Therapy","23196");
	 Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemTherapyPanel:PaLineItemDataPanel_ServiceProviderName")).getText().trim().contains("BRAINTREE"),"The Therapy panel service provider:BRAINTREE REHAB HOSPITAL search is not successfull");
	 log("Validating service provider search in Therapy panel is done");
	 Common.cancelAll();
	 }
	 
	 @Test
	 //Reason Code Description 
	 public void test23199() throws Exception{
	 TestNGCustom.TCNo="23199";
	 log("//TC 23199");
	 memberId=randomMemberid();
	 createPABase("23199","Therapy",memberId);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemTherapyPanel:PALineItemTherapyList_0:PaTherapyLineItemBean_ColValue_status")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemTherapyPanel:PaLineItemDataPanel_icd9TypeList"))).selectByVisibleText("Diagnosis");
	 //driver.findElement(By.cssSelector("img[alt=\"Pertinent ICD9 Code pop-up search\"]")).click();
	 driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaLineItemTherapyPanel:PaLineItemDataPanel_Diagnosis_CMD_SEARCH']/img")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemTherapyPanel:_id42:SADiagnosisPopupSearchCriteriaPanel:Diagnosis")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemTherapyPanel:_id42:SADiagnosisPopupSearchCriteriaPanel:Diagnosis")).sendKeys("J206");//7580   j206
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemTherapyPanel:_id42:SADiagnosisPopupSearchCriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemTherapyPanel:_id42:SADiagnosisPopupSearchResults_0:column1Value")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 Thread.sleep(12000);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();

	 changeStatus("0","IN REVIEW","Therapy");	
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemTherapyPanel:PALineItemTherapyList_0:PaTherapyLineItemBean_ColValue_status")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemTherapyPanel:PaLineItemDataPanel_PaLineitemStat"))).selectByVisibleText("DENIED");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemTherapyPanel:PaTherapyIacXrefPanel:PaIacXref_NewButtonClay:PaIacXrefList_newAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemTherapyPanel:PaTherapyIacXrefPanel:PaReasonCodeDataPanel_reasonCode")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemTherapyPanel:PaTherapyIacXrefPanel:PaReasonCodeDataPanel_reasonCode")).sendKeys("0601");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemTherapyPanel:PaTherapyIacXrefPanel:PaTherapyIacXrefPanel_addAction_btn")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_LetterIndicator"))).selectByVisibleText("Batch");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 Common.cancelAll();
	 log("Validating for Reason code description is done");
	 }
	 
	 @Test
	 //Search for and open an existing PA
	 public static void test23201() throws Exception{
	 TestNGCustom.TCNo="23201";
	 log("//TC 23201");
	 String sql="select sak_pa_prov,cde_service_loc from T_PA_PAUTH where rownum<2";
	 colNames.add("SAK_PA_PROV");//0
	 colNames.add("CDE_SERVICE_LOC");//1
	 colValues=Common.executeQuery(sql,colNames);
	 String svcLoc=colValues.get(1);
	 String sql1="select id_provider from t_pr_prov where sak_prov='"+colValues.get(0)+"'";
	 colNames.add("ID_PROVIDER");
	 colValues=Common.executeQuery(sql1,colNames);
	 String provider=colValues.get(0);
	 /*
	 String provider="110000003";
	 String svcLoc="A";
	 */
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_ProviderId")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_ProviderId")).sendKeys(provider);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:ProviderIDSearchControl")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:ProviderIDSearchControl")).sendKeys(svcLoc);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 String trackNum=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13']")).getText().trim();
	 String paNum=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:MainPaSearchResults:tbody_element']/tr[1]/td[2]")).getText().trim();
	 String memberId=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:MainPaSearchResults:tbody_element']/tr[1]/td[4]")).getText().trim();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:CLEAR")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).sendKeys(trackNum);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim().contains(paNum),"The panum in the info panel is not for tracknum");
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[5]/td[2]")).getText().trim().contains(memberId),"The memberid in the info panel is not for tracknum");
	 log("Searching and opening for an existing PA is done");
	 }
	 
	 
	    public static String claimsMemQuery(String tcNo) throws SQLException{
	    	
//		String memberSql = "select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ base.* from  t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid,t_re_elig e," +
//				    "t_cde_aid aid,t_re_aid_elig elig where " +
//				    "elig.sak_recip=base.sak_recip " +
//				    "and base.sak_recip = e.sak_recip "+
//				    "and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
//				    "and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
//				    "and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
//				    "and pgm. CDE_PGM_HEALTH='STD' " +
//				    "and elig.DTE_END='22991231' " + 
//				    "and not exists ( select sak_recip from t_re_pmp_assign asg where asg.sak_recip=base.sak_recip and asg.dte_end> 20130401) " +
//				    "and not exists ( select sak_recip from t_tpl_resource rs where rs.sak_recip=base.sak_recip and rs.dte_end> 20130401) " +
//				    "and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip and hib.dte_end> 20130401) " +
//				    "and not exists (select sak_recip from t_pa_pauth pa where pa.sak_recip=base.sak_recip) "+
//				    "and base.ind_active='Y' "+ 
//				    "and base.sak_recip > dbms_random.value * 6300000 " +
//				    "and rownum<2";
				    String memberSql= "select /*+RULE*/ base.* from t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid," +
							"t_cde_aid aid,t_re_aid_elig elig where " +
							"elig.sak_recip=base.sak_recip and base.dte_death='0' " +
							"and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
							"and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
							"and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
							"and pgm. CDE_PGM_HEALTH='STD' " +
							"and elig.DTE_END='22991231' " + 
		    				"and elig.cde_status1<>'H' " +
							"and not exists ( select sak_recip from t_re_pmp_assign asg where asg.sak_recip=base.sak_recip and asg.dte_end> to_char(sysdate, 'yyyyMMdd')) " +
							"and not exists ( select sak_recip from t_pa_pauth pa where pa.sak_recip=base.sak_recip) " +
							"and not exists ( select sak_recip from t_tpl_resource rs where rs.sak_recip=base.sak_recip and rs.dte_end> to_char(sysdate, 'yyyyMMdd')) " +
							"and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip and hib.dte_end> to_char(sysdate, 'yyyyMMdd')) " +
							"and base.ind_active='Y' "+ 
							"and base.sak_recip > dbms_random.value * 6300000 and rownum<2";							
						//	" and id_medicaid <> '200004022026' and id_medicaid <> '200012158936' and id_medicaid <> '200004214115' and id_medicaid <> '200000767022' and id_medicaid <> '200003411408' and id_medicaid <> '200002456109' and id_medicaid <> '200002711767' and id_medicaid <> '200003285490' and id_medicaid <> '100036287025' and id_medicaid <> '100223977339' and id_medicaid <> '100224074714' and id_medicaid <> '200000169649' and id_medicaid <> '200001760980' and id_medicaid <> 200004226824 and id_medicaid <> 100036654463";
         colNames.add("ID_MEDICAID");
         System.out.println("MID: "+memberId);
         System.out.println("SQL: "+memberSql);
         colValues=Common.executeQuery(memberSql,colNames);
         memberId=colValues.get(0); 
         System.out.println("MID: "+memberId);
//         String sql="update r_pa set MEMBERID="+memberId+" where TCNO='"+tcNo+"'";
//         colNames.add("ID_MEDICAID");
//         Common.executeQuery1(sql,colNames);
		 return memberId;
		 }
	 
	 
	 public static void createPABase(String tcNo,String assgnment,String memberId) throws SQLException{
//		if(!(tcNo.equals("23206a"))){
//	 String memberSql = "select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ base.* from  t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid,t_re_elig e," +
//					    "t_cde_aid aid,t_re_aid_elig elig where " +
//					    "elig.sak_recip=base.sak_recip " +
//					    "and base.sak_recip = e.sak_recip "+
//					    "and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
//					    "and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
//					    "and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
//					    "and pgm. CDE_PGM_HEALTH='STD' " +
//					    "and elig.DTE_END='22991231' " + 
//					    "and not exists ( select sak_recip from t_re_pmp_assign asg where asg.sak_recip=base.sak_recip and asg.dte_end> 20130401) " +
//					    "and not exists ( select sak_recip from t_tpl_resource rs where rs.sak_recip=base.sak_recip and rs.dte_end> 20130401) " +
//					    "and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip and hib.dte_end> 20130401) " +
//					    "and not exists (select sak_recip from t_pa_pauth pa where pa.sak_recip=base.sak_recip) "+
//					    "and base.ind_active='Y' "+ 
//					    "and base.sak_recip > dbms_random.value * 6300000 " +
//					    "and rownum<2";
//	     colNames.add("ID_MEDICAID");
//	     colValues=Common.executeQuery(memberSql,colNames);
//	     memberId=colValues.get(0);
//	     SelSql="select * from R_DAY2 where tc = '"+tcNo+"'";
//		 col="ID";
//		 DelSql="delete from R_DAY2 where tc = '"+tcNo+"'";  
//		 InsSql="insert into  R_DAY2 values ('"+tcNo+"', '"+memberId+"', 'PA MemberId', '"+Common.convertSysdate()+"')";
//		 Common.insertData(SelSql, col, DelSql, InsSql);
//	 }
		 sqlStatement=("select * from r_pa where TCNO='"+tcNo+"'");
		 colNames.add("ASSGNMENT");//0
		 colNames.add("MEMBERID"); //1   
		 colNames.add("REQPROVIDERID"); //2
		 colNames.add("PROCCODE"); //3
		 colNames.add("MODIFIER");//4
		 colNames.add("UNITS"); //5
		 colNames.add("AUTHPROCCODE");//6
		 colNames.add("AUTHMODIFIER");//7
		 colNames.add("AUTHUNITS");//8
		 colValues=Common.executeQuery1(sqlStatement, colNames );
		 providerId=colValues.get(2);
		 String provider=colValues.get(2).substring(0, colValues.get(2).length()-1),
		          svcloc=colValues.get(2).substring(colValues.get(2).length()-1);
		// memberId=colValues.get(1);
		 modifier=colValues.get(4);
		 units=colValues.get(5);
		 authModifier=colValues.get(7);
		 authUnits=colValues.get(8);   
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:NEW")).click();
     new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_PaAssignCode"))).selectByValue(colValues.get(0));
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_ReBase")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_ReBase")).sendKeys(memberId);	 
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauth_ProvIdBox")).sendKeys(provider);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaPauthBaseInformationPanel:PaPauthDataPanel_PrSvcLoc")).sendKeys(svcloc);
	 driver.findElement(By.xpath("//input[contains(@id, 'ReqContactNameId')]")).sendKeys("PAregression");
     driver.findElement(By.xpath("//input[contains(@id, 'ReqContactPhoneId')]")).sendKeys("6176176177");
   //  driver.findElement(By.xpath("//input[contains(@id, 'ReqContactFaxId')]")).sendKeys("6176176177");
	// driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItem_NewButtonClay:PaLineItemList_newAction_btn")).click();
     driver.findElement(By.xpath("//input[contains(@id, 'PaLineItemList_newAction_btn')]")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_providerID")).clear();
	                         //  MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_providerID
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_providerID")).sendKeys(provider);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:ProviderIDSearchControl")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:ProviderIDSearchControl")).sendKeys(svcloc);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_ReqProc")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_ReqProc")).sendKeys(colValues.get(3));
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_Modifier")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_Modifier")).sendKeys(modifier); 
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_UntSvcRequirementQuantity")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_UntSvcRequirementQuantity")).sendKeys(units);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaRequirementEffectiveDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaRequirementEffectiveDate")).sendKeys(Common.convertSysdate());
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaRequirementEndDate")).clear();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaRequirementEndDate")).sendKeys(Common.convertSysdate());
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_AuthProc")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_AuthProc")).sendKeys(colValues.get(6));
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_AuthorizedModifier")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_AuthorizedModifier")).sendKeys(authModifier); 
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_UntSvcAthQuantity")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_UntSvcAthQuantity")).sendKeys(authUnits);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaAuthorizationEffectiveDate")).clear();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaAuthorizationEffectiveDate")).sendKeys(Common.convertSysdate());
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaAuthorizationEndDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaAuthorizationEndDate")).sendKeys(Common.convertSysdate());
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaymentMethod"))).selectByVisibleText("Pay System Price");
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItem"+assgnment+"Panel_addAction_btn")).click();	 
	 }
	 
	 
	 public static void changeStatus(String rownum,String status,String assgnment){
	 String trackNum1=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
	 System.out.println("Tracking number: "+trackNum1);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PALineItem"+assgnment+"List_"+rownum+":Pa"+assgnment+"LineItemBean_ColValue_status")).click();//0
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaLineitemStat"))).selectByVisibleText(status);//"IN REVIEW"
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItem"+assgnment+"Panel_updateAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 String message=driver.findElement(By.cssSelector("td.message-text")).getText();
	 System.out.println("Message: "+message);
	 //Avoiding You and another user error
	 if(message.contains("You and another user have attempted to save the same record at the same time. This is not allowed. Re-enter your data and try to save the record again.")){
		 Common.cancelAll();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaMiniSearchTrackingNumberInput")).sendKeys(trackNum1);
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaMiniSearch_SearchButton")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
		 driver.findElement(By.linkText("Line Item")).click();
		 changeStatus(rownum,status,assgnment);
	     }
	 else{
		 Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"..."); 
	 }
	    
	
	 }
	
	 
	 @Test
	 //Enter an internal PA request for a Helmet where Provider ineligible  *MEMBERID HARDCODED*
	 public void test23203() throws Exception{
	 TestNGCustom.TCNo="23203";
	 log("//TC 23203");
	// claimsMemQuery("23203");
	 memberId=randomMemberid();
	 System.out.println("Member id: "+memberId);
	// String sql="update r_pa set MEMBERID='"+memberId+"' where TCNO='23203'";
	// colNames.add("ID_MEDICAID");
    // colValues=Common.executeQuery1(sql,colNames);
	 createPABase("23203","DME",memberId);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 //Checking the warning message
	// Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[3]/td[2]")).getText().trim().contains("Provider is not allowed to request"),"Validation for warning message:provider is not allowed to request in base failed");
	 log("Entering an internal PA request for a Helmet where Provider ineligible is done");
	// Common.cancelAll();
	 }
	 
	 @Test
	 //Validate duplicate internal PA request for DME Enterals(PA)
	 public void test23206() throws Exception{
	 TestNGCustom.TCNo="23206";
	 log("//TC 23206");
	 memberId=randomMemberid();
	 createPABase("23206","DME",memberId);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 createPABase("23206a","DME",memberId);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td[2]")).getText().trim().contains("Duplicate on Tracking Number"),"Validation for error message:Duplicate on Tracking Number failed");
	 log("Validating duplicate internal PA request for DME Enterals is done");
	 //Common.cancelAll();
	 }
	 
	 @Test
	 //Validate PA Auto Approval using PA Rpt Request panel to create(PA)  *NEED TO RUN BATCH JOB AND CHECK THE REPORT NEXT DAY*
	 public void test23208() throws Exception{
	 TestNGCustom.TCNo="23208";
	 log("//TC 23208");
	 statusReport("Prior Authorization Auto Approval Report","EXCEL");
	 String message=driver.findElement(By.cssSelector("td.message-text")).getText();
	 Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
	 log("Requested for reports need to check them on day2");
	 //CHECK POINT
	 }
	 
	 @Test
	 //Validate PA Status Report using PA Rpt Request panel to create
	 public  void test23209() throws Exception{
	 TestNGCustom.TCNo="23209";
	 log("//TC 23209");
	 statusReport("Prior Authorization Status Report","PDF");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaReportNavigatorPanel:PaReportNavigatorId:CANCELALL")).click();
	 Thread.sleep(5000);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaReportNavigatorPanel:PaReportNavigatorId:ITM_n5")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:AsyncReportSearch:AsyncReportSearchBean_CriteriaPanel:FunctionalArea"))).selectByVisibleText("PRIOR AUTHORIZATION");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:AsyncReportSearch:AsyncReportSearchBean_CriteriaPanel:ReportRequestDataPanel_UserID")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:AsyncReportSearch:AsyncReportSearchBean_CriteriaPanel:ReportRequestDataPanel_UserID")).sendKeys("BGENER00");
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:AsyncReportSearch:AsyncReportSearchBean_CriteriaPanel:ReportTitle"))).selectByVisibleText("Prior Authorization Status Report");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:AsyncReportSearch:AsyncReportSearchBean_CriteriaPanel:SEARCH")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:AsyncReportSearch:AsyncReportSearchResultsDataTable:tbody_element']/tr/td[2]/span")).getText().trim().contains("Prior Authorization Status Report"),"Validation of PA status reoprt failed");
	 log("Validating PA Status Report using PA Rpt Request panel to create is done");
	 }
	 
	 
	 public void statusReport(String title,String format){
		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaReportNavigatorPanel:PaReportNavigatorId:ITM_n1")).click();
		 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaRequestReport:ReportTitle"))).selectByVisibleText(title);
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaRequestReport:RequestReportDataPanel_DateFrom")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaRequestReport:RequestReportDataPanel_DateFrom")).sendKeys(Common.firstDateOfPreviousMonth());
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaRequestReport:RequestReportDataPanel_DateTo")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaRequestReport:RequestReportDataPanel_DateTo")).sendKeys(Common.lastDateOfPreviousMonth());
		 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaRequestReport:RequestReportDataPanel_FormatList"))).selectByVisibleText(format);
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaRequestReport:PaRequestReport_submitSaveAction_btn")).click();	 
	     }
	 
	 
	 //exclude
	 @Test
	 //Validate PA routing to MCB Review queue
	 //This test case is working only in IE8(tested in 15.01) passed with browser compatibility
	 public static void test23210() throws Exception{
	 TestNGCustom.TCNo="23210";
	 log("//TC 23210");
	 
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Worklist")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:_id12")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:WorkflowWorklistResults:_id46")).click();//Sorting on recieved date
	 driver.findElement(By.xpath("//*[@id='workList[0]']")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:_id99")).click();//Clicking review button
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PALineItemBasicList_0:PaLineItemBean_ColValue_status")).click();//clicking on line item
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaRequirementEndDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaRequirementEndDate")).sendKeys(Common.convertSysdatecustom(5));
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaLineitemStat"))).selectByValue("28");//Selecting IN REVIEW status
	 Common.save();
	 String paNum=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[4]/td[2]")).getText().trim();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:Worklist_link")).click();
	 driver.findElement(By.xpath("//*[@id='workList[0]']")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:_id100")).click();//Clicking Route button
	 driver.findElement(By.id("MMISForm:MMISBodyContent:WorkflowWorklistBean_radioValuePerson1")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:WorkflowRouting_DropDownPersonType"))).selectByValue("BGENER00");
	 Common.save();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Home")).click();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Worklist")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:WorkflowWorklist_DropDownQueueType"))).selectByValue("3");//Assigned status
	 driver.findElement(By.id("MMISForm:MMISBodyContent:_id12")).click();
	 int rownumqueue =driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:WorkflowWorklistResults:tbody_element']/tr")).size();
	    for(i=1;i<=rownumqueue;i++)
       {
	    if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:WorkflowWorklistResults:tbody_element']/tr["+i+"]/td[2]")).getText().trim().equals(paNum))
	    break;
       }
	  log("PA num is moved to Assigned Queue");
	  }
	
	 
	 @Test
	 //View Dental PA request through internal system
	 public static void test23211() throws Exception{
	 TestNGCustom.TCNo="23211";
	 log("//TC 23211");
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_ProviderId")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_ProviderId")).sendKeys("110022061");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:ProviderIDSearchControl")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:ProviderIDSearchControl")).sendKeys("B");
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaAssignmentCode"))).selectByVisibleText("DG - DENTAL GENERAL");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 String paNum=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:MainPaSearchResults:tbody_element']/tr[1]/td[2]")).getText().trim();
	 String memberId=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:MainPaSearchResults:tbody_element']/tr[1]/td[4]")).getText().trim();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[4]/td[2]")).getText().trim().contains("DENTAL GENERAL"),"Validation of PA status reoprt failed");
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[5]/td[2]")).getText().trim().contains(memberId),"Validation of PA status reoprt failed");
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim().contains(paNum),"Validation of PA status reoprt failed");
	 log("Viewing Dental PA request through internal system is done");
	 }
	 
	 @Test
	 //Web portal attachment upload 
	 public void test23213() throws Exception{
	 TestNGCustom.TCNo="23213";
	 log("//TC 23213");
	 memberId=randomMemberid();
	 createPABase("23213","DME",memberId);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 Thread.sleep(12000);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
	 changeStatus("0","IN REVIEW","DME");
	 statusToDeferred("0");
	 String trackNum1=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
	// String trackNum1="108161113";
	 System.out.println("Tracking num from TC#23213: "+trackNum1);
	 Common.portalLogin();
	 driver.findElement(By.linkText("Manage Service Authorizations")).click();
	 driver.findElement(By.linkText("Prior Authorization")).click();
	 driver.findElement(By.linkText("Inquire/Maintain PA Request")).click();
	 driver.findElement(By.xpath("//input[contains(@id,'trackingNumber')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id,'trackingNumber')]")).sendKeys(trackNum1);
	 driver.findElement(By.xpath("//input[contains(@id,'search_Button')]")).click();
	 driver.findElement(By.xpath("//span[contains(@id, 'trackingNum')]")).click();
	 driver.findElement(By.xpath("//a[contains(@id,'_MENUITEM_lineItems')]")).click();
	 driver.findElement(By.xpath("//a[contains(@id,'_MENUITEM_attachments')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id,'attachmentList:new_Button')]")).click();
	 new Select(driver.findElement(By.xpath("//select[contains(@id,'reportType')]"))).selectByVisibleText("DIAGNOSTIC REPORT");
	 new Select(driver.findElement(By.xpath("//select[contains(@id,'transmissionCode')]"))).selectByVisibleText("ELECTRONICALLY ONLY");
	 driver.findElement(By.xpath("//textarea[contains(@id,'description')]")).clear();
	 driver.findElement(By.xpath("//textarea[contains(@id,'description')]")).sendKeys("This is a test");
	 //driver.findElement(By.id("attachmentsSubView:attachments:j_id_id10pc3:j_id_id36pc3:fileUpload")).sendKeys(tempDirPath+"PA.txt");;
	 driver.findElement(By.xpath("//input[contains(@id,'fileUpload')]")).sendKeys(tempDirPath+"PA.txt");
	 driver.findElement(By.xpath("//input[contains(@id,'add_Button')]")).click();
	 driver.findElement(By.xpath("//a[contains(@id,'_MENUITEM_confirmation')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id,'submit_Button')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id,'ignoreWarnings')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id,'submit_Button')]")).click();
	 trackNum2=driver.findElement(By.xpath("//span[contains(@id,'trackingNumberText')]")).getText().trim();
	 System.out.println("Track# from tc#23213: "+trackNum2);
	 log("Track num:"+trackNum2);
	 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id,'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted after uploading attachment is not submitted successfully");
	 Common.portalLogout();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PriorAuth")).click();
     driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).sendKeys(trackNum2);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 driver.findElement(By.linkText("Line Item")).click();
	 Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PALineItemDMEList_0:PaDMELineItemBean_ColValue_statusDescriptionValue")).getText().trim().contains("ADDITIONAL INFORMATION RECEIVED"),"Validating the status additional info recieved in the base failed");
	 }
	 
	 
	 
	 @Test
	 //Add 26 line item to PA 
	 public void test39259_39260() throws Exception{
	 TestNGCustom.TCNo="39259_39260";
	 log("//TC 39259_39260");
	 memberId=randomMemberid();
	 createPABase("39259","",memberId);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	// String trackNum=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaInformationPanel_TOGGLE_COLLAPSE")).click();
     String paNum=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim();
	 log("PA number with 26 line items: "+paNum);
	 providerId=colValues.get(2);
	 String provider=providerId.substring(0, providerId.length()-1),
	          svcloc=providerId.substring(providerId.length()-1);
	 
	 String assgnment="";
	 for (int i=0; i<procCode.length;i++) {

	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItem_NewButtonClay:PaLineItemList_newAction_btn")).click();
	 if(procCode[i].equals("99238")) {
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td[2]")).getText().trim().contains("Line Item max has been reached"),"Validation for max reach of line item failed,TC# 39260 is Fail,TC# 39259 is PASS");
   	     log("Validating error message, 'Line Item max has been reached' is done, TC# 39260 is also PASS");
	     }
	 else {
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_providerID")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_providerID")).sendKeys(provider);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:ProviderIDSearchControl")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:ProviderIDSearchControl")).sendKeys(svcloc);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_ReqProc")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_ReqProc")).sendKeys(procCode[i]);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_Modifier")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_Modifier")).sendKeys(modifier); 
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_UntSvcRequirementQuantity")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_UntSvcRequirementQuantity")).sendKeys(units);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaRequirementEffectiveDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaRequirementEffectiveDate")).sendKeys(Common.convertSysdate());
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaRequirementEndDate")).clear();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaRequirementEndDate")).sendKeys(Common.convertSysdate());
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_AuthProc")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_AuthProc")).sendKeys(procCode[i]);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_AuthorizedModifier")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_AuthorizedModifier")).sendKeys(authModifier); 
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_UntSvcAthQuantity")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_UntSvcAthQuantity")).sendKeys(authUnits);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaAuthorizationEffectiveDate")).clear();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaAuthorizationEffectiveDate")).sendKeys(Common.convertSysdate());
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaAuthorizationEndDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaAuthorizationEndDate")).sendKeys(Common.convertSysdate());
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItemDataPanel_PaymentMethod"))).selectByVisibleText("Pay System Price");
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItem"+assgnment+"Panel:PaLineItem"+assgnment+"Panel_addAction_btn")).click();	
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
     warnings();
     Common.saveAll();
	         }
	      }
	  }
	 
	 
	 public static void createPA(String tcNo) throws SQLException{
		 sqlStatement=("select * from r_pa where TCNO='"+tcNo+"'" );
		 colNames.add("ASSGNMENT");//0
		 colNames.add("MEMBERID"); //1
		 colNames.add("REQPROVIDERID"); //2
		 colNames.add("DIAGCODE"); //3
		 colNames.add("PROCCODE"); //4
		 colNames.add("UNITS"); //5
		 colValues=Common.executeQuery1(sqlStatement, colNames );
		 driver.findElement(By.linkText("Manage Service Authorizations")).click();
		 driver.findElement(By.linkText("Prior Authorization")).click();
		 driver.findElement(By.linkText("Enter PA Request")).click();
		 new Select(driver.findElement(By.xpath("//select[contains(@id,'paMedicalAssignment')]"))).selectByValue(colValues.get(0)); 
		 driver.findElement(By.xpath("//input[contains(@id,'selectAssignmentType')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id,'memberId')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id,'memberId')]")).sendKeys(colValues.get(1));
		 new Select(driver.findElement(By.xpath("//select[contains(@id,'requestingProvider')]"))).selectByValue(colValues.get(2));
		 driver.findElement(By.xpath("//input[contains(@id, 'contactName')]")).sendKeys("PAregression");
		 driver.findElement(By.xpath("//input[contains(@id, 'contactPhone')]")).sendKeys("6176176177");
		// driver.findElement(By.xpath("//input[contains(@id, 'contactFax')]")).sendKeys("6176176177");
		// driver.findElement(By.xpath("//input[contains(@id, 'icdVersion:0')]")).click(); // selecting icd-9 radio button
		 driver.findElement(By.xpath("//input[contains(@id, 'primaryDiagnosisCode')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'primaryDiagnosisCode')]")).sendKeys(colValues.get(3));
		 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_lineItems')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'new_Button')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'procCode')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'procCode')]")).sendKeys(colValues.get(4)); 
		 driver.findElement(By.xpath("//input[contains(@id, 'reqEffectiveDate')]")).sendKeys(Common.convertSysdate());
		 driver.findElement(By.xpath("//input[contains(@id, 'reqEndDate')]")).sendKeys(Common.convertSysdate());
		 driver.findElement(By.xpath("//input[contains(@id, 'reqUnits')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'reqUnits')]")).sendKeys(colValues.get(5));
		 driver.findElement(By.xpath("//input[contains(@id, 'requestingProviderIndicator')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id,'provider1clear')]")).click();
		 Common.multiElements("//input[contains(@id,'serviceProvider')]").clear();
		 Common.multiElements("//input[contains(@id,'serviceProvider')]").sendKeys("");
		 driver.findElement(By.xpath("//input[contains(@id, 'add_Button')]")).click();
		// driver.findElement(By.xpath("//a[contains(@id, 'add_Button')]")).click();
		 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_confirmation')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
	     }
	 
	 @Test
	 //Validate override warning message-Member is in TPL(PA)
	 public static void test23214() throws Exception{
	 TestNGCustom.TCNo="23214";
	 log("//TC 23214");
	 String sql="select /*+RULE*/ base.* from t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid," +
							"t_cde_aid aid,t_re_aid_elig elig where " +
							"elig.sak_recip=base.sak_recip and base.dte_death='0' " +
							"and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
							"and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
							"and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
							"and pgm. CDE_PGM_HEALTH='STD' " +
							"and elig.DTE_END='22991231' " + 
		    				"and elig.cde_status1<>'H' " +
							//"and not exists ( select sak_recip from t_re_pmp_assign asg where asg.sak_recip=base.sak_recip and asg.dte_end> 20170322) " +
							"and exists ( select sak_recip from t_tpl_resource rs where rs.sak_recip=base.sak_recip and rs.dte_end> 20161231) " +
							//"and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip and hib.dte_end> 20170322) " +
							"and base.ind_active='Y' and base.id_medicaid<>'100219441001' "+ 
							"and base.sak_recip > dbms_random.value * 6300000 " +
							"and rownum<2";
	 colNames.add("ID_MEDICAID");//0
	 colValues=Common.executeQuery(sql, colNames );
	 memberId=colValues.get(0);
	 System.out.println("Member Id: "+memberId);
	 SelSql="select * from R_PA where TCNO = '23214'";
	 col="TCNO";
	 DelSql="delete from R_PA where TCNO ='23214'";  
	 InsSql="insert into  R_PA values ('23214','PA','"+memberId+"','110000309B',' ','A200','0001F',' ',' ',' ','1',' ',' ')";
	 System.out.println("sql before");
	 Common.insertData(SelSql, col, DelSql, InsSql);
	 Common.portalLogin();
	 createPA("23214");
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='confirmationSubView:confirmation']/div[1]/table[2]/tbody/tr[2]/td[2]")).getText().trim().contains("Member has TPL"),"Validation for warning message:Member has TPL failed");
	 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
	 String trackNum=driver.findElement(By.xpath("//span[contains(@id, 'trackingNumberText')]")).getText().trim();
	 System.out.println("Track# from tc#23214: "+trackNum);
	 log("Track num:"+trackNum);
	 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted is not saved successfully");
	 Common.portalLogout();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PriorAuth")).click();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).sendKeys(trackNum);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[19]/td[2]")).getText().trim().contains("Yes"),"Validating medicare is set to yes in base failed");
	 }
	 
	 @Test
	 //Validate override warning message-Member is in MCO
	 public static void test23215() throws Exception{
	 TestNGCustom.TCNo="23215";
	 log("//TC 23215");
	 Common.portalLogin();
	 createPA("23215");
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='confirmationSubView:confirmation']/div[1]/table[2]/tbody/tr[1]/td[2]")).getText().trim().contains("MCO on Submission Date"),"Validation for warning message:Member is in MCO failed");
	 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
	 String trackNum=driver.findElement(By.xpath("//span[contains(@id, 'trackingNumberText')]")).getText().trim();
	 System.out.println("Track# from tc#23215: "+trackNum);
	 log("Track num:"+trackNum);
	 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted is not saved successfully");
	 Common.portalLogout();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PriorAuth")).click();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).sendKeys(trackNum);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[13]/td[2]")).getText().trim().contains("Yes"),"Validating MCO is set to yes in base failed");
	 }
	 
	 @Test
	 //Validate override warning message-Member is in LTC
	 public static void test23216() throws Exception{
	 TestNGCustom.TCNo="23216";
	 log("//TC 23216");
	 
	 Common.portalLogin();
	 createPA("23216");
	 //Assert.assertTrue(driver.findElement(By.xpath("//*[@id='confirmationSubView:confirmation']/div[1]/table[2]/tbody/tr[1]/td[2]")).getText().trim().contains("Member is in LTC"),"Validation for warning message:Member is in LTC failed");
	 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
	 String trackNum=driver.findElement(By.xpath("//span[contains(@id, 'trackingNumberText')]")).getText().trim();
	 System.out.println("Track# from tc#23216: "+trackNum);
	 log("Track num:"+trackNum);
	 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted is not saved successfully");
	 Common.portalLogout();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PriorAuth")).click();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).sendKeys(trackNum);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[20]/td[2]")).getText().trim().contains("Yes"),"Validating LTC is set to yes in base failed");
	 }
	 
	 
	 @Test
	 //Validate override warning message-Member is in EPSDT
	 public static void test31478() throws Exception{
	 TestNGCustom.TCNo="31478";
	 log("//TC 31478");
	 //update proc code =0001F   in DB for 31478
	 Common.portalLogin();
	 createPA("31478");
	 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
	 String trackNum=driver.findElement(By.xpath("//span[contains(@id, 'trackingNumberText')]")).getText().trim();
	 System.out.println("Track# from tc#31478: "+trackNum);
	 log("Track num:"+trackNum);
	 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted is not saved successfully");
	 Common.portalLogout();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PriorAuth")).click();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).sendKeys(trackNum);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[18]/td[2]")).getText().trim().contains("Yes"),"Validating EPSDT is set to yes in base failed");
	 }
	 
	 @Test
	 //Validate override warning message-Member is in MCB
	 public static void test31479() throws Exception{
	 TestNGCustom.TCNo="31479";
	 log("//TC 31479");
	 Common.portalLogin();
	 createPA("31479");
	 
	 if(driver.findElements(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).size() > 0) 
		 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
	 
	 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
	 String trackNum=driver.findElement(By.xpath("//span[contains(@id, 'trackingNumberText')]")).getText().trim();
	 System.out.println("Track# from tc#31479: "+trackNum);
	 log("Track num:"+trackNum);
	 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted is not saved successfully");
	 Common.portalLogout();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PriorAuth")).click();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).sendKeys(trackNum);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[16]/td[2]")).getText().trim().contains("Yes"),"Validating MCB is set to yes in base failed");
	 }
	 
	 
	 @Test
	 //Enter 27th line item
	 public static void test23220() throws Exception{
	 TestNGCustom.TCNo="23220";
	 log("//TC 23220");
	 String sql="select l.sak_pa from T_PA_LINE_ITEM l,T_PA_PAUTH p where l.NUM_PA_LINE_ITEM='X' and l.sak_pa=p.sak_pa and p.sak_pa_assign='19' and rownum<2";
	 colNames.add("SAK_PA");
	 colValues=Common.executeQuery(sql,colNames);
	 String trackNum=colValues.get(0);
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:PaLineItemSearchResultDataPanel_TrackingNumber")).sendKeys(trackNum);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n132")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PALineItemDMEList:PaDMELineItemBean_ColHeader_paLineItemNumber")).click();//sorting on line-item number
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PALineItemDMEList:PaDMELineItemBean_ColHeader_paLineItemNumber")).click();
     String lineNum=driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PALineItemDMEList_0:PaDMELineItemBean_ColValue_paLineItemNumber")).getText().trim();
     System.out.println("The line num is: "+lineNum);
     if(lineNum.equals("Z")){
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItem_NewButtonClay:PaLineItemList_newAction_btn")).click();
     Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td[2]")).getText().trim().contains("Line Item max has been reached"),"Validation for max reach of line item failed");
     }
     else{ 
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItem_NewButtonClay:PaLineItemList_newAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_providerID")).sendKeys("110066943");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:ProviderIDSearchControl")).sendKeys("A");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_ReqProc")).sendKeys("0001F");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_UntSvcRequirementQuantity")).sendKeys("5");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaRequirementEffectiveDate")).sendKeys(Common.convertSysdate());
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaRequirementEndDate")).sendKeys(Common.convertSysdatecustom(1));
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_AuthProc")).sendKeys("0001F");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_UntSvcAthQuantity")).sendKeys("5");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaAuthorizationEffectiveDate")).sendKeys(Common.convertSysdate());
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaAuthorizationEndDate")).sendKeys(Common.convertSysdatecustom(1));
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaymentMethod"))).selectByVisibleText("Pay System Price");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 changeStatus("0","IN REVIEW","DME");
     changeStatus("0","APPROVED","DME");
     Common.cancelAll();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:ITM_n132")).click();
     //driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PALineItemBasicList:PaLineItemBean_ColHeader_paLineItemNumber")).click();//sorting on line-item number
     //driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PALineItemBasicList:PaLineItemBean_ColHeader_paLineItemNumber")).click();

     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PALineItemDMEList:PaDMELineItemBean_ColHeader_paLineItemNumber")).click();//sorting on line-item number
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PALineItemDMEList:PaDMELineItemBean_ColHeader_paLineItemNumber")).click();//sorting on line-item number
     
     String lineNum1=driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PALineItemDMEList_0:PaDMELineItemBean_ColValue_paLineItemNumber")).getText().trim();
     //String lineNum1=driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PALineItemDMEList_0:PaLineItemBean_ColValue_paLineItemNumber")).getText().trim();
     System.out.println("The line num1 is: "+lineNum1);
     if(lineNum1.equals("Z")){
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItem_NewButtonClay:PaLineItemList_newAction_btn")).click();
     Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td[2]")).getText().trim().contains("Line Item max has been reached"),"Validation for max reach of line item failed");
                     }
	         }
     log("Validating Line Item max has been reached whe trying to enter 27th line item is done");
	 }
	 
	 
	 
	
	 
	 public static void statusToDeferred(String num){
     driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PALineItemDMEList_"+num+":PaDMELineItemBean_ColValue_status")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaLineItemDataPanel_PaLineitemStat"))).selectByVisibleText("DEFERRED");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaDMEIacXrefPanel:PaIacXref_NewButtonClay:PaIacXrefList_newAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemDMEPanel:PaDMEIacXrefPanel:PaReasonCodeDataPanel_reasonCode")).sendKeys("1011");
	 //driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaIacXrefPanel:PaIacXrefPanel_addAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 }
	 
	 
	     public static String getPaNum(String tcNo) throws SQLException{
		 String sql="select * from r_pa where TCNO='"+tcNo+"'";
		 colNames.add("PANUM");
		 colValues=Common.executeQuery1(sql,colNames);
		 System.out.println("PAS# from table: "+colValues.get(0));
		 return colValues.get(0);
		 }
	  
		 //Jump To Workflow Paging functionality
		 @Test
		 public static void test27699() throws Exception{
		 
			 TestNGCustom.TCNo="27699";
			 log("//TC 27699");
		 
			 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Worklist")).click();
			 driver.findElement(By.id("MMISForm:MMISBodyContent:PaSearchWorklistBean_CriteriaPanel:SEARCH")).click();
			 driver.findElement(By.linkText("Next")).click();
		 
		 	 Assert.assertTrue(driver.findElements(By.xpath("//*[contains(text(),'Page 2 of ')]")).size() > 0, "Jumping to workflow paging functionality is not as expected");
		 	 log("Jumping to workflow paging functionality is validated");
		 
		 }
	
	
	
	

}
