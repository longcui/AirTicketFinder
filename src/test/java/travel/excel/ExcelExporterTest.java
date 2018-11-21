package travel.excel;

import org.junit.Test;
import travel.domain.TicketInfo;
import travel.domain.TicketPrice;

import java.time.LocalDate;
import java.util.ArrayList;

public class ExcelExporterTest {

    @Test
    public void test() {
        ArrayList<TicketInfo> ticketInfos = new ArrayList<>();
        ticketInfos.add(new TicketInfo("pek", "svg", LocalDate.now(),
                "aa", "bb", LocalDate.now(),
                new TicketPrice(1, 2, 3), "https://www.google.no"));
        ticketInfos.add(new TicketInfo("pek1", "svg1", LocalDate.now(),
                "aa1", "bb1", LocalDate.now(),
                new TicketPrice(111, 22, 33), "https://www.google.no1"));
        ticketInfos.add(new TicketInfo("pek2", "svg2", LocalDate.now(),
                "aa2", "bb2", LocalDate.now(),
                new TicketPrice(11, 222, 333), "https://www.google.no2"));
        ExcelExporter excelExporter = new ExcelExporter(ticketInfos);
        excelExporter.writeToExcel();
    }



}