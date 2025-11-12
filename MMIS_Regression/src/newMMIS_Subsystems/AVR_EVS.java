package newMMIS_Subsystems;

import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

@Listeners({ newMMIS_Subsystems.TestNGCustom.class })
public class AVR_EVS  extends Login{
	
	
	
	static String peTarget = "//form[@id='form1']/div/table[2]/tbody/tr";
	String eligTarget = "//tbody[@id='EligTab:j_id_id5pc3:j_id_id10pc3:j_id_id15pc3:coverageRestrictiveMessages:tbody_element']/tr";
	String ltcTarget  = "//tbody[@id='EligTab:j_id_id5pc3:j_id_id10pc3:j_id_id149pc3:j_id_id155pc3:LTCRestrictiveMessages:tbody_element']/tr";
	String mcTarget= "//tbody[@id='EligTab:j_id_id5pc3:j_id_id10pc3:j_id_id103pc3:j_id_id109pc3:MCORestrictiveMessages:tbody_element']/tr";
	String bhTarget = "//tbody[@id='EligTab:j_id_id5pc3:j_id_id10pc3:j_id_id126pc3:j_id_id132pc3:BHRestrictiveMessages:tbody_element']/tr";
	String pccTarget = "//tbody[@id='EligTab:j_id_id5pc3:j_id_id10pc3:j_id_id72pc3:j_id_id78pc3:PCCRestrictiveMessage:tbody_element']/tr";
	
	String[][] i = new String[28][28];
	
	WebElement element;
	boolean found;
	int rmSize;
	String rmMessage;
	
	public FileOutputStream fo;
	public WritableWorkbook wwb;
	public WritableSheet ws;
	public Label l;
	public String[] col;
	public String[] dtl = new String[3];
	public int row=0;
	public List<WebElement> list;
	
	@Parameters({ "environment" })
	@BeforeTest
    public void EVSStartup(String environment) throws Exception {
    	log("Starting EVS Subsystem......");
    	
		//Create Excel sheet for Claims report
		fo = new FileOutputStream(System.getProperty("user.dir")+"\\RM_member_extract.xls");
		wwb = Workbook.createWorkbook(fo);
		ws = wwb.createSheet("rpt1", 0);
		//Initialize the report
		Date date = new Date();
		l = new Label (0,0, environment+" AVR_EVS RM member extract - "+date.toString());
		ws.addCell(l);
		row++;
		row++;
		col = new String[7];
		col[0]="RM";
		col[1]="MEMBER";
		for (int i=0;i<2;i++) {
			l = new Label (i,row, col[i]);
			ws.addCell(l);
		}
    }
	
	@AfterTest
	public void teardown() throws Exception {
		wwb.write();
		wwb.close();
		Common.portalLogout();
	}
	
	@BeforeMethod
	public void LoginCheck() throws Exception {
		Common.resetPortal();
		testCheckDBLoginSuccessful();	
	}
	
	@Test
	public void RM_1_035() throws Exception {
		TestNGCustom.TCNo="RM_1_035";
				
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select b.id_medicaid from t_re_base b, t_re_aid_elig a"+
					 " where b.sak_recip=a.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and a.cde_status1 <> 'H' and a.sak_cde_aid <> 173 and a.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and exists (select 1 from t_re_aid_elig a1 where a1.sak_recip=b.sak_recip and a1.cde_status1 <> 'H' and a1.sak_cde_aid=173 and a1.dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum =1";
		
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1 / 035 DMH CLIENT";
		rmCommon(rmMessage, eligTarget); 

	}
	
	@Test
	public void RM_2_111() throws Exception {
		TestNGCustom.TCNo="RM_2_111";
		
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e, t_re_loc l"+
					  " where b.sak_recip=e.sak_recip and b.sak_recip=l.sak_recip"+
					  " and ind_active <> 'N' and dte_death='0'"+
					  " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					  " and l.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					  " and rownum=1";
		
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="2 / 111 Resident at Long-Term-Care Facility.";
		//Select LTC tab
		driver.findElement(By.xpath("//*[contains(@id, 'LTCList:0:facilityName')]")).click();
		rmCommon(rmMessage, ltcTarget); 

	}
	
	@Test
	public void RM_3_116() throws Exception {
		TestNGCustom.TCNo="RM_3_116";
		
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=118"+
					 " and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and not exists (select 1 from t_re_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_pub_hlth <> 2 and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " and rownum=1";

		
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="3 / 116 EAEDC (Cat. 04). Services Restricted. See 130 CMR 450.106. For questions, call Provider Services at 1-800-841-2900.";
		rmCommon(rmMessage, eligTarget); 
		
		
	}
	
	@Test
	public void RM_5_480() throws Exception {
		TestNGCustom.TCNo="RM_5_480";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
//		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
//					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
//					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=140"+
//					 " and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
//					 " and rownum=1";
		
        sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
                " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
                " and e.cde_status1 <> 'H'"+
                " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '75')"+
                " and to_char(sysdate,'YYYYMMDD') between e.dte_effective and e.dte_end"+
                " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="5 / 480 Bill Member's Private Health Insurance. See 130 CMR 450.316-317 for information on TPL Reqs and Payment Limitations on Claim Submissions.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_6_480() throws Exception {
		TestNGCustom.TCNo="RM_6_480";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
//		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
//					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
//					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=142"+
//					 " and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
//					 " and rownum=1";
		
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
                " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
                " and e.cde_status1 <> 'H'"+
                " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '78')"+
                " and to_char(sysdate,'YYYYMMDD') between e.dte_effective and e.dte_end"+
                " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid"+
                " and to_char(sysdate,'YYYYMMDD') between dte_effective and dte_end)"+
                " and rownum=1";
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="6 / 480 Bill Member's Private Health Insurance. See 130 CMR 450.316-317 For Info On TPL Reqs and Payment Limitations on Claim Submissions.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_7_485() throws Exception {
		TestNGCustom.TCNo="RM_7_485";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=141"+
					 " and to_char(sysdate,'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists (select 1 from t_re_aid_elig e1 where e1.sak_recip=b.sak_recip and e1.cde_status1 <> 'H'"+
					 " and e1.sak_cde_aid <> e.sak_cde_aid and to_char(sysdate,'YYYYMMDD') between e1.dte_effective and e1.dte_end)"+
					 " and rownum < 2";
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="7 / 485 Bill Member's Private Health Insurance. MassHealth pays for Copays and Deductibles for Well-Child Visits.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_8_485() throws Exception {
		TestNGCustom.TCNo="RM_8_485";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=143"+
					 " and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists (select 1 from t_re_aid_elig e1"+
					 " where e1.sak_recip=b.sak_recip and e1.cde_status1 <> 'H'"+
					 " and e1.sak_cde_aid <> e.sak_cde_aid"+
					 " and to_char(sysdate, 'YYYYMMDD') between e1.dte_effective and e1.dte_end)"+
					 " and rownum < 2";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="8 / 485 Bill Member's Private Health Insurance. Masshealth Pays For Copays And Deductibles For Well-Child Visits.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_9_516() throws Exception {
		TestNGCustom.TCNo="RM_9_516";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_loc l"+
					 " where b.sak_recip=l.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and l.dte_discharge >=to_char(sysdate,'YYYYMMDD') and l.sak_prov=27397 and l.cde_service_loc='A'"+
					 " and rownum=1";
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="9 / 516 Call HRCA at 617-325-8000 for authorization of all services except acute inpatient admissions.";
		//Select LTC tab
		driver.findElement(By.xpath("//*[contains(@id, 'LTCList:0:facilityName')]")).click();
		rmCommon(rmMessage, ltcTarget); 
	}
	
	@Test
	public void RM_10_006() throws Exception {
		TestNGCustom.TCNo="RM_10_006";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=31467 and l.cde_service_loc='A' and p.cde_rsn_mc_stop <> 'Z8'"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+ 
					 " 	and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="10 / 006 NHP Member. For Medical Services Call 1-800-462-5449. For Behavioral Health Services Call 1-800-414-2820";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
	}
	
	@Test
	public void RM_12_031() throws Exception {
		TestNGCustom.TCNo="RM_12_031";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+ 
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=31495 and l.cde_service_loc='A'"+
					 " and p.dte_end ='22991231' and p.dte_effective <= to_char(sysdate, 'YYYYMMDD')"+
					 " and substr(to_char(sysdate,'YYYYMMDD'),1,4) - substr(dte_birth,1,4) < 80"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="12 / 031 Prior Auth Required on All Care Except Emergencies. ESP North Shore. Call 781-581-3900 For Lynn Clients 978-837-9479 For Beverly Clients.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
	}
	
	@Test
	public void RM_13_036() throws Exception {
		TestNGCustom.TCNo="RM_13_036";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+ 
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=31494 and l.cde_service_loc='A'"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="13 / 036 Prior Authorization Mandatory For All Care Except For Emergencies. Call ESP of The Cambridge Hospital at 617-868-6323.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
	}
	
	@Test
	public void RM_14_041() throws Exception {
		TestNGCustom.TCNo="RM_14_041";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+ 
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=31449 and l.cde_service_loc='C'"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+ 
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="14 / 041 Prior Authorization Mandatory for all Care Except For Emergencies. Call ESP at Fallon at 508-852-2026.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
	}
	
	@Test
	public void RM_15_046() throws Exception {
		TestNGCustom.TCNo="RM_15_046";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=24263 and l.cde_service_loc='E'"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="15 / 046 Prior Authorization Mandatory for all care except for Emergencies. Call ESP Of Upham's Corner at 617-288-0970.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
	}
	
	@Test
	public void RM_16_051() throws Exception {
		TestNGCustom.TCNo="RM_16_051";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					" where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					" and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					" and l.sak_prov=27821 and l.cde_service_loc='J'"+
					" and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					" and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="16 / 051 Prior authorization mandatory for all care except for emergencies. Call Harbor Elder Services at 617-296-5100.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
	}
	
	@Test
	public void RM_19_171() throws Exception {
		TestNGCustom.TCNo="RM_19_171";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=20754 and l.cde_service_loc='C'"+
					 " and p.dte_end ='22991231' and p.dte_effective <= to_char(sysdate,'YYYYMMDD')"+
					 " and substr(to_char(sysdate,'YYYYMMDD'),1,4)-substr(dte_birth,1,4) < 80"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="19 / 171 Prior authorization mandatory for all care except for emergencies. Call ESP of East Boston at 617-568-6416.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
	}
	
	@Test
	public void RM_20_201() throws Exception {
		TestNGCustom.TCNo="RM_20_201";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=31450 and l.cde_service_loc='A'"+
					 " and p.dte_end ='22991231' and p.dte_effective <= to_char(sysdate,'YYYYMMDD')"+
					 " and substr(to_char(sysdate,'YYYYMMDD'),1,4)-substr(dte_birth,1,4) < 80"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="20 / 201 SENIOR CARE OPTIONS. Payment limited to SCO. Authorization needed for all services except emergencies. Call CCA: 1-866-610-2273.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
	}
	
	@Test
	public void RM_20_231() throws Exception {
		TestNGCustom.TCNo="RM_20_231";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=31448 and l.cde_service_loc='A'"+
					 " and p.dte_end ='22991231' and p.dte_effective <= to_char(sysdate,'YYYYMMDD')"+
					 " and substr(to_char(sysdate,'YYYYMMDD'),1,4)-substr(dte_birth,1,4) < 80"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="21 / 231 SENIOR CARE OPTIONS. Payment limited to SCO. Authorization needed for all services except emergencies. Call SWH: 1-888-794-7268.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
	}
	
	@Test
	public void RM_28_391() throws Exception {
		TestNGCustom.TCNo="RM_28_391";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=31447 and l.cde_service_loc='A'"+
					 " and p.dte_end ='22991231' and p.dte_effective <= to_char(sysdate,'YYYYMMDD')"+
					 " and substr(to_char(sysdate,'YYYYMMDD'),1,4)-substr(dte_birth,1,4) < 80"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="28 / 391 SENIOR CARE OPTIONS. Payment limited to SCO. Authorization needed for all services except emergencies. Call EVERCARE: 1-888-867-5511";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
	}
	
	@Test
	public void RM_29_495() throws Exception {
		TestNGCustom.TCNo="RM_29_495";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=144"+
					 " and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and rownum=1";
		
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="29 / 495 Eligible for Premium Assistance. Bill member's private health insurance.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_30_495() throws Exception {
		TestNGCustom.TCNo="RM_30_495";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=146"+
					 " and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and not exists (select 1 from t_re_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_pub_hlth not in (7,17) and dte_end='22991231')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="30 / 495 Eligible for Premium Assistance. Bill member's private health insurance.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_31_495() throws Exception {
		TestNGCustom.TCNo="RM_31_495";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=147"+
					 " and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and rownum=1";
		
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="31 / 495 ELIGIBLE FOR PREMIUM ASSISTANCE. BILL MEMBER'S PRIVATE HEALTH INSURANCE.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_32_495() throws Exception {
		TestNGCustom.TCNo="RM_32_495";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=145 and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and to_char(sysdate,'YYYYMMDD') between dte_effective and dte_end)"+
					 " and rownum=1";

		
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="32 / 495 Eligible for Premium Assistance. Bill member's private health insurance.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_33_505() throws Exception {
		TestNGCustom.TCNo="RM_33_505";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_pub_hlth=4 and e.dte_end >=to_char(sysdate,'YYYYMMDD')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="33 / 505 MassHealth CommonHealth member. For questions, call 1-800-841-2900";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_34_520() throws Exception {
		TestNGCustom.TCNo="RM_34_520";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_pub_hlth=13"+
					 " and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists(select 1 from t_re_elig e1"+
					 " where e1.sak_recip=b.sak_recip and e1.cde_status1 <> 'H'"+
					 " and e1.sak_pub_hlth <> e.sak_pub_hlth"+
					 " and to_char(sysdate, 'YYYYMMDD') between e1.dte_effective and e1.dte_end)"+
					 " and rownum < 2";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="34 / 520 Eligible for ambulatory prenatal care only.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_35_522() throws Exception {
		TestNGCustom.TCNo="RM_35_522";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_pub_hlth=10 and e.dte_end >=to_char(sysdate,'YYYYMMDD')"+
					 " and not exists (select 1 from t_re_elig  where sak_recip=b.sak_recip and cde_status1 <> 'H'"+
					 " and sak_pub_hlth <> e.sak_pub_hlth"+
					 " and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="35 / 522 Eligible for emergency services only.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_40_601() throws Exception {
		TestNGCustom.TCNo="RM_40_601";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_pub_hlth=11 and e.dte_end >=to_char(sysdate,'YYYYMMDD')"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="40 / 601 Eligible for emergency services, including labor and delivery, under Limited without copay under 130 CMR 450.130(d).";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_41_602() throws Exception {
		TestNGCustom.TCNo="RM_41_602";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_pub_hlth=11 and e.dte_end >=to_char(sysdate,'YYYYMMDD')"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="41 / 602 For eligibility dates and payment under Healthy Start for outpatient, non-emergency pregnancy-related services except labor and delivery and global delivery codes, call 1-888-488-9161.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_42_603() throws Exception {
		TestNGCustom.TCNo="RM_42_603";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_pub_hlth=38 and e.dte_end >=to_char(sysdate,'YYYYMMDD')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="42 / 603 Eligible for emergency services under Limited without copay under 130 CMR 450.130(d).";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_43_604() throws Exception {
		TestNGCustom.TCNo="RM_43_604";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_pub_hlth=38 and e.dte_end >=to_char(sysdate,'YYYYMMDD')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="43 / 604 For eligibility dates and payment for primary and preventive care services call CMSP at 1-800-909-2677.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_45_608() throws Exception {
		TestNGCustom.TCNo="RM_45_608";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
					  " where b.sak_recip=e.sak_recip"+
					  " and ind_active <> 'N' and dte_death='0'"+
					  " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD')"+
					  " and (exists(select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')) or"+
					  " exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					  " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="45 / 608 Member eligible for Medicare Part D. For member enrollment status or other information call 1-800-MEDICARE (1-800-633-4227).";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_46_611() throws Exception {
		TestNGCustom.TCNo="RM_46_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=27"+
					 " and (exists(select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')) or"+
					 " exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1<>'H' and sak_cde_aid<>27 and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="46 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_48_612() throws Exception {
		TestNGCustom.TCNo="RM_48_612";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=29"+
					 " and (exists(select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')) or"+
					 " exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1<>'H' and sak_cde_aid<>29 and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="48 / 612 Member is Specified Low Income Medicare Beneficiary. See 130 CMR 519.011(A).";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_49_612() throws Exception {
		TestNGCustom.TCNo="RM_49_612";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=10"+
					 " and (exists(select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')) or"+
					 " exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1<>'H' and sak_cde_aid<>10 and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="49 / 612 Member is Specified Low Income Medicare Beneficiary. See 130 CMR 519.011(A).";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_50_613() throws Exception {
		TestNGCustom.TCNo="RM_50_613";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip"+ 
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=163"+
					 " and (exists(select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')) or"+
					 " exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1<>'H' and sak_cde_aid<>163 and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="50 / 613 Member is Qualified Individual Beneficiary. SEE 130 CMR 519.011(B).";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_51_613() throws Exception {
		TestNGCustom.TCNo="RM_51_613";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
				     " where b.sak_recip=e.sak_recip"+ 
				     " and ind_active <> 'N' and dte_death='0'"+
				     " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=164"+
				     " and (exists(select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')) or"+
				     " exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
				     " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1<>'H' and sak_cde_aid<>164 and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
				     " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="51 / 613 Member is Qualified Individual Beneficiary. SEE 130 CMR 519.011(B).";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_68_126() throws Exception {
		TestNGCustom.TCNo="RM_68_126";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pub_hlth=73 and p.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="68 / 126 COMMUNITY CASE MANAGEMENT (CCM) MEMBER: PRIOR AUTHORIZATION REQUIRED FOR NURSING, HOME HEALTH AIDE AND PCA SERVICES. CONTACT CCM AT 1-800-863-6068";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_71_071() throws Exception {
		TestNGCustom.TCNo="RM_71_071";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e, t_re_lockin_period l"+
					 " where b.sak_recip=e.sak_recip and b.sak_recip=l.sak_recip"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and l.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="71 / 071 MEMBER ENROLLED IN PROGRAM THAT LIMITS HIM/HER TO 1 PHARMACY. FOR INFORMATION, MEMBER MAY CALL 1-800-841-2900, 8AM-5PM MON-FRI.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_74_74() throws Exception {
		TestNGCustom.TCNo="RM_74_74";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=173"+
					 " and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and not exists (select 1 from t_re_aid_elig e where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> 173 and dte_end >= to_char(sysdate,'YYYYMMDD'))"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="74 / 74 Client is not eligible for MassHealth";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_76_525() throws Exception {
		TestNGCustom.TCNo="RM_76_525";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.dte_end >=to_char(sysdate,'YYYYMMDD') and p.sak_pub_hlth=34"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="76 / 525 For mental health or substance abuse service authorization, call the Partnership at 1-800-495-0086.";
		//Select BH tab
		driver.findElement(By.xpath("//*[contains(@id, 'BHList:0:providerName')]")).click();
		rmCommon(rmMessage, bhTarget); 

	}
	
	@Test
	public void RM_89_646() throws Exception {
		TestNGCustom.TCNo="RM_89_646";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l, t_re_aid_elig e"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.sak_recip=b.sak_recip and e.cde_status1 <> 'H' and e.sak_cde_aid=108 and e.dte_end ='22991231'"+
					 " and p.cde_status1 <> 'H' and p.dte_end='22991231'"+
					 " and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc and l.sak_prov=31467 and l.cde_service_loc='A'"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> 108 and dte_end ='22991231')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="89 / 646 NHP MEMBER. For Vision services call 1-800-462-5449.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 

	}

	@Test
	public void RM_121_121() throws Exception {
		TestNGCustom.TCNo="RM_121_121";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_agency_aff a, t_re_aid_elig e"+
					 " where b.sak_recip=a.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and a.cde_agency='DSS' and to_char(sysdate, 'YYYYMMDD') between a.dte_eff and a.dte_end"+ 
					 " and e.sak_recip=b.sak_recip and e.cde_status1 <> 'H' and e.cde_agency='DSS' and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end and sak_cde_aid=55"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="121 / 121 DIRECT ALL INQUIRIES ABOUT ELIGIBILITY TO SOCIAL SERVICE WORKER";
		rmCommon(rmMessage, eligTarget); 

	}
	
	@Test
	public void RM_186_186() throws Exception {
		TestNGCustom.TCNo="RM_186_186";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e, t_cde_aid a"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and dte_birth > concat((substr(to_char(sysdate, 'YYYYMMDD'), 1,4)- 19), substr(to_char(sysdate, 'YYYYMMDD'),5,4))"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and e.sak_cde_aid=a.sak_cde_aid"+
					 " and not exists (select 1 from t_clm_copay_exempt where sak_cde_aid=e.sak_cde_aid)"+
					 " and not exists (select 1 from t_re_aid_elig e1, t_cde_aid a1 where e1.sak_recip=b.sak_recip and e1.cde_status1 <> 'H' and e1.dte_end >=to_char(sysdate, 'YYYYMMDD')"+
					 " and e1.sak_cde_aid=a1.sak_cde_aid and a1.cde_aid_hierarchy < a.cde_aid_hierarchy)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="186 / 186 EXEMPT FROM COPAY ON NON-PHARMACY SERVICES UNDER 130 CMR 450.130(D).";
		rmCommon(rmMessage, eligTarget); 

	}
	
	@Test
	public void RM_246_246() throws Exception {
		TestNGCustom.TCNo="RM_246_246";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e, t_cde_aid a"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and dte_birth > concat((substr(to_char(sysdate, 'YYYYMMDD'), 1,4)- 19), substr(to_char(sysdate, 'YYYYMMDD'),5,4))"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and e.sak_cde_aid=a.sak_cde_aid"+
					 " and not exists (select 1 from t_clm_copay_exempt where sak_cde_aid=e.sak_cde_aid)"+
					 " and not exists (select 1 from t_re_aid_elig e1, t_cde_aid a1 where e1.sak_recip=b.sak_recip and e1.cde_status1 <> 'H' and e1.dte_end >=to_char(sysdate, 'YYYYMMDD')"+ 
					 " and e1.sak_cde_aid=a1.sak_cde_aid and a1.cde_aid_hierarchy < a.cde_aid_hierarchy)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="246 / 246 EXEMPT FROM COPAY ON PHARMACY SERVICES UNDER 130 CMR 450.130(D).";
		rmCommon(rmMessage, eligTarget); 

	}
	
	@Test
	public void RM_271_271() throws Exception {
		TestNGCustom.TCNo="RM_271_271";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_limits l"+
					 " where b.sak_recip=l.sak_recip"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and l.cde_limit_type=2 and substr(l.end_period,1,4)=substr(to_char(sysdate,'YYYYMMDD'),1,4)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="271 / 271 MET CAP ON NON-PHARMACY SERVICES UNDER 130 CMR 450.130(C).";
		rmCommon(rmMessage, eligTarget); 

	}
	
	@Test
	public void RM_366_366() throws Exception {
		TestNGCustom.TCNo="RM_366_366";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_limits l"+
					 " where b.sak_recip=l.sak_recip"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and l.cde_limit_type=1 and substr(l.end_period,1,4)=substr(to_char(sysdate,'YYYYMMDD'),1,4)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="366 / 366 MET CAP ON PHARMACY SERVICES UNDER 130 CMR 450.130(C).";
		rmCommon(rmMessage, eligTarget); 

	}
	
	@Test
	public void RM_461_461() throws Exception {
		TestNGCustom.TCNo="RM_461_461";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and p.sak_pub_hlth=23"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="461 / 461 PRIMARY CARE CLINICIAN (PCC) PLAN MEMBER. CALL PCC FOR AUTHORIZATION FOR ALL SERVICES EXCEPT THOSE LISTED IN 130 CMR 450.118(J).";
		//Select MC PCC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MC_PCC:0:providerLegalName')]")).click();
		rmCommon(rmMessage, pccTarget); 

	}
	
	@Test
	public void RM_530_530() throws Exception {
		TestNGCustom.TCNo="RM_530_530";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p"+
					 " where b.sak_recip=p.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pub_hlth=34"+
					 " and to_char(sysdate,'YYYYMMDD') between p.dte_effective and p.dte_end"+
					 " and not exists (select 1 from t_re_pmp_assign p1 where p1.sak_recip=b.sak_recip and p1.cde_status1 <> 'H'"+
					 " and p1.sak_pub_hlth <> p.sak_pub_hlth and to_char(sysdate,'YYYYMMDD') between p1.dte_effective and p1.dte_end)"+
					 " and rownum < 2";
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="530 / 530 NO PCC/MCO AUTHORIZATIONS NEEDED. FOR MH/SA SERVICE AUTHORIZATION, CALL THE PARTNERSHIP AT 1-800-495-0086.";
		//Select BH tab
		driver.findElement(By.xpath("//*[contains(@id, 'BHList:0:providerName')]")).click();
		rmCommon(rmMessage, bhTarget); 

	}
	
	@Test
	public void RM_596_596() throws Exception {
		TestNGCustom.TCNo="RM_596_596";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.sak_cde_aid=110 and e.cde_status1 <> 'H' and e.dte_end ='22991231'"+
					 " and not exists (select 1 from t_re_pmp_assign where sak_recip=b.sak_recip and cde_status1 <> 'H' )"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="596 / 596 ESSENTIAL UNENROLLED. Member eligible for Essential but not enrolled. Member must call 800-841-2900 and enroll in Managed Care to receive these benefits. HSN is available.";
		rmCommon(rmMessage, eligTarget); 

	}
	
	@Test
	public void RM_597_597() throws Exception {
		TestNGCustom.TCNo="RM_597_597";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active = 'Y' and dte_death='0'"+
					 " and e.sak_cde_aid=108 and e.cde_status1 <> 'H' and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists (select 1 from t_re_pmp_assign where sak_recip=b.sak_recip and cde_status1 <> 'H' and to_char(sysdate,'YYYYMMDD') between dte_effective and dte_end)"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and to_char(sysdate,'YYYYMMDD') between dte_effective and dte_end)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="597 / 597 BASIC UNENROLLED. Member eligible for Basic but not enrolled. Member must call 800-841-2900 and enroll in Managed Care to receive these benefits. HSN is available.";
		rmCommon(rmMessage, eligTarget); 

	}
	
	@Test
	public void RM_614_614() throws Exception {
		TestNGCustom.TCNo="RM_614_614";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.dte_end >=to_char(sysdate,'YYYYMMDD') and p.sak_pub_hlth=33"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="614 / 614 BILL HOSPICE PROVIDER IF SERVICE IS RELATED TO TERMINAL ILLNESS.";
		rmCommon(rmMessage, eligTarget); 

	}
	
	@Test
	public void RM_640_640() throws Exception {
		TestNGCustom.TCNo="RM_640_640";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e, t_re_aid_elig a, t_cde_aid c"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_pub_hlth in (22,39) and e.dte_effective < to_char(sysdate,'YYYYMMDD') and e.dte_end > to_char(sysdate,'YYYYMMDD')"+
					 " and a.sak_recip=b.sak_recip and e.sak_pgm_elig=a.sak_pgm_elig and a.cde_status1 <> 'H' and a.dte_end > to_char(sysdate,'YYYYMMDD') and a.sak_cde_aid=c.sak_cde_aid"+
					 " and not exists (select 1 from t_re_pmp_assign where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_pub_hlth between 44 and 47 and dte_end > to_char(sysdate,'YYYYMMDD'))"+
					 " and not exists (select 1 from t_re_aid_elig a1, t_cde_aid c1 where a1.sak_recip=b.sak_recip and a1.cde_status1 <> 'H' and a1.dte_end > to_char(sysdate,'YYYYMMDD')"+
					 " and a1.sak_cde_aid=c1.sak_cde_aid and c1.cde_aid_hierarchy < c.cde_aid_hierarchy)"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="640 / 640 HSN NOT AVAILABLE";
		rmCommon(rmMessage, eligTarget); 

	}
	
	@Test
	public void RM_650_650() throws Exception {
		TestNGCustom.TCNo="RM_650_650";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end > to_char(sysdate,'YYYYMMDD') and e.sak_elig_start=3"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_elig_start <>3 and dte_end > to_char(sysdate,'YYYYMMDD'))"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="650 / MEMBER'S MASSHEALTH ELIGIBILITY IS TEMPORARY";
		rmCommon(rmMessage, eligTarget); 

	}
	
	@Test
	public void RM_740_311() throws Exception {
		TestNGCustom.TCNo="RM_740_311";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=31449 and l.cde_service_loc='B'"+
					 " and p.dte_end ='22991231'"+
					 " and p.sak_pub_hlth=24"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="740 / 311 FALLON MEMBER. For Medical Services Call 1-866-275-3247. For Behavioral Health Services CALL 1-888-421-8861.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 

	}
	
	@Test
	public void RM_741_311() throws Exception {
		TestNGCustom.TCNo="RM_741_311";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+ 
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=31449 and l.cde_service_loc='B'"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+ 
					 " and p.sak_pub_hlth=25"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="741 / 311 FALLON MEMBER. For Medical Services Call 1-866-275-3247. For Behavioral Health Services CALL 1-888-421-8861.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 

	}
	
	@Test
	public void RM_742_311() throws Exception {
		TestNGCustom.TCNo="RM_742_311";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+ 
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=31449 and l.cde_service_loc='B'"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and p.sak_pub_hlth=26"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="742 / 311 FALLON MEMBER. For Medical Services Call 1-866-275-3247. For Behavioral Health Services CALL 1-888-421-8861.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 

	}
	
	@Test
	public void RM_747_021() throws Exception {
		TestNGCustom.TCNo="RM_747_021";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+ 
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=25617 and l.cde_service_loc='D' and p.cde_rsn_mc_stop <> 'Z8'"+
					 " and p.sak_pub_hlth=24"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+ 
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="747 / 021 BMC Healthnet Member. For Medical Services Call 1-888-566-0008. For Behavioral Health Services Call 1-866-444-5155.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 

	}
	
	@Test
	public void RM_748_021() throws Exception {
		TestNGCustom.TCNo="RM_748_021";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+ 
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=25617 and l.cde_service_loc='D'"+
					 " and p.sak_pub_hlth=25"+
					 " and p.dte_end ='22991231'"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="748 / 021 BMC Healthnet Member. For Medical Services Call 1-888-566-0008. For Behavioral Health Services Call 1-866-444-5155.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 

	}
	
	@Test
	public void RM_749_021() throws Exception {
		TestNGCustom.TCNo="RM_749_021";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=25617 and l.cde_service_loc='D' and p.cde_rsn_mc_stop <> 'Z8'"+
					 " and p.sak_pub_hlth=26"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="749 / 021 BMC Healthnet Member. For Medical Services Call 1-888-566-0008. For Behavioral Health Services Call 1-866-444-5155.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 

	}
	
	@Test
	public void RM_770_648() throws Exception {
		TestNGCustom.TCNo="RM_770_648";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=180"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1<>'H' and sak_cde_aid<>180 and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="770 / 648 HSN PHARMACY COPAYS MAY BE APPLICABLE";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_773_500() throws Exception {
		TestNGCustom.TCNo="RM_773_500";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1<> 'H' and p.sak_pub_hlth=29 and p.dte_end >=to_char(sysdate,'YYYYMMDD')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="773 / 500 Special NHP program. Call NHP at 1-888-816-6000 for authorization for all services except family planning, glasses, and most dental. For Behavioral Health Services Call 1-800-414-2820.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_791_637() throws Exception {
		TestNGCustom.TCNo="RM_791_637";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e, t_tpl_resource t, t_coverage_xref c"+
					 " where b.sak_recip=e.sak_recip and b.sak_recip=t.sak_recip and t.sak_tpl_resource=c.sak_tpl_resource"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=180"+
					 " and c.dte_end >=to_char(sysdate,'YYYYMMDD') and c.dte_effective < to_char(sysdate,'YYYYMMDD')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="791 / 637 MEMBER IS HSN SECONDARY. BILL MEMBERS PRIVATE HEALTH INSURANCE. SEE 114.6 CMR 13.00 FOR INFO ON TPL REQS.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_792_637() throws Exception {
		TestNGCustom.TCNo="RM_792_637";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e, t_tpl_resource t, t_coverage_xref c"+
					 " where b.sak_recip=e.sak_recip and b.sak_recip=t.sak_recip and t.sak_tpl_resource=c.sak_tpl_resource"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=181"+
					 " and c.dte_end >=to_char(sysdate,'YYYYMMDD') and c.dte_effective < to_char(sysdate,'YYYYMMDD')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="792 / 637 MEMBER IS HSN SECONDARY. BILL MEMBERS PRIVATE HEALTH INSURANCE. SEE 114.6 CMR 13.00 FOR INFO ON TPL REQS.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_827_386() throws Exception {
		TestNGCustom.TCNo="RM_827_386";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=152 and e.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >= to_char(sysdate, 'YYYYMMDD') and sak_cde_aid <>152)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="827 / 386 Medicare-Covered Services Only.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_831_386() throws Exception {
		TestNGCustom.TCNo="RM_831_386";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=150 and e.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >= to_char(sysdate, 'YYYYMMDD') and sak_cde_aid <>150)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="831 / 386 Medicare-Covered Services Only.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_832_386() throws Exception {
		TestNGCustom.TCNo="RM_832_386";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=151 and e.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >= to_char(sysdate, 'YYYYMMDD') and sak_cde_aid <>151)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="832 / 386 Medicare-Covered Services Only.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_840_386() throws Exception {
		TestNGCustom.TCNo="RM_840_386";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=148 and e.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >= to_char(sysdate, 'YYYYMMDD') and sak_cde_aid <>148)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="840 / 386 Medicare-Covered Services Only.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_841_386() throws Exception {
		TestNGCustom.TCNo="RM_841_386";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=149 and e.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >= to_char(sysdate, 'YYYYMMDD') and sak_cde_aid <>149)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="841 / 386 Medicare-Covered Services Only.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_853_636() throws Exception {
		TestNGCustom.TCNo="RM_853_636";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					" where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					" and e.cde_status1 <> 'H' and e.sak_cde_aid=123"+
					" and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					" and not exists (select 1 from t_re_aid_elig e1"+
					" where e1.sak_recip=b.sak_recip and e1.cde_status1 <> 'H'"+
					" and e1.sak_cde_aid <> e.sak_cde_aid"+
					" and to_char(sysdate, 'YYYYMMDD') between e1.dte_effective and e1.dte_end)"+
					" and rownum < 2";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="853 / 636 MEMBER IS ALSO ELIGIBLE FOR HSN SECONDARY. SEE 101 CMR 613.00 FOR INFO ON HSN REQUIREMENTS.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_915_647() throws Exception {
		TestNGCustom.TCNo="RM_915_647";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig a"+
					 " where b.sak_recip=a.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and a.cde_status1 <> 'H' and a.sak_cde_aid=181"+
					 " and a.dte_end >=to_char(sysdate,'YYYYMMDD')"+
					 " and not exists (select 1 from t_re_aid_elig a1 where a1.sak_recip=b.sak_recip and a1.cde_status1 <> 'H'"+
					 " and a1.sak_cde_aid <> a.sak_cde_aid and a1.dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="915 / 647 HSN MEDICAL AND PHARMACY COPAYS MAY BE APPLICABLE";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_936_656() throws Exception {
		TestNGCustom.TCNo="RM_936_656";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=131"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >=to_char(sysdate,'YYYYMMDD') and sak_cde_aid<>e.sak_cde_aid)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="936 / 656 Member eligible for full MassHealth dental. Bill member's private health insurance first. For information on dental services and claims, call Doral at 1-800-207-5019.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_937_656() throws Exception {
		TestNGCustom.TCNo="RM_937_656";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
				 " where b.sak_recip=e.sak_recip"+
				 " and ind_active <> 'N' and dte_death='0'"+
				 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=144"+
				 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >=to_char(sysdate,'YYYYMMDD') and sak_cde_aid<>e.sak_cde_aid)"+
				 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="937 / 656 Member eligible for full MassHealth dental. Bill member's private health insurance first. For information on dental services and claims, call Doral at 1-800-207-5019.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_938_656() throws Exception {
		TestNGCustom.TCNo="RM_938_656";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=146"+
					 " and not exists (select 1 from t_re_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_pub_hlth not in (7,17) and dte_end='22991231')"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="938 / 656 Member eligible for full MassHealth dental. Bill member's private health insurance first. For information on dental services and claims, call Doral at 1-800-207-5019.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_939_656() throws Exception {
		TestNGCustom.TCNo="RM_939_656";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=141"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >=to_char(sysdate,'YYYYMMDD') and sak_cde_aid<>e.sak_cde_aid)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="939 / 656 Member eligible for full MassHealth dental. Bill member's private health insurance first. For information on dental services and claims, call Doral at 1-800-207-5019.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_940_656() throws Exception {
		TestNGCustom.TCNo="RM_940_656";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
//		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
//					 " where b.sak_recip=e.sak_recip"+
//					 " and ind_active <> 'N' and dte_death='0'"+
//					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=140"+
//					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >=to_char(sysdate,'YYYYMMDD') and sak_cde_aid<>e.sak_cde_aid)"+
//					 " and rownum=1";
		
        sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
                " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
                " and e.cde_status1 <> 'H'"+
                " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '75')"+
                " and to_char(sysdate,'YYYYMMDD') between e.dte_effective and e.dte_end"+
                " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="940 / 656 Member eligible for full MassHealth dental. Bill member's private health insurance first. For information on dental services and claims, call Doral at 1-800-207-5019.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_941_656() throws Exception {
		TestNGCustom.TCNo="RM_941_656";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=143"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >=to_char(sysdate,'YYYYMMDD') and sak_cde_aid<>e.sak_cde_aid)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="941 / 656 Member eligible for full MassHealth dental. Bill member's private health insurance first. For information on dental services and claims, call Doral at 1-800-207-5019.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_942_656() throws Exception {
		TestNGCustom.TCNo="RM_942_656";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
//		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
//					 " where b.sak_recip=e.sak_recip"+
//					 " and ind_active <> 'N' and dte_death='0'"+
//					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_cde_aid=142"+
//					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >=to_char(sysdate,'YYYYMMDD') and sak_cde_aid<>e.sak_cde_aid)"+
//					 " and rownum=1";
		
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
                     " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
                     " and e.cde_status1 <> 'H'"+
                     " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '78')"+
                     " and to_char(sysdate,'YYYYMMDD') between e.dte_effective and e.dte_end"+
                     " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid"+
                     " and to_char(sysdate,'YYYYMMDD') between dte_effective and dte_end)"+
                     " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="942 / 656 Member eligible for full MassHealth dental. Bill member's private health insurance first. For information on dental services and claims, call Doral at 1-800-207-5019.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_943_657() throws Exception {
		TestNGCustom.TCNo="RM_943_656";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
					 " where b.sak_recip=e.sak_recip"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_pub_hlth=10"+
					 " and not exists (select 1 from t_re_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >=to_char(sysdate,'YYYYMMDD') and sak_pub_hlth<>e.sak_pub_hlth)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="943 / 657 Member is covered for seasonal and H1N1 flu administration.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_944_657() throws Exception {
		TestNGCustom.TCNo="RM_944_657";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
				 " where b.sak_recip=e.sak_recip"+
				 " and ind_active <> 'N' and dte_death='0'"+
				 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_pub_hlth=18"+
				 " and not exists (select 1 from t_re_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >=to_char(sysdate,'YYYYMMDD') and sak_pub_hlth<>e.sak_pub_hlth)"+
				 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="944 / 657 Member is covered for seasonal and H1N1 flu administration.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_945_657() throws Exception {
		TestNGCustom.TCNo="RM_945_657";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
				 " where b.sak_recip=e.sak_recip"+
				 " and ind_active <> 'N' and dte_death='0'"+
				 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_pub_hlth=15"+
				 " and not exists (select 1 from t_re_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >=to_char(sysdate,'YYYYMMDD') and sak_pub_hlth<>e.sak_pub_hlth)"+
				 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="945 / 657 Member is covered for seasonal and H1N1 flu administration.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_946_657() throws Exception {
		TestNGCustom.TCNo="RM_946_657";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
				 " where b.sak_recip=e.sak_recip"+
				 " and ind_active <> 'N' and dte_death='0'"+
				 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_pub_hlth=38"+
				 " and not exists (select 1 from t_re_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >=to_char(sysdate,'YYYYMMDD') and sak_pub_hlth<>e.sak_pub_hlth)"+
				 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="946 / 657 Member is covered for seasonal and H1N1 flu administration.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_947_657() throws Exception {
		TestNGCustom.TCNo="RM_947_657";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
				 " where b.sak_recip=e.sak_recip"+
				 " and ind_active <> 'N' and dte_death='0'"+
				 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_pub_hlth=11"+
				 " and not exists (select 1 from t_re_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >=to_char(sysdate,'YYYYMMDD') and sak_pub_hlth<>e.sak_pub_hlth)"+
				 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="947 / 657 Member is covered for seasonal and H1N1 flu administration.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_948_657() throws Exception {
		TestNGCustom.TCNo="RM_948_657";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
					 " where b.sak_recip=e.sak_recip"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end ='22991231' and e.sak_pub_hlth=6"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="948 / 657 Member is covered for seasonal and H1N1 flu administration.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_949_657() throws Exception {
		TestNGCustom.TCNo="RM_949_657";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
				 " where b.sak_recip=e.sak_recip"+
				 " and ind_active <> 'N' and dte_death='0'"+
				 " and e.cde_status1 <> 'H' and e.dte_end >=to_char(sysdate,'YYYYMMDD') and e.sak_pub_hlth=5"+
				 " and not exists (select 1 from t_re_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >=to_char(sysdate,'YYYYMMDD') and sak_pub_hlth<>e.sak_pub_hlth)"+
				 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="949 / 657 Member is covered for seasonal and H1N1 flu administration.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_985_658() throws Exception {
		TestNGCustom.TCNo="RM_985_658";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_elig e"+
				 	 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
				 	 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
				  	 " and e.sak_pub_hlth=11"+
				  	 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="985 / 658 Effective July 1, 2010, global delivery codes for HSP members must be Billed to MassHealth. For more information, call 1-800-841-2900.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_986_661() throws Exception {
		TestNGCustom.TCNo="RM_986_661";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc and l.sak_prov=31464 and l.cde_service_loc='A'"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="986 / 661 Health New England member. For medical services call 1-800-786-9999. For behavioral health services call 1-800-495-0086.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_987_662() throws Exception {
		TestNGCustom.TCNo="RM_987_662";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc and l.sak_prov=31464 and l.cde_service_loc='A'"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="987 / 662 Health New England member. For dental services call 1-800-786-9999. For vision services call 1-800-786-9999";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1055_617() throws Exception {
		TestNGCustom.TCNo="RM_1055_617";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and substr(p.dte_end,1,6) > substr(to_char(sysdate,'YYYYMMDD'),1,6) and p.sak_pub_hlth=24"+
					 " and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc and l.sak_prov=31467 and l.cde_service_loc='A'"+
					 " and concat(substr(dte_birth,1,4)+65, substr(dte_birth,5,2)) > substr(to_char(sysdate,'YYYYMMDD'),1,6)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1055 / 617 NHP MEMBER. FOR DENTAL SERVICES CALL 1-800-685-9971. FOR VISION SERVICES CALL 1-800-638-3120.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1056_617() throws Exception {
		TestNGCustom.TCNo="RM_1056_617";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.dte_end='22991231' and p.sak_pub_hlth=25"+
					 " and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc and l.sak_prov=31467 and l.cde_service_loc='A'"+
					 " and substr(dte_birth,1,4)+65 > '2020'"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1056 / 617 NHP MEMBER. FOR DENTAL SERVICES CALL 1-800-685-9971. FOR VISION SERVICES CALL 1-800-638-3120.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1057_617() throws Exception {
		TestNGCustom.TCNo="RM_1057_617";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and substr(p.dte_end,1,6) > substr(to_char(sysdate,'YYYYMMDD'),1,6) and p.sak_pub_hlth=26"+
					 " and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc and l.sak_prov=31467 and l.cde_service_loc='A'"+
					 " and concat(substr(dte_birth,1,4)+65, substr(dte_birth,5,2)) > substr(to_char(sysdate,'YYYYMMDD'),1,6)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1057 / 617 NHP MEMBER. FOR DENTAL SERVICES CALL 1-800-685-9971. FOR VISION SERVICES CALL 1-800-638-3120.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1059_618() throws Exception {
		TestNGCustom.TCNo="RM_1059_618";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=25617 and l.cde_service_loc='D' and p.cde_rsn_mc_stop <> 'Z8'"+
					 " and p.sak_pub_hlth=24"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1059 / 618 BMC HEALTHNET PLAN MEMBER. FOR DENTAL SERVICES CALL 1-800-207-8147. FOR VISION SERVICES CALL 1-800-877-7195.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1060_618() throws Exception {
		TestNGCustom.TCNo="RM_1060_618";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=25617 and l.cde_service_loc='D'"+
					 " and p.sak_pub_hlth=25"+
					 " and p.dte_end ='22991231'"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1060 / 618 BMC HEALTHNET PLAN MEMBER. FOR DENTAL SERVICES CALL 1-800-207-8147. FOR VISION SERVICES CALL 1-800-877-7195.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1061_618() throws Exception {
		TestNGCustom.TCNo="RM_1061_618";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=25617 and l.cde_service_loc='D' and p.cde_rsn_mc_stop <> 'Z8'"+
					 " and p.sak_pub_hlth=26"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1061 / 618 BMC HEALTHNET PLAN MEMBER. FOR DENTAL SERVICES CALL 1-800-207-8147. FOR VISION SERVICES CALL 1-800-877-7195.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1063_619() throws Exception {
		TestNGCustom.TCNo="RM_1063_619";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=31449 and l.cde_service_loc='B'"+
					 " and p.dte_end ='22991231'"+
					 " and p.sak_pub_hlth=24"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1063 / 619 FALLON COMMUNITY HEALTH PLAN MEMBER. FOR DENTAL SERVICES CALL 1-866-275-3247. FOR VISION SERVICES CALL 1-866-275-3247.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1064_619() throws Exception {
		TestNGCustom.TCNo="RM_1064_619";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and substr(p.dte_end,1,6) > substr(to_char(sysdate,'YYYYMMDD'),1,6) and p.sak_pub_hlth=25"+
					 " and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc and l.sak_prov=31449 and l.cde_service_loc='B'"+
					 " and substr(dte_birth,1,4)+65 > substr(to_char(sysdate,'YYYYMMDD'),1,4)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1064 / 619 FALLON COMMUNITY HEALTH PLAN MEMBER. FOR DENTAL SERVICES CALL 1-866-275-3247. FOR VISION SERVICES CALL 1-866-275-3247.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1065_619() throws Exception {
		TestNGCustom.TCNo="RM_1065_619";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and substr(p.dte_end,1,6) > substr(to_char(sysdate,'YYYYMMDD'),1,6) and p.sak_pub_hlth=26"+
					 " and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc and l.sak_prov=31449 and l.cde_service_loc='B'"+
					 " and substr(dte_birth,1,4)+65 > substr(to_char(sysdate,'YYYYMMDD'),1,4)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1065 / 619 FALLON COMMUNITY HEALTH PLAN MEMBER. FOR DENTAL SERVICES CALL 1-866-275-3247. FOR VISION SERVICES CALL 1-866-275-3247.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1119_663() throws Exception {
		TestNGCustom.TCNo="RM_1119_663";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p"+
					 " where b.sak_recip=p.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.dte_end ='22991231' and p.sak_pub_hlth=82"+
					 " and substr(dte_birth,1,4)+20 > '2020'"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1119 / 663 Member is enrolled in BH managed care and has TPL or Medicare or is in an aid category excluded from enrollment in an MCO or the PCC Plan. MassHealth is the payer of last resort. For behavioral health services authorization, call 1-800-495-0086.";
		//Select BH tab
		driver.findElement(By.xpath("//*[contains(@id, 'BHList:0:providerName')]")).click();
		rmCommon(rmMessage, bhTarget); 
		
	}
	
	@Test
	public void RM_1120_664() throws Exception {
		TestNGCustom.TCNo="RM_1120_664";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p"+
					 " where b.sak_recip=p.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.dte_end ='22991231' and p.sak_pub_hlth=83"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1120 / 664 Member is enrolled in BH managed care and has TPL or Medicare or is in an aid category excluded from enrollment in an MCO or the PCC Plan. MassHealth is the payer of last resort. For behavioral health services authorization, call 1-800-495-0086.";
		//Select BH tab
		driver.findElement(By.xpath("//*[contains(@id, 'BHList:0:providerName')]")).click();
		rmCommon(rmMessage, bhTarget); 
		
	}
	
	@Test
	public void RM_1136_616() throws Exception {
		TestNGCustom.TCNo="RM_1136_616";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.dte_end ='22991231' and p.sak_pub_hlth=26"+
					 " and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc and l.sak_prov=88791 and l.cde_service_loc='A'"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1136 / 616 NETWORK HEALTH MEMBER. FOR DENTAL SERVICES CALL 1-888-257-1985. FOR VISION SERVICES CALL 1-888-257-1985";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1137_616() throws Exception {
		TestNGCustom.TCNo="RM_1137_616";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.dte_end ='22991231' and p.sak_pub_hlth=25"+
					 " and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc and l.sak_prov=88791 and l.cde_service_loc='A'"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1137 / 616 NETWORK HEALTH MEMBER. FOR DENTAL SERVICES CALL 1-888-257-1985. FOR VISION SERVICES CALL 1-888-257-1985";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1138_616() throws Exception {
		TestNGCustom.TCNo="RM_1138_616";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.dte_end ='22991231' and p.sak_pub_hlth=24"+
					 " and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc and l.sak_prov=88791 and l.cde_service_loc='A'"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1138 / 616 NETWORK HEALTH MEMBER. FOR DENTAL SERVICES CALL 1-888-257-1985. FOR VISION SERVICES CALL 1-888-257-1985";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1144_056() throws Exception {
		TestNGCustom.TCNo="RM_1144_056";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=88791 and l.cde_service_loc='A'"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and p.sak_pub_hlth=25"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1144 / 056 NETWORK HEALTH MEMBER. For Medical Services call 1-888-257-1985. For Behavioral Health Services call 1-888-257-1985.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	
	@Test
	public void RM_1145_056() throws Exception {
		TestNGCustom.TCNo="RM_1145_056";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=88791 and l.cde_service_loc='A'"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and p.sak_pub_hlth=26"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1145 / 056 NETWORK HEALTH MEMBER. For Medical Services call 1-888-257-1985. For Behavioral Health Services call 1-888-257-1985.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1146_056() throws Exception {
		TestNGCustom.TCNo="RM_1146_056";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=88791 and l.cde_service_loc='A'"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and p.sak_pub_hlth=24"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1146 / 056 NETWORK HEALTH MEMBER. For Medical Services call 1-888-257-1985. For Behavioral Health Services call 1-888-257-1985.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1147_056() throws Exception {
		TestNGCustom.TCNo="RM_1147_056";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=88791 and l.cde_service_loc='A'"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and p.sak_pub_hlth=75"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1147 / 056 NETWORK HEALTH MEMBER. For Medical Services call 1-888-257-1985. For Behavioral Health Services call 1-888-257-1985.";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1148_616() throws Exception {
		TestNGCustom.TCNo="RM_1148_616";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and ind_active <> 'N' and dte_death='0'"+
					 " and p.cde_status1 <> 'H' and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and l.sak_prov=88791 and l.cde_service_loc='A'"+
					 " and p.dte_end >= to_char(sysdate, 'YYYYMMDD')"+
					 " and p.sak_pub_hlth=75"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1148 / 616 NETWORK HEALTH MEMBER. FOR DENTAL SERVICES CALL 1-888-257-1985. FOR VISION SERVICES CALL 1-888-257-1985";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	@Test
	public void RM_1151_634() throws Exception {
		TestNGCustom.TCNo="RM_943_656";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b,  t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and not exists (select 1 from t_re_pmp_assign where cde_status1 <> 'H' and dte_end ='22991231' and sak_recip=b.sak_recip)"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=184 and e.dte_end='22991231'"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1151 / 634 MEMBER MUST ENROLL IN COMMCARE TO RECEIVE THESE BENEFITS. MEMBER MUST CALL 1-877-MA-ENROLL (1-877-623-6765).";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_1155_634() throws Exception {
		TestNGCustom.TCNo="RM_1155_634";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b,  t_re_aid_elig e"+
				 " where b.sak_recip=e.sak_recip and ind_active ='Y' and dte_death='0'"+
				 " and e.sak_cde_aid=191 and e.dte_end > to_char(sysdate, 'YYYYMMDD') and e.cde_status1 <> 'H'"+
				 " and not exists (select 1 from t_re_pmp_assign where cde_status1 <> 'H' and sak_recip=b.sak_recip and dte_end > to_char(sysdate, 'YYYYMMDD'))"+
				 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1155 / 634 MEMBER MUST ENROLL IN COMMCARE TO RECEIVE THESE BENEFITS. MEMBER MUST CALL 1-877-MA-ENROLL (1-877-623-6765).";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_1156_634() throws Exception {
		TestNGCustom.TCNo="RM_1156_634";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b,  t_re_aid_elig e"+
				 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
				 " and not exists (select 1 from t_re_pmp_assign where cde_status1 <> 'H' and dte_end ='22991231' and sak_recip=b.sak_recip)"+
				 " and e.cde_status1 <> 'H' and e.sak_cde_aid=193 and e.dte_end='22991231'"+
				 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1156 / 634 MEMBER MUST ENROLL IN COMMCARE TO RECEIVE THESE BENEFITS. MEMBER MUST CALL 1-877-MA-ENROLL (1-877-623-6765).";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_1157_634() throws Exception {
		TestNGCustom.TCNo="RM_1157_634";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
				" where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
				" and e.cde_status1 <> 'H' and e.sak_cde_aid=197 and e.dte_end >= to_char(sysdate, 'YYYYMMDD') and e.dte_effective <= to_char(sysdate, 'YYYYMMDD')"+
				" and not exists (select 1 from t_re_pmp_assign where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_end >= to_char(sysdate,'YYYYMMDD'))"+
				" and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1157 / 634 MEMBER MUST ENROLL IN COMMCARE TO RECEIVE THESE BENEFITS. MEMBER MUST CALL 1-877-MA-ENROLL (1-877-623-6765).";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_1158_634() throws Exception {
		TestNGCustom.TCNo="RM_1158_634";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b,  t_re_aid_elig e"+
				 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
				 " and not exists (select 1 from t_re_pmp_assign where cde_status1 <> 'H' and dte_end ='22991231' and sak_recip=b.sak_recip)"+
				 " and e.cde_status1 <> 'H' and e.sak_cde_aid=195 and e.dte_end='22991231'"+
				 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1158 / 634 MEMBER MUST ENROLL IN COMMCARE TO RECEIVE THESE BENEFITS. MEMBER MUST CALL 1-877-MA-ENROLL (1-877-623-6765).";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_1231_665() throws Exception {
		TestNGCustom.TCNo="RM_1231_665";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_pmp_assign p, t_pmp_svc_loc l"+
					 " where b.sak_recip=p.sak_recip and p.sak_pmp_ser_loc=l.sak_pmp_ser_loc"+
					 " and ind_active <> 'N' and dte_death='0'"+
					 " and l.sak_prov=31470 and l.cde_service_loc='B'"+
					 " and p.cde_status1 <> 'H' and p.dte_end='22991231'"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1231 / 665 Senior Care Options. Payment limited to SCO. Authorization needed for all services except emergencies. Call Tufts Health Plan Senior Care Options - 855-670-5934";
		//Select MC tab
		driver.findElement(By.xpath("//*[contains(@id, 'MCOList:0:providerName')]")).click();
		rmCommon(rmMessage, mcTarget); 
		
	}
	
	
	@Test
	public void RM_950_611() throws Exception {
		TestNGCustom.TCNo="RM_950_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '01')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="950 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_951_611() throws Exception {
		TestNGCustom.TCNo="RM_951_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '02')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="951 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget);  
		
	}
	
	@Test
	public void RM_952_611() throws Exception {
		TestNGCustom.TCNo="RM_952_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '03')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="952 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget);  
		
	}
	
	@Test
	public void RM_953_611() throws Exception {
		TestNGCustom.TCNo="RM_953_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '04')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="953 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget);  
		
	}
	
	@Test
	public void RM_954_611() throws Exception {
		TestNGCustom.TCNo="RM_954_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '05')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="954 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_955_611() throws Exception {
		TestNGCustom.TCNo="RM_955_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '06')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="955 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_956_611() throws Exception {
		TestNGCustom.TCNo="RM_956_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '07')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="956 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_957_611() throws Exception {
		TestNGCustom.TCNo="RM_957_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '14')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="957 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_958_611() throws Exception {
		TestNGCustom.TCNo="RM_958_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '15')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="958 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_959_611() throws Exception {
		TestNGCustom.TCNo="RM_959_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '17')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="959 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_960_611() throws Exception {
		TestNGCustom.TCNo="RM_960_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '18')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="960 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_962_611() throws Exception {
		TestNGCustom.TCNo="RM_962_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '20')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="962 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_963_611() throws Exception {
		TestNGCustom.TCNo="RM_963_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '21')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="963 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}	
	
	@Test
	public void RM_964_611() throws Exception {
		TestNGCustom.TCNo="RM_964_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '22')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="964 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}	
	
	@Test
	public void RM_965_611() throws Exception {
		TestNGCustom.TCNo="RM_965_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '23')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="965 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_966_611() throws Exception {
		TestNGCustom.TCNo="RM_966_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
				" where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
				" and e.cde_status1 <> 'H' and e.sak_cde_aid=4 and e.dte_end >= to_char(sysdate, 'YYYYMMDD') and e.dte_effective <= to_char(sysdate, 'YYYYMMDD')"+
				" and exists (select 1 from t_re_hib where sak_recip=b.sak_recip and  dte_end >= to_char(sysdate,'YYYYMMDD'))"+
				" and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="966 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_967_611() throws Exception {
		TestNGCustom.TCNo="RM_967_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '42')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="967 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}	
	
	@Test
	public void RM_968_611() throws Exception {
		TestNGCustom.TCNo="RM_968_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '43')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="968 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}	
	
	@Test
	public void RM_969_611() throws Exception {
		TestNGCustom.TCNo="RM_969_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '45')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="969 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_970_611() throws Exception {
		TestNGCustom.TCNo="RM_970_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '46')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="970 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}	
	@Test
	public void RM_971_611() throws Exception {
		TestNGCustom.TCNo="RM_971_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					" where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					" and e.cde_status1 <> 'H' and e.sak_cde_aid=3"+
					" and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					" and not exists (select 1 from t_re_aid_elig e1"+
					" where e1.sak_recip=b.sak_recip and e1.cde_status1 <> 'H'"+
					" and e1.sak_cde_aid <> e.sak_cde_aid"+
					" and to_char(sysdate, 'YYYYMMDD') between e1.dte_effective and e1.dte_end)"+
					" and exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip"+
					" and to_char(sysdate, 'YYYYMMDD') between dte_effective and dte_end)"+
					" and exists (select 1 from t_re_medicare_b where sak_recip=b.sak_recip"+
					" and to_char(sysdate, 'YYYYMMDD') between dte_effective and dte_end)"+
					" and rownum < 2";



		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="971 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}	
	@Test
	public void RM_972_611() throws Exception {
		TestNGCustom.TCNo="RM_972_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = 'TN')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="972 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}	
	@Test
	public void RM_973_611() throws Exception {
		TestNGCustom.TCNo="RM_973_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = 'TP')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="973 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}	
	@Test
	public void RM_974_611() throws Exception {
		TestNGCustom.TCNo="RM_974_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = 'TQ')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="974 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}	
	@Test
	public void RM_975_611() throws Exception {
		TestNGCustom.TCNo="RM_975_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=14"+
					 " and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists (select 1 from t_re_aid_elig e1"+
					 " where e1.sak_recip=b.sak_recip and e1.cde_status1 <> 'H'"+
					 " and e1.sak_cde_aid <> e.sak_cde_aid"+
					 " and to_char(sysdate, 'YYYYMMDD') between e1.dte_effective and e1.dte_end)"+
					 " and exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip"+
					 " and to_char(sysdate, 'YYYYMMDD') between dte_effective and dte_end)"+
					 " and exists (select 1 from t_re_medicare_b where sak_recip=b.sak_recip"+
					 " and to_char(sysdate, 'YYYYMMDD') between dte_effective and dte_end)"+
					 " and rownum < 2";



		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="975 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}	
	
	@Test
	public void RM_976_611() throws Exception {
		TestNGCustom.TCNo="RM_976_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=15 and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and"+
					 " to_char(sysdate,'YYYYMMDD')between dte_effective and dte_end) or"+
					 " exists (select 1 from t_re_medicare_b where sak_recip=b.sak_recip and"+
					 " to_char(sysdate,'YYYYMMDD')between dte_effective and dte_end)) and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid"+
					 " and dte_end >= to_char(sysdate,'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="976 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_977_611() throws Exception {
		TestNGCustom.TCNo="RM_977_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = 'UJ')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="977 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_980_611() throws Exception {
		TestNGCustom.TCNo="RM_980_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = 'UU')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="980 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_981_611() throws Exception {
		TestNGCustom.TCNo="RM_981_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=23"+
					 " and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip"+
					 " and to_char(sysdate, 'YYYYMMDD') between dte_effective and dte_end) or"+
					 " exists (select 1 from t_re_medicare_b where sak_recip=b.sak_recip"+
					 " and to_char(sysdate, 'YYYYMMDD') between dte_effective and dte_end))"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H'"+
					 " and sak_cde_aid <> e.sak_cde_aid and to_char(sysdate, 'YYYYMMDD') between dte_effective and dte_end)"+
					 " and rownum < 2";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="981 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_982_611() throws Exception {
		TestNGCustom.TCNo="RM_976_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = 'UC')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="982 / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_961d_611() throws Exception {

		i[0][0] = "01"; 
		i[1][0] = "03";
		i[2][0] = "05";
		i[3][0] = "06";
		i[4][0] = "07";
		i[5][0] = "14";
		i[6][0] = "18";
		i[7][0] = "20";
		i[8][0] = "21";
		i[9][0] = "22";
		i[10][0] = "23";
		i[11][0] = "40";
		i[12][0] = "42";
		i[13][0] = "43";
		i[14][0] = "45";
		i[15][0] = "46";
		i[16][0] = "48";
		i[17][0] = "TN";
		i[18][0] = "TP";
		i[19][0] = "TQ";
		i[20][0] = "TR";
		i[21][0] = "TS";
		i[22][0] = "US";
		i[23][0] = "UJ";
		i[24][0] = "UT";
		i[25][0] = "UU";
		i[26][0] = "UV";
		i[27][0] = "UC";
		
		i[0][1] = "950";
		i[1][1] = "952";
		i[2][1] = "954";
		i[3][1] = "955";
		i[4][1] = "956";
		i[5][1] = "957";
		i[6][1] = "960";
		i[7][1] = "962";
		i[8][1] = "963";
		i[9][1] = "964";
		i[10][1] = "965";
		i[11][1] = "966";
		i[12][1] = "967";
		i[13][1] = "968";
		i[14][1] = "969";
		i[15][1] = "970";
		i[16][1] = "971";
		i[17][1] = "972";
		i[18][1] = "973";
		i[19][1] = "974";
		i[20][1] = "975";
		i[21][1] = "976";
		i[22][1] = "977";
		i[23][1] = "978";
		i[24][1] = "979";
		i[25][1] = "980";
		i[26][1] = "981";
		i[27][1] = "982";

		
		for (int j=25;j<i.length;j++) {
		TestNGCustom.TCNo="RM_"+i[j][1]+"_611";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active <>'N' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.dte_end >= to_char(sysdate,'YYYYMMDD')"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '"+i[j][0]+"')"+
					 " and (exists (select 1 from t_re_medicare_a where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD'))"+
					 " or exists(select 1 from t_re_medicare_b where sak_recip=b.sak_recip and dte_end >=to_char(sysdate,'YYYYMMDD')))"+
					 " and not exists(select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid and dte_end >= to_char(sysdate, 'YYYYMMDD'))"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage=i[j][1]+" / 611 Member is Qualified Medicare Beneficiary. See 130 CMR 519.010.";
		rmCommon(rmMessage, eligTarget); 
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@class='buttonCommand' and @value='Close']")).size()>0) 
			driver.findElement(By.xpath("//input[@class='buttonCommand' and @value='Close']")).click();
		else if (driver.findElements(By.xpath("//input[@class='buttonCommand' and @value='Cancel Service']")).size()>0) 
			driver.findElement(By.xpath("//input[@class='buttonCommand' and @value='Cancel Service']")).click();
		
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.findElement(By.linkText("Home")).click();
		Common.isAlertPresent();
		driver.findElement(By.linkText("Home")).click();
		
		}
		
	}
	
	public void RMreset(String sql) throws Exception {
		driver.findElement(By.linkText("Manage Members")).click();
		driver.findElement(By.linkText("Eligibility")).click();
		driver.findElement(By.linkText("Verify Member Eligibility")).click();
		
		System.out.println("//TC "+TestNGCustom.TCNo+" - "+sql);

		
		//Select Provider
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'eligInquireSearchProviderID')]"))).selectByIndex(1);
		colNames.add("ID_MEDICAID");
		colValues=Common.executeQuery(sql, colNames);

		if (colValues.get(0).equals("null")) {
			fillExtract("No member found for this RM");
			new Select(driver.findElement(By.xpath("//select[contains(@id, 'eligInquireSearchProviderID')]"))).selectByIndex(0);
			log("Member: Skipping this because no member found for this RM");
		    throw new SkipException("Skipping this because no member found for this RM");
		}
		
		fillExtract(colValues.get(0));
		log("Member: "+colValues.get(0));
		driver.findElement(By.xpath("//input[contains(@id, 'memberID')]")).sendKeys(colValues.get(0));

		submit();
		Common.getPageError(peTarget);
		
		//Select Eligibility Tab
		driver.findElement(By.xpath("//*[contains(@id, 'MENUITEM_EligTab')]")).click();
		//Select Date Range Link
		driver.findElement(By.xpath("//*[contains(@id, '0:dateRange')]")).click();
		
	}
	
	public static void submit() {
		driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Submit']")).click();
	}
	
	public void rmCommon(String message, String target) {
		rmSize = driver.findElements(By.xpath(target)).size();
		found = false;
		for (int i=0;i<rmSize;i++) {
			element = driver.findElements(By.xpath(target)).get(i);
			if (element.getText().equals(message)) {
				found = true;
				rmSize=i+1;
			}
		}
		if (!found)
			Assert.assertTrue(false, "Did not get RM "+message); 
	}
	
	
	@DataProvider
	  public  Iterator<String[]> dpEVS(Method m) throws Exception 
	  {
	    System.out.println("dp EVS");
		ArrayList<String[]> rowList = new ArrayList<String[]>();
		int l = 0;
		String[][] z=new String[l][l];;

		if(m.getName().equals("connectorFullHSN")) {
			
			l=16;
			z = new String[l][l];
		
			z[0][0] = "1C"; 
			z[1][0] = "1K";
			z[2][0] = "1M";
			z[3][0] = "1Q";
			z[4][0] = "1W";
			z[5][0] = "1X";
			z[6][0] = "2C";
			z[7][0] = "2W";
			z[8][0] = "2X";
			z[9][0] = "3C";
			z[10][0] = "3W";
			z[11][0] = "3X";
			z[12][0] = "4C";
			z[13][0] = "6D";
			z[14][0] = "6R";
			z[15][0] = "6S";
			
			z[0][1]= "1504"; 
			z[1][1] = "1505";
			z[2][1] = "1506";
			z[3][1] = "1507";
			z[4][1] = "1508";
			z[5][1] = "1509";
			z[6][1] = "1510";
			z[7][1] = "1511";
			z[8][1] = "1512";
			z[9][1] = "1513";
			z[10][1] = "1514";
			z[11][1] = "1515";
			z[12][1] = "1516";
			z[13][1] = "1517";
			z[14][1] = "1518";
			z[15][1] = "1519";
		}
		
		if(m.getName().equals("qhpFullHSN")) {
			
			l=4;
			z = new String[l][l];
		
			z[0][0] = "7D"; 
			z[1][0] = "7L";
			z[2][0] = "9G";
			z[3][0] = "9H";
			
			z[0][1]= "1520"; 
			z[1][1] = "1521";
			z[2][1] = "1522";
			z[3][1] = "1523";
		}
		
		if(m.getName().equals("connectorPartHSN")) {
			
			l=11;
			z = new String[l][l];
		
			z[0][0] = "1B"; 
			z[1][0] = "1O";
			z[2][0] = "1R";
			z[3][0] = "1Y";
			z[4][0] = "2B";
			z[5][0] = "2Y";
			z[6][0] = "3B";
			z[7][0] = "3Y";
			z[8][0] = "4B";
			z[9][0] = "6E";
			z[10][0] = "6T";
			
			z[0][1]= "1524"; 
			z[1][1] = "1525";
			z[2][1] = "1526";
			z[3][1] = "1527";
			z[4][1] = "1528";
			z[5][1] = "1529";
			z[6][1] = "1530";
			z[7][1] = "1531";
			z[8][1] = "1532";
			z[9][1] = "1533";
			z[10][1] = "1534";
		}
		
		if(m.getName().equals("qhpPartHSN")) {
			
			l=2;
			z = new String[l][l];
		
			z[0][0] = "7E"; 
			z[1][0] = "9I";
			
			z[0][1]= "1535"; 
			z[1][1] = "1536";
		}
		
		for (int y=0;y<l;y++)
		{
			String[] h = new String[2];
			h[0]=z[y][0];
			h[1]=z[y][1];
			rowList.add(h);
		}					
	
			return rowList.iterator();
	  }
	
	@Test(dataProvider = "dpEVS")
	public void connectorFullHSN(String aid, String msgNo) throws Exception {
		
		TestNGCustom.TCNo="RM_"+msgNo+"_675";
		log("//TC "+TestNGCustom.TCNo);
		//Do not test these anymore
		if (msgNo.equals("1504")) {
			log("Skipping this TC because Gloria asked to remove this tc and not test anymore");
			throw new SkipException("Skipping this TC because Gloria asked to remove this tc and not test anymore");
		}
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '"+aid+"') and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid"+
					 " and to_char(sysdate,'YYYYMMDD') between dte_effective and dte_end)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage=msgNo+" / 675 Member eligible for ConnectorCare. HSN may be available. If enrolled, HSN dental is available.";
		rmCommon(rmMessage, eligTarget); 
				
	}
	
	@Test(dataProvider = "dpEVS")
	public void qhpFullHSN(String aid, String msgNo) throws Exception {
		
		TestNGCustom.TCNo="RM_"+msgNo+"_676";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '"+aid+"') and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid"+
					 " and to_char(sysdate,'YYYYMMDD') between dte_effective and dte_end)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage=msgNo+" / 676 Member eligible for coverage through the Health Connector. HSN available. If enrolled, member is HSN Secondary. Primary insurance must be billed first.";
		rmCommon(rmMessage, eligTarget); 
				
	}
	
	@Test(dataProvider = "dpEVS")
	public void connectorPartHSN(String aid, String msgNo) throws Exception {
		
		TestNGCustom.TCNo="RM_"+msgNo+"_677";
		log("//TC "+TestNGCustom.TCNo);
		//Do not test these anymore
		if (msgNo.equals("1524")) {
			log("Skipping this TC because Gloria asked to remove this tc and not test anymore");
			throw new SkipException("Skipping this TC because Gloria asked to remove this tc and not test anymore");
		}
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '"+aid+"') and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid"+
					 " and to_char(sysdate,'YYYYMMDD') between dte_effective and dte_end)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage=msgNo+" / 677 Member eligible for ConnectorCare. HSN Partial may be available. If enrolled, Partial HSN dental is available.";
		rmCommon(rmMessage, eligTarget); 
				
	}
	
	@Test(dataProvider = "dpEVS")
	public void qhpPartHSN(String aid, String msgNo) throws Exception {
		
		TestNGCustom.TCNo="RM_"+msgNo+"_678";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = '"+aid+"') and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid"+
					 " and to_char(sysdate,'YYYYMMDD') between dte_effective and dte_end)"+
					 " and rownum=1";

		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage=msgNo+" / 678 Member eligible for coverage through the Health Connector. Partial HSN available. If enrolled, member is HSN Secondary. Primary insurance must be billed first.";
		rmCommon(rmMessage, eligTarget); 
				
	}
	
	@Test
	public void RM_1503_679() throws Exception {
		TestNGCustom.TCNo="RM_1503_679";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H'"+
					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = 'AA')"+
					 " and to_char(sysdate,'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1503 / 679 Not Eligible for Managed Care Enrollment.";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_1543_636() throws Exception {
		TestNGCustom.TCNo="RM_1543_636";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=259 and to_char(sysdate,'YYYYMMDD') between dte_effective and dte_end"+
					 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid"+
					 " and to_char(sysdate,'YYYYMMDD') between dte_effective and dte_end)"+
					 " and rownum=1";

		
//		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
//					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
//					 " and e.cde_status1 <> 'H'"+
//					 " and e.sak_cde_aid = (select sak_cde_aid from t_cde_aid where cde_aid_category = 'K1')"+
//					 " and to_char(sysdate,'YYYYMMDD') between e.dte_effective and e.dte_end"+
//					 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1543 / 636 MEMBER IS ALSO ELIGIBLE FOR HSN SECONDARY. SEE 101 CMR 613.00 FOR INFO ON HSN REQUIREMENTS";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_1540() throws Exception {
		TestNGCustom.TCNo="RM_1540";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
				 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
				 " and e.cde_status1 <> 'H' and e.sak_cde_aid='246'"+
				 " and to_char(sysdate,'YYYYMMDD') between e.dte_effective and e.dte_end"+
				 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid"+
				 " and to_char(sysdate,'YYYYMMDD') between dte_effective and dte_end)"+
				 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1540 / Family Assistance Wrap Benefits";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_1541() throws Exception {
		TestNGCustom.TCNo="RM_1541";
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
				 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
				 " and e.cde_status1 <> 'H' and e.sak_cde_aid='247'"+
				 " and to_char(sysdate,'YYYYMMDD') between e.dte_effective and e.dte_end"+
				 " and not exists (select 1 from t_re_aid_elig where sak_recip=b.sak_recip and cde_status1 <> 'H' and sak_cde_aid <> e.sak_cde_aid"+
				 " and to_char(sysdate,'YYYYMMDD') between dte_effective and dte_end)"+
				 " and rownum=1";


		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1541 / Family Assistance Wrap Benefits";
		rmCommon(rmMessage, eligTarget); 
		
	}
	
	@Test
	public void RM_1560_part1() throws Exception {
		TestNGCustom.TCNo="RM_1560_part1";
				
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=311"+
					 " and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and to_char(to_date(e.dte_effective,'YYYYMMDD')+100,'YYYYMMDD')"+
					 " and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists (select 1 from t_re_aid_elig e1 where e1.sak_recip=b.sak_recip and e1.cde_status1 <> 'H'"+
					 " and to_char(sysdate, 'YYYYMMDD') between e1.dte_effective and e1.dte_end"+
					 " and e1.sak_cde_aid <> e.sak_cde_aid)"+
					 " and rownum < 2";
		
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1560 / TEMPORARY HSN AVAILABLE. MEMBER ELIGIBLE FOR CONNECTORCARE. IF MEMBER IS UNENROLLED, VISIT MAHEALTHCONNECTOR.ORG FOR MORE INFORMATION.";
		rmCommon(rmMessage, eligTarget); 

		rmMessage="770 / 648 HSN PHARMACY COPAYS MAY BE APPLICABLE";
		rmCommon(rmMessage, eligTarget); 

		rmMessage="633 / 633 HSN IS FOR CERTAIN HOSPITAL AND CHC SERVICES ONLY. MEMBER IS NOT ELIGIBLE FOR MASSHEALTH. CALL 1-877-910-2100.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_1560_part2() throws Exception {
		TestNGCustom.TCNo="RM_1560_part2";
				
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=310"+
					 " and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and to_char(to_date(e.dte_effective,'YYYYMMDD')+100,'YYYYMMDD')"+
					 " and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists (select 1 from t_re_aid_elig e1 where e1.sak_recip=b.sak_recip and e1.cde_status1 <> 'H'"+
					 " and to_char(sysdate, 'YYYYMMDD') between e1.dte_effective and e1.dte_end"+
					 " and e1.sak_cde_aid <> e.sak_cde_aid)"+
					 " and rownum < 2";
		
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1560 / TEMPORARY HSN AVAILABLE. MEMBER ELIGIBLE FOR CONNECTORCARE. IF MEMBER IS UNENROLLED, VISIT MAHEALTHCONNECTOR.ORG FOR MORE INFORMATION.";
		rmCommon(rmMessage, eligTarget); 

		rmMessage="770 / 648 HSN PHARMACY COPAYS MAY BE APPLICABLE";
		rmCommon(rmMessage, eligTarget); 

		rmMessage="633 / 633 HSN IS FOR CERTAIN HOSPITAL AND CHC SERVICES ONLY. MEMBER IS NOT ELIGIBLE FOR MASSHEALTH. CALL 1-877-910-2100.";
		rmCommon(rmMessage, eligTarget); 
	}
	
	@Test
	public void RM_1561_part1() throws Exception {
		TestNGCustom.TCNo="RM_1561_part1";
				
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=311"+
					 " and to_char(sysdate, 'YYYYMMDD') > to_char(to_date(e.dte_effective,'YYYYMMDD')+100,'YYYYMMDD')"+
					 " and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists (select 1 from t_re_aid_elig e1 where e1.sak_recip=b.sak_recip and e1.cde_status1 <> 'H'"+
					 " and to_char(sysdate, 'YYYYMMDD') between e1.dte_effective and e1.dte_end"+
					 " and e1.sak_cde_aid <> e.sak_cde_aid)"+
					 " and rownum < 2";
		
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1561 / HSN DENTAL AVAILABLE. MEMBER ELIGIBLE FOR CONNECTORCARE. IF MEMBER IS UNENROLLED, VISIT MAHEALTHCONNECTOR.ORG FOR MORE INFORMATION.";
		rmCommon(rmMessage, eligTarget); 

	}
	
	@Test
	public void RM_1561_part2() throws Exception {
		TestNGCustom.TCNo="RM_1561_part2";
				
		log("//TC "+TestNGCustom.TCNo);
        //Get member
		sqlStatement="select id_medicaid from t_re_base b, t_re_aid_elig e"+
					 " where b.sak_recip=e.sak_recip and ind_active='Y' and dte_death='0'"+
					 " and e.cde_status1 <> 'H' and e.sak_cde_aid=310"+
					 " and to_char(sysdate, 'YYYYMMDD') > to_char(to_date(e.dte_effective,'YYYYMMDD')+100,'YYYYMMDD')"+
					 " and to_char(sysdate, 'YYYYMMDD') between e.dte_effective and e.dte_end"+
					 " and not exists (select 1 from t_re_aid_elig e1 where e1.sak_recip=b.sak_recip and e1.cde_status1 <> 'H'"+
					 " and to_char(sysdate, 'YYYYMMDD') between e1.dte_effective and e1.dte_end"+
					 " and e1.sak_cde_aid <> e.sak_cde_aid)"+
					 " and rownum < 2";
		
		RMreset(sqlStatement);
		
		//Verify RM
		rmMessage="1561 / HSN DENTAL AVAILABLE. MEMBER ELIGIBLE FOR CONNECTORCARE. IF MEMBER IS UNENROLLED, VISIT MAHEALTHCONNECTOR.ORG FOR MORE INFORMATION.";
		rmCommon(rmMessage, eligTarget); 

	}
	
	public void fillExtract(String recp) throws Exception {
		
		row++;
		
		//Add RM no.
		l = new Label (0,row, TestNGCustom.TCNo);
		ws.addCell(l);
		
	    //Add member
		l = new Label (1,row, recp);
		ws.addCell(l);
	}
	

}