package newMMIS_Subsystems;

import java.sql.SQLException;
import java.sql.Statement;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Referrals_Portal extends Login{
	public static String clmNo;
	public static String status;
	public static String memberId;
	public static String memberId22480;
	public static String randMemId;
	public static String wZMember;
	public static Statement statement = null;
	
	@BeforeMethod
	public void LoginCheck() throws Exception {
		Common.resetPortal();
		testCheckDBLoginSuccessful();
	}
	
	public static String randomMemberid() throws SQLException{
		sqlStatement="Select distinct b.id_medicaid, c.id_provider, d.cde_service_loc from t_re_pmp_assign a, t_re_base b, t_pr_prov c, t_pmp_svc_loc d where a.sak_pub_hlth = 23 "+ 
                "and a.sak_recip = b.sak_recip and d.sak_prov = c.sak_prov and a.dte_end = 22991231 and a.cde_status1 <> 'H' and b.ind_active = 'Y' " +
                "and b.id_medicaid<>'100016248617' and b.id_medicaid<>'100016006445' and b.id_medicaid<>'100016002493' and b.sak_recip > dbms_random.value * 6300000 and rownum < 2";
		colNames.add("id_medicaid");
	 	colValues=Common.executeQuery(sqlStatement, colNames );
	 	randMemId=colValues.get(0);
	 	System.out.println("Random Member Id is: "+randMemId);
	 	
	 	return randMemId;
	    }
	
	
	public static String getMemberId() throws SQLException {
		sqlStatement="Select distinct b.id_medicaid from t_re_pmp_assign a, t_re_base b, t_pmp_svc_loc d where a.sak_pub_hlth = 23 and d.cde_service_loc='F' and a.sak_recip = b.sak_recip " +
			      "and d.sak_prov =82159 and a.sak_pmp_ser_loc=d.sak_pmp_ser_loc and a.dte_end >to_char(sysdate, 'YYYYMMDD') and a.cde_status1 <> 'H' " +
			      "and ind_active = 'Y' and b.sak_recip > dbms_random.value * 6300000 and rownum < 2"; 
	 colNames.add("id_medicaid");
	 colValues=Common.executeQuery(sqlStatement, colNames );
	 return wZMember=colValues.get(0);
	 
	}
	
	
		 //fetch pa from db which has prov 
		 @Test
		 //PCC referral search for a member(Referrals)
		 public static void test22479() throws Exception{
		 TestNGCustom.TCNo="22479";
		 
		 wZMember=randomMemberid();
		// wZMember= Referrals.wZMember("22479","110082151H","110082151H","2");
		 wZMember= Referrals.wZMember("22479",wZMember,"110082159F","110082159F","2");
		 //Checking member eligibility
		 driver.findElement(By.linkText("Manage Members")).click();
		 driver.findElement(By.linkText("Eligibility")).click();
		 driver.findElement(By.linkText("Verify Member Eligibility")).click();
		 new Select(driver.findElement(By.id("form1:eligibilitySearch:eligInquireSearchProviderID"))).selectByValue("110082159F");
		 driver.findElement(By.id("form1:eligibilitySearch:member_memberID")).clear();
		 //driver.findElement(By.id("form1:eligibilitySearch:member_memberID")).sendKeys(wZMember);
		 driver.findElement(By.xpath("//input[@id='form1:eligibilitySearch:member_memberID']")).sendKeys(wZMember);
		 driver.findElement(By.id("form1:j_id_id51pc3:j_id_id52pc3")).click();
		
		// Assert.assertTrue(driver.findElement(By.id("MemberTab:j_id_id5pc3:member_provider_idText")).getText().trim().equals("110003219A"),"Correct Member Eligibility is not displayed in Referrals");
		 // driver.findElement(By.id("MemberTab:verifyMemberEligibility_1_id13:verifyMemberEligibility_1_id14")).click();
		 driver.findElement(By.id("MemberTab:j_id_id55pc3:j_id_id56pc3")).click();
		 //PCC referral search by referring to provider
		 driver.findElement(By.linkText("Manage Service Authorizations")).click();
		 driver.findElement(By.linkText("Referrals")).click();
		 driver.findElement(By.linkText("Inquire Referral")).click();
		 driver.findElement(By.id("referralSearchForm:j_id_id2pc3:memberID2")).clear();
		 driver.findElement(By.id("referralSearchForm:j_id_id2pc3:memberID2")).sendKeys(wZMember);
		 new Select(driver.findElement(By.id("referralSearchForm:j_id_id2pc3:referringProvider"))).selectByValue("110082159F");
		 driver.findElement(By.id("referralSearchForm:j_id_id2pc3:search_Button")).click();
		 int rownum=driver.findElements(By.xpath("//*[@id='referralSearchForm:j_id_id53pc3:referralSearchResults:tbody_element']/tr")).size();
		 for(int i=1;i<=rownum;i++)
		 {
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='referralSearchForm:j_id_id53pc3:referralSearchResults:tbody_element']/tr["+i+"]/td[2]")).getText().trim().contains("SCHEY JOHN"),"PCC referral search for a member by the referring-to provider is not successfull in Referrals");
		 }
		 driver.findElement(By.id("referralSearchForm:j_id_id53pc3:referralSearchResults:0:referralNum")).click();
		 Assert.assertTrue(driver.findElement(By.id("referralViewInformationForm:referralViewInformationDataPanel:member_memberIDText")).getText().trim().equals(wZMember),"The Referral in the search results is not for the referring member id in Referrals");
		// Assert.assertTrue(driver.findElement(By.id("referralViewInformationForm:referralViewInformationDataPanel:referringProvider_npiText")).getText().trim().equals("1871575480"),"The Referral in the search results is not for the referring Provider NPI in Referrals");//1982668950
		 driver.findElement(By.id("referralViewInformationForm:j_id_id87pc3:j_id_id89pc3")).click(); //close
		 //Negative test case
		 driver.findElement(By.id("referralSearchForm:j_id_id2pc3:memberID2")).clear();
		 driver.findElement(By.id("referralSearchForm:j_id_id2pc3:memberID2")).sendKeys(wZMember);
		 new Select(driver.findElement(By.id("referralSearchForm:j_id_id2pc3:referringProvider"))).selectByValue("110048577A");
		 driver.findElement(By.id("referralSearchForm:j_id_id2pc3:search_Button")).click();
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='referralSearchForm']/table[2]/tbody/tr[1]/td/table/tbody/tr/td")).getText().trim().contains("No Records Found."),"PCC referral search for a member by another provider(Negative test case) is not successfull in Referrals");
	   	
		 }
		 
		 @Test
		 //PCC referral search for a member(Referrals)
		 public static void test22487() throws Exception{
		 TestNGCustom.TCNo="22487";
		 randomMemberid();
		 //Checking member eligibility
		 driver.findElement(By.linkText("Manage Members")).click();
		 driver.findElement(By.linkText("Eligibility")).click();
		 driver.findElement(By.linkText("Verify Member Eligibility")).click();
		 new Select(driver.findElement(By.id("form1:eligibilitySearch:eligInquireSearchProviderID"))).selectByValue("110082155A");
		 driver.findElement(By.id("form1:eligibilitySearch:member_memberID")).clear();
		 driver.findElement(By.id("form1:eligibilitySearch:member_memberID")).sendKeys(randMemId);
		 driver.findElement(By.id("form1:j_id_id51pc3:j_id_id52pc3")).click();
		 driver.findElement(By.id("MemberTab:_MENUITEM_EligTab")).click();
		 driver.findElement(By.id("EligTab:j_id_id5pc3:coverageList:0:dateRange")).click();
		// Assert.assertTrue(driver.findElement(By.xpath("//*[@id='EligTab:verifyMemberEligibility_1_id3:verifyMemberEligibility_1_id5:verifyMemberEligibility_1_id6']/b[3]")).getText().trim().equals(Common.convertSysdate()),"Member is not eligible for a service today");
		 Assert.assertTrue(driver.findElement(By.id("EligTab:j_id_id5pc3:j_id_id10pc3:j_id_id72pc3:MC_PCC:0:dateRange")).getText().trim().contains(Common.convertSysdate()),"Member is not eligible for a service today");
		 driver.findElement(By.id("EligTab:j_id_id291pc3:j_id_id292pc3")).click();//close
		 //PCC referral search by referring to provider
		 String sql="select pa.prior_auth_num,id_medicaid  from t_Pa_Pauth pa,t_pr_prov pr,t_re_base b,T_PA_LINE_ITEM  ext where id_provider='110003219' and b.sak_recip=pa.sak_recip and pa.sak_pa_prov=pr.sak_prov " +
		 		    "and ext.sak_pa=pa.sak_pa and ext.sak_pa_status=19 and pa.cde_auth_type='R' and pa.cde_service_loc='A' and ext.dte_pa_req_end>20160101 and rownum < 2";
		// colNames.add("PRIOR_AUTH_NUM");//0
		 colNames.add("ID_MEDICAID");//0
		 colValues=Common.executeQuery(sql,colNames);
		 System.out.println("Member id for referral search: "+colValues.get(0));
		 driver.findElement(By.linkText("Manage Service Authorizations")).click();
		 driver.findElement(By.linkText("Referrals")).click();
		 driver.findElement(By.linkText("Inquire Referral")).click();
		 driver.findElement(By.id("referralSearchForm:j_id_id2pc3:memberID2")).clear();
		 driver.findElement(By.id("referralSearchForm:j_id_id2pc3:memberID2")).sendKeys(colValues.get(0));
		 new Select(driver.findElement(By.id("referralSearchForm:j_id_id2pc3:referringProvider"))).selectByValue("110003219A");
		 driver.findElement(By.id("referralSearchForm:j_id_id2pc3:search_Button")).click();
         Assert.assertTrue(driver.findElement(By.xpath("//*[@id='referralSearchForm:j_id_id53pc3:referralSearchResults:tbody_element']/tr[1]/td[2]")).getText().trim().contains("SCHEY JOHN"),"PCC referral search for a member by the provider is not successfull in Referrals");
         driver.findElement(By.id("referralSearchForm:j_id_id53pc3:referralSearchResults:0:referralNum")).click();
		// Assert.assertTrue(driver.findElement(By.id("referralViewInformationForm:referralViewInformationDataPanel:referringProvider_npiText")).getText().trim().equals("1346282621"),"The Referral in the search results is not for the service Provider NPI in Referrals");
		 }
		 
		 
		 
		 
		 
		 @Test
		 //PCC referral search for a member(Referrals)  *MEMBER ID HARDCODED*
		 public static void test23413b() throws Exception{
		 TestNGCustom.TCNo="23413b";
		 log("//TC 23413b");
		 
		 Referrals.wZMember("23413b", getMemberId(),"110082159F"," ","3");
		 String sql="select * from r_referrals where tcno='23413b'";
		 colNames.add("MEMBERID");//0
		 colValues=Common.executeQuery1(sql,colNames);
		 driver.findElement(By.linkText("Manage Service Authorizations")).click();
		 driver.findElement(By.linkText("Referrals")).click();
		 driver.findElement(By.linkText("Enter New Referral")).click();
		 driver.findElement(By.id("referralAddForm:referralAddPanel:member_memberID")).clear();
		 driver.findElement(By.id("referralAddForm:referralAddPanel:member_memberID")).sendKeys(colValues.get(0));//WZ member
		 new Select(driver.findElement(By.id("referralAddForm:referralAddPanel:referringProvider"))).selectByValue("110082159F");
		 driver.findElement(By.id("referralAddForm:referralAddPanel:referralProvSearchlookup")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'lastName')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'lastName')]")).sendKeys("SCHEY");
		 driver.findElement(By.xpath("//input[contains(@id, 'firstName')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'firstName')]")).sendKeys("JOHN");
		 driver.findElement(By.id("submitProviderSearch:j_id_id2pc3:search_Button")).click();
		 driver.findElement(By.id("submitProviderSearch:j_id_id31pc3:ProviderList:0:displayName")).click();
		 new Select(driver.findElement(By.id("referralAddForm:referralAddPanel:assignment"))).selectByVisibleText("CONSULT, TEST AND TREAT");
		 driver.findElement(By.id("referralAddForm:referralAddPanel:referralLineItem_endDate")).clear();
		 driver.findElement(By.id("referralAddForm:referralAddPanel:referralLineItem_endDate")).sendKeys(Common.convertSysdatecustom(366));
		 driver.findElement(By.id("referralAddForm:referralAddPanel:referralLineItem_numVisits")).clear();
		 driver.findElement(By.id("referralAddForm:referralAddPanel:referralLineItem_numVisits")).sendKeys("3");
		 driver.findElement(By.id("referralAddForm:j_id_id79pc3:j_id_id81pc3")).click();
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='referralAddForm']/div[1]/table[2]/tbody/tr[1]/td[2]")).getText().trim().contains("more than 12 months"),"Checking for referral that spans more than 12 months is not successfull in Portal");
		 }
		 
		 
		 
		 @Test
		 //Searching for referral info of a member with referral number(Referral)
		 public static void test23414() throws Exception{
		 TestNGCustom.TCNo="23414";
		 String sql="select pa.prior_auth_num,id_medicaid  from t_Pa_Pauth pa,t_pr_prov pr,t_re_base b,T_PA_LINE_ITEM  ext where id_provider='110003219' and b.sak_recip=pa.sak_recip and pa.sak_pa_prov=pr.sak_prov " +
		 		    "and ext.sak_pa=pa.sak_pa and ext.sak_pa_status=19 and pa.cde_auth_type='R' and pa.cde_service_loc='A' and ext.dte_pa_req_end>20140101 and rownum < 2";
		 colNames.add("PRIOR_AUTH_NUM");//0
		 colNames.add("ID_MEDICAID");//1
		 colValues=Common.executeQuery(sql,colNames);
		 driver.findElement(By.linkText("Manage Service Authorizations")).click();
		 driver.findElement(By.linkText("Referrals")).click();
		 driver.findElement(By.linkText("Inquire Referral")).click();
		 driver.findElement(By.id("referralSearchForm:j_id_id2pc3:authorizationNumber")).sendKeys(colValues.get(0));
		 driver.findElement(By.id("referralSearchForm:j_id_id2pc3:search_Button")).click();
		 driver.findElement(By.id("referralSearchForm:j_id_id53pc3:referralSearchResults:0:referralNum")).click();
		 Assert.assertTrue(driver.findElement(By.id("referralViewInformationForm:referralViewInformationDataPanel:member_memberIDText")).getText().trim().equals(colValues.get(1)),"The Referral in the search results is not for the referring member id in Referrals");
		// driver.findElement(By.id("referralViewInformationForm:inquireReferral_1_id22:inquireReferral_1_id25")).click();
		 }
		 
		 
		@Test
		 //Referral required-deny no referral(Referral)
		 public static void test22480() throws Exception{
		 TestNGCustom.TCNo="22480";
		 log("//TC 22480");
		 randomMemberid();
		 Common.portalLogin();
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+randMemId+"'";
		 
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("22480", "M","110048577A", "100.00", "", "1134135759", "","",memberSql,"-7","-7");
		 clmNo = advSubmitClaims.clmICN("prof");
		// status=driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:enterSingleClaim_1_id5:claimStatusText")).getText().trim();
		 log("ICN: "+clmNo);
		 Referrals.checkEdit("3120");
         log("Got the edit:3120");
		 }
		 
		
		
		 
		 @Test
		 //Verify Referral exemptions-abortion services(Referral)
		 public static void test22489() throws Exception{
		 TestNGCustom.TCNo="22489";
		 log("//TC 22489");
		 sqlStatement="Select b.id_medicaid, c.id_provider, d.cde_service_loc from t_re_pmp_assign a, t_re_base b, t_pr_prov c, t_pmp_svc_loc d " +
		 		      "where a.sak_pub_hlth = 23 and b.cde_sex='F' and a.sak_recip = b.sak_recip and d.sak_prov = c.sak_prov and a.dte_end = 22991231 " +
		 		      "and a.cde_status1 <> 'H' and b.id_medicaid<>'100003891932' and id_medicaid<>'100016006445' and b.ind_active = 'Y' and b.sak_recip > dbms_random.value * 6300000 and rownum < 2";
	     colNames.add("id_medicaid");
	     colValues=Common.executeQuery(sqlStatement, colNames );
	     memberId=colValues.get(0);
	     System.out.println("memberid: "+colValues.get(0));
		 Common.portalLogin();
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("22489","M","110048577A","60.00", "", "1134135759", "", "",memberSql,"-8","-8");
		 clmNo = advSubmitClaims.clmICN("prof");
		// status= driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:icnText")).getText().trim();
		 System.out.println("ICN and status from TC#22489: "+clmNo+ ","+status);
		 Referrals.checkEdit("3120");
		 driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id46pc3:btnResubmit")).click();
		 driver.findElement(By.id("professionalBillingTab:_MENUITEM_Procedures")).click();
		 driver.findElement(By.id("proceduresTab:professionalServices:0:item")).click();
		 driver.findElement(By.id("proceduresTab:j_id_id16pc3:hcpcsProcedureCode")).clear();
		 driver.findElement(By.id("proceduresTab:j_id_id16pc3:hcpcsProcedureCode")).sendKeys("76801");
		 driver.findElement(By.id("proceduresTab:j_id_id16pc3:j_id_id27pc3:modifier1")).sendKeys("TC");
		 driver.findElement(By.id("proceduresTab:j_id_id16pc3:fromDate")).clear();
		 driver.findElement(By.id("proceduresTab:j_id_id16pc3:fromDate")).sendKeys(Common.convertSysdatecustom(-8));
		 driver.findElement(By.id("proceduresTab:j_id_id16pc3:toDate")).clear();
		 driver.findElement(By.id("proceduresTab:j_id_id16pc3:toDate")).sendKeys(Common.convertSysdatecustom(-8));
		 driver.findElement(By.id("proceduresTab:j_id_id16pc3:update_Button")).click();
		 driver.findElement(By.id("proceduresTab:_MENUITEM_Confirmation")).click();
		 Common.getPageError("//form[contains(@id,'Tab')]/div/table[2]/tbody/tr");
		 driver.findElement(By.id("profConfirmationSubView:confirmationTab:j_id_id32pc3:btnSubmit")).click();
		 String ICN=driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:icnText")).getText().trim();
		 System.out.println("ICN from TC#22489: "+ICN);
		 log("ICN: "+ICN);
		 Assert.assertTrue(driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:claimStatusText")).getText().trim().equals("Paid"),"The resubmitted claim is not paid for exemption,Abortion services");
		 }
		 
		 
		 
		 @Test
		 //Referrals exemptions-Emergency Service(Referral)
		 public static void test22490() throws Exception{
		 TestNGCustom.TCNo="22490";
		 log("//TC 22490");
		 randomMemberid();
		 memberId=colValues.get(0);
	     System.out.println("Memberid: "+colValues.get(0));
		 Common.portalLogin();
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("22490","M","110048577A","60.00", "", "1134135759", "", "",memberSql,"-9","-9");
		 clmNo = advSubmitClaims.clmICN("prof");
		 status= driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:claimStatusText")).getText().trim();
		 System.out.println("ICN and status from TC#22490: "+clmNo+ ","+status);
		 Referrals.checkEdit("3120");
		 driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id46pc3:btnResubmit")).click();
		 new Select(driver.findElement(By.id("professionalBillingTab:j_id_id6pc3:placeOfService"))).selectByValue("23");//Emergency
		 driver.findElement(By.id("professionalBillingTab:_MENUITEM_Procedures")).click();
		 driver.findElement(By.id("proceduresTab:professionalServices:0:item")).click();
		 new Select(driver.findElement(By.id("proceduresTab:j_id_id16pc3:placeOfService"))).selectByValue("23");
		 driver.findElement(By.id("proceduresTab:j_id_id16pc3:update_Button")).click();
		 driver.findElement(By.id("proceduresTab:_MENUITEM_Confirmation")).click();
		 Common.getPageError("//form[contains(@id,'Tab')]/div/table[2]/tbody/tr");
		 driver.findElement(By.id("profConfirmationSubView:confirmationTab:j_id_id32pc3:btnSubmit")).click();
		 String ICN=driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:icnText")).getText().trim();
		 System.out.println("ICN from TC#22490: "+ICN);
		 log("ICN: "+ICN);
		 Assert.assertTrue(driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:claimStatusText")).getText().trim().equals("Paid"),"The resubmitted claim is not paid for Emergency");
		 }
		 
		 
		 @Test
		 //Referrals exemptions-Family Planning (Referral)
		 public static void test22491() throws Exception{
		 TestNGCustom.TCNo="22491";
		 log("//TC 22491");
		 randomMemberid();
		 memberId=colValues.get(0);
	     System.out.println("Memberid from TC#22491: "+colValues.get(0));
		 Common.portalLogin();
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("22491", "M","110048577A", "60.00", "", "1134135759", "", "",memberSql,"-10","-10");
		 clmNo = advSubmitClaims.clmICN("prof");
		 status=driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:claimStatusText")).getText().trim();
		 System.out.println("ICN and status from TC#22491: "+clmNo+ ","+status);
		 String ICN=driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:icnText")).getText().trim();
		 System.out.println("ICN from TC#22491: "+ICN);
		 log("ICN: "+ICN);
		 Assert.assertTrue(status.equals("Paid"),"The Claim submitted for Family Planning Diag and procedure code is not PAID");
		 }
		 
		 
		 @Test
		 //Referrals exemptions-Family Planning Indicator(Referral)
		 //Same as test case #31225 --with 2 detail lines
		 public static void test22492() throws Exception{
		 TestNGCustom.TCNo="22492";
		 log("//TC 22492");
		 randomMemberid();
		 memberId=colValues.get(0);
	     System.out.println("memberid: "+colValues.get(0));
		 Common.portalLogin();
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("22492", "M","110048577A", "60.00", "", "1134135759", "", "",memberSql,"-11","-11");
		 clmNo = advSubmitClaims.clmICN("prof");
		 status=driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:claimStatusText")).getText().trim();
		 System.out.println("ICN and status from TC#22492: "+clmNo+ ","+status);
		 Referrals.checkEdit("3120");
		 driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id46pc3:btnResubmit")).click();
		 driver.findElement(By.id("professionalBillingTab:_MENUITEM_Procedures")).click();
		 driver.findElement(By.id("proceduresTab:professionalServices:0:item")).click();
		 driver.findElement(By.id("proceduresTab:j_id_id16pc3:familyPlanInd:0")).click();
		 driver.findElement(By.id("proceduresTab:j_id_id16pc3:update_Button")).click();
		 driver.findElement(By.id("proceduresTab:_MENUITEM_Confirmation")).click();
		 Common.getPageError("//form[contains(@id,'Tab')]/div/table[2]/tbody/tr");
		 driver.findElement(By.id("profConfirmationSubView:confirmationTab:j_id_id32pc3:btnSubmit")).click();
		 String ICN=driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:icnText")).getText().trim();
		 System.out.println("ICN from TC#22492: "+ICN);
		 log("ICN: "+ICN);
		 Assert.assertTrue(driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:claimStatusText")).getText().trim().equals("Paid"),"The resubmitted claim is not paid for family planning indicator");
		
		 
		 //submitClaimRef("22492");
		 //Assert.assertTrue(driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:EOBList_1:eobCode")).getText().trim().equals("3120"),"The claim submitted is not having 3120 edit");
		 }
		 
		 @Test
		 //Referrals exemptions-Mental Health(Referral)
		 public static void test22493() throws Exception{
		 TestNGCustom.TCNo="22493";
		 log("//TC 22493");
		 randomMemberid();
		 memberId=colValues.get(0);
	     System.out.println("memberid: "+colValues.get(0));
		 Common.portalLogin();
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("22493", "M","110048577A", "60.00", "", "1134135759", "", "",memberSql,"-12","-12");
		 clmNo = advSubmitClaims.clmICN("prof");
		 status=driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:claimStatusText")).getText().trim();
		 System.out.println("ICN and status from TC#22493: "+clmNo+ ","+status);
		 log("ICN: "+clmNo);
		 Assert.assertTrue(status.equals("Denied"),"The claim is not Denied for exemption,Mental Health");
		 Referrals.checkEdit("4013"); //4013 edit is  also fine
	     //check for edit:2614
		 }
		 
		 
		 @Test
		 //member should be age 10-20
		 //Referral exemptions-Family Planning Provider OB/GYN procedure/diagnosis codes(Referral)
		 //same test as 31225
		 public static void test22484() throws Exception{
		 TestNGCustom.TCNo="22484";
		 log("//TC 22484");
		 randomMemberid();//member should be age 10-20
	     memberId=colValues.get(0);
	    // memberId="100031059338";
	     System.out.println("memberid: "+colValues.get(0));
		 Common.portalLogin();
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("22484", "M","110027857A", "60.00", "", "1134135759", "", "",memberSql,"-13","-13");
		 clmNo = advSubmitClaims.clmICN("prof");
		 status=driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:claimStatusText")).getText().trim();
		 System.out.println("ICN and status from TC#22484: "+clmNo+ ","+status);
		 log("ICN: "+clmNo);
		 Assert.assertTrue(status.equals("Paid"),"The claim is not Paid for exemption,Family planning provider");
		 }
		 
		 
		 @Test
		 //Invalid referral number-edit 3121
		 public static void test31227() throws Exception{
		 TestNGCustom.TCNo="31227";
		 log("//TC 31227");
		 randomMemberid();
		 memberId=colValues.get(0);
		 System.out.println("memberid: "+colValues.get(0));
		 Common.portalLogin();
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("31227", "M","110047258A", "50.00", "", "1821017195", "1234567", "",memberSql,"-70","-70");
		 clmNo = advSubmitClaims.clmICN("prof");
		 status=driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:j_id_id7pc3:claimStatusText")).getText().trim();
		 System.out.println("ICN and status from TC#31227: "+clmNo+ ","+status);
		 Assert.assertTrue(status.contains("Denied"),"Claim is not denied with 3121 edit");
		 Referrals.checkEdit("3121");
		 log("ICN with 3121 edit: "+clmNo);
		 log("Tested only 3121 edit and not using Legacy Id");
		 }
		 
    }
