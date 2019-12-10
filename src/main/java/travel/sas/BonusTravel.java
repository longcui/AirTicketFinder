package travel.sas;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utils.CmdUtils;
import utils.EmailUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.stream.Collectors;

import static travel.Credential.RECIPIENT_139_MAIL;

public class BonusTravel {
    public enum ChinaCityCode {
        BEIJING("PEK"),
        SHANGHAI("PVG");

        String des;

        ChinaCityCode(String des) {
            this.des = des;
        }
    }

    public enum WesternCityCode {
        STAVANGER("SVG");
//        TROMSO("TOS"),
//        OSLO("OSL"),
//        CPH("CPH"),
//        BERGEN("BGO");
//        OSLO("OSL"),
//        COPENHAGEN("CPH");

        String des;

        WesternCityCode(String des) {
            this.des = des;
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Path path = new File("./tmp.txt").toPath();
        Random random = new Random();
        while (true) {
            try {
                for (ChinaCityCode chinaCityCode : ChinaCityCode.values()) {
                    for (WesternCityCode westernCityCode : WesternCityCode.values()) {
                        LocalDate localDate = LocalDate.of(2020, 1, 6);
                        for (int i = 0; i < 10; i++) {
                            localDate = localDate.plusDays(1);
                            String stringDate = localDate.format(DateTimeFormatter.BASIC_ISO_DATE);
                            URL url = new URL("https://api.flysas.com/offers/flights?to=" + westernCityCode.des + "&from=" + chinaCityCode.des + "&outDate=" + stringDate + "&adt=1&chd=0&inf=0&yth=0&bookingFlow=points&pos=no&channel=web&displayType=upsell");
//                            URL url = new URL("https://api.flysas.com/offers/flights?to=" + chinaCityCode.des + "&from=" + westernCityCode.des + "&outDate=" + stringDate + "&adt=1&chd=0&inf=0&yth=0&bookingFlow=points&pos=no&channel=web&displayType=upsell");
                            URLConnection urlConnection = url.openConnection();
                            InputStream inputStream = urlConnection.getInputStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                            String reply = bufferedReader.lines().collect(Collectors.joining());
                            Files.write(path, reply.getBytes());

                            System.out.println(LocalDateTime.now() + ": " + chinaCityCode + "->" + westernCityCode +  ": " + stringDate + ": " + reply);
//                            System.out.println(LocalDateTime.now() + ": " + westernCityCode + "->" + chinaCityCode +  ": " + stringDate + ": " + reply);
                            String cmdResult = CmdUtils.runCmd("type \"" + path.toUri().getPath().substring(1) + "\" | jq  \"{price: .outboundLowestFare.points, cabinName: .outboundLowestFare.cabinName}\"");
                            System.out.println(cmdResult);
                            if (!cmdResult.isEmpty()) {
                                JSONParser jsonParser = new JSONParser();
                                JSONObject jsonObject = (JSONObject) jsonParser.parse(cmdResult);
                                if(qualifiedTicket(jsonObject)) {
                                    EmailUtils.sendEmail(RECIPIENT_139_MAIL, "Bonus Travel Changed: " + reply, reply);
                                }
                            }
                            Thread.sleep(random.nextInt(10) * random.nextInt(30) * 1000);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("error: " + e);
                Thread.sleep(random.nextInt(10) * 60 * 1000);
            } finally {
                Files.deleteIfExists(path);
            }
        }
    }

    private static boolean qualifiedTicket(JSONObject jsonObject) {
        Long price = (Long) jsonObject.get("price");
        String cabinName = (String) jsonObject.get("cabinName");
        if(price == null || cabinName == null) {
            return false;
        }

        switch (cabinName) {
//            case "PLUS" : {
//                if(price < 80000) {
//                    return true;
//                }
//                break;
//            }
            case "BUSINESS": {
                if(price < 60000) {
                    return true;
                }
                break;
            }
            default:
                if(price < 40000) {
                    return true;
                }
                break;
        }
        return false;
    }
}
