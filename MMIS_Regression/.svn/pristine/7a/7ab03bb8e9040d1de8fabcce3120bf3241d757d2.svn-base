package newMMIS_Subsystems;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import ch.ethz.ssh2.StreamGobbler;

public class MC extends Login {
	
	@Test
    public void test834() throws Exception{
    	TestNGCustom.TCNo="test834";
    	log("//TC test834");
    	
	 	
        //Verify unix report
		String command, error;

		//Get Desired Filename
		command = "ls -ltr /edi/data/X12_OUT/WEB/110025617D.834D.WEB.*.* | grep '"+"Sep"+" "+"14  2013"+"'";
		error = "834 not found";
		String fileName = Common.connectUNIX(command, error);
		fileName = fileName.substring(fileName.length()-56);
		
		log(" 834 filename is: "+fileName);

		//Verify duplicate member data in file
		command = "cat "+fileName;
		//command = "grep "+inactMem+" "+fileName;
		error = "cannot open 834";
		String outputText = Common.connectUNIX(command, error);
		
		outputText = outputText.replace("~", "~\r\n");
		
		//Store 834
		String fileName1=tempDirPath+"834"+TestNGCustom.TCNo+".txt";
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
			if (line.contains("100053569859")) {
				targetText = pline;
				while (!(line.contains("INS"))) {
					targetText = targetText+"\r\n"+line;
					line = br.readLine();
				}
				break;
			}
		}
		log(targetText);
    }

}
