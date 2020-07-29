package com.commander.controller;

import com.commander.model.*;
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

    /* --------------------------------------- Default values for User Enums --------------------------------------------- */
    private static final ExcelType DEFAULT_EXCEL_TYPE = ExcelType.XLSX;
    private static final ImgType DEFAULT_IMG_TYPE = ImgType.JPG;

    /* ----------------------------------------- Misc Controller Keys ---------------------------------------------------- */
    static final String PROJECT_SOURCE_DELETE_KEY = "Delete";
    static final String PROJECT_SOURCE_SAVE_KEY = "Save";


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
        userPreferences.put(DOC_TYPE_KEY, DocOperation.DOCX_TO_PDF.getDocOperation());
        userPreferences.put(IMG_TYPE_KEY, user.getImgPreference().getExtension());
        userPreferences.put(SOURCE_POLICY_KEY, user.getSourceFilePolicy());
        userPreferences.put(NEW_USER_KEY, "false");
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

        user.setNuUser(userPreferences.getBoolean(NEW_USER_KEY, true)); //Default value is true, meaning no prefs stored for this user
        user.setDirectoryPath(userPreferences.get(DIR_PATH_KEY, null));
        user.setWriteDirectoryPath(userPreferences.get(DIR_WRITE_PATH_KEY, null));
        user.setSourceFilePolicy(userPreferences.get(SOURCE_POLICY_KEY, PROJECT_SOURCE_SAVE_KEY)); //Default is Save source file

        String docPreference = userPreferences.get(DOC_TYPE_KEY,DocOperation.DOCX_TO_PDF.getDocOperation()); //Default treatment for Word Documents Docx to Pdf
        String excelPreference = userPreferences.get(EXCEL_PREF_KEY, DEFAULT_EXCEL_TYPE.getExtension()); //Default Excel Type is XLSX
        String imgPreference = userPreferences.get(IMG_TYPE_KEY, DEFAULT_IMG_TYPE.getExtension()); //Default ImgType is JPG

        if (docPreference.equals(DocOperation.DOCX_TO_PDF.getDocOperation())) {
            user.setDocPreference(DocOperation.DOCX_TO_PDF);
        } else if (docPreference.equals(DocOperation.PDF_TO_TEXT.getDocOperation())) {
            user.setDocPreference(DocOperation.PDF_TO_TEXT);
        } else if (docPreference.equals(DocOperation.DOCX_TO_HTML.getDocOperation())) {
            user.setDocPreference(DocOperation.DOCX_TO_HTML);
        } else if (docPreference.equals(DocOperation.HTML_TO_DOCX.getDocOperation())) {
            user.setDocPreference(DocOperation.HTML_TO_DOCX);
        }

        if (excelPreference.equals(ExcelType.CSV.getExtension())) {
            user.setExcelPreference(ExcelType.CSV);
        } else if (excelPreference.equals(ExcelType.XLSX.getExtension())) {
            user.setExcelPreference(ExcelType.XLSX);
        }

        if (imgPreference.equals(ImgType.BMP.getExtension())) {
            user.setImgPreference(ImgType.BMP);
        } else if (imgPreference.equals(ImgType.GIF.getExtension())) {
            user.setImgPreference(ImgType.GIF);
        } else if (imgPreference.equals(ImgType.JPG.getExtension())) {
            user.setImgPreference(ImgType.JPG);
        } else {
            user.setImgPreference(ImgType.PNG);
        }

    }


    /**
     * Call Preferences.flush()
     */
    protected static void flushPreferences() {
        try {
            userPreferences.flush();
        } catch (BackingStoreException bse) {
            logger.error("BackingStoreException: " + bse.getMessage() + " | " + bse.getCause());

        }
    }

    protected abstract void onClose();

}

