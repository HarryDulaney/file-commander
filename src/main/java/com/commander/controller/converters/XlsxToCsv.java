package com.commander.controller.converters;

import com.commander.utils.DialogHelper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * {@code xlsxToCsv.class} is for programmatically converting a xlsx to a csv formatted file.
 *
 * @author Harry Dulaney
 */
public class XlsxToCsv extends Converter {
    private static Boolean success = true;

    public XlsxToCsv(File in, File out) {
        super(in, out);
    }


    @Override
    public void convert() {
        final long starttime = System.currentTimeMillis();

        FileWriter csvWriter = null;
        try {
            csvWriter = new FileWriter(out);

        } catch (IOException e) {
            success = false;
            System.out.println("Error @ -> FileWriter.csvWriter @ConvertUtils.convert(XLSXtoCSV) line: 121 ");
            e.printStackTrace();
        }
        assert csvWriter != null;
        try (FileInputStream fis = new FileInputStream(in)) {
            Workbook workBook = new XSSFWorkbook(fis);
            Iterator<Sheet> sheetIterator = workBook.sheetIterator();
            while (sheetIterator.hasNext()) {
                Sheet currentSheet = sheetIterator.next();
                Iterator<Row> iterator = currentSheet.iterator();
                while (iterator.hasNext()) {
                    Row row = iterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    StringBuilder stringBuffer = new StringBuilder();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        if (stringBuffer.length() != 0) {
                            stringBuffer.append(",");
                        }
                        //Checks the built in date formats and formats the cell to csv accordingly
                        String temp = BuiltinFormats.getBuiltinFormat(cell.getCellStyle().getDataFormat());
                        if (temp.equals(BuiltinFormats.getBuiltinFormat(0xe)) ||
                                temp.equals(BuiltinFormats.getBuiltinFormat(0xf)) ||
                                temp.equals(BuiltinFormats.getBuiltinFormat(0x10)) ||
                                temp.equals(BuiltinFormats.getBuiltinFormat(0x11))) {

                            stringBuffer.append(cell.getDateCellValue().toString());
                        } else {

                            switch (cell.getCellType()) {
                                case FORMULA:
                                    stringBuffer.append(cell.getCellFormula());
                                    break;
                                case NUMERIC:
                                    stringBuffer.append(cell.getNumericCellValue());
                                    break;
                                case STRING:
                                    stringBuffer.append(cell.getRichStringCellValue());
                                    break;
                                case BLANK:
                                    stringBuffer.append(" ");
                                case BOOLEAN:
                                    stringBuffer.append(cell.getBooleanCellValue());
                                    break;
                                case ERROR:
                                    stringBuffer.append(cell.getErrorCellValue());
                                    break;
                                default:
                            }
                        }
                    }
                    csvWriter.write(stringBuffer.toString());
                    csvWriter.write("\n");

                }
            }
            csvWriter.flush();
            workBook.close();
            log.info("Converted --> from: " + in.getName() + " to -> " + out.getName() + " in " + ((System.currentTimeMillis() - starttime) + " ms. "));

            DialogHelper.showInfoAlert("Successfully converted your Excel workbook to a csv file.", false);

        } catch (IOException e) {
            success = false;
            System.out.println("Error @ -> IOException @ConvertUtils.convert(XLSXtoCSV) FileOutputStream ");
            DialogHelper.showErrorAlert("Something went wrong creating your file");
            e.printStackTrace();
        }
        deleteSourceFile(success, out);

    }
}
