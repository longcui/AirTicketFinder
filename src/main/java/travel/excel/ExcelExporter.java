package travel.excel;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Hyperlink;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ExcelExporter {
    private static final Logger logger = Logger.getLogger(ExcelExporter.class);

    private List<String> fromCities;
    private List<String> fromDates;
    private List<String> toDates;
    private List<Double> bestPrices;
    private List<String> bestPriceUrls;

    public void writeToExcel() {
        try {
            logger.info("Generating Excel.");
//todo: why this does not work: createFile is false            File file = new File("C:\\prices\\price_" + LocalDateTime.now() + ".xlsx");
            LocalDateTime now = LocalDateTime.now();
            File file = new File("price_" + now.getDayOfMonth() + "_" + now.getHour() + "_" + now.getMinute() + ".xls");
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

                HSSFCell cellA1 = row1.createCell((short) 0);
                cellA1.setCellValue(fromCities.get(i));
                HSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFillForegroundColor(HSSFColor.GOLD.index);
                cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cellA1.setCellStyle(cellStyle);

                HSSFCell cellB1 = row1.createCell((short) 1);
                cellB1.setCellValue(fromDates.get(i));

                HSSFCell cellC1 = row1.createCell((short) 2);
                cellC1.setCellValue(toDates.get(i));

                HSSFCell cellD1 = row1.createCell((short) 3);
                cellD1.setCellValue(bestPrices.get(i));

                HSSFCell cellE1 = row1.createCell((short) 4);
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

        fromCities.clear();
        fromDates.clear();
        toDates.clear();
        bestPrices.clear();
        bestPriceUrls.clear();
        logger.info("file saved.");
    }

    public void setFromDates(List<String> fromDates) {
        this.fromDates = fromDates;
    }

    public void setToDates(List<String> toDates) {
        this.toDates = toDates;
    }

    public void setBestPrices(List<Double> bestPrices) {
        this.bestPrices = bestPrices;
    }

    public void setBestPriceUrls(List<String> bestPriceUrls) {
        this.bestPriceUrls = bestPriceUrls;
    }

    public void setFromCities(List<String> fromCities) {
        this.fromCities = fromCities;
    }
}
