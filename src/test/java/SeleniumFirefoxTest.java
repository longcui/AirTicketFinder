import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SeleniumFirefoxTest {

    @Test
    public void test() {
        System.setProperty("webdriver.gecko.driver","C:\\programs\\geckodriver-v0.19.1-win64\\geckodriver.exe");

        WebDriver driver = new FirefoxDriver();
        driver.get("http://www.google.com");
    }
}
