// [5-1-2018 AG]  Initial creation

package newMMIS_Subsystems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.SkipException;


public class ACO_common extends Login{
	
	public static String memberId;

public static String enrollment(String sql,String mcProgram,String startReason,String providerId,String effDate) throws SQLException{
	 
	  if(sql.contains("R_Day2")){
	  colNames.add("ID");
	  colValues=Common.executeQuery1(sql,colNames);  
	  }
	  else{
	 colNames.add("ID_MEDICAID");
     colValues=Common.executeQuery(sql,colNames);
	  }
     memberId=colValues.get(0);
   String provider=providerId.substring(0, providerId.length()-1),
	        svcloc=providerId.substring(providerId.length()-1);
   System.out.println("Provider: "+provider);
   System.out.println("Service Loc: "+svcloc);
   driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
   driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memberId);
   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
   driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_RePmpAssignSu")).click();
   driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_NewButtonClay:RePmpAssignSuList_newAction_btn")).click();
   new Select(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_McProgramList']"))).selectByValue(mcProgram);
  
   if(!(effDate.equals(" "))){
	  driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_EffectiveDate")).clear();
	  driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_EffectiveDate")).sendKeys(effDate);
       }
   
   if(!(providerId.equals(" "))){
	   driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_PMPIDSvcLoc")).clear();
	   driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_PMPIDSvcLoc']")).sendKeys(provider); //code change during 18.01
	   driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_RePmpHisDistanceSearchHis']")).sendKeys(svcloc);
	     }
   
   else{
  
        if(!(mcProgram.equals("HSPC")||mcProgram.equals("MSKSC")||mcProgram.equals("CCM")||mcProgram.equals("HBRW")||mcProgram.equals("MSTD")))
        {
	    driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_RePmpHisDistanceSearchHis_CMD_SEARCH']/img")).click();
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id371:RePmpDistanceSearchCriteriaPanel:DistanceArrayList"))).selectByValue("25");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id371:RePmpDistanceSearchCriteriaPanel:InfoSpecList"))).selectByValue("");
        driver.findElement(By.xpath("//*[contains(@id,'RePmpDistanceSearchCriteriaPanel:SEARCH')]")).click();
        driver.findElement(By.xpath("//*[contains(@id,'RePmpDistanceSearchResults_0:column1Value')]")).click(); 
        }
        else{
        	driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_RePmpHisDistanceSearchHis_CMD_SEARCH']/img")).click();
            driver.findElement(By.xpath("//*[contains(@id,'RePmpDistanceSearchCriteriaPanel:SEARCH')]")).click();
            driver.findElement(By.xpath("//*[contains(@id,'RePmpDistanceSearchResults_0:column2Value')]")).click();
            }
    }
   new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_StartReasonDropDownEntries"))).selectByValue(startReason);
   Common.save();
   System.out.println("Member enrolled successfully: "+memberId);
   return memberId;
   }
  

	
  public static String disEnrollment(String sql,String mcProgram,String stopReason) throws SQLException, IOException{
	 
	 int i=0;
 	 String EndDate="12/31/2299";	
 	 Calendar c = Calendar.getInstance();  
	 c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
//     Date lastDayOfMonth = c.getTime();
//     String date=(new SimpleDateFormat("MM/dd/yyyy").format(c.getTime()));
     
   colNames.add("ID_MEDICAID");
   colValues=Common.executeQuery(sql,colNames);
   memberId=colValues.get(0);
   String sqlStatement1=sql;
   driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
   driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memberId);
   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
   driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
   System.out.println("selected one memberid");
   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_RePmpAssignSu")).click();
   driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:RePmpAssignSuBean_ColHeader_endDate")).click();
   driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:RePmpAssignSuBean_ColHeader_endDate")).click();
   int rownum =driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr")).size();
   for(i=1;i<=rownum;i++)
   { 
      if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[3]")).getText().equals(mcProgram))
   	  {
   		if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[5]")).equals(EndDate));
       	   {
       		   break;
       	   }
   	  }
    }
   if(i==(rownum+1))
   {
   	  throw new SkipException("There is no member with mcprogram,"+mcProgram+"and enddate=12/31/2299 in the SU Panel");
   } 
   driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_"+(i-1)+":RePmpAssignSuBean_ColValue_publicHlthPgm_pgmHealthCode']")).click();
   driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_EndDate")).clear();
   if(mcProgram.equals("MSTD")||mcProgram.equals("PCCP")||mcProgram.equals("CBHI1"))
     {
   	 String endDate=Common.convertSysdate();
     driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_EndDate")).sendKeys(endDate);
	 }
   else
     {
   	 driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_EndDate")).sendKeys(lastDateOfPresentMonth());
	 }
   new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_StopReason"))).selectByValue(stopReason); 
   driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuHistoryPanel_updateAction_btn")).click();
   driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
   if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
		int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
        for(int j=0;j<length;j++)
         {
       	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(j).click();
         }
        driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
		}
   String message=driver.findElement(By.cssSelector("td.message-text")).getText();
   if(message.equals("Save Successful.")){
  	 System.out.println("Disenrollment for "+mcProgram+" is completed"); 
  	 Common.cancelAll();
      }
   else if((message.contains("No date overlaps are allowed for mutually exclusive"))||(message.contains("No PMP ID / Svc Loc found given Effective/End Date"))){
	   log("Member id with No date overlap error: "+memberId);
       i=i++; 
       sqlStatement1=sqlStatement1+" and id_medicaid<>"+memberId+"";
       System.out.println(sqlStatement1);
       colNames.add("id_medicaid");
       colValues=Common.executeQuery(sqlStatement1,colNames);
       String newMemberId=colValues.get(0);
	   System.out.println("New member id"+i+": "+newMemberId);
	   memberId=newMemberId;
	   Common.cancelAll();
	   driver.findElement(By.id("MMISForm:MMISHeader:header_value_home")).click();
	   disEnrollment(sqlStatement1,mcProgram,stopReason); 
	   }
   System.out.println("MemberId disenrolled successfully: "+memberId);
   return memberId;
	 }

 //Get last date of present month
	    public static String lastDateOfPresentMonth(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 0);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
		Date lastDateOfPresentMonth = cal.getTime();
		SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy");
		return (sdf.format(lastDateOfPresentMonth).toString());
	    }

}
