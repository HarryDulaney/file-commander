package com.commander.utils;

import com.commander.model.*;
import com.commander.utils.ValidationUtils;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Priority;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


/**
 * Global Application Constants
 *
 * @author HGDIV
 */
public class Constants {

    /* ------------------------------------------ FXML Path Constants ---------------------------------------------------- */
    public static final String DRAG_DROP_FXML = "/fxml/archive/draganddrop.fxml";
    public static final String PREF_EMBED_FXML = "/fxml/archive/settingsframe.fxml";
    public static final String ROOT_FXML = "/fxml/mainview.fxml";


    /* ------------------------------------------ Message Key Constants -------------------------------------------------- */
    static final String OPEN_CONVERTER = "OPEN_CONVERTER";
    static final String OPEN_PREFERENCES = "OPEN_PREFS";
    public static final String FRESH_START = "NEW_START_CONDITION";

    /* ------------------------------------------ Preferences Key Constants ---------------------------------------------- */

    public static final String NEW_USER_KEY = "NEW_USER_KEY";
    public static final String DIR_PATH_KEY = "SOURCE_PATH";
    public static final String DIR_WRITE_PATH_KEY = "WRITE_PATH";
    public static final String EXCEL_PREF_KEY = "EXCEL_PREF";
    public static final String DOC_TYPE_KEY = "DOC_PREF";
    public static final String IMG_TYPE_KEY = "IMG_PREF";
    public static final String SOURCE_POLICY_KEY = "SOURCE_POLICY";
    public static final String BACKGROUND_COLOR = "DEFAULT_BG_COLOR";


    /* ----------------------------------------- Misc Controller Keys ---------------------------------------------------- */
    public static final String PROJECT_SOURCE_DELETE_KEY = "Delete";
    public static final String PROJECT_SOURCE_SAVE_KEY = "Save";

    public static final String DOCX2PDF = "docx -> pdf";
    //    static final String PDFtxt2DOCX = "pdf -> (EXTRACT TEXT) -> docx";
    public static final String CLONE_PDF_TO_DOCX = "pdf -> docx (Windows Only)";

}

