package com.commander.controller.converters;

import com.commander.model.Convertible;
import com.commander.utils.DialogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

/**
 * Parent class to all Converters e.g XlsxToCsv, DocxToPdf..
 * Abstracts logic common to all converters: File in and File out
 * and handling of the source file after conversion is complete.
 *
 * @author HGDIV
 */
public abstract class Converter implements Convertible {

    /**
     * The In File.
     */
    File in;
    /**
     * The Out File.
     */
    File out;


    private static Boolean deleteSourceAfterConverted;
    /**
     * The constant log.
     */
    protected static Logger log = LoggerFactory.getLogger(Converter.class);
    private static final String TAG = Converter.class.getCanonicalName();


    /**
     * Instantiates a new Converter.
     *
     * @param in  the in
     * @param out the out
     */
    protected Converter(File in, File out) {
        this.in = in;
        this.out = out;

    }


    /**
     * Evaluates user preference deleteSourceAfterConverted and
     * the boolean success variable and deletes the source file if appropriate
     *
     * @param success was the file successfully converted and written
     * @param file    the source file
     */
    protected static void deleteSourceFile(boolean success, File file) {
        boolean deleted = false;
        if (deleteSourceAfterConverted) {
            if (success) {
                try {
                    Files.delete(file.toPath());
                    if (!Files.exists(file.toPath())) {
                        deleted = true;
                    }
                } catch (IOException ex) {
                    DialogHelper.showErrorAlert("Your source file " + file.getName() + "could not be deleted");
                    ex.printStackTrace();
                }

            }
        }
        log.info("Source File was " + (deleted ? "deleted" : "not deleted"));
    }


    /**
     * Sets resources.
     *
     * @param <T>       the type parameter
     * @param resources the resources
     */
    public static <T> void setResources(HashMap<String, T> resources) {
        if (resources.get("delete.policy") instanceof Boolean) {
            deleteSourceAfterConverted = (Boolean) resources.get("delete.policy");
        }

    }
}
