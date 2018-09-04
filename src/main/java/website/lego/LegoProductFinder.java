package website.lego;

import engine.WebContentFetchingEngine;
import travel.Credential;
import utils.EmailUtils;

import java.util.Date;
import java.util.Random;

public class LegoProductFinder {

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();

        while (true) {
//        boolean isProductExist = WebContentFetchingEngine.isProductExist("https://shop.lego.com/en-NO/Downtown-Diner-10260");
            boolean isProductExist = WebContentFetchingEngine.isProductExist("https://shop.lego.com/en-NO/Millennium-Falcon-75192");
            System.out.println(new Date() + ": " + isProductExist);

            if(isProductExist) {
                EmailUtils.sendEmail(Credential.EMAILS[0], "Millennium Falcon Product is available now", "https://shop.lego.com/en-NO/Millennium-Falcon-75192");
                break;
            }
//            EmailUtils.sendEmail("longcuino@gmail.com", "Millennium Falcon Product is available now", "https://shop.lego.com/en-NO/Millennium-Falcon-75192");

            int randomMin = random.nextInt(30) + 60;
            Thread.sleep(randomMin * 60 * 1000);
        }

    }
}
