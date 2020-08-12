package com.commander.controller.converters;

import com.commander.model.Convertible;
import com.commander.utils.DialogHelper;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * {@code DocxToPdf.class} programmatically converts docx to pdf file format.
 */
public class DocxToPdf extends Converter {

    private static Boolean success = true;


    public DocxToPdf(File in, File out) {
        super(in, out);
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

        } catch (IOException | InvalidFormatException e) {
            DialogHelper.showErrorAlert("Something went wrong, we were unable to convert you document.\nPlease ensure the output folder is write enabled");
            e.printStackTrace();
            success = false;
        }
        if (success) {
            DialogHelper.showInfoAlert("Success! Your file named: " + in.getName() + " was converted to: " + out.getName() + ",\nview it by clicking on the link to your output directory", false);

        }
        deleteSourceFile(success, in);
    }
}
