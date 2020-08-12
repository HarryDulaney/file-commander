package com.commander.controller.converters;

import com.commander.model.Convertible;
import com.commander.utils.DialogHelper;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.ResourceBundle;

public abstract class Converter implements Convertible {

    File in;
    File out;

    static AnchorPane toastPane;
    private static Boolean deleteSourceAfterConverted;
    protected static Logger log = LoggerFactory.getLogger(Converter.class);
    private static final String TAG = Converter.class.getCanonicalName();


    public Converter(File in, File out) {
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
        if (deleteSourceAfterConverted) {
            if (success) {
                try {
                    Files.delete(file.toPath());
                } catch (IOException ex) {
                    DialogHelper.showErrorAlert("Your source file " + file.getName() + "could not be deleted");
                    ex.printStackTrace();
                }

            }
        }

    }


    public static <T> void setResources(HashMap<String, T> resources) {
    if (resources.get("root.pane") instanceof Pane) {
        toastPane = (AnchorPane) resources.get("root.pane");

    }
    if (resources.get("delete.policy") instanceof Boolean) {
        deleteSourceAfterConverted = (Boolean) resources.get("delete.policy");
    }

    }
}
