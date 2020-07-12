package com.commander.controller;

import com.commander.model.DocType;
import com.commander.model.ExcelType;
import com.commander.model.ImgType;
import com.commander.model.User;
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
    private static final DocType DEFAULT_DOC_TYPE = DocType.DOCX;
    private static final ExcelType DEFAULT_EXCEL_TYPE = ExcelType.XLSX;
    private static final ImgType DEFAULT_IMG_TYPE = ImgType.JPG;

    /* ----------------------------------------- Misc Controller Keys ---------------------------------------------------- */
    static final String PROJECT_SOURCE_DELETE_KEY = "Delete-On-Converted";
    static final String PROJECT_SOURCE_SAVE_KEY = "Save-On-Converted";


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
        userPreferences.put(DOC_TYPE_KEY, user.getDocPreference().getExtension());
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

        user.setNuUser(userPreferences.getBoolean(NEW_USER_KEY, true));
        user.setDirectoryPath(userPreferences.get(DIR_PATH_KEY, null));
        user.setWriteDirectoryPath(userPreferences.get(DIR_WRITE_PATH_KEY, null));
        user.setSourceFilePolicy(userPreferences.get(SOURCE_POLICY_KEY, PROJECT_SOURCE_SAVE_KEY));

        String docType = userPreferences.get(DOC_TYPE_KEY, DEFAULT_DOC_TYPE.getExtension());
        String excelType = userPreferences.get(EXCEL_PREF_KEY, DEFAULT_EXCEL_TYPE.getExtension());
        String imgType = userPreferences.get(IMG_TYPE_KEY, DEFAULT_IMG_TYPE.getExtension());

        if (docType.equals(DocType.docx())) {
            user.setDocPreference(DocType.DOCX);
        } else if (docType.equals(DocType.pdf())) {
            user.setDocPreference(DocType.PDF);
        }

        if (excelType.equals(ExcelType.csv())) {
            user.setExcelPreference(ExcelType.CSV);
        } else if (excelType.equals(ExcelType.xlsx())) {
            user.setExcelPreference(ExcelType.XLSX);
        }

        if (imgType.equals(ImgType.bmp())) {
            user.setImgPreference(ImgType.BMP);
        } else if (imgType.equals(ImgType.gif())) {
            user.setImgPreference(ImgType.GIF);
        } else if (imgType.equals(ImgType.jpg())) {
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

