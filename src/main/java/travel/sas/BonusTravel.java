package travel.sas;

import utils.EmailUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.stream.Collectors;

public class BonusTravel {
    public enum ChinaCityCode {
        BEIJING("PEK"),
        SHANGHAI("PVG");

        String des;

        ChinaCityCode(String des) {
            this.des = des;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Random random = new Random();
        while (true) {
            for (ChinaCityCode chinaCityCode : ChinaCityCode.values()) {
                LocalDate localDate = LocalDate.of(2019, 8, 9);
                for (int i = 0; i < 7; i++) {
                    localDate = localDate.plusDays(1);
                    String stringDate = localDate.format(DateTimeFormatter.BASIC_ISO_DATE);
                    URL url = new URL("https://api.flysas.com/offers/flights?to=SVG&from=" + chinaCityCode.des +  "&outDate=" + stringDate + "&adt=1&chd=0&inf=0&yth=0&bookingFlow=points&pos=no&channel=web&displayType=upsell");
                    URLConnection urlConnection = url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String reply = bufferedReader.lines().collect(Collectors.joining());
                    System.out.println(chinaCityCode + ": " + stringDate + ": " + reply);
                    if (!reply.startsWith("{\"errors\":[{\"errorCode\"")) {
                        EmailUtils.sendEmail("longcuino@gmail.com", "Bonus Travel Changed: " + reply, reply);
                    }
                    Thread.sleep(random.nextInt(10) * 60 * 1000);
                }
            }

        }
    }
}
