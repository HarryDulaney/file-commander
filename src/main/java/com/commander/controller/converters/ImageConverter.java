package com.commander.controller.converters;

import com.commander.utils.DialogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * {@code ImageConverter.class} is for programmatically converting between image file types.
 * <em>Special case required for converting from a PNG to a JPG</em> {@link PngToJpg}
 */
public class ImageConverter extends Converter {

    private String format;
    final static Logger log = LoggerFactory.getLogger(ImageConverter.class);

    public ImageConverter(File fileIn, File fileOut, String format) {
        super(fileIn, fileOut);
        this.format = format;
    }

    private boolean genericConversion() {
        boolean result = true;
        BufferedImage bufferedImage;

        try {
            bufferedImage = ImageIO.read(in);
            try {
                ImageIO.write(bufferedImage, format, out);

            } catch (IllegalArgumentException iae) {
                DialogHelper.showErrorAlert("We could not complete the conversion because One or more required fields is null.");
                iae.printStackTrace();
                result = false;

            } catch (IOException ie) {
                DialogHelper.showErrorAlert("Image WRITE failed on " + out.getName() + ",\n we could not complete the conversion.");
                log.error(ie.getCause() + " happened while writing image file: " + out.getName());
                ie.printStackTrace();
                result = false;
            }

        } catch (IOException e) {
            log.error("Image READ failed on " + in.getName());
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
            DialogHelper.showInfoAlert("Success! Your image named: "+ in.getName() + " was converted to: " + out.getName() +",\nview it by clicking on the link to your output directory", false);
        } else {
            DialogHelper.showErrorAlert("Something went wrong converting the image, please ensure it is a supported format and try again.");
        }
    }
}