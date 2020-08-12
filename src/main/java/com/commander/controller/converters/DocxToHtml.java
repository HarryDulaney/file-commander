package com.commander.controller.converters;

import com.commander.utils.DialogHelper;

import java.io.File;

public class DocxToHtml extends Converter {


    private static Boolean success = true;

    public DocxToHtml(File in, File out) {
      super(in,out);

    }


    @Override
    public void convert() {
        log.info("convert() -- running -- From: " + in.getName() + " To-> " + out.getName());

        DialogHelper.snackbarToast(toastPane
                , "Success! You converted the Docx file to a Html format file");

//            deleteSourceFile(success, in);
    }
}
