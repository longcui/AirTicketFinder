package travel.finnAndMomodo;

import org.apache.log4j.Logger;
import travel.domain.TicketInfo;
import travel.domain.TicketPrice;
import travel.excel.ExcelExporter;

import java.time.LocalDate;

class FromStavangerToChina extends Travel {
    private static final Logger logger = Logger.getLogger(FromStavangerToChina.class);

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
            LocalDate from = start;
            LocalDate to = null;
            LocalDate toMax = null;
            if (args.length <= 1 || !args[1].equals("oneWay")) {
                to = start.plusDays(MINIMAL_STAY_DAY);
                toMax = start.plusDays(MAXIMAL_STAY_DAY);
            } else {
                MINIMAL_STAY_DAY = 0;
            }
            LocalDate lastPossibleLeaveDay = end.plusDays(-MINIMAL_STAY_DAY);

            while (from.isBefore(lastPossibleLeaveDay)) {
                to = start.plusDays(MINIMAL_STAY_DAY);
                do {
//                    System.out.println(from.getTime() + "-------" + to.getTime());
                    for (MomondoNorwayPlace momondoFromNorwayPlace : MomondoNorwayPlace.values()) {
                        FinnNorwayPlace finnFromNorwayPlace = FinnNorwayPlace.valueOf(momondoFromNorwayPlace.name());
                        for (MomondoChinaPlace momondoToChinaPlace : MomondoChinaPlace.values())
                        {
                             momondoToChinaPlace = MomondoChinaPlace.SHANGHAI;
                            FinnChinaPlace finnToChinaPlace = FinnChinaPlace.valueOf(momondoToChinaPlace.name());
                            for (MomondoChinaPlace momondoFromChinaPlace : MomondoChinaPlace.values())
                            {
                                FinnChinaPlace finnFromChinaPlace = FinnChinaPlace.valueOf(momondoFromChinaPlace.name());
                                for (MomondoNorwayPlace momondoToNorwayPlace : MomondoNorwayPlace.values()) {
                                    FinnNorwayPlace finnToNorwayPlace = FinnNorwayPlace.valueOf(momondoToNorwayPlace.name());
                                    String momondoURLString = getMomondoURLString(momondoFromNorwayPlace.getCode(), momondoToChinaPlace.getCode(), momondoFromChinaPlace.getCode(), momondoToNorwayPlace.getCode(), from, to);
                                    String finnURLString = getFinnURLString(finnFromNorwayPlace.getCode(), finnToChinaPlace.getCode(), finnFromChinaPlace.getCode(), finnToNorwayPlace.getCode(), from, to);
                                    try {
//                                        TicketPrice priceFromMomondo = new TicketPrice();
                                        TicketPrice priceFromMomondo = TravelAgent.getPriceFromMomondo(driver, momondoURLString);
                                        logger.info("Momondo   :" + momondoFromNorwayPlace + "-" + momondoToChinaPlace + "-" + momondoFromChinaPlace + "-" + momondoToNorwayPlace + ": " + from + " " + to + ". price is: " + priceFromMomondo + "url: " + momondoURLString);
                                        TicketInfo ticketInfoMomondo = new TicketInfo(momondoFromNorwayPlace.name(), momondoToChinaPlace.name(), from,
                                                momondoFromChinaPlace.name(), momondoToNorwayPlace.name(), to,
                                                priceFromMomondo, momondoURLString);
                                        ticketInfos.add(ticketInfoMomondo);

                                        //Finn could not show the cheapest based on the filter that max transfer 1 time
//                                        TicketPrice priceForFinn = new TicketPrice();
//                                        TicketPrice priceForFinn = TravelAgent.getPriceFromFinn(driver, finnURLString);
//                                        logger.info("Finn   :" + finnFromNorwayPlace + "-" + finnToChinaPlace + "-" + finnFromChinaPlace + "-" + finnToNorwayPlace + ": " + from + " " + to + ". price is: " + priceForFinn + "url: " + finnURLString);
//                                        TicketInfo ticketInfoFinn = new TicketInfo(finnFromNorwayPlace.name(), finnToChinaPlace.name(), from,
//                                                finnFromChinaPlace.name(), finnToNorwayPlace.name(), to,
//                                                priceForFinn, finnURLString);
//                                        ticketInfos.add(ticketInfoFinn);

                                        if (enableEmailNotification) {

                                        }
                                    } catch (Exception e) {

                                    }
                                    //                        for (String recipient : DEBUG_EMAILS) {
                                    //                            sendEmail(recipient, String.valueOf(bestPrice) + from.getTime() + to.getTime(), bestPriceURL);
                                    //                        }
                                    Thread.sleep(random.nextInt(100) * 1000);
//                        }
                                    if (to == null) {
                                        break;
                                    }
                                }
                            }
                        }
                        if (to != null) {
                            to = to.plusDays(1);
                        }
                    }
                } while (to != null && to.isBefore(toMax) && to.isBefore(end));
//                sleep(DEBUG_SLEEP_TIME * random.nextInt(10));
//                sleep(SLEEP_TIME * random.nextInt(10));
                from = from.plusDays(1);
            }
            ExcelExporter excelExporter = new ExcelExporter(ticketInfos);
            excelExporter.writeToExcel();
            ticketInfos.clear();
        }
    }
}