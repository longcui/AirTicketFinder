package travel.finnAndMomodo;

import org.apache.log4j.Logger;
import travel.domain.TicketInfo;
import travel.excel.ExcelExporter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

class FromStavangerToChina extends Travel {
    private static final Logger logger = Logger.getLogger(FromChinaTravel.class);

    /**
     * @param args args[0] could be "short"
     *             args[1] could be "oneWay"
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        FromStavangerToChina fromStavangerToChina = new FromStavangerToChina();
        fromStavangerToChina.searchTickets(args);
    }

    private void searchTickets(String[] args) throws InterruptedException {
        populateParams(args);

        while (true) {
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            if (args.length > 1 && args[1].equals("oneWay")) {
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
                if (to != null) {
                    to.setTime(from.getTime());
                    to.add(Calendar.DAY_OF_MONTH, MINIMAL_STAY_DAY);
                    toMax.setTime(from.getTime());
                    toMax.add(Calendar.DAY_OF_MONTH, MAXIMAL_STAY_DAY);
                }


                do {
//                    System.out.println(from.getTime() + "-------" + to.getTime());
                    for (MomondoNorwayPlace momondoFromNorwayPlace : MomondoNorwayPlace.values()) {
                        FinnNorwayPlace finnFromNorwayPlace = FinnNorwayPlace.valueOf(momondoFromNorwayPlace.name());
                        for (MomondoChinaPlace momondoToChinaPlace : MomondoChinaPlace.values()) {
                            FinnChinaPlace finnToChinaPlace = FinnChinaPlace.valueOf(momondoToChinaPlace.name());
                            for (MomondoChinaPlace momondoFromChinaPlace : MomondoChinaPlace.values()) {
                                FinnChinaPlace finnFromChinaPlace = FinnChinaPlace.valueOf(momondoFromChinaPlace.name());
                                for (MomondoNorwayPlace momondoToNorwayPlace : MomondoNorwayPlace.values()) {
                                    FinnNorwayPlace finnToNorwayPlace = FinnNorwayPlace.valueOf(momondoToNorwayPlace.name());
                                    String momondoURLString = getMomondoURLString(momondoFromNorwayPlace.getCode(), momondoToChinaPlace.getCode(), momondoFromChinaPlace.getCode(), momondoToNorwayPlace.getCode(), from, to);
                                    TicketInfo priceFromMomondo = new TicketInfo();
                                    String finnURLString = getFinnURLString(finnFromNorwayPlace.getCode(), finnToChinaPlace.getCode(), finnFromChinaPlace.getCode(), finnToNorwayPlace.getCode(), from, to);
                                    try {
                                        TicketInfo priceForFinn = TravelAgent.getPriceForFinn(driver, finnURLString);
                                        logger.info("Finn   :" + finnFromNorwayPlace + "-" + finnToChinaPlace + "-" + finnFromChinaPlace + "-" + finnToNorwayPlace + ": " + from.getTime() + " " + (to == null ? null : to.getTime()) + ". price is: " + priceForFinn);
                                        prepareWritingToExcel(momondoFromNorwayPlace.name(), momondoToChinaPlace.name(), momondoFromChinaPlace.name(), momondoToNorwayPlace.name(),
                                                from.getTime(), (to == null ? null : to.getTime()), priceForFinn, finnURLString);

                                        if (enableEmailNotification) {
                                            double priceThreshold = 3900;
                                            if ((to != null && (priceForFinn.getCheapest() < priceThreshold || priceFromMomondo.getCheapest() < priceThreshold))
                                                    || (to == null && (priceForFinn.getCheapest() < 2650))) {
                                                double price = priceForFinn.getCheapest() < priceFromMomondo.getCheapest() ? priceForFinn.getCheapest() : priceFromMomondo.getCheapest();
                                                String bestPriceUrl = priceForFinn.getCheapest() < priceFromMomondo.getCheapest() ? finnURLString : momondoURLString;
                                                handleNotifications(price, finnFromNorwayPlace + "-" + finnToChinaPlace + "-" + finnFromChinaPlace + "-" + finnToNorwayPlace + "  " + String.valueOf(price) + " NOK  from: " + new SimpleDateFormat("dd.MM.yyyy").format(from.getTime()) + (to == null ? "" : "  To: " + new SimpleDateFormat("dd.MM.yyyy").format(to.getTime())), bestPriceUrl);
                                            }
                                        }
                                    } catch (Exception e) {

                                    }
                                    //                        for (String recipient : DEBUG_EMAILS) {
                                    //                            sendEmail(recipient, String.valueOf(bestPrice) + from.getTime() + to.getTime(), bestPriceURL);
                                    //                        }
                                    Thread.sleep(random.nextInt(200) * 1000);
//                        }
                                    if (to == null) {
                                        break;
                                    }
                                }
                            }
                        }
                        if (to != null) {
                            to.add(Calendar.DAY_OF_MONTH, 1);
                        }
                    }
                } while (to != null && to.before(toMax) && to.before(end));
//                sleep(SLEEP_TIME);

//                sleep(DEBUG_SLEEP_TIME * random.nextInt(10));
                sleep(SLEEP_TIME * random.nextInt(10));
                from.add(Calendar.DAY_OF_MONTH, 1);

                ExcelExporter excelExporter = new ExcelExporter(fromChinaCities, toNorwayCities, fromNorwayCities, toChinaCities,
                        fromDates, toDates,
                        prices, bestPriceUrls);
                excelExporter.writeToExcel();
            }
        }
    }
}