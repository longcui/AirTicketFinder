package travel.finnAndMomodo;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private static int MINIMAL_STAY_DAY = 28;
    private static int MAXIMAL_STAY_DAY = 35;


    private final static Calendar start = Calendar.getInstance();
    private final static Calendar end = Calendar.getInstance();

    private static SimpleDateFormat sdfMo = new SimpleDateFormat("dd-M-YYYY");     //14-5-2015
    private static SimpleDateFormat sdfFin = new SimpleDateFormat("dd.MM.YYYY");     //14.05.2015

    private static List<String> fromCities = new ArrayList<String>();
    private static List<String> froms = new ArrayList<String>();
    private static List<String> tos = new ArrayList<String>();
    private static List<Double> bestPrices = new ArrayList<Double>();
    private static List<String> bestPriceUrls = new ArrayList<String>();

    private static WebDriver driver = new FirefoxDriver();

    public static void main(String[] args) throws Exception {
        //month: 0: jan
        start.set(2016, 7, 12);
        end.set(2015, 8, 17);

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
//                        double priceFromMomondo = getPriceFromMomondo(momondoCountry, from, to);
                        double priceFromMomondo = 99999;
                        double priceForFinn = TravelAgent.getPriceForFinn(driver, getFinnURLString(finnCountry, from, to));
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
            writeToExcel();
        }
    }

    private static void writeToExcel() {
        try {
            File file = new File("price.xls" + new Date());
            if(!file.exists()) {
                file.createNewFile();
                logger.info("file created.");
            }
            FileOutputStream fileOut = new FileOutputStream(file);
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
