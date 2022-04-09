package com.commander.model;

import com.commander.utils.Constants;
import com.commander.utils.ValidationUtils;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * <p>
 * User is an encapsulated POJO
 * that represents the application users properties, i.e. Id,
 * username, references, file extension type preferences.
 * </p>
 *
 * @author Harry Dulaney
 */
public class User {
    static Logger logger = LoggerFactory.getLogger(User.class);

    public static Preferences getUserPreferences() {
        if (userPreferences != null) {
            return userPreferences;
        }
        new User().loadPreferences();
        return userPreferences;
    }

    /**
     * CURRENT_USER_ID is the System property "user.name" used for automatically identifying a unique application user
     */
    private static final String CURRENT_USER_ID = System.getProperty("user.name");
    /**
     * directoryPath is a String holding the path to users source directory
     */
    private String directoryPath;
    /**
     * writeDirectoryPath is a String holding the path to the users output directory
     */
    private String writeDirectoryPath;
    /**
     * sourceFilePolicy is a String containing either "save" or "delete" indicating the users
     * assigned method for managing source files once the conversion to new format is complete
     */
    private String sourceFilePolicy;
    /**
     * replaceBgColor is a javafx.scene.paint.Color that is used in applying background coloring to
     * image conversions where the output format does not support transparency.
     */
    private Color replaceBgColor;
    /**
     * nuUser is true, if user is a first-time User with no persistent preferences
     */
    private Boolean nuUser;

    /**
     * excelPreference contains the users preferred formatting option for Excel workbook file types
     */
    private DocType excelPreference;
    /**
     * docPreference contains the users preferred formatting option for MSWord file types
     */
    private DocOperation docPreference;
    /**
     * imgPreference contains the users preferred formatting option for image files
     */
    private DocType imgPreference;
    /**
     * The users selected color theme i.e. light/ dark theme
     */
    private String colorThemePreference;
    /**
     * Preferences Object
     */
    private static Preferences userPreferences;


    /* --------------------------------------- Default values for User Enums --------------------------------------------- */
    public static final DocType DEFAULT_EXCEL_TYPE = DocType.XLSX;
    public static final DocType DEFAULT_IMG_TYPE = DocType.JPG;


    public User() {
        super();
    }


    /**
     * Persist User's Preferences to memory
     */
    public Boolean setPreferences() {
        userPreferences = Preferences.userNodeForPackage(getClass());

        if (this.getDirectoryPath() != null)
            userPreferences.put(Constants.DIR_PATH_KEY, this.getDirectoryPath());

        if (this.getWriteDirectoryPath() != null)
            userPreferences.put(Constants.DIR_WRITE_PATH_KEY, this.writeDirectoryPath);

        userPreferences.put(Constants.EXCEL_PREF_KEY, this.getExcelPreference().getExtension());
        userPreferences.put(Constants.DOC_TYPE_KEY, this.getDocPreference().getDocOperation());
        userPreferences.put(Constants.IMG_TYPE_KEY, this.getImgPreference().getExtension());
        userPreferences.put(Constants.SOURCE_POLICY_KEY, this.getSourceFilePolicy());

        String themePreference = this.getColorThemePreference() != null ? this.getColorThemePreference() : Constants.LIGHT_THEME_ID;
        userPreferences.put(Constants.GUI_COLOR_THEME, themePreference);
        if (ValidationUtils.validateUserPaths(this)) {
            userPreferences.putBoolean(Constants.NEW_USER_KEY, false);
        }
        // set color preference
        userPreferences.put(Constants.BACKGROUND_COLOR, this.getReplaceBgColor().toString());
        logger.info("Setting persistent user preferences, background color for images string value is: " + this.getReplaceBgColor().toString());

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
    public void loadPreferences() {

        userPreferences = Preferences.userNodeForPackage(getClass());
        this.setNuUser(userPreferences.getBoolean(Constants.NEW_USER_KEY, true)); //Default value is true, meaning no
        this.setDirectoryPath(userPreferences.get(Constants.DIR_PATH_KEY, null));
        this.setWriteDirectoryPath(userPreferences.get(Constants.DIR_WRITE_PATH_KEY, null));
        this.setSourceFilePolicy(userPreferences.get(Constants.SOURCE_POLICY_KEY, Constants.PROJECT_SOURCE_SAVE_KEY));
        this.setColorThemePreference(userPreferences.get(Constants.GUI_COLOR_THEME, Constants.LIGHT_THEME_ID));
        String docPreference = userPreferences.get(Constants.DOC_TYPE_KEY, DocOperation.DOCX_TO_PDF.getDocOperation());
        String excelPreference = userPreferences.get(Constants.EXCEL_PREF_KEY, DEFAULT_EXCEL_TYPE.getExtension());
        String imgPreference = userPreferences.get(Constants.IMG_TYPE_KEY, DEFAULT_IMG_TYPE.getExtension());
        String colorPreference = userPreferences.get(Constants.BACKGROUND_COLOR, Color.WHITE.toString()); // Default

        logger.info("Loading preferences, Background Color for images string value is: " + colorPreference);

        updateUser(docPreference, excelPreference, imgPreference, colorPreference);


    }

    private void updateUser(String docPreference, String excelPreference, String imgPreference, String colorStr) {

        if (docPreference.equals(DocOperation.DOCX_TO_PDF.getDocOperation())) {
            setDocPreference(DocOperation.DOCX_TO_PDF);
        } else if (docPreference.equals(DocOperation.PDF_txt_TO_DOCX.getDocOperation())) {
            setDocPreference(DocOperation.PDF_txt_TO_DOCX);
        } else if (docPreference.equals(DocOperation.PDF_TO_DOCX.getDocOperation())) {
            setDocPreference(DocOperation.PDF_TO_DOCX);
        }

        if (excelPreference.equals(DocType.CSV.getExtension())) {
            setExcelPreference(DocType.CSV);
        } else if (excelPreference.equals(DocType.XLSX.getExtension())) {
            setExcelPreference(DocType.XLSX);
        }

        if (imgPreference.equals(DocType.BMP.getExtension())) {
            this.setImgPreference(DocType.BMP);
        } else if (imgPreference.equals(DocType.GIF.getExtension())) {
            this.setImgPreference(DocType.GIF);
        } else if (imgPreference.equals(DocType.JPG.getExtension())) {
            this.setImgPreference(DocType.JPG);
        } else if (imgPreference.equals(DocType.PNG.getExtension())) {
            this.setImgPreference(DocType.PNG);
        }

        Color userColor = Color.valueOf(colorStr);
        this.setReplaceBgColor(userColor);

    }


    public User(DocType excelPreference, DocOperation docPreference, DocType imgPreference) {

        this.excelPreference = excelPreference;
        this.docPreference = docPreference;
        this.imgPreference = imgPreference;
    }


    public DocType getExcelPreference() {
        return excelPreference;
    }

    public void setExcelPreference(DocType excelPreference) {
        this.excelPreference = excelPreference;
    }

    public DocOperation getDocPreference() {
        return docPreference;
    }

    public void setDocPreference(DocOperation docPreference) {
        this.docPreference = docPreference;
    }

    public DocType getImgPreference() {
        return imgPreference;
    }

    public void setImgPreference(DocType imgPreference) {
        this.imgPreference = imgPreference;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public String getSourceFilePolicy() {
        return sourceFilePolicy;
    }

    public void setSourceFilePolicy(String sourceFilePolicy) {
        this.sourceFilePolicy = sourceFilePolicy;
    }

    public String getWriteDirectoryPath() {
        return writeDirectoryPath;
    }

    public void setWriteDirectoryPath(String writeDirectoryPath) {
        this.writeDirectoryPath = writeDirectoryPath;
    }

    public Boolean getNuUser() {
        return nuUser;
    }

    public void setNuUser(Boolean nuUser) {
        this.nuUser = nuUser;
    }

    public Color getReplaceBgColor() {
        return replaceBgColor;
    }

    public void setReplaceBgColor(Color replaceBgColor) {
        this.replaceBgColor = replaceBgColor;
    }

    public String getColorThemePreference() {
        return colorThemePreference;
    }

    public User setColorThemePreference(String colorThemePreference) {
        this.colorThemePreference = colorThemePreference;
        return this;
    }


    /**
     * @return A String who's contents can be used to uniquely identify
     * the current user of the application.
     */
    public static String getCurrentUserId() {
        return CURRENT_USER_ID;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return ((User) obj).getDirectoryPath().equals(this.getDirectoryPath())
                    && ((User) obj).getWriteDirectoryPath().equals(this.getWriteDirectoryPath())
                    && ((User) obj).getSourceFilePolicy().equals(this.getSourceFilePolicy())
                    && this.excelPreference.equals(((User) obj).getExcelPreference())
                    && this.docPreference.equals(((User) obj).getDocPreference())
                    && this.imgPreference.equals(((User) obj).getImgPreference())
                    && User.CURRENT_USER_ID.equals(((User) obj).getCurrentUserId());
        } else
            return false;
    }

    @Override
    public String toString() {
        return "User{" +
                ", userName='" + CURRENT_USER_ID + '\'' +
                ", directoryPath='" + directoryPath + '\'' +
                ", writeDirectoryPath='" + writeDirectoryPath + '\'' +
                ", sourceFilePolicy='" + sourceFilePolicy + '\'' +
                ", execPref='" + getExcelPreference().toString() + '\'' +
                ", docPref='" + getDocPreference().toString() + '\'' +
                ", imgPref='" + getImgPreference().toString() + '\'' +
                '}';
    }

}