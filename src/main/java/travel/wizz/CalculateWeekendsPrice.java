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
import java.net.MalformedURLException;
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


    public enum AIR_PORT_NAME{
        STAVANGER("SVG"),
        GDANSK("GDN");

        AIR_PORT_NAME(String name) {
            airportShortName = name;
        }
        private final String airportShortName;

        public String getAirportShortName() {
            return airportShortName;
        }
    }

    public class TravelOption implements Comparable{
        LocalDate fromDate, toDate;
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


        public TravelOption(LocalDate from, LocalDate to, int fromPrice, int toPrice) {
            this.fromDate = from;
            this.toDate = to;
            this.fromPrice = fromPrice;
            this.toPrice = toPrice;
        }

        @Override
        public String toString() {
            double totalPrice = fromPrice + toPrice;
            return "TravelOption{" +
                    "fromDate=" + fromDate +
                    ", toDate=" + toDate +
                    ", fromPrice=" + fromPrice +
                    ", toPrice=" + toPrice +
                    ", totalPrice=" + totalPrice +
                    "}\n";
        }

        public int compareTo(Object o) {
            TravelOption o22 = (TravelOption) o;

            int total1 = this.getFromPrice() + this.getToPrice();
            int total2 = o22.getFromPrice() + o22.getToPrice();
            return  total1 > total2 ? 1 : -1;
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
    public String getWizzUrl(AIR_PORT_NAME from, AIR_PORT_NAME to, int year, int month) {
        return "https://cdn.static.wizzair.com/en-GB/TimeTableAjax?departureIATA=" +
                from.getAirportShortName()
                +"&arrivalIATA=" +
                to.getAirportShortName()
                + "&year=" +
                year
                + "&month=" +
                month;
    }

    public HashMap<LocalDate, Integer> getPrices(AIR_PORT_NAME from, AIR_PORT_NAME to, int year, int month) {
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
                        String replaced = minimumPriceS.replace(",", "");
                        String currencyS = replaced.substring(0, 2);
                        if(currencyS.equals("zł")) {
                            double v = Double.valueOf(replaced.substring(2)) * 2.22;
                            price = (int)v;
                        } else if(currencyS.equals("kr")) {
                            price = Double.valueOf(replaced.substring(2)).intValue();
                        } else {
                            logger.error("Could not parse currency: " + currencyS);
                        }
                        rets.put(localDate, price);
                    }

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rets;
    }

    public List<TravelOption> getTravelOptions(AIR_PORT_NAME arrival, int year) {
        ArrayList<TravelOption> rets = new ArrayList<TravelOption>();
        Map<LocalDate, Integer> priceFrom = new HashedMap();
        Map<LocalDate, Integer> priceTo = new HashedMap();

        int month = LocalDate.now().getMonthValue();
        for(int i = month; i < 13; i ++){
            priceFrom.putAll(getPrices(AIR_PORT_NAME.STAVANGER, arrival, year, i));
            priceTo.putAll(getPrices(arrival, AIR_PORT_NAME.STAVANGER, year, i));
        }

        for (LocalDate date : priceFrom.keySet()) {
            Integer fromPrice = priceFrom.get(date);
            Integer toPrice = 0;
            if(date.getDayOfWeek() == DayOfWeek.THURSDAY ) {
                LocalDate saturday = date.plusDays(2);
                toPrice = priceTo.get(saturday);
                if (toPrice != null) {
                    rets.add(new TravelOption(date, saturday, fromPrice, toPrice));
                }

                LocalDate sunday = saturday.plusDays(1);
                toPrice = priceTo.get(sunday);
                if (toPrice != null) {
                    rets.add(new TravelOption(date, sunday, fromPrice, toPrice));
                }

                LocalDate monday = sunday.plusDays(1);
                toPrice = priceTo.get(monday);
                if (toPrice != null) {
                    rets.add(new TravelOption(date, monday, fromPrice, toPrice));
                }
            } else if(date.getDayOfWeek() == DayOfWeek.FRIDAY ) {
                LocalDate saturday = date.plusDays(1);
                toPrice = priceTo.get(saturday);
                if(toPrice != null){
                    rets.add(new TravelOption(date, saturday, fromPrice, toPrice));
                }

                LocalDate sunday = saturday.plusDays(1);
                toPrice = priceTo.get(sunday);
                if(toPrice != null){
                    rets.add(new TravelOption(date, sunday, fromPrice, toPrice));
                }

                LocalDate monday = sunday.plusDays(1);
                toPrice = priceTo.get(monday);
                if (toPrice != null) {
                    rets.add(new TravelOption(date, monday, fromPrice, toPrice));
                }
            } else if(date.getDayOfWeek() == DayOfWeek.SATURDAY){
                LocalDate sunday = date.plusDays(1);
                toPrice = priceTo.get(sunday);
                if(toPrice != null){
                    rets.add(new TravelOption(date, sunday, fromPrice, toPrice));
                }

                LocalDate monday = sunday.plusDays(1);
                toPrice = priceTo.get(monday);
                if (toPrice != null) {
                    rets.add(new TravelOption(date, monday, fromPrice, toPrice));
                }
            }
        }
        return rets;
    }

    public static void main(String[] args) {
        CalculateWeekendsPrice calculateWeekendsPrice = new CalculateWeekendsPrice();
        int year = 2016;
        AIR_PORT_NAME arrival = AIR_PORT_NAME.GDANSK;
        int month = 3;
        List<TravelOption> travelOptions = calculateWeekendsPrice.getTravelOptions(arrival, year);

        Collections.sort(travelOptions);
        System.out.println(travelOptions);

    }
}
