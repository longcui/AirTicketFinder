package travel.finnAndMomodo;

import org.openqa.selenium.WebDriver;
import travel.browser.SeleniumWebDriverFirefox;
import travel.domain.TicketInfo;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static utils.EmailUtils.sendEmail;

public abstract class Travel {

    protected static WebDriver driver = SeleniumWebDriverFirefox.getDriver();

    protected static int SLEEP_TIME = 60 * 60 * 1000;  //60min
    protected static int DEBUG_SLEEP_TIME = 5 * 1000;  //30sec


    protected static boolean enableEmailNotification = true;


    protected static List<String> fromChinaCities = new ArrayList<String>();
    protected static List<String> toNorwayCities = new ArrayList<String>();

    protected static List<String> fromNorwayCities = new ArrayList<String>();
    protected static List<String> toChinaCities = new ArrayList<String>();

    protected static List<String> fromDates = new ArrayList<String>();
    protected static List<String> toDates = new ArrayList<String>();

    protected static List<TicketInfo> prices = new ArrayList<TicketInfo>();
    protected static List<String> bestPriceUrls = new ArrayList<String>();

    protected static SimpleDateFormat sdfMo = new SimpleDateFormat("dd-M-YYYY");     //14-5-2015
    protected static SimpleDateFormat sdfFin = new SimpleDateFormat("dd.MM.YYYY");     //14.05.2015

    protected int MINIMAL_STAY_DAY = 25;
    //    private static int MINIMAL_STAY_DAY = 80;
    protected int MAXIMAL_STAY_DAY = 35;
//    private static int MAXIMAL_STAY_DAY = 87;

    //    private static String[] EMAILS = new String[]{"dragonworld1988@gmail.com", "newrala@gmail.com"};
    private static String[] EMAILS = new String[]{"dragonworld1988@gmail.com"};
    private static String[] DEBUG_EMAILS = new String[]{"dragonworld1988@gmail.com"};

    protected final static Calendar start = Calendar.getInstance();
    protected final static Calendar end = Calendar.getInstance();

    Random random = new Random();

    void populateParams(String[] args) {
        String startStr = System.getProperty("start");
        if(startStr != null) {
            start.setTime(Date.from(LocalDate.parse(startStr, DateTimeFormatter.ISO_DATE).atStartOfDay().toInstant(ZoneOffset.UTC)));
        }

        if(args.length > 1 && args[0].equals("short")) {
            MINIMAL_STAY_DAY = 25;
            MAXIMAL_STAY_DAY = 35;
        } else {
            MINIMAL_STAY_DAY = 45;
            MAXIMAL_STAY_DAY = 75;
        }

        String endStr = System.getProperty("end");
        if(endStr != null) {
            end.setTime(Date.from(LocalDate.parse(endStr, DateTimeFormatter.ISO_DATE).atStartOfDay().toInstant(ZoneOffset.UTC)));
        }
    }


    static void prepareWritingToExcel(String fromChinaCity, String toNorwayCity, String fromNorwayCity, String toChinaCity, Date from, Date to, TicketInfo price, String bestPriceURL) {
        fromChinaCities.add(fromChinaCity);
        toNorwayCities.add(toNorwayCity);
        fromNorwayCities.add(fromNorwayCity);
        toChinaCities.add(toChinaCity);
        fromDates.add(sdfFin.format(from));
        if(to != null) {
            toDates.add(sdfFin.format(to));
        }
        prices.add(price);
        bestPriceUrls.add(bestPriceURL);
    }


    static String getMomondoURLString(String DepartFromCityCode, String DepartToCityCode,
                                      String ArriveFromCityCode, String ArriveToCityCode,
                                      Calendar from, Calendar to) {
        String momondo = "http://www.MOMONDO.com/flightsearch/?Search=true&TripType=2&SegNo=2&SO0=" +
                DepartFromCityCode + "&SD0=" + DepartToCityCode + "&SDP0=";
        momondo += sdfMo.format(from.getTime());
        momondo += "&SO1=" + ArriveFromCityCode + "&SD1=" + ArriveToCityCode + "&SDP1=";
        if(to != null ){
            momondo += sdfMo.format(to.getTime());
        }
        momondo += "&AD=1&TK=ECO&DO=false&NA=false";
        return momondo;
    }

    //    https://www.finn.no/reise/flybilletter/resultat?tripType=openjaw&requestedOrigin=BGO.AIRPORT&requestedDestination=OSL.METROPOLITAN_AREA&requestedDepartureDate=16.03.2018&requestedOrigin2=SVG.AIRPORT
    // &requestedDestination2=PVG.AIRPORT&requestedReturnDate=04.06.2018&numberOfAdults=1&cabinType=economy
    static String getFinnURLString(String DepartFromCityCode, String DepartToCityCode, String ArriveFromCityCode, String ArriveToCityCode, Calendar from, Calendar to) {
        String finn = "https://www.FINN.no/reise/flybilletter/resultat?"
                + "requestedOrigin="  + DepartFromCityCode + "&requestedDestination=" + DepartToCityCode;
        finn += "&requestedDepartureDate=" + sdfFin.format(from.getTime());
        if(to != null) {
            finn += "&tripType=openjaw&requestedReturnDate=" + sdfFin.format(to.getTime());
            finn += "&requestedOrigin2=" + ArriveFromCityCode + "&requestedDestination2=" + ArriveToCityCode;
        } else {
            finn += "&tripType=oneway&requestedReturnDate=";
        }
        finn += "&numberOfAdults=1&numberOfChildren=0&cabinType=economy";
        return  finn;
    }

    static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    static void handleNotifications(double price, String subject, String url) {
            String[] es = EMAILS;
//            if (FromChinaTravel.logger.isDebugEnabled()) {
//                es = FromChinaTravel.DEBUG_EMAILS;
//            }

            for (String email : es) {
                sendEmail(email, subject, url);
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
        STAVANGER("SVG");
//        BERGEN("BGO"),
//        OSLO("OSL");

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


}
