package com.commander.model;

import javafx.scene.paint.Color;
import org.springframework.stereotype.Component;

/**
 * <p>
 * User is an encapsulated POJO
 * that represents the application users properties, i.e. Id,
 * username, references, file extension type preferences.
 * </p>
 *
 * @author HGDIV
 */

@Component
public class User {
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


    public User() {
        super();

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