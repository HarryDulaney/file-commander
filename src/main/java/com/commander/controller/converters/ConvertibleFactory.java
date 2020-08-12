package com.commander.controller.converters;

import com.commander.model.*;
import com.commander.utils.DialogHelper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.io.FilenameUtils.EXTENSION_SEPARATOR_STR;

/**
 * {@code ConvertibleFactory} is an utility class for creating {@code Convertible} objects
 * to be passed on to ConvertUtils for conversions.
 *
 * @author HGDIV
 */
public class ConvertibleFactory {

    /**
     * The Log.
     */
    static Logger log = LoggerFactory.getLogger(ConvertibleFactory.class);

    private ConvertibleFactory() {
        throw new UnsupportedOperationException("ConvertibleFactory is not meant to be instantiated.");
    }

/************************************************/
/*   MSWord File Format Convertible builders    *
/*************************************************/

    /**
     * Build new docx to pdf convertible.
     *
     * @param fileName           absolute filename.ext from src file
     * @param directoryPath      the directory path
     * @param writeDirectoryPath the write directory path
     * @return the convertible
     */
    public static Convertible createDocxToPdf(String fileName, String directoryPath, String writeDirectoryPath) throws FileAlreadyExistsException {

        String name = FilenameUtils.removeExtension(fileName);

        return new DocxToPdf(configReadPath(directoryPath, fileName), configWritePath(name, writeDirectoryPath, DocType.pdf()));
    }

    /**
     * Build new PDF EXTRACT TEXT TO DOCX Convertible.
     *
     * @param fileName           source file filename.ext
     * @param directoryPath      User's src file path
     * @param writeDirectoryPath User's write dir path
     * @return the convertible
     */
    public static Convertible createPdfToDocx(String fileName, String directoryPath, String writeDirectoryPath) throws FileAlreadyExistsException {

        String name = FilenameUtils.removeExtension(fileName);

        return new PdfTextToDocx(configReadPath(directoryPath, fileName), configWritePath(name, writeDirectoryPath, DocType.docx()));
    }

    /**
     * Build new PDF TO DOCX CLONE convertible.
     *
     * @param fileName           source file filename.ext
     * @param directoryPath      User's src file path
     * @param writeDirectoryPath User's write dir path
     * @return the convertible
     */
    public static Convertible createClonePDFtoDOCX(String fileName, String directoryPath, String writeDirectoryPath) throws FileAlreadyExistsException {

        String name = FilenameUtils.removeExtension(fileName);

        return new PdfToDocx(configReadPath(directoryPath, fileName), configWritePath(name, writeDirectoryPath, DocType.docx()));
    }





    /**
     * @param fileName
     * @param directoryPath
     * @param writeDirectoryPath
     * @return new Convertible
     */
    public static Convertible createDocx2HTML(String fileName, String directoryPath, String writeDirectoryPath) {
        return new DocxToHtml(null, null);//TODO
    }


/************************************************/
/*   Excel File Format Convertible Builders      *
/*************************************************/

    /**
     * Build new csv to xlsx convertible.
     *
     * @param fileName           source file filename.ext
     * @param directoryPath      User's src file path
     * @param writeDirectoryPath User's write dir path
     * @return the convertible
     */
    public static Convertible createCsvToXlsx(String fileName, String directoryPath, String writeDirectoryPath) throws FileAlreadyExistsException {
        String numRows = DialogHelper.showInputPrompt("Rows Per Sheet?", "How many rows would you like each sheet to have in your .XLSX workbook?\nPlease enter an integer number (e.g. 300)", "Info Request");
        Integer rowsPerSheet = Integer.parseInt(numRows);
        String baseName = FilenameUtils.removeExtension(fileName);

        return new CsvToXlsx(configReadPath(directoryPath, fileName), configWritePath(baseName, writeDirectoryPath, ExcelType.xlsx()), rowsPerSheet);
    }


    /**
     * Build new xlsx to csv convertible.
     *
     * @param fileName           source file filename.ext
     * @param directoryPath      User's src file path
     * @param writeDirectoryPath User's write dir path
     * @return the convertible
     */
    public static Convertible createXlsxToCsv(String fileName, String directoryPath, String writeDirectoryPath) throws FileAlreadyExistsException {

        String baseName = FilenameUtils.removeExtension(fileName);

        return new XlsxToCsv(configReadPath(directoryPath, fileName), configWritePath(baseName, writeDirectoryPath, ExcelType.csv()));
    }


/************************************************/
/*   Image File Format Convertible Builders      *
/*************************************************/


    /**
     * Build new {@code ConvertUtils.ImageConvert} convertible.
     *
     * @param fileName      source file filename.ext
     * @param directoryPath User's src file path
     * @param writeDirPath  User's write dir path
     * @param userPref      User's preference for this file format
     * @return the convertible
     */
    public static Convertible createImageConvert(String fileName, String directoryPath, String writeDirPath, ImgType userPref) throws FileAlreadyExistsException {

        String justName = FilenameUtils.removeExtension(fileName);
        String inputExt = EXTENSION_SEPARATOR_STR.concat(FilenameUtils.getExtension(fileName));
        String outputExt = userPref.getExtension();
        String userPrefExt = outputExt.replace(".", "");

        log.info("Creating Image Convert: From: " + inputExt + " To -> " + outputExt);

        if (inputExt.equals(DocType.png()) && userPref.getExtension().equals(DocType.jpg())) {

            return new PngToJpg(configReadPath(directoryPath, fileName), configWritePath(justName, writeDirPath, DocType.jpg()));

        } else {
            return new ImageConverter(configReadPath(directoryPath, fileName), configWritePath(justName, writeDirPath, outputExt), userPrefExt);
        }

    }


    private static File configReadPath(String directoryPath, String fileName) {
        Path dir = Paths.get(directoryPath);
        Path resolvedPath = dir.resolve(fileName);
        return resolvedPath.toFile();

    }

    private static File configWritePath(String baseName, String writeDirectory, String EXT) throws FileAlreadyExistsException {
        Path writePath = Paths.get(writeDirectory);
        Path resolvedPath = writePath.resolve(baseName + EXT);
        // Check to avoid overwrite
        if (resolvedPath.toFile().exists()) {
            String version = baseName + "-" + 1 + EXT;
            resolvedPath = writePath.resolve(version);
            if (resolvedPath.toFile().exists()) {
              throw new FileAlreadyExistsException("Too many copies of this file already exist in the output directory");
            }
        }
        return resolvedPath.toFile();
    }

}