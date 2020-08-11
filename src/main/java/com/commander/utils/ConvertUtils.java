package com.commander.utils;


import com.commander.model.Convertible;
import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.opencsv.CSVReader;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
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
import javax.naming.OperationNotSupportedException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * {@code ConvertUtils} contains static classes that implement the com.commander.model.Convertible
 * interface and perform single file conversions.
 *
 * @author HGDIV
 */
public class ConvertUtils {

    private static Pane toastPane;

    public static void setToastPane(Pane pane) {
        toastPane = pane;
    }

    private static Boolean deleteSourceAfterConverted;
    private static final String TAG = ConvertUtils.class.getCanonicalName();
    static Logger log = LoggerFactory.getLogger(ConvertUtils.class);

    private ConvertUtils() {
    }

    /**
     * Evaluates user preference deleteSourceAfterConverted and
     * the boolean success variable and deletes the source file if appropriate
     *
     * @param success was the file successfully converted and written
     * @param file    the source file
     */
    private static void deleteSourceFile(boolean success, File file) {
        if (deleteSourceAfterConverted) {
            if (success) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException ex) {
                    DialogHelper.showErrorAlert("Your source file " + file.getName() + "could not be deleted");
                    ex.printStackTrace();
                }

            }
        }

    }

    /**
     * {@code csvToXlsx.class} is for programmatically converting a csv to a xlsx formatted file.
     */
    static class CsvToXlsx implements Convertible {

        private final File csvIn;
        private final File xlsxOut;
        private static Boolean success = true;
        private Integer linesPerSheet;

        public CsvToXlsx(File csvIn, File xlsxOut, Integer linesPerSheet) {
            this.csvIn = csvIn;
            this.xlsxOut = xlsxOut;
            this.linesPerSheet = linesPerSheet;

        }

        @Override
        public void convert() {
            log.info("convert() -- running -- From: " + csvIn.getName() + " To-> " + xlsxOut.getName());
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
                deleteSourceFile(success, csvIn);

            }
        }
    }

    /**
     * {@code xlsxToCsv.class} is for programmatically converting a xlsx to a csv formatted file.
     */
    static class XlsxToCsv implements Convertible {

        private final File xlsxIn;
        private final File csvOut;
        private static Boolean success = true;

        public XlsxToCsv(File xlsxIn, File csvOut) {
            this.xlsxIn = xlsxIn;
            this.csvOut = csvOut;

        }


        @Override
        public void convert() {
            log.info("convert() -- running -- From: " + xlsxIn.getName() + " To-> " + csvOut.getName());
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

                DialogHelper.showInfoAlert("Successfully converted your Excel workbook to a csv file.", false);

            } catch (IOException e) {
                success = false;
                System.out.println("Error @ -> IOException @ConvertUtils.convert(XLSXtoCSV) FileOutputStream ");
                DialogHelper.showErrorAlert("Something went wrong creating your file");
                e.printStackTrace();
            }
            deleteSourceFile(success, csvOut);

        }
    }

    /**
     * {@code ClonePdfToDocx.class } Converts the whole PDF to a DOCX file formatted Word Doc
     * This conversion calls the underlying Microsoft Office installation,using documents4j, to convent the PDF to a Word formatted file -> .docx.
     *
     * For this conversion to be successful, all other instances of MS Word must be closed on the local machine.
     */
    static class ClonePdfToDocx implements Convertible {

        private final File in;
        private final File out;
        private static Boolean success = false;


        public ClonePdfToDocx(final File in, final File out) {
            this.in = in;
            this.out = out;

        }

        @Override
        public void convert() {
            DialogHelper.showInfoAlert("Please ensure that all instances of Microsoft " +
                    "Word are closed before pressing OK to start the conversion", true);

            try {
                IConverter converter = LocalConverter.builder()
                        .workerPool(20, 25, 2, TimeUnit.SECONDS)
                        .processTimeout(5, TimeUnit.SECONDS)
                        .build();

                success = converter
                        .convert(in).as(DocumentType.DOCX)
                        .to(out).as(DocumentType.PDF)
                        .execute();

            } catch (Exception e) {
                e.printStackTrace();
            }
            AnchorPane ap = new AnchorPane();

            if (success) {
                DialogHelper.snackbarToast(ap, "Success! We created a Word Document named " +
                        FilenameUtils.getName(in.toString()) + " from " + FilenameUtils.getName(in.toString()));
            } else {
                DialogHelper.snackbarToast(ap, "Something went wrong, we were not able to create a new docx word document " +
                        "from " + FilenameUtils.getName(in.toString()));
            }

        }
    }

    /**
     * {@code docxToPdf.class} programmatically converts docx to pdf file format.
     */
    static class docxToPdf implements Convertible {

        private final File in;
        private final File out;
        private static Boolean success = true;


        public docxToPdf(File in, File out) {
            this.in = in;
            this.out = out;

        }

        @Override
        public void convert() {
            log.info("convert() -- running -- From: " + in.getName() + " To-> " + out.getName());

            try {
                PdfOptions pdfOptions = PdfOptions.create();
                pdfOptions.fontEncoding("UTF-8");
                XWPFDocument d = new XWPFDocument(OPCPackage.open(in));
                FileOutputStream fileOutputStream = new FileOutputStream(out);
                PdfConverter.getInstance().convert(d, fileOutputStream, pdfOptions);
                fileOutputStream.close();

                DialogHelper.snackbarToast(toastPane, "Success!: Your Word document has been converted to pdf.");
            } catch (IOException | InvalidFormatException e) {
                DialogHelper.showErrorAlert("Something went wrong, we were unable to convert you document.\nPlease ensure the output folder is write enabled");
                e.printStackTrace();
                success = false;
            }
            deleteSourceFile(success, in);
        }
    }

    /**
     * {@code pdfToDocx.class} Extract the text content from the pdf and write to docx
     */
    static class pdfToDocx implements Convertible {

        private final File in;
        private final File out;
        private static Boolean success = true;

        public pdfToDocx(File in, File out) {
            this.in = in;
            this.out = out;

        }


        @Override
        public void convert() {
            log.info("convert() -- running -- From: " + in.getName() + " To-> " + out.getName());
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

                    run.addBreak(BreakType.PAGE);
                    FileOutputStream fos = new FileOutputStream(out);
                    doc.write(fos);
                    fos.close();
                    DialogHelper.snackbarToast(toastPane
                            , "Successfully created a new DOCX file with the text content from your PDF");

                    doc.close();
                }


            } catch (IOException e) {
                success = false;
                e.printStackTrace();
            }
            deleteSourceFile(success, in);
        }
    }

    /**
     * {@code DocxToHtml.class} Converts a  Docx format file
     */
    static class DocxToHtml implements Convertible {

        private final File in;
        private final File out;
        private static Boolean success = true;

        public DocxToHtml(File in, File out) {
            this.in = in;
            this.out = out;
            //TODO

        }


        @Override
        public void convert() {
            log.info("convert() -- running -- From: " + in.getName() + " To-> " + out.getName());

            DialogHelper.snackbarToast(toastPane
                    , "Success! You converted the Docx file to a Html format file");

//            deleteSourceFile(success, in);
        }
    }

    /**
     * {@code HtmlToDocx.class} Converts a html file to a  Docx format file
     */
    static class HtmlToDocx implements Convertible {

        private final File in;
        private final File out;
        private static Boolean success = true;

        public HtmlToDocx(File in, File out) {
            this.in = in;
            this.out = out;

        }
//    TODO

        @Override
        public void convert() {
            log.info("convert() -- running -- From: " + in.getName() + " To-> " + out.getName());

            DialogHelper.snackbarToast(toastPane
                    , "Success! You converted the html file to a Docx file format file");

//            deleteSourceFile(success, in);
        }
    }

    /**
     * {@code ImageConvert.class} is for programmatically converting between image file types.
     * <em>Special case required for converting from a PNG to a JPG</em> {@link PngToJpg}
     */
    static class ImageConvert implements Convertible {

        private final File in;
        private final File out;
        private final String format;
        private static Logger log = LoggerFactory.getLogger(ImageConvert.class);

        public ImageConvert(File in, File out, String format) {
            this.in = in;
            System.out.println("File in: " + in.toString());
            this.out = out;
            System.out.println("File out: " + out.toString());
            this.format = format;
            System.out.println("Format: " + format);
        }

        private boolean genericConversion() {
            boolean result = true;
            BufferedImage bufferedImage;

            try (FileInputStream fis = new FileInputStream(in)) {
                bufferedImage = ImageIO.read(fis);
                try (FileOutputStream fos = new FileOutputStream(out)) {
                    ImageIO.write(bufferedImage, format, out);

                } catch (IllegalArgumentException iae) {
                    DialogHelper.showErrorAlert("We could not complete the conversion because One or more required fields is null.");
                    iae.printStackTrace();
                    result = false;

                } catch (IOException ie) {
                    DialogHelper.showErrorAlert("Something went wrong reading or writing the files,\n we could not complete the conversion.");
                    log.error(ie.getCause() + " happened while writing image file: " + out.getName());
                    ie.printStackTrace();
                    result = false;
                }

            } catch (IOException e) {
                log.error("Image read failed on " + in.getName());
                DialogHelper.showErrorAlert("Check you have permission to read this file. Failed to read image from input: " + in.getName());
                e.printStackTrace();
                result = false;

            }

            return result;
        }

        @Override
        public void convert() {
            log.info("convert() -- running -- From: " + in.getName() + " To-> " + out.getName());
            boolean succeeded = genericConversion();
            if (succeeded) {
                deleteSourceFile(true, in);
                DialogHelper.showInfoAlert("Success! Your image was converted. To view it, click on the link to your output directory", false);
            } else {
                DialogHelper.showErrorAlert("Something went wrong converting the image, please ensure it is a supported format and try again.");
            }
        }
    }

    /**
     * {@code PngToJpg.class} is for programmatically converting from png to jpg image format type.
     * <em>This case requires manually removing the alpha channel before rendering the image as
     * a JPEG</em>
     */
    static class PngToJpg implements Convertible {

        private final File in;
        private final File out;
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
                e.printStackTrace();
                success = false;
            }
            if (bufferedImage != null) {

                BufferedImage imageRGB = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.OPAQUE);
                Graphics2D graphics = imageRGB.createGraphics();
                graphics.drawImage(bufferedImage, 0, 0, null);
                try {
                    ImageIO.write(imageRGB, "JPG", out);
                    DialogHelper.showInfoAlert("Successfully converted " + FilenameUtils.getName(in.toString() + " to " +
                            FilenameUtils.getName(out.toString())), false);
                } catch (IOException e) {
                    log.error(e.getCause() + " happened while writing the JPG file");
                    success = false;
                    e.printStackTrace();
                }
                graphics.dispose();
            } else {

                success = false;
            }
            deleteSourceFile(success, in);
        }

    }

    public static void setDeleteSourceAfterConverted(Boolean deleteSourceAfterConverted) {
        ConvertUtils.deleteSourceAfterConverted = deleteSourceAfterConverted;
    }
    static boolean checkUnderlyingOperSys(String os){
        String osLCStr = os.toLowerCase();
        return osLCStr.contains("windows");

    }
}