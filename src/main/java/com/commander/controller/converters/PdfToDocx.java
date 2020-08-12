package com.commander.controller.converters;

import com.commander.model.Convertible;
import com.commander.utils.DialogHelper;
import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * {@code pdfToDocx.class } Converts the whole PDF to a DOCX file formatted Word Doc
 * This conversion calls the underlying Microsoft Office installation,using documents4j, to convent the PDF to a Word formatted file -> .docx.
 * <p>
 * For this conversion to be successful, all other instances of MS Word must be closed on the local machine.
 */
public class PdfToDocx extends Converter {

    private static Boolean success = false;


    public PdfToDocx(File in, File out) {
        super(in, out);

    }

    @Override
    public void convert() {
        log.info("convert() -- running -- From: " + in.getName() + " To-> " + out.getName());
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
            DialogHelper.showInfoAlert("Success! We created a Word Document named " +
                    FilenameUtils.getName(in.toString()) + " from " + FilenameUtils.getName(in.toString()), false);
        } else {
            DialogHelper.snackbarToast(toastPane, "Something went wrong, we were not able to create a new docx word document " +
                    "from " + FilenameUtils.getName(in.toString()));
        }

    }
}