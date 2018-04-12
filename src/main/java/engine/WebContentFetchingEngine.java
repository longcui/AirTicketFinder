package engine;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import travel.browser.SeleniumWebDriverFirefox;

import java.util.List;

public class WebContentFetchingEngine {
    private static WebDriver driver = SeleniumWebDriverFirefox.getDriver();

    public static boolean isProductExist(String productUrl) {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        driver.get(productUrl);
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("available--now")));

        By className = By.className("available--now");
        List<WebElement> element = driver.findElements(className);
        if(element.size() != 0) {
            return true;
        } else {
            return false;
        }
    }
}
