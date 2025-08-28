package login;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;
import junit.framework.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;




public class login {
WebDriver driver;
WebDriverWait Wait;
Process ffmpeg;


private static final String FFMPEG = "C:\\Users\\aleena.j_simadvisory\\Documents\\ffmpeg\\bin\\ffmpeg.exe";


@Test(dataProvider = "getdata",dataProviderClass = dataprovider.class)
public void testcase1(String Username, String Password)throws Exception{

driver.manage().window().maximize();
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); 
Wait = new WebDriverWait(driver, Duration.ofSeconds(5));
driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
Wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username"))).sendKeys(Username);
Wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password"))).sendKeys(Password);
Wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();
Wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[@class='oxd-text oxd-text--h6 oxd-topbar-header-breadcrumb-module']")));
System.out.println("my current title is " + driver.getTitle());
Assert.assertTrue(driver.findElement(By.xpath("//h6[@class='oxd-text oxd-text--h6 oxd-topbar-header-breadcrumb-module']")).isDisplayed());
}



@BeforeTest()
public void beforetest(){
	
	WebDriverManager.chromedriver().setup();
	driver =new ChromeDriver();
}

@BeforeClass()
public void beforeclass() {
	 try {
	      String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	      String outFile = "C:\\Users\\aleena.j_simadvisory\\Documents\\selenium_recording_" + ts + ".mp4";

	      String[] cmd = {
	    	FFMPEG, "-y",
	        "-f", "gdigrab",                 
	        "-framerate", "30",
	        "-i", "desktop",
	        "-c:v", "libx264", "-preset", "veryfast", "-crf", "23", "-pix_fmt", "yuv420p",
	        outFile
	      };

	      ffmpeg = new ProcessBuilder(cmd).redirectErrorStream(true).start();

	      // Drain logs to avoid buffer blocking
	      new Thread(() -> {
	        try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getInputStream()))) {
	          while (br.readLine() != null) {}
	        } catch (IOException ignored) {}
	      }).start();
	      System.out.println("FFmpeg recording started → " + outFile);

	    } 
	    catch (IOException ioe) 
	    {
	      System.out.println(" FFmpeg not found or failed to start. Skipping recording. Details: " + ioe.getMessage());
	      ffmpeg = null; 
	    }
	

}

@BeforeMethod()
public void beforemethod() {
	driver.manage().deleteAllCookies();
}



@AfterClass()
public void afterclass() throws InterruptedException {
	 
    if (ffmpeg != null) {
      try (OutputStream os = ffmpeg.getOutputStream()) {
        os.write('q'); // graceful stop
        os.flush();
      } catch (IOException ignored) {}
      ffmpeg.waitFor();
      System.out.println("FFmpeg recording stopped.");
    }

	driver.quit();
}

@AfterMethod()
public void aftermethod() {
	try {
		WebDriverWait temp =new WebDriverWait(driver,Duration.ofSeconds(2));
		WebElement menu = temp.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".oxd-userdropdown")));
        menu.click();
        temp.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[normalize-space()='Logout']"))).click();  
		
	}catch( Exception e){
		
}
}

}
