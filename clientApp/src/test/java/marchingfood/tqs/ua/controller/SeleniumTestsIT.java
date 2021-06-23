package marchingfood.tqs.ua.controller;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import marchingfood.tqs.ua.model.Delivery;
import marchingfood.tqs.ua.model.Menu;
import marchingfood.tqs.ua.repository.ClientRepository;
import marchingfood.tqs.ua.repository.DeliveryRepository;
import marchingfood.tqs.ua.repository.MenuRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeleniumTestsIT {

    @LocalServerPort
    int serverPort;

    String url;

    @Autowired
    ClientRepository clientRepository;
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    DeliveryRepository deliveryRepository;

    @BeforeEach
    public void setUp() {
        url = String.format("http://backend:%d/", serverPort);
    }

    @Test
    @Order(0)
    void registerUser(ChromeDriver driver){
        String clientName = UUID.randomUUID().toString();
        String clientEmail = clientName+"@email.com";
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
        clientRepository.delete(clientRepository.findByName(clientName));

    }

    @Test
    @Order(1)
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

        Iterator<Delivery> it = clientRepository.findByName("user1").getOrderEntity().iterator();
        Delivery delivery = it.next();
        while (it.hasNext()){delivery=it.next();}
        deliveryRepository.delete(delivery);
    }

    @Test
    @Order(2)
    void adminUserTest(ChromeDriver driver) throws InterruptedException {
        Menu menu = menuRepository.findAllByNameContains("Sbubby Bread").get(0);
        driver.get(url);
        driver.manage().window().setSize(new Dimension(1000, 700));
        driver.findElement(By.cssSelector("div > .nav-item > .nav-link")).click();
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("admin");
        driver.findElement(By.id("login-submit")).click();
        driver.findElement(By.linkText("ADMIN")).click();
        assertDoesNotThrow(() -> driver.findElement(By.xpath("//td[contains(.,'user1')]")));
        driver.findElement(By.cssSelector(".fa-plus-square")).click();
        driver.findElement(By.name("name")).sendKeys("IT MENU FOR TESTING");
        driver.findElement(By.name("price")).sendKeys("10");
        driver.findElement(By.name("description")).sendKeys("New menu");
        driver.findElement(By.cssSelector("#adder_div input:nth-child(7)")).click();
        TimeUnit.MILLISECONDS.sleep(1000); // Page needs to load for some buttons to work and alerts to show
        assertDoesNotThrow(() -> driver.findElement(By.xpath("//span[contains(.,\'New menu\')]")));
        driver.findElement(By.cssSelector("tr:nth-child(3) .fas")).click();
        driver.findElement(By.id("edit-price")).sendKeys(Keys.CONTROL + "a");
        driver.findElement(By.id("edit-price")).sendKeys(Keys.DELETE);
        driver.findElement(By.id("edit-price")).sendKeys("6.0");
        driver.findElement(By.cssSelector("#edit_form > input:nth-child(7)")).click();
        assertThat(driver.findElement(By.id("menuPrice3")).getText(), is("6.0"));
        driver.findElement(By.cssSelector("tr:nth-child(6) form > .btn")).click();
        assertThrows(Exception.class, () -> driver.findElement(By.xpath("//span[contains(.,\'New menu\')]")));
        TimeUnit.MILLISECONDS.sleep(1000); // Page needs to load for some buttons to work and alerts to show
        driver.findElement(By.xpath("//a[contains(text(),'Log Out')]")).click();

        Menu menu2 = menuRepository.findAllByNameContains("IT MENU FOR TESTING").get(0);
        menu2.setDescription(menu.getDescription());
        menu2.setName(menu.getName());
        menu2.setPrice(menu.getPrice());
        menu2.setImageurl(menu.getImageurl());
        menuRepository.save(menu2);
    }
}
