package newMMIS_Subsystems;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.SkipException;



    public class Managedcare_Common extends Login{
	public List<WebElement> list;
	public static int i;
	public static String memberId,gender,newDOB,newRace,newSSN,newLN,newAdd,lname,command,error,pEndDate,outputText,prov,sakPCC,sakCPPCC;
	public static String sql,memSql,memId,provId,errorMessage="",svcLoc,providerId,npi;
	public static String SelSql,col,DelSql,InsSql;
	public static String fname,lastName,ssn,casenum,dob,deathDate,adr1,city,zip,dep,aid,agency,sex,race,lang,eligDate,eligDateXmlFormat,eligStartReason,eligEndDate,sakCase,sakRecip,regionCode,officeCode,eligStartDate,familySize,incomeAmount,applDate,lineCode,aidCode,catCode,mailAddress,mailZipCode,mailCity,midInit,provId1,serloc;
	 static String filelocation_170="C:\\Users\\gandhian\\Desktop\\Testing\\TPT automation\\aug 16\\Run 14\\run14.xls";
		static ArrayList<String> excelspreaddata=new ArrayList<>();
		static int rowpointer=1;
		public static	String fileName1="C:\\Users\\agandhi20\\Desktop\\Testing\\TPT automation\\834_Feb_24_2020\\110128116A.834M.WEB.0135250060.051";
		public static int stsecount = 6;
		public static String st02Count = "";
		public static int stsecount_820 = 8;
		public static double rmr_820 = 0;
		public static int entCounter = 0;


	
	
	public static String getProvId(String memId,String sph,String endDate) throws SQLException {
		sql="select sak_prov,cde_service_loc from t_pmp_svc_loc where sak_pmp_ser_loc=(select SAK_PMP_SER_LOC from t_re_pmp_assign where sak_recip=(select sak_recip from t_re_base where id_medicaid='"+memId+"') and dte_end='"+endDate+"' and sak_pub_hlth='"+sph+"' and cde_status1<>'H' and rownum=1)";
		colNames.add("SAK_PROV");//0
		colNames.add("CDE_SERVICE_LOC");//1
		colValues=Common.executeQuery(sql,colNames);
		String sakprov=colValues.get(0);
		serloc=colValues.get(1);
		System.out.println("sak prov:"+colValues.get(0));
		String sql1="select ID_PROVIDER from T_PR_PROV where sak_prov='"+sakprov+"'";
		colNames.add("ID_PROVIDER");
		colValues=Common.executeQuery(sql1,colNames);
		provId1=colValues.get(0);
		System.out.println("Provider id in getprovid func: "+provId1);
		return provId=provId1+serloc;
		}
	
	 
	
	public static String get834(String Pid, String dt834, String tc, String recp ) throws Exception{

        //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /edi/data/"+unixDir+"/X12_OUT/WEB/"+Pid+".834D.WEB.*.* | grep '"+Common.monthUNIX(dt834)+" "+Common.dayUNIX(dt834)+"'";
		System.out.println("Command: "+command);
		error = "834 not found";
		String fileName = Common.connectUNIX(command, error);
		if (fileName.contains(Common.yearUNIX(dt834))) {
			command = "ls -ltr /edi/data/"+unixDir+"/X12_OUT/WEB/"+Pid+".834D.WEB.*.* | grep '"+Common.monthUNIX(dt834)+" "+Common.dayUNIX(dt834)+"  "+Common.yearUNIX(dt834)+"'";
			fileName = Common.connectUNIX(command, error);
		}
	
		fileName = fileName.substring(fileName.length()-60);
		System.out.println("File name is: "+fileName);
		log("834 filename for "+tc+" is: "+fileName);

		//Verify duplicate member data in file
		command = "cat "+fileName;
		//command = "grep "+inactMem+" "+fileName;
		error = "cannot open 834";
		outputText = Common.connectUNIX(command, error);
		
		
		outputText = outputText.replace("~", "~\r\n");
		//System.out.println("O/P text: "+outputText);
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
			//if (line.contains(recp)) {
				if (line.contains("REF*0F*"+recp)) {//Changed from recp to "REF*0F*"+recp as in some 834s same memid is repeating twice eg:27077TC
				targetText = pline;
			while( !(line.contains("INS")||line.contains("LE*2"))) {
			
					targetText = targetText+"\r\n"+line;
					line = br.readLine();
				}
				break;
			}
		}
		
		if (targetText.equals(""))
			Assert.assertTrue(false, "Member "+recp+" is not in this 834");
		//return outputText;
		return targetText;
    }
	
	public static void get834_tptTesting(String Pid, String dt834, String recp, String destination ) throws Exception{

        //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /edi/data/"+unixDir+"/X12_OUT/WEB/"+Pid+".834D.WEB.*.* | grep '"+Common.monthUNIX(dt834)+" "+Common.dayUNIX(dt834)+"'";
		System.out.println("Command: "+command);
		error = "834 not found";
		String fileName = Common.connectUNIX(command, error);
		if (fileName.contains(Common.yearUNIX(dt834))) {
			command = "ls -ltr /edi/data/"+unixDir+"/X12_OUT/WEB/"+Pid+".834D.WEB.*.* | grep '"+Common.monthUNIX(dt834)+" "+Common.dayUNIX(dt834)+"  "+Common.yearUNIX(dt834)+"'";
			fileName = Common.connectUNIX(command, error);
		}
	
		fileName = fileName.substring(fileName.length()-60);
		System.out.println("File name is: "+fileName);

		//Verify duplicate member data in file
		command = "cat "+fileName;
		//command = "grep "+inactMem+" "+fileName;
		error = "cannot open 834";
		outputText = Common.connectUNIX(command, error);
		
		
		outputText = outputText.replace("~", "~\r\n");
		//System.out.println("O/P text: "+outputText);
		//Store 834
		String fileName1=tptTesting.dir834.getPath()+"\\834_"+Pid+".txt";
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
			//if (line.contains(recp)) {
				if (line.contains("REF*0F*"+recp)) {//Changed from recp to "REF*0F*"+recp as in some 834s same memid is repeating twice eg:27077TC
				targetText = pline;
			while( !(line.contains("INS")||line.contains("LE*2"))) {
			
					targetText = targetText+"\r\n"+line;
					line = br.readLine();
				}
				break;
			}
		}
		
		if (targetText.equals(""))
			Assert.assertTrue(false, "Member "+recp+" is not in this 834");
		//return outputText;
		
		//Create prov dir if it does not exist already	
		 File provDir = new File(destination+"\\"+Pid); 
		 if (!provDir.exists()) { 
			 System.out.println("creating directory: " + provDir.getName()); 
			 provDir.mkdir(); 
		 }

		//Create member file
		String memFile=provDir.getPath()+"\\"+recp+".txt";
	    out = new PrintWriter(memFile);   
	    out.println(targetText);
	    out.close();
		
	}
	
//	public static String get834_tptTesting_monthly(String Pid, String dt834, String recp) throws Exception{
//
//        //Verify unix report
//		String command, error;
//
//		//Get Desired Filename
//		command = "ls -ltr /edi/data/"+unixDir+"/X12_OUT/WEB/"+Pid+".834D.WEB.*.* | grep '"+Common.monthUNIX(dt834)+" "+Common.dayUNIX(dt834)+"'";
//		System.out.println("Command: "+command);
//		error = "834 not found";
//		String fileName = Common.connectUNIX(command, error);
//		if (fileName.contains(Common.yearUNIX(dt834))) {
//			command = "ls -ltr /edi/data/"+unixDir+"/X12_OUT/WEB/"+Pid+".834D.WEB.*.* | grep '"+Common.monthUNIX(dt834)+" "+Common.dayUNIX(dt834)+"  "+Common.yearUNIX(dt834)+"'";
//			fileName = Common.connectUNIX(command, error);
//		}
//	
//		fileName = fileName.substring(fileName.length()-60);
//		System.out.println("File name is: "+fileName);
//
//		//Verify duplicate member data in file
//		command = "cat "+fileName;
//		//command = "grep "+inactMem+" "+fileName;
//		error = "cannot open 834";
//		outputText = Common.connectUNIX(command, error);
//		
//		
//		outputText = outputText.replace("~", "~\r\n");
//		//System.out.println("O/P text: "+outputText);
//		//Store 834
//		String fileName1=tptTesting.dir834.getPath()+"\\834_"+Pid+".txt";
//    	PrintWriter out = new PrintWriter(fileName1);   
//    	out.println(outputText);
//    	out.close();
//
//    	BufferedReader br = new BufferedReader(new FileReader(fileName1));
//		String pline="";
//    	String line = ""; 
//		String targetText = "";
//		
//		while (line != null)
//		{
//			pline = line;
//			line = br.readLine();
//			//if (line.contains(recp)) {
//				if (line.contains("REF*0F*"+recp)) {//Changed from recp to "REF*0F*"+recp as in some 834s same memid is repeating twice eg:27077TC
//				targetText = pline;
//			while( !(line.contains("INS")||line.contains("LE*2"))) {
//			
//					targetText = targetText+"\r\n"+line;
//					line = br.readLine();
//				}
//				break;
//			}
//		}
//		
//		if (targetText.equals(""))
//			Assert.assertTrue(false, "Member "+recp+" is not in this 834");
//		//return outputText;
//		if (!(targetText.contains("INS*Y*18*021")))
//			Assert.assertTrue(false, "Member "+recp+" was found but does not have txn code 021");
//		return targetText;
//		
//	}
	
	public static String get834_tptTesting_monthly(String Pid, String dt834, String recp) throws Exception{

//        //Verify unix report
//		String command, error;
//
//		//Get Desired Filename
//		command = "ls -ltr /edi/data/"+unixDir+"/X12_OUT/WEB/"+Pid+".834D.WEB.*.* | grep '"+Common.monthUNIX(dt834)+" "+Common.dayUNIX(dt834)+"'";
//		System.out.println("Command: "+command);
//		error = "834 not found";
//		String fileName = Common.connectUNIX(command, error);
//		if (fileName.contains(Common.yearUNIX(dt834))) {
//			command = "ls -ltr /edi/data/"+unixDir+"/X12_OUT/WEB/"+Pid+".834D.WEB.*.* | grep '"+Common.monthUNIX(dt834)+" "+Common.dayUNIX(dt834)+"  "+Common.yearUNIX(dt834)+"'";
//			fileName = Common.connectUNIX(command, error);
//		}
//	
//		fileName = fileName.substring(fileName.length()-60);
//		System.out.println("File name is: "+fileName);
//
//		//Verify duplicate member data in file
//		command = "cat "+fileName;
//		//command = "grep "+inactMem+" "+fileName;
//		error = "cannot open 834";
//		outputText = Common.connectUNIX(command, error);
//		
//		
//		outputText = outputText.replace("~", "~\r\n");
//		//System.out.println("O/P text: "+outputText);
//		//Store 834
//		String fileName1=tptTesting.dir834.getPath()+"\\834_"+Pid+".txt";
//    	PrintWriter out = new PrintWriter(fileName1);   
//    	out.println(outputText);
//    	out.close();
//    	
		String fileName1="C:\\Users\\gandhian\\Desktop\\Testing\\TPT automation\\aug 15\\Anshul\\834 Member extract for prov 110031899B\\110031899B.834D.WEB.1223560019.227.txt";


    	BufferedReader br = new BufferedReader(new FileReader(fileName1));
		String pline="";
    	String line = ""; 
		String targetText = "";
		
		while (line != null)
		{
			pline = line;
			line = br.readLine();
			//if (line.contains(recp)) {
				if (line.contains("REF*0F*"+recp)) {//Changed from recp to "REF*0F*"+recp as in some 834s same memid is repeating twice eg:27077TC
				targetText = pline;
			while( !(line.contains("INS")||line.contains("LE*2"))) {
			
					targetText = targetText+"\r\n"+line;
					line = br.readLine();
				}
				break;
			}
		}
		
		if (targetText.equals(""))
			Assert.assertTrue(false, "Member "+recp+" is not in this 834");
		//return outputText;
		if (!(targetText.contains("INS*Y*18*021")))
			Assert.assertTrue(false, "Member "+recp+" was found but does not have txn code 021");
		return targetText;
		
	}
	
	public static String get834_tptTesting_monthlyFull(String Pid, String dt834, String recp, String seq) throws Exception{

//		String fileName1="C:\\Users\\agandhi20\\Desktop\\Testing\\TPT automation\\aug 21\\110031449H.834M.WEB.2349380011.234.txt";
		int stsecount1=0;

  	BufferedReader br = new BufferedReader(new FileReader(fileName1));
		String pline="";
  	String line = ""; 
		String targetText = "";
		log(seq+": Processing Member: "+recp);

		while (line != null)
		{
			pline = line;
			line = br.readLine();
			if(line.contains("IEA*1*")) //revisit, and handle null pointer exception for below break
				break;
			//if (line.contains(recp)) {
				if (line.contains("REF*0F*"+recp)) {//Changed from recp to "REF*0F*"+recp as in some 834s same memid is repeating twice eg:27077TC
					log("Member found. Writing to custom 834.");
					targetText = pline;
					stsecount1++;
					while( !(line.contains("INS")||line.contains("LE*2"))) {
			
					targetText = targetText+"\r\n"+line;
					line = br.readLine();
					stsecount1++;
				}
				break;
			}
		}
		
		if (targetText.equals("")) {
			log("Member is not in this 834");
			Assert.assertTrue(false, "Member "+recp+" is not in this 834");
		}
		
		//Add LE*2 in the end also
		targetText = targetText+"\r\n"+line;
		stsecount1++;
		
		//return outputText;
//		if (!(targetText.contains("INS*Y*18*001")) && !(targetText.contains("INS*Y*18*024"))) //change to 021 for new transactions, 001 for change transactions, 024 for disenrollments, 030 for monthly
		if (!(targetText.contains("INS*Y*18*030")))
			Assert.assertTrue(false, "Member "+recp+" was found but does not have txn code 024");
		stsecount=stsecount+stsecount1;
		return targetText;
		
	}
	
	public static String get820_tptTesting_monthlyFull(String Pid, String dt820, String recp, String seq) throws Exception{

//		String fileName1="C:\\Users\\agandhi20\\Desktop\\Testing\\TPT automation\\820\\feb_18_2020\\110020754C.820.WEB.1936330001.044.PACE.TXT";
		int stsecount1=0;
		double rmr=0;
		BufferedReader br = new BufferedReader(new FileReader(fileName1));
		String line = ""; 
		String targetText = "";
		String ent01="";
		log(seq+": Precessing Member: "+recp);
		while (line != null)
		{
			line = br.readLine();
			if(line.contains("IEA*1")) //revisit, and handle null pointer exception for below break
				break;
			if (line.contains(recp)) {
				Assert.assertTrue(line.contains("ENT*"), "'ENT' was not present, or member was not in the ENT segment for Member "+recp);
				//Get ENT01
				ent01 = line.substring(4, line.indexOf("*", line.indexOf("ENT*")+4));
				
				//Replace ENT01 with entCounter
				entCounter++;
				line=line.replace("ENT*"+ent01+"*", "ENT*"+entCounter+"*");
				log("Replaced ENT*"+ent01+"* with ENT*"+entCounter+"*");
				targetText = line;
				stsecount1++;
				line = br.readLine();
				Assert.assertTrue(line.contains(recp), "Member "+recp+" was not the same in the ENT and the following INS segment");
				while( !(line.contains("ENT*")||line.contains("SE*"))) {
					
					targetText = targetText+"\r\n"+line;
					stsecount1++;
					
					if (line.contains("RMR*")) {
						rmr= Double.parseDouble(line.substring(line.lastIndexOf('*')+1, line.indexOf('~')));
						log("RMR: "+rmr+"\r\n");
					}

					line = br.readLine();
				}
			break;
			}
		}

		
		if (targetText.equals("")){
			log("Member "+recp+" is not in this 820 for provider "+Pid);
			Assert.assertTrue(false, "Member "+recp+" is not in this 820 for provider "+Pid);
		}
		//return outputText;
//		if (!(targetText.contains("INS*Y*18*001")) && !(targetText.contains("INS*Y*18*024"))) //change to 021 for new transactions, 001 for change transactions, 024 for disenrollments, 030 for monthly
//		if (!(targetText.contains("INS*Y*18*030")))
//			Assert.assertTrue(false, "Member "+recp+" was found but does not have txn code 024");
		stsecount_820=stsecount_820+stsecount1;
		rmr_820=rmr_820+rmr;
//		System.out.println("RMR: "+rmr+" rmr total: "+rmr_820);
		System.out.println(rmr);
		return targetText;
		
	}
//	catch(NullPointerException npe){
//		
//	}
	
	public static String get834_tptTesting_monthlyDisenroll(String Pid, String dt834, String recp) throws Exception{

//		String fileName1="C:\\Users\\gandhian\\Desktop\\Testing\\TPT automation\\aug 15\\Anshul\\834 Member extract for prov 110031899B\\110031899B.834D.WEB.1223560019.227.txt";
		int stsecount1=0;

  	BufferedReader br = new BufferedReader(new FileReader(fileName1));
		String pline="";
  	String line = ""; 
		String targetText = "";
		
		while (line != null)
		{
			pline = line;
			line = br.readLine();
			//if (line.contains(recp)) {
				if (line.contains("REF*0F*"+recp)) {//Changed from recp to "REF*0F*"+recp as in some 834s same memid is repeating twice eg:27077TC
				targetText = pline;
				stsecount1++;
			while( !(line.contains("INS")) ) {
			
					targetText = targetText+"\r\n"+line;
					line = br.readLine();
					stsecount1++;
				}
				break;
			}
		}
		
//		//Add LE*2 in the end also
//		targetText = targetText+"\r\n"+line;
//		stsecount1++;
		
		if (targetText.equals(""))
			Assert.assertTrue(false, "Member "+recp+" is not in this 834");
		//return outputText;
//		if (!(targetText.contains("INS*Y*18*001")) && !(targetText.contains("INS*Y*18*024"))) //change to 021 for new transactions, 001 for change transactions, 024 for disenrollments 0||1 1||0 1||1 0||0
		if (!(targetText.contains("INS*Y*18*024")))
			Assert.assertTrue(false, "Member "+recp+" was found but does not have txn code 024");
		stsecount=stsecount+stsecount1;
		return targetText;
		
	}
	
	public static String get834_addHeader() throws Exception{

//		String fileName1="C:\\Users\\gandhian\\Desktop\\Testing\\TPT automation\\aug 15\\Anshul\\834 Member extract for prov 110031899B\\110031899B.834D.WEB.1223560019.227.txt";


  	BufferedReader br = new BufferedReader(new FileReader(fileName1));
  	String line = ""; 
		String targetText = "";
		
		for(int m=1;m<8;m++){
			line = br.readLine();
			if (m==7)
				targetText = targetText+line;
			else
				targetText = targetText+line+"\r\n";

		}
		
		//Get original ST02 count		
		st02Count=targetText.substring(targetText.indexOf("ST*834*")+7, targetText.indexOf("*", targetText.indexOf("ST*834*")+8));
		System.out.println("ST02 " + st02Count);

		return targetText;
		
	}
	
	public static String get820_addHeader() throws Exception{

//		String fileName1="C:\\Users\\gandhian\\Desktop\\Testing\\TPT automation\\aug 15\\Anshul\\834 Member extract for prov 110031899B\\110031899B.834D.WEB.1223560019.227.txt";


  	BufferedReader br = new BufferedReader(new FileReader(fileName1));
  	String line = ""; 
		String targetText = "";
		
		for(int m=1;m<10;m++){
			line = br.readLine();
			if (m==9)
				targetText = targetText+line;
			else
				targetText = targetText+line+"\r\n";

		}
		
		//Get original ST02 count		
		st02Count=targetText.substring(targetText.indexOf("ST*820*")+7, targetText.indexOf("*", targetText.indexOf("ST*820*")+8));
		System.out.println("ST02 " + st02Count);
		

		return targetText;
		
	}
	
	public static String get834_addFooter() throws Exception{

//		String fileName1="C:\\Users\\gandhian\\Desktop\\Testing\\TPT automation\\aug 15\\Anshul\\834 Member extract for prov 110031899B\\110031899B.834D.WEB.1223560019.227.txt";

  	BufferedReader br = new BufferedReader(new FileReader(fileName1));
  	int lines = 0;
  	
  	while (br.readLine() != null)
  		lines++;
  	br.close();
  	
  	br = new BufferedReader(new FileReader(fileName1));
  	String line = ""; 
		String targetText = "";
		
		for(int m=1;m<lines-2;m++){
			line = br.readLine();
		}
		
		for(int p=1;p<4;p++){
			line = br.readLine();
//			targetText = targetText+"\r\n"+line;
			if (p==3)
				targetText = targetText+line;
			else
				targetText = targetText+line+"\r\n";
		}

		
		//Get original SE01 count
		String seCount=targetText.substring(3, targetText.indexOf("*", targetText.indexOf("*")+1));
		System.out.println("SE01 "+seCount);
		
		//Replace SE01 count
		targetText = targetText.replace(seCount, Integer.toString(stsecount));
		
		//Get original GE count		
		String geCount=targetText.substring(targetText.indexOf("GE*")+3, targetText.indexOf("*", targetText.indexOf("GE*")+4));
		System.out.println("GE01 " + geCount);
//		System.out.println(stsecount);

		
		//Replace GE count
		targetText = targetText.replace("*"+geCount+"*", "*1*");
		
		
		//Get original SE02 count		
		String se02Count=targetText.substring(targetText.indexOf("*", targetText.indexOf("*")+1), targetText.indexOf("~"));
		System.out.println("SE02 " + se02Count);
//		System.out.println(stsecount);
		
		//Replace se02count with st02Count
		targetText = targetText.replace(se02Count, "*"+st02Count);
		
		return targetText;
		
	}
	
	public static String get820_addFooter() throws Exception{

//		String fileName1="C:\\Users\\gandhian\\Desktop\\Testing\\TPT automation\\aug 15\\Anshul\\834 Member extract for prov 110031899B\\110031899B.834D.WEB.1223560019.227.txt";

  	BufferedReader br = new BufferedReader(new FileReader(fileName1));
  	int lines = 0;
  	
  	while (br.readLine() != null)
  		lines++;
  	br.close();
  	
  	br = new BufferedReader(new FileReader(fileName1));
  	String line = ""; 
		String targetText = "";
		
		for(int m=1;m<lines-2;m++){
			line = br.readLine();
		}
		
		for(int p=1;p<4;p++){
			line = br.readLine();
//			targetText = targetText+"\r\n"+line;
			if (p==3)
				targetText = targetText+line;
			else
				targetText = targetText+line+"\r\n";
		}
		
		//Get original SE01 count
		String seCount=targetText.substring(3, targetText.indexOf("*", targetText.indexOf("*")+1));
		System.out.println("SE01 "+seCount);

		
		//Replace SE01 count
		targetText = targetText.replace(seCount, Integer.toString(stsecount_820));
		
		//Get original GE01 count		
		String geCount=targetText.substring(targetText.indexOf("GE*")+3, targetText.indexOf("*", targetText.indexOf("GE*")+4));
		System.out.println("GE01 " + geCount);

		
		//Replace GE01 count
		targetText = targetText.replace("GE*"+geCount+"*", "GE*1*");
		
		
		//Get original SE02 count		
		String se02Count=targetText.substring(targetText.indexOf("*", targetText.indexOf("*")+1), targetText.indexOf("~"));
		System.out.println("SE02 " + se02Count);
		
		//Replace se02count with st02Count
		targetText = targetText.replace(se02Count, "*"+st02Count);
		
		
		return targetText;
		
	}
	
	
	 public static String memberQuery(String sph) throws Exception {
//	 sql="select distinct b.id_medicaid, b.sak_recip,e.sak_cde_aid from t_re_base b,t_re_pmp_assign asg1, t_pmp_svc_loc loc, t_re_other_address o,t_re_aid_elig e where  substr(asg1.dte_end,1,6) > substr(to_char(sysdate,'YYYYMMDD'),1,6) " +
//		 "and asg1.sak_pub_hlth="+sph+" and asg1.cde_status1 <> 'H' and b.cde_agency='MHO' and e.dte_end='22991231' and asg1.dte_end=e.dte_end  and b.cde_agency=e.cde_agency and b.sak_recip= e.sak_recip and b.num_ssn<>' ' and b.sak_recip= asg1.sak_recip and loc.sak_pmp_ser_loc =asg1.sak_pmp_ser_loc and ind_active <> 'N' and dte_death='0' and asg1.dte_effective<to_char(sysdate, 'YYYYMMDD') and not exists( select 1 from t_re_loc loc1 where loc1.sak_recip=b.sak_recip and loc1.DTE_discharge> to_char(sysdate,'YYYYMMDD')) " +
//		 "and o.sak_recip = asg1.sak_recip and o.adr_zip_code in (02122,02123,02124,02125,02126,02127,02129,02130,02131,02132,02133,02134,02135,02136,02137,02147,02153,02163,02199,02201,02203,02208,02209,02210,02215,02222,02241,02445,02446,02447,02196,02228,02284,02101,02102,02103,02104,02105,02106,02107,02108,02109,02110,02111,02112,02113,02114,02115,02116,02117,02118,02119,02120,02121,02283,02211)" +
//		 "and b.sak_recip > dbms_random.value * 6300000 and rownum<2";
	 sql="select distinct b.id_medicaid,e.sak_cde_aid from t_re_base b,t_re_pmp_assign asg1, t_pmp_svc_loc loc, t_re_other_address o,t_re_aid_elig e where  substr(asg1.dte_end,1,6) > substr(to_char(sysdate,'YYYYMMDD'),1,6) and asg1.sak_pub_hlth="+sph+" and asg1.dte_end='22991231' and asg1.dte_end=e.dte_end and b.sak_recip=e.sak_recip and b.cde_agency=e.cde_agency and asg1.cde_status1 <> 'H' and b.sak_recip= asg1.sak_recip and loc.sak_pmp_ser_loc =asg1.sak_pmp_ser_loc and ind_active <> 'N' and dte_death='0' and asg1.dte_effective<to_char(sysdate, 'YYYYMMDD') and not exists( select 1 from t_re_loc loc1 where loc1.sak_recip=b.sak_recip and loc1.DTE_discharge> to_char(sysdate,'YYYYMMDD')) " +
		 "and o.sak_recip = asg1.sak_recip and b.num_ssn<>' ' and b.cde_agency='MHO' and e.dte_end='22991231' and o.adr_zip_code in (02122,02123,02124,02125,02126,02127,02129,02130,02131,02132,02133,02134,02135,02136,02137,02147,02153,02163,02199,02201,02203,02208,02209,02210,02215,02222,02241,02445,02446,02447,02196,02228,02284,02101,02102,02103,02104,02105,02106,02107,02108,02109,02110,02111,02112,02113,02114,02115,02116,02117,02118,02119,02120,02121,02283,02211) and not exists (select 1 from t_re_pmp_assign where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_effective >asg1.dte_effective and sak_pub_hlth="+sph+") " +
		 "and b.sak_recip > dbms_random.value * 6300000 and rownum<2";
	 return sql;
//	    colNames.add("ID_MEDICAID");
//		colValues=Common.executeQuery(sql,colNames);
//		return memId=colValues.get(0);
		}  
	
	
	public static void insertDataSql(String tcNo,String id,String des,String date) throws SQLException{
		SelSql="select * from R_DAY2 where tc = '"+tcNo+"'";
		col="ID";
		DelSql="delete from R_DAY2 where tc = '"+tcNo+"'";  
		InsSql="insert into  R_DAY2 values ('"+tcNo+"', '"+id+"', '"+des+"', '"+date+"')";
		Common.insertData(SelSql, col, DelSql, InsSql);	
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
	

	   //Get first date of next month
		public static String firstDateOfNextMonth(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH,1);
		cal.set(Calendar.DATE, 1);
		Date firstDateOfNexttMonth = cal.getTime();
		SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy");
		return (sdf.format(firstDateOfNexttMonth).toString());
		}
		
		public static void checkBasePanel(String tcNo,String mcProg,String endDate) throws Exception{
			memSql="select * from R_DAY2 where TC='"+tcNo+"'";
			colNames.add("ID");
			colNames.add("DES");
			colValues=Common.executeQuery1(memSql,colNames);
			memId=colValues.get(0);
			sakRecip=colValues.get(1);
			driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
			driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
			driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
			driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memId);
		    driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
			driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
			driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_RePmpAssignSu")).click();//Clicking SU panel
			driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:RePmpAssignSuBean_ColHeader_endDate")).click();//Sorting on end date twice
			driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:RePmpAssignSuBean_ColHeader_endDate")).click();
			int rowNum=driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr")).size();
		    for(i=1;i<=rowNum;i++){
			if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[3]")).getText().trim().contains(mcProg))
				{
				if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[5]")).getText().trim().equals(endDate)){
				break;
					  }
					}
				 }
			if(i==(rowNum+1))
			  {
			  throw new SkipException("The MC Program didn't ended with expected date");
			  } 	
				
			}
			
			
			
			
			public static String getNPI(String tcNo) throws SQLException {
			sql="select * from r_day2 where TC='"+tcNo+"'";
			colNames.add("ID");
			colValues=Common.executeQuery1(sql,colNames);
			provId=colValues.get(0);
			System.out.println("PCC provider id:"+provId);
			String provider=provId.substring(0, provId.length()-1),
			         svcloc=provId.substring(provId.length()-1);
			System.out.println("PCC provider id:"+provider);
			System.out.println("PCC provider id:"+svcloc);
			sql="select * from t_pr_appln where id_provider='"+provider+"' and cde_service_loc='"+svcloc+"'";
			colNames.add("ID_NPI");//0
			colValues=Common.executeQuery(sql,colNames);
			return npi=colValues.get(0);
			}
			
			
			public static String getDataRday2(String tcNo) throws SQLException{
			sql="select * from r_day2 where TC='"+tcNo+"'";	
			colNames.add("ID");//0
			colNames.add("DES");//1
			colValues=Common.executeQuery1(sql,colNames);
			String data1=colValues.get(0);
			return data1;
			}
			
		
	
			public static String getDOB(String memId) throws SQLException{
			    sql="select DTE_BIRTH from t_re_base where id_medicaid='"+memId+"'";
			    colNames.add("DTE_BIRTH");
			    colValues=Common.executeQuery(sql,colNames);
			    return colValues.get(0);
			    }
				
				
//				public static void checkDemographs(String tcNo) throws SQLException{
//			    String targetText=ManagedcareDay2.targetText;
//				Assert.assertTrue(targetText.contains("INS*Y*18*001"),"Transaction type:01 is not found in MSTD output 834 file");
//				Assert.assertTrue(targetText.contains("REF*0F*"+getDataRday2(tcNo)),"Member Id is not found in MSTD output 834 file");
//			    Assert.assertTrue(targetText.contains("N3*"+getDataRday2(tcNo+"NADD")),"New Address segment is not found in MSTD output 834 file");
//			    Assert.assertTrue(targetText.contains("N3*"+getDataRday2(tcNo+"OADD")),"Old Address segment is not found in MSTD output 834 file");
//			    Assert.assertTrue(targetText.contains("NM1*74*1*"+getDataRday2(tcNo+"NLN")+"*"+getDataRday2(tcNo+"FN")),"NM1 corrected data segment is not as expected in MSTD output 834 file");
//			    Assert.assertTrue(targetText.contains("DMG*D8*"+getDOB(getDataRday2(tcNo))+"*"+getDataRday2(tcNo+"NSex")),"DMG correct data segment is not as expected in MSTD output 834 file");
//			    }
	
				
				public static String getOrgName(String memId) throws SQLException{
					String sql1="select NAM_BUS from t_tpl_carrier c,t_re_base b,t_tpl_resource t where b.sak_recip=(select sak_recip from t_re_base where id_medicaid='"+memId+"') and b.sak_recip=t.sak_recip and t.sak_carrier=c.sak_carrier and rownum<2";
					colNames.add("NAM_BUS");
					colValues=Common.executeQuery(sql1, colNames);
//					if (colValues.get(0).equals("null"))
//					    throw new SkipException("Skipping this test because there was no org name returned from table");
					String orgName=colValues.get(0);
					return orgName;
				    }
					
					
					public static String getEffDate(String memId) throws Exception{
					String sql1="select dte_effective from t_coverage_xref where sak_tpl_resource=(select sak_tpl_resource from t_tpl_resource where sak_recip=(select sak_recip from t_re_base where id_medicaid='"+memId+"')) and rownum<2";
					colNames.add("DTE_EFFECTIVE");
					colValues=Common.executeQuery(sql1, colNames);
					if (colValues.get(0).equals("null"))
						throw new SkipException("Skipping this test because there was no effective date returned from table");
					return colValues.get(0);
					}
					
					

					//Convert date format to integer
					public static String convertDatetoInt(String date) throws Exception {
						SimpleDateFormat sdf1= new SimpleDateFormat("yyyyMMdd");
						SimpleDateFormat sdf2= new SimpleDateFormat("yyyy-MM-dd");
						Date dbDate=sdf2.parse(date);
						return sdf1.format(dbDate).toString();
					    }
					
					public static void getAssgnSource(String memId) throws Exception{
						String sql="select * from t_re_pmp_assign where sak_recip=(select sak_recip from t_re_base where id_medicaid='"+memId+"') and rownum<2";
						colNames.add("SAK_PUB_HLTH");
						colNames.add("SAK_MC_ENT_ADD");
						colValues=Common.executeQuery(sql,colNames);
						String sph=colValues.get(0);
						String assgnSource=colValues.get(1);
						log("For Member Id: "+memId+ "sakPubHlth= "+sph+" and Assignment Source is: "+assgnSource);
						
						}
					

					public static String tcXML(String otherId,String fName,String dob,String lineCode) throws Exception{
				    ssn="1"+Common.generateRandomTaxID().substring(0,8);	
				    eligDateXmlFormat=Member.fileSysdateCustom(-30);
				    applDate=Member.fileSysdateCustom(-10);
					String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<EligibilityRequest xmlns=\"http://xmlns.hhs.ma.gov/HHS/serviceobjects/versions/2.9/EligibilityServices\" "+
							    "\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<transactionsource\nid_medicaid=\" \"\nid_other=\""+otherId+"\"\nid_source=\"MHO\"" +
							    "\n/>\n<demographic\nnam_last=\"WHITE\"\nnam_first=\""+fName+"\"\nnum_primary_ssn=\""+ssn+"\"\nres_adr_street_1=\"10 MAIN ST\"\nres_adr_city=\"S.BOSTON\"\n" +
							    "res_adr_state=\"MA\"\nres_adr_zip_code=\"02122\"\ncde_lang_written=\"ENG\"\ncde_sex=\"M\"\ncde_race=\"UNKNOW\"\ncde_citizen=\"C\"\ndte_birth=\""+dob+"\"" +
							    "\n/>\n<case\nnum_case=\"011747421\"\ncde_case_status=\"1\"\nhoh_nam_first=\""+fName+"\"\nhoh_nam_last=\"MCCASE\"\nhoh_nam_init=\"\"\n/>\n<eligibility\ncde_line=\""+lineCode+"\"\ndte_begin_elig=\""+eligDateXmlFormat+"\"" +
							    "\ncde_elig_status=\"1\"\ncde_cat=\"90\"\ncde_open_reason=\"01\"\nfamily_size=\"3\"\ndte_appl=\""+applDate+"\"\ncde_region=\"06\"\ncde_office=\"520\"\namt_gross_income=\"0.00\"\n/>\n</EligibilityRequest>");
					
					String Mid=Member.getCustomMemebr(xml);
					//Assert.assertTrue(Member.result_type.equals("S"),"The result type is not Successfull:"+errorMessage);
					return Mid; 
					}
					
	public static void newBorn(String tcNo,String sql,String reason) throws Exception{
    colNames.add("NUM_CASE");//0
	colNames.add("ID_MEDICAID");//1
	colValues=Common.executeQuery(sql,colNames);
	String casenum=colValues.get(0);
	String motherMemId=colValues.get(1);
	log("Mother MemberId: "+motherMemId+", CaseNum: "+casenum);
	Managedcare_Common.insertDataSql(tcNo+"Mom",motherMemId,"Mother Member Id",Common.convertSysdate());
	Member.otherid = Common.generateRandomTaxID();
	if(tcNo.equals("30201")){
    dob =Member.fileSysdateCustom(-364); 
    System.out.println("DOB: "+dob);
	}
	else{
	dob =Member.fileSysdateCustom(-30);
	}
	fname=Common.generateRandomName();
	lname=Common.generateRandomName();
	ssn="1"+Common.generateRandomTaxID().substring(0,8);
	String addSql="select * from T_RE_OTHER_ADDRESS where sak_recip=(select sak_recip from t_re_base where id_medicaid='"+motherMemId+"') and cde_addr_usage='MR'";
	colNames.add("ADDRESS_LINE_1");//0
	colNames.add("ADR_CITY");//1
	colNames.add("ADR_STATE");//2
	colNames.add("ADR_ZIP_CODE");//3
	colValues=Common.executeQuery(addSql, colNames);
	String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><EligibilityRequest\nxmlns=\"http://xmlns.hhs.ma.gov/HHS/serviceobjects/versions/2.9/EligibilityServices\"\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<transactionsource id_medicaid=\" \" "+
				"id_other=\""+Member.otherid+"\" id_source=\"MHO\"/>\n<demographic cde_citizen=\"C\" cde_lang_written=\"ENG\" cde_race=\"WHITE\" cde_sex=\"F\" dte_birth=\""+dob+"\" nam_first=\""+fname+"\" nam_last=\"BURNS\" nam_mid_init=\" \" num_primary_ssn=\""+ssn+"\" res_adr_city=\""+colValues.get(1)+"\" res_adr_state=\""+colValues.get(2)+"\" res_adr_street_1=\""+colValues.get(0)+"\" res_adr_street_2=\" \" res_adr_zip_code=\""+colValues.get(3)+"\"/>"+
				"<case cde_case_status=\"1\" hoh_nam_first=\""+fname+"\" hoh_nam_init=\" \" hoh_nam_last=\"BURNS\" num_case=\""+casenum+"\"/>\n"+
				"<eligibility amt_gross_income=\"14294.22\" cde_cat=\"02\" cde_elig_status=\"1\" cde_line=\"02\" cde_office=\"600\" cde_open_reason=\"01\" cde_region=\"01\" dte_appl=\""+dob+"\" dte_begin_elig=\""+dob+"\" family_size=\"06\" />\n</EligibilityRequest>");
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
	String Mid=Member.getCustomMemebr(xml);
	log("Newborn MemberId: "+Mid+", Case num: "+getCaseNum(Mid));
	Managedcare_Common.insertDataSql(tcNo+"Nborn",Mid,"Newborn Member Id",Common.convertSysdate());
	//Assert.assertTrue(Member.result_type.equals("S"),"The result type is not Successfull:"+errorMessage);
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ManagedCare")).click();	
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ManagedCare")).click();
//	driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignNavigatorPanel:RePmpAssignNavigator:ITM_n202")).click();//Potential MC member
	driver.findElement(By.linkText("Potential MC Members")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:ReMcRecipPanel:ReMcRecipSearchResultBean_CriteriaPanel:ReMcRecipSearchResultDataPanel_MedicaidID")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:ReMcRecipPanel:ReMcRecipSearchResultBean_CriteriaPanel:ReMcRecipSearchResultDataPanel_MedicaidID")).sendKeys(Mid);
	driver.findElement(By.id("MMISForm:MMISBodyContent:ReMcRecipPanel:ReMcRecipSearchResultBean_CriteriaPanel:SEARCH")).click();
	Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ReMcRecipPanel:MCRecSearchResults:tbody_element']/tr/td[6]/span")).getText().trim().equals(reason), "The reason for the New Member Id created is not "+reason+" in Potential MC members panel");
	Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ReMcRecipPanel:MCRecSearchResults:tbody_element']/tr/td[7]/span")).getText().trim().equals(Common.convertSysdate()), "The Auto-Assignment Run Date is not Today's date in Potential MC members panel");	
	}
	
	
	public static String verifyReport(String data,String fileName) throws Exception{
	command = "grep "+data+" "+fileName;
	error = "The selected file "+fileName+" is not correct/ or it is empty/ or it does not have the desired "+data;
	System.out.println("output: "+Common.connectUNIX(command, error));
	log("\r\n"+Common.connectUNIX(command, error));	
	return Common.connectUNIX(command, error);
	}
								
							
		
		

	public static String  xmlData(String memId,String agency,String sakCdeAid,String endDate) throws SQLException{
    String memSql="select * from t_re_base where ID_MEDICAID='"+memId+"'";
	System.out.println("memid: "+memId);
	colNames.add("SAK_RECIP");//0
    colNames.add("ADR_STREET_1");//1
	colNames.add("ADR_CITY");//2
	colNames.add("ADR_ZIP_CODE");//3
	colNames.add("NUM_SSN");//4
	colNames.add("DTE_BIRTH");//5
	colNames.add("CDE_SEX");//6
	colNames.add("CDE_RACE");//7
	colNames.add("CDE_LANG_WRITTEN");//8
	colValues=Common.executeQuery(memSql,colNames);
    sakRecip=colValues.get(0);
	adr1=colValues.get(1);		
	city=colValues.get(2);
	zip=colValues.get(3);
	ssn=colValues.get(4);
	dob=colValues.get(5).substring(0,4)+"-"+colValues.get(5).substring(4,6) +"-"+colValues.get(5).substring(6,8);
	System.out.println("DOB: "+dob);
	sex=colValues.get(6);
	race=colValues.get(7);
	lang=colValues.get(8);
	String otherIdSql="select * from t_re_other_id where SAK_RECIP='"+sakRecip+"' and cde_agency='"+agency+"'"; 
	colNames.add("AGENCY_ID");//0
	colNames.add("EFFECTIVE_DATE");//1
	colValues=Common.executeQuery(otherIdSql,colNames);		
	Member.otherid=colValues.get(0);
	eligDate=colValues.get(1);
	eligDateXmlFormat=eligDate.substring(0,4)+"-"+eligDate.substring(4,6) +"-"+eligDate.substring(6,8);
	System.out.println("Eligibility start date xml format is:"+eligDateXmlFormat);
	String caseSql="select * from t_re_case_xref where sak_recip='"+sakRecip+"' and cde_agency='"+agency+"' and rownum<2"; 
	colNames.add("SAK_CASE");//0
	colNames.add("CDE_REGION");//1
	colNames.add("CDE_OFFICE");//2
	colNames.add("QTY_FAMILY_SIZE");//3
	colNames.add("AMT_GROSS_INCOME");//4
	colNames.add("DTE_APPL");//5
	colNames.add("DEP_NUM");//6
	colValues=Common.executeQuery(caseSql,colNames);
	sakCase=colValues.get(0);
	regionCode=colValues.get(1);
	officeCode=colValues.get(2);
	familySize=colValues.get(3);
	incomeAmount=colValues.get(4);
	applDate=colValues.get(5).substring(0,4)+"-"+colValues.get(5).substring(4,6) +"-"+colValues.get(5).substring(6,8);
	lineCode=colValues.get(6);
	System.out.println("Appl Date: "+applDate);
	String caseNumSql="select * from t_re_case where sak_case='"+sakCase+"'";
	colNames.add("NUM_CASE");//0
	colNames.add("NAM_LAST");//1
	colNames.add("NAM_FIRST");//2
	colNames.add("NAM_MID_INIT");//3
	colValues=Common.executeQuery(caseNumSql,colNames);
	casenum=colValues.get(0);
	lastName=colValues.get(1);
	fname=colValues.get(2);
	midInit=colValues.get(3);
	String eligSql="select a.CDE_AID_CATEGORY,es.CDE_START,e.DTE_EFFECTIVE,e.DTE_END from t_re_aid_elig e,t_cde_aid a, T_RE_ELIG_START es where a.sak_cde_aid="+sakCdeAid+" and e.sak_cde_aid=a.sak_cde_aid and e.sak_recip='"+sakRecip+"' " +
			       "and e.cde_agency='"+agency+"' and e.dte_end='"+endDate+"' and es.sak_elig_start = e.sak_elig_start and rownum=1";
	colNames.add("CDE_AID_CATEGORY");//0
	colNames.add("CDE_START");//1
	colNames.add("DTE_EFFECTIVE");//2
	colNames.add("DTE_END");//3
	colValues=Common.executeQuery(eligSql,colNames);
	catCode=colValues.get(0);
	eligStartReason=colValues.get(1);
	eligStartDate=colValues.get(2);
	eligEndDate=colValues.get(3).substring(0,4)+"-"+colValues.get(3).substring(4,6) +"-"+colValues.get(3).substring(6,8);
	deathDate=Member.fileSysdateCustom(-32);
	System.out.println("Death date: "+deathDate);
	String addressSql="select * from t_re_other_address where sak_recip='"+sakRecip+"' and cde_addr_usage='MM'";
	colNames.add("ADDRESS_LINE_1");//0
	colNames.add("ADR_CITY");//1
	colNames.add("ADR_ZIP_CODE");//2
	colValues=Common.executeQuery(addressSql,colNames);
	mailAddress=colValues.get(0);
	mailCity=colValues.get(1);
	mailZipCode=colValues.get(2);
	return memId;
    }
	
	public static String  xmlData_withsakAidElig(String memId,String agency,String sakCdeAid,String endDate, String sakAidElig) throws SQLException{
	    String memSql="select * from t_re_base where ID_MEDICAID='"+memId+"'";
		System.out.println("memid: "+memId);
		colNames.add("SAK_RECIP");//0
	    colNames.add("ADR_STREET_1");//1
		colNames.add("ADR_CITY");//2
		colNames.add("ADR_ZIP_CODE");//3
		colNames.add("NUM_SSN");//4
		colNames.add("DTE_BIRTH");//5
		colNames.add("CDE_SEX");//6
		colNames.add("CDE_RACE");//7
		colNames.add("CDE_LANG_WRITTEN");//8
		colValues=Common.executeQuery(memSql,colNames);
	    sakRecip=colValues.get(0);
		adr1=colValues.get(1);		
		city=colValues.get(2);
		zip=colValues.get(3);
		ssn=colValues.get(4);
		dob=colValues.get(5).substring(0,4)+"-"+colValues.get(5).substring(4,6) +"-"+colValues.get(5).substring(6,8);
		System.out.println("DOB: "+dob);
		sex=colValues.get(6);
		race=colValues.get(7);
		lang=colValues.get(8);
		String otherIdSql="select * from t_re_other_id where SAK_RECIP='"+sakRecip+"' and cde_agency='"+agency+"'"; 
		colNames.add("AGENCY_ID");//0
		colNames.add("EFFECTIVE_DATE");//1
		colValues=Common.executeQuery(otherIdSql,colNames);		
		Member.otherid=colValues.get(0);
		eligDate=colValues.get(1);
		eligDateXmlFormat=eligDate.substring(0,4)+"-"+eligDate.substring(4,6) +"-"+eligDate.substring(6,8);
		System.out.println("Eligibility start date xml format is:"+eligDateXmlFormat);
		String caseSql="select * from t_re_case_xref where sak_recip='"+sakRecip+"' and cde_agency='"+agency+"' and sak_aid_elig='"+sakAidElig+"' and rownum<2"; //AG- Added sakAidElig to get the correct case number for the aid cat in question
		colNames.add("SAK_CASE");//0
		colNames.add("CDE_REGION");//1
		colNames.add("CDE_OFFICE");//2
		colNames.add("QTY_FAMILY_SIZE");//3
		colNames.add("AMT_GROSS_INCOME");//4
		colNames.add("DTE_APPL");//5
		colNames.add("DEP_NUM");//6
		colValues=Common.executeQuery(caseSql,colNames);
		sakCase=colValues.get(0);
		regionCode=colValues.get(1);
		officeCode=colValues.get(2);
		familySize=colValues.get(3);
		incomeAmount=colValues.get(4);
		applDate=colValues.get(5).substring(0,4)+"-"+colValues.get(5).substring(4,6) +"-"+colValues.get(5).substring(6,8);
		lineCode=colValues.get(6);
		System.out.println("Appl Date: "+applDate);
		String caseNumSql="select * from t_re_case where sak_case='"+sakCase+"'";
		colNames.add("NUM_CASE");//0
		colNames.add("NAM_LAST");//1
		colNames.add("NAM_FIRST");//2
		colNames.add("NAM_MID_INIT");//3
		colValues=Common.executeQuery(caseNumSql,colNames);
		casenum=colValues.get(0);
		lastName=colValues.get(1);
		fname=colValues.get(2);
		midInit=colValues.get(3);
		String eligSql="select a.CDE_AID_CATEGORY,es.CDE_START,e.DTE_EFFECTIVE,e.DTE_END from t_re_aid_elig e,t_cde_aid a, T_RE_ELIG_START es where a.sak_cde_aid="+sakCdeAid+" and e.sak_cde_aid=a.sak_cde_aid and e.sak_recip='"+sakRecip+"' " +
				       "and e.cde_agency='"+agency+"' and e.dte_end='"+endDate+"' and es.sak_elig_start = e.sak_elig_start and rownum=1";
		System.out.println(eligSql);

		colNames.add("CDE_AID_CATEGORY");//0
		colNames.add("CDE_START");//1
		colNames.add("DTE_EFFECTIVE");//2
		colNames.add("DTE_END");//3
		colValues=Common.executeQuery(eligSql,colNames);
		catCode=colValues.get(0);
		eligStartReason=colValues.get(1);
		eligStartDate=colValues.get(2);
		eligEndDate=colValues.get(3).substring(0,4)+"-"+colValues.get(3).substring(4,6) +"-"+colValues.get(3).substring(6,8);
		deathDate=Member.fileSysdateCustom(-32);
		System.out.println("Death date: "+deathDate);
		String addressSql="select * from t_re_other_address where sak_recip='"+sakRecip+"' and cde_addr_usage='MM'";
		colNames.add("ADDRESS_LINE_1");//0
		colNames.add("ADR_CITY");//1
		colNames.add("ADR_ZIP_CODE");//2
		colValues=Common.executeQuery(addressSql,colNames);
		mailAddress=colValues.get(0);
		mailCity=colValues.get(1);
		mailZipCode=colValues.get(2);
		return memId;
	    }
	
	//Scenario-1 common method
	public static void autoDisenrollDeathdate(String memId,String mcProg,String benefitPlan) throws Exception {
	String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<EligibilityRequest xmlns=\"http://xmlns.hhs.ma.gov/HHS/serviceobjects/versions/2.9/EligibilityServices\" "+
			    "\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<transactionsource\nid_medicaid=\""+memId+"\"\nid_other=\""+Member.otherid+"\"\nid_source=\"MHO\"" +
			    "\n/>\n<demographic\nnam_last=\""+lastName+"\"\nnam_first=\""+fname+"\"\nnum_primary_ssn=\""+ssn+"\"\nres_adr_street_1=\""+adr1+"\"\nres_adr_city=\""+city+"\"\n" +
			    "res_adr_state=\"MA\"\nres_adr_zip_code=\""+zip+"\"\nmail_adr_street_1=\""+mailAddress+"\"\nmail_adr_city=\""+mailCity+"\"\nmail_adr_state=\"MA\"\nmail_adr_zip_code=\""+mailZipCode+"\"\ncde_lang_written=\""+lang+"\"\ncde_sex=\""+sex+"\"\ncde_race=\""+race+"\"\ncde_citizen=\"C\"\ndte_birth=\""+dob+"\"\ndte_death=\""+deathDate+"\"" +
			    "\n/>\n<case\nnum_case=\""+casenum+"\"\ncde_case_status=\"1\"\nhoh_nam_first=\""+fname+"\"\nhoh_nam_last=\""+lastName+"\"\nhoh_nam_init=\""+midInit+"\"\n/>\n<eligibility\ncde_line=\""+lineCode+"\"\ndte_begin_elig=\""+eligDateXmlFormat+"\"\ndte_end_elig=\""+deathDate+"\"\n" +
			    "cde_elig_status=\"4\"\ncde_cat=\""+catCode+"\"\ncde_open_reason=\""+eligStartReason+"\"\ncde_close_reason=\"49\"\nfamily_size=\""+familySize+"\"\ndte_appl=\""+applDate+"\"\ncde_region=\""+regionCode+"\"\ncde_office=\""+officeCode+"\"\namt_gross_income=\""+incomeAmount+"\"\n/>\n</EligibilityRequest>");
	/*	String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<EligibilityRequest xmlns=\"http://xmlns.hhs.ma.gov/HHS/serviceobjects/versions/2.9/EligibilityServices\" "+
			    "\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<transactionsource\nid_medicaid=\""+memId+"\"\nid_other=\""+Member.otherid+"\"\nid_source=\"MHO\"" +
			    "\n/>\n<demographic\nnam_last=\""+lastName+"\"\nnam_first=\""+fname+"\"\nnum_primary_ssn=\""+ssn+"\"\nres_adr_street_1=\""+adr1+"\"\nres_adr_city=\""+city+"\"\n" +
			    "res_adr_state=\"MA\"\nres_adr_zip_code=\""+zip+"\"\nmail_adr_street_1=\""+mailAddress+"\"\nmail_adr_city=\""+mailCity+"\"\nmail_adr_state=\"MA\"\nmail_adr_zip_code=\""+mailZipCode+"\"\ncde_lang_written=\""+lang+"\"\ncde_sex=\""+sex+"\"\ncde_race=\""+race+"\"\ncde_citizen=\"C\"\ndte_birth=\""+dob+"\"\n" +
			    "\n/>\n<case\nnum_case=\""+casenum+"\"\ncde_case_status=\"1\"\nhoh_nam_first=\""+fname+"\"\nhoh_nam_last=\""+lastName+"\"\nhoh_nam_init=\""+midInit+"\"\n/>\n<eligibility\ncde_line=\""+lineCode+"\"\ndte_begin_elig=\""+eligDateXmlFormat+"\"\n" +
			    "cde_elig_status=\"4\"\ncde_cat=\""+catCode+"\"\ncde_open_reason=\""+eligStartReason+"\"\ncde_close_reason=\"00\"\nfamily_size=\""+familySize+"\"\ndte_appl=\""+applDate+"\"\ncde_region=\""+regionCode+"\"\ncde_office=\""+officeCode+"\"\namt_gross_income=\""+incomeAmount+"\"\n/>\n</EligibilityRequest>");*/
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
	String Mid=Member.getCustomMemebr(xml);
	//Assert.assertTrue(Member.result_type.equals("S"),"The result type is not Successfull:"+errorMessage);
	
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(Mid);
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
	//driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id21")).click();
	driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_RePmpAssignSu")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:RePmpAssignSuBean_ColHeader_publicHlthPgm_pgmHealthCode")).click();//sorting the MCProgram
	//driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n108")).click();//Member Benefit Plan panel
    driver.findElement(By.linkText("Member Benefit Plan")).click();
	int rowNum=driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:EligPanel:EligList:tbody_element']/tr")).size();
	for(i=1;i<=rowNum;i++){
		if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:EligPanel:EligList:tbody_element']/tr["+i+"]/td[3]")).getText().trim().contains(benefitPlan))
		{
		 // if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:EligPanel:EligList:tbody_element']/tr["+i+"]/td[7]")).getText().trim().equals(Common.convertSysdatecustom(-32))){
		   break;
			//}
		}
	 }
	if(i==(rowNum+1))
     {
      throw new SkipException("The Benefit plan is not ended to death date in the Member Benefit Plan panel");
     } 
	Common.cancelAll();
	}
	
	
	//Scenario-2 Common method
	public static void autoDisenrollSysdate(String memId,String mcProg,String benefitPlan,String agency) throws Exception {
	String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<EligibilityRequest xmlns=\"http://xmlns.hhs.ma.gov/HHS/serviceobjects/versions/2.9/EligibilityServices\" "+
		        "\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<transactionsource\nid_medicaid=\""+memId+"\"\nid_other=\""+Member.otherid+"\"\nid_source=\""+agency+"\"" +
		        "\n/>\n<demographic\nnam_last=\""+lastName+"\"\nnam_first=\""+fname+"\"\nnum_primary_ssn=\""+ssn+"\"\nres_adr_street_1=\""+adr1+"\"\nres_adr_city=\""+city+"\"\n" +
		        "res_adr_state=\"MA\"\nres_adr_zip_code=\""+zip+"\"\nmail_adr_street_1=\""+mailAddress+"\"\nmail_adr_city=\""+mailCity+"\"\nmail_adr_state=\"MA\"\nmail_adr_zip_code=\""+mailZipCode+"\"\ncde_lang_written=\""+lang+"\"\ncde_sex=\""+sex+"\"\ncde_race=\""+race+"\"\ncde_citizen=\"C\"\ndte_birth=\""+dob+"\"" +
		        "\n/>\n<case\nnum_case=\""+casenum+"\"\ncde_case_status=\"1\"\nhoh_nam_first=\""+fname+"\"\nhoh_nam_last=\""+lastName+"\"\nhoh_nam_init=\""+midInit+"\"\n/>\n<eligibility\ncde_line=\""+lineCode+"\"\ndte_begin_elig=\""+eligDateXmlFormat+"\"\ndte_end_elig=\""+Member.fileSysdateCustom(0)+"\"\n" +
		        "cde_elig_status=\"4\"\ncde_cat=\""+catCode+"\"\ncde_open_reason=\""+eligStartReason+"\"\ncde_close_reason=\"00\"\nfamily_size=\""+familySize+"\"\ndte_appl=\""+applDate+"\"\ncde_region=\""+regionCode+"\"\ncde_office=\""+officeCode+"\"\namt_gross_income=\""+incomeAmount+"\"\n/>\n</EligibilityRequest>");
    Common.resetBase();
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
    String Mid=Member.getCustomMemebr(xml);
  //  Assert.assertTrue(Member.result_type.equals("S"),"The result type is not Successfull:"+errorMessage);
    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(Mid);
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
	driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
	driver.findElement(By.linkText("Member Benefit Plan")).click();
	int rowNum1=driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:EligPanel:EligList:tbody_element']/tr")).size();
	for(i=1;i<=rowNum1;i++){
		if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:EligPanel:EligList:tbody_element']/tr["+i+"]/td[3]")).getText().trim().contains(benefitPlan))
		  {
			if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:EligPanel:EligList:tbody_element']/tr["+i+"]/td[7]")).getText().trim().equals(Common.convertSysdate())){
		  break;
		  }
		}
	 }
	if(i==(rowNum1+1))
     {
      throw new SkipException("The Benefit plan is not ended to system date in the Member Benefit Plan panel");
     } 
	System.out.println("The Member aid cat/Benefit Plan is ended with today's date");
	log("The Member Benefit Plan is ended with today's date");
	//Common.cancelAll();
	}
	
	
	//Scenario-3 Common method
	public static void autoDisenrollTPLmem(String tcNo,String sph) throws Exception{
	String tplMemSql="select distinct b.id_medicaid, b.sak_recip from t_re_base b,t_re_pmp_assign asg1, t_pmp_svc_loc loc, t_re_other_address o where  substr(asg1.dte_end,1,6) > substr(to_char(sysdate,'YYYYMMDD'),1,6) " +
				     "and asg1.sak_pub_hlth='"+sph+"' and asg1.cde_status1 <> 'H' and b.cde_agency='MHO' and b.sak_recip= asg1.sak_recip and loc.sak_pmp_ser_loc =asg1.sak_pmp_ser_loc and ind_active <> 'N' and dte_death='0' " +
				     "and asg1.dte_effective<to_char(sysdate, 'YYYYMMDD') and not exists( select 1 from t_re_loc loc1 where loc1.sak_recip=b.sak_recip and loc1.DTE_discharge> to_char(sysdate,'YYYYMMDD')) and o.sak_recip = asg1.sak_recip " +
				     "and o.adr_zip_code in (02122,02123,02124,02125,02126,02127,02129,02130,02131,02132,02133,02134,02135,02136,02137,02147,02153,02163,02199,02201,02203,02208,02209,02210,02215,02222,02241,02445,02446,02447,02196,02228,02284,02101,02102,02103,02104,02105,02106,02107,02108,02109,02110,02111,02112,02113,02114,02115,02116,02117,02118,02119,02120,02121,02283,02211) " +
				     "and b.sak_recip > dbms_random.value * 6300000 and rownum<2";
    colNames.add("ID_MEDICAID");//0
	colNames.add("SAK_RECIP");//1
	colValues=Common.executeQuery(tplMemSql,colNames);
	memId=colValues.get(0);
	sakRecip=colValues.get(1);
	log("Member id:"+memId);
	System.out.println("Member id from TC#"+tcNo+":"+memId);
	//Inserting data into R_DAY2 table
	Managedcare_Common.insertDataSql(tcNo,memId,"Member Id",Common.convertSysdate());
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
    driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchBean_CriteriaPanel:NEW")).click();
	driver.findElement(By.cssSelector("img[alt=\"Member ID pop-up search\"]")).click();
   // driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:RecipientSearchCriteriaPanel:CurrentID")).clear();
	driver.findElement(By.xpath("//input[contains(@id,'RecipientSearchCriteriaPanel:CurrentID')]")).clear();
	driver.findElement(By.xpath("//input[contains(@id,'RecipientSearchCriteriaPanel:CurrentID')]")).sendKeys(memId);
   // driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:RecipientSearchCriteriaPanel:SEARCH")).click();
	driver.findElement(By.xpath("//*[contains(@id,'RecipientSearchCriteriaPanel:SEARCH')]")).click();
	//driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:RecipientSearchResults_0:column1Value")).click();
	driver.findElement(By.xpath("//*[contains(@id,'0:column1Value')]")).click();
	driver.findElement(By.cssSelector("img[alt=\"Carrier Number pop-up search\"]")).click();
	//driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:CarrierNumberSearchCriteriaPanel:carrierName")).clear();
	driver.findElement(By.xpath("//input[contains(@id,'carrierName')]")).clear();
	driver.findElement(By.xpath("//input[contains(@id,'carrierName')]")).sendKeys("BLUE");//need to update the carrier code from 0006005 to 0027000.
	//driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:CarrierNumberSearchCriteriaPanel:SEARCH")).click();
	driver.findElement(By.xpath("//*[contains(@id,'CarrierNumberSearchCriteriaPanel:SEARCH')]")).click();
	//driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:CarrierNumberSearchResults_1:column1Value")).click();//selecting 0027000 carrier number from search results
	driver.findElement(By.xpath("//*[contains(@id,'0:column1Value')]")).click();
	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SubContractorIndicator"))).selectByVisibleText("No");
	driver.findElement(By.cssSelector("img[alt=\"Relationship pop-up search\"]")).click();
   // driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:RelationshipSearchCriteriaPanel:xRelationCode")).clear();
	driver.findElement(By.xpath("//input[contains(@id,'xRelationCode')]")).sendKeys("I");
	/*driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:RelationshipSearchCriteriaPanel:SEARCH")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:RelationshipPopupSearchResults_0:column1Value")).click();*/
	driver.findElement(By.xpath("//*[contains(@id,'RelationshipSearchCriteriaPanel:SEARCH')]")).click();
	driver.findElement(By.xpath("//*[contains(@id,'0:column1Value')]")).click();
    driver.findElement(By.cssSelector("img[alt=\"Policy holder ID pop-up search\"]")).click();
	//driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:PolicyHolderSearchCriteriaPanel:LastName")).clear();
    driver.findElement(By.xpath("//input[contains(@id,'PolicyHolderSearchCriteriaPanel:LastName')]")).clear();
	driver.findElement(By.xpath("//input[contains(@id,'PolicyHolderSearchCriteriaPanel:LastName')]")).sendKeys("ROSE");
   /* driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:PolicyHolderSearchCriteriaPanel:SEARCH")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:PolicyHolderSearchResults_0:column1Value")).click();*/
	driver.findElement(By.xpath("//*[contains(@id,'PolicyHolderSearchCriteriaPanel:SEARCH')]")).click();
	driver.findElement(By.xpath("//*[contains(@id,'0:column1Value')]")).click();
	//driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_TplPolicyNumber")).clear();
	driver.findElement(By.xpath("//input[contains(@id,'TplResourceDataPanel_TplPolicyNumber')]")).clear();
	driver.findElement(By.xpath("//input[contains(@id,'TplResourceDataPanel_TplPolicyNumber')]")).sendKeys("27056222");
	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_CostAvoidIndicator"))).selectByVisibleText("No");
	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_OriginCode"))).selectByValue("T");//TPL ONLINE UPDATE
    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_CdeInitOrg"))).selectByValue("T");//TPL ONLINE UPDATE
	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SuspectCode"))).selectByVisibleText("VERIFIED");
	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SuspectDate")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SuspectDate")).sendKeys(Common.convertSysdatecustom(-35));
	Common.saveAll();
	Common.cancelAll();
	driver.findElement(By.id("MMISForm:MMISBodyContent:TplNavigatorPanel:TPLNavigator:ITM_n103")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:Coverage_NewButtonClay:CoverageList_newAction_btn")).click();
    driver.findElement(By.cssSelector("img[alt=\"Coverage Search pop-up search\"]")).click();
   // driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:_id37:CoverageCodeSearchCriteriaPanel:coverageCode")).clear();
    driver.findElement(By.xpath("//input[contains(@id,'CoverageCodeSearchCriteriaPanel:coverageCode')]")).clear();
    driver.findElement(By.xpath("//input[contains(@id,'CoverageCodeSearchCriteriaPanel:coverageCode')]")).sendKeys("02");
   /* driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:_id37:CoverageCodeSearchCriteriaPanel:SEARCH")).click();
    driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:_id37:CoverageCodePopupSearchResults_0:column1Value")).click();*/
    driver.findElement(By.xpath("//*[contains(@id,'CoverageCodeSearchCriteriaPanel:SEARCH')]")).click();
	driver.findElement(By.xpath("//*[contains(@id,'0:column1Value')]")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EffectiveDate")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EffectiveDate")).sendKeys(Common.convertSysdatecustom(-40));
	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EndDate")).clear();
    driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EndDate")).sendKeys("12/31/2299");
	driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefPanel_addAction_btn")).click();
	Common.saveAll();
	Common.cancelAll();	
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memId);
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
	//driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id21")).click();
	driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
	Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr[7]/td[2]")).getText().trim().contains("Yes"),"TPL comprehensive is not added to the Memberid");
	}
	 
	
	//Scenario-4 Common method
	public static void autoDisenrollMedicaremem(String tcNo,String sph) throws Exception{
	String MemSql="select distinct b.id_medicaid, b.sak_recip,b.num_ssn from t_re_base b,t_re_pmp_assign asg1, t_pmp_svc_loc loc, t_re_other_address o where  substr(asg1.dte_end,1,6) > substr(to_char(sysdate,'YYYYMMDD'),1,6) " +
			      "and asg1.sak_pub_hlth='"+sph+"' and asg1.cde_status1 <> 'H' and b.cde_agency='MHO' and b.sak_recip= asg1.sak_recip and loc.sak_pmp_ser_loc =asg1.sak_pmp_ser_loc and ind_active <> 'N' and dte_death='0' " +
			      "and asg1.dte_effective<to_char(sysdate, 'YYYYMMDD') and not exists( select 1 from t_re_loc loc1 where loc1.sak_recip=b.sak_recip and loc1.DTE_discharge> to_char(sysdate,'YYYYMMDD')) and o.sak_recip = asg1.sak_recip " +
			      "and o.adr_zip_code in (02122,02123,02124,02125,02126,02127,02129,02130,02131,02132,02133,02134,02135,02136,02137,02147,02153,02163,02199,02201,02203,02208,02209,02210,02215,02222,02241,02445,02446,02447,02196,02228,02284,02101,02102,02103,02104,02105,02106,02107,02108,02109,02110,02111,02112,02113,02114,02115,02116,02117,02118,02119,02120,02121,02283,02211) " +
			      "and b.sak_recip > dbms_random.value * 6300000 and rownum<2";
	colNames.add("ID_MEDICAID");//0
	colNames.add("SAK_RECIP");//1
	colNames.add("NUM_SSN");//2
	colValues=Common.executeQuery(MemSql,colNames);
	memId=colValues.get(0);
	sakRecip=colValues.get(1);
	String ssn=colValues.get(2);
	log("Member id:"+memId);
	System.out.println("Member id from TC#27056d:"+memId);
	//Inserting data into R_DAY2 table
	Managedcare_Common.insertDataSql(tcNo,memId,"Member ID",Common.convertSysdate());
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memId);
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
	//driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id21")).click();
    driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:GRP_Medicare")).click();
	//driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n109")).click();
	driver.findElement(By.linkText("Medicare ID")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareIdPanel:MedicareID_NewButtonClay:MedicareIDList_newAction_btn")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareIdPanel:HibDataPanel_MedicareID")).sendKeys(ssn+"M");
	Common.saveAll();
	Common.cancelAll();
	//Medicare-A coverage
	//driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n117")).click();//Medicare A Coverage
	driver.findElement(By.linkText("Medicare A Coverage")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicareACoverage_NewButtonClay:MedicareACoverageList_newAction_btn")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicareADataPanel_MedicareID")).sendKeys(ssn+"M");
	driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicareADataPanel_EffectiveDate")).sendKeys(Common.firstDateOfPresentMonth());
    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicareADataPanel_EndDate")).sendKeys("12/31/2299");
	driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicareADataPanel_HIOptionCodeSearch_CMD_SEARCH']/img")).click();
	driver.findElement(By.xpath("//*[contains(@id,'4:column1Value')]")).click();
	//driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareACoveragePanel:_id319:HIOptionCodeSearchResults_4:column1Value")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicareADataPanel_PremPayorCodeSearch")).sendKeys("S22");
	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicarADataPanel_CdeEntitleRsn"))).selectByValue("5");//Unknown
	Common.saveAll();
	Common.cancelAll();
	//Medicare-B coverage
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n118")).click();
    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBCoverage_NewButtonClay:MedicareBCoverageList_newAction_btn")).click();
    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_MedicareID")).sendKeys(ssn+"M");
    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_EffectiveDate")).clear();
    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_EffectiveDate")).sendKeys(Common.firstDateOfPresentMonth());
    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_EndDate")).clear();
    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_EndDate")).sendKeys("12/31/2299");
    driver.findElement(By.cssSelector("img[alt=\"SMI Option pop-up search\"]")).click();
    driver.findElement(By.xpath("//*[contains(@id,'3:column1Value')]")).click();
   // driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:_id319:SMIOptionCodeSearchResults_3:column1Value")).click();
    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_PremPayorCodeSearch")).clear();
    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_PremPayorCodeSearch")).sendKeys("220");
    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicarBDataPanel_CdeEntitleRsn"))).selectByValue("5");//Unknown
    Common.saveAll();
    Common.cancelAll();	
    }
	
	
	public static void addTPLandCovg(String tcNo,String memId){
	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_TPL")).click();
		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:TplSearchBean_CriteriaPanel:NEW")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLCurrentId")).clear();
		driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TPLCurrentId")).sendKeys(memId);
		driver.findElement(By.cssSelector("img[alt=\"Carrier Number pop-up search\"]")).click();
		//driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:CarrierNumberSearchCriteriaPanel:carrierCode")).clear(); 
		driver.findElement(By.xpath("//input[contains(@id,'CarrierNumberSearchCriteriaPanel:carrierCode')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'CarrierNumberSearchCriteriaPanel:carrierCode')]")).sendKeys("1");
		driver.findElement(By.xpath("//*[contains(@id,'CarrierNumberSearchCriteriaPanel:SEARCH')]")).click();
		/*driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:CarrierNumberSearchCriteriaPanel:SEARCH")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:CarrierNumberSearchResults_0:column1Value")).click();*/
		driver.findElement(By.xpath("//*[contains(@id,'0:column1Value')]")).click();
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SubContractorIndicator"))).selectByVisibleText("No");
		driver.findElement(By.cssSelector("img[alt=\"Relationship pop-up search\"]")).click();
		driver.findElement(By.xpath("//input[contains(@id,'RelationshipSearchCriteriaPanel:xRelationCode')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'RelationshipSearchCriteriaPanel:xRelationCode')]")).sendKeys("D");
		/*driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:RelationshipSearchCriteriaPanel:xRelationCode")).clear();
		driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:RelationshipSearchCriteriaPanel:xRelationCode")).sendKeys("D");
		    driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:RelationshipSearchCriteriaPanel:SEARCH")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:RelationshipPopupSearchResults_0:column1Value")).click();*/
		driver.findElement(By.xpath("//*[contains(@id,'RelationshipSearchCriteriaPanel:SEARCH')]")).click();
		driver.findElement(By.xpath("//*[contains(@id,'0:column1Value')]")).click();
		driver.findElement(By.cssSelector("img[alt=\"Policy holder ID pop-up search\"]")).click();
		driver.findElement(By.xpath("//input[contains(@id,'PolicyHolderSearchCriteriaPanel:LastName')]")).clear();
		driver.findElement(By.xpath("//input[contains(@id,'PolicyHolderSearchCriteriaPanel:LastName')]")).sendKeys("BURNS");
		driver.findElement(By.xpath("//*[contains(@id,'PolicyHolderSearchCriteriaPanel:SEARCH')]")).click();
		driver.findElement(By.xpath("//*[contains(@id,'0:column1Value')]")).click();
		   /* driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:PolicyHolderSearchCriteriaPanel:LastName")).clear();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:PolicyHolderSearchCriteriaPanel:LastName")).sendKeys("BURNS");
		    driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:PolicyHolderSearchCriteriaPanel:SEARCH")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:_id37:PolicyHolderSearchResults_0:column1Value")).click();*/
		driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_TplPolicyNumber")).clear();
		driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_TplPolicyNumber")).sendKeys(tcNo);
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_CostAvoidIndicator"))).selectByVisibleText("No");
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_OriginCode"))).selectByVisibleText("TPL ONLINE UPDATE");
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_CdeInitOrg"))).selectByVisibleText("TPL ONLINE UPDATE");
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SuspectCode"))).selectByVisibleText("VERIFIED");
		driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SuspectDate")).clear();
		driver.findElement(By.id("MMISForm:MMISBodyContent:TplBaseInformationPanel:TplResourceDataPanel_SuspectDate")).sendKeys(Common.lastDateOfPreviousMonth());
		Common.saveAll();
	    Common.cancelAll();
		driver.findElement(By.id("MMISForm:MMISBodyContent:TplNavigatorPanel:TPLNavigator:ITM_n103")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:Coverage_NewButtonClay:CoverageList_newAction_btn")).click();
		driver.findElement(By.cssSelector("img[alt=\"Coverage Search pop-up search\"]")).click();
		driver.findElement(By.xpath("//input[contains(@id,'CoverageCodeSearchCriteriaPanel:coverageCode')]")).clear(); 
		driver.findElement(By.xpath("//input[contains(@id,'CoverageCodeSearchCriteriaPanel:coverageCode')]")).sendKeys("02");
		driver.findElement(By.xpath("//*[contains(@id,'CoverageCodeSearchCriteriaPanel:SEARCH')]")).click();
		driver.findElement(By.xpath("//*[contains(@id,'0:column1Value')]")).click();
		  /*driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:_id37:CoverageCodeSearchCriteriaPanel:coverageCode")).clear();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:_id37:CoverageCodeSearchCriteriaPanel:coverageCode")).sendKeys("02");
		    driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:_id37:CoverageCodeSearchCriteriaPanel:SEARCH")).click();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:_id37:CoverageCodePopupSearchResults_0:column1Value")).click();*/
		    driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EffectiveDate")).clear();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EffectiveDate")).sendKeys(Common.firstDateOfPreviousMonth());
		    driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EndDate")).clear();
		    driver.findElement(By.id("MMISForm:MMISBodyContent:CoveragePanel:CoverageXrefDataPanel_EndDate")).sendKeys("12312299");
		    Common.saveAll();
		    Common.cancelAll();	
		}
	
	
	
	public static String changeDemographics(String sql,String tcNo) throws Exception{
	
	//sql="Select distinct b.id_medicaid from t_re_base b,t_re_pmp_assign p where b.sak_recip=p.sak_recip " +
	   // "and b.cde_agency='MHO' and p.sak_pub_hlth='"+sph+"' and p.dte_end='22991231' and p.cde_status1<>'H' and b.sak_recip > dbms_random.value * 6300000 and rownum<2";
	colNames.add("ID_MEDICAID");
	colNames.add("SAK_CDE_AID");
	colValues=Common.executeQuery(sql,colNames);
	memId=colValues.get(0);
	String sakcdeaid=colValues.get(1);
	//memId=Managedcare_Common.xmlData(sql,"MHO");
	memId=Managedcare_Common.xmlData(memId, "MHO", sakcdeaid, "22991231");
	Managedcare_Common.insertDataSql(tcNo,memId,"Member Id",Common.convertSysdate());
	System.out.println("Member Id: "+memId);
	Managedcare_Common.insertDataSql(tcNo+"FN",fname,"First Name",Common.convertSysdate());
	Managedcare_Common.insertDataSql(tcNo+"OLN",lastName,"Old Last Name",Common.convertSysdate());
	Managedcare_Common.insertDataSql(tcNo+"OADD",adr1,"Old Address",Common.convertSysdate());
	Managedcare_Common.insertDataSql(tcNo+"ORace",race,"Old Race",Common.convertSysdate());
	Managedcare_Common.insertDataSql(tcNo+"OSex",gender,"Old Gender",Common.convertSysdate());
	newLN=Common.generateRandomName();
	Managedcare_Common.insertDataSql(tcNo+"NLN",newLN,"New LastName",Common.convertSysdate());
	newAdd=adr1+Common.generateRandomTaxID().substring(0,2);
	Managedcare_Common.insertDataSql(tcNo+"NADD",newAdd,"New Address",Common.convertSysdate());
	if(race.equals("WHITE")){
		newRace="BLACK";
	 }
	else{
		newRace="WHITE";
	 }
	Managedcare_Common.insertDataSql(tcNo+"NRace",newRace,"New Race",Common.convertSysdate());
	if(sex.equals("F")){
		gender="M";
	}
	else{
		gender="F";
	    }
	Managedcare_Common.insertDataSql(tcNo+"NSex",gender,"New Gender",Common.convertSysdate());
	String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<EligibilityRequest xmlns=\"http://xmlns.hhs.ma.gov/HHS/serviceobjects/versions/2.9/EligibilityServices\" "+
		    "\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<transactionsource\nid_medicaid=\""+memId+"\"\nid_other=\""+Member.otherid+"\"\nid_source=\"MHO\"" +
		    "\n/>\n<demographic\nnam_last=\""+newLN+"\"\nnam_first=\""+fname+"\"\nnum_primary_ssn=\""+ssn+"\"\nres_adr_street_1=\""+newAdd+"\"\nres_adr_city=\""+city+"\"\n" +
		    "res_adr_state=\"MA\"\nres_adr_zip_code=\""+zip+"\"\nmail_adr_street_1=\""+mailAddress+"\"\nmail_adr_city=\""+mailCity+"\"\nmail_adr_state=\"MA\"\nmail_adr_zip_code=\""+mailZipCode+"\"\ncde_lang_written=\""+lang+"\"\ncde_sex=\""+gender+"\"\ncde_race=\""+newRace+"\"\ncde_citizen=\"C\"\ndte_birth=\""+dob+"\"" +
		    "\n/>\n<case\nnum_case=\""+casenum+"\"\ncde_case_status=\"1\"\nhoh_nam_first=\""+fname+"\"\nhoh_nam_last=\""+newLN+"\"\nhoh_nam_init=\""+midInit+"\"\n/>\n<eligibility\ncde_line=\""+lineCode+"\"\ndte_begin_elig=\""+eligDateXmlFormat+"\"\n" +
		    "cde_elig_status=\"1\"\ncde_cat=\""+catCode+"\"\ncde_open_reason=\""+eligStartReason+"\"\nfamily_size=\""+familySize+"\"\ndte_appl=\""+applDate+"\"\ncde_region=\""+regionCode+"\"\ncde_office=\""+officeCode+"\"\namt_gross_income=\""+incomeAmount+"\"\n/>\n</EligibilityRequest>");
   driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
   String Mid=Member.getCustomMemebr(xml);
  // Assert.assertTrue(Member.result_type.equals("S"),"The result type is not Successfull:"+errorMessage);
   Assert.assertTrue(Mid.equals(memId),"The member id after demographic changes is not same as before changing the demographics");
   System.out.println("Member Id: "+Mid);
   return Mid;
   }
	
		
	
	

	//Method to convert YYYYMMDD to YYYY-MM-DD
	public static String convertToXMLDate(String date) throws Exception{
    String dbDate=Common.convertDatetoInt(date);
	return dbDate.substring(0,4)+"-"+dbDate.substring(4,6) +"-"+dbDate.substring(6,8);
	}
	
	
	//SQL to fetch members for particular provider and program
	public static String transferProvSql(String sph,String sakProv,String svcloc){
	sql="select distinct b.id_medicaid from t_re_base b,t_re_pmp_assign asg1, t_pmp_svc_loc loc, t_re_other_address o where  substr(asg1.dte_end,1,6) > substr(to_char(sysdate,'YYYYMMDD'),1,6) and asg1.sak_pub_hlth='"+sph+"' and asg1.cde_status1 <> 'H' " +
		"and b.sak_recip= asg1.sak_recip and loc.sak_pmp_ser_loc =asg1.sak_pmp_ser_loc and loc.sak_prov='"+sakProv+"' and loc.CDE_SERVICE_LOC='"+svcloc+"' and loc.dte_end='22991231' and loc.dte_end=asg1.dte_end and ind_active <> 'N' and dte_death='0' and asg1.dte_effective<to_char(sysdate, 'YYYYMMDD') and not exists( select 1 from t_re_loc loc1 where loc1.sak_recip=b.sak_recip and loc1.DTE_discharge> to_char(sysdate,'YYYYMMDD')) " +
		"and o.sak_recip = asg1.sak_recip and o.adr_zip_code in (02122,02123,02124,02125,02126,02127,02129,02130,02131,02132,02133,02134,02135,02136,02137,02147,02153,02163,02199,02201,02203,02208,02209,02210,02215,02222,02241,02445,02446,02447,02196,02228,02284,02101,02102,02103,02104,02105,02106,02107,02108,02109,02110,02111,02112,02113,02114,02115,02116,02117,02118,02119,02120,02121,02283,02211) " +
		"and not exists (select 1 from t_re_pmp_assign where sak_recip=b.sak_recip and cde_status1 <> 'H' and dte_effective >asg1.dte_effective and sak_pub_hlth='"+sph+"') and b.id_medicaid<>'100045466041' and b.id_medicaid<>'100004291199' and b.id_medicaid<>'100032772137' and b.id_medicaid<>'100051829214' and b.sak_recip > dbms_random.value * 6300000 and rownum<2";
	return sql;
	}
		
	
	public static String getRatecell(String memId) throws Exception{
	String rateCell=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[20]/td[2]")).getText().trim();
	//ratecell is not being updated tables immediately
//	sql="select cde_rate_cell from T_MC_CENSUS c,t_mc_rate_cell r where c.sak_recip=(select sak_recip from t_re_base where id_medicaid='"+memId+"') and c.sak_rate_cell=r.sak_rate_cell and c.sak_pub_hlth='83' and rownum<2";
//	colNames.add("CDE_RATE_CELL");
//	colValues=Common.executeQuery(sql,colNames);
//	String  rateCell=colValues.get(0);
	return rateCell;
	}
	
	
	public static void addMMQ(String date){
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n113")).click();//Clicking Level Of Care/MMC
	driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCare_NewButtonClay:LevelOfCareList_newAction_btn")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareBean_providerID")).sendKeys("110025810");
	driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareBean_SvcLoc")).sendKeys("A");
	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_ReLocCode"))).selectByValue("3G");
	driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_EffectiveDate")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_EffectiveDate")).sendKeys(date);
	driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_AdmissionDate")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_AdmissionDate")).sendKeys(date);
	new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:LevelOfCarePanel:LevelOfCareDataPanel_ReLocAdmitReason"))).selectByValue("HM");
	Common.save();
	Common.cancelAll();	
	}
	
	public static void memSrch(String memId){
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memId);
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
	//driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id21")).click();
	driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_RePmpAssignSu")).click();	
	}
	
	
	public static String searchSuPanel(String mcProgram,String EndDate,String status){
	driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:RePmpAssignSuBean_ColHeader_endDate")).click();//Sorting on end date
	driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:RePmpAssignSuBean_ColHeader_endDate")).click();
	int rownum =driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr")).size();
	for(i=1;i<=rownum;i++)
    {
	 if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[3]")).getText().trim().contains(mcProgram)&&driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[5]")).getText().trim().equals(EndDate)&&driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[6]")).getText().trim().equals(status))
	       {
	        break;
			}
		 }
	 if(i==(rownum+1))
	     {
		throw new SkipException("There is no row with mcprogram,"+mcProgram+"and enddate="+EndDate+"in the SU Panel");
		 } 
	 String provider=driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_"+(i-1)+":RePmpAssignSuBean_ColValue_pmpProviderIdDisplay")).getText().trim();
	 svcLoc=driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_"+(i-1)+":RePmpAssignSuBean_ColValue_pmpServiceLocDisplay")).getText().trim();
	 return providerId=provider+svcLoc;  
	 }
	
		

	public static void portalFlow(String mcProgram,String memberId,String lastName,String firstName){
	if(mcProgram.equals("SCO"))
	       {
    new Select(driver.findElement(By.id("scoPaceMemberSearch:memberSearch:provider"))).selectByValue("110031450A");
	       }
	else if(mcProgram.equals("PACE"))
	       {
	new Select(driver.findElement(By.id("scoPaceMemberSearch:memberSearch:provider"))).selectByValue("110031495A");
	       }
	   driver.findElement(By.id("scoPaceMemberSearch:memberSearch:memberID")).clear();
	   driver.findElement(By.id("scoPaceMemberSearch:memberSearch:memberID")).sendKeys(memberId);
	   driver.findElement(By.id("scoPaceMemberSearch:memberSearch:lastName")).clear();
	   driver.findElement(By.id("scoPaceMemberSearch:memberSearch:lastName")).sendKeys(lastName);
	   driver.findElement(By.id("scoPaceMemberSearch:memberSearch:firstName")).clear();
	   driver.findElement(By.id("scoPaceMemberSearch:memberSearch:firstName")).sendKeys(firstName);
	   driver.findElement(By.xpath("//input[@class='buttonFunctional' and @alt='Search']")).click();	
	}
	
	public static void portalDisenrollEnroll(String mcProgram,String sph,String pid) throws Exception{
	
	sqlStatement ="select id_medicaid,(110000000+sak_prov) PROV, loc.cde_service_loc, b.nam_last,b.nam_first from t_re_base b,t_re_pmp_assign asg1, t_pmp_svc_loc loc where  substr(asg1.dte_end,1,6) > substr(to_char(sysdate,'YYYYMMDD'),1,6) " +
		          "and asg1.sak_pub_hlth="+sph+" and asg1.cde_status1 <> 'H' and b.sak_recip= asg1.sak_recip and loc.sak_pmp_ser_loc =asg1.sak_pmp_ser_loc and ind_active <> 'N' and dte_death='0' " +
		          "and asg1.dte_effective < to_char(sysdate, 'YYYYMMDD') and not exists( select 1 from t_re_loc loc1  where  loc1.sak_recip=b.sak_recip and loc1.DTE_discharge  > to_char(sysdate, 'YYYYMMDD')) " +
		          "and sak_prov="+pid+" and rownum<2";
	colNames.add("ID_MEDICAID");
    colNames.add("NAM_LAST");
    colNames.add("NAM_FIRST");
    colValues=Common.executeQuery(sqlStatement, colNames );
    memberId = colValues.get(0);
    lastName = colValues.get(1);
   String firstName=colValues.get(2);
   Common.portalLogin();
   driver.findElement(By.linkText("Manage Members")).click();
   driver.findElement(By.linkText("Enrollment")).click();
   driver.findElement(By.linkText("Enroll/Disenroll PACE Members")).click();
   portalFlow(mcProgram,memberId,lastName,firstName);
   String memNameDisenroll=(driver.findElement(By.xpath("//*[@id='scoPaceDisenrollment']/table/tbody/tr[2]/td[4]/span")).getText().trim());
   new Select(driver.findElement(By.xpath("//select[contains(@id,'disenrollReason')]"))).selectByVisibleText("Did Not Like Doctor");
   driver.findElement(By.xpath("//input[@class='buttonCommand' and @alt='Submit']")).click();
   
   List<WebElement> thankYouMsgElements = driver.findElements(By.xpath("//*[@id='scoPaceConfirmation:confirmation']/tbody/tr/td/span"));
	String message="";
	for (int i=0;i<thankYouMsgElements.size();i++) {
		message = message+thankYouMsgElements.get(i).getText(); 
		if (message.contains("has been enrolled"))
			break;
	}
  // Assert.assertTrue(message.contains("Thank-you. "+memNameDisenroll +" has been enrolled."), "Portal Enrollment of "+memNameDisenroll+" " +memberId+" is NOT Successfull:"+message);
 //  System.out.println("Portal Enrollment of "+mcProgram+" member is completed");
   
   String message1=driver.findElement(By.xpath("//*[@id='scoPaceDisenrollmentConfirmation:DisenrollmentConfirmation']/tbody/tr[1]/td")).getText().trim();
   Assert.assertTrue(message1.contains("Thank-you. "+memNameDisenroll+" has been disenrolled."),"Portal Disenrollment of "+memNameDisenroll+" " +memberId+" is NOT Successfull");
   System.out.println(mcProgram +" member is dis-enrolled");
  // driver.findElement(By.xpath("//*[@id='scoPaceDisenrollmentConfirmation:enrollDisenrollSCOPACEMembers_1_id11:enrollDisenrollSCOPACEMembers_1_id12']")).click();
   driver.findElement(By.id("scoPaceDisenrollmentConfirmation:j_id_id13pc3:j_id_id14pc3")).click();
   portalFlow(mcProgram,memberId,lastName,firstName);
   String memNameEnroll=(driver.findElement(By.xpath("//*[@id='ScoPaceVerification:verification']/tbody/tr[3]/td[2]/span")).getText().trim());
  // driver.findElement(By.id("ScoPaceVerification:enrollDisenrollSCOPACEMembers_1_id13:enrollDisenrollSCOPACEMembers_1_id15")).click();
   
   driver.findElement(By.id("ScoPaceVerification:j_id_id42pc3:j_id_id44pc3")).click();
   driver.findElement(By.id("scoPaceCertification:certification:certified")).click();
   driver.findElement(By.id("scoPaceCertification:j_id_id8pc3:j_id_id10pc3")).click();
   String message2=driver.findElement(By.xpath("//*[@id='scoPaceConfirmation:confirmation']/tbody/tr[1]/td")).getText().trim();
   Assert.assertTrue(message2.contains("Thank-you. "+memNameEnroll +" has been enrolled."), "Portal Enrollment of "+memNameEnroll+" " +memberId+" is NOT Successfull:"+message2);
   System.out.println(mcProgram+" member is enrolled");
   log(mcProgram+" member is disenrolled and enrolled for "+memberId);
   }
	
	
	public static void xmlFileTransfer(String fileName,String providerId,String indicator,String transDate,String memId1,String copayAmt,String svcDate,String processDate,String memId2,String popCopayAmt,String popSvcDate,String popProcessDate) throws Exception{
		Calendar cal = Calendar.getInstance();
		String xml;
		if(indicator.equals("Y")){
			xml=("<?xml version=\"1.0\"?>\n<copayList>\n<copayheader\n provider_id=\""+providerId+"\"\n pops_ind=\""+indicator+"\"\n transmission_date=\""+transDate+"\"\n/>\n<membercopay\n id_medicaid=\""+memId1+"\"\n dte_effective=\""+cal.get(Calendar.YEAR)+"-01-01\"\n dte_end=\""+cal.get(Calendar.YEAR)+"-12-31\"\n copay_type=\"P\"\n ytd_accumulator=\""+copayAmt+"\"\n copay_cap_ind=\"N\"\n dte_svc=\""+svcDate+"\"\n dte_process=\""+processDate+"\" dte_cap_notif_sent=\"0         \"" +
				    "\n/><membercopay\n id_medicaid=\""+memId2+"\"\n dte_effective=\""+cal.get(Calendar.YEAR)+"-01-01\"\n dte_end=\""+cal.get(Calendar.YEAR)+"-12-31\"\n copay_type=\"P\"\n ytd_accumulator=\""+popCopayAmt+"\"\n copay_cap_ind=\"N\"\n dte_svc=\""+popSvcDate+"\"\n dte_process=\""+popProcessDate+"\" dte_cap_notif_sent=\"0         \"/>\n<copaytrailer num_recs=\"4\" />\n</copayList>");
		 }
		else{
		    xml=("<?xml version=\"1.0\" encoding=\"us-ascii\"?>\n<copayList>\n<copayheader\n provider_id=\""+providerId+"\"\n POPS_ind=\""+indicator+"\"\n transmission_date=\""+transDate+"\"\n/>\n<membercopay\n id_medicaid=\""+memId1+"\"\n dte_effective=\""+cal.get(Calendar.YEAR)+"-01-01\"\n dte_end=\""+cal.get(Calendar.YEAR)+"-12-31\"\n copay_type=\"P\"\n ytd_accumulator=\""+copayAmt+"\"\n copay_cap_ind=\"N\"\n dte_svc=\""+svcDate+"\"\n dte_process=\""+processDate+"\" dte_cap_notif_sent=\"0         \"" +
				    "\n/><membercopay\n id_medicaid=\""+memId2+"\"\n dte_effective=\""+cal.get(Calendar.YEAR)+"-01-01\"\n dte_end=\""+cal.get(Calendar.YEAR)+"-12-31\"\n copay_type=\"P\"\n ytd_accumulator=\""+popCopayAmt+"\"\n copay_cap_ind=\"N\"\n dte_svc=\""+popSvcDate+"\"\n dte_process=\""+popProcessDate+"\" dte_cap_notif_sent=\"0         \"/>\n<copaytrailer num_recs=\"4\" />\n</copayList>");
		     }
		//Placing the xml file in temp folder with specified name
		String fileSourceName=tempDirPath+fileName;
		// writing xml string to xml file
		PrintWriter out = new PrintWriter(fileSourceName);
		out.println(xml);
		out.close();
		
		if(!new File(fileSourceName).exists())
		    {
			 System.out.println("XML file "+fileSourceName+", is not found in the temp folder");
			 Assert.assertTrue(false, "XML file,"+fileSourceName+", is not found in the temp folder");
		    }
		System.out.println("XML file "+fileSourceName+", is found in the temp folder");
		}
	
	public static String checkCST(String tcNo,String reqLtrType,String reqStatus) throws Exception{
		sql="select * from R_DAY2 where TC='"+tcNo+"'";
		colNames.add("ID");
		colValues=Common.executeQuery1(sql,colNames);
		memId=colValues.get(0);
		System.out.println(memId);
		String cstSql="select * from t_mc_cst_trigger where dte_status=(select max(dte_status) from t_mc_cst_trigger where sak_recip=(select SAK_RECIP from t_re_base where id_medicaid='"+memId+"')) " +
				      "and sak_recip=(select SAK_RECIP from t_re_base where id_medicaid='"+memId+"')";
		colNames.add("CDE_STATUS");//0
		colNames.add("CDE_LTR_TYPE");//1
		colNames.add("DTE_STATUS");//2
		colValues=Common.executeQuery(cstSql,colNames);
		String status=colValues.get(0);
		String ltrType=colValues.get(1);
		String date=colValues.get(2);
		System.out.println(status+ltrType+date);
		log("Letter Type:"+ltrType+", Status: "+status+", Date: "+date);
		if(status.equals("F")){
			String command, error;
            //Get Desired Filename
			command = "ls -ltr /customer/dsma/"+unixDir+"/data/mgd06761.xml.*";
			error = "There was no report file found";
			String fileName = Common.connectUNIX(command, error);
			fileName = fileName.substring(fileName.length()-41);
            System.out.println("Filename :"+fileName);
			//Verify member data in file
			command = "grep "+memId+" "+fileName;
			error = "The selected file is not correct/ or it is empty/ or it does not have the desired member";
			String outputText = Common.connectUNIX(command, error);
            System.out.println("Output Text is: "+outputText);
			Assert.assertTrue(outputText.contains(memId), "Member id not found in file");
			
			command = "grep "+ltrType+" "+fileName;
			error = "The selected file is not correct/ or it is empty/ or it does not have the desired member";
			String outputText1 = Common.connectUNIX(command, error);
            System.out.println("Output Text1 is: "+outputText1);
			Assert.assertTrue(outputText1.contains(ltrType), "Letter type is not found");
		    }
		Assert.assertTrue(ltrType.equals(reqLtrType),"The letter type in the CST table is not '"+reqLtrType+"' for TC#"+tcNo+"");
		log("The member is found in the mgd06761.xml for E8 letter");
        return date;
		}
	

	public static String checkMaxAge(String sql,String tcNo) throws Exception{//NOT COMPLETED
    colNames.add("CYCLE_DATE");//0
	colNames.add("END_DATE");//1
	colNames.add("DTE_TERMED");//2
	colNames.add("ID_MEDICAID");//3
	colNames.add("DTE_BIRTH");//4
	colValues=Common.executeQuery(sql,colNames);
	String cycleDate=colValues.get(0);
	String endDate=colValues.get(1);
	String dateTermed=colValues.get(2);
	String memId=colValues.get(3);
	String DOB=colValues.get(4);
	System.out.println("Member Id: "+memId);
    Assert.assertTrue(dateTermed.equals(cycleDate),"The member Date termed is not equal to cycle date");
	log("Member Id: "+memId+", Cycle Date: "+cycleDate+", DOB: "+DOB);
	Managedcare_Common.insertDataSql(tcNo,memId,"Max Age Member",Common.convertSysdate());
	Managedcare_Common.insertDataSql(tcNo+"end",endDate,"Max Age End Date",Common.convertSysdate());
	return dateTermed;
	}
	
	
	public static String addressChange(String sql,String aidCat,String city,String zipCode,String tcNo) throws Exception{
	colNames.add("ID_MEDICAID");//0
	colNames.add("DTE_END");//1
    colNames.add("SAK_CDE_AID");//2
	colNames.add("CDE_AGENCY");//3
	colValues=Common.executeQuery(sql, colNames);
	memId=colValues.get(0);String dteEnd=colValues.get(1);String sakCdeAid=colValues.get(2);String agency=colValues.get(3);
	System.out.println(dteEnd);
	System.out.println(memId);
	System.out.println(sakCdeAid);
	System.out.println(agency);
	memId=xmlData(memId,agency,sakCdeAid,dteEnd);
	log("Member Id: "+memId);
	eligDateXmlFormat=Member.fileSysdateCustom(-10);
	String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<EligibilityRequest xmlns=\"http://xmlns.hhs.ma.gov/HHS/serviceobjects/versions/2.9/EligibilityServices\" "+
				"\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<transactionsource\nid_medicaid=\""+memId+"\"\nid_other=\""+Member.otherid+"\"\nid_source=\""+agency+"\"" +
				"\n/>\n<demographic\nnam_last=\""+lastName+"\"\nnam_first=\""+fname+"\"\nnum_primary_ssn=\"236591249\"\nres_adr_street_1=\"101 ADAMS Street\"\nres_adr_city=\""+city+"\"\n" +
				"res_adr_state=\"MA\"\nres_adr_zip_code=\""+zipCode+"\"\nmail_adr_street_1=\""+mailAddress+"\"\nmail_adr_city=\""+mailCity+"\"\nmail_adr_state=\"MA\"\nmail_adr_zip_code=\""+mailZipCode+"\"\ncde_lang_written=\""+lang+"\"\ncde_sex=\""+sex+"\"\ncde_race=\""+race+"\"\ncde_citizen=\"C\"\ndte_birth=\""+dob+"\"" +
				"\n/>\n<case\nnum_case=\""+casenum+"\"\ncde_case_status=\"1\"\nhoh_nam_first=\""+fname+"\"\nhoh_nam_last=\""+lastName+"\"\nhoh_nam_init=\""+midInit+"\"\n/>\n<eligibility\ncde_line=\""+lineCode+"\"\ndte_begin_elig=\""+Member.fileSysdateCustom(-10)+"\"\n" +
				"cde_elig_status=\"1\"\ncde_cat=\""+aidCat+"\"\ncde_open_reason=\""+eligStartReason+"\"\nfamily_size=\""+familySize+"\"\ndte_appl=\""+applDate+"\"\ncde_region=\""+regionCode+"\"\ncde_office=\""+officeCode+"\"\namt_gross_income=\""+incomeAmount+"\"\n/>\n</EligibilityRequest>");
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
	 String Mid=Member.getCustomMemebr(xml);
	// Assert.assertTrue(Member.result_type.equals("S"),"The result type is not Successfull:"+errorMessage);
	 Managedcare_Common.insertDataSql(tcNo,Mid,"MCO Member Id",Common.convertSysdate());
	 Managedcare_Common.memSrch(Mid);
	 Common.cancelAll();
	 driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n131")).click();//Member Address panel
	 int rowNum=driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:OtherAddressPanel:OtherAddressList:tbody_element']/tr")).size();
     for(i=1;i<=rowNum;i++){
	 if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:OtherAddressPanel:OtherAddressList:tbody_element']/tr["+i+"]/td[8]")).getText().trim().equals("MR")){
		break;
		 }
	   }
	 Assert.assertTrue(driver.findElement(By.id("MMISForm:MMISBodyContent:OtherAddressPanel:OtherAddressList_"+(i-1)+":OtherAddressBean_ColValue_addressLine1")).getText().trim().equals("101 FEDERAL STREET"),"New Address is not updated");
	 Common.cancelAll();
	 return memId;
	 }
	
	
	
	public static void checkPotentialPanel(String memId,String reason){
    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ManagedCare")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignNavigatorPanel:RePmpAssignNavigator:ITM_n202")).click();//Potetnial MC members
	driver.findElement(By.id("MMISForm:MMISBodyContent:ReMcRecipPanel:ReMcRecipSearchResultBean_CriteriaPanel:ReMcRecipSearchResultDataPanel_MedicaidID")).sendKeys(memId);
	driver.findElement(By.id("MMISForm:MMISBodyContent:ReMcRecipPanel:ReMcRecipSearchResultBean_CriteriaPanel:SEARCH")).click();
    int rowNum=driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:ReMcRecipPanel:MCRecSearchResults:tbody_element']/tr")).size();
     for(i=1;i<=rowNum;i++){
	 if(!(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ReMcRecipPanel:MCRecSearchResults:tbody_element']/tr["+i+"]/td[3]")).getText().trim().contains("DNTL"))){
		break;
		 }
	   }
	Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:ReMcRecipPanel:MCRecSearchResults:tbody_element']/tr/td[6]/span")).getText().trim().contains(reason),"The "+reason+" is not updated in the Potential table");	
	}
	
	
	
	public static String checkESRDTable(String sql1,String sql2) throws SQLException{
	sql=sql1;
	colNames.add("ID_MEDICAID");
	colValues=Common.executeQuery(sql,colNames);
	memId=colValues.get(0);
	if(memId.equals("null")){
		sql=sql2;
		colNames.add("ID_MEDICAID");
		colValues=Common.executeQuery(sql,colNames);
		memId=colValues.get(0);
	    }
	return memId;
	}
	
	
	
	public static void checkAge(String memId){
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
    driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memId);
//	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys("100037121736");
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
	//driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id21")).click();
	driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
	if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[10]/td[2]")).getText().trim().contains("65")){
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n1")).click();//clicking Base Information tab	
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientPanel:RecipientDataPanel_BirthDate")).clear();
	driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientPanel:RecipientDataPanel_BirthDate")).sendKeys(Common.convertSysdatecustom(-10950));
	Common.save();
	    }
	
	}
	
	
	
	
	public static String addMonthsToDate(String date,int i) throws Exception {
	SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");
	Date date1 = sdf.parse(date);//Converting String date to Date format
	System.out.println(date1);
	System.out.println(sdf.format(date1));
 
	Calendar actDate = Calendar.getInstance(); 
    actDate.setTime(date1);
    actDate.add(Calendar.MONTH, i ); //adding months to given date
    Date finaldate=actDate.getTime(); //Final date in date format
    System.out.println("Final date: "+(sdf.format(finaldate).toString()));//Converts date format to String
	return  sdf.format(finaldate).toString();
    }
	
	
	public static String addDaysToDate(String date,int i) throws Exception {
	SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");
	Date date1 = sdf.parse(date);//Converting String date to Date format
	System.out.println(date1);
	System.out.println(sdf.format(date1));
 
	Calendar actDate = Calendar.getInstance(); 
    actDate.setTime(date1);
	actDate.add(Calendar.DATE, i ); //adding dates to given date
	Date finaldate=actDate.getTime(); //Final date in date format
	System.out.println("Final date: "+(sdf.format(finaldate).toString()));//Converts date format to String
	return  sdf.format(finaldate).toString();
	}
		
	
	
	
	
	public static String nonSTDtoSTDdisabled(String tcNo,String memId,String effDate,String eEndDate,String pEndDate,String sakCdeAid,int effDateAdd,int endDateAdd,String aidCat) throws Exception{
	
	/*colNames.add("ID_MEDICAID");//0
	colNames.add("DTE_EFFECTIVE");//1
	colNames.add("ELIG_END_DATE");//2
	colNames.add("ASSGN_END_DATE");//3
	colNames.add("SAK_CDE_AID");//4
	colValues=Common.executeQuery(sql, colNames);
	memId=colValues.get(0);
	String effDate=colValues.get(1);
	String eEndDate=colValues.get(2);
	pEndDate=colValues.get(3);
	String sakCdeAid=colValues.get(4);*/
	
	log("Member Id: "+memId+", Effective Date: "+effDate+", End Date: "+pEndDate);
	String dbDate=addDaysToDate(effDate, effDateAdd);
	String xmlEffDate=dbDate.substring(0,4)+"-"+dbDate.substring(4,6) +"-"+dbDate.substring(6,8);
	dbDate=addMonthsToDate(effDate, endDateAdd);
	String xmlEndDate=dbDate.substring(0,4)+"-"+dbDate.substring(4,6) +"-"+dbDate.substring(6,8);
	if(tcNo.equals("32411")){
		xmlEndDate="2299-12-31";
	    }
	
	memId=xmlData(memId,"MHO",sakCdeAid,eEndDate);
	
	String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<EligibilityRequest xmlns=\"http://xmlns.hhs.ma.gov/HHS/serviceobjects/versions/2.9/EligibilityServices\" "+
				"\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<transactionsource\nid_medicaid=\""+memId+"\"\nid_other=\""+Member.otherid+"\"\nid_source=\"MHO\"" +
				"\n/>\n<demographic\nnam_last=\""+lastName+"\"\nnam_first=\""+fname+"\"\nnum_primary_ssn=\""+ssn+"\"\nres_adr_street_1=\"101 Federal Street\"\nres_adr_city=\""+city+"\"\n" +
				"res_adr_state=\"MA\"\nres_adr_zip_code=\"02110\"\nmail_adr_street_1=\""+mailAddress+"\"\nmail_adr_city=\""+mailCity+"\"\nmail_adr_state=\"MA\"\nmail_adr_zip_code=\""+mailZipCode+"\"\ncde_lang_written=\""+lang+"\"\ncde_sex=\""+sex+"\"\ncde_race=\""+race+"\"\ncde_citizen=\"C\"\ndte_birth=\""+dob+"\"" +
				"\n/>\n<case\nnum_case=\""+casenum+"\"\ncde_case_status=\"1\"\nhoh_nam_first=\""+fname+"\"\nhoh_nam_last=\""+lastName+"\"\nhoh_nam_init=\""+midInit+"\"\n/>\n<eligibility\ncde_line=\""+lineCode+"\"\ndte_begin_elig=\""+xmlEffDate+"\"\ndte_end_elig=\""+xmlEndDate+"\"\n" +
				"cde_elig_status=\"1\"\ncde_cat=\""+aidCat+"\"\ncde_open_reason=\""+eligStartReason+"\"\nfamily_size=\""+familySize+"\"\ndte_appl=\""+applDate+"\"\ncde_region=\""+regionCode+"\"\ncde_office=\""+officeCode+"\"\namt_gross_income=\""+incomeAmount+"\"\n/>\n</EligibilityRequest>");
	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
	 String Mid=Member.getCustomMemebr(xml);
	 //Assert.assertTrue(Member.result_type.equals("S"),"The result type is not Successfull:"+errorMessage);
	 log("Effective date of new STD Disabled Aidcat: "+xmlEffDate);
	 log("End date of new STD Disabled Aidcat: "+xmlEndDate);
	 Managedcare_Common.insertDataSql(tcNo,Mid,"MCO NonSTD Member Id",Common.convertSysdate());
	 return memId;
	 }
	
	
	public static String getCaseNum(String memId) throws Exception{
	sql="select num_case from t_re_case c,t_re_base b where sak_case=(select sak_case from t_re_case_xref where sak_recip=(select sak_recip from t_re_base where id_medicaid='"+memId+"') " +
		"and cde_agency='MHO' and rownum<2) and b.sak_recip=(select sak_recip from t_re_base where id_medicaid='"+memId+"') and rownum<2";
	colNames.add("NUM_CASE");
	colValues=Common.executeQuery(sql, colNames);
	return colValues.get(0);
	
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
   		if(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+i+"]/td[5]")).equals(EndDate)); //status
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
  
  
  public static String enrollment(String sql,String mcProgram,String startReason,String providerId,String effDate) throws SQLException, IOException{
		 
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
 //  driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
   
 /*  driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memberId);*/
   
//   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_OtherId")).clear();
//   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_OtherId")).sendKeys(memberId);
//   
//   
//   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
//   driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
//   memberId=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText();*/
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
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id375:RePmpDistanceSearchCriteriaPanel:DistanceArrayList"))).selectByValue("25");
        new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id375:RePmpDistanceSearchCriteriaPanel:InfoSpecList"))).selectByValue("");
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
   
   
   if(mcProgram.equals("PACE")){
  	 driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n135")).click();
  	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:ClinicalInformation_NewButtonClay:ClinicalInformationList_newAction_btn")).click();
  	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_ServiceRequestCode"))).selectByVisibleText("PACE - PROG FOR ALL INCL CARE OF THE ELDERLY");
  	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_AssessmentDate")).clear();
  	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_AssessmentDate")).sendKeys(Common.firstDateOfPreviousMonth());
  	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_SCOEvalReason"))).selectByVisibleText("N - Nursing Home Certifiable");
  	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_AssessmentReason"))).selectByVisibleText("1 - INITIAL ASSESSMENT");
  	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_PaceApprovalDate")).clear();
  	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_PaceApprovalDate")).sendKeys(Common.firstDateOfPreviousMonth());
  	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:ClinicalInformationPanel_addAction_btn")).click();
       }
   
   Common.save();
   System.out.println("Member enrolled successfully: "+memberId);
      
     //Script added for TPT Testing
   if(!((mcProgram.equals("PACE"))||(mcProgram.equals("SCO"))||(mcProgram.equals("ICO")))){
		//sql="select * from t_re_base where id_medicaid='"+memberId+"'";
	  // Managedcare_Common.enrollment(sql,"DNTL1","62","110111117A",Common.convertSysdate());
	   Common.cancelAll();
	   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_RePmpAssignSu")).click();
	   driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_NewButtonClay:RePmpAssignSuList_newAction_btn")).click();
	   new Select(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_McProgramList']"))).selectByValue("DNTL1");
	   driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_EffectiveDate")).clear();
	   driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_EffectiveDate")).sendKeys(Common.convertSysdate());
	   driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_PMPIDSvcLoc")).clear();
	   driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_PMPIDSvcLoc']")).sendKeys("110111117"); //code change during 18.01
	   driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_RePmpHisDistanceSearchHis']")).sendKeys("A");
	   new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_StartReasonDropDownEntries"))).selectByValue("62");
	   Common.save();
	   System.out.println("Member enrolled to DNTL1 successfully: "+memberId);
	   //log("Member Id: "+memberId+" enrolled to DNTL1");
	    
        }
   
   
   return memberId;
   }
  
  
   public static String transfer(String sql,String mcProgram,String startReason,String mcProgramTo,String sph,String effDate) throws Exception{
	   int i=0;
  	  String sqlStatement1=sql;
	  colNames.add("ID_MEDICAID");
	  colValues=Common.executeQuery(sql,colNames);
	  memberId=colValues.get(0);
	  System.out.println("Member id from query: "+memberId);
	  driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
      driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSeaarchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memberId);
      driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
      driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_RePmpAssignSu")).click();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_NewButtonClay:RePmpAssignSuList_newAction_btn")).click();
      new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_McProgramList"))).selectByValue(mcProgramTo);
      if(!(effDate.equals(" "))){
    	  driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_EffectiveDate")).clear();
    	  driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_EffectiveDate")).sendKeys(effDate);
          }
                String provId=getProvId(memberId,sph,"22991231");
      System.out.println("Provider id from transfer method: "+provId);
      driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_RePmpHisDistanceSearchHis_CMD_SEARCH']/img")).click();
      //If transferring to PCCP,search panel and results panel is different
      if(mcProgramTo.equals("PCCP")||mcProgramTo.equals("CPPCC")){
      new Select(driver.findElement(By.xpath("//select[contains(@id,'DistanceArrayList')]"))).selectByValue("90");  //04
      driver.findElement(By.xpath("//*[contains(@id,'RePmpDistanceSearchCriteriaPanel:SEARCH')]")).click();
      int rownum=driver.findElements(By.xpath("//*[contains(@id,'/tr')]")).size();
    
    for(i=1;i<=rownum;i++){
    	   if(!(driver.findElement(By.xpath("//*[contains(@id,'RePmpDistanceSearchResults_"+(i-1)+":column3Value')]")).getText().contains(provId))){
    	   break;  
    	  }
        }
     driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id371:RePmpDistanceSearchResults_0:column1Value")).click();  //18.01
      }
      else{
      driver.findElement(By.xpath("//*[contains(@id,'RePmpDistanceSearchCriteriaPanel:SEARCH')]")).click(); 
      int rownum=driver.findElements(By.xpath("//*[contains(@id,'RePmpDistanceSearchResults:tbody_element')]")).size();
      System.out.println("Rownum: "+rownum);
      for(i=1;i<=rownum;i++){
    	  if(!(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id371:RePmpDistanceSearchResults:tbody_element']/tr["+i+"]/td[2]")).getText().contains(provId))){
    	  break;
    	  }
        }
      int j=1;
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:_id371:RePmpDistanceSearchResults_"+(j-1)+":column2Value")).click();
      }
      new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_StartReasonDropDownEntries"))).selectByValue(startReason);
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuHistoryPanel_addAction_btn")).click();
      //Need to add Clinical Information for PACE
      if(mcProgramTo.equals("PACE")){
    	 driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n135")).click();
    	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:ClinicalInformation_NewButtonClay:ClinicalInformationList_newAction_btn")).click();
    	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_ServiceRequestCode"))).selectByVisibleText("PACE - PROG FOR ALL INCL CARE OF THE ELDERLY");
    	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_AssessmentDate")).clear();
    	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_AssessmentDate")).sendKeys(Common.firstDateOfPreviousMonth());
    	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_SCOEvalReason"))).selectByVisibleText("N - Nursing Home Certifiable");
    	 new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_AssessmentReason"))).selectByVisibleText("1 - INITIAL ASSESSMENT");
    	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_PaceApprovalDate")).clear();
    	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:id_PaceApprovalDate")).sendKeys(Common.firstDateOfPreviousMonth());
    	 driver.findElement(By.id("MMISForm:MMISBodyContent:ClinicalInformationPanel:ClinicalInformationPanel_addAction_btn")).click();
         }
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
      if(message.contains("No date overlaps are allowed for mutually exclusive")){
    	log("Member id with No date overlap error: "+memberId);
        i=i++; 
        sqlStatement1=sqlStatement1+" and b.id_medicaid<>'"+memberId+"'";
        System.out.println(sqlStatement1);
        colNames.add("id_medicaid");
        colValues=Common.executeQuery(sqlStatement1,colNames);
        String newMemberId=colValues.get(0);
	       System.out.println("New member id"+i+": "+newMemberId);
	       memberId=newMemberId;
	       Common.cancelAll();
	       driver.findElement(By.id("MMISForm:MMISHeader:header_value_home")).click();
	       transfer(sqlStatement1,mcProgram,startReason,mcProgramTo,sph,effDate); 
	       }
      
      else{
          Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
          
         
      System.out.println("Transferring "+memberId+" from "+mcProgram+" to "+mcProgramTo+" is completed");
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:RePmpAssignSuBean_ColHeader_endDate")).click();//Sorting on end date
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:RePmpAssignSuBean_ColHeader_endDate")).click();
      Common.cancelAll();
      driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_ManagedCare")).click();
      }
      return memberId;
      }
   
   
   



      public static String transferThruMCPD(String tcNo,String sql,String mcProgram,String startReason,String mcProgramTo,String sph,String dname) throws SQLException, IOException{
	  int i=0; 
	  String sqlStatement1=sql;
	  colNames.add("ID_MEDICAID");
	  colValues=Common.executeQuery(sql,colNames);
	  memberId=colValues.get(0);
	  driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
      driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memberId);
      driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
      driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_RePmpAssignSu")).click();
      String provid=getProvId(memberId,sph,"22991231");
     // String provid=ManagedCare.searchSuPanel(mcProgram,"12/31/2299");
      String provider=provid.substring(0, provid.length()-1),
	           svcloc=provid.substring(provid.length()-1);
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_NewButtonClay:RePmpAssignSuList_newAction_btn")).click();
      new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_McProgramList"))).selectByValue(mcProgramTo);
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuHistoryPanel_showPDSearchSelectionPanel_btn")).click();
      int rownum= new Select(driver.findElement(By.xpath("//select[contains(@id,'Mc"+dname+"ProviderDataPanel_McoList')]"))).getOptions().size();
      System.out.println("Row num in the drop down: "+rownum);
      for(i=1;i<=rownum;i++){
    	  new Select(driver.findElement(By.xpath("//select[contains(@id,'Mc"+dname+"ProviderDataPanel_McoList')]"))).selectByIndex(i);
          String selected=new Select(driver.findElement(By.xpath("//select[contains(@id,'Mc"+dname+"ProviderDataPanel_McoList')]"))).getFirstSelectedOption().getText();
      if(!(selected.contains(provider+"/"+svcloc)||selected.contains("110031450/B")||selected.contains("110088791/A")||selected.contains("110031467/A"))){  
    	  System.out.println("Selected: "+selected);
    	  break;
          }
        }
     driver.findElement(By.xpath("//*[contains(@id,'"+dname+"SelectionSearchSUBean_CriteriaPanel:SEARCH')]")).click();
                                                          
     if(tcNo.contains("31710")){
    	  driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:McPLPDSelectionSearchPanel:McPLPDSUSelectionList_0:McPLPDSelectionSearchSUBean_ColValue_provId")).click();
    	   driver.findElement(By.xpath("//*[contains(@id, '0:Mc"+dname+"SelectionSearchSUBean_ColValue_provId')]")).click();
    	
    	   }
      else{
    	  driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:MCPDSelectionSearchPanel:MCPDSelectionList_1:editAction_MCPDSelectionSearchSUBean_provId")).click(); 
        }
      driver.findElement(By.xpath("//*[contains(@id,'Mc"+dname+"ProviderPanel_selectProvider_btn')]")).click();
      new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_StartReasonDropDownEntries"))).selectByValue(startReason);
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuHistoryPanel_addAction_btn")).click();
      driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
      driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	         for(int j=0;j<length;j++)
	          {
	        	 driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(j).click();
	          }
	         driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
			}
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
      String message=driver.findElement(By.cssSelector("td.message-text")).getText();
      if(message.contains("No date overlaps are allowed for mutually exclusive")){
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
	       transferThruMCPD(tcNo,sqlStatement1,mcProgram,startReason,mcProgramTo,sph,dname); 
	       }
      
      else{
          Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
          }
      System.out.println("Transferring "+memberId+" from "+mcProgram+" to "+mcProgramTo+" is completed");
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:RePmpAssignSuBean_ColHeader_endDate")).click();//Sorting on end date
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:RePmpAssignSuBean_ColHeader_endDate")).click();
      return memberId;
      }
   
   
   
      public static String transferThruHistoryPanel(String sql,String mcProgram,String startReason,String mcProgramTo,String sph,String dte_end) throws Exception{
	  int i=0;
  	  String sqlStatement1=sql;
	  colNames.add("ID_MEDICAID");
	  colValues=Common.executeQuery(sql,colNames);
	  memberId=colValues.get(0);
	  System.out.println("Member id from query: "+memberId);
	  driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
      driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memberId);
      driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
     // driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchResults_0:_id21")).click();
      driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_RePmpAssign")).click();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:RePmpAssignHisList_NewButtonClay:RePmpAssignHisList_newAction_btn")).click();
      new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:RePmpAssignDataPanel_McProgramList"))).selectByValue(mcProgramTo);
      String provId=getProvId(memberId,sph,dte_end);
      driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:RePmpAssignHis_RePmpHisDistanceSearchHis_CMD_SEARCH']/img")).click();
      new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:_id371:RePmpDistanceSearchCriteriaPanel:InfoSpecList"))).selectByValue("");
      new Select(driver.findElement(By.xpath("//select[contains(@id,'DistanceArrayList')]"))).selectByValue("20");
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:_id371:RePmpDistanceSearchCriteriaPanel:SEARCH")).click();
      int rownum=driver.findElements(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:_id371:RePmpDistanceSearchResults:tbody_element']/tr")).size();
      for(i=1;i<=rownum;i++){
    	  if(!(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:_id371:RePmpDistanceSearchResults:tbody_element']/tr["+i+"]/td[2]")).getText().contains(provId))){
    	   break;
    	  }
        }
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:_id371:RePmpDistanceSearchResults_"+(i-1)+":column1Value")).click();  //2
      new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:RePmpAssignHis_StartReason"))).selectByValue(startReason);
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:RePmpAssignHistoryPanel_addAction_btn")).click();
      driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
      String message=driver.findElement(By.cssSelector("td.message-text")).getText();
      if(message.contains("No date overlaps are allowed for mutually exclusive")){
    	log("Member id with No date overlap error: "+memberId);
        i=i++; 
        sqlStatement1=sqlStatement1+" and b.id_medicaid<>'"+memberId+"'";
        System.out.println(sqlStatement1);
        colNames.add("id_medicaid");
        colValues=Common.executeQuery(sqlStatement1,colNames);
        String newMemberId=colValues.get(0);
	       System.out.println("New member id"+i+": "+newMemberId);
	       memberId=newMemberId;
	       Common.cancelAll();
	       driver.findElement(By.id("MMISForm:MMISHeader:header_value_home")).click();
	       transferThruHistoryPanel(sqlStatement1,mcProgram,startReason,mcProgramTo,sph,dte_end); 
	       }
      
      else{
          Assert.assertTrue(message.equals("Save Successful."), "Save NOT successful Error Message: "+message+"...");
          }
      System.out.println("Transferring "+memberId+" from "+mcProgram+" to "+mcProgramTo+" is completed");
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:RePmpAssignHisList:RePmpAssignHistoryBean_ColHeader_endDate")).click();//Sorting on end date
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignHistoryPanel:RePmpAssignHisList:RePmpAssignHistoryBean_ColHeader_endDate")).click();
      return memberId;
      }
   
   
      public static void manualDisenroll(String endDate,String stopReason){
    	  int w;
    	 
      driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_RePmpAssignSu")).click();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:RePmpAssignSuBean_ColHeader_endDate")).click();//Sorting on end date
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:RePmpAssignSuBean_ColHeader_endDate")).click();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_0:RePmpAssignSuBean_ColValue_status")).click();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_EndDate")).clear();
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignDataPanel_EndDate")).sendKeys(endDate);
      new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_StopReason"))).selectByValue(stopReason);
      
      driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
      //Adding Warning logic to avoid  Fixed Enrollment Period warning
      driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if (driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).size()>0){
			int length=(driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]"))).size();
	        for(w=0;w<length;w++)
	         driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(w).click();
	         //selecting stop reason code again
	       
	         driver.findElement(By.xpath("//*[contains(@id,'0:RePmpAssignSuBean_ColValue_status')]")).click();
	       
	         new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignHisSu_StopReason"))).selectByValue(stopReason);
	         driver.findElements(By.xpath("//input[@type='checkbox' and contains(@name ,'Warnings')]")).get(w-1).click();
	        
		     driver.findElement(By.xpath("//input[@class='buttonImage' and @alt='Save All']")).click();
		     }
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
   //   Common.save();  
      }
      
      
      public static String getMedicareId(String memId){
      memSrch(memId);
      Common.cancelAll();
      String medicareId=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[4]/td[2]")).getText().trim();
	  return medicareId;  
      }
      
      
      public static void massTransferRunSql(String sql) throws SQLException{
      colNames.add("PROV"); //0
      colNames.add("ADR_CITY"); //1
      colNames.add("S_23"); //2 PCC sak
      colNames.add("S_95"); //3 CP PCC sak
      colValues=Common.executeQuery(sql, colNames);
      prov=colValues.get(0);  
      city=colValues.get(1);
      sakPCC=colValues.get(2);
      sakCPPCC=colValues.get(3);
      }
      
      
      public static void checkMassTransfer(String tcNo,String mcProgram,String endDate,String status) throws SQLException{
      memId=getDataRday2(tcNo);
      driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
      Managedcare_Common.memSrch(memId);
      Managedcare_Common.searchSuPanel(mcProgram,endDate, status);
      driver.findElement(By.id("MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList_"+(i-1)+":RePmpAssignSuBean_ColValue_status")).click();
      }
      
      
      public static void checkPSFEDates(String memId,String mcProg,String fepEffDate,String fepEndDate){
      memSrch(memId);
  	  searchSuPanel(mcProg, "12/31/2299", "Active");
  	  int j=i;
  	  System.out.println("j value: "+j);
  	  Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+(j)+"]/td[14]")).getText().trim().equals(fepEffDate));
  	  Assert.assertTrue(driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RePmpAssignSuHistoryPanel:RePmpAssignSuList:tbody_element']/tr["+(j)+"]/td[15]")).getText().trim().equals(fepEndDate));  
      }
      
//      public static void check() throws Exception {
//    	 // System.out.println("check0");
//    	  ExcelRead.setexcelfile(filelocation_170);
//  		while(rowpointer<ExcelRead.getlastrownum()) {
//  			excelspreaddata.clear();
//  			//Log.info("********************************************************************************************");
//  			//System.out.println("check1");
//  			excelspreaddata=ExcelRead.getspreaddata(rowpointer);
//  			//System.out.println("check2");
//  			setxmldata(excelspreaddata);
//  			//System.out.println("check3");
//  			rowpointer++;
//  		}
//  		
//      }
  		
      public static void setxmldata(ArrayList<String> data) throws Exception {
  	    String otherid = Common.generateRandomTaxID();
  		fname=Common.generateRandomName();
  		lname="TPT"+Common.generateRandomName();
  		casenum=Common.generateRandomTaxID();
  		ssn="1"+Common.generateRandomTaxID().substring(0,8);
  		String aidcat= data.get(0) ;// Check excel for value
  	    String mcProg= data.get(1) ;// Check excel for value
  	    String provId= data.get(2) ;// Check excel for value
  	    
  	  /*  String parameter= data.get(5); //parameter for change transactions
  	    String sakcdeaid= data.get(6); //parameter for change transactions
  	    memId=data.get(7); //parameter for change transactions*/
  	    
  	    dob =Member.fileSysdateCustom(-6570); //18yrs<21 years
  	    adr1="100 HANCOCK ST";
  		city=data.get(4) ;
  		zip=data.get(3) ;
  		String eligDate=Member.fileSysdateCustom(-365);
  		
  		
  		getNewMemebr(aidcat,otherid,fname, lname, ssn, casenum, dob, adr1, city, zip,mcProg,eligDate,provId);
		
	}
      
      
      public static void getNewMemebr(String aidcat,String otherid,String fname, String lname, String ssn, String casenum, String dob,String adr1, String newcity, String newzip,String mcProg,String eligDate,String provId) throws Exception{
    	  
    //New Enrollments
    	  if(mcProg.contains("CRPL")) {
    		 dob=Member.fileSysdateCustom(-8030); 
    		 System.out.println("CRPL dob: "+dob);
    	 }
    	 else if(mcProg.equals("SCO")){
    		 dob=Member.fileSysdateCustom(-24090); 
    		 System.out.println("SCO dob: "+dob);
    	 }
    	 else if(mcProg.equals("PACE")){
    		 dob=Member.fileSysdateCustom(-20440); 
    		 System.out.println("PACE dob: "+dob);
    	 }
         else if(mcProg.equalsIgnoreCase("ICO")){
        			dob =Member.fileSysdateCustom(-getRandomDOB(22, 64));
        		 System.out.println("ICO dob: "+dob);
        		 }
    	 
    	//Get Member ID
 		String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><EligibilityRequest\nxmlns=\"http://xmlns.hhs.ma.gov/HHS/serviceobjects/versions/2.9/EligibilityServices\"\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<transactionsource id_medicaid=\" \" "+
 					"id_other=\""+otherid+"\" id_source=\"MHO\"/>\n<demographic cde_citizen=\"C\" cde_lang_written=\"ENG\" cde_race=\"WHITE\" cde_sex=\"F\" dte_birth=\""+dob+"\" nam_first=\""+fname+"\" nam_last=\""+lname+"\" nam_mid_init=\" \" num_primary_ssn=\""+ssn+"\" res_adr_city=\""+city+"\" res_adr_state=\"MA\" res_adr_street_1=\""+adr1+"\" res_adr_street_2=\" \" res_adr_zip_code=\""+zip+"\"/>"+
 					"<case cde_case_status=\"1\" hoh_nam_first=\""+fname+"\" hoh_nam_init=\" \" hoh_nam_last=\"TPT Test\" num_case=\""+casenum+"\"/>\n"+
 					"<eligibility amt_gross_income=\"1429.22\" cde_cat=\""+aidcat+"\" cde_elig_status=\"1\" cde_line=\"00\" cde_office=\"600\" cde_open_reason=\"01\" cde_region=\"01\" dte_appl=\""+dob+"\" dte_begin_elig=\""+eligDate+"\" family_size=\"06\" />\n</EligibilityRequest>");
 		driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
 		String Mid=Member.getCustomMemebr1(xml);
 		 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Search")).click();
 		  /* driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).clear();
 		   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_MedicaidID")).sendKeys(memberId);*/
 		   
 		   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_OtherId")).clear();
 		   driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:RecipientSearchResultDataPanel_OtherId")).sendKeys(Mid);
 		  driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientSearchBean_CriteriaPanel:SEARCH")).click();
 		   driver.findElement(By.xpath("//*[contains(@id, 'RecipientSearchResults_0')]")).click();
 		   Mid=driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:RecipientInformationBean_DataPanel']/tbody/tr/td/table/tbody/tr/td[1]/table/tbody/tr[2]/td[2]")).getText();
 		  
 		   if(mcProg.equalsIgnoreCase("ICO")){
 			  addMedicare(Mid,ssn);
 		      }
 		   Member.memberSearch(Mid);
 		
// 		System.out.println("Result type: "+Member.result_type);
// 		Assert.assertTrue(Member.result_type.equals("S"),"The result type is not Successfull:"+errorMessage);
 		sql="select * from t_re_base where id_medicaid='"+Mid+"'";
 		
 		Mid=Managedcare_Common.enrollment(sql,mcProg,"62",provId,Common.convertSysdate());
 		
 		
 	/*	if(mcProg.contains("CRPL")){
 			dname="PLPD";
 		    }
 		else if(mcProg.equals("MSTDA")||mcProg.equals("MFASA")){
 			dname="ACPD";
 		}
 		else{
 			dname="MCPD";
 		}
 		
 		  String provider=provId.substring(0, provId.length()-1),
 			        svcloc=provId.substring(provId.length()-1);
 		  provId=provider+"/"+svcloc;
 		  System.out.println("Provider Id: "+provId);
 		enrollThruMCPD(sql,mcProg,Common.convertSysdatecustom(-30),provId,"62",dname);
 		String pcp=driver.findElement(By.xpath("//input[contains(@id, 'RePmpAssignDataPanel_memberPCPHistoryPCP_provIDExtn')]")).getAttribute("value");
 		log("Member Id: "+Mid+", Provider Id: "+provId+",PCP: "+pcp+", Aid Cat: "+aidcat);*/
 		
 		log("Member Id: "+Mid+", Provider Id: "+provId+", Aid Cat: "+aidcat);
 		driver.findElement(By.id("MMISForm:MMISHeader:header_value_home")).click();
    	 
    	
    /*	 xmlData(memId,"MHO",sakcdeaid,"22991231");
    	 
    	  //Change Transactions
    	 if(parameter.equals("DOB")){
    		 dob=Member.fileSysdateCustom(-8030); 
    		 System.out.println("new dob: "+dob);
    	     }
    	 else if(parameter.equals("SSN")){
    		 ssn="1"+Common.generateRandomTaxID().substring(0,8);
    		 System.out.println("new ssn: "+ssn);
    	     }
    	 else if(parameter.equalsIgnoreCase("NAME")){
    		 fname=Common.generateRandomName();
     		 lname="TPT"+Common.generateRandomName();
    	     }
    	 else if(parameter.equalsIgnoreCase("ADDRESS")){
    		 adr1="101 FEDERAL ST";
    	     }
    	 
    	 String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<EligibilityRequest xmlns=\"http://xmlns.hhs.ma.gov/HHS/serviceobjects/versions/2.9/EligibilityServices\" "+
 			    "\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<transactionsource\nid_medicaid=\""+memId+"\"\nid_other=\""+Member.otherid+"\"\nid_source=\"MHO\"" +
 			    "\n/>\n<demographic\nnam_last=\""+lname+"\"\nnam_first=\""+fname+"\"\nnum_primary_ssn=\""+ssn+"\"\nres_adr_street_1=\""+adr1+"\"\nres_adr_city=\""+newcity+"\"\n" +
 			    "res_adr_state=\"MA\"\nres_adr_zip_code=\""+newzip+"\"\nmail_adr_street_1=\""+mailAddress+"\"\nmail_adr_city=\""+mailCity+"\"\nmail_adr_state=\"MA\"\nmail_adr_zip_code=\""+mailZipCode+"\"\ncde_lang_written=\""+lang+"\"\ncde_sex=\""+sex+"\"\ncde_race=\""+race+"\"\ncde_citizen=\"C\"\ndte_birth=\""+dob+"\"" +
 			    "\n/>\n<case\nnum_case=\""+casenum+"\"\ncde_case_status=\"1\"\nhoh_nam_first=\""+fname+"\"\nhoh_nam_last=\""+lname+"\"\nhoh_nam_init=\""+midInit+"\"\n/>\n<eligibility\ncde_line=\""+lineCode+"\"\ndte_begin_elig=\""+eligDateXmlFormat+"\"\n" +
 			    "cde_elig_status=\"1\"\ncde_cat=\""+catCode+"\"\ncde_open_reason=\""+eligStartReason+"\"\nfamily_size=\""+familySize+"\"\ndte_appl=\""+applDate+"\"\ncde_region=\""+regionCode+"\"\ncde_office=\""+officeCode+"\"\namt_gross_income=\""+incomeAmount+"\"\n/>\n</EligibilityRequest>");
 	/*	String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<EligibilityRequest xmlns=\"http://xmlns.hhs.ma.gov/HHS/serviceobjects/versions/2.9/EligibilityServices\" "+
 			    "\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<transactionsource\nid_medicaid=\""+memId+"\"\nid_other=\""+Member.otherid+"\"\nid_source=\"MHO\"" +
 			    "\n/>\n<demographic\nnam_last=\""+lastName+"\"\nnam_first=\""+fname+"\"\nnum_primary_ssn=\""+ssn+"\"\nres_adr_street_1=\""+adr1+"\"\nres_adr_city=\""+city+"\"\n" +
 			    "res_adr_state=\"MA\"\nres_adr_zip_code=\""+zip+"\"\nmail_adr_street_1=\""+mailAddress+"\"\nmail_adr_city=\""+mailCity+"\"\nmail_adr_state=\"MA\"\nmail_adr_zip_code=\""+mailZipCode+"\"\ncde_lang_written=\""+lang+"\"\ncde_sex=\""+sex+"\"\ncde_race=\""+race+"\"\ncde_citizen=\"C\"\ndte_birth=\""+dob+"\"\n" +
 			    "\n/>\n<case\nnum_case=\""+casenum+"\"\ncde_case_status=\"1\"\nhoh_nam_first=\""+fname+"\"\nhoh_nam_last=\""+lastName+"\"\nhoh_nam_init=\""+midInit+"\"\n/>\n<eligibility\ncde_line=\""+lineCode+"\"\ndte_begin_elig=\""+eligDateXmlFormat+"\"\n" +
 			    "cde_elig_status=\"4\"\ncde_cat=\""+catCode+"\"\ncde_open_reason=\"0"+eligStartReason+"\"\ncde_close_reason=\"00\"\nfamily_size=\""+familySize+"\"\ndte_appl=\""+applDate+"\"\ncde_region=\""+regionCode+"\"\ncde_office=\""+officeCode+"\"\namt_gross_income=\""+incomeAmount+"\"\n/>\n</EligibilityRequest>");*/
 	//driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
 	//String Mid=Member.getCustomMemebr(xml);   */
    	 
    	 
    	     
    	  
    	
    	  
    	 
      }
      
      public static void check() throws Exception {
    	 ExcelRead.setexcelfile(filelocation_170);
  		while(rowpointer<ExcelRead.getlastrownum()) {
  			excelspreaddata.clear();
  			excelspreaddata=ExcelRead.getspreaddata(rowpointer);
  			setxmldata(excelspreaddata);
  			rowpointer++;
  		}
  		
      } 
      		
      public static int getRandomDOB(int minAge, int maxAge) {
          Random r = new Random();
          int Low = minAge*365;
          int High = maxAge*365;
          int Result = r.nextInt(High-Low) + Low;
          return Result;
}
      
  	public static void addMedicare(String memId,String ssn){
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:GRP_Medicare")).click();
		driver.findElement(By.linkText("Medicare ID")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareIdPanel:MedicareID_NewButtonClay:MedicareIDList_newAction_btn")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareIdPanel:HibDataPanel_MedicareID")).sendKeys(ssn+"M");
		Common.saveAll();
		Common.cancelAll();
		//Medicare-A coverage
		driver.findElement(By.linkText("Medicare A Coverage")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicareACoverage_NewButtonClay:MedicareACoverageList_newAction_btn")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicareADataPanel_MedicareID")).sendKeys(ssn+"M");
		driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicareADataPanel_EffectiveDate")).sendKeys(Common.firstDateOfPreviousMonth());
		driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicareADataPanel_EndDate")).sendKeys("12/31/2299");
		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicareADataPanel_HIOptionCodeSearch_CMD_SEARCH']/img")).click();
		driver.findElement(By.xpath("//*[contains(@id,'4:column1Value')]")).click();
		driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicareADataPanel_PremPayorCodeSearch")).sendKeys("S22");
		new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareACoveragePanel:MedicarADataPanel_CdeEntitleRsn"))).selectByValue("5");//Unknown
		Common.saveAll();
		Common.cancelAll();
		//Medicare-B coverage
		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_n118")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBCoverage_NewButtonClay:MedicareBCoverageList_newAction_btn")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_MedicareID")).sendKeys(ssn+"M");
	    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_EffectiveDate")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_EffectiveDate")).sendKeys(Common.firstDateOfPreviousMonth());
	    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_EndDate")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_EndDate")).sendKeys("12/31/2299");
	    driver.findElement(By.cssSelector("img[alt=\"SMI Option pop-up search\"]")).click();
	    driver.findElement(By.xpath("//*[contains(@id,'3:column1Value')]")).click();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_PremPayorCodeSearch")).clear();
	    driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicareBDataPanel_PremPayorCodeSearch")).sendKeys("220");
	    new Select(driver.findElement(By.id("MMISForm:MMISBodyContent:MedicareBCoveragePanel:MedicarBDataPanel_CdeEntitleRsn"))).selectByValue("5");//Unknown
	    Common.saveAll();
	    Common.cancelAll();	
	  	}
  	

    public static void check_change() throws Exception {
   	 ExcelRead.setexcelfile(filelocation_170);
 		while(rowpointer<ExcelRead.getlastrownum()) {
 			excelspreaddata.clear();
 			excelspreaddata=ExcelRead.getspreaddata(rowpointer);
 			setxmldata_change(excelspreaddata);
 			rowpointer++;
 		}
    }
 		
 		public static void setxmldata_change(ArrayList<String> data) throws Exception {
 	     	  /*  String otherid = Common.generateRandomTaxID();
 	     		fname=Common.generateRandomName();
 	     		lname="TPT"+Common.generateRandomName();
 	     		casenum=Common.generateRandomTaxID();
 	     		ssn="1"+Common.generateRandomTaxID().substring(0,8);*/
 	     		String aidcat= data.get(0) ;
 	     	    String mcProg= data.get(1) ;
 	     	    String provId= data.get(2) ;
 	     	    String newzip=data.get(3) ;
 	     	    String newcity=data.get(4) ;
 	     	    String sakcdeaid= data.get(5);
 	     	    memId=data.get(6);
 	     	    String parameter= data.get(7);
 	     	    String newAidCat= data.get(8);
 	     	    //dob =Member.fileSysdateCustom(-6570); //18yrs<21 years
 	     	    //adr1="100 HANCOCK ST";


 	     		String eligDate=Member.fileSysdateCustom(-65);
 	     		//getNewMemebr_change(aidcat,otherid,fname, lname, ssn, casenum, dob, adr1, city, zip,mcProg,eligDate,provId ,parameter,sakcdeaid,memId);

 	     		getNewMemebr_change(aidcat,newcity, newzip,mcProg,eligDate,provId ,parameter,sakcdeaid,memId,newAidCat);
 	         	}
 		
 		public static void getNewMemebr_change(String aidcat,String newcity, String newzip,String mcProg,String eligDate,String provId,String parameter,String sakcdeaid,String memId,String nAidCat) throws Exception{


            xmlData(memId,"MHO",sakcdeaid,"22991231");


      	  //Change Transactions
       	 if(parameter.equals("DOB")){
      		 //dob=getRandomDOB(1,64);
      		 if(mcProg.equalsIgnoreCase("SCO")){
      			dob =Member.fileSysdateCustom(-getRandomDOB(66, 99));
      		 }
      		 else if(mcProg.equalsIgnoreCase("PACE")){
      			dob =Member.fileSysdateCustom(-getRandomDOB(56, 99));
      		 }
      		 else if(mcProg.equalsIgnoreCase("ICO")){
       			dob =Member.fileSysdateCustom(-getRandomDOB(22,64));
       		 }

      		 else{
      		dob =Member.fileSysdateCustom(-getRandomDOB(1, 64));
      		 }
      		 System.out.println("new DOB: "+dob);
      		 log("Member Id: "+memId+", New DOB: "+dob);
      	     }
      	 else if(parameter.equals("SSN")){
      		 ssn="1"+Common.generateRandomTaxID().substring(0,8);
      		 System.out.println("new ssn: "+ssn);
      		 log("Member Id: "+memId+", New SSN: "+ssn);
      	     }
      	 else if(parameter.equalsIgnoreCase("NAME")){
      		 fname=Common.generateRandomName();
       		 lname="TPT"+Common.generateRandomName();
       		 System.out.println("New fname:"+fname);
       		 System.out.println("New lname:"+lname);
       		 log("Member Id: "+memId+", New FName: "+fname+", New LName: "+lname);
      	     }
      	 else if(parameter.equalsIgnoreCase("ADDRESS")){
      		 String adr="1"+Common.generateRandomTaxID().substring(0,2);
      		  adr1=adr+" FEDERAL ST";
      		  System.out.println("New Address:"+adr1);
      		 log("Member Id: "+memId+", New Address: "+adr1);
      	     }
      	 else if(parameter.contains("AidCat")){
      	 eligDate=Member.fileSysdateCustom(-65);
      	 catCode=aidcat;
      	 log("Member Id: "+memId+", New AidCat: "+catCode);
      	 }

       	 driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
       	 if(!(parameter.equalsIgnoreCase("AidCatEnd"))){
      	 String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<EligibilityRequest xmlns=\"http://xmlns.hhs.ma.gov/HHS/serviceobjects/versions/2.9/EligibilityServices\" "+
   			    "\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<transactionsource\nid_medicaid=\""+memId+"\"\nid_other=\""+Member.otherid+"\"\nid_source=\"MHO\"" +
   			    "\n/>\n<demographic\nnam_last=\""+lastName+"\"\nnam_first=\""+fname+"\"\nnum_primary_ssn=\""+ssn+"\"\nres_adr_street_1=\""+adr1+"\"\nres_adr_city=\""+city+"\"\n" +
   			    "res_adr_state=\"MA\"\nres_adr_zip_code=\""+zip+"\"\nmail_adr_street_1=\""+adr1+"\"\nmail_adr_city=\""+city+"\"\nmail_adr_state=\"MA\"\nmail_adr_zip_code=\""+zip+"\"\ncde_lang_written=\""+lang+"\"\ncde_sex=\""+sex+"\"\ncde_race=\""+race+"\"\ncde_citizen=\"C\"\ndte_birth=\""+dob+"\"" +
   			    "\n/>\n<case\nnum_case=\""+casenum+"\"\ncde_case_status=\"1\"\nhoh_nam_first=\""+fname+"\"\nhoh_nam_last=\""+lastName+"\"\nhoh_nam_init=\""+midInit+"\"\n/>\n<eligibility\ncde_line=\""+lineCode+"\"\ndte_begin_elig=\""+eligDate+"\"\n" +
   			    "cde_elig_status=\"1\"\ncde_cat=\""+nAidCat+"\"\ncde_open_reason=\""+eligStartReason+"\"\nfamily_size=\""+familySize+"\"\ndte_appl=\""+Member.fileSysdateCustom(0)+"\"\ncde_region=\""+regionCode+"\"\ncde_office=\""+officeCode+"\"\namt_gross_income=\""+incomeAmount+"\"\n/>\n</EligibilityRequest>");
      	memId=Member.getCustomMemebr(xml);
       	 }

       	 else if(parameter.equalsIgnoreCase("AidCatEnd")){
       		String xml=("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<EligibilityRequest xmlns=\"http://xmlns.hhs.ma.gov/HHS/serviceobjects/versions/2.9/EligibilityServices\" "+
       			    "\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n<transactionsource\nid_medicaid=\""+memId+"\"\nid_other=\""+Member.otherid+"\"\nid_source=\"MHO\"" +
       			    "\n/>\n<demographic\nnam_last=\""+lastName+"\"\nnam_first=\""+fname+"\"\nnum_primary_ssn=\""+ssn+"\"\nres_adr_street_1=\""+adr1+"\"\nres_adr_city=\""+city+"\"\n" +
       			    "res_adr_state=\"MA\"\nres_adr_zip_code=\""+zip+"\"\nmail_adr_street_1=\""+adr1+"\"\nmail_adr_city=\""+city+"\"\nmail_adr_state=\"MA\"\nmail_adr_zip_code=\""+zip+"\"\ncde_lang_written=\""+lang+"\"\ncde_sex=\""+sex+"\"\ncde_race=\""+race+"\"\ncde_citizen=\"C\"\ndte_birth=\""+dob+"\"" +
       			    "\n/>\n<case\nnum_case=\""+casenum+"\"\ncde_case_status=\"1\"\nhoh_nam_first=\""+fname+"\"\nhoh_nam_last=\""+lastName+"\"\nhoh_nam_init=\""+midInit+"\"\n/>\n<eligibility\ncde_line=\""+lineCode+"\"\ndte_begin_elig=\""+eligDate+"\"\n" +
       			    "cde_elig_status=\"1\"\ncde_cat=\""+nAidCat+"\"\ncde_open_reason=\""+eligStartReason+"\"\nfamily_size=\""+familySize+"\"\ndte_appl=\""+Member.fileSysdateCustom(0)+"\"\ncde_region=\""+regionCode+"\"\ncde_office=\""+officeCode+"\"\namt_gross_income=\""+incomeAmount+"\"\n/>\n<eligibility\ncde_line=\""+lineCode+"\"\ndte_begin_elig=\""+eligDate+"\"\ndte_end_elig=\""+Member.fileSysdateCustom(0)+"\"\n" +  //dte_begin_elig=\""+eligDate+"\"\n
       			    "cde_elig_status=\"4\"\ncde_cat=\""+aidcat+"\"\ncde_close_reason=\"00\"\nfamily_size=\""+familySize+"\"\ndte_appl=\""+Member.fileSysdateCustom(0)+"\"\ncde_region=\""+regionCode+"\"\ncde_office=\""+officeCode+"\"\namt_gross_income=\""+incomeAmount+"\"\n/>\n</EligibilityRequest>");
       		 memId=Member.getCustomMemebr(xml);

       	 }



   	     System.out.println("Updated Member successfully: "+memId);


   	     /******************Member Linking*********************
   	    driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Recipient")).click();
   		Managedcare_Common.memSrch(memId2);
   		Common.cancelAll();
   		driver.findElement(By.id("MMISForm:MMISBodyContent:RecipientNavigatorPanel:RecipientNavigator:ITM_Navi1")).click();
   		driver.findElement(By.id("MMISForm:MMISBodyContent:LinkRequestPanel:LinkRequest_NewButtonClay:LinkRequestList_newAction_btn")).click();
   		driver.findElement(By.xpath("//*[@id='MMISForm:MMISBodyContent:LinkRequestPanel:LinkRequestId_CMD_SEARCH']/img")).click();
   		//driver.findElement(By.id("MMISForm:MMISBodyContent:LinkRequestPanel:_id319:RecipientSearchCriteriaPanel:CurrentID")).sendKeys(memId1);
   		driver.findElement(By.xpath("//input[contains(@id,'RecipientSearchCriteriaPanel:CurrentID')]")).sendKeys(memId1);
   		//driver.findElement(By.id("MMISForm:MMISBodyContent:LinkRequestPanel:_id319:RecipientSearchCriteriaPanel:SEARCH")).click();
   		driver.findElement(By.xpath("//*[contains(@id,'RecipientSearchCriteriaPanel:SEARCH')]")).click();
   		//driver.findElement(By.id("MMISForm:MMISBodyContent:LinkRequestPanel:_id319:RecipientSearchResults_0:column1Value")).click();
   		driver.findElement(By.xpath("//*[contains(@id,'0:column1Value')]")).click();
   		Common.save();
   		Common.cancelAll();  */

   		 driver.findElement(By.id("MMISForm:MMISHeader:header_value_home")).click();
   	     }

     
      
        
//https://www.guru99.com/all-about-excel-in-selenium-poi-jxl.html
      //http://www.seleniumeasy.com/jxl-tutorials/how-to-read-excel-file-using-java
      //https://stackoverflow.com/questions/22689666/how-to-read-data-from-excel-sheet-in-selenium-webdriver
 }
