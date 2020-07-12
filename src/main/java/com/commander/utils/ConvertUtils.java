package com.commander.utils;


import com.commander.model.Convertible;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.opencsv.CSVReader;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;


/**
 * {@code ConvertUtils} contains static classes that implement the com.commander.model.Convertible
 * interface and perform single file conversions.
 *
 * @author HGDIV
 *
 */
public class ConvertUtils {

    private static Boolean deleteSourceAfterConverted;

    private static final String TAG = ConvertUtils.class.getCanonicalName();
    static Logger log = LoggerFactory.getLogger(ConvertUtils.class);
    public static Integer linesPerSheet = 250;

    private ConvertUtils() {

    }
    /**
     * {@code csvToXlsx.class} is for programmatically converting a csv to a xlsx formatted file.
     */
    static class csvToXlsx implements Convertible {

        private final File csvIn;
        private final File xlsxOut;
        private static Boolean success = true;

        public csvToXlsx(File csvIn, File xlsxOut) {
            this.csvIn = csvIn;
            this.xlsxOut = xlsxOut;
        }

        @Override
        public void convert() {

            try (Workbook workBook = new SXSSFWorkbook()) {

                CSVReader reader;
                SXSSFSheet sheet;
                int sheetNum = 1;
                try {
                    String[] writeList;
                    reader = new CSVReader(new FileReader(csvIn));
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

                try (FileOutputStream fileOutputStream = new FileOutputStream(xlsxOut)) {
                    workBook.write(fileOutputStream);
                    DialogHelper.showInfoAlert("Success! Your .csv file has been converted to .xlsx Excel format", false);


                } catch (FileNotFoundException fnfe) {
                    fnfe.printStackTrace();
                    log.error("Error on ConvertUtils.csvToXlsx.FileOutputStream ", fnfe.getCause());
                    success = false;
                }

            } catch (IOException ex) {
                log.error("IOException on ConvertUtils.csvToXlsx.workbook", ex.getCause());
                success = false;
            } finally {
                if (deleteSourceAfterConverted) {
                    if (success) {
                        try {
                            Files.delete(csvIn.toPath());
                        } catch (IOException ex) {
                            DialogHelper.showErrorAlert("Your source file " + csvIn.getName() + "could not be deleted");
                            ex.printStackTrace();
                        }

                    }
                }

            }

        }


    }

    /**
     * {@code xlsxToCsv.class} is for programmatically converting a xlsx to a csv formatted file.
     */
    static class xlsxToCsv implements Convertible {

        private final File xlsxIn;
        private final File csvOut;
        private static Boolean success = true;

        public xlsxToCsv(File xlsxIn, File csvOut) {
            this.xlsxIn = xlsxIn;
            this.csvOut = csvOut;

        }


        @Override
        public void convert() {
            FileWriter csvWriter = null;
            try {
                csvWriter = new FileWriter(csvOut);

            } catch (IOException e) {
                success = false;
                System.out.println("Error @ -> FileWriter.csvWriter @ConvertUtils.convert(XLSXtoCSV) line: 121 ");
                e.printStackTrace();
            }
            assert csvWriter != null;
            try (FileInputStream fis = new FileInputStream(xlsxIn)) {
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
                                    case BOOLEAN:
                                        stringBuffer.append(cell.getBooleanCellValue());
                                        break;
                                    case BLANK:
                                        stringBuffer.append(" ");
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

                DialogHelper.showInfoAlert("Your new file has been successfully created.", false);

            } catch (IOException e) {
                success = false;
                System.out.println("Error @ -> IOException @ConvertUtils.convert(XLSXtoCSV) FileOutputStream ");
                DialogHelper.showErrorAlert("Something went wrong creating your file");
                e.printStackTrace();
            }
            if (deleteSourceAfterConverted) {
                if (success) {
                    try {
                        Files.delete(xlsxIn.toPath());
                    } catch (IOException ex) {
                        DialogHelper.showErrorAlert("Your source file " + xlsxIn.getName() + "could not be deleted");
                        ex.printStackTrace();
                    }

                }
            }


        }
    }

    /**
     * {@code docxToPdf.class} is for programmatically converting a docx to a pdf formatted file.
     */
    static class docxToPdf implements Convertible {

        File in;
        File out;
        private static Boolean success = true;


        public docxToPdf(File in, File out) {
            this.in = in;
            this.out = out;

        }

        @Override
        public void convert() {
            try {
                PdfOptions pdfOptions = PdfOptions.create();
                pdfOptions.fontEncoding("UTF-8");
                XWPFDocument d = new XWPFDocument(OPCPackage.open(in));
                FileOutputStream fileOutputStream = new FileOutputStream(out);
                PdfConverter.getInstance().convert(d, fileOutputStream, pdfOptions);
                fileOutputStream.close();

                DialogHelper.showInfoAlert("Success!: Your Word document has been converted to pdf.", false);
            } catch (IOException | InvalidFormatException e) {
                DialogHelper.showErrorAlert("Something went wrong, we were unable to convert you document.\nPlease ensure the output folder is write enabled");
                e.printStackTrace();
            }

        }
    }

    /**
     * {@code pdfToDocx.class} is for programmatically converting a pdf to docx formatted file.
     */
    static class pdfToDocx implements Convertible {

        private File in;
        private File out;
        private static Boolean success = true;

        public pdfToDocx(File in, File out) {
            this.in = in;
            this.out = out;

        }


        @Override
        public void convert() {
            XWPFDocument doc = new XWPFDocument();
            PdfReader reader;
            try {
                reader = new PdfReader(in.getAbsolutePath());
                PdfReaderContentParser parser = new PdfReaderContentParser(reader);
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    TextExtractionStrategy strategy =
                            parser.processContent(i, new SimpleTextExtractionStrategy());
                    String text = strategy.getResultantText();
                    XWPFParagraph p = doc.createParagraph();
                    XWPFRun run = p.createRun();
                    run.setText(text);
                    run.addBreak(BreakType.PAGE);
                    FileOutputStream fos = new FileOutputStream(out);
                    doc.write(fos);
                    fos.close();
                    DialogHelper.showInfoAlert("Success!", false);
                    doc.close();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    /**
     * {@code ImageConvert.class} is for programmatically converting between image file storage types.
     * <em>Special case required for converting from a PNG to a JPG</em> {@link PngToJpg}
     */
    static class ImageConvert implements Convertible {

        private final File in;
        private File out;
        private final String format;
        private static Boolean success = true;

        public ImageConvert(File in, File out, String format) {
            this.in = in;
            this.out = out;
            this.format = format;
        }

        private boolean genericConversion() {
            boolean result = true;
            BufferedImage bufferedImage = null;
            try {
                bufferedImage = ImageIO.read(in);

            } catch (IOException e) {
                log.error(e.getCause() + " happened while reading image: " + in.getName());
                e.printStackTrace();

            }
            try {
                if (bufferedImage == null) {
                    result = false;
                } else {
                    ImageIO.write(bufferedImage, format, out);
                }

            }catch (IllegalArgumentException iae){
                DialogHelper.showErrorAlert("We could not complete the conversion because One or more required fields is null.");
                iae.printStackTrace();
                result = false;

            } catch (IOException ie) {
                DialogHelper.showErrorAlert("Something went wrong reading or writing the files,\n we could not complete the conversion.");
                log.error(ie.getCause() + " happened while writing image file: " + out.getName());
                ie.printStackTrace();
                result = false;
            }
            return result;
        }

        @Override
        public void convert() {

            if (genericConversion()) {
                DialogHelper.showInfoAlert("Success! Your image was converted. To view it, click on the link to your output directory", false);
            } else {
                DialogHelper.showErrorAlert("Something went wrong converting the image, please ensure it is a supported format and try again.");
            }


        }
    }

    /**
     * {@code PngToJpg.class} is for programmatically converting from png to jpg image format type.
     * <em>This case requires manually setting the background color.</em>
     */
    static class PngToJpg implements Convertible {

        private File in;
        private File out;
        private static Boolean success = true;


        public PngToJpg(File in, File out) {
            this.in = in;
            this.out = out;

        }

        @Override
        public void convert() {

            BufferedImage bufferedImage = null;

            try {
                bufferedImage = ImageIO.read(in);
            } catch (IOException e) {
                log.error(e.getCause() + " happened while reading the PNG file.");
                e.printStackTrace();
            }

            assert bufferedImage != null;
            BufferedImage outBuffImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            outBuffImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

            try {
                ImageIO.write(bufferedImage, "jpg", out);
            } catch (IOException e) {
                log.error(e.getCause() + " happened while writing the JPG file");
                e.printStackTrace();
            }
        }
    }

    public static void setDeleteSourceAfterConverted(Boolean deleteSourceAfterConverted) {
        ConvertUtils.deleteSourceAfterConverted = deleteSourceAfterConverted;
    }
}