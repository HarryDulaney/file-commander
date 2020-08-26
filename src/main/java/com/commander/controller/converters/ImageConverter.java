package com.commander.controller.converters;

import com.commander.utils.DialogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * {@code ImageConverter.class} is for programmatically converting between image file types.
 * <em>Special case required for converting from a PNG to a JPG</em> {@link PngConversions}
 */
public class ImageConverter extends Converter {

    private String format;
    static Logger log = LoggerFactory.getLogger(ImageConverter.class);
    private BufferedImage sourceImage;

    public ImageConverter(File fileIn, File fileOut, String format) {
        super(fileIn, fileOut);
        this.format = format;
    }

    private boolean genericConversion() {
        boolean result;

        try {
            final long starttime = System.currentTimeMillis();

            sourceImage = ImageIO.read(in);
            if (sourceImage.getTransparency() != BufferedImage.OPAQUE) {
                try {
                    result = ImageIO.write(sourceImage, format, out);
                    if (!result) {
                        throw new Exception("Conversion with transparent pixels failed, now attempting conversion with" +
                                " user default color replacing transparent pixels...");
                    }
                } catch (Exception exp) {
                    log.info(exp.getMessage());
                    result = handleTransBg(sourceImage, format, out);
                    if (result) {
                        log.info("The second conversion attempt was successful!");
                    }
                }
            } else {
                result = ImageIO.write(sourceImage, format, out);
            }


            log.info("Converted --> from: " + in.getName() + " to -> " + out.getName() + " in " + ((System.currentTimeMillis() - starttime) + " ms."));

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
        boolean succeeded = genericConversion();
        if (succeeded) {
            deleteSourceFile(true, in);
            DialogHelper.showInfoAlert("Success! Your image named: " + in.getName() + " was converted to: " + out.getName() +
                    ",\nview it by clicking on the link to your output directory", false);


        } else {
            DialogHelper.showErrorAlert("Something went wrong converting the image, please ensure it is a supported format and try again.");
        }
    }

    static boolean handleTransBg(BufferedImage srcImage, String imgFormat, File out) throws IOException {
        BufferedImage modImage = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphic = modImage.createGraphics();
        graphic.drawImage(srcImage, 0, 0, new java.awt.Color((float) bgColor.getRed(), (float) bgColor.getGreen(), (float) bgColor.getBlue()), null); //Set the bgColor per user preference
        boolean res = ImageIO.write(modImage, imgFormat, out);
        graphic.dispose();

        return res;
    }
}