package newMMIS_Subsystems;

import org.openqa.selenium.By;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class DF27476 extends Login {
	
	@BeforeTest
    public void DF27476Startup() throws Exception {
    	log("Starting DF 27476 Subsystem......");
    }
	
	@BeforeMethod
	public void LoginCheck() throws Exception {
		Common.resetBase();
		testCheckDBLoginSuccessful();	
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_Admin")).click();
	}
	
    @Test
    public void test27476() throws Exception{
	 	driver.findElement(By.id("MMISForm:MMISMenu:_MENUITEM_imaging")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:ImagingNavigatorPanel:ImagingMaintenance_id:GRP_provider")).click();
	 	driver.findElement(By.id("MMISForm:MMISBodyContent:ImagingNavigatorPanel:ImagingMaintenance_id:ITM_n110")).click();
	 	Common.search();

    }

}
