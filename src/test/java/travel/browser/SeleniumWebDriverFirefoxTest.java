package travel.browser;

import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumWebDriverFirefoxTest {
    int startDate = 17;
    int endDate = 28;

    @Test
    public void test1() {
        for(int i = startDate; i < endDate; i ++) {
            FirefoxDriver driver = SeleniumWebDriverFirefox.getDriver();
            driver.get("https://www.momondo.no/flight-search/SVG-PEK/2019-12-" + i + "?sort=price_a");
        }
    }

    @Test
    public void test11() {
        for(int i = startDate; i < endDate; i ++) {
            FirefoxDriver driver = SeleniumWebDriverFirefox.getDriver();
            driver.get("https://www.momondo.no/flight-search/osl-PEK/2019-12-" + i + "?sort=price_a");
        }
    }

    @Test
    public void test111() {
        for(int i = startDate; i < endDate; i ++) {
            FirefoxDriver driver = SeleniumWebDriverFirefox.getDriver();
            driver.get("https://www.momondo.no/flight-search/bgo-PEK/2019-12-" + i + "?sort=price_a");
        }
    }


    @Test
    public void test2() {
        for(int i = startDate; i < endDate; i ++) {
            FirefoxDriver driver = SeleniumWebDriverFirefox.getDriver();
            driver.get("https://www.momondo.no/flight-search/SVG-PVG/2019-12-" + i + "?sort=price_a");
        }
    }

    @Test
    public void test22() {
        for(int i = 10; i < endDate; i ++) {
            FirefoxDriver driver = SeleniumWebDriverFirefox.getDriver();
            driver.get("https://www.momondo.no/flight-search/osl-PVG/2019-12-" + i + "?sort=price_a");
        }
    }

    @Test
    public void test223() {
        for(int i = 10; i < endDate; i ++) {
            FirefoxDriver driver = SeleniumWebDriverFirefox.getDriver();
            driver.get("https://www.momondo.no/flight-search/bgo-PVG/2019-12-" + i + "?sort=price_a");
        }
    }





    @Test
    public void test3() {
        for(int i = 10; i < endDate; i ++) {
            FirefoxDriver driver = SeleniumWebDriverFirefox.getDriver();
            driver.get("https://www.momondo.no/flight-search/SVG-PEK/2019-12-" + i + "/2019-12-10?sort=price_a");
        }
    }

    @Test
    public void test4() {
        for(int i = 10; i < endDate; i ++) {
            FirefoxDriver driver = SeleniumWebDriverFirefox.getDriver();
            driver.get("https://www.momondo.no/flight-search/SVG-PVG/2019-12-" + i + "/2019-12-10?sort=price_a");
        }
    }

    @Test
    public void test45() throws InterruptedException {
        for(int i = 18; i < endDate; i ++) {
            FirefoxDriver driver = SeleniumWebDriverFirefox.getDriver();
            driver.get("https://www.momondo.no/flight-search/SVG-PEK/2019-12-" + i  + "-h/PVG-SVG/2020-01-13-h?sort=price_a");
            Thread.sleep(1000);
        }
    }

    @Test
    public void test5() throws InterruptedException {
        for(int i = 18; i < endDate; i ++) {
            FirefoxDriver driver = SeleniumWebDriverFirefox.getDriver();
            driver.get("https://www.momondo.no/flight-search/SVG-PVG/2019-12-" + i  + "-h/PVG-SVG/2020-01-13-h?sort=price_a");
            Thread.sleep(1000);
        }
    }
}