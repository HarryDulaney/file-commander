package com.commander.controller.converters;

import com.commander.utils.DialogHelper;

import java.io.File;

public class DocxToHtml extends Converter {


    private static Boolean success = false;

    public DocxToHtml(File in, File out) {
      super(in,out);

    }


    @Override
    public void convert() {
        long starttime = System.currentTimeMillis();
        if (success) {
            log.info("Converted --> from: " + in.getName() + " to -> " + out.getName() + " in " + ((System.currentTimeMillis() - starttime) + " ms."));

        }



//            deleteSourceFile(success, in);
    }
}
