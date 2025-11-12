package newMMIS_Subsystems;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
public class PAS extends Login{
	int i;
	static int j;
	public boolean tc23601a,tc23602a,tc23603a,tc23604a,tc23599a,tc23252a,tc23253a,tc23256a;
	public static boolean tc23234 = false;
	public static String pasNum23604;
	public static String pasNum23601;
	public static String pasNum23599;
	public static String pasNum23253;
	public static String pasNum23254;
	public static String pasNum23256;
	public static String pasNum23252;
	public static String memberId;
	public static String MemberId;
	public static String clmNo;
	public static String status;
	public static String trxInd,outOfStateReason,retroConvReason;
	public static String SelSql, col,  DelSql,  InsSql;
	public static String sqlSt="select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ base.* from  t_re_base base where ind_active='Y' and sak_recip > dbms_random.value * 6300000 and rownum < 2";
	public static String memberSql = "select /*+ NO_PARALLEL OPT_PARAM('_hash_join_enabled','FALSE') OPT_PARAM('_optimizer_sortmerge_join_enabled','FALSE') OPT_PARAM('_b_tree_bitmap_plans','FALSE') */ base.* from  t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid,t_re_elig e," +
		    "t_cde_aid aid,t_re_aid_elig elig where " +
		    "elig.sak_recip=base.sak_recip " +
		    "and base.sak_recip = e.sak_recip "+
		    "and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
		    "and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
		    "and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
		    "and pgm. CDE_PGM_HEALTH='STD' " +
		    "and elig.DTE_END='22991231' " + 
		    "and  not exists ( select sak_recip from t_re_pmp_assign asg where asg.sak_recip=base.sak_recip) " +
		    "and not exists ( select sak_recip from t_tpl_resource rs where rs.sak_recip=base.sak_recip) " +
		   // "and not exists (select sak_recip from t_re_loc loc where loc.sak_recip=base.sak_recip) "+
		    "and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip) " +
		    "and not exists (select sak_recip from t_pa_pauth pa where pa.sak_recip=base.sak_recip) "+
		    "and base.ind_active='Y' and base.sak_recip > dbms_random.value * 6300000 " +
		    "and rownum<2";
	
	public static void warnings(){
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
		int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
         for(int i=1;i<=length;i++)
          {
        	  driver.findElement(By.id("MMISForm:MMISBodyContent:PasNavigatorPanel:PasNavigator:HtmlMessages_CheckBox_Warnings"+i)).click();
          }
		}
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}
	
	public static void resetValues() {
		trxInd="No";
		outOfStateReason="";
		retroConvReason="";
	}
	

    @BeforeTest
    public void paStartup() throws Exception {
    log("Starting PAS Subsystem......");
    }
	
	@BeforeMethod
	public static void LoginCheck() throws Exception {
		Common.resetBase();
		testCheckDBLoginSuccessful();
		resetValues();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PAS")).click();
	}
	
	
	 public static void submitPAS(String sql,String tcNo,String admDate) throws SQLException{
		 if(tcNo.equals("23230a")){
			 sql="select ID from R_DAY2 where tc='23230'";
			 colNames.add("ID");
			 colValues=Common.executeQuery1(sql, colNames );
		     }
		 else {
		  colNames.add("id_medicaid");
		  colValues=Common.executeQuery(sql, colNames );
		      }
		  MemberId=colValues.get(0);
		     SelSql="select * from R_DAY2 where tc = '"+tcNo+"'";
			 col="ID";
			 DelSql="delete from R_DAY2 where tc = '"+tcNo+"'";  
			 InsSql="insert into  R_DAY2 values ('"+tcNo+"', '"+MemberId+"', 'PAS MemberId', '"+Common.convertSysdate()+"')";
			 Common.insertData(SelSql, col, DelSql, InsSql);
		 sqlStatement=("select * from r_pas where TCNO='"+tcNo+"'");
		 colNames.add("ASSGNMENT");//0
		 colNames.add("REQPROVID"); //1
		 colNames.add("FACILITYPROVID"); //2
		 colNames.add("ATTNDPROVID");//3
		 colNames.add("DIAGCODE"); //4
		 colNames.add("PROCCODE");//5
		 colNames.add("LOC");//6
		 colNames.add("DAYS");//7
		 colValues=Common.executeQuery1(sqlStatement, colNames );
		 String reqprovider=colValues.get(1).substring(0, colValues.get(1).length()-1),
		          reqsvcloc=colValues.get(1).substring(colValues.get(1).length()-1); 
		 String facilityprovider=colValues.get(2).substring(0, colValues.get(2).length()-1),
		          facilitysvcloc=colValues.get(2).substring(colValues.get(2).length()-1); 
		 String attndprovider=colValues.get(3).substring(0, colValues.get(3).length()-1),
		          attndsvcloc=colValues.get(3).substring(colValues.get(3).length()-1); 
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPasSearchBean_CriteriaPanel:NEW")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_RequestingProvID")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_RequestingProvID")).sendKeys(reqprovider);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_RequestingProvIDSearch")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_RequestingProvIDSearch")).sendKeys(reqsvcloc);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_RequestingContactFax")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_RequestingContactFax")).sendKeys("(617)658-9512");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_FacilityProvID")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_FacilityProvID")).sendKeys(facilityprovider);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_FacilityProvIDSearch")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_FacilityProvIDSearch")).sendKeys(facilitysvcloc);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_FacilityContact")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_FacilityContact")).sendKeys("6176589512");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_FacilityContactPhone")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_FacilityContactPhone")).sendKeys("6176589512");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_FacilityContactFax")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_FacilityContactFax")).sendKeys("6176589512");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_AttendingProvID")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_AttendingProvID")).sendKeys(attndprovider);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_AttendingProvIDSearch")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_AttendingProvIDSearch")).sendKeys(attndsvcloc);
	 //Added during R-21.03
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_AttendingPhysicianPhone")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_AttendingPhysicianPhone")).sendKeys("6176176177");
	 
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_MemberIDSearch")).clear();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_MemberIDSearch")).sendKeys(MemberId);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_AdmissionDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_AdmissionDate")).sendKeys(admDate);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_LengthOfStayDays")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_LengthOfStayDays")).sendKeys(colValues.get(7));//36
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_DiagnosisSearch")).clear();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_DiagnosisSearch")).sendKeys(colValues.get(4));//80701
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_AssignCode"))).selectByValue(colValues.get(0)); //ACUTE
     driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_RequestingContact")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_RequestingContact")).sendKeys("John");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_RequestingContactPhone")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_RequestingContactPhone")).sendKeys("6176589512");
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_AccidentIndicator"))).selectByVisibleText("No");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_ReceivedDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_ReceivedDate")).sendKeys(admDate);
	// if(tcNo.equals("23604")||tcNo.equals("23603")){32
	 //new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_TrxReadyIndicator"))).selectByVisibleText(trxInd); 
	// }
	// if(tcNo.equals("23252")){
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:OutOfStateReason_DropDown"))).selectByVisibleText(outOfStateReason);
	// }
	// if(tcNo.equals("23256")||tcNo.equals("23836a")){
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:RetroConversionReason_DropDown"))).selectByVisibleText(retroConvReason); 
	// }
	 
	 }
	 
	 
	 
	 
	 public static void lineItem(String tcNo,String payMethod,String dates,String days) throws SQLException{
		 sqlStatement=("select * from r_pas where TCNO='"+tcNo+"'");
		 colNames.add("PROCCODE");//0
		 colNames.add("LOC");//1
		 colNames.add("DAYS");//2
		 colValues=Common.executeQuery1(sqlStatement, colNames );
		 
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItem_NewButtonClay:PasLineItemList_newAction_btn")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_ReqLevelOfCare"))).selectByValue(colValues.get(1));//ACUTE - ADMIN DAY LEVEL OF CARE
     driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_RequirementEffectiveDate")).clear();
	 if(tcNo.equals("23256")){
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_RequirementEffectiveDate")).sendKeys(Common.convertSysdate()); 
	 }
	 else{
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_RequirementEffectiveDate")).sendKeys(dates);
	 }
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_RequestedDays")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_RequestedDays")).sendKeys(days); //36
     new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_ReqAdmType"))).selectByVisibleText("Medical");
	// if(!(tcNo.equals("23256")||tcNo.equals("23253")||tcNo.equals("23254"))){
	/* driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_ProcICD91Search")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_ProcICD91Search")).sendKeys(colValues.get(0));//7052...5A19054*/
     driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_ProcICDHCPCS1Search")).clear();	
     driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_ProcICDHCPCS1Search")).sendKeys(colValues.get(0));
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_AuthLevelOfCare"))).selectByValue(colValues.get(1));
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_AuthorizationEffectiveDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_AuthorizationEffectiveDate")).sendKeys(dates);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_AuthorizedDays")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_AuthorizedDays")).sendKeys(days);
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_AuthAdmType"))).selectByVisibleText("Medical");
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_PymtMethodCode"))).selectByVisibleText(payMethod);
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_ReviewerLevel"))).selectByVisibleText("Physician");
	//  }
	 if(tcNo.equals("31214")){
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_PrePaymentIndicator"))).selectByValue("Y");
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_PostPaymentIndicator"))).selectByValue("Y");
	 }
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemPanel_addAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasNavigatorPanel:PasNavigator:SAVEALL")).click();
	  }
	 
	 public static String getMemId(String tcNo) throws Exception{
		 String sql="select * from R_DAY2 where tc='"+tcNo+"'";
		 colNames.add("ID");
		 colValues=Common.executeQuery1(sql,colNames);
		 String memberId=colValues.get(0);
		 return memberId;
	     }
	 
	 
	 public static void checkEdit(String ct,String edit){
		   int i;
	   int rownum=driver.findElements(By.xpath("//*[@id='"+ct+"ClaimSubmissionSubView:submissionStatus:EOBList:tbody_element']/tr")).size();
	   for(i=1;i<=rownum;i++){
		   if(driver.findElement(By.id(""+ct+"ClaimSubmissionSubView:submissionStatus:EOBList:"+(i-1)+":eobCode")).getText().trim().equals(edit))
			                           
		   break;
		   }
	    if(i==(rownum+1))
         {
     	  throw new SkipException("Edit:"+edit+"is not found");
         } 
	   }
	
	 
	 
	 @Test
	 //DRG-PAS not on Claim admit-discharge pricing match--Claim recieves rateid of 20 (Autolook up for PAS)
	 public void test32193() throws Exception{
	 TestNGCustom.TCNo="32193";
	 log("//TC 32193");
	 //Member Id need to have Aidcategory=03 
	 String memSql=memberSql+" and elig.sak_cde_aid='182'";
	 submitPAS(memSql,"32193",Common.convertSysdatecustom(-65));
	 lineItem("32193","Acute SPAD/DRG",Common.convertSysdatecustom(-65),"4");
	 Common.save();
	 Thread.sleep(12000);
	 changeStatus("0","IN REVIEW");
	 changeStatus("0","APPROVED");
	 String pasNum=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
	 log("PAS num: "+pasNum);
	 String memberId=getMemId("32193");
	 String sql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
	 Common.portalLogin();
	 advSubmitClaims.initiateClaim();
	 advSubmitClaims.admType="3 - ELECTIVE";
	 advSubmitClaims.I("32193", "I", "110001958E", "1010", "111", "1922020882", "", "" , sql,"-65","-61");
	 clmNo = advSubmitClaims.clmICN("inst");
	 status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
	 System.out.println("ICN from TC#32193: "+clmNo);
	 Common.portalLogout();
	 checkRateId(clmNo,"20 - DRG ACUTE INPATIENT PAYMENT");
	 if(status.equals("Paid")){
		checkPAS(pasNum,clmNo);
		}
	 else{
		log("Claim submitted is not paid but got the expected RateId");
		}
	 
	 }
	 
		 
	 @Test
	 //The table T_PA_RATE_ID should have all the new rateIDs.RateID 20 and 40 should have field cde_pymt_method =3. RateIDs 21-23 and 41-43 should have field cde_pymt_method = 5.
	 //Need to run before running DRG pricing test cases
	 public void test32204() throws Exception{
	 TestNGCustom.TCNo="32204";
	 log("//TC 32204");
     String sql="select * from T_PA_RATE_ID where CDE_RATE_ID in ('20','40','21','22','23','41','42','43') and DTE_END='22991231'";
	 colNames.add("CDE_RATE_ID");
	 colNames.add("CDE_PYMT_METHOD");
	 colValues=Common.executeQuery(sql, colNames);
	 log("RateId,PaymentMethod in the table T_PA_RATE_ID with DTE_END='22991231' : "+colValues);
	 System.out.println("output from sql: "+colValues);
	
	 }
	 
	 
	 
	 
	 public static void checkRateId(String icn,String rateId) throws IOException {
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Claims")).click();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchBean_CriteriaPanel:ClmIcnNumber")).sendKeys(icn);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchResultsDataTable_0:_id14")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:UB92ClaimNavigatorPanel:UB92ClaimNavigator:ITM_UB92Claim12")).click();//Clicking Health Program link
	 driver.findElement(By.id("MMISForm:MMISBodyContent:HealthProgramPanel:HealthProgramList_0:PaymentInfoBean_ColValue_status")).click();
	 String RateId=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:HealthProgramPanel:PaymentInfoBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[11]/td[2]")).getText().trim();
	 if (RateId.contains(rateId)) {
		 log("ICN that received the expected Rate ID, "+rateId+" ,is: "+icn);
	 }
	 else {
	 log("ICN didnt receive expected Rate Id("+rateId+") but got "+RateId+" is: "+icn);
	 Assert.fail("Didnt get expected Rate Id("+rateId+"), but got "+RateId);
	      }
	 }
	 
	 
	 public static void checkPAS(String pasNum,String icn) throws IOException{
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PAS")).click();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPasSearchBean_CriteriaPanel:PasSearchResultsDataPanel_PasNumber")).sendKeys(pasNum);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPasSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPasSearchResults_0:PasSearch_link")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasNavigatorPanel:PasNavigator:ITM_n102")).click();//clicking Claim List panel
	 String icnPanel=driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimPriorAuthPanel:ClaimList1_0:ClaimPriorAuthBean_ColValue_claim_ICN")).getText().trim();
	 if (icnPanel.contains(icn)) {
		 log("The ICN in PAS Claim List panel is same as submitted ICN");
	    }
	 else {
	 log("The ICN in PAS Claim List panel is not same as submitted ICN");
	 Assert.fail("The ICN in PAS Claim List panel is not same as submitted ICN");
	     }
	
	 }
	 
	 public static String addLTC(String provId,String memId) throws Exception{
		 
		  String provider=provId.substring(0, provId.length()-1),
			      svcloc=provId.substring(provId.length()-1);	
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();	 
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
     driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
     driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memId);
     driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
     driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id23")).click();
     driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n113")).click();
     driver.manage().timeouts().implicitlyWait(1, TimeUnit.MILLISECONDS);
     boolean exists = driver.findElements(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareNoRecordsMessage") ).size() != 0;//if no records found message is there, exists=true
     System.out.println("Exists status: "+exists);
     driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
     if(exists==true){
     driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCare_NewButtonClay:LevelOfCareList_newAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareBean_providerID")).clear();
     driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareBean_providerID")).sendKeys(provider);
     driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareBean_SvcLoc")).clear();
     driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareBean_SvcLoc")).sendKeys(svcloc);
     new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_ReLocCode"))).selectByVisibleText("NF - NURSING FACILITY");
     new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_ReLocAdmitReason"))).selectByVisibleText("HM - Admitted from home");
     driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_EffectiveDate")).clear();
     driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_EffectiveDate")).sendKeys(Common.convertSysdatecustom(-70));
     driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_AdmissionDate")).clear();
     driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_AdmissionDate")).sendKeys(Common.convertSysdatecustom(-70));
     driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCarePanel_addAction_btn")).click();
     Common.saveAll();
     Common.cancelAll();
         }
	 return memId;
     }
	 
	 
	 
	  
		public static String addMMQ(String provId,String sql) throws Exception{
	     colNames.add("ID_MEDICAID");
	     colNames.add("NAM_LAST");
	     colNames.add("NAM_FIRST");
	     colValues=Common.executeQuery(sql, colNames);
	     String memId=colValues.get(0);
	     String lastName=colValues.get(1);
	     String firstName=colValues.get(2);
	     String FI = firstName.substring(0,((firstName.length())-(firstName.length()-1)));
	     String provider=provId.substring(0, provId.length()-1),
			      svcloc=provId.substring(provId.length()-1);	 
	     driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();	 
		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
         driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
         driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memId);
         driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
         driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id23")).click();
         driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n113")).click();
         driver.manage().timeouts().implicitlyWait(1, TimeUnit.MILLISECONDS);
         boolean exists = driver.findElements(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareNoRecordsMessage") ).size() != 0;//if no records found message is there, exists=true
         System.out.println("Exists status: "+exists);
         driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
         if(exists==true){
         driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCare_NewButtonClay:LevelOfCareList_newAction_btn")).click();
 		 driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareBean_providerID")).clear();
         driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareBean_providerID")).sendKeys(provider);
         driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareBean_SvcLoc")).clear();
         driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareBean_SvcLoc")).sendKeys(svcloc);
         new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_ReLocCode"))).selectByVisibleText("NF - NURSING FACILITY");
         new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_ReLocAdmitReason"))).selectByVisibleText("HM - Admitted from home");
         driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_EffectiveDate")).clear();
         driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_EffectiveDate")).sendKeys(Common.convertSysdatecustom(-70));
         driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_AdmissionDate")).clear();
         driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_AdmissionDate")).sendKeys(Common.convertSysdatecustom(-70));
         driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCarePanel_addAction_btn")).click();
         Common.saveAll();
         Common.cancelAll();
 		Common.portalLogin();
 		driver.findElement(By.linkText("Manage Members")).click();
 	    driver.findElement(By.linkText("Long Term Care")).click();
 	    driver.findElement(By.linkText("Enter Management Minutes Questionnaires (MMQ)")).click();
 	   // new Select(driver.findElement(By.id("mmqMemberSearchForm:mmqSearchPanel:providerID"))).selectByVisibleText("1356496947-110025810A-SHERRILL HOUSE INC-135 SO HUNTINGTON AV");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'providerID')]"))).selectByValue(provId);
 	    driver.findElement(By.xpath("//input[contains(@id, 'memberID')]")).clear();
 	    driver.findElement(By.xpath("//input[contains(@id, 'memberID')]")).sendKeys(memId);
 	    driver.findElement(By.xpath("//input[contains(@id, 'lastName')]")).clear();
 	    driver.findElement(By.xpath("//input[contains(@id, 'lastName')]")).sendKeys(lastName);
 	    driver.findElement(By.xpath("//input[contains(@id, 'firstInitial')]")).clear();
 	    driver.findElement(By.xpath("//input[contains(@id, 'firstInitial')]")).sendKeys(FI);
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'reasonForSubmission')]"))).selectByVisibleText("1 - New Admission");
 	    driver.findElement(By.xpath("//input[contains(@id, 'btnSearchMMQ')]")).click();
 	    driver.findElement(By.xpath("//*[@id='mmqPersonalInfoForm:mmqPersonalInfoPanel:dateOfAdmission']")).sendKeys(Common.convertSysdatecustom(-70));
 	    driver.findElement(By.xpath("//*[@id='mmqPersonalInfoForm:mmqPersonalInfoPanel:effectiveDate']")).sendKeys(Common.convertSysdatecustom(-70));
 	    driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_firstServiceQuestionTab')]")).click();
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'skilledObjDaily')]"))).selectByVisibleText("1 - No Observation");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'personalHygBathing')]"))).selectByVisibleText("1 - Independent/Restorative Prg.");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'personalHygGrooming')]"))).selectByVisibleText("2 - Assist");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'dressing')]"))).selectByVisibleText("1 - Independent/Restorative Prg.");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'mobility')]"))).selectByVisibleText("1 - Independent/Restorative Prg.");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'eating')]"))).selectByVisibleText("1 - Independent/Restorative Prg.");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'continenceCatBladder')]"))).selectByVisibleText("2 - Incontinent Occasionally");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'continenceCatBowel')]"))).selectByVisibleText("1 - Continent");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'bladderBowelRetaining')]"))).selectByVisibleText("2 - Bladder Retraining");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'positioning')]"))).selectByVisibleText("2 - Assist");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'pressureUlcerPrevention')]"))).selectByVisibleText("1 - No Preventative Measures");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'skillProcDailyPressUlcerStg1')]"))).selectByVisibleText("0");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'skillProcDailyPressUlcerStg2')]"))).selectByVisibleText("1");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'skillProcDailyPressUlcerStg3')]"))).selectByVisibleText("2");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'skillProcDailyPressUlcerStg4')]"))).selectByVisibleText("1");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'skillProcDailyPressUlcerFreq')]"))).selectByVisibleText("4");
 	    new Select(driver.findElement(By.xpath("//*[@id='mmqFirstServiceQuestionForm:mmqFirstServiceQuestionPanel:mmqServiceQuestionVO_skillProcDailyOther']"))).selectByVisibleText("1");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'skillProcDailyOtherProc1')]"))).selectByVisibleText("01 - Dressing Change");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'skillProcDailyOtherProc2')]"))).selectByVisibleText("02 - Catheter Irrigation");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'skillProcDailyOtherProc3')]"))).selectByVisibleText("05 - Ear Irrigation");
 	    //doubt
 	    //driver.findElement(By.xpath("//input[contains(@id, 'Get_Score')]")).click();
 	    driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_secondServiceQuestionTab')]")).click();
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'specialAttImmobility')]"))).selectByVisibleText("1");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'specialAttSeverSpasRigidity')]"))).selectByVisibleText("0");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'specialAttBehaviourProb')]"))).selectByVisibleText("1");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'specialAttIsolation')]"))).selectByVisibleText("1");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'restNursingCodeType1')]"))).selectByVisibleText("0 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'restNursingCodeType2')]"))).selectByVisibleText("0 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'restNursingCodeType3')]"))).selectByVisibleText("0 - None");
 	    driver.findElement(By.xpath("//input[contains(@id, 'Get_Score')]")).click();
 	    driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_additionalQuestionTab')]")).click();
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'toiletUse')]"))).selectByVisibleText("1 - Independent");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'transfer')]"))).selectByVisibleText("2 - Assist");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'mentalStatus')]"))).selectByVisibleText("2 - Disoriented");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'estraint')]"))).selectByVisibleText("1 - Not Ordered");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'activitiesParticipation')]"))).selectByVisibleText("2 - Occasionally Active");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'consultationType1')]"))).selectByVisibleText("00 - 00 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'consultationType2')]"))).selectByVisibleText("00 - 00 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'consultationType3')]"))).selectByVisibleText("00 - 00 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'consultationFreq1')]"))).selectByVisibleText("0 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'consultationFreq2')]"))).selectByVisibleText("0 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'consultationFreq3')]"))).selectByVisibleText("0 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'medicationMed1')]"))).selectByVisibleText("0 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'medicationFreq1')]"))).selectByVisibleText("0 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'medicationMed2')]"))).selectByVisibleText("0 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'medicationFreq2')]"))).selectByVisibleText("0 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'medicationMed3')]"))).selectByVisibleText("0 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'medicationFreq3')]"))).selectByVisibleText("0 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'medicationMed4')]"))).selectByVisibleText("0 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'medicationFreq4')]"))).selectByVisibleText("0 - None");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'accidContWeightAccidents')]"))).selectByVisibleText("No");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'accidContWeightContracture')]"))).selectByVisibleText("No");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'accidContWeightWeightChange')]"))).selectByVisibleText("No");
 	    driver.findElement(By.xpath("//input[contains(@id, 'primaryICDDiagnosis')]")).clear();
 	    driver.findElement(By.xpath("//input[contains(@id, 'primaryICDDiagnosis')]")).sendKeys("z006");//1234-ICD-9 diag code  A200--icd-10
 	    driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_certifiedStatementTab')]")).click();
 	    driver.findElement(By.xpath("//input[contains(@id, 'rnEvaluator')]")).click();
 	    driver.findElement(By.xpath("//input[contains(@id, 'rnEvaluator')]")).clear();
 	    driver.findElement(By.xpath("//input[contains(@id, 'rnEvaluator')]")).sendKeys("WILLIAM");
 	    driver.findElement(By.xpath("//input[contains(@id, 'evaluationDate')]")).sendKeys(Common.convertSysdate());
 	    driver.findElement(By.xpath("//input[contains(@id, 'nameOfAdmin')]")).clear();
 	    driver.findElement(By.xpath("//input[contains(@id, 'nameOfAdmin')]")).sendKeys("JOSEPH");
 	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'affiliation')]"))).selectByVisibleText("1 - Nursing Facility Staff");
 	     driver.findElement(By.xpath("//*[@id='mmqCertifiedStatementForm:j_id_id35pc3:j_id_id37pc3']")).click();
         }
         else{
        	 Common.cancelAll();
             }
        return memId;
	    }
	 
	 
	/*@Test  TEST CASE HAS BEEN REMOVED
	 //Enter PAS Re-Review request thru web portal *NEED TO PASS PAS# FROM 22503a*
	 public void test22503() throws Exception{
	 TestNGCustom.TCNo="22503";
	 log("//TC 22503");
	 Common.portalLogin();
	 PA.randomMemberid();
	 driver.findElement(By.linkText("Manage Service Authorizations")).click();
	 driver.findElement(By.linkText("Pre-Admission Screening")).click();
	 driver.findElement(By.linkText("Enter PAS Request")).click();
	 new Select(driver.findElement(By.xpath("//select[contains(@id, 'baseInfo_assignment')]"))).selectByVisibleText("CHRONIC DISEASE/REHAB");
	 new Select(driver.findElement(By.xpath("//select[contains(@id, 'baseInfo_requestingProvider')]"))).selectByValue("110020829G");
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_contactName')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_contactName')]")).sendKeys("John");
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_contactPhone')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_contactPhone')]")).sendKeys("6176895584");
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_providerContactFax')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_providerContactFax')]")).sendKeys("6176895584");
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityMassHealthId')]")).clear();
     driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityMassHealthId')]")).sendKeys("110024350B");
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityContactName')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityContactName')]")).sendKeys("Smith");
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityContactPhone')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityContactPhone')]")).sendKeys("6176895584");
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityContactFax')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityContactFax')]")).sendKeys("6176895584");
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_memberID')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_memberID')]")).sendKeys(colValues.get(0));
	 new Select(driver.findElement(By.xpath("//select[contains(@id, 'baseInfo_accidentIndicator')]"))).selectByVisibleText("No");
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_admissionDate')]")).sendKeys(Common.convertSysdate());
	 driver.findElement(By.xpath("//input[contains(@id, 'lengthOfStay')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id, 'lengthOfStay')]")).sendKeys("15");
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_primaryDiagnosisCode')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_primaryDiagnosisCode')]")).sendKeys("A15");//A15  78652
	 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_PASAddUpdLineItems')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id, 'lineItemsList:new_Button')]")).click();
	 new Select(driver.findElement(By.xpath("//select[contains(@id, 'requestedCareLevel')]"))).selectByVisibleText("CHRONIC/REHAB - ADMIN DAY LEVEL OF CARE");
	 new Select(driver.findElement(By.xpath("//select[contains(@id, 'requestedAdmissionType')]"))).selectByVisibleText("Medical");
	 driver.findElement(By.xpath("//input[contains(@id, 'requestedEffDate')]")).sendKeys(Common.convertSysdate());
	 driver.findElement(By.xpath("//input[contains(@id, 'requestedDays')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id, 'requestedDays')]")).sendKeys("15");
	 driver.findElement(By.xpath("//input[contains(@id, 'add_Button')]")).click();
	 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_PASAddUpdConfirm')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id, 'submit')]")).click(); 
	 if(driver.findElements(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).size()>0){
	 driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click(); 
	 driver.findElement(By.xpath("//input[contains(@id, 'submit')]")).click();
	  }
	 String pasNum=driver.findElement(By.xpath("//span[contains(@id, 'baseInfo_pasNumberText')]")).getText().trim();
	 System.out.println("PAS num from TC#22503: "+pasNum);
	 log("PAS num:"+pasNum);
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='pasConfirmationResponse']/table[1]/tbody/tr[2]/td/span[3]")).getText().trim().contains("successfully submitted"),"The PAS is not successfully submitted");
	 Common.portalLogout();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_PAS")).click();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Information")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasMiniSearchPreAdmScreeningInput")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasMiniSearchPreAdmScreeningInput")).sendKeys(pasNum);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasMiniSearch_SearchButton")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPasSearchResults_0:PasSearch_link")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasNavigatorPanel:PasNavigator:ITM_n105")).click();
	 Thread.sleep(12000);
	 changeStatus("0","IN REVIEW");
//	 warnings();
//	 Common.saveAll();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemList_0:PasLineItemBean_ColValue_status")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_LineitemStat"))).selectByVisibleText("DENIED");
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_ReviewerLevel"))).selectByVisibleText("MassHealth");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:PasIacXref_NewButtonClay:PasIacXrefList_newAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:PasReasonCodeDataSearch")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:PasReasonCodeDataSearch")).sendKeys("001");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:PasIacXrefPanel_addAction_btn")).click();
//	 warnings();
//	 Common.saveAll();
	 Common.save();
	 Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemList_0:PasLineItemBean_ColValue_lineitemStat_statusDescription")).getText().trim().contains("DENIED"),"The PAS status is not updated to DENIED");
	 Common.portalLogin();
	 driver.findElement(By.linkText("Manage Service Authorizations")).click();
	 driver.findElement(By.linkText("Pre-Admission Screening")).click();
	 driver.findElement(By.linkText("Inquire/Maintain PAS Request")).click();
	 driver.findElement(By.xpath("//input[contains(@id, 'pas')]")).click();
	 driver.findElement(By.xpath("//input[contains(@id, 'pas')]")).clear();
	 driver.findElement(By.xpath("//input[contains(@id, 'pas')]")).sendKeys(pasNum);
	 driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click();
	 driver.findElement(By.xpath("//span[contains(@id, '0:pas')]")).click();
	 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_PASInqUpdLineItems')]")).click();
	 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_PASInqUpdExt')]")).click();
     driver.findElement(By.xpath("//span[contains(@id, '0:lineItemID')]")).click();
     driver.findElement(By.xpath("//span[contains(@id, '0:extensionID')]")).click();

	
	 }*/
	
	
	 @Test
	 //Hosp and Admin Day Level of Care on same PAS
	 public void test22575() throws Exception{
	 TestNGCustom.TCNo="22575";
	 log("//TC 22575");
	// submitPAS(sqlSt,"22575",Common.convertSysdate());
	 submitPAS(sqlSt,"22575",Common.convertSysdatecustom(-100));
	// lineItem("22575","Chronic/Rehab and Acute with Rehab HLOC",Common.convertSysdatecustom(3),"30");
	 lineItem("22575","Chronic/Rehab and Acute with Rehab HLOC",Common.convertSysdatecustom(-97),"30");
//	 warnings();
//	 Common.saveAll();
	 Common.save();
	 Thread.sleep(12000);
	 changeStatus("0","IN REVIEW");
	 changeStatus("0","APPROVED");
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_LetterIndicator"))).selectByVisibleText("No Print");
	// lineItem("22575a","Chronic/Rehab and Acute with Rehab HLOC",Common.convertSysdatecustom(1),"2");
	 lineItem("22575a","Chronic/Rehab and Acute with Rehab HLOC",Common.convertSysdatecustom(-99),"2");
//	 warnings();
//	 Common.saveAll();
	 Common.save();
	 Thread.sleep(12000);
	 changeStatus("0","IN REVIEW");
	 changeStatus("0","APPROVED");
	 String pasNum=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
	 log("PAS num: "+pasNum);
	 }
	 
	 
	
	 
	 public static void changeStatus(String lineNum,String status){
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemList_"+lineNum+":PasLineItemBean_ColValue_status")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_LineitemStat"))).selectByVisibleText(status);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemPanel_updateAction_btn")).click();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PasNavigatorPanel:PasNavigator:SAVEALL")).click();
	 warnings();
	 Common.saveAll();
	 }
	
	
	@Test
	 //Duplicate Check on a Saved Request iWI 744
	 //TC#23245 included Duplicate PAS
	 public static void test23230() throws Exception{
	 TestNGCustom.TCNo="23230";
	 log("//TC 23230 & 23245");
	 submitPAS(sqlSt,"23230",Common.convertSysdatecustom(-100));
	 lineItem("23230","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdatecustom(-100),"35");
//	 warnings();
//	 Common.saveAll();
	 Common.save();
	 submitPAS(sqlSt,"23230a",Common.convertSysdatecustom(-100));
	 lineItem("23230a","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdatecustom(-100),"35");
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td[2]")).getText().trim().contains("Duplicate on PAS Number"),"The duplicate PAS error is not displayed");
	 log("Validating duplicate PAS is done");
	 }
	 
	
	@Test
	 //Move a Request from Review Queue to Assigned Queue 361
	 public static void test23231() throws Exception{
	 TestNGCustom.TCNo="23231";
	 log("//TC 23231");
	 submitPAS(sqlSt,"23231",Common.convertSysdate());
	 lineItem("23231","Chronic/Rehab and Acute with Rehab HLOC",Common.convertSysdate(),"35");
     Common.save();
	 Thread.sleep(12000);
	 changeStatus("0","IN REVIEW");
	 changeStatus("0","APPROVED");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemList_0:PasLineItemBean_ColValue_status")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_LineitemStat"))).selectByVisibleText("VOID");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:PasIacXref_NewButtonClay:PasIacXrefList_newAction_btn")).click();
	 driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:PasReasonCodeDataSearch_CMD_SEARCH']/img")).click();
	 //driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id53:PasReasonCodeSearchCriteriaPanel:Code")).clear();
	 //driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id51:PasReasonCodeSearchCriteriaPanel:Code")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id51:PasReasonCodeSearchCriteriaPanel:Code")).clear();

	// driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id51:PasReasonCodeSearchCriteriaPanel:Code")).sendKeys("0");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id51:PasReasonCodeSearchCriteriaPanel:Code")).sendKeys("0");

	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id51:PasReasonCodeSearchCriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id51:ReasCodeSearchResults_0:column1Value")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemPanel_updateAction_btn")).click();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PasNavigatorPanel:PasNavigator:SAVEALL")).click();
	 Common.save();
//	 NO PRINT in Base Information panel
	 String pasNum=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
	 System.out.println("PAS num: "+pasNum);
	 log("PAS Number: "+pasNum);
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Worklist")).click();
	// new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklist_DropDownQueueType"))).selectByValue("20");
	 //driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:_id12")).click();
	 //driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults:_id38")).click(); //Sorting on date recieved
	 //driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults:_id38")).click(); 
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorklistSearchBean_CriteriaPanel:SEARCH")).click();
	 driver.findElement(By.xpath("//*[text()='Date Received']")).click(); //Sorting on date recieved	 
	 driver.findElement(By.xpath("//*[text()='Date Received']")).click();
	  //pasNum=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults:tbody_element']/tr[1]/td[2]")).getText().trim();
	  pasNum=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasSearchResults:tbody_element']/tr[1]/td[2]")).getText().trim();

	 System.out.println("PAS# from tc#23231: "+pasNum);
	 log("PAS num: "+pasNum);
	 //driver.findElement(By.id("workList[0]")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasSearchResults_0:rowCheck")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaUser"))).selectByValue("RSHRESTJ");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:_id62")).click();//clicking route button
	 String message=driver.findElement(By.cssSelector("td.message-text")).getText();
	 Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");

	 
	 
	 
	 /*
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistBean_radioValuePerson1")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PaUser"))).selectByVisibleText("RSHRESTJ");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:_id104")).click();
	 Thread.sleep(12000);
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklist_DropDownQueueType"))).selectByValue("3");//selecting assigned queue
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:_id12")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults:_id38")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults:_id38")).click();
	 int rownum =driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults:tbody_element']/tr")).size();
     for(int i=1;i<=rownum;i++)
       {
        if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults:tbody_element']/tr["+i+"]/td[2]")).getText().equals(pasNum))
     	  {
	          {
	           break;
	          }
     	  }
	       Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults:tbody_element']/tr["+i+"]/td[2]")).getText().trim().equals(pasNum),"Moving a request from Review queue to Assigend queue failed");
	   }
	   */
    }
	
	
	 @Test
	 //Add decision status using the Decision Status Panel
	 public static void test23233() throws Exception{
	 TestNGCustom.TCNo="23233";
	 log("//TC 23233");
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasRelatedDataNavigatorPanel:PasRelatedDataNavigator:ITM_n3")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStatSearchResults:PasLineItemStatBean_ColHeader_paStatusCode")).click();//sorting on decision status code
	 if(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStatSearchResults_0:PasLineItemStatBean_ColValue_paStatusCode")).getText().equals("Z")){
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStatSearchResults_0:PasLineItemStatBean_ColValue_status")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStatDataPanel_StatusDescription")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStatDataPanel_StatusDescription")).sendKeys("For Test only"+Common.convertSysdate());
	// Common.saveAll();
	 Common.save();
	 }
	 else{
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStat_NewButtonClay:PasLineItemStatList_newAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStatDataPanel_PaStatusCode")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStatDataPanel_PaStatusCode")).sendKeys("Z");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStatDataPanel_StatusDescription")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStatDataPanel_StatusDescription")).sendKeys("For Test only"+Common.convertSysdate());
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStatDataPanel_StatusTypeCode"))).selectByVisibleText("Finalized");
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStatDataPanel_IndPayable"))).selectByVisibleText("No");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStatDataPanel_EndDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineitemStatPanel:PasLineItemStatDataPanel_EndDate")).sendKeys(Common.convertSysdate());
	// Common.saveAll();
	 Common.save();
	 }
	 Common.cancelAll();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:MainPasSearchBean_CriteriaPanel:NEW")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItem_NewButtonClay:PasLineItemList_newAction_btn")).click();
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_LineitemStat"))).selectByVisibleText("FOR TEST ONLY"+Common.convertSysdate());
	 String selected=new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItemDataPanel_LineitemStat"))).getFirstSelectedOption().getText();
	 Assert.assertTrue(selected.contains("FOR TEST ONLY"+Common.convertSysdate()), "Couldn't find newly added decision status in PAS line item tab");
	 Common.cancelAll();
	 log("Adding decision status using the Decision Status Panel is done");
	 }
	
	
	 public static void newAssignment(String code){
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCode_NewButtonClay:PasAssignCodeList_newAction_btn")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeDataPanel_Code")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeDataPanel_Code")).sendKeys("RT");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeDataPanel_Description")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeDataPanel_Description")).sendKeys(code);
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeDataPanel_AssignGroup"))).selectByVisibleText("PRE-ADMISSION SCREENING");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeDataPanel_EndDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeDataPanel_EndDate")).sendKeys(Common.convertSysdate());
	// Common.saveAll();
	 Common.save();
	 Common.cancelAll();
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 }
	 
	 public static void updateAssignment(String desc,String effDate,String endDate){
		 
	 boolean isExist=false;	 
		 
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasRelatedDataNavigatorPanel:PasRelatedDataNavigator:ITM_n1")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeSearchResults:PasAssignCodeBean_ColHeader_code")).click();//Sorting on Code
	 List<WebElement> codes=driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeSearchResults:tbody_element']/tr/td[3]//span"));
	 for (WebElement code : codes) {
		if(code.getText().equals("RT")) {
			isExist=true;
			break;
		}
	 }
	 
	 if(isExist){
		 driver.findElement(By.xpath("//*[text()='RT']")).click(); 
	 }
	 else{
		 newAssignment("Reg Testing");
	 }
	 
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeDataPanel_Description")).clear();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeDataPanel_Description")).sendKeys(desc);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeDataPanel_EffectiveDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeDataPanel_EffectiveDate")).sendKeys(effDate);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeDataPanel_EndDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasAssignCodePanel:PasAssignCodeDataPanel_EndDate")).sendKeys(endDate);
     //Common.saveAll();
	 Common.save();
	 Common.cancelAll();
	 }
	
	@Test
	 //Validate Assignment Maintenance Discontinue Code
	 public static void test23234() throws Exception{
	 TestNGCustom.TCNo="23234";
	 log("//TC 23234");
     updateAssignment("ENDTODAY"+Common.convertSysdate(),Common.convertSysdate(),Common.convertSysdate());
     submitPAS(sqlSt,"23234",Common.convertSysdate());
	 lineItem("23234","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdate(),"35");
//	 warnings();
//	 Common.saveAll();
	 Common.save();
	 Common.cancelAll();
	 updateAssignment("ENDTOMORROW"+Common.convertSysdate(),Common.convertSysdate(),Common.convertSysdatecustom(1));
	 submitPAS(sqlSt,"23234",Common.convertSysdate());
	 lineItem("23234","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdate(),"35");
//	 warnings();
//	 Common.saveAll();
	 Common.save();
	 Common.cancelAll();
	 updateAssignment("ENDYESTERDAY"+Common.convertSysdate(),Common.convertSysdatecustom(-1),Common.convertSysdatecustom(-1));
	 submitPAS(sqlSt,"23234",Common.convertSysdate());
	 lineItem("23234","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdate(),"35");
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td[2]")).getText().trim().contains("Assignment code is inactive. Please select an active code"),"Validating Assignment for End date=Yesterday failed");
	 log("Validate Assignment Maintenance in base is done");
	 tc23234=true;
	 }
	
	 public static void updateReasoncode(String desc){
		 int i;
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasRelatedDataNavigatorPanel:PasRelatedDataNavigator:ITM_n1234")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextSearchResults:PasIacTextBean_ColHeader_iacCode")).click();//sorting on reason code
	 int rownum=driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextSearchResults:tbody_element']/tr")).size();
	 for(i=0;i<=rownum;i++){
		 if(driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextSearchResults_"+i+":PasIacTextBean_ColValue_iacCode")).getText().contains("011")){
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextSearchResults_"+i+":PasIacTextBean_ColValue_status")).click();
		 break;
		 }
	/* else{
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacText_NewButtonClay:PasIacTextList_newAction_btn")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextDataPanel_IacCode")).sendKeys("011");
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextDataPanel_IacDescription")).sendKeys("REGRESSION TEST");
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextDataPanel_EffectiveDate")).sendKeys(Common.convertSysdate());
		 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextDataPanel_EndDate")).sendKeys(Common.convertSysdate());
		 Common.saveAll();
	     }*/
		
	 }
	 System.out.println("i value after coming out of loop: "+i);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextDataPanel_IacDescription")).clear();
	 changeReasoncode("EFFECTIVE DATE CURRENT",Common.convertSysdate(),Common.convertSysdate());
	 checkReasoncode("efftoday");
	 changeReasoncode("EFFECTIVE DATE TOMORROW",Common.convertSysdatecustom(1),Common.convertSysdatecustom(1));
	 checkReasoncode("efftomorrow");
	 changeReasoncode("END DATE PAST",Common.convertSysdatecustom(-7),Common.convertSysdatecustom(-2));
	 checkReasoncode("endpast");
	 changeReasoncode("END DATE CURRENT",Common.convertSysdate(),Common.convertSysdate());
	 checkReasoncode("endcurrent");
	 }
	 
	 public static void changeReasoncode(String desc,String effDate,String endDate){
		 //driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextSearchResults_0:PasIacTextBean_ColValue_status")).click();

	 driver.findElement(By.xpath("//*[text()='011']")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextDataPanel_IacDescription")).sendKeys(desc);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextDataPanel_EffectiveDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextDataPanel_EffectiveDate")).sendKeys(effDate);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextDataPanel_EndDate")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextDataPanel_EndDate")).sendKeys(endDate); 
	 Common.save();
	// Common.cancelAll();
	  }
	 
	
     public static void checkReasoncode(String desc){
     driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
     driver.findElement(By.id("MMISForm:MMISBodyContent:MainPasSearchBean_CriteriaPanel:NEW")).click();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasLineItem_NewButtonClay:PasLineItemList_newAction_btn")).click();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:PasIacXref_NewButtonClay:PasIacXrefList_newAction_btn")).click();
     driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:PasReasonCodeDataSearch_CMD_SEARCH']/img")).click();	 
     driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id51:PasReasonCodeSearchCriteriaPanel:Code")).clear();
     driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id51:PasReasonCodeSearchCriteriaPanel:Code")).sendKeys("011");
     driver.findElement(By.id("MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id51:PasReasonCodeSearchCriteriaPanel:SEARCH")).click();
     if(desc.equals("efftoday")){
     Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id51:ReasCodeSearchResults_0:column2Value']")).getText().trim().equals("EFFECTIVE DATE CURRENT"),"Validating reason code for EffectiveDateCurrent failed");
            }
     else if(desc.equals("efftomorrow")){
     Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id51:noRowsFoundText']")).getText().trim().equals("***No records found***"),"Validating reason code for EffectiveDateTomorrow failed"); 
	        }
	 else if(desc.equals("endpast")){
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id51:noRowsFoundText']")).getText().trim().equals("***No records found***"),"Validating reason code for EndDatePast failed");
	        }
	 else if(desc.equals("endcurrent")){
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasLineItemPanel:PasIacXrefPanel:_id51:ReasCodeSearchResults_0:column2Value']")).getText().trim().contains("END DATE CURRENT"),"Validating reason code for EndDateCurrent failed");
	        } 
     Common.cancelAll();
     driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasRelatedDataNavigatorPanel:PasRelatedDataNavigator:ITM_n1234")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextSearchResults:PasIacTextBean_ColHeader_iacCode")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextSearchResults_0:PasIacTextBean_ColValue_iacCode")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextDataPanel_IacDescription")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasIacTextPanel:PasIacTextDataPanel_IacDescription")).sendKeys("REGRESSION TEST");
	 Common.save();
	 }
     
     
     @Test
	 //Validate Assignment Maintenance add Assignment
	 public static void test23235() throws Exception{
	 TestNGCustom.TCNo="23235";
	 log("//TC 23235");
	// if (!tc23234) 
		//Assert.assertTrue(false, "Not continuing with TC#23235 as 23234 failed");
	 //else{
	 Common.portalLogin();
	 driver.findElement(By.linkText("Manage Service Authorizations")).click();
	 driver.findElement(By.linkText("Pre-Admission Screening")).click();
	 driver.findElement(By.linkText("Enter PAS Request")).click();
	 driver.findElement(By.xpath("//select[contains(@id, 'baseInfo_assignment')]")).click();
	 new Select(driver.findElement(By.xpath("//select[contains(@id, 'baseInfo_assignment')]"))).selectByVisibleText("REG TESTING");
	 String newAssgnPortal=driver.findElement(By.xpath("//select[contains(@id, 'baseInfo_assignment')]")).getText().trim();
	 Assert.assertTrue(driver.findElement(By.xpath("//select[contains(@id, 'baseInfo_assignment')]")).getText().trim().equals(newAssgnPortal),"Validating Assignment Maintenance add Assignment in portal failed");
	 log("Validate Assignment Maintenance in portal is done");
	  //  }
	 }
     
     
	 @Test
	 //Validate Reason Maintenance Discontinue/Add Codes
	 public static void test23236() throws Exception{
	 TestNGCustom.TCNo="23236";
	 log("//TC 23236");
	 updateReasoncode("efftoday");
     Common.cancelAll();
     log("Validating Reason Maintenance Discontinue/Add Codes is done");
	 }
	
	
	public void search(String name){
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:MainPasSearchBean_CriteriaPanel:NEW")).click();
	driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmDataPanel_FacilityProvIDSearch_CMD_SEARCH']/img")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchCriteriaPanel:Name")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchCriteriaPanel:Name")).sendKeys(name);
	}
	
	
	public void afterSrch(){
    driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchCriteriaPanel:SEARCH")).click();
	String Provid=driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchResults_0:column1Value")).getText().trim();
	Common.cancelAll();
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Provider")).click();
    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
    driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:ProviderIdPopUpSearchControl_providermainsearch")).sendKeys(Provid);
	driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderMainSearchBean_CriteriaPanel:SEARCH")).click();	
	}
	
	
	 @Test
	 //Validate Attending Physician provider Search
	 public void test23237() throws Exception{
	 TestNGCustom.TCNo="23237";
	 log("//TC 23237");
	 search("METROWEST");
	 //new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchCriteriaPanel:Gender"))).selectByVisibleText("Organization");
	 new Select(driver.findElement(By.xpath("//select[@id='MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchCriteriaPanel:Gender']"))).selectByVisibleText("Organization");
	 afterSrch();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[7]/td[2]")).getText().trim().equals("Organization"),"Validating the Attending Physician search failed");
	 log("Validate Attending Physician provider Search is done");
	 }
	
	
	 @Test
	 //Validate the Facility ID Search function included tc#23241
	 public void test23238() throws Exception{
	 TestNGCustom.TCNo="23238";
	 log("//TC 23238 & TC 23241");
	 search("METROWEST");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchCriteriaPanel:City")).sendKeys("FRAMINGHAM");
	 afterSrch();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:ProviderSearchResults_0:_id16")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[8]/td[2]")).getText().trim().equals("MARLBOROUGH"),"Validating the city in Facility Providerid search failed");
	 log("Validate the Facility ID Search function is done");
	 }
	
	
	 @Test
	 //Validate the Requesting Provider Search function
	 //including tc#23243
	 public void test23239() throws Exception{
	 TestNGCustom.TCNo="23239";
	 log("//TC 23239 & TC 23243");
	 search("METROWEST");
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchCriteriaPanel:Specialty"))).selectByVisibleText("MEDICAL/SURGICAL ICU");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchCriteriaPanel:SEARCH")).click();
	 Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchResults_0:column5Value")).getText().trim().equals("MEDICAL/SURGICAL ICU"),"Validating the speciality in Requesting Providerid search failed");
	 afterSrch();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ProviderSearchResults:tbody_element']/tr[1]/td[4]")).getText().trim().contains("METROWEST"),"Validating the name in Requesting Providerid search failed");
	 log("Validate the Requesting Provider Search function is done. Including TC#23243");
	 }
	
	
	@Test
	 //Validate the Requesting Provider NPI Search function
	 public void test23242() throws Exception{
	 TestNGCustom.TCNo="23242";
	 log("//TC 23242");
	 search("METROWEST");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchCriteriaPanel:ZipCode")).sendKeys("01701");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchCriteriaPanel:SEARCH")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchResults:header3Text")).click();
	 String npi=driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchResults_0:column3Value")).getText().trim();
	 System.out.println("NPI: "+npi);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:_id51:ServiceAuthorizationProviderSearchResults_0:column1Value")).click();
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[7]/td[2]")).getText().trim().contains(npi),"Validating the Requesting Provider NPI Search function failed");
	 log("Validate the Requesting Provider NPI Search function is done");
	 }
	
	
	 @Test
	 //PAS admission date after member eligibility ends (inactive)
	 public static void test23244() throws Exception{
	 TestNGCustom.TCNo="23244";
	 log("//TC 23244");
	 sqlStatement ="select id_medicaid from t_re_base where sak_recip=(select sak_recip from T_RE_AID_ELIG where dte_end< to_char(sysdate,'YYYYMMDD')and rownum=1)and rownum<2";
	 submitPAS(sqlStatement,"23244",Common.convertSysdate());
	 lineItem("23244","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdate(),"35");
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td[2]")).getText().trim().contains("Warning! Member not eligible on Submission Date!"),"Validating the admission date after member eligibility ends (inactive) failed");
	 log("Validating PAS admission date after member eligibility ends warning message is done");
	 }
	 
	
	@Test
	 //Validate Out of State PAS request is routed to MassHealth
	 public void test23252a() throws Exception{
	 TestNGCustom.TCNo="23252a";
	 log("//TC 23252");
	 outOfStateReason="Family or Friend Recommendation";
	 submitPAS(sqlSt,"23252",Common.convertSysdatecustom(-105));
	 lineItem("23252","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdatecustom(-105),"35");
     Common.save();
	 String pasnum=notePASNum("23252");
	 SelSql="select * from R_PAS where tcno = '23252a'";
	 col="PASNUM";
	 DelSql="delete from R_PAS where tcno = '23252a'";  
	 InsSql="insert into  R_PAS values ('23252a',' ',' ',' ',' ',' ',' ',' ',' ',' ','"+pasnum+"')";
	 Common.insertData(SelSql, col, DelSql, InsSql);	
	 tc23252a=true;
	 }
	
	@Test
	 //Validate Out of State PAS request is routed to MassHealth,Checking PAS num in the Worklist
	 public void test23252b() throws Exception{
	 TestNGCustom.TCNo="23252b";
	 if (!tc23252a) 
			Assert.assertTrue(false, "Not continuing with TC#23252b as 23252a failed");
	 else{
	 
	 gotoWorkflow(getPasNum("23252a"));
		Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasSearchResults:tbody_element']/tr/td[9]/span")).getText().equals("OS"));
		log("Verification for out of state PAS in worklist is done.");

	    } 
	 }
	
	
	 @Test
	 //Validate PAS Chronic Administrative Day Route to MassHealth
	 public void test23253a() throws Exception{
	 TestNGCustom.TCNo="23253a";
	 log("//TC 23253");
	 submitPAS(sqlSt,"23253",Common.convertSysdate());
	 lineItem("23253","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdate(),"36");
     Common.save();
	 String pasnum=notePASNum("23253");
	 SelSql="select * from R_PAS where tcno = '23253a'";
	 col="PASNUM";
	 DelSql="delete from R_PAS where tcno = '23253a'";  
	 InsSql="insert into  R_PAS values ('23253a',' ',' ',' ',' ',' ',' ',' ',' ',' ','"+pasnum+"')";
	 Common.insertData(SelSql, col, DelSql, InsSql);	
	 tc23253a=true;
	 }
	 
	 
	 @Test
	 //Validate PAS Chronic Administrative Day Route to MassHealth,Checking PAS num in the Worklist
	 public void test23253b() throws Exception{
	 TestNGCustom.TCNo="23253b";
	 if (!tc23253a) 
			Assert.assertTrue(false, "Not continuing with TC#23253b as 23253a failed");
	 else{
	 gotoWorkflow(getPasNum("23253a"));
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasSearchResults:tbody_element']/tr/td[9]/span")).getText().equals("CR"));
		log("Verification for chronic PAS in worklist is done.");
	    } 
	 }
	 
	 @Test
	 //Validate PAS Conversion request is routed to MassHealth
	 public void test23254() throws Exception{
	 TestNGCustom.TCNo="23254";
	 log("//TC 23254");
	 submitPAS(sqlSt,"23254",Common.convertSysdate());
	 lineItem("23254","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdate(),"30");
     Common.save();
	 notePASNum("23254");
	 }
	 
	 
	 @Test
	 //Validate that retro PAS request is routed to MassHealth
	 public void test23256a() throws Exception{
	 TestNGCustom.TCNo="23256a";
	 log("//TC 23256");
	 retroConvReason="Other";
	 submitPAS(sqlSt,"23256",Common.convertSysdate());
	 lineItem("23256","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdate(),"35");
	 Common.save();
	 String pasnum=notePASNum("23256");
	 SelSql="select * from R_PAS where tcno = '23256a'";
	 col="PASNUM";
	 DelSql="delete from R_PAS where tcno = '23256a'";  
	 InsSql="insert into  R_PAS values ('23256a',' ',' ',' ',' ',' ',' ',' ',' ',' ','"+pasnum+"')";
	 Common.insertData(SelSql, col, DelSql, InsSql);	
	 tc23256a=true;
	 }
	 
	 
	 @Test
	 //PAS Acute goes to MassHealth,Checking PAS num in the Worklist
	 public void test23256b() throws Exception{
	 TestNGCustom.TCNo="23256b";
	 if (!tc23256a) 
		Assert.assertTrue(false, "Not continuing with TC#23256b as 23256a failed");
	 else{
	 gotoWorkflow(getPasNum("23256a"));
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasSearchResults:tbody_element']/tr/td[9]/span")).getText().equals("CR"));
		log("Verification for retro chronic PAS in worklist is done.");
	    } 
	 }
	 
	 
	 
	 @Test
	 //PAS Acute goes to MassHealth
	 public void test23599a() throws Exception{
	 TestNGCustom.TCNo="23599a";
	 log("//TC 23599");
	// submitPAS(sqlSt,"23599",Common.convertSysdate());
	// lineItem("23599","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdate(),"35");
	 submitPAS(sqlSt,"23599",Common.convertSysdatecustom(-100));
	 lineItem("23599","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdatecustom(-100),"35");
     Common.save();
	 String pasnum=notePASNum("23599");
	 SelSql="select * from R_PAS where tcno = '23599a'";
	 col="PASNUM";
	 DelSql="delete from R_PAS where tcno = '23599a'";  
	 InsSql="insert into  R_PAS values ('23599a',' ',' ',' ',' ',' ',' ',' ',' ',' ','"+pasnum+"')";
	 Common.insertData(SelSql, col, DelSql, InsSql);	
	 tc23599a=true;
	 }
	 
	 @Test
	 //PAS Acute goes to MassHealth,Checking PAS num in the Worklist
	 public void test23599b() throws Exception{
	 TestNGCustom.TCNo="23599b";
	 if (!tc23599a) 
			Assert.assertTrue(false, "Not continuing with TC#23599b as 23599a failed");
	 else{
		// Thread.sleep(200000);	 
	 gotoWorkflow(getPasNum("23599a"));
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasSearchResults:tbody_element']/tr/td[9]/span")).getText().equals("RT"));
		log("Verification for retro Acute PAS in worklist is done.");
	    } 
	 }
	 
	 
	 public void gotoWorkflow(String pasNum){
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Worklist")).click();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorklistSearchBean_CriteriaPanel:PasWorklistSearchResultDataPanel_PasNumber")).sendKeys(pasNum);
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorklistSearchBean_CriteriaPanel:SEARCH")).click();
	 
//	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:_id12")).click();
//	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults:WorklistSortedHeader0")).click();
//	 driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults:WorklistSortedHeader0")).click();
//	 int rownum =driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults:tbody_element']/tr")).size();
//	 for(i=1;i<=rownum;i++)
//	    {
//	    if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults:tbody_element']/tr["+i+"]/td[2]")).getText().contains(pasNum)){
//	    	System.out.println("j value: "+j);
//	    	break;
//	        }
//	    
//	    else if(i==10){
//	    	driver.findElement(By.id("MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults_DataScrollernext")).click();
//	    	 for(j=1;j<=rownum;j++)
//	 	    {
//	 	    if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasWorkflowWorklistPanel:WorkflowWorklistResults:tbody_element']/tr["+j+"]/td[2]")).getText().contains(pasNum)){
//	 	    	System.out.println("j value: "+j);
//	 	    	break;
//	 	        }
//	       }
//	    }
//	    }
	   
	    	
	    
	    
	 
	 
	
	
 	 }
	 
	 
	 public String getPasNum(String tcNo) throws SQLException{
	 String sql="select * from r_pas where TCNO='"+tcNo+"'";
	 colNames.add("PASNUM");
	 colValues=Common.executeQuery1(sql,colNames);
	 System.out.println("PAS# from table: "+colValues.get(0));
	 return colValues.get(0);
	 }
	 
	 
	 public String notePASNum(String tcNo) throws IOException{
	 String warMessage=(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[3]/td[2]")).getText().trim());
	 String pasNum = warMessage.substring(30,41);
	 System.out.println("PAS# from tc#"+tcNo+": "+pasNum);
	 log("PAS num:" +pasNum);
	 return pasNum;
		 }
		
	
	 @Test
	 //PAS Acute w/ Rehab goes to MassHealth
	 public void test23601a() throws Exception{
	 TestNGCustom.TCNo="23601a";
	 log("//TC 23601a");
//	 submitPAS(sqlSt,"23601",Common.convertSysdate());
//	 lineItem("23601","Acute AD per Diem",Common.convertSysdate(),"35");
	 submitPAS(sqlSt,"23601",Common.convertSysdate());
	 lineItem("23601","Acute AD per Diem",Common.convertSysdate(),"35");
     Common.save();
	 String pasnum=notePASNum("23601");
	 //Naming 23601 while creating PAS as data in the table is with 23601 and while entering PAS num intp same table,r_pas,it will 23601 row before entering pas num
	 SelSql="select * from R_PAS where tcno = '23601a'";
	 col="PASNUM";
	 DelSql="delete from R_PAS where tcno = '23601a'";  
	 InsSql="insert into  R_PAS values ('23601a',' ',' ',' ',' ',' ',' ',' ',' ',' ','"+pasnum+"')";
	 Common.insertData(SelSql, col, DelSql, InsSql);	
	 tc23601a=true;
	 }
	 
	 
	 @Test
	 //PAS Acute w/ Rehab goes to MassHealth,Checking PAS num in the Worklist
	 public void test23601b() throws Exception{
	 TestNGCustom.TCNo="23601b";
	 if (!tc23601a) 
			Assert.assertTrue(false, "Not continuing with TC#23601b as 23601a failed");
	 else{
	 
	 gotoWorkflow(getPasNum("23601a"));
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasSearchResults:tbody_element']/tr/td[9]/span")).getText().equals("-"));
		log("Verification for retro Acute PAS in worklist is done.");
	    } 
	 }
	 
	 
	 @Test
	 //PAS Chronic w/ Rehab goes to MassHealth
	 public void test23602a() throws Exception{
	 TestNGCustom.TCNo="23602a";
	 log("//TC 23602a");
	 submitPAS(sqlSt,"23602",Common.convertSysdate());
	 lineItem("23602","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdate(),"35");
	// submitPAS(sqlSt,"23602",Common.convertSysdatecustom(-100));
	// lineItem("23602","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdatecustom(-100),"35");
     Common.save();
	 String pasnum=notePASNum("23602");
	 SelSql="select * from R_PAS where tcno = '23602a'";
	 col="PASNUM";
	 DelSql="delete from R_PAS where tcno = '23602a'";  
	 InsSql="insert into  R_PAS values ('23602a',' ',' ',' ',' ',' ',' ',' ',' ',' ','"+pasnum+"')";
	 Common.insertData(SelSql, col, DelSql, InsSql);	
	 tc23602a=true;
	 }
	 
	 @Test
	 //PAS Chronic w/ Rehab goes to MassHealth,Checking PAS num in the Worklist
	 public void test23602b() throws Exception{
	 TestNGCustom.TCNo="23602b";
	 if (!tc23602a) 
			Assert.assertTrue(false, "Not continuing with TC#23602b as 23602a failed");
	 else{
	 
	 gotoWorkflow(getPasNum("23602a"));
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasSearchResults:tbody_element']/tr/td[9]/span")).getText().equals("CR"));
		log("Verification for retro chronic PAS in worklist is done.");
	    } 
	 }
	 
	 
	 @Test
	 //Chronic PAS txn = Y and has MH review criteria
	 public void test23603a() throws Exception{
	 TestNGCustom.TCNo="23603a";
	 log("//TC 23603a");
	 trxInd="Yes";
	 submitPAS(sqlSt,"23603",Common.convertSysdate());
	 lineItem("23603","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdate(),"35");
	// submitPAS(sqlSt,"23603",Common.convertSysdatecustom(-100));
	// lineItem("23603","Chronic/Rehab and Acute with Rehab AD",Common.convertSysdatecustom(-100),"35");
     Common.save();
	 String pasnum=notePASNum("23603");
	 SelSql="select * from R_PAS where tcno = '23603a'";
	 col="PASNUM";
	 DelSql="delete from R_PAS where tcno = '23603a'";  
	 InsSql="insert into  R_PAS values ('23603a',' ',' ',' ',' ',' ',' ',' ',' ',' ','"+pasnum+"')";
	 Common.insertData(SelSql, col, DelSql, InsSql);	
	 tc23603a=true;
     }
	 
	 
	 @Test
	 //Chronic PAS txn = Y and has MH review criteria,Checking PAS num in the Worklist
	 public void test23603b() throws Exception{
	 TestNGCustom.TCNo="23603b";
	 if (!tc23603a) 
			Assert.assertTrue(false, "Not continuing with TC#23603b as 23603a failed");
	 else{
	 gotoWorkflow(getPasNum("23603a"));
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasSearchResults:tbody_element']/tr/td[9]/span")).getText().equals("CR"));
		log("Verification for chronic PAS in worklist is done.");
	    } 
	 }
	 
	 
	 
	 @Test
	 //Acute PAS txn = Y and has MH review criteria
	 public void test23604a() throws Exception{
	 TestNGCustom.TCNo="23604a";
	 log("//TC 23604a");
	 trxInd="Yes";
	 submitPAS(sqlSt,"23604",Common.convertSysdate());
	 lineItem("23604","Acute HLOC per Diem",Common.convertSysdate(),"35");
	// submitPAS(sqlSt,"23604",Common.convertSysdatecustom(-100));
	// lineItem("23604","Acute HLOC per Diem",Common.convertSysdatecustom(-100),"35");
     Common.save();
	 String pasnum=notePASNum("23604");
	 SelSql="select * from R_PAS where tcno = '23604a'";
	 col="PASNUM";
	 DelSql="delete from R_PAS where tcno = '23604a'";  
	 InsSql="insert into  R_PAS values ('23604a',' ',' ',' ',' ',' ',' ',' ',' ',' ','"+pasnum+"')";
	 Common.insertData(SelSql, col, DelSql, InsSql);	
	 tc23604a=true;
     }
	 
	 
	 @Test
	 //Acute PAS txn = Y and has MH review criteria,Checking PAS num in the Worklist
	 public void test23604b() throws Exception{
	 TestNGCustom.TCNo="23604b";
	 if (!tc23604a) 
			Assert.assertTrue(false, "Not continuing with TC#23604b as 23604a failed");
	 else{
	 gotoWorkflow(getPasNum("23604a"));
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:PasSearchResults:tbody_element']/tr/td[9]/span")).getText().equals("-"));
		log("Verification for acute PAS in worklist is done.");
	 
	    } 
	 }
	 
	 
	 @Test
	 //Acute Hosp and  Acute Admin Day Level of Care on same PAS with no consecutive dates 
	 public void test23836() throws Exception{
	 TestNGCustom.TCNo="23836";
	 log("//TC 23836");
	 retroConvReason="Other";
	 submitPAS(sqlSt,"23836a",Common.convertSysdate());
	 lineItem("23836a","Acute AD per Diem",Common.convertSysdate(),"35");
	 Common.save();
	 String pasnum=notePASNum("23836");
	 System.out.println("PAS num from TC#23836: "+pasnum);
	 Thread.sleep(12000);
	 changeStatus("0","IN REVIEW");
	 changeStatus("0","APPROVED");
	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_LetterIndicator"))).selectByVisibleText("No Print");
	 lineItem("23836b","Acute HLOC per Diem",Common.convertSysdatecustom(36),"1");
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_LengthOfStayDays")).clear();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:PreAdmScreeningPanel:PreAdmScreeningDataPanel_LengthOfStayDays")).sendKeys("400");
     Common.save();
     Thread.sleep(12000);
     changeStatus("0","IN REVIEW");
     changeStatus("0","APPROVED");
     }
	 
	 
	
	
	
	 public static void submitPASPortal(String tcNo) throws SQLException{
		 if(!(tcNo.equals("23255a"))){
	     driver.findElement(By.linkText("Manage Service Authorizations")).click();
		 driver.findElement(By.linkText("Pre-Admission Screening")).click();
		 driver.findElement(By.linkText("Enter PAS Request")).click();
		 }
		// driver.findElement(By.id("enterPasBaseInfoTab:enterPasRequest_2_id3:baseInfo_assignment")).click();
		 new Select(driver.findElement(By.xpath("//select[contains(@id, 'baseInfo_assignment')]"))).selectByVisibleText("CHRONIC DISEASE/REHAB");
		 new Select(driver.findElement(By.xpath("//select[contains(@id, 'baseInfo_requestingProvider')]"))).selectByValue("110047258A");
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_contactName')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_contactName')]")).sendKeys("John");
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_contactPhone')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_contactPhone')]")).sendKeys("6176895584");
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_providerContactFax')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_providerContactFax')]")).sendKeys("6176895584");
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityMassHealthId')]")).clear();
	     driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityMassHealthId')]")).sendKeys("110024350B");
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityContactName')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityContactName')]")).sendKeys("Smith");
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityContactPhone')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityContactPhone')]")).sendKeys("6176895584");
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityContactFax')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_facilityContactFax')]")).sendKeys("6176895584");
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_memberID')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_memberID')]")).sendKeys(memberId);
		 new Select(driver.findElement(By.xpath("//select[contains(@id, 'baseInfo_accidentIndicator')]"))).selectByVisibleText("No");
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_admissionDate')]")).sendKeys(Common.convertSysdate());
		 driver.findElement(By.xpath("//input[contains(@id, 'lengthOfStay')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'lengthOfStay')]")).sendKeys("15");
		// new Select(driver.findElement(By.xpath("//select[contains(@id, 'retroConversionReason')]"))).selectByVisibleText("Member Admitted As Self Pay"); //icd9
		// driver.findElement(By.xpath("//*[contains(@id, 'icdVersion:0')]")).click();//icd9
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_primaryDiagnosisCode')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'baseInfo_primaryDiagnosisCode')]")).sendKeys("R071");//78652    S2239XA
		 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_PASAddUpdLineItems')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'lineItemsList:new_Button')]")).click();
		 //driver.findElement(By.id("enterPasLineItemsTab:enterPasRequest_2_id3:enterPasRequest_2_id4:enterPasRequest_2_id5:requestedCareLevel")).click();
		 new Select(driver.findElement(By.xpath("//select[contains(@id, 'requestedCareLevel')]"))).selectByVisibleText("CHRONIC/REHAB - ADMIN DAY LEVEL OF CARE");
		 new Select(driver.findElement(By.xpath("//select[contains(@id, 'requestedAdmissionType')]"))).selectByVisibleText("Medical");
		 driver.findElement(By.xpath("//input[contains(@id, 'requestedEffDate')]")).sendKeys(Common.convertSysdate());
		 driver.findElement(By.xpath("//input[contains(@id, 'requestedDays')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'requestedDays')]")).sendKeys("15");
		 driver.findElement(By.xpath("//input[contains(@id, 'add_Button')]")).click();
		 driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_PASAddUpdConfirm')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'submit')]")).click(); 
		 driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		 
		 if(driver.findElements(By.xpath("//*[contains(@id, 'ignoreWarnings')]")).size()>0){
			 
			 driver.findElement(By.xpath("//*[contains(@id, 'ignoreWarnings')]")).click();
			 driver.findElement(By.xpath("//input[contains(@id, 'submit')]")).click(); 
		     
		 }
		 
	
		 
		 }
		 
	 @Test
	 //Validate duplicate PAS request message through web portal 
	 public static void test23255() throws Exception{
	 TestNGCustom.TCNo="23255";
	 log("//TC 23255");
	 sqlStatement ="select id_medicaid from t_re_base where ind_active = 'Y' and sak_recip > dbms_random.value * 6300000 and rownum < 2 and id_medicaid <> '100015789199' and id_medicaid <> '100031986324'";
	 	colNames.add("id_medicaid");
	 	colValues=Common.executeQuery(sqlStatement, colNames );
	 	memberId=colValues.get(0);
	 	System.out.println("Memberid: "+memberId);
	 Common.portalLogin();
	 submitPASPortal("23255");
	 String pasNum=driver.findElement(By.xpath("//span[contains(@id, 'baseInfo_pasNumberText')]")).getText().trim();
	 System.out.println("PAS num from TC#23255: "+pasNum);
	 log("PAS num: "+pasNum);
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='pasConfirmationResponse']/table[1]/tbody/tr[2]/td/span[3]")).getText().trim().contains("successfully submitted"),"The PAS is not successfully sumitted");
	 driver.findElement(By.xpath("//input[contains(@id, 'enterAnotherPreAdmissionScreening')]")).click();
	 submitPASPortal("23255a");
	 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='enterPasLineItemsTab']/div[1]/table[2]/tbody/tr[1]/td[2]")).getText().trim().contains("Duplicate on PAS Number"),"Validating for Duplicate PAS failed");
	 }
	 
	 
	
	 
	
	
}	
	
	
	


