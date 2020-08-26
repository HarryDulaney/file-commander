package com.commander.controller.converters;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class AbstractImageConverter extends Converter {
    /**
     * Instantiates a new Converter.
     *
     * @param in  the in
     * @param out the out
     */
    protected AbstractImageConverter(File in, File out) {
        super(in, out);
    }

    static boolean handleTransBg(BufferedImage srcImage, String imgFormat, File out) throws IOException, IllegalArgumentException {
        BufferedImage modImage = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphic = modImage.createGraphics();
        graphic.drawImage(srcImage, 0, 0, new java.awt.Color((float) bgColor.getRed(), (float) bgColor.getGreen(), (float) bgColor.getBlue()), null); //Set the bgColor per user preference
        boolean res = ImageIO.write(modImage, imgFormat, out);
        graphic.dispose();

        return res;
    }

}
