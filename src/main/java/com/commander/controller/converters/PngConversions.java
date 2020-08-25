package com.commander.controller.converters;


import com.commander.utils.DialogHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * {@code PngToJpgBmpGif.class} is for programmatically converting from png to the other three
 * image format types.
 * <p>
 * Because Png image files often have a transparent background
 * <em>This case requires manually removing the alpha channel before rendering the image as
 * another image format type.</em> //TODO: add Dialog popup to prompt user for background color choice when converting transparent PNG images to
 * unsupported formats (for transparency).
 *
 * @author Harry Dulaney
 */
public class PngConversions extends Converter {

    private static Boolean success = false;
    private final String format;


    public PngConversions(File fileIn, File fileOut, String format) {
        super(fileIn, fileOut);
        this.format = format;
    }

    @Override
    public void convert() {
        final long starttime = System.currentTimeMillis();
        try {
            BufferedImage bufferedImage = ImageIO.read(in);
            BufferedImage imageRGB = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = imageRGB.createGraphics();
            graphics.drawImage(bufferedImage, 0, 0, new java.awt.Color((float) bgColor.getRed(), (float) bgColor.getGreen(), (float) bgColor.getBlue()), null); //Set the background color per user preference
            success = ImageIO.write(imageRGB, format, out);
            graphics.dispose();
            log.info("Converted --> from: " + in.getName() + " to -> " + out.getName() + " in " + ((System.currentTimeMillis() - starttime) + " ms. "));

        } catch (IOException ioe) {
            log.error("IOException occurred while converting png.", ioe.getCause());
            ioe.printStackTrace();
        }
        if (success) {
            DialogHelper.showInfoAlert("Success! Your file named: " + in.getName() + " was converted to: " + out.getName() +
                    ",\nview it by clicking on the link to your output directory", false);

        } else {
            DialogHelper.showErrorAlert("Something went wrong writing the image.");
        }

        deleteSourceFile(success, in);


    }
}

