package travel.finnAndMomodo;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import travel.browser.SeleniumWebDriverFirefox;
import travel.domain.TicketInfo;
import travel.excel.ExcelExporter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import static utils.EmailUtils.sendEmail;

/**
 * Created by Longcui on 29.07.2014.
 */
public class ChinaTravel extends Travel{
    private static final Logger logger = Logger.getLogger(ChinaTravel.class);

    private static final String MOMONDO = "http://www.MOMONDO.com/flightsearch/?Search=true&TripType=2&SegNo=2&SO0=SHA&SD0=SVG&SDP0=14-05-2015&SO1=SVG&SD1=SHA&SDP1=20-05-2015&AD=1&TK=ECO&DO=false&NA=false";
    private static final String skyscan = "http://www.skyscanner.no/transport/flyavganger/svg/csha/150414/150415/billettpriser-fra-stavanger-til-shanghai-i-april-2015.html?adults=1&children=0&infants=0&cabinclass=economy&preferdirects=false&outboundaltsenabled=false&inboundaltsenabled=false&rtn=1";
    private static final String FINN = "http://www.FINN.no/reise/flybilletter/resultat?tripType=roundtrip&requestedOrigin=SHA.METROPOLITAN_AREA&requestedDestination=SVG.AIRPORT&requestedOrigin2=&requestedDestination2=&requestedDepartureDate=22.04.2015&requestedReturnDate=22.04.2015&numberOfAdults=1&numberOfChildren=0&cabinType=economy";
    //    private static String[] EMAILS = new String[]{"dragonworld1988@gmail.com", "newrala@gmail.com"};
    private static String[] EMAILS = new String[]{"dragonworld1988@gmail.com"};
    private static String[] DEBUG_EMAILS = new String[]{"dragonworld1988@gmail.com"};
    private static double momondoPrice;
    private static double unspecificPrice;
    private static int SLEEP_TIME = 60 * 60 * 1000;  //60min
    private static int DEBUG_SLEEP_TIME = 5 * 1000;  //30sec

    private int MINIMAL_STAY_DAY = 25;
//    private static int MINIMAL_STAY_DAY = 80;
    private int MAXIMAL_STAY_DAY = 35;
//    private static int MAXIMAL_STAY_DAY = 87;


    private final static Calendar start = Calendar.getInstance();
    private final static Calendar end = Calendar.getInstance();



    private static WebDriver driver = SeleniumWebDriverFirefox.getDriver();

    /**
     *
     * @param args args[0] could be "short"
     *             args[1] could be "oneWay"
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ChinaTravel chinaTravel = new ChinaTravel();
        chinaTravel.searchTickets(args);
    }

    private void searchTickets(String[] args) {
        //month: 0: jan
        start.set(2018, 2, 30);
        String startStr = System.getProperty("start");
        if(startStr != null) {
            start.setTime(Date.from(LocalDate.parse(startStr, DateTimeFormatter.ISO_DATE).atStartOfDay().toInstant(ZoneOffset.UTC)));
        }

        if(args.length > 1 && args[0].equals("short")) {
            end.set(2018, 4, 7);
           MINIMAL_STAY_DAY = 25;
           MAXIMAL_STAY_DAY = 35;
        } else {
            end.set(2018, 6, 5);
            MINIMAL_STAY_DAY = 80;
            MAXIMAL_STAY_DAY = 87;
        }

        String endStr = System.getProperty("end");
        if(endStr != null) {
            end.setTime(Date.from(LocalDate.parse(endStr, DateTimeFormatter.ISO_DATE).atStartOfDay().toInstant(ZoneOffset.UTC)));
        }

        while (true) {
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            if(args.length > 1 && args[1].equals("oneWay")) {
                to = null;
                MINIMAL_STAY_DAY = 0;
            }
            Calendar lastPossibleLeaveDay = Calendar.getInstance();
            lastPossibleLeaveDay.setTime(end.getTime());
            lastPossibleLeaveDay.add(Calendar.DAY_OF_MONTH, -MINIMAL_STAY_DAY);

            from.setTime(ChinaTravel.start.getTime());
            while (from.before(lastPossibleLeaveDay)) {
                String bestPriceURL;
                double bestPrice;

                Calendar toMax = Calendar.getInstance();
                if(to != null) {
                    to.setTime(from.getTime());
                    to.add(Calendar.DAY_OF_MONTH, MINIMAL_STAY_DAY);
                    toMax.setTime(from.getTime());
                    toMax.add(Calendar.DAY_OF_MONTH, MAXIMAL_STAY_DAY);
                }


                do {
//                    System.out.println(from.getTime() + "-------" + to.getTime());
                    for (MomondoChinaPlace momondoFromChinaPlace : MomondoChinaPlace.values()) {
                        FinnChinaPlace finnFromChinaPlace = FinnChinaPlace.valueOf(momondoFromChinaPlace.name());
                        for (MomondoNorwayPlace momondoToNorwayPlace : MomondoNorwayPlace.values()) {
                            FinnNorwayPlace finnToNorwayPlace = FinnNorwayPlace.valueOf(momondoToNorwayPlace.name());
//                            for (MomondoNorwayPlace momondoFromNorwayPlace : MomondoNorwayPlace.values()) {
                            MomondoNorwayPlace momondoFromNorwayPlace = MomondoNorwayPlace.STAVANGER;
                            {
                                FinnNorwayPlace finnFromNorwayPlace = FinnNorwayPlace.valueOf(momondoFromNorwayPlace.name());
                                for (MomondoChinaPlace momondoToChinaPlace : MomondoChinaPlace.values()) {
                                    FinnChinaPlace finnToChinaPlace = FinnChinaPlace.valueOf(momondoToChinaPlace.name());

                                    String momondoURLString = getMomondoURLString(momondoFromChinaPlace, momondoToNorwayPlace, momondoFromNorwayPlace, momondoToChinaPlace, from, to);
                                    TicketInfo priceFromMomondo = new TicketInfo();
//                                    TicketInfo priceFromMomondo = TravelAgent.getPriceFromMomondo(
//                                            driver, momondoURLString);
//                                    logger.info("Momondo:" + momondoFromChinaPlace + "-" + momondoToNorwayPlace + "-" + momondoFromNorwayPlace + "-" + momondoToChinaPlace +  ": " + from.getTime() + " " + to.getTime() + ". price is: " + priceFromMomondo);
//                                    prepareWritingToExcel(momondoFromChinaPlace.name(), momondoToNorwayPlace.name(), momondoFromNorwayPlace.name(), momondoToChinaPlace.name(),
//                                            from.getTime(), to.getTime(), priceFromMomondo, momondoURLString);


                                    //                        double priceFromMomondo = 99999;
                                    String finnURLString = getFinnURLString(finnFromChinaPlace, finnToNorwayPlace, finnFromNorwayPlace, finnToChinaPlace, from, to);
                                    TicketInfo priceForFinn = TravelAgent.getPriceForFinn(
                                            driver, finnURLString);
                                    //                        double priceForFinn = 555;
                                    logger.info("Finn   :" + finnFromChinaPlace + "-" + finnToNorwayPlace + "-" + finnFromNorwayPlace + "-" + finnToChinaPlace +  ": " + from.getTime() + " " + (to == null? null : to.getTime() ) + ". price is: " + priceForFinn);
                                    prepareWritingToExcel(momondoFromChinaPlace.name(), momondoToNorwayPlace.name(), momondoFromNorwayPlace.name(), momondoToChinaPlace.name(),
                                            from.getTime(), (to == null? null : to.getTime() ), priceForFinn, finnURLString);
                                    //                        for (String recipient : DEBUG_EMAILS) {
                                    //                            sendEmail(recipient, String.valueOf(bestPrice) + from.getTime() + to.getTime(), bestPriceURL);
                                    //                        }
                                    if (enableEmailNotification) {
                                        if((to != null && (priceForFinn.getCheapest() < 5500 || priceFromMomondo.getCheapest() < 5500))
                                                || (to == null && (priceForFinn.getCheapest() < 2650))) {
                                            double price = priceForFinn.getCheapest() < priceFromMomondo.getCheapest()? priceForFinn.getCheapest() : priceFromMomondo.getCheapest();
                                            String bestPriceUrl = priceForFinn.getCheapest() < priceFromMomondo.getCheapest()? finnURLString : momondoURLString;
                                            handleNotifications(price, momondoFromChinaPlace + "-" + momondoToNorwayPlace + "-" + momondoFromNorwayPlace + "-" + momondoToChinaPlace + "  " +  String.valueOf(price) + " NOK  from: " + new SimpleDateFormat("dd.MM.yyyy").format(from.getTime()) +   (to == null? "" : "  To: " + new SimpleDateFormat("dd.MM.yyyy").format(to.getTime())), bestPriceUrl);
                                        }
                                    }
//                        }
                                    if(to == null) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if(to != null){
                        to.add(Calendar.DAY_OF_MONTH, 1);
                    }
                } while (to != null && to.before(toMax) && to.before(end));
//                sleep(SLEEP_TIME);
                sleep(DEBUG_SLEEP_TIME);
                from.add(Calendar.DAY_OF_MONTH, 1);
            }
            ExcelExporter excelExporter = new ExcelExporter();
            excelExporter.setFromChinaCities(fromChinaCities);
            excelExporter.setToNorwayCities(toNorwayCities);
            excelExporter.setFromNorwayCities(fromNorwayCities);
            excelExporter.setToChinaCities(toChinaCities);
            excelExporter.setBestPrices(prices);
            excelExporter.setBestPriceUrls(bestPriceUrls);
            excelExporter.setFromDates(fromDates);
            excelExporter.setToDates(toDates);
//            excelExporter.writeToExcel();
        }

    }





    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private static String getMomondoURLString(MomondoChinaPlace momondoFromChinaPlace, MomondoNorwayPlace momondoToNorwayPlace,
                                              MomondoNorwayPlace momondoFromNorwayPlace, MomondoChinaPlace momondoToChinaPlace,
                                              Calendar from, Calendar to) {
        String momondo = "http://www.MOMONDO.com/flightsearch/?Search=true&TripType=2&SegNo=2&SO0=" +
                momondoFromChinaPlace.getCode() + "&SD0=" + momondoToNorwayPlace.getCode() + "&SDP0=";
        momondo += sdfMo.format(from.getTime());
        momondo += "&SO1=" + momondoFromNorwayPlace.code + "&SD1=" + momondoToChinaPlace.code + "&SDP1=";
        if(to != null ){
            momondo += sdfMo.format(to.getTime());
        }
        momondo += "&AD=1&TK=ECO&DO=false&NA=false";
        return momondo;
    }

//    https://www.finn.no/reise/flybilletter/resultat?tripType=openjaw&requestedOrigin=BGO.AIRPORT&requestedDestination=OSL.METROPOLITAN_AREA&requestedDepartureDate=16.03.2018&requestedOrigin2=SVG.AIRPORT
    // &requestedDestination2=PVG.AIRPORT&requestedReturnDate=04.06.2018&numberOfAdults=1&cabinType=economy

    private  static String getFinnURLString(FinnChinaPlace finnFromChinaPlace, FinnNorwayPlace finnToNorwayPlace, FinnNorwayPlace finnFromNorwayPlace, FinnChinaPlace finnToChinaPlace, Calendar from, Calendar to) {
        String finn = "https://www.FINN.no/reise/flybilletter/resultat?"
                + "requestedOrigin="  + finnFromChinaPlace.code + "&requestedDestination=" + finnToNorwayPlace.code;
        finn += "&requestedDepartureDate=" + sdfFin.format(from.getTime());
        if(to != null) {
            finn += "&tripType=roundtrip&requestedReturnDate=" + sdfFin.format(to.getTime());
            finn += "&requestedOrigin2=" + finnFromNorwayPlace.code + "&requestedDestination2=" + finnToChinaPlace.code;
        } else {
            finn += "&tripType=oneway&requestedReturnDate=";
        }
        finn += "&numberOfAdults=1&numberOfChildren=0&cabinType=economy";
        return  finn;
    }


    private static void handleNotifications(double price, String subject, String url) {
            String[] es = EMAILS;
            if (logger.isDebugEnabled()) {
                es = DEBUG_EMAILS;
            }

            for (String email : es) {
                sendEmail(email, subject, url);
            }
    }



    public enum MomondoChinaPlace {
        SHANGHAI("SHA"),
        BEIJING("BJS"),
        XIAN("SIA"),
//        GUANGZHOU("CAN"),
        NANJING("NKG");

        private String code;
        private MomondoChinaPlace(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum MomondoNorwayPlace {
        STAVANGER("SVG"),
        BERGEN("BGO"),
        OSLO("OSL");

        private String code;
        private MomondoNorwayPlace(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum FinnChinaPlace {
        SHANGHAI("SHA.METROPOLITAN_AREA"),
        BEIJING("BJS.METROPOLITAN_AREA"),
        XIAN("XIY.AIRPORT"),
        GUANGZHOU("CAN.AIRPORT"),
        NANJING("NKG.AIRPORT");

        private String code;
        private FinnChinaPlace(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum FinnNorwayPlace {
        STAVANGER("SVG.AIRPORT"),
        BERGEN("BGO.AIRPORT"),
        OSLO("OSL.METROPOLITAN_AREA");

        private String code;
        private FinnNorwayPlace(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}