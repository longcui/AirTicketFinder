import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static travel.browser.SeleniumWebDriverFirefox.GECKO_DRIVER;

public class SeleniumFirefoxTest {

    @Test
    public void test() {
        System.setProperty("webdriver.gecko.driver",GECKO_DRIVER);

        WebDriver driver = new FirefoxDriver();
        driver.get("http://www.google.com");
        driver.quit();
    }
}
