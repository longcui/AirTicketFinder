package travel.wizz;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by long on 15.03.2016.
 */

public class WizzWeekendsPriceV2 {

    private static Logger logger = Logger.getLogger(WizzWeekendsPriceV2.class);


    private enum AirportName {
        STAVANGER("SVG"),
        KATOWICE("KTW"),
//        RIGA("RIX"),
        SZCZECIN("SZZ"),
        CILNIUS("VNO"),
        KAUNAS("KUN"),
        GDANSK("GDN");

        AirportName(String name) {
            airportShortName = name;
        }
        private final String airportShortName;

        public String getAirportShortName() {
            return airportShortName;
        }
    }

    private class TravelOption implements Comparable{
        AirportName airportName;
        LocalDateTime fromDate, toDate;

        TravelOption(AirportName airportName, LocalDateTime fromDate, LocalDateTime toDate, int fromPrice, int toPrice) {
            this.airportName = airportName;
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.fromPrice = fromPrice;
            this.toPrice = toPrice;
        }

        @Override
        public String toString() {
            double totalPrice = fromPrice + toPrice;
            return "TravelOption{" +
                    "air_port_name=" + airportName +
                    ", fromDate=" + fromDate +
                    ", toDate=" + toDate +
                    ", fromPrice=" + fromPrice +
                    ", toPrice=" + toPrice +
                    ", totalPrice=" + totalPrice +
                    "}\n";
        }

        int fromPrice, toPrice;

        public LocalDateTime getFromDate() {
            return fromDate;
        }

        public LocalDateTime getToDate() {
            return toDate;
        }

        public int getFromPrice() {
            return fromPrice;
        }

        public int getToPrice() {
            return toPrice;
        }


        public int compareTo(Object o) {
            TravelOption o22 = (TravelOption) o;

            int total1 = this.getFromPrice() + this.getToPrice();
            int total2 = o22.getFromPrice() + o22.getToPrice();
//            return  total1 > total2 ? -1 : 1;
            return  - Integer.compare(total1, total2);
        }
    }

    /**
     * eg: year: 2016; month: 3
     * @param from
     * @param to
     * @param year
     * @param month
     * @return
     */
    public String getWizzUrl(AirportName from, AirportName to, int year, int month) {
        return "https://cdn.static.wizzair.com/en-GB/TimeTableAjax?departureIATA=" +
                from.getAirportShortName()
                +"&arrivalIATA=" +
                to.getAirportShortName()
                + "&year=" +
                year
                + "&month=" +
                month;
    }

    public HashMap<LocalDateTime, Integer> getPrices(AirportName from, AirportName to, int year, int month) {
        HashMap<LocalDateTime, Integer> rets = new HashMap<LocalDateTime, Integer>();
        try {
            String wizzUrl = getWizzUrl(from, to, year, month);
            logger.info("wizzUrl: " + wizzUrl);

            URL url = new URL(wizzUrl);
            URLConnection conn = url.openConnection();

            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                stringBuilder.append(line);
            }

            logger.debug("parsed url string is: " + stringBuilder.toString());
            JSONParser parser = new JSONParser();
            try {
                Object o = parser.parse(stringBuilder.toString());
                Iterator iterator = ((JSONArray) o).iterator();
                while (iterator.hasNext()) {
                    JSONObject jsonObject = (JSONObject) iterator.next();
                    Object minimumPrice = jsonObject.get("MinimumPrice");
                    if(minimumPrice != null){
                        Integer price = 0;
                        String minimumPriceS = (String) minimumPrice;
                        String parsedPrice = minimumPriceS.replace(",", "");
                        if(parsedPrice.startsWith("zł")) {
                            double v = Double.valueOf(parsedPrice.substring(2)) * 2.1;
                            price = (int)v;
                        } else if(parsedPrice.startsWith("kr")) {
                            price = Double.valueOf(parsedPrice.substring(2)).intValue();
                        }  else if(parsedPrice.startsWith("€")) {
                            price = (int)(Double.valueOf(parsedPrice.substring(1)) * 9);
                        } else {
                            logger.error("Could not parse price: " + parsedPrice);
                        }

                        //date and time
                        Object date = jsonObject.get("Date");
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");

                        for (Object flightObj : (JSONArray) jsonObject.get("Flights")) {
                            Object stdObj = ((JSONObject) flightObj).get("STD");
                            LocalDateTime localDateTime = LocalDateTime.parse((String) date + " " + (String) stdObj, dateTimeFormatter);
                            rets.put(localDateTime, price);
                        }
                    }

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rets;
    }


    private List<TravelOption> getTravelOptions(AirportName airportName) {
        Map<LocalDateTime, Integer> priceFrom = new HashMap();
        Map<LocalDateTime, Integer> priceTo = new HashMap();

        LocalDate localDate = LocalDate.now();
        for(int i = 0; i < 12; i ++){
            int month = localDate.getMonthValue();
            int year = localDate.getYear();
            priceFrom.putAll(getPrices(AirportName.STAVANGER, airportName, year, month));
            priceTo.putAll(getPrices(airportName, AirportName.STAVANGER, year, month));
            localDate = localDate.plusMonths(1);
        }

        return  filter(airportName, priceFrom, priceTo);
    }

    private List<TravelOption> filter(AirportName airport_name, Map<LocalDateTime, Integer> priceFrom, Map<LocalDateTime, Integer> priceTo) {
        ArrayList<TravelOption> rets = new ArrayList<>();
        for (LocalDateTime fromDateTime : priceFrom.keySet()) {
            Integer fromPrice = priceFrom.get(fromDateTime);
            Integer toPrice = 0;
            if(fromDateTime.getDayOfWeek() == DayOfWeek.THURSDAY ) {
                LocalDateTime saturday = fromDateTime.plusDays(2);
//                toPrice = priceTo.get(saturday.);
                if (toPrice != null) {
                    rets.add(new TravelOption(airport_name, fromDateTime, saturday, fromPrice, toPrice));
                }

                LocalDateTime sunday = saturday.plusDays(1);
                toPrice = priceTo.get(sunday);
                if (toPrice != null) {
                    rets.add(new TravelOption(airport_name, fromDateTime, sunday, fromPrice, toPrice));
                }

                LocalDateTime monday = sunday.plusDays(1);
                toPrice = priceTo.get(monday);
                if (toPrice != null) {
                    rets.add(new TravelOption(airport_name, fromDateTime, monday, fromPrice, toPrice));
                }
            } else if(fromDateTime.getDayOfWeek() == DayOfWeek.FRIDAY ) {
                LocalDateTime saturday = fromDateTime.plusDays(1);
                toPrice = priceTo.get(saturday);
                if(toPrice != null){
                    rets.add(new TravelOption(airport_name, fromDateTime, saturday, fromPrice, toPrice));
                }

                LocalDateTime sunday = saturday.plusDays(1);
                toPrice = priceTo.get(sunday);
                if(toPrice != null){
                    rets.add(new TravelOption(airport_name, fromDateTime, sunday, fromPrice, toPrice));
                }

                LocalDateTime monday = sunday.plusDays(1);
                toPrice = priceTo.get(monday);
                if (toPrice != null) {
                    rets.add(new TravelOption(airport_name, fromDateTime, monday, fromPrice, toPrice));
                }
            } else if(fromDateTime.getDayOfWeek() == DayOfWeek.SATURDAY){
                LocalDateTime sunday = fromDateTime.plusDays(1);
                toPrice = priceTo.get(sunday);
                if(toPrice != null){
                    rets.add(new TravelOption(airport_name, fromDateTime, sunday, fromPrice, toPrice));
                }

                LocalDateTime monday = sunday.plusDays(1);
                toPrice = priceTo.get(monday);
                if (toPrice != null) {
                    rets.add(new TravelOption(airport_name, fromDateTime, monday, fromPrice, toPrice));
                }
            }
        }
        return rets;
    }

    public static List<TravelOption> getTravelOptions() {
        WizzWeekendsPriceV2 wizzWeekendsPrice = new WizzWeekendsPriceV2();
        ArrayList<TravelOption> results = new ArrayList<>();
        for (AirportName airportName : AirportName.values()) {
            results.addAll(wizzWeekendsPrice.getTravelOptions(airportName));
        }
        return results;
    }

    public static void main(String[] args) {
        List<TravelOption> travelOptions = getTravelOptions();

        Collections.sort(travelOptions);
        System.out.println(travelOptions);

    }
}
