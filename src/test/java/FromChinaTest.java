//import org.apache.poi.hssf.usermodel.*;
//import travel.finnAndMomodo.FromChinaTravel;
//
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.NumberFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Locale;
//
///**
// * Created by Longcui on 17.04.2015.
// */
//public class FromChinaTest {
//
//    public void testDate() {
//        Calendar start = Calendar.getInstance();
//        Calendar end = Calendar.getInstance();
//
//        start.set(2015, 7, 10);
//        end.set(2015,8, 15);
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-YYYY");     //14-5-2015
//        System.out.println(sdf.format(start.getTime()));
//         sdf = new SimpleDateFormat("dd.MM.YYYY");     //14.5.2015
//        System.out.println(sdf.format(start.getTime()));
//    }
//
//    public void testPriceFormat() throws ParseException {
//        String price = "7,271";
//        NumberFormat numberFormat = NumberFormat.getInstance(Locale.FRANCE);
//        System.out.println(numberFormat.parse(price).doubleValue());
//
//        String priceFinn = "17 369,-";
////        NumberFormat numberFormatFinn = NumberFormat.getInstance(Locale.FRANCE);
////        System.out.println(numberFormatFinn.parse(price).doubleValue());
//        System.out.println(Double.parseDouble(priceFinn.substring(0, priceFinn.length() - 2).replaceAll(" ", "")));
//    }
//
//    public void testCountry() {
//        for (FromChinaTravel.MomondoChinaPlace country : FromChinaTravel.MomondoChinaPlace.values()) {
//            System.out.println(country.toString());
//            System.out.println(country.name());
//            System.out.println(country.getIataCode());
//        }
//    }
//
//    public void testWritingToExcel() {
//        try {
//            FileOutputStream fileOut = new FileOutputStream("price.xls");
//            HSSFWorkbook workbook = new HSSFWorkbook();
//            HSSFSheet worksheet = workbook.createSheet("POI Worksheet");
//
//            // index from 0,0... cell A1 is cell(0,0)
//            HSSFRow row1 = worksheet.createRow((short) 0);
//
//            HSSFCell cellA1 = row1.createCell((short) 0);
//            cellA1.setCellValue("Hello");
//            HSSFCellStyle cellStyle = workbook.createCellStyle();
////            cellStyle.setFillForegroundColor(HSSFColor.GOLD.index);
////            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//            cellA1.setCellStyle(cellStyle);
//
//            HSSFCell cellB1 = row1.createCell((short) 1);
//            cellB1.setCellValue("Goodbye");
//            cellStyle = workbook.createCellStyle();
////            cellStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
////            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//            cellB1.setCellStyle(cellStyle);
//
//            HSSFCell cellC1 = row1.createCell((short) 2);
//            cellC1.setCellValue(true);
//
//            HSSFCell cellD1 = row1.createCell((short) 3);
//            cellD1.setCellValue(new Date());
//            cellStyle = workbook.createCellStyle();
//            cellStyle.setDataFormat(HSSFDataFormat
//                    .getBuiltinFormat("m/d/yy h:mm"));
//            cellD1.setCellStyle(cellStyle);
//
//            workbook.write(fileOut);
//            fileOut.flush();
//            fileOut.close();
//        }catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}