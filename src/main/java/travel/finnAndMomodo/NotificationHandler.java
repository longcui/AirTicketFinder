package travel.finnAndMomodo;

import travel.domain.TicketPrice;

import java.text.SimpleDateFormat;
import java.util.Date;

import static travel.finnAndMomodo.Travel.handleNotifications;

public class NotificationHandler {

    public static void handleEmailNotification(TicketPrice priceForFinn, TicketPrice priceFromMomondo,
                                               String finnFromNorwayPlace, String finnToChinaPlace, String finnFromChinaPlace, String finnToNorwayPlace,
                                               String finnURLString, String momondoURLString,
                                               Date from, Date to) {
        double priceThreshold = 3900;
        if ((to != null) && (priceForFinn.getCheapest() < priceThreshold || priceFromMomondo.getCheapest() < priceThreshold)
                || ((to == null) && (priceForFinn.getCheapest() < 2650))) {
            double price = priceForFinn.getCheapest() < priceFromMomondo.getCheapest() ? priceForFinn.getCheapest() : priceFromMomondo.getCheapest();
            String bestPriceUrl = priceForFinn.getCheapest() < priceFromMomondo.getCheapest() ? finnURLString : momondoURLString;
            handleNotifications(price,
                    finnFromNorwayPlace + "-" + finnToChinaPlace + "-" + finnFromChinaPlace + "-" + finnToNorwayPlace + "  " + String.valueOf(price) + " NOK  " +
                            "from: " + new SimpleDateFormat("dd.MM.yyyy").format(from.getTime()) +
                            (to == null ? "" : "  To: " + new SimpleDateFormat("dd.MM.yyyy").format(to.getTime())), bestPriceUrl);
        }
    }
}
