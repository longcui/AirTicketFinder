package travel.excel;

import org.apache.log4j.Logger;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.*;
import org.jetbrains.annotations.NotNull;
import travel.domain.TicketInfo;
import travel.domain.TicketPrice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class ExcelExporter {
    private static final Logger logger = Logger.getLogger(ExcelExporter.class);

    @NotNull
    private final List<TicketInfo> ticketInfos;

    public ExcelExporter(List<TicketInfo> ticketInfos) {
        this.ticketInfos = ticketInfos;
    }


    public void writeToExcel() {
        if(ticketInfos.size() == 0) {
            logger.info("No ticket info to write to excel.");
            return;
        }

        Collections.sort(ticketInfos);

        try {
            logger.info("Generating Excel...");
            LocalDateTime now = LocalDateTime.now();
            File file = new File("ticketInfos " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")) + ".xls");
            if(!file.exists()) {
                boolean newFile = file.createNewFile();
                if(!newFile) {
                    throw new IllegalArgumentException("could not create file.");
                }
            }
            try(FileOutputStream fileOut = new FileOutputStream(file)) {
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet worksheet = workbook.createSheet("POI Worksheet");

                // index from 0,0... cell A1 is cell(0,0)
                int row = 0;
                for (TicketInfo ticketInfo : ticketInfos) {


                    HSSFRow row1 = worksheet.createRow((short) row);

                    int colIdx = 0;
                    HSSFCell cellA1 = row1.createCell((short) colIdx ++);
                    cellA1.setCellValue(ticketInfo.getOutboundFrom());
                    HSSFCellStyle cellStyle = workbook.createCellStyle();
//                    cellStyle.setFillForegroundColor(HSSFColor.GOLD.index);
//                    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                    cellA1.setCellStyle(cellStyle);

                    HSSFCell cellA11 = row1.createCell((short) colIdx ++);
                    cellA11.setCellValue(ticketInfo.getOutboundTo());

                    HSSFCell cellA12 = row1.createCell((short) colIdx ++);
                    cellA12.setCellValue(ticketInfo.getInboundFrom());

                    HSSFCell cellA13 = row1.createCell((short) colIdx ++);
                    cellA13.setCellValue(ticketInfo.getInboundTo());


                    HSSFCell cellB1 = row1.createCell((short) colIdx ++);
                    cellB1.setCellValue(ticketInfo.getOutboundDate().toString());

                    HSSFCell cellC1 = row1.createCell((short) colIdx ++);
                    cellC1.setCellValue(ticketInfo.getInboundDateTimeString());

                    TicketPrice ticketPrice = ticketInfo.getTicketPrice();

                    HSSFCell cellD1 = row1.createCell((short) colIdx ++);
                    cellD1.setCellValue(ticketPrice.getCheapest());

                    cellD1 = row1.createCell((short) colIdx ++);
                    cellD1.setCellValue(ticketPrice.getBest());

                    cellD1 = row1.createCell((short) colIdx ++);
                    cellD1.setCellValue(ticketPrice.getQuickest());


                    HSSFCell cellE1 = row1.createCell((short) colIdx);
                    cellE1.setCellValue(ticketInfo.getPriceUrl());


                    HSSFCellStyle cellStyle1 = workbook.createCellStyle();
                    HSSFFont font = workbook.createFont();
                    font.setUnderline(HSSFFont.U_SINGLE);
//                    font.setColor(HSSFColor.BLUE.index);
                    cellStyle1.setFont(font);
//                    cellE1.setCellStyle(cellStyle1);

                    HSSFHyperlink link = (HSSFHyperlink)workbook.getCreationHelper()
                            .createHyperlink(HyperlinkType.URL);
                    link.setAddress(ticketInfo.getPriceUrl());
                    cellE1.setHyperlink((HSSFHyperlink) link);
                    row ++;
                }

                setAutoSizeColumn(worksheet);

                workbook.write(fileOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("file saved.");
    }

    private void setAutoSizeColumn(HSSFSheet worksheet) {
        int columCnt = worksheet.getRow(worksheet.getFirstRowNum()).getLastCellNum();
        for (int i = 0; i < columCnt; i++) {
            worksheet.autoSizeColumn(i);
        }
    }


}
