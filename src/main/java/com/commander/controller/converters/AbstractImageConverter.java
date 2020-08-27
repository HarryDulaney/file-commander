package com.commander.controller.converters;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * {@code AbstractImageConverter} abstracts the logic for most common
 * image conversions. It is parent class to {@link ImageConverter} and also
 * {@link PngConversions}
 *
 *
 */
public abstract class AbstractImageConverter extends Converter {

    /**
     * The Format.
     */
    protected String format;

    /**
     * Instantiates a new Converter.
     *
     * @param in  the in
     * @param out the out
     */
    protected AbstractImageConverter(File in, File out) {
        super(in, out);
    }

    /**
     * Instantiates a new Abstract image converter.
     *
     * @param in     the in
     * @param out    the out
     * @param format the format
     */
    protected AbstractImageConverter(File in, File out, String format) {
        super(in, out);
        this.format = format;
    }


    /**
     * Handle trans bg boolean.
     *
     * @param srcImage  the src image
     * @param imgFormat the img format
     * @param out       the out
     * @return the boolean
     * @throws IOException              the io exception
     * @throws IllegalArgumentException the illegal argument exception
     */
    static boolean handleTransBg(BufferedImage srcImage, String imgFormat, File out) throws IOException, IllegalArgumentException {
        BufferedImage modImage = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphic = modImage.createGraphics();
        graphic.drawImage(srcImage, 0, 0, new java.awt.Color((float) bgColor.getRed(), (float) bgColor.getGreen(), (float) bgColor.getBlue()), null); //Set the bgColor per user preference
        boolean res = ImageIO.write(modImage, imgFormat, out);
        graphic.dispose();

        return res;
    }

//    /**
//     * Handle trans support boolean.
//     *
//     * @param srcImage  the src image
//     * @param imgFormat the img format
//     * @param out       the out
//     * @return the boolean
//     * @throws IOException              the io exception
//     * @throws IllegalArgumentException the illegal argument exception
//     */
//    static boolean handleTransSupport(BufferedImage srcImage, String imgFormat, File out) throws IOException, IllegalArgumentException {
//        log.info("Attempting manual conversion with alpha channel (transparent background)");
//        java.awt.Color transparency = new java.awt.Color(0,0,0,0);
//
//        graphic.dispose();
//
//        return res;
//    }

    /**
     * Handle simple img convert boolean.
     *
     * @param src       the src
     * @param srcFormat the src format
     * @param out       the out
     * @return the boolean
     * @throws IOException              the io exception
     * @throws IllegalArgumentException the illegal argument exception
     */
    static boolean handleSimpleImgConvert(BufferedImage src, String srcFormat, File out) throws IOException, IllegalArgumentException {
        return ImageIO.write(src, srcFormat, out);
    }

}
