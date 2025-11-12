package newMMIS_Subsystems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import org.testng.SkipException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ newMMIS_Subsystems.TestNGCustom.class })
public class tptTesting extends Login {
	
	public static File dir834 = new File(tempDirPath+"834"); 
	public static File dir820 = new File(tempDirPath+"820"); 
	public static String filelocation_834input="C:\\Users\\agandhi20\\Desktop\\Testing\\TPT automation\\834_Feb_24_2020\\834input_monthly.xls";
	public static String filelocation_820input="C:\\Users\\agandhi20\\Desktop\\Testing\\TPT automation\\820\\feb_18_2020\\820input_monthly.xls";
//	static ArrayList<String> excelspreaddata=new ArrayList<>(); 
	static int rowpointer=1;
	public static PrintWriter out834;
	public static PrintWriter out820;
	public static String monthly820fileName="";
	public static String custom_824_fileName="";
	public static String custom_834_fileName="";




	@BeforeTest
	public void create834dir() throws Exception{
		log("***************Starting custom 834 processing***************\r\n");

		 dir834.mkdir(); 
		 //For TC download834_monthly -Start
		//Create monthly dir if it does not exist already		
		File dirmonthly = new File(dir834.getPath()+"\\TPT Monthly");  
		if (!dirmonthly.exists()) { 
		 System.out.println("creating directory: " + dirmonthly.getName()); 
		 dirmonthly.mkdir(); 
		}
		String dest = dirmonthly.getPath();
		custom_834_fileName=Managedcare_Common.fileName1.substring(Managedcare_Common.fileName1.lastIndexOf("\\")+1, Managedcare_Common.fileName1.length());
		String monthly834fileName=dest+"\\"+custom_834_fileName;
	    out834 = new PrintWriter(monthly834fileName);   
//	    out834.println("Starting to extract member specific parts from 834");
	    
	    out834.println(Managedcare_Common.get834_addHeader());
		 //For TC download834_monthly -End

	}
	
	//If you want to produce 834, comment this @BeforeTest and uncomment above @BeforeTest
//	@BeforeTest
//	public void create820dir() throws Exception{
//		log("***************Starting custom 820 processing***************\r\n");
//			
//		 dir820.mkdir(); 
//		//Create monthly dir if it does not exist already		
//		File dirmonthly = new File(dir820.getPath()+"\\TPT Monthly");  
//		if (!dirmonthly.exists()) { 
//		 System.out.println("creating directory: " + dirmonthly.getName()); 
//		 dirmonthly.mkdir(); 
//		}
//		String dest = dirmonthly.getPath();
//		custom_824_fileName=Managedcare_Common.fileName1.substring(Managedcare_Common.fileName1.lastIndexOf("\\")+2, Managedcare_Common.fileName1.length());
//		monthly820fileName=dest+"\\"+custom_824_fileName;
////		monthly820fileName=dest+"\\824_custom.txt";
//	    out820 = new PrintWriter(monthly820fileName);   
////	    out834.println("Starting to extract member specific parts from 834");
//	    
//	    out820.println(Managedcare_Common.get820_addHeader());
//		 //For TC download834_monthly -End
//
//	}
	
	@AfterTest
	public void endcreate834dir() throws Exception{
		//For TC download834_monthly
//		out834.println();
//		out834.println();
		out834.println(Managedcare_Common.get834_addFooter());
//	    out834.println("Ending extract of member specific parts from 834");
	    out834.close();
		log("\r\n***************End of custom 834 processing***************");

	}
	
	//If you want to produce 834, comment this @AfterTest and uncomment above @AfterTest
//	@AfterTest
//	public void endcreate820dir() throws Exception{
//		out820.println(Managedcare_Common.get820_addFooter());
//	    out820.close();
//	    
//		//Replace BPR02
//		//Get original BPR02 count		
////		String customFileContent = monthly820fileName.toString();
////		System.out.println(customFileContent);
//	  	BufferedReader br = new BufferedReader(new FileReader(monthly820fileName));
//	  	String line = ""; 
//		String targetText = "";
//		line=br.readLine();
//	  	while (line != null) {
//	  		targetText=targetText+line+"\r\n";
//	  		line=br.readLine();
//	  	}
//	  	br.close();
//		String bpr02Count=targetText.substring(targetText.indexOf("*", targetText.indexOf("BPR*")+5), targetText.indexOf("*", targetText.indexOf("BPR*")+7));
//		System.out.println("BPR02 " + bpr02Count);
//		
//		//Replace BPR02 with rmr_820
//		targetText = targetText.replace(bpr02Count, "*"+Double.toString(Math.round(Managedcare_Common.rmr_820*100.0)/100.0));
//		
//		//write to file again
//		PrintWriter out820_final = new PrintWriter(monthly820fileName);   
//		out820_final.println(targetText);
//		out820_final.close();
//		
//		log("\r\n***************End of custom 820 processing***************");
//	}
	
	@Test
	public void testTPTData() throws Exception{
		TestNGCustom.TCNo="TPTData";
	
		Managedcare_Common.check();
		
	}
	
//	@Test
//	public void testTPTData_ACOB() throws Exception{
//
//		for (int i=0; i<20; i++)
//			Member.createMember("40");
//		
//	}
	
//	

//	@DataProvider
//	  public  Iterator<String[]> dp834(Method m) throws Exception 
//	  {
//	    System.out.println("dp 834");
//		ArrayList<String[]> excelspreaddata = new ArrayList<String[]>();
//
// 	 ExcelRead.setexcelfile(filelocation_834input);
//		while(rowpointer<ExcelRead.getlastrownum()) {
//			excelspreaddata.clear();
//			excelspreaddata=ExcelRead.getspreaddata1(rowpointer);
//			rowpointer++;
//		}	
		
	
	@DataProvider
	  public  Iterator<String[]> dp834() throws Exception{
		 	 ExcelRead.setexcelfile(filelocation_834input);

			ArrayList<String[]> rowList = new ArrayList<String[]>();
			//there are 3 columns

			for(int i=rowpointer;i<ExcelRead.getlastrownum();i++) {
				String[] colList = new String[ExcelRead.getlastcellnum()]; 
				for(int j=0;j<ExcelRead.getlastcellnum();j++) {
					ExcelRead.cell = ExcelRead.sheet.getCell(j, i);
					colList[j]=ExcelRead.cell.getContents().trim();
				}
				rowList.add(colList);

			}
			return rowList.iterator();
		}
	  
		@DataProvider
		  public  Iterator<String[]> dp820() throws Exception{
			 	 ExcelRead.setexcelfile(filelocation_820input);

				ArrayList<String[]> rowList = new ArrayList<String[]>();
				//there are 3 columns

				for(int i=rowpointer;i<ExcelRead.getlastrownum();i++) {
					String[] colList = new String[ExcelRead.getlastcellnum()]; 
					for(int j=0;j<ExcelRead.getlastcellnum();j++) {
						ExcelRead.cell = ExcelRead.sheet.getCell(j, i);
						colList[j]=ExcelRead.cell.getContents().trim();
					}
					rowList.add(colList);

				}
				return rowList.iterator();
			}
	  
	
	  @Test(dataProvider = "dp834")
	  	public void testAnshul(String Pid, String recp, String dt834, String tc ) throws Exception{
		TestNGCustom.TCNo="testdp_excel";
		
		System.out.println(Pid+" "+recp+" "+dt834);
		
	  }
	
	  @Test(dataProvider = "dp834")
	  	public void download834_021(String Pid, String recp, String dt834, String tc) throws Exception{
		TestNGCustom.TCNo="download834_021";
		
		//Create 021 dir if it does not exist already		
		 File dir021 = new File(dir834.getPath()+"\\TPT 021");  
		 if (!dir021.exists()) { 
			 System.out.println("creating directory: " + dir021.getName()); 
			 dir021.mkdir(); 
		 }
		 
		 String dest = dir021.getPath();
		Managedcare_Common.get834_tptTesting(Pid, dt834, recp, dest);
		log("834 successfully produced for tc "+tc+", Member "+recp);
		
	}
	  
	  @Test(dataProvider = "dp834")
	  	public void download834_monthly(String Pid, String recp, String dt834, String tc) throws Exception{
		TestNGCustom.TCNo="download834";
		
		//Create monthly dir if it does not exist already		
		 File dirmonthly = new File(dir834.getPath()+"\\TPT Monthly");  
		 if (!dirmonthly.exists()) { 
			 System.out.println("creating directory: " + dirmonthly.getName()); 
			 dirmonthly.mkdir(); 
		 }
		 

		 out834.println(Managedcare_Common.get834_tptTesting_monthlyFull(Pid, dt834, recp, tc));
		//log("834 successfully produced for tc "+tc+", Member "+recp);
		
	}
	  
	  @Test(dataProvider = "dp820")
	  	public void download820_monthly(String Pid, String recp, String dt820, String tc) throws Exception{
		TestNGCustom.TCNo="download820";
		
		//Create monthly dir if it does not exist already		
		 File dirmonthly = new File(dir820.getPath()+"\\TPT Monthly");  
		 if (!dirmonthly.exists()) { 
			 System.out.println("creating directory: " + dirmonthly.getName()); 
			 dirmonthly.mkdir(); 
		 }
		 

		 out820.println(Managedcare_Common.get820_tptTesting_monthlyFull(Pid, dt820, recp, tc));
		
	}
	  
	  @Test
		public void testTPTData_change() throws Exception{
			TestNGCustom.TCNo="TPTData_change";
		
			Managedcare_Common.check_change();
			
		}
	
}
