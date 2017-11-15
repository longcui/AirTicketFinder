package travel.finnAndMomodo;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import travel.domain.TicketInfo;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Longcui on 21.04.2015.
 */
public class TravelAgent {

    private static Logger logger = Logger.getLogger(TravelAgent.class);


    public static TicketInfo getPriceForFinn(WebDriver driver, String finnURLString)  {
        try{
            //        driver.manage().window().setPosition(new Point(-2000, 0));
            WebDriverWait wait = new WebDriverWait(driver, 90);

            driver.get(finnURLString);

//            wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("p.mbt.mrm.largetext"), "Billigst!"));
//            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("largetext primary-blue inline-banner-board")));
//            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.largetext.primary-blue.inline-banner-board")));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("progressIndicator")));
            //        List<WebElement> finnPrice = driver.findElements(By.cssSelector("p.mbt.mtt.mrm.largetext.blue"));
            try {
                By cssSelector = By.cssSelector("div.largetext.primary-blue.inline-banner-board");
                By descriAndTimes = By.cssSelector(".mtt.stone.inline-banner-board.smalltext");

                List<WebElement> elements = driver.findElements(cssSelector);
                TicketInfo ticketInfo = new TicketInfo();
                for (WebElement element : elements) {
                    String finnPrice = element.getText();
                    finnPrice = finnPrice.replaceAll(" ", "");
                    //eg:Billigst 6 050,-
                    if(finnPrice.startsWith("Billigst")) {
                        finnPrice = finnPrice.substring("Billigst".length(), finnPrice.length() - 2);
                        String durationText = driver.findElements(descriAndTimes).get(0).getText();
                        ticketInfo.setCheapest(Double.parseDouble(finnPrice));
                    } else if (finnPrice.startsWith("Raskest")) {
                        finnPrice = finnPrice.substring("Raskest".length(), finnPrice.length() - 2);
                        ticketInfo.setQuickest(Double.parseDouble(finnPrice));
                    } else if (finnPrice.startsWith("Best")) {
                        finnPrice = finnPrice.substring("Best".length(), finnPrice.length() - 2);
                        ticketInfo.setBest(Double.parseDouble(finnPrice));
                    }
                }
                return ticketInfo;
            } catch (StaleElementReferenceException e) {
                logger.error("should not be here" + e.toString());
                return getPriceForFinn(driver, finnURLString);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return getPriceForFinn(driver, finnURLString);
        }
    }

//    private Duration parseMomondoDuration(String durationStr) {
//        if(durationStr != null) {
//            String[] durations = durationStr.split(" og ");
//            if(durations.length == 2) {
//
//            }
//        }
//    }

    public static TicketInfo getPriceFromMomondo(WebDriver driver, String momondoURLString) {
        try {
            //        driver.manage().window().setPosition(new Point(-2000, 0));
            //        Dimension windowMinSize = new Dimension(100,100);
            //        driver.manage().window().setSize(windowMinSize);
            WebDriverWait wait = new WebDriverWait(driver, 180);        //second
            wait.ignoring(TimeoutException.class);
            driver.get(momondoURLString);
            try {
                wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("div#searchProgressText"), "Search complete"));
            } catch (TimeoutException e) {
                logger.error(e.getMessage());
                logger.error("weired!!");
                return null;
            }

            String cheapestPrice = driver.findElement(By.cssSelector("li.option.cheapest span.price")).getText();
            String quickestPrice = driver.findElement(By.cssSelector("li.option.quickest span.price")).getText();
            String bestPrice = driver.findElement(By.cssSelector("li.option.rating.bestdeal span.price")).getText();

            NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
            TicketInfo price = new TicketInfo();
            price.setCheapest(numberFormat.parse(cheapestPrice).doubleValue() * 1000);
            price.setQuickest(numberFormat.parse(quickestPrice).doubleValue() * 1000);
            price.setBest(numberFormat.parse(bestPrice).doubleValue() * 1000);

            return price;


//            By cssSelector = By.cssSelector("span.value");
//            if (driver.findElements(cssSelector).size() > 0) {
//                WebElement webElement = driver.findElement(cssSelector);
//                //        List<WebElement> webElements = driver.findElements(By.cssSelector("span.value"));
//                //        for (WebElement webElement : webElements) {
//                //            String price = webElement.getText();
//                //            logger.info("price is: " + price);
//                //        }
//                String price = webElement.getText();
//                NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
//                try {
//                    return numberFormat.parse(price).doubleValue() * 1000;
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                    return 99999;
//                }
//            } else {
//                logger.info("no result found in momondo.");
//                return 99999;
//            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}

