package travel.finnAndMomodo;

import org.apache.log4j.Logger;
import travel.domain.TicketInfo;
import travel.excel.ExcelExporter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Longcui on 29.07.2014.
 */
public class FromChinaTravel extends Travel{
    private static final Logger logger = Logger.getLogger(FromChinaTravel.class);


    /**
     *
     * @param args args[0] could be "short"
     *             args[1] could be "oneWay"
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        FromChinaTravel chinaTravel = new FromChinaTravel();
        chinaTravel.searchTickets(args);
    }

    private void searchTickets(String[] args) throws InterruptedException {
        populateParams(args);

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

            from.setTime(FromChinaTravel.start.getTime());
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

                                    String momondoURLString = getMomondoURLString(momondoFromChinaPlace.getCode(), momondoToNorwayPlace.getCode(), momondoFromNorwayPlace.getCode(), momondoToChinaPlace.getCode(), from, to);
                                    TicketInfo priceFromMomondo = new TicketInfo();
//                                    TicketInfo priceFromMomondo = TravelAgent.getPriceFromMomondo(
//                                            driver, momondoURLString);
//                                    logger.info("Momondo:" + momondoFromChinaPlace + "-" + momondoToNorwayPlace + "-" + momondoFromNorwayPlace + "-" + momondoToChinaPlace +  ": " + from.getTime() + " " + to.getTime() + ". price is: " + priceFromMomondo);
//                                    prepareWritingToExcel(momondoFromChinaPlace.name(), momondoToNorwayPlace.name(), momondoFromNorwayPlace.name(), momondoToChinaPlace.name(),
//                                            from.getTime(), to.getTime(), priceFromMomondo, momondoURLString);


                                    //                        double priceFromMomondo = 99999;
                                    String finnURLString = getFinnURLString(finnFromChinaPlace.getCode(), finnToNorwayPlace.getCode(), finnFromNorwayPlace.getCode(), finnToChinaPlace.getCode(), from, to);
                                    TicketInfo priceForFinn = TravelAgent.getPriceFromFinn(
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

            ExcelExporter excelExporter = new ExcelExporter(fromChinaCities, toNorwayCities, fromNorwayCities, toChinaCities,
                    fromDates, toDates,
                    prices, bestPriceUrls);
//            excelExporter.writeToExcel();
        }
    }




}
