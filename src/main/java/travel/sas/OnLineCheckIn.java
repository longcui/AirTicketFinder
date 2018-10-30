package travel.sas;

import engine.WebContentFetchingEngine;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import travel.Credential;
import utils.EmailUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class OnLineCheckIn {
    public static void main(String... args) throws InterruptedException {
        Random random = new Random();
        while (true) {
            WebDriver webDriver = WebContentFetchingEngine.getWebDriver();
            String url = "https://www.sas.no/checkin/itinerary?bookingreference=" + Credential.BOOKING_REF + "&names=" + Credential.SUR_NAME;
            WebDriverWait wait = new WebDriverWait(webDriver, 100);
            webDriver.get(url);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3")));
            List<WebElement> elements = webDriver.findElements(By.cssSelector("h3"));
            Optional<WebElement> element = elements.stream()
                    .filter(webElement -> webElement.getText().contains("IKKE TILGJENGELIG"))
                    .findFirst();
            if (element.isPresent()) {
                System.out.println(element.get().getText());
                EmailUtils.sendEmail(Credential.EMAILS[0], "Check in ", url);
                System.out.println("sent");
            }
            Thread.sleep(random.nextInt(10 * 60) * 1000);
            System.out.println("done");
        }
    }
}
