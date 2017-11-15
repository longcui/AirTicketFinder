//package travel.finnAndMomodo;
//
//import org.apache.log4j.Logger;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.firefox.FirefoxProfile;
//import org.openqa.selenium.firefox.internal.ProfilesIni;
//import travel.Credential;
//import travel.domain.TicketInfo;
//
//import javax.mail.*;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///**
// * Created by Longcui on 29.07.2014.
// */
//public abstract class TravelCalculator extends Travel{
//    private static final Logger logger = Logger.getLogger(TravelCalculator.class);
//
//    private static ProfilesIni listProfiles = new ProfilesIni();
//    private static  FirefoxProfile profile = listProfiles.getProfile("default");
//    private static WebDriver driver = new FirefoxDriver();
//
////    private static WebDriver driver = new FirefoxDriver();
////    private static WebDriver driver = new ChromeDriver();
//
//    private static final String FROM_CITY = "SVG";
//
//    private static double momondoPrice;
//    private static double unspecificPrice;
//    private static int SLEEP_TIME = 30 * 60 * 1000;  //30min
//    private static int DEBUG_SLEEP_TIME = 30 * 1000;  //30sec
//
//    private static int MINIMAL_STAY_DAY = 75;
//    private static int MAXIMAL_STAY_DAY = 87;
//
//
//    private final static Calendar start = Calendar.getInstance();
//    private final static Calendar end = Calendar.getInstance();
//
//    private final static boolean hasSpringFestival = false;
//    private final static Calendar springFestivalStart = Calendar.getInstance();
//    private final static Calendar springFestivalEnd= Calendar.getInstance();
//
//
//    private static boolean enableEmailNotification = true;
//    private static boolean enableSavingToExcel = false;
//
//    public static void main(String[] args) throws Exception {
////        start.set(2016, 0, 16);     // Jan!
//        springFestivalStart.set(2016, 7, 12);
//        springFestivalEnd.set(2016, 9, 15);
//
//        start.set(2018, 2, 17);     //
//        end.set(2018, 5, 30);        //
//
//        while (true) {
//            Calendar lastPossibleDayToGo = Calendar.getInstance();
//            lastPossibleDayToGo.setTime(end.getTime());
//            lastPossibleDayToGo.add(Calendar.DAY_OF_MONTH, -MINIMAL_STAY_DAY);
//
//            Calendar from = Calendar.getInstance();
//            Calendar to = Calendar.getInstance();
//            from.setTime(start.getTime());
//            while (from.before(lastPossibleDayToGo)) {
//                String bestPriceURL;
//                double bestPrice;
//
//                to.setTime(from.getTime());
//                to.add(Calendar.DAY_OF_MONTH, MINIMAL_STAY_DAY);
//                Calendar toMax = Calendar.getInstance();
//                toMax.setTime(from.getTime());
//                toMax.add(Calendar.DAY_OF_MONTH, MAXIMAL_STAY_DAY);
//
//                boolean springFestivalCondition = (hasSpringFestival && from.before(springFestivalStart) && to.after(springFestivalEnd)) || !hasSpringFestival;
//                while (to.before(toMax) && to.before(end) && springFestivalCondition) {
////                    System.out.println(from.getTime() + "-------" + to.getTime());
//                    for (MomondoCountry momondoCountry : MomondoCountry.values()) {
//                        String momondoURLString = getMomondoURLString(momondoCountry, from, to);
//                        TicketInfo priceFromMomondo = TravelAgent.getPriceFromMomondo(driver, momondoURLString);
//                        logger.info("Momondo:" + momondoCountry + ":  " + from.getTime() + to.getTime() + ". price is: " + priceFromMomondo + "  " + momondoURLString);
//
//                        FinnCountry finnCountry = FinnCountry.valueOf(momondoCountry.name());
//                        String finnURLString = getFinnURLString(finnCountry, from, to);
//                        TicketInfo priceForFinn = TravelAgent.getPriceForFinn(driver, finnURLString);
//                        logger.info("Finn:" + finnCountry + ":  " + from.getTime() + to.getTime() + ". price is: " + priceForFinn + "   " + finnURLString);
//
////                        for (String recipient : DEBUG_EMAILS) {
////                            sendEmail(recipient, String.valueOf(bestPrice) + from.getTime() + to.getTime(), bestPriceURL);
////                        }
//                        if(enableSavingToExcel) {
//                            prepareWritingToExcel(momondoCountry.name(), from.getTime(), to.getTime(), priceFromMomondo, bestPriceURL);
//                            prepareWritingToExcel(momondoCountry.name(), from.getTime(), to.getTime(), priceForFinn, bestPriceURL);
//                        }
//
//                        if(enableEmailNotification) {
//                            handleNotifications(bestPrice, momondoCountry.getTravelType(), momondoCountry.name() + ": " + String.valueOf(bestPrice) + "NOK  from: " + new SimpleDateFormat("dd.MM.yyyy").format(from.getTime()) + "  To: " +  new SimpleDateFormat("dd.MM.yyyy").format(to.getTime()), bestPriceURL);
//                        }
//                    }
//                    to.add(Calendar.DAY_OF_MONTH, 1);
////                    sleep(DEBUG_SLEEP_TIME);
//                    sleep(SLEEP_TIME);
//                }
//                if(enableSavingToExcel && fromCities.size() > 0) {
////                    writeToExcel(from);
//                }
//                from.add(Calendar.DAY_OF_MONTH, 1);
//            }
//        }
//    }
//
//
//
//    private static void sleep(long milliseconds) {
//        try {
//            Thread.sleep(milliseconds);
//        } catch (InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
//    }
//
//    private static String getMomondoURLString(MomondoCountry country, Calendar from, Calendar to) {
//        String momondo = "http://www.MOMONDO.com/flightsearch/?Search=true&TripType=2&SegNo=2&SO0=" + FROM_CITY + "&SD0=" + country.getCode() + "&SDP0=";
//        momondo += sdfMo.format(from.getTime());
//        momondo += "&SO1=" + country.code + "&SD1=" + FROM_CITY + "&SDP1=";
//        momondo += sdfMo.format(to.getTime());
//        momondo += "&AD=1&TK=ECO&DO=false&NA=false";
//        return momondo;
//    }
//
//    private  static String getFinnURLString(FinnCountry country, Calendar from, Calendar to) {
//        String finn = "http://www.FINN.no/reise/flybilletter/resultat?tripType=roundtrip&requestedOrigin=" + country.code + "&requestedDestination=" + FROM_CITY + ".AIRPORT&requestedOrigin2=&requestedDestination2=&requestedDepartureDate=";
//        finn += sdfFin.format(from.getTime());
//        finn += "&requestedReturnDate=";
//        finn += sdfFin.format(to.getTime());
//        finn += "&numberOfAdults=1&numberOfChildren=0&cabinType=economy";
//        return  finn;
//    }
//
//    private static void handleNotifications(double price, int travelType, String subject, String url) {
//        double thresholdPrice = 0;
//        switch (travelType) {
//            case 0: {
//                thresholdPrice = 3500;
//                break;
//            }
//            case 1: {
//                thresholdPrice = 3500;
//                break;
//            }
//            case 2: {
//                thresholdPrice = 2000;
//                break;
//            }
//            case 3: {
//                thresholdPrice = 2500;
//                break;
//            }
//            default: {
//                thresholdPrice = 2500;
//                break;
//            }
//        }
//        handleNotifications(price, thresholdPrice, subject, url);
//
//    }
//
//    private static void handleNotifications(double price, double thresholdPrice, String subject, String url) {
//        if (price < thresholdPrice) {
//            String[] es = Credential.EMAILS;
//            if (logger.isDebugEnabled()) {
//                es = Credential.DEBUG_EMAILS;
//            }
//
//            for (String email : es) {
//                sendEmail(email, subject, url);
//            }
//        }
//    }
//
//    private static void sendEmail(String recipient, String subject, String url) {
//
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587");
//
//        Session session = Session.getInstance(props,
//                new javax.mail.Authenticator() {
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication(Credential.DEV_EMAIL, Credential.DEV_EMAIL_PASSWORD);
//                    }
//                });
//
//        try {
//
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(Credential.DEV_EMAIL));
//            message.setRecipients(Message.RecipientType.TO,
//                    InternetAddress.parse(recipient));
//            message.setSubject(subject);
//            message.setText(url);
//
//            Transport.send(message);
//
//            System.out.println("email sent");
//
//        } catch (MessagingException e) {
//            logger.error(e);
//        }
//// 	Properties props = new Properties();
////		props.put("mail.smtp.host", "smtp.gmail.com");
////		props.put("mail.smtp.socketFactory.port", "465");
////		props.put("mail.smtp.socketFactory.class",
////				"javax.net.ssl.SSLSocketFactory");
////		props.put("mail.smtp.auth", "true");
////		props.put("mail.smtp.port", "465");
////
////		Session session = Session.getDefaultInstance(props,
////			new javax.mail.Authenticator() {
////				protected PasswordAuthentication getPasswordAuthentication() {
////					return new PasswordAuthentication(username, password);
////				}
////			});
////
////		try {
////
////			Message message = new MimeMessage(session);
////			message.setFrom(new InternetAddress("longcuino@gmail.com"));
////			message.setRecipients(Message.RecipientType.TO,
////					InternetAddress.parse("longcuino@gmail.com"));
////			message.setSubject("Testing Subject");
////			message.setText("Dear Mail Crawler," +
////					"\n\n No spam to my email, please!");
////
////			Transport.send(message);
////
////			System.out.println("Done");
////
////		} catch (MessagingException e) {
////			throw new RuntimeException(e);
////		}
//    }
//
//    public enum MomondoCountry {
//        NANJING("NKG", 0),
//
//        SHANGHAI("SHA", 1),
//        BEIJING("BJS", 1);
////        HANGZHOU("HGH", 1);
////        WUHAN("WUH", 1);
//
////        GUANGZHOU("CAN", 2),
////        CHENGDU("CTU", 2),
////        SHENYANG("SHE", 2),
////        SHENZHEN("SZX", 2),
////        XIAMEN("XMN", 2),      //KLM
////
////        TAIPEI("TPE", 3),
////        HONGKONG("HKG", 3);
//
//        private String code;
//        private int travelType;
//        private MomondoCountry (String code, int travelType) {
//            this.code = code;
//            this.travelType = travelType;
//        }
//
//        public String getCode() {
//            return code;
//        }
//
//        public int getTravelType() {
//            return travelType;
//        }
//    }
//
//    public enum FinnCountry {
//        NANJING("NKG.AIRPORT", 0),
//
//        SHANGHAI("SHA.METROPOLITAN_AREA", 1),
//        BEIJING("BJS.METROPOLITAN_AREA", 1);
////        HANGZHOU("HGH.AIRPORT", 1);
////        WUHAN("WUH.AIRPORT", 1);
//
//
////        XIAN("XIY.AIRPORT", 2),
////        CHENGDU("CTU.AIRPORT", 2),
////        SHENYANG("SHE.AIRPORT", 2),
////        SHENZHEN("SZX.AIRPORT", 2),
////        GUANGZHOU("CAN.AIRPORT", 2),
////        XIAMEN("XMN.AIRPORT", 2),      //KLM
////
////        TAIPEI("TPE.METROPOLITAN_AREA", 3),
////        HONGKONG("HKG.AIRPORT", 3);
//
//        ;
////        Paris("PAR"),
////        BERLIN("BER"),
////        WARSAW("WAW"),
////        Szczecin("SZZ"),
////        ALICANTE("ALC"),
////        Gdansk("GND"),
////        Hamburg("HAM"),
////        Munich("MUC");
//
//
//        private String code;
//        private int travelType;
//        private FinnCountry(String code, int travelType ) {
//            this.code = code;
//            this.travelType = travelType;
//        }
//
//        public String getCode() {
//            return code;
//        }
//
//        public int getTravelType() {
//            return travelType;
//        }
//    }
//
//}
