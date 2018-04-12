package travel.sas;

import travel.finnAndMomodo.Travel;
import utils.EmailUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class BonusTravel extends Travel {
    public enum ChinaCityCode {
        BEIJING("PEK");
//        SHANGHAI("PVG");

        String des;

        ChinaCityCode(String des) {
            this.des = des;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        while (true) {
            for (ChinaCityCode chinaCityCode : ChinaCityCode.values()) {
                LocalDate localDate = LocalDate.of(2018, 6, 27);
                for (int i = 0; i < 6; i++) {
                    localDate = localDate.plusDays(1);
                    String stringDate = localDate.format(DateTimeFormatter.BASIC_ISO_DATE);
                    URL url = new URL("https://api.flysas.com/offers/flights?to=SVG&from=" + chinaCityCode.des +  "&outDate=" + stringDate + "&adt=2&chd=0&inf=0&yth=0&bookingFlow=points&pos=no&channel=web&displayType=upsell");
                    URLConnection urlConnection = url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    Scanner scanner = new Scanner(inputStream);
                    String reply = scanner.next();
                    System.out.println(chinaCityCode + ": " + stringDate + ": " + reply);
                    if (!reply.startsWith("{\"errors\":[{\"errorCode\"")) {
                        EmailUtils.sendEmail("longcuino@gmail.com", "Bonus Travel Changed: " + reply, reply);
                    }
                    Thread.sleep(5 * 60 * 1000);
                }
            }

        }
    }
}
