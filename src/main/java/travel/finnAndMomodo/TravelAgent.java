package travel.finnAndMomodo;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Longcui on 21.04.2015.
 */
public class TravelAgent {

    private static Logger logger = Logger.getLogger(TravelAgent.class);


    public static double getPriceForFinn(WebDriver driver, String finnURLString)  {
        try{
            //        driver.manage().window().setPosition(new Point(-2000, 0));
            WebDriverWait wait = new WebDriverWait(driver, 90);

            driver.get(finnURLString);

//            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("p.mbt.mrm.largetext"), "Billigst!"));
//            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("largetext primary-blue inline-banner-board")));
//            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.largetext.primary-blue.inline-banner-board")));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("progressIndicator")));
            //        List<WebElement> finnPrice = driver.findElements(By.cssSelector("p.mbt.mtt.mrm.largetext.blue"));
            double price = Double.MAX_VALUE;
            try {
                By cssSelector = By.cssSelector("div.largetext.primary-blue.inline-banner-board");
                List<WebElement> elements = driver.findElements(cssSelector);
                for (WebElement element : elements) {
                    //eg:Billigst 6 050,-
                    String finnPrice = element.getText();
                    if(finnPrice.startsWith("Billigst ")) {
                        finnPrice = finnPrice.replaceAll(" ", "");
                        finnPrice = finnPrice.substring("Billigst".length(), finnPrice.length() - 2);
                        price = Double.parseDouble(finnPrice);
                    }

                }

//                if(elements.size() > 0) {
//                    for(int i = 0; i < 6; i ++) {
//                        WebElement element = driver.findElement(cssSelector);
//                        String finnPrice = element.getText();
//                        double tempP = Double.parseDouble(finnPrice.substring(0, finnPrice.length() - 2).replaceAll(" ", ""));
//                        if(tempP < price) {
//                            price = tempP;
//                            Thread.sleep(5 * 1000);
//                        } else {
//                            break;
//                        }
//                    }
//                } else {
//                    logger.info("no such css selector exists");
//                }
            } catch (StaleElementReferenceException e) {
                logger.error("should not be here" + e.toString());
            }
            //        double bestPrice = Double.MAX_VALUE;
            //        for (WebElement webElement : finnPrice) {
            //            String price = webElement.getText();
            //            logger.info("Finn:" + country + ":  " + from.getTime() + to.getTime() + ". price is: " + price);
            //            double p = Double.parseDouble(price.substring(0, price.length() - 2).replaceAll(" ", ""));
            //            if (p < bestPrice) {
            //                bestPrice = p;
            //            }
            //        }
            return price;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("!!!");
            return 99999;
        }
    }

    public static double getPriceFromMomondo(WebDriver driver, String momondoURLString) {
        try {
            //        driver.manage().window().setPosition(new Point(-2000, 0));
            //        Dimension windowMinSize = new Dimension(100,100);
            //        driver.manage().window().setSize(windowMinSize);
            WebDriverWait wait = new WebDriverWait(driver, 180);        //second
            wait.ignoring(TimeoutException.class);
            driver.get(momondoURLString);
            // Find the text input element by its name
            //            List<WebElement> elements = driver.findElements(By.className(".value"));
            //            wait.until(ExpectedConditions.visibilityOf(elements.get(0)));
            //        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span.value")));
            //        WebElement value = driver.findElement(By.cssSelector("span.value"));
            try {
                wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("div#searchProgressText"), "Search complete"));
            } catch (TimeoutException e) {
                logger.error(e.getMessage());
                logger.error("weired!!");
                return 99999;
            }
            By cssSelector = By.cssSelector("span.value");
            if (driver.findElements(cssSelector).size() > 0) {
                WebElement webElement = driver.findElement(cssSelector);
                //        List<WebElement> webElements = driver.findElements(By.cssSelector("span.value"));
                //        for (WebElement webElement : webElements) {
                //            String price = webElement.getText();
                //            logger.info("price is: " + price);
                //        }
                String price = webElement.getText();
                NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
                try {
                    return numberFormat.parse(price).doubleValue() * 1000;
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 99999;
                }
            } else {
                logger.info("no result found in momondo.");
                return 99999;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 99999;
        }
    }
}

