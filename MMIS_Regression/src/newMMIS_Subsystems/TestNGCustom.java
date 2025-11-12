package newMMIS_Subsystems;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.*;

public class TestNGCustom extends TestListenerAdapter {
	private int Count = 0;
	public static String TCNo="(test_case_number)";

	// Take screen shot only for failed test case
	@Override
	public void onTestFailure(ITestResult tr) {
		ScreenShot();
	}

	@Override
	public void onTestSkipped(ITestResult tr) {
		ScreenShot();
	}

	@Override
	public void onTestSuccess(ITestResult tr) {
		// ScreenShot();
	}

	public void ScreenShot() {
		try {

			String NewFileNamePath;

			// Code to get screen resolution
			// Get the default toolkit
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			// Get the current screen size
			Dimension scrnsize = toolkit.getScreenSize();
			// Print the screen size
			//System.out.println("Screen size : " + scrnsize);

			// Get the dir path
			File directory = new File(".");
//			File directory = new File("C:\\Users\\agandhi20\\Workspace\\Regression_TR");
			System.out.println(directory.getCanonicalPath());

			// get current date time with Date() to create unique file name
			DateFormat dateFormat = new SimpleDateFormat(
					"dd_MMM_yyyy__hh_mm_ssaa");
			// get current date time with Date()
			Date date = new Date();
			// System.out.println(dateFormat.format(date));

			// To identify the system
			InetAddress ownIP = InetAddress.getLocalHost();
			// System.out.println("IP of my system is := "+ownIP.getHostAddress());

			NewFileNamePath = directory.getCanonicalPath() + "\\ScreenShots\\"
					+"_" + dateFormat.format(date)
					+ "_" + ownIP.getHostAddress() + "_TC "+TCNo+".png";
			System.out.println(NewFileNamePath);

			/* Capture the screen shot of the area of the screen defined by the
			   rectangle */
			Robot robot = new Robot();
			BufferedImage bi = robot.createScreenCapture(new Rectangle(1280,
					1024));
			ImageIO.write(bi, "png", new File(NewFileNamePath));
			Count++;// Assign each screen shot a number
			NewFileNamePath = Count + "." + NewFileNamePath;
			Reporter.log(NewFileNamePath);

		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
