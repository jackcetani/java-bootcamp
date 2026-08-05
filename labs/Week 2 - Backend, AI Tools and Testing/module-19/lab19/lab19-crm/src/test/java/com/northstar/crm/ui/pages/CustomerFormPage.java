package com.northstar.crm.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/** Page Object — locate via data-testid only. */
public class CustomerFormPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CustomerFormPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public CustomerFormPage open(String baseUrl) {
        driver.get(baseUrl + "/customers.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid=\"customer-id\"]")));
        return this;
    }

    public void fill(String id, String name, String email, String status) {
        var idField = driver.findElement(By.cssSelector("[data-testid=\"customer-id\"]"));
        idField.clear();
        idField.sendKeys(id);

        var nameField = driver.findElement(By.cssSelector("[data-testid=\"full-name\"]"));
        nameField.clear();
        nameField.sendKeys(name);

        var emailField = driver.findElement(By.cssSelector("[data-testid=\"email\"]"));
        emailField.clear();
        emailField.sendKeys(email);

        var statusField = driver.findElement(By.cssSelector("[data-testid=\"status\"]"));
        statusField.clear();
        statusField.sendKeys(status);
    }

    public void submit() {
        driver.findElement(By.cssSelector("[data-testid=\"submit-customer\"]")).click();
    }

    public String resultText() {
        By resultLocator = By.cssSelector("[data-testid=\"create-result\"]");
        wait.until(d -> !d.findElement(resultLocator).getText().isBlank());
        return driver.findElement(resultLocator).getText();
    }
}