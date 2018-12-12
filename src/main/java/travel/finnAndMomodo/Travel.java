package travel.finnAndMomodo;

import org.openqa.selenium.WebDriver;
import travel.browser.SeleniumWebDriverFirefox;
import travel.domain.TicketInfo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static utils.EmailUtils.sendEmail;

public abstract class Travel {

    protected static WebDriver driver = SeleniumWebDriverFirefox.getDriver();

    protected static int SLEEP_TIME = 60 * 60 * 1000;  //60min
    protected static int DEBUG_SLEEP_TIME = 5 * 1000;  //30sec


    protected static boolean enableEmailNotification = true;


    protected static List<TicketInfo> ticketInfos = new ArrayList<TicketInfo>();


    protected static DateTimeFormatter momondoIsoLocalDate = DateTimeFormatter.ISO_LOCAL_DATE;
    protected static DateTimeFormatter sdfFin = DateTimeFormatter.ofPattern("dd.MM.YYYY");     //14.05.2015

    protected int MINIMAL_STAY_DAY;
    protected int MAXIMAL_STAY_DAY;

    //    private static String[] EMAILS = new String[]{"dragonworld1988@gmail.com", "newrala@gmail.com"};
    private static String[] EMAILS = new String[]{"dragonworld1988@gmail.com"};
    private static String[] DEBUG_EMAILS = new String[]{"dragonworld1988@gmail.com"};

    protected LocalDate start;
    protected LocalDate end;

    Random random = new Random();
    private String[] froms;
    private String[] tos;
    private String[] from2s;
    private String[] to2s;

    public enum TripType {
        ONEWAY,
        TUR_RETUR;
    }

    void populateParams(String[] args) {
        String startStr = System.getProperty("start");
        if(startStr != null) {
            start = LocalDate.parse(startStr, DateTimeFormatter.ISO_DATE);
        }

        String minMaxStayDay = System.getProperty("minMaxStayDay");
        if(minMaxStayDay != null) {
            String[] strings = minMaxStayDay.split(",");
            MINIMAL_STAY_DAY = Integer.parseInt(strings[0]);
            MAXIMAL_STAY_DAY = Integer.parseInt(strings[1]);
        } else {
            MINIMAL_STAY_DAY = 25;
            MAXIMAL_STAY_DAY = 35;
        }

        String endStr = System.getProperty("end");
        if(endStr != null) {
            end = LocalDate.parse(endStr, DateTimeFormatter.ISO_DATE);
        }

        froms = System.getProperty("from").split(",");
        tos = System.getProperty("to").split(",");
        from2s = System.getProperty("from2").split(",");
        to2s = System.getProperty("to2").split(",");
    }





    static String getMomondoURLString(String DepartFromCityCode, String DepartToCityCode,
                                      String ArriveFromCityCode, String ArriveToCityCode,
                                      LocalDate from, LocalDate to) {
        String momondo = "https://www.momondo.no/flight-search/" +
                DepartFromCityCode + "-" + DepartToCityCode + "/";

        momondo += from.format(momondoIsoLocalDate);
        momondo += "/" + ArriveFromCityCode + "-" + ArriveToCityCode;
        if(to != null ){
            momondo += "/" + to.format(momondoIsoLocalDate);
        }
        momondo += "?sort=price_a&fs=stops=~1&attempt=3";
        return momondo;
    }

    //    https://www.finn.no/reise/flybilletter/resultat?tripType=openjaw&requestedOrigin=BGO.AIRPORT&requestedDestination=OSL.METROPOLITAN_AREA&requestedDepartureDate=16.03.2018&requestedOrigin2=SVG.AIRPORT
    // &requestedDestination2=PVG.AIRPORT&requestedReturnDate=04.06.2018&numberOfAdults=1&cabinType=economy
    static String getFinnURLString(String DepartFromCityCode, String DepartToCityCode, String ArriveFromCityCode, String ArriveToCityCode, LocalDate from, LocalDate to) {
        String finn = "https://www.FINN.no/reise/flybilletter/resultat?"
                + "requestedOrigin="  + DepartFromCityCode + "&requestedDestination=" + DepartToCityCode;
        finn += "&requestedDepartureDate=" + from;
        if(to != null) {
            finn += "&tripType=openjaw&requestedReturnDate=" + to.format(sdfFin);
            finn += "&requestedOrigin2=" + ArriveFromCityCode + "&requestedDestination2=" + ArriveToCityCode;
        } else {
            finn += "&tripType=oneway&requestedReturnDate=";
        }
        finn += "&numberOfAdults=1&numberOfChildren=0&cabinType=economy&maximumNumberOfStops=1";
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




    public enum CITY {
        STAVANGER("SVG", "SVG.AIRPORT"),
        BERGEN("BGO", "BGO.AIRPORT"),
        OSLO("OSL", "OSL.METROPOLITAN_AREA"),

        SHANGHAI("PVG", "SHA.METROPOLITAN_AREA"),
        BEIJING("PEK", "BJS.METROPOLITAN_AREA"),
        XIAN("SIA", "XIY.AIRPORT"),
        GUANGZHOU("CAN", "CAN.AIRPORT"),
        NANJING("NKG", "NKG.AIRPORT");

        private String iataCode;
        private String finnCode;

        CITY(String code, String finnCode) {
            this.iataCode = code;
            this.finnCode = finnCode;
        }

        public String getIataCode() {
            return iataCode;
        }

        public String getFinnCode() {
            return finnCode;
        }

        public String getMomondoCode() {
            return iataCode;
        }
    }


}
