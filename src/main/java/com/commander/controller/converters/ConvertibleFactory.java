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
 * {@code ConvertibleFactory} is an utility class for creating {@code Convertible} objects.
 *
 * @author Harry Dulaney
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

        return new CsvToXlsx(configReadPath(directoryPath, fileName), configWritePath(baseName, writeDirectoryPath, DocType.xlsx()), rowsPerSheet);
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

        return new XlsxToCsv(configReadPath(directoryPath, fileName), configWritePath(baseName, writeDirectoryPath, DocType.csv()));
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
     * @param imgPref       User's preference for this file format
     * @return the convertible
     */
    public static Convertible createImageConvert(String fileName, String directoryPath, String writeDirPath, DocType imgPref) throws FileAlreadyExistsException {

        String justName = FilenameUtils.removeExtension(fileName);
        String startExtension = EXTENSION_SEPARATOR_STR.concat(FilenameUtils.getExtension(fileName));
        String targetExtension = imgPref.getExtension();
        String targetID = imgPref.getId();
        targetID = targetID.toUpperCase();

        log.info("Creating Image Convert: From: " + startExtension + " To -> " + targetExtension);

        return new ImageConverter(configReadPath(directoryPath, fileName), configWritePath(justName, writeDirPath, targetExtension), targetID);
    }

    /**
     * Build new {@code PngToJpgBmpGif.class} convertible.
     *
     * @param fileName      source file filename.ext
     * @param directoryPath User's src file path
     * @param writeDirPath  User's write dir path
     * @param imgPref       User's preference for this file format
     * @return the convertible
     */
    public static Convertible createPngConvert(String fileName, String directoryPath, String writeDirPath, DocType imgPref) throws FileAlreadyExistsException {

        String name = FilenameUtils.removeExtension(fileName);
        String inputExtension = EXTENSION_SEPARATOR_STR.concat(FilenameUtils.getExtension(fileName));
        String targetExtension = imgPref.getExtension();
        String formatId = imgPref.getId();
        formatId = formatId.toUpperCase();

        log.info("Creating PngConvert: From: " + inputExtension + " To -> " + targetExtension);


        return new PngConversions(configReadPath(directoryPath, fileName), configWritePath(name, writeDirPath, targetExtension), formatId);

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