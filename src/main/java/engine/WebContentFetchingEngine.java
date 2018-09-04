package engine;

import org.openqa.selenium.WebDriver;
import travel.browser.SeleniumWebDriverFirefox;

public class WebContentFetchingEngine {
    private static WebDriver driver = SeleniumWebDriverFirefox.getDriver();

    public static WebDriver getWebDriver() {
        return driver;
    }
}
