package marchingfood.tqs.ua.controller;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DefaultUserTestIT {

    @LocalServerPort
    int serverPort;

    String url;

    @BeforeEach
    public void setUp() {
        url = String.format("http://localhost:%d/", serverPort);
    }

    @Test
    void registerUser(ChromeDriver driver){
        String clientName = "client";
        String clientEmail = "client@email.com";
        String clientHome = "home";
        String clientPass = "randompass";

        driver.get(url);
        driver.findElement(By.cssSelector("div > .nav-item > .nav-link")).click();
        driver.findElement(By.linkText("Register here")).click();
        driver.findElement(By.id("name_com")).sendKeys(clientName);
        driver.findElement(By.id("email_com")).sendKeys(clientEmail);
        driver.findElement(By.id("address_com")).sendKeys(clientHome);
        driver.findElement(By.id("password")).sendKeys(clientPass);
        driver.findElement(By.cssSelector(".btn")).click();;
        assertDoesNotThrow(() -> driver.findElement(By.xpath("//h3[contains(.,'Login')]")));
        driver.findElement(By.linkText("Register here")).click();
        driver.findElement(By.id("name_com")).sendKeys(clientName);
        driver.findElement(By.id("email_com")).sendKeys(clientEmail);
        driver.findElement(By.id("address_com")).sendKeys(clientHome);
        driver.findElement(By.id("password")).sendKeys(clientPass);
        driver.findElement(By.cssSelector(".btn")).click();
        assertDoesNotThrow(() -> driver.findElement(By.xpath("//div[contains(.,'There was an unexpected error (type=Bad Request, status=400).')]")));
        driver.get(url+"login");
        driver.findElement(By.id("username")).sendKeys(clientName);
        driver.findElement(By.id("password")).sendKeys(clientPass);
        driver.findElement(By.id("login-submit")).click();
        assertDoesNotThrow(() -> driver.findElement(By.xpath("//a[contains(text(),'Log Out')]")));
        driver.findElement(By.xpath("//a[contains(text(),'Log Out')]")).click();
    }

    @Test
    void defaultUserTest(ChromeDriver driver) throws InterruptedException {
        driver.get(url);
        driver.manage().window().setSize(new Dimension(1000, 700));
        driver.findElement(By.cssSelector("div > .nav-item > .nav-link")).click();
        driver.findElement(By.id("username")).sendKeys("user1");
        driver.findElement(By.id("password")).sendKeys("12345");
        driver.findElement(By.id("login-submit")).click();
        driver.findElement(By.cssSelector(".nav-item:nth-child(2) > .nav-link")).click();
        TimeUnit.MILLISECONDS.sleep(4000); // Page needs to load for some buttons to work and alerts to show
        driver.findElement(By.id("1")).click();
        assertThat(driver.switchTo().alert().getText(), is("Item added to cart"));
        driver.switchTo().alert().accept();
        driver.findElement(By.id("2")).click();
        assertThat(driver.switchTo().alert().getText(), is("Item added to cart"));
        driver.switchTo().alert().accept();
        driver.findElement(By.id("1")).click();
        assertThat(driver.switchTo().alert().getText(), is("Item added to cart"));
        driver.switchTo().alert().accept();
        driver.findElement(By.cssSelector("div:nth-child(3) > .nav-item > .nav-link")).click();
        TimeUnit.MILLISECONDS.sleep(2000); // Page needs to load for some buttons to work and alerts to show
        driver.findElement(By.xpath("(//button[@type=\'button\'])[2]")).click();
        assertThat(driver.switchTo().alert().getText(), is("Item removed from cart"));
        driver.switchTo().alert().accept();
        TimeUnit.MILLISECONDS.sleep(2000); // Page needs to load for some buttons to work and alerts to show
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertThat(driver.switchTo().alert().getText(), is("Delivery Ordered Successfully"));
        driver.switchTo().alert().accept();
        TimeUnit.MILLISECONDS.sleep(1000); // Page needs to load for some buttons to work and alerts to show
        driver.findElement(By.cssSelector("div:nth-child(4) .nav-link")).click();
        driver.findElement(By.xpath("//td[5]/button")).click();
        driver.findElement(By.id("rating")).sendKeys("5");
        driver.findElement(By.id("description")).sendKeys("Good");
        driver.findElement(By.id("submit")).click();
        TimeUnit.MILLISECONDS.sleep(1000); // Page needs to load for some buttons to work and alerts to show
        assertThat(driver.findElement(By.cssSelector("h2:nth-child(3)")).getText(), is("Your Review has been posted successfully"));
        driver.findElement(By.xpath("//a[contains(text(),'Log Out')]")).click();
    }

}
