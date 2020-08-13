package com.commander.controller.converters;


import com.commander.utils.DialogHelper;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * {@code PngToJpgBmpGif.class} is for programmatically converting from png to the other three
 * image format types.
 * <em>This case requires manually removing the alpha channel before rendering the image as
 * another image format type.</em>
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
        log.info("convert() -- running -- From: " + in.getName() + " To-> " + out.getName());
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        if (success) {
            BufferedImage imageRGB = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.OPAQUE);
            Graphics2D graphics = imageRGB.createGraphics();
            graphics.drawImage(bufferedImage, 0, 0, null);
            try {
                ImageIO.write(imageRGB, format, out);

            } catch (IOException e) {
                log.error(e.getCause() + " happened while writing this PNG to the new image format");
                success = false;
                e.printStackTrace();
            }
            graphics.dispose();
        }

        if (success) {
            DialogHelper.showInfoAlert("Success! Your file named: " + in.getName() + " was converted to: " + out.getName() + ",\nview it by clicking on the link to your output directory", false);
        }
        deleteSourceFile(success, in);
    }

}
