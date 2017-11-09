package travel.excel;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Hyperlink;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ExcelExporter {
    private static final Logger logger = Logger.getLogger(ExcelExporter.class);

    private static List<String> fromCities;
    private static List<String> froms ;
    private static List<String> tos ;
    private static List<Double> bestPrices;
    private static List<String> bestPriceUrls;

    private static void writeToExcel(Calendar from) {
        try {
            System.out.println("Generate Xls for date: " + from);
            FileOutputStream fileOut = new FileOutputStream("price_" + new SimpleDateFormat("dd-MMM-yyyy").format(from.getTime()) + ".xls");
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet worksheet = workbook.createSheet("POI Worksheet");

            // index from 0,0... cell A1 is cell(0,0)
            for(int i =0; i < fromCities.size(); i ++) {
                HSSFRow row1 = worksheet.createRow((short) i);

                HSSFCell cellA1 = row1.createCell((short) 0);
                cellA1.setCellValue(fromCities.get(i));
                HSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setFillForegroundColor(HSSFColor.GOLD.index);
                cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cellA1.setCellStyle(cellStyle);

                HSSFCell cellB1 = row1.createCell((short) 1);
                cellB1.setCellValue(froms.get(i));

                HSSFCell cellC1 = row1.createCell((short) 2);
                cellC1.setCellValue(tos.get(i));

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
        froms.clear();
        tos.clear();
        bestPrices.clear();
        bestPriceUrls.clear();
        logger.info("file saved.");
    }

    public static void setFromCities(List<String> fromCities) {
        ExcelExporter.fromCities = fromCities;
    }

    public static void setFroms(List<String> froms) {
        ExcelExporter.froms = froms;
    }

    public static void setTos(List<String> tos) {
        ExcelExporter.tos = tos;
    }

    public static void setBestPrices(List<Double> bestPrices) {
        ExcelExporter.bestPrices = bestPrices;
    }

    public static void setBestPriceUrls(List<String> bestPriceUrls) {
        ExcelExporter.bestPriceUrls = bestPriceUrls;
    }
}
