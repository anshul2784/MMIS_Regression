package newMMIS_Subsystems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection; 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import ch.ethz.ssh2.channel.Channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.sql.DriverManager;


public class Common extends Login {
	
//	public static String sqlStatement;
	public static Connection connection, connection1 = null;
	public static Alert alert;
	public static int multiElementSelect = 1;
	public static String localUserDir="/home/rvattumilli/testRptFormat";
//	public static String localUserDir="/home/agandhi/testRptFormat";
	public static String SelSql, col, DelSql, InsSql;

	
	//Brings Application to Home page before each @Test annotation
	public static void resetBase() throws Exception  {
		if (!(winHandleCurrent.equals("empty"))) 
			Common.switchToMainWin();
		
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (!(driver.findElements(By.id("MMISForm:MMISHeader:header_value_home")).size()>0) || driver.findElements(By.xpath("//a[contains(@href, 'EHSProviderPortal/jsp/logout.jsp')]")).size()>0) {
			System.out.println("Logging into Base application again because could not find an active Base window");
			driver.quit();
			//Login to the Base application. This will also set the timer to 30 seconds
			testLoginBase();
		}
		
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@class='buttonImage' and @alt='Cancel All']")).size()>0) {
			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Cancel All']")).click();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		}
		
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.findElement(By.id("MMISForm:MMISHeader:header_value_home")).click();
		driver.findElement(By.id("MMISForm:MMISHeader:header_value_home")).click();
	}
		
		public static void resetPortal() throws Exception  {
			if (!(winHandleCurrent.equals("empty"))) 
				Common.switchToMainWin();
			
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			if ((driver.findElements(By.id("MMISForm:MMISHeader:header_value_home")).size()>0) || !(driver.findElements(By.linkText("Logout")).size()>0)) {
				System.out.println("Logging into Portal again because could not find an active Portal window");
				driver.quit();
				//Login to the portal. This will also set the timer to 30 seconds
				testLoginPortal();
			}
			
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			if (driver.findElements(By.xpath("//input[@class='buttonCommand' and @value='Close']")).size()>0) 
				driver.findElement(By.xpath("//input[@class='buttonCommand' and @value='Close']")).click();
			else if (driver.findElements(By.xpath("//input[@class='buttonCommand' and @value='Cancel Service']")).size()>0) 
				driver.findElement(By.xpath("//input[@class='buttonCommand' and @value='Cancel Service']")).click();
			
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			driver.findElement(By.linkText("Home Services")).click();
			isAlertPresent();
			driver.findElement(By.linkText("Home Services")).click();
	}
	
	public static void saveAll() {
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		if ((driver.findElements(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).size())>0) 
			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).click();
//		if ((driver.findElements(By.xpath("//input[@class='buttonImage' and @alt='Skip Change']")).size())>0) 
//			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Skip Change']")).click();

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		String message=driver.findElement(By.cssSelector("td.message-text")).getText();
		Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"..."); //Assert modified by Priya to include Login failure message in reports
	}
	
	public static void cancelAll() {
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Cancel All']")).click();
	}
	
	public static void update() {
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Update']")).click();
	}
	
	
	public static void search() {
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Search']")).click();
	}
	
	
	public static void minMax() throws Exception {
		//Check Minimize
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Minimize']")).click();
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		Assert.assertTrue(!(driver.findElements(By.cssSelector("strong")).size()>0));
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		//Check Maximize
		driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Maximize']")).click();
		Assert.assertTrue(driver.findElements(By.cssSelector("strong")).size()>0);
		
		//Click on Help link
	    driver.findElement(By.linkText("Help")).click();
		
		//Store the current window Handle
		winHandleCurrent=driver.getWindowHandle();
		

		//Switch to new window
		for(String winHandleNew:driver.getWindowHandles())
			driver.switchTo().window(winHandleNew);
		
		Assert.assertTrue(driver.findElement(By.cssSelector("h1")).getText().equals("RELATED DATA MAINTENANCE HELP"));
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/table/tbody/tr/td/table[3]/tbody/tr/td/div/table/tbody/tr[6]/td[2]/p/b/span")).getText().equals("Cancel All"));

		//Close the second window
		driver.close();
				
		//Switch back to main window
		switchToMainWin();
		
		//Store the current window Handle
		winHandleCurrent=driver.getWindowHandle();
		
		
		//Click on Print Button
		driver.findElement(By.xpath("//input[@type='image' and @alt='Print']")).click();
		
		//Switch to new window
		for(String winHandleNew:driver.getWindowHandles())
			driver.switchTo().window(winHandleNew);
		
		if((driver.findElement(By.cssSelector("strong")).getText().equals("Codes")))
			Assert.assertTrue(driver.findElement(By.cssSelector("strong")).getText().equals("Codes"));
		else if((driver.findElement(By.cssSelector("strong")).getText().equals("Other")))
			Assert.assertTrue(driver.findElement(By.cssSelector("strong")).getText().equals("Other"));
		else
			Assert.assertTrue(driver.findElement(By.cssSelector("strong")).getText().equals("Xref"));

		//Close the second window
		driver.close();
			
		//Switch back to main window
		switchToMainWin();
	}
	
	public static void switchToMainWin() {
		driver.switchTo().window(winHandleCurrent);
		winHandleCurrent="empty";
	}
	
	//Get system date in MM/DD/YYYY format
	public static String convertSysdate() {
		Date sysdate=new Date();
		SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy");
		return (sdf.format(sysdate).toString());
	}
	
	//Get system date +/- days in MM/DD/YYYY format
	public static String convertSysdatecustom(int i) {
		SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy");
		Calendar cal = Calendar.getInstance();     
		cal.add( Calendar.DATE, i );    
		return (sdf.format(cal.getTime()).toString());
	}
	
	//Get given date +/- days in MM/DD/YYYY format
	public static String convertGivendatecustom(String inputStrDt, int i) throws Exception {
		SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy");
		Date date =sdf.parse(inputStrDt);

		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		cal.add( Calendar.DATE, i );    
		return (sdf.format(cal.getTime()).toString());
	}
	
	//Get database date from yyyyMMdd format to MM/dd/YYYY format
	public static String convertDate(String dbDate) throws Exception {
		SimpleDateFormat sdf1= new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat sdf2= new SimpleDateFormat("yyyyMMdd");
		
		Date date=sdf2.parse(dbDate);
		return sdf1.format(date).toString();
	}
	
	//Convert date from MM/dd/YYYY format YYYY-MM-dd format
	public static String convertDate_yyyyHIFENmmHIFENdd(String regularDate) throws Exception {
		SimpleDateFormat sdf1= new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2= new SimpleDateFormat("MM/dd/yyyy");
		
		Date date=sdf2.parse(regularDate);
		return sdf1.format(date).toString();
	}
	
	//Convert date from YYYY-MM-dd format to MM/dd/YYYY  format
	public static String convertDate_yyyyHIFENmmHIFENddToRegular(String yyyyHIFENmmHIFENdd) throws Exception {
		SimpleDateFormat sdf1= new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat sdf2= new SimpleDateFormat("yyyy-MM-dd");
		
		Date date=sdf2.parse(yyyyHIFENmmHIFENdd);
		return sdf1.format(date).toString();
	}
	
	//Get member sak from member ID
	public static String getMemberSak(String id_member) throws SQLException {
		String s = null;
		sqlStatement="select SAK_RECIP from t_re_base where id_medicaid="+id_member;
		Statement MemberSakstatement = null;
		ResultSet MemberSakresultset = null;
		
		MemberSakstatement = connection.createStatement();
		MemberSakresultset = MemberSakstatement.executeQuery(sqlStatement);
		
		while (MemberSakresultset.next()) {
			s=MemberSakresultset.getString("SAK_RECIP");
		}
		return s;
	}
	
	//Get member ID from member sak
	public static String getMemberID(String sak) throws SQLException {
		String s = null;
		sqlStatement="select ID_MEDICAID from t_re_base where sak_recip="+sak;
		Statement MemberSakstatement = null;
		ResultSet MemberSakresultset = null;
		
		MemberSakstatement = connection.createStatement();
		MemberSakresultset = MemberSakstatement.executeQuery(sqlStatement);
		
		while (MemberSakresultset.next()) {
			s=MemberSakresultset.getString("ID_MEDICAID");
		}
		return s;
	}
	
	//Execute SQL and fetch results
	public static ArrayList<String>  executeQuery( String sqlStatement, ArrayList<String> fields ) throws SQLException {
		ArrayList<String> col = new ArrayList<String>();
		Statement statement = null;
		ResultSet resultset = null;
		
		statement = connection.createStatement();
		resultset = statement.executeQuery(sqlStatement);

		if (resultset.next()) {
			resultset = statement.executeQuery(sqlStatement);
			while (resultset.next()) {
				for (int i=0;i<fields.size();i++){
					col.add(resultset.getString(fields.get(i)).trim());
					}
				} 
			fields.clear();
			statement.close();
			resultset.close();
			return col;
			} 
		 else {
			 fields.clear();
			 statement.close();
			 resultset.close();
			 throw new SkipException("Skipping this test  because this SQL did not return any rows: "+sqlStatement);
	}
//		else {
//				col.add("null");
//				fields.clear();
//				return col;
//			}
		}
	
	//Execute SQL and fetch results
//	public static ArrayList<String>  executeQuery( String sqlStatement, ArrayList<String> fields ) throws SQLException {
//		ArrayList<String> col = new ArrayList<String>();
//		Statement statement = null;
//		ResultSet resultset = null;
//		
//		statement = connection.createStatement();
//		resultset = statement.executeQuery(sqlStatement);
//
//		if (resultset.next()) {
//			resultset = statement.executeQuery(sqlStatement);
//			while (resultset.next()) {
//				for (int i=0;i<fields.size();i++){
//					col.add(resultset.getString(fields.get(i)).trim());
//					}
//				} 
//			fields.clear();
//			} else {
//				fields.clear();
//				throw new SkipException("Skipping this test  because this SQL did not return any rows: "+sqlStatement);
//			}
//				
//		return col;
//		}
	
	//MO and UAT DB connection
	public static boolean connectDB(String environment)
			throws Exception {
		
		String u = "mnarala";
		String p = "mnarala";

		try{ 
			String hostName;
		    String serviceName;
		    if (environment.equals("MO")) {
		    	hostName="10.196.210.9";
			    serviceName="mamism1";
			    } 
		    else if (environment.equals("MOREG")) {
		    	hostName="170.63.149.70";
			    serviceName="mamist3";
		    }
		    else if (environment.equals("PERF")) {
		    	hostName="10.196.210.73";
			    serviceName="mamisp3";
			    u = "agandhi";
				p = "gandhi8!";
		    }
		    else if (environment.equals("POC")) {
		    	hostName="170.63.156.69";
			    serviceName="mamist5";
		    }
		    else if (environment.equals("ST")) {
		    	hostName="10.196.210.77";
			    serviceName="MAMISP2";
			    u="ynaini";
			    p="nw5.xf3p";
		    }
		    else if (environment.equals("MO2")) {
		    	hostName="10.196.210.83";
			    serviceName="MAMISX1";
			    u = "agandhi";
			    p = "gandhi8!";
		    }
		    else if (environment.equals("AWSMO")) {
		    	hostName="ehsmo-mis-rds1.ehs.state.ma.us";
			    serviceName="MAMISM1";
			    u = "agandhi";
			    p = "toyota8!";
		    }
		    else if (environment.equals("AWSUAT")) {
		    	hostName="ehsac-mis-rds1.ehs.state.ma.us";
			    serviceName="MAMISA1";
			    u = "agandhi";
			    p = "toyota8!";
		    }
		    else if (environment.equals("AWSMO2")) {
		    	hostName="ehsm2-mis-rds1.ehs.state.ma.us";
			    serviceName="MAMISX1";
			    u = "agandhi";
			    p = "toyota8!";
		    }
		    else if (environment.equals("AWSPERF")) {
		    	hostName="ehspf-mis-rds1.ehs.state.ma.us";
			    serviceName="MAMISP3";
			    u = "agandhi";
			    p = "toyota8!";
		    }
		    else if (environment.equals("AWSPROD")) {
		    	hostName="ehspr-mis-rds1.csxzdz8uzlt0.us-east-1.rds.amazonaws.com";
			    serviceName="MAMISP1";
			    u = "mnarala";
			    p = "reset.2t";
		    }
		    else {
			    	hostName="10.196.210.71";
			        serviceName="mamisa1";
			        }
//		    System.out.println("DB: "+environment+" "+u+" "+p);
		    connection = DriverManager.getConnection("jdbc:oracle:thin:@"+hostName+":1521:"+serviceName,u,p);
		    System.out.println("Connected to "+hostName+" "+serviceName);	
		    return true;
		    } catch(Exception e) {
		    	return false;
		    	}
		}
	
	//MIP DB connection
		public static boolean connectMIP() throws Exception {
			try{ 
				String host = "10.220.64.82";
			    String svcName = "madocp1";
			    
			    connection1 = DriverManager.getConnection("jdbc:oracle:thin:@"+host+":1521:"+svcName,"automation","mn6ag+kh");
			    System.out.println("Connected to "+host+" "+svcName);	
			    return true;
			    } catch(Exception e) {
			    	return false;
			    	}
			}
		
		//Execute MIP SQL and fetch results
		public static ArrayList<String>  executeQuery1( String sqlStatement, ArrayList<String> fields ) throws SQLException {
			ArrayList<String> colMIP = new ArrayList<String>();
			Statement st = null;
			ResultSet res = null;
			
			st = connection1.createStatement();
			res = st.executeQuery(sqlStatement);

			if (res.next()) {
				res = st.executeQuery(sqlStatement);
				while (res.next()) {
					for (int i=0;i<fields.size();i++){
						colMIP.add(res.getString(fields.get(i)).trim());
						}
					} 
				fields.clear();
				st.close();
				res.close();
				return colMIP;
				} else {
					colMIP.add("null");
					fields.clear();
					st.close();
					res.close();
					return colMIP;
					}
		}
		
		//Portal login
		public static void portalLogin() throws Exception {
			//Logout of NewMMIS application
			driver.findElement(By.linkText("Logout")).click();
			driver.quit();
			
			//Login Back to Portal
			testLoginPortal();
		}
		
		//Portal logout
		public static void portalLogout() throws Exception {
			//Logout of NewMMIS application
			driver.findElement(By.linkText("Logout")).click();
			driver.quit();
			
			//Login Back to NewMMIS
			testLoginBase();
		}
		
		//***NO RECORDS FOUND***
		public static void searchRecords(String memberId) throws Exception{
	    	 try
	            {
	    		 driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
	    		 if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISContentTable']/tbody/tr/td/div/table/tbody/tr/td/div/table/tbody/tr/td/span")).getText().equals("***No records found***"))
	            	throw new SkipException("There are no records for this Member Id:"+memberId);
	            }
	    	 catch(NoSuchElementException Ex)
	            {
	            }
	 		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	     }
		
		//Define Arraylists for preferences
		public static ArrayList<String> codes = new ArrayList<String>();
		public static ArrayList<String> other = new ArrayList<String>();
		public static ArrayList<String> xref = new ArrayList<String>();

		
		//Set reset preferences
		public static void Pref(ArrayList<String> codes, ArrayList<String> other, ArrayList<String> xref, String subsystem) throws Exception{
			driver.findElement(By.linkText("Preferences")).click();
		    
			//Store the current window Handle
			winHandleCurrent=driver.getWindowHandle();
			
			//Switch to new window
			for(String winHandleNew:driver.getWindowHandles())
				driver.switchTo().window(winHandleNew);
			
			//Verify that new preferences widow pops up
			Assert.assertTrue(driver.findElement(By.cssSelector("td.header")).getText().equals("Preferences"));
			
			//Set preferences for codes
			if (!(codes.get(0).equals("empty"))) {
				for (int i=0; i<codes.size(); i++) 
					driver.findElement(By.id(codes.get(i))).click();
			}
			
			//Set preferences for other
			if(!(other.get(0).equals("empty"))) {
				if(!(codes.get(0).equals("empty"))) {
					driver.findElement(By.linkText("Other")).click();
					} 
				for (int i=0; i<other.size(); i++) 
					driver.findElement(By.id(other.get(i))).click();
			}
			
			//Set preferences for xref
			if(!(xref.get(0).equals("empty"))) {
				if(!(codes.get(0).equals("empty")) || !(other.get(0).equals("empty"))) {
					driver.findElement(By.linkText("Xref")).click();
					} 
				for (int i=0; i<xref.size(); i++) 
					driver.findElement(By.id(xref.get(i))).click();
			}
			
			
			driver.findElement(By.xpath("//input[@type='image' and @alt='Save Preferences']")).click();
			
			//Call Alert to handle Popup
			alert = driver.switchTo().alert();
			System.out.println(alert.getText());
			alert.accept();

		    //Switch back to main window
		    Common.switchToMainWin();
		    
		    //Reset page and come back on Related data to see preferences selected
		    Common.resetBase();
			driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_"+subsystem)).click();
			driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_RelatedData")).click();
		}
		
		//Clear previous preferences
		public static void clearPref(ArrayList<String> codes, ArrayList<String> other, ArrayList<String> xref){
			codes.clear();
			other.clear();
			xref.clear();
		}
		
		public static String generateRandomName(){
			String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			int stringLength = 8;
			String randomString = "";
			for (int i=0; i<stringLength; i++) {
				int rnum = (int) Math.floor(Math.random() * allowedChars.length());
				randomString += allowedChars.substring(rnum, rnum+1);
			}
			return randomString;
		}
		
		public static String generateRandomTaxID(){
			String allowedChars = "1234567890";
			int stringLength = 9;
			String randomString = "";
			for (int i=0; i<stringLength; i++) {
				int rnum = (int) Math.floor(Math.random() * allowedChars.length());
				randomString += allowedChars.substring(rnum, rnum+1);
			}
			
			return randomString;
		}
		
		public static void warning(){
			driver.findElement(By.xpath("//input[@type='checkbox' and contains(@name ,'Warning')]")).click();
		}
		
		public static ArrayList<String[]> getDBTestData(String sqlStatement,int noOfCols, Connection conn) throws Exception {
			ArrayList<String[]> rowList = new ArrayList<String[]>();
			Statement statement = null;
			ResultSet resultSet = null;
			
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sqlStatement);
			String[] colList=null;
			int i;
			
			// checking if empty
			if (!(resultSet.isBeforeFirst())) 
			{    
				 System.out.println("No data");
				 statement.close();
				 resultSet.close();
				 return null;
			} 

			while (resultSet.next()) 
			{
				colList=new String[noOfCols];
					for (i=0;i<noOfCols;i++)
					{
						colList[i]=resultSet.getString(i+1).trim();
					}					
				rowList.add(colList);
		    } 
			 statement.close();
			 resultSet.close();
			 return rowList;
				
		}
		
		public static ArrayList<String> getDataRday2_new(String tcNo,String desc) throws Exception {
	  		sqlStatement = "select * from r_day2 where TC='" + tcNo + "' and DES= '"+desc+"'";
	  		colNames.add("ID");
	  		colNames.add("DATE_REQUESTED");
	  		return colValues = Common.executeQuery1(sqlStatement, colNames);
	  	}
		
		public static void getPageError(String target) {
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			if (driver.findElements(By.xpath(target)).size()>0) {
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				int noOfErrors = driver.findElements(By.xpath(target)).size();
				String Error = "";
				WebElement element;
				for (int i=0;i<noOfErrors;i++) {
					element = driver.findElements(By.xpath(target)).get(i);
					Error = Error+"Error("+(i+1)+") "+element.getText()+" ";			
				}
				Assert.assertTrue(false, Error); 
			}
			else
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		}
		
		public static void isAlertPresent() 
		{ 
		    try 
		    { 
				alert = driver.switchTo().alert();
				System.out.println(alert.getText());
				alert.accept(); 
		    } 
		    catch (NoAlertPresentException Ex) 
		    { 
		    }  
		} 
		
		//***NO RECORDS FOUND***
		
	    
	    public static String connectUNIX_old(String command, String error) throws Exception{

	    	String hostname;
			if (env.equals("MO"))
				hostname = "10.196.210.9";
			else if ((env.equals("UAT")))
				hostname = "10.196.210.71";
			else if ((env.equals("PERF")))
				hostname = "10.196.210.73";
			else if ((env.equals("MO2")))
				hostname = "10.196.210.83";
			else if ((env.equals("AWSMO")))
				hostname = "ehsmo-mis-lbt1.ehs.state.ma.us";
			else
				throw new SkipException("You are not conducting this test in MO, MO2 or UAT, so skipping this report test case because current environment is not running cybermation");

			String username = "angandhi"; //AWSMO username is agandhi, so configure that accordingly
			String password = "PRague.1"; //For UAT its AUburn.1. For other envs, its CAnton.1
			String outputText = "";

			try
			{
				// Create a connection instance
				ch.ethz.ssh2.Connection conn = new ch.ethz.ssh2.Connection(hostname); //Can't use this in import statement because already have an SQL connection
				conn.connect();

				// Authenticate.
				boolean isAuthenticated = conn.authenticateWithPassword(username, password);
				if (isAuthenticated == false)
					throw new IOException("Authentication failed.");

				/* Create a session */
				Session sess = conn.openSession();
				System.out.println("Running Command:"+command);
				sess.execCommand(command);

				//Read response
				InputStream stdout = new StreamGobbler(sess.getStdout());
				BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
				String line = ""; 
				
				while (true)
				{
					line = br.readLine();
					if (line == null)
						break;
					outputText = outputText+line;
				}
				if (outputText.equals("") && !(command.contains("echo")) && !(command.contains("cp ")) && !(command.contains("chmod")) && !(command.contains("uncompress")) && !(command.contains("rm ")) && !(command.contains("cd ")) && !(command.contains("rm "))) //Some commands have spaces in front of them because one of the commands was interfering when files are in /home/angandhi/testRptFoRMat (see last word has rm..testRptFormat)
					Assert.assertTrue(false, error);
				
				/* Show exit status, if available (otherwise "null") */
				System.out.println("ExitCode: " + sess.getExitStatus());

				// Close this session and connection 
				sess.close();
				conn.close();

			}
			catch (IOException e)
			{
				e.printStackTrace(System.err);
				System.exit(2);
			}
			return outputText;
	    }
	    
	    public static String connectUNIX(String command, String error) throws Exception{
	    	
	    	String hostname, username, password;
            hostname=getHostname(env);

            colNames.add("NAM_USER");
            colNames.add("PASSWORD");
            String ediLoginSql = "select nam_user, password from automation.v_edi_login where days_to_reset_pwd > 1 and hostname = '"+hostname+"'";
            colValues = executeQuery1(ediLoginSql, colNames);
            if (!colValues.get(0).equals("null")) {
                           username = colValues.get(0);
                           password = decodePwd(colValues.get(1));
//                           username = "agandhi";
//                           password = "DEnver.1";
//                           System.out.println(username +" "+password);
            } else {
                           System.out.println("No data returned from DB for UNIX Host authorization");
                           log("No data returned from DB for UNIX Host authorization");
                           throw new SkipException("No data returned from DB for UNIX Host authorization");
            }
            String outputText = "";
			try
			{	
				if(env.contains("AWS")) { //Linux based connection for AWS environments
					// Create a connection instance
					JSch jsch=new JSch(); 
					com.jcraft.jsch.Session sess = jsch.getSession(username, hostname, 22);
					
					//Configure to not check hostkey as we get Unknown hostkey error without this
					java.util.Properties config = new java.util.Properties(); 				
					config.put("StrictHostKeyChecking", "no");
					sess.setConfig(config);
					
					sess.setPassword(password);
					sess.connect();
					
					System.out.println("Running Command:"+command);
					
					com.jcraft.jsch.Channel channel=sess.openChannel("exec");
				      ((ChannelExec)channel).setCommand(command);
				 
				      channel.setInputStream(null);
				 
				      ((ChannelExec)channel).setErrStream(System.err);
				 
				      InputStream in=channel.getInputStream();
				 
				      channel.connect();
				      if (command.contains("sed"))
				    	  Thread.sleep(1000); //Added because channel needs time for returning very large outputs like from sed command
				      //Read response
				      byte[] tmp=new byte[102400]; //100KB buffer
				      while(true){
				        while(in.available()>=0){ // '>' changed to '>=' because for sed command output is large and still coming. =0 means still coming, so added =
				          int i=in.read(tmp, 0,102400);
				          if(i<0)break;
				          outputText=outputText+(new String(tmp, 0, i)); //Added "outputText+" because while loop executing more than once in case of large inputstreams like from sed command
				        }
				        if(channel.isClosed()){
				            System.out.println("exit-status: "+channel.getExitStatus());
				            break;
				          }
				        try{Thread.sleep(1000);}catch(Exception ee){}
					    // Close this channel and session
					    channel.disconnect();
					    sess.disconnect();
				      }
					}
				else { //Unix based connection for on-prem environments	
					// Create a connection instance
					ch.ethz.ssh2.Connection conn = new ch.ethz.ssh2.Connection(hostname); //Can't use this in import statement because already have an SQL connection
					conn.connect();

					// Authenticate.
					boolean isAuthenticated = conn.authenticateWithPassword(username, password);
					if (isAuthenticated == false)
						throw new IOException("Authentication failed.");

					/* Create a session */
					Session sess = conn.openSession();
					System.out.println("Running Command:"+command);
					sess.execCommand(command);

					//Read response
					InputStream stdout = new StreamGobbler(sess.getStdout());
					BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
					String line = ""; 
					
					while (true)
					{
						line = br.readLine();
						if (line == null)
							break;
						outputText = outputText+line;
					}
					/* Show exit status, if available (otherwise "null") */
					System.out.println("ExitCode: " + sess.getExitStatus());

					// Close this session and connection 
					sess.close();
					conn.close();
				}
				
				// ls in linux is adding a newline at the end. Added this on 6/17/2025 by AG to handle newline
				if (command.contains("ls"))
					outputText=removeNewline(outputText);
				    
				if (outputText.equals("") && !(command.contains("echo")) && !(command.contains("cp ")) && !(command.contains("chmod")) && !(command.contains("uncompress")) && !(command.contains("rm ")) && !(command.contains("cd ")) && !(command.contains("rm "))) //Some commands have spaces in front of them because one of the commands was interfering when files are in /home/angandhi/testRptFoRMat (see last word has rm..testRptFormat)
					Assert.assertTrue(false, error);
				
//				/* Show exit status, if available (otherwise "null") */
//				System.out.println("ExitCode: " + sess.);
				
		}
			catch (IOException e)
			{
				e.printStackTrace(System.err);
				System.exit(2);
			}
			return outputText;
	    }
	    
	    public static void connectUNIXsftp(String copyFrom, String copyTo) throws Exception{

	    	
			String hostname;
			if (env.equals("MO"))
				hostname = "10.196.210.9";
			else if ((env.equals("UAT")))
				hostname = "10.196.210.71";
			else if ((env.equals("PERF")))
				hostname = "10.196.210.73";
			else
				throw new SkipException("You are not conducting this test in MO or UAT, so skipping this report test case because current environment is not running cybermation");

			String username = "angandhi";
			String password = "EAston.1";

			try
			{	
				JSch jsch=new JSch();
				jsch.setConfig("StrictHostKeyChecking", "no");
			      com.jcraft.jsch.Session session=jsch.getSession(username, hostname);
			      session.setPassword(password);
			      session.connect();
			      com.jcraft.jsch.Channel channel = session.openChannel("sftp");
                  channel.connect();
                  ChannelSftp sftpChannel = (ChannelSftp) channel; 
                  sftpChannel.put(copyFrom, copyTo);

                  sftpChannel.exit();
                  session.disconnect();
                  System.out.println("Successfully copied the file "+copyFrom+" to "+copyTo);
			}
			        			    
			catch (JSchException e) {
                e.printStackTrace();  
            } catch (SftpException e) {
                e.printStackTrace();
            }
	    }
	    
	    public static String getHostname(String env) {
            if (env.equals("MO"))
                           return "10.196.210.9";
            else if ((env.equals("UAT")))
                           return  "10.196.210.71";
            else if ((env.equals("PERF")))
                           return  "10.196.210.73";
            // RV - Include MO2 env host
            else if ((env.equals("MO2")))
                           return  "10.196.210.83";
            else if (env.equals("MOREG")) 
                           return "170.63.149.70";
            else if (env.equals("POC")) 
                           return "170.63.156.69";
            else if (env.equals("ST")) 
                           return "10.196.210.77";
            else if (env.equals("AWSMO")) 
                return "ehsmo-mis-lbt1.ehs.state.ma.us"; //return "10.202.202.110";
            else if (env.equals("AWSUAT")) 
            	return "ehsac-mis-lbt1.ehs.state.ma.us";
            else if (env.equals("AWSMO2"))
            	return "ehsm2-mis-lbt1.ehs.state.ma.us";
            else if (env.equals("AWSPERF")) 
            	return "ehspf-mis-lbt1.ehs.state.ma.us";
            else if (env.equals("AWSPROD")) 
            	return "ehsm2-mis-lbt1.ehs.state.ma.us"; //change later this is not correct
            else
                           throw new SkipException("You are not conducting this test in supported env, so skipping this report test case because current environment is not running cybermation");
	    }

	    public static String decodePwd(String encryptedpwd) throws UnsupportedEncodingException {
            
            byte[] b = Base64.getDecoder().decode(encryptedpwd);
            return new String(b, "utf-8");
	    }

	    
	    public static String monthUNIX(String fileDate){
	    	
	    	String[] i = new String [13];
	    	i[1]="Jan";
	    	i[2]="Feb";
	    	i[3]="Mar";
	    	i[4]="Apr";
	    	i[5]="May";
	    	i[6]="Jun";
	    	i[7]="Jul";
	    	i[8]="Aug";
	    	i[9]="Sep";
	    	i[10]="Oct";
	    	i[11]="Nov";
	    	i[12]="Dec";
	    	
	    	int monthNum1 = Integer.parseInt(fileDate.substring(0,1));
	    	int monthNum2 = Integer.parseInt(fileDate.substring(1,2));
	    	
	    	if (monthNum1==0)
	    		return i[monthNum2];
	    	else
	    		return i[10+monthNum2];

	    }
	    
	    public static String dayUNIX(String fileDate){
	    	
	    	String dayNum1 = fileDate.substring(3,4);
	    	String dayNum2 = fileDate.substring(4,5);
	    	
	    	if (dayNum1.equals("0"))
	    		return " "+dayNum2;
	    	else
	    		return dayNum1+dayNum2;

	    }
	    
	    public static String yearUNIX(String fileDate){
	    		return fileDate.substring(6,10);
	    }
	    
		public static void SaveWarnings(){
			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
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
			  }
		
		public static void save(){
			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
		    driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
				int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
		         for(int i=0;i<length;i++)
		        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
			     driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
			if ((driver.findElements(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).size())>0) 
			driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Accept Change']")).click();
//			if ((driver.findElements(By.xpath("//input[@class='buttonImage' and @alt='Skip Change']")).size())>0) 
//				driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Skip Change']")).click();
			//Again check if warning message is there
			if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
				int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
		         for(int i=0;i<length;i++)
		        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(i).click();
			     driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			String message=driver.findElement(By.cssSelector("td.message-text")).getText();
			Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
			
		}
		
		public static void insertData(String SelSql, String col, String DelSql, String InsSql) throws SQLException {
	    	Statement statement = Common.connection1.createStatement();
			sqlStatement = SelSql;
			colNames.add(col);
			colValues = Common.executeQuery1(sqlStatement, colNames);
			if (!(colValues.get(0).equals("null"))) {
				sqlStatement = DelSql;
				statement.executeQuery(sqlStatement);
			}
			sqlStatement = InsSql;
			statement.executeQuery(sqlStatement);
		}
		
		public static void insertData_day2(String tcNo, String id, String des, String date) throws Exception {
			SelSql = "SELECT * FROM R_DAY2 WHERE TC = '" + tcNo + "' AND DES = '" + des + "'";
			col = "TC";
			DelSql = "DELETE FROM R_DAY2 WHERE TC = '" + tcNo + "' AND DES = '" + des + "'";
			InsSql = "INSERT INTO R_DAY2 (TC, ID, DES, DATE_REQUESTED) VALUES ('" + tcNo + "', '" + id + "', '" + des + "', '" + date + "')";
			insertData(SelSql, col, DelSql, InsSql);
		}
		
		public static String fileName() throws Exception {
			Thread.sleep(2000); //Allow time for file download
			File directory = new File(tempDirPath);
			File[] files = directory.listFiles();
			if (files.length>1) {
		        Arrays.sort( files, new Comparator<File>()
		        {
		        public int compare(File o1, File o2) {
		            return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
		        }
		        });
			}
//			files[0].renameTo(tempDirPath);
	            return (files[0].getName());

			
		}
		
		public static String get834(String Pid, String dt834, String tc, String recp ) throws Exception{

	        //Verify unix report
			String command, error;

			//Get Desired Filename
			command = "ls -ltr /edi/data/X12_OUT/WEB/"+Pid+".834D.WEB.*.* | grep '"+monthUNIX(dt834)+" "+dayUNIX(dt834)+"'";
			error = "834 not found";
			String fileName = Common.connectUNIX(command, error);
			//If the fileName has year stamp, get the correct year file
			if (fileName.contains(yearUNIX(dt834))) {
				command = "ls -ltr /edi/data/X12_OUT/WEB/"+Pid+".834D.WEB.*.* | grep '"+monthUNIX(dt834)+" "+dayUNIX(dt834)+"  "+yearUNIX(dt834)+"'";
				fileName = Common.connectUNIX(command, error);
			}
		
			fileName = fileName.substring(fileName.length()-56);
			
			log(" 834 filename for "+tc+" is: "+fileName);

			//Verify duplicate member data in file
			command = "cat "+fileName;
			//command = "grep "+inactMem+" "+fileName;
			error = "cannot open 834";
			String outputText = Common.connectUNIX(command, error);
			
			outputText = outputText.replace("~", "~\r\n");
			
			//Store 834
			String fileName1=tempDirPath+"834"+tc+".txt";
	    	PrintWriter out = new PrintWriter(fileName1);
	    	out.println(outputText);
	    	out.close();

	    	BufferedReader br = new BufferedReader(new FileReader(fileName1));
			String pline="";
	    	String line = ""; 
			String targetText = "";
			
			while (line != null)
			{
				pline = line;
				line = br.readLine();
				if (line.contains(recp)) {
					targetText = pline;
					while (!(line.contains("INS"))) {
						targetText = targetText+"\r\n"+line;
						line = br.readLine();
					}
					break;
				}
			}
			
			if (targetText.equals(""))
				Assert.assertTrue(false, "Member "+recp+" is not in this 834");
			return targetText;
	    }
		
		//Convert date fromat to integer
		public static String convertDatetoInt(String date) throws Exception {
			SimpleDateFormat sdf1= new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf2= new SimpleDateFormat("MM/dd/yyyy");
			Date dbDate=sdf2.parse(date);
			return sdf1.format(dbDate).toString();
		    }
		
		//Get system date from String to Date  format
		public static Date StrtoDT(String inputStrDt) throws Exception {
			Date date =new SimpleDateFormat("MM/dd/yyyy").parse(inputStrDt);
			return date;
		}
		
	    public static void connectUNIXmultiCommands(String[] command) throws Exception{

	    	
			String hostname;
			if (env.equals("MO"))
				hostname = "10.196.210.9";
			else if ((env.equals("UAT")))
				hostname = "10.196.210.71";
			else
				throw new SkipException("You are not conducting this test in MO or UAT, so skipping this report test case because current environment is not running cybermation");

			String username = "angandhi";
			String password = "AIzwal.1";

			try
			{
				// Create a connection instance
				ch.ethz.ssh2.Connection conn = new ch.ethz.ssh2.Connection(hostname); //Can't use this in import statement because already have an SQL connection
				conn.connect();

				// Authenticate.
				boolean isAuthenticated = conn.authenticateWithPassword(username, password);
				if (isAuthenticated == false)
					throw new IOException("Authentication failed.");

				/* Create a session */
				Session sess = conn.openSession();
				for (int i=0;i<command.length;i++) {
					System.out.println("Running Command:"+command[i]);
					sess.execCommand(command[i]);
					/* Show exit status, if available (otherwise "null") */
					System.out.println("ExitCode: " + sess.getExitStatus());
					Thread.sleep(2000);
				}

				// Close this session and connection 
				sess.close();
				conn.close();

			}
			catch (IOException e)
			{
				e.printStackTrace(System.err);
				System.exit(2);
			}
	    }
	    
	    //Elements present twice on page in TR, so creating problems, so below code is added
	    public static WebElement multiElements(String trgt) {
		    int elementSize = driver.findElements(By.xpath(trgt)).size();
		    WebElement multiElement;
//		    System.out.println(elementSize+" "+trgt+" "+multiElementSelect);
//		    for (int i=0;i<elementSize;i++)
//		    	System.out.println(driver.findElements(By.xpath(trgt)).get(i).getText());
		    if (elementSize==1)
		    	multiElement=driver.findElements(By.xpath(trgt)).get(0);
		    else
		    	multiElement=driver.findElements(By.xpath(trgt)).get(multiElementSelect);
		    if (multiElementSelect!=1)
		    	multiElementSelect=1;
		    return multiElement;

	    }
	    
	    //Get first day of current month
		public static String getFirstDay() {
			SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy");
			Calendar cal = Calendar.getInstance();
		    int firstDate = cal.getActualMinimum(Calendar.DATE);

		    cal.set(Calendar.DATE, firstDate);  
			return (sdf.format(cal.getTime()).toString());
		}
		
		public static String getFirstDayOfMonth(int year, int month) {
            // Create a Calendar instance and set it to the specified year and month
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1); // Month is 0-based in Calendar, so subtract 1
            calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the month

            // Format the date to mm/dd/yyyy
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            return dateFormat.format(calendar.getTime());
        }

	    
	    //Get last day of current month
		public static String getLastDay() {
			SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy");
			Calendar cal = Calendar.getInstance();
		    int lastDate = cal.getActualMaximum(Calendar.DATE);

		    cal.set(Calendar.DATE, lastDate);  
			return (sdf.format(cal.getTime()).toString());
		}
		
		//Unzips unix files if zipped
//	    public static String chkCompress(String fl) throws Exception {
//	    	
//	    	String command,error,flName=fl;
//	    	String lastindex = flName.substring(fl.length()-1, fl.length());
//	    	
//	    	if (!(lastindex.equals("Z"))) { //for Linux ls command resulting in hidden /n in the end
//	    		flName=flName.substring(0, fl.length()-1); 
//	    		lastindex = flName.substring(fl.length()-2, fl.length()-1); 
//	    	}
//	    	
//	    	if (lastindex.equals("Z")) {
////	    		//clear localUserDir
////	        	command = "rm -fr "+localUserDir+"/*";
////	        	error = "rm  "+localUserDir+" was not successfull";
////	        	Common.connectUNIX(command, error);
//	    		//Empty localUserDir
////	    		String comm[] = new String[2];
////	    		comm[0] = "cd "+localUserDir;
////	    		comm[1] = "rm -f *";
////	    		connectUNIXmultiCommands(comm);
//	    		//copy file to local user dir
//	        	command = "cp "+flName+" "+localUserDir;
//				System.out.println("Command:"+command);
//	        	error = "There was no report file found to copy to "+localUserDir;
//	        	Common.connectUNIX(command, error);
//	        	//Get new filename
////	        	command = "ls "+localUserDir+"/*.Z";//This is creating problems if other .Z files are present as well
//	        	command = "ls "+localUserDir+flName.substring(fl.lastIndexOf("/"), fl.length());
//	        	error = "There was no compressed file found in "+localUserDir;
//	        	flName=Common.connectUNIX(command, error);
//	        	//uncompress the file
//	        	command = "uncompress "+flName;
//	        	error = "There was no report file found";
//	    		Common.connectUNIX(command, error);
//	    		//Verify file was uncompressed
//	    		flName=flName.substring(0, flName.length()-2);
//	        	command = "ls "+flName;
//	        	error = "The uncompressed file '"+flName+"' was not found";
//	        	String resp = Common.connectUNIX(command, error);
//	    		Assert.assertTrue(resp.equals(flName), "Uncompress was not successfull for "+fl);
//	    		log("The uncompressed filename is: "+flName);
//	    		return flName;
//	    	}
//	    	else
//	    		return fl;
//	    		
//	    }
		
	    public static String chkCompress(String fl) throws Exception {
	    	
	    	String command,error,flName=fl; //fl may have a /n in the end. Retaining fl if needed in future to check that (by retrieving its last index)
	    	flName=removeNewline(flName);
	    	String lastindex = flName.substring(flName.length()-1, flName.length());

	    	if (lastindex.equals("Z")) {
	    		//copy file to local user dir
	        	command = "cp "+flName+" "+localUserDir;
	        	error = "There was no report file found to copy to "+localUserDir;
	        	Common.connectUNIX(command, error);
	        	//Get new filename
//	        	command = "ls "+localUserDir+"/*.Z";//This is creating problems if other .Z files are present as well
	        	command = "ls "+localUserDir+flName.substring(flName.lastIndexOf("/"), flName.length());
	        	error = "There was no compressed file found in "+localUserDir;
	        	flName=Common.connectUNIX(command, error);
	        	flName=removeNewline(flName);
	        	//uncompress the file
	        	command = "uncompress "+flName;
	        	error = "There was no report file found";
	    		Common.connectUNIX(command, error);
	    		//Verify file was uncompressed
	    		String UncompressedflName=flName.substring(0, flName.length()-2);
	        	command = "ls "+UncompressedflName;
	        	error = "The uncompressed file '"+UncompressedflName+"' was not found";
	        	String resp = Common.connectUNIX(command, error);
	        	resp = removeNewline(resp);
	    		Assert.assertTrue(resp.equals(UncompressedflName), "Uncompress was not successfull for "+flName);
	    		log("The uncompressed filename is: "+UncompressedflName);
	    		return UncompressedflName;
	    	}
	    	else
	    		return flName;
	    		
	    }
	    
	    //ls command in linux is coming with a /n in the end. 
		//Parsing and using filename for string comparison using ls is not a great idea in linux
	    //This function will check and remove /n from ls output
	    public static String removeNewline(String lsFileName){
	    	String lastindex = lsFileName.substring(lsFileName.length()-1, lsFileName.length());
	    	if (lastindex.equals("\n")) 
	    		lsFileName = lsFileName.substring(0, lsFileName.length()-1);	
	    	return lsFileName;
			}
	    
	    //Priya's function
		public static String firstDateOfPresentMonth(){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 0);
			cal.set(Calendar.DATE, 1);
			Date firstDateOfPresentMonth = cal.getTime();
			SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy");
			return (sdf.format(firstDateOfPresentMonth).toString());
			}
		
	    //Priya's function
	    //Get last date of prevoius month
		public static String lastDateOfPreviousMonth(){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
			Date lastDateOfPreviousMonth = cal.getTime();
			SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy");
			return (sdf.format(lastDateOfPreviousMonth).toString());
		    }
		
	    //Priya's function
	    public static String firstDateOfPreviousMonth(){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			cal.set(Calendar.DATE, 1);
			Date firstDateOfPreviousMonth = cal.getTime();
			SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy");
			return (sdf.format(firstDateOfPreviousMonth).toString());
			
	    }
	    
	    //Convert date from MM/dd/YYYY format YYYY-MM-dd format
	    public static String convertDate_DBformat_TO_fileFormat(String regularDate) throws Exception {
			SimpleDateFormat sdf1= new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf2= new SimpleDateFormat("yyyyMMdd");
				
			Date date=sdf2.parse(regularDate);
			return sdf1.format(date).toString();
		}
	    
	    public static void screenShot(String name) throws IOException {
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            if (!new File(tempDirPath).exists()) {
                            new File(tempDirPath).mkdir();
            }
            FileUtils.copyFile(scrFile, new File(tempDirPath + name + ".png"));
}

	    public static void updateMipQuery(String sqlStatement) throws SQLException {
			  //String sqlStatmentToUpdate = "update r_proc set FDOS = ?,  TDOS = ? where tc = '25992'; commit;";
			
			 
			// TODO Auto-generated method stub
			Statement statement = null;
			ResultSet resultset = null;
			statement = connection1.createStatement();
			
			statement.executeUpdate(sqlStatement);
		}  
	    
	  //Get difference between 2 dates in days
        public static String findDifference(String startDte,String endDte) throws Exception {
        	SimpleDateFormat sdf1= new SimpleDateFormat(startDte);
            SimpleDateFormat sdf2= new SimpleDateFormat(endDte);
            Date d1 = sdf1.parse(startDte); 
            Date d2 = sdf2.parse(endDte); 
			// Calculate time difference 
			long difference_In_Time = d2.getTime() - d1.getTime(); 
			// Calculate time difference in days
			long difference_In_Days = TimeUnit.MILLISECONDS.toDays(difference_In_Time)% 365;
			System.out.println("no of days: "+difference_In_Days);
			// String days= difference_In_Days.toString();
			// String days= String.format("%d", difference_In_Days);
			String days=String. valueOf(difference_In_Days);
			return days;             
        }

        /************************************************************************/
        /*                                                                      */
        /* Function Name:  writeTCDesc()                                        */
        /*                                                                      */
        /*   Description:  This function will write TC Desc to console and log  */
        /*                                                                      */
        /*    Parameters:  tcNumber                                             */
        /*                                                                      */
        /*       Returns:   None                                                */
/************************************************************************/
        /*                                                                      */
        /*                       MODIFICATION LOG                               */
        /*                                                                      */
        /* Date      A.E. Name       Description                                */
        /* --------  --------------  ------------------------------------------ */
        /* 12/05/21  R.Vattumilli    Initial Creation of writeTCDesc()          */
        /*                                                                      */
/************************************************************************/
        
        public static void writeTCDesc(String tcNumber) throws Exception {                      
                       TestNGCustom.TCNo = tcNumber;
                       //boolean templogMode = debugLogmode;
                       //debugLogmode = debugLogmode == true ? false : false;
                       ArrayList<String> tcData = new ArrayList<String>();
                       ArrayList<String> tcData1 = new ArrayList<String>();
                       tcData.add("NAM");                    
                       String tcDescSql = tcNumber.length() > 5 ? "select nam from test_case where sak_test_case = " +tcNumber.substring(0, 5) : "select nam from test_case where sak_test_case = " +tcNumber;
                       tcData1 = executeQuery1(tcDescSql, tcData);
                       //log(String.format(OUTPUT_FORMAT, "TC "+tcNumber, tcData1.get(0)));
                       System.out.println("TC "+tcNumber+": "+ tcData1.get(0));                            
                       //debugLogmode = templogMode == true ? true : false;                             
                       tcData.clear();
                       tcData1.clear();
        }
        
        private void addLongTimeCareWhereCareTypeIsRestHome(String svcloc,String provider, String mem, String fdos) throws Exception {
            
            
            Common.portalLogout();
                          // member tab-base app                           
            driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
            driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
            driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(mem);
			driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();                              
			driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id23")).click();
			                          
			driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_MdsLevelOfCare")).click();
			
			            
			 driver.findElement(By.id("MMISForm:MMISBodyContent:MdsLevelOfCarePanel:MdsLevelOfCare_NewButtonClay:MdsLevelOfCareList_newAction_btn")).click();
			driver.findElement(By.xpath("//*[@id=\"MMISForm:MMISBodyContent:MdsLevelOfCarePanel:MdsLevelOfCareBean_SvcLoc_CMD_SEARCH\"]/img")).click();
			driver.findElement(By.xpath("//input[contains(@id,'LevelofCareSearchCriteriaPanel:ProviderID')]")).clear();                                   
			
			driver.findElement(By.xpath("//input[contains(@id,'LevelofCareSearchCriteriaPanel:ProviderID')]")).sendKeys(provider);                                   
			 driver.findElement(By.xpath("//input[contains(@id,'LevelofCareSearchCriteriaPanel:ServiceLocationCode')]")).clear();                                   
			 driver.findElement(By.xpath("//input[contains(@id,'LevelofCareSearchCriteriaPanel:ServiceLocationCode')]")).sendKeys(svcloc); 
			 driver.findElement(By.xpath("//*[contains(@id,'LevelofCareSearchCriteriaPanel:SEARCH')]")).click();                                   
			 driver.findElement(By.xpath("//*[contains(@id,'LevelofCareSearchResults_0:column1Value')]")).click();                                   
			            
			 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:MdsLevelOfCarePanel:MdsLevelOfCareBean_reLocCode"))).selectByVisibleText("RH - Rest home");
			new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:MdsLevelOfCarePanel:MdsLevelOfCareDataPanel_ReLocAdmitReason"))).selectByVisibleText("HM - Admitted from home");
			driver.findElement(By.id("MMISForm:MMISBodyContent:MdsLevelOfCarePanel:MdsLevelOfCareDataPanel_EffectiveDate")).clear();
			driver.findElement(By.id("MMISForm:MMISBodyContent:MdsLevelOfCarePanel:MdsLevelOfCareDataPanel_EffectiveDate")).sendKeys(Common.convertSysdatecustom(Integer.parseInt(fdos)));
			driver.findElement(By.id("MMISForm:MMISBodyContent:MdsLevelOfCarePanel:MdsLevelOfCareDataPanel_AdmissionDate")).clear();
			driver.findElement(By.id("MMISForm:MMISBodyContent:MdsLevelOfCarePanel:MdsLevelOfCareDataPanel_AdmissionDate")).sendKeys(Common.convertSysdatecustom(Integer.parseInt(fdos)));

                // click add
           driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Add']")).click();

           Common.saveAll();
           Common.cancelAll();

            Common.portalLogin();               
        }
        
        public static String firstDateOfAnyMonth( String date) {
            return date.substring(0,3)+"01"+date.substring(5);
        }
        
      //Find difference between 2 dates
        public static String findDayDifferenceBetweenDates_UIformat(String firstDate, String secondDate) throws Exception {
                            
            LocalDate startDate = LocalDate.of(Integer.valueOf(firstDate.substring(6)), Integer.valueOf(firstDate.substring(0, 2)) , Integer.valueOf(firstDate.substring(3, 5)) );
            LocalDate endDate = LocalDate.of(Integer.valueOf(secondDate.substring(6)), Integer.valueOf(secondDate.substring(0, 2)) , Integer.valueOf(secondDate.substring(3, 5)) );

            // Calculate the difference in days
            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
            System.out.println("Difference in days: " + daysBetween);
            return daysBetween + "";
        }

	    public  static Integer  firstDateOfPreviousMonthAndCalculateDaysFromTodayAndAddDaysIfProvidedInArgument( int daysToBeAdded ){
	 			Calendar cal = Calendar.getInstance();
	 	        Date currentDate = cal.getTime();

	 			cal.add(Calendar.MONTH, -1);
	 			cal.set(Calendar.DATE, 1);
	 			Date firstDateOfPreviousMonth = cal.getTime();

	 		    long diffInMillies = Math.abs(firstDateOfPreviousMonth.getTime() - currentDate.getTime());
	 		    Integer diff = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	 		 System.out.println(diff+ "------------------ this sis the difference");
	 			return diff+daysToBeAdded;
	 			
	 	 }

        
	    
	}