package com.commander.controller.converters;

import com.commander.utils.DialogHelper;
import org.apache.commons.compress.PasswordRequiredException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * {@code pdfToDocx.class} Extract the text content from the pdf and write to docx
 */
public class PdfTextToDocx extends Converter {


    private static Boolean success = true;

    public PdfTextToDocx(File in, File out) {
        super(in, out);

    }


    @Override
    public void convert() {
        final long starttime = System.currentTimeMillis();
        PDDocument pdf;
        try {
            pdf = PDDocument.load(in);
//            int numPages = pdf.getNumberOfPages();
            try {
                AccessPermission ap = pdf.getCurrentAccessPermission();
                if (!ap.canExtractContent()) {
                    throw new PasswordRequiredException("This pdf is password protected or has restricted access permissions and cannot be read");
                }
                PDFTextStripper textStripper = new PDFTextStripper();
                String pdfText = textStripper.getText(pdf);

                // Handle MSWord docx file creation
                XWPFDocument wordDoc = new XWPFDocument();
                XWPFParagraph p = wordDoc.createParagraph();
                XWPFRun run = p.createRun();
                run.setText(pdfText);
                run.addBreak(BreakType.PAGE);

                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out))) {

                    wordDoc.write(bos);

                    wordDoc.close();
                    log.info("Converted --> from: " + in.getName() + " to -> " + out.getName() + " in " + ((System.currentTimeMillis() - starttime) + " ms."));


                } catch (IOException e) {
                    success = false;
                    e.printStackTrace();
                }


            } catch (PasswordRequiredException pre) {
                DialogHelper.showErrorAlert("Document has protected access, please remove password protection or change access permissions to allow " +
                        "the pdf to be read");
                log.error("Error reading pdf, password protected or access permission restricted", pre.getCause());
                return;
            }


        } catch (IOException ioe) {
            log.error("Error while extracting text from pdf document in PdfTextToDocx.class", ioe.getCause());
            success = false;
            ioe.printStackTrace();
        }


        if (!success) {
            DialogHelper.showInfoAlert("Something went wrong and we were unable to complete the operation", false);
        } else {
            DialogHelper.showInfoAlert("Success! Your file named: " + in.getName() + " had it's text content extracted and was saved as: " + out.getName() + "," +
                    "\nView it by clicking on the link to your output directory", false);
            deleteSourceFile(true, in);

        }
    }
}
