package travel.excel;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Hyperlink;
import travel.domain.TicketInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ExcelExporter {
    private static final Logger logger = Logger.getLogger(ExcelExporter.class);

    final private List<String> fromChinaCities;
    final private List<String> toNorwayCities;
    final private List<String> fromNorwayCities;
    final private List<String> toChinaCities;
    /**
     * eg: String like 10.06.2019
     */
    final private List<String> fromDates;
    final private List<String> toDates;

    final private List<TicketInfo> bestPrices;
    final private List<String> bestPriceUrls;

    public ExcelExporter(List<String> fromChinaCities, List<String> toNorwayCities, List<String> fromNorwayCities, List<String> toChinaCities, List<String> fromDates, List<String> toDates, List<TicketInfo> bestPrices, List<String> bestPriceUrls) {
        this.fromChinaCities = fromChinaCities;
        this.toNorwayCities = toNorwayCities;
        this.fromNorwayCities = fromNorwayCities;
        this.toChinaCities = toChinaCities;
        this.fromDates = fromDates;
        this.toDates = toDates;
        this.bestPrices = bestPrices;
        this.bestPriceUrls = bestPriceUrls;
    }



    public void writeToExcel() {
        try {
            logger.info("Generating Excel.");
//todo: why this does not work: createFile is false            File file = new File("C:\\prices\\price_" + LocalDateTime.now() + ".xlsx");
            LocalDateTime now = LocalDateTime.now();
            File file = new File("price_" + Optional.ofNullable(fromDates.get(0)).orElse("0") + "(" + now.getHour() + "_" + now.getMinute() + ").xls");
//            File file = new File("test1122");
            if(!file.exists()) {
//                boolean mkdirs = file.mkdirs();
//                if(!mkdirs) {
//                    logger.error("could not make dirs");
//                }

                boolean newFile = file.createNewFile();
                if(!newFile) {
                    logger.error("could not create file.");
                }
            }
            FileOutputStream fileOut = new FileOutputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet worksheet = workbook.createSheet("POI Worksheet");

            // index from 0,0... cell A1 is cell(0,0)
            for(int i = 0; i < fromDates.size(); i ++) {
                HSSFRow row1 = worksheet.createRow((short) i);

                int colIdx = 0;
                HSSFCell cellA1 = row1.createCell((short) colIdx ++);
                cellA1.setCellValue(fromChinaCities.get(i));
                HSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFillForegroundColor(HSSFColor.GOLD.index);
                cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cellA1.setCellStyle(cellStyle);

                HSSFCell cellA11 = row1.createCell((short) colIdx ++);
                cellA11.setCellValue(toNorwayCities.get(i));

                HSSFCell cellA12 = row1.createCell((short) colIdx ++);
                cellA12.setCellValue(fromNorwayCities.get(i));

                HSSFCell cellA13 = row1.createCell((short) colIdx ++);
                cellA13.setCellValue(toChinaCities.get(i));


                HSSFCell cellB1 = row1.createCell((short) colIdx ++);
                cellB1.setCellValue(fromDates.get(i));

                HSSFCell cellC1 = row1.createCell((short) colIdx ++);
                cellC1.setCellValue(toDates.size() < (i + 1) || toDates.get(i) == null? "": toDates.get(i));

                HSSFCell cellD1 = row1.createCell((short) colIdx ++);
                cellD1.setCellValue(bestPrices.get(i).getCheapest());

                HSSFCell cellD11 = row1.createCell((short) colIdx ++);
                cellD11.setCellValue(bestPrices.get(i).getQuickest());

                HSSFCell cellD12 = row1.createCell((short) colIdx ++);
                cellD12.setCellValue(bestPrices.get(i).getBest());

                HSSFCell cellE1 = row1.createCell((short) colIdx ++);
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

        fromChinaCities.clear();
        toNorwayCities.clear();
        fromNorwayCities.clear();
        toNorwayCities.clear();
        fromDates.clear();
        toDates.clear();
        bestPrices.clear();
        bestPriceUrls.clear();
        logger.info("file saved.");
    }


}
