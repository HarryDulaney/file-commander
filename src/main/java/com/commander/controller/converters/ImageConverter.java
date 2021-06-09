package com.commander.controller.converters;

import com.commander.model.DocType;
import com.commander.utils.DialogHelper;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * {@code ImageConverter } is for programmatically converting between image file types.
 */
public class ImageConverter extends AbstractImageConverter {

    private String format;
    static Logger log = LoggerFactory.getLogger(ImageConverter.class);

    /**
     * Instantiates a new Converter.
     *
     * @param fileIn  the in
     * @param fileOut the out
     * @param format  the output format
     */
    public ImageConverter(File fileIn, File fileOut, String format) {
        super(fileIn, fileOut);
        this.format = format;
    }

    private boolean genericConversion() {
        boolean result;
        try {
            final long starttime = System.currentTimeMillis();

            BufferedImage sourceImage = ImageIO.read(in);
            if (sourceImage.getTransparency() != BufferedImage.OPAQUE) {
                if ((FilenameUtils.getExtension(in.getName())).equals(DocType.GIF_ID) && !(format.equalsIgnoreCase(DocType.PNG_ID))) {
                    result = handleTransBg(sourceImage, format, out);

                } else {
                    try {
                        result = ImageIO.write(sourceImage, format, out);
                        if (!result) {
                            throw new Exception();
                        }
                    } catch (Exception exp) {
                        log.info("Conversion with transparent pixels failed, now attempting conversion with" +
                                " user default color replacing transparent pixels...");
                        result = handleTransBg(sourceImage, format, out);
                        if (result) {
                            log.info("The second conversion attempt was successful!");
                            log.info("Converted --> from: " + in.getName() + " to -> " + out.getName() + " in " + ((System.currentTimeMillis() - starttime) + " ms."));

                        }
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

}