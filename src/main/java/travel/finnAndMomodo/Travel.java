package travel.finnAndMomodo;

import travel.Credential;
import travel.domain.TicketInfo;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Travel {

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



    protected static void prepareWritingToExcel(String fromChinaCity, String toNorwayCity, String fromNorwayCity, String toChinaCity, Date from, Date to, TicketInfo price, String bestPriceURL) {
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

    protected static void sendEmail(String recipient, String subject, String url) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Credential.DEV_EMAIL, Credential.DEV_EMAIL_PASSWORD);
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
}
