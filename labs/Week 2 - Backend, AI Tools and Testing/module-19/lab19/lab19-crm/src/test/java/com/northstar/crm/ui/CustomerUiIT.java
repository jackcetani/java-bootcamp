package com.northstar.crm.ui;

import com.northstar.crm.ui.pages.CustomerFormPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerUiIT {

    @LocalServerPort
    int port;

    WebDriver driver;
    String baseUrl;

    @BeforeAll
    static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,900");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void quit() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void createCustomerViaUi() {
        CustomerFormPage page = new CustomerFormPage(driver).open(baseUrl);
        page.fill("CUS-2001", "Test Via UI", "test.via.ui@example.com", "ACTIVE");
        page.submit();

        String result = page.resultText();
        assertTrue(result.contains("CUS-2001"));
        assertTrue(result.contains("Test Via UI"));
    }

    @Test
    void blankFullNameShowsValidationMessage() {
        CustomerFormPage page = new CustomerFormPage(driver).open(baseUrl);
        page.fill("CUS-2002", "", "blank.name@example.com", "PROSPECT");
        page.submit();

        String result = page.resultText().toLowerCase();
        assertTrue(result.contains("full name"));
    }
}