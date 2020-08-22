package com.commander.controller.converters;

import java.io.File;

public class HtmlToDocx extends Converter{
    protected HtmlToDocx(File in, File out) {
        super(in, out);
    }

    @Override
    public void convert() {
        final long starttime = System.currentTimeMillis();

        log.info("Converted --> from: " + in.getName() + " to -> " + out.getName() + " in " + ((System.currentTimeMillis() - starttime) + " ms. "));

    }
}
