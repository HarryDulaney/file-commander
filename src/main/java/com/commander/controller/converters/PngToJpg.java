package com.commander.controller.converters;


import com.commander.utils.DialogHelper;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * {@code PngToJpg.class} is for programmatically converting from png to jpg image format type.
 * <em>This case requires manually removing the alpha channel before rendering the image as
 * a JPEG</em>
 */
public class PngToJpg extends Converter{

    private static Boolean success = true;


    public PngToJpg(File fileIn, File fileOut) {
        super(fileIn, fileOut);
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
        if (bufferedImage != null) {

            BufferedImage imageRGB = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.OPAQUE);
            Graphics2D graphics = imageRGB.createGraphics();
            graphics.drawImage(bufferedImage, 0, 0, null);
            try {
                ImageIO.write(imageRGB, "jpg", out);
                DialogHelper.showInfoAlert("Successfully converted " + FilenameUtils.getName(in.toString() + " to " +
                        FilenameUtils.getName(out.toString())), false);
            } catch (IOException e) {
                log.error(e.getCause() + " happened while writing the JPG file");
                success = false;
                e.printStackTrace();
            }
            graphics.dispose();
        } else {

            success = false;
        }
        if (success) {
            DialogHelper.showInfoAlert("Success! Your file named: " + in.getName() + " was converted to: " + out.getName() + ",\nview it by clicking on the link to your output directory", false);
        }
        deleteSourceFile(success, in);
    }

}
