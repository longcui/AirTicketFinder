package travelBetweenDates;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Longcui on 29.07.2014.
 */
public abstract class TravelCalculator {
    private static final Logger logger = Logger.getLogger(FromChina.class);

    private static WebDriver driver = new FirefoxDriver();

    private static final String FROM_CITY = "SVG";
    //    private static String[] EMAILS = new String[]{"dragonworld1988@gmail.com", "newrala@gmail.com"};
    private static String[] EMAILS = new String[]{"dragonworld1988@gmail.com"};
    private static String[] DEBUG_EMAILS = new String[]{"dragonworld1988@gmail.com"};
    private static double momondoPrice;
    private static double unspecificPrice;
    private static int SLEEP_TIME = 30 * 60 * 1000;  //30min
    private static int DEBUG_SLEEP_TIME = 30 * 1000;  //30sec

    private static int MINIMAL_STAY_DAY = 7;
    private static int MAXIMAL_STAY_DAY = 30;


    private final static Calendar start = Calendar.getInstance();
    private final static Calendar end = Calendar.getInstance();

    private final static boolean hasSpringFestival = false;
    private final static Calendar springFestivalStart = Calendar.getInstance();
    private final static Calendar springFestivalEnd= Calendar.getInstance();

    private static SimpleDateFormat sdfMo = new SimpleDateFormat("dd-M-YYYY");     //14-5-2015
    private static SimpleDateFormat sdfFin = new SimpleDateFormat("dd.MM.YYYY");     //14.05.2015

    private static List<String> fromCities = new ArrayList<String>();
    private static List<String> froms = new ArrayList<String>();
    private static List<String> tos = new ArrayList<String>();
    private static List<Double> bestPrices = new ArrayList<Double>();
    private static List<String> bestPriceUrls = new ArrayList<String>();

    private static boolean enableEmailNotification = true;
    private static boolean enableSavingToExcel = true;

    public static void main(String[] args) throws Exception {
//        start.set(2016, 0, 16);     //
//        end.set(2016, 2, 5);        //
        springFestivalStart.set(2016, 1, 8);
        springFestivalEnd.set(2016, 1, 15);

//        start.set(2016, 8, 1);     //
//        end.set(2016, 11, 31);        //

        start.set(2017, 1, 1);     //
        end.set(2017, 11, 31);        //

        while (true) {
            Calendar lastPossibleDayToGo = Calendar.getInstance();
            lastPossibleDayToGo.setTime(end.getTime());
            lastPossibleDayToGo.add(Calendar.DAY_OF_MONTH, -MINIMAL_STAY_DAY);

            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.setTime(start.getTime());
            while (from.before(lastPossibleDayToGo)) {
                String bestPriceURL;
                double bestPrice;

                to.setTime(from.getTime());
                to.add(Calendar.DAY_OF_MONTH, MINIMAL_STAY_DAY);
                Calendar toMax = Calendar.getInstance();
                toMax.setTime(from.getTime());
                toMax.add(Calendar.DAY_OF_MONTH, MAXIMAL_STAY_DAY);

                boolean springFestivalCondition = (hasSpringFestival && from.before(springFestivalStart) && to.after(springFestivalEnd)) || !hasSpringFestival;
                while (to.before(toMax) && to.before(end) && springFestivalCondition) {
//                    System.out.println(from.getTime() + "-------" + to.getTime());
                    for (MomondoCountry momondoCountry : MomondoCountry.values()) {
                        String momondoURLString = getMomondoURLString(momondoCountry, from, to);
                        double priceFromMomondo = TravelAgent.getPriceFromMomondo(driver, momondoURLString);
                        logger.info("Momondo:" + momondoCountry + ":  " + from.getTime() + to.getTime() + ". price is: " + priceFromMomondo + "  " + momondoURLString);

                        FinnCountry finnCountry = FinnCountry.valueOf(momondoCountry.name());
                        String finnURLString = getFinnURLString(finnCountry, from, to);
                        double priceForFinn = TravelAgent.getPriceForFinn(driver, finnURLString);
                        logger.info("Finn:" + finnCountry + ":  " + from.getTime() + to.getTime() + ". price is: " + priceForFinn + "   " + finnURLString);

                        if(priceForFinn < priceFromMomondo) {
                            bestPriceURL = finnURLString;
                            bestPrice = priceForFinn;
                        } else {
                            bestPriceURL = momondoURLString;
                            bestPrice = priceFromMomondo;
                        }
//                        for (String recipient : DEBUG_EMAILS) {
//                            sendEmail(recipient, String.valueOf(bestPrice) + from.getTime() + to.getTime(), bestPriceURL);
//                        }
                        if(enableSavingToExcel) {
                            prepareWritingToExcel(momondoCountry.name(), from.getTime(), to.getTime(), bestPrice, bestPriceURL);
                        }

                        if(enableEmailNotification) {
                            handleNotifications(bestPrice, momondoCountry.getTravelType(), momondoCountry.name() + ": " + String.valueOf(bestPrice) + "NOK  from: " + new SimpleDateFormat("dd.MM.yyyy").format(from.getTime()) + "  To: " +  new SimpleDateFormat("dd.MM.yyyy").format(to.getTime()), bestPriceURL);
                        }
                    }
                    to.add(Calendar.DAY_OF_MONTH, 1);
                    sleep(DEBUG_SLEEP_TIME);
                }
                if(enableSavingToExcel && fromCities.size() > 0) {
                    writeToExcel();
                }
                from.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
    }

    private static void writeToExcel() {
        try {
            FileOutputStream fileOut = new FileOutputStream("price_" + new SimpleDateFormat("dd-M-yyyy hh").format(new Date()) + ".xls");
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet worksheet = workbook.createSheet("POI Worksheet");

            // index from 0,0... cell A1 is cell(0,0)
            for(int i =0; i < fromCities.size(); i ++) {
                HSSFRow row1 = worksheet.createRow((short) i);

                HSSFCell cellA1 = row1.createCell((short) 0);
                cellA1.setCellValue(fromCities.get(i));
                HSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFillForegroundColor(HSSFColor.GOLD.index);
                cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cellA1.setCellStyle(cellStyle);

                HSSFCell cellB1 = row1.createCell((short) 1);
                cellB1.setCellValue(froms.get(i));

                HSSFCell cellC1 = row1.createCell((short) 2);
                cellC1.setCellValue(tos.get(i));

                HSSFCell cellD1 = row1.createCell((short) 3);
                cellD1.setCellValue(bestPrices.get(i));

                HSSFCell cellE1 = row1.createCell((short) 4);
                cellE1.setCellValue(bestPriceUrls.get(i));

                HSSFCellStyle cellStyle1 = workbook.createCellStyle();
                HSSFFont font = workbook.createFont();
                font.setUnderline(HSSFFont.U_SINGLE);
                font.setColor(HSSFColor.BLUE.index);
                cellStyle1.setFont(font);
                cellE1.setCellStyle(cellStyle1);

                HSSFHyperlink link = (HSSFHyperlink)workbook.getCreationHelper()
                        .createHyperlink(Hyperlink.LINK_URL);
                link.setAddress(bestPriceUrls.get(i));
                cellE1.setHyperlink((HSSFHyperlink) link);
            }

            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fromCities.clear();
        froms.clear();
        tos.clear();
        bestPrices.clear();
        bestPriceUrls.clear();
        logger.info("file saved.");
    }

    private static void prepareWritingToExcel(String fromCity, Date from, Date to, double bestPrice, String bestPriceURL) {
        fromCities.add(fromCity);
        froms.add(sdfFin.format(from));
        tos.add(sdfFin.format(to));
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
        String momondo = "http://www.MOMONDO.com/flightsearch/?Search=true&TripType=2&SegNo=2&SO0=" + FROM_CITY + "&SD0=" + country.getCode() + "&SDP0=";
        momondo += sdfMo.format(from.getTime());
        momondo += "&SO1=" + country.code + "&SD1=" + FROM_CITY + "&SDP1=";
        momondo += sdfMo.format(to.getTime());
        momondo += "&AD=1&TK=ECO&DO=false&NA=false";
        return momondo;
    }

    private  static String getFinnURLString(FinnCountry country, Calendar from, Calendar to) {
        String finn = "http://www.FINN.no/reise/flybilletter/resultat?tripType=roundtrip&requestedOrigin=" + country.code + "&requestedDestination=" + FROM_CITY + ".AIRPORT&requestedOrigin2=&requestedDestination2=&requestedDepartureDate=";
        finn += sdfFin.format(from.getTime());
        finn += "&requestedReturnDate=";
        finn += sdfFin.format(to.getTime());
        finn += "&numberOfAdults=1&numberOfChildren=0&cabinType=economy";
        return  finn;
    }

    private static void handleNotifications(double price, int travelType, String subject, String url) {
        double thresholdPrice = 0;
        switch (travelType) {
            case 0: {
                thresholdPrice = 3000;
                break;
            }
            case 1: {
                thresholdPrice = 2500;
                break;
            }
            case 2: {
                thresholdPrice = 2000;
                break;
            }
            case 3: {
                thresholdPrice = 2500;
                break;
            }
            default: {
                thresholdPrice = 2500;
                break;
            }
        }
        handleNotifications(price, thresholdPrice, subject, url);

    }

    private static void handleNotifications(double price, double thresholdPrice, String subject, String url) {
        if (price < thresholdPrice) {
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
        NANJING("NKG", 0),

        SHANGHAI("SHA", 1),
        BEIJING("BJS", 1),
        HANGZHOU("HGH", 1),
        WUHAN("WUH", 1),

        GUANGZHOU("CAN", 2),
        CHENGDU("CTU", 2),
        SHENYANG("SHE", 2),
        SHENZHEN("SZX", 2),
        XIAMEN("XMN", 2),      //KLM

        TAIPEI("TPE", 3),
        HONGKONG("HKG", 3);

        private String code;
        private int travelType;
        private MomondoCountry (String code, int travelType) {
            this.code = code;
            this.travelType = travelType;
        }

        public String getCode() {
            return code;
        }

        public int getTravelType() {
            return travelType;
        }
    }

    public enum FinnCountry {
        NANJING("NKG.AIRPORT", 0),

        SHANGHAI("SHA.METROPOLITAN_AREA", 1),
        BEIJING("BJS.METROPOLITAN_AREA", 1),
        HANGZHOU("HGH.AIRPORT", 1),
        WUHAN("WUH.AIRPORT", 1),


        XIAN("XIY.AIRPORT", 2),
        CHENGDU("CTU.AIRPORT", 2),
        SHENYANG("SHE.AIRPORT", 2),
        SHENZHEN("SZX.AIRPORT", 2),
        GUANGZHOU("CAN.AIRPORT", 2),
        XIAMEN("XMN.AIRPORT", 2),      //KLM

        TAIPEI("TPE.METROPOLITAN_AREA", 3),
        HONGKONG("HKG.AIRPORT", 3);

        ;
//        Paris("PAR"),
//        BERLIN("BER"),
//        WARSAW("WAW"),
//        Szczecin("SZZ"),
//        ALICANTE("ALC"),
//        Gdansk("GND"),
//        Hamburg("HAM"),
//        Munich("MUC");


        private String code;
        private int travelType;
        private FinnCountry(String code, int travelType ) {
            this.code = code;
            this.travelType = travelType;
        }

        public String getCode() {
            return code;
        }

        public int getTravelType() {
            return travelType;
        }
    }

}
