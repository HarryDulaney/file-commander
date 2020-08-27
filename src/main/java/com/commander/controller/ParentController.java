package com.commander.controller;

import com.commander.model.*;
import com.commander.utils.ValidationUtils;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


/**
 * {@code ParentController} is the Parent class to application controllers.
 * <p>
 * ParentController works in conjunction with the WindowHelper utility to
 * share the Stage instance between controllers.
 *
 * @author HGDIV
 */
public abstract class ParentController {

    protected Stage stage;
    static Logger logger = LoggerFactory.getLogger(ParentController.class);
    private static Preferences userPreferences;
    static User user;

    static {
        user = new User();

    }


    /* ------------------------------------------ FXML Path Constants ---------------------------------------------------- */
    static final String DRAG_DROP_FXML = "/fxml/draganddrop.fxml";
    static final String PREF_EMBED_FXML = "/fxml/settingsframe.fxml";
    static final String ROOT_FXML = "/fxml/settingsview.fxml";


    /* ------------------------------------------ Message Key Constants -------------------------------------------------- */
    static final String OPEN_CONVERTER = "OPEN_CONVERTER";
    static final String OPEN_PREFERENCES = "OPEN_PREFS";
    public static final String FRESH_START = "NEW_START_CONDITION";

    /* ------------------------------------------ Preferences Key Constants ---------------------------------------------- */

    static final String NEW_USER_KEY = "NEW_USER_KEY";
    static final String DIR_PATH_KEY = "SOURCE_PATH";
    static final String DIR_WRITE_PATH_KEY = "WRITE_PATH";
    static final String EXCEL_PREF_KEY = "EXCEL_PREF";
    static final String DOC_TYPE_KEY = "DOC_PREF";
    static final String IMG_TYPE_KEY = "IMG_PREF";
    static final String SOURCE_POLICY_KEY = "SOURCE_POLICY";
    static final String BACKGROUND_COLOR = "DEFAULT_BG_COLOR";


    /* --------------------------------------- Default values for User Enums --------------------------------------------- */
    private static final DocType DEFAULT_EXCEL_TYPE = DocType.XLSX;
    private static final DocType DEFAULT_IMG_TYPE = DocType.JPG;

    /* ----------------------------------------- Misc Controller Keys ---------------------------------------------------- */
    static final String PROJECT_SOURCE_DELETE_KEY = "Delete";
    static final String PROJECT_SOURCE_SAVE_KEY = "Save";

    static final String DOCX2PDF = "docx -> pdf";
//    static final String PDFtxt2DOCX = "pdf -> (EXTRACT TEXT) -> docx";
    static final String CLONE_PDF_TO_DOCX = "pdf -> docx (Windows Only)";


    /**
     * {@code init(Stage s, HashMap<String,T> p} Overridden in child controller
     * and called via super chaining
     * <p>
     * Also, called from WindowUtil when rendering over the primaryStage
     *
     * @param stage      Current Stage
     * @param parameters passed from caller
     * @param <T>        Usually a String value
     */
    public <T> void init(Stage stage, HashMap<String, T> parameters) {
        this.stage = stage;


        this.stage.setOnHiding(e -> onClose());
        this.stage.setOnHidden(e -> onClose());
    }

    public Stage getStage() {
        return stage;
    }

    public static String getRootFxml() {
        return ROOT_FXML;
    }


    /**
     * Persist User's Preferences to memory
     */
    protected static Boolean setPreferences() {
        userPreferences = Preferences.userNodeForPackage(ParentController.class);

        userPreferences.put(DIR_PATH_KEY, user.getDirectoryPath());
        userPreferences.put(DIR_WRITE_PATH_KEY, user.getWriteDirectoryPath());
        userPreferences.put(EXCEL_PREF_KEY, user.getExcelPreference().getExtension());
        userPreferences.put(DOC_TYPE_KEY, user.getDocPreference().getDocOperation());
        userPreferences.put(IMG_TYPE_KEY, user.getImgPreference().getExtension());
        userPreferences.put(SOURCE_POLICY_KEY, user.getSourceFilePolicy());
        if (ValidationUtils.validateUserPaths(user)) {
            userPreferences.putBoolean(NEW_USER_KEY, false);
        }
        // set color preference
        userPreferences.put(BACKGROUND_COLOR, user.getReplaceBgColor().toString());
        logger.info("Setting persistent user preferences, background color for images string value is: " + user.getReplaceBgColor().toString());

        try {
            userPreferences.sync();
            return true;
        } catch (BackingStoreException bse) {
            logger.error(bse.getMessage(), bse);
            return false;
        }

    }

    /**
     * Load Preferences into User (Handled internally by ParentController)
     */
    protected static void loadPreferences() {

        userPreferences = Preferences.userNodeForPackage(ParentController.class);
        user.setNuUser(userPreferences.getBoolean(NEW_USER_KEY, true)); //Default value is true, meaning no pref stored for this user
        user.setDirectoryPath(userPreferences.get(DIR_PATH_KEY, null));
        user.setWriteDirectoryPath(userPreferences.get(DIR_WRITE_PATH_KEY, null));
        user.setSourceFilePolicy(userPreferences.get(SOURCE_POLICY_KEY, PROJECT_SOURCE_SAVE_KEY)); //Default is Save source file

        String docPreference = userPreferences.get(DOC_TYPE_KEY, DocOperation.DOCX_TO_PDF.getDocOperation()); //Default treatment for Word Documents Docx to Pdf
        String excelPreference = userPreferences.get(EXCEL_PREF_KEY, DEFAULT_EXCEL_TYPE.getExtension()); //Default Excel Type is XLSX
        String imgPreference = userPreferences.get(IMG_TYPE_KEY, DEFAULT_IMG_TYPE.getExtension()); //Default ImgType is JPG
        String colorPreference = userPreferences.get(BACKGROUND_COLOR, Color.WHITE.toString()); // Default white background
        logger.info("Loading preferences, Background Color for images string value is: " + colorPreference);
        // set user preferences
        updateUser(docPreference, excelPreference, imgPreference, colorPreference);


    }

    private static void updateUser(String docPreference, String excelPreference, String imgPreference, String colorStr) {

        if (docPreference.equals(DocOperation.DOCX_TO_PDF.getDocOperation())) {
            user.setDocPreference(DocOperation.DOCX_TO_PDF);
        } else if (docPreference.equals(DocOperation.PDF_txt_TO_DOCX.getDocOperation())) {
            user.setDocPreference(DocOperation.PDF_txt_TO_DOCX);
        } else if (docPreference.equals(DocOperation.PDF_TO_DOCX.getDocOperation())) {
            user.setDocPreference(DocOperation.PDF_TO_DOCX);
        }

        if (excelPreference.equals(DocType.CSV.getExtension())) {
            user.setExcelPreference(DocType.CSV);
        } else if (excelPreference.equals(DocType.XLSX.getExtension())) {
            user.setExcelPreference(DocType.XLSX);
        }

        if (imgPreference.equals(DocType.BMP.getExtension())) {
            user.setImgPreference(DocType.BMP);
        } else if (imgPreference.equals(DocType.GIF.getExtension())) {
            user.setImgPreference(DocType.GIF);
        } else if (imgPreference.equals(DocType.JPG.getExtension())) {
            user.setImgPreference(DocType.JPG);
        } else if (imgPreference.equals(DocType.PNG.getExtension())) {
            user.setImgPreference(DocType.PNG);
        }

        Color userColor = Color.valueOf(colorStr);
        user.setReplaceBgColor(userColor);

    }

    protected abstract void onClose();

}

