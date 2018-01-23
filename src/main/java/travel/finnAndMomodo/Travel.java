package travel.finnAndMomodo;

import travel.domain.TicketInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


}
