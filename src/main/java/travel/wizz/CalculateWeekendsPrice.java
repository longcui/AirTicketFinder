package travel.wizz;

import org.apache.commons.collections.map.HashedMap;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by long on 15.03.2016.
 */
public class CalculateWeekendsPrice {

    private static Logger logger = Logger.getLogger(CalculateWeekendsPrice.class);


    private enum AIRPORT_NAME {
        STAVANGER("SVG"),
        KATOWICE("KTW"),
        RIGA("RIX"),
        SZCZECIN("SZZ"),
        CILNIUS("VNO"),
        KAUNAS("KUN"),
        GDANSK("GDN");

        AIRPORT_NAME(String name) {
            airportShortName = name;
        }
        private final String airportShortName;

        public String getAirportShortName() {
            return airportShortName;
        }
    }

    private class TravelOption implements Comparable{
        AIRPORT_NAME AIRPORT_name;
        LocalDate fromDate, toDate;

        TravelOption(AIRPORT_NAME AIRPORT_name, LocalDate fromDate, LocalDate toDate, int fromPrice, int toPrice) {
            this.AIRPORT_name = AIRPORT_name;
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.fromPrice = fromPrice;
            this.toPrice = toPrice;
        }

        @Override
        public String toString() {
            double totalPrice = fromPrice + toPrice;
            return "TravelOption{" +
                    "air_port_name=" + AIRPORT_name +
                    ", fromDate=" + fromDate +
                    ", toDate=" + toDate +
                    ", fromPrice=" + fromPrice +
                    ", toPrice=" + toPrice +
                    ", totalPrice=" + totalPrice +
                    "}\n";
        }

        int fromPrice, toPrice;

        public LocalDate getFromDate() {
            return fromDate;
        }

        public LocalDate getToDate() {
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
    public String getWizzUrl(AIRPORT_NAME from, AIRPORT_NAME to, int year, int month) {
        return "https://cdn.static.wizzair.com/en-GB/TimeTableAjax?departureIATA=" +
                from.getAirportShortName()
                +"&arrivalIATA=" +
                to.getAirportShortName()
                + "&year=" +
                year
                + "&month=" +
                month;
    }

    public HashMap<LocalDate, Integer> getPrices(AIRPORT_NAME from, AIRPORT_NAME to, int year, int month) {
        HashMap<LocalDate, Integer> rets = new HashMap<LocalDate, Integer>();
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
                        Object date = jsonObject.get("Date");
                        if(date == null){
                            logger.error("Date is null.");
                            continue;
                        }
                        //                            Date yyyyMMdd = new SimpleDateFormat("yyyyMMdd").parse(date.toString());
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                        LocalDate localDate = LocalDate.parse((String) date, dateTimeFormatter);

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
                        rets.put(localDate, price);
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

    private List<TravelOption> getTravelOptions(AIRPORT_NAME airport_name) {
        ArrayList<TravelOption> rets = new ArrayList<>();
        Map<LocalDate, Integer> priceFrom = new HashedMap();
        Map<LocalDate, Integer> priceTo = new HashedMap();

        LocalDate localDate = LocalDate.now();
        for(int i = 0; i < 12; i ++){
            int month = localDate.getMonthValue();
            int year = localDate.getYear();
            priceFrom.putAll(getPrices(AIRPORT_NAME.STAVANGER, airport_name, year, month));
            priceTo.putAll(getPrices(airport_name, AIRPORT_NAME.STAVANGER, year, month));
            localDate = localDate.plusMonths(1);
        }

        for (LocalDate fromDate : priceFrom.keySet()) {
            Integer fromPrice = priceFrom.get(fromDate);
            Integer toPrice = 0;
            if(fromDate.getDayOfWeek() == DayOfWeek.THURSDAY ) {
                LocalDate saturday = fromDate.plusDays(2);
                toPrice = priceTo.get(saturday);
                if (toPrice != null) {
                    rets.add(new TravelOption(airport_name, fromDate, saturday, fromPrice, toPrice));
                }

                LocalDate sunday = saturday.plusDays(1);
                toPrice = priceTo.get(sunday);
                if (toPrice != null) {
                    rets.add(new TravelOption(airport_name, fromDate, sunday, fromPrice, toPrice));
                }

                LocalDate monday = sunday.plusDays(1);
                toPrice = priceTo.get(monday);
                if (toPrice != null) {
                    rets.add(new TravelOption(airport_name, fromDate, monday, fromPrice, toPrice));
                }
            } else if(fromDate.getDayOfWeek() == DayOfWeek.FRIDAY ) {
                LocalDate saturday = fromDate.plusDays(1);
                toPrice = priceTo.get(saturday);
                if(toPrice != null){
                    rets.add(new TravelOption(airport_name, fromDate, saturday, fromPrice, toPrice));
                }

                LocalDate sunday = saturday.plusDays(1);
                toPrice = priceTo.get(sunday);
                if(toPrice != null){
                    rets.add(new TravelOption(airport_name, fromDate, sunday, fromPrice, toPrice));
                }

                LocalDate monday = sunday.plusDays(1);
                toPrice = priceTo.get(monday);
                if (toPrice != null) {
                    rets.add(new TravelOption(airport_name, fromDate, monday, fromPrice, toPrice));
                }
            } else if(fromDate.getDayOfWeek() == DayOfWeek.SATURDAY){
                LocalDate sunday = fromDate.plusDays(1);
                toPrice = priceTo.get(sunday);
                if(toPrice != null){
                    rets.add(new TravelOption(airport_name, fromDate, sunday, fromPrice, toPrice));
                }

                LocalDate monday = sunday.plusDays(1);
                toPrice = priceTo.get(monday);
                if (toPrice != null) {
                    rets.add(new TravelOption(airport_name, fromDate, monday, fromPrice, toPrice));
                }
            }
        }
        return rets;
    }

    public static void main(String[] args) {
        CalculateWeekendsPrice calculateWeekendsPrice = new CalculateWeekendsPrice();
        ArrayList<TravelOption> results = new ArrayList<>();
        for (AIRPORT_NAME AIRPORT_name : AIRPORT_NAME.values()) {
            results.addAll(calculateWeekendsPrice.getTravelOptions(AIRPORT_name));
        }


        Collections.sort(results);
        System.out.println(results);

    }
}
