package com.automation.google;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import org.apache.commons.io.FileUtils;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GoogleSearchTest {

    @Test
    public void testGoogleSearch() {
        WebDriverManager.chromedriver().clearDriverCache().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            // Langkah 1: Buka Google
            driver.get("https://www.google.com");
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
            takeScreenshot(driver, "1_google_homepage");

           
            WebElement searchBox = findSearchBox(wait);
            searchBox.clear();
            searchBox.sendKeys("Selenium WebDriver");
            searchBox.sendKeys(Keys.RETURN);
            takeScreenshot(driver, "2_after_search");

           
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("search")));
            WebElement firstResult = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("div.g a h3, div.rc a h3")
                )
            );
            firstResult.click();
            takeScreenshot(driver, "3_after_click_first_result");

            wait.until(ExpectedConditions.titleContains("Selenium"));
            String title = driver.getTitle();
            System.out.println("Page title: " + title);
            assertTrue(title.toLowerCase().contains("selenium"));
            takeScreenshot(driver, "4_verification_success");

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
            takeScreenshot(driver, "error_" + System.currentTimeMillis());
            throw e; // Re-throw exception agar test gagal
        } finally {
            driver.quit();
        }
    }

   
    private WebElement findSearchBox(WebDriverWait wait) {
        By[] possibleLocators = {
            By.name("q"),
            By.xpath("//textarea[@name='q']"),
            By.cssSelector("[name='q']")
        };
        
        for (By locator : possibleLocators) {
            try {
                return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            } catch (TimeoutException e) {
                continue;
            }
        }
        throw new RuntimeException("Google search box not found");
    }

   
    private void takeScreenshot(WebDriver driver, String screenshotName) {
        try {
            File folder = new File("screenshots");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            File destination = new File(folder, screenshotName + "_" + timestamp + ".png");
            FileUtils.copyFile(screenshot, destination);
            System.out.println("Screenshot disimpan di: " + destination.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Gagal mengambil screenshot: " + e.getMessage());
        }
    }
}