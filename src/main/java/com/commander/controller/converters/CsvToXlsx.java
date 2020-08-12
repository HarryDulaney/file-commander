package com.commander.controller.converters;

import com.commander.utils.DialogHelper;
import com.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;

/**
 * {@code csvToXlsx.class} is for programmatically converting a csv to a xlsx formatted file.
 *
 * @author Harry Dulaney
 */
public class CsvToXlsx extends Converter {

    private static Boolean success = true;
    private Integer linesPerSheet;

    public CsvToXlsx(File in, File out, Integer linesPerSheet) {
        super(in,out);
        this.linesPerSheet = linesPerSheet;

    }

    @Override
    public void convert() {
        log.info("convert() -- running -- From: " + in.getName() + " To-> " + out.getName());
        try (Workbook workBook = new SXSSFWorkbook()) {

            CSVReader reader;
            SXSSFSheet sheet;
            int sheetNum = 1;
            try {
                String[] writeList;
                reader = new CSVReader(new FileReader(in));
                sheet = (SXSSFSheet) workBook.createSheet("Sheet".concat(String.valueOf(sheetNum++)));
                int rowNumber = 0;
                while ((writeList = reader.readNext()) != null) {
                    Row currentRow = sheet.createRow(rowNumber++);
                    for (int i = 0; i < writeList.length; i++) {
                        currentRow.createCell(i).setCellValue(writeList[i]);
                    }
                    if ((rowNumber == linesPerSheet)) {
                        sheet = (SXSSFSheet) workBook.createSheet("Sheet".concat(String.valueOf(sheetNum++)));
                        rowNumber = 0;
                    }
                }
            } catch (Exception e) {
                success = false;
                e.printStackTrace();
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(out)) {
                workBook.write(fileOutputStream);
                if (success) {
                    DialogHelper.showInfoAlert("Success! Your file named: " + in.getName() + " was converted to: " + out.getName() + ",\nview it by clicking on the link to your output directory", false);
                }


            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
                log.error("Error on ConvertUtils.csvToXlsx.FileOutputStream ", fnfe.getCause());
                success = false;
            }

        } catch (IOException ex) {
            log.error("IOException on ConvertUtils.csvToXlsx.workbook", ex.getCause());
            success = false;
        } finally {
            deleteSourceFile(success, in);

        }
    }
}
