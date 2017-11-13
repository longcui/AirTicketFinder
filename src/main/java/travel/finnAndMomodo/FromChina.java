package travel.finnAndMomodo;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import travel.browser.SeleniumWebDriverFirefox;
import travel.excel.ExcelExporter;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Longcui on 29.07.2014.
 */
public class FromChina {
    private static final Logger logger = Logger.getLogger(FromChina.class);

    private static final String MOMONDO = "http://www.MOMONDO.com/flightsearch/?Search=true&TripType=2&SegNo=2&SO0=SHA&SD0=SVG&SDP0=14-05-2015&SO1=SVG&SD1=SHA&SDP1=20-05-2015&AD=1&TK=ECO&DO=false&NA=false";
    private static final String skyscan = "http://www.skyscanner.no/transport/flyavganger/svg/csha/150414/150415/billettpriser-fra-stavanger-til-shanghai-i-april-2015.html?adults=1&children=0&infants=0&cabinclass=economy&preferdirects=false&outboundaltsenabled=false&inboundaltsenabled=false&rtn=1";
    private static final String FINN = "http://www.FINN.no/reise/flybilletter/resultat?tripType=roundtrip&requestedOrigin=SHA.METROPOLITAN_AREA&requestedDestination=SVG.AIRPORT&requestedOrigin2=&requestedDestination2=&requestedDepartureDate=22.04.2015&requestedReturnDate=22.04.2015&numberOfAdults=1&numberOfChildren=0&cabinType=economy";
    //    private static String[] EMAILS = new String[]{"dragonworld1988@gmail.com", "newrala@gmail.com"};
    private static String[] EMAILS = new String[]{"dragonworld1988@gmail.com"};
    private static String[] DEBUG_EMAILS = new String[]{"dragonworld1988@gmail.com"};
    private static double momondoPrice;
    private static double unspecificPrice;
    private static int SLEEP_TIME = 30 * 60 * 1000;  //30min
    private static int DEBUG_SLEEP_TIME = 5 * 1000;  //30sec

    private static int MINIMAL_STAY_DAY = 25;
//    private static int MINIMAL_STAY_DAY = 80;
    private static int MAXIMAL_STAY_DAY = 35;
//    private static int MAXIMAL_STAY_DAY = 87;


    private final static Calendar start = Calendar.getInstance();
    private final static Calendar end = Calendar.getInstance();

    private static SimpleDateFormat sdfMo = new SimpleDateFormat("dd-M-YYYY");     //14-5-2015
    private static SimpleDateFormat sdfFin = new SimpleDateFormat("dd.MM.YYYY");     //14.05.2015

    private static List<String> fromChinaCities = new ArrayList<String>();
    private static List<String> toNorwayCities = new ArrayList<String>();
    private static List<String> fromNorwayCities = new ArrayList<String>();
    private static List<String> toChinaCities = new ArrayList<String>();

    private static List<String> fromDates = new ArrayList<String>();
    private static List<String> toDates = new ArrayList<String>();

    private static List<Double> bestPrices = new ArrayList<Double>();
    private static List<String> bestPriceUrls = new ArrayList<String>();

    private static WebDriver driver = SeleniumWebDriverFirefox.getDriver();

    public static void main(String[] args) throws Exception {
        //month: 0: jan
        start.set(2018, 2, 30);
//        end.set(2018, 4, 7);
        end.set(2018, 6, 5);

        while (true) {
            Calendar lastPossibleLeaveDay = Calendar.getInstance();
            lastPossibleLeaveDay.setTime(end.getTime());
            lastPossibleLeaveDay.add(Calendar.DAY_OF_MONTH, -MINIMAL_STAY_DAY);

            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.setTime(start.getTime());
            while (from.before(lastPossibleLeaveDay)) {
                String bestPriceURL;
                double bestPrice;

                to.setTime(from.getTime());
                to.add(Calendar.DAY_OF_MONTH, MINIMAL_STAY_DAY);
                Calendar toMax = Calendar.getInstance();
                toMax.setTime(from.getTime());
                toMax.add(Calendar.DAY_OF_MONTH, MAXIMAL_STAY_DAY);


                while (to.before(toMax) && to.before(end)) {
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
                                    double priceFromMomondo = TravelAgent.getPriceFromMomondo(
                                            driver, momondoURLString);
                                    logger.info("Momondo:" + momondoFromChinaPlace + "-" + momondoToNorwayPlace + "-" + momondoFromNorwayPlace + "-" + momondoToChinaPlace +  ": " + from.getTime() + " " + to.getTime() + ". price is: " + priceFromMomondo);
                                    prepareWritingToExcel(momondoFromChinaPlace.name(), momondoToNorwayPlace.name(), momondoFromNorwayPlace.name(), momondoToChinaPlace.name(),
                                            from.getTime(), to.getTime(), priceFromMomondo, momondoURLString);


                                    //                        double priceFromMomondo = 99999;
                                    String finnURLString = getFinnURLString(finnFromChinaPlace, finnToNorwayPlace, finnFromNorwayPlace, finnToChinaPlace, from, to);
                                    double priceForFinn = TravelAgent.getPriceForFinn(
                                            driver, finnURLString);
                                    //                        double priceForFinn = 555;
                                    logger.info("Finn   :" + finnFromChinaPlace + "-" + finnToNorwayPlace + "-" + finnFromNorwayPlace + "-" + finnToChinaPlace +  ": " + from.getTime() + " " + to.getTime() + ". price is: " + priceForFinn);
                                    prepareWritingToExcel(momondoFromChinaPlace.name(), momondoToNorwayPlace.name(), momondoFromNorwayPlace.name(), momondoToChinaPlace.name(),
                                            from.getTime(), to.getTime(), priceForFinn, finnURLString);
                                    //                        for (String recipient : DEBUG_EMAILS) {
                                    //                            sendEmail(recipient, String.valueOf(bestPrice) + from.getTime() + to.getTime(), bestPriceURL);
                                    //                        }
                                }
                            }
                        }

                    }
                    to.add(Calendar.DAY_OF_MONTH, 1);

                }
//                sleep(DEBUG_SLEEP_TIME);
                from.add(Calendar.DAY_OF_MONTH, 1);
            }

            ExcelExporter excelExporter = new ExcelExporter();
            excelExporter.setFromChinaCities(fromChinaCities);
            excelExporter.setToNorwayCities(toNorwayCities);
            excelExporter.setFromNorwayCities(fromNorwayCities);
            excelExporter.setToChinaCities(toChinaCities);
            excelExporter.setBestPrices(bestPrices);
            excelExporter.setBestPriceUrls(bestPriceUrls);
            excelExporter.setFromDates(fromDates);
            excelExporter.setToDates(toDates);
            excelExporter.writeToExcel();
        }
    }


    private static void prepareWritingToExcel(String fromChinaCity, String toNorwayCity, String fromNorwayCity, String toChinaCity, Date from, Date to, double bestPrice, String bestPriceURL) {
        fromChinaCities.add(fromChinaCity);
        toNorwayCities.add(toNorwayCity);
        fromNorwayCities.add(fromNorwayCity);
        toChinaCities.add(toChinaCity);
        fromDates.add(sdfFin.format(from));
        toDates.add(sdfFin.format(to));
        bestPrices.add(bestPrice);
        bestPriceUrls.add(bestPriceURL);
    }


    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private static String getMomondoURLString(MomondoChinaPlace momondoFromChinaPlace, MomondoNorwayPlace momondoToNorwayPlace, MomondoNorwayPlace momondoFromNorwayPlace, MomondoChinaPlace momondoToChinaPlace, Calendar from, Calendar to) {
        String momondo = "http://www.MOMONDO.com/flightsearch/?Search=true&TripType=2&SegNo=2&SO0=" +
                momondoFromChinaPlace.getCode() + "&SD0=" + momondoToNorwayPlace.getCode() + "&SDP0=";
        momondo += sdfMo.format(from.getTime());
        momondo += "&SO1=" + momondoFromNorwayPlace.code + "&SD1=" + momondoToChinaPlace.code + "&SDP1=";
        momondo += sdfMo.format(to.getTime());
        momondo += "&AD=1&TK=ECO&DO=false&NA=false";
        return momondo;
    }

//    https://www.finn.no/reise/flybilletter/resultat?tripType=openjaw&requestedOrigin=BGO.AIRPORT&requestedDestination=OSL.METROPOLITAN_AREA&requestedDepartureDate=16.03.2018&requestedOrigin2=SVG.AIRPORT
    // &requestedDestination2=PVG.AIRPORT&requestedReturnDate=04.06.2018&numberOfAdults=1&cabinType=economy

    private  static String getFinnURLString(FinnChinaPlace finnFromChinaPlace, FinnNorwayPlace finnToNorwayPlace, FinnNorwayPlace finnFromNorwayPlace, FinnChinaPlace finnToChinaPlace, Calendar from, Calendar to) {
        String finn = "http://www.FINN.no/reise/flybilletter/resultat?tripType=openjaw"
                + "&requestedOrigin="  + finnFromChinaPlace.code + "&requestedDestination=" + finnToNorwayPlace.code
                + "&requestedOrigin2=" + finnFromNorwayPlace.code + "&requestedDestination2=" + finnToChinaPlace.code;
        finn += "&requestedDepartureDate=" + sdfFin.format(from.getTime());
        finn += "&requestedReturnDate="  + sdfFin.format(to.getTime());
        finn += "&numberOfAdults=1&numberOfChildren=0&cabinType=economy";
        return  finn;
    }


    private static void handleNotifications(double price, double thresholdPrice, String subject, String url) {
        if (price < thresholdPrice) {
            String content = "";
            subject += price;

            String[] es = EMAILS;
            if (logger.isDebugEnabled()) {
                es = DEBUG_EMAILS;
            }

            for (String email : es) {
                sendEmail(email, subject, url);
            }
        }
    }

    private static void sendEmail(String recipient, String subject, String url) {
        final String username = "longcuidev@gmail.com";
        final String password = "clclzpzp";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("longcuidev@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(url);

            Transport.send(message);

            System.out.println("email sent");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
// 	Properties props = new Properties();
//		props.put("mail.smtp.host", "smtp.gmail.com");
//		props.put("mail.smtp.socketFactory.port", "465");
//		props.put("mail.smtp.socketFactory.class",
//				"javax.net.ssl.SSLSocketFactory");
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.port", "465");
//
//		Session session = Session.getDefaultInstance(props,
//			new javax.mail.Authenticator() {
//				protected PasswordAuthentication getPasswordAuthentication() {
//					return new PasswordAuthentication(username, password);
//				}
//			});
//
//		try {
//
//			Message message = new MimeMessage(session);
//			message.setFrom(new InternetAddress("longcuino@gmail.com"));
//			message.setRecipients(Message.RecipientType.TO,
//					InternetAddress.parse("longcuino@gmail.com"));
//			message.setSubject("Testing Subject");
//			message.setText("Dear Mail Crawler," +
//					"\n\n No spam to my email, please!");
//
//			Transport.send(message);
//
//			System.out.println("Done");
//
//		} catch (MessagingException e) {
//			throw new RuntimeException(e);
//		}
    }

    public enum MomondoChinaPlace {
        SHANGHAI("SHA");
//        BEIJING("BJS"),
//        XIAN("SIA"),
//        GUANGZHOU("CAN"),
//        NANJING("NKG");

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
//        XIAN("XIY.AIRPORT"),
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
