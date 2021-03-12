package com.commander.controller.converters;


import com.commander.model.DocType;
import com.commander.utils.DialogHelper;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * {@code PngConversions.class} is for programmatically converting from png to the other three
 * image format types.
 * <p>
 * Because Png image files often have a transparent background
 * <em>This case requires manually removing the alpha channel before rendering the image as
 * another image format type.</em>
 *
 * @author Harry Dulaney
 */
public class PngConversions extends AbstractImageConverter {

    private static Boolean success = false;
    private final String format;


    public PngConversions(File fileIn, File fileOut, String format) {
        super(fileIn, fileOut);
        this.format = format;
    }

    @Override
    public void convert() {
        // TODO: USE THE PNGDECODER to rework these methods
        final long starttime = System.currentTimeMillis();
        /* Handle PNG to JPG*/
        if (format.equalsIgnoreCase(DocType.JPG_ID)) {
            try {
                BufferedImage bufferedImage = ImageIO.read(in);
                success = handleTransBg(bufferedImage, format, out);

                log.info("Converted --> from: " + in.getName() + " to -> " + out.getName() + " in " + ((System.currentTimeMillis() - starttime) + " ms. "));

            } catch (IOException | IllegalArgumentException ioe) {
                log.error("Exception occurred while converting png.", ioe.getCause());
                ioe.printStackTrace();
            }
            /* Handle PNG to BMP */
        } else if (format.equalsIgnoreCase(DocType.BMP_ID)) {
            try {
                BufferedImage bufferedImage = ImageIO.read(in);
                if (bufferedImage.getTransparency() != BufferedImage.OPAQUE) {
                    try {
                        success = handleSimpleImgConvert(bufferedImage, format, out);
                        if (!success) {
                            throw new Exception();
                        }
                        log.info("Converted --> from: " + in.getName() + " to -> " + out.getName() + " in " + ((System.currentTimeMillis() - starttime) + " ms."));

                    } catch (Exception ex1) {
                        log.info("Png conversion with transparent pixels failed, now attempting conversion with" +
                                " user default color replacing transparent pixels...");
                        ex1.printStackTrace();
                        boolean result = handleTransBg(bufferedImage, format, out);
                        if (result) {
                            success = true;
                            log.info("The second conversion attempt was successful!");
                            log.info("Converted --> from: " + in.getName() + " to -> " + out.getName() + " in " + ((System.currentTimeMillis() - starttime) + " ms."));

                        }

                    }

                } else {
                    success = handleSimpleImgConvert(bufferedImage, format, out);
                }


            } catch (IOException | IllegalArgumentException e) {
                log.error("Error converting: " + in.getName() + " to -> " + out.getName());
                e.printStackTrace();
            }
            /* Handle PNG to GIF*/
        } else {
            try {
                BufferedImage bufferedImage = ImageIO.read(in);
                try {
                    success = handleSimpleImgConvert(bufferedImage, format, out);
                    if (!success) {
                        throw new Exception();
                    }
                    log.info("Converted --> from: " + in.getName() + " to -> " + out.getName() + " in " + ((System.currentTimeMillis() - starttime) + " ms."));

                } catch (Exception e) {
                    log.info("Png conversion with transparent pixels failed, now attempting conversion with" +
                            " user default color replacing transparent pixels...");
                    boolean result = handleTransBg(bufferedImage, format, out);
                    if (result) {
                        success = true;
                        log.info("The second conversion attempt was successful!");
                        log.info("Converted --> from: " + in.getName() + " to -> " + out.getName() + " in " + ((System.currentTimeMillis() - starttime) + " ms."));

                    }

                }
            } catch (IOException | IllegalArgumentException exception) {
                log.error("Conversion failed on " + in.getName());
                exception.printStackTrace();
            }

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

