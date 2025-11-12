package newMMIS_Subsystems;

import java.util.ArrayList;
import java.util.Iterator;

import org.openqa.selenium.By;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class aims extends Login{

	@BeforeTest
    public void provStartup() throws Exception {
    	log("Starting AIMS test......");
    }
	
	@BeforeMethod
	public void LoginCheck() throws Exception {
		Common.resetBase();
		testCheckDBLoginSuccessful();	
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Provider")).click();
	}
	
//	@DataProvider
//	public  int counter() throws Exception {
//			int i=0;
//			for(int j=0;j<6;j++)
//				i=j;
//			return i;
//	}
	

//    @Test(dataProvider = "counter")
	@Test
    public void test1() throws Exception{
    	log ("Starting test 1");
    	
    	Common.portalLogin();
    	log("Base logout successfull");
    	log("Portal login successfull");
    	
    	Common.portalLogout();
    	log("Portal logout successfull");
    	log("Base login successfull");
    }
	
	@Test
    public void test2() throws Exception{
    	log ("Starting test 2");
    	
    	Common.portalLogin();
    	log("Base logout successfull");
    	log("Portal login successfull");
    	
    	Common.portalLogout();
    	log("Portal logout successfull");
    	log("Base login successfull");
    }
	
	@Test
    public void test3() throws Exception{
    	log ("Starting test 3");
    	
    	Common.portalLogin();
    	log("Base logout successfull");
    	log("Portal login successfull");
    	
    	Common.portalLogout();
    	log("Portal logout successfull");
    	log("Base login successfull");
    }
	
	@Test
    public void test4() throws Exception{
    	log ("Starting test 4");
    	
    	Common.portalLogin();
    	log("Base logout successfull");
    	log("Portal login successfull");
    	
    	Common.portalLogout();
    	log("Portal logout successfull");
    	log("Base login successfull");
    }
	
	@Test
    public void test5() throws Exception{
    	log ("Starting test 5");
    	
    	Common.portalLogin();
    	log("Base logout successfull");
    	log("Portal login successfull");
    	
    	Common.portalLogout();
    	log("Portal logout successfull");
    	log("Base login successfull");
    }
	
	@Test
    public void test6() throws Exception{
    	log ("Starting test 6");
    	
    	Common.portalLogin();
    	log("Base logout successfull");
    	log("Portal login successfull");
    	
    	Common.portalLogout();
    	log("Portal logout successfull");
    	log("Base login successfull");
    }
	
	@Test
    public void test7() throws Exception{
    	log ("Starting test 7");
    	
    	Common.portalLogin();
    	log("Base logout successfull");
    	log("Portal login successfull");
    	
    	Common.portalLogout();
    	log("Portal logout successfull");
    	log("Base login successfull");
    }
	
	@Test
    public void test8() throws Exception{
    	log ("Starting test 8");
    	
    	Common.portalLogin();
    	log("Base logout successfull");
    	log("Portal login successfull");
    	
    	Common.portalLogout();
    	log("Portal logout successfull");
    	log("Base login successfull");
    }
	
	@Test
    public void test9() throws Exception{
    	log ("Starting test 9");
    	
    	Common.portalLogin();
    	log("Base logout successfull");
    	log("Portal login successfull");
    	
    	Common.portalLogout();
    	log("Portal logout successfull");
    	log("Base login successfull");
    }
	
	@Test
    public void test10() throws Exception{
    	log ("Starting test 10");
    	
    	Common.portalLogin();
    	log("Base logout successfull");
    	log("Portal login successfull");
    	
    	Common.portalLogout();
    	log("Portal logout successfull");
    	log("Base login successfull");
    }
    
}

