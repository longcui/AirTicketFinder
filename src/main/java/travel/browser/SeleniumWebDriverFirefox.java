package travel.browser;

import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumWebDriverFirefox {
    public static FirefoxDriver getDriver() {
        System.setProperty("webdriver.gecko.driver", "C:\\programs\\geckodriver-v0.23.0-win64\\geckodriver.exe");

        https://stackoverflow.com/questions/37803781/disable-log-trace-in-marionette-driver
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");
        return  new FirefoxDriver();
    }
}
