package newMMIS_Subsystems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ newMMIS_Subsystems.TestNGCustom.class })
public class TPL extends Login{
	
	public String SelSql, col, DelSql, InsSql;
	
	//Recovery testing variables
	public static String check_eft_ind="C";
	public static String prov_carrier_ind="C";     
	public static String num_check="";
	public static String num_wire_transfer="";
	public static String name_on_check=""; 
	public static String amt_paid="";
	public static String nam_remitter="";
	public static String dte_check_numwire="";
	public static String id_provider="";
	public static String cde_service_loc="";
	public static String cde_carrier="";
	public static String cde_project="BPSA"; 
	public static String cde_unit="HMS1";
	public static String cde_dept="TPL1";
	public static String num_ref="";
			
	public static String associate_payment_ind="Y";
	public static String icn_tcn_ind="I";
	public static String num_icn_tcn="";
	public static String num_dtl="";
	public static String num_tpl_policy="";
	public static String id_medicaid="";
	public static String clm_dtl_recovery_amt="";
	public static String cde_pgm_health="OI01";
	public static String dte_first_svc="";
	public static String dte_last_svc="";
	public static String cde_reason_two="08";
	
	public static String cde_clm_adj_reason="2";
	public static String cde_clm_adj_group="PR";
	public static String amt_adjustment="";
	
	public static String txnType="HMS";
	public static double hmsGDG=0.0000;
	public static double accsGDG=0.0000;
	public static String recoveryFile="";
	public static String ExistingRecoveryICNs="2020085723022"; // random ICN to start with
	public static String fetchedICN="2020085723022"; // random ICN to start with
	
	public String recoveryFileContents;
	
	//recovery_HMS_SOHEMA variables
	public static String res_mem="";
	public static String resource_identifier="-1";
	public static String res_carrier="";
	public static String cde_employer="";
	public static String cde_policy_type="I";
	public static String num_tpl_policy_res="";
	public static String nam_plan=" ";
	public static String cde_relation="18";
	public static String ind_med_support="Y";
	public static String dte_med_support_begin="";
	public static String dte_med_support_end="2299-12-31";
	public static String cde_suspect="1";
	
	public static String coverage_sequence="-1";
	public static String oi_plan_identifier="";
	public static String cov_dte_effective="";
	public static String cov_dte_end="";
	public static String cde_pgm_health_res_cov="";
	
	public static String policy_owner_identifier="0";
	public static String owner_nam_last="";
	public static String owner_nam_first="";
	public static String owner_adr_mail_strt1="17 FULLER RD";
	public static String owner_adr_mail_city="FOXBORO";
	public static String owner_num_ssn="";
	public static String owner_dte_birth="";
	
	public static String txnType_res="HMS";
	public static double hmsGDG_TPLresource=0.0000;
	public static double sohemaGDG_TPLresource=0.0000;
	public static String resourceFile="";

	public String resourceFileContents;
	public static int resource_hmsGDGcount=0;
	public static int resource_sohemaGDGcount=0;




	
	@BeforeTest
    public void tplStartup() throws Exception {
    	log("Starting TPL Subsystem......");
    }
	
	@BeforeMethod
	public void LoginCheck() throws Exception {
		Common.resetBase();
		testCheckDBLoginSuccessful();	
		resetValues();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	}
	
    @Test
    public void test22455a() throws Exception{
    	TestNGCustom.TCNo="22455a";
    	log("//TC 22455a");
    	
    	String target;
    	String fieldName;
    	Boolean textProperty;
    	
    	searchTPLMem("100004650981");
    	
    	target="//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[3]/td[2]";
    	fieldName = "Member Name";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);
    	
    	target="//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[3]/td[2]";
    	fieldName = "Carrier Name";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);

    	//Coverage
        driver.findElement(By.id("MMISForm:MMISBodyContent:TplNavigatorPanel:TPLNavigator:ITM_n103")).click();
    	for (int i=0;i<3;i++){
        	target="//span[@id='MMISForm:MMISBodyContent:CoveragePanel:CoverageList_"+i+":CoverageXrefBean_ColValue_otherInsurancePlanCode']";
        	fieldName = "OI plan no."+(i+1);
        	textProperty = true;
        	verifyField(target, fieldName, textProperty);
    	}
    	
    	//Dependents of policy
        driver.findElement(By.id("MMISForm:MMISBodyContent:TplNavigatorPanel:TPLNavigator:ITM_n104")).click();
        
    	target="//span[@id='MMISForm:MMISBodyContent:DependentsofPolicyPanel:DependentsofPolicyList_0:DependentsofPolicyBean_ColValue_savedStatus']";
    	fieldName = "Dependents of policy: Record Status:";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);
        
    	target="//span[@id='MMISForm:MMISBodyContent:DependentsofPolicyPanel:DependentsofPolicyList_0:DependentsofPolicyBean_ColValue_medicaidID']";
    	fieldName = "Dependents of policy: Member:";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);
    	
    	target="//span[@id='MMISForm:MMISBodyContent:DependentsofPolicyPanel:DependentsofPolicyList_0:DependentsofPolicyBean_ColValue_lastName']";
    	fieldName = "Dependents of policy: Member Last Name:";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);
    	
    	target="//span[@id='MMISForm:MMISBodyContent:DependentsofPolicyPanel:DependentsofPolicyList_0:DependentsofPolicyBean_ColValue_firstName']";
    	fieldName = "Dependents of policy: Member First Name:";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);
    	
    	target="//span[@id='MMISForm:MMISBodyContent:DependentsofPolicyPanel:DependentsofPolicyList_0:DependentsofPolicyBean_ColValue_ssnNumber']";
    	fieldName = "Dependents of policy: Member SSN:";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);
    	
    	target="//span[@id='MMISForm:MMISBodyContent:DependentsofPolicyPanel:DependentsofPolicyList_0:DependentsofPolicyBean_ColValue_birthDate']";
    	fieldName = "Dependents of policy: Member DoB:";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);
    	
    	//Additional policies- Already have TC 23310 for it.
    	
    	//Related Data - HIPAA Service/Coverage
    	Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataXref")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n45")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:HIPAAServiceCoverageTypePanel:HIPAAServiceCoverageTypeSearchResults_0:HipaaCoverageXrefBean_ColValue_status")).click();
	 	
	 	Assert.assertTrue(driver.findElement(By.cssSelector("h3.panel-header")).getText().trim().equals("HIPAA Service-Coverage Maintenance"), "HIPAA Service-Coverage Maintenance Panel did not open");

	 	target="//input[@id='MMISForm:MMISBodyContent:HIPAAServiceCoverageTypePanel:HipaaCoverageXref_CoverageType']";
    	fieldName = "HIPAA Service/Coverage: Coverage Type:";
    	textProperty = false;
    	verifyField(target, fieldName, textProperty);
    	
    	target="//input[@id='MMISForm:MMISBodyContent:HIPAAServiceCoverageTypePanel:HipaaCoverageXref_SvcType']";
    	fieldName = "HIPAA Service/Coverage: HIPAA Service Type:";
    	textProperty = false;
    	verifyField(target, fieldName, textProperty);
    	
    	target="//textarea[@id='MMISForm:MMISBodyContent:HIPAAServiceCoverageTypePanel:HipaaCoverageXrefDataPanel_ServTypeDescription']";
    	fieldName = "HIPAA Service/Coverage: HIPAA Service Description:";
    	textProperty = false;
    	verifyField(target, fieldName, textProperty);
    	
    	//Related Data - Carrier
    	//Search by name
    	Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n22")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n22")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierBean_CriteriaPanel:TplCarrierDataPanel_BusName")).sendKeys("BLUE CROSS BLUE SHIELD OF MA");
	 	Common.search();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierList_0:TplCarrierBean_ColValue_status")).click();
	 	
	 	//Search by id
    	Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n22")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n22")).click();
//	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierBean_CriteriaPanel:TplCarrierDataPanel_CarrierCode")).sendKeys("0027033");
//	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierBean_CriteriaPanel:TplCarrierDataPanel_CarrierCode")).sendKeys("0986102");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierBean_CriteriaPanel:TplCarrierDataPanel_CarrierCode")).sendKeys("0001002");

	 	Common.search();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierList_0:TplCarrierBean_ColValue_status")).click();
	 	
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierBean_DataPanel']/thead/tr/td/table/tbody/tr/td/h3")).getText().trim().equals("Carrier Maintenance"), "Carrier Maintenance Panel did not open");
    	
	 	target="//input[@id='MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_CarrierCode']";
    	fieldName = "Carrier Number";
    	textProperty = false;
    	verifyField(target, fieldName, textProperty);
    	
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TplCarrierPanel:TplCorrAddressBean_DataPanel']/thead/tr/td/table/tbody/tr/td/h3")).getText().trim().equals("Correspondence Address Maintenance"), "Correspondence Address Maintenance Panel did not open");
    	
	 	target="//input[@id='MMISForm:MMISBodyContent:TplCarrierPanel:Address_1']";
    	fieldName = "Carrier Coresspondence Address: Address 1";
    	textProperty = false;
    	verifyField(target, fieldName, textProperty);
    	
	 	target="//input[@id='MMISForm:MMISBodyContent:TplCarrierPanel:Address_3']";
    	fieldName = "Carrier Coresspondence Address: City";
    	textProperty = false;
    	verifyField(target, fieldName, textProperty);
    	
	 	target="//select[@id='MMISForm:MMISBodyContent:TplCarrierPanel:Address_4']/option[@selected='selected']";
    	fieldName = "Carrier Coresspondence Address: State";
    	textProperty = false;
    	verifyField(target, fieldName, textProperty);
    	
	 	target="//input[@id='MMISForm:MMISBodyContent:TplCarrierPanel:Address_5']";
    	fieldName = "Carrier Coresspondence Address: Zip";
    	textProperty = false;
    	verifyField(target, fieldName, textProperty);
    	
    	//Carrier to OI Plan
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div/table/tbody/tr/td/div[2]/table/thead/tr/th/table/tbody/tr/td/h3")).getText().trim().equals("Carrier to OI Plan"), "Carrier to OI Plan Panel did not open");
//    	for (int i=0;i<3;i++){
//        	target="//span[@id='MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierToOIPlanPanel:TplCarrierPlanXrefList_"+i+":TplCarrierToOIPlanBean_ColValue_otherInsurancePlanToCoverage_otherInsurancePlanCode']";
//        	fieldName = "Carrier to OI Plan: OI plan no."+(i+1);
//        	textProperty = true;
//        	verifyField(target, fieldName, textProperty);
//    	}
    	target="//span[@id='MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierToOIPlanPanel:TplCarrierPlanXrefList_0:TplCarrierToOIPlanBean_ColValue_otherInsurancePlanToCoverage_otherInsurancePlanCode']";
    	fieldName = "Carrier to OI Plan: OI plan no. 1";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);
    	
    	//Employers For Carrier Search
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div/table/tbody/tr/td/div[3]/table/thead/tr/th/table/tbody/tr/td/h3")).getText().trim().equals("Employers For Carrier Search"), "Employers For Carrier Search Panel did not open");
	 	Assert.assertTrue(driver.findElement(By.cssSelector("h4.panel-header")).getText().trim().equals("Search Criteria"), "Search Criteria Panel did not open");
	 	
    	target="//span[@id='MMISForm:MMISBodyContent:TplCarrierPanel:EmpCarrierXrefPanel:TplEmpCarrierXrefList_0:TplEmpCarrierXrefBean_ColValue_tplEmployer_employerCode']";
    	fieldName = "Carrier to Employer Xref: ***No records found***";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);
    	
    	//Status
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/div/table/tbody/tr/td/div[4]/table/thead/tr/th/table/tbody/tr/td/h3")).getText().trim().equals("Status"), "Status Panel did not open");

    	target="//span[@id='MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierStatusPanel:TplCarrierStatusList_0:TplCarrierStatusBean_ColValue_status']";
    	fieldName = "Status: Record Status:";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);
    	
    	target="//span[@id='MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierStatusPanel:TplCarrierStatusList_0:TplCarrierStatusBean_ColValue_statusIndicator']";    	
    	fieldName = "Status: Status:";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);
    	
    	target="//span[@id='MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierStatusPanel:TplCarrierStatusList_0:TplCarrierStatusBean_ColValue_effectiveDate']";    	
    	fieldName = "Status:  Effective Date:";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);
    	
    	target="//span[@id='MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierStatusPanel:TplCarrierStatusList_0:TplCarrierStatusBean_ColValue_endDate']";    	
    	fieldName = "Status: End Date:";
    	textProperty = true;
    	verifyField(target, fieldName, textProperty);
	 	
    	
    	//Carriers by Employer
    	Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TPLReportsNavigatorPanel:TPLReportsNavigator:ITM_n100")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CarriersByEmployerPanel:CarriersByEmployerBean_CriteriaPanel:EmployerID")).sendKeys("0008803");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Batch Print']")).click();
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
		
		//Employers by Carrier
    	Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TPLReportsNavigatorPanel:TPLReportsNavigator:ITM_n103")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:EmployersByCarrierPanel:EmployersByCarrierBean_CriteriaPanel:CarrierCodeID")).sendKeys("0027000");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Batch Print']")).click();
		message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
		
		//Carriers by member
    	Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TPLReportsNavigatorPanel:TPLReportsNavigator:ITM_n101")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CarriersByRecipientPanel:CarriersByRecipientBean_CriteriaPanel:MemberID")).sendKeys("100004650981");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Batch Print']")).click();
		message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
		
		//Members By Carrier
    	Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TPLReportsNavigatorPanel:TPLReportsNavigator:ITM_n104")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientsByCarrierPanel:RecipientsByCarrierBean_CriteriaPanel:CarrierCodeID")).sendKeys("0027033");
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Batch Print']")).click();
		message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
		

		
    }
    
    @Test
    public void test22456() throws Exception{
    	TestNGCustom.TCNo="22456";
    	log("//TC 22456");

    	//Get TPL member
    	sqlStatement = "select /*+RULE*/ b.id_medicaid, b.num_ssn, h.nam_first, h.nam_last, b.nam_first NAM_FIRST_M, b.nam_last NAM_LAST_M from t_tpl_resource r, t_re_base b, t_policy_holder h, t_coverage_xref xref"+
    				   " where r.sak_recip = b.sak_recip  and h.sak_pol_hold = r.sak_policy_ownr and b.ind_active = 'Y' and b.num_ssn <> ' ' and r.sak_tpl_resource = xref.sak_tpl_resource and xref.dte_end = 22991231 and b.nam_mid_init = ' '"+
    				   " and exists (select r1.sak_recip from t_tpl_resource r1 where r1.sak_recip = r.sak_recip group by r1.sak_recip having count(r1.sak_recip) = 1 )"+
    				   " and exists (select r2.sak_policy_ownr from t_tpl_resource r2 where r2.sak_policy_ownr = r.sak_policy_ownr group by r2.sak_policy_ownr having count(r2.sak_policy_ownr) > 5 )"+
    				   " and b.num_ssn not in ('011848497', '033866952', '011888601', '028532656', '028534685', '028489467', '028496483', '028706596', '029054208', '028712203', '028678192')"+ //Excluding these SSNs as they have multiple members with same ssn. Creating problems for script.
    				   " and b.sak_recip > dbms_random.value * 6300000 " +
    				   " and rownum < 2";
    	colNames.add("ID_MEDICAID"); //0
    	colNames.add("NUM_SSN"); 	//1
    	colNames.add("NAM_FIRST"); 	//2
    	colNames.add("NAM_LAST"); 	//3
    	colNames.add("NAM_FIRST_M");	//4
    	colNames.add("NAM_LAST_M"); 	//5
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	
    	log(" TPL Member: "+colValues.get(0)+" SSN: "+colValues.get(1)+" FName: "+colValues.get(2)+" LName: "+colValues.get(3)+" PolFname "+colValues.get(4)+" PolLname "+colValues.get(5));
    	
    	//Search by member ID
    	searchTPLMem(colValues.get(0));
	 	
	 	//Select the member and get TPL info
	 	String idMem = driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[2]/td[2]")).getText();
	 	String numPol = driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[2]/td[2]")).getText();
	 	String  numCarrier= driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[2]/td[2]")).getText();
	 	String  nameMember= driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[3]/td[2]")).getText();
	 	String  namePolHold= driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[3]/td[2]")).getText();
	 	String  nameCarrier= driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[3]/td[2]")).getText();
	 	String  ssnMem= driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[4]/td[2]")).getText();
	 	String  ssnPol= driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[4]/td[2]")).getText();
	 	String  minEff= driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[4]/td[2]")).getText();
	 	String  maxEnd= driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[5]/td[2]")).getText();
	 	String  polType= driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[5]/td[2]")).getText();
	 	Common.cancelAll();
	 	
	 	//Search by SSN
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchBean_CriteriaPanel:TplSearchResultDataPanel_RecipientSSN")).sendKeys(colValues.get(1));
	 	Common.search();
	 	Common.searchRecords(colValues.get(0));
	 	
	 	//Verify TPL info
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchResults_0:_id14")).click();
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[2]/td[2]")).getText().equals(idMem), "idMem mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[2]/td[2]")).getText().equals(numPol), "numPol mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[2]/td[2]")).getText().equals(numCarrier), "numCarrier mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[3]/td[2]")).getText().equals(nameMember), "nameMember mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[3]/td[2]")).getText().equals(namePolHold), "namePolHold mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[3]/td[2]")).getText().equals(nameCarrier), "nameCarrier mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[4]/td[2]")).getText().equals(ssnMem), "ssnMem mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[4]/td[2]")).getText().equals(ssnPol), "ssnPol mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[4]/td[2]")).getText().equals(minEff), "minEff mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[5]/td[2]")).getText().equals(maxEnd), "maxEnd mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[5]/td[2]")).getText().equals(polType), "polType mismatch");
	 	Common.cancelAll();
	 	
	 	//Search by Policyholder Name
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchBean_CriteriaPanel:TplSearchResultDataPanel_PolicyholderLastName")).sendKeys(colValues.get(3));
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchBean_CriteriaPanel:TplSearchResultDataPanel_PolicyholderFirstName")).sendKeys(colValues.get(2));
	 	Common.search();
	 	Common.searchRecords(colValues.get(0));
	 	boolean found= false;
	 	for (int i=0;i<5;i++) {
	 		if (driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchResults_"+i+":_id14")).getText().equals(colValues.get(0))) {
	 			driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchResults_"+i+":_id14")).click();
	 			i=5;
	 			found = true;
	 		}
	 	}
	 	if (!found)
		 	Assert.assertTrue(false, "Unable to get member by policyholdername");

	 	
	 	//Verify TPL info
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[2]/td[2]")).getText().equals(idMem), "idMem mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[2]/td[2]")).getText().equals(numPol), "numPol mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[2]/td[2]")).getText().equals(numCarrier), "numCarrier mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[3]/td[2]")).getText().equals(nameMember), "nameMember mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[3]/td[2]")).getText().equals(namePolHold), "namePolHold mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[3]/td[2]")).getText().equals(nameCarrier), "nameCarrier mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[4]/td[2]")).getText().equals(ssnMem), "ssnMem mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[4]/td[2]")).getText().equals(ssnPol), "ssnPol mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[4]/td[2]")).getText().equals(minEff), "minEff mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[5]/td[2]")).getText().equals(maxEnd), "maxEnd mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:TPLInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[5]/td[2]")).getText().equals(polType), "polType mismatch");
	 	Common.cancelAll();

	 	//verify member information panels
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();

	 	Member.memberSearch(colValues.get(0));	 	
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[2]/td[2]")).getText().equals(idMem), "idMem mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[2]/td[2]")).getText().equals(colValues.get(5)+", "+colValues.get(4)), "nameMember mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[6]/td[2]")).getText().equals(ssnMem), "ssnMem mismatch");
	 	Assert.assertTrue(driver.findElement(By.xpath("//table[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[7]/td[2]")).getText().equals("Yes"), "TPL indicator expected YES but found NO");

    }
    
    public static void searchTPLMem(String member) throws Exception {
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchBean_CriteriaPanel:TplSearchResultDataPanel_CurrentID")).sendKeys(member);
	 	Common.search();
	 	Common.searchRecords(member);
	 	if (driver.findElements(By.id("MMISForm:MMISBodyContent:TplSearchResults_0:_id14")).size()==0)
	 		throw new SkipException("Skipping this test because the TPL member you are trying to search for was not found");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchResults_0:_id14")).click();

    }
    
    @Test
    public void test22459() throws Exception{
    	TestNGCustom.TCNo="22459";
    	log("//TC 22459");
    	
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	    //Select Other
	    driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	    //Select carrier
	    driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n22")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierBean_CriteriaPanel:TplCarrierDataPanel_CarrierCode")).sendKeys("0027014");
	    Common.search();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierList_0:TplCarrierBean_ColValue_carrierCode")).click();
	    
	 	Assert.assertTrue(driver.findElements(By.xpath("//span[contains(@id,'TplCarrierToOIPlanBean_ColValue_status')]")).size()>1, "Multiple and concurrent OI plan profiles are not displayed for carrier");
    }
    
    @Test
    public void test23291a() throws Exception{
    	TestNGCustom.TCNo="23291a";
    	log("//TC 23291a");
    	
    	sqlStatement="Select  A.Id_Medicaid, A.Num_Ssn, A.Nam_First, A.Nam_Last, A.Dte_Birth, C.Agency_Id From T_Re_Base A, T_Re_Other_Id C Where A.Ind_Active = 'Y' and num_ssn <>' ' And A.Sak_Recip=C.Sak_Recip And C.Cde_Agency = 'MHO' And C.End_Date = 22991231 and A.sak_recip not in (select sak_recip from t_tpl_resource) and Rownum < 2";
    	colNames.add("ID_MEDICAID");
    	colNames.add("NUM_SSN");
    	colNames.add("NAM_FIRST");
    	colNames.add("NAM_LAST");
    	colNames.add("DTE_BIRTH");
    	colNames.add("AGENCY_ID");

    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String member = colValues.get(0);
    	String ssn = colValues.get(1);
    	String f = colValues.get(2);
    	String l = colValues.get(3);
    	String dob = colValues.get(4);
    	String agID = colValues.get(5);
    	
    	log("Member: "+ member);
    	
    	//Store Member in table
    	Statement statement = Common.connection1.createStatement();
		sqlStatement = "select * from R_DAY2 where tc = '23291'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (!(colValues.get(0).equals("null"))) {
			sqlStatement = "delete from  R_DAY2 where tc = '23291'";
			statement.executeQuery(sqlStatement);
		}
		sqlStatement = "insert into  R_DAY2 values ('23291', '"+member+"', 'TPL-9007-D Letter', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
    	
		String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<MA21TPLs\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\nxsi:noNamespaceSchemaLocation=\"schema\\MultipleMA21tpl.xsd\">\n\n<MA21TPL>\n <memberid id_medicaid=\""+member+"\" "+
				"id_other=\""+agID+"\" id_source=\"MHO\">\n<policyowner tpl_status=\"U\" cde_carrier=\"0027000\" polc_type_cd=\"H\" ma21_id=\""+Common.generateRandomTaxID()+"\" nam_last=\""+l+"\" nam_first=\""+f+"\" nam_mid_init=\" \" gender=\"M\" dob=\""+dob.substring(0, 4)+"-"+dob.substring(4, 6)+"-"+dob.substring(6, 8)+"\" num_ssn=\""+ssn+"\" group_no=\" \" family_indv_cd=\"I\" family_indv_cd_desc=\"INDIVIDUAL\" polc_number_reported=\""+Common.generateRandomTaxID()+"\" tpl_confirmed_polc_no=\""+Common.generateRandomTaxID()+Common.generateRandomTaxID().substring(0, 4)+"\" polc_source_type_ind=\"E\" polc_verified_ind=\"S\" polc_begin_dte=\"2013-06-06\" polc_end_dte=\"2299-12-31\" phip_begin_dte=\"2013-06-06\" phip_end_dte=\"2299-12-31\" tpl_subsidy_pay_status=\"NP\" polc_dma_pays_begin_dte=\"2013-06-06\" polc_dma_pays_end_dte=\"2299-12-31\"\n/>"+
				"\n</memberid>\n</MA21TPL>\n</MA21TPLs>");    	//filename is same as otherid for uniqueness
		
    	String dir="";
    	if (env.equals("MO"))
    		dir="MA21";
    	else
    		dir="MA21UAT";

    	String fileName=tempDirPath+agID+Common.generateRandomName().substring(0,3)+".xml";
    	String fileNameZ="Z:\\"+dir+"\\"+agID+Common.generateRandomName().substring(0,3)+".xml";
    	
    	// writing xml string to xml file
    	PrintWriter out = new PrintWriter(fileNameZ);
    	out.println(xml);
    	out.close();
    	
    	//Create copy at local temp folder
    	out = new PrintWriter(fileName);
    	out.println(xml);
    	out.close();
    	
    	String tplCMD;
    	if (env.equals("MO"))
    		tplCMD = "runtestTPL "+fileNameZ;
    	else
    		tplCMD = "runtestTPL_TR "+fileNameZ;
    	System.out.println (tplCMD);
    	log("\r\n"+tplCMD);
    	
    	try {
    	    // Execute command
    	    String command[] = new String[3];
    	    command[0] = "cmd";
    	    command[1] = "/c";
    	    command[2] = "Z: && cd "+dir+" && "+tplCMD;
    	    Process child = Runtime.getRuntime().exec(command);
    	    
    	    BufferedReader input = new BufferedReader(new InputStreamReader(child.getInputStream()));

            String line=null;

            while((line=input.readLine()) != null) {
                System.out.println(line);
                log("\r\n"+line);
            }

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(child.getErrorStream()));
            String Error;

            while ((Error = stdError.readLine()) != null) {
            System.out.println(Error);
            }
            while ((Error = stdInput.readLine()) != null) {
            System.out.println(Error);
            }
            
            log("\r\nError: "+Error );
            
            int exitVal = child.waitFor();
            System.out.println("Exited with error code "+exitVal);

    	    
    	} catch (IOException e) {
    	}
    	
    	//Make sure this member has been enrolled into TPL subsystem
    	searchTPLMem(member);
		
    }
    
    //Day 2
    
    @Test
    public void test22455b() throws Exception{
    	TestNGCustom.TCNo="22455b";
    	log("//TC 22455b");
    	
        //Verify TPL-0017-R unix report
		String command, error;

		//Get Desired Filename
//		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/tpl01701.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/tpl01701.rpt.* | tail -1"; //grabs the latest gdg of the file

		
		error = "There was no report file found";
		String fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The CARRIERS BY EMPLOYER report filename is: "+fileName);

		//Verify Employee ID in file
		command = "grep 0008803 "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Employer ID";
		log("\r\n"+Common.connectUNIX(command, error));
		
		//Verify Employee Name in file
		command = "grep 'AMTRAK TRAVEL CO' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Employer Name";
		log("\r\n"+Common.connectUNIX(command, error));
		
        //Verify TPL-0018-R unix report
		//Get Desired Filename
//		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/tpl01801.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/tpl01801.rpt.* | tail -1"; //grabs the latest gdg of the file

		error = "There was no report file found";
		fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The EMPLOYERS BY CARRIER report filename is: "+fileName);

		//Verify Carrier ID in file
		command = "grep 'CARRIER NUMBER   0027000' "+fileName+" | tail -1";
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Carrier ID";
		log("\r\n"+Common.connectUNIX(command, error));
		
		//Verify Carrier Name in file
		command = "grep 'CARRIER NAME     BLUE CROSS BLUE SHIELD OF MA' "+fileName+" | tail -1";
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Carrier Name";
		log("\r\n"+Common.connectUNIX(command, error));
		
		//This  and next 2 segments no such employer id 0000084 in AWSUAT, so may not find in report
		//Verify Employer data in file
		command = "grep '0000084' "+fileName+" | tail -1";
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Employer ID";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep 'LOWELL PUBLIC SCHOOL' "+fileName+" | tail -1";
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Employer Name";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep '375 MERRIMACK ST RM 19' "+fileName+" | tail -1";
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Employer Address";
		log("\r\n"+Common.connectUNIX(command, error));
		
        //Verify TPL-0019-R unix report
		//Get Desired Filename
//		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/tpl01901.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/tpl01901.rpt.* | tail -1"; //grabs the latest gdg of the file

		error = "There was no report file found";
		fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The MEMBERS BY CARRIER report filename is: "+fileName);

		//Verify Carrier ID in file
		command = "grep 'CARRIER NUMBER   0027033' "+fileName+" | tail -1";
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Carrier ID";
		log("\r\n"+Common.connectUNIX(command, error));
		
		//Verify Carrier Name in file
		command = "grep 'CARRIER NAME     BLUE CROSS DENTAL OF RI' "+fileName+" | tail -1";
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Carrier Name";
		log("\r\n"+Common.connectUNIX(command, error));
		
		//Verify Member Data in file
		command = "grep 'MEMBER ID     MEMBER NAME                              POLICY NUMBER     OI PLAN  OI PLAN DESCRIPTION' "+fileName+" | tail -1";
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Member header information";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep '100003606272' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Member ID";
		log("\r\n"+Common.connectUNIX(command, error));
		
        //Verify TPL-0020-R unix report
		//Get Desired Filename
//		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/tpl02001.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-1))+" "+Common.dayUNIX(Common.convertSysdatecustom(-1))+"'";
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt/tpl02001.rpt.* | tail -1"; //grabs the latest gdg of the file

		error = "There was no report file found";
		fileName = Common.connectUNIX(command, error);
		fileName=fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		log(" The CARRIERS BY MEMBER report filename is: "+fileName);

		//Verify Member ID in file
		command = "grep 'MEMBER ID    100004650981' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Member ID";
		log("\r\n"+Common.connectUNIX(command, error));
		
		//This segment different member name in AWSUAT, so may not find in report
		//Verify Member Name in file
//		command = "grep 'MEMBER NAME  SARRUDA             , WILLIAM         A' "+fileName;
		command = "grep 'MEMBER NAME  CORADO              , ANNALEE         L' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Member Name";
		log("\r\n"+Common.connectUNIX(command, error));
		
		//Verify Carrier Data in file
		command = "grep 'CARRIER NUMBER:  0027000' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Carrier ID";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep 'CARRIER NAME     BLUE CROSS BLUE SHIELD OF MA' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Carrier Name";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep 'CARRIER ADDRESS  PO BOX 986015' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Carrier Address- Street";
		log("\r\n"+Common.connectUNIX(command, error));
		
		command = "grep 'BOSTON         , MA 02298-6015' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Carrier Address- City, State, Zip";
		log("\r\n"+Common.connectUNIX(command, error));
    }
    
    @Test
    public void test23291b() throws Exception{
    	TestNGCustom.TCNo="23291b";
    	log("//TC 23291b");
    	
	 	//get the member for TPL 9007 Letter
		sqlStatement = "select * from R_DAY2 where tc = '23291'";
		colNames.add("ID");
		colNames.add("DATE_REQUESTED");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member enrolled for TPL 9007 letter");

		String Mem = colValues.get(0);
		String DateR = colValues.get(1);
		
		//Got to letter request search panel
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RptsAndLetters")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:TPLReportsNavigatorPanel:TPLReportsNavigator:GRP_g2")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:TPLReportsNavigatorPanel:TPLReportsNavigator:ITM_n105")).click();
	    
	    //Search for the letter with the member ID
	    driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestBean_CriteriaPanel:LetterRequestDataPanel_MemberId")).sendKeys(Mem);
	    Common.search();
	 	Assert.assertTrue(driver.findElements(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id30")).size()>0, "There is no letter produced for this member");
	 	//Sort on end date
	    driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:_id37")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:_id37")).click();
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id30")).getText().equals("TPL-9007-D"), "The 9007 letter is not produced");
	 	Assert.assertTrue(driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults:tbody_element']/tr/td[2]")).getText().equals(DateR), "The 9007 letter is not for the one for the requested date.");
	 	
	    //Produce the letter
	    driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterSearchResults_0:_id30")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:LetterRequestSearchPanel:LetterRequestSearchPanel_generateAndPrintWithButtonDispReset_btn")).click();
    	
		log("The 9007 letter is produced for Member: "+Mem+". Please see Anshul Gandhi for the letter");
    }
    
    @Test
    public void test23310_22788() throws Exception{
    	TestNGCustom.TCNo="23310_22788";
    	log("//TC 23310_22788");

    	//Get TPL member
    	sqlStatement = "select b.id_medicaid from t_tpl_resource r, t_re_base b, t_coverage_xref xref"+
				   " where r.sak_recip = b.sak_recip  and b.ind_active = 'Y' and r.sak_tpl_resource = xref.sak_tpl_resource and xref.dte_end = 22991231"+
				   " and exists (select r1.sak_recip from t_tpl_resource r1 where r1.sak_recip = r.sak_recip group by r1.sak_recip having count(r1.sak_recip) > 4 ) and rownum < 2";
    	colNames.add("ID_MEDICAID"); //0
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	
    	String tplMem = colValues.get(0);
    	
    	log(" TPL Member: "+tplMem);
    	
    	//Search by member ID
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchBean_CriteriaPanel:TplSearchResultDataPanel_CurrentID")).sendKeys(tplMem);
	 	Common.search();
	 	Common.searchRecords(tplMem);
	 	//Order by end date desc to get 12/31/2299 on top
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchResults:_id53")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchResults:_id53")).click();
	 	//Select 1st record
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchResults_0:_id14")).click();
	 	
	 	//Additional policies
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplNavigatorPanel:TPLNavigator:ITM_n101")).click();
	 	//order by pol no. desc
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AdditionalPoliciesPanel:AdditionalPoliciesList:AdditionalPoliciesBean_ColHeader_tplPolicyNumber")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AdditionalPoliciesPanel:AdditionalPoliciesList:AdditionalPoliciesBean_ColHeader_tplPolicyNumber")).click();
	 	//Verify >1 policies are present
	 	if (driver.findElements(By.xpath("//span[contains(@id, 'AdditionalPoliciesBean_ColValue_tplPolicyNumber')]")).size()<2)
	 		Assert.assertTrue(false, "Multiple Additional policies were not present");
	 	
	 	//Check overlapping coverage warning
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplNavigatorPanel:TPLNavigator:ITM_n103")).click();
	 	//Sort by end date desc to get 12/31/2299 on top
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageList:CoverageXrefBean_ColHeader_endDate")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageList:CoverageXrefBean_ColHeader_endDate")).click();
	 	//Copy latest coverage and oi plan
	 	String oi = driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageList_0:CoverageXrefBean_ColValue_otherInsurancePlanCode")).getText();
	 	String cov = driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageList_0:CoverageXrefBean_ColValue_otherInsurancePlanToCoverage_coverageCode")).getText();
	 	//Click New Button
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:Coverage_NewButtonClay:CoverageList_newAction_btn")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageDataPanel_CoverageCode")).sendKeys(cov);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageDataPanel_OIPlanCode")).sendKeys(oi);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EffectiveDate")).sendKeys(Common.convertSysdate());
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EndDate")).sendKeys("12/31/2299");
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Near duplicate coverage identified, do you wish to continue with save?"), "Did not get duplicate coverage warning message. Instead got: "+message+"...");
    }
    
    @Test
    public void test31942() throws Exception{
    	
    	TestNGCustom.TCNo="31942 - Part D outbound file Test Case";
    	log("//TC "+TestNGCustom.TCNo);
    	log("Please verify the files checked below are produced for job run that occured after this regression cycle");
    	//Get latest ELGJM600 logs
		String command, error;
		command = "ls -ltr /customer/dsma/"+unixDir+"/logs/elgjm600.*.* | tail -1";
		error = "There was no log file found";
		String fileName = Common.connectUNIX(command, error);
		log("The Generate Part D Outgoing Request log file is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		
		//Verify job completed
		command = "grep 'JOB: /customer/dsma/"+unixDir+"/job/ELGJM600 completed' "+fileName;
		error = "Log file indicates there was an abend";
		String response= Common.connectUNIX(command, error);
		log("\r\n"+response);
		
		//Get the month for which the data is present in dat file
		String month = response.substring(response.length()-17, response.length()-15);
		String year = response.substring(response.length()-11, response.length()-9);
		String period = month+"20"+year;
		log("The extract has dat for month: "+period);
		
		//Get dat filename
		command = "grep 'elm60005.dat' "+fileName+" | head -1";
		error = "Didnt find any dat file with the name elm60005.dat.*";
		response= Common.connectUNIX(command, error);
		fileName=response.substring(response.indexOf("/"), response.length());
		log("\r\nThe Generate Part D Outgoing extract file filename is: "+fileName);
		
//		//Get dat file 
//		command = "ls "+response;
//		error = "There was no data file found with the filename: "+response;
//		fileName = Common.connectUNIX(command, error);
//		log("The Generate Part D Outgoing extract file is: "+fileName);
//		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		
		//Verify data in extract file
		command = "grep 'DET' "+fileName+" | head -1";
		error = "Dat file does not contain any DET data";
		response= Common.connectUNIX(command, error);
		log("\r\n"+response);
		
		//Get member from the response
		String member = response.substring(35, 55).trim();
		log("Member id is: "+member);
		int count = Integer.parseInt(member.substring(0, 2));
    	Assert.assertTrue(count>0, "You did not find a member in extract. Instead you found '"+member+"' for member");
		
		//Get this members info from DB
		sqlStatement = "SELECT base.id_medicaid, "+
                "hib.id_medicare, "+
                "to_char(to_date(base.dte_birth, 'YYYYMMDD'), 'MMDDYYYY') as dte_birth, "+
                "base.cde_sex, "+
                "NVL(base.num_ssn, '999999999') as num_ssn, "+
                "base.nam_last, "+
                "base.nam_first "+
                "FROM "+
                "t_re_base base, "+
                "t_re_hib  hib "+
                "WHERE "+
                "hib.sak_recip = base.sak_recip "+
                "AND base.id_medicaid = "+member;
    	colNames.add("ID_MEDICAID");//0
    	colNames.add("ID_MEDICARE");//1
    	colNames.add("DTE_BIRTH");	//2
    	colNames.add("CDE_SEX");	//3
    	colNames.add("NUM_SSN"); 	//4
    	colNames.add("NAM_LAST"); 	//5
    	colNames.add("NAM_FIRST"); 	//6
    	colValues = Common.executeQuery(sqlStatement, colNames);	
    	
    	String ID_MEDICAID = colValues.get(0);
    	String ID_MEDICARE = colValues.get(1);
    	String DTE_BIRTH = colValues.get(2);
    	String CDE_SEX = colValues.get(3);
    	String NUM_SSN = colValues.get(4);
    	String NAM_LAST = colValues.get(5);
    	String NAM_FIRST = colValues.get(6);
    	
    	Assert.assertTrue(response.contains(ID_MEDICAID), "The selected line in dat file doe not contain ID_MEDICAID: "+ID_MEDICAID);
    	Assert.assertTrue(response.contains(ID_MEDICARE), "The selected line in dat file doe not contain ID_MEDICARE: "+ID_MEDICARE);
    	Assert.assertTrue(response.contains(DTE_BIRTH), "The selected line in dat file doe not contain DTE_BIRTH: "+DTE_BIRTH);
    	Assert.assertTrue(response.contains(CDE_SEX), "The selected line in dat file doe not contain CDE_SEX: "+CDE_SEX);
    	Assert.assertTrue(response.contains(NUM_SSN), "The selected line in dat file doe not contain NUM_SSN: "+NUM_SSN);
    	Assert.assertTrue(response.contains(NAM_LAST), "The selected line in dat file doe not contain NAM_LAST: "+NAM_LAST);
    	Assert.assertTrue(response.contains(NAM_FIRST), "The selected line in dat file doe not contain NAM_FIRST: "+NAM_FIRST);
    	
    	//Verify Report
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/elm60001.rpt.* | tail -1";
		error = "There was no report file found";
		fileName = Common.connectUNIX(command, error);
		log("The Generate Part D Outgoing extract file is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		
		//Verify data in extract file
		command = "grep 'TOTAL PART D TRANSACTIONS READ' "+fileName;
		error = "No data exists in report";
		response= Common.connectUNIX(command, error);
		response = response.trim();
		log("\r\n"+response);
		count = Integer.parseInt(response.substring(response.length()-2, response.length()));
    	Assert.assertTrue(count>0, "TOTAL PART D TRANSACTIONS READ count is 0");
    	
		command = "grep 'TOTAL PART D TRANSACTIONS WRITTEN TO OUTGOING FILE' "+fileName;
		error = "No data exists in report";
		response= Common.connectUNIX(command, error);
		response = response.trim();
		log("\r\n"+response);
		count = Integer.parseInt(response.substring(response.length()-2, response.length()));
    	Assert.assertTrue(count>0, "TOTAL PART D TRANSACTIONS WRITTEN TO OUTGOING FILE count is 0");
    	
    }
    
    
    @Test
    public void test31938aMA21_ver() throws Exception{
    	
    	TestNGCustom.TCNo="31938a - HMS TPL file Test Case Day 1";
    	log("//TC "+TestNGCustom.TCNo);
    	
    	//Get a member who does not have TPL
	    sqlStatement = "select /*+RULE*/ base.ID_MEDICAID from t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid, T_Re_Other_Id C, " +
				"t_cde_aid aid,t_re_aid_elig elig where " +
				"elig.sak_recip=base.sak_recip " +
				"and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
				"and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
				"and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
				"and pgm. CDE_PGM_HEALTH='STD' " +
				"and elig.DTE_END='22991231' " + 
				"and elig.cde_status1<>'H' " +
				"and not exists ( select sak_recip from t_tpl_resource rs where rs.sak_recip=base.sak_recip) " +
				"and base.ind_active='Y' and base.Sak_Recip=C.Sak_Recip And C.Cde_Agency = 'MHO'  And C.End_Date = 22991231 "+ 
				"and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip ) " + //Added to not have any medicare records in tpl search panel post run of below tpl enrollment process
				"and base.sak_recip > dbms_random.value * 6300000 " +
				"and rownum<2 ";
	    
	    System.out.println(sqlStatement);
	    colNames.add("ID_MEDICAID");
	    colValues=Common.executeQuery(sqlStatement, colNames);
	    String mem=colValues.get(0);

	    //Add TPL to this member using soap
	    sqlStatement="Select  A.Id_Medicaid, A.Num_Ssn, A.Nam_First, A.Nam_Last, A.Dte_Birth, C.Agency_Id From T_Re_Base A, T_Re_Other_Id C Where A.Sak_Recip=C.Sak_Recip and a.id_medicaid = '"+mem+"'";
    	colNames.add("ID_MEDICAID");
    	colNames.add("NUM_SSN");
    	colNames.add("NAM_FIRST");
    	colNames.add("NAM_LAST");
    	colNames.add("DTE_BIRTH");
    	colNames.add("AGENCY_ID");

    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String member = colValues.get(0);
    	String ssn = colValues.get(1);
    	String fi = colValues.get(2);
    	String l = colValues.get(3);
    	String dob = colValues.get(4);
    	String agID = colValues.get(5);
    	
    	log("Member: "+ member);

    	String bgDt = Common.convertSysdatecustom(-30).substring(6, 10)+"-"+Common.convertSysdatecustom(-30).substring(0, 2)+"-"+Common.convertSysdatecustom(-30).substring(3, 5);
    	String polNum = Common.generateRandomTaxID()+Common.generateRandomTaxID().substring(0, 4);
    	
		String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<MA21TPLs\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\nxsi:noNamespaceSchemaLocation=\"schema\\MultipleMA21tpl.xsd\">\n\n<MA21TPL>\n <memberid id_medicaid=\""+member+"\" "+
				"id_other=\""+agID+"\" id_source=\"MHO\">\n<policyowner tpl_status=\"U\" cde_carrier=\"0101001\" polc_type_cd=\"H\" ma21_id=\""+Common.generateRandomTaxID()+"\" nam_last=\""+l+"\" nam_first=\""+fi+"\" nam_mid_init=\" \" gender=\"M\" dob=\""+dob.substring(0, 4)+"-"+dob.substring(4, 6)+"-"+dob.substring(6, 8)+"\" num_ssn=\""+ssn+"\" group_no=\" \" family_indv_cd=\"I\" family_indv_cd_desc=\"INDIVIDUAL\" polc_number_reported=\""+Common.generateRandomTaxID()+"\" tpl_confirmed_polc_no=\""+polNum+"\" polc_source_type_ind=\"E\" polc_verified_ind=\"S\" polc_begin_dte=\""+bgDt+"\" polc_end_dte=\"2299-12-31\" phip_begin_dte=\""+bgDt+"\" phip_end_dte=\"2299-12-31\" tpl_subsidy_pay_status=\"NP\" polc_dma_pays_begin_dte=\""+bgDt+"\" polc_dma_pays_end_dte=\"2299-12-31\"\n/>"+
				"\n<absentparent ma21_id=\"00003175501\" nam_last=\"WESSON\" nam_first=\"ROBERT\" nam_mid_init=\"Z\" num_ssn=\"100031759\" dte_birth=\"1992-10-11\" cde_sex=\"M\" adr_strt1=\"28444TH ST.\" adr_strt2=\"APT 31759\" adr_city=\"LOWELL\" adr_state=\"MA\" adr_zip=\"317591001\" nam_country=\"MEXICO\" num_phone=\"7815553291\" med_support_order_ind=\"Y\" cde_good_cause=\"INCEST\"/>"+
				"\n</memberid>\n</MA21TPL>\n</MA21TPLs>");    	//filename is same as otherid for uniqueness

    	String fileName=tempDirPath+agID+Common.generateRandomName().substring(0,3)+".xml";
    	String envMA21 = "";
    	if (env.equals("MO"))
    		envMA21 = "MA21";
    	else
    		envMA21 = "MA21UAT";
    			
    			
    	String fileNameZ="Z:\\"+envMA21+"\\"+agID+Common.generateRandomName().substring(0,3)+".xml";
    	
    	// writing xml string to xml file
    	PrintWriter out = new PrintWriter(fileNameZ);
    	out.println(xml);
    	out.close();
    	
    	//Create copy at local temp folder
    	out = new PrintWriter(fileName);
    	out.println(xml);
    	out.close();
    	
    	String tplCMD;
    	if (env.equals("MO"))
    		tplCMD = "runtestTPL "+fileNameZ;
    	else
    		tplCMD = "runtestTPL_TR "+fileNameZ;
    	System.out.println (tplCMD);
    	log("\r\n"+tplCMD);
    	
    	try {
    	    // Execute command
    	    String command[] = new String[3];
    	    command[0] = "cmd";
    	    command[1] = "/c";
    	    command[2] = "Z: && cd "+envMA21+" && "+tplCMD;
    	    Process child = Runtime.getRuntime().exec(command);
    	    
    	    BufferedReader input = new BufferedReader(new InputStreamReader(child.getInputStream()));

            String line=null;

            while((line=input.readLine()) != null) {
                System.out.println(line);
                log("\r\n"+line);
            }

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(child.getErrorStream()));
            String Error;

            while ((Error = stdError.readLine()) != null) {
            System.out.println(Error);
            }
            while ((Error = stdInput.readLine()) != null) {
            System.out.println(Error);
            }
            
            log("\r\nError: "+Error );
            
            int exitVal = child.waitFor();
            System.out.println("Exited with error code "+exitVal);

    	    
    	} catch (IOException e) {
    	}
    	
    	//Get sak_tpl_resource from t_tpl_resource
    	sqlStatement = "select sak_tpl_resource from t_tpl_resource where sak_recip = ( select sak_recip from t_re_base where id_medicaid = '"+mem+"')";
    	colNames.add("SAK_TPL_RESOURCE");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String sakTPLRes = colValues.get(0);
    	
    	//We will change the coverage dates in the I/P file to below
    	String effDT = Common.convertSysdatecustom(-1).substring(6, 10)+"-"+Common.convertSysdatecustom(-1).substring(0, 2)+"-"+Common.convertSysdatecustom(-1).substring(3, 5);
    	String endDT = Common.convertSysdatecustom(1).substring(6, 10)+"-"+Common.convertSysdatecustom(1).substring(0, 2)+"-"+Common.convertSysdatecustom(1).substring(3, 5);

    	//Store Member in day 2 table
    	Statement statement = Common.connection1.createStatement();
		sqlStatement = "delete from  R_DAY2 where tc = '31938'";
		statement.executeQuery(sqlStatement);
		sqlStatement = "insert into  R_DAY2 values ('31938', '"+mem+"', 'member', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
		sqlStatement = "insert into  R_DAY2 values ('31938', '"+sakTPLRes+"', 'sakTPLRes', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
		sqlStatement = "insert into  R_DAY2 values ('31938', '"+polNum+"', 'polNum', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
		sqlStatement = "insert into  R_DAY2 values ('31938', '"+effDT.replace("-", "")+"', 'effDT', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
		sqlStatement = "insert into  R_DAY2 values ('31938', '"+endDT.replace("-", "")+"', 'endDT', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
    	
    	//Now create I/P file for TPLJD959 (It was TPLJD930 earlier, which was phased out, and now TPLJD959/TPLJD960 do the same procedure)
    	String inputFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"+
    			"<resourceUpdates> \n"+
    			"<resourceUpdate> \n"+
    			       "<member id_medicaid=\""+mem+"\" ind_good_cause=\"N\"> "+
    			            "<resource  id_medicaid=\""+mem+"\"  resource_identifier=\""+sakTPLRes+"\"  \n"+
    			            "cde_carrier=\"0101001\"  "+
    			            "cde_policy_type=\"I\" num_tpl_policy=\""+polNum+"\"  num_group=\"02257761\"  \n"+
    			            "cde_relation=\"G8\" cde_suspect=\"1\"> "+
    			            	 "<coverage coverage_sequence=\"1\" resource_identifier=\""+sakTPLRes+"\"  \n"+
    			               "oi_plan_identifier=\"3\"  \n"+
    			               "dte_effective=\""+effDT+"\" dte_end=\""+endDT+"\"  \n"+
    			               "cde_pgm_health=\"OI02\" /> \n"+
    			            "</resource> \n"+
    			       "</member> \n"+
    			"</resourceUpdate> \n"+
    			"</resourceUpdates> ";
    	
    	log(inputFileContent);
    	
    	//Create I/P file in home dir 
//		String command[] = new String[4];
//		command[0] = "cd /home/angandhi/regression/tc31398";
//		command[1] = "cat > tpd093000.xml.0001";
//		command[2] = inputFileContent;
//		command[3] = "^D";
//		Common.connectUNIXmultiCommands(command);
    	
    	//Change the Last change origin to OTHER, because HMS is lower priority than MA21
    	searchTPLMem(mem);
    	driver.findElement(By.linkText("Base Information")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_CdeInitOrg"))).selectByVisibleText("OTHER");
		//not using save all because an extra message of "Would you like to generate a TPL Verification Letter?" also comes
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
		String message=driver.findElement(By.xpath("//table[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[5]/td[2]")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
	
		String command, error;
		command = "echo '"+inputFileContent+"' > /home/angandhi/regression/tc31938/tpd093000.xml.0001";
		error = "There was no input file found";
		Common.connectUNIX(command, error);
		
		command = "cp /home/angandhi/regression/tc31938/tpd093000.xml.0001 /customer/dsma/"+unixDir+"/data";
		error = "File copy not successful";
		Common.connectUNIX(command, error);
		
		command = "chmod 777 /customer/dsma/"+unixDir+"/data/tpd093000.xml.0001";
		error = "File permission change not successful";
		Common.connectUNIX(command, error);

    }
    
    @Test
    public void test31938a() throws Exception{
    	
    	TestNGCustom.TCNo="31938a - HMS TPL file Test Case Day 1";
    	log("//TC "+TestNGCustom.TCNo);
    	
    	//Add resource
    	//Get a member who does not have TPL
	    sqlStatement = "select base.ID_MEDICAID from t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid, T_Re_Other_Id C, " +
				"t_cde_aid aid,t_re_aid_elig elig where " +
				"elig.sak_recip=base.sak_recip " +
				"and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
				"and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
				"and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
				"and pgm. CDE_PGM_HEALTH='STD' " +
				"and elig.DTE_END='22991231' " + 
				"and elig.cde_status1<>'H' " +
				"and not exists ( select sak_recip from t_tpl_resource rs where rs.sak_recip=base.sak_recip) " +
				"and base.ind_active='Y' and base.Sak_Recip=C.Sak_Recip And C.Cde_Agency = 'HIX'  And C.End_Date = 22991231 "+ //notice change in agency ID to HIX
				"and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip ) " + //Added to not have any medicare records in tpl search panel post run of below tpl enrollment process
				"and base.sak_recip > dbms_random.value * 6300000 " +
				"and rownum<2 ";
	    
	    System.out.println(sqlStatement);
	    colNames.add("ID_MEDICAID");
	    colValues=Common.executeQuery(sqlStatement, colNames);
	    String mem=colValues.get(0);
	    
	    //Get a policy holder ID
	    sqlStatement = "select SAK_POL_HOLD from t_policy_Holder where adr_mail_strt1 <> ' ' and adr_mail_city <> ' ' and adr_mail_state = 'MA' and num_ssn is not null  and dte_birth <> '0' and rownum < 2";
	    System.out.println(sqlStatement);
	    colNames.add("SAK_POL_HOLD");
	    colValues=Common.executeQuery(sqlStatement, colNames);
	    String polHolderID=colValues.get(0);
	    //Create a policyNumber
    	String polNum = Common.generateRandomTaxID()+Common.generateRandomTaxID().substring(0, 4);

	    
    	//Add resource
        Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();

	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLCurrentId")).sendKeys(mem);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLcarrierNumber")).sendKeys("0101001");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SubContractorIndicator"))).selectByVisibleText("No");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLRelationship")).sendKeys("01");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLPolicyHolderSearch")).sendKeys(polHolderID);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_TplPolicyNumber")).sendKeys(polNum);
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_CostAvoidIndicator"))).selectByVisibleText("No");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_OriginCode"))).selectByVisibleText("MA21");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_CdeInitOrg"))).selectByVisibleText("MA21");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SuspectCode"))).selectByVisibleText("VERIFIED");
        driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SuspectDate")).sendKeys(Common.convertSysdate());
        Common.saveAll();
        
        //Add Coverage
        driver.findElement(By.id("MMISForm:MMISBodyContent:TplNavigatorPanel:TPLNavigator:ITM_n103")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:Coverage_NewButtonClay:CoverageList_newAction_btn")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageDataPanel_CoverageCode")).sendKeys("02");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageDataPanel_OIPlanCode")).sendKeys("OI02");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EffectiveDate")).sendKeys(Common.convertSysdate());
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EndDate")).sendKeys("12/31/2299");
	 	Common.saveAll();
	 	log("Resource successfully added for Member: "+mem);
    	
//    	//Get a member who does not have TPL
//	    sqlStatement = "select /*+RULE*/ base.ID_MEDICAID from t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid, T_Re_Other_Id C, " +
//				"t_cde_aid aid,t_re_aid_elig elig where " +
//				"elig.sak_recip=base.sak_recip " +
//				"and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
//				"and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
//				"and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
//				"and pgm. CDE_PGM_HEALTH='STD' " +
//				"and elig.DTE_END='22991231' " + 
//				"and elig.cde_status1<>'H' " +
//				"and not exists ( select sak_recip from t_tpl_resource rs where rs.sak_recip=base.sak_recip) " +
//				"and base.ind_active='Y' and base.Sak_Recip=C.Sak_Recip And C.Cde_Agency = 'MHO'  And C.End_Date = 22991231 "+ 
//				"and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip ) " + //Added to not have any medicare records in tpl search panel post run of below tpl enrollment process
//				"and base.sak_recip > dbms_random.value * 6300000 " +
//				"and rownum<2 ";
//	    
//	    System.out.println(sqlStatement);
//	    colNames.add("ID_MEDICAID");
//	    colValues=Common.executeQuery(sqlStatement, colNames);
//	    String mem=colValues.get(0);
//
//	    //Add TPL to this member using soap
//	    sqlStatement="Select  A.Id_Medicaid, A.Num_Ssn, A.Nam_First, A.Nam_Last, A.Dte_Birth, C.Agency_Id From T_Re_Base A, T_Re_Other_Id C Where A.Sak_Recip=C.Sak_Recip and a.id_medicaid = '"+mem+"'";
//    	colNames.add("ID_MEDICAID");
//    	colNames.add("NUM_SSN");
//    	colNames.add("NAM_FIRST");
//    	colNames.add("NAM_LAST");
//    	colNames.add("DTE_BIRTH");
//    	colNames.add("AGENCY_ID");
//
//    	colValues = Common.executeQuery(sqlStatement, colNames);
//    	String member = colValues.get(0);
//    	String ssn = colValues.get(1);
//    	String fi = colValues.get(2);
//    	String l = colValues.get(3);
//    	String dob = colValues.get(4);
//    	String agID = colValues.get(5);
//    	
//    	log("Member: "+ member);
//
//    	String bgDt = Common.convertSysdatecustom(-30).substring(6, 10)+"-"+Common.convertSysdatecustom(-30).substring(0, 2)+"-"+Common.convertSysdatecustom(-30).substring(3, 5);
//    	
//		String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<MA21TPLs\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\nxsi:noNamespaceSchemaLocation=\"schema\\MultipleMA21tpl.xsd\">\n\n<MA21TPL>\n <memberid id_medicaid=\""+member+"\" "+
//				"id_other=\""+agID+"\" id_source=\"MHO\">\n<policyowner tpl_status=\"U\" cde_carrier=\"0101001\" polc_type_cd=\"H\" ma21_id=\""+Common.generateRandomTaxID()+"\" nam_last=\""+l+"\" nam_first=\""+fi+"\" nam_mid_init=\" \" gender=\"M\" dob=\""+dob.substring(0, 4)+"-"+dob.substring(4, 6)+"-"+dob.substring(6, 8)+"\" num_ssn=\""+ssn+"\" group_no=\" \" family_indv_cd=\"I\" family_indv_cd_desc=\"INDIVIDUAL\" polc_number_reported=\""+Common.generateRandomTaxID()+"\" tpl_confirmed_polc_no=\""+polNum+"\" polc_source_type_ind=\"E\" polc_verified_ind=\"S\" polc_begin_dte=\""+bgDt+"\" polc_end_dte=\"2299-12-31\" phip_begin_dte=\""+bgDt+"\" phip_end_dte=\"2299-12-31\" tpl_subsidy_pay_status=\"NP\" polc_dma_pays_begin_dte=\""+bgDt+"\" polc_dma_pays_end_dte=\"2299-12-31\"\n/>"+
//				"\n<absentparent ma21_id=\"00003175501\" nam_last=\"WESSON\" nam_first=\"ROBERT\" nam_mid_init=\"Z\" num_ssn=\"100031759\" dte_birth=\"1992-10-11\" cde_sex=\"M\" adr_strt1=\"28444TH ST.\" adr_strt2=\"APT 31759\" adr_city=\"LOWELL\" adr_state=\"MA\" adr_zip=\"317591001\" nam_country=\"MEXICO\" num_phone=\"7815553291\" med_support_order_ind=\"Y\" cde_good_cause=\"INCEST\"/>"+
//				"\n</memberid>\n</MA21TPL>\n</MA21TPLs>");    	//filename is same as otherid for uniqueness
//
//    	String fileName=tempDirPath+agID+Common.generateRandomName().substring(0,3)+".xml";
//    	String envMA21 = "";
//    	if (env.equals("MO"))
//    		envMA21 = "MA21";
//    	else
//    		envMA21 = "MA21UAT";
//    			
//    			
//    	String fileNameZ="Z:\\"+envMA21+"\\"+agID+Common.generateRandomName().substring(0,3)+".xml";
//    	
//    	// writing xml string to xml file
//    	PrintWriter out = new PrintWriter(fileNameZ);
//    	out.println(xml);
//    	out.close();
//    	
//    	//Create copy at local temp folder
//    	out = new PrintWriter(fileName);
//    	out.println(xml);
//    	out.close();
//    	
//    	String tplCMD;
//    	if (env.equals("MO"))
//    		tplCMD = "runtestTPL "+fileNameZ;
//    	else
//    		tplCMD = "runtestTPL_TR "+fileNameZ;
//    	System.out.println (tplCMD);
//    	log("\r\n"+tplCMD);
//    	
//    	try {
//    	    // Execute command
//    	    String command[] = new String[3];
//    	    command[0] = "cmd";
//    	    command[1] = "/c";
//    	    command[2] = "Z: && cd "+envMA21+" && "+tplCMD;
//    	    Process child = Runtime.getRuntime().exec(command);
//    	    
//    	    BufferedReader input = new BufferedReader(new InputStreamReader(child.getInputStream()));
//
//            String line=null;
//
//            while((line=input.readLine()) != null) {
//                System.out.println(line);
//                log("\r\n"+line);
//            }
//
//            BufferedReader stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));
//            BufferedReader stdError = new BufferedReader(new InputStreamReader(child.getErrorStream()));
//            String Error;
//
//            while ((Error = stdError.readLine()) != null) {
//            System.out.println(Error);
//            }
//            while ((Error = stdInput.readLine()) != null) {
//            System.out.println(Error);
//            }
//            
//            log("\r\nError: "+Error );
//            
//            int exitVal = child.waitFor();
//            System.out.println("Exited with error code "+exitVal);
//
//    	    
//    	} catch (IOException e) {
//    	}
    	
    	//Get sak_tpl_resource from t_tpl_resource
    	sqlStatement = "select sak_tpl_resource from t_tpl_resource where sak_recip = ( select sak_recip from t_re_base where id_medicaid = '"+mem+"')";
    	colNames.add("SAK_TPL_RESOURCE");
    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String sakTPLRes = colValues.get(0);
    	
    	//We will change the coverage dates in the I/P file to below
    	String effDT = Common.convertSysdatecustom(-1).substring(6, 10)+"-"+Common.convertSysdatecustom(-1).substring(0, 2)+"-"+Common.convertSysdatecustom(-1).substring(3, 5);
    	String endDT = Common.convertSysdatecustom(1).substring(6, 10)+"-"+Common.convertSysdatecustom(1).substring(0, 2)+"-"+Common.convertSysdatecustom(1).substring(3, 5);

    	//Store Member in day 2 table
    	Statement statement = Common.connection1.createStatement();
		sqlStatement = "delete from  R_DAY2 where tc = '31938'";
		statement.executeQuery(sqlStatement);
		sqlStatement = "insert into  R_DAY2 values ('31938', '"+mem+"', 'member', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
		sqlStatement = "insert into  R_DAY2 values ('31938', '"+sakTPLRes+"', 'sakTPLRes', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
		sqlStatement = "insert into  R_DAY2 values ('31938', '"+polNum+"', 'polNum', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
		sqlStatement = "insert into  R_DAY2 values ('31938', '"+effDT.replace("-", "")+"', 'effDT', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
		sqlStatement = "insert into  R_DAY2 values ('31938', '"+endDT.replace("-", "")+"', 'endDT', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
    	
    	//Now create I/P file for TPLJD959 (It was TPLJD930 earlier, which was phased out, and now TPLJD959/TPLJD960 do the same procedure)
    	String inputFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n"+
    			"<resourceUpdates> \n"+
    			"<resourceUpdate> \n"+
    			       "<member id_medicaid=\""+mem+"\" ind_good_cause=\"N\"> "+
    			            "<resource  id_medicaid=\""+mem+"\"  resource_identifier=\""+sakTPLRes+"\"  \n"+
    			            "cde_carrier=\"0101001\"  "+
    			            "cde_policy_type=\"G\" num_tpl_policy=\""+polNum+"\"  num_group=\"02257761\"  \n"+
    			            "cde_relation=\"G8\" cde_suspect=\"1\"> "+
    			            	 "<coverage coverage_sequence=\"1\" resource_identifier=\""+sakTPLRes+"\"  \n"+
    			               "oi_plan_identifier=\"3\"  \n"+
    			               "dte_effective=\""+effDT+"\" dte_end=\""+endDT+"\"  \n"+
    			               "cde_pgm_health=\"OI02\" /> \n"+
    			            "</resource> \n"+
    			       "</member> \n"+
    			"</resourceUpdate> \n"+
    			"</resourceUpdates> ";
    	
    	log(inputFileContent);
    	
    	//Create I/P file in home dir 
//		String command[] = new String[4];
//		command[0] = "cd /home/angandhi/regression/tc31398";
//		command[1] = "cat > tpd093000.xml.0001";
//		command[2] = inputFileContent;
//		command[3] = "^D";
//		Common.connectUNIXmultiCommands(command);
	
		String command, error;
		command = "echo '"+inputFileContent+"' > /home/angandhi/regression/tc31938/tpd093000.xml.0001";
		error = "There was no input file found";
		Common.connectUNIX(command, error);
		
		command = "cp /home/angandhi/regression/tc31938/tpd093000.xml.0001 /customer/dsma/"+unixDir+"/data";
		error = "File copy not successful";
		Common.connectUNIX(command, error);
		
		command = "chmod 777 /customer/dsma/"+unixDir+"/data/tpd093000.xml.0001";
		error = "File permission change not successful";
		Common.connectUNIX(command, error);

    }
    
    @Test
    public void test31938b() throws Exception{
    	TestNGCustom.TCNo="31938b - HMS TPL file Test Case Day 2";
    	log("//TC "+TestNGCustom.TCNo);
    	
	 	//get data for tpl0930d.rpt
		sqlStatement = "select ID from R_DAY2 where tc = '31938' and DES = 'member'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member found for tpl0930d.rpt");
		String mem = colValues.get(0);
		
		sqlStatement = "select ID from R_DAY2 where tc = '31938' and DES = 'polNum'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no polNum found for tpl0930d.rpt");
		String polNum = colValues.get(0);
		
		sqlStatement = "select ID from R_DAY2 where tc = '31938' and DES = 'sakTPLRes'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no sakTPLRes found for tpl0930d.rpt");
		String sakTPLRes = colValues.get(0);
		
		sqlStatement = "select ID from R_DAY2 where tc = '31938' and DES = 'effDT'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no effDT found for tpl0930d.rpt");
		String effDT = colValues.get(0);
		
		sqlStatement = "select ID from R_DAY2 where tc = '31938' and DES = 'endDT'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no endDT found for tpl0930d.rpt");
		String endDT = colValues.get(0);
		
		//Get member name
		sqlStatement = "select NAM_LAST, NAM_FIRST from t_re_base where id_medicaid = '"+mem+"'";
		colNames.add("NAM_LAST");
		colNames.add("NAM_FIRST");
		colValues = Common.executeQuery(sqlStatement, colNames);
		String lName = colValues.get(0);
		String fName = colValues.get(1);

		
		//Get report
		String command, error;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl0960d.rpt.* | tail -1"; //Filename was changed from tpl0930d to tpl0960d because TPLJD930 is phased out and TPLJD959/TPLJD960 now do this procedure
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		log("The MASSHEALTH CONTRACTOR INTERFACE ERROR REPORT is: "+fileName);
		
		String resp="";
		
		//Verify member in report
		command = "grep "+mem+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Member ID '"+mem+"'";
		resp = Common.connectUNIX(command, error).trim();
		log("\r\n"+resp);
		Assert.assertTrue(resp.contains(lName), "Member last name '"+lName+"' not found in report");
		Assert.assertTrue(resp.contains(fName), "Member first name '"+fName+"' not found in report");
		
		command = "grep "+sakTPLRes+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired sakTPLRes '"+sakTPLRes+"'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		Assert.assertTrue(resp.contains("0101001"), "Carrier code 0101001 not found in report");
		Assert.assertTrue(resp.contains("POL TYPE : G"), "POL TYPE : G not found in report");
		Assert.assertTrue(resp.contains(polNum), "Policy Number '"+polNum+"' not found in report");
		
		command = "grep 02257761 "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired group no. 02257761";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		Assert.assertTrue(resp.contains("REL: G8"), "REL: G8 not found in report");

		command = "grep "+effDT+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired effDT '"+effDT+"'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		Assert.assertTrue(resp.contains("EFF DTE: "+effDT), "EFF DTE: "+effDT+" not found in report");
		Assert.assertTrue(resp.contains("END DTE: "+endDT), "END DTE: "+endDT+" not found in report");
		Assert.assertTrue(resp.contains("OI: OI02"), "OI: OI02 not found in report");
		
		command = "grep 'Coverage Updated' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the 'Coverage Updated' message";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		
		command = "grep 'SEGMENTS UPDATED FOR COVERAGES:' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the 'SEGMENTS UPDATED FOR COVERAGES:' message";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		Assert.assertTrue(resp.contains("1"), "SEGMENTS UPDATED FOR COVERAGES: was not set to 1 in report");

		//Got to TPL panel to verify coverage
		effDT=Common.convertDate(effDT);
		endDT=Common.convertDate(endDT);

    	searchTPLMem(mem);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplNavigatorPanel:TPLNavigator:ITM_n103")).click();
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageList_0:CoverageXrefBean_ColValue_effectiveDate")).getText().equals(effDT), "effDT mismatch");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageList_0:CoverageXrefBean_ColValue_endDate")).getText().equals(endDT), "endDT mismatch");
    	
    }
    
    @Test
    public void test31939a() throws Exception{
    	
    	TestNGCustom.TCNo="31939a - HIX TPL File Test Case Day 1";
    	log("//TC "+TestNGCustom.TCNo);
    	
    	//Get a member who does not have TPL
	    sqlStatement = "select /*+RULE*/  base.ID_MEDICAID from t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid, T_Re_Other_Id C, " +
				"t_cde_aid aid,t_re_aid_elig elig where " +
				"elig.sak_recip=base.sak_recip " +
				"and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
				"and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
				"and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
				"and pgm. CDE_PGM_HEALTH='STD' " +
				"and elig.DTE_END='22991231' " + 
				"and elig.cde_status1<>'H' " +
				"and not exists ( select sak_recip from t_tpl_resource rs where rs.sak_recip=base.sak_recip) " +
				"and base.ind_active='Y' and base.Sak_Recip=C.Sak_Recip And C.Cde_Agency = 'HIX'  And C.End_Date = 22991231 "+ //notice change in agency ID to HIX
				"and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip ) " + //Added to not have any medicare records in tpl search panel post run of below tpl enrollment process
				"and base.num_ssn<>' ' " +
				"and base.sak_recip > dbms_random.value * 6300000 " +
				"and rownum<2 ";
	    
	    System.out.println(sqlStatement);
	    colNames.add("ID_MEDICAID");
	    colValues=Common.executeQuery(sqlStatement, colNames);
	    String mem=colValues.get(0);

	    //Add TPL to this member using TPLJD950
	    sqlStatement="Select  A.Id_Medicaid, A.Num_Ssn, A.Nam_First, A.Nam_Last, A.Dte_Birth, C.Agency_Id From T_Re_Base A, T_Re_Other_Id C Where A.Sak_Recip=C.Sak_Recip and C.Cde_Agency = 'HIX' and a.id_medicaid = '"+mem+"'";
    	colNames.add("ID_MEDICAID");
    	colNames.add("NUM_SSN");
    	colNames.add("NAM_FIRST");
    	colNames.add("NAM_LAST");
    	colNames.add("DTE_BIRTH");
    	colNames.add("AGENCY_ID");

    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String member = colValues.get(0);
    	String ssn = colValues.get(1);
    	String fi = colValues.get(2);
    	String l = colValues.get(3);
    	String dob = colValues.get(4);
    	String agID = colValues.get(5);
    	
    	log("Member: "+ member);

    	String bgDt = Common.convertSysdatecustom(-30).substring(6, 10)+"-"+Common.convertSysdatecustom(-30).substring(0, 2)+"-"+Common.convertSysdatecustom(-30).substring(3, 5);
    	String polNum = Common.generateRandomTaxID()+Common.generateRandomTaxID().substring(0, 4);
    	String ma21ID = "00"+Common.generateRandomTaxID();
    	dob = dob.substring(0, 4)+"-"+dob.substring(4, 6)+"-"+dob.substring(6, 8);

    	//Store Member in day 2 table
    	Statement statement = Common.connection1.createStatement();
		sqlStatement = "delete from  R_DAY2 where tc = '31939'";
		statement.executeQuery(sqlStatement);
		sqlStatement = "insert into  R_DAY2 values ('31939', '"+mem+"', 'member', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
		sqlStatement = "insert into  R_DAY2 values ('31939', '"+polNum+"', 'polNum', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
		sqlStatement = "insert into  R_DAY2 values ('31939', '"+bgDt.replace("-", "")+"', 'effDT', '"+Common.convertSysdate()+"')";
		statement.executeQuery(sqlStatement);
//		sqlStatement = "insert into  R_DAY2 values ('31939', '"+ma21ID+"', 'ma21ID', '"+Common.convertSysdate()+"')";
//		statement.executeQuery(sqlStatement);
    	
    	//Create I/P file for TPLJD950
    	String inputFileContent = "<TPLS> \n"+
    			"<TPL> \n"+
    			"<memberid id_medicaid=\""+mem+"\" id_other=\""+agID+"\" id_source=\"HIX\"> \n"+
    			"<policyowner tpl_status=\"I\" \n"+
    			"cde_carrier=\"0101001\" \n"+
    			"polc_type_cd=\"H\" \n"+
    			"ma21_id=\"11111111111\" \n"+
    			"nam_last=\""+l+"\" \n"+
    			"nam_first=\""+fi+"\" \n"+
    			"nam_mid_init=\"\" \n"+
    			"gender=\"M\" \n"+
    			"dob=\""+dob+"\" \n"+
    			"num_ssn=\""+ssn+"\" \n"+
    			"group_no=\"1111111E\" \n"+
    			"family_indv_cd=\"F\" \n"+
    			"family_indv_cd_desc=\"GROUP\" \n"+
    			"polc_number_reported=\"121212123\" \n"+
    			"tpl_confirmed_polc_no=\""+polNum+"\" \n"+
    			"polc_source_type_ind=\"E\" \n"+
    			"polc_verified_ind=\"S\" \n"+
    			"polc_begin_dte=\""+bgDt+"\" \n"+
    			"polc_end_dte=\"2299-12-31\" \n"+
    			"phip_begin_dte=\""+bgDt+"\" \n"+
    			"phip_end_dte=\"2299-12-31\" \n"+
    			"tpl_subsidy_pay_status=\"PP\" \n"+
    			"polc_dma_pays_begin_dte=\""+bgDt+"\" \n"+
    			"polc_dma_pays_end_dte=\"2299-12-31\"> \n"+
    			"<employer nam_bus=\"BEE FIBERGLASS\" \n"+
    			"adr_mail_strt1=\"536 DWIGHT ST SUITE 7\" \n"+
    			"addr_mail_city=\"HOLYOKE\" \n"+
    			"addr_mail_state=\"MA\" \n"+
    			"adr_mail_zip=\"01040\" \n"+
    			"adr_mail_zip_4=\"0000\" \n"+
    			"nam_country=\"USA\" \n"+
    			"num_phone=\"4133225555\" \n"+
    			"ein=\"000099423\"/> \n"+
    			"</policyowner> \n"+
    			"<absentparent \n"+
    			"ma21_id=\""+ma21ID+"\" \n"+
    			"nam_last=\"TESTA\" \n"+
    			"nam_first=\"MARY\" \n"+
    			"nam_mid_init=\"T\" \n"+
    			"num_ssn=\"071412375\" \n"+
    			"dte_birth=\"1975-02-18\" \n"+
    			"cde_sex=\"F\" \n"+
    			"cde_good_cause=\"ADOPTC\" \n"+
    			"empl_name=\"SMART TESTING\" \n"+
    			"empl_street_addr1=\"100 WINTER STREET\" \n"+
    			"empl_city=\"BOSTON\" \n"+
    			"empl_state=\"MA\" \n"+
    			"empl_zip=\"02110\" \n"+
    			"empl_zip_4=\"1234\" \n"+
    			"empl_phone=\"6175881293\" /> \n"+
    			"<accident tacc_accident_only_ind=\"Y\" \n"+
    			"tacc_oth_hi_ind=\"Y\" \n"+
    			"tacc_form_sent_ind=\"Y\" \n"+
    			"tacc_form_sent_dte=\"2013-09-23T04:00:00Z\" \n"+
    			"tacc_verified_ind=\"N\" \n"+
    			"tacc_tpl_complete_ind=\" \" /> \n"+
    			"</memberid> \n"+
    			"</TPL> \n"+
    			"</TPLS> \n";

    	
    	System.out.println(inputFileContent);

		String command, error;
		command = "echo '"+inputFileContent+"' > /home/angandhi/regression/tc31939/tpd09500.xml.0001";
		error = "There was no input file found";
		Common.connectUNIX(command, error);
		
		command = "cp /home/angandhi/regression/tc31939/tpd09500.xml.0001 /customer/dsma/"+unixDir+"/data";
		error = "File copy not successful";
		Common.connectUNIX(command, error);
		
		command = "chmod 777 /customer/dsma/"+unixDir+"/data/tpd09500.xml.0001";
		error = "File permission change not successful";
		Common.connectUNIX(command, error);

    }
    
    @Test
    public void test31939b() throws Exception{
    	TestNGCustom.TCNo="31939b - HIX TPL File Test Case Day 2";
    	log("//TC "+TestNGCustom.TCNo);
    	
	 	//get data for tpl0901d.rpt
		sqlStatement = "select ID from R_DAY2 where tc = '31939' and DES = 'member'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member found for tpl0901d.rpt");
		String mem = colValues.get(0);
		
		sqlStatement = "select ID from R_DAY2 where tc = '31939' and DES = 'polNum'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no polNum found for tpl0901d.rpt");
		String polNum = colValues.get(0);
		
		sqlStatement = "select ID from R_DAY2 where tc = '31939' and DES = 'effDT'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no effDT found for tpl0901d.rpt");
		String effDT = colValues.get(0);
		
		//Get member info
	    sqlStatement="Select  A.Id_Medicaid, A.Num_Ssn, A.Nam_First, A.Nam_Last, A.Dte_Birth, C.Agency_Id From T_Re_Base A, T_Re_Other_Id C Where A.Sak_Recip=C.Sak_Recip and C.Cde_Agency = 'HIX' and a.id_medicaid = '"+mem+"'";
    	colNames.add("ID_MEDICAID");
    	colNames.add("NUM_SSN");
    	colNames.add("NAM_FIRST");
    	colNames.add("NAM_LAST");
    	colNames.add("DTE_BIRTH");
    	colNames.add("AGENCY_ID");

    	colValues = Common.executeQuery(sqlStatement, colNames);
    	String member = colValues.get(0);
    	String ssn = colValues.get(1);
    	String fi = colValues.get(2);
    	String l = colValues.get(3);
    	String dob = colValues.get(4);
    	String agID = colValues.get(5);

		
		//Get report
		String command, error;
//		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl0901d.rpt.* | grep '"+Common.monthUNIX(Common.convertSysdatecustom(-3))+" "+Common.dayUNIX(Common.convertSysdatecustom(-3))+"' | head -n 1"; // This is to grab the second last report because the job runs 2 times a day and produces 2 reports in one day, so capturing the earlier one.
//		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl0901d.rpt.* | tail -2 | head -n 1"; // This is to grab the second last report because the job runs 2 times a day and produces 2 reports in one day, so capturing the earlier one.
//		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl0901d.rpt.* | tail -1";//For TR UAT, the above process is not working. first, the first job is run at 5 pm, so if you enter data after 5, the 2nd job will pick up. Also, first run is getting archived, so unzipped it, but it becomes latest now, so tail -1
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl0901d.rpt.* | tail -2 | head -n 1";//changed to pick the first file only. Its is zipped, so now added code to unzip it

		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("The MA21 INTERFACE REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		String resp="";
		
		//Verify member in report
		command = "grep "+member+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Member ID '"+member+"'";
		resp = Common.connectUNIX(command, error).trim();
		log("\r\n"+resp);
		Assert.assertTrue(resp.contains(l), "Member last name '"+l+"' not found in report");
		Assert.assertTrue(resp.contains(fi), "Member first name '"+fi+"' not found in report");
		Assert.assertTrue(resp.contains(fi), "Member MA21/Agency name '"+agID+"' not found in report");
		
		command = "grep "+dob+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired dob '"+dob+"'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		ssn=ssn.substring(0, 3)+"-"+ssn.substring(3, 5)+"-"+ssn.substring(5, 8);
		Assert.assertTrue(resp.contains(ssn), "SSN '"+ssn+"' not found in report");
		
		//Verify resource information in report
		command = "grep 'CONFIRMED POLICY: "+polNum+"' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired polNum '"+polNum+"'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);

		command = "grep "+effDT+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired effDT '"+effDT+"'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		
		//Verify Employer information in report
		command = "grep 'BEE FIBERGLASS' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Employer name 'BEE FIBERGLASS'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		Assert.assertTrue(resp.contains("ADDR: 536 DWIGHT ST SUITE 7"), "Employer Address 'ADDR: 536 DWIGHT ST SUITE 7' not found in report");

		command = "grep 'HOLYOKE' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired employer city 'HOLYOKE'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		Assert.assertTrue(resp.contains("01040"), "Employer Zip code '01040' not found in report");

		command = "grep '4133225555' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired employer phone '4133225555'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		Assert.assertTrue(resp.contains("EIN: 00-0099423"), "Employer 'EIN: 00-0099423' not found in report");
		
		//Verify Absent Parent information in report
		command = "grep 'TESTA' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired AP last name 'TESTA'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		Assert.assertTrue(resp.contains("FIRST NAME: MARY"), "AP 'FIRST NAME: MARY' not found in report");
		Assert.assertTrue(resp.contains("DOB: 19750218"), "AP 'DOB: 19750218' not found in report");

		command = "grep 'SSN: 071-41-2375' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired AP 'SSN: 071-41-2375'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);

		command = "grep 'GOOD CAUSE: ADOPTC' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired AP 'GOOD CAUSE: ADOPTC'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		
		//Verify Absent Parent Employer information in report
		command = "grep 'ABSENT PARENT EMPLOYER NAME: SMART TESTING' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired AP Employer name 'ABSENT PARENT EMPLOYER NAME: SMART TESTING'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);

		command = "grep 'ADDR: 100 WINTER STREET' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired AP Emp Street Adr 'ADDR: 100 WINTER STREET'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);

		command = "grep 'CITY: BOSTON' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired AP Emp city 'CITY: BOSTON'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		Assert.assertTrue(resp.contains("02110"), "AP Emp Zip '02110' not found in report");

		command = "grep 'PHONE: 6175881293' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired AP Emp phone 'PHONE: 6175881293'";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		
		//Verify Accident segment
		log("\r\n//Accident");
		command = "grep 'OTHER HEALTH INSURANCE: Y' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the desired Accident segment 'OTHER HEALTH INSURANCE: Y'";
		resp = Common.connectUNIX(command, error).trim();
		log("\r\n"+resp);
		Assert.assertTrue(resp.contains("A16/A17 FORM SENT: Y"), "Accident segment 'A16/A17 FORM SENT: Y' not found in report");
		
		//Verify no errors were there in the report 
		command = "grep 'ERRORS:' "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the 'ERRORS:' message";
		resp = Common.connectUNIX(command, error).trim();;
		log("\r\n"+resp);
		Assert.assertTrue(resp.contains("0"), "ERRORS: was not set to 0 in report");

		//Got to TPL panel to verify resource was added
    	searchTPLMem(member);
    	
    }
    
    @Test
    public void test31940() throws Exception{
    	
    	TestNGCustom.TCNo="31940 - Add update TPL data via panels Test Case";
    	log("//TC "+TestNGCustom.TCNo);
    	
    	String polHolderLname = Common.generateRandomName();
    	String polHolderFname = Common.generateRandomName();
    	String polHolderSSN = Common.generateRandomTaxID();
    	String polHolderDoB = Common.convertSysdatecustom(-12775);

    	//Add PolicyHolder
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n29")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_LastName")).sendKeys(polHolderLname);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_FirstName")).sendKeys(polHolderFname);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_SsnNumber")).sendKeys(polHolderSSN);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_BirthDate")).sendKeys(polHolderDoB);
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_GenderCode"))).selectByVisibleText("Male");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_MailStrt1")).sendKeys("101 FEDERAL ST");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_MailCity")).sendKeys("BOSTON");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_State"))).selectByVisibleText("MA");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_MailZip")).sendKeys("02110");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_OriginCode"))).selectByVisibleText("MA21");
        Common.saveAll();
        
        String polHolderID = driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderList_0:PolicyHolderBean_ColValue_policyHolderId")).getText();
        log("PolicyHolder Successfully added. PolicyHolder ID is: "+polHolderID);
        
        //Update PolicyHolder- change address
        Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n29")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderBean_CriteriaPanel:PolicyHolder_sakString")).sendKeys(polHolderID);
	 	Common.search();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderList_0:PolicyHolderBean_ColValue_status")).click();
	 	
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_MailStrt1")).clear();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_MailStrt1")).sendKeys("100 HANCOCK ST");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_MailCity")).clear();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_MailCity")).sendKeys("QUINCY");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_MailZip")).clear();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_MailZip")).sendKeys("02171");
	 	Common.saveAll();
	 	
	 	//Verfiy changes
        Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n29")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderBean_CriteriaPanel:PolicyHolder_sakString")).sendKeys(polHolderID);
	 	Common.search();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderList_0:PolicyHolderBean_ColValue_status")).click();
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_MailStrt1")).getAttribute("value").equals("100 HANCOCK ST"), "Address 1 did not update");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_MailCity")).getAttribute("value").equals("QUINCY"), "City did not update");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:PolicyHolderPanel:PolicyHolderDataPanel_MailZip")).getAttribute("value").equals("02171"), "Zip did not update");
	 	
        log("PolicyHolder Successfully updated");
	 	
	 	//Add Absent Parent
	 	
    	String absParentLname = Common.generateRandomName();
    	String absParentFname = Common.generateRandomName();
    	String absParentSSN = Common.generateRandomTaxID();
    	String absParentDoB = Common.convertSysdatecustom(-12775);
    	String absParentPhone = "1"+Common.generateRandomTaxID();
    	
        Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n33")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_LastName")).sendKeys(absParentLname);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_FirstName")).sendKeys(absParentFname);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_BirthDate")).sendKeys(absParentDoB);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_SsnNumber")).sendKeys(absParentSSN);
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_CdeSex"))).selectByVisibleText("Male");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_Country")).sendKeys("USA");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_Street1")).sendKeys("101 FEDERAL ST");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_Street2")).sendKeys("FL 9");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_City")).sendKeys("BOSTON");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_State"))).selectByVisibleText("MA");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_ZipCode")).sendKeys("02110");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_PhoneNum")).sendKeys(absParentPhone);
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_OriginCode"))).selectByVisibleText("MA21");
        Common.saveAll();
        
        String absParentID = driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentList_0:AbsentParentBean_ColValue_sakAbsentParent")).getText();
        log("Absent Parent Successfully added. Absent Parent ID is: "+absParentID);
        
        //Update Absent Parent- change address
        Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n33")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentBean_CriteriaPanel:AbsentParentDataPanel_SakAbsentParent")).sendKeys(absParentID);
	 	Common.search();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentList_0:AbsentParentBean_ColValue_sakAbsentParent")).click();
	 	
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_Street1")).clear();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_Street1")).sendKeys("100 HANCOCK ST");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_Street2")).clear();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_City")).clear();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_City")).sendKeys("QUINCY");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_ZipCode")).clear();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_ZipCode")).sendKeys("02171");
	 	Common.saveAll();
	 	
	 	//Verfiy changes
        Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n33")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentBean_CriteriaPanel:AbsentParentDataPanel_SakAbsentParent")).sendKeys(absParentID);
	 	Common.search();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentList_0:AbsentParentBean_ColValue_sakAbsentParent")).click();
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_Street1")).getAttribute("value").equals("100 HANCOCK ST"), "Address 1 did not update");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_City")).getAttribute("value").equals("QUINCY"), "City did not update");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:AbsentParentPanel:AbsentParentDataPanel_ZipCode")).getAttribute("value").equals("02171"), "Zip did not update");
        
        log("Absent Parent Successfully updated");
	 	
        //Add resource
    	//Get a member who does not have TPL
	    sqlStatement = "select base.ID_MEDICAID from t_re_base base, t_pub_hlth_pgm pgm,t_pub_hlth_aid pubaid, T_Re_Other_Id C, " +
				"t_cde_aid aid,t_re_aid_elig elig where " +
				"elig.sak_recip=base.sak_recip " +
				"and pgm.SAK_PUB_HLTH=pubaid.SAK_PUB_HLTH " +
				"and pubaid.SAK_CDE_AID=aid.SAK_CDE_AID "  +
				"and  aid.SAK_CDE_AID= elig.SAK_CDE_AID "  +
				"and pgm. CDE_PGM_HEALTH='STD' " +
				"and elig.DTE_END='22991231' " + 
				"and elig.cde_status1<>'H' " +
				"and not exists ( select sak_recip from t_tpl_resource rs where rs.sak_recip=base.sak_recip) " +
				"and base.ind_active='Y' and base.Sak_Recip=C.Sak_Recip And C.Cde_Agency = 'HIX'  And C.End_Date = 22991231 "+ //notice change in agency ID to HIX
				"and not exists (select sak_recip from t_re_hib hib where hib.sak_recip=base.sak_recip ) " + //Added to not have any medicare records in tpl search panel post run of below tpl enrollment process
				"and base.sak_recip > dbms_random.value * 6300000 " +
				"and rownum<2 ";
	    
	    System.out.println(sqlStatement);
	    colNames.add("ID_MEDICAID");
	    colValues=Common.executeQuery(sqlStatement, colNames);
	    String mem=colValues.get(0);
    	
    	//Add resource
        Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();

	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLCurrentId")).sendKeys(mem);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLcarrierNumber")).sendKeys("0101001");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SubContractorIndicator"))).selectByVisibleText("No");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLRelationship")).sendKeys("01");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLPolicyHolderSearch")).sendKeys(polHolderID);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_TplPolicyNumber")).sendKeys(Common.generateRandomTaxID());
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_CostAvoidIndicator"))).selectByVisibleText("No");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_OriginCode"))).selectByVisibleText("MA21");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_CdeInitOrg"))).selectByVisibleText("MA21");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SuspectCode"))).selectByVisibleText("VERIFIED");
        driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SuspectDate")).sendKeys(Common.convertSysdate());
        Common.saveAll();
        
        //Add Coverage
        driver.findElement(By.id("MMISForm:MMISBodyContent:TplNavigatorPanel:TPLNavigator:ITM_n103")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:Coverage_NewButtonClay:CoverageList_newAction_btn")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageDataPanel_CoverageCode")).sendKeys("02");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageDataPanel_OIPlanCode")).sendKeys("OI02");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EffectiveDate")).sendKeys(Common.convertSysdate());
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EndDate")).sendKeys("12/31/2299");
	 	Common.saveAll();
	 	
	 	//Add absent parent
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplNavigatorPanel:TPLNavigator:ITM_n106")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParent_NewButtonClay:MemberAbsentParentList_newAction_btn")).click();
//	 	driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParent_SakAbsentParent_CMD_SEARCH")).click(); //not working for chrome
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParent_SakAbsentParent_CMD_SEARCH']/img")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:_id37:AbsentParentSearchCriteriaPanel:AbsentParentID")).sendKeys(absParentID);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:_id37:AbsentParentSearchCriteriaPanel:SEARCH")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:_id37:AbsentParentSearchResults_0:column1Value")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();
	 	Common.saveAll();
	 	log("Resource successfully added for Member: "+mem);
	 	
	 	//Update resource- carrier and policy number
	 	String polNumber = Common.generateRandomTaxID();
        Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	searchTPLMem(mem);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplNavigatorPanel:TPLNavigator:ITM_n1")).click();
	 	
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLcarrierNumber")).clear();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLcarrierNumber")).sendKeys("0027000");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_TplPolicyNumber")).clear();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_TplPolicyNumber")).sendKeys(polNumber);
	 	Common.saveAll();
	 	
	 	//Veriy changes
        Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	searchTPLMem(mem);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplNavigatorPanel:TPLNavigator:ITM_n1")).click();

	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLcarrierNumber")).getAttribute("value").equals("0027000"), "Carrier did not update");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_TplPolicyNumber")).getAttribute("value").equals(polNumber), "Policy Number did not update");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLPolicyHolderSearch")).getAttribute("value").equals(polHolderID), "Policy Holder ID not found");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_PolicyHolderName")).getAttribute("value").contains(polHolderLname), "Policy Holder last name not found");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_PolicyHolderName")).getAttribute("value").contains(polHolderFname), "Policy Holder first name not found");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SsnNumber")).getAttribute("value").equals(polHolderSSN), "Policy Holder SSN not found");

	 	//Verify absent parent data
	 	absParentPhone = "("+absParentPhone.substring(0, 3)+")"+absParentPhone.substring(3, 6)+"-"+absParentPhone.substring(6, 10);//Because phone is dispalyed in this format (122)804-1041
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplNavigatorPanel:TPLNavigator:ITM_n106")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParentList_0:MemberAbsentParentBean_ColValue_status")).click();
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParent_SakAbsentParent")).getAttribute("value").equals(absParentID), "Absent Parent ID not found");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParentXrefDataPanel_FirstName")).getAttribute("value").equals(absParentFname), "Absent Parent first name not found");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParentXrefDataPanel_LastName")).getAttribute("value").equals(absParentLname), "Absent Parent last name not found");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParentXrefDataPanel_Country")).getAttribute("value").equals("USA"), "Absent Parent country not found");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParentXrefDataPanel_Street1")).getAttribute("value").equals("100 HANCOCK ST"), "Absent Parent Adr 1 not found");
//	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParentXrefDataPanel_Street2")).getAttribute("value").equals("FL 9"), "Absent Parent Adr 2 not found");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParentXrefDataPanel_City")).getAttribute("value").equals("QUINCY"), "Absent Parent City not found");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParentXrefDataPanel_StateCode")).getAttribute("value").equals("MA"), "Absent Parent State not found");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParentXrefDataPanel_ZipCode")).getAttribute("value").equals("02171"), "Absent Parent Zip not found");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParentXrefDataPanel_BirthDate")).getAttribute("value").equals(absParentDoB), "Absent Parent DoB not found");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParentXrefDataPanel_SexDescription")).getAttribute("value").equals("Male"), "Absent Parent Sex not found");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:MemberAbsentParentPanel:MemberAbsentParentXrefDataPanel_PhoneNum")).getAttribute("value").equals(absParentPhone), "Absent Parent Phone no. not found");

	 	log("Resource successfully updated");

    }
    
    @Test
    public void test31941_22788() throws Exception{
    	
    	TestNGCustom.TCNo="31941 - Addupdate carrier data via panels Test Case and 22788 - Verify duplicate carrier message and Verify duplicate employer message";
    	log("//TC "+TestNGCustom.TCNo);
    	
    	String carrierID = Common.generateRandomTaxID().substring(0, 7);
    	String carrierName = Common.generateRandomName();
    	String carrierFEIN = Common.generateRandomTaxID();
    	
    	//Add Carrier
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n22")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_CarrierCode")).sendKeys(carrierID);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_BusNameCar")).sendKeys(carrierName);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_Ein")).sendKeys(carrierFEIN);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailStrt1")).sendKeys("101 FEDERAL ST");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailCity")).sendKeys("BOSTON");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_State"))).selectByVisibleText("MA");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailZip")).sendKeys("02110");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_Undeliverable"))).selectByVisibleText("No");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrier_SubContractorIndicator1"))).selectByVisibleText("Main");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_OriginCode"))).selectByVisibleText("MA21");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_IndMedCom"))).selectByVisibleText("Commercial");

        //Add correspondence address
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:Address_1")).sendKeys("127 gas light dr");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:Address_3")).sendKeys("WEYMOUTH");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:Address_4"))).selectByVisibleText("MA");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:Address_5")).sendKeys("02190");
	 	
	 	//Carrier to OI plan
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierToOIPlanPanel:TplCarrierToOIPlan_NewButtonClay:TplCarrierToOIPlanList_newAction_btn")).click();
//	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierToOIPlanPanel:TplCarrierToOIPlan_OIPlanCode_CMD_SEARCH")).click(); //not working for chrome
	 	driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierToOIPlanPanel:TplCarrierToOIPlan_OIPlanCode_CMD_SEARCH']/img")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierToOIPlanPanel:_id61:CoverageCodeSearchCriteriaPanel:OiPlanCode")).sendKeys("OI02");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierToOIPlanPanel:_id61:CoverageCodeSearchCriteriaPanel:SEARCH")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierToOIPlanPanel:_id61:CoverageCodePopupSearchResults_0:column1Value")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierToOIPlanPanel:TplCarrierPlanXrefDataPanel_EffectiveDate")).sendKeys(Common.convertSysdate());
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierToOIPlanPanel:TplCarrierPlanXrefDataPanel_EndDate")).sendKeys("12/31/2299");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierToOIPlanPanel:TplCarrierToOIPlanPanelPanel_addAction_btn")).click();

	 	//Employers For Carrier
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:EmpCarrierXrefPanel:TplEmpCarrierXref_NewButtonClay:TplEmpCarrierXrefList_newAction_btn")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:EmpCarrierXrefPanel:TplEmpCarrierXrefDataPanel_EmployerCode")).sendKeys("0000006");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:EmpCarrierXrefPanel:EmpCarrierXrefPanelPanel_addAction_btn")).click();
	 	
	 	//Add Status
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierStatusPanel:TplCarrierStatus_NewButtonClay:TplCarrierStatusList_newAction_btn")).click();
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierStatusPanel:TplCarrierStatusDataPanel_StatusIndicator"))).selectByVisibleText("Active");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierStatusPanel:TplCarrierStatusDataPanel_EffectiveDate")).sendKeys(Common.convertSysdate());
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierStatusPanel:TplCarrierStatusDataPanel_EndDate")).sendKeys("12/31/2299");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierStatusPanel:TplCarrierStatusPanel_addAction_btn")).click();
	 	
	 	//Recode after DF 30249 is fixed
	 	Common.saveAll();
//		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
//	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierList_0:TplCarrierBean_ColValue_status")).getText().equals("Saved"), "Save was not successfull");
//	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierList_0:TplCarrierBean_ColValue_carrierCode")).getText().equals(carrierID), "Save was not successfull");
	 	log("Carrier Successfully Added. Carrier ID is: "+carrierID);
	 	
	 	//Update carrier - Change Address
	 	Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n22")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierBean_CriteriaPanel:TplCarrierDataPanel_CarrierCode")).sendKeys(carrierID);
	 	Common.search();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierList_0:TplCarrierBean_ColValue_status")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailStrt1")).clear();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailStrt1")).sendKeys("100 HANCOCK ST");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailCity")).clear();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailCity")).sendKeys("QUINCY");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailZip")).clear();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailZip")).sendKeys("02171");
	 	
	 	Common.saveAll();
	 	
	 	//Verify Updates
	 	Common.resetBase();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n22")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierBean_CriteriaPanel:TplCarrierDataPanel_CarrierCode")).sendKeys(carrierID);
	 	Common.search();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierList_0:TplCarrierBean_ColValue_status")).click();

	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailStrt1")).getAttribute("value").equals("100 HANCOCK ST"), "Address 1 did not update");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailCity")).getAttribute("value").equals("QUINCY"), "City did not update");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailZip")).getAttribute("value").equals("02171"), "Zip did not update");
	 	
	 	log("Carrier Address successfully updated.");
	 	
	 	//TC 22788
	 	LoginCheck();
	 	carrierID = Common.generateRandomTaxID().substring(0, 7);
    	carrierName = Common.generateRandomName();
    	
    	//Add Carrier
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n22")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_CarrierCode")).sendKeys(carrierID);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_BusNameCar")).sendKeys(carrierName);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_Ein")).sendKeys(carrierFEIN);
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailStrt1")).sendKeys("100 HANCOCK ST");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailCity")).sendKeys("QUINCY");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_State"))).selectByVisibleText("MA");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_MailZip")).sendKeys("02171");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_Undeliverable"))).selectByVisibleText("No");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrier_SubContractorIndicator1"))).selectByVisibleText("Main");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_OriginCode"))).selectByVisibleText("MA21");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierDataPanel_IndMedCom"))).selectByVisibleText("Commercial");
        
        //Save All and check for dulplicate warning message
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		if ((driver.findElements(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).size())>0) 
			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).click();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Exact duplicate carrier identified, do you wish to continue with save?"), "Did not get duplicate carrier warning message. Instead got: "+message+"...");

		Common.save();
		
		//Assert that we get the 'Saved' message and the carrier ID
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierList_0:TplCarrierBean_ColValue_status")).getText().equals("Saved"), "Save was not successfull");
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplCarrierPanel:TplCarrierList_0:TplCarrierBean_ColValue_carrierCode")).getText().equals(carrierID), "Save was not successfull");
		
	 	log("Duplicate alert message was successfully displayed, and duplicate carrier for same FEIN was saved successfully. Carrier ID is: "+carrierID);
	 	
	 	//Add employer and then add again with same FEIN. Verify duplicate message
	 	//Add employer record first
    	String empID = "";
    	String empName = Common.generateRandomName();
    	String empFEIN = Common.generateRandomTaxID();
    	
	 	LoginCheck();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n25")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_BusName")).sendKeys(empName);
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_Ein")).sendKeys(empFEIN);
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_MailStrt1")).sendKeys("100 HANCOCK ST");
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_MailCity")).sendKeys("QUINCY");
        new Select(driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_State"))).selectByVisibleText("MA");
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_MailZip")).sendKeys("02171");
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_Country")).sendKeys("USA");
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_ContactName")).sendKeys(empName);
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_PhoneNumber")).sendKeys("9"+Common.generateRandomTaxID());
        new Select(driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_ErisaIndicator"))).selectByVisibleText("No");
        new Select(driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_Union"))).selectByVisibleText("No");
        Common.save();
        empID = driver.findElement(By.id("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerList_0:TplEmployerBean_ColValue_employerCode")).getText();
	 	
	 	//Verify Employer successfully saved
	 	LoginCheck();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n25")).click();
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerBean_CriteriaPanel:TplEmployerDataPanel_EmployerCode")).sendKeys(empID);
	 	Common.search();
	 	Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerList_0:TplEmployerBean_ColValue_employerCode")).getText().equals(empID), "Save was not successfull");
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerList_0:TplEmployerBean_ColValue_employerCode")).click();
	 	Assert.assertTrue(driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_BusName")).getAttribute("value").equals(empName), "Emp Name not correct");
	 	Assert.assertTrue(driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_MailStrt1")).getAttribute("value").equals("100 HANCOCK ST"), "Emp Street1 not correct");
	 	Assert.assertTrue(driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_MailCity")).getAttribute("value").equals("QUINCY"), "Emp City not correct");
	 	Assert.assertTrue(driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_MailZip")).getAttribute("value").equals("02171"), "Emp Zip not correct");
	 	
	 	log("Employer Successfully Added. Employer ID is: "+empID);


	 	//Create new employer record for test with same FEIN number. Verify Duplicate warning
	 	empName = Common.generateRandomName();
	 	
	 	LoginCheck();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:GRP_RelatedDataOther")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplRelatedDataNavigatorPanel:TplRelatedDataNavigatorId:ITM_n25")).click();
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='New']")).click();
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_BusName")).sendKeys(empName);
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_Ein")).sendKeys(empFEIN);
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_MailStrt1")).sendKeys("101 FEDERAL ST");
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_MailCity")).sendKeys("BOSTON");
        new Select(driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_State"))).selectByVisibleText("MA");
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_MailZip")).sendKeys("02110");
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_Country")).sendKeys("USA");
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_ContactName")).sendKeys(empName);
	 	driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_PhoneNumber")).sendKeys("9"+Common.generateRandomTaxID());
        new Select(driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_ErisaIndicator"))).selectByVisibleText("No");
        new Select(driver.findElement(By.name("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerDataPanel_Union"))).selectByVisibleText("No");
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
		message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Another employer with the same FEIN exists, please review"), "Did not get duplicate employer warning message. Instead got: "+message+"...");
		Common.save();
        empID = driver.findElement(By.id("MMISForm:MMISBodyContent:TplEmployerPanel:TplEmployerList_0:TplEmployerBean_ColValue_employerCode")).getText();
	 	log("Duplicate alert message was successfully displayed, and duplicate employer for same FEIN was saved successfully. Employer ID is: "+empID);

    }
    
    @Test
    public void test31414_31432_31466_HMS_PartA() throws Exception{
    	TestNGCustom.TCNo="31414_31432_31466_HMS_PartA";
    	log("//TC 31414_31432_31466_HMS_PartA");
    	
    	sqlStatement = "select h.num_icn_fl,b.id_medicaid,d.amt_paid,h.dte_first_svc,h.dte_to_date from T_hist_directory h, t_re_base b, AIM01.T_PD_PHYS_DTL d "+
    			"where h.sak_recip = b.sak_recip "+
    			"and h.CDE_CLM_STATUS = 'P' "+
    			"and h.cde_clm_type = 'M' "+
    			"and h.amt_paid>10 "+ //Because we will be recovering total $10 in recovery and adj
    			"and d.sak_claim=h.sak_claim "+
    			"and d.num_dtl=1 "+
    			"and d.cde_clm_status = 'P' "+
    			"and d.amt_paid>10 "+
    			"and h.num_icn_fl not in ("+ExistingRecoveryICNs+") "+
    			"and not exists (select 1 from t_tpl_ar_health_detail ar where ar.sak_claim = h.sak_claim) "+
    			"and rownum < 2";

    	colNames.add("NUM_ICN_FL");
    	colNames.add("ID_MEDICAID");
    	colNames.add("AMT_PAID");
    	colNames.add("DTE_FIRST_SVC");
    	colNames.add("DTE_TO_DATE");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	fetchedICN = colValues.get(0);
    	String memID = colValues.get(1);
    	String claimDtlPaidAmt = colValues.get(2);
    	String fdos = colValues.get(3);
    	String tdos = colValues.get(4);
    	
    	sqlStatement = "select c.cde_carrier from t_tpl_carrier c "+
    			"where c.nam_bus <> ' ' "+
    			"and not exists (select 1 from T_TPL_CASH_RECEIPT r where r.sak_carrier = c.sak_carrier) "+
    			"and c.cde_carrier > dbms_random.value * 6300000 "+
    			"and rownum < 2 ";
    	colNames.add("CDE_CARRIER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String carrier = colValues.get(0);


    	num_check=Common.generateRandomTaxID().substring(0,8);
    	name_on_check=Common.generateRandomName();
    	amt_paid=String.format("%.2f", Double.parseDouble(claimDtlPaidAmt)+100); //Making check amount to be claim detail paid amount +$100;
        dte_check_numwire=Common.convertDatetoInt(Common.convertSysdate()); //Today's date
        cde_carrier=carrier;
        num_ref=Common.generateRandomTaxID().substring(0,3);
        num_icn_tcn=fetchedICN; 
        num_dtl="1"; 
        id_medicaid=memID;
        clm_dtl_recovery_amt="1.42";
        dte_first_svc=fdos;
        dte_last_svc=tdos;
        amt_adjustment="8.58";
        
        recoveryFileContents = getEFT()+getClaimRecovery()+getAdjustment();
        createRecoveryFile(recoveryFileContents);
        transferFile();
        
        //Store Data for Day_2
    	SelSql="select * from r_day2 where TC = '31414hms' and DES='num_check'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414hms' and DES='num_check'";
    	InsSql="insert into r_day2 values ('31414hms', '"+num_check+"', 'num_check', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '31414hms' and DES='amt_paid'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414hms' and DES='amt_paid'";
    	InsSql="insert into r_day2 values ('31414hms', '"+amt_paid+"', 'amt_paid', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '31414hms' and DES='cde_carrier'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414hms' and DES='cde_carrier'";
    	InsSql="insert into r_day2 values ('31414hms', '"+cde_carrier+"', 'cde_carrier', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '31414hms' and DES='num_icn_tcn'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414hms' and DES='num_icn_tcn'";
    	InsSql="insert into r_day2 values ('31414hms', '"+num_icn_tcn+"', 'num_icn_tcn', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '31414hms' and DES='id_medicaid'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414hms' and DES='id_medicaid'";
    	InsSql="insert into r_day2 values ('31414hms', '"+id_medicaid+"', 'id_medicaid', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '31414hms' and DES='clm_dtl_recovery_amt'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414hms' and DES='clm_dtl_recovery_amt'";
    	InsSql="insert into r_day2 values ('31414hms', '"+clm_dtl_recovery_amt+"', 'clm_dtl_recovery_amt', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '31414hms' and DES='amt_adjustment'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414hms' and DES='amt_adjustment'";
    	InsSql="insert into r_day2 values ('31414hms', '"+amt_adjustment+"', 'amt_adjustment', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    }
    
    @Test
    public void test31414_31432_31466_HMS_PartB() throws Exception{
    	TestNGCustom.TCNo="31414_31432_31466_HMS_PartB";
    	log("//TC 31414_31432_31466_HMS_PartB");
    	
    	log("For test data that was validated for this TC, please refer to the query select * from r_day2 where TC = '31414hms'");
    	
    	//Get Data for Day_2
		sqlStatement = "select * from r_day2 where TC = '31414hms' and DES='num_check' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colNames.add("DATE_REQUESTED");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no CCN/AR created for this TC");
		num_check=colValues.get(0);
		String arDate=colValues.get(1);
		
		sqlStatement = "select * from r_day2 where TC = '31414hms' and DES='amt_paid' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		amt_paid=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '31414hms' and DES='cde_carrier' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		cde_carrier=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '31414hms' and DES='num_icn_tcn' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		num_icn_tcn=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '31414hms' and DES='id_medicaid' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		id_medicaid=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '31414hms' and DES='clm_dtl_recovery_amt' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		clm_dtl_recovery_amt=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '31414hms' and DES='amt_adjustment' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		amt_adjustment=colValues.get(0);
		
		//Navigate to TPL cash receipt panel
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TplCashReceipt")).click();
	    driver.findElement(By.xpath("//input[contains(@id,'CheckNumber')]")).sendKeys(num_check);
	    driver.findElement(By.xpath("//input[contains(@id,'checkDate')]")).sendKeys(arDate);
	    Common.search();
	    String ccn=driver.findElement(By.xpath("//*[contains(@id,'TplCashReceiptSearchResults_0')]")).getText();
	    log("CCN is: "+ccn);
	    driver.findElement(By.xpath("//*[contains(@id,'TplCashReceiptSearchResults_0')]")).click();
	    
	    //Validate CCN info
	    driver.findElement(By.linkText("Base Information")).click();

	 	String cashRcpt_carrier=driver.findElement(By.xpath("//input[contains(@id,'CarrierCodeSearch')]")).getAttribute("value").trim();
	    Assert.assertTrue(cashRcpt_carrier.equals(cde_carrier), "In TPL Cash receipt panel, couldn't find carrier: "+cde_carrier+". Instead got carrier: "+cashRcpt_carrier);

	 	String cashRcpt_PaidAmount=driver.findElement(By.xpath("//input[contains(@id,'PaidAmount')]")).getAttribute("value").trim();
	    Assert.assertTrue(cashRcpt_PaidAmount.contains(amt_paid), "In TPL Cash receipt panel, couldn't find amt_paid: "+amt_paid+". Instead got amt_paid: "+cashRcpt_PaidAmount);
	    
	 	String cashRcpt_CheckNumber=driver.findElement(By.xpath("//input[contains(@id,'CheckNumber')]")).getAttribute("value").trim();
	    Assert.assertTrue(cashRcpt_CheckNumber.equals(num_check), "In TPL Cash receipt panel, couldn't find num_check: "+num_check+". Instead got num_check: "+cashRcpt_CheckNumber);
	    
	    
	 	String cashRcpt_ReceiptDate=driver.findElement(By.xpath("//input[contains(@id,'ReceiptDate')]")).getAttribute("value").trim();
	    Assert.assertTrue(cashRcpt_ReceiptDate.equals(arDate), "In TPL Cash receipt panel, couldn't find ReceiptDate: "+arDate+". Instead got ReceiptDate: "+cashRcpt_ReceiptDate);
	    
	    log("CCN info is correct");
	    Common.cancelAll();
	    
	    //Navigate to TPL AR panel
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ARSearch")).click();
	    driver.findElement(By.xpath("//input[contains(@id,'icnNumber')]")).sendKeys(num_icn_tcn);
	    Common.search();
	    String AR=driver.findElement(By.xpath("//*[contains(@id,'TplArSearchResults_0:_')]")).getText();
	    log("AR is: "+AR);
	    driver.findElement(By.xpath("//*[contains(@id,'TplArSearchResults_0:_')]")).click();

	    //Validate AR info
	    driver.findElement(By.linkText("Base Information")).click();
	    
	 	String AR_amountBilled=driver.findElement(By.xpath("//input[contains(@id,'amountBilled')]")).getAttribute("value").trim();
	 	sqlStatement = "Select h.amt_paid from t_hist_directory h where h.num_icn_fl="+num_icn_tcn;
	 	colNames.add("AMT_PAID");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String clm_dtl_paid_amt = colValues.get(0);
	    Assert.assertTrue(AR_amountBilled.contains(clm_dtl_paid_amt), "In TPL AR Base info panel, couldn't find AR_amountBilled: "+clm_dtl_paid_amt+". Instead got AR_amountBilled: "+AR_amountBilled);
	    
	 	String AR_ICNDataSearchID=driver.findElement(By.xpath("//input[contains(@id,'ICNDataSearchID')]")).getAttribute("value").trim();
	    Assert.assertTrue(AR_ICNDataSearchID.equals(num_icn_tcn), "In TPL AR Base info panel, couldn't find ICN: "+num_icn_tcn+". Instead got ICN: "+AR_ICNDataSearchID);
	    
	 	String AR_MemberID=driver.findElement(By.xpath("//input[contains(@id,'TPLCurrentId')]")).getAttribute("value").trim();
	    Assert.assertTrue(AR_MemberID.equals(id_medicaid), "In TPL AR Base info panel, couldn't find Member: "+id_medicaid+". Instead got Member: "+AR_MemberID);
	    
	    log("AR info is correct");
	    Common.cancelAll();
	    
	    //Navigate to TPL AR  Detail Disposition panel
		driver.findElement(By.linkText("Carrier AR Detail Dispositions")).click();
		
		//Validate Recovery/disposition
		driver.findElement(By.linkText("1")).click();
		//Sort on CCN
	    driver.findElement(By.xpath("//*[contains(@id,'TplArDispsBean_ColHeader_numdtl')]")).sendKeys(num_icn_tcn);
	    driver.findElement(By.xpath("//*[contains(@id,'TplArDispsBean_ColHeader_cashCtlNo')]")).sendKeys(num_icn_tcn);
	    driver.findElement(By.xpath("//*[contains(@id,'TplArDispsBean_ColHeader_cashCtlNo')]")).sendKeys(num_icn_tcn);
	    
	 	String AR_disp_date=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_0:TplArDispsBean_ColValue_addedDate')]")).getText();
	    Assert.assertTrue(AR_disp_date.equals(arDate), "In TPL AR Detail Disp panel for recovery, couldn't find Carrier: "+arDate+". Instead got Carrier: "+AR_disp_date);
	    
	 	String AR_DispAmt=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_0:TplArDispsBean_ColValue_amount')]")).getText();
	    Assert.assertTrue(AR_DispAmt.contains(clm_dtl_recovery_amt), "In TPL AR Detail Disp panel for recovery, couldn't find AR_DispAmt: "+clm_dtl_recovery_amt+". Instead got AR_DispAmt: "+AR_DispAmt);
	    
	 	String AR_ccn=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_0:TplArDispsBean_ColValue_cashCtlNo')]")).getText();
	    Assert.assertTrue(AR_ccn.equals(ccn), "In TPL AR Detail Disp panel for recovery, couldn't find CCN: "+ccn+". Instead got CCN: "+AR_ccn);

	 	String AR_disp_carrier=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_0:TplArDispsBean_ColValue_tplCarrier_carrierCode')]")).getText();
	    Assert.assertTrue(AR_disp_carrier.equals(cde_carrier), "In TPL AR Detail Disp panel for recovery, couldn't find Carrier: "+cde_carrier+". Instead got Carrier: "+AR_disp_carrier);
	    
	    log("AR Detail Disposition info is correct");
	    
	    //Validate Adjustment
	 	String AR_adj_date=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_1:TplArDispsBean_ColValue_addedDate')]")).getText();
	    Assert.assertTrue(AR_adj_date.equals(arDate), "In TPL AR Detail Disp panel for adj, couldn't find Carrier: "+arDate+". Instead got Carrier: "+AR_adj_date);
	    
	 	String AR_AdjAmt=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_1:TplArDispsBean_ColValue_amount')]")).getText();
	    Assert.assertTrue(AR_AdjAmt.contains(amt_adjustment), "In TPL AR Detail Disp panel for adj, couldn't find Adjustment amount: "+amt_adjustment+". Instead got Adjustment amount: "+AR_AdjAmt);
	    
	 	String AR_adj_carrier=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_1:TplArDispsBean_ColValue_tplCarrier_carrierCode')]")).getText();
	    Assert.assertTrue(AR_adj_carrier.equals(cde_carrier), "In TPL AR Detail Disp panel for adj, couldn't find Carrier: "+cde_carrier+". Instead got Carrier: "+AR_adj_carrier);
	    
	    log("AR Adjustment info is correct");
	    
	    //Validate report
    	//Get the report name
		String command, error, resp;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl91003.rpt.* | tail -n 2 | head -n 1"; //This is to get the 2nd last file, because HMS job TPLJD910 job runs before TPLJD909, and both produce same report
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("TPL SUCCESSFUL INBOUND RECOVERY SUMMARY REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		command = "grep \"HMS             "+id_medicaid+"      "+cde_carrier+"        "+num_check+"                                "+arDate+"\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"HMS             "+id_medicaid+"      "+cde_carrier+"        "+num_check+"                                "+arDate+"\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
	    Assert.assertTrue(resp.contains(clm_dtl_recovery_amt), "The selected file does not have the clm_dtl_recovery_amt:"+clm_dtl_recovery_amt+" string in it");
		
//		command = "grep HMS "+fileName;
//		error = "The selected file is not correct/ or it is empty/ or it does not have the HMS string in it";
//		resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
//
//		command = "grep "+id_medicaid+" "+fileName;
//		error = "The selected file is not correct/ or it is empty/ or it does not have the Member ID:"+id_medicaid+" string in it";
//		resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
//		
//		command = "grep "+cde_carrier+" "+fileName;
//		error = "The selected file is not correct/ or it is empty/ or it does not have the Carrier:"+cde_carrier+" string in it";
//		resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
//		
//		command = "grep "+num_check+" "+fileName;
//		error = "The selected file is not correct/ or it is empty/ or it does not have the Check number:"+num_check+" string in it";
//		resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
//		
//		command = "grep "+arDate+" "+fileName+" | sed -n 2p"; 
//		error = "The selected file is not correct/ or it is empty/ or it does not have the run date:"+arDate+" string in it";
//		resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
//		
//		command = "grep "+clm_dtl_recovery_amt+" "+fileName;
//		error = "The selected file is not correct/ or it is empty/ or it does not have the clm_dtl_recovery_amt:"+clm_dtl_recovery_amt+" string in it";
//		resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
		
//		command = "grep \"TOTAL SEGMENTS PROCESSED          =              3\" "+fileName;
//		error = "The selected file is not correct/ or it is empty/ or it does not have the TOTAL SEGMENTS PROCESSED          =              3 string in process summary";
//		resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
//		
//		command = "grep \"CHECK/EFT      SEGMENTS INSERTED  =              1\" "+fileName;
//		error = "The selected file is not correct/ or it is empty/ or it does not have the CHECK/EFT      SEGMENTS INSERTED  =              1 string in process summary";
//		resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
//		
//		command = "grep \"CHECK/EFT      SEGMENTS IN ERROR  =              0\" "+fileName;
//		error = "The selected file is not correct/ or it is empty/ or it does not have the CHECK/EFT      SEGMENTS IN ERROR  =              0 string in process summary";
//		resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
//		
//		command = "grep \"CLAIM RECOVERY SEGMENTS INSERTED  =              1\" "+fileName;
//		error = "The selected file is not correct/ or it is empty/ or it does not have the CLAIM RECOVERY SEGMENTS INSERTED  =              1 string in process summary";
//		resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
//		
//		command = "grep \"CLAIM RECOVERY SEGMENTS IN ERROR  =              0\" "+fileName;
//		error = "The selected file is not correct/ or it is empty/ or it does not have the CLAIM RECOVERY SEGMENTS IN ERROR  =              0 string in process summary";
//		resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
//		
//		command = "grep \"ADJUSTMENTS    SEGMENTS INSERTED  =              1\" "+fileName;
//		error = "The selected file is not correct/ or it is empty/ or it does not have the ADJUSTMENTS    SEGMENTS INSERTED  =              1 string in process summary";
//		resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
//		
//		command = "grep \"ADJUSTMENTS    SEGMENTS IN ERROR  =              0\" "+fileName;
//		error = "The selected file is not correct/ or it is empty/ or it does not have the ADJUSTMENTS    SEGMENTS IN ERROR  =              0 string in process summary";
//		resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
		
    }
    
    @Test
    public void test31414_31432_31466_ACCS_PartA() throws Exception{
    	TestNGCustom.TCNo="31414_31432_31466_ACCS_PartA";
    	log("//TC 31414_31432_31466_ACCS_PartA");
    	
    	sqlStatement = "select h.num_icn_fl,b.id_medicaid,d.amt_paid,h.dte_first_svc,h.dte_to_date from T_hist_directory h, t_re_base b, AIM01.T_PD_PHYS_DTL d "+
    			"where h.sak_recip = b.sak_recip "+
    			"and h.CDE_CLM_STATUS = 'P' "+
    			"and h.cde_clm_type = 'M' "+
    			"and h.amt_paid>10 "+ //Because we will be recovering total $10 in recovery and adj
    			"and d.sak_claim=h.sak_claim "+
    			"and d.num_dtl=1 "+
    			"and d.cde_clm_status = 'P' "+
    			"and d.amt_paid>10 "+
    			"and h.num_icn_fl not in ("+ExistingRecoveryICNs+") "+
    			"and not exists (select 1 from t_tpl_ar_health_detail ar where ar.sak_claim = h.sak_claim) "+
    			"and rownum < 2";

    	colNames.add("NUM_ICN_FL");
    	colNames.add("ID_MEDICAID");
    	colNames.add("AMT_PAID");
    	colNames.add("DTE_FIRST_SVC");
    	colNames.add("DTE_TO_DATE");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	fetchedICN = colValues.get(0);
    	String memID = colValues.get(1);
    	String claimDtlPaidAmt = colValues.get(2);
    	String fdos = colValues.get(3);
    	String tdos = colValues.get(4);
    	
    	sqlStatement = "select c.cde_carrier from t_tpl_carrier c "+
    			"where c.nam_bus <> ' ' "+
    			"and not exists (select 1 from T_TPL_CASH_RECEIPT r where r.sak_carrier = c.sak_carrier) "+
    			"and c.cde_carrier > dbms_random.value * 6300000 "+
    			"and rownum < 2 ";
    	colNames.add("CDE_CARRIER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String carrier = colValues.get(0);

    	cde_unit="ACC1";
    	num_check=Common.generateRandomTaxID().substring(0,8);
    	name_on_check=Common.generateRandomName();
    	amt_paid=String.format("%.2f", Double.parseDouble(claimDtlPaidAmt)+100); //Making check amount to be claim detail paid amount +$100;
        dte_check_numwire=Common.convertDatetoInt(Common.convertSysdate()); //Today's date
        cde_carrier=carrier;
        num_ref=Common.generateRandomTaxID().substring(0,3);
        num_icn_tcn=fetchedICN; 
        num_dtl="1"; 
        id_medicaid=memID;
        clm_dtl_recovery_amt="1.42";
        dte_first_svc=fdos;
        dte_last_svc=tdos;
        amt_adjustment="8.58";
        
        txnType="ACCS";
        recoveryFileContents = getEFT()+getClaimRecovery()+getAdjustment();
        createRecoveryFile(recoveryFileContents);
        transferFile();
        
        //Store Data for Day_2
    	SelSql="select * from r_day2 where TC = '31414accs' and DES='num_check'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414accs' and DES='num_check'";
    	InsSql="insert into r_day2 values ('31414accs', '"+num_check+"', 'num_check', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '31414accs' and DES='amt_paid'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414accs' and DES='amt_paid'";
    	InsSql="insert into r_day2 values ('31414accs', '"+amt_paid+"', 'amt_paid', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '31414accs' and DES='cde_carrier'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414accs' and DES='cde_carrier'";
    	InsSql="insert into r_day2 values ('31414accs', '"+cde_carrier+"', 'cde_carrier', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '31414accs' and DES='num_icn_tcn'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414accs' and DES='num_icn_tcn'";
    	InsSql="insert into r_day2 values ('31414accs', '"+num_icn_tcn+"', 'num_icn_tcn', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '31414accs' and DES='id_medicaid'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414accs' and DES='id_medicaid'";
    	InsSql="insert into r_day2 values ('31414accs', '"+id_medicaid+"', 'id_medicaid', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '31414accs' and DES='clm_dtl_recovery_amt'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414accs' and DES='clm_dtl_recovery_amt'";
    	InsSql="insert into r_day2 values ('31414accs', '"+clm_dtl_recovery_amt+"', 'clm_dtl_recovery_amt', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '31414accs' and DES='amt_adjustment'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31414accs' and DES='amt_adjustment'";
    	InsSql="insert into r_day2 values ('31414accs', '"+amt_adjustment+"', 'amt_adjustment', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    }
    
    @Test
    public void test31414_31432_31466_ACCS_PartB() throws Exception{
    	TestNGCustom.TCNo="31414_31432_31466_ACCS_PartB";
    	log("//TC 31414_31432_31466_ACCS_PartB");
    	
    	log("For test data that was validated for this TC, please refer to the query select * from r_day2 where TC = '31414accs'");
    	
    	//Get Data for Day_2
		sqlStatement = "select * from r_day2 where TC = '31414accs' and DES='num_check' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colNames.add("DATE_REQUESTED");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no CCN/AR created for this TC");
		num_check=colValues.get(0);
		String arDate=colValues.get(1);
		
		sqlStatement = "select * from r_day2 where TC = '31414accs' and DES='amt_paid' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		amt_paid=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '31414accs' and DES='cde_carrier' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		cde_carrier=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '31414accs' and DES='num_icn_tcn' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		num_icn_tcn=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '31414accs' and DES='id_medicaid' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		id_medicaid=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '31414accs' and DES='clm_dtl_recovery_amt' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		clm_dtl_recovery_amt=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '31414accs' and DES='amt_adjustment' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		amt_adjustment=colValues.get(0);
		
		//Navigate to TPL cash receipt panel
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TplCashReceipt")).click();
	    driver.findElement(By.xpath("//input[contains(@id,'CheckNumber')]")).sendKeys(num_check);
	    driver.findElement(By.xpath("//input[contains(@id,'checkDate')]")).sendKeys(arDate);
	    Common.search();
	    String ccn=driver.findElement(By.xpath("//*[contains(@id,'TplCashReceiptSearchResults_0')]")).getText();
	    log("CCN is: "+ccn);
	    driver.findElement(By.xpath("//*[contains(@id,'TplCashReceiptSearchResults_0')]")).click();
	    
	    //Validate CCN info
	    driver.findElement(By.linkText("Base Information")).click();

	 	String cashRcpt_carrier=driver.findElement(By.xpath("//input[contains(@id,'CarrierCodeSearch')]")).getAttribute("value").trim();
	    Assert.assertTrue(cashRcpt_carrier.equals(cde_carrier), "In TPL Cash receipt panel, couldn't find carrier: "+cde_carrier+". Instead got carrier: "+cashRcpt_carrier);

	 	String cashRcpt_PaidAmount=driver.findElement(By.xpath("//input[contains(@id,'PaidAmount')]")).getAttribute("value").trim();
	    Assert.assertTrue(cashRcpt_PaidAmount.contains(amt_paid), "In TPL Cash receipt panel, couldn't find amt_paid: "+amt_paid+". Instead got amt_paid: "+cashRcpt_PaidAmount);
	    
	 	String cashRcpt_CheckNumber=driver.findElement(By.xpath("//input[contains(@id,'CheckNumber')]")).getAttribute("value").trim();
	    Assert.assertTrue(cashRcpt_CheckNumber.equals(num_check), "In TPL Cash receipt panel, couldn't find num_check: "+num_check+". Instead got num_check: "+cashRcpt_CheckNumber);
	    
	    
	 	String cashRcpt_ReceiptDate=driver.findElement(By.xpath("//input[contains(@id,'ReceiptDate')]")).getAttribute("value").trim();
	    Assert.assertTrue(cashRcpt_ReceiptDate.equals(arDate), "In TPL Cash receipt panel, couldn't find ReceiptDate: "+arDate+". Instead got ReceiptDate: "+cashRcpt_ReceiptDate);
	    
	    log("CCN info is correct");
	    Common.cancelAll();
	    
	    //Navigate to TPL AR panel
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ARSearch")).click();
	    driver.findElement(By.xpath("//input[contains(@id,'icnNumber')]")).sendKeys(num_icn_tcn);
	    Common.search();
	    String AR=driver.findElement(By.xpath("//*[contains(@id,'TplArSearchResults_0:_')]")).getText();
	    log("AR is: "+AR);
	    driver.findElement(By.xpath("//*[contains(@id,'TplArSearchResults_0:_')]")).click();

	    //Validate AR info
	    driver.findElement(By.linkText("Base Information")).click();
	    
	 	String AR_amountBilled=driver.findElement(By.xpath("//input[contains(@id,'amountBilled')]")).getAttribute("value").trim();
	 	sqlStatement = "Select h.amt_paid from t_hist_directory h where h.num_icn_fl="+num_icn_tcn;
	 	colNames.add("AMT_PAID");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String clm_dtl_paid_amt = colValues.get(0);
	    Assert.assertTrue(AR_amountBilled.contains(clm_dtl_paid_amt), "In TPL AR Base info panel, couldn't find AR_amountBilled: "+clm_dtl_paid_amt+". Instead got AR_amountBilled: "+AR_amountBilled);
	    
	 	String AR_ICNDataSearchID=driver.findElement(By.xpath("//input[contains(@id,'ICNDataSearchID')]")).getAttribute("value").trim();
	    Assert.assertTrue(AR_ICNDataSearchID.equals(num_icn_tcn), "In TPL AR Base info panel, couldn't find ICN: "+num_icn_tcn+". Instead got ICN: "+AR_ICNDataSearchID);
	    
	 	String AR_MemberID=driver.findElement(By.xpath("//input[contains(@id,'TPLCurrentId')]")).getAttribute("value").trim();
	    Assert.assertTrue(AR_MemberID.equals(id_medicaid), "In TPL AR Base info panel, couldn't find Member: "+id_medicaid+". Instead got Member: "+AR_MemberID);
	    
	    log("AR info is correct");
	    Common.cancelAll();
	    
	    //Navigate to TPL AR  Detail Disposition panel
		driver.findElement(By.linkText("Carrier AR Detail Dispositions")).click();
		
		//Validate Recovery/disposition
		driver.findElement(By.linkText("1")).click();
		//Sort on CCN
	    driver.findElement(By.xpath("//*[contains(@id,'TplArDispsBean_ColHeader_numdtl')]")).sendKeys(num_icn_tcn);
	    driver.findElement(By.xpath("//*[contains(@id,'TplArDispsBean_ColHeader_cashCtlNo')]")).sendKeys(num_icn_tcn);
	    driver.findElement(By.xpath("//*[contains(@id,'TplArDispsBean_ColHeader_cashCtlNo')]")).sendKeys(num_icn_tcn);
	    
	 	String AR_disp_date=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_0:TplArDispsBean_ColValue_addedDate')]")).getText();
	    Assert.assertTrue(AR_disp_date.equals(arDate), "In TPL AR Detail Disp panel for recovery, couldn't find Carrier: "+arDate+". Instead got Carrier: "+AR_disp_date);
	    
	 	String AR_DispAmt=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_0:TplArDispsBean_ColValue_amount')]")).getText();
	    Assert.assertTrue(AR_DispAmt.contains(clm_dtl_recovery_amt), "In TPL AR Detail Disp panel for recovery, couldn't find AR_DispAmt: "+clm_dtl_recovery_amt+". Instead got AR_DispAmt: "+AR_DispAmt);
	    
	 	String AR_ccn=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_0:TplArDispsBean_ColValue_cashCtlNo')]")).getText();
	    Assert.assertTrue(AR_ccn.equals(ccn), "In TPL AR Detail Disp panel for recovery, couldn't find CCN: "+ccn+". Instead got CCN: "+AR_ccn);

	 	String AR_disp_carrier=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_0:TplArDispsBean_ColValue_tplCarrier_carrierCode')]")).getText();
	    Assert.assertTrue(AR_disp_carrier.equals(cde_carrier), "In TPL AR Detail Disp panel for recovery, couldn't find Carrier: "+cde_carrier+". Instead got Carrier: "+AR_disp_carrier);
	    
	    log("AR Detail Disposition info is correct");
	    
	    //Validate Adjustment
	 	String AR_adj_date=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_1:TplArDispsBean_ColValue_addedDate')]")).getText();
	    Assert.assertTrue(AR_adj_date.equals(arDate), "In TPL AR Detail Disp panel for adj, couldn't find Carrier: "+arDate+". Instead got Carrier: "+AR_adj_date);
	    
	 	String AR_AdjAmt=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_1:TplArDispsBean_ColValue_amount')]")).getText();
	    Assert.assertTrue(AR_AdjAmt.contains(amt_adjustment), "In TPL AR Detail Disp panel for adj, couldn't find Adjustment amount: "+amt_adjustment+". Instead got Adjustment amount: "+AR_AdjAmt);
	    
	 	String AR_adj_carrier=driver.findElement(By.xpath("//*[contains(@id,'TplArDispsList_1:TplArDispsBean_ColValue_tplCarrier_carrierCode')]")).getText();
	    Assert.assertTrue(AR_adj_carrier.equals(cde_carrier), "In TPL AR Detail Disp panel for adj, couldn't find Carrier: "+cde_carrier+". Instead got Carrier: "+AR_adj_carrier);
	    
	    log("AR Adjustment info is correct");
	    
	    //Validate report
    	//Get the report name
		String command, error, resp;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl91003.rpt.* | tail -1";
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("TPL SUCCESSFUL INBOUND RECOVERY SUMMARY REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		command = "grep \"Accenture       "+id_medicaid+"      "+cde_carrier+"        "+num_check+"                                "+arDate+"\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"Accenture       "+id_medicaid+"      "+cde_carrier+"        "+num_check+"                                "+arDate+"\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
	    Assert.assertTrue(resp.contains(clm_dtl_recovery_amt), "The selected file does not have the clm_dtl_recovery_amt:"+clm_dtl_recovery_amt+" string in it");
		
    }
    
    @Test
    public void test31432_HMS_PartA() throws Exception{
    	TestNGCustom.TCNo="31432_HMS_PartA";
    	log("//TC 31432_HMS_PartA - Recovery - icn with paid and denied lines- CT M");
    	
    	sqlStatement = "select h.num_icn_fl,b.id_medicaid,d.amt_paid,h.dte_first_svc,h.dte_to_date from T_hist_directory h, t_re_base b, AIM01.T_PD_PHYS_DTL d, AIM01.T_PD_PHYS_DTL d1 "+
    			"where h.sak_recip = b.sak_recip "+
    			"and h.CDE_CLM_STATUS = 'P' "+
    			"and h.cde_clm_type = 'M' "+
    			"and h.amt_paid>10 "+ //Because we will be recovering total $10 in recovery and adj
    			"and d.sak_claim=h.sak_claim "+
    			"and d.num_dtl=1 "+ //Detail 1 paid
    			"and d.cde_clm_status = 'P' "+
    			"and d.amt_paid>10 "+
    			"and d1.cde_clm_status = 'D' "+
    			"and d1.sak_claim=h.sak_claim "+
    			"and d1.num_dtl=2 "+ //Detail 2 denied
    			"and h.num_icn_fl not in ("+ExistingRecoveryICNs+") "+
    			"and not exists (select 1 from t_tpl_ar_health_detail ar where ar.sak_claim = h.sak_claim) "+
    			"and rownum < 2";

    	colNames.add("NUM_ICN_FL");
    	colNames.add("ID_MEDICAID");
    	colNames.add("AMT_PAID");
    	colNames.add("DTE_FIRST_SVC");
    	colNames.add("DTE_TO_DATE");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	fetchedICN = colValues.get(0);
    	String memID = colValues.get(1);
    	String claimDtlPaidAmt = colValues.get(2);
    	String fdos = colValues.get(3);
    	String tdos = colValues.get(4);
    	
    	sqlStatement = "select c.cde_carrier from t_tpl_carrier c "+
    			"where c.nam_bus <> ' ' "+
    			"and not exists (select 1 from T_TPL_CASH_RECEIPT r where r.sak_carrier = c.sak_carrier) "+
    			"and c.cde_carrier > dbms_random.value * 6300000 "+
    			"and rownum < 2 ";
    	colNames.add("CDE_CARRIER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String carrier = colValues.get(0);


    	num_check=Common.generateRandomTaxID().substring(0,8);
    	name_on_check=Common.generateRandomName();
    	amt_paid=String.format("%.2f", Double.parseDouble(claimDtlPaidAmt)+100); //Making check amount to be claim detail paid amount +$100;
        dte_check_numwire=Common.convertDatetoInt(Common.convertSysdate()); //Today's date
        cde_carrier=carrier;
        num_ref=Common.generateRandomTaxID().substring(0,3);
        num_icn_tcn=fetchedICN; 
        num_dtl="1"; 
        id_medicaid=memID;
        clm_dtl_recovery_amt="1.42";
        dte_first_svc=fdos;
        dte_last_svc=tdos;
        amt_adjustment="8.58";
        
        recoveryFileContents = getEFT()+getClaimRecovery();
        createRecoveryFile(recoveryFileContents);
        transferFile();
        
        //Store Data for Day_2
    	SelSql="select * from r_day2 where TC = '31432hms' and DES='num_icn_tcn'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31432hms' and DES='num_icn_tcn'";
    	InsSql="insert into r_day2 values ('31432hms', '"+num_icn_tcn+"', 'num_icn_tcn', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);

    }
    
    @Test
    public void test31432_HMS_PartB() throws Exception{
    	TestNGCustom.TCNo="31432_HMS_PartB";
    	log("//TC 31432_HMS_PartB");
    	
		sqlStatement = "select * from r_day2 where TC = '31432hms' and DES='num_icn_tcn' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		num_icn_tcn=colValues.get(0);
		
	    //Navigate to TPL AR panel
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ARSearch")).click();
	    driver.findElement(By.xpath("//input[contains(@id,'icnNumber')]")).sendKeys(num_icn_tcn);
	    Common.search();
	    String AR=driver.findElement(By.xpath("//*[contains(@id,'TplArSearchResults_0:_')]")).getText();
	    log("AR is: "+AR);
	    driver.findElement(By.xpath("//*[contains(@id,'TplArSearchResults_0:_')]")).click();
	    
	    //Navigate to TPL AR  Detail Disposition panel
		driver.findElement(By.linkText("Carrier AR Detail Dispositions")).click();
		
		//Make sure no detail 2 as it was denied
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		int dtl2Size= driver.findElements(By.linkText("2")).size();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	    Assert.assertTrue(dtl2Size==0, "Detail 2 was denied for this ICN:"+num_icn_tcn+", but still AR:"+AR+" had detail 2");
	    log("Detail 2 was denied for this ICN:"+num_icn_tcn+", and successfully validated that AR:"+AR+" did not have detail 2");

    }
    
    @Test
    public void test31424_HMS_PartA() throws Exception{
    	TestNGCustom.TCNo="31424_HMS_PartA";
    	log("//TC 31424_HMS_PartA - Recovery - invalid icn");
    	
    	sqlStatement = "select h.num_icn_fl,b.id_medicaid,d.amt_paid,h.dte_first_svc,h.dte_to_date from T_hist_directory h, t_re_base b, AIM01.T_PD_PHYS_DTL d, AIM01.T_PD_PHYS_DTL d1 "+
    			"where h.sak_recip = b.sak_recip "+
    			"and h.CDE_CLM_STATUS = 'P' "+
    			"and h.cde_clm_type = 'M' "+
    			"and h.amt_paid>10 "+ //Because we will be recovering total $10 in recovery and adj
    			"and d.sak_claim=h.sak_claim "+
    			"and d.num_dtl=1 "+ //Detail 1 paid
    			"and d.cde_clm_status = 'P' "+
    			"and d.amt_paid>10 "+
    			"and d1.cde_clm_status = 'D' "+
    			"and d1.sak_claim=h.sak_claim "+
    			"and d1.num_dtl=2 "+ //Detail 2 denied
    			"and h.num_icn_fl not in ("+ExistingRecoveryICNs+") "+
    			"and not exists (select 1 from t_tpl_ar_health_detail ar where ar.sak_claim = h.sak_claim) "+
    			"and rownum < 2";

    	colNames.add("NUM_ICN_FL");
    	colNames.add("ID_MEDICAID");
    	colNames.add("AMT_PAID");
    	colNames.add("DTE_FIRST_SVC");
    	colNames.add("DTE_TO_DATE");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	fetchedICN = colValues.get(0);
    	String memID = colValues.get(1);
    	String claimDtlPaidAmt = colValues.get(2);
    	String fdos = colValues.get(3);
    	String tdos = colValues.get(4);
    	
    	sqlStatement = "select c.cde_carrier from t_tpl_carrier c "+
    			"where c.nam_bus <> ' ' "+
    			"and not exists (select 1 from T_TPL_CASH_RECEIPT r where r.sak_carrier = c.sak_carrier) "+
    			"and c.cde_carrier > dbms_random.value * 6300000 "+
    			"and rownum < 2 ";
    	colNames.add("CDE_CARRIER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String carrier = colValues.get(0);


    	num_check=Common.generateRandomTaxID().substring(0,8);
    	name_on_check=Common.generateRandomName();
    	amt_paid=String.format("%.2f", Double.parseDouble(claimDtlPaidAmt)+100); //Making check amount to be claim detail paid amount +$100;
        dte_check_numwire=Common.convertDatetoInt(Common.convertSysdate()); //Today's date
        cde_carrier=carrier;
        num_ref=Common.generateRandomTaxID().substring(0,3);
        num_icn_tcn=fetchedICN; 
        num_dtl="1"; 
        id_medicaid=memID;
        clm_dtl_recovery_amt="1.42";
        dte_first_svc=fdos;
        dte_last_svc=tdos;
        amt_adjustment="8.58";
        
        //Make ICN invalid
        num_icn_tcn = "1234567891234";
        recoveryFileContents = getEFT()+getClaimRecovery();
        createRecoveryFile(recoveryFileContents);
        transferFile();
        
        //Store Data for Day_2
    	SelSql="select * from r_day2 where TC = '31424hms' and DES='num_icn_tcn'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31424hms' and DES='num_icn_tcn'";
    	InsSql="insert into r_day2 values ('31424hms', '"+num_icn_tcn+"', 'num_icn_tcn', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);

    }
    
    @Test
    public void test31424_HMS_PartB() throws Exception{
    	TestNGCustom.TCNo="31424_HMS_PartB";
    	log("//TC 31424_HMS_PartB - Recovery - invalid icn");
    	
		sqlStatement = "select * from r_day2 where TC = '31424hms' and DES='num_icn_tcn' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		num_icn_tcn=colValues.get(0);
		
		validateNoAR(num_icn_tcn);
	    
	    //Validate report
    	//Get the report name
		String command, error, resp;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl91002.rpt.* | tail -n 2 | head -n 1"; //This is to get the 2nd last file, because HMS job TPLJD910 job runs before TPLJD909, and both produce same report
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("TPL INBOUND RECOVERY ERROR REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		command = "grep "+num_icn_tcn+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the invalid ICN:"+num_icn_tcn;
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
		
		command = "grep \"RECOVERY - ICN/TCN NOT IN MMIS\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"RECOVERY - ICN/TCN NOT IN MMIS\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);

    }
    
    @Test
    public void test31427_HMS_PartA() throws Exception{
    	TestNGCustom.TCNo="31427_HMS_PartA";
    	log("//TC 31427_HMS_PartA - Recovery-invalid line num- CT M");
    	
    	sqlStatement = "select h.num_icn_fl,b.id_medicaid,d.amt_paid,h.dte_first_svc,h.dte_to_date from T_hist_directory h, t_re_base b, AIM01.T_PD_PHYS_DTL d "+
    			"where h.sak_recip = b.sak_recip "+
    			"and h.CDE_CLM_STATUS = 'P' "+
    			"and h.cde_clm_type = 'M' "+
    			"and h.amt_paid>10 "+ //Because we will be recovering total $10 in recovery and adj
    			"and d.sak_claim=h.sak_claim "+
    			"and d.num_dtl=1 "+
    			"and d.cde_clm_status = 'P' "+
    			"and d.amt_paid>10 "+
    			"and h.num_icn_fl not in ("+ExistingRecoveryICNs+") "+
    			"and not exists (select 1 from t_tpl_ar_health_detail ar where ar.sak_claim = h.sak_claim) "+
    			"and rownum < 2";

    	colNames.add("NUM_ICN_FL");
    	colNames.add("ID_MEDICAID");
    	colNames.add("AMT_PAID");
    	colNames.add("DTE_FIRST_SVC");
    	colNames.add("DTE_TO_DATE");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	fetchedICN = colValues.get(0);
    	String memID = colValues.get(1);
    	String claimDtlPaidAmt = colValues.get(2);
    	String fdos = colValues.get(3);
    	String tdos = colValues.get(4);
    	
    	sqlStatement = "select c.cde_carrier from t_tpl_carrier c "+
    			"where c.nam_bus <> ' ' "+
    			"and not exists (select 1 from T_TPL_CASH_RECEIPT r where r.sak_carrier = c.sak_carrier) "+
    			"and c.cde_carrier > dbms_random.value * 6300000 "+
    			"and rownum < 2 ";
    	colNames.add("CDE_CARRIER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String carrier = colValues.get(0);


    	num_check=Common.generateRandomTaxID().substring(0,8);
    	name_on_check=Common.generateRandomName();
    	amt_paid=String.format("%.2f", Double.parseDouble(claimDtlPaidAmt)+100); //Making check amount to be claim detail paid amount +$100;
        dte_check_numwire=Common.convertDatetoInt(Common.convertSysdate()); //Today's date
        cde_carrier=carrier;
        num_ref=Common.generateRandomTaxID().substring(0,3);
        num_icn_tcn=fetchedICN; 
        num_dtl="99"; //Make detail invalid 
        id_medicaid=memID;
        clm_dtl_recovery_amt="1.42";
        dte_first_svc=fdos;
        dte_last_svc=tdos;
        amt_adjustment="8.58";
        
        recoveryFileContents = getEFT()+getClaimRecovery();
        createRecoveryFile(recoveryFileContents);
        transferFile();
        
        //Store Data for Day_2
    	SelSql="select * from r_day2 where TC = '31427hms' and DES='num_icn_tcn'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31427hms' and DES='num_icn_tcn'";
    	InsSql="insert into r_day2 values ('31427hms', '"+num_icn_tcn+"', 'num_icn_tcn', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);

    }
    
    @Test
    public void test31427_HMS_PartB() throws Exception{
    	TestNGCustom.TCNo="31427_HMS_PartB";
    	log("//TC 31427_HMS_PartB - Recovery-invalid line num- CT M");
    	
    	sqlStatement = "select * from r_day2 where TC = '31427hms' and DES='num_icn_tcn' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		num_icn_tcn=colValues.get(0);
		
		validateNoAR(num_icn_tcn);
	    
	    //Validate report
    	//Get the report name
		String command, error, resp;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl91002.rpt.* | tail -n 2 | head -n 1"; //This is to get the 2nd last file, because HMS job TPLJD910 job runs before TPLJD909, and both produce same report 
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("TPL INBOUND RECOVERY ERROR REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		command = "grep "+num_icn_tcn+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the ICN:"+num_icn_tcn;
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
		
		command = "grep \"RECOVERY DETAIL NUMBER NOT FOUND IN MMIS\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"RECOVERY DETAIL NUMBER NOT FOUND IN MMIS\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
		
    }
    
    @Test
    public void test31431_ACCS_PartA() throws Exception{
    	TestNGCustom.TCNo="31431_ACCS_PartA";
    	log("//TC 31431_ACCS_PartA - Recovery denied icn- CT M");
    	
    	sqlStatement = "select h.num_icn_fl,b.id_medicaid,d.amt_paid,h.dte_first_svc,h.dte_to_date from T_hist_directory h, t_re_base b, AIM01.T_DENY_PHYS_DTL d "+
    			"where h.sak_recip = b.sak_recip "+
    			"and h.CDE_CLM_STATUS = 'D' "+
    			"and h.cde_clm_type = 'M' "+
    			"and d.sak_claim=h.sak_claim "+
    			"and d.num_dtl=1 "+
    			"and d.cde_clm_status = 'D' "+
    			"and not exists (select 1 from t_tpl_ar_health_detail ar where ar.sak_claim = h.sak_claim) "+
    			"and h.num_icn_fl not in ("+ExistingRecoveryICNs+") "+
    			"and rownum < 2";

    	colNames.add("NUM_ICN_FL");
    	colNames.add("ID_MEDICAID");
    	colNames.add("AMT_PAID");
    	colNames.add("DTE_FIRST_SVC");
    	colNames.add("DTE_TO_DATE");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	fetchedICN = colValues.get(0);
    	String memID = colValues.get(1);
    	String claimDtlPaidAmt = colValues.get(2);
    	String fdos = colValues.get(3);
    	String tdos = colValues.get(4);
    	
    	sqlStatement = "select c.cde_carrier from t_tpl_carrier c "+
    			"where c.nam_bus <> ' ' "+
    			"and not exists (select 1 from T_TPL_CASH_RECEIPT r where r.sak_carrier = c.sak_carrier) "+
    			"and c.cde_carrier > dbms_random.value * 6300000 "+
    			"and rownum < 2 ";
    	colNames.add("CDE_CARRIER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String carrier = colValues.get(0);

    	cde_unit="ACC1";
    	num_check=Common.generateRandomTaxID().substring(0,8);
    	name_on_check=Common.generateRandomName();
    	amt_paid=String.format("%.2f", Double.parseDouble(claimDtlPaidAmt)+100); //Making check amount to be claim detail paid amount +$100;
        dte_check_numwire=Common.convertDatetoInt(Common.convertSysdate()); //Today's date
        cde_carrier=carrier;
        num_ref=Common.generateRandomTaxID().substring(0,3);
        num_icn_tcn=fetchedICN; 
        num_dtl="1";
        id_medicaid=memID;
        clm_dtl_recovery_amt="1.42";
        dte_first_svc=fdos;
        dte_last_svc=tdos;
        amt_adjustment="8.58";
        
    	txnType="ACCS";
        recoveryFileContents = getEFT()+getClaimRecovery();
        createRecoveryFile(recoveryFileContents);
        transferFile();
        
        //Store Data for Day_2
    	SelSql="select * from r_day2 where TC = '31431accs' and DES='num_icn_tcn'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31431accs' and DES='num_icn_tcn'";
    	InsSql="insert into r_day2 values ('31431accs', '"+num_icn_tcn+"', 'num_icn_tcn', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);

    }
    
    @Test
    public void test31431_ACCS_PartB() throws Exception{
    	TestNGCustom.TCNo="31431_ACCS_PartB";
    	log("//TC 31431_ACCS_PartB - Recovery denied icn- CT M");
    	
    	sqlStatement = "select * from r_day2 where TC = '31431accs' and DES='num_icn_tcn' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		num_icn_tcn=colValues.get(0);
		
		validateNoAR(num_icn_tcn);
	    
	    //Validate report
    	//Get the report name
		String command, error, resp;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl91002.rpt.* | tail -1";
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("TPL INBOUND RECOVERY ERROR REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		command = "grep "+num_icn_tcn+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the ICN:"+num_icn_tcn;
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
		
		command = "grep \"RECOVERY - CLAIM IS MMIS DENIED CLAIM\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"RECOVERY - CLAIM IS MMIS DENIED CLAIM\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
		
    }
    
    @Test
    public void test31439_ACCS_PartA() throws Exception{
    	TestNGCustom.TCNo="31439_ACCS_PartA";
    	log("//TC 31439_ACCS_PartA - Recovery-CCN balance less than recovery amt");
    	
    	sqlStatement = "select h.num_icn_fl,b.id_medicaid,d.amt_paid,h.dte_first_svc,h.dte_to_date from T_hist_directory h, t_re_base b, AIM01.T_PD_PHYS_DTL d "+
    			"where h.sak_recip = b.sak_recip "+
    			"and h.CDE_CLM_STATUS = 'P' "+
    			"and h.cde_clm_type = 'M' "+
    			"and h.amt_paid>10 "+ //Because we will be recovering total $10 in recovery and adj
    			"and d.sak_claim=h.sak_claim "+
    			"and d.num_dtl=1 "+
    			"and d.cde_clm_status = 'P' "+
    			"and d.amt_paid>10 "+
    			"and h.num_icn_fl not in ("+ExistingRecoveryICNs+") "+
    			"and not exists (select 1 from t_tpl_ar_health_detail ar where ar.sak_claim = h.sak_claim) "+
    			"and rownum < 2";

    	colNames.add("NUM_ICN_FL");
    	colNames.add("ID_MEDICAID");
    	colNames.add("AMT_PAID");
    	colNames.add("DTE_FIRST_SVC");
    	colNames.add("DTE_TO_DATE");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	fetchedICN = colValues.get(0);
    	String memID = colValues.get(1);
    	String claimDtlPaidAmt = colValues.get(2);
    	String fdos = colValues.get(3);
    	String tdos = colValues.get(4);
    	
    	sqlStatement = "select c.cde_carrier from t_tpl_carrier c "+
    			"where c.nam_bus <> ' ' "+
    			"and not exists (select 1 from T_TPL_CASH_RECEIPT r where r.sak_carrier = c.sak_carrier) "+
    			"and c.cde_carrier > dbms_random.value * 6300000 "+
    			"and rownum < 2 ";
    	colNames.add("CDE_CARRIER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String carrier = colValues.get(0);

    	cde_unit="ACC1";
    	num_check=Common.generateRandomTaxID().substring(0,8);
    	name_on_check=Common.generateRandomName();
    	amt_paid="1.00"; //to make CCN amt< recovery amt
        dte_check_numwire=Common.convertDatetoInt(Common.convertSysdate()); //Today's date
        cde_carrier=carrier;
        num_ref=Common.generateRandomTaxID().substring(0,3);
        num_icn_tcn=fetchedICN; 
        num_dtl="1";
        id_medicaid=memID;
        clm_dtl_recovery_amt="1.42";
        dte_first_svc=fdos;
        dte_last_svc=tdos;
        amt_adjustment="8.58";
        
        txnType="ACCS";
        recoveryFileContents = getEFT()+getClaimRecovery();
        createRecoveryFile(recoveryFileContents);
        transferFile();
        
        //Store Data for Day_2
    	SelSql="select * from r_day2 where TC = '31439accs' and DES='num_icn_tcn'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31439accs' and DES='num_icn_tcn'";
    	InsSql="insert into r_day2 values ('31439accs', '"+num_icn_tcn+"', 'num_icn_tcn', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);

    }
    
    @Test
    public void test31439_ACCS_PartB() throws Exception{
    	TestNGCustom.TCNo="31439_ACCS_PartB";
    	log("//TC 31439_ACCS_PartB - Recovery-CCN balance less than recovery amt");
    	
    	sqlStatement = "select * from r_day2 where TC = '31439accs' and DES='num_icn_tcn' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		num_icn_tcn=colValues.get(0);
		
		validateNoAR(num_icn_tcn);
	    
	    //Validate report
    	//Get the report name
		String command, error, resp;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl91002.rpt.* | tail -1"; 
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("TPL INBOUND RECOVERY ERROR REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		command = "grep "+num_icn_tcn+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the ICN:"+num_icn_tcn;
		resp = Common.connectUNIX(command, error);
		log(resp);
		
		command = "grep \"RECOVERY AMOUNT GREATER THAN BALANCE AMT FOR THE CCN\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"RECOVERY AMOUNT GREATER THAN BALANCE AMT FOR THE CCN\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
		
    }
    
    @Test
    public void test31456_ACCS_PartA() throws Exception{
    	TestNGCustom.TCNo="31456_ACCS_PartA";
    	log("//TC 31456_ACCS_PartA - Adjustment- tcn- icn found - no AR");
    	
    	sqlStatement = "select h.num_icn_fl,b.id_medicaid,d.amt_paid,h.dte_first_svc,h.dte_to_date from T_hist_directory h, t_re_base b, AIM01.T_PD_PHYS_DTL d "+
    			"where h.sak_recip = b.sak_recip "+
    			"and h.CDE_CLM_STATUS = 'P' "+
    			"and h.cde_clm_type = 'M' "+
    			"and h.amt_paid>10 "+ //Because we will be recovering total $10 in recovery and adj
    			"and d.sak_claim=h.sak_claim "+
    			"and d.num_dtl=1 "+
    			"and d.cde_clm_status = 'P' "+
    			"and d.amt_paid>10 "+
    			"and h.num_icn_fl not in ("+ExistingRecoveryICNs+") "+
    			"and not exists (select 1 from t_tpl_ar_health_detail ar where ar.sak_claim = h.sak_claim) "+
    			"and rownum < 2";

    	colNames.add("NUM_ICN_FL");
    	colNames.add("ID_MEDICAID");
    	colNames.add("AMT_PAID");
    	colNames.add("DTE_FIRST_SVC");
    	colNames.add("DTE_TO_DATE");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	fetchedICN = colValues.get(0);
    	String memID = colValues.get(1);
    	String claimDtlPaidAmt = colValues.get(2);
    	String fdos = colValues.get(3);
    	String tdos = colValues.get(4);
    	
    	sqlStatement = "select c.cde_carrier from t_tpl_carrier c "+
    			"where c.nam_bus <> ' ' "+
    			"and not exists (select 1 from T_TPL_CASH_RECEIPT r where r.sak_carrier = c.sak_carrier) "+
    			"and c.cde_carrier > dbms_random.value * 6300000 "+
    			"and rownum < 2 ";
    	colNames.add("CDE_CARRIER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String carrier = colValues.get(0);

    	cde_unit="ACC1";
    	num_check=Common.generateRandomTaxID().substring(0,8);
    	name_on_check=Common.generateRandomName();
    	amt_paid=String.format("%.2f", Double.parseDouble(claimDtlPaidAmt)+100); //Making check amount to be claim detail paid amount +$100;
        dte_check_numwire=Common.convertDatetoInt(Common.convertSysdate()); //Today's date
        cde_carrier=carrier;
        num_ref=Common.generateRandomTaxID().substring(0,3);
        num_icn_tcn=fetchedICN; 
        num_dtl="1";
        id_medicaid=memID;
        clm_dtl_recovery_amt="1.42";
        dte_first_svc=fdos;
        dte_last_svc=tdos;
        amt_adjustment="8.58";
        
        txnType="ACCS";
        recoveryFileContents = getAdjustment();
        createRecoveryFile(recoveryFileContents);
        transferFile();
        
        //Store Data for Day_2
    	SelSql="select * from r_day2 where TC = '31456accs' and DES='num_icn_tcn'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31456accs' and DES='num_icn_tcn'";
    	InsSql="insert into r_day2 values ('31456accs', '"+num_icn_tcn+"', 'num_icn_tcn', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);

    }
    
    @Test
    public void test31456_ACCS_PartB() throws Exception{
    	TestNGCustom.TCNo="31456_ACCS_PartB";
    	log("//TC 31456_ACCS_PartB - Adjustment- tcn- icn found - no AR");
    	
    	sqlStatement = "select * from r_day2 where TC = '31456accs' and DES='num_icn_tcn' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		num_icn_tcn=colValues.get(0);
		
		validateNoAR(num_icn_tcn);
	    
	    //Validate report
    	//Get the report name
		String command, error, resp;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl91002.rpt.* | tail -1"; 
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("TPL INBOUND RECOVERY ERROR REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		command = "grep "+num_icn_tcn+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the ICN:"+num_icn_tcn;
		resp = Common.connectUNIX(command, error);
		log(resp);
		
		command = "grep \"NO AR FOUND FOR THE ADJUSTMENT RECORD\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"NO AR FOUND FOR THE ADJUSTMENT RECORD\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
		
    }
    
    @Test
    public void test31461_ACCS_PartA() throws Exception{
    	TestNGCustom.TCNo="31461_ACCS_PartA";
    	log("//TC 31461_ACCS_PartA - Adjustment-icn - adj amt > line paid amt ; CT M");
    	
    	sqlStatement = "select h.num_icn_fl,b.id_medicaid,d.amt_paid,h.dte_first_svc,h.dte_to_date from T_hist_directory h, t_re_base b, AIM01.T_PD_PHYS_DTL d "+
    			"where h.sak_recip = b.sak_recip "+
    			"and h.CDE_CLM_STATUS = 'P' "+
    			"and h.cde_clm_type = 'M' "+
    			"and h.amt_paid>10 "+ //Because we will be recovering total $10 in recovery and adj
    			"and d.sak_claim=h.sak_claim "+
    			"and d.num_dtl=1 "+
    			"and d.cde_clm_status = 'P' "+
    			"and d.amt_paid>10 "+
    			"and h.num_icn_fl not in ("+ExistingRecoveryICNs+") "+
    			"and not exists (select 1 from t_tpl_ar_health_detail ar where ar.sak_claim = h.sak_claim) "+
    			"and rownum < 2";

    	colNames.add("NUM_ICN_FL");
    	colNames.add("ID_MEDICAID");
    	colNames.add("AMT_PAID");
    	colNames.add("DTE_FIRST_SVC");
    	colNames.add("DTE_TO_DATE");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	fetchedICN = colValues.get(0);
    	String memID = colValues.get(1);
    	String claimDtlPaidAmt = colValues.get(2);
    	String fdos = colValues.get(3);
    	String tdos = colValues.get(4);
    	
    	sqlStatement = "select c.cde_carrier from t_tpl_carrier c "+
    			"where c.nam_bus <> ' ' "+
    			"and not exists (select 1 from T_TPL_CASH_RECEIPT r where r.sak_carrier = c.sak_carrier) "+
    			"and c.cde_carrier > dbms_random.value * 6300000 "+
    			"and rownum < 2 ";
    	colNames.add("CDE_CARRIER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String carrier = colValues.get(0);

    	cde_unit="ACC1";
    	num_check=Common.generateRandomTaxID().substring(0,8);
    	name_on_check=Common.generateRandomName();
    	amt_paid=String.format("%.2f", Double.parseDouble(claimDtlPaidAmt)+100); //Making check amount to be claim detail paid amount +$100;
        dte_check_numwire=Common.convertDatetoInt(Common.convertSysdate()); //Today's date
        cde_carrier=carrier;
        num_ref=Common.generateRandomTaxID().substring(0,3);
        num_icn_tcn=fetchedICN; 
        num_dtl="1";
        id_medicaid=memID;
        clm_dtl_recovery_amt="1.42";
        dte_first_svc=fdos;
        dte_last_svc=tdos;
        amt_adjustment="800000.58"; //To make adj amt > line paid amt
        
        txnType="ACCS";
        recoveryFileContents = getEFT()+getClaimRecovery()+getAdjustment();
        createRecoveryFile(recoveryFileContents);
        transferFile();
        
        //Store Data for Day_2
    	SelSql="select * from r_day2 where TC = '31461accs' and DES='num_icn_tcn'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '31461accs' and DES='num_icn_tcn'";
    	InsSql="insert into r_day2 values ('31461accs', '"+num_icn_tcn+"', 'num_icn_tcn', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);

    }
    
    @Test
    public void test31461_ACCS_PartB() throws Exception{
    	TestNGCustom.TCNo="31461_ACCS_PartB";
    	log("//TC 31461_ACCS_PartB - Adjustment-icn - adj amt > line paid amt ; CT M");
    	
    	sqlStatement = "select * from r_day2 where TC = '31456accs' and DES='num_icn_tcn' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		num_icn_tcn=colValues.get(0);
			    
	    //Validate report
    	//Get the report name
		String command, error, resp;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl91002.rpt.* | tail -1"; 
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("TPL INBOUND RECOVERY ERROR REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		command = "grep "+num_icn_tcn+" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the ICN:"+num_icn_tcn;
		resp = Common.connectUNIX(command, error);
		log(resp);
		
		command = "grep \"ADJUSTMENT AMOUNT IS GREATER THAN CLAIM DETAIL PAID AMOUNT\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"ADJUSTMENT AMOUNT IS GREATER THAN CLAIM DETAIL PAID AMOUNT\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
		
    }
    
    //TPL Resource HMS SOHEMA TCs
    @Test
    public void test33853_SOHEMA_PartA() throws Exception{
    	TestNGCustom.TCNo="33853_SOHEMA_PartA";
    	log("//TC 33853_SOHEMA_PartA - Add mutliple resource segments-ship Test Case");
    	
    	sqlStatement = "select b.id_medicaid from T_Re_base b, t_tpl_resource r "+ 
    					"where b.ind_active = 'Y' "+
    					"and b.dte_death = 0 "+
    					"and b.sak_recip<>r.sak_recip "+
    					"and b.sak_recip > dbms_random.value * 6300000 "+
    					"and rownum<2";

    	colNames.add("ID_MEDICAID");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String memID = colValues.get(0);
    	
    	sqlStatement = "select c.cde_carrier from t_tpl_carrier c "+
    			"where c.nam_bus <> ' ' "+
    			"and not exists (select 1 from T_TPL_CASH_RECEIPT r where r.sak_carrier = c.sak_carrier) "+
    			"and c.cde_carrier > dbms_random.value * 6300000 "+
    			"and rownum < 2 ";
    	colNames.add("CDE_CARRIER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String carrier = colValues.get(0);
    	
    	sqlStatement = "select * from t_tpl_employer e "+
    				"where e.nam_bus like '%BOS%' "+
    				"and e.cde_employer > dbms_random.value * 6300000 "+
    				"and rownum<2";
    	colNames.add("CDE_EMPLOYER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String emp = colValues.get(0);

    	res_mem=memID;
    	res_carrier=carrier;
    	cde_employer=emp;
    	num_tpl_policy_res=Common.generateRandomTaxID();
    	nam_plan=Common.generateRandomName();
    	dte_med_support_begin=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdate());
    	
    	//Add coverage 1
    	oi_plan_identifier="3";
    	cov_dte_effective=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdate());
    	cov_dte_end=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdatecustom(365));
    	String cov1_dte_effective = Common.convertDate_yyyyHIFENmmHIFENddToRegular(cov_dte_effective); //To store in DB
    	String cov1_dte_end = Common.convertDate_yyyyHIFENmmHIFENddToRegular(cov_dte_end); //To store in DB  	
    	cde_pgm_health_res_cov="OI02";
    	String cov1=getCov_tplREShmsSOHEMA();
    	
    	//Add coverage 2
    	coverage_sequence="-2";
    	oi_plan_identifier="17";
    	cov_dte_effective=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdatecustom(5));
    	cov_dte_end=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdatecustom(370));
    	cde_pgm_health_res_cov="OI17";
    	String cov2_dte_effective = Common.convertDate_yyyyHIFENmmHIFENddToRegular(cov_dte_effective); //To store in DB
    	String cov2_dte_end = Common.convertDate_yyyyHIFENmmHIFENddToRegular(cov_dte_end); //To store in DB   		
    	String cov2=getCov_tplREShmsSOHEMA();
    	
    	owner_nam_last=Common.generateRandomName();
    	owner_nam_first=Common.generateRandomName();
    	owner_num_ssn="1"+Common.generateRandomTaxID().substring(0,8);
    	owner_dte_birth=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdatecustom(-10950)); //30 years old
        
    	txnType_res="SOHEMA";
        resourceFileContents = getOuterTag_tplREShmsSOHEMA_start()+getInnerTag_tplREShmsSOHEMA_start()+getMem_tplREShmsSOHEMA_start()+getRes_tplREShmsSOHEMA_start()+cov1+cov2+getPol_tplREShmsSOHEMA()+getRes_tplREShmsSOHEMA_end()+getMem_tplREShmsSOHEMA_end()+getInnerTag_tplREShmsSOHEMA_end()+getOuterTag_tplREShmsSOHEMA_end();
        createResourceFile(resourceFileContents);
        transferFile_TPLresource();
        
        //Store Data for Day_2
    	SelSql="select * from r_day2 where TC = '33853sohema' and DES='res_mem'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33853sohema' and DES='res_mem'";
    	InsSql="insert into r_day2 values ('33853sohema', '"+res_mem+"', 'res_mem', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33853sohema' and DES='res_carrier'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33853sohema' and DES='res_carrier'";
    	InsSql="insert into r_day2 values ('33853sohema', '"+res_carrier+"', 'res_carrier', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33853sohema' and DES='cde_relation'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33853sohema' and DES='cde_relation'";
    	InsSql="insert into r_day2 values ('33853sohema', '"+cde_relation+"', 'cde_relation', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33853sohema' and DES='owner_nam_last'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33853sohema' and DES='owner_nam_last'";
    	InsSql="insert into r_day2 values ('33853sohema', '"+owner_nam_last+"', 'owner_nam_last', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33853sohema' and DES='owner_nam_first'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33853sohema' and DES='owner_nam_first'";
    	InsSql="insert into r_day2 values ('33853sohema', '"+owner_nam_first+"', 'owner_nam_first', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33853sohema' and DES='num_tpl_policy_res'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33853sohema' and DES='num_tpl_policy_res'";
    	InsSql="insert into r_day2 values ('33853sohema', '"+num_tpl_policy_res+"', 'num_tpl_policy_res', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33853sohema' and DES='cov1_dte_effective'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33853sohema' and DES='cov1_dte_effective'";
    	InsSql="insert into r_day2 values ('33853sohema', '"+cov1_dte_effective+"', 'cov1_dte_effective', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33853sohema' and DES='cov1_dte_end'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33853sohema' and DES='cov1_dte_end'";
    	InsSql="insert into r_day2 values ('33853sohema', '"+cov1_dte_end+"', 'cov1_dte_end', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33853sohema' and DES='cov2_dte_effective'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33853sohema' and DES='cov2_dte_effective'";
    	InsSql="insert into r_day2 values ('33853sohema', '"+cov2_dte_effective+"', 'cov2_dte_effective', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33853sohema' and DES='cov2_dte_end'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33853sohema' and DES='cov2_dte_end'";
    	InsSql="insert into r_day2 values ('33853sohema', '"+cov2_dte_end+"', 'cov2_dte_end', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33853sohema' and DES='cde_employer'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33853sohema' and DES='cde_employer'";
    	InsSql="insert into r_day2 values ('33853sohema', '"+cde_employer+"', 'cde_employer', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33853sohema' and DES='nam_plan'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33853sohema' and DES='nam_plan'";
    	InsSql="insert into r_day2 values ('33853sohema', '"+nam_plan+"', 'nam_plan', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	resource_sohemaGDGcount++;
    	SelSql="select * from r_day2 where TC = '33853sohema' and DES='sohema_tail'"; //To fetch the correct report on day 2 as each file is producing a separate report, even in the same run
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33853sohema' and DES='sohema_tail'";
    	InsSql="insert into r_day2 values ('33853sohema', '"+resource_sohemaGDGcount+"', 'sohema_tail', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);

    }
    
    @Test
    public void test33853_SOHEMA_PartB() throws Exception{
    	TestNGCustom.TCNo="33853_SOHEMA_PartB";
    	log("//TC 33853_SOHEMA_PartB - Add mutliple resource segments-ship Test Case");
    	
    	log("For test data that was validated for this TC, please refer to the query select * from r_day2 where TC = '33853sohema'");
    	
    	//Get Data for Day_2
		sqlStatement = "select * from r_day2 where TC = '33853sohema' and DES='res_mem' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colNames.add("DATE_REQUESTED");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no resource created for this TC");
		res_mem=colValues.get(0);
		String resDate=colValues.get(1);
		
		sqlStatement = "select * from r_day2 where TC = '33853sohema' and DES='amt_paid' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		amt_paid=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33853sohema' and DES='res_carrier' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		res_carrier=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33853sohema' and DES='cde_relation' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		cde_relation=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33853sohema' and DES='owner_nam_last' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		owner_nam_last=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33853sohema' and DES='owner_nam_first' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		owner_nam_first=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33853sohema' and DES='num_tpl_policy_res' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		num_tpl_policy_res=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33853sohema' and DES='cov1_dte_effective' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		String cov1_dte_effective=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33853sohema' and DES='cov1_dte_end' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		String cov1_dte_end=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33853sohema' and DES='cov2_dte_effective' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		String cov2_dte_effective=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33853sohema' and DES='cov2_dte_end' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		String cov2_dte_end=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33853sohema' and DES='cde_employer' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		cde_employer=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33853sohema' and DES='nam_plan' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		nam_plan=colValues.get(0);
		
		//Navigate to TPL search panel and search for the member
		searchTPLMem(res_mem);
		
		//Validate Base information
	    driver.findElement(By.linkText("Base Information")).click();

	 	String tplMember=driver.findElement(By.xpath("//input[contains(@id,'TPLCurrentId')]")).getAttribute("value").trim();
	    Assert.assertTrue(tplMember.equals(res_mem), "In TPL Base info panel, couldn't find member: "+res_mem+". Instead got member: "+tplMember);

	 	String tplCarrier=driver.findElement(By.xpath("//input[contains(@id,'TPLcarrierNumber')]")).getAttribute("value").trim();
	    Assert.assertTrue(tplCarrier.equals(res_carrier), "In TPL Base info panel, couldn't find carrier: "+res_carrier+". Instead got carrier: "+tplCarrier);
	    
	 	String TPLEmployerID=driver.findElement(By.xpath("//input[contains(@id,'TPLEmployerID')]")).getAttribute("value").trim();
	    Assert.assertTrue(TPLEmployerID.equals(cde_employer), "In TPL Base info panel, couldn't find TPLEmployerID: "+res_mem+". Instead got TPLEmployerID: "+tplMember);

	    String BillToCode=new Select(driver.findElement(By.xpath("//Select[contains(@id,'BillToCode')]"))).getFirstSelectedOption().getText();
	    Assert.assertTrue(BillToCode.equals("Carrier"), "In TPL Base info panel, couldn't find BillToCode: 'Carrier'. Instead got BillToCode: "+BillToCode);
	    
	 	String TPLRelationship=driver.findElement(By.xpath("//input[contains(@id,'TPLRelationship')]")).getAttribute("value").trim();
	    Assert.assertTrue(TPLRelationship.equals("18"), "In TPL Base info panel, couldn't find TPLRelationship: '18'. Instead got TPLRelationship: "+TPLRelationship);

	    sqlStatement = "select SAK_POL_HOLD from t_policy_holder e where e.nam_last = '"+owner_nam_last+"'";
	    colNames.add("SAK_POL_HOLD");
	    colValues=Common.executeQuery(sqlStatement, colNames);
	    String filePolicyHolder=colValues.get(0);
	 	String TPLPolicyHolder=driver.findElement(By.xpath("//input[contains(@id,'TPLPolicyHolderSearch')]")).getAttribute("value").trim();
	    Assert.assertTrue(TPLPolicyHolder.equals(filePolicyHolder), "In TPL Base info panel, couldn't find TPLPolicyHolder: "+filePolicyHolder+". Instead got TPLPolicyHolder: "+TPLPolicyHolder);
 
	    String PolicyHolderName=driver.findElement(By.xpath("//input[contains(@id,'PolicyHolderName')]")).getAttribute("value").trim();
	    Assert.assertTrue(PolicyHolderName.contains(owner_nam_last), "In TPL Base info panel, couldn't find PolicyHolderLastName: "+owner_nam_last+". Instead got PolicyHolderLastName: "+PolicyHolderName);
	    Assert.assertTrue(PolicyHolderName.contains(owner_nam_first), "In TPL Base info panel, couldn't find PolicyHolderFirstName: "+owner_nam_first+". Instead got PolicyHolderFirstName: "+PolicyHolderName);
	    
	    String TplPolicyNumber=driver.findElement(By.xpath("//input[contains(@id,'TplPolicyNumber')]")).getAttribute("value").trim();
	    Assert.assertTrue(TplPolicyNumber.equals(num_tpl_policy_res), "In TPL Base info panel, couldn't find TplPolicyNumber: "+num_tpl_policy_res+". Instead got TplPolicyNumber: "+TplPolicyNumber);
	    
	    String PlanName=driver.findElement(By.xpath("//input[contains(@id,'PlanName')]")).getAttribute("value").trim();
	    Assert.assertTrue(PlanName.equals(nam_plan), "In TPL Base info panel, couldn't find PlanName: "+nam_plan+". Instead got PlanName: "+PlanName);
	    
	 	String OriginCode=new Select(driver.findElement(By.xpath("//Select[contains(@id,'OriginCode')]"))).getFirstSelectedOption().getText();
	    Assert.assertTrue(OriginCode.equals("SOHEMA"), "In TPL Base info panel, couldn't find OriginCode: 'SOHEMA'. Instead got OriginCode: "+OriginCode);
	    
	 	String CdeInitOrg=new Select(driver.findElement(By.xpath("//Select[contains(@id,'CdeInitOrg')]"))).getFirstSelectedOption().getText();
	    Assert.assertTrue(CdeInitOrg.equals("SOHEMA"), "In TPL Base info panel, couldn't find LastChangeOrigin: 'SOHEMA'. Instead got LastChangeOrigin: "+CdeInitOrg);
	    
	    Common.cancelAll();
	    log("TPL Base information is correct");
	    
		//Validate Coverage
	    driver.findElement(By.linkText("Coverage")).click();
	    //Sort by OI plan code ascending
	    driver.findElement(By.xpath("//*[contains(@id,'otherInsurancePlanDescription')]")).click();
	    driver.findElement(By.xpath("//*[contains(@id,'otherInsurancePlanCode')]")).click();
	    
	    String cov1=driver.findElement(By.xpath("//*[contains(@id,'CoverageList_0:CoverageXrefBean_ColValue_otherInsurancePlanCode')]")).getText();
	    Assert.assertTrue(cov1.equals("OI02"), "In TPL Resource Coverage panel, couldn't find first Coverage plan as: OI02. Instead got first Coverage plan as: "+cov1);
	    
	    String cov1EffDt=driver.findElement(By.xpath("//*[contains(@id,'CoverageList_0:CoverageXrefBean_ColValue_effectiveDate')]")).getText();
	    Assert.assertTrue(cov1EffDt.equals(cov1_dte_effective), "In TPL Resource Coverage panel, couldn't find first Coverage eff date as: "+cov1_dte_effective+". Instead got first Coverage eff date as: "+cov1EffDt);
	    
	    String cov1EndDt=driver.findElement(By.xpath("//*[contains(@id,'CoverageList_0:CoverageXrefBean_ColValue_endDate')]")).getText();
	    Assert.assertTrue(cov1EndDt.equals(cov1_dte_end), "In TPL Resource Coverage panel, couldn't find first Coverage end date as: "+cov1_dte_end+". Instead got first Coverage end date as: "+cov1EndDt);
	    
	    String cov2=driver.findElement(By.xpath("//*[contains(@id,'CoverageList_1:CoverageXrefBean_ColValue_otherInsurancePlanCode')]")).getText();
	    Assert.assertTrue(cov2.equals("OI17"), "In TPL Resource Coverage panel, couldn't find second Coverage plan as: OI17. Instead got second Coverage plan as: "+cov2);
	    
	    String cov2EffDt=driver.findElement(By.xpath("//*[contains(@id,'CoverageList_1:CoverageXrefBean_ColValue_effectiveDate')]")).getText();
	    Assert.assertTrue(cov2EffDt.equals(cov2_dte_effective), "In TPL Resource Coverage panel, couldn't find second Coverage eff date as: "+cov2_dte_effective+". Instead got second Coverage eff date as: "+cov2EffDt);
	    
	    String cov2EndDt=driver.findElement(By.xpath("//*[contains(@id,'CoverageList_1:CoverageXrefBean_ColValue_endDate')]")).getText();
	    Assert.assertTrue(cov2EndDt.equals(cov2_dte_end), "In TPL Resource Coverage panel, couldn't find second Coverage end date as: "+cov2_dte_end+". Instead got second Coverage end date as: "+cov2EndDt);
	    
	    log("TPL Resource Coverage information is correct");

	    //Validate report
    	//Get the report name
	    txnType_res="SOHEMA";
		String command, error, resp;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl0920d.rpt.* | tail -"+getTail_TPLresource("33853sohema", txnType_res);
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("SOHEMA CONTRACTOR INTERFACE ERROR REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		command = "awk 'c-->0;$0~s{if(b)for(c=b+1;c>1;c--)print r[(NR-c+1)%b];print;c=a}b{r[NR%b]=$0}' b=0 a=4 s="+res_mem+" "+fileName; //. This is to print the next 4 lines after grep on string. "b" and "a" are the number of lines to print before and after string res_mem
		error = "The selected file is not correct/ or it is empty/ or it does not have the member: "+res_mem+" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
	    Assert.assertTrue(resp.contains("Resource Added"), "The selected file does not have the 'Resource Added' string in it");
	    Assert.assertTrue(resp.contains("Coverage Added"), "The selected file does not have the 'Coverage Added' string in it");
	    Assert.assertTrue(resp.contains("Policy Holder Added"), "The selected file does not have the 'Policy Holder Added' string in it");
	    
	    sqlStatement = "select sak_tpl_resource from t_tpl_resource where sak_recip = (select sak_recip from t_re_base where id_medicaid = "+res_mem+")"; //Get sak_tpl-resource
	    colNames.add("SAK_TPL_RESOURCE");
	    colValues=Common.executeQuery(sqlStatement, colNames);
	    String SAK_TPL_RESOURCE=colValues.get(0);
		command = "grep \"  RESOURCE:   "+SAK_TPL_RESOURCE+"  CARRIER CODE: "+res_carrier+"  EMPLOYER CODE: "+cde_employer+"  POL TYPE : I   POL NUM: "+num_tpl_policy_res+"\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"  RESOURCE:   "+SAK_TPL_RESOURCE+"  CARRIER CODE: "+res_carrier+"  EMPLOYER CODE: "+cde_employer+"  POL TYPE : I   POL NUM: "+num_tpl_policy_res+"\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);

		command = "grep \"  COVERAGE SEQ:         1  EFF DTE: "+Common.convertDatetoInt(cov1_dte_effective)+"   END DTE: "+Common.convertDatetoInt(cov1_dte_end)+"   OI: OI02\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"  COVERAGE SEQ:         1  EFF DTE: "+Common.convertDatetoInt(cov1_dte_effective)+"   END DTE: "+Common.convertDatetoInt(cov1_dte_end)+"   OI: OI02\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
		
		command = "grep \"  COVERAGE SEQ:         2  EFF DTE: "+Common.convertDatetoInt(cov2_dte_effective)+"   END DTE: "+Common.convertDatetoInt(cov2_dte_end)+"   OI: OI17\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"  COVERAGE SEQ:         2  EFF DTE: "+Common.convertDatetoInt(cov2_dte_effective)+"   END DTE: "+Common.convertDatetoInt(cov2_dte_end)+"   OI: OI17\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
		
		command = "grep \"  POLICY OWNER:   "+filePolicyHolder+" LAST NAME: "+owner_nam_last+"             FIRST NAME: "+owner_nam_first+"\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"  POLICY OWNER:   "+filePolicyHolder+" LAST NAME: "+owner_nam_last+"             FIRST NAME: "+owner_nam_first+"\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
    }
    
    @Test
    public void test33854_HMS_PartA() throws Exception{
    	TestNGCustom.TCNo="33854_HMS_PartA";
    	log("//TC 33854_HMS_PartA - Add new resource; Good cause with value Y- hms Test Case");
    	
    	sqlStatement = "select b.id_medicaid from T_Re_base b, t_tpl_resource r "+ 
    					"where b.ind_active = 'Y' "+
    					"and b.dte_death = 0 "+
    					"and b.sak_recip<>r.sak_recip "+
    					"and b.sak_recip > dbms_random.value * 6300000 "+
    					"and rownum<2";

    	colNames.add("ID_MEDICAID");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String memID = colValues.get(0);
    	
    	sqlStatement = "select c.cde_carrier from t_tpl_carrier c "+
    			"where c.nam_bus <> ' ' "+
    			"and not exists (select 1 from T_TPL_CASH_RECEIPT r where r.sak_carrier = c.sak_carrier) "+
    			"and c.cde_carrier > dbms_random.value * 6300000 "+
    			"and rownum < 2 ";
    	colNames.add("CDE_CARRIER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String carrier = colValues.get(0);
    	
    	sqlStatement = "select * from t_tpl_employer e "+
    				"where e.nam_bus like '%BOS%' "+
    				"and e.cde_employer > dbms_random.value * 6300000 "+
    				"and rownum<2";
    	colNames.add("CDE_EMPLOYER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String emp = colValues.get(0);

    	res_mem=memID;
    	res_carrier=carrier;
    	cde_employer=emp;
    	num_tpl_policy_res=Common.generateRandomTaxID();
    	nam_plan=Common.generateRandomName();
    	dte_med_support_begin=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdate());
    	
    	//Add coverage 1
    	oi_plan_identifier="3";
    	cov_dte_effective=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdate());
    	cov_dte_end=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdatecustom(365));
    	String cov1_dte_effective = Common.convertDate_yyyyHIFENmmHIFENddToRegular(cov_dte_effective); //To store in DB
    	String cov1_dte_end = Common.convertDate_yyyyHIFENmmHIFENddToRegular(cov_dte_end); //To store in DB  	
    	cde_pgm_health_res_cov="OI02";
    	String cov1=getCov_tplREShmsSOHEMA();
    	
    	//Add coverage 2
    	coverage_sequence="-2";
    	oi_plan_identifier="17";
    	cov_dte_effective=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdatecustom(5));
    	cov_dte_end=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdatecustom(370));
    	cde_pgm_health_res_cov="OI17";
    	String cov2_dte_effective = Common.convertDate_yyyyHIFENmmHIFENddToRegular(cov_dte_effective); //To store in DB
    	String cov2_dte_end = Common.convertDate_yyyyHIFENmmHIFENddToRegular(cov_dte_end); //To store in DB   		
    	String cov2=getCov_tplREShmsSOHEMA();
    	
    	owner_nam_last=Common.generateRandomName();
    	owner_nam_first=Common.generateRandomName();
    	owner_num_ssn="1"+Common.generateRandomTaxID().substring(0,8);
    	owner_dte_birth=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdatecustom(-10950)); //30 years old
        
        resourceFileContents = getOuterTag_tplREShmsSOHEMA_start()+getInnerTag_tplREShmsSOHEMA_start()+getMem_tplREShmsSOHEMA_start()+getRes_tplREShmsSOHEMA_start()+cov1+cov2+getPol_tplREShmsSOHEMA()+getRes_tplREShmsSOHEMA_end()+getMem_tplREShmsSOHEMA_end()+getInnerTag_tplREShmsSOHEMA_end()+getOuterTag_tplREShmsSOHEMA_end();
        createResourceFile(resourceFileContents);
        transferFile_TPLresource();
        
        //Store Data for Day_2
    	SelSql="select * from r_day2 where TC = '33854hms' and DES='res_mem'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33854hms' and DES='res_mem'";
    	InsSql="insert into r_day2 values ('33854hms', '"+res_mem+"', 'res_mem', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33854hms' and DES='res_carrier'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33854hms' and DES='res_carrier'";
    	InsSql="insert into r_day2 values ('33854hms', '"+res_carrier+"', 'res_carrier', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33854hms' and DES='cde_relation'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33854hms' and DES='cde_relation'";
    	InsSql="insert into r_day2 values ('33854hms', '"+cde_relation+"', 'cde_relation', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33854hms' and DES='owner_nam_last'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33854hms' and DES='owner_nam_last'";
    	InsSql="insert into r_day2 values ('33854hms', '"+owner_nam_last+"', 'owner_nam_last', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33854hms' and DES='owner_nam_first'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33854hms' and DES='owner_nam_first'";
    	InsSql="insert into r_day2 values ('33854hms', '"+owner_nam_first+"', 'owner_nam_first', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33854hms' and DES='num_tpl_policy_res'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33854hms' and DES='num_tpl_policy_res'";
    	InsSql="insert into r_day2 values ('33854hms', '"+num_tpl_policy_res+"', 'num_tpl_policy_res', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33854hms' and DES='cov1_dte_effective'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33854hms' and DES='cov1_dte_effective'";
    	InsSql="insert into r_day2 values ('33854hms', '"+cov1_dte_effective+"', 'cov1_dte_effective', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33854hms' and DES='cov1_dte_end'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33854hms' and DES='cov1_dte_end'";
    	InsSql="insert into r_day2 values ('33854hms', '"+cov1_dte_end+"', 'cov1_dte_end', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33854hms' and DES='cov2_dte_effective'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33854hms' and DES='cov2_dte_effective'";
    	InsSql="insert into r_day2 values ('33854hms', '"+cov2_dte_effective+"', 'cov2_dte_effective', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33854hms' and DES='cov2_dte_end'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33854hms' and DES='cov2_dte_end'";
    	InsSql="insert into r_day2 values ('33854hms', '"+cov2_dte_end+"', 'cov2_dte_end', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33854hms' and DES='cde_employer'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33854hms' and DES='cde_employer'";
    	InsSql="insert into r_day2 values ('33854hms', '"+cde_employer+"', 'cde_employer', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33854hms' and DES='nam_plan'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33854hms' and DES='nam_plan'";
    	InsSql="insert into r_day2 values ('33854hms', '"+nam_plan+"', 'nam_plan', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	resource_hmsGDGcount++;
    	SelSql="select * from r_day2 where TC = '33854hms' and DES='hms_tail'"; //To fetch the correct report on day 2 as each file is producing a separate report, even in the same run
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33854hms' and DES='hms_tail'";
    	InsSql="insert into r_day2 values ('33854hms', '"+resource_hmsGDGcount+"', 'hms_tail', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);

    }
    
    @Test
    public void test33854_HMS_PartB() throws Exception{
    	TestNGCustom.TCNo="33854_HMS_PartB";
    	log("//TC 33854_HMS_PartB - Add new resource; Good cause with value Y- hms Test Case");
    	
    	log("For test data that was validated for this TC, please refer to the query select * from r_day2 where TC = '33854hms'");
    	
    	//Get Data for Day_2
		sqlStatement = "select * from r_day2 where TC = '33854hms' and DES='res_mem' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colNames.add("DATE_REQUESTED");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no resource created for this TC");
		res_mem=colValues.get(0);
		String resDate=colValues.get(1);
		
		sqlStatement = "select * from r_day2 where TC = '33854hms' and DES='amt_paid' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		amt_paid=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33854hms' and DES='res_carrier' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		res_carrier=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33854hms' and DES='cde_relation' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		cde_relation=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33854hms' and DES='owner_nam_last' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		owner_nam_last=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33854hms' and DES='owner_nam_first' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		owner_nam_first=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33854hms' and DES='num_tpl_policy_res' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		num_tpl_policy_res=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33854hms' and DES='cov1_dte_effective' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		String cov1_dte_effective=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33854hms' and DES='cov1_dte_end' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		String cov1_dte_end=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33854hms' and DES='cov2_dte_effective' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		String cov2_dte_effective=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33854hms' and DES='cov2_dte_end' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		String cov2_dte_end=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33854hms' and DES='cde_employer' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		cde_employer=colValues.get(0);
		
		sqlStatement = "select * from r_day2 where TC = '33854hms' and DES='nam_plan' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		nam_plan=colValues.get(0);
		
		//Navigate to TPL search panel and search for the member
		searchTPLMem(res_mem);
		
		//Validate Base information
	    driver.findElement(By.linkText("Base Information")).click();

	 	String tplMember=driver.findElement(By.xpath("//input[contains(@id,'TPLCurrentId')]")).getAttribute("value").trim();
	    Assert.assertTrue(tplMember.equals(res_mem), "In TPL Base info panel, couldn't find member: "+res_mem+". Instead got member: "+tplMember);

	 	String tplCarrier=driver.findElement(By.xpath("//input[contains(@id,'TPLcarrierNumber')]")).getAttribute("value").trim();
	    Assert.assertTrue(tplCarrier.equals(res_carrier), "In TPL Base info panel, couldn't find carrier: "+res_carrier+". Instead got carrier: "+tplCarrier);
	    
	 	String TPLEmployerID=driver.findElement(By.xpath("//input[contains(@id,'TPLEmployerID')]")).getAttribute("value").trim();
	    Assert.assertTrue(TPLEmployerID.equals(cde_employer), "In TPL Base info panel, couldn't find TPLEmployerID: "+res_mem+". Instead got TPLEmployerID: "+tplMember);

	    String BillToCode=new Select(driver.findElement(By.xpath("//Select[contains(@id,'BillToCode')]"))).getFirstSelectedOption().getText();
	    Assert.assertTrue(BillToCode.equals("Carrier"), "In TPL Base info panel, couldn't find BillToCode: 'Carrier'. Instead got BillToCode: "+BillToCode);
	    
	 	String TPLRelationship=driver.findElement(By.xpath("//input[contains(@id,'TPLRelationship')]")).getAttribute("value").trim();
	    Assert.assertTrue(TPLRelationship.equals("18"), "In TPL Base info panel, couldn't find TPLRelationship: '18'. Instead got TPLRelationship: "+TPLRelationship);

	    sqlStatement = "select SAK_POL_HOLD from t_policy_holder e where e.nam_last = '"+owner_nam_last+"'";
	    colNames.add("SAK_POL_HOLD");
	    colValues=Common.executeQuery(sqlStatement, colNames);
	    String filePolicyHolder=colValues.get(0);
	 	String TPLPolicyHolder=driver.findElement(By.xpath("//input[contains(@id,'TPLPolicyHolderSearch')]")).getAttribute("value").trim();
	    Assert.assertTrue(TPLPolicyHolder.equals(filePolicyHolder), "In TPL Base info panel, couldn't find TPLPolicyHolder: "+filePolicyHolder+". Instead got TPLPolicyHolder: "+TPLPolicyHolder);
 
	    String PolicyHolderName=driver.findElement(By.xpath("//input[contains(@id,'PolicyHolderName')]")).getAttribute("value").trim();
	    Assert.assertTrue(PolicyHolderName.contains(owner_nam_last), "In TPL Base info panel, couldn't find PolicyHolderLastName: "+owner_nam_last+". Instead got PolicyHolderLastName: "+PolicyHolderName);
	    Assert.assertTrue(PolicyHolderName.contains(owner_nam_first), "In TPL Base info panel, couldn't find PolicyHolderFirstName: "+owner_nam_first+". Instead got PolicyHolderFirstName: "+PolicyHolderName);
	    
	    String TplPolicyNumber=driver.findElement(By.xpath("//input[contains(@id,'TplPolicyNumber')]")).getAttribute("value").trim();
	    Assert.assertTrue(TplPolicyNumber.equals(num_tpl_policy_res), "In TPL Base info panel, couldn't find TplPolicyNumber: "+num_tpl_policy_res+". Instead got TplPolicyNumber: "+TplPolicyNumber);
	    
	    String PlanName=driver.findElement(By.xpath("//input[contains(@id,'PlanName')]")).getAttribute("value").trim();
	    Assert.assertTrue(PlanName.equals(nam_plan), "In TPL Base info panel, couldn't find PlanName: "+nam_plan+". Instead got PlanName: "+PlanName);
	    
	 	String OriginCode=new Select(driver.findElement(By.xpath("//Select[contains(@id,'OriginCode')]"))).getFirstSelectedOption().getText();
	    Assert.assertTrue(OriginCode.equals("SOHEMA"), "In TPL Base info panel, couldn't find OriginCode: 'SOHEMA'. Instead got OriginCode: "+OriginCode);
	    
	 	String CdeInitOrg=new Select(driver.findElement(By.xpath("//Select[contains(@id,'CdeInitOrg')]"))).getFirstSelectedOption().getText();
	    Assert.assertTrue(CdeInitOrg.equals("SOHEMA"), "In TPL Base info panel, couldn't find LastChangeOrigin: 'SOHEMA'. Instead got LastChangeOrigin: "+CdeInitOrg);
	    
	    Common.cancelAll();
	    log("TPL Base information is correct");
	    
		//Validate Coverage
	    driver.findElement(By.linkText("Coverage")).click();
	    //Sort by OI plan code ascending
	    driver.findElement(By.xpath("//*[contains(@id,'otherInsurancePlanDescription')]")).click();
	    driver.findElement(By.xpath("//*[contains(@id,'otherInsurancePlanCode')]")).click();
	    
	    String cov1=driver.findElement(By.xpath("//*[contains(@id,'CoverageList_0:CoverageXrefBean_ColValue_otherInsurancePlanCode')]")).getText();
	    Assert.assertTrue(cov1.equals("OI02"), "In TPL Resource Coverage panel, couldn't find first Coverage plan as: OI02. Instead got first Coverage plan as: "+cov1);
	    
	    String cov1EffDt=driver.findElement(By.xpath("//*[contains(@id,'CoverageList_0:CoverageXrefBean_ColValue_effectiveDate')]")).getText();
	    Assert.assertTrue(cov1EffDt.equals(cov1_dte_effective), "In TPL Resource Coverage panel, couldn't find first Coverage eff date as: "+cov1_dte_effective+". Instead got first Coverage eff date as: "+cov1EffDt);
	    
	    String cov1EndDt=driver.findElement(By.xpath("//*[contains(@id,'CoverageList_0:CoverageXrefBean_ColValue_endDate')]")).getText();
	    Assert.assertTrue(cov1EndDt.equals(cov1_dte_end), "In TPL Resource Coverage panel, couldn't find first Coverage end date as: "+cov1_dte_end+". Instead got first Coverage end date as: "+cov1EndDt);
	    
	    String cov2=driver.findElement(By.xpath("//*[contains(@id,'CoverageList_1:CoverageXrefBean_ColValue_otherInsurancePlanCode')]")).getText();
	    Assert.assertTrue(cov2.equals("OI17"), "In TPL Resource Coverage panel, couldn't find second Coverage plan as: OI17. Instead got second Coverage plan as: "+cov2);
	    
	    String cov2EffDt=driver.findElement(By.xpath("//*[contains(@id,'CoverageList_1:CoverageXrefBean_ColValue_effectiveDate')]")).getText();
	    Assert.assertTrue(cov2EffDt.equals(cov2_dte_effective), "In TPL Resource Coverage panel, couldn't find second Coverage eff date as: "+cov2_dte_effective+". Instead got second Coverage eff date as: "+cov2EffDt);
	    
	    String cov2EndDt=driver.findElement(By.xpath("//*[contains(@id,'CoverageList_1:CoverageXrefBean_ColValue_endDate')]")).getText();
	    Assert.assertTrue(cov2EndDt.equals(cov2_dte_end), "In TPL Resource Coverage panel, couldn't find second Coverage end date as: "+cov2_dte_end+". Instead got second Coverage end date as: "+cov2EndDt);
	    
	    log("TPL Resource Coverage information is correct");

	    //Validate report
    	//Get the report name
		String command, error, resp;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl0960d.rpt.* | tail -"+getTail_TPLresource("33854hms", txnType_res);
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("HMS CONTRACTOR INTERFACE ERROR REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		command = "awk 'c-->0;$0~s{if(b)for(c=b+1;c>1;c--)print r[(NR-c+1)%b];print;c=a}b{r[NR%b]=$0}' b=0 a=4 s="+res_mem+" "+fileName; //. This is to print the next 4 lines after grep on string. "b" and "a" are the number of lines to print before and after string res_mem
		error = "The selected file is not correct/ or it is empty/ or it does not have the member: "+res_mem+" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
	    Assert.assertTrue(resp.contains("Resource Added"), "The selected file does not have the 'Resource Added' string in it");
	    Assert.assertTrue(resp.contains("Coverage Added"), "The selected file does not have the 'Coverage Added' string in it");
	    Assert.assertTrue(resp.contains("Policy Holder Added"), "The selected file does not have the 'Policy Holder Added' string in it");
	    
	    sqlStatement = "select sak_tpl_resource from t_tpl_resource where sak_recip = (select sak_recip from t_re_base where id_medicaid = "+res_mem+")"; //Get sak_tpl-resource
	    colNames.add("SAK_TPL_RESOURCE");
	    colValues=Common.executeQuery(sqlStatement, colNames);
	    String SAK_TPL_RESOURCE=colValues.get(0);
		command = "grep \"  RESOURCE:   "+SAK_TPL_RESOURCE+"  CARRIER CODE: "+res_carrier+"  EMPLOYER CODE: "+cde_employer+"  POL TYPE : I   POL NUM: "+num_tpl_policy_res+"\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"  RESOURCE:   "+SAK_TPL_RESOURCE+"  CARRIER CODE: "+res_carrier+"  EMPLOYER CODE: "+cde_employer+"  POL TYPE : I   POL NUM: "+num_tpl_policy_res+"\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);

		command = "grep \"  COVERAGE SEQ:         1  EFF DTE: "+Common.convertDatetoInt(cov1_dte_effective)+"   END DTE: "+Common.convertDatetoInt(cov1_dte_end)+"   OI: OI02\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"  COVERAGE SEQ:         1  EFF DTE: "+Common.convertDatetoInt(cov1_dte_effective)+"   END DTE: "+Common.convertDatetoInt(cov1_dte_end)+"   OI: OI02\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
		
		command = "grep \"  COVERAGE SEQ:         2  EFF DTE: "+Common.convertDatetoInt(cov2_dte_effective)+"   END DTE: "+Common.convertDatetoInt(cov2_dte_end)+"   OI: OI17\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"  COVERAGE SEQ:         2  EFF DTE: "+Common.convertDatetoInt(cov2_dte_effective)+"   END DTE: "+Common.convertDatetoInt(cov2_dte_end)+"   OI: OI17\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
		
		command = "grep \"  POLICY OWNER:   "+filePolicyHolder+" LAST NAME: "+owner_nam_last+"             FIRST NAME: "+owner_nam_first+"\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"  POLICY OWNER:   "+filePolicyHolder+" LAST NAME: "+owner_nam_last+"             FIRST NAME: "+owner_nam_first+"\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
    }
    
    @Test
    public void test33870_HMS_PartA() throws Exception{
    	TestNGCustom.TCNo="33870_HMS_PartA";
    	log("//TC 33870_HMS_PartA - Invalid carrier code - insert-hms Test Case");
    	
    	sqlStatement = "select b.id_medicaid from T_Re_base b, t_tpl_resource r "+ 
    					"where b.ind_active = 'Y' "+
    					"and b.dte_death = 0 "+
    					"and b.sak_recip<>r.sak_recip "+
    					"and b.sak_recip > dbms_random.value * 6300000 "+
    					"and rownum<2";

    	colNames.add("ID_MEDICAID");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String memID = colValues.get(0);
    	
    	sqlStatement = "select * from t_tpl_employer e "+
    				"where e.nam_bus like '%BOS%' "+
    				"and e.cde_employer > dbms_random.value * 6300000 "+
    				"and rownum<2";
    	colNames.add("CDE_EMPLOYER");
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	String emp = colValues.get(0);

    	res_mem=memID;
    	res_carrier="1234567"; //invalid carrier code
    	cde_employer=emp;
    	num_tpl_policy_res=Common.generateRandomTaxID();
    	nam_plan=Common.generateRandomName();
    	dte_med_support_begin=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdate());
    	
    	//Add coverage 1
    	oi_plan_identifier="3";
    	cov_dte_effective=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdate());
    	cov_dte_end=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdatecustom(365)); 	
    	cde_pgm_health_res_cov="OI02";
    	String cov1=getCov_tplREShmsSOHEMA();
    	
    	//Add coverage 2
    	coverage_sequence="-2";
    	oi_plan_identifier="17";
    	cov_dte_effective=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdatecustom(5));
    	cov_dte_end=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdatecustom(370));
    	cde_pgm_health_res_cov="OI17"; 		
    	String cov2=getCov_tplREShmsSOHEMA();
    	
    	owner_nam_last=Common.generateRandomName();
    	owner_nam_first=Common.generateRandomName();
    	owner_num_ssn="1"+Common.generateRandomTaxID().substring(0,8);
    	owner_dte_birth=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdatecustom(-10950)); //30 years old
        
        resourceFileContents = getOuterTag_tplREShmsSOHEMA_start()+getInnerTag_tplREShmsSOHEMA_start()+getMem_tplREShmsSOHEMA_start()+getRes_tplREShmsSOHEMA_start()+cov1+cov2+getPol_tplREShmsSOHEMA()+getRes_tplREShmsSOHEMA_end()+getMem_tplREShmsSOHEMA_end()+getInnerTag_tplREShmsSOHEMA_end()+getOuterTag_tplREShmsSOHEMA_end();
        createResourceFile(resourceFileContents);
        transferFile_TPLresource();
        
        //Store Data for Day_2
    	SelSql="select * from r_day2 where TC = '33870hms' and DES='res_mem'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33870hms' and DES='res_mem'";
    	InsSql="insert into r_day2 values ('33870hms', '"+res_mem+"', 'res_mem', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	resource_hmsGDGcount++;
    	SelSql="select * from r_day2 where TC = '33870hms' and DES='hms_tail'"; //To fetch the correct report on day 2 as each file is producing a separate report, even in the same run
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33870hms' and DES='hms_tail'";
    	InsSql="insert into r_day2 values ('33870hms', '"+resource_hmsGDGcount+"', 'hms_tail', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);

    }
    
    @Test
    public void test33870_HMS_PartB() throws Exception{
    	TestNGCustom.TCNo="33870_HMS_PartB";
    	log("//TC 33870_HMS_PartB - Invalid carrier code - insert-hms Test Case");
    	
    	log("For test data that was validated for this TC, please refer to the query select * from r_day2 where TC = '33870hms'");
    	
    	//Get Data for Day_2
		sqlStatement = "select * from r_day2 where TC = '33870hms' and DES='res_mem' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colNames.add("DATE_REQUESTED");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no resource created for this TC");
		res_mem=colValues.get(0);
		String resDate=colValues.get(1);

		validateNoTPLresource(res_mem);

	    //Validate report
    	//Get the report name
		String command, error, resp;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl0960d.rpt.* | tail -"+getTail_TPLresource("33870hms", txnType_res);
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("HMS CONTRACTOR INTERFACE ERROR REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);

		command = "awk 'c-->0;$0~s{if(b)for(c=b+1;c>1;c--)print r[(NR-c+1)%b];print;c=a}b{r[NR%b]=$0}' b=0 a=1 s="+res_mem+" "+fileName; 
		error = "The selected file is not correct/ or it is empty/ or it does not have the member: "+res_mem+" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
	    Assert.assertTrue(resp.contains("CARRIER CODE NOT FOUND"), "The selected file does not have the 'CARRIER CODE NOT FOUND' string in it");
	    
    }
    
    @Test
    public void test33867_SOHEMA_PartA() throws Exception{
    	TestNGCustom.TCNo="33867_SOHEMA_PartA";
    	log("//TC 33867_SOHEMA_PartA - coverage update- end dt > eff dt-ship Test Case");
    	
    	sqlStatement = "select b.id_medicaid, r.sak_tpl_resource, r.SAK_POLICY_OWNR, r.CDE_POLICY_TYPE, r.NUM_TPL_POLICY, c.cde_carrier, x.num_seq_batch, x.DTE_EFFECTIVE, x.DTE_END, e.CDE_EMPLOYER, x.SAK_OI_COVERAGE, p.CDE_PGM_HEALTH  from t_re_base b, t_tpl_resource r, t_coverage_xref x, t_tpl_carrier c, t_tpl_employer e, t_tpl_oi_coverage_xref xr, t_pub_hlth_pgm p "+
    			"where b.sak_recip = r.sak_recip "+
    			"and r.sak_tpl_resource = x.sak_tpl_resource "+
    			"and r.sak_carrier=c.sak_carrier "+
    			"and b.ind_active='Y' "+
    			"and b.dte_death=0 "+
    			"and e.sak_emp=r.sak_emp "+
    			"and r.sak_emp<>-1 "+
    			"and x.sak_oi_coverage = xr.sak_oi_coverage "+
    			"and xr.sak_pub_hlth=p.sak_pub_hlth "+
    			"and x.dte_effective < "+Common.convertDatetoInt(Common.convertSysdate())+" "+
    			"and x.dte_end <> "+Common.convertDatetoInt(Common.convertSysdate())+" "+
    			"and exists (select r1.sak_recip from t_tpl_resource r1 where r1.sak_tpl_resource=r.sak_tpl_resource group by r1.sak_recip having count(r1.sak_recip)=1 ) "+
    			"and exists (select x1.sak_tpl_resource from t_coverage_xref x1 where x1.sak_tpl_resource=x.sak_tpl_resource group by x1.sak_tpl_resource having count(x1.sak_tpl_resource)=1 ) "+
    			"and rownum < 2";

    	colNames.add("ID_MEDICAID");
    	colNames.add("SAK_TPL_RESOURCE");
    	colNames.add("CDE_POLICY_TYPE");
    	colNames.add("NUM_TPL_POLICY");
    	colNames.add("CDE_CARRIER");
    	colNames.add("DTE_EFFECTIVE");
    	colNames.add("DTE_END");
    	colNames.add("CDE_EMPLOYER");
    	colNames.add("SAK_OI_COVERAGE");
    	colNames.add("CDE_PGM_HEALTH");
    	colNames.add("CDE_RELATION");

    	colValues=Common.executeQuery(sqlStatement, colNames);
    	
    	res_mem = colValues.get(0);
    	resource_identifier=colValues.get(1);
    	cde_policy_type=colValues.get(2);
    	num_tpl_policy_res=colValues.get(3);
    	res_carrier=colValues.get(4);
    	cov_dte_effective=colValues.get(5);
    	cov_dte_end=colValues.get(6);
    	cde_employer=colValues.get(7);
    	oi_plan_identifier=colValues.get(8);
    	cde_pgm_health_res_cov=colValues.get(9);
    	cde_relation=colValues.get(9);
    	
    	dte_med_support_begin=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdate());
    	coverage_sequence="1";
    	cov_dte_effective=Common.convertDate(cov_dte_effective);
    	cov_dte_end=Common.convertDate(cov_dte_end);
    	String old_cov_dte_end=cov_dte_end;
    	cov_dte_end=Common.convertDate_yyyyHIFENmmHIFENdd(Common.convertSysdate()); //We will update this date in SOHEMA txn 
    	String cov1_dte_end = Common.convertDate_yyyyHIFENmmHIFENddToRegular(cov_dte_end); //To store in DB  	

    	txnType_res="SOHEMA";
        resourceFileContents = getOuterTag_tplREShmsSOHEMA_start()+getInnerTag_tplREShmsSOHEMA_start()+getMem_tplREShmsSOHEMA_start()+getRes_tplREShmsSOHEMA_start()+getCov_tplREShmsSOHEMA()+getRes_tplREShmsSOHEMA_end()+getMem_tplREShmsSOHEMA_end()+getInnerTag_tplREShmsSOHEMA_end()+getOuterTag_tplREShmsSOHEMA_end();
        createResourceFile(resourceFileContents);
        transferFile_TPLresource();
        
        //Store Data for Day_2
    	SelSql="select * from r_day2 where TC = '33867sohema' and DES='res_mem'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33867sohema' and DES='res_mem'";
    	InsSql="insert into r_day2 values ('33867sohema', '"+res_mem+"', 'res_mem', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33867sohema' and DES='old_cov_dte_end'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33867sohema' and DES='old_cov_dte_end'";
    	InsSql="insert into r_day2 values ('33867sohema', '"+old_cov_dte_end+"', 'old_cov_dte_end', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '33867sohema' and DES='cov_dte_end'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33867sohema' and DES='cov_dte_end'";
    	InsSql="insert into r_day2 values ('33867sohema', '"+cov1_dte_end+"', 'cov_dte_end', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	resource_sohemaGDGcount++;
    	SelSql="select * from r_day2 where TC = '33867sohema' and DES='sohema_tail'"; //To fetch the correct report on day 2 as each file is producing a separate report, even in the same run
    	col="ID";
    	DelSql="delete from r_day2 where TC = '33867sohema' and DES='sohema_tail'";
    	InsSql="insert into r_day2 values ('33867sohema', '"+resource_sohemaGDGcount+"', 'sohema_tail', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);

    }
    
    @Test
    public void test33867_SOHEMA_PartB() throws Exception{
    	TestNGCustom.TCNo="33867_SOHEMA_PartB";
    	log("//TC 33867_SOHEMA_PartB - coverage update- end dt > eff dt-ship Test Case");
    	
    	log("For test data that was validated for this TC, please refer to the query select * from r_day2 where TC = '33867sohema'");
    	
    	//Get Data for Day_2
		sqlStatement = "select * from r_day2 where TC = '33867sohema' and DES='res_mem' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colNames.add("DATE_REQUESTED");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no resource created for this TC");
		res_mem=colValues.get(0);
		String resDate=colValues.get(1);

		sqlStatement = "select * from r_day2 where TC = '33867sohema' and DES='old_cov_dte_end' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		String old_cov_dte_end=colValues.get(0);
	    log("TPL Resource old Coverage end date was: "+old_cov_dte_end);
		
		sqlStatement = "select * from r_day2 where TC = '33867sohema' and DES='cov_dte_end' and DATE_REQUESTED = '"+Common.convertSysdatecustom(-1)+"'";
		colNames.add("ID");
		colValues = Common.executeQuery1(sqlStatement, colNames);
		cov_dte_end=colValues.get(0);
		
		//Navigate to TPL search panel and search for the member
		searchTPLMem(res_mem);

		//Validate Coverage
	    driver.findElement(By.linkText("Coverage")).click();

	    String covEndDt=driver.findElement(By.xpath("//*[contains(@id,'ColValue_endDate')]")).getText();
	    Assert.assertTrue(covEndDt.equals(cov_dte_end), "In TPL Resource Coverage panel, couldn't find updated Coverage end date as: "+cov_dte_end+". Instead got Coverage end date as: "+covEndDt);
	
	    log("TPL Resource Coverage end date was successfully updated to: "+cov_dte_end);

	    //Validate report
    	//Get the report name
	    txnType_res="SOHEMA";
		String command, error, resp;
		command = "ls -ltr /customer/dsma/"+unixDir+"/rpt01/tpl0920d.rpt.* | tail -"+getTail_TPLresource("33867sohema", txnType_res);
		error = "There was no report found";
		String fileName = Common.connectUNIX(command, error);
		log("SOHEMA CONTRACTOR INTERFACE ERROR REPORT is: "+fileName);
		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
		fileName = Common.chkCompress(fileName);
		
		command = "awk 'c-->0;$0~s{if(b)for(c=b+1;c>1;c--)print r[(NR-c+1)%b];print;c=a}b{r[NR%b]=$0}' b=0 a=1 s="+res_mem+" "+fileName; //. This is to print the next 4 lines after grep on string. "b" and "a" are the number of lines to print before and after string res_mem
		error = "The selected file is not correct/ or it is empty/ or it does not have the member: "+res_mem+" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
	    Assert.assertTrue(resp.contains("Coverage Updated"), "The selected file does not have the 'Coverage Updated' string in it");

		command = "grep \"  COVERAGE SEQ:         1\" "+fileName;
		error = "The selected file is not correct/ or it is empty/ or it does not have the \"  COVERAGE SEQ:         1\" string in it";
		resp = Common.connectUNIX(command, error).trim();
		log(resp);
	    Assert.assertTrue(resp.contains(cov_dte_end), "The selected file does not have the coverage end date: "+cov_dte_end+" string in it");
    }
    
    @Test
    public void testSUConn() throws Exception{
    	String  from = "C:\\Users\\agandhi20\\Desktop\\zz.pdf";
		String to = "/home/angandhi";
		Common.connectUNIXsftp(from, to);
//		System.out.println("pwd is: "+resp);
    }
    
    public String getEFT() throws Exception {
    	return "<EFTRecord  check_eft_ind=\""+check_eft_ind+"\" \r\n"+
		"prov_carrier_ind=\""+prov_carrier_ind+"\" \r\n"+     
        "num_check=\""+num_check+"\" \r\n"+
        "num_wire_transfer=\""+num_wire_transfer+"\" \r\n"+
        "name=\""+name_on_check+" INSURANCE\" \r\n"+
        "amt_paid=\""+amt_paid+"\" \r\n"+
        "nam_remitter=\""+nam_remitter+"\" \r\n"+
        "dte_check_numwire=\""+dte_check_numwire+"\" \r\n"+
        "id_provider=\""+id_provider+"\" \r\n"+
        "cde_service_loc=\""+cde_service_loc+"\" \r\n"+
        "cde_carrier=\""+cde_carrier+"\" \r\n"+
        "cde_project=\""+cde_project+"\" \r\n"+
        "cde_unit=\""+cde_unit+"\" \r\n"+
        "cde_dept=\""+cde_dept+"\" \r\n"+
        "num_ref=\""+num_ref+"\"> \r\n"+
        "</EFTRecord>\r\n"; 
    }
    
    public String getClaimRecovery() throws Exception {
    	return "<ClaimRecovery \r\n"+  
    		      "prov_carrier_ind=\""+prov_carrier_ind+"\" \r\n"+   
    		      "associate_payment_ind=\""+associate_payment_ind+"\" \r\n"+  
    		      "icn_tcn_ind=\""+icn_tcn_ind+"\" \r\n"+ 
    		      "num_icn_tcn=\""+num_icn_tcn+"\" \r\n"+ 
    		      "num_dtl=\""+num_dtl+"\" \r\n"+ 
    		      "num_tpl_policy=\""+num_tpl_policy+"\" \r\n"+ 
    		      "id_medicaid=\""+id_medicaid+"\" \r\n"+ 
    		      "clm_dtl_recovery_amt=\""+clm_dtl_recovery_amt+"\" \r\n"+ 
    		      "cde_carrier=\""+cde_carrier+"\" \r\n"+ 
    		      "cde_pgm_health=\""+cde_pgm_health+"\" \r\n"+ 
    		      "num_check=\""+num_check+"\" \r\n"+ 
    		      "num_wire_transfer=\""+num_wire_transfer+"\" \r\n"+ 
    		      "dte_check_numwire=\""+dte_check_numwire+"\" \r\n"+ 
    		      "dte_first_svc=\""+dte_first_svc+"\" \r\n"+ 
    		      "dte_last_svc=\""+dte_last_svc+"\" \r\n"+ 
    		      "id_provider=\""+id_provider+"\" \r\n"+ 
    		      "cde_service_loc=\""+cde_service_loc+"\" \r\n"+ 
    		      "name=\""+name_on_check+" INSURANCE\" \r\n"+ 
    		      "cde_project=\""+cde_project+"\" \r\n"+ 
    		      "cde_unit=\""+cde_unit+"\" \r\n"+ 
    		      "cde_dept=\""+cde_dept+"\" \r\n"+ 
    		      "num_ref=\""+num_ref+"\" \r\n"+ 
    		      "cde_reason_two=\""+cde_reason_two+"\"> \r\n"+ 
    		"</ClaimRecovery>\r\n"; 
    }
    
    public String getAdjustment() throws Exception {
    	return "<AdjustmentInfo \r\n"+
    		    "num_icn_tcn=\""+num_icn_tcn+"\" \r\n"+
    		    "icn_tcn_ind=\""+icn_tcn_ind+"\" \r\n"+
    		    "num_dtl=\""+num_dtl+"\" \r\n"+
    		    "cde_clm_adj_reason=\""+cde_clm_adj_reason+"\" \r\n"+
    		    "cde_clm_adj_group=\""+cde_clm_adj_group+"\" \r\n"+
    		    "amt_adjustment=\""+amt_adjustment+"\" \r\n"+
    		    "cde_project=\""+cde_project+"\" \r\n"+
    		    "cde_unit=\""+cde_unit+"\" \r\n"+
    		    "cde_dept=\""+cde_dept+"\" \r\n"+
    		    "num_ref=\""+num_ref+"\" \r\n"+
    		    "prov_carrier_ind=\""+prov_carrier_ind+"\" \r\n"+
    		    "cde_carrier=\""+cde_carrier+"\" \r\n"+
    		    "id_provider=\""+id_provider+"\" \r\n"+
    		    "cde_service_loc=\""+cde_service_loc+"\" > \r\n"+
    		"</AdjustmentInfo>\r\n"; 
    }
    
    public static void createRecoveryFile(String contents) throws Exception  {
    	
    	String fileContents = "<TPLRecoveries>\r\n"+
    						  "<TPLRecoverie>\r\n"+
    						  contents+
    						  "</TPLRecoverie>\r\n"+
    						  "</TPLRecoveries>\r\n";
    	
    	if(txnType.equals("HMS")) {
    		hmsGDG=hmsGDG+0.0001;
    		recoveryFile=tempDirPath+"tpd91000.xml"+String.format("%.4f", hmsGDG).substring(1);
    	}
    	
    	if(txnType.equals("ACCS")) {
    		accsGDG=accsGDG+0.0001;
    		recoveryFile=tempDirPath+"tpd90900.xml"+String.format("%.4f", accsGDG).substring(1);
    	}
    	
    	// writing xml string to xml file
    	PrintWriter out = new PrintWriter(recoveryFile);
    	out.println(fileContents);
    	out.close();
    	log("Successfully created recovery file: "+recoveryFile);
    	
    }
    
    public static void  transferFile() throws Exception {
    	String  from = recoveryFile;
		String to = "/customer/dsma/"+unixDir+"/data01";
		Common.connectUNIXsftp(from, to);
		log("Successfully transferred file to unix $DATADIR");
		
		//change permission
		String command, error;
		String recoveryFileName=recoveryFile.substring(recoveryFile.lastIndexOf("\\")+1, recoveryFile.length());
		command = "chmod 777 /customer/dsma/"+unixDir+"/data01/"+recoveryFileName;
		error = "Could not change permission to 777";
		String resp = Common.connectUNIX(command, error);
		log("Successfully changed permission to 777 for file: "+recoveryFileName);
    }
    
    public static void validateNoAR(String icnValue) throws Exception {
    	
	    //Navigate to TPL AR panel
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ARSearch")).click();
	    driver.findElement(By.xpath("//input[contains(@id,'icnNumber')]")).sendKeys(icnValue);
	    Common.search();
	    
	    //Make sure no AR is created
	    String ARmsg =driver.findElement(By.xpath("//html/body/form/table[1]/tbody/tr/td[2]/span/table[2]/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr/td/div/table/tbody/tr/td/span")).getText();
	    Assert.assertTrue(ARmsg.equals("***No records found***"), "AR was created for ICN:"+icnValue);
	    log("Successfully validated that no AR was created for ICN:"+icnValue);
    	
    }
    
    public static void validateNoTPLresource(String memberid) throws Exception {
    	
	    //Navigate to TPL search panel
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchBean_CriteriaPanel:TplSearchResultDataPanel_CurrentID")).sendKeys(memberid);
	 	Common.search();
	    
	    //Make sure no TPL resource is created
	    String tplSearchMsg =driver.findElement(By.xpath("//html/body/form/table[1]/tbody/tr/td[2]/span/table[2]/tbody/tr/td/table/tbody/tr/td/div/table/tbody/tr/td/div/table/tbody/tr/td/span")).getText();
	    Assert.assertTrue(tplSearchMsg.equals("***No records found***"), "TPL resource was created for Member:"+memberid);
	    log("Successfully validated that no TPL resource was created for ICN:"+memberid);
    	
    }
    
//    public static void  validateNoAbend() throws Exception {
//		String command, error, fileName, resp;
//		String recoveryFileName=recoveryFile.substring(recoveryFile.lastIndexOf("\\")+1, recoveryFile.length());
//		command = "ls -ltr /customer/dsma/"+unixDir+"/logs/tpljd910.*.* | tail -1"; 
//		error = "Could not find any log file for HMS recovery job TPLJD910";
//		fileName = Common.connectUNIX(command, error);
//		fileName = fileName.substring(fileName.indexOf("/"), fileName.length());
//    	
//		//Verify job completed
//		command = "grep 'REPORT PERIOD : ' "+fileName+" | tail -1"; ;
//		error = "The selected file is not correct/ or it is empty/ or it does not have the 'REPORT PERIOD :' string in it";
//		String resp = Common.connectUNIX(command, error).trim();
//		log("\r\n"+resp);
//		//Get the from and to date from the report period
//		String fromDT=resp.substring(16,26);
//		String toDT=resp.substring(29);
//		//Make sure the reporting period is the last month
//		Assert.assertTrue(Common.StrtoDT(toDT).after(Common.StrtoDT(Common.convertSysdatecustom(-31))), "The report period on the date is not the last month. Investigate why a report is not produced with last month as the reporting period");
//
//		
//    }
    
    public String getOuterTag_tplREShmsSOHEMA_start() throws Exception {
    	return "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \r\n"+                                                                                                                                                                            
    			"<resourceUpdates>\r\n";
    }
    
    public String getOuterTag_tplREShmsSOHEMA_end() throws Exception {
    	return "</resourceUpdates>\r\n";
    }
    
    public String getInnerTag_tplREShmsSOHEMA_start() throws Exception {
    	return "<resourceUpdate>\r\n";
    }
    
    public String getInnerTag_tplREShmsSOHEMA_end() throws Exception {
    	return "</resourceUpdate>\r\n";
    }
    
    public String getMem_tplREShmsSOHEMA_start() throws Exception {
    	return "<member id_medicaid=\""+res_mem+"\" ind_good_cause=\"N\">\r\n"; 
    }
    
    public String getMem_tplREShmsSOHEMA_end() throws Exception {
    	return "</member>\r\n"; 
    }
    
    public String getRes_tplREShmsSOHEMA_start() throws Exception {
    	return "<resource  id_medicaid=\""+res_mem+"\"  resource_identifier=\""+resource_identifier+"\" \r\n"+                                                                                                                                   
                "cde_carrier=\""+res_carrier+"\" cde_carrier_main=\" \" ind_subcontractor=\" \" id_member_subcontractor=\" \" cde_employer=\""+cde_employer+"\" \r\n"+                                                                                                                                                                                
                "cde_policy_type=\""+cde_policy_type+"\" num_tpl_policy=\""+num_tpl_policy_res+"\"  num_group=\" \" unconfirmed_pol_number=\" \" nam_plan=\""+nam_plan+"\" \r\n"+                                                                                                                                
                "cde_relation=\""+cde_relation+"\" ind_med_support=\""+ind_med_support+"\" dte_med_support_begin=\""+dte_med_support_begin+"\" dte_med_support_end=\""+dte_med_support_end+"\" cde_suspect=\""+cde_suspect+"\">\r\n"; 
    }
    
    public String getRes_tplREShmsSOHEMA_end() throws Exception {
    	return "</resource>\r\n"; 
    }
    
    public String getCov_tplREShmsSOHEMA() throws Exception {
    	return "<coverage coverage_sequence=\""+coverage_sequence+"\" resource_identifier=\""+resource_identifier+"\" \r\n"+                                                                                                                                  
                "oi_plan_identifier=\""+oi_plan_identifier+"\" \r\n"+                                                                                                                                                                            
                "dte_effective=\""+cov_dte_effective+"\" dte_end=\""+cov_dte_end+"\" \r\n"+                                                                                                                                                    
                "cde_pgm_health=\""+cde_pgm_health_res_cov+"\"> \r\n"+                                                                                                                                                                             
                "</coverage>\r\n"; 
    }
    
    public String getPol_tplREShmsSOHEMA() throws Exception {
    	return "<policyholder policy_owner_identifier=\""+policy_owner_identifier+"\"  resource_identifier=\""+resource_identifier+"\" \r\n"+                                                                                                                            
               "owner_nam_last=\""+owner_nam_last+"\" owner_nam_first=\""+owner_nam_first+"\" owner_nam_mid_init=\"\" \r\n"+                                                                                                                      
               "owner_adr_mail_strt1=\""+owner_adr_mail_strt1+"\" owner_adr_mail_city=\""+owner_adr_mail_city+"\" \r\n"+                                                                                                                          
               "owner_adr_mail_state=\"MA\" owner_adr_mail_zip=\"02035\" \r\n"+                                                                                                                                               
               "owner_num_ssn=\""+owner_num_ssn+"\" owner_dte_birth=\""+owner_dte_birth+"\"> \r\n"+                                                                                                                                            
               "</policyholder>\r\n";
    }
    
    public static void createResourceFile(String contents) throws Exception  {
    	
    	if(txnType_res.equals("HMS")) {
    		hmsGDG_TPLresource=hmsGDG_TPLresource+0.0001;
    		resourceFile=tempDirPath+"tpd093000.xml"+String.format("%.4f", hmsGDG_TPLresource).substring(1);
    	}
    	
    	if(txnType_res.equals("SOHEMA")) {
    		sohemaGDG_TPLresource=sohemaGDG_TPLresource+0.0001;
    		resourceFile=tempDirPath+"tpd092000.xml"+String.format("%.4f", sohemaGDG_TPLresource).substring(1);
    	}
    	
    	// writing xml string to xml file
    	PrintWriter out = new PrintWriter(resourceFile);
    	out.println(contents);
    	out.close();
    	log("Successfully created "+txnType_res+" resource file: "+resourceFile);
    	
    }
    
    public static void  transferFile_TPLresource() throws Exception {
    	String  from = resourceFile;
		String to = "/customer/dsma/"+unixDir+"/data01";
		Common.connectUNIXsftp(from, to);
		log("Successfully transferred file to unix $DATADIR");
		
		//change permission
		String command, error;
		String resourceFileName=resourceFile.substring(resourceFile.lastIndexOf("\\")+1, resourceFile.length());
		command = "chmod 777 /customer/dsma/"+unixDir+"/data01/"+resourceFileName;
		error = "Could not change permission to 777";
		String resp = Common.connectUNIX(command, error);
		log("Successfully changed permission to 777 for file: "+resourceFileName);
    }
    
    public static String  getTail_TPLresource(String testCase, String transactionTYPE) throws Exception {
    	if (transactionTYPE.equals("SOHEMA"))
    		sqlStatement = "select max(ID) as maxID, min(ID) as minID from r_day2 where DES='sohema_tail'";
    	if (transactionTYPE.equals("HMS"))
    		sqlStatement = "select max(ID) as maxID, min(ID) as minID from r_day2 where DES='hms_tail'";

    	colNames.add("maxID");
    	colNames.add("minID");
    	colValues = Common.executeQuery1(sqlStatement, colNames);
    	String maxID=colValues.get(0);
    	String minID=colValues.get(1);
    	
    	if (transactionTYPE.equals("SOHEMA"))
    		sqlStatement = "select ID from r_day2 where TC='"+testCase+"' and DES='sohema_tail'";
    	if (transactionTYPE.equals("HMS"))
    		sqlStatement = "select ID from r_day2 where TC='"+testCase+"' and DES='hms_tail'";
    	colNames.add("ID");
    	colValues = Common.executeQuery1(sqlStatement, colNames);
    	String ID=colValues.get(0);
    	
    	int tail= Integer.parseInt(maxID)+Integer.parseInt(minID)-Integer.parseInt(ID);
    	return  Integer.toString(tail);
    }


    
	public static void resetValues() {
		//recovery
		check_eft_ind="C";
		prov_carrier_ind="C";     
		num_check="";
		num_wire_transfer="";
		name_on_check=""; 
		amt_paid="";
		nam_remitter="";
		dte_check_numwire="";
		id_provider="";
		cde_service_loc="";
		cde_carrier="";
		cde_project="BPSA"; 
		cde_unit="HMS1";
		cde_dept="TPL1";
		num_ref="";
					
		associate_payment_ind="Y";
		icn_tcn_ind="I";
		num_icn_tcn="";
		num_dtl="";
		num_tpl_policy="";
		id_medicaid="";
		cde_pgm_health="OI01";
		dte_first_svc="";
		dte_last_svc="";
		cde_reason_two="08";
			
		cde_clm_adj_reason="2";
		cde_clm_adj_group="PR";
		amt_adjustment="";
		
    	ExistingRecoveryICNs=ExistingRecoveryICNs+", "+fetchedICN;
    	txnType="HMS";
    	
    	//Res_HMS/Sohema
    	res_mem="";
    	resource_identifier="-1";
    	res_carrier="";
    	cde_employer="";
    	cde_policy_type="I";
    	num_tpl_policy_res="";
    	nam_plan=" ";
    	cde_relation="18";
    	ind_med_support="Y";
    	dte_med_support_begin="";
    	dte_med_support_end="2299-12-31";
    	cde_suspect="1";
    	
    	coverage_sequence="-1";
    	oi_plan_identifier="";
    	cov_dte_effective="";
    	cov_dte_end="";
    	cde_pgm_health_res_cov="";
    	
    	policy_owner_identifier="0";
    	owner_nam_last="";
    	owner_nam_first="";
    	owner_adr_mail_strt1="17 FULLER RD";
    	owner_adr_mail_city="FOXBORO";
    	owner_num_ssn="";
    	owner_dte_birth="";
    	
    	txnType_res="HMS";
	}
	

    
    public void verifyField(String target, String fieldName, Boolean textProperty) throws Exception {
    	String fieldValue;
    	if (textProperty)
    	  fieldValue = driver.findElement(By.xpath(target)).getText().trim();
    	else
    	  fieldValue = driver.findElement(By.xpath(target)).getAttribute("value").trim();
	 	Assert.assertTrue(!(fieldValue.equals("")), "No value present for "+fieldName);
	 	log(fieldName+": "+fieldValue);


    }

}