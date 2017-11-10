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

    private static int MINIMAL_STAY_DAY = 80;
    private static int MAXIMAL_STAY_DAY = 88;


    private final static Calendar start = Calendar.getInstance();
    private final static Calendar end = Calendar.getInstance();

    private static SimpleDateFormat sdfMo = new SimpleDateFormat("dd-M-YYYY");     //14-5-2015
    private static SimpleDateFormat sdfFin = new SimpleDateFormat("dd.MM.YYYY");     //14.05.2015

    private static List<String> fromCities = new ArrayList<String>();
    private static List<String> fromDates = new ArrayList<String>();
    private static List<String> toDates = new ArrayList<String>();
    private static List<Double> bestPrices = new ArrayList<Double>();
    private static List<String> bestPriceUrls = new ArrayList<String>();

    private static WebDriver driver = SeleniumWebDriverFirefox.getDriver();

    public static void main(String[] args) throws Exception {
        //month: 0: jan
        start.set(2018, 2, 15);
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
                    for (MomondoCountry momondoCountry : MomondoCountry.values()) {
                        FinnPlace finnCountry = FinnPlace.valueOf(momondoCountry.name());
                        double priceFromMomondo = TravelAgent.getPriceFromMomondo(driver, getMomondoURLString(momondoCountry, from, to));
                        logger.info("Momondo:" + momondoCountry + ":  " + from.getTime() + to.getTime() + ". price is: " + priceFromMomondo);

//                        double priceFromMomondo = 99999;
                        double priceForFinn = TravelAgent.getPriceForFinn(driver, getFinnURLString(finnCountry, from, to));
//                        double priceForFinn = 555;
                        logger.info("Finn:" + finnCountry + ":  " + from.getTime() + to.getTime() + ". price is: " + priceForFinn);
                        if(priceForFinn < priceFromMomondo) {
                            bestPriceURL = getFinnURLString(finnCountry, from, to);
                            bestPrice = priceForFinn;
                        } else {
                            bestPriceURL = getMomondoURLString(momondoCountry, from, to);
                            bestPrice = priceFromMomondo;
                        }
//                        for (String recipient : DEBUG_EMAILS) {
//                            sendEmail(recipient, String.valueOf(bestPrice) + from.getTime() + to.getTime(), bestPriceURL);
//                        }
                        prepareWritingToExcel(momondoCountry.name(), from.getTime(), to.getTime(), bestPrice, bestPriceURL);
                    }
                    to.add(Calendar.DAY_OF_MONTH, 1);

                }
//                sleep(DEBUG_SLEEP_TIME);
                from.add(Calendar.DAY_OF_MONTH, 1);
            }

            ExcelExporter excelExporter = new ExcelExporter();
            excelExporter.setBestPrices(bestPrices);
            excelExporter.setBestPriceUrls(bestPriceUrls);
            excelExporter.setFromCities(fromCities);
            excelExporter.setFromDates(fromDates);
            excelExporter.setToDates(toDates);
            excelExporter.writeToExcel();
        }
    }


    private static void prepareWritingToExcel(String fromCity, Date from, Date to, double bestPrice, String bestPriceURL) {
       fromCities.add(fromCity);
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

    private static String getMomondoURLString(MomondoCountry country, Calendar from, Calendar to) {
        String momondo = "http://www.MOMONDO.com/flightsearch/?Search=true&TripType=2&SegNo=2&SO0=" + country.getCode() + "&SD0=SVG&SDP0=";
        momondo += sdfMo.format(from.getTime());
        momondo += "&SO1=SVG&SD1=" + country.code + "&SDP1=";
        momondo += sdfMo.format(to.getTime());
        momondo += "&AD=1&TK=ECO&DO=false&NA=false";
        return momondo;
    }

    private  static String getFinnURLString(FinnPlace country, Calendar from, Calendar to) {
        String finn = "http://www.FINN.no/reise/flybilletter/resultat?tripType=roundtrip&requestedOrigin=" + country.code + "&requestedDestination=SVG.AIRPORT&requestedOrigin2=&requestedDestination2=&requestedDepartureDate=";
        finn += sdfFin.format(from.getTime());
        finn += "&requestedReturnDate=";
        finn += sdfFin.format(to.getTime());
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

    public enum MomondoCountry {
//        SHANGHAI("SHA"),
        BEIJING("BJS");
//        XIAN("SIA"),
//        GUANGZHOU("CAN"),
//        NANJING("NKG");

        private String code;
        private MomondoCountry (String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public enum FinnPlace {
//        SHANGHAI("SHA.METROPOLITAN_AREA"),
        BEIJING("BJS.METROPOLITAN_AREA");
//        XIAN("XIY.AIRPORT"),
//        GUANGZHOU("CAN.AIRPORT"),
//        NANJING("NKG.AIRPORT");

        private String code;
        private FinnPlace(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
