package com.commander.controller.converters;


import com.commander.model.DocType;
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
 * another image format type.</em>
 *
 * @author Harry Dulaney
 */
public class PngToJpgBmpGif extends Converter {

    private static Boolean success = true;
    private String format;


    public PngToJpgBmpGif(File fileIn, File fileOut, String format) {
        super(fileIn, fileOut);
        this.format = format;
    }

    @Override
    public void convert() {
        log.info("convert() -- running -- From: " + in.getName() + " To -> " + out.getName());
        if (format.equals(DocType.BMP_ID)) {
            try {

                BufferedImage bufferedImage = ImageIO.read(in);
                success = ImageIO.write(bufferedImage, DocType.BMP_ID, out);

            } catch (IOException ioException) {
                ioException.printStackTrace();
                DialogHelper.showErrorAlert("Error reading the image, conversion was unsuccessful");
            }

        } else if (format.equals(DocType.JPG_ID)) {
            try {
                BufferedImage bufferedImage = ImageIO.read(in);

                BufferedImage imageRGB = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.OPAQUE);
                Graphics2D graphics = imageRGB.createGraphics();
                graphics.drawImage(bufferedImage, 0, 0, null);
                success = ImageIO.write(imageRGB, DocType.JPG_ID, out);
                graphics.dispose();

            } catch (IOException e) {
                log.error(e.getCause() + " happened while writing this PNG to the new image format");
                e.printStackTrace();
            }


        } else {
            try {
                BufferedImage bufferedImage = ImageIO.read(in);

                BufferedImage imageRGB = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getTransparency());
                Graphics2D graphics = imageRGB.createGraphics();
                graphics.drawImage(bufferedImage, 0, 0, null);
                success = ImageIO.write(imageRGB, format, out);
                graphics.dispose();

            } catch (IOException e) {
                log.error(e.getCause() + " happened while writing this PNG to the new image format");
                e.printStackTrace();
            }
        }


        if (success) {
            DialogHelper.showInfoAlert("Success! Your file named: " + in.getName() + " was converted to: " + out.getName() + ",\nview it by clicking on the link to your output directory", false);
        } else {
            DialogHelper.showErrorAlert("Something went wrong writing the image.");
        }

        deleteSourceFile(success, in);


    }
}

