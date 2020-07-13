package com.commander.utils;

import com.commander.model.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.commons.io.FilenameUtils.EXTENSION_SEPARATOR_STR;

import static com.commander.utils.ConvertUtils.*;

/**
 * {@code ConvertibleFactory} is an utility class for creating {@code Convertible} objects
 * to be passed on to ConvertUtils for conversions.
 *
 * @author HGDIV
 */
public class ConvertibleFactory {

    static Logger log = LoggerFactory.getLogger(ConvertibleFactory.class);

    private ConvertibleFactory() {
    }


    public static Convertible createCsvToXlsx(String fileName, String directoryPath, String writeDirectoryPath) {
        String numRows = DialogHelper.showInputPrompt("Rows Per Sheet?", "How many rows would you like each sheet to have in your .XLSX workbook?\nPlease enter an integer number (e.g. 300)", "Info Request");
        Integer rowsPerSheet = Integer.parseInt(numRows);
        String baseName = FilenameUtils.removeExtension(fileName);

        return new csvToXlsx(configReadPath(directoryPath, fileName), configWritePath(baseName, writeDirectoryPath, ExcelType.xlsx()),rowsPerSheet);
    }


    public static Convertible createXlsxToCsv(String fileName, String directoryPath, String writeDirectoryPath) {

        String baseName = FilenameUtils.removeExtension(fileName);

        return new xlsxToCsv(configReadPath(directoryPath, fileName), configWritePath(baseName, writeDirectoryPath, ExcelType.csv()));
    }


    public static Convertible createDocxToPdf(String fileName, String directoryPath, String writeDirectoryPath) {

        String name = FilenameUtils.removeExtension(fileName);

        return new docxToPdf(configReadPath(directoryPath, fileName), configWritePath(name, writeDirectoryPath, DocType.pdf()));
    }


    public static Convertible createPdfToDocx(String fileName, String directoryPath, String writeDirectoryPath) {

        String name = FilenameUtils.removeExtension(fileName);

        return new pdfToDocx(configReadPath(directoryPath, fileName), configWritePath(name, writeDirectoryPath, DocType.docx()));
    }

    public static Convertible createImageConvert(String fileName, String directoryPath, String writeDirPath, ImgType userPref) {

        String justName = FilenameUtils.removeExtension(fileName);
        String inputExt = EXTENSION_SEPARATOR_STR.concat(FilenameUtils.getExtension(fileName));
        String userPrefExt = userPref.getExtension();
        log.info("Creating Image Convert: From: " + inputExt + " To-> " + userPrefExt);

        if (inputExt.equals(ImgType.png()) && userPref.getExtension().equals(ImgType.jpg())) {

            return new PngToJpg(configReadPath(directoryPath, fileName), configWritePath(justName, writeDirPath, ImgType.jpg()));

        } else {
            return new ImageConvert(configReadPath(directoryPath, fileName), configWritePath(justName, writeDirPath, userPrefExt), userPrefExt);

        }

    }


    private static File configReadPath(String directoryPath, String fileName) {
        Path dir = Paths.get(directoryPath);
        Path resolvedPath = dir.resolve(fileName);
        return resolvedPath.toFile();

    }

    private static File configWritePath(String baseName, String writeDirectory, final String EXT) {
        Path writePath = Paths.get(writeDirectory);
        Path resolvedPath = writePath.resolve(baseName + EXT);
        return resolvedPath.toFile();
    }
}