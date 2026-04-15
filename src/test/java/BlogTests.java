import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

public class BlogTests {

    String baseUrl = "http://wordpresstestsite/";
    WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        //Thread.sleep(2000);
        //junit check if test failed, take screenshot or keep the window open
        driver.quit();
    }

    @Test
    public void guestCommentInvalidEmail() {
        driver.get(baseUrl+"/how-technology-is-shaping-everyday-life/");
        WebElement commentField = driver.findElement(By.id("comment"));
        commentField.sendKeys("Invalid email test.");
        WebElement nameField = driver.findElement(By.id("author"));
        nameField.sendKeys("Test User");
        WebElement emailField = driver.findElement(By.id("email"));
        emailField.sendKeys("notanemail");
        WebElement submitButton = driver.findElement(By.id("submit"));
        submitButton.click();
        String currentUrl = driver.getCurrentUrl();
        Assertions.assertEquals(
                baseUrl+"/how-technology-is-shaping-everyday-life/",
                currentUrl
        );
    }

    @Test
    public void guestCommentMissingName() {
        driver.get(baseUrl + "/how-technology-is-shaping-everyday-life/");
        WebElement commentField = driver.findElement(By.id("comment"));
        commentField.sendKeys("Comment without name.");
        WebElement emailField = driver.findElement(By.id("email"));
        emailField.sendKeys("noname@test.com");
        WebElement submitButton = driver.findElement(By.id("submit"));
        submitButton.click();
        String currentUrl = driver.getCurrentUrl();
        Assertions.assertEquals(
                baseUrl+"/how-technology-is-shaping-everyday-life/",
                currentUrl
        );
    }

    @Test
    public void xssPayloadComment() {
        driver.get(baseUrl + "wp-login.php");
        driver.findElement(By.id("user_login")).sendKeys("author1");
        driver.findElement(By.id("user_pass")).sendKeys("1234");
        driver.findElement(By.id("wp-submit")).click();

        driver.get(baseUrl + "/how-technology-is-shaping-everyday-life/");

        WebElement commentField = driver.findElement(By.id("comment"));
        commentField.sendKeys("<script>alert('xss')</script>");
        driver.findElement(By.id("submit")).click();

        // Check if alert appeared (it shouldn't)
        boolean alertPresent = false;
        try {
            org.openqa.selenium.Alert alert = driver.switchTo().alert();
            alertPresent = true;
            alert.dismiss(); // close it
        } catch (org.openqa.selenium.NoAlertPresentException e) {
            alertPresent = false;
        }

        Assertions.assertFalse(
                alertPresent,
                "XSS vulnerability detected! Alert dialog appeared — script was executed."
        );
    }
}