package newMMIS_Subsystems;



import java.sql.Statement;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PA_Portal extends Login {
	public static Statement statement = null;
	public static String memberId,memId;
	public static String  SelSql, col,  DelSql,  InsSql,sql;
	static int i;
	public static String ReferenceDt = "08/01/2015"; //This is the ICD 9 to 10 cut off date in UAT
	
	public static String randMemSql="select id_medicaid from t_re_base where ind_active = 'Y' and sak_recip > dbms_random.value * 6300000 and rownum < 2";
	
	public static String memSql="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ base.* from  t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid,t_re_elig e," +
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
	
	

	@BeforeMethod
	public void LoginCheck() throws Exception {
		Common.resetPortal();
		testCheckDBLoginSuccessful();
	}
	
	
	public static void portalWarnings(){
		 driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		 if(driver.findElements(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).size()>0)
		   {
		 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
		   }
		 driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
	}
	
	
	public static String icdVersion9() throws Exception {
		String date=Common.convertSysdate();
		Date refEffDt= Common.StrtoDT(ReferenceDt);
		Date effDt= Common.StrtoDT(date);

		if (effDt.after(refEffDt))
			return Common.convertSysdatecustom(-35);
		else
			return Common.convertSysdate();
		
	}
	
	
	
	     //public static String createPA(String tcNo,String pnum,String binum,String assgn,String sql) throws Exception{
		 public static String createPA(String tcNo,String assgn, String sql) throws Exception{
		   if(tcNo.equals("23207a")){
			   colNames.add("memberid");
			   colValues=Common.executeQuery1(sql, colNames );
			   }
		   else{
		    colNames.add("id_medicaid");
		 	colValues=Common.executeQuery(sql, colNames );
		     }
		   memId=colValues.get(0);
		   //Data we are taking from R_PA table except the Member Id
		 sqlStatement=("select * from r_pa where TCNO='"+tcNo+"'");
		 colNames.add("ASSGNMENT");//0
		 colNames.add("MEMBERID"); //1
		 colNames.add("REQPROVIDERID"); //2
		 colNames.add("DIAGCODE"); //3
		 colNames.add("PROCCODE"); //4
		 colNames.add("UNITS"); //5
		 colValues=Common.executeQuery1(sqlStatement,colNames);
		 driver.findElement(By.linkText("Home")).click();
		 driver.findElement(By.linkText("Manage Service Authorizations")).click();
		 driver.findElement(By.linkText("Prior Authorization")).click();
		 driver.findElement(By.linkText("Enter PA Request")).click();
		 new Select(driver.findElement(By.id("enterpa:j_id_id2pc3:pa"+assgn+"Assignment"))).selectByValue(colValues.get(0)); 
		 Common.getPageError("//form[@id='enterpa']/div[1]/table[2]/tbody/tr");//To catch the error in template page
		 
		 driver.findElement(By.xpath("//input[contains(@id,'selectAssignmentType')]")).click();
		 //driver.findElement(By.id("enterpa:j_id_id20pc3:selectAssignmentType")).click();
		 driver.findElement(By.xpath("//input[contains(@id,'memberId')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id,'memberId')]")).sendKeys(memId);
		 new Select(driver.findElement(By.xpath("//select[contains(@id,'requestingProvider')]"))).selectByValue(colValues.get(2));
		 driver.findElement(By.xpath("//input[contains(@id, 'contactName')]")).sendKeys("PAsmoke");
		 driver.findElement(By.xpath("//input[contains(@id, 'contactPhone')]")).sendKeys("6176176177");
		// driver.findElement(By.xpath("//input[contains(@id, 'contactFax')]")).sendKeys("6176176177");
		 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_lineItems')]")).click();
		 Common.getPageError("//form[@id='baseInfoSubView:baseInfo']/div[1]/table[2]/tbody/tr");//To catch the error in Base info page
		 driver.findElement(By.xpath("//input[contains(@id, 'new_Button')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'procCode')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'procCode')]")).sendKeys(colValues.get(4)); //"S9128"
		 driver.findElement(By.xpath("//input[contains(@id, 'reqEffectiveDate')]")).sendKeys(Common.convertSysdate());
		 driver.findElement(By.xpath("//input[contains(@id, 'reqEndDate')]")).sendKeys(Common.convertSysdate());
		 driver.findElement(By.xpath("//input[contains(@id, 'reqUnits')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'reqUnits')]")).sendKeys(colValues.get(5));
		 driver.findElement(By.xpath("//input[contains(@id, 'requestingProviderIndicator')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id,'provider1clear')]")).click();
		 Common.multiElements("//input[contains(@id,'serviceProvider')]").clear();
		 Common.multiElements("//input[contains(@id,'serviceProvider')]").sendKeys("");
		 driver.findElement(By.xpath("//input[contains(@id, 'add_Button')]")).click();
		 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_confirmation')]")).click();
		 Common.getPageError("//form[@id='lineItems']/div[1]/table[2]/tbody/tr");//To catch the error in Line items page
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
		 return memId;
		 }
	 
	 
	     public static String warningsSubmitPA() throws Exception{
		 portalWarnings();	
		 Common.getPageError("//form[@id='confirmationSubView:confirmation']/div[1]/table[2]/tbody/tr");//To catch the error in Confirmation page
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted for Speech therapy is not successfull");
		 String tNum=driver.findElement(By.xpath("//span[contains(@id, 'trackingNumberText')]")).getText().trim();
		 driver.findElement(By.xpath("//input[contains(@id, 'requestAnother_Button')]")).click();
		 log("Tracking num:"+tNum);
		 return tNum;
	     }
		 
	     
	     @Test
		 //Validate override warning message-Member is in TPL(PA)
		 public static void test23214() throws Exception{
		 TestNGCustom.TCNo="23214";
		 log("//TC 23214");
		sql="select distinct b.id_medicaid, b.sak_recip,b.num_ssn from t_re_base b,t_re_pmp_assign asg1 where  substr(asg1.dte_end,1,6) > substr(to_char(sysdate,'YYYYMMDD'),1,6) and asg1.cde_status1 <> 'H' and b.sak_recip= asg1.sak_recip " +
			"and ind_active <> 'N' and dte_death='0'  and exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_effective<to_char(sysdate, 'YYYYMMDD') and dte_end='22991231') and asg1.dte_effective<to_char(sysdate, 'YYYYMMDD') " +
			"and exists ( select sak_recip from t_tpl_resource rs where rs.sak_recip=b.sak_recip and rs.dte_end> to_char(sysdate, 'YYYYMMDD')) and b.sak_recip > dbms_random.value * 6300000 and rownum<2"; 
		//sql="select * from t_re_base where id_medicaid='100051754909'";
		 createPA("23214","Medical",sql);
		 //Assert.assertTrue(driver.findElement(By.xpath("//*[@id='confirmationSubView:confirmation']/div[1]/table[2]/tbody/tr[2]/td[2]")).getText().trim().contains("Member has TPL"),"Validation for warning message:Member has TPL failed");
		 if(driver.findElements(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).size()>0) {
			 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
		 }
		 //driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
		 
		 String trackNum="";
		 if(driver.findElements(By.xpath("//span[contains(@id, 'trackingNumberText')]")).size()>0) {
			  trackNum=driver.findElement(By.xpath("//span[contains(@id, 'trackingNumberText')]")).getText().trim();
		 }else {
			 
		 }
		 //String trackNum=driver.findElement(By.xpath("//span[contains(@id, 'trackingNumberText')]")).getText().trim();
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
		// "select id_medicaid from t_re_base where ind_active = 'Y' and not exists ( select sak_recip from t_re_pmp_assign asg where asg.sak_recip=base.sak_recip and asg.dte_end> 20130401) and sak_recip > dbms_random.value * 6300000 and rownum < 2"
		 String memSql="select distinct b.id_medicaid,e.sak_cde_aid from t_re_base b,t_re_pmp_assign asg1, t_pmp_svc_loc loc, t_re_other_address o,t_re_aid_elig e where  substr(asg1.dte_end,1,6) > substr(to_char(sysdate,'YYYYMMDD'),1,6) and asg1.sak_pub_hlth=24 " +
		 		"and asg1.dte_end='22991231' and asg1.dte_end=e.dte_end and b.sak_recip=e.sak_recip and b.cde_agency=e.cde_agency and asg1.cde_status1 <> 'H' and b.sak_recip= asg1.sak_recip and loc.sak_pmp_ser_loc =asg1.sak_pmp_ser_loc and ind_active <> 'N' and dte_death='0' and asg1.dte_effective<to_char(sysdate, 'YYYYMMDD') " +
		 		"and not exists( select 1 from t_re_loc loc1 where loc1.sak_recip=b.sak_recip and loc1.DTE_discharge> to_char(sysdate,'YYYYMMDD')) and o.sak_recip = asg1.sak_recip and b.cde_agency='MHO' and e.dte_end='22991231' and b.sak_recip > dbms_random.value * 6300000 and rownum<2";
		 createPA("23214","Medical",memSql);
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
		 String memSql="select * from t_re_base where id_medicaid='100000000131'";//***************update sql*************************/////
		 createPA("23216","Medical",memSql);
		// Assert.assertTrue(driver.findElement(By.xpath("//*[@id='confirmationSubView:confirmation']/div[1]/table[2]/tbody/tr[1]/td[2]")).getText().trim().contains("Member is in LTC"),"Validation for warning message:Member is in LTC failed");
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
		 //Enter speech therapy PA through portal(PA)
		 public static void test22499() throws Exception{
		 TestNGCustom.TCNo="22499";
		 log("//TC 22499");
		 createPA("22499","Therapy",randMemSql);
		 String tNum=warningsSubmitPA();
		 SelSql="select * from R_DAY2 where tc = '22499'";
		 col="ID";
		 DelSql="delete from R_DAY2 where tc = '22499'";  
		 InsSql="insert into  R_DAY2 values ('22499', '"+tNum+"', 'Tracking num', '"+Common.convertSysdate()+"')";
		 Common.insertData(SelSql, col, DelSql, InsSql);	
		 }
		 
		 
		 @Test
		 //Enter therapy PA through portal
		 public static void test22500() throws Exception{
		 TestNGCustom.TCNo="22500";
		 log("//TC 22500");
         //Creating PA thru portal
		 //ICD-9 codes diag code: 51730, proc code-99214,Modifier--SA
	     String memberId=createPA("22500","Medical",memSql);
		 String tNum=warningsSubmitPA();
		 Common.portalLogout();
		 Thread.sleep(13000);
		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PriorAuth")).click();
		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Information")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaMiniSearchTrackingNumberInput")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaMiniSearchTrackingNumberInput")).sendKeys(tNum);
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaMiniSearch_SearchButton")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPaSearchResults_0:_id13")).click();
		 driver.findElement(By.linkText("Line Item")).click();
		 driver.findElement(By.linkText("Saved")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_AuthProc")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_AuthProc")).sendKeys("99214");
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_AuthorizedModifier")).sendKeys("SA");
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_UntSvcAthQuantity")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_UntSvcAthQuantity")).sendKeys("5");
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaAuthorizationEffectiveDate")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaAuthorizationEffectiveDate")).sendKeys(Common.convertSysdatecustom(-7));
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaAuthorizationEndDate")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaAuthorizationEndDate")).sendKeys(Common.convertSysdatecustom(-7));
		 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaLineitemStat"))).selectByVisibleText("IN REVIEW");
		 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaymentMethod"))).selectByVisibleText("Pay System Price");
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemPanel_updateAction_btn")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
		 PA.warnings();
		 Common.saveAll();
		 driver.findElement(By.linkText("Saved")).click();
		 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaLineItemPanel:PaLineItemDataPanel_PaLineitemStat"))).selectByVisibleText("APPROVED");
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PaNavigatorPanel:PaNavigator:SAVEALL")).click();
		 PA.warnings();
		 Common.saveAll();
		 Common.cancelAll();   
		 String paNum22500=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PaInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[3]/td[2]")).getText().trim();
		 log("PA num:"+paNum22500);
		 Common.portalLogin();
		 String pas=paNum22500;
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		 advSubmitClaims.orderingProv[0]="110000254";
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("22500","M", "110047258A", "3000.00", "", "0", "", pas, memberSql,"0","0");
		 String clmNo=advSubmitClaims.clmICN("prof");
		 String status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		 log("ICN:"+clmNo+", Status: "+status);
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim().equals("Paid"),"The claim submitted is not Paid");
		 Common.portalLogout();
		 }
		 
		 
		 @Test
		 //Modify existing PA and submit(PA)  DONE
		 public static void test22573() throws Exception{
		 TestNGCustom.TCNo="22573";
		 log("//TC 22573");
		 //Creating PA thru portal
		 createPA("22573","Therapy",randMemSql);
		 String tNum=warningsSubmitPA();
		 driver.findElement(By.linkText("Inquire/Maintain PA Request")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'trackingNumber')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'trackingNumber')]")).sendKeys(tNum);
		 driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click();
		 driver.findElement(By.xpath("//span[contains(@id, 'trackingNum')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'modify_Button')]")).click();
		 driver.findElement(By.xpath("//textarea[contains(@id, 'clinicalRationale')]")).clear();
		 driver.findElement(By.xpath("//textarea[contains(@id, 'clinicalRationale')]")).sendKeys("Testing");
		 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_lineItems')]")).click();
		 driver.findElement(By.xpath("//span[contains(@id, ':0:lineItem')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'reqUnits')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'reqUnits')]")).sendKeys("7");
		 driver.findElement(By.xpath("//input[contains(@id, 'update_Button')]")).click();
		 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_confirmation')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted after modification is not successfull");
		 }
		 
		 
		 public static void checkWar(String warMessg){
	     int rowNum=driver.findElements(By.xpath("//*[@id='confirmationSubView:confirmation']/div[1]/table[2]/tbody/tr")).size();
	     System.out.println("rownum: "+rowNum);
	     for(i=1;i<=rowNum;i++){
	    	 if(driver.findElement(By.xpath("//*[@id='confirmationSubView:confirmation']/div[1]/table[2]/tbody/tr["+i+"]/td[2]")).getText().trim().contains(warMessg)){
	    		 break;
	    	 }
	     }
		 }
		 
		 
		 @Test
		 //Web validate override warning-Member not eligible(PA)  *MEMBER ID HARDCODED*
		 public static void test23189() throws Exception{
		 TestNGCustom.TCNo="23189";
		 log("//TC 23189");
		 createPA("23189","Therapy",randMemSql);//7806,s1040
		 checkWar("Warning! Member");
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='confirmationSubView:confirmation']/div[1]/table[2]/tbody/tr["+i+"]/td[2]")).getText().trim().contains("Warning! Member"),"Validation for warning message:member not eligible failed");
		 log("Validation for warning message:member not eligible is done");
		 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
		 String tNum=driver.findElement(By.xpath("//span[contains(@id, 'trackingNumberText')]")).getText().trim();
		 System.out.println("Tracking# from TCNo23189: "+tNum);
		 log("Track num:"+tNum);
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted is not saved successfully");
		 driver.findElement(By.xpath("//input[contains(@id, 'requestAnother_Button')]")).click();
		 }
				 
		 @Test
		 //Save partially completed PA (PA)
		 public static void test23212() throws Exception{
		 TestNGCustom.TCNo="23212";
		 log("//TC 23212");
		 driver.findElement(By.linkText("Manage Service Authorizations")).click();
		 driver.findElement(By.linkText("Prior Authorization")).click();
		 driver.findElement(By.linkText("Enter PA Request")).click();
		 new Select(driver.findElement(By.xpath("//select[contains(@id,'paMedicalAssignment')]"))).selectByVisibleText("PHYSICIAN ADULT");
		 driver.findElement(By.xpath("//input[contains(@id,'selectAssignmentType')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id,'memberId')]")).clear();
		 PA.randomMemberid();
		 driver.findElement(By.xpath("//input[contains(@id,'memberId')]")).sendKeys(colValues.get(0));
		 new Select(driver.findElement(By.xpath("//select[contains(@id,'requestingProvider')]"))).selectByValue("110022129D");
		 driver.findElement(By.xpath("//input[contains(@id, 'contactName')]")).sendKeys("PAsmoke");
		 driver.findElement(By.xpath("//input[contains(@id, 'contactPhone')]")).sendKeys("6176176177");
		 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_lineItems')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'new_Button')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'procCode')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'procCode')]")).sendKeys("0001F"); 
		 driver.findElement(By.xpath("//input[contains(@id, 'reqEffectiveDate')]")).sendKeys(Common.convertSysdate());
		 driver.findElement(By.xpath("//input[contains(@id, 'reqEndDate')]")).sendKeys(Common.convertSysdate());
		 driver.findElement(By.xpath("//input[contains(@id, 'reqUnits')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'reqUnits')]")).sendKeys("1");
		 driver.findElement(By.xpath("//input[contains(@id, 'requestingProviderIndicator')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id,'provider1clear')]")).click();
		 Common.multiElements("//input[contains(@id,'serviceProvider')]").clear();
		 Common.multiElements("//input[contains(@id,'serviceProvider')]").sendKeys("");
		 driver.findElement(By.xpath("//input[contains(@id, 'add_Button')]")).click();
		 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_confirmation')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'save_Button')]")).click();
		 String trackNum="";
		 if(driver.findElements(By.xpath("//*[@id='lineItems']/div[1]/table[2]/tbody/tr/td[2]")).size()>0) {
			 String warMessage=(driver.findElement(By.xpath("//*[@id='lineItems']/div[1]/table[2]/tbody/tr/td[2]")).getText().trim());
			  trackNum = warMessage.substring(43,52);
		 }
		 System.out.println("Tracking# from tc#23212: "+trackNum);
		 log("Track num:"+trackNum);
		 }
		
		
		 @Test
		 //Validate override warning message-Member is not eligible(PA)
		 public static void test23217() throws Exception{
		 TestNGCustom.TCNo="23217";
		 log("//TC 23217");
		 createPA("23217","Therapy", randMemSql);//7806,99205
		 checkWar("Member not eligible");
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='confirmationSubView:confirmation']/div[1]/table[2]/tbody/tr["+i+"]/td[2]")).getText().trim().contains("Member not eligible"),"Validation for warning message:Member not eligible failed");
		 log("Validating the Member not eligible warning message is done");
		 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click(); 
		 driver.findElement(By.xpath("//input[contains(@id, 'requestAnother_Button')]")).click();
		 }
		 
		 
		 @Test
		 //Submitting a Medical PA through the Web Portal(PA)
		 public static void test23202() throws Exception{
		 TestNGCustom.TCNo="23202";
		 log("//TC 23202");
		 createPA("23202","Therapy", randMemSql);//7806,X9881
		 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click(); 
		 String tNum=driver.findElement(By.xpath("//span[contains(@id, 'trackingNumberText')]")).getText().trim();
		 System.out.println("Tracking# from TCNo23202: "+tNum);
		 log("Track num:"+tNum);
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted is not saved successfully");
		 driver.findElement(By.xpath("//input[contains(@id, 'requestAnother_Button')]")).click();
		 }
		 
		 @Test
		 //Adding service for ineligible Provider through web portal(PA)  *MEMBER ID HARDCODED*
		 public void test23204() throws Exception{
		 TestNGCustom.TCNo="23204";
		 log("//TC 23204");
		 PA.randomMemberid();
		 SelSql="select * from R_PA where tcno = '23204'";
		 col="TCNO";
		 DelSql="delete from R_PA where tcno = '23204'";  
		 InsSql="insert into  R_PA values ('23204','PA','"+memberId+"','110027770A',' ','A200','72195',' ',' ',' ','1',' ',' ')";//7806,S1040
		 Common.insertData(SelSql, col, DelSql, InsSql);
		 createPA("23204", "Medical", randMemSql);
		 checkWar("Provider is not allowed");
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='confirmationSubView:confirmation']/div[1]/table[2]/tbody/tr["+i+"]/td[2]")).getText().trim().contains("Provider is not allowed"),"Validation for warning message:provider is not allowed to request the service failed");
		 log("Validation for warning message:provider is not allowed to request the service is done");
		 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click(); 
		 String tNum=driver.findElement(By.xpath("//span[contains(@id, 'trackingNumberText')]")).getText().trim();
		 System.out.println("Tracking# from TCNo23204: "+tNum);
		 log("Track num:"+tNum);
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted is not saved successfully");
		 driver.findElement(By.xpath("//input[contains(@id, 'requestAnother_Button')]")).click();
		 }
		
		 
		 @Test
		 //Validate error message-Procedure code invalid(PA)
		 public static void test23205() throws Exception{
		 TestNGCustom.TCNo="23205";
		 log("//TC 23205");
		 createPA("23205","DME", memSql);//7806,A9
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='confirmationSubView:confirmation']/div[1]/table[2]/tbody/tr[1]/td[2]")).getText().trim().contains("Procedure Code is invalid"),"Validation for error message:Procedure Code is invalid failed");
		 log("Validating error message-Procedure code invalid is done");
		 }
		 
		 
		 @Test
		 //Validate error message-Duplicate PA request (PA)
		 public void test23207() throws Exception{
		 TestNGCustom.TCNo="23207";
		 log("//TC 23207");
		 createPA("23207","Medical",randMemSql);
		 SelSql="select * from R_PA where tcno = '23207'";
		 col="TCNO";
		 DelSql="delete from R_PA where tcno = '23207'";  
		 InsSql="insert into  R_PA values ('23207', 'PA', '"+memId+"','110022129D',' ','A200','54405',' ',' ',' ','1',' ',' ')";//7806,E1390
		 Common.insertData(SelSql, col, DelSql, InsSql);
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click(); 
		 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click(); 
		 driver.findElement(By.xpath("//input[contains(@id, 'requestAnother_Button')]")).click();
		 SelSql="select * from R_PA where tcno = '23207a'";
		 col="TCNO";
		 DelSql="delete from R_PA where tcno = '23207a'";  
		 InsSql="insert into  R_PA values ('23207a', 'PA', '"+memId+"','110022129D',' ','A200','54405',' ',' ',' ','1',' ',' ')";//7806,E1390
		 Common.insertData(SelSql, col, DelSql, InsSql);
		 String sql="select * from r_pa where tcno='23207a'";
		 createPA("23207a","Medical",sql);
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='confirmationSubView:confirmation']/div[1]/table[2]/tbody/tr[1]/td[2]")).getText().trim().contains("Duplicate on Tracking Number"),"Validation for error message:Duplicate on Tracking Number failed");
		 log("Validating for duplicate PA message in portal is done");
		  }
		 
		 
		 public static String checkErrMesg(String tcNo,String assgn, String sql) throws Exception{
		  
		    colNames.add("id_medicaid");
		 	colValues=Common.executeQuery(sql, colNames);
		    memId=colValues.get(0);
		   //Data we are taking from R_PA table except the Member Id
		 sqlStatement=("select * from r_pa where TCNO='"+tcNo+"'");
		 colNames.add("ASSGNMENT");//0
		 colNames.add("MEMBERID"); //1
		 colNames.add("REQPROVIDERID"); //2
		 colNames.add("DIAGCODE"); //3
		
		 colValues=Common.executeQuery1(sqlStatement,colNames);
		 driver.findElement(By.linkText("Home")).click();
		 driver.findElement(By.linkText("Manage Service Authorizations")).click();
		 driver.findElement(By.linkText("Prior Authorization")).click();
		 driver.findElement(By.linkText("Enter PA Request")).click();
		//new Select(driver.findElement(By.id("enterpa:j_id_id2pc3:pa"+assgn+"Assignment"))).selectByValue(colValues.get(0)); 
		 new Select(driver.findElement(By.xpath("//select[contains(@id,'pa"+assgn+"Assignment')]"))).selectByValue(colValues.get(0));
		 Common.getPageError("//form[@id='enterpa']/div[1]/table[2]/tbody/tr");//To catch the error in template page
		 
		 driver.findElement(By.xpath("//input[contains(@id,'selectAssignmentType')]")).click();
		 //driver.findElement(By.id("enterpa:j_id_id20pc3:selectAssignmentType")).click();
		 driver.findElement(By.xpath("//input[contains(@id,'memberId')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id,'memberId')]")).sendKeys(memId);
		 new Select(driver.findElement(By.xpath("//select[contains(@id,'requestingProvider')]"))).selectByValue(colValues.get(2));
		 driver.findElement(By.xpath("//input[contains(@id, 'contactName')]")).sendKeys("PAsmoke");
		 driver.findElement(By.xpath("//input[contains(@id, 'contactPhone')]")).sendKeys("6176176177");
		// driver.findElement(By.xpath("//input[contains(@id, 'contactFax')]")).sendKeys("6176176177");
		 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_lineItems')]")).click();
		//Common.getPageError("//form[@id='baseInfoSubView:baseInfo']/div[1]/table[2]/tbody/tr");//To catch the error in Base info page
		 String errMesg=driver.findElement(By.xpath("//form[@id='baseInfoSubView:baseInfo']/div[1]/table[2]/tbody/tr")).getText();
		 return errMesg;
		 
		 
		 }
		 
		 
		 @Test
		 //POSC Assignments Changes-HOME HEALTH
		 public void test39262() throws Exception{
		 TestNGCustom.TCNo="39262";
		 log("//TC 39262");
		 String errMesg=checkErrMesg("39262","Medical",randMemSql);
		 Assert.assertTrue(errMesg.contains("Assignment HOME HEALTH cannot be used with the provider type HOME HEALTH AGENCY (60). Please use another provider or assignment."), "The expected error message, 'Assignment HOME HEALTH cannot be used with the provider type HOME HEALTH AGENCY (60)' is not generated");
		 log("Validating error mesg, 'Assignment HOME HEALTH cannot be used with the provider type HOME HEALTH AGENCY (60)' is done.");
		 }
		 
		 
		 @Test
		 //POSC Assignments Changes-Physical Therapy
		 public void test39263() throws Exception{
		 TestNGCustom.TCNo="39263";
		 log("//TC 39263");
		 String errMesg=checkErrMesg("39263","Therapy",randMemSql);
		 Assert.assertTrue(errMesg.contains("Assignment PHYSICAL THERAPY cannot be used with the provider type HOME HEALTH AGENCY (60). Please use another provider or assignment."), "The expected error message, 'Assignment HOME HEALTH cannot be used with the provider type HOME HEALTH AGENCY (60)' is not generated");
		 log("Validating error mesg, 'Assignment PHYSICAL THERAPY cannot be used with the provider type HOME HEALTH AGENCY (60)' is done.");
		 }
		 
		 
		 @Test
		 //POSC Status Changes- Void
		 public void test39264() throws Exception{
		 TestNGCustom.TCNo="39264";
		 log("//TC 39264");
		 createPA("39264", "Medical", randMemSql);
		 
		 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click(); 
		 String tNum=driver.findElement(By.xpath("//span[contains(@id, 'trackingNumberText')]")).getText().trim();
		 System.out.println("Tracking# from TCNo39264: "+tNum);
		 log("Track num:"+tNum);
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted is not saved successfully");
		 driver.findElement(By.xpath("//input[contains(@id, 'requestAnother_Button')]")).click();
		 driver.findElement(By.linkText("Inquire/Maintain PA Request")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'trackingNumber')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'trackingNumber')]")).sendKeys(tNum);
		 driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click(); 
		 driver.findElement(By.xpath("//a[contains(@id, 'priorAuthSearchResults:0:trackingNum')]")).click(); 
		 driver.findElement(By.xpath("//input[contains(@id, 'void_Button')]")).click(); 
		 driver.findElement(By.xpath("//input[contains(@id, 'confirm_Button')]")).click();
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'voidResponseSubView:voidResponse')]")).getText().trim().contains("has been voided"),"The PA is not successfully voided");
		 log("The PA is voided successfully");
		 driver.findElement(By.xpath("//input[contains(@id, 'close_Button')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'trackingNumber')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'trackingNumber')]")).sendKeys(tNum);
		 driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click(); 
		 driver.findElement(By.xpath("//a[contains(@id, 'priorAuthSearchResults:0:trackingNum')]")).click(); 
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'statusText')]")).getText().trim().contains("Cancelled by Provider"),"The voided PA status was not updated to 'Cancelled by Provider'");
		 log("The voided PA status was updated to 'Cancelled by Provider'");
		 }
		 
		 
		 @Test
		 //POSC Assignments Changes- validate error mesg, WR assignment with provider type 06
		 public void test39265a() throws Exception{
		 TestNGCustom.TCNo="39265a";
		 log("//TC 39265a");
		 String errMesg=checkErrMesg("39265a","DME",randMemSql);
		 Assert.assertTrue(errMesg.contains("Assignment WHEELCHAIRS AND REPAIRS cannot be used with the provider type PODIATRIST (06). Please use another provider or assignment."), "The expected error message, 'Assignment WHEELCHAIRS AND REPAIRS cannot be used with the provider type PODIATRIST (06)' is not generated");
		 log("Validating error mesg, 'Assignment WHEELCHAIRS AND REPAIRS cannot be used with the provider type PODIATRIST (06)' is done.");
		 }
		 
		 @Test
		 //POSC Assignments Changes- validate error mesg, AP assignment with provider type 40
		 public void test39265b() throws Exception{
		 TestNGCustom.TCNo="39265b";
		 log("//TC 39265b");
		 String errMesg=checkErrMesg("39265b","DME",randMemSql);
		 Assert.assertTrue(errMesg.contains("Assignment ABSORBENT PRODUCTS cannot be used with the provider type PHARMACY (40). Please use another provider or assignment."), "The expected error message, 'Assignment ABSORBENT PRODUCTS cannot be used with the provider type PHARMACY (40)' is not generated");
		 log("Validating error mesg, 'Assignment ABSORBENT PRODUCTS cannot be used with the provider type PHARMACY (40)' is done.");
		 }
		 
		 @Test
		 //POSC Assignments Changes- validate error mesg, DM assignment with provider type 41
		 public void test39265c() throws Exception{
		 TestNGCustom.TCNo="39265c";
		 log("//TC 39265c");
		 String errMesg=checkErrMesg("39265c","DME",randMemSql);
		 Assert.assertTrue(errMesg.contains("Assignment DME-OTHER cannot be used with the provider type DURABLE MEDICAL EQUIPMENT (41). Please use another provider or assignment."), "The expected error message, 'Assignment DME-OTHER cannot be used with the provider type DURABLE MEDICAL EQUIPMENT (41)' is not generated");
		 log("Validating error mesg, 'Assignment DME-OTHER cannot be used with the provider type DURABLE MEDICAL EQUIPMENT (41)' is done.");
		 }
		 
		 @Test
		 //POSC Assignments Changes- validate error mesg, OP assignment with provider type 41
		 public void test39265d() throws Exception{
		 TestNGCustom.TCNo="39265d";
		 log("//TC 39265d");
		 String errMesg=checkErrMesg("39265d","DME",randMemSql);
		 Assert.assertTrue(errMesg.contains("Assignment ORTHOTICS AND PROSTHETICS cannot be used with the provider type DURABLE MEDICAL EQUIPMENT (41). Please use another provider or assignment."), "The expected error message, 'Assignment ORTHOTICS AND PROSTHETICS cannot be used with the provider type DURABLE MEDICAL EQUIPMENT (41)' is not generated");
		 log("Validating error mesg, 'Assignment ORTHOTICS AND PROSTHETICS cannot be used with the provider type DURABLE MEDICAL EQUIPMENT (41)' is done.");
		 }
		 
		 @Test
		 //POSC Assignments Changes- validate error mesg, OX assignment with provider type 41
		 public void test39265e() throws Exception{
		 TestNGCustom.TCNo="39265e";
		 log("//TC 39265e");
		 String errMesg=checkErrMesg("39265e","DME",randMemSql);
		 Assert.assertTrue(errMesg.contains("Assignment OXYGEN cannot be used with the provider type DURABLE MEDICAL EQUIPMENT (41). Please use another provider or assignment."), "The expected error message, 'Assignment OXYGEN cannot be used with the provider type DURABLE MEDICAL EQUIPMENT (41)' is not generated");
		 log("Validating error mesg, 'Assignment OXYGEN cannot be used with the provider type DURABLE MEDICAL EQUIPMENT (41)' is done.");
		 }
		 
		 
		 @Test
		 //POSC Assignments Changes- validate error mesg, OX assignment with provider type 42
		 public void test39265f() throws Exception{
		 TestNGCustom.TCNo="39265f";
		 log("//TC 39265f");
		 String errMesg=checkErrMesg("39265f","DME",randMemSql);
		 Assert.assertTrue(errMesg.contains("Assignment OXYGEN cannot be used with the provider type OXYGEN AND RESPIRATORY THERAPY EQUIP (42). Please use another provider or assignment."), "The expected error message, 'Assignment OXYGEN cannot be used with the provider type OXYGEN AND RESPIRATORY THERAPY EQUIP (42)' is not generated");
		 log("Validating error mesg, 'Assignment OXYGEN cannot be used with the provider type OXYGEN AND RESPIRATORY THERAPY EQUIP (42)' is done.");
		 }
		 
		 @Test
		 //POSC Assignments Changes- validate error mesg, OP assignment with provider type 43
		 public void test39265g() throws Exception{
		 TestNGCustom.TCNo="39265g";
		 log("//TC 39265g");
		 String errMesg=checkErrMesg("39265g","DME",randMemSql);
		 Assert.assertTrue(errMesg.contains("Assignment ORTHOTICS AND PROSTHETICS cannot be used with the provider type PROSTHETICS (43). Please use another provider or assignment."), "The expected error message, 'Assignment ORTHOTICS AND PROSTHETICS cannot be used with the provider type PROSTHETICS (43)' is not generated");
		 log("Validating error mesg, 'Assignment ORTHOTICS AND PROSTHETICS cannot be used with the provider type PROSTHETICS (43)' is done.");
		 }
		 
		 @Test
		 //POSC Assignments Changes- validate error mesg, SD assignment with provider type 47
		 public void test39265h() throws Exception{
		 TestNGCustom.TCNo="39265h";
		 log("//TC 39265h");
		 String errMesg=checkErrMesg("39265h","DME",randMemSql);
		 Assert.assertTrue(errMesg.contains("Assignment STANDERS cannot be used with the provider type ORTHOTICS (47). Please use another provider or assignment."), "The expected error message, 'Assignment STANDERS cannot be used with the provider type ORTHOTICS (47)' is not generated");
		 log("Validating error mesg, 'Assignment STANDERS cannot be used with the provider type ORTHOTICS (47)' is done.");
		 }
		 
		 @Test
		 //POSC Assignments Changes- validate error mesg, AP assignment with provider type 79
		 public void test39265i() throws Exception{
		 TestNGCustom.TCNo="39265i";
		 log("//TC 39265i");
		 String errMesg=checkErrMesg("39265i","DME",randMemSql);
		 Assert.assertTrue(errMesg.contains("Assignment ABSORBENT PRODUCTS cannot be used with the provider type DMEPOS (79). Please use another provider or assignment."), "The expected error message, 'Assignment ABSORBENT PRODUCTS cannot be used with the provider type DMEPOS (79)' is not generated");
		 log("Validating error mesg, 'Assignment ABSORBENT PRODUCTS cannot be used with the provider type DMEPOS (79)' is done.");
		 }
		 
		 
		 @Test
		 //Create/Update PA contact info
		 public void test39266() throws Exception{
		 TestNGCustom.TCNo="39266";
		 log("//TC 39266");
		 createPA("39266","Medical",randMemSql);
		 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click(); 
		 String tNum=driver.findElement(By.xpath("//span[contains(@id, 'trackingNumberText')]")).getText().trim();
		 System.out.println("Tracking# from TCNo39266: "+tNum);
		 log("Track num:"+tNum);
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted is not saved successfully");
		 driver.findElement(By.xpath("//input[contains(@id, 'requestAnother_Button')]")).click();
		 driver.findElement(By.linkText("Inquire/Maintain PA Request")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'trackingNumber')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'trackingNumber')]")).sendKeys(tNum);
		 driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click(); 
		 driver.findElement(By.xpath("//a[contains(@id, 'priorAuthSearchResults:0:trackingNum')]")).click(); 
		 driver.findElement(By.xpath("//input[contains(@id, 'modify_Button')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'contactName')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'contactName')]")).sendKeys("PARegTest");
		 driver.findElement(By.xpath("//input[contains(@id, 'contactPhone')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'contactPhone')]")).sendKeys("7817817811");
		 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_confirmation')]")).click();
		 Common.getPageError("//form[@id='lineItems']/div[1]/table[2]/tbody/tr");//To catch the error in Line items page
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit_Button')]")).click();
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'submissionResponse')]")).getText().trim().contains("successfully submitted"),"The PA submitted is not saved successfully");
		 log("PA's contact info has been updated successfully");
		 }
		 
		 
		 
		 @Test
		 //POSC Changes- Procedure Codes, validate error message
		 public void test39268() throws Exception{
		 TestNGCustom.TCNo="39268";
		 log("//TC 39268");
		    colNames.add("id_medicaid");
		 	colValues=Common.executeQuery(randMemSql, colNames );
		     //}
		   memId=colValues.get(0);
		 sqlStatement=("select * from r_pa where TCNO='39268'");
		 colNames.add("ASSGNMENT");//0
		 colNames.add("MEMBERID"); //1
		 colNames.add("REQPROVIDERID"); //2
		 colNames.add("DIAGCODE"); //3
		 colNames.add("PROCCODE"); //4
		 colNames.add("UNITS"); //5
		 colValues=Common.executeQuery1(sqlStatement,colNames);
		 driver.findElement(By.linkText("Home")).click();
		 driver.findElement(By.linkText("Manage Service Authorizations")).click();
		 driver.findElement(By.linkText("Prior Authorization")).click();
		 driver.findElement(By.linkText("Enter PA Request")).click();
		 new Select(driver.findElement(By.id("enterpa:j_id_id2pc3:paMedicalAssignment"))).selectByValue(colValues.get(0)); 
		 Common.getPageError("//form[@id='enterpa']/div[1]/table[2]/tbody/tr");//To catch the error in template page
		 
		 driver.findElement(By.xpath("//input[contains(@id,'selectAssignmentType')]")).click();
		 
		 driver.findElement(By.xpath("//input[contains(@id,'memberId')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id,'memberId')]")).sendKeys(memId);
		 new Select(driver.findElement(By.xpath("//select[contains(@id,'requestingProvider')]"))).selectByValue(colValues.get(2));
		 driver.findElement(By.xpath("//input[contains(@id, 'contactName')]")).sendKeys("PAsmoke");
		 driver.findElement(By.xpath("//input[contains(@id, 'contactPhone')]")).sendKeys("6176176177");
		 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_lineItems')]")).click();
		 Common.getPageError("//form[@id='baseInfoSubView:baseInfo']/div[1]/table[2]/tbody/tr");//To catch the error in Base info page
		 driver.findElement(By.xpath("//input[contains(@id, 'new_Button')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'procCode')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'procCode')]")).sendKeys(colValues.get(4)); //"S9128"
		 driver.findElement(By.xpath("//input[contains(@id, 'reqEffectiveDate')]")).sendKeys(Common.convertSysdate());
		 driver.findElement(By.xpath("//input[contains(@id, 'reqEndDate')]")).sendKeys(Common.convertSysdate());
		 driver.findElement(By.xpath("//input[contains(@id, 'reqUnits')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'reqUnits')]")).sendKeys(colValues.get(5));
		 driver.findElement(By.xpath("//input[contains(@id, 'requestingProviderIndicator')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id,'provider1clear')]")).click();
		 Common.multiElements("//input[contains(@id,'serviceProvider')]").clear();
		 Common.multiElements("//input[contains(@id,'serviceProvider')]").sendKeys("");
		 driver.findElement(By.xpath("//input[contains(@id, 'add_Button')]")).click();
		 String errMesg=driver.findElement(By.xpath("//form[@id='lineItems']/div[1]/table[2]/tbody/tr")).getText();
		 Assert.assertTrue(errMesg.contains("Procedure code "+colValues.get(4)+" does not require a Prior Authorization, please remove."), "The expected error message, 'Procedure code "+colValues.get(4)+" does not require a Prior Authorization, please remove.' is not generated");
		 log("Validating error mesg, 'Procedure code "+colValues.get(4)+" does not require a Prior Authorization, please remove.' is done.");
		 }
		 
		 
		 
		 
		 

}
