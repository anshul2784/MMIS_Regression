// [5-1-2018 AG]  Initial creation

package newMMIS_Subsystems;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

@Listeners({ newMMIS_Subsystems.TestNGCustom.class })
public class ManagedCareACOC extends Login{
	
	public static String memId, sakCdeAid, sakAidElig, effdt, enddt, mcoid, mcosl, acocid, acocsl, pcpid,pcpsl, mcProgram, sak_pub, bp="MASSHEALTH STANDARD";
	int i;
	
	@BeforeTest
    public void ACOStartup() throws Exception {
    	log("Starting ACO C regression......");
    }
	
	@BeforeMethod
	public void LoginCheck() throws Exception {
		Common.resetBase();
		testCheckDBLoginSuccessful();	
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
	}
    
    @Test
    public void test35410() throws Exception{
    	TestNGCustom.TCNo="test35410";
    	log("//TC "+TestNGCustom.TCNo);
 
    	//Get ACOC assignment data
    	sak_pub="24";
    	sqlStatement = sqlacoc(sak_pub);
    	colValues=Common.executeQuery(sqlStatement, colNames);
    	col_acoc_values();
		validate_UI_acocDates();
    	log_acoc_data();
    	
        //Validate assignment data on UI from  DB values
    	validate_UI_acoc();
    	
    	//Modify start and end date for the MC program
    	Member.memberSearch(memId);
    	
		driver.findElement(By.xpath("//*[contains(@id, 'ITM_RePmpAssignSu')]")).click();
		driver.findElement(By.xpath("//*[contains(@id, 'RePmpAssignSuBean_ColHeader_effectiveDate')]")).click();
		driver.findElement(By.xpath("//*[contains(@id, 'RePmpAssignSuBean_ColHeader_endDate')]")).click();
		driver.findElement(By.xpath("//*[contains(@id, 'RePmpAssignSuBean_ColHeader_endDate')]")).click();
        int rownum =driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr")).size();
        for(i=1;i<=rownum;i++)
        	if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[3]")).getText().trim().equals(mcProgram)&&driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[5]")).getText().trim().equals(enddt)&&driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_"+(i-1)+":RePmpAssignSuBean_ColValue_status1Description")).getText().trim().equals("Active"))
    	   	 break;
        
        if(i==(rownum+1))
        	throw new SkipException("There is no active record with mcprogram "+mcProgram+"and enddate "+enddt+" in the SU Panel");
        else
            driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_"+(i-1)+":RePmpAssignSuBean_ColValue_publicHlthPgm_pgmHealthCode']")).click();
        
        String prevEffdt = effdt;
        String prevEnddt = enddt;
        effdt = Common.convertSysdatecustom(-1); //setting new eff date
        enddt = "12/31/2298"; //setting new end date
        driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EffectiveDate')]")).clear();
        driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EffectiveDate')]")).sendKeys(effdt);
        driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EndDate')]")).click(); //Clicking somewhere on page to refresh it and make changes to eff date on ACO and PCP eff dates
        driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EndDate')]")).clear();
        driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EndDate')]")).sendKeys(enddt);
        driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EffectiveDate')]")).click(); //Clicking somewhere on page to refresh it and make changes to end date on ACO and PCP end dates
        
        //The other end dates should change now only
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EffectiveDate')]")).getAttribute("value").equals(effdt),"MCO Effective date mismatch");
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_pcpEffDate')]")).getAttribute("value").equals(effdt),"ACOC Effective date mismatch");
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryPCP_pcpEffDate')]")).getAttribute("value").equals(effdt),"PCP Effective date mismatch");
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EndDate')]")).getAttribute("value").equals(enddt),"MCO End date not set to "+enddt);
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_pcpEndDate')]")).getAttribute("value").equals(enddt),"ACOC End date not set to "+enddt);
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryPCP_pcpEndDate')]")).getAttribute("value").equals(enddt),"PCP End date not set to "+enddt);

    	//change stop reason
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_StopReason"))).selectByValue("62");
	    Common.save();
	    log("Changed MCO eff date in SU panel from "+prevEffdt+" to "+effdt+". Changed MCO end date in SU panel from "+prevEnddt+" to "+enddt);
	    
	    //Verify data again
	    validate_UI_acoc();
	    log("The associated ACO and PCP also systematically have the start/end dates changed to match the updated MCO start/end dates");
	    
	    //Verify non-SU panel dates
    	Member.memberSearch(memId);
		driver.findElement(By.xpath("//*[contains(@id, 'ITM_RePmpAssign')]")).click();
		driver.findElement(By.xpath("//*[contains(@id, 'RePmpAssignHistoryBean_ColHeader_effectiveDate')]")).click();
		driver.findElement(By.xpath("//*[contains(@id, 'RePmpAssignHistoryBean_ColHeader_endDate')]")).click();
		driver.findElement(By.xpath("//*[contains(@id, 'RePmpAssignHistoryBean_ColHeader_endDate')]")).click();
        rownum =driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:RePmpAssignHisList:tbody_element']/tr")).size();
        for(i=1;i<=rownum;i++)
        	if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:RePmpAssignHisList:tbody_element']/tr["+i+"]/td[3]")).getText().trim().equals(mcProgram)&&driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:RePmpAssignHisList:tbody_element']/tr["+i+"]/td[5]")).getText().trim().equals(enddt))
    	   	 break;

        if(i==(rownum+1))
        	throw new SkipException("There is no active record with mcprogram "+mcProgram+"and enddate "+enddt+" in the PMP Panel");
        else
	        driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:RePmpAssignHisList_"+(i-1)+":RePmpAssignHistoryBean_ColValue_publicHlthPgm_pgmHealthCode']")).click();
	    
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EffectiveDate')]")).getAttribute("value").equals(effdt),"MCO Effective date mismatch");
        Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EndDate')]")).getAttribute("value").equals(enddt),"MCO End date mismatch");
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignHis_PMPIDSvcLoc')]")).getAttribute("value").equals(mcoid),"MCO ID mismatch");
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignHis_RePmpHisDistanceSearchHis')]")).getAttribute("value").equals(mcosl),"MCO SL mismatch");
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_provIDExtn')]")).getAttribute("value").equals(acocid+acocsl),"ACOC PID SL mismatch");
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_pcpEffDate')]")).getAttribute("value").equals(effdt),"ACOC Effective date mismatch");
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_pcpEndDate')]")).getAttribute("value").equals(enddt),"ACOC End date mismatch");
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryPCP_provIDExtn')]")).getAttribute("value").equals(pcpid+pcpsl),"PCP PID SL mismatch");
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryPCP_pcpEffDate')]")).getAttribute("value").equals(effdt),"PCP Effective date mismatch");
    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryPCP_pcpEndDate')]")).getAttribute("value").equals(enddt),"PCP End date mismatch");
    	
    	//Ensure dates non-SU panel are not editable
    	if (driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EffectiveDate')]")).isEnabled())
    		Assert.assertTrue(false ,"MCO Effective date is enabled on non-SU panel");
    	if (driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_pcpEffDate')]")).isEnabled())
    		Assert.assertTrue(false ,"ACOC Effective date is enabled on non-SU panel");
    	if (driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryPCP_pcpEffDate')]")).isEnabled())
    		Assert.assertTrue(false ,"PCP Effective date is enabled on non-SU panel");
    	if (driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EndDate')]")).isEnabled())
    		Assert.assertTrue(false ,"MCO End date is enabled on non-SU panel");
    	if (driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_pcpEndDate')]")).isEnabled())
    		Assert.assertTrue(false ,"ACOC End date is enabled on non-SU panel");
    	if (driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryPCP_pcpEndDate')]")).isEnabled())
    		Assert.assertTrue(false ,"PCP End date is enabled on non-SU panel");
    	
	    log("On the regular PMP panel, manual modification could not be made to the MCO start or end date, or their ACOC/PCP counterparts.");
	    
		//aDD CODE FOR Member PCP-ACOC history panel
		//add 834

    }
    
    @Test
    public void test35411_A() throws Exception{
    	//Disenroll existing ACO C enrollment - Batch Test Case Day 1
    	TestNGCustom.TCNo="test35411_A";
    	log("//TC "+TestNGCustom.TCNo);
    	
    	//Get ACOC assignment data
    	sak_pub="24"; //For MSTD program 
    	sqlStatement = sqlacoc(sak_pub);
		colValues=Common.executeQuery(sqlStatement, colNames);
		col_acoc_values();
		validate_UI_acocDates();
		
		//Log ACOC assignment data
		log_acoc_data();
		
		//Verify data on the panels
		validate_UI_acoc();
		
		//Prepare xml to end date aid cat
		memId=Managedcare_Common.xmlData_withsakAidElig(memId,"MHO",sakCdeAid,"22991231",sakAidElig);
		
		//run the disenrollment xml
		Managedcare_Common.autoDisenrollSysdate(memId,"MSTD",bp,"MHO");
		
		//store data for day_2
		enddt = Common.convertSysdate();
		acoc_data_day2();

    }
    
    @Test
    public void test35411_B() throws Exception{
    	//Disenroll existing ACO C enrollment - Batch Test Case Day 2
    	TestNGCustom.TCNo="test35411_B";
    	log("//TC "+TestNGCustom.TCNo);
    	
    	//Get member data
    	TestNGCustom.TCNo="test35411_A"; //Set for below SQLs
    	memId = fetch_acoc_data_day2("select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='Member ID'");
    	effdt = fetch_acoc_data_day2("select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='Eff date'");
    	enddt = fetch_acoc_data_day2("select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='End date'");
    	mcoid = fetch_acoc_data_day2("select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='MCO ID'");
    	mcosl = fetch_acoc_data_day2("select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='MCO SL'");
    	acocid = fetch_acoc_data_day2("select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='ACOC ID'");
    	acocsl = fetch_acoc_data_day2("select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='ACOC SL'");
    	pcpid = fetch_acoc_data_day2("select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='PCP ID'");
    	pcpsl = fetch_acoc_data_day2("select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='PCP SL'");
    	mcProgram = fetch_acoc_data_day2("select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='MC PROGRAM'");
    	
    	log("Member: "+memId);
    	
		//Verify data on the panels
		validate_UI_acoc();
		log("Successfully validated that Batch disenrolled the member. The associated ACO C, PCP are also end dated with the same end date as the assignment end date");
		
		//aDD CODE FOR Member PCP-ACOC history panel
		//add 834
		

    }
    
    @Test
    public void test35413() throws Exception{
    	//Transfer- ACO C enrollment - UI
    	TestNGCustom.TCNo="test35413";
    	log("//TC "+TestNGCustom.TCNo);
    	
    	//Get ACOC assignment data
    	sak_pub="24"; //For MSTD program 
    	sqlStatement = sqlacoc(sak_pub);
		colValues=Common.executeQuery(sqlStatement, colNames);
		col_acoc_values();
		validate_UI_acocDates();
		
		//Log ACOC assignment data
		log_acoc_data();
		
		//Verify data on the panels
		validate_UI_acoc();
		
		//Transfer to MSTDA


    }
    
    //SQL to get active ACOC assignment
    public String sqlacoc(String sakpub) throws Exception{
		colNames.add("ID_MEDICAID");//0
		colNames.add("SAK_CDE_AID");//1
		colNames.add("DTE_EFFECTIVE");//2
		colNames.add("DTE_END");//3
		colNames.add("MCO_ID");//4
		colNames.add("MCO_SL");//5
		colNames.add("ACOC_ID");//6
		colNames.add("ACOC_SL");//7
		colNames.add("PCP_ID");//8
		colNames.add("PCP_SL");//9
		colNames.add("CDE_PGM_HEALTH");//10
		colNames.add("SAK_AID_ELIG");//11
    	return "select distinct b.id_medicaid,e.sak_cde_aid, p.dte_effective, p.dte_end, pm.id_provider as mco_id, s.cde_service_loc as mco_sl, pc.id_provider as acoc_id, pec.cde_service_loc as acoc_sl "+
    			",pp.id_provider as pcp_id, pe.cde_service_loc as pcp_sl, pub.cde_pgm_health, e.sak_aid_elig "+ //The sak_aid_lig is to get details for the exact elig row
				"from t_re_base b, t_re_pmp_assign p,t_re_aid_elig e, t_re_pmp_assign_extn pe, t_re_pmp_assign_extn pec, t_pmp_svc_loc s, t_pr_prov pm, t_pr_prov pc, t_pr_prov pp, t_pub_hlth_pgm pub, T_RE_ELIG bp, t_pub_hlth_pgm pub1 "+
				"where b.sak_recip=p.sak_recip "+
				"and b.sak_recip= e.sak_recip "+
				"and b.ind_active='Y' and b.dte_death='0' "+ 
				"and b.dte_birth < "+Common.convertDatetoInt(Common.convertSysdatecustom(-8030))+" "+ //22 years
				"and p.cde_status1 <> 'H' "+ 
				"and p.sak_pub_hlth="+sakpub+" "+ 
				"and p.dte_end='22991231' "+ 
				"and b.num_ssn <>' ' "+
				"and p.dte_end=e.dte_end "+ 
				"and e.cde_agency='MHO' and e.cde_status1 <> 'H' "+
				"and p.DTE_CHANGED not in (to_char(sysdate,'YYYYMMDD'), to_char(sysdate-1,'YYYYMMDD')) "+ 
				"and p.sak_re_pmp_assign = pe.sak_re_pmp_assign "+
				"and pe.sak_re_pmp_assign = pec.sak_re_pmp_assign "+
				"and pec.cde_rec_type = 'ACOC' "+
				"and pe.cde_rec_type = 'PCP' "+
				"and p.sak_pmp_ser_loc = s.sak_pmp_ser_loc "+ 
				"and s.sak_prov = pm.sak_prov "+
				"and pec.sak_prov = pc.sak_prov "+
				"and pe.sak_prov = pp.sak_prov "+
				"and pub.sak_pub_hlth = p.sak_pub_hlth "+
				"and e.sak_recip = bp.sak_recip "+
				"and e.SAK_PGM_ELIG = bp.SAK_PGM_ELIG "+
				"and e.dte_end= bp.dte_end "+
				"and bp.sak_pub_hlth = pub1.sak_pub_hlth "+
				"and pub1.dsc_pgm_health = '"+bp+"' "+
				"and exists (select e1.dte_end from t_re_aid_elig e1 where e1.sak_aid_elig = e.sak_aid_elig and e1.dte_end not between "+Common.convertDatetoInt(Common.convertSysdate())+" and 22991230  group by e1.dte_end having count(e1.dte_end) = 1) "+
				"and b.num_ssn not like '0%' "+
				"and rownum<2";
    	
    	
    }
    
    //assign col values
    public void col_acoc_values() throws Exception{
    	memId=colValues.get(0);
    	sakCdeAid=colValues.get(1);
    	effdt=Common.convertDate(colValues.get(2));
    	enddt=Common.convertDate(colValues.get(3));
    	mcoid=colValues.get(4);
    	mcosl=colValues.get(5);
    	acocid=colValues.get(6);
    	acocsl=colValues.get(7);
    	pcpid=colValues.get(8);
    	pcpsl=colValues.get(9);
    	mcProgram=colValues.get(10);
    	sakAidElig = colValues.get(11);
    }
    
    //Validate assignment data on UI from  DB values
    public void validate_UI_acoc() throws Exception{
    	LoginCheck();
    	Member.memberSearch(memId);
    	
		driver.findElement(By.xpath("//*[contains(@id, 'ITM_RePmpAssignSu')]")).click();
		driver.findElement(By.xpath("//*[contains(@id, 'RePmpAssignSuBean_ColHeader_effectiveDate')]")).click();
		driver.findElement(By.xpath("//*[contains(@id, 'RePmpAssignSuBean_ColHeader_endDate')]")).click();
		driver.findElement(By.xpath("//*[contains(@id, 'RePmpAssignSuBean_ColHeader_endDate')]")).click();
        int rownum =driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr")).size();
        for(i=1;i<=rownum;i++)
        	if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[3]")).getText().trim().equals(mcProgram)&&driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[5]")).getText().trim().equals(enddt)&&driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_"+(i-1)+":RePmpAssignSuBean_ColValue_status1Description")).getText().trim().equals("Active"))
    	   	 break;

        if(i==(rownum+1))
        	throw new SkipException("There is no active record with mcprogram "+mcProgram+"and enddate "+enddt+" in the SU Panel");
        else
        {
	        driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_"+(i-1)+":RePmpAssignSuBean_ColValue_publicHlthPgm_pgmHealthCode']")).click();
	    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EffectiveDate')]")).getAttribute("value").equals(effdt),"MCO Effective date mismatch");
	    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EndDate')]")).getAttribute("value").equals(enddt),"MCO End date mismatch");
	    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignHisSu_PMPIDSvcLoc')]")).getAttribute("value").equals(mcoid),"MCO ID mismatch");
	    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignHisSu_RePmpHisDistanceSearchHis')]")).getAttribute("value").equals(mcosl),"MCO SL mismatch");
	    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_provIDExtn')]")).getAttribute("value").equals(acocid+acocsl),"ACOC PID SL mismatch");
	    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_pcpEffDate')]")).getAttribute("value").equals(effdt),"ACOC Effective date mismatch");
	    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_pcpEndDate')]")).getAttribute("value").equals(enddt),"ACOC End date mismatch");
	    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryPCP_provIDExtn')]")).getAttribute("value").equals(pcpid+pcpsl),"PCP PID SL mismatch");
	    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryPCP_pcpEffDate')]")).getAttribute("value").equals(effdt),"PCP Effective date mismatch");
	    	Assert.assertTrue(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryPCP_pcpEndDate')]")).getAttribute("value").equals(enddt),"PCP End date mismatch");
	    	LoginCheck();
        }
        
    }
    
    //Ensure MCO-ACO-PCP start/end dates are same
    public void validate_UI_acocDates() throws Exception{
    	LoginCheck();
    	Member.memberSearch(memId);
    	
		driver.findElement(By.xpath("//*[contains(@id, 'ITM_RePmpAssignSu')]")).click();
		driver.findElement(By.xpath("//*[contains(@id, 'RePmpAssignSuBean_ColHeader_effectiveDate')]")).click();
		driver.findElement(By.xpath("//*[contains(@id, 'RePmpAssignSuBean_ColHeader_endDate')]")).click();
		driver.findElement(By.xpath("//*[contains(@id, 'RePmpAssignSuBean_ColHeader_endDate')]")).click();
        int rownum =driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr")).size();
        for(i=1;i<=rownum;i++)
        	if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[3]")).getText().trim().equals(mcProgram)&&driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[5]")).getText().trim().equals(enddt)&&driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_"+(i-1)+":RePmpAssignSuBean_ColValue_status1Description")).getText().trim().equals("Active"))
    	   	 break;

        if(i==(rownum+1))
        	throw new SkipException("There is no active record with mcprogram "+mcProgram+"and enddate "+enddt+" in the SU Panel");
        else
	        driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_"+(i-1)+":RePmpAssignSuBean_ColValue_publicHlthPgm_pgmHealthCode']")).click();
        
        if(!(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_pcpEffDate')]")).getAttribute("value").equals(effdt))) {
        	
        	effdt = driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_pcpEffDate')]")).getAttribute("value");
            driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EffectiveDate')]")).clear();
            driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EffectiveDate')]")).sendKeys(effdt);
            driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EndDate')]")).click(); //Clicking somewhere on page to refresh it and make changes to eff date on ACO and PCP eff dates
        	//change stop reason
    	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_StopReason"))).selectByValue("62");
    	    Common.save();
    	    
        }
        
        else if(!(driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_pcpEndDate')]")).getAttribute("value").equals(enddt))) {

        	enddt = driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryACOC_pcpEndDate')]")).getAttribute("value");
        	driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EndDate')]")).clear();
        	driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EndDate')]")).sendKeys(enddt);
        	driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_EffectiveDate')]")).click(); //Clicking somewhere on page to refresh it and make changes to end date on ACO and PCP end dates
      	  	//change stop reason
        	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_StopReason"))).selectByValue("62");
        	Common.save();
        	
        }        
        
    }
    
  //Store data for ACOC disenrollment cases for day 2
    public void acoc_data_day2() throws Exception{  	
    	String SelSql,col,DelSql,InsSql;
    	
    	SelSql="select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='Member ID'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='Member ID'";
    	InsSql="insert into r_day2 values ('"+TestNGCustom.TCNo+"', '"+memId+"', 'Member ID', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='Eff date'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='Eff date'";
    	InsSql="insert into r_day2 values ('"+TestNGCustom.TCNo+"', '"+effdt+"', 'Eff date', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='MCO ID'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='MCO ID'";
    	InsSql="insert into r_day2 values ('"+TestNGCustom.TCNo+"', '"+mcoid+"', 'MCO ID', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='MCO SL'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='MCO SL'";
    	InsSql="insert into r_day2 values ('"+TestNGCustom.TCNo+"', '"+mcosl+"', 'MCO SL', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='ACOC ID'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='ACOC ID'";
    	InsSql="insert into r_day2 values ('"+TestNGCustom.TCNo+"', '"+acocid+"', 'ACOC ID', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='ACOC SL'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='ACOC SL'";
    	InsSql="insert into r_day2 values ('"+TestNGCustom.TCNo+"', '"+acocsl+"', 'ACOC SL', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='PCP ID'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='PCP ID'";
    	InsSql="insert into r_day2 values ('"+TestNGCustom.TCNo+"', '"+pcpid+"', 'PCP ID', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='PCP SL'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='PCP SL'";
    	InsSql="insert into r_day2 values ('"+TestNGCustom.TCNo+"', '"+pcpsl+"', 'PCP SL', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);

    	SelSql="select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='MC PROGRAM'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='MC PROGRAM'";
    	InsSql="insert into r_day2 values ('"+TestNGCustom.TCNo+"', '"+mcProgram+"', 'MC PROGRAM', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    	
    	SelSql="select * from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='End date'";
    	col="ID";
    	DelSql="delete from r_day2 where TC = '"+TestNGCustom.TCNo+"' and DES='End date'";
    	InsSql="insert into r_day2 values ('"+TestNGCustom.TCNo+"', '"+enddt+"', 'End date', '"+Common.convertSysdate()+"')";
    	Common.insertData(SelSql, col, DelSql, InsSql);
    }
    
    
    //Fetch data for ACOC disenrollment cases for day 2
    public String fetch_acoc_data_day2(String sql) throws Exception{  
		colNames.add("ID");
		colValues = Common.executeQuery1(sql, colNames);
		if (colValues.get(0).equals("null"))
		    throw new SkipException("Skipping this test because there was no member data for sql: "+sql);
		return colValues.get(0);
    }
    
    
    //log acoc assignment details
    public void log_acoc_data() throws Exception{
		log("Member: "+memId);
		log("MC Program: "+mcProgram);
		log("MCO Effective date: "+effdt);
		log("MCO End date: "+enddt);
		log("MCO ID: "+mcoid);
		log("MCO SL: "+mcosl);
		log("ACOC ID: "+acocid);
		log("ACOC SL: "+acocsl);
		log("ACOC Effective date: "+effdt);
		log("ACOC End date: "+enddt);
		log("PCP ID: "+pcpid);
		log("PCP SL: "+pcpsl);
		log("PCP Effective date: "+effdt);
		log("PCP End date: "+enddt);
		log("sak_aid_elig: "+sakAidElig);
    }



}
