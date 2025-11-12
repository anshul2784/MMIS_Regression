
package newMMIS_Subsystems;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class Referrals extends Login {
	public static String clmNo;
	public static String status;
	public static String memberId;
	public static String memberId22481;
	public static String memberId22483;
	public static String memberId22496;
	public static String memberId22497;
	public static String refNum22497;
	public static String refNum22481;
	public static Statement statement = null;
	public static ResultSet resultset = null;
	public static Connection connection= null;
	public static String wZMember;
	static String icn1;
	public static String SelSql, col,  DelSql,  InsSql;
	
	@BeforeMethod
	public void LoginCheck() throws Exception {
		Common.resetBase();
		testCheckDBLoginSuccessful();
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Referrals")).click();
	}
	
	 @BeforeTest
	    public void paStartup() throws Exception {
	    log("Starting Referrals Subsystem......");
	    }
	
	public static void warnings(){
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
		int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
         for(int i=1;i<=length;i++)
          {
	       driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:HtmlMessages_CheckBox_Warnings"+i)).click();
          }
		}
  		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  	  }
	
	
	
	/*************************Need to enter new PCCP members as we are not having enough data for all the test cases
	 * @throws SQLException 
	 * @throws IOException **************************************/
	
	 @Test
	public static String testaddWZMembers() throws SQLException, IOException{
		 TestNGCustom.TCNo="add";
		 String sql="select b.id_medicaid, b.sak_recip from t_re_base b,t_re_pmp_assign asg1, t_pmp_svc_loc loc, t_re_other_address o where  substr(asg1.dte_end,1,6) > substr(to_char(sysdate,'YYYYMMDD'),1,6) " +
				   "and asg1.sak_pub_hlth=23 and asg1.cde_status1 <> 'H' and b.sak_recip= asg1.sak_recip and loc.sak_pmp_ser_loc =asg1.sak_pmp_ser_loc and ind_active <> 'N' and dte_death='0' and asg1.dte_effective<to_char(sysdate-210, 'YYYYMMDD') and not exists( select 1 from t_re_loc loc1 where loc1.sak_recip=b.sak_recip and loc1.DTE_discharge> to_char(sysdate,'YYYYMMDD')) " +
				   "and o.sak_recip = asg1.sak_recip and o.adr_zip_code in (02122,02123,02124,02125,02126,02127,02129,02130,02131,02132,02133,02134,02135,02136,02137,02147,02153,02163,02199,02201,02203,02208,02209,02210,02215,02222,02241,02445,02446,02447,02196,02228,02284,02101,02102,02103,02104,02105,02106,02107,02108,02109,02110,02111,02112,02113,02114,02115,02116,02117,02118,02119,02120,02121,02283,02211) " +
				   "and not exists (select 1 from t_re_pmp_assign where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_effective >asg1.dte_effective and sak_pub_hlth=24) " +
				   "and b.id_medicaid <> '100004726202' and b.id_medicaid <> '100000412922' and b.id_medicaid <> '100207910868' and b.sak_recip > dbms_random.value * 6300000 and rownum<2 and b.id_medicaid <> '200004414015' and id_medicaid <> '200003548063'";
		
	//String memId= Managedcare_Common.enrollment(sql,"PCCP","62","110047258A",Common.convertSysdatecustom(-1));
		 String memId= Managedcare_Common.enrollment(sql,"PCCP","62","110000034F",Common.convertSysdatecustom(-200));

		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Referrals")).click();
		System.out.println("PCCP Member Id: "+memId);
		return memId;
	    }
	
	
	
	
	     public static String wZMember(String tcNo,String wZMember,String refProv,String servProv,String qty) throws SQLException{
	     //need to check if this query is working for all remaning test cases
		/* sqlStatement="Select distinct b.id_medicaid from t_re_pmp_assign a, t_re_base b, t_pmp_svc_loc d where a.sak_pub_hlth = 23 and d.cde_service_loc='F' and a.sak_recip = b.sak_recip " +
				      "and d.sak_prov =82159 and a.sak_pmp_ser_loc=d.sak_pmp_ser_loc and a.dte_end >to_char(sysdate, 'YYYYMMDD') and a.cde_status1 <> 'H' " +
				      "and ind_active = 'Y' and b.sak_recip > dbms_random.value * 6300000 and rownum < 2"; 
		 colNames.add("id_medicaid");
		 colValues=Common.executeQuery(sqlStatement, colNames );
		 wZMember=colValues.get(0);*/
		 
	     //Insert Data in DB
		 SelSql="select * from R_REFERRALS where tcno = '"+tcNo+"'";
		 col="TCNO";
		 DelSql="delete from R_REFERRALS where tcno = '"+tcNo+"'";  
		 InsSql="insert into  R_REFERRALS values ('"+tcNo+"', '"+wZMember+"', '"+refProv+"', '"+servProv+"','"+qty+"')";
		 Common.insertData(SelSql, col, DelSql, InsSql);
		 System.out.println("WZ Member for TC#"+tcNo+": "+wZMember);
		 return wZMember;
		 }
	
	
	     public static void pccMemProv(String sql,String tcNo,String provId,String units) throws SQLException{
//		 String sql="Select distinct b.id_medicaid,d.sak_prov,d.cde_service_loc from t_re_pmp_assign a, t_re_base b, t_pmp_svc_loc d where a.sak_pub_hlth = 23 " +
//		 		    "and d.cde_service_loc='A' and a.sak_recip = b.sak_recip and a.sak_pmp_ser_loc=d.sak_pmp_ser_loc and a.dte_end = 22991231 and a.cde_status1 <> 'H' " +
//	 		        "and b.ind_active = 'Y' and b.sak_recip > dbms_random.value * 6300000 and rownum < 2";
	     colNames.add("ID_MEDICAID");//0
	     colNames.add("SAK_PROV");//1
	     colNames.add("CDE_SERVICE_LOC");//2
	     colValues=Common.executeQuery(sql, colNames);
	     memberId=colValues.get(0);
	     String sakProv=colValues.get(1);
	     String serviceLoc=colValues.get(2);
	     String sql1="select  ID_PROVIDER from T_PR_PROV where sak_prov='"+sakProv+"'";
	     colNames.add("ID_PROVIDER");
	     colValues=Common.executeQuery(sql1, colNames);
	     String provider=colValues.get(0);
	     String providerId=provider+serviceLoc;
		 //Insert Data in DB
		 SelSql="select * from R_Referrals where tcno ='"+tcNo+"'";
		 col="MEMBERID";
		 DelSql="delete from R_REFERRALS where tcno = '"+tcNo+"'";  
		 InsSql="insert into  R_REFERRALS values ('"+tcNo+"', '"+memberId+"', '"+providerId+"', '"+provId+"','"+units+"')";
		 Common.insertData(SelSql, col, DelSql, InsSql);	
	     }
	     
	
	     public static void createReferral(String tcNo,String effDate,String endDate) throws SQLException{
		 sqlStatement=("select * from r_referrals where TCNO='"+tcNo+"'");
		 colNames.add("MEMBERID");//0
		 colNames.add("REFPROVIDERID"); //1
		 colNames.add("SERVPROVIDERID"); //2
		 colNames.add("QUANTITY"); //3
		 System.out.println("Executing SQL...");
		 colValues=Common.executeQuery1(sqlStatement, colNames );
		 String refprovider=colValues.get(1).substring(0, colValues.get(1).length()-1),
		          refsvcloc=colValues.get(1).substring(colValues.get(1).length()-1);
		 String servprovider=colValues.get(2).substring(0, colValues.get(2).length()-1),
		          servsvcloc=colValues.get(2).substring(colValues.get(2).length()-1);
		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:NEW")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:ReferralsPanel:ReferralsDataPanel_ReferringPrSvcLoc")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:ReferralsPanel:ReferralsDataPanel_ReferringPrSvcLoc")).sendKeys(refprovider); //"110082155"
		 driver.findElement(By.id("MMISForm:MMISBodyContent:ReferralsPanel:ReferralsReferringProvIDSearch")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:ReferralsPanel:ReferralsReferringProvIDSearch")).sendKeys(refsvcloc); //"A"
	     driver.findElement(By.id("MMISForm:MMISBodyContent:ReferralsPanel:ReferralsDataPanel_ServicePrSvcLoc")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:ReferralsPanel:ReferralsDataPanel_ServicePrSvcLoc")).sendKeys(servprovider);  //"110048577"
		 driver.findElement(By.id("MMISForm:MMISBodyContent:ReferralsPanel:ReferralsServiceProviderIDSearch")).clear(); 
		 driver.findElement(By.id("MMISForm:MMISBodyContent:ReferralsPanel:ReferralsServiceProviderIDSearch")).sendKeys(servsvcloc);//for 22481 F and remaining A
		 driver.findElement(By.id("MMISForm:MMISBodyContent:ReferralsPanel:ReferralsMemberIdSearch")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:ReferralsPanel:ReferralsMemberIdSearch")).sendKeys(colValues.get(0));
		 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ReferralsPanel:ReferralsDataPanel_AssignCode"))).selectByVisibleText("CONSULT, TEST AND TREAT");
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItem_NewButtonClay:RfLineItemList_newAction_btn")).click();
//		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_RequirementEffectiveDate")).clear();
//		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_RequirementEffectiveDate")).sendKeys(effDate);
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_AuthorizationEffectiveDate")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_AuthorizationEffectiveDate")).sendKeys(effDate);
		// driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_RequirementEndDate")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_AuthorizationEndDate")).clear();
		    if(tcNo.equals("22497"))
		    {
		   // driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_RequirementEndDate")).sendKeys(Common.convertSysdate());
		    	driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_AuthorizationEndDate")).sendKeys(Common.convertSysdate());
		    }
		    else if(tcNo.equals("23413a")){
		  //  driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_RequirementEndDate")).sendKeys(Common.convertSysdatecustom(365));//1 yr
		    	 driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_AuthorizationEndDate")).sendKeys(Common.convertSysdatecustom(365));//1 yr
		    }
		    //driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_RequirementEndDate")).sendKeys(Common.convertSysdatecustom(1));//tomorrow
		    driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_AuthorizationEndDate")).sendKeys(Common.convertSysdatecustom(1));//tomorrow
		    driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_UntSvcRequirementQuantity")).clear();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_UntSvcRequirementQuantity")).sendKeys(colValues.get(3));
		    
	        }
		
	  
	
	   @Test
	    //Checking for duplicate referral
	    public static void test22474() throws Exception{
		TestNGCustom.TCNo="22474";
		log("//TC 22474");
		//wZMember("22474","110082155A","110003219A","3");
	    String memId=testaddWZMembers();
		wZMember("22474",memId,"110000034F","110082155A","3");
		
		createReferral("22474",Common.convertSysdatecustom(-90),Common.convertSysdatecustom(-89));
		driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:SAVEALL")).click();
		warnings();
		Common.saveAll();
		String refNum=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
		System.out.println("Ref num from tc#22474: "+refNum);
		log("Referral num: "+refNum);
		createReferral("22474",Common.convertSysdatecustom(-90),Common.convertSysdatecustom(-89));
		driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:SAVEALL")).click();
		warnings();
		
		List <WebElement> warningList= driver.findElements(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]//tr//td[2]"));
		boolean warningExist=false;
		for (WebElement warning : warningList) {
			if(warning.getText().trim().contains("Duplicate")) { 
				warningExist=true;
				break;
			}
		}
		
		Assert.assertTrue(warningExist,"Checking for duplicate referral is not successfull");

		//Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td[2]")).getText().trim().contains("Duplicate"),"Checking for duplicate referral is not successfull");
		Common.cancelAll();
	    }//need to pass the same data to the function for duplicate
	    

	    @Test
	    //Checking for retro referral
	    public static void test22477() throws Exception{
		TestNGCustom.TCNo="22477";
		log("//TC 22477");
		int i;
		String memId=testaddWZMembers();
		wZMember("22477",memId,"110000034F","110082155A","3");
		createReferral("22477",Common.convertSysdatecustom(-100),Common.convertSysdatecustom(-100));
		Common.SaveWarnings();
        driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:SAVEALL")).click();
        int warMessgs=driver.findElements(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr")).size();
		for(i=1;i<=warMessgs;i++){
			 if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr["+(i+1)+"]/td[2]")).getText().trim().contains("Effective Date prior"))
			  {
			  break;	 
			  }
		 }
		Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr["+(i+1)+"]/td[2]")).getText().trim().contains("Effective Date prior"),"Checking for retro referral is not successfull");
		log("Validating for retro referral is done");
        }
	    
	   
	    @Test
	    //Checking for more than 12 months span referral
	    public static void test23413a() throws Exception{
		TestNGCustom.TCNo="23413a";
		log("//TC 23413a");
		int i;
		String memId=testaddWZMembers();
		wZMember("23413a",memId,"110000034F","110082155A","3");
		createReferral("23413a",Common.convertSysdatecustom(-90),Common.convertSysdatecustom(365));
		driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:SAVEALL")).click();
		int warMessgs=driver.findElements(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr")).size();
		System.out.println("No of warning messages: "+warMessgs);
		for(i=1;i<=warMessgs;i++){
		 if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr["+(i+1)+"]/td[2]")).getText().trim().contains("Warning! Referral spans more than 12 months!"))
		    {
			break;		 
		    }
		  }
		Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/table[2]/tbody/tr/td/table/tbody/tr[3]/td/table/tbody/tr[2]/td/table/tbody/tr[2]/td/table/tbody/tr["+(i+1)+"]/td[2]")).getText().trim().contains("12 months"),"Checking for a referral more than a year is not successfull");
		log("Validating for more than 12 months warning is done");
	    Common.cancelAll();
		}
	   
	  
	  
	   
	  
	    public static void changeEdit(String status) throws Exception{
		TestNGCustom.TCNo="changeEditToPaid";
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Reference")).click();
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ErrorDisposition")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ErrorDispSearchBean_CriteriaPanel:ErrorDispSearch_escCode")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ErrorDispSearchBean_CriteriaPanel:ErrorDispSearch_escCode")).sendKeys("5928");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ErrorDispSearchBean_CriteriaPanel:SEARCH")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ErrorDispSearchResults_0:ErrorDispSearch_currentEscCode")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ErrorDispNavigatorPanel:ErrorDispNavigator:ITM_n101")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ErrorDispCritPanel:ErrorDispLineList_0:ErrorDispLineBean_ColValue_status")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ErrorDispCritPanel:RegPanel:RegionDispList_0:RegionDispBean_ColValue_status")).click();
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ErrorDispCritPanel:RegPanel:RegionDispDataPanel_CdeClmStatus"))).selectByVisibleText("P - PAID");
	    Common.saveAll();
	    Common.cancelAll();
	    }
	   
	  
	   @Test
		 //Checking compatibility between base and portal(Referral)
		 public static void test23411() throws Exception{
		 
	    	TestNGCustom.TCNo="23411";
	    	log("//TC 23411");
	    	String memId=testaddWZMembers();
		 
	        wZMember("23411",memId,"110000034F","110082155A","3");
		 
	    	String mcProgram ="PCCB";
	    	String providerId="110028118A";
	    	String startReason="62";
	    	String referringProvId="110028118A";
	    	String visitsBeforeUpdate="3";
	    	String visitsChangedTo="5";
		 
	    	String provider=providerId.substring(0, providerId.length()-1),
			       svcloc=providerId.substring(providerId.length()-1);
	    	System.out.println("Provider: "+provider);
	        System.out.println("Service Loc: "+svcloc);
		   
	    	driver.findElement(By.id("MMISForm:MMISHeader:header_value_home")).click();
	    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
	    	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
	    	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memId);
		 	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
		 	driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
		 	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_RePmpAssignSu")).click();
		 
		 	driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_NewButtonClay:RePmpAssignSuList_newAction_btn")).click();
		 	new Select(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_McProgramList']"))).selectByValue(mcProgram);
		 
		 	driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_RePmpHisDistanceSearchHis_CMD_SEARCH']/img")).click();
		 	new Select(driver.findElement(By.xpath("//select[contains(@id,'DistanceArrayList')]"))).selectByValue("50");  
		 	//new Select(driver.findElement(By.xpath("//select[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id393:RePmpDistanceSearchCriteriaPanel:AcoBList']"))).selectByValue("117145B");  //04
		 	new Select(driver.findElement(By.xpath("//select[contains(@id, 'MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id39') and contains(@id, 'RePmpDistanceSearchCriteriaPanel:AcoBList')]"))).selectByValue("117145B");  //04

		 	driver.findElement(By.xpath("//*[contains(@id,'RePmpDistanceSearchCriteriaPanel:SEARCH')]")).click();
	     
		 		//System.out.println(!(driver.findElements(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id393:RePmpDistanceSearchResults:tbody_element']//tr/td[3]/a/span[text()='110028118']//..//..//../td[4]/span[text()='A']//..//../td[1]/a")).size() > 0)); 
		 		System.out.println(!(driver.findElements(By.xpath("//tbody[contains(@id, 'MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id39') and contains(@id, 'RePmpDistanceSearchResults:tbody_element')]//tr/td[3]/a/span[text()='110028118']//..//..//../td[4]/span[text()='A']//..//../td[1]/a")).size() > 0)); 

		 		//while(!(driver.findElements(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id393:RePmpDistanceSearchResults:tbody_element']//tr/td[3]/a/span[text()='110028118']//..//..//../td[4]/span[text()='A']//..//../td[1]/a")).size() > 0)) {
			 	while(!(driver.findElements(By.xpath("//tbody[contains(@id, 'MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id39') and contains(@id, 'RePmpDistanceSearchResults:tbody_element')]//tr/td[3]/a/span[text()='110028118']//..//..//../td[4]/span[text()='A']//..//../td[1]/a")).size() > 0)) {

		 			//driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id393:RePmpDistanceSearchPaginatornext")).click();
		 			driver.findElement(By.xpath("//*[contains(@id, 'MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id39') and contains(@id, 'RePmpDistanceSearchPaginatornext')]")).click();

			 	}
	     
		 	//System.out.println(driver.findElements(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id393:RePmpDistanceSearchResults:tbody_element']//tr/td[3]/a/span[text()='110028118']//..//..//../td[4]/span[text()='A']//..//../td[1]/a")).size() < 0); 
		 	System.out.println(driver.findElements(By.xpath("//tbody[contains(@id, 'MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id39') and contains(@id, 'RePmpDistanceSearchResults:tbody_element')]//tr/td[3]/a/span[text()='110028118']//..//..//../td[4]/span[text()='A']//..//../td[1]/a")).size() < 0); 


		 	//driver.findElement(By.xpath("//tbody[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id393:RePmpDistanceSearchResults:tbody_element']//tr/td[3]/a/span[text()='110028118']//..//..//../td[4]/span[text()='A']//..//../td[1]/a")).click();
		 	driver.findElement(By.xpath("//tbody[contains(@id, 'MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id39') and contains(@id, 'RePmpDistanceSearchResults:tbody_element')]//tr/td[3]/a/span[text()='110028118']//..//..//../td[4]/span[text()='A']//..//../td[1]/a")).click();
		 	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_StartReasonDropDownEntries"))).selectByValue(startReason);

		 	driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_EffectiveDate']")).clear();
		 	driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_EffectiveDate']")).sendKeys(Common.getFirstDayOfMonth(2024, 5));
		 	Common.SaveWarnings();
		 
		 	Common.portalLogin();
	     
		 	driver.findElement(By.linkText("Manage Service Authorizations")).click();
		 	driver.findElement(By.linkText("Referrals")).click();
		 	driver.findElement(By.linkText("Enter New Referral")).click();
		 
		 	driver.findElement(By.id("referralAddForm:referralAddPanel:member_memberID")).clear();
		 	driver.findElement(By.id("referralAddForm:referralAddPanel:member_memberID")).sendKeys(memId);//WZ member

		 	new Select(driver.findElement(By.id("referralAddForm:referralAddPanel:referringProvider"))).selectByValue(referringProvId);
		 
		 	driver.findElement(By.id("referralAddForm:referralAddPanel:referralIndividualRefProvSearchlookup")).click();
		 	driver.findElement(By.xpath("//input[@id='submitProviderSearch:j_id_id2pc3:providerLookupSearchSubView:j_id_id5pc3:npi']")).clear();
		 	driver.findElement(By.xpath("//input[@id='submitProviderSearch:j_id_id2pc3:providerLookupSearchSubView:j_id_id5pc3:npi']")).sendKeys("110091759");
		 	driver.findElement(By.id("submitProviderSearch:j_id_id2pc3:search_Button")).click();
		 	driver.findElement(By.xpath("//a[@id='submitProviderSearch:j_id_id31pc3:ProviderList:0:displayName_link']")).click();

		 	driver.findElement(By.id("referralAddForm:referralAddPanel:referralProvSearchlookup")).click();
		 	driver.findElement(By.xpath("//input[@id='submitProviderSearch:j_id_id2pc3:providerLookupSearchSubView:j_id_id5pc3:npi']")).clear();
		 	driver.findElement(By.xpath("//input[@id='submitProviderSearch:j_id_id2pc3:providerLookupSearchSubView:j_id_id5pc3:npi']")).sendKeys("110027813");
		 	driver.findElement(By.id("submitProviderSearch:j_id_id2pc3:search_Button")).click();
		 	driver.findElement(By.xpath("//tbody[@id='submitProviderSearch:j_id_id31pc3:ProviderList:tbody_element']/tr/td[2]/span[contains(text(), '45 DIMOCK ST') and contains(text(), 'ROXBURY')]//..//../td[1]/a/span[contains(text(), 'DIMOCK COMMUNITY HEALTH CENTER')]")).click();;

		 	new Select(driver.findElement(By.id("referralAddForm:referralAddPanel:assignment"))).selectByVisibleText("CONSULT, TEST AND TREAT");
		 	driver.findElement(By.id("referralAddForm:referralAddPanel:referralLineItem_endDate")).clear();
		 	driver.findElement(By.id("referralAddForm:referralAddPanel:referralLineItem_endDate")).sendKeys(Common.convertSysdatecustom(366));
		 	driver.findElement(By.id("referralAddForm:referralAddPanel:referralLineItem_numVisits")).clear();
		 	driver.findElement(By.id("referralAddForm:referralAddPanel:referralLineItem_numVisits")).sendKeys(visitsBeforeUpdate);
		 	driver.findElement(By.id("referralAddForm:j_id_id79pc3:j_id_id81pc3")).click();
		 
		 		if(driver.findElements(By.xpath("//input[@id='referralAddForm:j_id_id1pc3:ignoreWarnings']")).size()>0) {
		 		
		 			driver.findElement(By.xpath("//input[@id='referralAddForm:j_id_id1pc3:ignoreWarnings']")).click();
		 			driver.findElement(By.id("referralAddForm:j_id_id79pc3:j_id_id81pc3")).click();

		 		}
		 	
		 	String refNum=driver.findElement(By.xpath("//*[text()='Referral Authorization #']//../span[3]")).getText();

		 	Common.portalLogout();
		 	
		 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Referrals")).click();
		 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		 	driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:RfSearchResultsDataPanel_ReferralNumber")).clear();
		 	driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:RfSearchResultsDataPanel_ReferralNumber")).sendKeys(refNum);
		 	driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:SEARCH")).click();
		 	driver.findElement(By.id("MMISForm:MMISBodyContent:RfSearchResults_0:RfSearch_link")).click();
		 	driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:ITM_n105")).click();
		 	driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemList_0:RfLineItemBean_ColValue_status")).click();
		 	driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_UntSvcRequirementQuantity']")).clear();
		 	driver.findElement(By.xpath("//input[@id='MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemDataPanel_UntSvcRequirementQuantity']")).sendKeys("5");
		 
		 	Common.SaveWarnings();
		 
		 	Common.portalLogin();
		 	searchReferral_portal(refNum);
		 	String visitsAfterUpdate=driver.findElement(By.xpath("//input[@id='referralViewInformationForm:referralViewInformationDataPanel:referralLineItem_numVisits']")).getAttribute("value");
		 
		 	Assert.assertTrue(visitsAfterUpdate.trim().equals(visitsChangedTo.trim()), "Visits changes don't affect on Portal");
		 
	     }


public static void searchReferral_portal(String refNum){
		   
		     driver.findElement(By.linkText("Manage Service Authorizations")).click();
			 driver.findElement(By.linkText("Referrals")).click();
			 driver.findElement(By.linkText("Inquire Referral")).click();
			 driver.findElement(By.xpath("//input[contains(@id, 'authorizationNumber')]")).sendKeys(refNum);
			 driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click();
			 driver.findElement(By.xpath("//span[contains(@id, '0:referralNum')]")).click();
	  
	   }
	   // following is the old version of test case 
	/*	 public static void test23411() throws Exception{
		 TestNGCustom.TCNo="23411";
		 log("//TC 23411");
		 String memId=testaddWZMembers();
			wZMember("23411",memId,"110000034F","110082155A","3");
		 createReferral("23411",Common.convertSysdatecustom(-90),Common.convertSysdatecustom(-89));
		 //Thread.sleep(15000);
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:SAVEALL")).click();
		 warnings();
		 Common.saveAll();
		 String refNum=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
		 System.out.println("Ref num from tc#23411: "+refNum);
		 log("Referral num: "+refNum);
		 Common.portalLogin();
		 driver.findElement(By.linkText("Manage Service Authorizations")).click();
		 driver.findElement(By.linkText("Referrals")).click();
		 driver.findElement(By.linkText("Inquire Referral")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'authorizationNumber')]")).sendKeys(refNum);
		 driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click();
		 driver.findElement(By.xpath("//span[contains(@id, '0:referralNum')]")).click();
		 driver.findElement(By.xpath("//textarea[contains(@id, 'reason')]")).clear();
		 driver.findElement(By.xpath("//textarea[contains(@id, 'reason')]")).sendKeys("Test");
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'referralLineItem_balanceVisitsText')]")).getText().trim().equals("3"),"The no of visits in Base are not equal to that of in Portal");
		 driver.findElement(By.xpath("//input[contains(@id, 'referralLineItem_numVisits')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'referralLineItem_numVisits')]")).sendKeys("4");
		 driver.findElement(By.xpath("//*[@id='referralViewInformationForm:referralViewInformationDataPanel:j_id_id84pc3:j_id_id86pc3']")).click();
		 driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		 if(driver.findElements(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).size()>0)
		  {
		  driver.findElement(By.xpath("//input[contains(@id, 'ignoreWarnings')]")).click();
		  //Change
		  driver.findElement(By.xpath("//*[@id='referralViewInformationForm:referralViewInformationDataPanel:j_id_id84pc3:j_id_id86pc3']")).click();
		  }
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='submissionStatus:referralAddResponseDataPanel']/tbody/tr[1]/td/span[2]")).getText().trim().contains("successfully"),"The changes made in the portal are not saved successfully in Referrals");
		 Common.portalLogout();
		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Referrals")).click();
		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:RfSearchResultsDataPanel_ReferralNumber")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:RfSearchResultsDataPanel_ReferralNumber")).sendKeys(refNum);
		 driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:SEARCH")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfSearchResults_0:RfSearch_link")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:ITM_n105")).click();
		 Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:RfLineItemPanel:RfLineItemList_0:RfLineItemBean_ColValue_untSvcRequirementQuantity")).getText().trim().contains("4"),"The changes made in the portal are not saved in the base");
         Common.cancelAll();
		 }
	   */
	   
	  public static void submitClaimRef(String tcNo) throws SQLException{
		driver.findElement(By.linkText("Manage Claims and Payments")).click();
		driver.findElement(By.linkText("Enter Single Claim")).click();
		//Doubt
		driver.findElement(By.xpath("//*[contains(@id, 'professionalClaimLink')]")).click();
	    //driver.findElement(By.id("enterclaim:enterSingleClaim_1_id1:enterSingleClaim_1_id4")).click();
	    new Select(driver.findElement(By.xpath("//select[contains(@id, 'billingProviderID')]"))).selectByValue("110048577A");
		driver.findElement(By.xpath("//input[contains(@id, 'memberID')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id, 'memberID')]")).sendKeys(memberId22497);
		if(tcNo.equals("22496")){
		sqlStatement ="Select b.id_medicaid from t_re_pmp_assign a, t_re_base b, t_pmp_svc_loc d where a.sak_pub_hlth = 23 and d.cde_service_loc='A' " +
				      "and a.sak_recip = b.sak_recip and d.sak_prov =47258 and a.sak_pmp_ser_loc=d.sak_pmp_ser_loc and a.dte_end = 22991231 " +
				      "and a.cde_status1 <> 'H' and b.ind_active = 'Y' and b.sak_recip > dbms_random.value * 6300000 and rownum < 2";
	     colNames.add("id_medicaid");
	     colValues=Common.executeQuery(sqlStatement, colNames );
	     driver.findElement(By.xpath("//input[contains(@id, 'memberID')]")).sendKeys(colValues.get(0));
		 }
		else if(tcNo.equals("22497a")){
		//wZMember();
		memberId22496=colValues.get(0);
		driver.findElement(By.xpath("//input[contains(@id, 'memberID')]")).sendKeys(memberId22496);
		 }
		driver.findElement(By.xpath("//input[contains(@id, 'patientAccountNumber')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id, 'patientAccountNumber')]")).sendKeys("195134");
		driver.findElement(By.xpath("//input[contains(@id, 'lastName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id, 'lastName')]")).sendKeys("Roy");
		driver.findElement(By.xpath("//input[contains(@id, 'firstName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id, 'firstName')]")).sendKeys("Franklin");
		driver.findElement(By.xpath("//input[contains(@id, 'birthDate')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id, 'birthDate')]")).sendKeys("03/20/1984");
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'gender')]"))).selectByVisibleText("F - Female");
		driver.findElement(By.xpath("//input[contains(@id, 'memberAdrLine1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id, 'memberAdrLine1')]")).sendKeys("100 Hancock Street");
		driver.findElement(By.xpath("//input[contains(@id, 'memberCity')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id, 'memberCity')]")).sendKeys("Boston");
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'memberState')]"))).selectByVisibleText("MA - Massachusetts");
		driver.findElement(By.xpath("//input[contains(@id, 'memberZip')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id, 'memberZip')]")).sendKeys("02110");
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'releaseOfInfo')]"))).selectByVisibleText("Y - Yes, Provider has a Signed Statement Permitting Release of Medical Billing Data Related to a Claim");
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'placeOfService')]"))).selectByVisibleText("11 - OFFICE");
		if(tcNo.equals("22497")||tcNo.equals("22497a")){
			System.out.println("Ref num in claim: "+refNum22497);
		driver.findElement(By.xpath("//input[contains(@id, 'referralNumber')]")).sendKeys(refNum22497);
		}
        new Select(driver.findElement(By.xpath("//select[contains(@id, 'assignmentOfBenefits')]"))).selectByVisibleText("No");
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'signatureOnFile')]"))).selectByVisibleText("Yes");
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'providerAcceptsAssignment')]"))).selectByVisibleText("A - Assigned");
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'claimFilingIndicator')]"))).selectByVisibleText("MC - MEDICAID");
		driver.findElement(By.xpath("//*[@id='professionalBillingTab:j_id_id123pc3:diagnosisCode1']")).clear();
		driver.findElement(By.xpath("//*[@id='professionalBillingTab:j_id_id123pc3:diagnosisCode1']")).sendKeys("J206");//7807
		driver.findElement(By.xpath("//input[contains(@id, 'totalCharges')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id, 'totalCharges')]")).sendKeys("60.00");
		driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_Procedures')]")).click();
		Common.getPageError("//form[contains(@id,'Tab')]/div/table[2]/tbody/tr");
		driver.findElement(By.xpath("//input[contains(@id, 'new_Button')]")).click();
		driver.findElement(By.xpath("//input[contains(@id, 'hcpcsProcedureCode')]")).clear();
		if(tcNo.equals("22497a"))
		{
		driver.findElement(By.xpath("//input[contains(@id, 'hcpcsProcedureCode")).sendKeys("93005");
		}
		else 
		{    //general
		driver.findElement(By.xpath("//input[contains(@id, 'hcpcsProcedureCode')]")).sendKeys("99213");//92502
		}
		if(tcNo.equals("22497")||tcNo.equals("22497a"))
		{
		driver.findElement(By.xpath("//input[contains(@id, 'fromDate')]")).sendKeys(Common.convertSysdatecustom(-2));
		driver.findElement(By.xpath("//input[contains(@id, 'toDate')]")).sendKeys(Common.convertSysdatecustom(-2));
		}
		else if(tcNo.equals("22498"))
		{
		driver.findElement(By.xpath("//input[contains(@id, 'fromDate')]")).sendKeys(Common.convertSysdatecustom(-1));
		driver.findElement(By.xpath("//input[contains(@id, 'toDate')]")).sendKeys(Common.convertSysdatecustom(-1));
		}
		driver.findElement(By.xpath("//input[contains(@id, 'diagnosisCrossReference1')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id, 'diagnosisCrossReference1')]")).sendKeys("01");
		driver.findElement(By.xpath("//input[contains(@id, 'charges')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id, 'charges')]")).sendKeys("60.00");
		driver.findElement(By.xpath("//input[contains(@id, 'units')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id, 'units')]")).sendKeys("1");
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'unitsOfMeasurment')]"))).selectByVisibleText("UN - Unit");
		new Select(driver.findElement(By.xpath("//select[contains(@id, 'epsdt')]"))).selectByVisibleText("No");
		driver.findElement(By.xpath("//input[contains(@id, 'add_Button')]")).click();
		driver.findElement(By.xpath("//a[contains(@id, '_MENUITEM_Confirmation')]")).click();
		Common.getPageError("//form[contains(@id,'Tab')]/div/table[2]/tbody/tr");
		driver.findElement(By.xpath("//input[contains(@id, 'btnSubmit')]")).click();
		Common.getPageError("//form[contains(@id,'Tab')]/div/table[2]/tbody/tr");
		}
		
	   
	     @Test
		 //Referrals and covering providers-requesting(referring provider is a covering)(Referrals)
	     //Need to check if ckerns is added as covering provider for william zucker(provider-->110047258A-->Covering Practitioners and Locum Tenums-->add 110000312A-->saveall)
		 public static void test22481() throws Exception{
		 TestNGCustom.TCNo="22481";
		 log("//TC 22481");
		 String memId=testaddWZMembers();
			wZMember("22481",memId,"110000034F","110065850A","3");
      //   wZMember("22481","110082151H","110065850A","3");
	     Common.portalLogin();
		 String sqlstatement="select MEMBERID from R_REFERRALS where tcno='22481'";
		 colNames.add("MEMBERID");
		 colValues=Common.executeQuery1(sqlstatement,colNames);
		 memberId=colValues.get(0);
		 String sql="select * from T_RE_BASE where ID_MEDICAID='"+memberId+"'";
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("22481", "M", "110065850A", "60", " ", "1821017195", " ", " ", sql,"0","0");
		 clmNo = advSubmitClaims.clmICN("prof");
		 status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		 System.out.println("Claim ICN with edit 3120: from TC#22481 "+clmNo);
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim().equals("Paid"),"The claim submitted is not denied");
		 checkEdit("3120");
		 
		 Common.portalLogout();
		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Referrals")).click();
		 createReferral("22481",Common.convertSysdate(),Common.convertSysdatecustom(-1));
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:SAVEALL")).click();
		 warnings();
		 Common.saveAll();
		 refNum22481=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
		 System.out.println("Ref num from tc#22481: "+refNum22481);
		 log("Referral num: "+refNum22481);
		 Common.cancelAll();
		 Common.portalLogin();
		 driver.findElement(By.linkText("Manage Claims and Payments")).click();
		 driver.findElement(By.linkText("Inquire Claim Status")).click();
		 new Select(driver.findElement(By.xpath("//select[contains(@id, 'billingProviderID')]"))).selectByValue("110065850A");//110022129F
		 driver.findElement(By.xpath("//input[contains(@id, 'icn')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'icn')]")).sendKeys(clmNo);
		 driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click();
		 driver.findElement(By.xpath("//span[contains(@id, '0:icn')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'btnResubmit')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'referralNumber')]")).sendKeys(refNum22481);
         driver.findElement(By.xpath("//a[contains(@id, ':_MENUITEM_Confirmation')]")).click();
         driver.findElement(By.xpath("//input[contains(@id, 'btnSubmit')]")).click();
         String icn22481=driver.findElement(By.xpath("//span[contains(@id, 'icnText')]")).getText().trim();
         System.out.println("ICN from TC#22481: "+icn22481);
		 Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim().equals("Paid"),"The claim submitted is not paid");
		 log("ICN with PAID status: "+icn22481);
		 Common.portalLogout();
      	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Referrals")).click();
		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:RfSearchResultsDataPanel_ReferralNumber")).sendKeys(refNum22481);
		 driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:SEARCH")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfSearchResults_0:RfSearch_link")).click();
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[14]/td[2]")).getText().trim().contains("2"),"The balance visits is  not equal to 2");
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:ITM_n102")).click();
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[2]/table/thead/tr/th/table/tbody/tr/td[1]/h2")).getText().trim().equals("Claim List"),"The claim list panel is not opened");
		 Common.cancelAll();
		 
		 
		/* driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Claims")).click();
		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchBean_CriteriaPanel:ClmIcnNumber")).clear();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchBean_CriteriaPanel:ClmIcnNumber")).sendKeys(icn22481);
		 driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchBean_CriteriaPanel:SEARCH")).click();
		 driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchResultsDataTable_0:_id14")).click();
		 driver.findElement(By.xpath("(//img[@alt='Detail Link'])[2]")).click();
		 winHandleCurrent=driver.getWindowHandle();
		 for(String winHandleNew:driver.getWindowHandles())
		 driver.switchTo().window(winHandleNew);
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[11]/td[2]")).getText().trim().contains("2"),"The balance visits is  not equal to 2");
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:ITM_n102")).click();
		 Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/div[2]/table/thead/tr/th/table/tbody/tr/td[1]/h2")).getText().trim().equals("Claim List"),"The claim list panel is not opened");
		 driver.close();
		 Common.switchToMainWin();*/
		 }
	   
	   
	   
	    @Test
	    //Referrals and covering providers-requesting Provider(service/billing provider is a covering for the memberï¿½s PCC) 
	    //Need to check the dates of covering provider(110000312A) and submit the claim within that dates range
	    public static void test22483() throws Exception{
		TestNGCustom.TCNo="22483";
		log("//TC 22483");
		 String memId=testaddWZMembers();
			wZMember("22483",memId,"110000034F","110065850A","3");
		//wZMember("22483","110000312A","110065850A","3");
		String sqlstatement="select MEMBERID from R_REFERRALS where tcno='22483'";
		colNames.add("MEMBERID");
		colValues=Common.executeQuery1(sqlstatement,colNames);
		memberId=colValues.get(0);
		Common.portalLogin();
		String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		advSubmitClaims.initiateClaim();
		advSubmitClaims.M("22483", "M","110082159F", "60.00", "", "1821017195", "", "",memberSql,"0","0");
		clmNo = advSubmitClaims.clmICN("prof");
		status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		System.out.println("ICN and status from TC#22483: "+clmNo+ ","+status);
		log("ICN: "+clmNo);
		Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim().equals("Paid"),"The claim submitted is not paid for covering provider id");
		Common.portalLogout();
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Claims")).click();
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchBean_CriteriaPanel:ClmIcnNumber")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchBean_CriteriaPanel:ClmIcnNumber")).sendKeys(clmNo);
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchBean_CriteriaPanel:SEARCH")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:ClaimSearchResultsDataTable_0:_id14")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:PhysicianClaimNavigatorPanel:PhysicianClaimNavigator:ITM_PhysicianClaim5")).click();
	    Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:PhysicianClaimDetailPanel:_id123_0:PhysicianDetailBean_ColValue_physicianDetailKey_numReferralRefSysFindInd")).getText().trim().contains("No"),"The referral system is not empty");
	    }
	   
	   
	   @Test
	   //Verify auto look-up for the referral(Referrals)
	   public static void test22496() throws Exception{
	   TestNGCustom.TCNo="22496";
	   log("//TC 22496");
	   String sql="Select distinct b.id_medicaid,d.sak_prov,d.cde_service_loc from t_re_pmp_assign a, t_re_base b, t_pmp_svc_loc d where a.sak_pub_hlth = 23 " +
	 		      "and d.cde_service_loc='A' and a.sak_recip = b.sak_recip and a.sak_pmp_ser_loc=d.sak_pmp_ser_loc and a.dte_end = 22991231 and a.cde_status1 <> 'H' " +
		          "and b.ind_active = 'Y' and b.id_medicaid<>'100049839713' and b.sak_recip > dbms_random.value * 6300000 and rownum < 2";
	   pccMemProv(sql,"22496","110048577A","1");
	   Common.portalLogin();
	   String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
	   advSubmitClaims.initiateClaim();
	   advSubmitClaims.M("22496", "M","110048577A", "60.00", "", "1134135759", "", "",memberSql,"0","0");
	   clmNo = advSubmitClaims.clmICN("prof");
	   checkEdit("3120");
	   status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
	   System.out.println("ICN and status from TC#22496 with edit 3120: "+clmNo+ ","+status);
	   log("ICN with 3120 edit: "+clmNo);
	   Common.portalLogout();
	   driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Referrals")).click();
	   createReferral("22496",Common.convertSysdatecustom(-5),Common.convertSysdatecustom(1));//need to create a referrals with same dos as claim or need to submit a claim with system date
	   driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:SAVEALL")).click();
	   warnings();
	   Common.saveAll();
	   String refNum22496=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
	   System.out.println("Ref num from tc#22496: "+refNum22496);
	   log("Referral num: "+refNum22496);
	   Common.portalLogin();
	   driver.findElement(By.linkText("Manage Claims and Payments")).click();
	   driver.findElement(By.linkText("Inquire Claim Status")).click();
	   new Select(driver.findElement(By.xpath("//select[contains(@id, 'billingProviderID')]"))).selectByValue("110048577A");
	   driver.findElement(By.xpath("//input[contains(@id, 'icn')]")).sendKeys(clmNo);
	   driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click();
	   driver.findElement(By.xpath("//span[contains(@id, '0:icn')]")).click();
	   driver.findElement(By.xpath("//input[contains(@id, 'btnResubmit')]")).click();

	   driver.findElement(By.xpath("//input[contains(@id, 'referralNumber')]")).sendKeys(refNum22496);
       driver.findElement(By.xpath("//a[contains(@id, ':_MENUITEM_Confirmation')]")).click();
       driver.findElement(By.xpath("//input[contains(@id, 'btnSubmit')]")).click();
       Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim().equals("Paid"),"The resubmitted claim is not Paid");
	   //TC#31231 No available units-edit 3122
	   log("//TC 31231");
	   advSubmitClaims.initiateClaim();
	   advSubmitClaims.M("31231", "M","110048577A", "60.00", "", "1760571087", refNum22496, "",memberSql,"-4","-4");
	   clmNo = advSubmitClaims.clmICN("prof");
	   status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
	   System.out.println("ICN and status from TC#31231: "+clmNo+ ","+status);
	   log("ICN with edit 3122: "+clmNo);
	   //Common.portalLogout();
	   }
	   
	   
	   @Test
	    //Referrals and covering providers-requesting Provider(service/billing provider is a covering for the memberï¿½s PCC) 
	    public static void test22497() throws Exception{
		TestNGCustom.TCNo="22497";
		int i;
		log("//TC 22497");
		 String memId=testaddWZMembers();
			wZMember("22497",memId,"110000034F","110048577A","3");
		//wZMember("22497","110082151H","110048577A","3");
		createReferral("22497",Common.convertSysdatecustom(-6),Common.convertSysdatecustom(1));
		driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:SAVEALL")).click();
		warnings();
		Common.saveAll();
		refNum22497=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
	    System.out.println("Ref num from tc#22497: "+refNum22497);
	    log("Referral num: "+refNum22497);
	    Common.cancelAll();
	    Common.portalLogin();
	    String sqlstatement="select MEMBERID from R_REFERRALS where tcno='22497'";
		colNames.add("MEMBERID");
		colValues=Common.executeQuery1(sqlstatement,colNames);
		memberId=colValues.get(0);
		Common.portalLogin();
		String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		advSubmitClaims.initiateClaim();
		advSubmitClaims.M("22497", "M","110048577A", "60.00", "", "1134135759", refNum22497, "",memberSql,"-6","-6"); //need to run and check
		clmNo = advSubmitClaims.clmICN("prof");
		status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		System.out.println("ICN and status from TC#22497: "+clmNo+ ","+status);
		log("ICN from TC#22497: "+clmNo);
		Assert.assertTrue(status.equals("Paid"),"The claim submitted is not paid for covering provider id");
		driver.findElement(By.xpath("//input[contains(@id, 'btnCancelClaims')]")).click();
		//TC#22498
		advSubmitClaims.initiateClaim();
		advSubmitClaims.M("22498", "M","110048577A", "60.00", "", "1134135759", refNum22497, "",memberSql,"-5","-5");
		clmNo = advSubmitClaims.clmICN("prof");
		status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		System.out.println("ICN and status from TC#22498: "+clmNo+ ","+status);
		Assert.assertTrue(status.equals("Paid"),"The 2nd(tc#22498) claim submitted is not Paid");
		log("ICN2(TC#22498):"+clmNo);
		driver.findElement(By.xpath("//input[contains(@id, 'btnCancelClaims')]")).click();
		//TC#22497a
		advSubmitClaims.initiateClaim();
		advSubmitClaims.M("22497a", "M","110048577A", "60.00", "", "1134135759", refNum22497, "",memberSql,"-6","-6");
		clmNo = advSubmitClaims.clmICN("prof");
		status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		System.out.println("ICN and status from TC#22497a: "+clmNo+ ","+status);
		Assert.assertTrue(status.equals("Paid"),"The claim submitted is not Paid");
		log("ICN with Procedure code(90471):"+clmNo);
	    Common.portalLogout();
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Referrals")).click();
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:RfSearchResultsDataPanel_ReferralNumber")).sendKeys(refNum22497);
	    driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:SEARCH")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:RfSearchResults_0:RfSearch_link")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:ITM_n102")).click();
	    int rownum=driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:ClaimPriorAuthPanel:ClaimList1:tbody_element']/tr")).size();
	    for(i=1;i<=rownum;i++)
        {
        if((driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ClaimPriorAuthPanel:ClaimList1:tbody_element']/tr["+i+"]/td[2]")).getText().equals(clmNo))&&(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ClaimPriorAuthPanel:ClaimList1:tbody_element']/tr["+i+"]/td[5]")).getText().trim().equals("0")))
        break;
        }
	    log("Units used in the referral is 0 in the claim list");
        }
	   
	   
	   
	   public static void checkEdit(String edit){
		   int i;
	   int rownum=driver.findElements(By.xpath("//*[@id='profClaimSubmissionSubView:submissionStatus:EOBList:tbody_element']/tr")).size();
	   for(i=1;i<=rownum;i++){
		   if(driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:EOBList:"+(i-1)+":eobCode")).getText().trim().equals(edit))
		   break;
		   }
	    if(i==(rownum+1))
          {
       	  throw new SkipException("Edit:"+edit+"is not found");
          } 
	   }
	   
	   
	     @Test
		 //Miss match rendering provider-edit 3124
		 public static void test31228() throws Exception{
		 TestNGCustom.TCNo="31228";
		 log("//TC 31228");
		 String sql="Select distinct b.id_medicaid,d.sak_prov,d.cde_service_loc from t_re_pmp_assign a, t_re_base b, t_pmp_svc_loc d where a.sak_pub_hlth = 23 " +
		 		    "and d.cde_service_loc='A' and b.cde_sex='F' and a.sak_recip = b.sak_recip and a.sak_pmp_ser_loc=d.sak_pmp_ser_loc and a.dte_end = 22991231 and a.cde_status1 <> 'H' " +
	 		        "and b.ind_active = 'Y' and b.sak_recip > dbms_random.value * 6300000 and rownum < 2";
		 pccMemProv(sql,"31228", "110000254A", "3");
		 createReferral("31228",Common.convertSysdatecustom(-90),Common.convertSysdatecustom(-89));
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:SAVEALL")).click();
		 warnings();
		 Common.saveAll();
		 String refNum31228=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
		 System.out.println("Ref num from tc#31228: "+refNum31228);
		 log("Referral num: "+refNum31228); 
		 Common.cancelAll();
		 Common.portalLogin();
		 String sqlstatement="select MEMBERID from R_REFERRALS where tcno='31228'";
		 colNames.add("MEMBERID");
		 colValues=Common.executeQuery1(sqlstatement,colNames);
		 memberId=colValues.get(0);
		 Common.portalLogin();
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("31228", "M","110000254A", "100.00", "", "1821017195", "", "",memberSql,"-90","-90");
		 clmNo = advSubmitClaims.clmICN("prof");
		 status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		 System.out.println("ICN and status from TC#31228: "+clmNo+ ","+status);
		 log("ICN with 3120 edit: "+clmNo);
		 checkEdit("3120");
		 driver.findElement(By.xpath("//input[contains(@id, 'btnReplace')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'referralNumber')]")).sendKeys(refNum31228);
		 driver.findElement(By.xpath("//a[contains(@id, ':_MENUITEM_Confirmation')]")).click();
	     driver.findElement(By.xpath("//input[contains(@id, 'btnSubmit')]")).click();
	     clmNo = SubmitClaims.clmICN("prof");
		 status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		 checkEdit("3124");
		 log("ICN with edit 3124: "+clmNo);
		 Common.portalLogout();
		 }
	     
	     
	     @Test
		 //Miss Match member ID-edit 3125
		 public static void test31229() throws Exception{
		 TestNGCustom.TCNo="31229";
		 log("//TC 31229");
		 String memId=testaddWZMembers();
			wZMember("31229",memId,"110000034F","110048577A","3");
		// wZMember("31229","110047258A","110048577A","3");
		 createReferral("31229",Common.convertSysdatecustom(-120),Common.convertSysdatecustom(1));
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:SAVEALL")).click();
		 warnings();
		 Common.saveAll();
		 String refNum31229=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
		 System.out.println("Ref num from tc#31229: "+refNum31229);
		 log("Referral num: "+refNum31229);
		 Common.cancelAll();
		 Common.portalLogin();
         sqlStatement="Select distinct b.id_medicaid, c.id_provider, d.cde_service_loc from t_re_pmp_assign a, t_re_base b, t_pr_prov c, t_pmp_svc_loc d " +
	 		          "where a.sak_pub_hlth = 23 and b.cde_sex='F' and a.sak_recip = b.sak_recip and d.sak_prov = c.sak_prov and a.dte_end = 22991231 " +
	 		          "and not exists (select 1 from t_pa_pauth where sak_recip=b.sak_recip) and a.cde_status1 <> 'H' and b.ind_active = 'Y' and b.sak_recip > dbms_random.value * 6300000 and rownum < 2";
         colNames.add("ID_MEDICAID");
         colValues=Common.executeQuery(sqlStatement, colNames);
         memberId=colValues.get(0);
         Common.portalLogin();
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("31229", "M","110048577A", "100.00", "", "1821017195", "", "",memberSql,"-120","-120");
		 clmNo = advSubmitClaims.clmICN("prof");
		 status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		 System.out.println("ICN and status from TC#31229: "+clmNo+ ","+status);
		 Referrals.checkEdit("3120");
		 log("ICN with edit 3120: "+clmNo);
		 log("Used Proc code:76801, Modifier:TC for detail-2, ICD-9 codes");
		 driver.findElement(By.xpath("//input[contains(@id, 'btnReplace')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'referralNumber')]")).sendKeys(refNum31229);
		 driver.findElement(By.xpath("//a[contains(@id, ':_MENUITEM_Confirmation')]")).click();
	     driver.findElement(By.xpath("//input[contains(@id, 'btnSubmit')]")).click();
	   	 clmNo = advSubmitClaims.clmICN("prof");
		 status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		 Referrals.checkEdit("3125");
		 log("ICN with edit 3125: "+clmNo);
		 }
	     
	     
	     
	     @Test
		 //Outside service date-edit 3126
		 public static void test31230() throws Exception{
		 TestNGCustom.TCNo="31230";
		 log("//TC 31230");
		 String sql="Select distinct b.id_medicaid,d.sak_prov,d.cde_service_loc from t_re_pmp_assign a, t_re_base b, t_pmp_svc_loc d where a.sak_pub_hlth = 23 " +
	 		        "and d.cde_service_loc='A' and a.sak_recip = b.sak_recip and a.sak_pmp_ser_loc=d.sak_pmp_ser_loc and a.dte_end = 22991231 and a.cde_status1 <> 'H' " +
	 		        "and b.ind_active = 'Y' and b.sak_recip > dbms_random.value * 6300000 and rownum < 2";
		 pccMemProv(sql,"31230", "110048577A" ,"1");
		 Common.portalLogin();
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("31230", "M","110048577A", "50.00", "", "1821017195", "", "",memberSql,"-120","-120");
		 clmNo = advSubmitClaims.clmICN("prof");
		 status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		 System.out.println("ICN and status from TC#31230: "+clmNo+ ","+status);
		 Referrals.checkEdit("3120");
		 log("ICN with edit 3120: "+clmNo);
		 Common.portalLogout();
		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Referrals")).click();
		 createReferral("31230",Common.convertSysdate(),Common.convertSysdatecustom(1));
		 driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:SAVEALL")).click();
		 warnings();
		 //Common.saveAll();
		 Common.SaveWarnings();
		 String refNum31230=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
		 System.out.println("Ref num from tc#31230: "+refNum31230);
		 log("Referral num: "+refNum31230);
		 Common.cancelAll();
		 Common.portalLogin();
		 driver.findElement(By.linkText("Home")).click();
		 driver.findElement(By.linkText("Manage Claims and Payments")).click();
		 driver.findElement(By.linkText("Inquire Claim Status")).click();
		 new Select(driver.findElement(By.xpath("//select[contains(@id, 'billingProviderID')]"))).selectByValue("110048577A");
		 driver.findElement(By.xpath("//input[contains(@id, 'icn')]")).clear();
		 driver.findElement(By.xpath("//input[contains(@id, 'icn')]")).sendKeys(clmNo);
		 driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click();
		 driver.findElement(By.xpath("//span[contains(@id, '0:icn')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'btnResubmit')]")).click();
		 driver.findElement(By.xpath("//input[contains(@id, 'referralNumber')]")).sendKeys(refNum31230);
	     driver.findElement(By.xpath("//a[contains(@id, ':_MENUITEM_Confirmation')]")).click();
	     driver.findElement(By.xpath("//input[contains(@id, 'btnSubmit')]")).click();
	     String icn=driver.findElement(By.xpath("//span[contains(@id, 'icnText')]")).getText().trim();
	     log("ICN: "+icn);
		 status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		 Referrals.checkEdit("3126");
		 log("ICN with edit 3126: "+icn);
	     }
		 
	  
		 @Test
		 //Referral claim-Adjustment
		 public static void test31239() throws Exception{
		 TestNGCustom.TCNo="31239";
		 log("//TC 31239");
		// Referrals_Portal.randomMemberid();
		// memberId=colValues.get(0);
		// System.out.println("memberid: "+colValues.get(0));
		 String sql="Select distinct b.id_medicaid,d.sak_prov,d.cde_service_loc from t_re_pmp_assign a, t_re_base b, t_pmp_svc_loc d, T_PA_PAUTH auth where a.sak_pub_hlth = 23 " +
	 		    "and d.cde_service_loc='A' and a.sak_recip = b.sak_recip and a.sak_pmp_ser_loc=d.sak_pmp_ser_loc and a.dte_end = 22991231 and a.cde_status1 <> 'H' " +
	 		    "and b.ind_active = 'Y' and  b.sak_recip=auth.sak_recip and auth.prior_auth_num not like '%R%' and b.id_medicaid<>'100201874474' and b.id_medicaid<> '200003318730' and b.sak_recip > dbms_random.value * 6300000 and rownum < 2";
		 pccMemProv(sql,"31239","110048577A","5");
		 //Common.portalLogin();
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		 Common.portalLogin();
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("31239", "M","110048577A", "50.00", "", "1760571087", "", "",memberSql,"0","0");
		 clmNo = advSubmitClaims.clmICN("prof");
		 log("ICN: "+clmNo);
		 status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		 System.out.println("ICN and status from TC#31239: "+clmNo+ ","+status);
		 Assert.assertTrue(status.contains("Denied"),"Claim is not denied with 3120 edit");
		 Referrals.checkEdit("3120");
		 Common.portalLogout();
		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Referrals")).click();
		 createReferral("31239",Common.convertSysdatecustom(-65),Common.convertSysdatecustom(1));
	     driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:SAVEALL")).click();
		 warnings();
		 Common.saveAll();
	     String refNum31239=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
		   System.out.println("Ref num from tc#31239: "+refNum31239);
		   log("Referral num: "+refNum31239);
		   Common.portalLogin();
		   driver.findElement(By.linkText("Manage Claims and Payments")).click();
		   driver.findElement(By.linkText("Inquire Claim Status")).click();
		   new Select(driver.findElement(By.xpath("//select[contains(@id, 'billingProviderID')]"))).selectByValue("110048577A");
		   driver.findElement(By.xpath("//input[contains(@id, 'icn')]")).sendKeys(clmNo);
		   driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click();
		   driver.findElement(By.xpath("//span[contains(@id, '0:icn')]")).click();
		   driver.findElement(By.xpath("//input[contains(@id, 'btnResubmit')]")).click();
		   
		   driver.findElement(By.xpath("//a[contains(@id, ':_MENUITEM_Confirmation')]")).click();
		   driver.findElement(By.xpath("//input[contains(@id, 'btnSubmit')]")).click();
		   Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim().equals("Paid"),"The resubmitted claim is not Paid");
		   String paidIcn=driver.findElement(By.xpath("//span[contains(@id, 'icnText')]")).getText().trim();
		   log("Paid ICN (Referral is auto-entered by System): "+paidIcn);
		   
		   driver.findElement(By.xpath("//input[contains(@id, 'btnReplace')]")).click();
		   driver.findElement(By.xpath("//a[contains(@id, ':_MENUITEM_Confirmation')]")).click();
		   driver.findElement(By.xpath("//input[contains(@id, 'btnSubmit')]")).click();
		   String replaceIcn=driver.findElement(By.xpath("//span[contains(@id, 'icnText')]")).getText().trim();
		   log("Replaced ICN: "+replaceIcn);
		   Common.portalLogout();
		   driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Referrals")).click();
		   driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		   driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:RfSearchResultsDataPanel_ReferralNumber")).sendKeys(refNum31239);
		   driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:SEARCH")).click();
		   driver.findElement(By.id("MMISForm:MMISBodyContent:RfSearchResults_0:RfSearch_link")).click();
		   Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[14]/td[2]")).getText().trim().equals("4"), "The balanced visits are not 4 after replacing the claim");
	    	 } 
		 
		 
		 
		 @Test
		 //Referral claim-Adjustment
		 public static void test31240() throws Exception{
		 TestNGCustom.TCNo="31240";
		 log("//TC 31240");
		// Referrals_Portal.randomMemberid();
		// memberId=colValues.get(0);
		// System.out.println("memberid: "+colValues.get(0));
		 String sql="Select distinct b.id_medicaid,d.sak_prov,d.cde_service_loc from t_re_pmp_assign a, t_re_base b, t_pmp_svc_loc d where a.sak_pub_hlth = 23 " +
	 		    "and d.cde_service_loc='A' and a.sak_recip = b.sak_recip and a.sak_pmp_ser_loc=d.sak_pmp_ser_loc and a.dte_end = 22991231 and a.cde_status1 <> 'H' " +
	 		    "and b.ind_active = 'Y' and b.id_medicaid<>'100033238179' and b.sak_recip > dbms_random.value * 6300000 and rownum < 2";
		 pccMemProv(sql,"31240","110048577A","5");
		 Common.portalLogin();
		 String memberSql="select * from t_re_base where ID_MEDICAID='"+memberId+"'";
		 Common.portalLogin();
		 advSubmitClaims.initiateClaim();
		 advSubmitClaims.M("31240", "M","110048577A", "50.00", "", "1760571087", "", "",memberSql,"-30","-30");
		 clmNo = advSubmitClaims.clmICN("prof");
		 log("ICN: "+clmNo);
		 status=driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim();
		 System.out.println("ICN and status from TC#31240: "+clmNo+ ","+status);
		 Assert.assertTrue(status.contains("Denied"),"Claim is not denied with 3120 edit");
		 Referrals.checkEdit("3120");
		 Common.portalLogout();
		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Referrals")).click();
		 createReferral("31240",Common.convertSysdatecustom(-65),Common.convertSysdatecustom(1));
	     driver.findElement(By.id("MMISForm:MMISBodyContent:RfNavigatorPanel:RfNavigator:SAVEALL")).click();
		 warnings();
		 Common.saveAll();
	     String refNum31240=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText().trim();
		   System.out.println("Ref num from tc#31240: "+refNum31240);
		   log("Referral num: "+refNum31240);
		   Common.portalLogin();
		   driver.findElement(By.linkText("Manage Claims and Payments")).click();
		   driver.findElement(By.linkText("Inquire Claim Status")).click();
		   new Select(driver.findElement(By.xpath("//select[contains(@id, 'billingProviderID')]"))).selectByValue("110048577A");
		   driver.findElement(By.xpath("//input[contains(@id, 'icn')]")).sendKeys(clmNo);
		   driver.findElement(By.xpath("//input[contains(@id, 'search_Button')]")).click();
		   driver.findElement(By.xpath("//span[contains(@id, '0:icn')]")).click();
		   driver.findElement(By.xpath("//input[contains(@id, 'btnResubmit')]")).click();
		   
		   driver.findElement(By.xpath("//a[contains(@id, ':_MENUITEM_Confirmation')]")).click();
		   driver.findElement(By.xpath("//input[contains(@id, 'btnSubmit')]")).click();
		   Assert.assertTrue(driver.findElement(By.xpath("//span[contains(@id, 'claimStatusText')]")).getText().trim().equals("Paid"),"The resubmitted claim is not Paid");
		   String paidIcn=driver.findElement(By.xpath("//span[contains(@id, 'icnText')]")).getText().trim();
		   log("Paid ICN (Referral is taken by System): "+paidIcn);
		   
		   //driver.findElement(By.id("profClaimSubmissionSubView:submissionStatus:inquireClaimStatus_1_id22:btnReplace")).click();
		   driver.findElement(By.xpath("//input[contains(@id, 'btnVoid')]")).click();
		   driver.findElement(By.xpath("//a[contains(@id, ':_MENUITEM_Confirmation')]")).click();
		   driver.findElement(By.xpath("//input[contains(@id, 'btnSubmit')]")).click();
		   String replaceIcn=driver.findElement(By.xpath("//span[contains(@id, 'icnText')]")).getText().trim();
		   log("Replaced ICN: "+replaceIcn);
		   Common.portalLogout();
		   driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Referrals")).click();
		   driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		   driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:RfSearchResultsDataPanel_ReferralNumber")).sendKeys(refNum31240);
		   driver.findElement(By.id("MMISForm:MMISBodyContent:MainRfSearchBean_CriteriaPanel:SEARCH")).click();
		   driver.findElement(By.id("MMISForm:MMISBodyContent:RfSearchResults_0:RfSearch_link")).click();
		   Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RfInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[11]/td[2]")).getText().trim().equals("4"), "The balanced visits are not 4 after replacing the claim");
	    	 } 
	     
	     
	   }
