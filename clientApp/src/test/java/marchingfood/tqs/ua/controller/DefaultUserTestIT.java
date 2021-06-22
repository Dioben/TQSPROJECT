package marchingfood.tqs.ua.controller;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import marchingfood.tqs.ua.model.Client;
import marchingfood.tqs.ua.model.Delivery;
import marchingfood.tqs.ua.service.DeliveryService;
import marchingfood.tqs.ua.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SeleniumJupiter.class)
public class DefaultUserTestIT {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    DeliveryService deliveryService;

    @BeforeEach
    public void setUp() {
        Client user = userService.getUserByName("user1");

        Delivery delivery = new Delivery();
        delivery.setDelivered(false);
        delivery.setAddress("doesn't matter");
        delivery.setClient(user);
        delivery.setPaid(false);

        deliveryService.saveDelivery(delivery);
    }

    @Test
    public void defaultUserTest(ChromeDriver driver) {
        driver.get("http://localhost:8000/");
        driver.manage().window().setSize(new Dimension(1000, 700));
        driver.findElement(By.cssSelector("div > .nav-item > .nav-link")).click();
//        driver.findElement(By.linkText("Register here")).click();
//        driver.findElement(By.id("name_com")).sendKeys("client");
//        driver.findElement(By.id("email_com")).sendKeys("client@email.com");
//        driver.findElement(By.id("address_com")).sendKeys("home");
//        driver.findElement(By.id("password")).sendKeys("randompass");
//        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.id("username")).sendKeys("user1");
        driver.findElement(By.id("password")).sendKeys("12345");
        driver.findElement(By.id("login-submit")).click();
        driver.findElement(By.cssSelector(".nav-item:nth-child(2) > .nav-link")).click();
        driver.findElement(By.id("6")).click();
//        driver.findElement(By.id("#\\31 > .fas")).click();
/*        driver.findElement(By.cssSelector("div:nth-child(3) > .nav-item > .nav-link")).click();
        driver.findElement(By.xpath("(//button[@type=\'button\'])[2]")).click();
        driver.findElement(By.cssSelector(".btn-primary")).click();
        driver.findElement(By.cssSelector("div:nth-child(4) .nav-link")).click();
        driver.findElement(By.id("button17")).click();
        driver.findElement(By.id("rating")).sendKeys("1");
        driver.findElement(By.id("description")).sendKeys("Took way too long.");
        driver.findElement(By.id("submit")).click();
        driver.findElement(By.cssSelector("div:nth-child(5) .nav-link")).click();*/
    }

}
