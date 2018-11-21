package travel.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;

public class TicketInfo implements Comparable<TicketInfo>{
    /*
    departure
     */
    @NotNull
    private final String outboundFrom, outboundTo;
    @NotNull
    private final LocalDate outboundDate;

    /**
     * Return
     */
    @Nullable
    private final String inboundFrom, inboundTo;
    @Nullable
    private final LocalDate inboundDate;

    @NotNull
    private final TicketPrice ticketPrice;

    @NotNull
    private final String priceUrl;

    public TicketInfo(@NotNull String outboundFrom, @NotNull String outboundTo, @NotNull LocalDate outboundDate,
                      @NotNull String inboundFrom, @NotNull String inboundTo, @Nullable LocalDate inboundDate,
                      @NotNull TicketPrice ticketPrice, @NotNull String priceUrl) {
        this.outboundFrom = outboundFrom;
        this.outboundTo = outboundTo;
        this.outboundDate = outboundDate;
        this.inboundFrom = inboundFrom;
        this.inboundTo = inboundTo;
        this.inboundDate = inboundDate;
        this.ticketPrice = ticketPrice;
        this.priceUrl = priceUrl;
    }

    public String getOutboundFrom() {
        return outboundFrom;
    }

    public String getOutboundTo() {
        return outboundTo;
    }

    public LocalDate getOutboundDate() {
        return outboundDate;
    }

    public String getInboundFrom() {
        return inboundFrom;
    }

    public String getInboundTo() {
        return inboundTo;
    }

    public LocalDate getInboundDate() {
        return inboundDate;
    }


    @NotNull
    public String getInboundDateTimeString() {
        return inboundDate == null? "" : inboundDate.toString();
    }

    public TicketPrice getTicketPrice() {
        return ticketPrice;
    }

    public String getPriceUrl() {
        return priceUrl;
    }


    @Override
    public int compareTo(@NotNull TicketInfo o) {
        return ticketPrice.compareTo(o.getTicketPrice());
    }
}
