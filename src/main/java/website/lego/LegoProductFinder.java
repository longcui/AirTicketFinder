package website.lego;

import engine.WebContentFetchingEngine;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import travel.Credential;
import utils.EmailUtils;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class LegoProductFinder {

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();

        while (true) {
            String url = "https://shop.lego.com/en-NO/Millennium-Falcon-75192";
//        boolean isProductExist = WebContentFetchingEngine.isProductExist("https://shop.lego.com/en-NO/Downtown-Diner-10260");
            WebDriver webDriver = WebContentFetchingEngine.getWebDriver();
            WebDriverWait wait = new WebDriverWait(webDriver, 30);
            webDriver.get(url);
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("available--now")));

            By className = By.className("available--now");
            List<WebElement> element = webDriver.findElements(className);
            boolean isProductExist = element.size() != 0;
            System.out.println(new Date() + ": " + isProductExist);

            if(isProductExist) {
                EmailUtils.sendEmail(Credential.EMAILS[0], "Millennium Falcon Product is available now", url);
                break;
            }
//            EmailUtils.sendEmail("longcuino@gmail.com", "Millennium Falcon Product is available now", "https://shop.lego.com/en-NO/Millennium-Falcon-75192");

            int randomMin = random.nextInt(30) + 60;
            Thread.sleep(randomMin * 60 * 1000);
        }

    }
}
